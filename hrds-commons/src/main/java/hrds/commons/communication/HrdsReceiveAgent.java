package hrds.commons.communication;

import fd.ng.core.utils.ClassUtil;
import fd.ng.netserver.conf.HttpServerConfBean;
import fd.ng.netserver.http.WebServer;
import fd.ng.web.helper.ActionInstanceHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class HrdsReceiveAgent extends WebServer {
    protected static final Logger logger = LogManager.getLogger(HrdsReceiveAgent.class.getName());

    public HrdsReceiveAgent(HttpServerConfBean confBase) {
        super(confBase);
    }

    @Override
    protected void doInit() throws Exception {
        logger.info("Initialize webinfo done.");
    }

    public static void main(String[] args) {
        HrdsReceiveAgent hrdsReceiveAgent = new HrdsReceiveAgent(HrdsReceiceAgentConf.confBean);
        hrdsReceiveAgent.init();
        hrdsReceiveAgent.config();
        try {
            ClassUtil.loadClass(ActionInstanceHelper.class.getName());
            logger.info("Initialize actions done.");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        hrdsReceiveAgent.start();
        // 如果不需要对启动过程进行细粒度的控制，可使用下面一句代替 init, config, start
        //webServer.running();
    }

    @Override
    protected void addServlet(ServletContextHandler contextApp) {
    }

    @Override
    protected void addFilter(ServletContextHandler contextApp) {
    }

    @Override
    protected void addListener(ServletContextHandler contextApp) {
    }
}
