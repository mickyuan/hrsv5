package hrds.h.biz.realloader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author mick
 * @title: JobState
 * @projectName hrsv5
 * @description: TODO
 * @date 20-4-9下午4:50
 */
public interface JobState {
    Log logger = LogFactory.getLog(JobState.class);
    /**
     * 启动作业
     */
    void startJob();

    /**
     * 完成作业
     */
    void endJob(boolean isSuccessful);
}
