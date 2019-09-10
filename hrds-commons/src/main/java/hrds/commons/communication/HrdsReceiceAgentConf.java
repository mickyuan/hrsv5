package hrds.commons.communication;

import fd.ng.core.yaml.YamlMap;
import fd.ng.netserver.conf.HttpServerConf;
import fd.ng.netserver.conf.HttpServerConfBean;
import hrds.commons.utils.PropertyParaValue;

public final class HrdsReceiceAgentConf extends HttpServerConfBean {
    public static final HttpServerConfBean confBean;

    static {
        confBean = HttpServerConf.getHttpServer("hyrenagent");
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
