package hrds.c.biz.util.redisconf;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.StringUtil;
import hrds.commons.utils.PropertyParaValue;

import java.util.HashMap;
import java.util.Map;

@DocClass(desc = "Redis参数Yaml数据", author = "dhw", createdate = "2020/3/20 16:05")
public class RedisParam {

	public static final String CONF_FILE_NAME = "redis.conf";

	public static Map<String, Object> getRedisParam() {
		Map<String, Object> redisMap = new HashMap<>();
		Map<String, Object> map = setRedisParam("", "");
		redisMap.put("redis", map);
		return redisMap;
	}

	public static Map<String, Object> setRedisParam(String redis_ip, String redis_port) {
		Map<String, Object> map = new HashMap<>();
		map.put("timeout", 100);
		if (StringUtil.isBlank(redis_ip) || StringUtil.isBlank(redis_port)) {
			map.put("ip", PropertyParaValue.getString("redis_ip", "10.71.4.52"));
			map.put("port", PropertyParaValue.getString("redis_port", "56379"));
		} else {
			map.put("ip", redis_ip);
			map.put("port", redis_port);
		}
		return map;
	}
}
