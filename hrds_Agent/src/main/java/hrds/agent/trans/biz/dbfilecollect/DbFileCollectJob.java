package hrds.agent.trans.biz.dbfilecollect;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.JobStatusInfo;
import hrds.agent.job.biz.bean.SourceDataConfBean;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.core.DataFileJobImpl;
import hrds.agent.job.biz.utils.FileUtil;
import hrds.agent.job.biz.utils.JobStatusInfoUtil;
import hrds.commons.base.AgentBaseAction;
import hrds.commons.utils.Constant;
import hrds.commons.utils.PackUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@DocClass(desc = "db文件采集agent接受发送任务的接口", author = "zxz", createdate = "2020/3/26 9:56")
public class DbFileCollectJob extends AgentBaseAction {
	//打印日志
	private static final Log log = LogFactory.getLog(DbFileCollectJob.class);

	@Method(desc = "Db数据文件采集和前端交互生成配置文件的接口",
			logicStep = "1.对配置信息解压缩并反序列化为SourceDataConfBean对象" +
					"2.将页面传递过来的压缩信息解压写文件")
	@Param(name = "taskInfo", desc = "数据库采集需要的参数实体bean的json对象字符串",
			range = "所有这张表不能为空的字段的值必须有，为空则会抛异常，" +
					"collectTableBeanArray对应的表CollectTableBean这个实体不能为空的字段的值必须有，为空则会抛异常")
	@Return(desc = "执行返回信息", range = "不会为空")
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
			message = "db文件采集生成配置文件失败:" + e.getMessage();
		}
		return message;
	}

	@Method(desc = "Db数据文件采集和前端交互立即执行的接口",
			logicStep = "1.对配置信息解压缩并反序列化为SourceDataConfBean对象" +
					"2.将页面传递过来的压缩信息解压写文件" +
					"3.获取json数组转成CollectTableBean的集合" +
					"4.遍历，设置跑批日期，多线程执行任务" +
					"5.打印每个线程执行情况")
	@Param(name = "etlDate", desc = "跑批日期", range = "不能为空")
	@Param(name = "taskInfo", desc = "数据库采集需要的参数实体bean的json对象字符串",
			range = "所有这张表不能为空的字段的值必须有，为空则会抛异常，" +
					"collectTableBeanArray对应的表CollectTableBean这个实体不能为空的字段的值必须有，为空则会抛异常")
	@Return(desc = "执行返回信息", range = "不会为空")
	public String executeImmediately(String etlDate, String taskInfo) {
		String message = "执行成功";
		ExecutorService executor = null;
		try {
			//1.对配置信息解压缩并反序列化为SourceDataConfBean对象
			SourceDataConfBean sourceDataConfBean =
					JSONObject.parseObject(PackUtil.unpackMsg(taskInfo).get("msg"), SourceDataConfBean.class);
			//2.将页面传递过来的压缩信息解压写文件
			FileUtil.createFile(Constant.MESSAGEFILE + sourceDataConfBean.getDatabase_id(),
					PackUtil.unpackMsg(taskInfo).get("msg"));
			//3.获取json数组转成CollectTableBean的集合
			List<CollectTableBean> collectTableBeanList = sourceDataConfBean.getCollectTableBeanArray();
			executor = Executors.newFixedThreadPool(JobConstant.AVAILABLEPROCESSORS);
			List<Future<JobStatusInfo>> list = new ArrayList<>();
			//4.遍历，设置跑批日期，多线程执行任务
			for (CollectTableBean collectTableBean : collectTableBeanList) {
				//设置跑批日期
				collectTableBean.setEtlDate(etlDate);
				//为了确保多个线程之间的值不互相干涉，复制对象的值。
				SourceDataConfBean sourceDataConfBean1 = JSONObject.parseObject(
						JSONObject.toJSONString(sourceDataConfBean), SourceDataConfBean.class);
				DataFileJobImpl fileCollectJob = new DataFileJobImpl(sourceDataConfBean1, collectTableBean);
				Future<JobStatusInfo> submit = executor.submit(fileCollectJob);
				list.add(submit);
			}
			//5.打印每个线程执行情况
			JobStatusInfoUtil.printJobStatusInfo(list);
		} catch (Exception e) {
			log.error(e);
			message = "执行db文件采集入库任务失败:" + e.getMessage();
//			throw new AppSystemException("执行db文件采集入库任务失败:" + e.getMessage());
		} finally {
			if (executor != null)
				executor.shutdown();
		}
		return message;
	}
}
