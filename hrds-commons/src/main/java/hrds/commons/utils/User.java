package hrds.commons.utils;

import fd.ng.core.bean.EqualsBuilder;
import fd.ng.core.bean.FeedBean;
import fd.ng.core.utils.StringUtil;

public class User extends FeedBean {
    private Long userId; //用户ID
    private String roleId; //角色ID
    private String userName; //用户名称
    private String userPassword; //用户密码
    private String userEmail; //邮箱
    private String userMobile; //移动电话
    private String userType; //用户类型
    private String loginIp; //登录IP
    private String loginDate; //最后登录时间
    private String userState; //用户状态
    private Long depId; //部门ID
    private String depName; //部门名称
    private String roleName; //角色名称
    private String userTypeGroup;//用户类型组

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUserId(String userId) {
        if (!StringUtil.isEmpty(userId)) {
            this.userId = new Long(userId);
        }
    }

    public String getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(String loginDate) {
        this.loginDate = loginDate;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public String getUserState() {
        return userState;
    }

    public void setUserState(String userState) {
        this.userState = userState;
    }

    public Long getDepId() {
        return depId;
    }

    public void setDepId(Long depId) {
        this.depId = depId;
    }

    public void setDepId(String depId) {
        if (!StringUtil.isEmpty(depId)) {
            this.depId = new Long(depId);
        }
    }

    public String getDepName() {
        return depName;
    }

    public void setDepName(String depName) {
        this.depName = depName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getUserTypeGroup() {
        return userTypeGroup;
    }

    public void setUserTypeGroup(String userTypeGroup) {
        this.userTypeGroup = userTypeGroup;
    }
}
