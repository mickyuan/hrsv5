package hrds.testbase;

import fd.ng.core.exception.internal.RawlayerRuntimeException;
import fd.ng.core.utils.ArrayUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.netclient.http.HttpClient;
import fd.ng.netserver.conf.HttpServerConf;
import fd.ng.test.junit.FdBaseTestCase;
import hrds.commons.utils.ActionUtil;
import hrds.commons.utils.User;
import okhttp3.Cookie;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Optional;

public class WebBaseTestCase extends FdBaseTestCase {
	protected String getHost() {
		return (StringUtil.isBlank(HttpServerConf.confBean.getHost()) ? "localhost" : HttpServerConf.confBean.getHost());
	}

	protected String getPort() {
		return String.valueOf(HttpServerConf.confBean.getHttpPort());
	}

	protected String getHostPort() {
		String ActionPattern = HttpServerConf.confBean.getActionPattern();
		if (ActionPattern.endsWith("/*")) ActionPattern = ActionPattern.substring(0, ActionPattern.length() - 2);
		return "http://" + getHost() + ":" + getPort();
	}

	protected String getUrlCtx() {
		return getHostPort() + HttpServerConf.confBean.getWebContext();
	}

	protected String getUrlActionPattern() {
		String ActionPattern = HttpServerConf.confBean.getActionPattern();
		if (ActionPattern.endsWith("/*")) ActionPattern = ActionPattern.substring(0, ActionPattern.length() - 2);
		return getUrlCtx() + ActionPattern;
	}

	protected String getActionPath() {
		return getUrlActionPattern()
				+ "/" + this.getClass().getPackage().getName().replace(".", "/");
	}

	protected String getActionUrl(String actionMethodName) {
		return getActionPath() + "/" + actionMethodName;
	}

	protected User getCookic() {
		try {
			String host = getHost();
			List<Cookie> cookieList = HttpClient.getCookie(host);
			if (cookieList.size() != 0) {
				for (Cookie cookie : cookieList) {
					if (ActionUtil._userCookieName.equals(cookie.name())) {
						String val0 = cookie.value();
						String val1 = URLDecoder.decode(val0, StringUtil.UTF_8);
						Optional<String> cookieStr = JsonUtil.toObjectSafety(val1, String.class);
						User userCookie  = JsonUtil.toObjectSafety(cookieStr.get(),User.class).get();
						return userCookie == null ? new User() : userCookie;
					}
				}
			}
			return null;
		} catch (UnsupportedEncodingException e) {
			throw new RawlayerRuntimeException(e);
		}
	}
}
