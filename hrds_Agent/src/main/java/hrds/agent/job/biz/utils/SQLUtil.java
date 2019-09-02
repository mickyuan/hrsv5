package hrds.agent.job.biz.utils;

import hrds.agent.job.biz.constant.DBTypeConstant;
import hrds.agent.job.biz.constant.JobConstant;

import java.util.Set;

/**
 * ClassName: SQLUtil <br/>
 * Function: 根据表名和列名获取采集SQL <br/>
 * Reason: 数据库直连采集
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public class SQLUtil {
    /**
    * @Description:  根据表名和列名获取采集SQL,根据不同数据库的类型对SQL语句中的列名进行处理
    * @Param:   tableName:表名
    * @Param:   columnName:要采集的列名
    * @Param:   dbType:数据库类型
    * @return:String
    * @Author: WangZhengcheng
    * @Date: 2019/8/13
    */
    public static String getCollectSQL(String tableName, Set<String> columnName, String dbType){
        String strSql = "";
        StringBuilder columnSB = new StringBuilder();
        for(String s : columnName) {
            if(DBTypeConstant.MYSQL.getCode() == Integer.parseInt(dbType)){
                columnSB.append(JobConstant.CLEAN_SEPARATOR).append(s).append(JobConstant.CLEAN_SEPARATOR).append(JobConstant.COLUMN_SEPARATOR);
            }else{
                columnSB.append(s).append(JobConstant.COLUMN_SEPARATOR);
            }
        }
        String column = columnSB.toString().substring(0,columnSB.toString().length() - 1);
        if(DBTypeConstant.MYSQL.getCode() == Integer.parseInt(dbType)){
            //如果数据库类型是MySQL,则对每一列加飘号
            strSql = "select " + column + " from " + JobConstant.CLEAN_SEPARATOR + tableName + JobConstant.CLEAN_SEPARATOR;
        }else if(DBTypeConstant.ORACLE9IFOLLOW.getCode() == Integer.parseInt(dbType) || DBTypeConstant.ORACLE10GABOV.getCode() == Integer.parseInt(dbType)){
            //如果数据库类型是Oracle，则进行如下处理
            strSql = "select /*+parallel(" +  tableName + ",4)*/ " + column + " from " + tableName;
        }else{
            //对其他数据库，不做特殊处理
            strSql = "select " + column + " from " + tableName;
        }
        return strSql;
    }
}
