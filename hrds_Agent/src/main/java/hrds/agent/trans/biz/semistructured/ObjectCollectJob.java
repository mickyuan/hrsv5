package hrds.agent.trans.biz.semistructured;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import hrds.agent.job.biz.bean.JobParamBean;
import hrds.agent.job.biz.bean.ObjectCollectParamBean;
import hrds.agent.job.biz.core.JobFactory;
import hrds.commons.base.AgentBaseAction;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Object_collect;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.ConnUtil;
import hrds.commons.utils.PackUtil;
import hrds.commons.utils.xlstoxml.Xls2xml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

@DocClass(desc = "接收页面定义的参数执行object采集", author = "zxz", createdate = "2019/10/23 16:29")
public class ObjectCollectJob extends AgentBaseAction {

	private static final Type TYPE = new TypeReference<Map<String, Object>>() {
	}.getType();

	@Method(desc = "object采集和前端交互的接口",
			logicStep = "1.获取json数组转成ObjectCollectParamBean的集合" +
					"2.校验对象的值是否正确" +
					"3.使用JobFactory工厂类调用后台方法")
	@Param(name = "object_collect", desc = "半结构化对象采集设置表对象",
			isBean = true, range = "所有这张表不能为空的字段的值必须有，为空则会抛异常")
	@Param(name = "objectCollectParamBeanArray", desc = "多条半结构化对象采集存储到hadoop存储信息实体合集的" +
			"json数组字符串", range = "所有ObjectCollectParamBean这个实体不能为空的字段的值必须有，为空则会抛异常")
	public void execute(Object_collect object_collect, String objectCollectParamBeanArray) {
		//1.获取json数组转成ObjectCollectParamBean的集合
		List<ObjectCollectParamBean> objectCollectParamBeanList = JSONArray.parseArray(objectCollectParamBeanArray,
				ObjectCollectParamBean.class);
		//2.校验对象的值是否正确
		//TODO 使用公共方法校验所有传入参数的对象的值的合法性
		//TODO Agent这个参数该怎么接，是统一封装成工厂需要的参数吗？
		//3.使用JobFactory工厂类调用后台方法
		JobFactory.newInstance(null, null, new JobParamBean(),
				"", null).runJob();
	}

	@Method(desc = "解析半结构化采集数据字典获取表数据",
			logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
					"2.获取生成xml文件文件名" +
					"3.判断是否存在数据字典，根据不同情况做不同处理" +
					"4.有数据字典写xml文件获取数据字典数据" +
					"5.数据字典不存在，获取当前日期下的数据文件数据" +
					"6.返回解析后的数据字典所有表数据")
	@Param(name = "file_path", desc = "文件存储路径", range = "不为空")
	@Param(name = "is_dictionary", desc = "是否存在数据字典", range = "使用（IsFlag）代码项")
	@Param(name = "data_date", desc = "数据日期", range = "是否存在数据字典选择否的时候必选", nullable = true)
	@Param(name = "file_suffix", desc = "文件后缀名", range = "无限制")
	@Return(desc = "返回解析后的数据文件数据", range = "不能为空")
	public String getDicTable(String file_path, String file_suffix, String is_dictionary, String data_date) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		// 2.获取生成xml文件文件名
		String xmlName = ConnUtil.getDataBaseFile("", "", file_path, "");
		// 3.判断是否存在数据字典，根据不同情况做不同处理
		List<Object> jsonData;
		if (IsFlag.Shi == (IsFlag.ofEnumByCode(is_dictionary))) {
			// 4.有数据字典写xml文件获取数据字典数据
			Xls2xml.toXml2(file_path, xmlName);
			jsonData = ConnUtil.getDicTable(xmlName);
		} else {
			// 5.数据字典不存在，获取当前日期下的数据文件数据
			jsonData = ConnUtil.getTableByNoDictionary(file_path, data_date, file_suffix);
		}
		// 6.返回解析后的数据字典所有表数据
		return PackUtil.packMsg(JsonUtil.toJson(jsonData));
	}

	@Method(desc = "解析半结构化采集数据字典获取表数据",
			logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
					"2.获取生成xml文件文件名" +
					"3.有数据字典写xml文件获取数据字典数据" +
					"4.返回解析后的所有数据字典表对应列数据")
	@Param(name = "file_path", desc = "文件存储路径", range = "不为空")
	@Param(name = "table_name", desc = "表名称", range = "不为空")
	@Return(desc = "返回解析后的数据文件数据", range = "不能为空")
	public String getDicColumnByTable(String file_path, String table_name) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		// 2.获取生成xml文件文件名
		String xmlName = ConnUtil.getDataBaseFile("", "", file_path, "");
		// 3.有数据字典写xml文件获取数据字典数据
		Xls2xml.toXml2(file_path, xmlName);
		// 4.返回解析后的所有数据字典表对应列数据
		return PackUtil.packMsg(JsonUtil.toJson(ConnUtil.getColumnByTable2(xmlName, table_name)));
	}

	@Method(desc = "解析半结构化采集数据字典获取表数据",
			logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
					"2.获取生成xml文件文件名" +
					"3.有数据字典写xml文件获取数据字典数据" +
					"4.返回有数据字典时的数据处理方式数据")
	@Param(name = "file_path", desc = "文件存储路径", range = "不为空")
	@Param(name = "table_name", desc = "表名称", range = "不为空")
	@Return(desc = "返回解析后的数据文件数据", range = "不能为空")
	public String getHandleTypeByTable(String file_path, String table_name) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		// 2.获取生成xml文件文件名
		String xmlName = ConnUtil.getDataBaseFile("", "", file_path, "");
		// 3.有数据字典写xml文件获取数据字典数据
		Xls2xml.toXml2(file_path, xmlName);
		// 4.返回有数据字典时的数据处理方式数据
		return PackUtil.packMsg(JsonUtil.toJson(ConnUtil.getHandleTypeByTable(file_path, table_name)));
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
