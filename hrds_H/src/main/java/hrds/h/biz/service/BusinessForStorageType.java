package hrds.h.biz.service;


import hrds.commons.codes.StorageType;
import hrds.commons.exception.AppSystemException;
import hrds.h.biz.config.MarketConf;
import hrds.h.biz.realloader.Loader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

/**
 * 根据存储类型来运行的程序逻辑
 * 是loader基础实现的业务包装类
 * @author mick
 */
public final class BusinessForStorageType implements ILoadBussiness {
    static final Logger logger = LogManager.getLogger();

    protected Loader loader;
    protected MarketConf conf;

    public BusinessForStorageType(Loader loader) {
        this.loader = loader;
        conf = loader.getConf();
    }

    /**
     * 实际需要调用的方法
     */
    @Override
    public void eventLoad() {

        String loaderName = loader.getClass().getSimpleName();
        logger.info("开始计算并导入数据，导入类型为：" + loaderName);
        loader.ensureRelation();
        try {
            String storageType = conf.getDmDatatable().getStorage_type();

            //如果是非替换的情况下，要进行restore操作
            if (!StorageType.TiHuan.getCode().equals(storageType)) {
                restore();
            }

            logger.info("======================= 前置作业执行 =======================");
            loader.preWork();

            logger.info("======================= 主作业执行 =======================");
            if (StorageType.TiHuan.getCode().equals(storageType)) {
                replace();
            } else if (StorageType.ZhuiJia.getCode().equals(storageType)) {
                append();
            } else if (StorageType.QuanLiang.getCode().equals(storageType)) {
                increment();
            } else {
                throw new AppSystemException("无效的进数方式: " + storageType);
            }

            logger.info("======================= 后置作业执行 =======================");
            loader.finalWork();

        } catch (Exception e) {
            try {
                logger.warn("作业执行失败，执行回滚操作。");
                loader.restore();
                loader.handleException();
            } catch (Exception warn) {
                logger.warn("作业回滚异常： ", e);
            }
            try {
                throw e;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    private void restore() throws SQLException {
        if (conf.isRerun()) {
            logger.info("此任务在日期 " + conf.getEtlDate() + " 已运行过，恢复数据到上次跑批完的数据状态.");
            loader.restore();
        }
    }

    private void replace() {
        logger.info("======================= 替换 =======================");
        loader.replace();
    }

    private void append() throws SQLException {
        logger.info("======================= 追加 =======================");
        loader.append();
    }

    private void increment() throws SQLException {
        logger.info("======================= 增量 =======================");
        loader.increment();
    }

}
