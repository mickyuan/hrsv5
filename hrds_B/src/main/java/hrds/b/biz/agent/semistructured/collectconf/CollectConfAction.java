package hrds.b.biz.agent.semistructured.collectconf;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import fd.ng.web.util.Dbo;
import hrds.b.biz.agent.datafileconf.CheckParam;
import hrds.b.biz.agent.tools.SendMsgUtil;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.ObjectCollectType;
import hrds.commons.entity.Object_collect;
import hrds.commons.entity.Object_collect_task;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.AgentActionUtil;
import hrds.commons.utils.key.PrimayKeyGener;

import java.util.List;
import java.util.Map;

@DocClass(desc = "半结构化采集配置类", author = "dhw", createdate = "2020/6/9 10:36")
public class CollectConfAction extends BaseAction {

	@Method(desc = "获取新增半结构化采集配置信息",
			logicStep = "1.根据agent_id获取调用Agent服务的接口" +
					"2.根据url远程调用Agent的后端代码获取采集服务器上的日期、" +
					"时间、操作系统类型和主机名等基本信息" +
					"3.返回新增半结构化采集配置信息")
	@Param(name = "agent_id", desc = "采集agent主键ID", range = "不为空")
	@Return(desc = "返回新增半结构化采集配置信息", range = "不会为空")
	public Map<String, Object> getAddObjectCollectConf(long agent_id) {
		// 1.根据agent_id获取调用Agent服务的接口
		String url = AgentActionUtil.getUrl(agent_id, getUserId(), AgentActionUtil.GETSERVERINFO);
		//2.根据url远程调用Agent的后端代码获取采集服务器上的日期、时间、操作系统类型和主机名等基本信息
		HttpClient.ResponseValue resVal = new HttpClient().post(url);
		ActionResult ar = JsonUtil.toObjectSafety(resVal.getBodyString(), ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接远程" + url + "服务异常"));
		if (!ar.isSuccess()) {
			throw new BusinessException("远程连接" + url + "的Agent失败");
		}
		// 3.返回新增半结构化采集配置信息
		Map<String, Object> map = ar.getDataForMap();
		map.put("localDate", DateUtil.getSysDate());
		map.put("localTime", DateUtil.getSysTime());
		return map;
	}

	@Method(desc = "根据对象采集id获取半结构化采集配置信息（编辑任务时数据回显）",
			logicStep = "1.数据可访问权限处理方式：通过agent_id进行访问权限限制" +
					"2.检查当前任务是否存在" +
					"3.根据对象采集id查询半结构化采集配置首页数据")
	@Param(name = "odc_id", desc = "对象采集id", range = "不为空")
	@Return(desc = "返回对象采集id查询半结构化采集配置首页数据", range = "不为空")
	public Map<String, Object> getObjectCollectConfById(long odc_id) {
		// 1.数据可访问权限处理方式：通过agent_id进行访问权限限制
		// 2.检查当前任务是否存在
		long countNum = Dbo.queryNumber(
				"SELECT COUNT(1) FROM  " + Object_collect.TableName + " WHERE odc_id = ?",
				odc_id).orElseThrow(() -> new BusinessException("SQL查询错误"));
		if (countNum == 0) {
			CheckParam.throwErrorMsg("任务( %s )不存在!!!", odc_id);
		}
		// 3.根据对象采集id查询半结构化采集配置首页数据
		return Dbo.queryOneObject(
				"SELECT * FROM " + Object_collect.TableName + " WHERE odc_id = ?", odc_id);
	}

	@Method(desc = "半结构化采集查看表",
			logicStep = "1.数据可访问权限处理方式：通过user_id与agent_id进行访问权限限制" +
					"2.判断当是否存在数据字典选择否的时候数据日前是否为空" +
					"3.获取解析与agent服务交互返回响应表数据")
	@Param(name = "agent_id", desc = "agent信息表主键ID", range = "新增agent时生成")
	@Param(name = "file_path", desc = "采集文件路径", range = "不为空")
	@Param(name = "is_dictionary", desc = "是否存在数据字典", range = "使用（IsFlag）代码项")
	@Param(name = "data_date", desc = "数据日期", range = "是否存在数据字典选择否的时候必选", nullable = true)
	@Param(name = "file_suffix", desc = "文件后缀名", range = "无限制")
	@Return(desc = "返回解析数据字典后的表数据", range = "无限制")
	public List<Object_collect_task> viewTable(long agent_id, String file_path, String is_dictionary,
	                                           String data_date, String file_suffix) {
		// 1.数据可访问权限处理方式：通过user_id与agent_id进行访问权限限制
		// 2.判断当是否存在数据字典选择否的时候数据日期是否为空
		if (IsFlag.Fou == IsFlag.ofEnumByCode(is_dictionary)
				&& StringUtil.isBlank(data_date)) {
			throw new BusinessException("当是否存在数据字典选择否，数据日期不能为空");
		}
		// 3.获取解析与agent服务交互返回响应表数据
		return SendMsgUtil.getDictionaryTableInfo(agent_id, file_path, is_dictionary, data_date, file_suffix,
				getUserId());
	}

	@Method(desc = "保存半结构化文件采集页面信息到对象采集设置表对象，同时返回对象采集id",
			logicStep = "1.数据可访问权限处理方式：该表没有对应的用户访问权限限制" +
					"2.判断是否存在数据字典选择否的时候数据日期是否为空" +
					"3.如果选择有数据字典数据日期为空" +
					"4.保存object_collect表数据入库，这里新增编辑放在一起是因为可能会上一步下一步查看配置信息" +
					"4.1.新增时根据obj_number查询半结构化采集任务编号是否重复" +
					"4.2 更新" +
					"5.返回对象采集ID")
	@Param(name = "object_collect", desc = "对象采集设置表对象，对象中不能为空的字段必须有值",
			range = "不可为空", isBean = true)
	@Return(desc = "对象采集设置表id，新建的id后台生成的所以要返回到前端", range = "不会为空")
	public long saveObjectCollect(Object_collect object_collect) {
		// 1.数据可访问权限处理方式：该表没有对应的用户访问权限限制
		// 之前对象采集存在行采集与对象采集两种，目前仅支持行采集,所以默认给
		object_collect.setObject_collect_type(ObjectCollectType.HangCaiJi.getCode());
		// 2.判断是否存在数据字典选择否的时候数据日期是否为空
		if (IsFlag.Fou == IsFlag.ofEnumByCode(object_collect.getIs_dictionary()) &&
				StringUtil.isBlank(object_collect.getData_date())) {
			throw new BusinessException("当是否存在数据字典选择否的时候，数据日期不能为空");
		}
		// 3.如果选择有数据字典数据日期为空
		if (IsFlag.Shi == IsFlag.ofEnumByCode(object_collect.getIs_dictionary())) {
			object_collect.setData_date("");
		}
		// 4.保存object_collect表数据入库，这里新增编辑放在一起是因为可能会上一步下一步查看配置信息
		if (object_collect.getOdc_id() == null) {
			// 4.1.新增时根据obj_number查询半结构化采集任务编号是否重复
			isObjNumberExist(object_collect.getObj_number());
			// 新增
			object_collect.setOdc_id(PrimayKeyGener.getNextId());
			object_collect.add(Dbo.db());
		} else {
			// 4.2 更新
			object_collect.update(Dbo.db());
		}
		// 5.返回对象采集ID
		return object_collect.getOdc_id();
	}

	@Method(desc = "判断半结构化采集任务编号是否重复", logicStep = "1.判断半结构化采集任务编号是否重复")
	@Param(name = "obj_number", desc = "采集任务编号", range = "新增半结构化配置信息时生成")
	private void isObjNumberExist(String obj_number) {
		// 1.判断半结构化采集任务编号是否重复
		long count = Dbo.queryNumber(
				"SELECT count(1) count FROM " + Object_collect.TableName + " WHERE obj_number = ?",
				obj_number).orElseThrow(() -> new BusinessException("sql查询错误"));
		if (count > 0) {
			throw new BusinessException("半结构化采集任务编号重复");
		}
	}

}
