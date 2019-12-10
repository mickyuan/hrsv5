package hrds.agent.job.biz.core.filecollectstage;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.FileNameUtils;
import hrds.agent.job.biz.bean.AvroBean;
import hrds.agent.job.biz.bean.FileCollectParamBean;
import hrds.agent.job.biz.core.filecollectstage.methods.AvroBeanProcess;
import hrds.agent.job.biz.core.filecollectstage.methods.AvroOper;
import hrds.agent.trans.biz.unstructuredfilecollect.FileCollectJob;
import hrds.commons.codes.IsFlag;
import hrds.commons.exception.AppSystemException;
import hrds.commons.hadoop.hadoop_helper.HdfsOperator;
import hrds.commons.hadoop.utils.BatchShell;
import hrds.commons.utils.Constant;
import hrds.commons.utils.MapDBHelper;
import hrds.commons.utils.PathUtil;
import hrds.commons.utils.PropertyParaUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.Path;

import java.io.File;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;

@DocClass(desc = "将avro文件读取出来存到大数据平台上", author = "zxz", createdate = "2019/11/5 10:12")
public class FileCollectLoadingDataStageImpl implements Callable<String> {

	private static final Log log = LogFactory.getLog(FileCollectLoadingDataStageImpl.class);
	private FileCollectParamBean fileCollectParamBean;
	//大文件文件夹路径
	private static final String BIGFILENAME = "bigFiles";
	private ConcurrentMap<String, String> fileNameHTreeMap;
	//mapDB操作对象
	private MapDBHelper mapDBHelper;

	FileCollectLoadingDataStageImpl(FileCollectParamBean paramBean,
	                                ConcurrentMap<String, String> fileNameHTreeMap, MapDBHelper mapDBHelper) {
		this.mapDBHelper = mapDBHelper;
		this.fileNameHTreeMap = fileNameHTreeMap;
		this.fileCollectParamBean = paramBean;
		String fileCollectHdfsPath = FileNameUtils.normalize(PropertyParaUtil.getString("pathprefix",
				"/hrds") + File.separator + PathUtil.DCL + File.separator + paramBean.getFcs_id()
				+ File.separator + paramBean.getFile_source_id() + File.separator + BIGFILENAME, true);
		if (Constant.hasHadoopEnv) {
			//创建hdfs文件夹
			try (HdfsOperator operator = new HdfsOperator()) {
				Path path = new Path(fileCollectHdfsPath);
				if (!operator.exists(path)) {
					operator.mkdir(path);
				}
			} catch (Exception e) {
				log.error("检查当前环境是否配置集群客户端", e);
				throw new AppSystemException("初始化文件采集数据加载类失败");
			}
		}
	}

	@Override
	public String call() {
		String message = "";
		log.info("Start FileCollectLoadingDataStageImpl Thread ...");
		ArrayBlockingQueue<String> queue = FileCollectJob.mapQueue.get(fileCollectParamBean.getFile_source_id());
		try {
			while (true) {
				//从队列中拿出信息，当队列中无信息时，该线程阻塞
				String queueMeta = queue.take();
				log.info("queue.size: " + queue.size() + " ; queueMeta: " + queueMeta);
				JSONObject queueJb = JSONObject.parseObject(queueMeta);
				String sysDate = queueJb.getString("sys_date");
				//文件采集的id
				String avroFileAbsolutionPath = queueJb.getString("avroFileAbsolutionPath");
				String fileCollectHdfsPath = queueJb.getString("fileCollectHdfsPath");
				String jobRsId = queueJb.getString("job_rs_id");
				long watcherId = queueJb.getLong("watcher_id");
				/*
				 * 对其做处理（上传hdfs，删除本地，入mysql，solr，hbase）
				 */
				BatchShell.execStationaryHDFSShell(avroFileAbsolutionPath, fileCollectHdfsPath);
				//判断如果是大文件，则只做文件上传处理，大文件的信息记录在其他队列中，这里直接取下一个队列的数据
				if (IsFlag.Shi.getCode().equals(queueJb.getString("isBigFile"))) {
					continue;
				}
				//获取hdfs上的avro文件路径
				Path avroPath = new Path(fileCollectHdfsPath +
						FileNameUtils.getName(avroFileAbsolutionPath));
				//传输文件并入库，入hbase
				log.info("处理  -->" + avroPath.getName());
				//不是hadoop版获取本地的卸数的Avro文件
				if (!Constant.hasHadoopEnv) {
					avroPath = new Path(avroFileAbsolutionPath);
				}
				//获取AvroBeans
				List<AvroBean> avroBeans = AvroOper.getAvroBeans(avroPath);
				AvroBeanProcess abp = new AvroBeanProcess(fileCollectParamBean, sysDate, jobRsId);
				//判断是否入solr
				if (IsFlag.Shi.getCode().equals(fileCollectParamBean.getIs_solr())) {
					abp.saveInSolr(avroBeans);
				}
				//存入Mysql和solr
				List<String[]> hbaseList = abp.saveMetaData(avroBeans, fileNameHTreeMap);
				if (!Constant.hasHadoopEnv) {
					abp.saveInPostgreSupersedeHbase(hbaseList);
				} else {
					//存入HBase
					abp.saveInHbase(hbaseList);
				}
				//保存到mapDB
				saveInMapDB(avroBeans);
				//处理完后 删除本地文件
				boolean delete = FileUtils.getFile(avroFileAbsolutionPath).delete();
				if (!delete) {
					log.error("删除本地文件" + avroFileAbsolutionPath + "失败！");
				}
				//值为0代表当前拿到的值是队列最后一个,退出循环
				if (watcherId == AvroOper.LASTELEMENT) {
					log.info("End FileCollectLoadingDataStageImpl Thread ...");
					break;
				}
			}
		} catch (Exception e) {
			log.error("Failed to process Avro file in hdfs FileSystem...", e);
			message = e.getMessage();
		}
		return message;
	}

	/**
	 * 保存文件信息到mapDB
	 */
	private void saveInMapDB(List<AvroBean> avroBeans) {
		for (AvroBean bean : avroBeans) {
			JSONObject object = new JSONObject();
			object.put("uuid", bean.getUuid());
			object.put("file_md5", bean.getFile_md5());
			fileNameHTreeMap.put(bean.getFile_scr_path(), object.toJSONString());
		}
		mapDBHelper.commit();
		log.info("提交到mapDB");
	}
}