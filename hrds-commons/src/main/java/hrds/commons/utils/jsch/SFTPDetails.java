package hrds.commons.utils.jsch;

public class SFTPDetails {

  /** 登陆密码 */
  private String pwd;
  /** 登陆地址IP */
  private String host;
  /**  登陆用户 */
  private int port;
  /**  登陆端口 */
  private String user_name;

  public String getUser_name() {
    return user_name;
  }

  public void setUser_name(String user_name) {
    this.user_name = user_name;
  }

  public String getPwd() {
    return pwd;
  }

  public void setPwd(String pwd) {
    this.pwd = pwd;
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
}
