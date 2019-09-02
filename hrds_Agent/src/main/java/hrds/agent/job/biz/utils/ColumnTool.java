package hrds.agent.job.biz.utils;

import fd.ng.core.utils.StringUtil;
import hrds.agent.job.biz.bean.ColumnSplitBean;
import hrds.agent.job.biz.constant.JobConstant;

import java.util.List;
import java.util.Map;

/**
 * @description: 用于对需要进行列合并(表清洗)、列拆分(列清洗)清洗的列，更新列信息的工具类
 * @author: WangZhengcheng
 * @create: 2019-08-28 15:37
 **/
public class ColumnTool {

    /** 
    * @Description: 列拆分
    * @Param: [columns : 所有列列名,使用\001分隔]
    * @Param: [columnsTypeAndPreci : 所有列类型(长度，精度)，使用 | 分隔]
    * @Param: [columnsLength : 所有列长度，使用 | 分隔]
    * @Param: [allSplitRule : 所有列的拆分规则]
    * @return: void
    * @Author: WangZhengcheng 
    * @Date: 2019/8/28
    *
    */ 
    public static void updateColumnSplit(StringBuilder columns, StringBuilder columnsTypeAndPreci, StringBuilder columnsLength, Map<String, List<ColumnSplitBean>> allSplitRule){
        if (columns == null || columns.length() == 0) {
            throw new RuntimeException("进行列拆分清洗时，列名不能为空");
        }
        if (columnsTypeAndPreci == null || columnsTypeAndPreci.length() == 0) {
            throw new RuntimeException("进行列拆分清洗时，列类型不能为空");
        }
        if (columnsLength == null || columnsLength.length() == 0) {
            throw new RuntimeException("进行列拆分清洗时，列长度不能为空");
        }
        String columnsName = columns.toString();

        for(String colName: allSplitRule.keySet()){
            //用于追加或替换的列名(JobConstant.COLUMN_NAME_SEPARATOR分隔)
            StringBuilder appendColName = new StringBuilder();
            //用于追加或替换的列类型(JobConstant.COLUMN_TYPE_SEPARATOR分隔)
            StringBuilder appendColType = new StringBuilder();
            //用于追加或替换的列长度(JobConstant.COLUMN_TYPE_SEPARATOR分隔)
            StringBuilder appendColLen = new StringBuilder();
            List<ColumnSplitBean> splitListByColName = allSplitRule.get(colName);
            //在列拆分的过程中，保留原有字段
            appendColName.append(colName).append(JobConstant.COLUMN_NAME_SEPARATOR);
            //找到该列在原columns中的位置
            int findColIndex = findColIndex(columnsName, colName, JobConstant.COLUMN_NAME_SEPARATOR);
            //将该字段拆分后的内容追加到三个变量上
            for(int i = 0; i <splitListByColName.size(); i++ ){
                ColumnSplitBean splitBean = splitListByColName.get(i);
                appendColName.append(splitBean.getColName()).append(JobConstant.COLUMN_NAME_SEPARATOR);
                appendColType.append(splitBean.getColType()).append(JobConstant.COLUMN_TYPE_SEPARATOR);
                appendColLen.append(getLength(splitBean.getColType())).append(JobConstant.COLUMN_TYPE_SEPARATOR);
            }
            //去掉最后一位分隔符
            appendColName = appendColName.deleteCharAt(appendColName.length() - 1);
            appendColType = appendColType.deleteCharAt(appendColType.length() - 1);
            appendColLen = appendColLen.deleteCharAt(appendColLen.length() - 1);
            //获取对应列类型的位置，插入拆分后的列类型
            int typeIndex = searchIndex(columnsTypeAndPreci.toString(), findColIndex, JobConstant.COLUMN_TYPE_SEPARATOR);
            //如果找到下标，说明不是最后一个，要插入新加的类型
            if( typeIndex != -1 ) {
                columnsTypeAndPreci.insert(typeIndex, JobConstant.COLUMN_TYPE_SEPARATOR + appendColType.toString());
            }else {
                //如果没有找到，说明是整个字符串的最后一个，直接进行追加
                columnsTypeAndPreci.append(JobConstant.COLUMN_TYPE_SEPARATOR).append(appendColType.toString());
            }
            //获取对应列长度的位置插入拆分后的列长度
            int lenIndex = searchIndex(columnsLength.toString(), findColIndex, JobConstant.COLUMN_TYPE_SEPARATOR);
            if( lenIndex != -1 ) {
                columnsLength.insert(lenIndex, JobConstant.COLUMN_TYPE_SEPARATOR + appendColLen.toString());
            }else{
                columnsLength.append(JobConstant.COLUMN_TYPE_SEPARATOR).append(appendColLen.toString());
            }
            columnsName = StringUtil.replace(columnsName.toUpperCase(), colName.toUpperCase(), appendColName.toString().toUpperCase());
        }
        columns.delete(0, columns.length()).append(columnsName);
    }

    /** 
    * @Description: 列合并
    * @Param: [columns : 所有列列名,使用\001分隔]
    * @Param: [columnsTypeAndPreci : 所有列类型(长度，精度)，使用 | 分隔]
    * @Param: [columnsLength : 所有列长度，使用 | 分隔]
    * @Param: [tbMergeRule : 表清洗列的合并规则]
    * @return: void
    * @Author: WangZhengcheng 
    * @Date: 2019/8/30 
    */ 
    public static void updateColumnMerge(StringBuilder columns, StringBuilder columnsTypeAndPreci, StringBuilder columnsLength, Map<String, String> tbMergeRule){
        if (columns == null || columns.length() == 0) {
            throw new RuntimeException("进行列拆分清洗时，列名不能为空");
        }
        if (columnsTypeAndPreci == null || columnsTypeAndPreci.length() == 0) {
            throw new RuntimeException("进行列拆分清洗时，列类型不能为空");
        }
        if (columnsLength == null || columnsLength.length() == 0) {
            throw new RuntimeException("进行列拆分清洗时，列长度不能为空");
        }
        for(String key : tbMergeRule.keySet()) {
            //获取表名和类型
            List<String> split = StringUtil.split(key, JobConstant.CLEAN_SEPARATOR);
            columns.append(JobConstant.COLUMN_NAME_SEPARATOR).append(split.get(0));
            columnsTypeAndPreci.append(JobConstant.COLUMN_TYPE_SEPARATOR).append(split.get(1));
            columnsLength.append(JobConstant.COLUMN_TYPE_SEPARATOR).append(getLength(split.get(1)));
        }
    }

    /**
     * @Description:  找到将要被合并的列在所有表的所有列中的下标
     * @Param: [column : 该张表所有的列的列名]
     * @Param: [str : 将要被合并的列名，列与列之间用逗号分隔]
     * @return: int[]
     * @Author: WangZhengcheng
     * @Date: 2019/8/30
     */
    public static int[] findColIndex(String[] column, String str) {
        List<String> split = StringUtil.split(str, ",");
        int[] index = new int[split.size()];
        for(int i = 0; i < split.size(); i++) {
            for(int j = 0; j < column.length; j++) {
                if( split.get(i).equalsIgnoreCase(column[j]) ) {
                    index[i] = j;
                }
            }
        }
        return index;
    }

    /**
    * @Description: 在原列名信息中，按照某分隔符，找到某列所在位置并返回
    * @Param: [columnsName : 原列名]
    * @Param: [colName : 要寻找的列名]
    * @Param: [separator : 分隔符]
    * @return: int
    * @Author: WangZhengcheng
    * @Date: 2019/8/29
    */
    private static int findColIndex(String columnsName, String colName, String separator){
        List<String> column = StringUtil.split(columnsName, separator);
        int index = 0;
        for(int j = 0; j < column.size(); j++) {
            if( column.get(j).equalsIgnoreCase(colName) ) {
                index = j + 1;
                break;
            }
        }
        return index;
    }

    /**
    * @Description: 获取每个字典的长度
    * @Param: [columnType : 列类型]
    * @return: int
    * @Author: WangZhengcheng
    * @Date: 2019/8/28
    */
    private static int getLength(String columnType) {
        columnType = columnType.trim();
        int length = 0;
        if(columnType.equalsIgnoreCase("INTEGER")){
            length = 12;
        }else if(columnType.equalsIgnoreCase("BIGINT")){
            length = 22;
        }else if(columnType.equalsIgnoreCase("SMALLINT")){
            length = 8;
        }else if(columnType.equalsIgnoreCase("DOUBLE")){
            length = 24;
        }else if(columnType.equalsIgnoreCase("REAL")){
            length = 16;
        }else if(columnType.equalsIgnoreCase("TIMESTAMP")){
            length = 14;
        }else if(columnType.equalsIgnoreCase("DATE")){
            length = 8;
        }else if(columnType.equalsIgnoreCase("LONGVARCHAR")){
            length = 4000;
        }else if(columnType.equalsIgnoreCase("CLOB")){
            length = 4000;
        }else if(columnType.equalsIgnoreCase("BLOB")){
            length = 4000;
        }else if(columnType.equalsIgnoreCase("DECFLOAT")){
            length = 34;
        }else if(columnType.equalsIgnoreCase("DECFLOAT")){
            length = 34;
        }else {
            int start = columnType.indexOf("(");
            int end = columnType.indexOf(")");
            String substring = columnType.substring(start + 1, end);
            if( substring.indexOf(",") != -1 ) {
                List<String> split = StringUtil.split(substring, ",");
                return Integer.parseInt(split.get(0)) + 2;
            }
            return Integer.parseInt(substring);
        }
        return length;
    }

    /** 
    * @Description: 用于获取对应列类型的位置
    * @Param: [str : 列类型]
    * @Param: [index : 该列在原字符串中出现的位置]
    * @Param: [separator : 分隔符]
    * @return: int
    * @Author: WangZhengcheng 
    * @Date: 2019/8/29 
    */ 
    private static int searchIndex(String str, int index, String separator) {
        int temp = 0;//第一个出现的索引位置
        int num = 0;
        while( temp != -1 ) {
            num++;
            temp = str.indexOf(separator, temp + 1);//从这个索引往后开始第一个出现的位置
            if( num == index ) {
                break;
            }
        }
        return temp;
    }
}
