package hrds.agent.job.biz.core.dbstage;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.FileNameUtils;
import hrds.agent.job.biz.bean.*;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.constant.RunStatusConstant;
import hrds.agent.job.biz.constant.StageConstant;
import hrds.agent.job.biz.core.AbstractJobStage;
import hrds.agent.job.biz.core.dbstage.service.CollectPage;
import hrds.agent.job.biz.core.service.CollectTableHandleFactory;
import hrds.agent.job.biz.utils.DataExtractUtil;
import hrds.agent.job.biz.utils.FileUtil;
import hrds.agent.job.biz.utils.JobStatusInfoUtil;
import hrds.commons.codes.CollectType;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.UnloadType;
import hrds.commons.exception.AppSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
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
			if (UnloadType.QuanLiangXieShu.getCode().equals(collectTableBean.getUnload_type())) {
				//全量卸数
				fullAmountExtract(stageParamInfo);
			} else if (UnloadType.ZengLiangXieShu.getCode().equals(collectTableBean.getUnload_type())) {
				//增量卸数
				incrementExtract(stageParamInfo);
			} else {
				throw new AppSystemException("数据抽取卸数方式类型不正确");
			}
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.SUCCEED.getCode(), "执行成功");
			LOGGER.info("------------------数据库直连采集卸数阶段成功------------------");
		} catch (Exception e) {
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.FAILED.getCode(), e.getMessage());
			LOGGER.error("数据库直连采集卸数阶段失败：", e);
		}
		//结束给stageParamInfo塞值
		JobStatusInfoUtil.endStageParamInfo(stageParamInfo, statusInfo, collectTableBean
				, CollectType.ShuJuKuCaiJi.getCode());
		return stageParamInfo;
	}

	@Override
	public int getStageCode() {
		return StageConstant.UNLOADDATA.getCode();
	}

	/**
	 * 增量抽取
	 */
	private void incrementExtract(StageParamInfo stageParamInfo) {
		//TODO
	}

	/**
	 * 全量抽取
	 */
	private void fullAmountExtract(StageParamInfo stageParamInfo) throws Exception {
		TableBean tableBean = CollectTableHandleFactory.getCollectTableHandleInstance(sourceDataConfBean)
				.generateTableInfo(sourceDataConfBean, collectTableBean);
		stageParamInfo.setTableBean(tableBean);
		//根据collectSql中是否包含~@^分隔符判断是否用户自定义sql并行抽取
		if (tableBean.getCollectSQL().contains(JobConstant.SQLDELIMITER)) {
			//包含，是否用户自定义的sql进行多线程抽取
			customizeParallelExtract(stageParamInfo, tableBean);
		} else {
			//不包含
			pageParallelExtract(stageParamInfo, tableBean);
		}
		//XXX 数据字典的路径,数据字典的路径应该是指定位置，待定
		String dictionaryPath = FileNameUtils.normalize(collectTableBean.getData_extraction_def_list().
				get(0).getPlane_url() + File.separator + collectTableBean.getTable_name()
				+ File.separator, true);
		//写数据字典
		DataExtractUtil.writeDataDictionary(dictionaryPath, collectTableBean.getTable_name(),
				tableBean.getColumnMetaInfo(), tableBean.getColTypeMetaInfo(), collectTableBean.getStorage_type(),
				collectTableBean.getData_extraction_def_list());
	}

	/**
	 * 自定义并行抽取
	 */
	private void customizeParallelExtract(StageParamInfo stageParamInfo, TableBean tableBean) {
		//TODO
	}


	/**
	 * 分页并行抽取
	 */
	@SuppressWarnings("unchecked")
	private void pageParallelExtract(StageParamInfo stageParamInfo, TableBean tableBean) throws Exception {
		ExecutorService executorService = null;
		try {
			//fileResult中是生成的所有数据文件的路径，用于判断卸数阶段结果
			List<String> fileResult = new ArrayList<>();
			//pageCountResult是本次采集作业每个线程采集到的数据量，用于写meta文件
			List<Long> pageCountResult = new ArrayList<>();
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
			//5、获得结果,用于校验多线程采集的结果和写Meta文件
			for (Future<Map<String, Object>> future : futures) {
				fileResult.addAll((List<String>) future.get().get("filePathList"));
				pageCountResult.add(Long.parseLong((String) future.get().get("pageCount")));
			}
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
		} finally {
			//关闭线程池
			if (executorService != null)
				executorService.shutdown();
		}
	}
}
