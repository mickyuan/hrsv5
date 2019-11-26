package hrds.agent.job.biz.core.filecollectstage;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.FileNameUtils;
import hrds.agent.job.biz.bean.FileCollectParamBean;
import hrds.agent.job.biz.bean.StageStatusInfo;
import hrds.agent.job.biz.constant.RunStatusConstant;
import hrds.agent.job.biz.constant.StageConstant;
import hrds.agent.job.biz.core.AbstractJobStage;
import hrds.agent.job.biz.core.filecollectstage.methods.AvroOper;
import hrds.agent.job.biz.core.filecollectstage.methods.CollectionWatcher;
import hrds.commons.utils.MapDBHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@DocClass(desc = "文件采集卸数程序主类", author = "zxz", createdate = "2019/10/30 16:38")
public class FileCollectUnloadDataStageImpl extends AbstractJobStage {
	//打印日志
	private static final Log log = LogFactory.getLog(FileCollectUnloadDataStageImpl.class);
	//新增的需要做文件采集的文件
	private List<String> newFile;
	//变化的文件
	private List<String> changeFileList;
	//文件采集设置表主键
	private FileCollectParamBean fileCollectParamBean;
	//mapDB表对象
	private ConcurrentMap<String, String> fileNameHTreeMap;
	//mapDB操作对象
	private MapDBHelper mapDBHelper;
	//XXX 文件采集卸数到本地所在的顶层目录
	private static final String FOLDER = System.getProperty("user.dir") + File.separator + "DIRFile";

	/**
	 * 非结构化文件采集的作业实现类构造方法.
	 *
	 * @param fileCollectParamBean FileCollectParamBean
	 *                             含义：文件采集需要的参数实体bean
	 *                             取值范围：所有这张表不能为空的字段的值必须有，为空则会抛异常
	 * @param newFile              List<String>
	 *                             含义：新增文件全路径的集合
	 *                             取值范围：可能为空
	 * @param changeFileList       List<String>
	 *                             含义：变化文件全路径的合集
	 *                             取值范围：可能为空
	 */
	public FileCollectUnloadDataStageImpl(FileCollectParamBean fileCollectParamBean, List<String> newFile,
	                                      List<String> changeFileList, ConcurrentMap<String, String> fileNameHTreeMap,
	                                      MapDBHelper mapDBHelper) {
		this.newFile = newFile;
		this.changeFileList = changeFileList;
		this.fileCollectParamBean = fileCollectParamBean;
		this.fileNameHTreeMap = fileNameHTreeMap;
		this.mapDBHelper = mapDBHelper;
	}


	@Override
	public StageStatusInfo handleStage() {
		//1.设置数据加载阶段的任务id,开始日期开始时间
		long startTime = System.currentTimeMillis();
		log.info("开始采集" + fileCollectParamBean.getFile_source_path() + "下文件");
		StageStatusInfo statusInfo = new StageStatusInfo();
		statusInfo.setJobId(String.valueOf(fileCollectParamBean.getFcs_id()));
		statusInfo.setStageNameCode(StageConstant.UNLOADDATA.getCode());
		statusInfo.setStartDate(DateUtil.getSysDate());
		statusInfo.setStartTime(DateUtil.getSysTime());
		//没有新增或者变化的文件，直接返回成功状态
		if (newFile.size() > 0 || changeFileList.size() > 0) {
			fileCollectParamBean.setSysDate(DateUtil.getSysDate());
			fileCollectParamBean.setSysTime(DateUtil.getSysTime());
			AvroOper os = new AvroOper(fileCollectParamBean, fileNameHTreeMap);
			CollectionWatcher collectionWatcher = os.getCollectionWatcher();
			String loadMessage = "";
			ExecutorService executorService = null;
			try {
				//2.文件临时存放目录,卸数到本地的目录
				String unLoadPath = FOLDER + File.separator + fileCollectParamBean.getFcs_id() +
						File.separator + fileCollectParamBean.getFile_source_id() + File.separator
						+ DateUtil.getSysDate() + File.separator;
				//将要存入数据库的路径都要改成一致的Unix分隔符
				unLoadPath = FileNameUtils.normalize(unLoadPath, true);
				fileCollectParamBean.setUnLoadPath(unLoadPath);
				//4.获取作业采集情况信息表id
				//此处不会有海量的任务需要执行，不会出现队列中等待的任务对象过多的OOM事件。
				executorService = Executors.newFixedThreadPool(1);
				FileCollectLoadingDataStageImpl loadingDataStage = new FileCollectLoadingDataStageImpl(
						fileCollectParamBean, fileNameHTreeMap, mapDBHelper);
				Future<String> submit = executorService.submit(loadingDataStage);
				//5.调用卸数主程序,对增量文件和非增量文件进行卸数
				if (changeFileList.size() > 0) {
					os.putAllFiles2Avro(newFile, false, false);
					os.putAllFiles2Avro(changeFileList, true, true);
				} else {
					os.putAllFiles2Avro(newFile, false, true);
				}
				loadMessage = submit.get();
				//程序走到这里意味着文件采集卸数和加载都做完了
				log.info("文件夹" + fileCollectParamBean.getFile_source_path() + "下文件采集结束");
			} catch (Exception e) {
				//6.保存作业错误信息表
				loadMessage += e.getMessage();
				statusInfo.setStatusCode(RunStatusConstant.FAILED.getCode());
				log.error("非结构化对象采集卸数失败", e);
			} finally {
				if (executorService != null)
					executorService.shutdown();
				long collectTotal = newFile.size() + changeFileList.size();
				collectionWatcher.setCollect_total(collectTotal);
				collectionWatcher.setExcuteLength(String.valueOf((System.currentTimeMillis() - startTime) / 1000));
				collectionWatcher.endJob(loadMessage);
			}
		}else{
			log.info("没有变化的文件，执行结束");
		}
		//这里进行结束标识登记
		//7.设置数据加载阶段结束时间，成功还是失败的标识
		statusInfo.setEndDate(DateUtil.getSysDate());
		statusInfo.setEndTime(DateUtil.getSysTime());
		statusInfo.setStatusCode(RunStatusConstant.SUCCEED.getCode());
		return statusInfo;
	}
}
