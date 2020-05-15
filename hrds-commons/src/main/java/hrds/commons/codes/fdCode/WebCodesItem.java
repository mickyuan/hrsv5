package hrds.commons.codes.fdCode;

import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.db.resultset.Result;
import hrds.commons.exception.AppSystemException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @program: hrsv5
 * @description:
 * @author: xchao
 * @create: 2020-05-15 12:40
 */
public class WebCodesItem {
	@fd.ng.core.annotation.Method(desc = "根据代码项分组编号和代码value获取code",
			logicStep = "1、根据map获取代码项；2、根据value获取code")
	@Param(name = "category", desc = "代码项分组编号", range = "代码项分组编号")
	@Param(name = "value", desc = "代码项中文名", range = "代码项中文名")
	@Return(desc = "返回代码项code", range = "返回代码项code")
	public static String getCode(String category, String value) {
		Class aClass = hrds.commons.codes.CodesItem.mapCat.get(category);
		if (aClass == null) return "";
		Enum[] enumsCode = new Enum[]{};
		Method values1 = null;
		try {
			values1 = aClass.getMethod("values", new Class[]{});
			Object aNull = values1.invoke("null");
			enumsCode = (Enum[]) aNull;
			for (Enum aClass1 : enumsCode) {
				Method getValue = aClass1.getClass().getMethod("getValue", new Class[]{});
				Object valueName = getValue.invoke(aClass1);
				if(valueName.equals(value)){
					Method getCode = aClass1.getClass().getMethod("getCode", new Class[]{});
					Object code = getCode.invoke(aClass1);
					return String.valueOf(code);
				}
			}
		} catch (Exception e) {
			throw new AppSystemException("根据分类编号" + category + "和代码项值" + value + "没有找到对应的代码项", e);
		}
		return "";
	}

	@fd.ng.core.annotation.Method(desc = "根据代码项分组编号和代码项值，获取中文名称",
			logicStep = "1、根据map获取代码项；2、反射调用ofValueByCode")
	@Param(name = "category", desc = "代码项分组编号", range = "代码项分组编号")
	@Param(name = "code", desc = "代码项编号", range = "代码项编号")
	@Return(desc = "返回代码项中文含义", range = "代码项中文含义")
	public static String getValue(String category, String code) {
		Class aClass = hrds.commons.codes.CodesItem.mapCat.get(category);
		if (aClass == null) return "";
		Object invoke = null;
		try {
			Method ofValueByCode = aClass.getMethod("ofValueByCode", String.class);
			invoke = ofValueByCode.invoke(null, code);
		} catch (Exception e) {
			throw new AppSystemException("根据分类编号" + category + "和代码项" + code + "没有找到对应的代码项", e);
		}
		return (invoke == null) ? "" : invoke.toString();
	}

	@fd.ng.core.annotation.Method(desc = "根据代码项分组编号，获取该代码项所有的信息",
			logicStep = "1、根据map获取代码项；2、反射调用调用枚举的4个方法，4、将数据放到result中返回")
	@Param(name = "category", desc = "代码项分组编号", range = "代码项分组编号")
	@Return(desc = "返回该代码项的所有信息", range = "返回该代码项的所有信息")
	public static Result getCategoryItems(String category) {
		Class aClass = hrds.commons.codes.CodesItem.mapCat.get(category);
		Enum[] aa = new Enum[]{};
		Result rs = new Result();
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		try {
			Method values1 = aClass.getMethod("values", new Class[]{});
			Object aNull = values1.invoke("null");
			aa = (Enum[]) aNull;
			for (Enum aClass1 : aa) {
				Map<String, Object> map = new HashMap<String, Object>();
				Method getCode = aClass1.getClass().getMethod("getCode", new Class[]{});
				Object code = getCode.invoke(aClass1);
				map.put("code", code);
				Method getValue = aClass1.getClass().getMethod("getValue", new Class[]{});
				Object value = getValue.invoke(aClass1);
				map.put("value", value);
				Method getCatCode = aClass1.getClass().getMethod("getCatCode", new Class[]{});
				Object catCode = getCatCode.invoke(aClass1);
				map.put("catCode", catCode);
				Method getCatValue = aClass1.getClass().getMethod("getCatValue", new Class[]{});
				Object catValue = getCatValue.invoke(aClass1);
				map.put("catValue", catValue);
				results.add(map);
			}
			rs.add(results);
		} catch (Exception e) {
			throw new AppSystemException("根据" + category + "没有找到对应的代码项", e);
		}
		return rs;
	}

	@fd.ng.core.annotation.Method(desc = "根据代码项分组编号，获取代码项code值，key为枚举类名",
			logicStep = "1、根据map获取代码项；2、反射调用调用枚举的4个方法，4、将数据放到result中返回")
	@Param(name = "category", desc = "代码项分组编号", range = "代码项分组编号")
	@Return(desc = "返回该代码项的所有信息", range = "返回该代码项的所有信息")
	public static Map<String,String> getCodeItems(String category) {
		Class aClass = hrds.commons.codes.CodesItem.mapCat.get(category);
		Enum[] aa = new Enum[]{};
		Map<String, String> map = new HashMap<String, String>();
		try {
			Method values1 = aClass.getMethod("values", new Class[]{});
			Object aNull = values1.invoke("null");
			aa = (Enum[]) aNull;
			for (Enum aClass1 : aa) {
				Method getCode = aClass1.getClass().getMethod("getCode", new Class[]{});
				String code = getCode.invoke(aClass1).toString();
				map.put(aClass1.name(),code);
			}
		} catch (Exception e) {
			throw new AppSystemException("根据" + category + "没有找到对应的代码项", e);
		}
		return map;
	}

	@fd.ng.core.annotation.Method(desc = "获取代码项信息，提供前端一一对应进行查看",
			logicStep = "1、根据map获取代码项；2、反射调用调用枚举的4个方法，4、将数据放到result中返回")
	@Return(desc = "返回系统中所有代码项信息", range = "返回系统中所有代码项信息")
	public static Map<String, Object> getAllCodeItems() {
		Map<String, Object> map = new HashMap<String, Object>();
		Set<Map.Entry<String, Class>> entries = hrds.commons.codes.CodesItem.mapCat.entrySet();
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		for (Map.Entry<String, Class> entry : entries) {
			String key = entry.getKey();
			Result rs = getCategoryItems(key);
			List<Map<String, Object>> maps = rs.toList();
			map.put(key, maps);
		}
		return map;
	}
}
