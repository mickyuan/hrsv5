package hrds.agent.job.biz.core;

import com.alibaba.fastjson.JSONObject;
import hrds.agent.job.biz.bean.JobStatusInfo;
import hrds.agent.job.biz.bean.StageStatusInfo;
import hrds.agent.job.biz.constant.RunStatusConstant;
import hrds.agent.job.biz.constant.StageConstant;
import hrds.agent.job.biz.utils.EnumUtil;
import hrds.agent.job.biz.utils.ProductFileUtil;
import hrds.commons.exception.AppSystemException;

/**
 * ClassName: JobStageController <br/>
 * Function: 作业阶段控制器 <br/>
 * Reason: 用于注册各个阶段，形成一个采集作业阶段链条，从第一个阶段开始执行，并且根据上一阶段的执行状态判断下一阶段是否执行
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public class JobStageController {

	//责任链头节点
	private JobStageInterface head;
	//责任链尾节点
	private JobStageInterface last;

	/**
	 * 注册阶段,使用可变参数，供外部传入1-N个阶段进行注册
	 *
	 * 1、调用本类中的重载的registerJobStage方法完成阶段注册
	 *
	 * @Param: stages JobStageInterface
	 *         含义：要被注册到责任链中的一个采集作业的0-N个阶段
	 *         取值范围：可变参数，可以传入0-N个JobStageInterface实例
	 *
	 * @return: 无
	 *
	 * */
	public void registerJobStage(JobStageInterface... stages) {
		for (JobStageInterface stage : stages) {
			registerJobStage(stage);
		}
	}

	/**
	 * 重载注册阶段，本类内部使用，真正处理阶段处理逻辑
	 *
	 * 1、如果责任链头节点为空，说明整个责任链为空，构建只有一个节点的责任链
	 * 2、如果责任链头节点不为空，则设置尾节点的下一个节点是传入的stage节点，stage节点变为尾节点
	 *
	 * @Param: stages JobStageInterface
	 *         含义：要被注册到责任链中的一个采集作业的1个阶段
	 *         取值范围：JobStageInterface实例
	 *
	 * @return: 无
	 *
	 * */
	private void registerJobStage(JobStageInterface stage) {
		//1、如果责任链头节点为空，说明整个责任链为空，构建只有一个节点的责任链
		if (head == null) {
			last = head = stage;
		} else {
			last.setNextStage(stage);
			last = stage;
		}
	}

	/**
	 * 按照顺序从采集作业的第一个阶段开始执行
	 *
	 * 1、从第一个阶段开始执行，并判断执行结果
	 * 2、若第一阶段执行成功，记录阶段执行状态，并继续向下面的阶段执行
	 * 3、若第一阶段执行失败，目前的处理逻辑是直接记录错误信息，然后返回jobStatusInfo
	 * 4、若除第一阶段外的其他阶段执行失败，记录错误信息，尚欠是否继续运行下一阶段的逻辑
	 *
	 * @Param: statusFilePath String
	 *         含义：作业状态文件路径
	 *         取值范围：不为空
	 * @Param: jobStatus JobStatusInfo
	 *         含义：作业状态对象
	 *         取值范围：JobStatusInfo实体类对象
	 *
	 * @return: fd.ng.db.resultset.Result
	 *          含义：查询结果集，查询出的结果可能有0-N条
	 *          取值范围：不会为null
	 *
	 * */
	public JobStatusInfo handleStageByOrder(String statusFilePath, JobStatusInfo jobStatus)
			throws Exception {

		JobStatusInfo jobInfo = jobStatus;
		//1、从第一个阶段开始执行
		StageStatusInfo firstStageStatus = head.handleStage();
		//判断第一阶段的执行结果
		if (firstStageStatus.getStatusCode() == RunStatusConstant.SUCCEED.getCode()) {
			//2、若第一阶段执行成功，记录阶段执行状态，并继续向下执行
			jobInfo = setStageStatus(firstStageStatus, jobInfo);
			//TODO 讨论，是否由该种方式来记录运行时状态
			ProductFileUtil.createStatusFile(statusFilePath, JSONObject.toJSONString(jobInfo));
			JobStageInterface stage = head;
			while ((stage = stage.getNextStage()) != null) {
				StageStatusInfo stageStatusInfo = stage.handleStage();
				if (stageStatusInfo.getStatusCode() == RunStatusConstant.SUCCEED.getCode()) {
					jobInfo = setStageStatus(stageStatusInfo, jobInfo);
				} else {
					//TODO 下面的处理方式待商榷
					// 4、若除第一阶段外的其他阶段执行失败，记录错误信息，尚欠是否继续运行下一阶段的逻辑
					jobInfo = setStageStatus(stageStatusInfo, jobInfo);
					StageConstant stageConstant = EnumUtil.getEnumByCode(StageConstant.class,
							stageStatusInfo.getStageNameCode());
					if(stageConstant != null){
						jobInfo.setExceptionInfo(stageConstant.getDesc() + "阶段执行失败");
					}else{
						throw new AppSystemException("系统不能识别该作业阶段");
					}
				}
				//记录每个阶段的状态
				ProductFileUtil.createStatusFile(statusFilePath, JSONObject.toJSONString(jobInfo));
			}
		} else {
			//TODO 下面的处理方式待商榷
			//3、若第一阶段执行失败，目前的处理逻辑是直接记录错误信息，然后返回jobStatusInfo
			jobInfo = setStageStatus(firstStageStatus, jobInfo);
			StageConstant stageConstant = EnumUtil.getEnumByCode(StageConstant.class,
					firstStageStatus.getStageNameCode());
			if(stageConstant != null){
				jobInfo.setExceptionInfo(stageConstant.getDesc() + "阶段执行失败");
			}else{
				throw new AppSystemException("系统不能识别该作业阶段");
			}
		}
		jobInfo.setRunStatus(1000);
		ProductFileUtil.createStatusFile(statusFilePath, JSONObject.toJSONString(jobInfo));
		//此时代表作业到达了终态，所以不记录作业状态，由控制整个作业的TaskControl来记录
		return jobInfo;
	}

	/**
	 * 每个阶段执行完之后，无论成功还是失败，记录阶段执行状态
	 *
	 * 1、通过stageStatus得到当前任务的阶段
	 * 2、判断处于哪一个阶段，在jobStatus设置当前阶段的状态信息
	 *
	 * @Param: stageStatus StageStatusInfo
	 *         含义：阶段状态信息
	 *         取值范围：StageStatusInfo实体类对象
	 * @Param: jobStatus JobStatusInfo
	 *         含义：作业状态信息
	 *         取值范围：JobStatusInfo实体类对象
	 *
	 * @return: fd.ng.db.resultset.Result
	 *          含义：查询结果集，查询出的结果可能有0-N条
	 *          取值范围：不会为null
	 *
	 * */
	private JobStatusInfo setStageStatus(StageStatusInfo stageStatus, JobStatusInfo jobStatus) {
		//1、通过stageStatus得到当前任务的阶段
		StageConstant stage = EnumUtil.getEnumByCode(StageConstant.class,
				stageStatus.getStageNameCode());
		if (stage == null) {
			throw new AppSystemException("获取阶段信息失败");
		}
		//2、判断处于哪一个阶段，在jobStatus设置当前阶段的状态信息
		if (stage == StageConstant.UNLOADDATA) {
			jobStatus.setUnloadDataStatus(stageStatus);
		} else if (stage == StageConstant.UPLOAD) {
			jobStatus.setUploadStatus(stageStatus);
		} else if (stage == StageConstant.DATALOADING) {
			jobStatus.setDataLodingStatus(stageStatus);
		} else if (stage == StageConstant.CALINCREMENT) {
			jobStatus.setCalIncrementStatus(stageStatus);
		} else {
			jobStatus.setDataRegistrationStatus(stageStatus);
		}
		return jobStatus;
	}

	//成员变量的getter/seeter，由idea自动生成，没有处理逻辑
	public JobStageInterface getHead() {
		return head;
	}

	public void setHead(JobStageInterface head) {
		this.head = head;
	}

	public JobStageInterface getLast() {
		return last;
	}

	public void setLast(JobStageInterface last) {
		this.last = last;
	}
}
