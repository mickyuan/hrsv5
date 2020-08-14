package hrds.c.biz.util.conf;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;

import java.util.HashMap;
import java.util.Map;

@DocClass(desc = "control/trigger/appinfo配置文件参数Yaml数据", author = "dhw", createdate = "2020/3/20 16:05")
public class ETLConfParam {

	public static final String CONTROL_CONF_NAME = "control.conf";
	public static final String Trigger_CONF_NAME = "trigger.conf";
	public static final String CONTROL_APPINFO = "control_appinfo.conf";
	public static final String TRIGGER_APPINFO = "trigger_appinfo.conf";

	@Method(desc = "获取Control appinfo配置信息", logicStep = "1.配置appinfo信息" +
			"2.返回appinfo配置信息")
	@Return(desc = "返回Control appinfo配置信息集合", range = "无限制")
	public static Map<String, Object> getControlAppInfoConfParam() {
		Map<String, Object> appInfoMap = new HashMap<>();
		// 1.配置appinfo信息
		appInfoMap.put("basePackage", "hrds");
		appInfoMap.put("projectId", "Cont");
		// 2.返回appinfo配置信息
		return appInfoMap;
	}

	@Method(desc = "获取Trigger appinfo配置信息", logicStep = "1.配置appinfo信息" +
			"2.返回Trigger appinfo配置信息")
	@Return(desc = "返回Trigger appinfo配置信息集合", range = "无限制")
	public static Map<String, Object> getTriggerAppInfoConfParam() {
		Map<String, Object> appInfoMap = new HashMap<>();
		// 1.配置appinfo信息
		appInfoMap.put("basePackage", "hrds");
		appInfoMap.put("projectId", "Trig");
		// 2.返回appinfo配置信息
		return appInfoMap;
	}

	@Method(desc = "获取control配置信息", logicStep = "1.配置消息推送notify信息" +
			"2.设置redis配置信息" +
			"3.返回control配置信息")
	@Return(desc = "返回control配置信息集合", range = "无限制")
	public static Map<String, Object> getControlConfParam(String etl_serv_ip) {
		Map<String, Object> controlMap = new HashMap<>();
		// 1.配置消息推送notify信息
		Map<String, Object> notifyParam = setNotifyParam();
		controlMap.put("notify", notifyParam);
		// 2.设置hazelcast配置信息
		controlMap.put("hazelcast", setHazelcastParam(etl_serv_ip));
//		// 2.设置redis配置信息
//		Map<String, Object> redisParam = setRedisParam("", "");
//		controlMap.put("redis", redisParam);
//		// 3.返回control配置信息
		return controlMap;
	}

	@Method(desc = "获取trigger配置信息", logicStep = "1.设置redis配置信息" +
			"2.返回trigger配置信息")
	@Return(desc = "返回trigger配置信息集合", range = "无限制")
	public static Map<String, Object> getTriggerConfParam(String etl_serv_ip) {
		Map<String, Object> triggerMap = new HashMap<>();
		// 1.设置hazelcast配置信息
		triggerMap.put("hazelcast", setHazelcastParam(etl_serv_ip));
//		// 1.设置redis配置信息
//		Map<String, Object> redisParam = setRedisParam("", "");
//		triggerMap.put("redis", redisParam);
//		// 2.返回trigger配置信息
		return triggerMap;
	}

	public static Map<String, Object> setHazelcastParam(String etl_serv_ip) {
		Map<String, Object> map = new HashMap<>();
		map.put("localAddress", etl_serv_ip);
		map.put("autoIncrementPort", 5701);
		map.put("portCount", 100);
		return map;
	}

//	public static Map<String, Object> setRedisParam(String redis_ip, String redis_port) {
//		Map<String, Object> map = new HashMap<>();
//		map.put("timeout", 100000);
//		if (StringUtil.isBlank(redis_ip) || StringUtil.isBlank(redis_port)) {
//			map.put("redisIp", PropertyParaValue.getString("redis_ip", "172.168.0.61"));
//			String redis_port1 = PropertyParaValue.getString("redis_port", "56379");
//			map.put("redisPort", Integer.parseInt(redis_port1));
//		} else {
//			map.put("redisIp", redis_ip);
//			map.put("redisPort", Integer.parseInt(redis_port));
//		}
//		return map;
//	}

	public static Map<String, Object> setNotifyParam() {
		// fixme 目前写死，后面应该是读配置
		Map<String, Object> map = new HashMap<>();
		map.put("isNeedSendSMS", true);
		map.put("smsAccountName", 1);
		map.put("smsAccountPasswd", 1);
		map.put("cmHostIp", 1);
		map.put("cmHostPort", 1);
		map.put("wsHostIp", 1);
		map.put("wsHostPort", 1);
		map.put("phoneNumber", 1);
		map.put("bizType", 1);

		return map;
	}
}
