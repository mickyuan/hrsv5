package hrds.agent.job.biz.dataclean.tableclean;

import fd.ng.core.utils.StringUtil;

import java.util.Map;

/**
 * ClassName: TbReplaceImpl <br/>
 * Function: 数据库直连采集表清洗字符替换实现类 <br/>
 * Reason: 继承AbstractTableClean抽象类，只针对一个字符替换方法进行实现
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public class TbReplaceImpl extends AbstractTableClean {

    @Override
    public String replace(Map<String, String> replaceMap, String columnValue){
        if (replaceMap != null && !(replaceMap.isEmpty())) {
            for(String OriField : replaceMap.keySet()) {
                String newField = replaceMap.get(OriField);
                columnValue = StringUtil.replace(columnValue, OriField, newField);
            }
        }
        return columnValue;
    }
}
