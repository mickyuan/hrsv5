package hrds.h.biz.realloader;


import hrds.h.biz.service.AbstractBusiness;
import hrds.h.biz.service.FirstLoad;
import hrds.h.biz.service.NonFirstLoad;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Closeable;

public interface Loader extends FirstLoad, NonFirstLoad, Closeable {
    Log logger = LogFactory.getLog(Loader.class);

}
