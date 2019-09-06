package hrds.commons.action;

import fd.ng.web.action.AbstractWebappBaseAction;
import fd.ng.web.util.RequestUtil;

/**
 * @program: hrsv5
 * @description: 测试联通
 * @author: xchao
 * @create: 2019-09-05 16:53
 */
public class actionTest extends AbstractWebappBaseAction {
    public void testcc() {
        System.out.println(RequestUtil.getJson());
    }
}
