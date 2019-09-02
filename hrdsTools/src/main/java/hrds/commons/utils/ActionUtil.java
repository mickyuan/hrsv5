package hrds.commons.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

public class ActionUtil {
    private static final Logger logger = LogManager.getLogger(ActionUtil.class.getName());

    public static hrds.commons.utils.User getUser(HttpServletRequest request) {
        hrds.commons.utils.User user = new User();
       /* try {
            Cookie[] cookies = request.getCookies();//这样便可以获取一个cookie数组
            String userJson = "";
            for (Cookie cookie : cookies) {
                if ("user_cookie".equals(cookie.getName())) {
                    userJson = URLDecoder.decode(cookie.getValue(), "UTF-8");
                }
            }
            JSONObject userJsonObj = (JSONObject) JSONObject.parse(userJson);
            if (userJsonObj != null) {
                user.setUserId(Long.valueOf(userJsonObj.getString("user_id")));
                user.setUserName(userJsonObj.getString("user_name"));
                user.setUserType(userJsonObj.getString("user_type"));
                user.setUserPassword(userJsonObj.getString("user_password"));
                user.setDepName(userJsonObj.getString("dep_name"));
                user.setDepId(userJsonObj.getString("dep_id"));
                user.setRoleName(userJsonObj.getString("role_name"));
                user.setRoleId(userJsonObj.getString("role_id"));
                user.setUserTypeGroup(userJsonObj.getString("usertype_group"));
            }
        } catch (Exception e) {
            logger.error("获取user失败", e);
            //throw new BusinessException("获取user失败");
            return user;
        }*/
        return user;
    }

}
