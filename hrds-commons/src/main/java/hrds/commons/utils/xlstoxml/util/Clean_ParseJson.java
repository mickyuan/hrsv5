package hrds.commons.utils.xlstoxml.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import fd.ng.core.utils.StringUtil;
import hrds.commons.entity.Column_split;

import java.util.*;

public class Clean_ParseJson {
	public static String STRSPLIT = "^";

	public static Map<String, Object> parseJson(JSONObject jsonsingle, JSONObject jsonmsg, String columns) throws Exception {

		Map<String, Object> all = new HashMap<String, Object>();
		Map<String, Map<String, String>> deleSpecialSpace = new HashMap<String, Map<String, String>>();
		Map<String, String> strFilling = new HashMap<String, String>();
		Map<String, String> strDateing = new HashMap<String, String>();
		Map<String, Map<String, Column_split>> splitIng = new HashMap<String, Map<String, Column_split>>();
		Map<String, String> mergeIng = new LinkedHashMap<String, String>();
		Map<String, String> codeIng = new HashMap<String, String>();
		Map<String, String> Triming = new HashMap<String, String>();
		Map<String, Map<Integer, String>> ordering = new HashMap<String, Map<Integer, String>>();

		/**
		 * 在这里获取转义的逻辑（按任务清洗、按表清洗、按字段清洗）最终组成map传入
		 * ******************start*************************************
		 * 对于数据清洗需要定义一个顺序，首先以对字段的清洗顺序优先级最高，其次是表的清洗 最后是整个任务
		 */
		String column_file_length = "0";
		JSONObject all_column_clean_result = jsonsingle.getJSONObject("all_column_clean_result");
		if (!all_column_clean_result.isEmpty()) {
			/**
			 * 按字段清洗*****************start
			 */
			List<String> split = StringUtil.split(columns.toLowerCase(), ",");
			for (int j = 0; j < split.size(); j++) {
				String columnname = split.get(j);
				JSONObject column_name = all_column_clean_result.getJSONObject(columnname);

				//获取所有字段的清洗顺序

				String order = column_name.getString("clean_order");//字段清洗顺序
				Map<Integer, String> changeKeyValue = changeKeyValue(order);


				JSONArray is_column_repeat_result = column_name.getJSONArray("is_column_repeat_result");//字段清洗规则
				//替换
				Map<String, String> colmap = new HashMap<String, String>();//没个列的清洗规则
				if (!is_column_repeat_result.isEmpty()) {
					for (int i = 0; i < is_column_repeat_result.size(); i++) {
						JSONObject object = JSONObject.parseObject(is_column_repeat_result.get(i).toString());
						String field = StringUtil.unicode2String(object.getString("field"));//获取原字段
						String replace_feild = StringUtil.unicode2String(object.getString("replace_feild"));//获取替换字段
						colmap.put(field, replace_feild);//进行替换
					}
				}
				deleSpecialSpace.put(columnname.toUpperCase(), colmap);

				//列补齐
				StringBuffer filling = new StringBuffer();
				JSONObject is_column_file_result = column_name.getJSONObject("is_column_file_result");//按字段补齐
				if (!is_column_file_result.isEmpty()) {
					column_file_length = is_column_file_result.getString("filling_length");//获取补齐长度
					String filling_type = is_column_file_result.getString("filling_type");//获取补齐类型
					String character_filling = StringUtil.unicode2String(is_column_file_result.getString("character_filling"));
					filling.append(column_file_length).append(STRSPLIT).append(filling_type).append(STRSPLIT).append(character_filling);
				}
				strFilling.put(columnname.toUpperCase(), filling.toString());
				//日期转换
				StringBuffer dateing = new StringBuffer();
				JSONObject is_column_time_result = column_name.getJSONObject("is_column_time_result");//按日期格式转换
				if (!is_column_time_result.isEmpty()) {
					String convert_format = (is_column_time_result.getString("convert_format"));
					String old_format = (is_column_time_result.getString("old_format"));
					dateing.append(convert_format).append(STRSPLIT).append(old_format);
				}
				strDateing.put(columnname.toUpperCase(), dateing.toString());
				//列拆分
				Map<String, Column_split> splitmap = new LinkedHashMap<String, Column_split>();//拆分出来的列和值

				JSONArray columnSplitResult = column_name.getJSONArray("columnSplitResult");//获取列合并规则
				if (!columnSplitResult.isEmpty()) {
					for (int i = 0; i < columnSplitResult.size(); i++) {
						//col_name,col_offset,split_sep,seq,split_type,col_type
						JSONObject object = JSONObject.parseObject(columnSplitResult.get(i).toString());
						String split_sep = StringUtil.unicode2String(object.getString("split_sep"));
						Column_split cp = new Column_split();
						cp.setCol_offset(object.getString("col_offset"));
						cp.setSplit_sep(split_sep);
						cp.setSeq(object.getString("seq"));
						cp.setSplit_type(object.getString("split_type"));
						cp.setCol_type(object.getString("col_type"));
						splitmap.put(object.getString("col_name"), cp);
					}
					splitIng.put(columnname.toUpperCase(), splitmap);
				}

				//码值转换

				JSONObject columnCodeResult = column_name.getJSONObject("columnCodeResult");//获取码值转换规则
				if (!columnCodeResult.isEmpty()) {
					codeIng.put(columnname.toUpperCase(), columnCodeResult.toString());
				}

				//列值trim
				JSONObject trimResult = column_name.getJSONObject("trimResult");//获取码值转换规则
				if (!trimResult.isEmpty()) {
					Triming.put(columnname.toUpperCase(), trimResult.toString());
				}
				if (!changeKeyValue.isEmpty()) {
					ordering.put(columnname.toUpperCase(), changeKeyValue);
				}
			}
			/**
			 * 按字段清洗*****************end
			 * 按表清洗*****************start
			 */
			JSONObject table_clean_result = jsonsingle.getJSONObject("table_clean_result");
			String order = table_clean_result.getString("clean_order");//字段清洗顺序
			Map<Integer, String> changeKeyValue = changeKeyValue(order);
			JSONArray is_table_repeat_result = table_clean_result.getJSONArray("is_table_repeat_result");//表清洗规则

			if (!is_table_repeat_result.isEmpty()) { //表替换条件是否为空，不为空进行替换
				// 遍历获取每一个替换信息
				Map<String, String> tablemap = new HashMap<String, String>();//没个列的清洗规则
				for (int i = 0; i < is_table_repeat_result.size(); i++) {
					JSONObject object = JSONObject.parseObject(is_table_repeat_result.get(i).toString());
					String field = StringUtil.unicode2String(object.getString("field"));//获取原字段
					String replace_feild = StringUtil.unicode2String(object.getString("replace_feild"));//获取替换字段
					tablemap.put(field, replace_feild);
				}
				deleSpecialSpace.put("tablemap_deleSpecialSpace", tablemap);
			}
			JSONObject is_table_fille_result = table_clean_result.getJSONObject("is_table_fille_result");//按表补齐
			StringBuffer filling = new StringBuffer();
			if (!is_table_fille_result.isEmpty()) {// 表补齐条件是否为空，不为空进行替换
				column_file_length = is_table_fille_result.getString("filling_length");//获取补齐长度
				String filling_type = is_table_fille_result.getString("filling_type");//获取补齐类型
				String character_filling = StringUtil.unicode2String(is_table_fille_result.getString("character_filling"));//获取补齐字符
				filling.append(column_file_length).append(STRSPLIT).append(filling_type).append(STRSPLIT).append(character_filling);
				strFilling.put("tablemap_strfilling", filling.toString());
			}

			//表的所有字段trim

			JSONObject isTableTrimResult = table_clean_result.getJSONObject("isTableTrimResult");//按表补齐
			if (!isTableTrimResult.isEmpty()) {// 表补齐条件是否为空，不为空进行替换
				Triming.put("tablemap_striming", isTableTrimResult.toString());
			}

			//列合并

			JSONArray columnMergeResult = table_clean_result.getJSONArray("columnMergeResult");//获取列合并规则
			if (!columnMergeResult.isEmpty()) {
				for (int i = 0; i < columnMergeResult.size(); i++) {
					JSONObject object = JSONObject.parseObject(columnMergeResult.get(i).toString());
					mergeIng.put(object.getString("col_name") + STRSPLIT + object.getString("col_type"), object.getString("old_name"));
				}
			}
			if (!changeKeyValue.isEmpty()) {
				ordering.put("tablemap", changeKeyValue);
			}
		}
		/**
		 * 按表清洗*****************end
		 * 按任务清洗***********start
		 */
		JSONObject all_clean_result = jsonmsg.getJSONObject("all_clean_result");
		JSONArray is_all_repeat_result = all_clean_result.getJSONArray("is_all_repeat_result");
		String order = all_clean_result.getString("clean_order");//字段清洗顺序
		Map<Integer, String> changeKeyValue = changeKeyValue(order);
		if (!is_all_repeat_result.isEmpty()) {
			Map<String, String> taskmap = new HashMap<String, String>();//没个列的清洗规则
			for (int i = 0; i < is_all_repeat_result.size(); i++) {
				JSONObject object = JSONObject.parseObject(is_all_repeat_result.get(i).toString());
				String field = StringUtil.unicode2String(object.getString("field"));
				String replace_feild = StringUtil.unicode2String(object.getString("replace_feild"));
				taskmap.put(field, replace_feild);
			}
			deleSpecialSpace.put("taskmap_deleSpecialSpace", taskmap);
		}

		JSONObject is_all_fille_result = all_clean_result.getJSONObject("is_all_fille_result");//按任务补齐
		StringBuffer filling = new StringBuffer();
		if (!is_all_fille_result.isEmpty()) {// 所有表补齐条件是否为空，不为空进行替换
			column_file_length = is_all_fille_result.getString("filling_length");//获取补齐长度
			String filling_type = is_all_fille_result.getString("filling_type");//获取补齐类型
			String character_filling = StringUtil.unicode2String(is_all_fille_result.getString("character_filling"));//获取补齐字符
			filling.append(column_file_length).append(STRSPLIT).append(filling_type).append(STRSPLIT).append(character_filling);
		}
		strFilling.put("taskmap_strfilling", filling.toString());

		//表的所有字段trim

		JSONObject isAllTrimResult = all_clean_result.getJSONObject("isAllTrimResult");//按表补齐
		if (!isAllTrimResult.isEmpty()) {// 表补齐条件是否为空，不为空进行替换
			Triming.put("taskmap_striming", isAllTrimResult.toString());
		}
		if (!changeKeyValue.isEmpty()) {
			ordering.put("taskmap", changeKeyValue);
		}
		/**
		 * 按任务清洗***********end
		 */

		all.put("deleSpecialSpace", deleSpecialSpace);
		all.put("strFilling", strFilling);
		all.put("dating", strDateing);
		all.put("splitIng", splitIng);
		all.put("mergeIng", mergeIng);
		all.put("codeIng", codeIng);
		all.put("Triming", Triming);
		all.put("ordering", ordering);
		return all;
	}

	private static Map<Integer, String> changeKeyValue(String order) {

		Map<Integer, String> map = new HashMap<>();
		if (!StringUtil.isEmpty(order)) {
			JSONObject jsonOrder = JSONObject.parseObject(order);
			Set<String> jsonSet = jsonOrder.keySet();
			for (String key : jsonSet) {
				map.put(jsonOrder.getIntValue(key), key);
			}
		}
		return map;

	}
}
