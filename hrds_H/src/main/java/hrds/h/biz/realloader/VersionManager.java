package hrds.h.biz.realloader;

import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.entity.Datatable_field_info;
import hrds.commons.entity.Dm_operation_info;
import hrds.commons.utils.Constant;
import hrds.h.biz.config.MarketConf;

import java.util.List;
import java.util.Optional;

public class VersionManager {
    private final DatabaseWrapper db;
    private final List<Datatable_field_info> fields;
    private final String tableName;
    private final String etlDate;
    private final Long datatableId;

    public VersionManager(MarketConf conf) {
        this.fields = conf.getDatatableFields();
        this.tableName = conf.getTableName();
        this.etlDate = conf.getEtlDate();
        this.datatableId = Long.parseLong(conf.getDatatableId());
        db = new DatabaseWrapper();
        db.beginTrans();
    }

    /**
     * 如果存在 end_date 为 00000000 的就说明有无效记录
     * 此时需要做以下几件事：
     * 1.把表rename成当前有效版本的日期后缀的表，然后重新建表
     * 2.把元数据库中的有效的变为过期，把无效的变为有效
     *
     * @return 是否是版本过期的
     */
    boolean isVersionExpire() {
        return fields.stream()
                .anyMatch(field -> Constant.INITDATE.equals(field.getEnd_date()) ||
                        Constant.INVDATE.equals(field.getEnd_date()));
    }

    /**
     * @return 需要重命名的表名
     */
    String getRenameTableName() {
        return tableName + "_" + etlDate.substring(2);
    }

    /**
     * 把有效变为过期
     * 把失效变为有效
     */
    void updateFieldVersion() {
        SqlOperator.execute(db,"update datatable_field_info set end_date = ? where datatable_id = ?" +
                        " and end_date = ?", etlDate, datatableId, Constant.INVDATE);
        SqlOperator.execute(db, "update datatable_field_info set start_date = ?, end_date = ? where datatable_id = ?" +
                        " and end_date = ?", etlDate, Constant.MAXDATE, datatableId, Constant.INITDATE);
    }

    void updateSqlVersion() {
        Optional<Dm_operation_info> dmOperationInfo = SqlOperator.queryOneObject(db, Dm_operation_info.class, "select * from " + Dm_operation_info.TableName +
                " where datatable_id = ? and end_date = ?", datatableId, Constant.INITDATE);
        // 没有无效数据则不管，如果有无效数据，则把有效数据变成过期数据，把无效数据变成有效数据
        if (!dmOperationInfo.isPresent()) return;

        // 有效变失效
        db.execute("update dm_operation_info set end_date = ? where datatable_id = ? and end_date = ?",
                etlDate, datatableId, Constant.INVDATE);
        // 无效变有效
        db.execute("update dm_operation_info set start_date = ?, end_date = ? where datatable_id = ? and end_date = ?",
                etlDate, Constant.MAXDATE, datatableId, Constant.INITDATE);
    }

    void rollBack() {
        if (db != null) {
            db.rollback();
        }
    }

    void commit() {
        if (db != null) {
            db.commit();
        }
    }
}
