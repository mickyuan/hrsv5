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

        if (conf.isFirstLoad()) {
            logger.info("======================= 首次执行 =======================");
            logger.info("======================= 主作业执行 =======================");
            loader.firstLoad();
            logger.info("======================= 后置作业执行 =======================");
            loader.finalWork();
        } else {
            logger.info("======================= 非首次执行 =======================");
            logger.info("======================= 前置作业执行 =======================");
            logger.info("======================= 主作业执行 =======================");
            //该变量表示追加，替换还是增量
            String storageType = conf.getDmDatatable().getStorage_type();
            if (StorageType.TiHuan.getCode().equals(storageType)) {
                logger.info("======================= 替换 =======================");
                loader.replace();
            } else if (StorageType.ZhuiJia.getCode().equals(storageType)) {
                logger.info("======================= 追加 =======================");
                /**
                 * 支持追加重跑，前提是该loader实现了{@link NonFirstLoad#restore()}
                 */
                restore(loaderName);
                loader.append();
            } else if (StorageType.ZengLiang.getCode().equals(storageType)) {
                logger.info("======================= 增量 =======================");
                /**
                 * 支持增量重跑，前提是该loader实现了{@link NonFirstLoad#restore()}
                 */
                restore(loaderName);
                loader.increment();
            } else {
                throw new AppSystemException("无效的进数方式: " + storageType);
            }
            logger.info("======================= 后置作业执行 =======================");
            loader.finalWork();
        }

    }

    private void restore(String loaderName) {
        if (conf.isRerun()) {
            logger.info("此任务在日期 " + conf.getEtlDate() + " 已运行过，恢复数据到上次跑批完的数据状态: " + loaderName);
            loader.restore();
        }
    }

}
