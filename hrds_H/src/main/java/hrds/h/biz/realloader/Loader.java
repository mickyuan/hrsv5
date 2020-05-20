package hrds.h.biz.realloader;


import hrds.h.biz.config.MarketConf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;

public interface Loader extends Closeable {
    Logger logger = LogManager.getLogger(Loader.class);

    /**
     * 确保relation存在
     */
    void ensureRelation();

    /**
     * 追加
     *
     * @return
     * @author yuanqi
     * Date:2018年5月13日下午10:29:22
     * @throws Exception
     * @since JDK 1.7
     */
    void append();

    /**
     * 替换
     *
     * @return
     * @author yuanqi
     * Date:2018年5月13日下午10:29:30
     * @throws Exception
     * @since JDK 1.7
     */
    void replace() ;

    /**
     * 增量
     *
     * @return
     * @author yuanqi
     * Date:2018年5月13日下午10:29:37
     * @since JDK 1.7
     */
    void increment();

    /**
     * 恢复数据到上次跑批结束
     *
     * @return
     * @author xxx
     * Date:2018年10月31日10:25:36
     * @since JDK 1.7
     */
    void restore();

    /**
     * 后置作业
     */
    void finalWork();

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
