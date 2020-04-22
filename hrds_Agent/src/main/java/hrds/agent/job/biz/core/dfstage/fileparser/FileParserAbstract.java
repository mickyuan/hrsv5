package hrds.agent.job.biz.core.dfstage.fileparser;

import fd.ng.core.utils.FileNameUtils;
import fd.ng.core.utils.MD5Util;
import fd.ng.core.utils.StringUtil;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.core.service.JdbcCollectTableHandleParse;
import hrds.agent.job.biz.dataclean.Clean;
import hrds.agent.job.biz.dataclean.CleanFactory;
import hrds.agent.job.biz.dataclean.DataCleanInterface;
import hrds.commons.codes.DataBaseCode;
import hrds.commons.codes.FileFormat;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.StorageType;
import hrds.commons.utils.Constant;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * FileParserAbstract
 * date: 2020/4/21 16:44
 * author: zxz
 */
public abstract class FileParserAbstract implements FileParserInterface {
	//采集db文件的文件信息
	TableBean tableBean;
	//采集的db文件定义的表信息
	CollectTableBean collectTableBean;
	//读文件的全路径
	String readFile;
	//写文件的流
	BufferedWriter writer;
	//解析db文件的所有列
	List<String> dictionaryColumnList;
	//进目标库的所有列，包括列合并、列拆分、HYREN_S_DATE、HYREN_E_DATE、HYREN_MD5_VAL三列
	private List<String> allColumnList;
	//数据字典定义的所有的列类型
	private List<String> dictionaryTypeList;
	//列合并的map
	private Map<String, String> mergeIng;
	//列清洗拆分合并的处理类
	private Clean cl;
	//拼接每一列的MD5值
	private StringBuilder midStringOther;
	//拼接每一列的值
	private StringBuilder lineSb;
	//清洗接口
	private DataCleanInterface allClean;
	//列分隔符
	private static final String column_separator = JobConstant.DATADELIMITER;
	//行分隔符
	private static final String line_separator = JobConstant.DEFAULTLINESEPARATOR;
	//判断是否追加结束日期和MD5字段
	private boolean isMd5;
	//转存落地的文件路径
	String unloadFileAbsolutePath;

	@SuppressWarnings("unchecked")
	FileParserAbstract(TableBean tableBean, CollectTableBean collectTableBean, String readFile) throws Exception {
		this.collectTableBean = collectTableBean;
		this.readFile = readFile;
		this.tableBean = tableBean;
		this.unloadFileAbsolutePath = FileNameUtils.normalize(Constant.DBFILEUNLOADFOLDER +
				collectTableBean.getDatabase_id() + File.separator + collectTableBean.getHbase_name() +
				File.separator + FileNameUtils.getName(readFile));
		this.writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(unloadFileAbsolutePath),
				DataBaseCode.ofValueByCode(tableBean.getDbFileArchivedCode())));
		//清洗配置
		allClean = CleanFactory.getInstance().getObjectClean("clean_database");
		//获取所有字段的名称，包括列分割和列合并出来的字段名称
		this.allColumnList = StringUtil.split(tableBean.getColumnMetaInfo(), JdbcCollectTableHandleParse.STRSPLIT);
		//获取所有查询的字段的名称，不包括列分割和列合并出来的字段名称
		this.dictionaryColumnList = StringUtil.split(tableBean.getAllColumns(), JdbcCollectTableHandleParse.STRSPLIT);
		this.mergeIng = (Map<String, String>) tableBean.getParseJson().get("mergeIng");//字符合并
		this.cl = new Clean(tableBean.getParseJson(), allClean);
		this.midStringOther = new StringBuilder();
		this.lineSb = new StringBuilder();
		this.dictionaryTypeList = StringUtil.split(tableBean.getAllType(), JdbcCollectTableHandleParse.STRSPLIT);
		//根据(是否拉链存储且增量进数)和是否算md5判断是否要追加MD5和结束日期两个字段
		this.isMd5 = IsFlag.Shi.getCode().equals(collectTableBean.getIs_md5()) ||
				(IsFlag.Shi.getCode().equals(collectTableBean.getIs_zipper()) &&
						StorageType.ZengLiang.getCode().equals(collectTableBean.getStorage_type()));
	}

	@Override
	public void stopStream() throws IOException {
		writer.close();
	}

	/**
	 * 处理从文件中读取出来的数据
	 *
	 * @param lineList 每条数据所在的集合
	 */
	@Override
	public void dealLine(List<String> lineList) throws IOException {
		String columnName;
		String columnData;
		//处理每一行之前先清空MD5的值
		midStringOther.delete(0, midStringOther.length());
		lineSb.delete(0, midStringOther.length());
		//遍历每一列数据
		for (int i = 0; i < lineList.size(); i++) {
			columnName = dictionaryColumnList.get(i);
			columnData = lineList.get(i);
			if (null == columnData) {
				columnData = "";
			}
			//列清洗
			columnData = cl.cleanColumn(columnData, columnName, null,
					dictionaryTypeList.get(i), FileFormat.FeiDingChang.getCode(), null,
					null, column_separator);
			//清理不规则的数据
			columnData = clearIrregularData(columnData);
			midStringOther.append(columnData);
			lineSb.append(columnData).append(column_separator);
		}
		//如果有列合并处理合并信息
		if (!mergeIng.isEmpty()) {
			List<String> arrColString = StringUtil.split(midStringOther.toString(),
					JobConstant.DATADELIMITER);
			String merge = allClean.merge(mergeIng, arrColString.toArray(new String[0]), allColumnList.toArray
							(new String[0]), null, null, FileFormat.FeiDingChang.getCode(),
					null, column_separator);
			midStringOther.append(merge);
			lineSb.append(merge).append(column_separator);
		}
		lineSb.append(Constant.SDATENAME);
		if (isMd5) {
			lineSb.append(column_separator).append(Constant.MAXDATE);
			lineSb.append(column_separator).append(MD5Util.md5String(midStringOther.toString()));
		}
		lineSb.append(line_separator);
		writer.write(lineSb.toString());
	}

	/**
	 * 清理掉不规则的数据
	 *
	 * @param columnData 单列的数据
	 * @return 清理之后的数据
	 */
	private String clearIrregularData(String columnData) {
		//TODO 目前针对换行符的问题，经过测试，可以通过自定义hive的TextInputFormat能解决自定义表的换行符，
		//TODO 但是如果页面自定义填写换行符，就导致需要每一个不同的换行符都需要对应一个自定义hive的
		//TODO TextInputFormat，难以实现，因此需要使用默认的行分隔符，或者提前实现几个TextInputFormat供选择
		//TODO 下面几行是使用默认的行分隔符，需要替换到数据本身的换行符，这里应该替换成特殊字符串，以便于还原
		if (columnData.contains("\r")) {
			columnData = columnData.replace('\r', ' ');
		}
		if (columnData.contains("\n")) {
			columnData = columnData.replace('\n', ' ');
		}
		if (columnData.contains("\r\n")) {
			columnData = StringUtil.replace(columnData, "\r\n", " ");
		}
		return columnData;
	}
}
