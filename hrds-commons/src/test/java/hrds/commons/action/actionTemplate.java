package hrds.commons.action;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import hrds.commons.base.BaseAction;
import hrds.commons.entity.Sys_user;
import hrds.commons.utils.User;

import java.sql.ResultSet;
import java.util.*;

@DocClass(desc = "action注释模版",author = "xchao",createdate = "2019-09-16 10:57")
public class actionTemplate extends BaseAction {
    @Method(desc = "获取用户",
            logicStep = "1、获取用户相似度级分类" +
                    "2、斯蒂芬吉林省地方j" +
                    "3、sdfdsfsd" +
                    "4、斯蒂芬来看待警方立刻收到了放开就说了肯定塑料袋看风景" +
                    "5、莱克斯顿副经理看路上看到剧分里看电视剧了是的看积分历史库到机房" +
                    "6、送到路口附近了历史解放路看电视两市低开解放路看电视禄口街道私聊发历史库到机房了开始记得发")
    @Param(name = "name", desc = "用户名", range = "任意", example = "李强",valueIfNull = "xchao")
    @Param(name = "sex", desc = "性别", range = "男，女", example = "女",valueIfNull = "男")
    @Param(name = "user", desc = "用户实体", range = "实体bean",isBean = true)
    @Param(name = "age", desc = "年龄", range = "10-100之间")
    @Param(name = "ip", desc = "agentIp信息", range = "任意", example = "10.71.9.100")
    public void getUserInfo(String name, String sex, Sys_user user, int age, String ip) {

    }

    @Method(desc = "新增用户", logicStep = "")
    @Param(name = "user", desc = "用户实体", isBean = true, range = "实体bean")
    @Return(desc = "返回是否新增成功的表示",range = "true,false")
    public boolean addUser(Sys_user user) {

        return false;
    }

    @Method(desc = "获取用户信息，返回实体信息", logicStep = "")
    @Param(name = "user", desc = "用户实体", isBean = true, range = "实体bean")
    @Return(desc = "返回用户的基本信息以实体返回", range = "User实体ben", isBean = true)
    public User getUser(Sys_user user) {

        return new User();
    }


    @Method(desc = "新增用户", logicStep = "")
    @Return(desc = "返回用户的基本信息以result返回", range = "返回用户result")
    public Result getUsercc() {
        List aa = new ArrayList<Map<String, Object>>();
        Map map = new HashMap();
        map.put("abc", "sdfdsfsd");
        aa.add(map);
        Result a = new Result();
        a.add(aa);

        a.getString(0, "abc");
        return new Result();
    }
    public static void main(String[] args) {
        try(DatabaseWrapper db = new DatabaseWrapper.Builder().dbname("Hive").create()){
            Set<String> aa = new HashSet<>();
            aa.add("sdf");
            aa.add("bb");
            String testcc = db.getDbtype().ofKeyLableSql("testcc", aa,"aa");
            Result result = SqlOperator.queryResult(db, "select * from testcc");
            ResultSet resultSet = db.queryPagedGetResultSet("select * from testcc", 1, 1000, false);
        }
    }
}
