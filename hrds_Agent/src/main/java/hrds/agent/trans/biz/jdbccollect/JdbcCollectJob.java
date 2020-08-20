package hrds.agent.trans.biz.jdbccollect;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.utils.StringUtil;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.JobStatusInfo;
import hrds.agent.job.biz.bean.SourceDataConfBean;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.core.DataBaseJobImpl;
import hrds.agent.job.biz.core.metaparse.CollectTableHandleFactory;
import hrds.agent.job.biz.utils.DataExtractUtil;
import hrds.agent.job.biz.utils.FileUtil;
import hrds.agent.job.biz.utils.JobStatusInfoUtil;
import hrds.commons.base.AgentBaseAction;
import hrds.commons.entity.Data_extraction_def;
import hrds.commons.utils.Constant;
import hrds.commons.utils.PackUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@DocClass(desc = "数据库抽取接收消息接口", author = "zxz", createdate = "2019/12/2 10:35")
public class JdbcCollectJob extends AgentBaseAction {
	//打印日志
	private static final Log log = LogFactory.getLog(JdbcCollectJob.class);

	@Method(desc = "数据库抽取和前端交互生成页面配置到agent所在目录的接口",
			logicStep = "1.对配置信息解压缩并反序列化为SourceDataConfBean对象" +
					"2.将页面传递过来的压缩信息解压写文件")
	@Param(name = "taskInfo", desc = "数据库采集需要的参数实体bean的json对象字符串",
			range = "所有sourceDataConfBean表不能为空的字段的值必须有，为空则会抛异常，" +
					"collectTableBeanArray对应的表CollectTableBean这个实体不能为空的字段的值必须有，为空则会抛异常")
	public String execute(String taskInfo) {
		String message = "执行成功";
		try {
			//1.对配置信息解压缩并反序列化为SourceDataConfBean对象
			SourceDataConfBean sourceDataConfBean =
					JSONObject.parseObject(PackUtil.unpackMsg(taskInfo).get("msg"), SourceDataConfBean.class);
			//2.将页面传递过来的压缩信息解压写文件
			FileUtil.createFile(Constant.MESSAGEFILE + sourceDataConfBean.getDatabase_id(),
					PackUtil.unpackMsg(taskInfo).get("msg"));
		} catch (Exception e) {
			log.error(e);
			message = "数据库抽取生成配置文件失败:" + e.getMessage();
		}
		return message;
	}

	@Method(desc = "数据库抽取和前端交互立即执行的接口",
			logicStep = "1.对配置信息解压缩并反序列化为SourceDataConfBean对象" +
					"2.将页面传递过来的压缩信息解压写文件" +
					"3.初始化当前任务需要保存的文件的根目录" +
					"4.获取json数组转成CollectTableBean的集合" +
					"5.遍历CollectTableBean的集合，遍历落地文件格式，设置跑批日期，当前线程采集的文件格式" +
					"6.打印每个线程执行情况")
	@Param(name = "etlDate", desc = "跑批日期", range = "不能为空")
	@Param(name = "taskInfo", desc = "数据库采集需要的参数实体bean的json对象字符串",
			range = "所有sourceDataConfBean表不能为空的字段的值必须有，为空则会抛异常，" +
					"collectTableBeanArray对应的表CollectTableBean这个实体不能为空的字段的值必须有，为空则会抛异常")
	@Param(name = "sqlParam", desc = "参数占位符", range = "可以为空", nullable = true)
	public String executeImmediately(String etlDate, String taskInfo, String sqlParam) {
		String message = "执行成功";
		ExecutorService executor = null;
		try {
			//1.对配置信息解压缩并反序列化为SourceDataConfBean对象
			SourceDataConfBean sourceDataConfBean =
					JSONObject.parseObject(PackUtil.unpackMsg(taskInfo).get("msg"), SourceDataConfBean.class);
			//2.将页面传递过来的压缩信息解压写文件
			FileUtil.createFile(Constant.MESSAGEFILE + sourceDataConfBean.getDatabase_id(),
					PackUtil.unpackMsg(taskInfo).get("msg"));
			//3.初始化当前任务需要保存的文件的根目录
			String[] paths = {Constant.DICTIONARY};
			FileUtil.initPath(paths);
			//4.获取json数组转成CollectTableBean的集合
			List<CollectTableBean> collectTableBeanList = sourceDataConfBean.getCollectTableBeanArray();
			//此处不会有海量的任务需要执行，不会出现队列中等待的任务对象过多的OOM事件。
			executor = Executors.newFixedThreadPool(JobConstant.AVAILABLEPROCESSORS);
			List<Future<JobStatusInfo>> list = new ArrayList<>();
			//5.遍历CollectTableBean的集合，遍历落地文件格式，设置跑批日期，当前线程采集的文件格式
			for (CollectTableBean collectTableBean : collectTableBeanList) {
				List<Data_extraction_def> data_extraction_def_list = collectTableBean.getData_extraction_def_list();
				for (Data_extraction_def data_extraction_def : data_extraction_def_list) {
					collectTableBean.setEtlDate(etlDate);
					//设置sql占位符参数
					if (!StringUtil.isBlank(sqlParam)) {
						collectTableBean.setSqlParam(sqlParam);
					}
					collectTableBean.setSelectFileFormat(data_extraction_def.getDbfile_format());
					//为了确保多个线程之间的值不互相干涉，复制对象的值。
					SourceDataConfBean sourceDataConfBean1 = JSONObject.parseObject(
							JSONObject.toJSONString(sourceDataConfBean), SourceDataConfBean.class);
					CollectTableBean collectTableBean1 = JSONObject.parseObject(
							JSONObject.toJSONString(collectTableBean), CollectTableBean.class);
					//多线程执行
					DataBaseJobImpl fileCollectJob = new DataBaseJobImpl(sourceDataConfBean1, collectTableBean1);
					Future<JobStatusInfo> submit = executor.submit(fileCollectJob);
					list.add(submit);
				}
			}
			//6.打印每个线程执行情况
			JobStatusInfoUtil.printJobStatusInfo(list);
		} catch (Exception e) {
			log.error(e);
			message = "执行数据库抽取生成文件任务失败:" + e.getMessage();
//            throw new AppSystemException("执行数据库抽取任务失败:" + e.getMessage());
		} finally {
			if (executor != null)
				executor.shutdown();
		}
		return message;
	}

	@Method(desc = "数据库抽取和前端交互生成数据字典的接口",
			logicStep = "1.对配置信息解压缩并反序列化为SourceDataConfBean对象" +
					"2.将页面传递过来的压缩信息解压写文件" +
					"3.获取json数组转成CollectTableBean的集合" +
					"4.遍历CollectTableBean的集合" +
					"5.获取需要采集的表的meta信息" +
					"6.将数据字典信息解析，并返回")
	@Param(name = "taskInfo", desc = "数据库采集需要的参数实体bean的json对象字符串",
			range = "所有sourceDataConfBean表不能为空的字段的值必须有，为空则会抛异常，" +
					"collectTableBeanArray对应的表CollectTableBean这个实体不能为空的字段的值必须有，为空则会抛异常")
	@Param(name = "sqlParam", desc = "参数占位符", range = "可以为空", nullable = true)
	public String getDictionaryJson(String taskInfo, String sqlParam) {
		//1.对配置信息解压缩并反序列化为SourceDataConfBean对象
		SourceDataConfBean sourceDataConfBean =
				JSONObject.parseObject(PackUtil.unpackMsg(taskInfo).get("msg"), SourceDataConfBean.class);
		//2.将页面传递过来的压缩信息解压写文件
		FileUtil.createFile(Constant.MESSAGEFILE + sourceDataConfBean.getDatabase_id(),
				PackUtil.unpackMsg(taskInfo).get("msg"));
		String dd_data = "";
		//3.获取json数组转成CollectTableBean的集合
		List<CollectTableBean> collectTableBeanList = sourceDataConfBean.getCollectTableBeanArray();
		//4.遍历CollectTableBean的集合
		for (CollectTableBean collectTableBean : collectTableBeanList) {
			//设置sql占位符参数
			if (!StringUtil.isBlank(sqlParam)) {
				collectTableBean.setSqlParam(sqlParam);
			}
			//5.获取需要采集的表的meta信息
			TableBean tableBean = CollectTableHandleFactory.getCollectTableHandleInstance(sourceDataConfBean)
					.generateTableInfo(sourceDataConfBean, collectTableBean);
			//6.将数据字典信息解析，并返回
			dd_data = DataExtractUtil.parseJsonDictionary(dd_data, collectTableBean.getTable_name(),
					tableBean.getColumnMetaInfo(), tableBean.getColTypeMetaInfo(),
					collectTableBean.getTransSeparatorExtractionList(), collectTableBean.getUnload_type(),
					tableBean.getPrimaryKeyInfo(), tableBean.getInsertColumnInfo(), tableBean.getUpdateColumnInfo()
					, tableBean.getDeleteColumnInfo(), collectTableBean.getHbase_name());
		}
		return dd_data;
	}
}
