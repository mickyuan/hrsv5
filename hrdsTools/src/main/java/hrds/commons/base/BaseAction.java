package hrds.commons.base;

import fd.ng.core.utils.StringUtil;
import fd.ng.web.action.AbstractWebappBaseAction;
import fd.ng.web.action.ActionResult;
import fd.ng.web.action.ActionResultHelper;
import hrds.commons.utils.ActionUtil;
import hrds.commons.utils.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

public class BaseAction extends AbstractWebappBaseAction {
    private static final Logger logger = LogManager.getLogger(BaseAction.class.getName());

    @Override
    protected ActionResult _doPreProcess(HttpServletRequest request) {
        User user = getUser();
        if (user == null) {
            return ActionResultHelper.bizError("no cookies");
        }
        String userId = String.valueOf(user.getUserId());
        if (StringUtil.isEmpty(userId)) {
            return ActionResultHelper.bizError("no login");
        }
        return null; // 验证通过
    }

    /**
     * 获取用户登录用户信息
     * @return
     */
    protected User getUser() {
        return ActionUtil.getUser();
    }

    /**
     * 获取当前登录的用户ID信息
     *
     * @return
     */
    protected Long getUserId() {
        User user = ActionUtil.getUser();
        return user.getUserId();
    }

    /**
     * 获取当前登录用户的用户名字信息
     *
     * @return
     */
    protected String getUserName() {
        User user = ActionUtil.getUser();
        return user.getUserName();
    }
}
