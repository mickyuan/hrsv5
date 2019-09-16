package hrds.control.constans;

import fd.ng.core.conf.ConfFileLoader;
import fd.ng.core.yaml.YamlFactory;
import fd.ng.core.yaml.YamlMap;
import hrds.commons.exception.AppSystemException;

/**
 * ClassName: ControlConfigure<br>
 * Description: 用于读取配置文件的类。<br>
 * Author: Tiger.Wang<br>
 * Date: 2019/9/16 16:52<br>
 * Since: JDK 1.8
 **/
public class ControlConfigure {

	private static final YamlMap control;

	static {
		try {
			control = YamlFactory.load(ConfFileLoader.getConfFile("control")).asMap();
		} catch (YamlFactory.YamlFileNotFoundException var2) {
			throw new AppSystemException("无法加载control配置文件");
		}
	}

	/**
	 * ClassName: NotifyConfig<br>
	 * Description: 用于读取control配置文件中notify节点配置信息的类。<br>
	 * Author: Tiger.Wang<br>
	 * Date: 2019/9/16 16:52<br>
	 * Since: JDK 1.8
	 **/
	public static class NotifyConfig {

		private static final String CONFNAME = "notify";

		public static final Boolean isNeedSendSMS;
		public static final String smsAccountName;
		public static final String smsAccountPasswd;
		public static final String cmHostIp;
		public static final Integer cmHostPort;
		public static final String wsHostIp;
		public static final Integer wsHostPort;
		public static final String phoneNumber;
		public static final String bizType;

		static {
			if(!control.exist(CONFNAME)) {
				throw new AppSystemException("无法从control配置文件中加载属性" + CONFNAME);
			}
			YamlMap notify = control.getMap(CONFNAME);
			isNeedSendSMS = notify.getBool("isNeedSendSMS");
			smsAccountName = notify.getString("smsAccountName");
			smsAccountPasswd = notify.getString("smsAccountPasswd");
			cmHostIp = notify.getString("cmHostIp");
			cmHostPort = notify.getInt("cmHostPort");
			wsHostIp = notify.getString("wsHostIp");
			wsHostPort = notify.getInt("wsHostPort");
			phoneNumber = notify.getString("phoneNumber");
			bizType = notify.getString("bizType");
		}
	}

	/**
	 * ClassName: RedisConfig<br>
	 * Description: 用于读取control配置文件中redis节点配置信息的类。<br>
	 * Author: Tiger.Wang<br>
	 * Date: 2019/9/16 16:52<br>
	 * Since: JDK 1.8
	 **/
	public static class RedisConfig {

		private static final String CONFNAME = "redis";

		public static final String redisIp;
		public static final Integer redisPort;
		public static final Integer timeout;

		static {
			if(!control.exist(CONFNAME)) {
				throw new AppSystemException("无法从control配置文件中加载属性" + CONFNAME);
			}
			YamlMap redis = control.getMap(CONFNAME);
			redisIp = redis.getString("redisIp");
			redisPort = redis.getInt("redisPort");
			timeout = redis.getInt("timeout");
		}
	}
}
