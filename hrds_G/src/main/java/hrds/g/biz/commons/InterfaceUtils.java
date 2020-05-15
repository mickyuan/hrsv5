package hrds.g.biz.commons;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import hrds.commons.utils.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "接口公用方法", author = "dhw", createdate = "2020/5/15 10:18")
public class InterfaceUtils {

	@Method(desc = "封装list数据", logicStep = "1.判断集合是否为空，为空返回null" +
			"2.处理数据为list<map>格式数据")
	@Param(name = "columnList", desc = "查询字段信息集合", range = "无限制")
	@Return(desc = "返回数据为list<map>格式数据", range = "无限制")
	public static List<Map<String, String>> getMaps(List<String> columnList) {
		// 1.判断集合是否为空，为空返回null
		if (columnList.isEmpty()) {
			return null;
		}
		// 2.处理数据为list<map>格式数据
		List<Map<String, String>> list = new ArrayList<>();
		columnList = StringUtil.split(columnList.get(0), Constant.METAINFOSPLIT);
		for (String table_column_name : columnList) {
			Map<String, String> columnMap = new HashMap<>();
			columnMap.put("table_column_name", table_column_name);
			list.add(columnMap);
		}
		return list;
	}
}
