package hrds.agent.job.biz.core;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import hrds.agent.job.biz.bean.JobStatusInfo;
import hrds.agent.job.biz.bean.MetaInfoBean;
import hrds.agent.job.biz.bean.ObjectCollectParamBean;
import hrds.agent.job.biz.bean.ObjectTableBean;
import hrds.agent.job.biz.core.objectstage.*;
import hrds.agent.job.biz.utils.JobStatusInfoUtil;
import hrds.commons.utils.Constant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.List;

@DocClass(desc = "执行半结构化对象采集作业实现类", author = "zxz", createdate = "2019/10/23 17:50")
public class ObjectCollectJobImpl implements JobInterface {
	//打印日志
	private static final Logger log = LogManager.getLogger();
	//半结构化对象采集设置表对象
	private final ObjectCollectParamBean objectCollectParamBean;
	//多条半结构化对象采集存储到hadoop存储信息实体合集
	private final ObjectTableBean objectTableBean;

	/**
	 * 半结构化对象采集的作业实现类构造方法.
	 *
	 * @param objectCollectParamBean Object_collect
	 *                               含义：半结构化对象采集设置表对象
	 *                               取值范围：所有这张表不能为空的字段的值必须有，为空则会抛异常
	 * @param objectTableBean        List<ObjectCollectParamBean>
	 *                               含义：多条半结构化对象采集存储到hadoop配置信息实体合集
	 *                               取值范围：所有这个实体不能为空的字段的值必须有，为空则会抛异常
	 */
	public ObjectCollectJobImpl(ObjectCollectParamBean objectCollectParamBean, ObjectTableBean objectTableBean) {
		this.objectCollectParamBean = objectCollectParamBean;
		this.objectTableBean = objectTableBean;
	}

	@Method(desc = "执行半结构化对象采集的作业",
			logicStep = "1、设置作业ID" +
					"2、构建每个阶段具体的实现类，按照顺序执行(卸数,数据加载,数据登记)" +
					"3、构建责任链，串起每个阶段" +
					"4、按照顺序从第一个阶段开始执行作业")
	@Return(desc = "封装有作业状态信息的实体类对象", range = "JobStatusInfo类对象，不会为null")
	@Override
	public JobStatusInfo runJob() {
		String statusFilePath = Constant.JOBINFOPATH + objectCollectParamBean.getOdc_id()
				+ File.separator + objectTableBean.getOcs_id() + File.separator + Constant.JOBFILENAME;
		//JobStatusInfo对象，表示一个作业的状态
		JobStatusInfo jobStatus = JobStatusInfoUtil.getStartJobStatusInfo(statusFilePath,
				objectTableBean.getOcs_id(), objectTableBean.getEn_name());
		//2、构建每个阶段具体的实现类，目前先按照完整顺序执行(卸数,上传,数据加载,计算增量,数据登记)，
		// 后期可改造为按照配置构建采集阶段
		JobStageInterface unloadData = new ObjectUnloadDataStageImpl(objectCollectParamBean, objectTableBean);
		//上传
		JobStageInterface upload = new ObjectUploadStageImpl(objectTableBean);
		//加载
		JobStageInterface dataLoading = new ObjectLoadingDataStageImpl(objectTableBean);
		//增量
		JobStageInterface calIncrement = new ObjectCalIncrementStageImpl(objectTableBean);
		//登记
		JobStageInterface dataRegistration = new ObjectRegistrationStageImpl(objectTableBean);
		//利用JobStageController构建本次数据库直连采集作业流程
		JobStageController controller = new JobStageController();
		//TODO 永远保证五个阶段，在每个阶段内部设置更合理的状态，比如直接加载时，unloadData和upload阶段的状态设置为跳过
		//3、构建责任链，串起每个阶段
		controller.registerJobStage(unloadData, upload, dataLoading, calIncrement, dataRegistration);
		//4、按照顺序从第一个阶段开始执行作业
		try {
			jobStatus = controller.handleStageByOrder(statusFilePath, jobStatus);
		} catch (Exception e) {
			//TODO 是否记录日志待讨论,因为目前的处理逻辑是数据库直连采集发生的所有checked
			// 类型异常全部向上抛，抛到这里统一处理
			log.error("对象采集异常", e);
		}
		return jobStatus;
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
