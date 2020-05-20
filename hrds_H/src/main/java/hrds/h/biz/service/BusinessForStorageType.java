package hrds.h.biz.service;


import hrds.commons.codes.StorageType;
import hrds.commons.exception.AppSystemException;
import hrds.h.biz.realloader.Loader;

public final class BusinessForStorageType extends AbstractBusiness {

    public BusinessForStorageType(Loader loader) {

        super(loader);
    }

    /**
     *
     */
    @Override
    public void eventLoad() {

        String loaderName = loader.getClass().getSimpleName();
        logger.info("开始计算并导入数据，导入类型为：" + loaderName);
        loader.ensureRelation();
        try {
            logger.info("======================= 主作业执行 =======================");
            String storageType = conf.getDmDatatable().getStorage_type();
            if (StorageType.TiHuan.getCode().equals(storageType)) {
                replace();
            } else if (StorageType.ZhuiJia.getCode().equals(storageType)) {
                append();
            } else if (StorageType.ZengLiang.getCode().equals(storageType)) {
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
            } catch (Exception ignored) {
            }
            throw e;
        }
    }

    private void restore() {
        if (conf.isRerun()) {
            logger.info("此任务在日期 " + conf.getEtlDate() + " 已运行过，恢复数据到上次跑批完的数据状态.");
            loader.restore();
        }
    }

    private void replace() {
        logger.info("======================= 替换 =======================");
        loader.replace();
    }

    private void append() {
        logger.info("======================= 追加 =======================");
        // 支持追加重跑，前提是该loader实现了{@link NonFirstLoad#restore()}
        restore();
        loader.append();
    }

    private void increment() {
        logger.info("======================= 增量 =======================");
        // 支持增量重跑，前提是该loader实现了{@link NonFirstLoad#restore()}
        restore();
        loader.increment();
    }

}
