package hrds.biz.datasource;

import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
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
            //for (int i = 0; i < nums.length; i++)
            //    assertThat("initData : " + i, nums[i], is(1));
            SqlOperator.commitTransaction(db);
        }
    }

    @Test
    public void add() {
        String source_id = "1000000001";
        String datasource_name = "cs";
        String datasource_number = "cs01";
        String create_date = DateUtil.getSysDate();
        String create_time = DateUtil.getDateTime(DateTimeFormatter.ofPattern("HHmmss"));
        String user_id = "1001";
        String source_remark = "测试";
        String depIds = "1000000011,1000000012,1000000013";
        String bodyString = new HttpClient()
                .addData("source_id", source_id)
                .addData("source_remark", source_remark)
                .addData("datasource_name", datasource_name)
                .addData("datasource_number", datasource_number)
                .addData("create_date", create_date)
                .addData("create_time", create_time)
                .addData("user_id", user_id)
                //.addData("depIds", depIds)
                .post(getActionUrl("add")).getBodyString();
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

            for (String dep_id : depIds.split(",")) {
                Map<String, Object> SRDResult = SqlOperator.queryOneObject(db,
                        "select * from " + SRDTableName + " where dep_id=?", new BigDecimal(dep_id));
                BigDecimal new_source_id = (BigDecimal) SRDResult.get("source_id");
                assertThat(new BigDecimal(source_id), is(new_source_id));
            }
        }
    }

    @Test
    public void updateDataSource() {
        String source_id = "1000000001";
        String depIds = "1000000001,1000000002";
        String datasource_name = "ceshi2";
        String source_remark = "测试2";
        String responseValue = new HttpClient()
                .addData("source_id", source_id)
                .addData("depIds", depIds)
                .addData("datasource_name", datasource_name)
                .addData("source_remark", source_remark)
                .post(getActionUrl("updateDataSource"))
                .getBodyString();
        ActionResult ar = JsonUtil.toObject(responseValue, ActionResult.class);
        assertThat(ar.isSuccess(), is(true));

        // 验证DB里面的数据是否正确
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            // 验证data_source表信息
            Map<String, Object> result = SqlOperator.queryOneObject(db,
                    "select * from " + DSTableName + " where source_id=?", new BigDecimal(source_id));
            String new_datasource_name = (String) result.get("datasource_name");
            String new_source_remark = (String) result.get("source_remark");
            assertThat(datasource_name, is(new_datasource_name));
            assertThat(source_remark, is(new_source_remark));

            // 验证source_relation_dep表信息
            for (String dep_id : depIds.split(",")) {
                Map<String, Object> stringObjectMap = SqlOperator.queryOneObject(db,
                        "select * from " + SRDTableName + " where dep_id=?", new BigDecimal(dep_id));
                BigDecimal new_dep_id = (BigDecimal) stringObjectMap.get("dep_id");
                BigDecimal new_source_id = (BigDecimal) stringObjectMap.get("source_id");
                assertThat(new BigDecimal(dep_id), is(new_dep_id));
                assertThat(new BigDecimal(source_id), is(new_source_id));
            }
        }
    }

    @Test
    public void deleteDataSource() {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            String source_id = "1000000001";
            // 验证DB里面预期被删除的数据是存在的
            OptionalLong result = SqlOperator.queryNumber(db,
                    "select count(1) from " + DataSource.TableName + " where source_id=?", source_id);
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
                    "select count(1) from " + DataSource.TableName + " where source_id=?", source_id);
            assertThat(result.orElse(Long.MIN_VALUE), is(0L)); // 被删除了所以为0
        }
    }

    @Test
    public void deleteSourceRelationDep() {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            String source_id = "1000000001";
            // 验证DB里面预期被删除的数据是存在的
            OptionalLong result = SqlOperator.queryNumber(db,
                    "select count(1) from " + SourceRelationDep.TableName + " where source_id=?", source_id);
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
                    "select count(1) from " + SourceRelationDep.TableName + " where source_id=?", source_id);
            assertThat(result.orElse(Long.MIN_VALUE), is(0L)); // 被删除了所以为0
        }
    }

}
