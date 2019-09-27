package hrds.commons.utils;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.utils.JsonUtil;
import fd.ng.web.util.RequestUtil;
import hrds.commons.exception.BusinessException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ActionUtil {
    private static final Logger logger = LogManager.getLogger();
    private static final String _userCookieName = "Hyren_userCookie";

    /**
     * 獲取userCookie中的数据
     *
     * @return
     */
    public static User getUser() {
        User user = new User();
        try {
            //这样便可以获取一个cookie数组
            String cookieSrc = RequestUtil.getCookieObject(_userCookieName, String.class);
            User userCookie  = JsonUtil.toObjectSafety(cookieSrc,User.class).get();
            return userCookie == null ? user : userCookie;
        } catch (Exception e) {
            logger.error("获取user失败", e);
            throw new BusinessException("获取user失败");
        }
    }

    /**
     * setCookieUser,将user数据存放到Cookie中
     *
     * @param user
     */
    public static void setCookieUser(User user) {
        String jsonUser = JsonUtil.toJson(user);
        RequestUtil.putCookieObject(_userCookieName, jsonUser);
    }

}
