package hrds.h.biz.realloader;

import hrds.commons.exception.AppSystemException;
import hrds.h.biz.service.BusinessForStorageType;
import hrds.h.biz.service.ILoadBussiness;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

        boolean success = false;
        try {
            jobState.startJob();
            iLoadBussiness.eventLoad();
            success = true;
        } catch (Throwable e) {
            throw new AppSystemException(e);
        } finally {
            jobState.endJob(success);
        }

    }


    @Override
    public void close() throws IOException {
        if (iLoadBussiness != null)
            iLoadBussiness.close();
    }


}
