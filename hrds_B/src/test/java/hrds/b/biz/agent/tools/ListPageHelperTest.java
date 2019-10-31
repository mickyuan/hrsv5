package hrds.b.biz.agent.tools;

import fd.ng.core.annotation.DocClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "为实现给List集合进行分页设计的工具类测试类", author = "WangZhengcheng")
public class ListPageHelperTest {

	private static final List<String> testedList;

	static{
		testedList = new ArrayList<>();
		for(int i = 0; i < 20; i++){
			String value = "value" + String.valueOf(i);
			testedList.add(value);
		}
	}

	@Test
	public void getPageListTestCaseOne(){
		List<String> pageList = new ListPageHelper<>(testedList, 1,3).getList();
		for(String str : pageList){
			if(!str.equalsIgnoreCase("value0") && !str.equalsIgnoreCase("value1") && !str.equalsIgnoreCase("value2")){
				assertThat("出现了不符合预期的情况：" + str, true, is(false));
			}
		}
	}

	@Test
	public void getPageListTestCaseTwo(){
		List<String> pageList = new ListPageHelper<>(testedList, 2,3).getList();
		for(String str : pageList){
			if(!str.equalsIgnoreCase("value3") && !str.equalsIgnoreCase("value4") && !str.equalsIgnoreCase("value5")){
				assertThat("出现了不符合预期的情况：" + str, true, is(false));
			}
		}
	}
}
