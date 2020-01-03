package hrds.agent.job.biz.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import hrds.agent.job.biz.bean.JobStatusInfo;
import hrds.agent.job.biz.bean.StageParamInfo;
import hrds.agent.job.biz.bean.StageStatusInfo;
import hrds.agent.job.biz.constant.RunStatusConstant;
import hrds.agent.job.biz.constant.StageConstant;
import hrds.agent.job.biz.utils.EnumUtil;
import hrds.agent.job.biz.utils.FileUtil;
import hrds.commons.codes.DataBaseCode;
import hrds.commons.exception.AppSystemException;

import java.io.File;

@DocClass(desc = "作业阶段控制器,用于注册各个阶段，形成一个采集作业阶段链条，从第一个阶段开始执行，" +
		"并且根据上一阶段的执行状态判断下一阶段是否执行", author = "WangZhengcheng")
public class JobStageController {

	//责任链头节点
	private JobStageInterface head;
	//责任链尾节点
	private JobStageInterface last;

	@Method(desc = "注册阶段,使用可变参数，供外部传入1-N个阶段进行注册", logicStep = "" +
			"1、调用本类中的重载的registerJobStage方法完成阶段注册")
	@Param(name = "stages", desc = "要被注册到责任链中的一个采集作业的0-N个阶段", range = "可变参数，可以传入0-N个JobStageInterface实例")
	public void registerJobStage(JobStageInterface... stages) {
		for (JobStageInterface stage : stages) {
			registerJobStage(stage);
		}
	}

	@Method(desc = "重载注册阶段，本类内部使用，真正处理阶段处理逻辑", logicStep = "" +
			"1、如果责任链头节点为空，说明整个责任链为空，构建只有一个节点的责任链" +
			"2、如果责任链头节点不为空，则设置尾节点的下一个节点是传入的stage节点，stage节点变为尾节点")
	@Param(name = "stage", desc = "要被注册到责任链中的一个采集作业的1个阶段", range = "JobStageInterface实例")
	private void registerJobStage(JobStageInterface stage) {
		//1、如果责任链头节点为空，说明整个责任链为空，构建只有一个节点的责任链
		if (head == null) {
			last = head = stage;
		} else {
			last.setNextStage(stage);
			last = stage;
		}
	}

	@Method(desc = "按照顺序从采集作业的第一个阶段开始执行", logicStep = "" +
			"1、根据状态文件路径获取状态文件" +
			"2、判断JobStatusInfo中的StageParamInfo是否为空，如果为空，说明卸数阶段还未执行，new一个新的StageParamInfo对象进行卸数" +
			"   如果不为空，说明可能本次handleStageByOrder可能是断点续读，使用从文件中获取到的StageParamInfo对象执行责任链的头节点" +
			"3、按照责任链依次执行每个节点，首先执行头节点" +
			"   3-1、如果头节点执行成功" +
			"       3-1-1、根据阶段设置阶段状态" +
			"       3-1-2、将需要在各个阶段中传递的参数重新存到jobInfo中" +
			"   3-2、如果头节点执行失败" +
			"       3-2-1、根据阶段设置阶段状态" +
			"       3-2-2、设置错误信息" +
			"4、写文件" +
			"5、执行除第一阶段外剩下的其他阶段，采取的方式仍然是先读取文件，然后执行，执行完之后写文件" +
			"6、最终作业每个阶段全部执行完成，返回")
	@Param(name = "statusFilePath", desc = "作业状态文件路径", range = "不为空")
	@Return(desc = "作业状态信息", range = "不会为null")
	public JobStatusInfo handleStageByOrder(String statusFilePath, JobStatusInfo jobStatusInfo) throws Exception {

		if(StringUtil.isBlank(statusFilePath)){
			throw new AppSystemException("状态文件路径不能为空");
		}

		if(jobStatusInfo == null){
			throw new AppSystemException("作业状态对象不能为空");
		}

		//1、根据状态文件路径获取状态文件
		File file = new File(statusFilePath);

		/*
		 * 2、判断JobStatusInfo中的StageParamInfo是否为空，如果为空，说明卸数阶段还未执行，new一个新的StageParamInfo对象进行卸数
		 * 如果不为空，说明可能本次handleStageByOrder可能是断点续读，使用从文件中获取到的StageParamInfo对象执行责任链的头节点
		 */
		StageParamInfo stageParamInfo = jobStatusInfo.getStageParamInfo();
		if(stageParamInfo == null){
			stageParamInfo = new StageParamInfo();
		}
		//3、按照责任链依次执行每个节点，首先执行头节点
		StageParamInfo firstStageParamInfo = head.handleStage(stageParamInfo);
		jobStatusInfo = setStageStatus(firstStageParamInfo.getStatusInfo(), jobStatusInfo);
		//3-1、如果头节点执行成功
		if (firstStageParamInfo.getStatusInfo().getStatusCode() == RunStatusConstant.SUCCEED.getCode()) {
			//3-1-1、根据阶段设置阶段状态
			//3-1-2、将需要在各个阶段中传递的参数重新存到jobInfo中
			jobStatusInfo.setStageParamInfo(firstStageParamInfo);
		}
		//3-2、如果头节点执行失败
		else if(firstStageParamInfo.getStatusInfo().getStatusCode() == RunStatusConstant.FAILED.getCode()){
			//3-2-1、根据阶段设置阶段状态
			StageConstant stageConstant = EnumUtil.getEnumByCode(StageConstant.class,
					firstStageParamInfo.getStatusInfo().getStageNameCode());
			//3-2-2、设置错误信息
			if(stageConstant != null){
				jobStatusInfo.setExceptionInfo(stageConstant.getDesc() + "阶段执行失败");
			}else{
				throw new AppSystemException("系统不能识别该作业阶段");
			}
		}else{
			throw new AppSystemException("除了成功和失败，其他状态目前暂时未做处理");
		}

		//4、写文件
		FileUtil.writeString2File(file, JSON.toJSONString(jobStatusInfo), DataBaseCode.UTF_8.getValue());

		//5、执行除第一阶段外剩下的其他阶段，采取的方式仍然是先读取文件，然后执行，执行完之后写文件
		JobStageInterface stage = head;
		while ((stage = stage.getNextStage()) != null) {
			//重新读文件，获取jobStatusInfo
			jobStatusInfo = JSONObject.parseObject(FileUtil.readFile2String(file), JobStatusInfo.class);
			//如果从文件中获取到的作业状态信息中异常信息不为空，说明当前作业执行失败，直接返回jobStatusInfo
			if(StringUtil.isNotBlank(jobStatusInfo.getExceptionInfo())){
				return jobStatusInfo;
			}
			StageParamInfo otherParamInfo = stage.handleStage(jobStatusInfo.getStageParamInfo());
			jobStatusInfo = setStageStatus(otherParamInfo.getStatusInfo(), jobStatusInfo);
			if (otherParamInfo.getStatusInfo().getStatusCode() == RunStatusConstant.SUCCEED.getCode()) {
				jobStatusInfo.setStageParamInfo(otherParamInfo);
			}else if(otherParamInfo.getStatusInfo().getStatusCode() == RunStatusConstant.FAILED.getCode()){
				StageConstant stageConstant = EnumUtil.getEnumByCode(StageConstant.class,
						firstStageParamInfo.getStatusInfo().getStageNameCode());
				if(stageConstant != null){
					jobStatusInfo.setExceptionInfo(stageConstant.getDesc() + "阶段执行失败");
				}else{
					throw new AppSystemException("系统不能识别该作业阶段" + otherParamInfo.getStatusInfo().getStatusCode());
				}
			}else{
				throw new AppSystemException("除了成功和失败，其他状态目前暂时未做处理");
			}
			//写文件
			FileUtil.writeString2File(file, JSON.toJSONString(jobStatusInfo), DataBaseCode.UTF_8.getValue());
		}
		//6、最终作业每个阶段全部执行完成，返回
		return jobStatusInfo;
	}

	@Method(desc = "每个阶段执行完之后，无论成功还是失败，记录阶段执行状态", logicStep = "" +
			"1、通过stageStatus得到当前任务的阶段" +
			"2、判断处于哪一个阶段，在jobStatus设置当前阶段的状态信息")
	@Param(name = "stageStatus", desc = "阶段状态信息", range = "StageStatusInfo实体类对象")
	@Param(name = "jobStatus", desc = "作业状态信息", range = "JobStatusInfo实体类对象")
	@Return(desc = "查询结果集，查询出的结果可能有0-N条", range = "不会为null")
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
		} else if (stage == StageConstant.DATAREGISTRATION){
			jobStatus.setDataRegistrationStatus(stageStatus);
		} else{
			throw new AppSystemException("系统不支持的采集阶段");
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
