package hrds.b.biz.agent.unstructuredfilecollect;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.AgentActionUtil;
import hrds.commons.utils.DboExecute;
import hrds.commons.utils.PackUtil;
import hrds.commons.utils.key.PrimayKeyGener;

import java.util.List;
import java.util.Map;

@DocClass(desc = "非结构化文件采集接口类，处理非机构化采集的增改查", author = "zxz", createdate = "2019/9/11 14:47")
public class UnstructuredFileCollectAction extends BaseAction {

	@Method(desc = "该方法在页面点击添加非结构化文件采集时调用，获取非结构化采集配置页面初始化的值" +
			",当为编辑时，则同时返回回显的值",
			logicStep = "1.根据前端传过来的agent_id获取agent的连接url" +
					"2.调用远程Agent的后端代码获取采集服务器上的日期、时间、操作系统类型和主机名等基本信息" +
					"3.文件系统采集ID不为空则获取文件系统设置表信息")
	@Param(name = "file_collect_set", desc = "文件系统设置表对象，接收页面传过来的参数Agent_id和fcs_id(文件系统采集ID)"
			, range = "Agent_id不可为空，fcs_id可为空", isBean = true)
	@Return(desc = "页面需要的Agent所在服务器的基本信息、文件系统设置表信息", range = "不会为空")
	public Map<String, Object> searchFileCollect(File_collect_set file_collect_set) {
		//TODO file_collect_set里面各字段的值需要校验，应该使用一个公共的校验类进行校验
		if (file_collect_set.getAgent_id() == null) {
			throw new BusinessException("agent_id不能为空");
		}
		//数据可访问权限处理方式：传入用户需要有Agent信息表对应数据的访问权限
		//1.根据前端传过来的agent_id获取agent的连接url
		String url = AgentActionUtil.getUrl(file_collect_set.getAgent_id(), getUserId()
				, AgentActionUtil.GETSERVERINFO);
		//2.调用远程Agent的后端代码获取采集服务器上的日期、时间、操作系统类型和主机名等基本信息
		//调用工具类方法给agent发消息，并获取agent响应
		HttpClient.ResponseValue resVal = new HttpClient().post(url);
		ActionResult ar = JsonUtil.toObjectSafety(resVal.getBodyString(), ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接" + url + "服务异常"));
		if (!ar.isSuccess()) {
			throw new BusinessException("连接" + url + "失败");
		}
		Map<String, Object> map = ar.getDataForMap();
		map.put("localdate", DateUtil.getSysDate());
		map.put("localtime", DateUtil.getSysTime());
		//3.文件系统采集ID不为空则获取文件系统设置表信息
		//XXX 这里的Fcs_id不为空则返回回显数据，为空则不处理
		if (file_collect_set.getFcs_id() != null) {
			File_collect_set file_collect_set_info = Dbo.queryOneObject(File_collect_set.class,
					"SELECT * FROM " + File_collect_set.TableName + " WHERE fcs_id = ?",
					file_collect_set.getFcs_id())
					.orElseThrow(() -> new BusinessException(
							"根据fcs_id" + file_collect_set.getFcs_id() + "查询不到file_collect_set表信息"));
			map.put("file_collect_set_info", file_collect_set_info);
		}
		//返回到前端的信息
		return map;
	}

	@Method(desc = "保存非结构化文件采集页面信息到文件系统设置表对象，同时返回文件系统采集表id",
			logicStep = "1.根据fcs_name查询非结构化任务名称是否重复" +
					"2.保存File_collect_set表")
	@Param(name = "file_collect_set", desc = "文件系统设置表对象", range = "不可为空", isBean = true)
	@Return(desc = "返回文件系统采集表主键，保证添加源文件设置表时可以取到主键", range = "不会为空")
	public long addFileCollect(File_collect_set file_collect_set) {
		//数据可访问权限处理方式：该表没有对应的用户访问权限限制
		//TODO file_collect_set里面各字段的值需要校验，应该使用一个公共的校验类进行校验
		//1.根据fcs_name查询非结构化任务名称是否重复
		long count = Dbo.queryNumber("SELECT count(1) count FROM " + File_collect_set.TableName
				+ " WHERE fcs_name = ?", file_collect_set.getFcs_name())
				.orElseThrow(() -> new BusinessException("查询得到的数据必须有且只有一条"));
		if (count > 0) {
			throw new BusinessException("非结构化任务名称重复");
		}
		//2.保存File_collect_set表
		file_collect_set.setFcs_id(PrimayKeyGener.getNextId());
		file_collect_set.setIs_sendok(IsFlag.Fou.getCode());
		file_collect_set.add(Dbo.db());
		return file_collect_set.getFcs_id();
	}

	@Method(desc = "更新非结构化文件采集页面信息到文件系统设置表对象",
			logicStep = "1.根据fcs_name查询非结构化任务名称是否与其他采集任务名称重复" +
					"2.更新File_collect_set表")
	@Param(name = "file_collect_set", desc = "文件系统设置表对象", range = "不可为空", isBean = true)
	public void updateFileCollect(File_collect_set file_collect_set) {
		//数据可访问权限处理方式：该表没有对应的用户访问权限限制
		//TODO file_collect_set里面各字段的值需要校验，应该使用一个公共的校验类进行校验
		//新增逻辑
		if (file_collect_set.getFcs_id() == null) {
			throw new BusinessException("更新file_collect_set表时fcs_id不能为空");
		}
		//更新之后没有重新发送之前默认为false
		file_collect_set.setIs_sendok(IsFlag.Fou.getCode());
		//1.根据fcs_name查询非结构化任务名称是否与其他采集任务名称重复
		long count = Dbo.queryNumber("SELECT count(1) count FROM " + File_collect_set.TableName
						+ " WHERE " + "fcs_name = ? AND fcs_id != ?"
				, file_collect_set.getFcs_name(), file_collect_set.getFcs_id())
				.orElseThrow(() -> new BusinessException("查询得到的数据必须有且只有一条"));
		if (count > 0) {
			throw new BusinessException("非结构化任务名称重复");
		}
		//2.更新File_collect_set表
		file_collect_set.update(Dbo.db());
	}

	@Method(desc = "根据文件系统设置表id获取源文件设置表的合集",
			logicStep = "1.根据文件系统采集id查询源文件设置表返回到前端")
	@Param(name = "fcs_id", desc = "文件系统设置表的id", range = "不可为空")
	@Return(desc = "源文件设置表基本信息", range = "可能为空")
	public Result searchFileSource(long fcs_id) {
		//数据可访问权限处理方式：该表没有对应的用户访问权限限制
		//FIXME 数据权限检查呢？本类所有方法都没有做检查！
		//1.根据文件系统采集id查询源文件设置表返回到前端
		return Dbo.queryResult("SELECT * FROM " + File_source.TableName + " WHERE fcs_id = ?", fcs_id);
	}

	@Method(desc = "选择文件夹Agent所在服务器的文件夹",
			logicStep = "1.根据前端传过来的agent_id获取需要访问的url" +
					"2.调用远程Agent后端代码获取Agent服务器上文件夹路径" +
					"3.返回到前端")
	@Param(name = "agent_id", desc = "文件采集Agent的id", range = "不能为空")
	@Param(name = "path", desc = "选择Agent服务器所在路径下的文件夹，为空则返回根目录下的所有文件夹",
			valueIfNull = "", range = "可为空")
	@Return(desc = "路径下文件夹的名称和服务器操作系统的名称的集合", range = "可能为空")
	//XXX 这里不用nullable是不想下面传参数那里判断null
	public List<Map> selectPath(long agent_id, String path) {
		//TODO 根据操作系统校验文件路径，应该使用一个公共的校验类进行校验
		//数据可访问权限处理方式，传入用户需要有Agent对应数据的访问权限
		//1.根据前端传过来的agent_id获取需要访问的url
		String url = AgentActionUtil.getUrl(agent_id, getUserId(), AgentActionUtil.GETSYSTEMFILEINFO);
		//调用工具类方法给agent发消息，并获取agent响应
		//2.调用远程Agent后端代码获取Agent服务器上文件夹路径
		HttpClient.ResponseValue resVal = new HttpClient()
				.addData("pathVal", path)
				.post(url);
		ActionResult ar = JsonUtil.toObjectSafety(resVal.getBodyString(), ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接" + url + "服务异常"));
		if (!ar.isSuccess()) {
			throw new BusinessException("连接远程Agent获取文件夹失败");
		}
		//3.返回到前端
		return ar.getDataForEntityList(Map.class);
	}

	@Method(desc = "将页面选择的需要采集的源文件路径保存到数据库",
			logicStep = "1.获取json数组转成源文件设置表的集合" +
					"2.根据文件系统设置表id删除源文件设置表" +
					"3.遍历源文件设置表的集合，将对象的数据插入到源文件设置表" +
					"4.更新文件系统设置表")
	@Param(name = "file_sources_array", desc = "多条源文件设置表的JSONArray格式的字符串，" +
			"其中file_source表不能为空的列所对应的值不能为空", range = "不能为空")
	public void saveFileSource(String file_sources_array) {
		//数据可访问权限处理方式：该表没有对应的用户访问权限限制
		//1.获取json数组转成源文件设置表的集合
		List<File_source> file_sources = JSONArray.parseArray(file_sources_array, File_source.class);
		//获取fcs_id
		Long fcs_id;
		if (file_sources != null && file_sources.size() > 0) {
			//获取fcs_id
			fcs_id = file_sources.get(0).getFcs_id();
			if (fcs_id == null) {
				throw new BusinessException("fcs_id不能为空");
			}
		} else {
			throw new BusinessException("源文件设置信息有且最少有一个");
		}
		//2.根据文件系统设置表id删除源文件设置表
		int delete_num = Dbo.execute("DELETE FROM  " + File_source.TableName
				+ " WHERE fcs_id = ?", fcs_id);
		if (delete_num < 0) {
			throw new BusinessException("根据fcs_id = " + fcs_id + "删除源文件设置表失败");
		}
		//3.遍历源文件设置表的集合，将对象的数据插入到源文件设置表
		for (File_source file_source : file_sources) {
			//TODO file_sources里面各字段的值需要校验，应该使用一个公共的校验类进行校验
			//判断同一个非结构化采集文件路径是否重复
			long count = Dbo.queryNumber("SELECT count(1) count FROM " + File_source.TableName
					+ " WHERE fcs_id = ? AND file_source_path = ?", fcs_id, file_source.getFile_source_path())
					.orElseThrow(() -> new BusinessException("查询得到的数据必须有且只有一条"));
			if (count > 0) {
				throw new BusinessException("同一个非结构化采集请不要选择重复的文件路径");
			}
			file_source.setFile_source_id(PrimayKeyGener.getNextId());
			file_source.add(Dbo.db());
		}
		//FIXME 下面这么多代码，仅仅是为了更新一个字段吗？ 如果是，就要用 update SQL 来做，完全没必要多查询一次数据库
		//4.更新文件系统设置表
		DboExecute.updatesOrThrow("更新表" + File_collect_set.TableName + "失败",
				"UPDATE " + File_collect_set.TableName + " SET is_sendok = ?"
						+ " WHERE fcs_id = ? ", IsFlag.Shi.getCode(), fcs_id);
		//发送数据到agent进行执行
		executeJob(fcs_id);
	}

	@Method(desc = "执行文件采集", logicStep = "")
	@Param(name = "fcs_id", desc = "文件采集id", range = "不可为空")
	public void executeJob(long fcs_id) {
		//XXX 接收参数，执行作业，立即启动这里应该会有超时问题，启动作业后是否马上给返回值，后面的作业调度，待讨论
		Result result = Dbo.queryResult("SELECT " +
				"    t1.*,t2.agent_name,t3.source_id,t3.datasource_name,t4.dep_id " +
				"FROM " +
				File_collect_set.TableName + " t1 " +
				"LEFT JOIN " +
				Agent_info.TableName + " t2 " +
				"ON " +
				"    t1.agent_id = t2.agent_id " +
				"LEFT JOIN " +
				Data_source.TableName + " t3 " +
				"ON " +
				"    t3.source_id = t2.source_id " +
				"LEFT JOIN " +
				Sys_user.TableName + " t4 " +
				"ON " +
				"    t4.user_id = t2.user_id " +
				"    where t1.fcs_id = ?", fcs_id);
		if (result.isEmpty()) {
			throw new BusinessException("查询不到数据，请检查传入的id是否正确");
		}
//		LOGGER.info("打印数据 " + result.toJSON());
		JSONObject object = JSONArray.parseArray(result.toJSON()).getJSONObject(0);
		//获取需要采集的文件源列表
		Result source = Dbo.queryResult("SELECT * FROM " + File_source.TableName + " where fcs_id = ?", fcs_id);
		if (source.isEmpty()) {
			throw new BusinessException("查询不到文件源设置表数据，请检查传入的id是否正确");
		}
		object.put("file_sourceList", JSONArray.parseArray(source.toJSON()));
		long agent_id = result.getLong(0, "agent_id");
		//1.根据前端传过来的agent_id获取需要访问的url
		String url = AgentActionUtil.getUrl(agent_id, getUserId(), AgentActionUtil.EXECUTEFILECOLLECT);
		//调用工具类方法给agent发消息，并获取agent响应
		//2.调用远程Agent后端代码获取Agent服务器上文件夹路径
		HttpClient.ResponseValue resVal = new HttpClient()
//				.addData("fcs_id", result.getString(0, "fcs_id"))
//				.addData("agent_id", result.getString(0, "agent_id"))
//				.addData("fcs_name", result.getString(0, "fcs_name"))
//				.addData("host_name", result.getString(0, "host_name"))
//				.addData("system_type", result.getString(0, "system_type"))
//				.addData("is_sendok", result.getString(0, "is_sendok"))
//				.addData("is_solr", result.getString(0, "is_solr"))
//				.addData("agent_name", result.getString(0, "agent_name"))
//				.addData("source_id", result.getString(0, "source_id"))
//				.addData("datasource_name", result.getString(0, "datasource_name"))
//				.addData("dep_id", result.getString(0, "dep_id"))
				.addData("fileCollectTaskInfo", PackUtil.packMsg(object.toJSONString()))
				.post(url);
		ActionResult ar = JsonUtil.toObjectSafety(resVal.getBodyString(), ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接" + url + "服务异常"));
		if (!ar.isSuccess()) {
			throw new BusinessException("执行文件采集作业失败");
		}
	}

}
