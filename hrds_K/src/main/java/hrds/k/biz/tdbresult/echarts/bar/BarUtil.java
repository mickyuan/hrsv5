package hrds.k.biz.tdbresult.echarts.bar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 柱状图数据格式处理
 */
public class BarUtil {

	/**
	 * 提取统计图表数据
	 *
	 * @param columnFeatureAnalysisResult 字段特征分析结果
	 * @return 统计图表数据  Map<String,Object>
	 */
	public static Map<String, Object> extractStatisticsChartData(List<Map<String, Object>> columnFeatureAnalysisResult) {
		//字段名列表
		List<Object> column_name_s = new ArrayList<>();
		//总记录数
		List<Object> col_record_s = new ArrayList<>();
		//去重记录数
		List<Object> col_distinct_s = new ArrayList<>();
		//最大长度
		List<Object> max_len_s = new ArrayList<>();
		//最小长度
		List<Object> min_len_s = new ArrayList<>();
		//平均长度
		List<Object> avg_len_s = new ArrayList<>();
		//平均长度偏度
		List<Object> skew_len_s = new ArrayList<>();
		//平均长度峰度
		List<Object> kurt_len_s = new ArrayList<>();
		//平均长度中位数
		List<Object> median_len_s = new ArrayList<>();
		//平均长度方差
		List<Object> var_len_s = new ArrayList<>();
		//是否包含中文
		List<Object> has_chinese_s = new ArrayList<>();
		//技术分类
		List<Object> tech_cate_s = new ArrayList<>();
		//提取数据
		columnFeatureAnalysisResult.forEach(columnFeatureInfo -> {
			column_name_s.add(columnFeatureInfo.get("col_code"));
			col_record_s.add(columnFeatureInfo.get("col_records"));
			col_distinct_s.add(columnFeatureInfo.get("col_distinct"));
			max_len_s.add(columnFeatureInfo.get("max_len"));
			min_len_s.add(columnFeatureInfo.get("min_len"));
			avg_len_s.add(columnFeatureInfo.get("avg_len"));
			skew_len_s.add(columnFeatureInfo.get("skew_len"));
			kurt_len_s.add(columnFeatureInfo.get("kurt_len"));
			median_len_s.add(columnFeatureInfo.get("median_len"));
			var_len_s.add(columnFeatureInfo.get("var_len"));
			has_chinese_s.add(columnFeatureInfo.get("has_chinese"));
			tech_cate_s.add(columnFeatureInfo.get("tech_cate"));
		});
		//封装提取的数据
		Map<String, Object> columnFeatureAnalysisResultMap = new HashMap<>();
		columnFeatureAnalysisResultMap.put("column_name_s", column_name_s);
		columnFeatureAnalysisResultMap.put("col_record_s", col_record_s);
		columnFeatureAnalysisResultMap.put("col_distinct_s", col_distinct_s);
		columnFeatureAnalysisResultMap.put("max_len_s", max_len_s);
		columnFeatureAnalysisResultMap.put("min_len_s", min_len_s);
		columnFeatureAnalysisResultMap.put("avg_len_s", avg_len_s);
		columnFeatureAnalysisResultMap.put("skew_len_s", skew_len_s);
		columnFeatureAnalysisResultMap.put("kurt_len_s", kurt_len_s);
		columnFeatureAnalysisResultMap.put("median_len_s", median_len_s);
		columnFeatureAnalysisResultMap.put("var_len_s", var_len_s);
		columnFeatureAnalysisResultMap.put("has_chinese_s", has_chinese_s);
		columnFeatureAnalysisResultMap.put("tech_cate_s", tech_cate_s);
		return columnFeatureAnalysisResultMap;
	}
}
