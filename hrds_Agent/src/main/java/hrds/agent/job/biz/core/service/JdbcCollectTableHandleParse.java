package hrds.agent.job.biz.core.service;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.SourceDataConfBean;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.commons.codes.StorageType;
import hrds.commons.entity.Column_split;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.ConnUtil;
import hrds.commons.utils.Constant;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@DocClass(desc = "根据页面所选的表和字段对jdbc所返回的meta信息进行解析", author = "zxz", createdate = "2019/12/4 11:17")
public class JdbcCollectTableHandleParse extends AbstractCollectTableHandle {

	@SuppressWarnings("unchecked")
	@Method(desc = "根据数据源信息和采集表信息得到卸数元信息", logicStep = "" +
			"1、根据数据源信息和采集表信息抽取SQL" +
			"2、根据数据源信息和抽取SQL，执行SQL，获取")
	@Param(name = "sourceDataConfBean", desc = "数据库采集,DB文件采集数据源配置信息", range = "不为空")
	@Param(name = "collectTableBean", desc = "数据库采集表配置信息", range = "不为空")
	@Return(desc = "卸数阶段元信息", range = "不为空")
	public TableBean generateTableInfo(SourceDataConfBean sourceDataConfBean,
	                                   CollectTableBean collectTableBean) {
		TableBean tableBean = new TableBean();
		Connection conn = null;
		try {
			//获取jdbc连接
			conn = ConnUtil.getConnection(sourceDataConfBean.getDatabase_drive(), sourceDataConfBean.getJdbc_url(),
					sourceDataConfBean.getUser_name(), sourceDataConfBean.getDatabase_pad());
			//1、根据数据源信息和采集表信息获取数据库抽取SQL
			String collectSQL = getCollectSQL(sourceDataConfBean, collectTableBean);
			tableBean.setCollectSQL(collectSQL);
			//抽取sql可能包含分隔符，判断如果包含分隔符，取第一条sql获取meta信息（注：包含分隔符表明并行抽取）
			ResultSet resultSet;
			if (collectSQL.contains(JobConstant.SQLDELIMITER)) {
				resultSet = getResultSet(StringUtil.split(collectSQL, JobConstant.SQLDELIMITER).get(0), conn);
			} else {
				resultSet = getResultSet(collectSQL, conn);
			}
			StringBuilder columnMetaInfo = new StringBuilder();//生成的元信息列名
			StringBuilder allColumns = new StringBuilder();//要采集的列名
			StringBuilder colTypeMetaInfo = new StringBuilder();//生成的元信息列类型
			StringBuilder allType = new StringBuilder();//要采集的列类型
			StringBuilder colLengthInfo = new StringBuilder();//生成的元信息列长度
			/* Get result set metadata */
			ResultSetMetaData rsMetaData = resultSet.getMetaData();
			int numberOfColumns = rsMetaData.getColumnCount();//获得列的数量
			int[] typeArray = new int[numberOfColumns];//列类型数组
			// Write header
			for (int i = 1; i <= numberOfColumns; i++) {
				String columnTmp = rsMetaData.getColumnName(i);
				int columnType = rsMetaData.getColumnType(i);
				//TODO 下一行未知
				if (!columnTmp.equalsIgnoreCase("hyren_rn")) {
					columnMetaInfo.append(columnTmp).append(STRSPLIT);
					allColumns.append(columnTmp.toUpperCase()).append(STRSPLIT);
				}
				typeArray[i - 1] = columnType;
			}
			//判断使用最后采集的sql去进行处理
			Map<String, String> tableColTypeAndLength = getTableColTypeAndLengthSql(resultSet);
			//直接遍历
			for (String key : tableColTypeAndLength.keySet()) {
				List<String> split = StringUtil.split(tableColTypeAndLength.get(key), "::");
				colTypeMetaInfo.append(split.get(0)).append(STRSPLIT);
				allType.append(split.get(0)).append(STRSPLIT);
				colLengthInfo.append(split.get(1)).append(STRSPLIT);
			}
			columnMetaInfo.deleteCharAt(columnMetaInfo.length() - 1);//元信息列名
			allColumns.deleteCharAt(allColumns.length() - 1);//列名
			colLengthInfo.deleteCharAt(colLengthInfo.length() - 1);//列长度
			colTypeMetaInfo.deleteCharAt(colTypeMetaInfo.length() - 1);//列类型
			allType.deleteCharAt(allType.length() - 1);//列类型
			//清洗配置
			Map<String, Object> parseJson = parseJson(collectTableBean);
			Map<String, Map<String, Column_split>> splitIng = (Map<String, Map<String, Column_split>>) parseJson.get("splitIng");//字符拆分
			Map<String, String> mergeIng = (Map<String, String>) parseJson.get("mergeIng");//字符合并
			//更新拆分和合并的列信息
			String colMeta = updateColumn(mergeIng, splitIng, columnMetaInfo, colTypeMetaInfo, colLengthInfo);
			columnMetaInfo.delete(0, columnMetaInfo.length()).append(colMeta);
			//这里是根据不同存储目的地会有相同的拉链方式，则这新增拉链字段在这里增加
			columnMetaInfo.append(STRSPLIT).append(Constant.SDATENAME);
			colTypeMetaInfo.append(STRSPLIT).append("char(8)");
			colLengthInfo.append(STRSPLIT).append("8");
			//增量进数方式
			if (StorageType.ZengLiang.getCode().equals(collectTableBean.getStorage_type())) {
				columnMetaInfo.append(STRSPLIT).append(Constant.EDATENAME).append(STRSPLIT).append(Constant.MD5NAME);
				colTypeMetaInfo.append(STRSPLIT).append("char(8)").append(STRSPLIT).append("char(32)");
				colLengthInfo.append(STRSPLIT).append("8").append(STRSPLIT).append("32");
			}
			// 页面定义的清洗格式进行卸数
			tableBean.setAllColumns(allColumns.toString());
			tableBean.setAllType(allType.toString());
			tableBean.setColLengthInfo(colLengthInfo.toString());
			tableBean.setColTypeMetaInfo(colTypeMetaInfo.toString());
			tableBean.setColumnMetaInfo(columnMetaInfo.toString().toUpperCase());
			tableBean.setTypeArray(typeArray);
			tableBean.setParseJson(parseJson);
		} catch (Exception e) {
			throw new AppSystemException("根据数据源信息和采集表信息得到卸数元信息失败！", e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return tableBean;
	}
}
