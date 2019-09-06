package hrds.b.biz.dataquery;

import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.AgentType;
import hrds.commons.entity.Agent_info;
import hrds.commons.entity.Data_source;
import hrds.commons.entity.Source_relation_dep;
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
            List<Object[]> dsList = new ArrayList<>();// data_source 数据
            List<Object[]> srdList = new ArrayList<>();// Source_relation_dep 数据
            List<Object[]> aiList = new ArrayList<>();// Agent_info 数据
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
                Object[] aiData = new Object[]{agent_id, agent_name, agent_type, agent_ip, agent_port, agent_status, create_date, create_time, source_id, user_id};
                aiList.add(aiData);
            }
            //插入 Data_source 数据
            int[] dsDataNum = SqlOperator.executeBatch(db,
                    "insert into " + Data_source.TableName + "(source_id, source_remark, datasource_name, " +
                            "datasource_number,create_date, create_time, user_id) values(?, ?,?,?,?,?,?)",
                    dsList
            );
            assertThat("Data_source 数据初始化", dsDataNum.length, is(Init_Rows));
            //插入 source_relation_dep 数据
            int[] srdDataNum = SqlOperator.executeBatch(db,
                    "insert into " + Source_relation_dep.TableName + "  values(?, ?)",
                    srdList
            );
            assertThat("Source_relation_dep 数据初始化", srdDataNum.length, is(Init_Rows));
            //插入 agent_info 数据
            int[] aiDataNum = SqlOperator.executeBatch(db,
                    "insert into " + Agent_info.TableName +
                            " (agent_id, agent_name, agent_type, agent_ip, agent_port, agent_status, create_date, create_time, source_id, user_id)" +
                            " values(?,?,?,?,?,?,?,?,?,?)",
                    aiList
            );
            assertThat("Agent_info 数据初始化", aiDataNum.length, is(Init_Rows));
            //提交所有数据库执行操作
            SqlOperator.commitTransaction(db);
        }
    }

    @After
    public void after() {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            for (long i = -500L; i < -500L + Init_Rows; i++) {
                // 测试完成后删除 data_source 表测试数据
                SqlOperator.execute(db, "delete from " + Data_source.TableName + "  where source_id=?", i);
                SqlOperator.commitTransaction(db);
                long dsDataNum = SqlOperator.queryNumber(db,
                        "select count(1) from " + Data_source.TableName + "  where source_id=?", i)
                        .orElseThrow(() -> new RuntimeException("count fail!"));
                assertThat("data_source 表此条数据删除后，记录数应该为0", dsDataNum, is(0L));
                // 测试完成后删除 source_relation_dep 测试数据
                SqlOperator.execute(db, "delete from " + Source_relation_dep.TableName + "  where source_id=?", i);
                SqlOperator.commitTransaction(db);
                long srdDataNum = SqlOperator.queryNumber(db,
                        "select count(1) from " + Source_relation_dep.TableName + "  where " +
                                "source_id=?", i)
                        .orElseThrow(() -> new RuntimeException("count fail!"));
                assertThat("source_relation_dep 表此条数据删除后，记录数应该为0", srdDataNum, is(0L));
                // 测试完成后删除 agent_info 测试数据
                SqlOperator.execute(db, "delete from " + Agent_info.TableName + "  where agent_id=?", i);
                SqlOperator.commitTransaction(db);
                long aiDataNum = SqlOperator.queryNumber(db,
                        "select count(1) from " + Source_relation_dep.TableName + "  where " +
                                "source_id=?", i)
                        .orElseThrow(() -> new RuntimeException("count fail!"));
                assertThat("agent_info 表此条数据删除后，记录数应该为0", aiDataNum, is(0L));
                //提交所有数据库执行操作
            }
        }
    }
    @Test
    public void getFileDataSource() {
        long dep_id = -500L ;
        String bodyString = new HttpClient()
                .addData("dep_id", dep_id)
                .post(getActionUrl("getFileDataSource")).getBodyString();
        ActionResult ar = JsonUtil.toObject(bodyString, ActionResult.class);
        assertThat(ar.isSuccess(), is(true));
    }
}

