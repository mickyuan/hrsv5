package hrds.k.biz.tdbresult.echarts.pie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 饼图Pie数据处理
 */
public class PieUtil {

	/**
	 * 提取图例数据和系列数据
	 *
	 * @param fieldSameStatisticalResult 检索条件,搜索条件格式为:key=value 例如:name=S10_I_USER或者class=5 不写key默认为表名称搜索
	 * @return 图例数据 List<String>
	 */
	public static Map<String, Object> extractLegendDataAndSeriesData(List<Map<String, Object>> fieldSameStatisticalResult) {
		//图例数据
		List<String> legendData = new ArrayList<>();
		//系列数据
		List<Map<String, Object>> seriesData = new ArrayList<>();
		fieldSameStatisticalResult.forEach(item -> {
			//提取图例数据
			String legendName = "";
			if (null != item.get("category_same")) {
				legendName = item.get("category_same").toString();
			}
			if (!legendData.contains(legendName)) {
				legendData.add(legendName);
			}
			//提取结果中的系列数据
			Map<String, Object> map = new HashMap<>();
			map.put("name", item.get("category_same").toString());
			map.put("value", item.get("count").toString());
			seriesData.add(map);
		});
		//设置返回数据
		Map<String, Object> fieldEqualityCategoryMap = new HashMap<>();
		fieldEqualityCategoryMap.put("legendData", legendData);
		fieldEqualityCategoryMap.put("seriesData", seriesData);
		return fieldEqualityCategoryMap;
	}
}
