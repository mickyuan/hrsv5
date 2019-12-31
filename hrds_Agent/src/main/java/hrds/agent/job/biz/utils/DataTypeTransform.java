package hrds.agent.job.biz.utils;

import fd.ng.core.conf.ConfFileLoader;
import fd.ng.core.yaml.YamlArray;
import fd.ng.core.yaml.YamlFactory;
import fd.ng.core.yaml.YamlMap;
import org.eclipse.jetty.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataTypeTransform {

	private static final Map<String, YamlMap> map = new HashMap<>();
	/* 这里是为了让某些转换的类型直接返回，不要长度，比如说date类型，有些数据库取出来的date类型长度只有8，但是转为
	 * 字符串类型取出来是很长的一串字符串，varchar(8)不够用，所以要直接返回varchar(32),所以这里有一个list,list包含
	 * 的值直接返回
	 */
	private static final List<String> list = new ArrayList<>();
	private static final String LKH = "(";
	private static final String RKH = ")";

	static {
		YamlMap rootConfig = YamlFactory.load(ConfFileLoader.getConfFile("typecontrast")).asMap();
		YamlArray arrays = rootConfig.getArray("typecontrast");
		for (int i = 0; i < arrays.size(); i++) {
			YamlMap trans = arrays.getMap(i);
			map.put(trans.getString("NAME"), trans);
		}
		list.add("VARCHAR(32)");
	}

	/**
	 * 转换为对应的存储数据库支持类型
	 */
	public static String tansform(String type, String dtcs_name) {

		type = type.trim().toUpperCase();
		//要转换的值带括号，则取出括号里面的值并拼接没有值得类型
		if (type.contains(LKH) && type.contains(RKH)) {
			String key_1 = type.substring(0, type.indexOf(LKH));
			String key_2 = key_1 + LKH + RKH;
			//默认类型是规范的即一对括号
			String length = type.substring(type.indexOf(LKH) + 1, type.length() - 1);
			//String length = type.substring(type.indexOf(LKH));
			//获取要替换的值
			String val = map.get(dtcs_name).getString(key_2);
			if (null == val) {
				return type;
			} else {
				if (list.contains(val)) {
					return val;
				}
				//如果要替换的值有括号则在括号中拼接长度信息
				if (val.contains(LKH) && val.contains(RKH)) {
					return val.substring(0, val.indexOf(LKH) + 1) + length + RKH;
				} else {
					return val;
				}
			}
		} else {
			if (null == map.get(dtcs_name).getString(type)) {
				return type;
			} else {
				return map.get(dtcs_name).getString(type);
			}
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
	public static List<String> tansform(List<String> types, String dtcs_name) {

		List<String> transformedTypeList = new ArrayList<>();
		for (String string : types) {
			transformedTypeList.add(tansform(string, dtcs_name));
		}
		return transformedTypeList;
	}

}
