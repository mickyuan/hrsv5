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
	 * ClassName: HazelcastConfig<br>
	 * Description: 用于读取control配置文件中hazelcast配置信息的类。<br>
	 * Author: Tiger.Wang<br>
	 * Date: 2019/9/16 16:52<br>
	 * Since: JDK 1.8
	 **/
	public static class HazelcastConfig {

		private static final String CONFNAME = "hazelcast";

		public static final String localAddress;
		public static final Integer autoIncrementPort;
		public static final Integer portCount;

		static {
			if (!trigger.exist(CONFNAME)) {
				throw new AppSystemException("无法从trigger配置文件中加载属性" + CONFNAME);
			}
			YamlMap hazelcast = trigger.getMap(CONFNAME);
			localAddress = hazelcast.getString("localAddress");
			autoIncrementPort = hazelcast.getInt("autoIncrementPort");
			portCount = hazelcast.getInt("portCount");
		}
	}
}
