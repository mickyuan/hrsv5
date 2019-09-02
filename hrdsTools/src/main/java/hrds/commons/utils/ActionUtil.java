package hrds.commons.utils;

import fd.ng.web.util.RequestUtil;
import hrds.commons.exception.BusinessException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ActionUtil {
    private static final Logger logger = LogManager.getLogger(ActionUtil.class.getName());
    private static final String _userCookieName = "userCookie";

    /**
     * 獲取userCookie中的数据
     *
     * @return
     */
    public static hrds.commons.utils.User getUser() {
        hrds.commons.utils.User user = new User();
        try {
            //这样便可以获取一个cookie数组
            User userCookie = RequestUtil.getCookieObject(_userCookieName, user.getClass());
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
        RequestUtil.putCookieObject(_userCookieName, user.getClass());
    }

}
