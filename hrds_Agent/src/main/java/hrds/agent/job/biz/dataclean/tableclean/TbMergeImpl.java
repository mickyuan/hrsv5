package hrds.agent.job.biz.dataclean.tableclean;

import hrds.agent.job.biz.constant.FileFormatConstant;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.utils.ColumnTool;
import hrds.agent.job.biz.utils.ParquetUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.parquet.example.data.Group;

import java.util.List;
import java.util.Map;

/**
 * ClassName: TbMergeImpl <br/>
 * Function: 数据库直连采集表清洗列合并实现类 <br/>
 * Reason: 继承AbstractTableClean抽象类，只针对一个列合并方法进行实现
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public class TbMergeImpl extends AbstractTableClean {

    //TODO ORC,SEQUENCE未实现
    @Override
    public String merge(Map<String, String> mergeRule, String[] columnsValue, String[] columnsName, Group group, List<Object> lineData, String fileType){
        StringBuilder afterMergeColValue = new StringBuilder(4096);
        for(String colNameAndType : mergeRule.keySet()){
            int[] alliIdex = ColumnTool.findColIndex(columnsName, mergeRule.get(colNameAndType));
            for(int i = 0; i < alliIdex.length; i++){
                afterMergeColValue.append(columnsValue[alliIdex[i]]);
            }
            if(group != null){
                String[] colNameAndTypeArr = StringUtils.splitByWholeSeparatorPreserveAllTokens(colNameAndType, JobConstant.CLEAN_SEPARATOR);
                ParquetUtil.addData2Group(group, colNameAndTypeArr[0], colNameAndTypeArr[1], afterMergeColValue.toString());
                afterMergeColValue.delete(0, afterMergeColValue.length());
            }
            if(lineData != null){
                //未实现
            }
            if(fileType.equals(FileFormatConstant.CSV.getMessage())){
                afterMergeColValue.append(JobConstant.COLUMN_NAME_SEPARATOR);
            }else if(fileType.equals(FileFormatConstant.SEQUENCEFILE.getMessage())){
                afterMergeColValue.append(JobConstant.COLUMN_NAME_SEPARATOR);
            }
        }
        return afterMergeColValue.toString();
    }
}
