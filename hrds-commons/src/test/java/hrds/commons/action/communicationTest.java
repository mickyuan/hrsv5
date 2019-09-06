package hrds.commons.action;

import fd.ng.core.utils.JsonUtil;
import fd.ng.netclient.http.HttpClient;
import fd.ng.netclient.http.SubmitMediaType;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.DatabaseType;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @program: hrsv5
 * @description: 通讯测试
 * @author: xchao
 * @create: 2019-09-04 16:29
 */
public class communicationTest {
    /**
     * 测试连接
     * 1、获取agent的连接
     * 2、从想要的数据库中获取连接信息
     * 3、使用httpclient发送json数据
     */
    @Test
    public void testConnection(){
        // 1、获取agent的连接,ip端口，都是从数据库中获取
        String url = "http://127.0.0.1:56000/agent/receive/";
        String action = "hrds/agent/trans/biz/testConn";
        Map map = new HashMap<>();
        //2、从想要的数据库中获取连接信息
        map.put("dbtype", DatabaseType.Postgresql.getValue());
        map.put("driver","org.postgresql.Driver");
        map.put("url","jdbc:postgresql://10.71.4.52:31001/hrsdxg");
        map.put("username","hrsdxg");
        map.put("password","hrsdxg");

        //3、使用httpclient发送json数据
        HttpClient.ResponseValue resVal = new HttpClient(SubmitMediaType.JSON)
                .addJson(JsonUtil.toJson(map))
                .post(url+action);
        ActionResult actionResult = JsonUtil.toObject(resVal.getBodyString(), ActionResult.class);
        assertThat(actionResult.getCode(),is(200));
        assertThat(actionResult.getData(),is(true));
        assertThat(actionResult.isSuccess(),is(true));
    }
}
