package hrds.agent.job.biz.core.dfstage.fileparser;

import fd.ng.core.annotation.DocClass;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "解析文件抽象类", createdate = "2020/4/21 16:44", author = "zxz")
public abstract class FileParserAbstract implements FileParserInterface {
	//采集db文件的文件信息
	protected TableBean tableBean;
	//采集的db文件定义的表信息
	private final CollectTableBean collectTableBean;
	//读文件的全路径
	protected String readFile;
	//写文件的流
	protected BufferedWriter writer;
	//解析db文件的所有列
	protected List<String> dictionaryColumnList;
	//进目标库的所有列，包括列合并、列拆分、HYREN_S_DATE、HYREN_E_DATE、HYREN_MD5_VAL三列
	private final List<String> allColumnList;
	//数据字典定义的所有的列类型
	protected List<String> dictionaryTypeList;
	//列合并的map
	private final Map<String, String> mergeIng;
	//列清洗拆分合并的处理类
	private final Clean cl;
	//获取所有列的值用来做列合并
	private final StringBuilder mergeStringTmp;
	//获取页面选择列算拉链时算md5的列，当没有选择拉链字段，默认使用全字段算md5
	private final Map<String, Boolean> md5Col;
	private final StringBuilder md5StringTmp;
	//拼接每一列的值
	private final StringBuilder lineSb;
	//清洗接口
	private final DataCleanInterface allClean;
	//判断是否追加结束日期和MD5字段
	private final boolean isMd5;
	//转存落地的文件路径
	protected String unloadFileAbsolutePath;
	//跑批日期
	private final String etl_date;
	//操作日期
	private final String operateDate;
	//操作时间
	private final String operateTime;
	//操作人
	private final long user_id;

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
		this.mergeStringTmp = new StringBuilder();
		this.lineSb = new StringBuilder();
		this.dictionaryTypeList = StringUtil.split(tableBean.getAllType(), Constant.METAINFOSPLIT);
		//根据(是否拉链存储且增量进数)和是否算md5判断是否要追加MD5和结束日期两个字段
		this.isMd5 = IsFlag.Shi.getCode().equals(collectTableBean.getIs_md5()) ||
				(IsFlag.Shi.getCode().equals(collectTableBean.getIs_zipper()) &&
						StorageType.ZengLiang.getCode().equals(collectTableBean.getStorage_type())) ||
				(IsFlag.Shi.getCode().equals(collectTableBean.getIs_zipper()) &&
				StorageType.QuanLiang.getCode().equals(collectTableBean.getStorage_type()));
		this.etl_date = collectTableBean.getEtlDate();
		this.operateDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		this.operateTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
		this.user_id = collectTableBean.getUser_id();
		this.md5Col = transMd5ColMap(tableBean.getIsZipperFieldInfo());
		this.md5StringTmp = new StringBuilder(1024 * 1024);
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
		md5StringTmp.delete(0, md5StringTmp.length());
		mergeStringTmp.delete(0, mergeStringTmp.length());
		lineSb.delete(0, lineSb.length());
		//遍历每一列数据
		for (int i = 0; i < lineList.size(); i++) {
			columnName = dictionaryColumnList.get(i);
			//转存这里碰到HYREN_S_DATE、HYREN_E_DATE、HYREN_MD5_VAL三列直接跳过
			if (Constant.SDATENAME.equalsIgnoreCase(columnName)
					|| Constant.EDATENAME.equalsIgnoreCase(columnName)
					|| Constant.MD5NAME.equalsIgnoreCase(columnName)
					|| Constant.HYREN_OPER_DATE.equalsIgnoreCase(columnName)
					|| Constant.HYREN_OPER_TIME.equalsIgnoreCase(columnName)
					|| Constant.HYREN_OPER_PERSON.equalsIgnoreCase(columnName)) {
				continue;
			}
			columnData = lineList.get(i);
			if (null == columnData) {
				columnData = "";
			}
			//判断是否是算md5的列，算md5
			if (md5Col.get(columnName) != null && md5Col.get(columnName)) {
				md5StringTmp.append(columnData);
			}
			//列清洗
			columnData = cl.cleanColumn(columnData, columnName, null,
					dictionaryTypeList.get(i), FileFormat.FeiDingChang.getCode(), null,
					null, Constant.DATADELIMITER);
			//清理不规则的数据
			columnData = AbstractFileWriter.clearIrregularData(columnData);
			mergeStringTmp.append(columnData).append(Constant.DATADELIMITER);
			lineSb.append(columnData).append(Constant.DATADELIMITER);
		}
		//如果有列合并处理合并信息
		if (!mergeIng.isEmpty()) {
			List<String> arrColString = StringUtil.split(mergeStringTmp.toString(),
					Constant.DATADELIMITER);
			String merge = allClean.merge(mergeIng, arrColString.toArray(new String[0]), allColumnList.toArray
							(new String[0]), null, null, FileFormat.FeiDingChang.getCode(),
					null, Constant.DATADELIMITER);
			mergeStringTmp.append(merge);
			lineSb.append(merge).append(Constant.DATADELIMITER);
		}
		//追加开始日期
		lineSb.append(etl_date);
		if (isMd5) {
			//根据(是否拉链存储且增量进数)和是否算md5判断是否要追加MD5和结束日期两个字段 追加结束日期和MD5
			lineSb.append(Constant.DATADELIMITER).append(Constant.MAXDATE);
			lineSb.append(Constant.DATADELIMITER).append(MD5Util.md5String(md5StringTmp.toString()));
		}
		//拼接操作时间、操作日期、操作人
		appendOperateInfo(lineSb);
		lineSb.append(Constant.DEFAULTLINESEPARATOR);
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

	/**
	 * 添加操作日期、操作时间、操作人
	 */
	private void appendOperateInfo(StringBuilder sb) {
		if (JobConstant.ISADDOPERATEINFO) {
			sb.append(Constant.DATADELIMITER).append(operateDate).append(Constant.DATADELIMITER)
					.append(operateTime).append(Constant.DATADELIMITER).append(user_id);
		}
	}

	/**
	 * 查询是否选择了拉链字段，如果有，则不做任何操作，没有，则全部key的值变为true
	 */
	protected Map<String, Boolean> transMd5ColMap(Map<String, Boolean> md5ColMap) {
		Map<String, Boolean> map = new HashMap<>();
		boolean flag = true;
		for (String key : md5ColMap.keySet()) {
			if (md5ColMap.get(key)) {
				flag = false;
				break;
			}
		}
		if (flag) {
			for (String key : md5ColMap.keySet()) {
				map.put(key, true);
			}
		} else {
			map = md5ColMap;
		}
		return map;
	}
}
