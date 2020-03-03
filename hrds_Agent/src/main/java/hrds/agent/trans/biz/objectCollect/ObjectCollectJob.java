package hrds.agent.trans.biz.objectCollect;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
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

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

@DocClass(desc = "接收页面定义的参数执行object采集", author = "zxz", createdate = "2019/10/23 16:29")
public class ObjectCollectJob extends AgentBaseAction {

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

	@Method(desc = "解析半结构化采集数据字典", logicStep = "")
	@Param(name = "objectCollectParam", desc = "半结构化采集参数", range = "不为空")
	@Return(desc = "", range = "")
	public String parseObjectCollectDataDictionary(String objectCollectParam) throws Exception {
		Type type = new TypeReference<Map<String, String>>() {
		}.getType();
		Map<String, String> objectCollectMap = JsonUtil.toObject(objectCollectParam, type);
		String xmlName = ConnUtil.getDataBaseFile("", "",
				objectCollectMap.get("file_path"), "");
		JSONObject jsonData = new JSONObject();
		if (StringUtil.isNotBlank(objectCollectMap.get("is_dictionary"))) {
			if (IsFlag.Shi == (IsFlag.ofEnumByCode(objectCollectMap.get("is_dictionary")))) {
				Xls2xml.toXml(objectCollectMap.get("file_path"), xmlName);
				jsonData = ConnUtil .getTableToXML2(xmlName);
			} else if (IsFlag.Fou == (IsFlag.ofEnumByCode(objectCollectMap.get("is_dictionary")))) {
				jsonData = ConnUtil.getTableFromJson(objectCollectMap.get("file_path"),
						objectCollectMap.get("data_date"), objectCollectMap.get("file_suffix"));
			} else {
				throw new BusinessException("字段is_dictionary不为是否标志，请检查：is_dictionary:"
						+ objectCollectMap.get("is_dictionary"));
			}
		}
		return PackUtil.packMsg(jsonData.toString());
	}
}
