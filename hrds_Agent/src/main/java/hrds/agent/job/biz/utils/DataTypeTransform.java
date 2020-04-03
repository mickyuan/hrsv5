package hrds.agent.job.biz.utils;

import fd.ng.core.conf.ConfFileLoader;
import fd.ng.core.utils.StringUtil;
import fd.ng.core.yaml.YamlArray;
import fd.ng.core.yaml.YamlFactory;
import fd.ng.core.yaml.YamlMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataTypeTransform {

	private static final Map<String, YamlMap> map = new HashMap<>();
	/* 这里是为了让某些转换的类型直接返回，不要根据原来的长度拼接新的长度，比如说NVARCHAR(16)类型，从数据库取出来的
	 * 类型长度只有16，但是转为普通数据库的varchar(16)长度不够用，所以要直接返回varchar(32),所以这里有一个list,
	 * list包含的值直接返回
	 */
	private static final List<String> list = new ArrayList<>();
	private static final String LKH = "(";
	private static final String RKH = ")";

	static {
		YamlMap rootConfig = YamlFactory.load(ConfFileLoader.getConfFile("contrast")).asMap();
		YamlArray arrays = rootConfig.getArray("typecontrast");
		for (int i = 0; i < arrays.size(); i++) {
			YamlMap trans = arrays.getMap(i);
			map.put(trans.getString("NAME"), trans);
		}
		list.add("VARCHAR(32)");
		list.add("VARCHAR(64)");
		list.add("VARCHAR(128)");
		list.add("VARCHAR(256)");
		list.add("VARCHAR(512)");
		list.add("VARCHAR(1024)");
		list.add("VARCHAR(2048)");
		list.add("VARCHAR(4000)");
	}

	/**
	 * 转换为对应的存储数据库支持类型
	 */
	public static String tansform(String type, String dsl_name) {
		type = type.trim().toUpperCase();
		//要转换的值带括号，则取出括号里面的值并拼接没有值得类型
		if (type.contains(LKH) && type.contains(RKH)) {
			String key_1 = type.substring(0, type.indexOf(LKH));
			String key_2 = key_1 + LKH + RKH;
			//默认类型是规范的即一对括号
			String length = type.substring(type.indexOf(LKH) + 1, type.length() - 1);
			//获取要替换的值
			String val = map.get(dsl_name).getString(key_2, key_2);
			if (list.contains(val)) {
				return val;
			}
			//如果要替换的值有括号则在括号中拼接长度信息
			if (val.contains(LKH) && val.contains(RKH)) {
				return val.substring(0, val.indexOf(LKH) + 1) + length + RKH;
			} else {
				return val;
			}
		} else {
			return map.get(dsl_name).getString(type, type);
		}
	}

	public static String combineform(String type, String length) {

		if (StringUtil.isEmpty(length) || "null".equals(length)) {
			length = String.valueOf(100);
		}
		switch (type.toLowerCase()) {
			case "char": {
				type = type + "(" + length + ")";
			}
			;
			break;
			case "varchar": {
				type = type + "(" + length + ")";
			}
			;
			break;
			case "decimal": {
				type = type + "(" + length + ")";
			}
			;
			break;
		}
		return type;
	}

	/**
	 * 源数据库的类型转换成对应的存储目的地支持的类型
	 */
	public static List<String> tansform(List<String> types, String dsl_name) {
		List<String> transformedTypeList = new ArrayList<>();
		for (String string : types) {
			transformedTypeList.add(tansform(string, dsl_name));
		}
		return transformedTypeList;
	}

}
