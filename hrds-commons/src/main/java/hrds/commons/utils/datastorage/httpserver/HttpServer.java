package hrds.commons.utils.datastorage.httpserver;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.StringUtil;
import fd.ng.core.yaml.YamlArray;
import fd.ng.core.yaml.YamlFactory;
import fd.ng.core.yaml.YamlMap;
import fd.ng.netserver.conf.HttpServerConf;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.PropertyParaValue;
import hrds.commons.utils.deployentity.HttpYaml;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "组成HttpServer.conf配置文件的数据", author = "Mr.Lee", createdate = "2020-02-17 14:23")
public class HttpServer {
  /** httpserver的name名称 */
  public static final String HTTP_NAME = "httpserver";
  /** httpserver文件名称 */
  public static final String HTTP_CONF_NAME = "httpserver.conf";
  /** 海云接口服务配置文件key名称 */
  private static final String HYREN_NAME = "hyren_main";

  public static Map<String, List<HttpYaml>> httpserverConfData(
      String webContext, String actionPattern, String agentHost, String agentport) {
    String httpServerPath =
        System.getProperty("user.dir")
            + File.separator
            + "resources"
            + File.separator
            + "fdconfig"
            + File.separator
            + HTTP_CONF_NAME;
    YamlMap rootConfig = YamlFactory.load(new File(httpServerPath)).asMap();
    YamlArray typeContrast = rootConfig.getArray(HTTP_NAME);
    List<HttpYaml> httpServerConfList = new ArrayList<HttpYaml>();
    for (int i = 0; i < typeContrast.size(); i++) {
      YamlMap itemMap = typeContrast.getMap(i);
      // agent的配置信息
      if (HttpServerConf.DEFAULT_DBNAME.equals(itemMap.getString("name"))) {
        HttpYaml confBean = new HttpYaml();
        confBean.setName(HttpServerConf.DEFAULT_DBNAME);
        confBean.setHost(agentHost);
        confBean.setPort(Integer.parseInt(agentport));
        confBean.setWebContext(
            StringUtil.isNotBlank(webContext) ? webContext : itemMap.getString("webContext"));
        confBean.setActionPattern(
            StringUtil.isNotBlank(actionPattern)
                ? actionPattern
                : itemMap.getString("actionPattern"));
        YamlMap session = itemMap.getMap("session");
        if (session != null) {
          HttpYaml yamlItem = new HttpYaml();
          yamlItem.setMaxage(session.getInt("maxage", 300)); // 默认的session过期时间：5分钟。
          yamlItem.setHttponly(session.getString("httponly", "false"));
          confBean.setSession(yamlItem);
        }
        httpServerConfList.add(confBean);

      } else if (HYREN_NAME.equals(itemMap.getString("name"))) { // 这里是海云服务接口的配置信息
        HttpYaml confBeanMap = new HttpYaml();
        confBeanMap.setName(HYREN_NAME);
        confBeanMap.setHost(PropertyParaValue.getString("hyren_host", "127.0.0.1"));
        confBeanMap.setPort(PropertyParaValue.getInt("hyren_port", 2000));
        confBeanMap.setWebContext(itemMap.getString("webContext"));
        confBeanMap.setActionPattern(itemMap.getString("actionPattern"));
        httpServerConfList.add(confBeanMap);
      } else {
        throw new BusinessException("未在配置文件中找到对应的 name 信息");
      }
    }
    Map<String, List<HttpYaml>> httpServerMap = new HashMap<>();
    httpServerMap.put(HttpServer.HTTP_NAME, httpServerConfList);
    return httpServerMap;
  }
}
