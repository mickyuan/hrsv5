package hrds.agent.job.biz.core.dbstage;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
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
			"3、根据列名和表名获得采集SQL" +
			"4、使用工厂模式获得数据库方言策略" +
			"5、根据采集线程数，计算每个任务的采集数量" +
			"6、构建线程对象CollectPage，放入线程池执行" +
			"7、获得结果,用于校验多线程采集的结果和写Meta文件")
	@Return(desc = "StageStatusInfo是保存每个阶段状态信息的实体类", range = "不会为null，StageStatusInfo实体类对象")
	@SuppressWarnings("unchecked")
	@Override
	public StageStatusInfo handleStage()
			throws Exception {
		LOGGER.info("------------------数据库直连采集卸数阶段开始------------------");
		//1、创建卸数阶段状态信息，更新作业ID,阶段名，阶段开始时间
		StageStatusInfo statusInfo = new StageStatusInfo();
		statusInfo.setJobId(collectTableBean.getTable_id());
		statusInfo.setStageNameCode(StageConstant.UNLOADDATA.getCode());
		statusInfo.setStartDate(DateUtil.getSysDate());
		statusInfo.setStartTime(DateUtil.getSysTime());
		//TODO 目前对于同一张表的清洗规则和查询出来的表结构是一样的，未来可能根据不同目的地去实现不同的清洗
		tableBean = CollectTableHandleParse.generateTableInfo(sourceDataConfBean, collectTableBean);
		//2、解析作业信息，得到表名和表数据量
		String tableCount = collectTableBean.getTable_count();
		//获得用户提供的用于分页的列
		//TODO 这里分页需要用到的字段目前没用
//		String pageColumn = jobInfo.getPageColumn();
		//4、使用工厂模式获得数据库方言策略
//		DialectStrategyFactory factory = DialectStrategyFactory.getInstance();
//		DataBaseDialectStrategy strategy =
//				factory.createDialectStrategy(dbConfigBean.getDatabase_type());
		//fileResult中是生成的所有数据文件的路径，用于判断卸数阶段结果
		List<String> fileResult = new ArrayList<>();
		//RSResult中是所有分页查询得到的ResultSet，用于写meta文件
//		List<ResultSet> RSResult = new ArrayList<>();
		//pageCountResult是本次采集作业每个线程采集到的数据量，用于写meta文件
		List<Long> pageCountResult = new ArrayList<>();
		//5、16+1个线程采集。计算每个任务的采集数量,得到start,end,然后封装成CollectTask对象,目前写死，后期可以放在yml文件中,根据服务器实际情况，修改配置
		//TODO 讨论如何配置线程，海云现在是根据页面预估数+后台配置每个线程数据量，算出来需要多少个线程。也可以改成页面配置线程数，后台分配每个线程的数据量
		int threadCount = Integer.parseInt(PropertyParaUtil.getString("threadCount", "15"));
//		if (pageColumn == null || pageColumn.trim().isEmpty()) {
//			//TODO 用户若未提供该张表用于分页的数据列，后续操作待讨论
//		} else {
		//用户提供了该张表用于分页的数据列
		long totalCount = Long.parseLong(tableCount);
		long pageRow = totalCount / threadCount;
		//6、创建固定大小的线程池，执行分页查询(线程池类型和线程数可以后续改造)
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
		//最后一个线程的最大条数设为Integer.MAX_VALUE
		long lastPageEnd = Long.MAX_VALUE;
		CollectPage lastPage = new CollectPage(sourceDataConfBean, collectTableBean, tableBean,
				lastPageStart, lastPageEnd, threadCount + 1, pageRow);
		Future<Map<String, Object>> lastFuture = executorService.submit(lastPage);
		futures.add(lastFuture);
		//关闭线程池
		executorService.shutdown();
		while (!executorService.awaitTermination(100, TimeUnit.MILLISECONDS)) {
			System.out.println("线程池正在关闭");
		}
		//7、获得结果,用于校验多线程采集的结果和写Meta文件
		for (Future<Map<String, Object>> future : futures) {
			fileResult.addAll((List<String>) future.get().get("filePathList"));
//			RSResult.add((ResultSet) future.get().get("pageData"));
			pageCountResult.add((Long) future.get().get("pageCount"));
		}
//		}

		//获得列类型
		columnTypes.addAll(Arrays.asList(tableBean.getAllType().toString().split(CollectTableHandleParse.STRSPLIT)));
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
				LOGGER.error("数据库直连采集" + filePath + "文件不存在");
			}
		}

		//判断本次卸数阶段是否成功
		if (!(fileResult.isEmpty())) {
			for (String filePath : fileResult) {
				if (!FileUtil.decideFileExist(filePath)) {
					//如果某个数据文件在指定的目录下不存在，则卸数阶段失败
					statusInfo.setStatusCode(RunStatusConstant.FAILED.getCode());
					//设置错误信息
					statusInfo.setMessage("路径为 : " + filePath + "的数据文件不存在");
					//设置结束时间
					statusInfo.setEndDate(DateUtil.getSysDate());
					statusInfo.setEndTime(DateUtil.getSysTime());
					//记录日志
					LOGGER.info("------------------数据库直连采集卸数阶段失败------------------");
					return statusInfo;
				}
			}
			statusInfo.setStatusCode(RunStatusConstant.SUCCEED.getCode());
			statusInfo.setEndDate(DateUtil.getSysDate());
			statusInfo.setEndTime(DateUtil.getSysTime());
			fileArr = new String[fileResult.size()];
			fileResult.toArray(fileArr);
			//记录日志
			LOGGER.info("------------------数据库直连采集卸数阶段成功------------------");
			return statusInfo;
		} else {
			statusInfo.setStatusCode(RunStatusConstant.FAILED.getCode());
			statusInfo.setMessage("数据文件全部缺失");
			statusInfo.setEndDate(DateUtil.getSysDate());
			statusInfo.setEndTime(DateUtil.getSysTime());
			//记录日志
			LOGGER.info("------------------数据库直连采集卸数阶段失败------------------");
			return statusInfo;
		}
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

	@Method(desc = "获得列类型，对于像varchar这种有列长度的类型，只保留类型，不保留长度/精度", logicStep = "" +
			"1、如果长度为0，则不做任何处理" +
			"2、如果长度不为0，则只保留数据类型，不保留长度")
	@Param(name = "columnTypeName", desc = "列类型", range = "不为空，格式：列类型(长度)/列类型")
	@Param(name = "precision", desc = "对于数字类型，precision表示的是数字的精度，对于字符类型，这里表示的是长度"
			, range = "不限")
	@Return(desc = "只保留类型，不保留长度/精度", range = "不会为null")
	private String getColumnType(String columnTypeName, int precision) {
		//1、如果长度为0，则不做任何处理
		//2、如果长度不为0，则只保留数据类型，不保留长度
		if (precision != 0) {
			int index = columnTypeName.indexOf("(");
			if (index != -1) {
				columnTypeName = columnTypeName.substring(0, index);
			}
		}
		return columnTypeName;
	}

}
