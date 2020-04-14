package hrds.h.biz.realloader;


import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.commons.codes.JobExecuteState;
import hrds.commons.entity.Dm_relation_datatable;
import hrds.commons.exception.AppSystemException;

/**
 * @Author: Mick Yuan
 * @Date:
 * @Since jdk1.8
 */
public class JobStateImpl implements JobState {

    private Dm_relation_datatable dmRelationDatatable;

    public JobStateImpl(Dm_relation_datatable dmRelationDatatable) {
        this.dmRelationDatatable = dmRelationDatatable;
    }

    @Override
    public void startJob() {

        //如果该作业的数据库状态是运行中的话，则异常退出
        String jobStateCode = dmRelationDatatable.getIs_successful();
        if (JobExecuteState.YunXing.getCode().equals(jobStateCode)) {
            throw new AppSystemException("作业正在运行中，请勿重复提交。");
        }

        //设置作业状态的运行中
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            dmRelationDatatable.setIs_successful(JobExecuteState.YunXing.getCode());
            dmRelationDatatable.update(db);
        }

    }

    @Override
    public void endJob(boolean isSuccessful) {

        //根据返回结果，将数据库中的运行状态改为相应状态 完成 or 失败
        String jobCode = isSuccessful ?
                JobExecuteState.WanCheng.getCode() : JobExecuteState.ShiBai.getCode();
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            dmRelationDatatable.setIs_successful(jobCode);
            dmRelationDatatable.update(db);
        }
    }

}
