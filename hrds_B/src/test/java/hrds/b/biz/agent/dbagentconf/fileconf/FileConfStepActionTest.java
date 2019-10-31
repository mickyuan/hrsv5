package hrds.b.biz.agent.dbagentconf.fileconf;

import fd.ng.core.annotation.DocClass;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@DocClass(desc = "FileConfStepAction单元测试类", author = "WangZhengcheng")
public class FileConfStepActionTest extends WebBaseTestCase{

	@Before
	public void before(){
		InitAndDestDataForFileConf.before();
	}

	@Test
	public void getInitInfo(){

	}

	@Test
	public void getAllTbSepConf(){

	}

	@Test
	public void saveAllTbSepConf(){

	}

	@After
	public void after(){
		InitAndDestDataForFileConf.after();
	}

}
