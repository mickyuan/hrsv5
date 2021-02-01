package hrds.agent.job.biz.core.jdbcdirectstage;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.conf.Dbtype;
import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.agent.job.biz.bean.*;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.constant.RunStatusConstant;
import hrds.agent.job.biz.constant.StageConstant;
import hrds.agent.job.biz.core.AbstractJobStage;
import hrds.agent.job.biz.core.dfstage.DFUploadStageImpl;
import hrds.agent.job.biz.core.jdbcdirectstage.service.CollectPage;
import hrds.agent.job.biz.utils.JobStatusInfoUtil;
import hrds.commons.codes.AgentType;
import hrds.commons.codes.DatabaseType;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.Store_type;
import hrds.commons.collection.ConnectionTool;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.ConnUtil;
import hrds.commons.utils.Constant;
import hrds.commons.utils.DruidParseQuerySql;
import hrds.commons.utils.StorageTypeKey;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@DocClass(desc = "数据库直连采集数据上传阶段", author = "zxz")
public class JdbcDirectUploadStageImpl extends AbstractJobStage {
	//打印日志
	private static final Logger LOGGER = LogManager.getLogger();
	//数据采集表对应的存储的所有信息
	private final CollectTableBean collectTableBean;
	private final SourceDataConfBean sourceDataConfBean;
	//操作日期
	private final String operateDate;
	//操作时间
	private final String operateTime;
	//操作人
	private final String user_id;

	public JdbcDirectUploadStageImpl(SourceDataConfBean sourceDataConfBean, CollectTableBean collectTableBean) {
		this.sourceDataConfBean = sourceDataConfBean;
		this.collectTableBean = collectTableBean;
		this.operateDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		this.operateTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
		this.user_id = String.valueOf(collectTableBean.getUser_id());
	}

	@Method(desc = "数据库直连采集数据上传阶段处理逻辑，处理完成后，无论成功还是失败，" +
			"将相关状态信息封装到StageStatusInfo对象中返回", logicStep = "" +
			"1、创建卸数阶段状态信息，更新作业ID,阶段名，阶段开始时间" +
			"2、调用方法，进行文件上传，文件数组和上传目录由构造器传入")
	@Return(desc = "StageStatusInfo是保存每个阶段状态信息的实体类", range = "不会为null,StageStatusInfo实体类对象")
	@Override
	public StageParamInfo handleStage(StageParamInfo stageParamInfo) {
		long startTime = System.currentTimeMillis();
		LOGGER.info("------------------表" + collectTableBean.getTable_name()
				+ "数据库直连采集上传阶段开始------------------");
		//1、创建卸数阶段状态信息，更新作业ID,阶段名，阶段开始时间
		StageStatusInfo statusInfo = new StageStatusInfo();
		JobStatusInfoUtil.startStageStatusInfo(statusInfo, collectTableBean.getTable_id(),
				StageConstant.UPLOAD.getCode());
		try {
			long rowCount = 0;
			//遍历多个存储目的地，TODO 这里目前先不用多线程采集，因为后续作业调度，每个目的地都会生成一个作业
			List<DataStoreConfBean> dataStoreConfBeanList = collectTableBean.getDataStoreConfBean();
			if (JdbcDirectUnloadDataStageImpl.doAllSupportExternal(collectTableBean.getDataStoreConfBean())) {
				//支持外部表
				for (DataStoreConfBean dataStoreConfBean : dataStoreConfBeanList) {
					//根据存储类型上传到目的地
					if (Store_type.DATABASE.getCode().equals(dataStoreConfBean.getStore_type())) {
						DFUploadStageImpl.execSftpToDbServer(dataStoreConfBean, stageParamInfo.getFileArr()
								, collectTableBean);
					} else if (Store_type.HIVE.getCode().equals(dataStoreConfBean.getStore_type())) {
						//设置hive的默认类型
						dataStoreConfBean.getData_store_connect_attr().put(StorageTypeKey.database_type,
								DatabaseType.Hive.getCode());
						//有hadoop客户端，通过直接上传hdfs，映射外部表的方式进hive
						DFUploadStageImpl.execHDFSShell(dataStoreConfBean, stageParamInfo.getFileArr(), collectTableBean);
					} else if (Store_type.HBASE.getCode().equals(dataStoreConfBean.getStore_type())) {
						//数据进hbase加载使用BulkLoad加载hdfs上的文件，所以这里必须有hdfs的操作权限，上传hdfs
						DFUploadStageImpl.execHDFSShell(dataStoreConfBean, stageParamInfo.getFileArr(), collectTableBean);
					} else if (Store_type.SOLR.getCode().equals(dataStoreConfBean.getStore_type())) {
						LOGGER.warn("数据库直连采集数据上传进Solr没有实现");
					} else if (Store_type.ElasticSearch.getCode().equals(dataStoreConfBean.getStore_type())) {
						LOGGER.warn("数据库直连采集数据上传进ElasticSearch没有实现");
					} else if (Store_type.MONGODB.getCode().equals(dataStoreConfBean.getStore_type())) {
						LOGGER.warn("数据库直连采集数据上传进MONGODB没有实现");
					} else {
						//TODO 上面的待补充。
						throw new AppSystemException("不支持的存储类型");
					}
				}
			} else {
				for (DataStoreConfBean dataStoreConfBean : dataStoreConfBeanList) {
					//根据存储类型上传到目的地
					if (Store_type.DATABASE.getCode().equals(dataStoreConfBean.getStore_type())) {
						rowCount = jdbcToDataBase(stageParamInfo.getTableBean(), dataStoreConfBean);
					} else if (Store_type.HIVE.getCode().equals(dataStoreConfBean.getStore_type())) {
						//设置hive的默认类型
						dataStoreConfBean.getData_store_connect_attr().put(StorageTypeKey.database_type,
								DatabaseType.Hive.getCode());
						rowCount = jdbcToDataBase(stageParamInfo.getTableBean(), dataStoreConfBean);
					} else if (Store_type.HBASE.getCode().equals(dataStoreConfBean.getStore_type())) {
						LOGGER.warn("数据库直连采集数据上传进HBASE没有实现:hbase类型的存储层" +
								dataStoreConfBean.getDsl_name() + "未选择支持外部表");
					} else if (Store_type.SOLR.getCode().equals(dataStoreConfBean.getStore_type())) {
						LOGGER.warn("数据库直连采集数据上传进SOlR没有实现");
					} else if (Store_type.ElasticSearch.getCode().equals(dataStoreConfBean.getStore_type())) {
						LOGGER.warn("数据库直连采集数据上传进ElasticSearch没有实现");
					} else if (Store_type.MONGODB.getCode().equals(dataStoreConfBean.getStore_type())) {
						LOGGER.warn("数据库直连采集数据上传进MONGODB没有实现");
					} else {
						//TODO 上面的待补充。
						throw new AppSystemException("不支持的存储类型");
					}
				}
				stageParamInfo.setRowCount(rowCount);
			}
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.SUCCEED.getCode(), "执行成功");
			LOGGER.info("------------------表" + collectTableBean.getTable_name()
					+ "数据库直连采集上传阶段成功------------------执行时间为："
					+ (System.currentTimeMillis() - startTime) / 1000 + "，秒");
		} catch (Exception e) {
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.FAILED.getCode(), e.getMessage());
			LOGGER.error(collectTableBean.getTable_name() + "数据库直连采集上传阶段失败：", e);
		}
		//结束给stageParamInfo塞值
		JobStatusInfoUtil.endStageParamInfo(stageParamInfo, statusInfo, collectTableBean
				, AgentType.ShuJuKu.getCode());
		return stageParamInfo;
	}

	private long jdbcToDataBase(TableBean tableBean, DataStoreConfBean dataStoreConfBean) {
		long rowCount = 0;
		//TODO 目前实现两种，同一jdbc、不同jdbc 需要增加一种是否支持外部表，支持外部表应该先落地，再加载比较快
		Map<String, String> data_store_connect_attr = dataStoreConfBean.getData_store_connect_attr();
		//判断是否是同一jdbc连接
		boolean flag = isSameJdbc(sourceDataConfBean.getJdbc_url(), sourceDataConfBean.getDatabase_type(),
				sourceDataConfBean.getDatabase_name(), data_store_connect_attr.get(StorageTypeKey.jdbc_url),
				data_store_connect_attr.get(StorageTypeKey.database_type),
				data_store_connect_attr.get(StorageTypeKey.database_name));
		String todayTableName = collectTableBean.getHbase_name() + "_" + 1;
		DatabaseWrapper db = null;
		try {
			//获取连接
			db = ConnectionTool.getDBWrapper(dataStoreConfBean.getData_store_connect_attr());
			//备份表上次执行进数的数据
			backupToDayTable(todayTableName, db);
			//先创建表，再多线程batch数据入库，根据数据保留天数做相应调整，成功则删除最早一次进数保留的数据
			DFUploadStageImpl.createTodayTable(tableBean, todayTableName, dataStoreConfBean, db);
			if (flag) {
				//获取查询数据的字段的列
				String insert_join = StringUtils.join(StringUtil.split(tableBean.getColumnMetaInfo(),
						Constant.METAINFOSPLIT), ",");
				String select_join = StringUtils.join(StringUtil.split(tableBean.getAllColumns(),
						Constant.METAINFOSPLIT), ",") + "," + collectTableBean.getEtlDate();
				if (JobConstant.ISADDOPERATEINFO) {
					select_join = select_join + ",'" + operateDate + "','" + operateTime + "'," + user_id;
				}
				//执行复制表
				if (tableBean.getCollectSQL().contains(Constant.SQLDELIMITER) ||
						IsFlag.Shi.getCode().equals(collectTableBean.getIs_customize_sql())) {
					//多次执行insert into select
					//1、读取并行抽取sql数
					List<String> parallelSqlList = StringUtil.split(tableBean.getCollectSQL(), Constant.SQLDELIMITER);
					for (String sql : parallelSqlList) {
						executeSameSql(db, sql, todayTableName, insert_join, select_join);
					}
				} else {
					executeSameSql(db, tableBean.getCollectSQL(), todayTableName, insert_join, select_join);
				}
			} else {
				List<Future<Long>> futures;
				//根据collectSql中是否包含`@^分隔符判断是否用户自定义sql并行抽取。
				// 为了防止用户自定义并行抽取，又只写了一个sql,因此加了第二个判断条件
				if (tableBean.getCollectSQL().contains(Constant.SQLDELIMITER) ||
						IsFlag.Shi.getCode().equals(collectTableBean.getIs_customize_sql())) {
					//包含，是否用户自定义的sql进行多线程抽取
					futures = customizeParallelExtract(tableBean, dataStoreConfBean);
				} else {
					//不包含
					futures = pageParallelExtract(tableBean, dataStoreConfBean);
				}
				//5、获得本次采集总数据量
				for (Future<Long> future : futures) {
					if (future.get() < 0) {
						throw new AppSystemException("数据Batch提交到库" + dataStoreConfBean.getDsl_name() + "异常");
					}
					rowCount += future.get();
				}
			}
			//根据表存储期限备份每张表存储期限内进数的数据
			backupPastTable(collectTableBean, db);
			LOGGER.info("数据成功进入库" + dataStoreConfBean.getDsl_name() + "下的表" + collectTableBean.getHbase_name()
					+ ",总计进数" + rowCount + "条");
			return rowCount;
		} catch (Exception e) {
			if (db != null) {
				//执行失败，恢复上次进数的数据
				recoverBackupToDayTable(todayTableName, db);
			}
			throw new AppSystemException("数据库直连采集batch进库" + dataStoreConfBean.getDsl_name() + "下的表"
					+ collectTableBean.getHbase_name() + "异常", e);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	private void executeSameSql(DatabaseWrapper db, String sql, String todayTableName, String insert_join,
								String select_join) {
		if (db.getDbtype() != Dbtype.POSTGRESQL) {
			List<String> tableNames = DruidParseQuerySql.parseSqlTableToList(sql);
			for (String table : tableNames) {
				if (!table.contains(".")) {
					sql = StringUtil.replace(sql, " " + table,
							" " + sourceDataConfBean.getDatabase_name() + "." + table);
				}
			}
		}
		if (Dbtype.DB2V1 == db.getDbtype() || Dbtype.DB2V2 == db.getDbtype()) {
			db.execute("INSERT INTO " + todayTableName + "(" + insert_join + ")" +
					" ( SELECT " + select_join + " FROM ( " + sql + ") AS hyren_dcl_temp )");
		} else {
			db.execute("INSERT INTO " + todayTableName + "(" + insert_join + ")" +
					" SELECT " + select_join + " FROM ( " + sql + ") hyren_dcl_temp ");
		}
	}

	private boolean isSameJdbc(String source_jdbc_url, String source_database_type, String source_database_name,
							   String target_jdbc_url, String target_database_type, String target_database_name) {
		Map<String, String> sourceJdbcUrlInfo = ConnUtil.getJDBCUrlInfo(source_jdbc_url, source_database_type);
		Map<String, String> targetJdbcUrlInfo = ConnUtil.getJDBCUrlInfo(target_jdbc_url, target_database_type);
		if (!source_database_type.equals(target_database_type)) {
			return false;
		} else {
			if (DatabaseType.TeraData.getCode().equals(source_database_type)) {
				if (sourceJdbcUrlInfo.get("ip") == null || targetJdbcUrlInfo.get("ip") == null) {
					return false;
				} else {
					return sourceJdbcUrlInfo.get("ip").equals(targetJdbcUrlInfo.get("ip"));
				}
			} else if (DatabaseType.Postgresql.getCode().equals(source_database_type)) {
				if (sourceJdbcUrlInfo.get("ip") == null || sourceJdbcUrlInfo.get("port") == null
						|| targetJdbcUrlInfo.get("ip") == null || targetJdbcUrlInfo.get("port") == null
						|| source_database_name == null || target_database_name == null) {
					return false;
				} else {
					return sourceJdbcUrlInfo.get("ip").equals(targetJdbcUrlInfo.get("ip")) &&
							sourceJdbcUrlInfo.get("port").equals(targetJdbcUrlInfo.get("port")) &&
							source_database_name.equals(target_database_name);
				}
			} else {
				if (sourceJdbcUrlInfo.get("ip") == null || sourceJdbcUrlInfo.get("port") == null
						|| targetJdbcUrlInfo.get("ip") == null || targetJdbcUrlInfo.get("port") == null) {
					return false;
				} else {
					return sourceJdbcUrlInfo.get("ip").equals(targetJdbcUrlInfo.get("ip")) &&
							sourceJdbcUrlInfo.get("port").equals(targetJdbcUrlInfo.get("port")) &&
							source_database_name.equals(target_database_name);
				}
			}
		}
	}

	/**
	 * 自定义并行抽取
	 */
	private List<Future<Long>> customizeParallelExtract(TableBean tableBean,
														DataStoreConfBean dataStoreConfBean) {
		ExecutorService executorService = null;
		try {
			List<Future<Long>> futures = new ArrayList<>();
			int lastPageEnd = Integer.MAX_VALUE;
			//1、读取并行抽取sql数
			List<String> parallelSqlList = StringUtil.split(tableBean.getCollectSQL(), Constant.SQLDELIMITER);
			//2、创建固定大小的线程池，执行分页查询(线程池类型和线程数可以后续改造)
			// 此处不会有海量的任务需要执行，不会出现队列中等待的任务对象过多的OOM事件。
			executorService = Executors.newFixedThreadPool(JobConstant.AVAILABLEPROCESSORS);
			for (String sql : parallelSqlList) {
				//直接每个线程都去0到最大值的数据量
				CollectPage lastPage = new CollectPage(sourceDataConfBean, collectTableBean, tableBean,
						1, lastPageEnd, dataStoreConfBean, sql);
				Future<Long> lastFuture = executorService.submit(lastPage);
				futures.add(lastFuture);
			}
			return futures;
		} catch (Exception e) {
			throw new AppSystemException("执行分页卸数程序失败", e);
		} finally {
			closeExecutor(executorService);
		}
	}

	/**
	 * 关闭线程池
	 *
	 * @param executorService 线程池
	 */
	private void closeExecutor(ExecutorService executorService) {
		//关闭线程池
		if (executorService != null) {
			try {
				//停止接收新任务，原来的任务继续执行
				executorService.shutdown();
			} catch (Exception e) {
				LOGGER.warn("销毁线程池出现错误", e);
			}
		}
	}

	/**
	 * 分页并行抽取
	 */
	private List<Future<Long>> pageParallelExtract(TableBean tableBean
			, DataStoreConfBean dataStoreConfBean) {
		ExecutorService executorService = null;
		try {
			List<Future<Long>> futures = new ArrayList<>();
			int lastPageEnd = Integer.MAX_VALUE;
			//判断是否并行抽取
			if (IsFlag.Shi.getCode().equals(collectTableBean.getIs_parallel())) {
				//2、解析作业信息，得到表名和表数据量
				int totalCount = Integer.parseInt(collectTableBean.getTable_count());
				//获取每日新增数据量，重新计算表的数据总量
				int days = DateUtil.dateMargin(collectTableBean.getRec_num_date(), collectTableBean.getEtlDate());
				//跑批日期小于获取数据总量日期，数据总量不变
				days = Math.max(days, 0);
				totalCount += collectTableBean.getDataincrement() * days;
				//3、读取并行抽取线程数
				int threadCount = collectTableBean.getPageparallels();
				if (threadCount > totalCount) {
					throw new AppSystemException("多线程抽取数据，页面填写的多线程数大于表的总数据量");
				}
				int pageRow = totalCount / threadCount;
				//4、创建固定大小的线程池，执行分页查询(线程池类型和线程数可以后续改造)
				// 此处不会有海量的任务需要执行，不会出现队列中等待的任务对象过多的OOM事件。
				executorService = Executors.newFixedThreadPool(JobConstant.AVAILABLEPROCESSORS);
				for (int i = 0; i < threadCount; i++) {
					int start = (i * pageRow) + 1;
					int end = (i + 1) * pageRow;
					//传入i(分页页码)，pageRow(每页的数据量)，用于写avro时的行号
					CollectPage page = new CollectPage(sourceDataConfBean, collectTableBean, tableBean
							, start, end, dataStoreConfBean);
					Future<Long> future = executorService.submit(page);
					futures.add(future);
				}
				int lastPageStart = pageRow * threadCount + 1;
				//最后一个线程的最大条数设为Long.MAX_VALUE
				CollectPage lastPage = new CollectPage(sourceDataConfBean, collectTableBean, tableBean,
						lastPageStart, lastPageEnd, dataStoreConfBean);
				Future<Long> lastFuture = executorService.submit(lastPage);
				futures.add(lastFuture);
			} else if (IsFlag.Fou.getCode().equals(collectTableBean.getIs_parallel())) {
				//4、创建固定大小的线程池，执行分页查询(线程池类型和线程数可以后续改造)
				// 此处不会有海量的任务需要执行，不会出现队列中等待的任务对象过多的OOM事件。
				executorService = Executors.newFixedThreadPool(1);
				CollectPage collectPage = new CollectPage(sourceDataConfBean, collectTableBean, tableBean,
						1, lastPageEnd, dataStoreConfBean);
				Future<Long> lastFuture = executorService.submit(collectPage);
				futures.add(lastFuture);
			} else {
				throw new AppSystemException("错误的是否标识");
			}
			return futures;
		} catch (Exception e) {
			throw new AppSystemException("执行分页卸数程序失败", e);
		} finally {
			closeExecutor(executorService);
		}
	}

	@Override
	public int getStageCode() {
		return StageConstant.UPLOAD.getCode();
	}
}
