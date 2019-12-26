package hrds.agent.job.biz.core.dbstage;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import hrds.agent.job.biz.bean.*;
import hrds.agent.job.biz.constant.RunStatusConstant;
import hrds.agent.job.biz.constant.StageConstant;
import hrds.agent.job.biz.core.AbstractJobStage;
import hrds.agent.job.biz.core.dbstage.service.ReadFileToDataBase;
import hrds.commons.codes.Store_type;
import hrds.commons.exception.AppSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@DocClass(desc = "数据库直连采集数据上传阶段", author = "WangZhengcheng")
public class DBUploadStageImpl extends AbstractJobStage {
	private final static Logger LOGGER = LoggerFactory.getLogger(DBUploadStageImpl.class);
	//卸数到本地的文件绝对路径
	private final String[] localFiles;
	//数据采集表对应的存储的所有信息
	private CollectTableBean collectTableBean;
	//数据库采集表对应的meta信息
	private TableBean tableBean;

	public DBUploadStageImpl(TableBean tableBean, CollectTableBean collectTableBean, String[] localFiles) {
		this.collectTableBean = collectTableBean;
		this.localFiles = localFiles;
		this.tableBean = tableBean;
	}

	@Method(desc = "数据库直连采集数据上传阶段处理逻辑，处理完成后，无论成功还是失败，" +
			"将相关状态信息封装到StageStatusInfo对象中返回", logicStep = "" +
			"1、创建卸数阶段状态信息，更新作业ID,阶段名，阶段开始时间" +
			"2、调用方法，进行文件上传，文件数组和上传目录由构造器传入")
	@Return(desc = "StageStatusInfo是保存每个阶段状态信息的实体类", range = "不会为null,StageStatusInfo实体类对象")
	@Override
	public StageStatusInfo handleStage() {
		LOGGER.info("------------------数据库直连采集上传阶段开始------------------");
		//1、创建卸数阶段状态信息，更新作业ID,阶段名，阶段开始时间
		StageStatusInfo statusInfo = new StageStatusInfo();
		statusInfo.setStageNameCode(StageConstant.UPLOAD.getCode());
		statusInfo.setJobId(collectTableBean.getTable_id());
		statusInfo.setStartDate(DateUtil.getSysDate());
		statusInfo.setStartTime(DateUtil.getSysTime());
		try {
			List<DataStoreConfBean> dataStoreConfBeanList = collectTableBean.getDataStoreConfBean();
			for (DataStoreConfBean dataStoreConfBean : dataStoreConfBeanList) {
				//此处不会有海量的任务需要执行，不会出现队列中等待的任务对象过多的OOM事件。
				//TODO Runtime.getRuntime().availableProcessors()此处不能用这个,因为可能同时又多个数据库采集同时进行
				//这里多个文件，使用多线程读取，进外部数据库。
				ExecutorService executor = Executors.newFixedThreadPool(5);
				long count = 0;
				List<Future<Long>> list = new ArrayList<>();
				//根据存储类型上传到目的地
				if (Store_type.DATABASE.getCode().equals(dataStoreConfBean.getStore_type())) {
					//数据库类型
					for (String fileAbsolutePath : localFiles) {
						ReadFileToDataBase readFileToDataBase = new ReadFileToDataBase(fileAbsolutePath, tableBean,
								collectTableBean, dataStoreConfBean);
						//TODO 这个状态是不是可以在这里
						Future<Long> submit = executor.submit(readFileToDataBase);
						list.add(submit);
					}
				} else if (Store_type.HBASE.getCode().equals(dataStoreConfBean.getStore_type())) {

				} else if (Store_type.SOLR.getCode().equals(dataStoreConfBean.getStore_type())) {

				} else if (Store_type.ElasticSearch.getCode().equals(dataStoreConfBean.getStore_type())) {

				} else if (Store_type.MONGODB.getCode().equals(dataStoreConfBean.getStore_type())) {

				}else{
					//TODO 上面的待补充。
					throw new AppSystemException("不支持的存储类型");
				}
				for (Future<Long> future : list) {
					count += future.get();
				}
				LOGGER.info("数据成功进入库" + dataStoreConfBean.getDsl_name() + "下的表" + collectTableBean.getHbase_name()
						+ ",总计进数" + count + "条");
			}
			//2、调用方法，进行文件上传，文件数组和上传目录由构造器传入
//			executor.executeUpload2Hdfs(localFiles, remoteDir);
		} catch (Exception e) {
			statusInfo.setStatusCode(RunStatusConstant.FAILED.getCode());
			statusInfo.setMessage(FAILD_MSG + "：" + e.getMessage());
			LOGGER.info("------------------数据库直连采集上传阶段失败------------------");
			LOGGER.error(FAILD_MSG + "：{}", e.getMessage());
		}
		statusInfo.setStatusCode(RunStatusConstant.SUCCEED.getCode());
		statusInfo.setEndDate(DateUtil.getSysDate());
		statusInfo.setStartTime(DateUtil.getSysTime());
		LOGGER.info("------------------数据库直连采集上传阶段成功------------------");
		return statusInfo;
	}
}
