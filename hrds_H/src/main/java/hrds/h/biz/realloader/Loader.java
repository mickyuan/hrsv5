package hrds.h.biz.realloader;


import hrds.h.biz.config.MarketConf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;

/**
 * 所有 Loader 类的 业务接口
 * 需要实现
 * 1.确保 relation 存在
 * 2.追加替换删除
 * 3.数据恢复
 * 4.后置作业
 * 4.获取集市配置实体
 * @author mick
 */
public interface Loader extends Closeable {
    Logger logger = LogManager.getLogger();

    /**
     * 确保relation存在
     */
    void ensureRelation();

    /**
     * 追加
     *
     * @return
     * @throws Exception
     * @author yuanqi
     * Date:2018年5月13日下午10:29:22
     * @since JDK 1.7
     */
    void append();

    /**
     * 替换
     *
     * @return
     * @throws Exception
     * @author yuanqi
     * Date:2018年5月13日下午10:29:30
     * @since JDK 1.7
     */
    void replace();

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

    /**
     * log warn 级别输出
     *
     * @param message 日志输出信息
     */
    default void logWarn(String message) {
        if (logger.isWarnEnabled()) {
            logger.warn(message);
        }
    }

    /**
     * log debug 级别输出
     *
     * @param message 日志输出信息
     */
    default void logDebug(String message) {
        if (logger.isDebugEnabled()) {
            logger.debug(message);
        }
    }

    /**
     * log info 级别输出
     *
     * @param message 日志输出信息
     */
    default void logInfo(String message) {
        if (logger.isInfoEnabled()) {
            logger.info(message);
        }
    }

    /**
     * 确保所有loader都有获取集市配置实体的能力
     * @return 集市配置实体
     */
    MarketConf getConf();
}
