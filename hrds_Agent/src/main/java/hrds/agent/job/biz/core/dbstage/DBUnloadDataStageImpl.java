package hrds.agent.job.biz.core.dbstage;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.FileNameUtils;
import fd.ng.core.utils.StringUtil;
import hrds.agent.job.biz.bean.*;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.constant.RunStatusConstant;
import hrds.agent.job.biz.constant.StageConstant;
import hrds.agent.job.biz.core.AbstractJobStage;
import hrds.agent.job.biz.core.dbstage.service.CollectPage;
import hrds.agent.job.biz.core.dbstage.service.ResultSetParser;
import hrds.agent.job.biz.core.metaparse.AbstractCollectTableHandle;
import hrds.agent.job.biz.core.metaparse.CollectTableHandleFactory;
import hrds.agent.job.biz.utils.DataExtractUtil;
import hrds.agent.job.biz.utils.FileUtil;
import hrds.agent.job.biz.utils.JobStatusInfoUtil;
import hrds.commons.codes.CollectType;
import hrds.commons.codes.FileFormat;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.UnloadType;
import hrds.commons.entity.Data_extraction_def;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.ConnUtil;
import hrds.commons.utils.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@DocClass(desc = "数据库直连采集数据卸数阶段", author = "WangZhengcheng")
public class DBUnloadDataStageImpl extends AbstractJobStage {

	private final static Logger LOGGER = LoggerFactory.getLogger(DBUnloadDataStageImpl.class);

	private SourceDataConfBean sourceDataConfBean;
	private CollectTableBean collectTableBean;

	public DBUnloadDataStageImpl(SourceDataConfBean sourceDataConfBean, CollectTableBean collectTableBean) {
		this.sourceDataConfBean = sourceDataConfBean;
		this.collectTableBean = collectTableBean;
	}

	@Method(desc = "数据库直连采集数据卸数阶段处理逻辑，处理完成后，无论成功还是失败，" +
			"将相关状态信息封装到StageStatusInfo对象中返回", logicStep = "" +
			"1、创建卸数阶段状态信息，更新作业ID,阶段名，阶段开始时间" +
			"2、解析作业信息，得到表名和表数据量" +
			"3、根据采集线程数，计算每个任务的采集数量" +
			"4、构建线程对象CollectPage，放入线程池执行" +
			"5、获得结果,用于校验多线程采集的结果和写Meta文件" +
			"6、判断本次卸数阶段是否成功，设置成功或者错误信息")
	@Return(desc = "StageStatusInfo是保存每个阶段状态信息的实体类", range = "不会为null，StageStatusInfo实体类对象")
	@Override
	public StageParamInfo handleStage(StageParamInfo stageParamInfo) {
		LOGGER.info("------------------数据库直连采集卸数阶段开始------------------");
		//1、创建卸数阶段状态信息，更新作业ID,阶段名，阶段开始时间
		StageStatusInfo statusInfo = new StageStatusInfo();
		JobStatusInfoUtil.startStageStatusInfo(statusInfo, collectTableBean.getTable_id(),
				StageConstant.UNLOADDATA.getCode());
		try {
			//开始执行防止重跑，先把抽取的文件的目录重命名
			renameUnloadDir(collectTableBean);
			//执行卸数
			TableBean tableBean = CollectTableHandleFactory.getCollectTableHandleInstance(sourceDataConfBean)
					.generateTableInfo(sourceDataConfBean, collectTableBean);
			if (UnloadType.QuanLiangXieShu.getCode().equals(collectTableBean.getUnload_type())) {
				//全量卸数
				fullAmountExtract(stageParamInfo, tableBean);
			} else if (UnloadType.ZengLiangXieShu.getCode().equals(collectTableBean.getUnload_type())) {
				//增量卸数
				incrementExtract(stageParamInfo, tableBean);
			} else {
				throw new AppSystemException("数据抽取卸数方式类型不正确");
			}
			stageParamInfo.setTableBean(tableBean);
			//数据字典的路径
			String dictionaryPath = FileNameUtils.normalize(Constant.DICTIONARY + File.separator +
					collectTableBean.getDatabase_id() + File.separator, true);
			//写数据字典
			DataExtractUtil.writeDataDictionary(dictionaryPath, collectTableBean.getTable_name(),
					tableBean.getColumnMetaInfo(), tableBean.getColTypeMetaInfo(),
					collectTableBean.getData_extraction_def_list(), collectTableBean.getUnload_type(),
					tableBean.getPrimaryKeyInfo(), tableBean.getInsertColumnInfo(), tableBean.getUpdateColumnInfo()
					, tableBean.getDeleteColumnInfo());
			//卸数成功，删除重命名的目录
			deleteRenameDir(collectTableBean);
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.SUCCEED.getCode(), "执行成功");
			LOGGER.info("------------------数据库直连采集卸数阶段成功------------------");
		} catch (Exception e) {
			//卸数失败，删除本次卸数的目录，恢复数据
			try {
				restoreRenameDir(collectTableBean);
			} catch (Exception e1) {
				LOGGER.warn("卸数失败，恢复上次卸数数据失败", e);
			}
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.FAILED.getCode(), e.getMessage());
			LOGGER.error("数据库直连采集卸数阶段失败：", e);
		}
		//结束给stageParamInfo塞值
		JobStatusInfoUtil.endStageParamInfo(stageParamInfo, statusInfo, collectTableBean
				, CollectType.ShuJuKuCaiJi.getCode());
		return stageParamInfo;
	}

	/**
	 * 卸数失败，删除本次卸数的文件目录，恢复上次卸数的文件目录（同一个跑批日期的情况下）
	 *
	 * @param collectTableBean 表存储信息
	 */
	private void restoreRenameDir(CollectTableBean collectTableBean) throws Exception {
		List<Data_extraction_def> data_extraction_def_list = collectTableBean.getData_extraction_def_list();
		for (Data_extraction_def extraction_def : data_extraction_def_list) {
			String targetName = extraction_def.getPlane_url() + File.separator + collectTableBean.getEtlDate()
					+ File.separator + collectTableBean.getTable_name() + File.separator +
					FileFormat.ofValueByCode(extraction_def.getDbfile_format()) + File.separator;
			File file = new File(targetName);
			if (file.exists()) {
				fd.ng.core.utils.FileUtil.deleteDirectory(file);
			}
			String sourceName = extraction_def.getPlane_url() + File.separator + collectTableBean.getEtlDate()
					+ File.separator + collectTableBean.getTable_name() + File.separator +
					FileFormat.ofValueByCode(extraction_def.getDbfile_format()) + "_BAK" + File.separator;
			File sourceFile = new File(sourceName);
			if (sourceFile.exists()) {
				if (!sourceFile.renameTo(new File(targetName)))
					throw new AppSystemException("重名" + sourceName + "为" + targetName + "失败");
			}
		}
	}

	/**
	 * 卸数成功，将上次卸数的文件目录删除（同一个跑批日期的情况下）
	 *
	 * @param collectTableBean 表存储信息
	 */
	private void deleteRenameDir(CollectTableBean collectTableBean) throws Exception {
		List<Data_extraction_def> data_extraction_def_list = collectTableBean.getData_extraction_def_list();
		for (Data_extraction_def extraction_def : data_extraction_def_list) {
			String targetName = extraction_def.getPlane_url() + File.separator + collectTableBean.getEtlDate()
					+ File.separator + collectTableBean.getTable_name() + File.separator +
					FileFormat.ofValueByCode(extraction_def.getDbfile_format()) + "_BAK" + File.separator;
			File file = new File(targetName);
			if (file.exists()) {
				fd.ng.core.utils.FileUtil.deleteDirectory(file);
			}
		}
	}

	/**
	 * 开始卸数，将上次卸数的文件目录重命名（同一个跑批日期的情况下）
	 *
	 * @param collectTableBean 表存储信息
	 */
	private void renameUnloadDir(CollectTableBean collectTableBean) {
		List<Data_extraction_def> data_extraction_def_list = collectTableBean.getData_extraction_def_list();
		for (Data_extraction_def extraction_def : data_extraction_def_list) {
			String sourceName = extraction_def.getPlane_url() + File.separator + collectTableBean.getEtlDate()
					+ File.separator + collectTableBean.getTable_name() + File.separator +
					FileFormat.ofValueByCode(extraction_def.getDbfile_format()) + File.separator;
			File file = new File(sourceName);
			String targetName = extraction_def.getPlane_url() + File.separator + collectTableBean.getEtlDate()
					+ File.separator + collectTableBean.getTable_name() + File.separator +
					FileFormat.ofValueByCode(extraction_def.getDbfile_format()) + "_BAK" + File.separator;
			if (file.exists()) {
				if (!file.renameTo(new File(targetName)))
					throw new AppSystemException("重名" + sourceName + "为" + targetName + "失败");
			}
		}
	}

	@Override
	public int getStageCode() {
		return StageConstant.UNLOADDATA.getCode();
	}

	/**
	 * 增量抽取
	 */
	private void incrementExtract(StageParamInfo stageParamInfo, TableBean tableBean) {
		Connection conn = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			//获取jdbc连接
			conn = ConnUtil.getConnection(sourceDataConfBean.getDatabase_drive(), sourceDataConfBean.getJdbc_url(),
					sourceDataConfBean.getUser_name(), sourceDataConfBean.getDatabase_pad());
			List<String> fileResult = new ArrayList<>();
			//pageCountResult是本次采集作业每个线程采集到的数据量，用于写meta文件
			List<Long> pageCountResult = new ArrayList<>();
			//增量抽取是根据页面传过来的三个sql直接抽取出增量数据
			String incrementSql = collectTableBean.getSql();
			List<String> incrementSqlList = getSortJson(JSONObject.parseObject(incrementSql));
			String[] operateArray = {"delete", "update", "insert"};
			//遍历json根据json的key执行sql,拼接对应的操作方式,增量抽取是写到同一个文件，因此这里不使用多线程
			for (int i = 0; i < incrementSqlList.size(); i++) {
				//获取增量的sql
				String sql = incrementSqlList.get(i);
				if (!StringUtil.isEmpty(sql)) {
					//替换掉sql中需要传递的参数
					sql = AbstractCollectTableHandle.replaceSqlParam(sql, collectTableBean.getSqlParam());
					statement = conn.createStatement();
					//查询数据
					resultSet = statement.executeQuery(sql);
					tableBean.setOperate(operateArray[i]);
					//2、解析ResultSet，并写数据文件
					ResultSetParser parser = new ResultSetParser();
					//文件路径
					String unLoadInfo = parser.parseResultSet(resultSet, collectTableBean, 0,
							tableBean, collectTableBean.getData_extraction_def_list().get(0));
					if (!StringUtil.isEmpty(unLoadInfo) && unLoadInfo.contains(Constant.METAINFOSPLIT)) {
						List<String> unLoadInfoList = StringUtil.split(unLoadInfo, Constant.METAINFOSPLIT);
						String pageCount = unLoadInfoList.get(unLoadInfoList.size() - 1);
						unLoadInfoList.remove(unLoadInfoList.size() - 1);
						fileResult.addAll(unLoadInfoList);
						pageCountResult.add(Long.parseLong(pageCount));
					}
				}
			}
			countResult(fileResult, pageCountResult, stageParamInfo);
		} catch (Exception e) {
			throw new AppSystemException("执行增量抽取sql失败", e);
		} finally {
			try {
				if (resultSet != null)
					resultSet.close();
				if (statement != null)
					statement.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				LOGGER.error(e.getMessage());
			}
		}
	}

	public static void countResult(List<String> fileResult, List<Long> pageCountResult, StageParamInfo stageParamInfo) {
		//获得本次采集总数据量
		long rowCount = 0;
		for (Long pageCount : pageCountResult) {
			rowCount += pageCount;
		}
		stageParamInfo.setRowCount(rowCount);
		//获得本次采集生成的数据文件的总大小
		long fileSize = 0;
		String[] fileArr = new String[fileResult.size()];
		for (int i = 0; i < fileResult.size(); i++) {
			fileArr[i] = fileResult.get(i);
			//判断文件是否存在，如果某个文件存在，则计算大小，若不存在，记录日志并继续运行
			if (FileUtil.decideFileExist(fileArr[i])) {
				long singleFileSize = FileUtil.getFileSize(fileArr[i]);
				fileSize += singleFileSize;
			} else {
				throw new AppSystemException("数据库直连采集" + fileArr[i] + "文件不存在");
			}
		}
		stageParamInfo.setFileArr(fileArr);
		stageParamInfo.setFileSize(fileSize);
	}

	/**
	 * 全量抽取
	 */
	@SuppressWarnings("unchecked")
	private void fullAmountExtract(StageParamInfo stageParamInfo, TableBean tableBean) throws Exception {
		//fileResult中是生成的所有数据文件的路径，用于判断卸数阶段结果
		List<String> fileResult = new ArrayList<>();
		//pageCountResult是本次采集作业每个线程采集到的数据量，用于写meta文件
		List<Long> pageCountResult = new ArrayList<>();
		List<Future<Map<String, Object>>> futures;
		//根据collectSql中是否包含~@^分隔符判断是否用户自定义sql并行抽取。
		// 为了防止用户自定义并行抽取，又只写了一个sql,加了第二个判断条件
		if (tableBean.getCollectSQL().contains(JobConstant.SQLDELIMITER) ||
				IsFlag.Shi.getCode().equals(collectTableBean.getIs_customize_sql())) {
			//包含，是否用户自定义的sql进行多线程抽取
			futures = customizeParallelExtract(tableBean);
		} else {
			//不包含
			futures = pageParallelExtract(tableBean);
		}
		//5、获得结果,用于校验多线程采集的结果和写Meta文件
		for (Future<Map<String, Object>> future : futures) {
			fileResult.addAll((List<String>) future.get().get("filePathList"));
			pageCountResult.add(Long.parseLong((String) future.get().get("pageCount")));
		}
		//获得本次采集总数据量
		countResult(fileResult, pageCountResult, stageParamInfo);
	}

	/**
	 * 自定义并行抽取
	 */
	private List<Future<Map<String, Object>>> customizeParallelExtract(TableBean tableBean) {
		ExecutorService executorService = null;
		try {
			List<Future<Map<String, Object>>> futures = new ArrayList<>();
			long lastPageEnd = Long.MAX_VALUE;
			//判断是否并行抽取
			if (IsFlag.Shi.getCode().equals(collectTableBean.getIs_parallel())) {
				//3、读取并行抽取sql数
				List<String> parallelSqlList = StringUtil.split(tableBean.getCollectSQL(), JobConstant.SQLDELIMITER);
				//4、创建固定大小的线程池，执行分页查询(线程池类型和线程数可以后续改造)
				// 此处不会有海量的任务需要执行，不会出现队列中等待的任务对象过多的OOM事件。
				executorService = Executors.newFixedThreadPool(parallelSqlList.size());
				for (int i = 0; i < parallelSqlList.size(); i++) {
					//最后一个线程的最大条数设为Long.MAX_VALUE
					CollectPage lastPage = new CollectPage(sourceDataConfBean, collectTableBean, tableBean,
							0, lastPageEnd, i, lastPageEnd, parallelSqlList.get(i));
					Future<Map<String, Object>> lastFuture = executorService.submit(lastPage);
					futures.add(lastFuture);
				}
			}
			return futures;
		} finally {
			//关闭线程池
			if (executorService != null)
				executorService.shutdown();
		}
	}


	/**
	 * 分页并行抽取
	 */
	private List<Future<Map<String, Object>>> pageParallelExtract(TableBean tableBean) {
		ExecutorService executorService = null;
		try {
			List<Future<Map<String, Object>>> futures = new ArrayList<>();
			long lastPageEnd = Long.MAX_VALUE;
			//判断是否并行抽取
			if (IsFlag.Shi.getCode().equals(collectTableBean.getIs_parallel())) {
				//2、解析作业信息，得到表名和表数据量
				long totalCount = Long.parseLong(collectTableBean.getTable_count());
				//获取每日新增数据量，重新计算表的数据总量
				int days = DateUtil.dateMargin(collectTableBean.getRec_num_date(), collectTableBean.getEtlDate());
				//跑批日期小于获取数据总量日期，数据总量不变
				days = days > 0 ? days : 0;
				totalCount += collectTableBean.getDataincrement() * days;
				//3、读取并行抽取线程数
				Integer threadCount = collectTableBean.getPageparallels();
				long pageRow = totalCount / threadCount;
				//4、创建固定大小的线程池，执行分页查询(线程池类型和线程数可以后续改造)
				// 此处不会有海量的任务需要执行，不会出现队列中等待的任务对象过多的OOM事件。
				executorService = Executors.newFixedThreadPool(threadCount);
				for (int i = 0; i < threadCount; i++) {
					long start = (i * pageRow);
					long end = (i + 1) * pageRow;
					//传入i(分页页码)，pageRow(每页的数据量)，用于写avro时的行号
					CollectPage page = new CollectPage(sourceDataConfBean, collectTableBean, tableBean
							, start, end, i, pageRow);
					Future<Map<String, Object>> future = executorService.submit(page);
					futures.add(future);
				}
				long lastPageStart = pageRow * threadCount;
				//最后一个线程的最大条数设为Long.MAX_VALUE
				CollectPage lastPage = new CollectPage(sourceDataConfBean, collectTableBean, tableBean,
						lastPageStart, lastPageEnd, threadCount, pageRow);
				Future<Map<String, Object>> lastFuture = executorService.submit(lastPage);
				futures.add(lastFuture);
			} else if (IsFlag.Fou.getCode().equals(collectTableBean.getIs_parallel())) {
				//4、创建固定大小的线程池，执行分页查询(线程池类型和线程数可以后续改造)
				// 此处不会有海量的任务需要执行，不会出现队列中等待的任务对象过多的OOM事件。
				executorService = Executors.newFixedThreadPool(1);
				CollectPage collectPage = new CollectPage(sourceDataConfBean, collectTableBean, tableBean,
						0, lastPageEnd, 0, lastPageEnd);
				Future<Map<String, Object>> lastFuture = executorService.submit(collectPage);
				futures.add(lastFuture);
			} else {
				throw new AppSystemException("错误的是否标识");
			}
			return futures;
		} finally {
			//关闭线程池
			if (executorService != null)
				executorService.shutdown();
		}
	}

	/**
	 * 对增量sql进行排序，保证写文件的顺序是先删除,再更新,再新增。避免出现把新增数据删除或者更新了的情况
	 */
	private List<String> getSortJson(JSONObject json) {
		List<String> sqlList = new ArrayList<>();
		sqlList.add(json.getString("delete"));
		sqlList.add(json.getString("update"));
		sqlList.add(json.getString("insert"));
		return sqlList;
	}

}
