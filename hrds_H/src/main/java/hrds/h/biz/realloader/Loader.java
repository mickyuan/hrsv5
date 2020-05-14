package hrds.h.biz.realloader;


import hrds.h.biz.config.MarketConf;
import hrds.h.biz.service.FirstLoad;
import hrds.h.biz.service.NonFirstLoad;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;

public interface Loader extends FirstLoad, NonFirstLoad, Closeable {
    Logger logger = LogManager.getLogger(Loader.class);

    MarketConf getConf();
}
