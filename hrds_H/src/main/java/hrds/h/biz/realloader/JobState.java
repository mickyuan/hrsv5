package hrds.h.biz.realloader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author mick
 * @title: JobState
 * @projectName hrsv5
 * @description: TODO
 * @date 20-4-9下午4:50
 */
public interface JobState {
    Logger logger = LogManager.getLogger(JobState.class);
    /**
     * 启动作业
     */
    void startJob();

    /**
     * 完成作业
     */
    void endJob(boolean isSuccessful);
}
