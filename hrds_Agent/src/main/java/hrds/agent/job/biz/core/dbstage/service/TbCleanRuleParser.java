package hrds.agent.job.biz.core.dbstage.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.Class;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import hrds.agent.job.biz.bean.TableCleanResult;
import hrds.agent.job.biz.bean.TableTrimResult;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.utils.StringOperator;
import hrds.commons.exception.AppSystemException;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Class(desc = "表清洗规则解析，对实体中的JSON信息解析，在程序内部参数传递过程中，不允许使用JSON格式数据",
		author = "WangZhengcheng")
public class TbCleanRuleParser {

	@Method(desc = "解析表清洗规则", logicStep = "" +
			"1、解析表清洗优先级" +
			"2、解析表所有字段替换规则" +
			"3、解析表所有字段补齐规则" +
			"4、解析表所有字段是否进行首尾去空" +
			"5、解析列合并规则" +
			"6、将上述所有组装成Map<String, Object>返回，key是清洗项名称，value是清洗规则")
	@Param(name = "tbCleanRule", desc = "存有表清洗规则的实体类对象", range = "不为空，ColumnCleanResult类型对象")
	@Return(desc = "存放有解析后的表清洗规则的集合", range = "不会为null，key是清洗项名称，value是清洗规则")
	public static Map<String, Object> parseTbCleanRule(TableCleanResult tbCleanRule) {
		if (tbCleanRule == null) {
			throw new AppSystemException("解析表清洗规则时，清洗规则对象不能为空");
		}

		//用于存放所有类型的列清洗规则，并最终返回
		Map<String, Object> result = new HashMap<>();
		//用于存放表清洗优先级
		Map<Integer, String> tbOrderMap = new HashMap<>();
		//用于存放表清洗字符替换规则
		Map<String, String> tbReplaceMap = new HashMap<>();
		//用于存放表清洗列合并规则
		Map<String, String> tbMergeMap = new LinkedHashMap<>();

		//解析表清洗优先级
		String tbCleanOrder = tbCleanRule.getClean_order();
		if (!StringUtils.isBlank(tbCleanOrder)) {
			JSONObject orderObj = JSONObject.parseObject(tbCleanOrder);
			Set<String> keySet = orderObj.keySet();
			for (String key : keySet) {
				tbOrderMap.put(orderObj.getIntValue(key), key);
			}
			result.put("cleanOrder", tbOrderMap);
		} else {
			throw new AppSystemException("解析表清洗规则时，清洗优先级不能为空");
		}

		//解析表所有字段替换规则
		JSONArray tbReplaceArr = JSON.parseArray(tbCleanRule.getIs_table_repeat_result());
		if (tbReplaceArr != null && !tbReplaceArr.isEmpty()) {
			Iterator<Object> iterator = tbReplaceArr.iterator();
			if(iterator.hasNext()){
				JSONObject singleObj = JSONObject.parseObject(iterator.next().toString());
				//获取原字段
				String oriField = StringOperator.unicode2String(singleObj.getString("field"));
				//获取替换字段
				String newField = StringOperator.unicode2String(singleObj.getString("replace_feild"));
				tbReplaceMap.put(oriField, newField);
			}
			result.put("replace", tbReplaceMap);
		}

		//解析表所有字段补齐规则
		JSONObject tbCompObj = JSONObject.parseObject(tbCleanRule.getIs_table_fille_result());
		StringBuilder tbCompRule = new StringBuilder();
		if (tbCompObj != null && !tbCompObj.isEmpty()) {
			//获取补齐长度
			String compLength = tbCompObj.getString("filling_length");
			//获取补齐类型(前补齐/后补齐)
			String compType = tbCompObj.getString("filling_type");
			//获取补齐字符
			String comChar = StringOperator.unicode2String(tbCompObj.getString("character_filling"));
			tbCompRule.append(compLength).append(JobConstant.CLEAN_SEPARATOR).append(compType).
					append(JobConstant.CLEAN_SEPARATOR).append(comChar);
			result.put("complete", tbCompRule);
		}

		//解析表所有字段是否进行首尾去空
		TableTrimResult tableTrimResult = tbCleanRule.getTableTrimResult();
		if (tableTrimResult != null) {
			result.put("trim", true);
		}

		//解析列合并规则
		JSONArray mergeArr = JSON.parseArray(tbCleanRule.getColumnMergeResult());
		if (mergeArr != null && !mergeArr.isEmpty()) {
			Iterator<Object> iterator = mergeArr.iterator();
			if(iterator.hasNext()){
				JSONObject singleObj = JSONObject.parseObject(iterator.next().toString());
				String colNameAndType = singleObj.getString("col_name") + JobConstant.CLEAN_SEPARATOR +
						singleObj.getString("col_type");
				tbMergeMap.put(colNameAndType, singleObj.getString("old_name"));
			}
			result.put("merge", tbMergeMap);
		}

		return result;
	}

}
