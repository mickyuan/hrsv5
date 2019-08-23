package hrds.biz.datasource;

import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.entity.DataSource;
import hrds.entity.SourceRelationDep;
import hrds.testbase.WebBaseTestCase;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Map;
import java.util.OptionalLong;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DataSourceActionTest extends WebBaseTestCase {
    private static final String SRDTableName = SourceRelationDep.TableName;

    private static final String DSTableName = DataSource.TableName;

    @Test
    public void addDataSource() {
        int source_id = 1000000001;
        String datasource_name = "ceshi";
        String datasource_number = "cs01";
        String create_date = DateUtil.getSysDate();
        String create_time = DateUtil.getDateTime();
        int user_id = 1001;
        String source_remark = "测试";
        String[] dep_id = {"1000000001", "1000000002", "1000000003"};
        String responseValue = new HttpClient().addData("source_id", source_id)
                .addData("datasource_number", datasource_number)
                .addData("datasource_name", datasource_name)
                .addData("create_date", create_date)
                .addData("create_time", create_time)
                .addData("user_id", user_id)
                .addData("source_remark", source_remark)
                //.addData("dep_id", dep_id)
                .post(getActionUrl("addDataSource"))
                .getBodyString();
        ActionResult ar = JsonUtil.toObject(responseValue, ActionResult.class);
        assertThat(ar.isSuccess(), is(true));

        // 验证DB里面的数据是否正确
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            Map<String, Object> result = SqlOperator.queryOneObject(db,
                    "select * from " + DSTableName + " where source_id=?", source_id);
            String new_datasource_name = (String) result.get("datasource_name");
            String new_create_date = (String) result.get("create_date");
            String new_create_time = (String) result.get("create_time");
            String new_user_id = (String) result.get("user_id");

            assertThat(datasource_name, is(new_datasource_name));
            assertThat(create_date, is(new_create_date));
            assertThat(create_time, is(new_create_time));
            assertThat(user_id, is(new_user_id));

            for (String depId : dep_id) {
                Map<String, Object> SRDResult = SqlOperator.queryOneObject(db,
                        "select * from " + SRDTableName + " where dep_id=?", depId);
                int new_source_id = Integer.parseInt((String) result.get("source_id"));
                assertThat(source_id, is(new_source_id));
            }
        }
    }

    @Test
    public void updateDataSource() {
        String source_id = "1000000001";
        String[] dep_id = {"1000000001", "1000000002"};
        String datasource_name = "ceshi2";
        String source_remark = "测试2";
        String responseValue = new HttpClient()
                .addData("datasource_name", datasource_name).addData("source_id", source_id).addData("dep_id", dep_id).addData("source_remark", source_remark)
                .post(getActionUrl("updateDataSource"))
                .getBodyString();
        ActionResult ar = JsonUtil.toObject(responseValue, ActionResult.class);
        assertThat(ar.isSuccess(), is(true));

        // 验证DB里面的数据是否正确
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            // 验证data_source表信息
            Map<String, Object> result = SqlOperator.queryOneObject(db,
                    "select * from " + DataSource.TableName + " where source_id=?", source_id);
            String new_datasource_name = (String) result.get("datasource_name");
            String new_source_remark = (String) result.get("source_remark");
            assertThat(datasource_name, is(new_datasource_name));
            assertThat(source_remark, is(new_source_remark));

            // 验证source_relation_dep表信息
            Result rs = SqlOperator.queryResult(db,
                    "select * from " + SourceRelationDep.TableName + " where source_id=?", source_id);
            for (int i = 0; i < rs.getRowCount(); i++) {
                BigDecimal new_dep_id = rs.getBigDecimal(i, "dep_id");
                assertThat(datasource_name, is(new_datasource_name));
                assertThat(dep_id, is(new_dep_id));
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
