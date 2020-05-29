package hrds.commons.utils.datastorage.httpserver;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.StringUtil;
import fd.ng.netserver.conf.HttpServerConf;
import hrds.commons.utils.PropertyParaValue;
import hrds.commons.utils.deployentity.HttpYaml;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "组成HttpServer.conf配置文件的数据", author = "Mr.Lee", createdate = "2020-02-17 14:23")
public class HttpServer {

  /**
   * httpserver的name名称
   */
  public static final String HTTP_NAME = "httpserver";
  /**
   * httpserver文件名称
   */
  public static final String HTTP_CONF_NAME = "httpserver.conf";
  /**
   * 海云接口服务配置文件key名称
   */
  private static final String HYREN_NAME = "hyren_main";

  /**
   * Agent的webContext,这里需要和海云的receive进行通讯,所以值是默认的,如果海云receive修了了,这里也要进行修改!!!
   */
  private static final String WEB_CONTEXT = "/main";

  /**
   * Agent的actionPattern,这里需要和海云的receive进行通讯,所以值是默认的!!! Agent启动路径未填写时也默认使用
   * FIXME ,如果海云receive修了了,这里也要进行修改!!!
   */
  private static final String ACTION_PATTERN = "/receive/*";

  /**
   * Agent的webContext,根据部署Agent时进行判断,如果用户填写的有,则使用填写的,否则使用该默认值
   */
  private static final String AGENT_CONTEXT = "/agent";

  public static Map<String, List<HttpYaml>> httpserverConfData(
	  String webContext, String actionPattern, String agentHost, String agentport) {

	//----------------Agent启动配置信息-------------------------
	HttpYaml confBean = new HttpYaml();
	confBean.setName(HttpServerConf.DEFAULT_DBNAME);
	confBean.setHost(agentHost);
	confBean.setPort(Integer.parseInt(agentport));
	confBean.setWebContext(
		StringUtil.isNotBlank(webContext) ? webContext : AGENT_CONTEXT);
	confBean.setActionPattern(
		StringUtil.isNotBlank(actionPattern)
			? actionPattern
			: ACTION_PATTERN);

	//FIXME 下面的session Agent暂时用不到,先注释
//	YamlMap session = itemMap.getMap("session");
//	if (session != null) {
//	  HttpYaml yamlItem = new HttpYaml();
//	  yamlItem.setMaxage(session.getInt("maxage", 300)); // 默认的session过期时间：5分钟。
//	  yamlItem.setHttponly(session.getString("httponly", "false"));
//	  confBean.setSession(yamlItem);
//	}
	//存放所有配置信息的集合
	List<HttpYaml> httpServerConfList = new ArrayList<HttpYaml>();
	//放入Agent启动配置信息
	httpServerConfList.add(confBean);

	//----------------海云Receive配置连接信息-------------------------
    confBean = new HttpYaml();
	confBean.setName(HYREN_NAME);
	confBean.setHost(PropertyParaValue.getString("hyren_host", "127.0.0.1"));
	confBean.setPort(PropertyParaValue.getInt("hyren_port", 2000));
	confBean.setWebContext(WEB_CONTEXT);
	confBean.setActionPattern(ACTION_PATTERN);
	//放入海云Receive配置连接信息
	httpServerConfList.add(confBean);

	Map<String, List<HttpYaml>> httpServerMap = new HashMap<>();
	//将配置信息统一归属到 HttpServer.HTTP_NAME 下
	httpServerMap.put(HttpServer.HTTP_NAME, httpServerConfList);
	return httpServerMap;
  }
}
