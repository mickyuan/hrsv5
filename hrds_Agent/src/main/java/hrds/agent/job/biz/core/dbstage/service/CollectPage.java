package hrds.agent.job.biz.core.dbstage.service;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.agent.job.biz.bean.DBConfigBean;
import hrds.agent.job.biz.bean.JobInfo;
import hrds.agent.job.biz.core.dbstage.dbdialect.strategy.DataBaseDialectStrategy;
import hrds.agent.trans.biz.ConnetionTool;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

@DocClass(desc = "多线程采集线程类，子线程向主线程返回的有生成的文件路径，当前线程采集到的ResultSet，" +
		"当前线程采集到的数据量", author = "WangZhengcheng")
public class CollectPage implements Callable<Map<String, Object>> {

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

	@Method(desc = "多线程采集执行方法", logicStep = "" +
			"1、执行查询，获取ResultSet" +
			"2、解析ResultSet，并写数据文件" +
			"3、数据落地文件后，线程执行完毕后的返回内容，用于写作业meta文件和验证本次采集任务的结果")
	@Return(desc = "当前线程完成任务(查询数据，落地数据文件)后的结果", range = "三对Entry，key分别为：" +
			"1、filePath，代表生成的数据文件路径" +
			"2、pageData，代表当前线程采集到的ResultSet" +
			"3、pageCount，代表当前线程采集到的数据量")
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


	@Method(desc = "根据分页SQL获取ResultSet", logicStep = "" +
			"1、将DBConfigBean对象传入工具类ConnetionTool，得到DatabaseWrapper" +
			"2、将采集SQL，当前页的start，end转换通过strategy转为分页SQL" +
			"3、调用方法获得当前线程的分页数据并返回")
	@Param(name = "dbInfo", desc = "数据库连接配置信息", range = "不为空，DBConfigBean类型对象")
	@Param(name = "strategy", desc = "数据库方言策略实例", range = "不为空，DataBaseDialectStrategy接口实例")
	@Param(name = "strSql", desc = "数据库直连采集作业SQL语句", range = "不为空")
	@Param(name = "pageColumn", desc = "海云应用管理端传过来的，画面上由用户提供的用于分页的列名", range = "不为空")
	@Param(name = "start", desc = "当前分页开始条数", range = "不限")
	@Param(name = "end", desc = "当前分页结束条数", range = "不限")
	@Return(desc = "当前线程执行分页SQL查询得到的结果集", range = "不会为null")
	private ResultSet getPageData(DBConfigBean dbInfo, DataBaseDialectStrategy strategy, String strSql,
	                              String pageColumn, int start, int end) {
		//TODO pageColumn是海云应用管理端传过来的，画面上由用户提供的用于分页的列名，但是目前使用的是拼接SQL语句进行分页，所以pageColumn暂时用不到
		//1、将DBConfigBean对象传入工具类ConnetionTool，得到DatabaseWrapper
		try(DatabaseWrapper dbWrapper = ConnetionTool.getDBWrapper(dbInfo);){
			//2、将采集SQL，当前页的start，end转换通过strategy转为分页SQL
			String pageSql = strategy.createPageSql(strSql, start, end);
			//3、调用方法获得当前线程的分页数据并返回
			return dbWrapper.queryGetResultSet(pageSql);
		}
	}
}
