package hrds.commons;

import fd.ng.netserver.conf.HttpServerConf;
import fd.ng.netserver.conf.HttpServerConfBean;
import fd.ng.web.util.RequestUtil;

/**
 * @program: hrsv5
 * @description: 测试联通
 * @author: xchao
 * @create: 2019-09-05 16:53
 */
public class actionTest {
    public static void main(String[] args) {
        HttpServerConfBean hyrenagent = HttpServerConf.getHttpServer("hyrenagent");
        String url = "http://"+hyrenagent.getHost()+":"+hyrenagent.getHttpPort()+"/agent/receive/";
        String action = "hrds/commons/testcc";
        System.out.println(url);
    }
    public void testcc() {
        System.out.println(RequestUtil.getJson());


    }

}
