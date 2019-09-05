package hrds.agent.job.biz.dataclean.columnclean;

import fd.ng.core.utils.StringUtil;

import java.util.Map;

/**
 * ClassName: ColCVConverImpl <br/>
 * Function: 数据库直连采集列清洗码值转换实现类 <br/>
 * Reason: 继承AbstractColumnClean抽象类，只针对一个码值转换方法进行实现
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public class ColCVConverImpl extends AbstractColumnClean {
    @Override
    public String CVConver(Map<String, String> ruleMap, String columnValue){
        if(ruleMap != null && !ruleMap.isEmpty()){
            for(String key : ruleMap.keySet()){
                if(columnValue.equalsIgnoreCase(key)){
                    columnValue = StringUtil.replace(columnValue, key, ruleMap.get(key));
                }
            }
        }
        return columnValue;
    }
}
