package hrds.commons.utils;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.conf.ConfFileLoader;
import fd.ng.core.yaml.YamlFactory;
import fd.ng.core.yaml.YamlMap;

@DocClass(desc = "agent获取系统参数配置类", author = "zxz", createdate = "2019/11/8 14:30")
public class PropertyParaUtil {

	private static YamlMap paramMap;

	//读取配置文件sysparam.conf
	static {
		YamlMap rootConfig = YamlFactory.load(ConfFileLoader.getConfFile("sysparam")).asMap();
		paramMap = rootConfig.getArray("param").getMap(0);
	}

	@Method(desc = "根据key获取value,取不到则给默认值",
			logicStep = "1.判断配置文件中的paramMap是否为空" +
					"2.存在key则返回value" +
					"3.不存在key,则返回默认值")
	@Param(name = "key", desc = "key值", range = "不可为空")
	@Param(name = "defaultValue", desc = "默认值", range = "可以为空")
	@Return(desc = "key对应的值", range = "不会为空")
	public static String getString(String key, String defaultValue) {
		//1.判断配置文件中的paramMap是否为空
		if (paramMap != null) {
			//2.存在key则返回value
			if (paramMap.exist(key)) {
				return paramMap.getString(key);
			}
		}
		//3.不存在key,则返回默认值
		return defaultValue;
	}
}
