package hrds.agent.job.biz.core.dbstage.dbdialect.strategy;

import hrds.agent.job.biz.constant.DBTypeConstant;
import hrds.commons.exception.AppSystemException;

/**
 * ClassName: DialectStrategyFactory <br/>
 * Function: 数据库方言策略工厂 <br/>
 * Reason: 根据数据库类型获取数据库方言策略(简单工厂模式)
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public class DialectStrategyFactory {

    private static boolean flag;

    private DialectStrategyFactory(){
        if(!flag){
            flag = true;
        }else{
            throw new AppSystemException("不能多次创建单例对象");
        }
    }

    /* 此处使用一个内部类来维护单例 */
    private static class Inner {
        private static DialectStrategyFactory instance = new DialectStrategyFactory();
    }

    /* 获取实例 */
    public static DialectStrategyFactory getInstance() {
        return Inner.instance;
    }

    /** 
    * @Description: 根据数据库类型获取对应的数据库分页策略
    * @Param:  dbType：数据库类型
    * @return:  DataBaseDialectStrategy
    * @Author: WangZhengcheng 
    * @Date: 2019/8/13 
    */ 
    public static DataBaseDialectStrategy createDialectStrategy(String dbType){
        if(dbType == null || dbType.isEmpty()){
            throw new AppSystemException("数据库类型不能为空");
        }
        if (Integer.parseInt(dbType) == DBTypeConstant.MYSQL.getCode()) {
            return new MySQLDialectStrategy();
        }else if (Integer.parseInt(dbType) == DBTypeConstant.ORACLE9IFOLLOW.getCode()) {
            //TODO 目前fdcode只有oracle9iAbove
            throw new AppSystemException("系统不支持Oracle9i及以下");
        }else if (Integer.parseInt(dbType) == DBTypeConstant.ORACLE10GABOV.getCode()) {
            return new OracleDialectStrategy();
        }else if (Integer.parseInt(dbType) == DBTypeConstant.SQLSERVRER2000.getCode()) {
            //TODO 返回SQLSERVER2000的策略
        }else if (Integer.parseInt(dbType) == DBTypeConstant.SQLSERVRER2005.getCode()) {
            //TODO 返回SQLSERVER2005的策略
        }else if (Integer.parseInt(dbType) == DBTypeConstant.DB2.getCode()) {
            //TODO 返回DB2的策略，但是目前DB2的策略有两个，分别是dbv1,dbv2，需要决定
        }else if (Integer.parseInt(dbType) == DBTypeConstant.SYBASE.getCode()) {
            //TODO 返回Sybase的策略
        }else if (Integer.parseInt(dbType) == DBTypeConstant.INFORMATIC.getCode()) {
            //TODO 返回Informatic的策略
        }else if (Integer.parseInt(dbType) == DBTypeConstant.H2.getCode()) {
            //TODO 返回H2的策略
        }else if (Integer.parseInt(dbType) == DBTypeConstant.APACHEDERBY.getCode()) {
            //TODO 返回ApacheDervy的策略
        }else if (Integer.parseInt(dbType) == DBTypeConstant.POSTGRESQL.getCode()) {
            return new PgSQLDialectStrategy();
        }else if (Integer.parseInt(dbType) == DBTypeConstant.GBASE.getCode()) {
            //TODO 返回GBase策略
        }else if (Integer.parseInt(dbType) == DBTypeConstant.TERADATA.getCode()) {
            //TODO 返回TeraData策略
        }else{
            throw new AppSystemException("系统不支持的数据库类型");
        }
        return null;
    }
}
