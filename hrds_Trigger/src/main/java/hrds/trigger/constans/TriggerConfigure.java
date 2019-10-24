package hrds.trigger.constans;

import fd.ng.core.conf.ConfFileLoader;
import fd.ng.core.yaml.YamlFactory;
import fd.ng.core.yaml.YamlMap;
import hrds.commons.exception.AppSystemException;

/**
 * ClassName: TriggerConfigure<br>
 * Description: 用于读取配置文件的类。<br>
 * Author: Tiger.Wang<br>
 * Date: 2019/9/16 16:52<br>
 * Since: JDK 1.8
 **/
public class TriggerConfigure {

	private static final YamlMap trigger;

	static {
		try {
			trigger = YamlFactory.load(ConfFileLoader.getConfFile("trigger")).asMap();
		} catch (YamlFactory.YamlFileNotFoundException var2) {
			throw new AppSystemException("无法加载trigger配置文件");
		}
	}

	/**
	 * ClassName: RedisConfig<br>
	 * Description: 用于读取trigger配置文件中redis节点配置信息的类。<br>
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
			if(!trigger.exist(CONFNAME)) {
				throw new AppSystemException("无法从trigger配置文件中加载属性" + CONFNAME);
			}
			YamlMap redis = trigger.getMap(CONFNAME);
			redisIp = redis.getString("redisIp");
			redisPort = redis.getInt("redisPort");
			timeout = redis.getInt("timeout");
		}
	}
}
