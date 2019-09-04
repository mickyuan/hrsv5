package hrds.commons.action;

import fd.ng.core.utils.DateUtil;
import hrds.commons.base.BaseAction;
import hrds.commons.utils.ActionUtil;
import hrds.commons.utils.User;
import org.junit.Test;

public class cookieTest extends  BaseAction{
    @Test
    public void addUser() {

        User userCookie = new User();
        userCookie.setUserId("1001");
        userCookie.setUserName("张三");
        userCookie.setRoleId("100001");
        userCookie.setDepName("采集管理员");
        userCookie.setLoginIp("127.0.0.1");
        userCookie.setLoginDate(DateUtil.getSysDate());

        ActionUtil.setCookieUser(userCookie);


        User user = getUser();//获取当前登陆的用户信息
        Long userId = getUserId();//获取当前用户的ID
        String userName = getUserName();//获取当前用户的名字
    }
}
