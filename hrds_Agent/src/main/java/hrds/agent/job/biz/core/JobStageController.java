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

    private JobStageInterface head;
    private JobStageInterface last;

    //注册阶段,使用可变参数，可以注册1-N个阶段
    public void registerJobStage(JobStageInterface... stages){
        for (JobStageInterface stage : stages) {
            registerJobStage(stage);
        }
    }

    private void registerJobStage(JobStageInterface stage){
        if (head == null) {
            last = head = stage;
        }else {
            last.setNextStage(stage);
            last = stage;
        }
    }

    /** 
    * @Description: 按照顺序从采集作业的第一个阶段开始执行
    * @Param:  statusFilePath：作业状态文件目录
    * @return:  jobStatus：作业状态对象
    * @Author: WangZhengcheng 
    * @Date: 2019/8/13
    */
    /*
    * 1、从第一个阶段开始执行，并判断执行结果
    * 2、若第一阶段执行成功，记录阶段执行状态，并继续向下面的阶段执行
    * 3、若第一阶段执行失败，目前的处理逻辑是直接记录错误信息，然后返回jobStatusInfo
    * 4、若除第一阶段外的其他阶段执行失败，记录错误信息，尚欠是否继续运行下一阶段的逻辑
    * */
    public JobStatusInfo handleStageByOrder(String statusFilePath, JobStatusInfo jobStatus) throws Exception{

        JobStatusInfo jobInfo = jobStatus;
        //1、从第一个阶段开始执行
        StageStatusInfo firstStageStatus = head.handleStage();
        //判断第一阶段的执行结果
        if(firstStageStatus.getStatusCode() == RunStatusConstant.SUCCEED.getCode()) {
            //2、若第一阶段执行成功，记录阶段执行状态，并继续向下执行
            jobInfo = setStageStatus(firstStageStatus, jobInfo);
            //TODO 讨论，是否由该种方式来记录运行时状态
            ProductFileUtil.createStatusFile(statusFilePath, JSONObject.toJSONString(jobInfo));
            JobStageInterface stage = head;
            while((stage = stage.getNextStage()) != null){
                StageStatusInfo stageStatusInfo = stage.handleStage();
                if(stageStatusInfo.getStatusCode() == RunStatusConstant.SUCCEED.getCode()){
                    jobInfo = setStageStatus(stageStatusInfo, jobInfo);
                }else{
                    //TODO 下面的处理方式待商榷
                    // 4、若除第一阶段外的其他阶段执行失败，记录错误信息，尚欠是否继续运行下一阶段的逻辑
                    jobInfo = setStageStatus(stageStatusInfo, jobInfo);
                    jobInfo.setExceptionInfo(EnumUtil.getEnumByCode(StageConstant.class, stageStatusInfo.getStageNameCode()).getDesc() + "阶段执行失败");
                }
                //记录每个阶段的状态
                ProductFileUtil.createStatusFile(statusFilePath, JSONObject.toJSONString(jobInfo));
            }
        }else{
            //TODO 下面的处理方式待商榷
            //3、若第一阶段执行失败，目前的处理逻辑是直接记录错误信息，然后返回jobStatusInfo
            jobInfo = setStageStatus(firstStageStatus, jobInfo);
            jobInfo.setExceptionInfo(EnumUtil.getEnumByCode(StageConstant.class, firstStageStatus.getStageNameCode()).getDesc() + "阶段执行失败");
        }
        jobInfo.setRunStatus(1000);
        ProductFileUtil.createStatusFile(statusFilePath, JSONObject.toJSONString(jobInfo));
        //此时代表作业到达了终态，所以不记录作业状态，由控制整个作业的TaskControl来记录
        return jobInfo;
    }

    /*
    * 每个阶段执行完之后，无论成功还是失败，记录阶段执行状态
    * */
    private JobStatusInfo setStageStatus(StageStatusInfo stageStatus, JobStatusInfo jobStatus){
        StageConstant stage = EnumUtil.getEnumByCode(StageConstant.class, stageStatus.getStageNameCode());
        if (stage == null) {
            throw new AppSystemException("获取阶段信息失败");
        }
        if(stage.equals(StageConstant.UNLOADDATA)){
            jobStatus.setUnloadDataStatus(stageStatus);
        }else if (stage.equals(StageConstant.UPLOAD)){
            jobStatus.setUploadStatus(stageStatus);
        }else if (stage.equals(StageConstant.DATALOADING)){
            jobStatus.setDataLodingStatus(stageStatus);
        }else if (stage.equals(StageConstant.CALINCREMENT)){
            jobStatus.setCalIncrementStatus(stageStatus);
        }else{
            jobStatus.setDataRegistrationStatus(stageStatus);
        }
        return jobStatus;
    }

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
