package hrds.agent.job.biz.core.objectstage;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import hrds.agent.job.biz.bean.ObjectCollectParamBean;
import hrds.agent.job.biz.bean.ObjectTableBean;
import hrds.agent.job.biz.bean.StageParamInfo;
import hrds.agent.job.biz.bean.StageStatusInfo;
import hrds.agent.job.biz.constant.RunStatusConstant;
import hrds.agent.job.biz.constant.StageConstant;
import hrds.agent.job.biz.core.AbstractJobStage;
import hrds.agent.job.biz.utils.JobStatusInfoUtil;
import hrds.commons.codes.AgentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//XXX 对象采集要不要加卸数，通过看代码，理解出来的以前的对象采集是没有卸数这一步
@DocClass(desc = "半结构化对象采集卸数实现", author = "zxz", createdate = "2019/10/24 11:43")
public class ObjectUnloadDataStageImpl extends AbstractJobStage {
	//打印日志
	private static final Logger LOGGER = LogManager.getLogger();

	private final ObjectCollectParamBean objectCollectParamBean;
	private final ObjectTableBean objectTableBean;

	public ObjectUnloadDataStageImpl(ObjectCollectParamBean objectCollectParamBean,
									 ObjectTableBean objectTableBean) {
		this.objectCollectParamBean = objectCollectParamBean;
		this.objectTableBean = objectTableBean;
	}

	@Method(desc = "半结构化对象采集，半结构化对象采集卸数实现，处理完成后，无论成功还是失败，" +
			"将相关状态信息封装到StageStatusInfo对象中返回", logicStep = "")
	@Return(desc = "StageStatusInfo是保存每个阶段状态信息的实体类", range = "不会为null,StageStatusInfo实体类对象")
	@Override
	public StageParamInfo handleStage(StageParamInfo stageParamInfo) {
		//开始时间
		long startTime = System.currentTimeMillis();
		//TODO 这边所有的注释都叫半结构化对象采集
		LOGGER.info("------------------表" + objectTableBean.getEn_name()
				+ "半结构化对象采集卸数阶段开始------------------");
		//1、创建卸数阶段状态信息，更新作业ID,阶段名，阶段开始时间
		StageStatusInfo statusInfo = new StageStatusInfo();
		JobStatusInfoUtil.startStageStatusInfo(statusInfo, objectTableBean.getOcs_id(),
				StageConstant.UNLOADDATA.getCode());
		try {
			//开始执行防止重跑，先把抽取的文件的目录重命名
//			renameUnloadDir(collectTableBean);
			//执行卸数
//			TableBean tableBean = CollectTableHandleFactory.getCollectTableHandleInstance(sourceDataConfBean)
//					.generateTableInfo(sourceDataConfBean, collectTableBean);
			//增量卸数
//			incrementExtract(stageParamInfo, tableBean, collectTableBean, sourceDataConfBean);
//			stageParamInfo.setTableBean(tableBean);
			//根据系统参数配置是否写数据字典，因为页面提供了下载任务的数据字典，每次跑批再去写数据字典会影响效率
//			if (JobConstant.ISWRITEDICTIONARY) {
//				//数据字典的路径
//				String dictionaryPath = FileNameUtils.normalize(JobConstant.DICTIONARY + File.separator
//						, true);
//				//写数据字典
//				DataExtractUtil.writeDataDictionary(dictionaryPath, collectTableBean.getTable_name(),
//						tableBean.getColumnMetaInfo(), tableBean.getColTypeMetaInfo(),
//						CollectTableBeanUtil.getTransSeparatorExtractionList(
//								collectTableBean.getData_extraction_def_list()), collectTableBean.getUnload_type(),
//						tableBean.getPrimaryKeyInfo(), tableBean.getInsertColumnInfo(), tableBean.getUpdateColumnInfo()
//						, tableBean.getDeleteColumnInfo(), collectTableBean.getHbase_name(),
//						sourceDataConfBean.getTask_name());
//			}
			//卸数成功，删除重命名的目录
//			deleteRenameDir(collectTableBean);
			//卸数成功，写ok文件
//			createOKFile(collectTableBean);
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.SUCCEED.getCode(), "执行成功");
			LOGGER.info("------------------表" + objectTableBean.getEn_name()
					+ "半结构化对象采集卸数阶段成功------------------执行时间为："
					+ (System.currentTimeMillis() - startTime) / 1000 + "，秒");
		} catch (Exception e) {
			//卸数失败，删除本次卸数的目录，恢复数据
			try {
//				restoreRenameDir(collectTableBean);
			} catch (Exception e1) {
				LOGGER.warn(objectTableBean.getEn_name() + "半结构化对象采集，恢复上次卸数数据失败", e);
			}
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.FAILED.getCode(), e.getMessage());
			LOGGER.error(objectTableBean.getEn_name() + "半结构化对象采集卸数阶段失败：", e);
		}
		//结束给stageParamInfo塞值
		JobStatusInfoUtil.endStageParamInfo(stageParamInfo, statusInfo, objectTableBean
				, AgentType.ShuJuKu.getCode());
		return stageParamInfo;
	}

	@Override
	public int getStageCode() {
		return StageConstant.UNLOADDATA.getCode();
	}
}
