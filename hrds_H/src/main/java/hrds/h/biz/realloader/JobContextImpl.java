package hrds.h.biz.realloader;

import fd.ng.core.utils.DateUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.commons.codes.JobExecuteState;
import hrds.commons.entity.Dm_datatable;
import hrds.commons.entity.Dtab_relation_store;
import hrds.commons.exception.AppSystemException;
import hrds.h.biz.config.MarketConf;

/**
 * 作业上下文对象
 * @Author: Mick Yuan
 * @Date:
 * @Since jdk1.8
 */
public class JobContextImpl implements JobContext {

    MarketConf conf;
    private final Dtab_relation_store dtabRelationStore;
    private final Dm_datatable dmDatatable;
    private long jobStartTime;
    /**
     * 程序运行过程中，JVM退出执行job错误退出
     */
    final Thread shutdownThread;

    public JobContextImpl(MarketConf conf) {
        this.conf = conf;
        this.dtabRelationStore = conf.getDtabRelationStore();
        this.dmDatatable = conf.getDmDatatable();
        shutdownThread = new Thread(() -> endJob(false));
    }

    @Override
    public void startJob() {

        //如果该作业的数据库状态是运行中的话，则异常退出
        String jobStateCode = dtabRelationStore.getIs_successful();
        if (JobExecuteState.YunXing.getCode().equals(jobStateCode)) {
            throw new AppSystemException("作业正在运行中，请勿重复提交。");
        }

        //设置作业状态的运行中
        DatabaseWrapper db = null;
        try {
            db = new DatabaseWrapper();
            db.beginTrans();
            dtabRelationStore.setIs_successful(JobExecuteState.YunXing.getCode());
            dtabRelationStore.update(db);
            db.commit();
        } catch (Exception e) {
            if (db != null) {
                db.rollback();
            }
            throw e;
        } finally {
            if (db != null) {
                db.close();
            }
        }

        Runtime.getRuntime().addShutdownHook(shutdownThread);

        jobStartTime = System.currentTimeMillis();

        logger.info(String.format("[%s] 集市作业开始运行 [%s]: etlDate: %s, reRun: %s.",
                DateUtil.getDateTime(DateUtil.DATETIME_ZHCN), conf.getDatatableId(),
                conf.getEtlDate(), conf.isRerun()));
    }

    @Override
    public void endJob(boolean isSuccessful) {

        //根据返回结果，将数据库中的运行状态改为相应状态 完成 or 失败
        String jobCode = isSuccessful ?
                JobExecuteState.WanCheng.getCode() : JobExecuteState.ShiBai.getCode();
        DatabaseWrapper db = null;
        try {
            db = new DatabaseWrapper();
            db.beginTrans();
            //更新运行状态
            dtabRelationStore.setIs_successful(jobCode);
            dtabRelationStore.update(db);
            // 更新跑批日期
            dmDatatable.setDatac_date(DateUtil.getSysDate());
            dmDatatable.setDatac_time(DateUtil.getSysTime());
            dmDatatable.setEtl_date(conf.getEtlDate());
            dmDatatable.update(db);
            db.commit();
        } catch (Exception e) {
            if (db != null) {
                db.rollback();
            }
            throw e;
        } finally {
            Runtime.getRuntime().removeShutdownHook(shutdownThread);
            if (db != null) {
                db.close();
            }
            logger.info(String.format("[%s] 集市作业运行结束 [%s]: successful: %s, lastTime: %s s.",
                    DateUtil.getDateTime(DateUtil.DATETIME_ZHCN), conf.getDatatableId(),
                    isSuccessful, (System.currentTimeMillis() - jobStartTime) / 1000F));

        }
    }
}
