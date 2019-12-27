package hrds.agent.job.biz.core.dbstage;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.SourceDataConfBean;
import hrds.agent.job.biz.bean.StageStatusInfo;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.constant.RunStatusConstant;
import hrds.agent.job.biz.constant.StageConstant;
import hrds.agent.job.biz.core.AbstractJobStage;
import hrds.agent.job.biz.core.dbstage.service.CollectPage;
import hrds.agent.job.biz.core.dbstage.service.CollectTableHandleParse;
import hrds.agent.job.biz.utils.FileUtil;
import hrds.agent.job.biz.utils.JobStatusInfoUtil;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.PropertyParaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@DocClass(desc = "数据库直连采集数据卸数阶段", author = "WangZhengcheng")
public class DBUnloadDataStageImpl extends AbstractJobStage {

	private final static Logger LOGGER = LoggerFactory.getLogger(DBUnloadDataStageImpl.class);

	private SourceDataConfBean sourceDataConfBean;
	private CollectTableBean collectTableBean;
	//列类型
	private List<String> columnTypes = new ArrayList<>();
	//本次采集数据量
	private long rowCount = 0;
	//本次采集表的meta信息
	private TableBean tableBean;
	//本次采集生成的数据文件的总大小
	private long fileSize = 0;
	//本次采集生成的数据文件
	private String[] fileArr;

	//TODO 卸数的分隔符应该前台指定
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
	@SuppressWarnings("unchecked")
	@Override
	public StageStatusInfo handleStage() {
		LOGGER.info("------------------数据库直连采集卸数阶段开始------------------");
		//1、创建卸数阶段状态信息，更新作业ID,阶段名，阶段开始时间
		StageStatusInfo statusInfo = new StageStatusInfo();
		JobStatusInfoUtil.startStageStatusInfo(statusInfo, collectTableBean.getTable_id(),
				StageConstant.UNLOADDATA.getCode());
		try {
			//TODO 目前对于同一张表的清洗规则和查询出来的表结构是一样的，未来可能根据不同目的地去实现不同的清洗
			tableBean = CollectTableHandleParse.generateTableInfo(sourceDataConfBean, collectTableBean);
			//2、解析作业信息，得到表名和表数据量
			String tableCount = collectTableBean.getTable_count();
			//fileResult中是生成的所有数据文件的路径，用于判断卸数阶段结果
			List<String> fileResult = new ArrayList<>();
			//pageCountResult是本次采集作业每个线程采集到的数据量，用于写meta文件
			List<Long> pageCountResult = new ArrayList<>();
			//3、从配置文件中读取并行抽取线程数
			//TODO 目前是在配置文件中配置线程数，后期，如果用户在页面上设置了并行抽取，那么应该使用用户配置的线程数
			int threadCount = Integer.parseInt(PropertyParaUtil.getString("threadCount", "15"));
			long totalCount = Long.parseLong(tableCount);
			long pageRow = totalCount / threadCount;
			//4、创建固定大小的线程池，执行分页查询(线程池类型和线程数可以后续改造)
			// 此处不会有海量的任务需要执行，不会出现队列中等待的任务对象过多的OOM事件。
			ExecutorService executorService = Executors.newFixedThreadPool(threadCount + 1);
			List<Future<Map<String, Object>>> futures = new ArrayList<>();
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
			long lastPageEnd = Long.MAX_VALUE;
			CollectPage lastPage = new CollectPage(sourceDataConfBean, collectTableBean, tableBean,
					lastPageStart, lastPageEnd, threadCount + 1, pageRow);
			Future<Map<String, Object>> lastFuture = executorService.submit(lastPage);
			futures.add(lastFuture);
			//关闭线程池
			executorService.shutdown();
			while (!executorService.awaitTermination(100, TimeUnit.MILLISECONDS)) {
				LOGGER.info("线程池正在关闭");
			}
			//5、获得结果,用于校验多线程采集的结果和写Meta文件
			for (Future<Map<String, Object>> future : futures) {
				fileResult.addAll((List<String>) future.get().get("filePathList"));
				pageCountResult.add((Long) future.get().get("pageCount"));
			}
			fileArr = (String[]) fileResult.toArray();
			//获得列类型
			columnTypes.addAll(Arrays.asList(tableBean.getAllType().split(CollectTableHandleParse.STRSPLIT)));
			//获得本次采集总数据量
			for (Long pageCount : pageCountResult) {
				rowCount += pageCount;
			}
			//获得本次采集生成的数据文件的总大小
			for (String filePath : fileResult) {
				//判断文件是否存在，如果某个文件存在，则计算大小，若不存在，记录日志并继续运行
				if (FileUtil.decideFileExist(filePath)) {
					long singleFileSize = FileUtil.getFileSize(filePath);
					fileSize += singleFileSize;
				} else {
					throw new AppSystemException("数据库直连采集" + filePath + "文件不存在");
				}
			}
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.SUCCEED.getCode(), "执行成功");
			LOGGER.info("------------------数据库直连采集卸数阶段成功------------------");
		} catch (Exception e) {
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.FAILED.getCode(), e.getMessage());
			LOGGER.error("数据库直连采集卸数阶段失败：", e.getMessage());
		}
		return statusInfo;
	}

	@Method(desc = "获取本次数据库采集单张表的mate信息",
			logicStep = "1、直接返回成员变量tableBean")
	@Return(desc = "数据库采集单张表的mate信息",
			range = "不会为null")
	public TableBean getTableBean() {
		return tableBean;
	}

	@Method(desc = "获取数据列类型，用于写meta文件", logicStep = "1、直接返回成员变量columnTypes")
	@Return(desc = "当前采集表所有列的列类型", range = "不会为null")
	public List<String> getColumnTypes() {
		return this.columnTypes;
	}

	@Method(desc = "获取本次数据库直连采集作业采集到的数据总条数，用于写meta文件",
			logicStep = "1、直接返回成员变量rowCount")
	@Return(desc = "当前作业采集数据量(一张表一个作业，作业内部使用多线程对表数据进行采集)", range = "不限")
	public long getRowCount() {
		return rowCount;
	}

	@Method(desc = "获取本次数据库直连采集作业采集卸数后生成的数据文件总大小，用于写meta文件",
			logicStep = "1、直接返回成员变量fileSize")
	@Return(desc = "多线程卸数落地数据文件的文件总大小", range = "不限，单位是字节")
	public long getFileSize() {
		return fileSize;
	}

	@Method(desc = "获取本次数据库直连采集作业采集卸数后生成的数据文件的路径，用于上传HDFS",
			logicStep = "1、直接返回成员变成fileArr")
	@Return(desc = "多线程采集，每个线程写一个数据文件，多线程采集最终会有多个文件，用数组存放多个文件的路径",
			range = "不会为null")
	public String[] getFileArr() {
		return fileArr;
	}
}
