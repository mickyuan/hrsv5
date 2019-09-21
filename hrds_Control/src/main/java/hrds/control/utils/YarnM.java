package hrds.control.utils;

import hrds.control.hadoop.readConfig.ConfigReader;
import org.apache.hadoop.yarn.client.api.YarnClient;

public enum  YarnM {
	instance;  
    public YarnClient getYarnClient(){
    	YarnClient client  = YarnClient.createYarnClient();  
        client.init(ConfigReader.getConfiguration());
        client.start();
		return client;
    }   
}
