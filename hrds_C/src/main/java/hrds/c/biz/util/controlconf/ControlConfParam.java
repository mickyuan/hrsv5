package hrds.c.biz.util.controlconf;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.StringUtil;
import hrds.commons.utils.PropertyParaValue;

import java.util.HashMap;
import java.util.Map;

@DocClass(desc = "Redis配置文件参数Yaml数据", author = "dhw", createdate = "2020/3/20 16:05")
public class ControlConfParam {

	public static final String CONF_FILE_NAME = "control.conf";

	public static Map<String, Object> getControlConfParam() {
		Map<String, Object> redisMap = new HashMap<>();
		Map<String, Object> map = setRedisParam("", "");
		redisMap.put("redis", map);
		return redisMap;
	}

	public static Map<String, Object> setRedisParam(String redis_ip, String redis_port) {
		Map<String, Object> map = new HashMap<>();
		map.put("timeout", 100000);
		if (StringUtil.isBlank(redis_ip) || StringUtil.isBlank(redis_port)) {
			map.put("redisIp", PropertyParaValue.getString("redis_ip", "172.168.0.61"));
			String redis_port1 = PropertyParaValue.getString("redis_port", "56379");
			map.put("redisPort", Integer.parseInt(redis_port1));
		} else {
			map.put("redisIp", redis_ip);
			map.put("redisPort", Integer.parseInt(redis_port));
		}
		return map;
	}
}
