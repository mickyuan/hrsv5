package hrds.agent.job.biz.core;

import fd.ng.core.annotation.DocClass;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.JobStatusInfo;
import hrds.agent.job.biz.bean.MetaInfoBean;
import hrds.agent.job.biz.bean.SourceDataConfBean;
import hrds.agent.job.biz.core.dfstage.*;
import hrds.agent.job.biz.utils.JobStatusInfoUtil;
import hrds.commons.utils.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

@DocClass(desc = "完成数据文件采集的作业实现")
public class DataFileJobImpl implements JobInterface {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataFileJobImpl.class);

	private CollectTableBean collectTableBean;
	private SourceDataConfBean sourceDataConfBean;

	public DataFileJobImpl(SourceDataConfBean sourceDataConfBean, CollectTableBean collectTableBean) {
		this.sourceDataConfBean = sourceDataConfBean;
		this.collectTableBean = collectTableBean;
	}

	@Override
	public JobStatusInfo runJob() {
		String statusFilePath = Constant.JOBINFOPATH + sourceDataConfBean.getDatabase_id()
				+ File.separator + collectTableBean.getTable_id() + File.separator + Constant.JOBFILENAME;
		//JobStatusInfo对象，表示一个作业的状态
		JobStatusInfo jobStatusInfo = JobStatusInfoUtil.getStartJobStatusInfo(statusFilePath,
				collectTableBean.getTable_id());
		//2、构建每个阶段具体的实现类，目前先按照完整顺序执行(卸数,上传,数据加载,计算增量,数据登记)，
		// 后期可改造为按照配置构建采集阶段
		JobStageInterface unloadData = new DFUnloadDataStageImpl(sourceDataConfBean, collectTableBean);
		//上传
		JobStageInterface upload = new DFUploadStageImpl(collectTableBean);
		//加载
		JobStageInterface dataLoading = new DFDataLoadingStageImpl(collectTableBean);
		//增量
		JobStageInterface calIncrement = new DFCalIncrementStageImpl(collectTableBean);
		//登记
		JobStageInterface dataRegistration = new DFDataRegistrationStageImpl(collectTableBean);
		//利用JobStageController构建本次数据库直连采集作业流程
		JobStageController controller = new JobStageController();
		//TODO 永远保证五个阶段，在每个阶段内部设置更合理的状态，比如直接加载时，unloadData和upload阶段的状态设置为跳过
		//3、构建责任链，串起每个阶段
		controller.registerJobStage(unloadData, upload, dataLoading, calIncrement, dataRegistration);
		//4、按照顺序从第一个阶段开始执行作业
		try {
			jobStatusInfo = controller.handleStageByOrder(statusFilePath, jobStatusInfo);
		} catch (Exception e) {
			//TODO 是否记录日志待讨论,因为目前的处理逻辑是数据库直连采集发生的所有checked
			// 类型异常全部向上抛，抛到这里统一处理
			LOGGER.error("数据库采集异常", e);
		}
		return jobStatusInfo;
	}

	@Override
	public List<MetaInfoBean> getMetaInfoGroup() {
		return null;
	}

	@Override
	public MetaInfoBean getMetaInfo() {
		return null;
	}

	@Override
	public JobStatusInfo call() {
		//多线程执行作业
		return runJob();
	}
}