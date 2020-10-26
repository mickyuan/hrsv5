package hrds.agent.trans.biz.semistructured;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import hrds.agent.job.biz.bean.JobStatusInfo;
import hrds.agent.job.biz.bean.ObjectCollectParamBean;
import hrds.agent.job.biz.bean.ObjectTableBean;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.core.ObjectCollectJobImpl;
import hrds.agent.job.biz.utils.FileUtil;
import hrds.agent.job.biz.utils.JobStatusInfoUtil;
import hrds.commons.base.AgentBaseAction;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.BeanUtils;
import hrds.commons.utils.ConnUtil;
import hrds.commons.utils.PackUtil;
import hrds.commons.utils.xlstoxml.Xls2xml;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@DocClass(desc = "接收页面定义的参数执行object采集", author = "zxz", createdate = "2019/10/23 16:29")
public class ObjectCollectJob extends AgentBaseAction {
	//打印日志
	private static final Logger log = LogManager.getLogger();
	private static final Type TYPE = new TypeReference<Map<String, Object>>() {
	}.getType();

	@Method(desc = "半结构化对象采集和前端交互生成配置文件的接口", logicStep = "" +
			"1.将页面传递过来的压缩信息解压写文件")
	@Param(name = "taskInfo", desc = "半结构化对象采集需要的参数实体bean的json对象字符串", range = "不能为空")
	@Return(desc = "执行返回信息", range = "不会为空")
	public String execute(String taskInfo) {
		String message = "执行成功";
		try {
			//1.对配置信息解压缩并反序列化为ObjectCollectParamBean对象
			ObjectCollectParamBean objectCollectParamBean =
					JSONObject.parseObject(PackUtil.unpackMsg(taskInfo).get("msg"), ObjectCollectParamBean.class);
			//2.将页面传递过来的压缩信息解压写文件
			FileUtil.createFile(JobConstant.MESSAGEFILE + objectCollectParamBean.getOdc_id(),
					PackUtil.unpackMsg(taskInfo).get("msg"));
		} catch (Exception e) {
			log.error(e);
			message = "对象采集生成配置文件失败:" + e.getMessage();
		}
		return message;
	}

	@Method(desc = "半结构化对象采集和前端交互立即执行的接口", logicStep = "" +
			"1.将页面传递过来的压缩信息解压写文件")
	@Param(name = "etlDate", desc = "跑批日期", range = "不能为空")
	@Param(name = "taskInfo", desc = "半结构化对象采集需要的参数实体bean的json对象字符串", range = "不能为空")
	@Return(desc = "执行返回信息", range = "不会为空")
	public String executeImmediately(String etlDate, String taskInfo) {
		String message = "执行成功";
		ExecutorService executor = null;
		try {
			//1.对配置信息解压缩并反序列化为ObjectCollectParamBean对象
			ObjectCollectParamBean objectCollectParamBean =
					JSONObject.parseObject(PackUtil.unpackMsg(taskInfo).get("msg"), ObjectCollectParamBean.class);
			//2.将页面传递过来的压缩信息解压写文件
			FileUtil.createFile(JobConstant.MESSAGEFILE + objectCollectParamBean.getOdc_id(),
					PackUtil.unpackMsg(taskInfo).get("msg"));
			//3.获取json数组转成CollectTableBean的集合
			List<ObjectTableBean> objectTableBeanList = objectCollectParamBean.getObjectTableBeanList();
			executor = Executors.newFixedThreadPool(JobConstant.AVAILABLEPROCESSORS);
			List<Future<JobStatusInfo>> list = new ArrayList<>();
			//4.遍历，设置跑批日期，多线程执行任务
			for (ObjectTableBean objectTableBean : objectTableBeanList) {
				//设置跑批日期
				objectTableBean.setEtlDate(etlDate);
				//设置sql占位符参数
				//为了确保多个线程之间的值不互相干涉，复制对象的值。
				ObjectCollectParamBean objectCollectParamBean1 = new ObjectCollectParamBean();
				BeanUtils.copyProperties(objectCollectParamBean, objectCollectParamBean1);
				ObjectCollectJobImpl objectCollectJob = new ObjectCollectJobImpl(objectCollectParamBean1, objectTableBean);
				Future<JobStatusInfo> submit = executor.submit(objectCollectJob);
				list.add(submit);
			}
			//5.打印每个线程执行情况
			JobStatusInfoUtil.printJobStatusInfo(list);
		} catch (Exception e) {
			log.error(e);
			message = "执行对象采集入库任务失败:" + e.getMessage();
		} finally {
			if (executor != null)
				executor.shutdown();
		}
		return message;
	}

	@Method(desc = "有数据字典时解析半结构化采集数据字典获取表数据",
			logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
					"2.获取生成xml文件文件名" +
					"3.有数据字典写xml文件获取数据字典数据" +
					"5.返回解析后的数据字典所有表数据")
	@Param(name = "file_path", desc = "文件存储路径", range = "不为空")
	@Return(desc = "返回解析后的数据文件数据", range = "不能为空")
	public String getDicTable(String file_path) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		// 2.获取生成xml文件文件名
		String xmlName = ConnUtil.getDataBaseFile("", "", file_path, "");
		// 3.有数据字典写xml文件获取数据字典数据
		Xls2xml.toXml2(file_path, xmlName);
		List<Map<String, String>> dicTable = ConnUtil.getDicTable(xmlName);
		// 4.返回解析后的数据字典所有表数据
		return PackUtil.packMsg(JsonUtil.toJson(dicTable));
	}

	@Method(desc = "没有数据字典解析半结构化采集数据字典获取表数据",
			logicStep = "1.数据字典不存在，获取当前日期下的数据文件数据" +
					"2.返回解析后的数据字典所有表数据")
	@Param(name = "file_path", desc = "文件存储路径", range = "不为空")
	@Param(name = "data_date", desc = "数据日期", range = "是否存在数据字典选择否的时候必选", nullable = true)
	@Param(name = "file_suffix", desc = "文件后缀名", range = "无限制")
	@Return(desc = "返回解析后的数据文件数据", range = "不能为空")
	public String getFirstLineData(String file_path, String file_suffix, String data_date) {
		// 1.数据字典不存在，获取当前日期下的数据文件数据
		List<Map<String, String>> tableByNoDictionary = ConnUtil.getTableByNoDictionary(file_path, data_date, file_suffix);
		// 2.返回解析后的数据字典所有表数据
		return PackUtil.packMsg(JsonUtil.toJson(tableByNoDictionary));
	}

	@Method(desc = "解析半结构化采集数据字典获取表数据",
			logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
					"2.获取生成xml文件文件名" +
					"3.有数据字典写xml文件获取数据字典数据" +
					"4.返回解析后的所有数据字典表对应列数据")
	@Param(name = "file_path", desc = "文件存储路径", range = "不为空")
	@Return(desc = "返回解析后的数据文件数据", range = "不能为空")
	public String getAllDicColumns(String file_path) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		// 2.获取生成xml文件文件名
		String xmlName = ConnUtil.getDataBaseFile("", "", file_path, "");
		// 3.有数据字典写xml文件获取数据字典数据
		Xls2xml.toXml2(file_path, xmlName);
		// 4.返回解析后的所有数据字典表对应列数据
		return PackUtil.packMsg(JsonUtil.toJson(ConnUtil.getColumnByXml2(xmlName)));
	}

	@Method(desc = "解析半结构化采集数据字典获取表数据",
			logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
					"2.获取生成xml文件文件名" +
					"3.有数据字典写xml文件获取数据字典数据" +
					"4.返回有数据字典时的数据处理方式数据")
	@Param(name = "file_path", desc = "文件存储路径", range = "不为空")
	@Return(desc = "返回解析后的数据文件数据", range = "不能为空")
	public String getAllHandleType(String file_path) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		// 2.获取生成xml文件文件名
		String xmlName = ConnUtil.getDataBaseFile("", "", file_path, "");
		// 3.有数据字典写xml文件获取数据字典数据
		Xls2xml.toXml2(file_path, xmlName);
		// 4.返回有数据字典时的数据处理方式数据
		return PackUtil.packMsg(JsonUtil.toJson(ConnUtil.getAllHandleType(xmlName)));
	}

	@Method(desc = "获取解包后半结构化采集与http交互参数",
			logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
					"2.解包" +
					"3.返回解包后的半结构化采集与http交互参数")
	@Param(name = "objectCollectParam", desc = "半结构化采集参数", range = "不为空")
	@Return(desc = "返回解包后的半结构化采集与http交互参数", range = "不为空")
	private Map<String, String> getDictionaryParam(String jsonParam) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		// 2.解包
		Map<String, String> unpackMsg = PackUtil.unpackMsg(jsonParam);
		// 3.返回解包后的半结构化采集与http交互参数
		return JsonUtil.toObject(unpackMsg.get("msg"), TYPE);
	}

	@Method(desc = "重写数据字典",
			logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
					"2.获取解包后半结构化采集与http交互参数" +
					"3.获取数据字典文件路径" +
					"4.获取重写数据字典参数" +
					"5.创建数据字典目录" +
					"6.写数据字典文件")
	@Param(name = "dictionaryParam", desc = "半结构化采集重写数据字典与agent交互参数", range = "不为空")
	@Param(name = "file_path", desc = "数据字典文件路径", range = "不为空")
	public void writeDictionary(String file_path, String dictionaryParam) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		BufferedWriter bufferedWriter = null;
		try {
			// 2.获取解包后半结构化采集与http交互参数
			Map<String, String> jsonMsgMap = getDictionaryParam(dictionaryParam);
			String dictionaryFilepath = file_path + File.separator + "writeDictionary" + File.separator;
			// 4.获取重写数据字典参数
			String jsonArray = jsonMsgMap.get("dictionaryParam");
			// 5.创建数据字典目录
			File dictionaryFile = new File(dictionaryFilepath);
			if (!dictionaryFile.exists()) {
				if (!dictionaryFile.mkdir()) {
					throw new BusinessException("创建数据字典目录失败！");
				}
			}
			// 6.写数据字典文件
			String pathName = dictionaryFilepath + DateUtil.getSysDate() + DateUtil.getSysTime()
					+ "_dd_data.json";
			dictionaryFile = new File(pathName);
			bufferedWriter = new BufferedWriter(new FileWriter(dictionaryFile));
			bufferedWriter.write(jsonArray);
		} catch (Exception e) {
			throw new BusinessException("写dd_data.json时失败");
		} finally {
			if (bufferedWriter != null) {
				try {
					bufferedWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
