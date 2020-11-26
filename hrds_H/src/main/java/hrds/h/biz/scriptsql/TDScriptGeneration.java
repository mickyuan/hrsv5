package hrds.h.biz.scriptsql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcConstants;
import fd.ng.core.utils.FileNameUtils;
import fd.ng.core.utils.StringUtil;
import fd.ng.web.util.RequestUtil;
import fd.ng.web.util.ResponseUtil;
import hrds.commons.codes.DataBaseCode;
import hrds.commons.codes.StorageType;
import hrds.commons.codes.StoreLayerAdded;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.PropertyParaValue;
import hrds.h.biz.config.MarketConf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

public class TDScriptGeneration {


	public List<String> sqlGeneration(MarketConf conf, String createTableColumnTypes) {

		List<String> sqlList = new ArrayList<>();
		//前置SQL信息
		if (StringUtil.isNotBlank(conf.getPreSql())) {
			sqlList.add(conf.getPreSql());
		}
		//检查表名是否存在
		if (StringUtil.isBlank(conf.getTableName())) {
			throw new AppSystemException("表名未传递");
		}
		//检查表字段信息是否存在
		if (StringUtil.isBlank(createTableColumnTypes)) {
			throw new AppSystemException("表字段信息未传递");
		}
		//检查数据加载方式是否传递
		if (StringUtil.isBlank(conf.getDmDatatable().getStorage_type())) {
			throw new AppSystemException("表数据加载方式未传递");
		}
		//检查脚本模板路径是否存在
//		if (StringUtil.isBlank(jsonObject.getString("scriptModelPath"))) {
//			throw new AppSystemException("脚本模板未传递");
//		}

		//数据SQL
		if (StringUtil.isBlank(conf.getBeforeReplaceSql())) {
			throw new AppSystemException("数据源sql未传递");
		}

		//附加信息,如主键,索引等
		List<String> additionalAttrs = conf.getAddAttrColMap().get(StoreLayerAdded.SuoYinLie.getCode());
		//mapping数据信息
		if (additionalAttrs == null || additionalAttrs.isEmpty()) {
			String createsql = "create multiset table " + conf.getTableName() + "(" + createTableColumnTypes + ")";
			//createsql = SQLUtils.format(createsql, JdbcConstants.TERADATA);
			sqlList.add(createsql);
		} else {
			String createsql = "create multiset table " + conf.getTableName() + "(" + createTableColumnTypes + ")" + String
					.format("PRIMARY INDEX(%s)", String.join(",", additionalAttrs));
			//createsql = SQLUtils.format(createsql, JdbcConstants.TERADATA);
			sqlList.add(createsql);
		}
		//如果是替换的方式,先将表的删除,然后在重新创建,并加载数据
		StorageType store_type = StorageType.ofEnumByCode(conf.getDmDatatable().getStorage_type());
		if (store_type == StorageType.TiHuan) {
			sqlList.add("drop table " + conf.getTableName());
			if (additionalAttrs == null || additionalAttrs.isEmpty()) {
				String createsql = "create multiset table " + conf.getTableName() + "(" + createTableColumnTypes + ") ";
				//createsql = SQLUtils.format(createsql, JdbcConstants.TERADATA);
				sqlList.add(createsql);
			} else {
				String createsql = "create multiset table " + conf.getTableName() + "(" + createTableColumnTypes + ") " + String
						.format("PRIMARY INDEX(%s)", String.join(",", additionalAttrs));
				//createsql = SQLUtils.format(createsql, JdbcConstants.TERADATA);
				sqlList.add(createsql);
			}
		}
		String insertsql = "insert into " + conf.getTableName() + " select * from (" + conf.getBeforeReplaceSql() + ") hyren";
//		insertsql = SQLUtils.format(insertsql, JdbcConstants.TERADATA);
		sqlList.add(insertsql);

		//后置sql信息
		if (StringUtil.isNotBlank(conf.getFinalSql())) {
			String finalSql = conf.getFinalSql();
//			finalSql = SQLUtils.format(finalSql, JdbcConstants.TERADATA);
			sqlList.add(finalSql);
		}
		return sqlList;
	}
//	public void scriptGeneration(MarketConf conf, String createTableColumnTypes) {
//		scriptGeneration(sqlList, conf.getTableName());
//	}

	public void scriptGeneration(List<String> mappingSqlList, String tableName) {
		BufferedReader read = null;
		BufferedWriter writer = null;
		//fixme 服务器路径
//		String scriptModelPath = PropertyParaValue.getString("scriptPatt", "/home/hyshf/");
		//fixme 本地测试用路径
		String scriptModelPath = "C:\\tmp\\perl模板.pl";
		String fileSuffixName = FileNameUtils.getExtension(scriptModelPath);
		String plFileName = tableName + "." + fileSuffixName;
		HttpServletResponse response = ResponseUtil.getResponse();
		try (OutputStream out = response.getOutputStream();) {
			File modelPath = new File(scriptModelPath);
			if (!modelPath.exists()) {
				throw new AppSystemException("模板文件不存在");
			}
			read = new BufferedReader(new FileReader(modelPath));
			File createFile = new File(System.getProperty("user.dir"));
			if (!createFile.exists()) {
				throw new AppSystemException("本地目录不存在,无法建立脚本");
			}
			String plfilename = plFileName;
			if (plfilename.contains("${") && plfilename.contains("}")) {
				plfilename = plfilename.substring(plfilename.indexOf("}") + 2);
			}
			writer = new BufferedWriter(new FileWriter(createFile.getAbsolutePath() + File.separator + plfilename));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = read.readLine()) != null) {
				if (line.contains("my $SQL=<<EOF_SQL;")) {
					buffer.append(line).append(System.lineSeparator());
					mappingSqlList.forEach(item -> {
						buffer.append(item).append(System.lineSeparator()).append(";").append(System.lineSeparator())
								.append(".IF ERRORCODE <> 0 THEN .GOTO QUITWITHERROR;").append(System.lineSeparator())
								.append(System.lineSeparator());
					});
					writer.write(buffer.toString());
				} else if (line.startsWith("my $SCTIPT=")) {
					writer.write("my $SCTIPT=" + "\"" + plFileName + "\"");
				} else {
					buffer.append(line).append(System.lineSeparator());
				}
			}
			// 清空response
			response.reset();
			// 设置响应头，控制浏览器下载该文件
			if (RequestUtil.getRequest().getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {
				// 4.1firefox浏览器
				response.setHeader("content-disposition", "attachment;filename="
						+ new String(plFileName.getBytes(DataBaseCode.UTF_8.getValue()),
						DataBaseCode.ISO_8859_1.getValue()));
			} else {
				// 4.2其它浏览器
				response.setHeader("content-disposition", "attachment;filename="
						+ new String(plFileName.getBytes(), DataBaseCode.UTF_8.getValue()));
			}
			response.setHeader("content-type", "text/html;charset=" + DataBaseCode.UTF_8.getValue());
			response.setCharacterEncoding(DataBaseCode.UTF_8.getValue());
			response.setContentType("APPLICATION/OCTET-STREAM");
			// 创建输出流
			out.write(buffer.toString().getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (read != null) {
				try {
					read.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
