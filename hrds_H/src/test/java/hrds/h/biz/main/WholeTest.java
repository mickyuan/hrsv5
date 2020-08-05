package hrds.h.biz.main;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.DateUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.codes.*;
import hrds.commons.collection.ConnectionTool;
import hrds.commons.entity.*;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.h.biz.MainClass;
import hrds.testbase.WebBaseTestCase;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DocClass(desc = "集市总测试类", author = "mick")
public class WholeTest extends WebBaseTestCase {


    @Test
    public void test1() throws IOException {
        //通过控制参数来达到不同入库方式
        final long dslId = MarketConfig.getLong("oracle_dsl_id");
        final long marketADslId = MarketConfig.getLong("pgsql_dsl_id");
        final long marketBDslId = MarketConfig.getLong("oracle_dsl_id");
        final StorageType storageType = StorageType.ZengLiang;
        final IsFlag isRepeat = IsFlag.Shi;
        final SqlEngine sqlEngine = SqlEngine.JDBC;
        processAll(dslId, marketADslId, marketBDslId, storageType, isRepeat, sqlEngine);
    }

    @Test
    public void test2() throws IOException {
        //通过控制参数来达到不同入库方式
        final long dslId = MarketConfig.getLong("oracle_dsl_id");
        final long marketADslId = MarketConfig.getLong("pgsql_dsl_id");
        final long marketBDslId = MarketConfig.getLong("oracle_dsl_id");
        final StorageType storageType = StorageType.ZhuiJia;
        final IsFlag isRepeat = IsFlag.Shi;
        final SqlEngine sqlEngine = SqlEngine.JDBC;
        processAll(dslId, marketADslId, marketBDslId, storageType, isRepeat, sqlEngine);
    }

    @Test
    public void test3() throws IOException {
        //通过控制参数来达到不同入库方式
        final long dslId = MarketConfig.getLong("oracle_dsl_id");
        final long marketADslId = MarketConfig.getLong("pgsql_dsl_id");
        final long marketBDslId = MarketConfig.getLong("oracle_dsl_id");
        final StorageType storageType = StorageType.TiHuan;
        final IsFlag isRepeat = IsFlag.Shi;
        final SqlEngine sqlEngine = SqlEngine.JDBC;
        processAll(dslId, marketADslId, marketBDslId, storageType, isRepeat, sqlEngine);
    }

    @Test
    public void test4() throws IOException {
        //通过控制参数来达到不同入库方式
        final long dslId = MarketConfig.getLong("oracle_dsl_id");
        final long marketADslId = MarketConfig.getLong("pgsql_dsl_id");
        final long marketBDslId = MarketConfig.getLong("oracle_dsl_id");
        final StorageType storageType = StorageType.ZengLiang;
        final IsFlag isRepeat = IsFlag.Fou;
        final SqlEngine sqlEngine = SqlEngine.JDBC;
        processAll(dslId, marketADslId, marketBDslId, storageType, isRepeat, sqlEngine);
    }

    @Test
    public void test5() throws IOException {
        //通过控制参数来达到不同入库方式
        final long dslId = MarketConfig.getLong("oracle_dsl_id");
        final long marketADslId = MarketConfig.getLong("pgsql_dsl_id");
        final long marketBDslId = MarketConfig.getLong("oracle_dsl_id");
        final StorageType storageType = StorageType.ZhuiJia;
        final IsFlag isRepeat = IsFlag.Fou;
        final SqlEngine sqlEngine = SqlEngine.JDBC;
        processAll(dslId, marketADslId, marketBDslId, storageType, isRepeat, sqlEngine);
    }

    @Test
    public void test6() throws IOException {
        //通过控制参数来达到不同入库方式
        final long dslId = MarketConfig.getLong("oracle_dsl_id");
        final long marketADslId = MarketConfig.getLong("pgsql_dsl_id");
        final long marketBDslId = MarketConfig.getLong("oracle_dsl_id");
        final StorageType storageType = StorageType.TiHuan;
        final IsFlag isRepeat = IsFlag.Fou;
        final SqlEngine sqlEngine = SqlEngine.JDBC;
        processAll(dslId, marketADslId, marketBDslId, storageType, isRepeat, sqlEngine);
    }

    @Test
    public void test7() throws IOException {
        //通过控制参数来达到不同入库方式
        final long dslId = MarketConfig.getLong("pgsql_dsl_id");
        final long marketADslId = MarketConfig.getLong("pgsql_dsl_id");
        final long marketBDslId = MarketConfig.getLong("pgsql_dsl_id");
        final StorageType storageType = StorageType.ZengLiang;
        final IsFlag isRepeat = IsFlag.Shi;
        final SqlEngine sqlEngine = SqlEngine.JDBC;
        processAll(dslId, marketADslId, marketBDslId, storageType, isRepeat, sqlEngine);
    }

    @Test
    public void test8() throws IOException {
        //通过控制参数来达到不同入库方式
        final long dslId = MarketConfig.getLong("pgsql_dsl_id");
        final long marketADslId = MarketConfig.getLong("pgsql_dsl_id");
        final long marketBDslId = MarketConfig.getLong("pgsql_dsl_id");
        final StorageType storageType = StorageType.ZhuiJia;
        final IsFlag isRepeat = IsFlag.Shi;
        final SqlEngine sqlEngine = SqlEngine.JDBC;
        processAll(dslId, marketADslId, marketBDslId, storageType, isRepeat, sqlEngine);
    }

    @Test
    public void test9() throws IOException {
        //通过控制参数来达到不同入库方式
        final long dslId = MarketConfig.getLong("pgsql_dsl_id");
        final long marketADslId = MarketConfig.getLong("pgsql_dsl_id");
        final long marketBDslId = MarketConfig.getLong("pgsql_dsl_id");
        final StorageType storageType = StorageType.TiHuan;
        final IsFlag isRepeat = IsFlag.Shi;
        final SqlEngine sqlEngine = SqlEngine.JDBC;
        processAll(dslId, marketADslId, marketBDslId, storageType, isRepeat, sqlEngine);
    }

    @Test
    public void test10() throws IOException {
        //通过控制参数来达到不同入库方式
        final long dslId = MarketConfig.getLong("pgsql_dsl_id");
        final long marketADslId = MarketConfig.getLong("pgsql_dsl_id");
        final long marketBDslId = MarketConfig.getLong("pgsql_dsl_id");
        final StorageType storageType = StorageType.ZengLiang;
        final IsFlag isRepeat = IsFlag.Fou;
        final SqlEngine sqlEngine = SqlEngine.JDBC;
        processAll(dslId, marketADslId, marketBDslId, storageType, isRepeat, sqlEngine);
    }

    @Test
    public void test11() throws IOException {
        //通过控制参数来达到不同入库方式
        final long dslId = MarketConfig.getLong("pgsql_dsl_id");
        final long marketADslId = MarketConfig.getLong("pgsql_dsl_id");
        final long marketBDslId = MarketConfig.getLong("pgsql_dsl_id");
        final StorageType storageType = StorageType.ZhuiJia;
        final IsFlag isRepeat = IsFlag.Fou;
        final SqlEngine sqlEngine = SqlEngine.JDBC;
        processAll(dslId, marketADslId, marketBDslId, storageType, isRepeat, sqlEngine);
    }

    @Test
    public void test12() throws IOException {
        //通过控制参数来达到不同入库方式
        final long dslId = MarketConfig.getLong("pgsql_dsl_id");
        final long marketADslId = MarketConfig.getLong("pgsql_dsl_id");
        final long marketBDslId = MarketConfig.getLong("pgsql_dsl_id");
        final StorageType storageType = StorageType.TiHuan;
        final IsFlag isRepeat = IsFlag.Fou;
        final SqlEngine sqlEngine = SqlEngine.JDBC;
        processAll(dslId, marketADslId, marketBDslId, storageType, isRepeat, sqlEngine);
    }

    private void processAll(long dslId, long marketADslId, long marketBDslId, StorageType storageType, IsFlag isRepeat, SqlEngine sqlEngine) throws IOException {
        final long datatableId = PrimayKeyGener.getNextId();
        final long marketAId = PrimayKeyGener.getNextId();
        final long marketBId = PrimayKeyGener.getNextId();
        //不然表名太长会导致 oracle中长度超过30位报错
        final String tableName = "market_" + String.valueOf(datatableId).substring(10, 18);
        final String sourceTableNameA = "marketA_" + datatableId;
        final String sourceTableNameB = "marketB_" + datatableId;

        try {
            initData(datatableId, tableName, marketAId, marketBId,
                    sourceTableNameA, sourceTableNameB, marketADslId, marketBDslId, dslId, isRepeat, sqlEngine, storageType);
            executeAndValidate(datatableId, tableName, sourceTableNameB, storageType, dslId, marketBDslId);
        } finally {
            cleanData(datatableId, tableName, marketADslId, marketBDslId,
                    marketAId, marketBId, sourceTableNameA, sourceTableNameB, dslId);
        }
    }

    private void initData(long datatableId, String tableName, long marketAId, long marketBId, String sourceTableNameA, String sourceTableNameB, long marketADslId, long marketBDslId,
                         long dslId, IsFlag isRepeat, SqlEngine sqlEngine, StorageType storageType) {
        System.out.println(datatableId);
        DatabaseWrapper db = null;
        DatabaseWrapper marketAConn = null;
        DatabaseWrapper marketBConn = null;
        try {
            db = new DatabaseWrapper();
            marketAConn = getConnFromDslId(db, marketADslId);
            marketBConn = getConnFromDslId(db, marketBDslId);
            marketAConn.beginTrans();
            marketBConn.beginTrans();
            db.beginTrans();
            insertDmDatatable(db, datatableId, tableName, isRepeat, sqlEngine, storageType);
            insertDatatableFieldInfo(db, datatableId);
            insertDmOperationInfo(db, datatableId, sourceTableNameA, sourceTableNameB);
            insertDtabRelationStore(db, dslId, datatableId);

            insertDmDatatable(db, marketAId, sourceTableNameA, isRepeat, sqlEngine, storageType);
            insertDatatableFieldInfo(db, marketAId);
            insertDmOperationInfo(db, marketAId, sourceTableNameA, sourceTableNameB);
            insertDtabRelationStore(db, marketADslId, marketAId);
            createAndInsertMarketA(marketAConn, sourceTableNameA);

            insertDmDatatable(db, marketBId, sourceTableNameB, isRepeat, sqlEngine, storageType);
            insertDatatableFieldInfo(db, marketBId);
            insertDmOperationInfo(db, marketBId, sourceTableNameA, sourceTableNameB);
            insertDtabRelationStore(db, marketBDslId, marketBId);
            createAndInsertMarketB(marketBConn, sourceTableNameB);

            db.commit();
            marketAConn.commit();
            marketBConn.commit();
        } catch (Exception e) {
            if (db != null) {
                db.rollback();
            }
            if (marketAConn != null) {
                marketAConn.rollback();
            }
            if (marketBConn != null) {
                marketBConn.rollback();
            }
            throw e;
        } finally {
            if (db != null) {
                db.close();
            }
            if (marketAConn != null) {
                marketAConn.close();
            }
            if (marketBConn != null) {
                marketBConn.close();
            }
        }
    }


    private void cleanData(long datatableId, String tableName, long marketADslId, long marketBDslId, long marketAId, long marketBId, String sourceTableNameA, String sourceTableNameB, long dslId) {
        System.out.println(datatableId);
        DatabaseWrapper db = null;
        DatabaseWrapper marketAConn = null;
        DatabaseWrapper marketBConn = null;
        DatabaseWrapper tableDb = null;
        try {
            db = new DatabaseWrapper();
            marketAConn = getConnFromDslId(db, marketADslId);
            marketBConn = getConnFromDslId(db, marketBDslId);
            tableDb = getConnFromDslId(db, dslId);
            marketAConn.beginTrans();
            marketBConn.beginTrans();
            tableDb.beginTrans();
            db.beginTrans();

            cleanSingleInfo(db, tableDb, datatableId, tableName);
            cleanSingleInfo(db, marketAConn, marketAId, sourceTableNameA);
            cleanSingleInfo(db, marketBConn, marketBId, sourceTableNameB);

            db.commit();
            marketAConn.commit();
            marketBConn.commit();
            tableDb.commit();
        } catch (Exception e) {
            if (db != null) {
                db.rollback();
            }
            if (marketAConn != null) {
                marketAConn.rollback();
            }
            if (marketBConn != null) {
                marketBConn.rollback();
            }
            if (tableDb != null) {
                tableDb.rollback();
            }
            throw e;
        } finally {
            if (db != null) {
                db.close();
            }
            if (marketAConn != null) {
                marketAConn.close();
            }
            if (marketBConn != null) {
                marketBConn.close();
            }
            if (tableDb != null) {
                tableDb.close();
            }
        }
    }

    private static void cleanSingleInfo(DatabaseWrapper db, DatabaseWrapper tableDb, long datatableId, String tableName) {
        db.execute("delete from dm_datatable where datatable_id = ?", datatableId);
        db.execute("delete from datatable_field_info where datatable_id = ?", datatableId);
        db.execute("delete from dm_operation_info where datatable_id = ?", datatableId);
        db.execute("delete from dtab_relation_store where tab_id = ?", datatableId);
        if (tableDb.isExistTable(tableName))
            tableDb.execute("drop table " + tableName);
    }

    private void executeAndValidate(long datatableId, String tableName, String sourceTableNameB, StorageType storageType, long dslId, long marketBDslId) throws IOException {

        try (DatabaseWrapper db = new DatabaseWrapper(); DatabaseWrapper conn = getConnFromDslId(db, dslId);) {
            //先跑一次,属于铺底
            MainClass.run(String.valueOf(datatableId), "20200702", "date_a=20200601;date_b=20200630");
            //铺底验证
            assertThat("sql关联结果集是2条",
                    SqlOperator.queryList(conn, "select * from " + tableName).size(), is(2));
            assertThat("两条结果集定值，hyren_s_date,hyren_e_date结果验证",
                    SqlOperator.queryList(conn, "select * from " + tableName +
                            " where constant = 'constantValue' " +
                            "and hyren_s_date = '20200702' " +
                            "and hyren_e_date = '99991231'").size(), is(2));
            //再跑一次，需要根据追加替换还是增量进行数据结果验证
            //替换
            if (StorageType.TiHuan.equals(storageType)) {
                MainClass.run(String.valueOf(datatableId), "20200703", "date_a=20200601;date_b=20200630");
                assertThat("替换后的sql关联结果集是2条",
                        SqlOperator.queryList(conn, "select * from " + tableName).size(), is(2));
                assertThat("两条结果集定值，hyren_s_date,hyren_e_date结果验证",
                        SqlOperator.queryList(conn, "select * from " + tableName +
                                " where constant = 'constantValue' " +
                                "and hyren_s_date = '20200703' " +
                                "and hyren_e_date = '99991231'").size(), is(2));
            } else if (StorageType.ZhuiJia.equals(storageType)) {
                MainClass.run(String.valueOf(datatableId), "20200703", "date_a=20200601;date_b=20200630");
                assertThat("sql关联结果集是4条",
                        SqlOperator.queryList(conn, "select * from " + tableName).size(), is(4));
                assertThat("两条结果集定值，hyren_s_date,hyren_e_date结果验证",
                        SqlOperator.queryList(conn, "select * from " + tableName +
                                " where constant = 'constantValue' " +
                                "and hyren_s_date = '20200702' " +
                                "and hyren_e_date = '99991231'").size(), is(2));
                assertThat("两条结果集定值，hyren_s_date,hyren_e_date结果验证",
                        SqlOperator.queryList(conn, "select * from " + tableName +
                                " where constant = 'constantValue' " +
                                "and hyren_s_date = '20200703' " +
                                "and hyren_e_date = '99991231'").size(), is(2));
            } else if (StorageType.ZengLiang.equals(storageType)) {
                String updateSourceTableContentSql = "update " + sourceTableNameB + " set name = '赵七' where id = '1003'";
                DatabaseWrapper bDb = null;
                try {
                    bDb = getConnFromDslId(db, marketBDslId);
                    bDb.beginTrans();
                    bDb.execute(updateSourceTableContentSql);
                    bDb.commit();
                } catch (Exception e) {
                    if (bDb != null) {
                        bDb.rollback();
                    }
                    throw e;
                } finally {
                    if (bDb != null) {
                        bDb.close();
                    }
                }
                MainClass.run(String.valueOf(datatableId), "20200703", "date_a=20200601;date_b=20200630");
//                assertThat("增量后sql关联结果集是3条，新增一条，关联一条",
//                        SqlOperator.queryList(conn, "select * from " + tableName).size(), is(3));
                assertThat("关链的一条，hyren_s_date,hyren_e_date结果验证",
                        SqlOperator.queryList(conn, "select * from " + tableName +
                                " where name = '赵六' " +
                                "and constant = 'constantValue' " +
                                "and hyren_s_date = '20200702' " +
                                "and hyren_e_date = '20200703'").size(), is(1));
                assertThat("未变化的一条，hyren_s_date,hyren_e_date结果验证",
                        SqlOperator.queryList(conn, "select * from " + tableName +
                                " where constant = 'constantValue' " +
                                "and hyren_s_date = '20200702' " +
                                "and hyren_e_date = '99991231'").size(), is(1));
                assertThat("新增的一条，hyren_s_date,hyren_e_date结果验证",
                        SqlOperator.queryList(conn, "select * from " + tableName +
                                " where name = '赵七' " +
                                "and constant = 'constantValue' " +
                                "and hyren_s_date = '20200703' " +
                                "and hyren_e_date = '99991231'").size(), is(1));
            } else {
                throw new AppSystemException("无法识别 StorageType：" + StorageType.CodeName);
            }

        }
    }


    private void insertDmDatatable(DatabaseWrapper db, Long datatableId, String tableName, IsFlag isRepeat,
                                   SqlEngine sqlEngine, StorageType storageType) {
        Dm_datatable dmDatatable = new Dm_datatable();
        dmDatatable.setDatatable_id(datatableId);
        dmDatatable.setCategory_id(PrimayKeyGener.getNextId());
        dmDatatable.setData_mart_id(PrimayKeyGener.getNextId());
        dmDatatable.setDatac_date(DateUtil.getSysDate());
        dmDatatable.setDatac_time(DateUtil.getSysTime());
        dmDatatable.setDatatable_cn_name("集市测试表");
        dmDatatable.setDatatable_create_date(DateUtil.getSysDate());
        dmDatatable.setDatatable_create_time(DateUtil.getSysTime());
        dmDatatable.setDatatable_desc("用于集市测试的集市表");
        dmDatatable.setDdlc_date(DateUtil.getSysDate());
        dmDatatable.setDdlc_time(DateUtil.getSysTime());
        dmDatatable.setDatatable_due_date(DateUtil.getSysDate());
        dmDatatable.setEtl_date("00000000");
        dmDatatable.setDatatable_en_name(tableName);
        dmDatatable.setDatatable_lifecycle("");
        dmDatatable.setTable_storage(TableStorage.ShuJuBiao.getCode());
        // 是否是表名可重复集市
        dmDatatable.setRepeat_flag(isRepeat.getCode());
        //TODO JDBC spark
        dmDatatable.setSql_engine(sqlEngine.getCode());
        //TODO 追加，替换，增量
        dmDatatable.setStorage_type(storageType.getCode());
        dmDatatable.add(db);
    }

    private void insertDatatableFieldInfo(DatabaseWrapper db, Long datatableId) {
        Datatable_field_info datatableFieldInfo = new Datatable_field_info();
        datatableFieldInfo.setDatatable_field_id(PrimayKeyGener.getNextId());
        datatableFieldInfo.setDatatable_id(datatableId);
        datatableFieldInfo.setField_cn_name("年龄");
        datatableFieldInfo.setField_en_name("age");
        datatableFieldInfo.setField_desc("");
        datatableFieldInfo.setField_length("100");
        datatableFieldInfo.setField_process(ProcessType.YingShe.getCode());
        datatableFieldInfo.setField_seq("1");
        datatableFieldInfo.setField_type("char");
        datatableFieldInfo.add(db);

        Datatable_field_info datatableFieldInfo1 = new Datatable_field_info();
        datatableFieldInfo1.setDatatable_field_id(PrimayKeyGener.getNextId());
        datatableFieldInfo1.setDatatable_id(datatableId);
        datatableFieldInfo1.setField_cn_name("名字");
        datatableFieldInfo1.setField_en_name("name");
        datatableFieldInfo1.setField_desc("");
        datatableFieldInfo1.setField_length("100");
        datatableFieldInfo1.setField_process(ProcessType.YingShe.getCode());
        datatableFieldInfo1.setField_seq("2");
        datatableFieldInfo1.setField_type("char");
        datatableFieldInfo1.add(db);


        Datatable_field_info datatableFieldInfo2 = new Datatable_field_info();
        datatableFieldInfo2.setDatatable_field_id(PrimayKeyGener.getNextId());
        datatableFieldInfo2.setDatatable_id(datatableId);
        datatableFieldInfo2.setField_cn_name("地区");
        datatableFieldInfo2.setField_en_name("area");
        datatableFieldInfo2.setField_desc("");
        datatableFieldInfo2.setField_length("100");
        datatableFieldInfo2.setField_process(ProcessType.YingShe.getCode());
        datatableFieldInfo2.setField_seq("3");
        datatableFieldInfo2.setField_type("char");
        datatableFieldInfo2.add(db);

        Datatable_field_info datatableFieldInfo3 = new Datatable_field_info();
        datatableFieldInfo3.setDatatable_field_id(PrimayKeyGener.getNextId());
        datatableFieldInfo3.setDatatable_id(datatableId);
        datatableFieldInfo3.setField_cn_name("定值");
        datatableFieldInfo3.setField_en_name("constant");
        datatableFieldInfo3.setField_desc("");
        datatableFieldInfo3.setField_length("100");
        datatableFieldInfo3.setField_process(ProcessType.DingZhi.getCode());
        datatableFieldInfo3.setField_seq("4");
        datatableFieldInfo3.setField_type("char");
        datatableFieldInfo3.add(db);

        Datatable_field_info datatableFieldInfo4 = new Datatable_field_info();
        datatableFieldInfo4.setDatatable_field_id(PrimayKeyGener.getNextId());
        datatableFieldInfo4.setDatatable_id(datatableId);
        datatableFieldInfo4.setField_cn_name("自增值");
        datatableFieldInfo4.setField_en_name("increaseCol");
        datatableFieldInfo4.setField_desc("");
        datatableFieldInfo4.setField_length("100");
        datatableFieldInfo4.setField_process(ProcessType.ZiZeng.getCode());
        datatableFieldInfo4.setField_seq("5");
        datatableFieldInfo4.setField_type("char");
        datatableFieldInfo4.add(db);
    }

    private void insertDmOperationInfo(DatabaseWrapper db, Long datatableId, String sourceTableNameA, String sourceTableNameB) {

        Dm_operation_info dmOperationInfo = new Dm_operation_info();
        dmOperationInfo.setDatatable_id(datatableId);
        dmOperationInfo.setExecute_sql("select a.age,b.name,a.area from " +
                sourceTableNameA + " a join " + sourceTableNameB + " b" +
                " on a.id = b.id " +
                "where a.h_date > '#{date_a}' and b.h_date < '#{date_b}'");
        dmOperationInfo.setId(PrimayKeyGener.getNextId());
        dmOperationInfo.setSearch_name("xxxxx");
        dmOperationInfo.add(db);
    }

    private void insertDtabRelationStore(DatabaseWrapper db, Long dslId, Long datatableId) {
        Dtab_relation_store dtabRelationStore = new Dtab_relation_store();
        dtabRelationStore.setTab_id(datatableId);
        dtabRelationStore.setDsl_id(dslId);
        dtabRelationStore.setData_source(StoreLayerDataSource.DM.getCode());
        dtabRelationStore.add(db);
    }

    private void createAndInsertMarketA(DatabaseWrapper db, String sourceTableNameA) {
        String createMarketA = "create table " + sourceTableNameA + " (id char(4),age char(2),area char(100),salary decimal(4,0),h_date char(8))";
        String insert1 = "insert into " + sourceTableNameA + " (id,age,area,salary,h_date) values(1000,23,'Japan',2345,'20200608')";
        String insert2 = "insert into " + sourceTableNameA + " (id,age,area,salary,h_date) values(1001,25,'Japan',5249,'20200618')";
        String insert3 = "insert into " + sourceTableNameA + " (id,age,area,salary,h_date) values(1002,23,'China',4024,'20200209')";
        String insert4 = "insert into " + sourceTableNameA + " (id,age,area,salary,h_date) values(1003,28,'China',9999,'20200620')";
        db.execute(createMarketA);
        db.execute(insert1);
        db.execute(insert2);
        db.execute(insert3);
        db.execute(insert4);
    }


    private void createAndInsertMarketB(DatabaseWrapper db, String sourceTableNameB) {
        String createMarketB = "create table " + sourceTableNameB + " (id char(4),name char(10),status char(1),h_date char(8))";
        String insert1 = "insert into " + sourceTableNameB + " (id,name,status,h_date) values(1000,'张三','0','20200608')";
        String insert2 = "insert into " + sourceTableNameB + " (id,name,status,h_date) values(1001,'李四','1','20201008')";
        String insert3 = "insert into " + sourceTableNameB + " (id,name,status,h_date) values(1002,'王五','1','20200609')";
        String insert4 = "insert into " + sourceTableNameB + " (id,name,status,h_date) values(1003,'赵六','3','20200620')";
        db.execute(createMarketB);
        db.execute(insert1);
        db.execute(insert2);
        db.execute(insert3);
        db.execute(insert4);

    }

    private DatabaseWrapper getConnFromDslId(DatabaseWrapper db, long dslId) {
        List<Data_store_layer_attr> dataStoreLayerAttrs = SqlOperator.queryList(db, Data_store_layer_attr.class,
                "select * from data_store_layer_attr where dsl_id = ?", dslId);
        final Map<String, String> tableLayerAttrs = new HashMap<>();

        dataStoreLayerAttrs.forEach(propertyRecord ->
                tableLayerAttrs.put(propertyRecord.getStorage_property_key()
                        , propertyRecord.getStorage_property_val()));

        return ConnectionTool.getDBWrapper(tableLayerAttrs);
    }

}
