package hrds.b.biz.dataquery;

import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.AgentType;
import hrds.commons.entity.*;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DataQueryWebActionTest extends WebBaseTestCase{
    private static final int Init_Rows = 10; // 向表中初始化的数据条数。

    @Before
    public void before() {
        // 初始化测试用例数据
        try ( DatabaseWrapper db = new DatabaseWrapper() ) {
            List<Object[]> dsList = new ArrayList<>();  // Data_source 数据
            List<Object[]> srdList = new ArrayList<>(); // Source_relation_dep 数据
            List<Object[]> aiList = new ArrayList<>();  // Agent_info 数据
            List<Object[]> fcsList = new ArrayList<>(); // File_collect_set 数据
            List<Object[]> sfaList = new ArrayList<>(); // Source_file_attribute 数据
            List<Object[]> daList = new ArrayList<>();  // Data_auth 数据
            List<Object[]> siList = new ArrayList<>();  // Search_info 数据
            for (long i = -500L; i < -500L + Init_Rows; i++) {
                //初始化 data_source 数据
                long source_id = i;
                String source_remark = "init" + i;
                String datasource_name = "init" + i;
                String datasource_number = "init" + i;
                String create_date = DateUtil.getSysDate();
                String create_time = DateUtil.getSysTime();
                long user_id = 1001L;
                long dep_id = i + Init_Rows;
                Object[] dsData = new Object[]{source_id, source_remark, datasource_name, datasource_number, create_date, create_time, user_id};
                dsList.add(dsData);
                //初始化 Source_relation_dep 数据
                Object[] srdData = new Object[]{dep_id, source_id};
                srdList.add(srdData);
                //初始化 Agent_info 数据
                long agent_id = i;
                String agent_name = "init" + i;
                String agent_type = AgentType.WenJianXiTong.getCode();
                String agent_ip = "127.0.0.1";
                String agent_port = "88888";
                String agent_status = "1";
                Object[] aiData = new Object[]{agent_id, agent_name, agent_type, agent_ip, agent_port, agent_status,
                        create_date, create_time, source_id, user_id
                };
                aiList.add(aiData);
                //初始化 File_collect_set 数据
                long fcs_id = i;
                String fcs_name = "init" + i;
                String host_name = "init" + i;
                String system_type = "init" + i;
                String is_sendok = "1";
                String is_solr = "1";
                String remark = "init" + i;
                Object[] fcsData = new Object[]{fcs_id, agent_id, fcs_name, host_name, system_type, is_sendok, is_solr, remark};
                fcsList.add(fcsData);
                //初始化 Source_file_attribute 数据
                long file_id = i;
                String is_in_hbase = "0";
                long seqencing = 0L;
                String collect_type = "0";
                String original_name = "init" + i;
                String original_update_date = create_date;
                String original_update_time = create_time;
                String table_name = "init" + i;
                String hbase_name = "init" + i;
                String meta_info = "init" + i;
                String storage_date = create_date;
                String storage_time = create_time;
                long file_size = i;
                String file_type = "init" + i;
                String file_suffix = "init" + i;
                String source_path = "init" + i;
                String file_md5 = "init" + i;
                String file_avro_path = "init" + i;
                long file_avro_block = i;
                String is_big_file = "0";
                String is_cache = "0";
                long folder_id = i;
                long collect_set_id = i;
                Object[] sfaData = new  Object[]{file_id, is_in_hbase, seqencing, collect_type, original_name,
                        original_update_date, original_update_time, table_name, hbase_name, meta_info, storage_date,
                        storage_time, file_size, file_type, file_suffix, source_path, file_md5, file_avro_path,
                        file_avro_block, is_big_file, is_cache, folder_id, agent_id, source_id, collect_set_id
                };
                sfaList.add(sfaData);
                //初始化 Data_auth 数据
                long da_id = i;
                String apply_date = create_date;
                String apply_time = create_time;
                String apply_type = "2";
                String auth_type = "2";
                String audit_date = create_date;
                String audit_time = create_time;
                long audit_userid = i;
                String audit_name = "init" + i;
                Object[] daData = new Object[]{da_id, apply_date, apply_time, apply_type, auth_type, audit_date, audit_time,
                        audit_userid, audit_name, file_id, user_id, dep_id, agent_id, source_id, collect_set_id
                };
                daList.add(daData);
                //初始化 Search_info 数据
                long si_id = i;
                String word_name = "init" + i;
                long si_count = i;
                String si_remark = "init" + i;
                Object[] siData = new Object[]{si_id, file_id, word_name, si_count, si_remark};
                siList.add(siData);
            }
            //插入 Data_source 数据
            int[] dsDataNum = SqlOperator.executeBatch(db,
                    "insert into " + Data_source.TableName + "(source_id, source_remark, datasource_name, " +
                            "datasource_number,create_date, create_time, user_id) values(?,?,?,?,?,?,?)",
                    dsList
            );
            assertThat("Data_source 数据初始化", dsDataNum.length, is(Init_Rows));
            //插入 Source_relation_dep 数据
            int[] srdDataNum = SqlOperator.executeBatch(db,
                    "insert into " + Source_relation_dep.TableName + "  values(?, ?)",
                    srdList
            );
            assertThat("Source_relation_dep 数据初始化", srdDataNum.length, is(Init_Rows));
            //插入 Agent_info 数据
            int[] aiDataNum = SqlOperator.executeBatch(db,
                    "insert into " + Agent_info.TableName +
                            " (agent_id, agent_name, agent_type, agent_ip, agent_port, agent_status, create_date," +
                            " create_time, source_id, user_id)" +
                            " values(?,?,?,?,?,?,?,?,?,?)",
                    aiList
            );
            assertThat("Agent_info 数据初始化", aiDataNum.length, is(Init_Rows));
            //插入 File_collect_set 数据
            int[] fcsDataNum = SqlOperator.executeBatch(db,
                    "insert into " + File_collect_set.TableName +
                            " (fcs_id, agent_id, fcs_name, host_name, system_type, is_sendok, is_solr, remark)" +
                            " values(?,?,?,?,?,?,?,?)",
                    fcsList
            );
            assertThat("Agent_info 数据初始化", fcsDataNum.length, is(Init_Rows));
            //插入 Source_file_attribute 数据
            int[] sfaDataNum = SqlOperator.executeBatch(db,
                    "insert into " + Source_file_attribute.TableName + "" +
                            " (file_id, is_in_hbase, seqencing, collect_type, original_name, original_update_date," +
                            " original_update_time, table_name, hbase_name, meta_info, storage_date, storage_time," +
                            " file_size, file_type, file_suffix, source_path, file_md5, file_avro_path, file_avro_block," +
                            " is_big_file, is_cache, folder_id, agent_id, source_id, collect_set_id)" +
                            " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                    sfaList
            );
            assertThat("Source_file_attribute 数据初始化", sfaDataNum.length, is(Init_Rows));
            //插入 Data_auth 数据
            int[] daDataNum = SqlOperator.executeBatch(db,
                    "insert into " + Data_auth.TableName + "" +
                            " (da_id, apply_date, apply_time, apply_type, auth_type, audit_date, audit_time, audit_userid," +
                            " audit_name, file_id, user_id, dep_id, agent_id, source_id, collect_set_id)" +
                            " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                    daList
            );
            assertThat("Data_auth 数据初始化", daDataNum.length, is(Init_Rows));
            //插入 Search_info 数据
            int[] siDataNum = SqlOperator.executeBatch(db,
                    "insert into " + Search_info.TableName + "(si_id, file_id, word_name, si_count, si_remark)" +
                            " values(?,?,?,?,?)",
                    siList
            );
            assertThat("Search_info 数据初始化", siDataNum.length, is(Init_Rows));
            //提交所有数据库执行操作
            SqlOperator.commitTransaction(db);
        }
    }

    @After
    public void after() {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            for (long i = -500L; i < -500L + Init_Rows; i++) {
                // 测试完成后删除 Data_source 表测试数据
                SqlOperator.execute(db, "delete from " + Data_source.TableName + "  where source_id=?", i);
                SqlOperator.commitTransaction(db);
                long dsDataNum = SqlOperator.queryNumber(db,
                        "select count(1) from " + Data_source.TableName + "  where source_id=?", i)
                        .orElseThrow(() -> new RuntimeException("count fail!"));
                assertThat("data_source 表此条数据删除后，记录数应该为0", dsDataNum, is(0L));
                // 测试完成后删除 Source_relation_dep 测试数据
                SqlOperator.execute(db, "delete from " + Source_relation_dep.TableName + "  where source_id=?", i);
                SqlOperator.commitTransaction(db);
                long srdDataNum = SqlOperator.queryNumber(db,
                        "select count(1) from " + Source_relation_dep.TableName + "  where " +
                                "source_id=?", i)
                        .orElseThrow(() -> new RuntimeException("count fail!"));
                assertThat("source_relation_dep 表此条数据删除后，记录数应该为0", srdDataNum, is(0L));
                // 测试完成后删除 Agent_info 测试数据
                SqlOperator.execute(db, "delete from " + Agent_info.TableName + "  where agent_id=?", i);
                SqlOperator.commitTransaction(db);
                long aiDataNum = SqlOperator.queryNumber(db,
                        "select count(1) from " + Agent_info.TableName + "  where " +
                                "agent_id=?", i)
                        .orElseThrow(() -> new RuntimeException("count fail!"));
                assertThat("agent_info 表此条数据删除后，记录数应该为0", aiDataNum, is(0L));
                // 测试完成后删除 File_collect_set 测试数据
                SqlOperator.execute(db, "delete from " + File_collect_set.TableName + "  where agent_id=?", i);
                SqlOperator.commitTransaction(db);
                long fcsDataNum = SqlOperator.queryNumber(db,
                        "select count(1) from " + File_collect_set.TableName + "  where " +
                                "fcs_id=?", i)
                        .orElseThrow(() -> new RuntimeException("count fail!"));
                assertThat("file_collect_set 表此条数据删除后，记录数应该为0", fcsDataNum, is(0L));
                // 测试完成后删除 Source_file_attribute 测试数据
                SqlOperator.execute(db, "delete from " + Source_file_attribute.TableName + "  where file_id='" +i+"'");
                SqlOperator.commitTransaction(db);
                long sfaDataNum = SqlOperator.queryNumber(db,
                        "select count(1) from " + Source_file_attribute.TableName + "  where " +
                                "file_id='"+ i + "'")
                        .orElseThrow(() -> new RuntimeException("count fail!"));
                assertThat("source_file_attribute 表此条数据删除后，记录数应该为0", sfaDataNum, is(0L));
                // 测试完成后删除 Data_auth 测试数据
                SqlOperator.execute(db, "delete from " + Data_auth.TableName + "  where da_id=?", i);
                SqlOperator.commitTransaction(db);
                long daDataNum = SqlOperator.queryNumber(db,
                        "select count(1) from " + Data_auth.TableName + "  where " +
                                "da_id=?", i)
                        .orElseThrow(() -> new RuntimeException("count fail!"));
                assertThat("data_auth 表此条数据删除后，记录数应该为0", daDataNum, is(0L));
                // 测试完成后删除 Search_info 表测试数据
                SqlOperator.execute(db, "delete from " + Search_info.TableName + "  where si_id=?", i);
                SqlOperator.commitTransaction(db);
                long siDataNum = SqlOperator.queryNumber(db,
                        "select count(1) from " + Search_info.TableName + "  where si_id=?", i)
                        .orElseThrow(() -> new RuntimeException("count fail!"));
                assertThat("search_info 表此条数据删除后，记录数应该为0", siDataNum, is(0L));
            }
        }
    }
    @Test
    public void getFileDataSource() {
        long depId = -500L;
        String bodyString = new HttpClient()
                .addData("depId", depId)
                .post(getActionUrl("getFileDataSource")).getBodyString();
        ActionResult ar = JsonUtil.toObject(bodyString, ActionResult.class);
        assertThat(ar.isSuccess(), is(true));
    }
    @Test
    public void  getFileCollectionTask() {
        long sourceId = -500L;
        String bodyString = new HttpClient()
                .addData("sourceId", sourceId)
                .post(getActionUrl("getFileCollectionTask")).getBodyString();
        ActionResult ar = JsonUtil.toObject(bodyString, ActionResult.class);
        assertThat(ar.isSuccess(), is(true));
    }
    @Test
    public void downloadFileCheck() {
        long userId = 1001L;
        String fileId = "-500";
        String bodyString = new HttpClient()
                .addData("userId", userId)
                .addData("fileId", fileId)
                .post(getActionUrl("downloadFileCheck")).getBodyString();
        ActionResult ar = JsonUtil.toObject(bodyString, ActionResult.class);
        assertThat(ar.isSuccess(), is(true));
    }
    @Test
    public void modifySortCount() {
        String fileId = "-500";
        String queryKeyword = "init-500";
        String bodyString = new HttpClient()
                .addData("fileId", fileId)
                .addData("queryKeyword", queryKeyword)
                .post(getActionUrl("modifySortCount")).getBodyString();
        ActionResult ar = JsonUtil.toObject(bodyString, ActionResult.class);
        assertThat(ar.isSuccess(), is(true));
    }
}

