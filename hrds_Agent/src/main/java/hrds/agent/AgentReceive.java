package hrds.agent;

import fd.ng.core.utils.ClassUtil;
import fd.ng.netserver.http.WebServer;
import fd.ng.web.helper.ActionInstanceHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @program: hrsv5
 * @description: agent的接收进程
 * @author: xchao
 * @create: 2019-09-05 11:13
 */
public class AgentReceive extends WebServer {
    protected static final Logger logger = LogManager.getLogger(AgentReceive.class.getName());

    @Override
    protected void doInit() throws Exception {
        logger.info("Initialize webinfo done.");
    }

    public static void main(String[] args) {
        AgentReceive agentReceive = new AgentReceive();
        agentReceive.init();
        agentReceive.config();
        try {
            ClassUtil.loadClass(ActionInstanceHelper.class.getName());
            logger.info("Initialize actions done.");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        agentReceive.start();
        // 如果不需要对启动过程进行细粒度的控制，可使用下面一句代替 init, config, start
        //agentReceive.running();
    }
}
