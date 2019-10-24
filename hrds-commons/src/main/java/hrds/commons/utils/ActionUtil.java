package hrds.commons.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.web.util.RequestUtil;
import hrds.commons.exception.BusinessException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Optional;

public class ActionUtil {
	private static final Logger logger = LogManager.getLogger();
	public static final String _userCookieName = "Hyren_userCookie";

	/**
	 * 獲取userCookie中的数据
	 * TODO 只是个临时方案
	 * @return
	 */
	public static User getUser(String cookieSrc) {
		User user = new User();
		try {
			//这样便可以获取一个cookie数组
			if(StringUtil.isEmpty(cookieSrc)){
				cookieSrc = RequestUtil.getCookieObject(_userCookieName, String.class);
			}else {
				cookieSrc = new String(Base64.getDecoder().decode(cookieSrc));
			}
			Optional<User> optional = JsonUtil.toObjectSafety(cookieSrc, User.class);
			if(optional.isPresent()){
				User user1 = optional.get();
				return user1;
			}
			return user;
		} catch (Exception e) {
			logger.error("获取user失败", e);
			throw new BusinessException("获取user失败");
		}
	}

	/**
	 * setCookieUser,将user数据存放到Cookie中
	 * TODO 只是个临时方案
	 * @param user
	 */
	public static String setCookieUser(User user) {
		String jsonUser = JsonUtil.toJson(user);
		String strValue = Base64.getEncoder().encodeToString(jsonUser.getBytes());
		RequestUtil.putCookieObject(_userCookieName, jsonUser);
		return strValue;
	}
}