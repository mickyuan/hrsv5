package hrds.agent.job.biz.core.dbstage.service;

import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.agent.job.biz.bean.DBConfigBean;
import hrds.agent.job.biz.bean.JobInfo;
import hrds.agent.job.biz.core.dbstage.dbdialect.strategy.DataBaseDialectStrategy;
import hrds.agent.trans.biz.ConnetionTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * ClassName: CollectPage <br/>
 * Function: 多线程采集线程类， <br/>
 * Reason: 子线程向主线程返回的有生成的文件路径，当前线程采集到的ResultSet，当前线程采集到的数据量
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public class CollectPage implements Callable<Map<String,Object>>{

    private final static Logger LOGGER = LoggerFactory.getLogger(CollectPage.class);
    private DBConfigBean dbInfo;
    private DataBaseDialectStrategy strategy;
    private String strSql;
    private String pageColumn;
    private int start;
    private int end;
    private int pageNum;
    private int pageRow;
    private JobInfo jobInfo;

    public CollectPage(JobInfo jobInfo, DBConfigBean dbInfo, DataBaseDialectStrategy strategy, String strSql, String pageColumn, int start, int end, int pageNum, int pageRow) {
        this.jobInfo = jobInfo;
        this.dbInfo = dbInfo;
        this.strategy = strategy;
        this.strSql = strSql;
        this.pageColumn = pageColumn;
        this.start = start;
        this.end = end;
        this.pageNum = pageNum;
        this.pageRow = pageRow;
    }

    /*
    * 1、执行查询，获取ResultSet
    * 2、解析ResultSet，并写数据文件
    * 3、数据落地文件后，线程执行完毕后的返回内容，用于写作业meta文件和验证本次采集任务的结果
    * */
    @Override
    public Map<String,Object> call() throws SQLException, IOException{
        //1、执行查询，获取ResultSet
        ResultSet pageData = this.getPageData(this.dbInfo, strategy, strSql, pageColumn, start, end);
        //2、解析ResultSet，并写数据文件
        ResultSetParser parser = new ResultSetParser();
        //文件路径
        String filePath = "";
        //用于统计当前线程采集到的数据量
        int columnCount = 0;
        filePath = parser.parseResultSet(pageData, this.jobInfo, this.pageNum, this.pageRow);
        columnCount = pageData.getMetaData().getColumnCount();
        //3、多线程采集阶段，线程执行完毕后的返回内容，用于写作业meta文件和验证本次采集任务的结果
        Map<String,Object> map = new HashMap();
        //采集生成的文件路径
        map.put("filePath", filePath);
        //当前线程采集到的ResultSet
        map.put("pageData", pageData);
        //当前线程采集到的数据量
        map.put("pageCount", columnCount);
        return map;
    }

    /*
     * 根据分页SQL获取ResultSet
     * */
    /**
    * @Description:  根据分页SQL获取ResultSet
    * @Param:  dbInfo:数据库连接配置信息
    * @Param:  strategy：数据库方言策略
    * @Param:  strSql：数据库采集SQL
    * @Param:  pageColumn：用户提供的数据库表用于分页的列
    * @Param:  start：当前分页开始条数
    * @Param:  end：当前分页结束条数
    * @return:ResultSet
    * @Author: WangZhengcheng
    * @Date: 2019/8/13
    */
    private ResultSet getPageData(DBConfigBean dbInfo, DataBaseDialectStrategy strategy, String strSql, String pageColumn, int start, int end){
        //TODO pageColumn是前台用户提供的用于分页的列，但是目前使用的fdcode中自带的对数据库的分页操作，所以pageColumn暂时用不到
        DatabaseWrapper dbWrapper = ConnetionTool.getDBWrapper(dbInfo);
        String pageSql = strategy.createPageSql(strSql, start, end);
        ResultSet pageData = dbWrapper.queryGetResultSet(pageSql);
        dbWrapper.close();
        return pageData;
    }
}
