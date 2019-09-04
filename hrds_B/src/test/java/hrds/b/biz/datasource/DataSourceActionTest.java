package hrds.b.biz.datasource;

import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.AgentStatus;
import hrds.commons.codes.AgentType;
import hrds.commons.entity.Agent_info;
import hrds.commons.entity.Data_source;
import hrds.commons.entity.Source_relation_dep;
import hrds.commons.exception.BusinessException;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DataSourceActionTest extends WebBaseTestCase {
    private static final int Init_Rows = 10; // 向表中初始化的数据条数。

    @Before
    public void before() {
        // 初始化测试用例数据
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            List<Object[]> params = new ArrayList<>();
            List<Object[]> srdParams = new ArrayList<>();
            List<Object[]> aiParams = new ArrayList<>();
            // 初始化data_source表信息
            for (Long i = -300L; i < -300 + Init_Rows; i++) {
                Long source_id = i;
                String source_remark = "init" + i;
                String datasource_name = "init" + i;
                String datasource_number = "init" + i;
                String create_date = DateUtil.getSysDate();
                String create_time = DateUtil.getSysTime();
                Long user_id = 1001L;
                Long dep_id = i + Init_Rows;
                Long agent_id = i;
                String agent_name = "数据库agent";
                String agent_type = AgentType.ShuJuKu.getCode();
                String agent_ip = "10.71.4.51";
                String agent_port = "34567";
                String agent_status = AgentStatus.YiLianJie.getCode();
                Object[] objects = new Object[]{source_id, source_remark, datasource_name, datasource_number, create_date, create_time, user_id};
                params.add(objects);
                // source_relation_dep表信息
                Object[] srdObjects = {dep_id, source_id};
                srdParams.add(srdObjects);
                // agent_info表信息
                Object[] aiObjects = {agent_id, agent_name, agent_type, agent_ip, agent_port, agent_status, create_date
                        , create_time, source_id, user_id};
                aiParams.add(aiObjects);
            }
            int[] num = SqlOperator.executeBatch(db,
                    "insert into " + Data_source.TableName + "( source_id, source_remark, datasource_name, " +
                            "datasource_number,create_date, create_time, user_id) values(?, ?,?,?,?,?,?)",
                    params
            );
            assertThat("测试数据初始化", num.length, is(Init_Rows));

            // 初始化source_relation_dep表信息
            int[] srdNum = SqlOperator.executeBatch(db,
                    "insert into " + Source_relation_dep.TableName + "  values(?, ?)",
                    srdParams
            );
            assertThat("测试数据初始化", srdNum.length, is(Init_Rows));

            // 初始化agent_info表信息
            int[] aiNum = SqlOperator.executeBatch(db,
                    "insert into " + Agent_info.TableName + "  values(?, ?,?, ?,?, ?,?, ?,?, ?)",
                    aiParams
            );
            assertThat("测试数据初始化", aiNum.length, is(Init_Rows));

            SqlOperator.commitTransaction(db);
        }
    }

    @After
    public void after() {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            Long num = 0L;
            Long srdNum = 0L;
            Long aiNum = 0L;
            for (Long i = -300L; i < -300 + Init_Rows; i++) {
                // 测试完成后删除data_source表测试数据
                SqlOperator.execute(db, "delete from " + Data_source.TableName + "  where source_id=?", i);
                SqlOperator.commitTransaction(db);
                num = SqlOperator.queryNumber(db, "select count(1) from " + Data_source.TableName)
                        .orElseThrow(() -> new RuntimeException("count fail!"));

                // 测试完成后删除source_relation_dep表测试数据
                SqlOperator.execute(db, "delete from " + Source_relation_dep.TableName + "  where source_id=?", i);
                SqlOperator.commitTransaction(db);
                srdNum = SqlOperator.queryNumber(db, "select count(1) from " + Source_relation_dep.TableName)
                        .orElseThrow(() -> new RuntimeException("count fail!"));


                // 测试完成后删除source_relation_dep表测试数据
                SqlOperator.execute(db, "delete from " + Agent_info.TableName + "  where source_id=?", i);
                SqlOperator.commitTransaction(db);
                aiNum = SqlOperator.queryNumber(db, "select count(1) from " + Agent_info.TableName)
                        .orElseThrow(() -> new RuntimeException("count fail!"));

            }
            assertThat("整个表数据删除后，表记录数应该为0", num, is(0L));
            assertThat("整个表数据删除后，表记录数应该为0", srdNum, is(0L));
            assertThat("整个表数据删除后，表记录数应该为0", aiNum, is(0L));
        }
    }

    @Test
    public void saveDataSource() {
        Long source_id = -300L;
        String datasource_name = "cs";
        String datasource_number = "cs01";
        String create_date = DateUtil.getSysDate();
        String create_time = DateUtil.getSysTime();
        Long user_id = 1001L;
        String source_remark = "测试";
        String dep_id = "1000000011,1000000012,1000000013";
        String bodyString = new HttpClient()
                .addData("source_id", source_id)
                .addData("source_remark", source_remark)
                .addData("datasource_name", datasource_name)
                .addData("datasource_number", datasource_number)
                .addData("create_date", create_date)
                .addData("create_time", create_time)
                .addData("user_id", user_id)
                .addData("dep_id", dep_id)
                .post(getActionUrl("saveDataSource")).getBodyString();
        ActionResult ar = JsonUtil.toObject(bodyString, ActionResult.class);
        assertThat(ar.isSuccess(), is(true));
        // 验证DB里面的数据是否正确(接收的数据与入库数据做对比）
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            Map<String, Object> result = SqlOperator.queryOneObject(db,
                    "select * from " + Data_source.TableName + " where source_id=?", source_id);
            String new_datasource_name = (String) result.get("datasource_name");
            String new_create_date = (String) result.get("create_date");
            String new_create_time = (String) result.get("create_time");
            Long new_user_id = (Long) result.get("user_id");

            assertThat(datasource_name, is(new_datasource_name));
            assertThat(create_date, is(new_create_date));
            assertThat(create_time, is(new_create_time));
            assertThat(new BigDecimal(user_id), is(new_user_id));

            SqlOperator.queryNumber(db,
                    "select count(*) count from " + Source_relation_dep.TableName + " where source_id=?",
                    source_id).orElseThrow(() -> new BusinessException("查询异常"));
        }
    }

    @Test
    public void searchDataSource() {
        Long source_id = -300L;
        String bodyString = new HttpClient().addData("source_id", source_id)
                .post(getActionUrl("searchDataSource")).getBodyString();
        ActionResult ar = JsonUtil.toObject(bodyString, ActionResult.class);
        assertThat(ar.isSuccess(),is(true));
    }

    @Test
    public void deleteDataSource() {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            Long source_id = -300L;
            // 验证DB里面预期被删除的数据是存在的
            OptionalLong result = SqlOperator.queryNumber(db,
                    "select count(1) from " + Data_source.TableName + " where source_id=?", source_id);
            assertThat(result.orElse(Long.MIN_VALUE), is(1L)); // 被删除前为1

            // 业务处理
            String responseValue = new HttpClient()
                    .addData("source_id", source_id)
                    .post(getActionUrl("deleteDataSource"))
                    .getBodyString();
            ActionResult ar = JsonUtil.toObject(responseValue, ActionResult.class);
            assertThat(ar.isSuccess(), is(true));

            // 验证DB里面的数据是否正确
            result = SqlOperator.queryNumber(db,
                    "select count(1) from " + Data_source.TableName + " where source_id=?", source_id);
            assertThat(result.orElse(Long.MIN_VALUE), is(0L)); // 被删除了所以为0
        }
    }

    @Test
    public void deleteSourceRelationDep() {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            String source_id = "1000000001";
            // 验证DB里面预期被删除的数据是存在的
            OptionalLong result = SqlOperator.queryNumber(db,
                    "select count(1) from " + Source_relation_dep.TableName + " where source_id=?", source_id);
            assertThat(result.orElse(Long.MIN_VALUE), is(3L)); // 被删除前为3

            // 业务处理
            String responseValue = new HttpClient()
                    .addData("source_id", source_id)
                    .post(getActionUrl("deleteSourceRelationDep"))
                    .getBodyString();
            ActionResult ar = JsonUtil.toObject(responseValue, ActionResult.class);
            assertThat(ar.isSuccess(), is(true));

            // 验证DB里面的数据是否正确
            result = SqlOperator.queryNumber(db,
                    "select count(1) from " + Source_relation_dep.TableName + " where source_id=?", source_id);
            assertThat(result.orElse(Long.MIN_VALUE), is(0L)); // 被删除了所以为0
        }
    }

    @Test
    public void saveAgent() {
        int agent_id = -300;
        String agent_name = "数据库agent";
        String agent_type = AgentType.ShuJuKu.getCode();
        String agent_ip = "10.71.4.51";
        String agent_port = "3456";
        String agent_status = AgentStatus.YiLianJie.getCode();
        String create_date = DateUtil.getSysDate();
        String create_time = DateUtil.getSysTime();
        int source_id = -300;
        int user_id = 1001;
        String agentResult = new HttpClient().addData("agent_id", agent_id)
                .addData("agent_name", agent_name)
                .addData("agent_type", agent_type)
                .addData("agent_ip", agent_ip)
                .addData("agent_port", agent_port)
                .addData("agent_status", agent_status)
                .addData("create_date", create_date)
                .addData("create_time", create_time)
                .addData("source_id", source_id)
                .addData("user_id	", user_id)
                .post(getActionUrl("saveAgent")).getBodyString();
        ActionResult actionResult = JsonUtil.toObject(agentResult, ActionResult.class);
        assertThat(actionResult.isSuccess(), is(true));

        // 验证DB里面的数据是否正确
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            Map<String, Object> result = SqlOperator.queryOneObject(db,
                    "select * from " + Agent_info.TableName + " where agent_id=?", new BigDecimal(agent_id));
            String new_agent_name = (String) result.get("agent_name");
            String new_agent_type = (String) result.get("agent_type");
            String new_agent_ip = (String) result.get("agent_ip");
            String new_agent_port = (String) result.get("agent_port");
            String new_agent_status = (String) result.get("agent_status");
            String new_create_date = (String) result.get("create_date");
            String new_create_time = (String) result.get("create_time");
            BigDecimal new_source_id = (BigDecimal) result.get("source_id");
            BigDecimal new_user_id = (BigDecimal) result.get("user_id");

            assertThat(agent_name, is(new_agent_name));
            assertThat(agent_type, is(new_agent_type));
            assertThat(agent_ip, is(new_agent_ip));
            assertThat(agent_port, is(new_agent_port));
            assertThat(agent_status, is(new_agent_status));
            assertThat(create_date, is(new_create_date));
            assertThat(create_time, is(new_create_time));
            assertThat(new BigDecimal(source_id), is(new_source_id));
            assertThat(new BigDecimal(user_id), is(new_user_id));
        }
    }

}
