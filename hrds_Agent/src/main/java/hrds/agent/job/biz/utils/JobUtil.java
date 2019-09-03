package hrds.agent.job.biz.utils;


import hrds.agent.job.biz.bean.ColumnCleanResult;
import hrds.commons.exception.AppSystemException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JobUtil {

    /*
    * 根据JobInfo中的列清洗信息返回需要采集的列名
    * */
    public static Set<String> getCollectColumnName(List<ColumnCleanResult> list){
        if (list == null || list.isEmpty() ) {
            throw new AppSystemException("采集作业信息不能为空");
        }
        Set<String> columnNames = new HashSet<>();
        for(ColumnCleanResult column : list){
            String columnName = column.getColumnName();
            columnNames.add(columnName);
        }
        return columnNames;
    }
}
