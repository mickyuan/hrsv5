package hrds.h.biz.realloader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 作业上下文对象接口
 * @author mick
 * @date 20-4-9下午4:50
 */
public interface JobContext {
    Logger logger = LogManager.getLogger();
    /**
     * 启动作业
     */
    void startJob();

    /**
     * 完成作业
     */
    void endJob(boolean isSuccessful);
}
