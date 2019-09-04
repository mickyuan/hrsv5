package hrds.commons.communication;

import fd.ng.netserver.conf.HttpServerConfBean;
import hrds.commons.utils.PropertyParaValue;

public final class HrdsReceiceAgentConf extends HttpServerConfBean {
    public static final HttpServerConfBean confBean = new HttpServerConfBean();

    static {
        confBean.setHost(PropertyParaValue.getString("hyren_host", "127.0.0.1"));
        confBean.setHttpPort(PropertyParaValue.getInt("hyren_port", 2000));
        confBean.setIdleTimeout(PropertyParaValue.getInt("hyren_Timeout", 30000));
        confBean.setHttpsPort(PropertyParaValue.getInt("hyrens_host", 0));
        confBean.setWebContext(PropertyParaValue.getString("webContext", "/fdctx"));
        confBean.setActionPattern(PropertyParaValue.getString("actionPattern", "/agent/*"));
        confBean.setSession_MaxAge(confBean.Session_MaxAge_Default);
        confBean.setSession_HttpOnly(false);
    }


    private HrdsReceiceAgentConf() { throw new AssertionError("No HttpServerConf instances for you!"); }

    public static String string() {
        return "HttpServerConf{" +
                "host=" + confBean.getHost() +
                ", httpPort=" + confBean.getHttpPort() +
                ", httpsPort=" + ((confBean.getHttpsPort()==0)?"":String.valueOf(confBean.getHttpsPort())) +
                ", webContext=" + confBean.getWebContext() +
                ", actionPattern=" + confBean.getActionPattern() +

                ", Session_MaxAge=" + confBean.getSession_MaxAge() +
                ", Session_HttpOnly=" + confBean.isSession_HttpOnly() +

                '}';
    }
}
