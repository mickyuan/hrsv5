package hrds.biz.B0101;

import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.entity.SourceRelationDep;
import hrds.testbase.WebBaseTestCase;
import org.hamcrest.MatcherAssert;

import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DataSourceActionTest extends WebBaseTestCase {
    private static final String UATTableName = SourceRelationDep.TableName;
    private static final int Init_Rows = 10; // 向表中初始化的数据条数。

    @Test
    public void addDataSource() {
        String source_id = "1234567890";
        String datasource_name = "ceshi";
        String create_date = DateUtil.getSysDate();
        String create_time = DateUtil.getDateTime();
        String user_id = "1001";
        String bodyString = new HttpClient().addData("source_id", source_id).addData("datasource_name", datasource_name).addData("create_date", create_date).addData("create_time", create_time).addData("user_id", user_id).post(getActionUrl("addDataSource")).getBodyString();
        ActionResult ar = JsonUtil.toObject(bodyString, ActionResult.class);
        MatcherAssert.assertThat(ar.isSuccess(), is(true));

        // 验证DB里面的数据是否正确
        try (DatabaseWrapper db = new DatabaseWrapper.Builder().dbname("local").create()) {
            Map<String, Object> result = SqlOperator.queryOneObject(db,
                    "select * from " + UATTableName + " where source_id=?", source_id);
            String new_datasource_name = (String)result.get("datasource_name");
            String new_create_date = (String)result.get("create_date");
            String new_create_time = (String)result.get("create_time");
            String new_user_id = (String)result.get("user_id");

            assertThat(datasource_name, is(new_datasource_name));
            assertThat(create_date, is(new_create_date));
            assertThat(create_time, is(new_create_time));
            assertThat(user_id, is(new_user_id));

        }


    }

}
