package hrds.trigger.utils;

import org.apache.hadoop.yarn.client.api.YarnClient;

import hrds.trigger.hadoop.readConfig.ConfigReader;

/**
 * @ClassName: YarnM
 * @Description: 用于单例模式获取Yarn客户端实例的类。
 * @Author: Tiger.Wang
 * @Date: 2019/8/30 11:41
 **/
public enum YarnM {
	instance;

	/**
	 * 获取yarn操作实例。<br>
	 * 1.根据配置文件构造Yarn客户端对象。
	 * @author Tiger.Wang
	 * @date 2019/10/8
	 * @return org.apache.hadoop.yarn.client.api.YarnClient<br>
	 *          含义：Yarn客户端对象。<br>
	 *          取值范围：不会为null。
	 */
    public YarnClient getYarnClient(){

    	YarnClient client  = YarnClient.createYarnClient();  
        client.init(ConfigReader.getConfiguration());
        client.start();

		return client;
    }   
}
