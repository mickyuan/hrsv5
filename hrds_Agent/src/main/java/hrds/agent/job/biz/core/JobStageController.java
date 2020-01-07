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
import java.io.IOException;

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
			"2、以下三种状态，需要从卸数阶段开始执行" +
			"   2-1、状态文件不存在，表示该采集任务没有执行过" +
			"   2-2、状态文件存在，且五个阶段的状态都是成功，表示重跑该任务" +
			"   2-3、状态文件存在，且卸数阶段失败，表示卸数失败，重跑卸数阶段" +
			"   2-4、执行头节点" +
			"   2-5、如果头节点执行成功，根据阶段设置阶段状态，将需要在各个阶段中传递的参数重新存到jobStatusInfo中，并写状态文件" +
			"   2-6、如果头节点执行失败，根据阶段设置阶段状态，将需要在各个阶段中传递的参数重新存到jobStatusInfo中，写状态文件，直接返回" +
			"3、执行除第一阶段外剩下的其他阶段" +
			"   3-1、读取状态文件，根据阶段状态编码获取当前阶段的执行状态" +
			"   3-2、如果当前阶段执行状态不为空，表示采集曾经执行过该阶段" +
			"   3-3、如果该阶段执行成功，跳过本次循环，执行下一阶段" +
			"   3-4、如果该阶段执行失败，则重跑当前阶段，当前阶段的处理逻辑和卸数阶段一致" +
			"   3-5、如果当前阶段执行状态为空，表示采集还没有执行当前阶段，则执行当前阶段")
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
		 * 2、以下三种状态，需要从卸数阶段开始执行
		 *      2-1、状态文件不存在，表示该采集任务没有执行过
		 *      2-2、状态文件存在，且五个阶段的状态都是成功，表示重跑该任务
		 *      2-3、状态文件存在，且卸数阶段失败，表示卸数失败，重跑卸数阶段
		 */
		//2-1、状态文件不存在，表示该采集任务没有执行过
		boolean fileFlag = file.exists();
		//2-2、状态文件存在，且五个阶段的状态都是成功，表示重跑该任务
		boolean redoFlag = false;
		if(fileFlag){
			StageStatusInfo unloadStatus = jobStatusInfo.getUnloadDataStatus();
			StageStatusInfo uploadStatus = jobStatusInfo.getUploadStatus();
			StageStatusInfo lodingStatus = jobStatusInfo.getDataLodingStatus();
			StageStatusInfo calIncrementStatus = jobStatusInfo.getCalIncrementStatus();
			StageStatusInfo registStatus = jobStatusInfo.getDataRegistrationStatus();

			if(unloadStatus != null && uploadStatus != null && lodingStatus != null && calIncrementStatus != null
					&& registStatus != null){
				int unloadStatusCode = unloadStatus.getStatusCode();
				int uploadStatusCode = uploadStatus.getStatusCode();
				int lodingStatusCode = lodingStatus.getStatusCode();
				int incrementStatusCode = calIncrementStatus.getStatusCode();
				int registStatusCode = registStatus.getStatusCode();

				int succeedCode = RunStatusConstant.SUCCEED.getCode();

				if(unloadStatusCode == succeedCode && uploadStatusCode == succeedCode && lodingStatusCode == succeedCode
						&& incrementStatusCode == succeedCode && registStatusCode == succeedCode){
					redoFlag = true;
				}
			}
		}
		//2-3、状态文件存在，且卸数阶段失败，表示卸数失败，重跑卸数阶段
		int unloadStatusCode = RunStatusConstant.SUCCEED.getCode();
		if(fileFlag && jobStatusInfo.getUnloadDataStatus() != null){
			unloadStatusCode = jobStatusInfo.getUnloadDataStatus().getStatusCode();
		}

		if(!fileFlag || redoFlag || (unloadStatusCode == RunStatusConstant.FAILED.getCode())){
			//2-4、执行头节点
			StageParamInfo stageParamInfo = jobStatusInfo.getStageParamInfo();
			if(stageParamInfo == null){
				stageParamInfo = new StageParamInfo();
			}
			StageParamInfo firstStageParamInfo = head.handleStage(stageParamInfo);
			jobStatusInfo = setStageStatus(firstStageParamInfo.getStatusInfo(), jobStatusInfo);
			//2-5、如果头节点执行成功，根据阶段设置阶段状态，将需要在各个阶段中传递的参数重新存到jobStatusInfo中，并写状态文件
			if (firstStageParamInfo.getStatusInfo().getStatusCode() == RunStatusConstant.SUCCEED.getCode()) {
				dealSucceedStage(file, jobStatusInfo, firstStageParamInfo);
			}
			//2-6、如果头节点执行失败，根据阶段设置阶段状态，将需要在各个阶段中传递的参数重新存到jobStatusInfo中，写状态文件，直接返回
			else if(firstStageParamInfo.getStatusInfo().getStatusCode() == RunStatusConstant.FAILED.getCode()){
				dealFailedStage(file, jobStatusInfo);
				return jobStatusInfo;
			}else{
				throw new AppSystemException("除了成功和失败，其他状态目前暂时未做处理");
			}
		}

		//3、执行除第一阶段外剩下的其他阶段
		JobStageInterface stage = head;
		while ((stage = stage.getNextStage()) != null) {
			//3-1、读取状态文件，根据阶段状态编码获取当前阶段的执行状态
			jobStatusInfo = JSONObject.parseObject(FileUtil.readFile2String(file), JobStatusInfo.class);
			StageStatusInfo currentStageStatus = getStageStatusByCode(stage.getStageCode(), jobStatusInfo);
			//3-2、如果当前阶段执行状态不为空，表示采集曾经执行过该阶段
			if(currentStageStatus != null){
				//3-3、如果该阶段执行成功，跳过本次循环，执行下一阶段
				if(currentStageStatus.getStatusCode() == RunStatusConstant.SUCCEED.getCode()){
					continue;
				}
				//3-4、如果该阶段执行失败，则重跑当前阶段，当前阶段的处理逻辑和卸数阶段一致
				else if(currentStageStatus.getStatusCode() == RunStatusConstant.FAILED.getCode()){
					StageParamInfo otherParamInfo = stage.handleStage(jobStatusInfo.getStageParamInfo());
					jobStatusInfo = setStageStatus(otherParamInfo.getStatusInfo(), jobStatusInfo);
					if (otherParamInfo.getStatusInfo().getStatusCode() == RunStatusConstant.SUCCEED.getCode()) {
						dealSucceedStage(file, jobStatusInfo, otherParamInfo);
					}else if(otherParamInfo.getStatusInfo().getStatusCode() == RunStatusConstant.FAILED.getCode()){
						dealFailedStage(file, jobStatusInfo);
						return jobStatusInfo;
					}else{
						throw new AppSystemException("除了成功和失败，其他状态目前暂时未做处理");
					}
				}else{
					throw new AppSystemException("除了成功和失败，其他状态目前暂时未做处理");
				}
			}
			//3-5、如果当前阶段执行状态为空，表示采集还没有执行当前阶段，则执行当前阶段
			else{
				StageParamInfo otherParamInfo = stage.handleStage(jobStatusInfo.getStageParamInfo());
				jobStatusInfo = setStageStatus(otherParamInfo.getStatusInfo(), jobStatusInfo);
				if (otherParamInfo.getStatusInfo().getStatusCode() == RunStatusConstant.SUCCEED.getCode()) {
					dealSucceedStage(file, jobStatusInfo, otherParamInfo);
				}else if(otherParamInfo.getStatusInfo().getStatusCode() == RunStatusConstant.FAILED.getCode()){
					dealFailedStage(file, jobStatusInfo);
					return jobStatusInfo;
				}else{
					throw new AppSystemException("除了成功和失败，其他状态目前暂时未做处理");
				}
			}
		}
		return jobStatusInfo;
	}

	@Method(desc = "处理执行成功的阶段", logicStep = "" +
			"1、将执行成功的阶段参数封装到作业状态中" +
			"2、将作业状态写到状态文件中")
	@Param(name = "statusFile", desc = "指向作业状态文件", range = "不为空")
	@Param(name = "jobStatusInfo", desc = "作业状态对象", range = "JobStatusInfo实体类对象")
	@Param(name = "stageParamInfo", desc = "stageParamInfo", range = "StageParamInfo实体类对象")
	private void dealSucceedStage(File statusFile, JobStatusInfo jobStatusInfo, StageParamInfo stageParamInfo) throws IOException{
		//1、将执行成功的阶段参数封装到作业状态中
		jobStatusInfo.setStageParamInfo(stageParamInfo);
		//2、将作业状态写到状态文件中
		FileUtil.writeString2File(statusFile, JSON.toJSONString(jobStatusInfo), DataBaseCode.UTF_8.getValue());
	}

	@Method(desc = "处理执行失败的阶段", logicStep = "" +
			"1、将作业状态写到状态文件中")
	@Param(name = "statusFile", desc = "指向作业状态文件", range = "不为空")
	@Param(name = "jobStatusInfo", desc = "作业状态对象", range = "JobStatusInfo实体类对象")
	private void dealFailedStage(File statusFile, JobStatusInfo jobStatusInfo) throws IOException{
		//1、将作业状态写到状态文件中
		FileUtil.writeString2File(statusFile, JSON.toJSONString(jobStatusInfo), DataBaseCode.UTF_8.getValue());
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

	@Method(desc = "根据采集阶段编码获取采集阶段状态", logicStep = "" +
			"1、通过stageCode得到当前任务的阶段" +
			"2、判断处于哪一个阶段，在jobStatus中获取当前阶段的状态信息")
	@Param(name = "stageCode", desc = "采集阶段编码", range = "不为空")
	@Param(name = "jobStatus", desc = "采集任务状态对象", range = "不为空")
	@Return(desc = "采集阶段状态", range = "StageStatusInfo类对象")
	private StageStatusInfo getStageStatusByCode(int stageCode, JobStatusInfo jobStatus){
		//1、通过stageCode得到当前任务的阶段
		StageConstant stage = EnumUtil.getEnumByCode(StageConstant.class, stageCode);
		if (stage == null) {
			throw new AppSystemException("获取阶段信息失败");
		}
		//2、判断处于哪一个阶段，在jobStatus中获取当前阶段的状态信息
		StageStatusInfo stageStatus;
		if (stage == StageConstant.UNLOADDATA) {
			stageStatus = jobStatus.getUnloadDataStatus();
		} else if (stage == StageConstant.UPLOAD) {
			stageStatus = jobStatus.getUploadStatus();
		} else if (stage == StageConstant.DATALOADING) {
			stageStatus = jobStatus.getDataLodingStatus();
		} else if (stage == StageConstant.CALINCREMENT) {
			stageStatus = jobStatus.getCalIncrementStatus();
		} else if (stage == StageConstant.DATAREGISTRATION){
			stageStatus = jobStatus.getDataRegistrationStatus();
		} else{
			throw new AppSystemException("系统不支持的采集阶段");
		}
		return stageStatus;
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
