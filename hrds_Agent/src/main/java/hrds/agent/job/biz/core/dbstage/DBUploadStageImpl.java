package hrds.agent.job.biz.core.dbstage;

import hrds.agent.job.biz.bean.StageStatusInfo;
import hrds.agent.job.biz.constant.RunStatusConstant;
import hrds.agent.job.biz.constant.StageConstant;
import hrds.agent.job.biz.core.AbstractJobStage;
import hrds.agent.job.biz.utils.DateUtil;
import hrds.agent.job.biz.utils.ScriptExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ClassName: DBUploadStageImpl <br/>
 * Function: 数据上传阶段  <br/>
 * Reason: 数据库直连采集
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public class DBUploadStageImpl extends AbstractJobStage {
    private final static Logger LOGGER = LoggerFactory.getLogger(DBUploadStageImpl.class);

    private final String jobId;
    private final String[] localFiles;
    private final String remoteDir;

    public DBUploadStageImpl(String jobId, String[] localFiles, String remoteDir) {
        this.jobId = jobId;
        this.localFiles = localFiles;
        this.remoteDir = remoteDir;
    }

    /*
    * 1、创建卸数阶段状态信息，更新作业ID,阶段名，阶段开始时间
    * 2、调用方法，进行文件上传，文件数组和上传目录由构造器传入
    * */
    @Override
    public StageStatusInfo handleStage() {
        LOGGER.info("------------------数据库直连采集上传阶段开始------------------");
        //1、创建卸数阶段状态信息，更新作业ID,阶段名，阶段开始时间
        StageStatusInfo statusInfo = new StageStatusInfo();
        statusInfo.setStageNameCode(StageConstant.UPLOAD.getCode());
        statusInfo.setJobId(jobId);
        statusInfo.setStartDate(DateUtil.getLocalDateByChar8());
        statusInfo.setStartTime(DateUtil.getLocalTimeByChar6());
        ScriptExecutor executor = new ScriptExecutor();
        try {
            //2、调用方法，进行文件上传，文件数组和上传目录由构造器传入
            executor.executeUpload2Hdfs(localFiles, remoteDir);
        }catch (IllegalStateException | InterruptedException e){
            statusInfo.setStatusCode(RunStatusConstant.FAILED.getCode());
            statusInfo.setMessage(FAILD_MSG+ "：" + e.getMessage());
            LOGGER.info("------------------数据库直连采集上传阶段失败------------------");
            LOGGER.error(FAILD_MSG+ "：{}", e.getMessage());
        }
        statusInfo.setStatusCode(RunStatusConstant.SUCCEED.getCode());
        statusInfo.setEndDate(DateUtil.getLocalDateByChar8());
        statusInfo.setStartTime(DateUtil.getLocalTimeByChar6());
        LOGGER.info("------------------数据库直连采集上传阶段成功------------------");
        return statusInfo;
    }
}
