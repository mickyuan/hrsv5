package hrds.agent.job.biz.dataclean.tableclean;

import fd.ng.core.utils.StringUtil;
import hrds.agent.job.biz.constant.CompleteTypeConstant;
import hrds.agent.job.biz.constant.JobConstant;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * ClassName: TbCompleteImpl <br/>
 * Function: 数据库直连采集表清洗字符补齐实现类 <br/>
 * Reason: 继承AbstractTableClean抽象类，只针对一个字符补齐方法进行实现
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public class TbCompleteImpl extends AbstractTableClean {

    @Override
    public String complete(StringBuilder completeSB, String columnValue){
        if(completeSB != null){
            List<String> strings = StringUtil.split(completeSB.toString(), JobConstant.CLEAN_SEPARATOR);
            int completeLength = Integer.parseInt(strings.get(0));
            String completeType = strings.get(1);
            String completeCharacter = strings.get(2);
            if(CompleteTypeConstant.BEFORE.getCode() == Integer.parseInt(completeType) ) {
                // 前补齐
                columnValue = StringUtils.leftPad(columnValue, completeLength, completeCharacter);
            } else if(CompleteTypeConstant.AFTER.getCode() == Integer.parseInt(completeType) ) {
                // 后补齐
                columnValue = StringUtils.rightPad(columnValue, completeLength, completeCharacter);
            }
        }
        return columnValue;
    }
}
