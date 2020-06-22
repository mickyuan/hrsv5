package hrds.testbase;

import fd.ng.core.conf.ConfFileLoader;
import fd.ng.core.utils.StringUtil;
import fd.ng.core.yaml.YamlFactory;
import fd.ng.core.yaml.YamlMap;
import fd.ng.netserver.conf.HttpServerConf;
import fd.ng.test.junit.FdBaseTestCase;

public class WebBaseTestCase extends FdBaseTestCase {
	//读取测试用例初始化数据
	protected static final YamlMap agentInitConfig = YamlFactory.load(ConfFileLoader.getConfFile("testinfo")).asMap();
	protected String getHost() {
		return (StringUtil.isBlank(HttpServerConf.confBean.getHost())?"localhost": HttpServerConf.confBean.getHost());
	}
	protected String getPort() {
		return String.valueOf(HttpServerConf.confBean.getHttpPort());
	}
	protected String getHostPort() {
		String ActionPattern = HttpServerConf.confBean.getActionPattern();
		if(ActionPattern.endsWith("/*")) ActionPattern = ActionPattern.substring(0, ActionPattern.length()-2);
		return "http://"+ getHost()	+ ":" + getPort();
	}
	protected String getUrlCtx() {
		return getHostPort() + HttpServerConf.confBean.getWebContext();
	}
	protected String getUrlActionPattern() {
		String ActionPattern = HttpServerConf.confBean.getActionPattern();
		if(ActionPattern.endsWith("/*")) ActionPattern = ActionPattern.substring(0, ActionPattern.length()-2);
		return getUrlCtx() + ActionPattern;
	}
	protected String getActionPath() {
		return getUrlActionPattern()
				+ "/" + this.getClass().getPackage().getName().replace(".", "/");
	}
	protected String getActionUrl(String actionMethodName) {
		return getActionPath() + "/" + actionMethodName;
	}
}
