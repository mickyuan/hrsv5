package hrds.commons.code;

import hrds.commons.codes.AgentStatus;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @program: hrsv5
 * @description: 代码项测试用例
 * @author: xchao
 * @create: 2019-09-10 09:42
 */
public class codeTest {
	@Test
	public void getCode(){
		AgentStatus weiLianJie = AgentStatus.WeiLianJie;
		assertThat(weiLianJie.getCode(),is("2"));
		assertThat(weiLianJie.getValue(),is("未连接"));
		assertThat(weiLianJie.getCatCode(),is("4"));
		assertThat(weiLianJie.getCatValue(),is("Agent状态"));
		assertThat(AgentStatus.getValue(weiLianJie.getCode()),is("未连接"));
		/*AgentStatus.WeiLianJie.getCode().toString();
		AgentStatus.getObjCatValue().toString();
		AgentStatus.YiLianJie.toString();
		AgentStatus.values().toString();*/



	}
}
