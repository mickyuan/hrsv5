package hrds.agent.job.biz.core;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.JobStatusInfo;
import hrds.agent.job.biz.bean.MetaInfoBean;
import hrds.agent.job.biz.bean.SourceDataConfBean;
import hrds.agent.job.biz.core.jdbcdirectstage.*;
import hrds.agent.job.biz.utils.JobStatusInfoUtil;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.Constant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@DocClass(desc = "数据库直连采集的作业实现", author = "zxz")
public class JdbcDirectJobImpl implements JobInterface {
	//打印日志
	private static final Logger LOGGER = LogManager.getLogger();
	private final CollectTableBean collectTableBean;
	private final SourceDataConfBean sourceDataConfBean;
	private final static Map<String, Thread> threadMap = new ConcurrentHashMap<>();

	public JdbcDirectJobImpl(SourceDataConfBean sourceDataConfBean, CollectTableBean collectTableBean) {
		this.sourceDataConfBean = sourceDataConfBean;
		this.collectTableBean = collectTableBean;
	}

	@Method(desc = "重写接口中的runJob()方法，实现数据库直连采集的逻辑", logicStep = "" +
			"1、设置作业ID，开始时间" +
			"2、构建每个阶段具体的实现类对象" +
			"3、构建责任链，串起每个阶段" +
			"4、按照顺序从第一个阶段开始执行作业" +
			"5、阶段执行完成后，写meta信息")
	@Return(desc = "封装有作业状态信息的实体类对象", range = "JobStatusInfo类对象，不会为null")
	@Override
	public JobStatusInfo runJob() {
		String statusFilePath = Constant.JOBINFOPATH + sourceDataConfBean.getDatabase_id()
				+ File.separator + collectTableBean.getTable_id() + File.separator + Constant.JOBFILENAME;
		//JobStatusInfo对象，表示一个作业的状态
		JobStatusInfo jobStatusInfo = JobStatusInfoUtil.getStartJobStatusInfo(statusFilePath,
				collectTableBean.getTable_id(), collectTableBean.getTable_name());
		//2、构建每个阶段具体的实现类，目前先按照完整顺序执行(卸数,上传,数据加载,计算增量,数据登记)，
		// 后期可改造为按照配置构建采集阶段
		JobStageInterface unloadData = new JdbcDirectUnloadDataStageImpl(sourceDataConfBean, collectTableBean);
		//上传
		JobStageInterface upload = new JdbcDirectUploadStageImpl(sourceDataConfBean, collectTableBean);
		//加载
		JobStageInterface dataLoading = new JdbcDirectDataLoadingStageImpl(collectTableBean);
		//增量
		JobStageInterface calIncrement = new JdbcDirectCalIncrementStageImpl(collectTableBean);
		//登记
		JobStageInterface dataRegistration = new JdbcDirectDataRegistrationStageImpl(collectTableBean);
		//利用JobStageController构建本次数据库直连采集作业流程
		JobStageController controller = new JobStageController();
		//TODO 永远保证五个阶段，在每个阶段内部设置更合理的状态，比如直接加载时，unloadData和upload阶段的状态设置为跳过
		//3、构建责任链，串起每个阶段
		controller.registerJobStage(unloadData, upload, dataLoading, calIncrement, dataRegistration);
		//4、按照顺序从第一个阶段开始执行作业
		try {
			if (collectTableBean.getInterval_time() != null && collectTableBean.getInterval_time() > 0) {
				try {
					if (threadMap.get(collectTableBean.getTable_id()) == null) {
						threadMap.put(collectTableBean.getTable_id(), Thread.currentThread());
					} else {
						//如果存在这个表实时采集在执行，先打断之前执行的，再重新执行
						threadMap.get(collectTableBean.getTable_id()).interrupt();
					}
					//TODO 这里如果同一个任务多张表，有一张表选择按照频率采集，页面通过立即执行的方式执行则会导致没有返回，因为实时的一直在执行
					int interval_time = collectTableBean.getInterval_time();
					do {
						jobStatusInfo = controller.handleStageByOrder(statusFilePath, jobStatusInfo);
						TimeUnit.SECONDS.sleep(interval_time);
					} while (!DateUtil.getSysDate().equals(collectTableBean.getOver_date()));
				} catch (Exception e) {
					throw new AppSystemException("数据库直连采集实时采集异常", e);
				}
			} else {
				jobStatusInfo = controller.handleStageByOrder(statusFilePath, jobStatusInfo);
			}
		} catch (Exception e) {
			//TODO 是否记录日志待讨论,因为目前的处理逻辑是数据库直连采集发生的所有checked
			// 类型异常全部向上抛，抛到这里统一处理
			LOGGER.error("数据库采集异常", e);
		}
		return jobStatusInfo;
	}

	//下面两个方法所有JobInterface接口实现类都应该是这么实现
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
