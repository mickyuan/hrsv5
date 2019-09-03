package hrds.agent.trans.biz;

import fd.ng.db.conf.ConnWay;
import fd.ng.db.conf.DbinfosConf;
import fd.ng.db.conf.Dbtype;
import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.agent.job.biz.bean.DBConfigBean;
import hrds.agent.job.biz.constant.DBTypeConstant;
import hrds.commons.exception.AppSystemException;

/**
 * ClassName: ConnetionTool <br/>
 * Function: 数据库直连采集获取数据库连接 <br/>
 * Reason: 这个类还可以用于测试数据库连接
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public class ConnetionTool {

    /** 
    * @Description: 根据数据库配置信息获取数据库连接
    * @Param:  dbInfo：数据库连接配置信息
    * @return:  DatabaseWrapper
    * @Author: WangZhengcheng 
    * @Date: 2019/8/13 
    */ 
    public static DatabaseWrapper getDBWrapper(DBConfigBean dbInfo){
        DbinfosConf.Dbinfo dbinfo = new DbinfosConf.Dbinfo();
        dbinfo.setName(DbinfosConf.DEFAULT_DBNAME);
        dbinfo.setDriver(dbInfo.getJdbc_url());
        dbinfo.setUsername(dbInfo.getUser_name());
        dbinfo.setPassword(dbInfo.getDatabase_pad());
        dbinfo.setWay(ConnWay.JDBC);
        String dbType = dbInfo.getDatabase_type();
        if(DBTypeConstant.MYSQL.getCode() == Integer.parseInt(dbType)){
            dbinfo.setDbtype(Dbtype.MYSQL);
        }else if (DBTypeConstant.ORACLE9IFOLLOW.getCode() == Integer.parseInt(dbType)){
            throw new AppSystemException("系统不支持Oracle9i及以下");
        }else if(DBTypeConstant.ORACLE10GABOV.getCode() == Integer.parseInt(dbType)){
            dbinfo.setDbtype(Dbtype.ORACLE);
        }else if(DBTypeConstant.SQLSERVRER2000.getCode() == Integer.parseInt(dbType)){
            dbinfo.setDbtype(Dbtype.SQLSERVER);
        }else if(DBTypeConstant.SQLSERVRER2005.getCode() == Integer.parseInt(dbType)){
            dbinfo.setDbtype(Dbtype.SQLSERVER);
        }else if(DBTypeConstant.DB2.getCode() == Integer.parseInt(dbType)){
            //TODO 使用db2v1还是db2v2
        }else if(DBTypeConstant.SYBASE.getCode() == Integer.parseInt(dbType)){
            dbinfo.setDbtype(Dbtype.SYBASE);
        }else if(DBTypeConstant.INFORMATIC.getCode() == Integer.parseInt(dbType)){
            //Informatic
        }else if(DBTypeConstant.H2.getCode() == Integer.parseInt(dbType)){
            //H2
        }else if(DBTypeConstant.APACHEDERBY.getCode() == Integer.parseInt(dbType)){
            //ApacheDerBy
        }else if(DBTypeConstant.POSTGRESQL.getCode() == Integer.parseInt(dbType)){
            dbinfo.setDbtype(Dbtype.POSTGRESQL);
        }else if(DBTypeConstant.GBASE.getCode() == Integer.parseInt(dbType)){
            dbinfo.setDbtype(Dbtype.GBASE);
        }else if(DBTypeConstant.TERADATA.getCode() == Integer.parseInt(dbType)){
            dbinfo.setDbtype(Dbtype.TERADATA);
        }else{
            throw new AppSystemException("系统不支持该数据库类型");
        }
        dbinfo.setShow_conn_time(true);
        dbinfo.setShow_sql(true);
        return new DatabaseWrapper.Builder().dbconf(dbinfo).create();
    }
}
