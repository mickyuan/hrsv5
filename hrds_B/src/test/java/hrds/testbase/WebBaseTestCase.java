package hrds.testbase;

import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.netclient.http.HttpClient;
import fd.ng.netserver.conf.HttpServerConf;
import fd.ng.test.junit.FdBaseTestCase;
import fd.ng.web.action.ActionResult;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.Constant;

public class WebBaseTestCase extends FdBaseTestCase {

	protected String getHost() {
		return (StringUtil.isBlank(HttpServerConf.confBean.getHost()) ? "localhost" : HttpServerConf.confBean.getHost());
	}

	protected String getPort() {
		return String.valueOf(HttpServerConf.confBean.getHttpPort());
	}

	protected String getHostPort() {
		String ActionPattern = HttpServerConf.confBean.getActionPattern();
		if (ActionPattern.endsWith("/*")) {
			ActionPattern = ActionPattern.substring(0, ActionPattern.length() - 2);
		}
		return "http://" + getHost() + ":" + getPort();
	}

	protected String getUrlCtx() {
		return getHostPort() + HttpServerConf.confBean.getWebContext();
	}

	protected String getUrlActionPattern() {
		String ActionPattern = HttpServerConf.confBean.getActionPattern();
		if (ActionPattern.endsWith("/*")) {
			ActionPattern = ActionPattern.substring(0, ActionPattern.length() - 2);
		}
		return getUrlCtx() + ActionPattern;
	}

	protected String getActionPath() {
		return getUrlActionPattern()
				+ "/" + this.getClass().getPackage().getName().replace(".", "/");
	}

	protected String getActionUrl(String actionMethodName) {
		return getActionPath() + "/" + actionMethodName;
	}


	public static ActionResult login() {
		String responseValue = new HttpClient().buildSession()
				.addData("user_id", Constant.TESTINITCONFIG.getString("user_id", "2001"))
				.addData("password", Constant.TESTINITCONFIG.getString("password", "1"))
				.post(Constant.TESTINITCONFIG.getString("login_url", "http://127.0.0.1:8888/A/action/hrds/a/biz/login/login"))
				.getBodyString();
		return JsonUtil.toObjectSafety(responseValue, ActionResult.class).orElseThrow(() -> new BusinessException("连接失败"));
	}
}
