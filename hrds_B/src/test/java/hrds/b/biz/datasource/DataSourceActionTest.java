package hrds.b.biz.datasource;

import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.entity.AgentInfo;
import hrds.entity.DataSource;
import hrds.entity.SourceRelationDep;
import hrds.testbase.WebBaseTestCase;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DataSourceActionTest extends WebBaseTestCase {
    private static final String SRDTableName = SourceRelationDep.TableName;
    private static final String DSTableName = DataSource.TableName;
    private static final int Init_Rows = 10; // 向表中初始化的数据条数。

    @Before
    public void before() {
        // 初始化测试用例数据
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            List<Object[]> params = new ArrayList<>();
            for (int i = 0; i < Init_Rows; i++) {
                int source_id = i;
                String source_remark = "init-" + i;
                String datasource_name = "init-" + i;
                String datasource_number = "init-" + i;
                String create_date = "init-" + i;
                String create_time = "init-" + i;
                int user_id = 1001;
                Object[] objects = new Object[]{source_id, source_remark, datasource_name, datasource_number, create_date, create_time, user_id};
                params.add(objects);
            }
            int[] nums = SqlOperator.executeBatch(db,
                    "insert into " + DSTableName + "( source_id, source_remark, datasource_name, datasource_number, create_date, create_time, user_id) values(?, ?,?,?,?,?,?)",
                    params
            );
            assertThat("测试数据初始化", nums.length, is(Init_Rows));
            SqlOperator.commitTransaction(db);
        }
    }

    @Test
    public void save() {
        String source_id = "1000000003";
        String datasource_name = "cs";
        String datasource_number = "cs01";
        String create_date = DateUtil.getSysDate();
        String create_time = DateUtil.getDateTime(DateTimeFormatter.ofPattern("HHmmss"));
        String user_id = "1001";
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
        System.out.println(bodyString);
        ActionResult ar = JsonUtil.toObject(bodyString, ActionResult.class);
        assertThat(ar.isSuccess(), is(true));
        // 验证DB里面的数据是否正确
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            Map<String, Object> result = SqlOperator.queryOneObject(db,
                    "select * from " + DSTableName + " where source_id=?", new BigDecimal(source_id));
            String new_datasource_name = (String) result.get("datasource_name");
            String new_create_date = (String) result.get("create_date");
            String new_create_time = (String) result.get("create_time");
            BigDecimal new_user_id = (BigDecimal) result.get("user_id");

            assertThat(datasource_name, is(new_datasource_name));
            assertThat(create_date, is(new_create_date));
            assertThat(create_time, is(new_create_time));
            assertThat(new BigDecimal(user_id), is(new_user_id));

            Map<String, Object> SRDResult = SqlOperator.queryOneObject(db,
                    "select count(*) count from " + SRDTableName + " where source_id=?", new BigDecimal(source_id));
            int count = (int) SRDResult.get("count");
            assertThat(dep_id.length(), is(count));
        }
    }

    @Test
    public void update() {
        String source_id = "1000000003";
        String datasource_name = "cs2";
        String datasource_number = "cs02";
        String create_date = DateUtil.getSysDate();
        String create_time = DateUtil.getDateTime(DateTimeFormatter.ofPattern("HHmmss"));
        String user_id = "1001";
        String source_remark = "测试2";
        String dep_id = "1000000011";
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
        System.out.println(bodyString);
        ActionResult ar = JsonUtil.toObject(bodyString, ActionResult.class);
        assertThat(ar.isSuccess(), is(true));
        // 验证DB里面的数据是否正确
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            Map<String, Object> result = SqlOperator.queryOneObject(db,
                    "select * from " + DSTableName + " where source_id=?", new BigDecimal(source_id));
            String new_datasource_name = (String) result.get("datasource_name");
            assertThat(datasource_name, is(new_datasource_name));

            Map<String, Object> SRDResult = SqlOperator.queryOneObject(db,
                    "select count(*) count from " + SRDTableName + " where source_id=?", new BigDecimal(source_id));
            int count = (int) SRDResult.get("count");
            assertThat(dep_id.length(), is(count));
        }
    }

    @Test
    public void deleteDataSource() {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            String source_id = "1000000003";
            // 验证DB里面预期被删除的数据是存在的
            OptionalLong result = SqlOperator.queryNumber(db,
                    "select count(1) from " + DataSource.TableName + " where source_id=?", new BigDecimal(source_id));
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
                    "select count(1) from " + DataSource.TableName + " where source_id=?", new BigDecimal(source_id));
            assertThat(result.orElse(Long.MIN_VALUE), is(0L)); // 被删除了所以为0
        }
    }

    @Test
    public void deleteSourceRelationDep() {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            String source_id = "1000000001";
            // 验证DB里面预期被删除的数据是存在的
            OptionalLong result = SqlOperator.queryNumber(db,
                    "select count(1) from " + SourceRelationDep.TableName + " where source_id=?", new BigDecimal(source_id));
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
                    "select count(1) from " + SourceRelationDep.TableName + " where source_id=?", new BigDecimal(source_id));
            assertThat(result.orElse(Long.MIN_VALUE), is(0L)); // 被删除了所以为0
        }
    }

    @Test
    public void saveAgent() {
        String agent_id = "1000000001";
        String agent_name = "数据库agent";
        String agent_type = "1";
        String agent_ip = "10.71.4.51";
        String agent_port = "3456";
        String agent_status = "0";
        String create_date = DateUtil.getSysDate();
        String create_time = DateUtil.getDateTime(DateTimeFormatter.ofPattern("HHmmss"));
        String source_id = "1000000005";
        String user_id = "1001";
        String saveAgent = new HttpClient().addData("agent_id", agent_id)
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
        ActionResult actionResult = JsonUtil.toObject(saveAgent, ActionResult.class);
        System.out.println(saveAgent);
        assertThat(actionResult.isSuccess(), is(true));

        // 验证DB里面的数据是否正确
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            Map<String, Object> result = SqlOperator.queryOneObject(db,
                    "select * from " + AgentInfo.TableName + " where agent_id=?", new BigDecimal(agent_id));
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
