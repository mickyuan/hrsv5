package hrds.commons.utils.agentmonitor;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import fd.ng.netclient.http.HttpClient;

@DocClass(desc = "Agent的监听服务类", author = "Mr.Lee", createdate = "2020-05-15 10:20")
public class AgentMonitorUtil {

  /** 建立HttpClient请求工具类 */
  private static final HttpClient httpClient  = new HttpClient();

  @Method(desc = "检查Agent信息是否能够请求成功,如果能够请求成功并获取到返回值,则认为能够通讯", logicStep = "")
  @Param(name = "host", desc = "Agent ip", range = "不可为空")
  @Param(name = "port", desc = "Agent 端口", range = "不可为空")
  @Return(desc = "返回true/false", range = "true-表示能够正常请求,反之异常")
  public static boolean agentMonitor(String host, int port) {
    httpClient.reset();
    String httpMsg = httpClient.post("http://" + host + ":" + port).getBodyString();
    if (StringUtil.isBlank(httpMsg)) {
      return false;
    } else {
      return true;
    }
  }
}
