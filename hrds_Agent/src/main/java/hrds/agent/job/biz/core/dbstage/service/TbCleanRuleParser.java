package hrds.agent.job.biz.core.dbstage.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hrds.agent.job.biz.bean.TableCleanResult;
import hrds.agent.job.biz.bean.TableTrimResult;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @description: 表清洗规则解析，对实体中的JSON信息解析，在程序内部参数传递过程中，不允许使用JSON格式数据
 * @author: WangZhengcheng
 * @create: 2019-08-29 15:37
 **/
public class TbCleanRuleParser {

    public static Map<String, Object> parseTbCleanRule(TableCleanResult tbCleanResult){
        if(tbCleanResult == null){
            throw new RuntimeException("解析表清洗规则时，清洗规则对象不能为空");
        }

        //用于存放所有类型的列清洗规则，并最终返回
        Map<String, Object> result = new HashMap<>();
        //用于存放表清洗优先级
        Map<Integer, String> tbOrderMap = new HashMap<>();
        //用于存放表清洗字符替换规则
        Map<String, String> tbReplaceMap = new HashMap<String, String>();
        //用于存放表清洗列合并规则
        Map<String, String> tbMergeMap = new LinkedHashMap<String, String>();

        //解析表清洗优先级
        String tbCleanOrder = tbCleanResult.getClean_order();
        if(!StringUtils.isBlank(tbCleanOrder)){
            JSONObject orderObj = JSONObject.parseObject(tbCleanOrder);
            Set<String> keySet = orderObj.keySet();
            for(String key : keySet) {
                tbOrderMap.put(orderObj.getIntValue(key), key);
            }
            result.put("cleanOrder", tbOrderMap);
        }else{
            throw new RuntimeException("解析表清洗规则时，清洗优先级不能为空");
        }

        //解析表所有字段替换规则
        JSONArray tbReplaceArr = JSON.parseArray(tbCleanResult.getIs_table_repeat_result());
        if(tbReplaceArr != null && !tbReplaceArr.isEmpty()){
            for(int i = 0; i < tbReplaceArr.size(); i++){
                JSONObject singleObj = JSONObject.parseObject(tbReplaceArr.get(i).toString());
                //获取原字段
                String oriField = StringUtil.unicode2String(singleObj.getString("field"));
                //获取替换字段
                String newFeild = StringUtil.unicode2String(singleObj.getString("replace_feild"));
                tbReplaceMap.put(oriField, newFeild);
            }
            result.put("replace", tbReplaceMap);
        }

        //解析表所有字段补齐规则
        JSONObject tbCompObj = JSONObject.parseObject(tbCleanResult.getIs_table_fille_result());
        StringBuilder tbCompRule = new StringBuilder();
        if(tbCompObj != null && !tbCompObj.isEmpty()){
            //获取补齐长度
            String compLength = tbCompObj.getString("filling_length");
            //获取补齐类型(前补齐/后补齐)
            String compType = tbCompObj.getString("filling_type");
            //获取补齐字符
            String comChar = StringUtil.unicode2String(tbCompObj.getString("character_filling"));
            tbCompRule.append(compLength).append(JobConstant.CLEAN_SEPARATOR).append(compType).append(JobConstant.CLEAN_SEPARATOR).append(comChar);
            result.put("complete", tbCompRule);
        }

        //解析表所有字段是否进行首尾去空
        TableTrimResult tableTrimResult = tbCleanResult.getTableTrimResult();
        if(tableTrimResult != null){
            result.put("trim", true);
        }

        //解析列合并规则
        JSONArray mergeArr = JSON.parseArray(tbCleanResult.getColumnMergeResult());
        if(mergeArr != null && !mergeArr.isEmpty()){
            for(int i = 0; i < mergeArr.size(); i++){
                JSONObject singleObj = JSONObject.parseObject(mergeArr.get(i).toString());
                StringBuilder colNameAndType = new StringBuilder();
                colNameAndType.append(singleObj.getString("col_name")).append(JobConstant.CLEAN_SEPARATOR).append(singleObj.getString("col_type"));
                tbMergeMap.put(colNameAndType.toString(), singleObj.getString("old_name"));
            }
            result.put("merge", tbMergeMap);
        }

        return result;
    }

}
