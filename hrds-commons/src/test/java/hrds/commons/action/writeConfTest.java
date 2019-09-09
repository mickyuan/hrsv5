package hrds.commons.action;

import fd.ng.netserver.conf.HttpServerConf;
import fd.ng.netserver.conf.HttpServerConfBean;
import hrds.commons.codes.Dispatch_Type;
import hrds.commons.communication.HrdsReceiceAgentConf;
import hrds.commons.utils.jsch.WriteConf;
import org.junit.Test;

/**
 * <p>标    题: 海云数服 V5.0</p>
 * <p>描    述: </p>
 * <p>版    权: Copyright (c) 2019</p>
 * <p>公    司: 博彦科技(上海)有限公司</p>
 * <p>@author : Mr.Lee</p>
 * <p>创建时间 : 2019-09-06 10:42</p>
 * <p>version: JDK 1.8</p>
 */
public class writeConfTest {

	@Test
	public void httpServer() {

		WriteConf.writeHttpServeConf("httpserver.conf");
		WriteConf.writeHyRenConf("httpserver.conf");
	}
}
