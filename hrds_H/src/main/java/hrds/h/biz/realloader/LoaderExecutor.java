package hrds.h.biz.realloader;

import hrds.commons.exception.AppSystemException;
import hrds.h.biz.service.BusinessForStorageType;
import hrds.h.biz.service.ILoadBussiness;

import java.io.Closeable;
import java.io.IOException;

/**
 * @Author: Mick Yuan
 * @Date:
 * @Since jdk1.8
 */
public class LoaderExecutor implements Closeable {

    private ILoadBussiness iLoadBussiness;
    private JobState jobState;


    public LoaderExecutor register(Loader loader) {
        this.iLoadBussiness = new BusinessForStorageType(loader);
        return this;
    }

    public LoaderExecutor setJobState(JobState jobState) {
        this.jobState = jobState;
        return this;
    }

    public void execute() {

        try {
            jobState.startJob();
            iLoadBussiness.eventLoad();
        } catch (Throwable e) {
            jobState.endJob(false);
            throw new AppSystemException(e);
        }
        jobState.endJob(true);

    }


    @Override
    public void close() throws IOException {
        if (iLoadBussiness != null)
            iLoadBussiness.close();
    }


}
