package hrds.h.biz.service;


import hrds.commons.codes.StorageType;
import hrds.commons.exception.AppSystemException;
import hrds.h.biz.realloader.Loader;

public final class BusinessForStorageType extends AbstractBusiness {

    public BusinessForStorageType(Loader load) {

        super(load);
    }

    /**
     *
     */
    @Override
    public void eventLoad() {

        String loaderName = load.getClass().getSimpleName();
        logger.info("开始计算并导入数据，导入类型为：" + loaderName);

        if (conf.isFirstLoad()) {
            logger.info("首次执行=======================");
            load.firstLoad();
        } else {
            logger.info("第二次执行=======================");
            //该变量表示追加，替换还是增量
            String storageType = conf.getDmDatatable().getStorage_type();
            if (StorageType.TiHuan.getCode().equals(storageType)) {
                logger.info("替换=======================");
                load.replace();
            } else if (StorageType.ZhuiJia.getCode().equals(storageType)) {
                logger.info("追加=======================");
                /**
                 * 支持追加重跑，前提是该loader实现了{@link NonFirstLoad#restore()}
                 */
                restore(loaderName);
                load.append();
            } else if (StorageType.ZengLiang.getCode().equals(storageType)) {
                logger.info("增量=======================");
                /**
                 * 支持追加重跑，前提是该loader实现了{@link NonFirstLoad#restore()}
                 */
                restore(loaderName);
                load.increment();
            } else {
                throw new AppSystemException("无效的进数方式: " + storageType);
            }
        }

    }

    private void restore(String loaderName) {
        if (conf.isRerun()) {
            logger.info("此任务在日期 " + conf.getEtlDate() + " 已运行过，恢复数据到上次跑批完的数据状态: " + loaderName);
            load.restore();
        }
    }

}
