package hrds.h.biz.realloader;

/**
 * @author mick
 * @title: JobState
 * @projectName hrsv5
 * @description: TODO
 * @date 20-4-9下午4:50
 */
public interface JobState {

    /**
     * 启动作业
     */
    void startJob();

    /**
     * 完成作业
     */
    void endJob(boolean isSuccessful);
}
