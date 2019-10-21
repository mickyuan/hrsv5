package hrds.commons.code;

import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.AgentStatus;
import hrds.commons.entity.Sys_para;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;

/**
 * @program: hrsv5
 * @description: 代码项测试用例
 * @author: xchao
 * @create: 2019-09-10 09:42
 */
public class codeTest extends BaseAction {
	@Method(desc = "获取用户",
			logicStep = "1、获取用户相似度级分类" +
					"2、斯蒂芬吉林省地方j" +
					"3、sdfdsfsd" +
					"4、斯蒂芬来看待警方立刻收到了放开就说了肯定塑料袋看风景" +
					"5、莱克斯顿副经理看路上看到剧分里看电视剧了是的看积分历史库到机房" +
					"6、送到路口附近了历史解放路看电视两市低开解放路看电视禄口街道私聊发历史库到机房了开始记得发")
	@Param(name = "ip", desc = "agentIp信息", range = "任意", example = "10.71.9.100")
	public void t41_queryToBean(String ip) {
		try(DatabaseWrapper db = new DatabaseWrapper()) {
			Optional<Sys_para> result = SqlOperator.queryOneObject(db, Sys_para.class,
					"select * from " + Sys_para.TableName + " where para_name=?",
					"sys_Name"
			);
			// 不提取 Opetional 总的对象，直接使用
			assertThat(result.map(Sys_para::getPara_name).get(), Matchers.is("sys_Name"));
			assertThat(result.map(Sys_para::getPara_value).get(), Matchers.is("海云数服"));
		}
	}
}
