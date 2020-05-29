package hrds.h.biz.realloader;

import hrds.commons.exception.AppSystemException;
import hrds.h.biz.service.BusinessForStorageType;
import hrds.h.biz.service.ILoadBussiness;

/**
 * Loader 执行类
 * @Author: Mick Yuan
 * @Date:
 * @Since jdk1.8
 */
public class LoaderExecutor {

    private ILoadBussiness iLoadBussiness;
    private JobContext jobContext;

    /**
     * 注册 loader
     * @param loader loader实现
     * @return 当前对象
     */
    public LoaderExecutor register(Loader loader) {
        this.iLoadBussiness = new BusinessForStorageType(loader);
        return this;
    }

    /**
     * 设置作业上下文对象
     * @param jobContext 作业上下文对象
     * @return
     */
    public LoaderExecutor setJobContext(JobContext jobContext) {
        this.jobContext = jobContext;
        return this;
    }

    /**
     * 执行loader作业
     * 上下对象记录成功还是失败
     */
    public void execute() {
        //作业成功还是异常完成
        boolean success = false;
        try {
            jobContext.startJob();
            iLoadBussiness.eventLoad();
            success = true;
        } finally {
            jobContext.endJob(success);
        }

    }
}
