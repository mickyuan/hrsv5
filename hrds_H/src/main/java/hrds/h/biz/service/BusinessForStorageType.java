package hrds.h.biz.service;


import hrds.commons.codes.StorageType;
import hrds.commons.exception.AppSystemException;
import hrds.h.biz.realloader.Loader;

public class BusinessForStorageType extends AbstractBusiness {

    public BusinessForStorageType(Loader load) {

        super(load);
    }

    /**
     */
    @Override
    public void eventLoad() {

        String loaderName = load.getClass().getSimpleName();
        logger.info(" 开始计算并导入数据，导入类型为：" + loaderName);

        if (conf.isFirstLoad()) {
            logger.info("首次执行=======================");
            load.firstLoadData();
        } else {
            logger.info("第二次执行=======================");
            //该变量表示追加，替换还是增量
            String storageType = conf.getDmDatatable().getStorage_type();
            if (StorageType.TiHuan.getCode().equals(storageType)) {
                logger.info("替换=======================");
                load.replaceData();
            } else if (StorageType.ZhuiJia.getCode().equals(storageType)) {
                logger.info("追加=======================");
                /*
                 * 如果跑批日期已经追加成功了，就执行重跑追加，否则均追加
                 */
                if (conf.isRerun()) {
                    logger.info("此任务已运行成功，重跑任务: " + loaderName);
                    load.reAppendData();
                }
                load.appendData();
            } else if (StorageType.ZengLiang.getCode().equals(storageType)) {
                logger.info("增量=======================");
                load.IncrementData();
            } else {
                throw new AppSystemException("无效的进数方式: " + storageType);
            }
        }

    }

}
