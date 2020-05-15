package hrds.h.biz.realloader;


import hrds.h.biz.config.MarketConf;
import hrds.h.biz.service.FirstLoad;
import hrds.h.biz.service.NonFirstLoad;
import hrds.h.biz.service.PreFinalLoad;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;

public interface Loader extends FirstLoad, NonFirstLoad, PreFinalLoad, Closeable {
    Logger logger = LogManager.getLogger(Loader.class);

    default void logDebug(String errorMessage){
        if(logger.isDebugEnabled()){
            logger.debug(errorMessage);
        }
    }

    default void logInfo(String message){
        if(logger.isInfoEnabled()){
            logger.info(message);
        }
    }

    MarketConf getConf();
}
