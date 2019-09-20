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
public class CollectPage implements Callable<Map<String, Object>> {

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

	public CollectPage(JobInfo jobInfo, DBConfigBean dbInfo,
	                   DataBaseDialectStrategy strategy, String strSql,
	                   String pageColumn, int start, int end, int pageNum, int pageRow) {
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

	/**
	 * @Description: 多线程采集执行方法
	 * @return: java.util.Map<java.lang.String, java.lang.Object>
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/11
	 * 步骤：
	 * 1、执行查询，获取ResultSet
	 * 2、解析ResultSet，并写数据文件
	 * 3、数据落地文件后，线程执行完毕后的返回内容，用于写作业meta文件和验证本次采集任务的结果
	 */
	@Override
	public Map<String, Object> call() throws SQLException, IOException {
		//1、执行查询，获取ResultSet
		ResultSet pageData = this.getPageData(this.dbInfo, strategy, strSql, pageColumn, start, end);
		//2、解析ResultSet，并写数据文件
		ResultSetParser parser = new ResultSetParser();
		//文件路径
		String filePath = parser.parseResultSet(pageData, this.jobInfo, this.pageNum, this.pageRow);
		//用于统计当前线程采集到的数据量
		int columnCount = pageData.getMetaData().getColumnCount();
		//3、多线程采集阶段，线程执行完毕后的返回内容，用于写作业meta文件和验证本次采集任务的结果
		Map<String, Object> map = new HashMap<>();
		//采集生成的文件路径
		map.put("filePath", filePath);
		//当前线程采集到的ResultSet
		map.put("pageData", pageData);
		//当前线程采集到的数据量
		map.put("pageCount", columnCount);
		return map;
	}


	/**
	 * @Description: 根据分页SQL获取ResultSet
	 * @Param: dbInfo:数据库连接配置信息, 取值范围 : DBConfigBean类型对象
	 * @Param: strategy：数据库方言策略, 取值范围 : DataBaseDialectStrategy接口实例
	 * @Param: strSql：数据库采集SQL, 取值范围 : String
	 * @Param: pageColumn：用户提供的数据库表用于分页的列, 取值范围 : int
	 * @Param: start：当前分页开始条数, 取值范围 : int
	 * @Param: end：当前分页结束条数, 取值范围 : int
	 * @return: ResultSet
	 * @Author: WangZhengcheng
	 * @Date: 2019/8/13
	 * 步骤：
	 * 1、将DBConfigBean对象传入工具类ConnetionTool，得到DatabaseWrapper
	 * 2、将采集SQL，当前页的start，end转换通过strategy转为分页SQL
	 * 3、调用方法获得当前线程的分页数据
	 * 4、关闭资源
	 * 5、返回结果集
	 */
	private ResultSet getPageData(DBConfigBean dbInfo, DataBaseDialectStrategy strategy, String strSql,
	                              String pageColumn, int start, int end) {
		//TODO pageColumn是前台用户提供的用于分页的列，但是目前使用的fdcode中自带的对数据库的分页操作，所以pageColumn暂时用不到
		//1、将DBConfigBean对象传入工具类ConnetionTool，得到DatabaseWrapper
		DatabaseWrapper dbWrapper = ConnetionTool.getDBWrapper(dbInfo);
		//2、将采集SQL，当前页的start，end转换通过strategy转为分页SQL
		String pageSql = strategy.createPageSql(strSql, start, end);
		//3、调用方法获得当前线程的分页数据
		ResultSet pageData = dbWrapper.queryGetResultSet(pageSql);
		//4、关闭资源
		dbWrapper.close();
		//5、返回结果集
		return pageData;
	}
}
