package hrds.main;

import fd.ng.core.utils.ClassUtil;
import fd.ng.netserver.http.WebServer;
import fd.ng.web.helper.ActionInstanceHelper;
import hrds.f.biz.timer.TimerManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class AppMain extends WebServer {

  private static final Logger logger = LogManager.getLogger(hrds.main.AppMain.class.getName());

  public static void main(String[] args) {
	AppMain webServer = new AppMain();
	webServer.init();
	webServer.config();
	try {
	  new TimerManager().AutoMonCommunication();
	  ClassUtil.loadClass(ActionInstanceHelper.class.getName());
	  logger.info("Initialize actions done.");
	} catch (ClassNotFoundException e) {
	  throw new RuntimeException(e);
	}
	webServer.start();

	// 如果不需要对启动过程进行细粒度的控制，可使用下面一句代替 init, config, start
	//		webServer.running();
  }

  // -----------------  定制本项目使用的各种能力  -----------------

  @Override
  protected void addServlet(ServletContextHandler contextApp) {
	//		contextApp.addServlet(MyServlet.class, "/my/*");
  }

  @Override
  protected void addFilter(ServletContextHandler contextApp) {
	//		contextApp.addFilter(MyFilter.class,"/my/*", EnumSet.of(DispatcherType.REQUEST));
	//		contextApp.addFilter(WrapFdwebFilter.class, WebinfoHelper.ActionPattern,
	// EnumSet.of(DispatcherType.REQUEST));
  }

  @Override
  protected void addListener(ServletContextHandler contextApp) {
	//		contextApp.getServletHandler().addListener(new ListenerHolder(InitListener.class));
	//		contextApp.getServletHandler().addListener(new ListenerHolder(RequestListener.class));
  }
}
