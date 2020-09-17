package hrds.agent.job.biz.core.jdbcdirectstage;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.FileNameUtils;
import fd.ng.core.utils.StringUtil;
import hrds.agent.job.biz.bean.*;
import hrds.agent.job.biz.constant.RunStatusConstant;
import hrds.agent.job.biz.constant.StageConstant;
import hrds.agent.job.biz.core.AbstractJobStage;
import hrds.agent.job.biz.core.dbstage.DBUnloadDataStageImpl;
import hrds.agent.job.biz.core.metaparse.CollectTableHandleFactory;
import hrds.agent.job.biz.utils.JobStatusInfoUtil;
import hrds.commons.codes.*;
import hrds.commons.entity.Data_extraction_def;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.Constant;
import hrds.commons.utils.StorageTypeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "数据库直连采集数据卸数阶段", author = "zxz")
public class JdbcDirectUnloadDataStageImpl extends AbstractJobStage {

	private final static Logger LOGGER = LoggerFactory.getLogger(JdbcDirectUnloadDataStageImpl.class);

	private final SourceDataConfBean sourceDataConfBean;
	private final CollectTableBean collectTableBean;

	public JdbcDirectUnloadDataStageImpl(SourceDataConfBean sourceDataConfBean, CollectTableBean collectTableBean) {
		this.sourceDataConfBean = sourceDataConfBean;
		this.collectTableBean = collectTableBean;
	}

	@Method(desc = "数据库直连采集卸数阶段处理逻辑，处理完成后，无论成功还是失败，" +
			"将相关状态信息封装到StageStatusInfo对象中返回", logicStep = "" +
			"1.创建卸数阶段状态信息，更新作业ID,阶段名，阶段开始时间" +
			"2.根据页面填写，获取数库直连采集的sql,meta信息等" +
			"3.结束给stageParamInfo塞值")
	@Return(desc = "StageStatusInfo是保存每个阶段状态信息的实体类", range = "不会为null，StageStatusInfo实体类对象")
	@Override
	public StageParamInfo handleStage(StageParamInfo stageParamInfo) {
		//开始时间
		long startTime = System.currentTimeMillis();
		LOGGER.info("------------------表" + collectTableBean.getTable_name()
				+ "数据库直连采集卸数阶段开始------------------");
		//1.创建卸数阶段状态信息，更新作业ID,阶段名，阶段开始时间
		StageStatusInfo statusInfo = new StageStatusInfo();
		JobStatusInfoUtil.startStageStatusInfo(statusInfo, collectTableBean.getTable_id(),
				StageConstant.UNLOADDATA.getCode());
		try {
			//2.根据页面填写，获取数库直连采集的sql,meta信息等
			TableBean tableBean = CollectTableHandleFactory.getCollectTableHandleInstance(sourceDataConfBean)
					.generateTableInfo(sourceDataConfBean, collectTableBean);
			//数据库直连采集、是直接从数据库到数据库，判定表存储层选择的多个目的地都支持外部表时，内部应采用先卸数成文件再通过外部表loader
			// 到数据库，文件的格式统一使用固定分隔符的非定长格式，编码根据存储层的数据库编码来写文件（选择的多个存储层编码不一样时给出提示）
			//3.判断存储层是否均支持外部表
			if (doAllSupportExternal(collectTableBean.getDataStoreConfBean())) {
				//支持外部表
				//获取写文件编码 如果没有填写编码，则默认使用utf-8编码
				String storeDataBaseCode = getStoreDataBaseCode(collectTableBean.getTable_name(),
						collectTableBean.getDataStoreConfBean(), DataBaseCode.UTF_8.getCode());
				//设置默认的卸数目录、换行符、文件分隔符...
				setTableBeanAndDataExtractionDef(storeDataBaseCode, tableBean, collectTableBean);
				//防止有进程被kill导致文件夹下有脏数据，清空脏数据
				String midName = Constant.DBFILEUNLOADFOLDER + File.separator + collectTableBean.getEtlDate()
						+ File.separator + collectTableBean.getTable_name() + File.separator
						+ Constant.fileFormatMap.get(FileFormat.FeiDingChang.getCode()) + File.separator;
				midName = FileNameUtils.normalize(midName, true);
				File dir = new File(midName);
				if (dir.exists()) {
					fd.ng.core.utils.FileUtil.cleanDirectory(dir);
				}
				//调用数据库抽取全量卸数代码
				DBUnloadDataStageImpl.fullAmountExtract(stageParamInfo, tableBean, collectTableBean, sourceDataConfBean);
				String[] fileArr = stageParamInfo.getFileArr();
				if (fileArr != null && fileArr.length > 0) {
					String[] fileNameArr = new String[fileArr.length];
					for (int i = 0; i < fileArr.length; i++) {
						fileNameArr[i] = FileNameUtils.getName(fileArr[i]);
					}
					stageParamInfo.setFileNameArr(fileNameArr);
				}
			}
			stageParamInfo.setTableBean(tableBean);
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.SUCCEED.getCode(), "执行成功");
			LOGGER.info("------------------表" + collectTableBean.getTable_name()
					+ "数据库直连采集卸数阶段成功------------------执行时间为："
					+ (System.currentTimeMillis() - startTime) / 1000 + "，秒");
		} catch (Exception e) {
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.FAILED.getCode(), e.getMessage());
			LOGGER.error(collectTableBean.getTable_name() + "数据库直连采集卸数阶段失败：", e);
		}
		//4.结束给stageParamInfo塞值
		JobStatusInfoUtil.endStageParamInfo(stageParamInfo, statusInfo, collectTableBean
				, AgentType.ShuJuKu.getCode());
		return stageParamInfo;
	}

	private void setTableBeanAndDataExtractionDef(String storeDataBaseCode, TableBean tableBean,
												  CollectTableBean collectTableBean) {
		//设置tableBean默认值
		tableBean.setFile_code(storeDataBaseCode);
		//列分隔符为默认值
		tableBean.setColumn_separator(Constant.DATADELIMITER);
		tableBean.setIs_header(IsFlag.Fou.getCode());
		//XXX 换行符默认使用linux的换行符
		tableBean.setRow_separator(Constant.DEFAULTLINESEPARATOR);
		tableBean.setFile_format(FileFormat.FeiDingChang.getCode());
		tableBean.setParseJson(new HashMap<>());
		//设置卸数默认值
		Data_extraction_def data_extraction_def = new Data_extraction_def();
		data_extraction_def.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
		data_extraction_def.setDatabase_code(storeDataBaseCode);
		data_extraction_def.setDbfile_format(FileFormat.FeiDingChang.getCode());
		data_extraction_def.setDatabase_separatorr(StringUtil.string2Unicode(Constant.DATADELIMITER));
		data_extraction_def.setIs_header(IsFlag.Fou.getCode());
		data_extraction_def.setFile_suffix("dat");
		data_extraction_def.setRow_separator(StringUtil.string2Unicode(Constant.DEFAULTLINESEPARATORSTR));
		data_extraction_def.setPlane_url(Constant.DBFILEUNLOADFOLDER);
		collectTableBean.setSelectFileFormat(FileFormat.FeiDingChang.getCode());
		List<Data_extraction_def> list = new ArrayList<>();
		list.add(data_extraction_def);
		collectTableBean.setData_extraction_def_list(list);
	}

	/**
	 * 判断是否都支持外部表
	 */
	public static boolean doAllSupportExternal(List<DataStoreConfBean> dataStoreConfBeanList) {
		boolean flag = true;
		for (DataStoreConfBean storeConfBean : dataStoreConfBeanList) {
			if (IsFlag.Fou.getCode().equals(storeConfBean.getIs_hadoopclient())) {
				//有一个不支持外部表，直接给false，然后退出循环
				flag = false;
				break;
			}
		}
		return flag;
	}

	/**
	 * 获取卸数文件的编码
	 */
	public static String getStoreDataBaseCode(String table_name, List<DataStoreConfBean> dataStoreConfBeanList, String defaultCode) {
		String code = "";
		for (DataStoreConfBean storeConfBean : dataStoreConfBeanList) {
			Map<String, String> data_store_connect_attr = storeConfBean.getData_store_connect_attr();
			if(data_store_connect_attr !=null && !data_store_connect_attr.isEmpty()) {
				if (data_store_connect_attr.get(StorageTypeKey.database_code) != null) {
					//为空则说明第一次进来
					if (StringUtil.isEmpty(code)) {
						code = data_store_connect_attr.get(StorageTypeKey.database_code);
					} else {
						if (!code.equals(data_store_connect_attr.get(StorageTypeKey.database_code))) {
							throw new AppSystemException("表" + table_name + "数据库直连采集选择多个存储层，存储层的编码" +
									"不一致，分别为" + DataBaseCode.ofValueByCode(code) + "、" + DataBaseCode.ofValueByCode(
									data_store_connect_attr.get(StorageTypeKey.database_code)) + "，请配置多个任务执行！");
						}
					}
				}
			}
		}
		//TODO 这里页面存储层配置要改成代码项
		for (DataBaseCode typeCode : DataBaseCode.values()) {
			if (typeCode.getValue().equalsIgnoreCase(code)) {
				code = typeCode.getCode();
				break;
			}
		}
		if (StringUtil.isEmpty(code)) {
			code = defaultCode;
		}
		return code;
	}


	@Override
	public int getStageCode() {
		return StageConstant.UNLOADDATA.getCode();
	}

}
