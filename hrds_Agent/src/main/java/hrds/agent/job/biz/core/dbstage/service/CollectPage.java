package hrds.agent.job.biz.core.dbstage.service;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.SourceDataConfBean;
import hrds.agent.job.biz.bean.TableBean;
import hrds.commons.codes.DatabaseType;
import hrds.commons.entity.Data_extraction_def;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.ConnUtil;
import hrds.commons.utils.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@DocClass(desc = "多线程采集线程类，子线程向主线程返回的有生成的文件路径，当前线程采集到的ResultSet，" +
		"当前线程采集到的数据量", author = "WangZhengcheng")
public class CollectPage implements Callable<Map<String, Object>> {
	private final static Logger LOGGER = LoggerFactory.getLogger(CollectPage.class);
	private SourceDataConfBean sourceDataConfBean;
	private CollectTableBean collectTableBean;
	private TableBean tableBean;
	private String sql;
	private long start;
	private long end;
	private int pageNum;
	private long pageRow;

	public CollectPage(SourceDataConfBean sourceDataConfBean, CollectTableBean collectTableBean,
	                   TableBean tableBean, long start, long end, int pageNum, long pageRow) {
		this.sourceDataConfBean = sourceDataConfBean;
		this.collectTableBean = collectTableBean;
		this.tableBean = tableBean;
		this.start = start;
		this.end = end;
		this.pageNum = pageNum;
		this.pageRow = pageRow;
		this.sql = tableBean.getCollectSQL();
	}

	public CollectPage(SourceDataConfBean sourceDataConfBean, CollectTableBean collectTableBean,
	                   TableBean tableBean, long start, long end, int pageNum, long pageRow, String sql) {
		this.sourceDataConfBean = sourceDataConfBean;
		this.collectTableBean = collectTableBean;
		this.tableBean = tableBean;
		this.start = start;
		this.end = end;
		this.pageNum = pageNum;
		this.pageRow = pageRow;
		this.sql = sql;
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
	public Map<String, Object> call() {
		Connection conn = null;
		try {
			//获取jdbc连接
			conn = ConnUtil.getConnection(sourceDataConfBean.getDatabase_drive(), sourceDataConfBean.getJdbc_url(),
					sourceDataConfBean.getUser_name(), sourceDataConfBean.getDatabase_pad());
			//获得数据抽取文件格式
			List<Data_extraction_def> data_extraction_def_list = collectTableBean.getData_extraction_def_list();
			if (data_extraction_def_list == null || data_extraction_def_list.isEmpty()) {
				throw new AppSystemException("抽取的文件格式不能为空");
			}
			//抽取这里可以同时抽成多种文件格式，遍历，执行卸数。
			//TODO 这里有一个优化的方式，就是在一个resultSet里面根据逻辑写多个文件，暂时直接遍历，复用以前的方法
			//TODO 抽取这里其实不用返回文件路径，待删除
			Map<String, Object> map = new HashMap<>();
			for (Data_extraction_def data_extraction_def : data_extraction_def_list) {
				//1、执行查询，获取ResultSet
				ResultSet resultSet = getPageData(conn);
				if (resultSet != null) {
					//2、解析ResultSet，并写数据文件
					ResultSetParser parser = new ResultSetParser();
					//文件路径
					String unLoadInfo = parser.parseResultSet(resultSet, collectTableBean, pageNum,
							tableBean, data_extraction_def);
					if (!StringUtil.isEmpty(unLoadInfo) && unLoadInfo.contains(Constant.METAINFOSPLIT)) {
						List<String> unLoadInfoList = StringUtil.split(unLoadInfo, Constant.METAINFOSPLIT);
						map.put("pageCount", unLoadInfoList.get(unLoadInfoList.size() - 1));
						unLoadInfoList.remove(unLoadInfoList.size() - 1);
						map.put("filePathList", unLoadInfoList);
					}
					resultSet.close();
				}
			}
			return map;
		} catch (Exception e) {
			throw new AppSystemException("执行分页卸数程序失败", e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					LOGGER.error(e.getMessage());
				}
			}
		}

	}

	@Method(desc = "根据分页SQL获取ResultSet", logicStep = "" +
			"1、将DBConfigBean对象传入工具类ConnectionTool，得到DatabaseWrapper" +
			"2、将采集SQL，当前页的start，end转换通过strategy转为分页SQL" +
			"3、调用方法获得当前线程的分页数据并返回")
	@Param(name = "strategy", desc = "数据库方言策略实例", range = "不为空，DataBaseDialectStrategy接口实例")
	@Param(name = "strSql", desc = "数据库直连采集作业SQL语句", range = "不为空")
	@Param(name = "start", desc = "当前分页开始条数", range = "不限")
	@Param(name = "end", desc = "当前分页结束条数", range = "不限")
	@Return(desc = "当前线程执行分页SQL查询得到的结果集", range = "不会为null")
	private ResultSet getPageData(Connection conn) throws Exception {
		// TODO 默认使用主键做分页，没有主键默认使用第一个字段
		DatabaseMetaData databaseMetaData = conn.getMetaData();
		ResultSet rs = databaseMetaData.getPrimaryKeys(null, null,
				collectTableBean.getTable_name().toUpperCase());
		String primaryKey = null;
		if (rs.next()) {
			primaryKey = rs.getString("COLUMN_NAME");
		}
		rs.close();
		if (StringUtil.isEmpty(primaryKey)) {
			primaryKey = collectTableBean.getCollectTableColumnBeanList().get(0).getColumn_name();
		}
		String database_type = sourceDataConfBean.getDatabase_type();
		//拼分页的sql
		String pageSql = pageForSql(database_type, primaryKey);
		Statement statement = conn.createStatement();
		//TODO 不同数据库的set fetchSize 实现方式不同，暂时设置Oracle 的参数 其他的目前不予处理
		if (DatabaseType.Oracle10g.getCode().equals(database_type) ||
				DatabaseType.Oracle9i.getCode().equals(database_type)) {
			statement.setFetchSize(400);
		}
		if (DatabaseType.MYSQL.getCode().equals(database_type))
			((com.mysql.jdbc.Statement) statement).enableStreamingResults();
		return statement.executeQuery(pageSql);
	}

	private String pageForSql(String dataType, String primaryKey) {
		//定义一个临时遍历，获取sql
		String tempSql;
		//定义一个临时的分页数,不改变成员变量的值
		long pageRowTemp;
		//判断如果end为最后一次线程的值，对于MYSQL和Postgresql的最后一页值为最大值
		if (end == Long.MAX_VALUE) {
			pageRowTemp = end;
		} else {
			pageRowTemp = pageRow;
		}
		LOGGER.info("start-->" + start + "  limit --> " + pageRowTemp + "  end--> " + end);
		if (DatabaseType.MYSQL.getCode().equals(dataType)) {
			tempSql = "select * from (" + sql + ") as hyren_collect_temp limit " + start + "," + pageRowTemp;
		} else if (DatabaseType.TeraData.getCode().equals(dataType)) {
			tempSql = "select * from (" + sql + ") as hyren_collect_temp qualify row_number() over(order by "
					+ primaryKey + ") >= " + start + " and row_number() over(order by "
					+ primaryKey + ") <=" + end;
		} else if (DatabaseType.Oracle9i.getCode().equals(dataType) ||
				DatabaseType.Oracle10g.getCode().equals(dataType)) {
			tempSql = "select * from (select t.*,rownum hyren_rn from (" + sql + ") t where rownum <= "
					+ Math.abs(end) + ") t1 where t1.hyren_rn>" + start + "";
		} else if (DatabaseType.Postgresql.getCode().equals(dataType)) {
			tempSql = "select * from (" + sql + ") as hyren_collect_temp  limit " + pageRowTemp + " offset " + start;
		} else {
			//TODO 这里欢迎补全，最后else抛异常
			tempSql = "select * from (" + sql + ") as hyren_collect_temp  limit " + start + "," + pageRowTemp;
		}
		LOGGER.info("分页这里执行的sql是：" + tempSql);
		return tempSql;
	}

}
