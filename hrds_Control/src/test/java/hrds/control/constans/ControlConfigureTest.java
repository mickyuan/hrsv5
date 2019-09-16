package hrds.control.constans;

import fd.ng.core.utils.StringUtil;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * ClassName: ControlConfigureTest<br>
 * Description: 用于测试control项目中读取配置文件的类。<br>
 * Author: Tiger.Wang<br>
 * Date: 2019/9/16 16:52<br>
 * Since: JDK 1.8
 **/
public class ControlConfigureTest {

	@Test
	public void testNotifyConfig() {

		assertNotNull("测试是否读取到control配置文件notify节点的isNeedSendSMS属性",
				ControlConfigure.NotifyConfig.isNeedSendSMS);

		assertFalse("测试是否读取到control配置文件notify节点的smsAccountName属性",
				StringUtil.isEmpty(ControlConfigure.NotifyConfig.smsAccountName));

		assertFalse("测试是否读取到control配置文件notify节点的smsAccountPasswd属性",
				StringUtil.isEmpty(ControlConfigure.NotifyConfig.smsAccountPasswd));

		assertFalse("测试是否读取到control配置文件notify节点的cmHostIp属性",
				StringUtil.isEmpty(ControlConfigure.NotifyConfig.cmHostIp));

		assertNotNull("测试是否读取到control配置文件notify节点的cmHostPort属性",
				ControlConfigure.NotifyConfig.cmHostPort);

		assertFalse("测试是否读取到control配置文件notify节点的wsHostIp属性",
				StringUtil.isEmpty(ControlConfigure.NotifyConfig.wsHostIp));

		assertNotNull("测试是否读取到control配置文件notify节点的wsHostPort属性",
				ControlConfigure.NotifyConfig.wsHostPort);

		assertFalse("测试是否读取到control配置文件notify节点的phoneNumber属性",
				StringUtil.isEmpty(ControlConfigure.NotifyConfig.phoneNumber));

		assertFalse("测试是否读取到control配置文件notify节点的bizType属性",
				StringUtil.isEmpty(ControlConfigure.NotifyConfig.bizType));
	}

	@Test
	public void testRedisConfig() {

		assertFalse("测试是否读取到control配置文件redis节点的redisIp属性",
				StringUtil.isEmpty(ControlConfigure.RedisConfig.redisIp));

		assertNotNull("测试是否读取到control配置文件redis节点的redisPort属性",
				ControlConfigure.RedisConfig.redisPort);

		assertNotNull("测试是否读取到control配置文件redis节点的timeout属性",
				ControlConfigure.RedisConfig.timeout);
	}

}