package hrds.agent.job.biz.core.dfstage.fileparser;

import fd.ng.core.utils.FileNameUtils;
import fd.ng.core.utils.MD5Util;
import fd.ng.core.utils.StringUtil;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.core.dbstage.writer.AbstractFileWriter;
import hrds.agent.job.biz.dataclean.Clean;
import hrds.agent.job.biz.dataclean.CleanFactory;
import hrds.agent.job.biz.dataclean.DataCleanInterface;
import hrds.commons.codes.DataBaseCode;
import hrds.commons.codes.FileFormat;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.StorageType;
import hrds.commons.exception.AppSystemException;
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
	protected TableBean tableBean;
	//采集的db文件定义的表信息
	private CollectTableBean collectTableBean;
	//读文件的全路径
	protected String readFile;
	//写文件的流
	protected BufferedWriter writer;
	//解析db文件的所有列
	protected List<String> dictionaryColumnList;
	//进目标库的所有列，包括列合并、列拆分、HYREN_S_DATE、HYREN_E_DATE、HYREN_MD5_VAL三列
	private List<String> allColumnList;
	//数据字典定义的所有的列类型
	protected List<String> dictionaryTypeList;
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
	//判断是否追加结束日期和MD5字段
	private boolean isMd5;
	//转存落地的文件路径
	protected String unloadFileAbsolutePath;
	//跑批日期
	private String etl_date;

	@SuppressWarnings("unchecked")
	protected FileParserAbstract(TableBean tableBean, CollectTableBean collectTableBean, String readFile) throws Exception {
		this.collectTableBean = collectTableBean;
		this.readFile = readFile;
		this.tableBean = tableBean;
		this.unloadFileAbsolutePath = FileNameUtils.normalize(Constant.DBFILEUNLOADFOLDER +
				collectTableBean.getDatabase_id() + File.separator + collectTableBean.getHbase_name() +
				File.separator + collectTableBean.getEtlDate() + File.separator +
				FileNameUtils.getName(readFile), true);
		this.writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(unloadFileAbsolutePath),
				DataBaseCode.ofValueByCode(tableBean.getDbFileArchivedCode())));
		//清洗配置
		allClean = CleanFactory.getInstance().getObjectClean("clean_database");
		//获取所有字段的名称，包括列分割和列合并出来的字段名称
		this.allColumnList = StringUtil.split(tableBean.getColumnMetaInfo(), Constant.METAINFOSPLIT);
		//获取所有查询的字段的名称，不包括列分割和列合并出来的字段名称
		this.dictionaryColumnList = StringUtil.split(tableBean.getAllColumns(), Constant.METAINFOSPLIT);
		this.mergeIng = (Map<String, String>) tableBean.getParseJson().get("mergeIng");//字符合并
		this.cl = new Clean(tableBean.getParseJson(), allClean);
		this.midStringOther = new StringBuilder();
		this.lineSb = new StringBuilder();
		this.dictionaryTypeList = StringUtil.split(tableBean.getAllType(), Constant.METAINFOSPLIT);
		//根据(是否拉链存储且增量进数)和是否算md5判断是否要追加MD5和结束日期两个字段
		this.isMd5 = IsFlag.Shi.getCode().equals(collectTableBean.getIs_md5()) ||
				(IsFlag.Shi.getCode().equals(collectTableBean.getIs_zipper()) &&
						StorageType.ZengLiang.getCode().equals(collectTableBean.getStorage_type()));
		this.etl_date = collectTableBean.getEtlDate();
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
		lineSb.delete(0, lineSb.length());
		//遍历每一列数据
		for (int i = 0; i < lineList.size(); i++) {
			columnName = dictionaryColumnList.get(i);
			//转存这里碰到HYREN_S_DATE、HYREN_E_DATE、HYREN_MD5_VAL三列直接跳过
			if (Constant.SDATENAME.equalsIgnoreCase(columnName) || Constant.EDATENAME.equalsIgnoreCase(columnName)
					|| Constant.MD5NAME.equalsIgnoreCase(columnName)) {
				continue;
			}
			columnData = lineList.get(i);
			if (null == columnData) {
				columnData = "";
			}
			//列清洗
			columnData = cl.cleanColumn(columnData, columnName, null,
					dictionaryTypeList.get(i), FileFormat.FeiDingChang.getCode(), null,
					null, JobConstant.DATADELIMITER);
			//清理不规则的数据
			columnData = AbstractFileWriter.clearIrregularData(columnData);
			midStringOther.append(columnData).append(JobConstant.DATADELIMITER);
			lineSb.append(columnData).append(JobConstant.DATADELIMITER);
		}
		//如果有列合并处理合并信息
		if (!mergeIng.isEmpty()) {
			List<String> arrColString = StringUtil.split(midStringOther.toString(),
					JobConstant.DATADELIMITER);
			String merge = allClean.merge(mergeIng, arrColString.toArray(new String[0]), allColumnList.toArray
							(new String[0]), null, null, FileFormat.FeiDingChang.getCode(),
					null, JobConstant.DATADELIMITER);
			midStringOther.append(merge);
			lineSb.append(merge).append(JobConstant.DATADELIMITER);
		}
		//追加开始日期
		lineSb.append(etl_date);
		if (isMd5) {
			//根据(是否拉链存储且增量进数)和是否算md5判断是否要追加MD5和结束日期两个字段 追加结束日期和MD5
			lineSb.append(JobConstant.DATADELIMITER).append(Constant.MAXDATE);
			lineSb.append(JobConstant.DATADELIMITER).append(MD5Util.md5String(midStringOther.toString()));
		}
		lineSb.append(JobConstant.DEFAULTLINESEPARATOR);
		writer.write(lineSb.toString());
	}

	protected void checkData(List<String> valueList, long fileRowCount) {
		if (dictionaryColumnList.size() != valueList.size()) {
			String mss = "第 " + fileRowCount + " 行数据 ，数据字典表(" + collectTableBean.getTable_name()
					+ " )定义长度和数据不匹配！" + "\n" + "数据字典定义的长度是  " + valueList.size()
					+ " 现在获取的长度是  " + dictionaryColumnList.size() + "\n" + "数据为 " + valueList;
			//XXX 写文件还是直接抛异常  写文件就需要返回值，抛异常就不需要返回值
			throw new AppSystemException(mss);
		}
	}
}
