package hrds.trigger.task.helper;

import com.hazelcast.config.Config;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;
import hrds.commons.exception.AppSystemException;
import hrds.trigger.constans.TriggerConfigure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ClassName: HazelcastHelper
 * Description: 用于操作Hazelcast的类
 * Author: zxz
 * Date: 2020/8/13 13:57
 * Since: JDK 1.8
 **/
public class HazelcastHelper {

	private static final Logger logger = LogManager.getLogger();

	private final HazelcastInstance hazelcastInstance;
	// 目的是做成单例
	private static final HazelcastHelper INSTANCE = new HazelcastHelper();

	/**
	 * HazelcastHelper类构造器。<br>
	 * 1.初始化类。
	 */
	private HazelcastHelper() {
		// 创建默认config对象
		Config config = new Config();
		//设置启动socket绑定的ip
		config.setProperty("hazelcast.local.localAddress", TriggerConfigure.HazelcastConfig.localAddress);
		config.setProperty("hazelcast.socket.bind.any", "false");
		// 获取network元素<network></network>
		NetworkConfig netConfig = config.getNetworkConfig();
		// 设置公开的ip地址
		netConfig.setPublicAddress(TriggerConfigure.HazelcastConfig.localAddress);
		//设置端口的起始地址
		netConfig.setPort(TriggerConfigure.HazelcastConfig.autoIncrementPort);
		//端口被占用会+1查看是否可用，如果还是不能用会继续向后探查直到autoIncrementPort+portCount
		netConfig.setPortAutoIncrement(true);
		netConfig.setPortCount(TriggerConfigure.HazelcastConfig.portCount);
		// 获取join元素<join></join>
		// 获取multicast元素设置不使用多播使用UDP协议
		netConfig.getJoin().getMulticastConfig().setEnabled(false);
		//配置TCP发现机制
		TcpIpConfig tcpIpConfig = netConfig.getJoin().getTcpIpConfig();
		tcpIpConfig.setEnabled(true);
		//TODO 这里后续会修改为control和trigger不在同一台机器，页面部署会指定多个ip地址，这个则会改为ip1,ip2,ip3...ipn
		tcpIpConfig.addMember(TriggerConfigure.HazelcastConfig.localAddress);
		this.hazelcastInstance = Hazelcast.newHazelcastInstance(config);
		logger.info("---------- Hazelcas构建成功 ----------");
	}

	/**
	 * 获得HazelcastHelper实例。<br>
	 * 1.返回HazelcastHelper实例。
	 */
	public static HazelcastHelper getInstance() {

		//1.返回HazelcastHelper实例。
		return INSTANCE;
	}

	/**
	 * 根据key清除hazelcast分布式缓存中的记录。
	 */
	public long deleteByKey(String... keys) {
		long deleteCount = 0;
		for (String key : keys) {
			IQueue<Object> queue = hazelcastInstance.getQueue(key);
			deleteCount += queue.size();
			queue.clear();
		}
		return deleteCount;
	}

	/**
	 * 根据key，将内容追加到hazelcast分布式缓存对应的队列后。
	 */
	public void rpush(String key, String content) {
		//1.根据key，将内容追加到hazelcast分布式缓存对应的队列后。
		try {
			hazelcastInstance.getQueue(key).put(content);
		} catch (Exception ex) {
			throw new AppSystemException("将数据追加到hazelcast缓存中" + key + "队列失败", ex);
		}
	}

	/**
	 * 根据键值获取hazelcast中，该键值对应的队列的大小。
	 */
	public long llen(String key) {
		//1.获取hazelcast缓存中对应队列的大小
		try {
			return hazelcastInstance.getQueue(key).size();
		} catch (Exception ex) {
			throw new AppSystemException("获取hazelcast缓存中" + key + "队列的大小失败", ex);
		}
	}

	/**
	 * 根据键值，将hazelcast中队列最靠前的数据弹出。
	 */
	public String lpop(String key) {
		//1.弹出hazelcast缓存中对应的key队列最靠前的数据。
		try {
			return (String) hazelcastInstance.getQueue(key).take();
		} catch (Exception ex) {
			throw new AppSystemException("获取hazelcast缓存中" + key + "队列中最靠前的数据失败", ex);
		}
	}

	/**
	 * 关闭hazelcastInstance分布式缓存。
	 */
	public void close() {
		//1.关闭hazelcastInstance分布式缓存。
		hazelcastInstance.shutdown();
		logger.info("---------- 关闭hazelcastInstance分布式缓存成功 ----------");
	}
}
