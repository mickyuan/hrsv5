package hrds.commons.code;

import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
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
public class codeTest {
	@Test
	public void getCode(){
		/*AgentStatus weiLianJie = AgentStatus.WeiLianJie;
		assertThat(weiLianJie.getCode(),is("2"));
		assertThat(weiLianJie.getValue(),is("未连接"));
		assertThat(weiLianJie.getCatCode(),is("4"));
		assertThat(weiLianJie.getCatValue(),is("Agent状态"));
		assertThat(AgentStatus.ofValueByCode(weiLianJie.getCode()),is("未连接"));*/
		/*AgentStatus.WeiLianJie.getCode().toString();
		AgentStatus.getObjCatValue().toString();
		AgentStatus.YiLianJie.toString();
		AgentStatus.values().toString();*/
	}
	@Test
	public void t41_queryToBean() {
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
