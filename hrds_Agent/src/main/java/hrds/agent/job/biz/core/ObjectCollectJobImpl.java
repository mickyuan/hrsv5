package hrds.agent.job.biz.core;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.utils.DateUtil;
import hrds.agent.job.biz.bean.JobStatusInfo;
import hrds.agent.job.biz.bean.MetaInfoBean;
import hrds.agent.job.biz.bean.ObjectCollectParamBean;
import hrds.agent.job.biz.core.objectstage.ObjectLoadingDataStageImpl;
import hrds.agent.job.biz.core.objectstage.ObjectRegistrationStageImpl;
import hrds.agent.job.biz.core.objectstage.ObjectUnloadDataStageImpl;
import hrds.commons.entity.Object_collect;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

@DocClass(desc = "执行半结构化对象采集作业实现类", author = "zxz", createdate = "2019/10/23 17:50")
public class ObjectCollectJobImpl implements JobInterface {
	//打印日志
	private static final Log log = LogFactory.getLog(ObjectCollectJobImpl.class);
	//作业执行完成的mate信息
//	private MetaInfoBean mateInfo = new MetaInfoBean();
	//半结构化对象采集设置表对象
	private Object_collect object_collect;
	//多条半结构化对象采集存储到hadoop存储信息实体合集
	private List<ObjectCollectParamBean> objectCollectParamBeanList;
	//JobStatusInfo对象，表示一个作业的状态
	private JobStatusInfo jobStatus;
	//XXX 待讨论
	private final static String statusFilePath = "";

	/**
	 * 半结构化对象采集的作业实现类构造方法.
	 *
	 * @param object_collect             Object_collect
	 *                                   含义：半结构化对象采集设置表对象
	 *                                   取值范围：所有这张表不能为空的字段的值必须有，为空则会抛异常
	 * @param objectCollectParamBeanList List<ObjectCollectParamBean>
	 *                                   含义：多条半结构化对象采集存储到hadoop配置信息实体合集
	 *                                   取值范围：所有这个实体不能为空的字段的值必须有，为空则会抛异常
	 * @param jobStatus                  JobStatusInfo
	 *                                   含义：JobStatusInfo对象，表示一个作业的状态
	 *                                   取值范围：不能为空
	 */
	public ObjectCollectJobImpl(Object_collect object_collect, List<ObjectCollectParamBean> objectCollectParamBeanList
			, JobStatusInfo jobStatus) {
		this.object_collect = object_collect;
		this.objectCollectParamBeanList = objectCollectParamBeanList;
		this.jobStatus = jobStatus;
	}

	@Method(desc = "执行半结构化对象采集的作业",
			logicStep = "1、设置作业ID" +
					"2、构建每个阶段具体的实现类，按照顺序执行(卸数,数据加载,数据登记)" +
					"3、构建责任链，串起每个阶段" +
					"4、按照顺序从第一个阶段开始执行作业")
	@Override
	public JobStatusInfo runJob() {
		//1、设置作业ID
		jobStatus.setJobId(String.valueOf(object_collect.getOdc_id()));
		//设置作业开始时间
		jobStatus.setStartDate(DateUtil.getSysDate());
		jobStatus.setStartTime(DateUtil.getSysTime());
		//2、构建每个阶段具体的实现类，按照顺序执行(卸数,数据加载,数据登记)
		//空实现
		JobStageInterface unloadData = new ObjectUnloadDataStageImpl();
		JobStageInterface upload = new ObjectLoadingDataStageImpl(this.object_collect,
				this.objectCollectParamBeanList);
		//空实现
		JobStageInterface dataRegistration = new ObjectRegistrationStageImpl();
		//利用JobStageController构建本次半结构化对象采集作业流程
		JobStageController controller = new JobStageController();
		//3、构建责任链，串起每个阶段
		controller.registerJobStage(unloadData, upload, dataRegistration);
		//4、按照顺序从第一个阶段开始执行作业
		try {
			//XXX 这里需要决定每个状态执行完成数据存到哪里，文件？mapDB?palDB?,哪个目录？
			jobStatus = controller.handleStageByOrder(statusFilePath, jobStatus);
		} catch (Exception e) {
			log.error("非结构化对象采集异常", e);
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
