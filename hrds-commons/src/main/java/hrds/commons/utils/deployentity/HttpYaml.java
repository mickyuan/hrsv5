package hrds.commons.utils.deployentity;

import fd.ng.core.annotation.DocClass;

@DocClass(desc = "写HttpYaml文件的实体", author = "Mr.Lee", createdate = "2020-02-17 13:57")
public class HttpYaml {

  private String name;
  private String host;
  private int port;
  private String webContext;
  private String actionPattern;
  private long maxage;
  private String httponly;
  private String hyren_host;
  private int hyren_port;
  private HttpYaml session;

  public String getName() {

    return name;
  }

  public void setName(String name) {

    this.name = name;
  }

  public String getHost() {

    return host;
  }

  public void setHost(String host) {

    this.host = host;
  }

  public int getPort() {

    return port;
  }

  public void setPort(int port) {

    this.port = port;
  }

  public String getWebContext() {

    return webContext;
  }

  public void setWebContext(String webContext) {

    this.webContext = webContext;
  }

  public String getActionPattern() {

    return actionPattern;
  }

  public void setActionPattern(String actionPattern) {

    this.actionPattern = actionPattern;
  }

  public long getMaxage() {

    return maxage;
  }

  public void setMaxage(long maxage) {

    this.maxage = maxage;
  }

  public String getHttponly() {

    return httponly;
  }

  public void setHttponly(String httponly) {

    this.httponly = httponly;
  }

  public String getHyren_host() {

    return hyren_host;
  }

  public void setHyren_host(String hyren_host) {

    this.hyren_host = hyren_host;
  }

  public int getHyren_port() {

    return hyren_port;
  }

  public void setHyren_port(int hyren_port) {

    this.hyren_port = hyren_port;
  }

  public HttpYaml getSession() {

    return session;
  }

  public void setSession(HttpYaml session) {

    this.session = session;
  }
}
