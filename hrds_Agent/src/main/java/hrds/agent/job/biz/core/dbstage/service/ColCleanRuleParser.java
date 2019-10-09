package hrds.agent.job.biz.core.dbstage.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hrds.agent.job.biz.bean.ColumnCleanResult;
import hrds.agent.job.biz.bean.ColumnSplitBean;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.utils.StringOperator;
import hrds.commons.exception.AppSystemException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 列清洗规则解析类，对实体中的JSON信息解析，在程序内部参数传递过程中，不允许使用JSON格式数据
 * @author: WangZhengcheng
 * @create: 2019-08-29 15:30
 **/
public class ColCleanRuleParser {

	/**
	 * 解析列清洗规则
	 *
	 * 1、解析清洗顺序
	 * 2、解析字符替换规则
	 * 3、解析字符补齐规则
	 * 4、解析日期转换规则
	 * 5、码值转换
	 * 6、首尾去空
	 * 7、解析字段拆分规则
	 * 8、将上述所有组装成Map<String, Object> 集合并返回，
	 *
	 * @Param: rule ColumnCleanResult
	 *         含义：存有列清洗规则的实体类对象
	 *         取值范围：不为空，ColumnCleanResult类型对象
	 *
	 * @return: Map<String, Object>
	 *          含义：存放有解析后的列清洗规则的集合
	 *          取值范围：不会为null，key是清洗项名称，value是清洗规则
	 *
	 * */
	public static Map<String, Object> parseColCleanRule(ColumnCleanResult rule)
			throws UnsupportedEncodingException {
		if (rule == null) {
			throw new AppSystemException("列清洗规则不能为空");
		}
		//用于存放所有类型的列清洗规则，并最终返回
		Map<String, Object> result = new HashMap<>();
		//存放清洗顺序
		Map<Integer, String> orderMap = new HashMap<>();
		//存放字符替换规则
		Map<String, String> replaceMap = new HashMap<>();
		//用于存放码值转换规则
		Map<String, String> convertMap = new HashMap<>();

		///1、解析清洗顺序
		String cleanOrder = rule.getClean_order();
		if (cleanOrder != null && !cleanOrder.isEmpty()) {
			//JSON串的形式为{\"complement\":1,\"replacement\":2,\"formatting\":3,\"conversion\":4,\"consolidation\":5,\"split\":6,\"trim\":7}
			JSONObject obj = JSONObject.parseObject(cleanOrder);
			for (String key : obj.keySet()) {
				orderMap.put(obj.getIntValue(key), key);
				result.put("clean_order", orderMap);
			}
		} else {
			throw new AppSystemException("清洗优先级不能为空");
		}

		//2、解析字符替换规则
		JSONArray replaceRule = JSON.parseArray(rule.getIs_column_repeat_result());
		if (replaceRule != null && !replaceRule.isEmpty()) {
			for (int j = 0; j < replaceRule.size(); j++) {
				JSONObject obj = JSONObject.parseObject(replaceRule.get(j).toString());
				String OriField = URLDecoder.decode(obj.getString("field"), "UTF-8");
				String newField = URLDecoder.decode(obj.getString("replace_feild"), "UTF-8");
				replaceMap.put(OriField, newField);
			}
			result.put("replace", replaceMap);
		}

		//3、解析字符补齐规则
		StringBuilder completeSB = new StringBuilder();
		JSONObject completeObject = JSONObject.parseObject(rule.getIs_column_file_result());
		if (completeObject != null && !completeObject.isEmpty()) {
			//获取补齐长度
			String completeLength = completeObject.getString("filling_length");
			//获取补齐类型
			String completeType = completeObject.getString("filling_type");
			//获得要填补的字符串
			String completeCharacter = URLDecoder.decode(completeObject.
					getString("character_filling"), "UTF-8");
			//结果追加到StringBuilder中，并且用^分隔
			completeSB.append(completeLength).append(JobConstant.CLEAN_SEPARATOR).
					append(completeType).append(JobConstant.CLEAN_SEPARATOR).append(completeCharacter);
			result.put("complete", completeSB);
		}

		//4、解析日期转换规则
		StringBuilder dateSB = new StringBuilder();
		JSONObject dateObject = JSONObject.parseObject(rule.getIs_column_time_result());
		if (dateObject != null && !dateObject.isEmpty()) {
			String oldFormat = dateObject.getString("old_format");
			String newFormat = dateObject.getString("convert_format");
			//结果追加到StringBuilder中，并且用^分隔
			dateSB.append(newFormat).append(JobConstant.CLEAN_SEPARATOR).append(oldFormat);
			result.put("dateConver", dateSB);
		}

		//5、码值转换
		JSONObject CVConverObject = JSONObject.parseObject(rule.getColumnCodeResult());
		if (CVConverObject != null && !CVConverObject.isEmpty()) {
			for (String key : CVConverObject.keySet()) {
				convertMap.put(key, CVConverObject.getString(key));
			}
			result.put("CVConver", convertMap);
		}

		//6、首尾去空
		JSONObject trimObject = JSONObject.parseObject(rule.getTrimResult());
		if (trimObject != null && !trimObject.isEmpty()) {
			result.put("trim", true);
		}

		//7、解析字段拆分规则
		JSONArray splitArray = JSON.parseArray(rule.getColumnSplitResult());
		//用于存放拆分出来的列和值
		List<ColumnSplitBean> splitList = new ArrayList<>();
		if (splitArray != null && !splitArray.isEmpty()) {
			for (int i = 0; i < splitArray.size(); i++) {
				ColumnSplitBean bean = JSONObject.parseObject(splitArray.get(i).toString(),
						ColumnSplitBean.class);
				bean.setSplitSep(StringOperator.unicode2String(bean.getSplitSep()));
				splitList.add(bean);
			}
			result.put("split", splitList);
		}

		return result;
	}

}
