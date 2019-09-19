package hrds.b.biz.agent.objectcollect;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.netserver.conf.HttpServerConf;
import fd.ng.netserver.conf.HttpServerConfBean;
import fd.ng.web.action.ActionResult;
import fd.ng.web.annotation.RequestBean;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.key.PrimayKeyGener;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

/**
 * description: 对象采集接口类，处理对象采集的增删改查 <br>
 * date: 2019/9/16 15:02 <br>
 * author: zxz <br>
 * version: 5.0 <br>
 */
public class ObjectCollectAction extends BaseAction {

	/**
	 * description: 该方法在页面点击添加半结构化采集时调用，获取半结构化采集配置页面初始化的值，
	 * 当为编辑时，则同时返回回显的值 <br>
	 * date: 2019/9/16 15:18 <br>
	 * author: zxz <br>
	 * version: 5.0 <br>
	 * 步骤：
	 * 1.根据前端传过来的agent_id获取agent的ip和端口等基本信息
	 * 2.调用远程Agent的后端代码获取采集服务器上的日期、时间、操作系统类型和主机名等基本信息
	 * 3.对象采集id不为空则表示当前操作为编辑，获取对象采集设置表信息
	 * 4.返回到前端
	 *
	 * @param object_collect Object_collect
	 *                       含义：对象采集设置表对象，接收页面传过来的参数Agent_id和odc_id(对象采集id)
	 *                       取值范围：不可为空
	 * @return com.alibaba.fastjson.JSONObject
	 * 含义：页面需要的Agent所在服务器的基本信息、对象采集设置表信息
	 * 取值范围：不会为空
	 */
	public JSONObject searchObjectCollect(@RequestBean Object_collect object_collect) {
		//1.根据前端传过来的agent_id获取agent的ip和端口等基本信息
		Optional<Agent_info> agent_infoResult = Dbo.queryOneObject(Agent_info.class,
				"SELECT * FROM agent_info WHERE agent_id = ? ", object_collect.getAgent_id());
		Agent_info agent_info = agent_infoResult.orElseThrow(() -> new BusinessException("根据Agent_id" +
				object_collect.getAgent_id() + "查询不到Agent_info表信息"));
		//2.调用远程Agent的后端代码获取采集服务器上的日期、时间、操作系统类型和主机名等基本信息
		HttpServerConfBean test = HttpServerConf.getHttpServer("agentServerInfo");
		String webContext = test.getWebContext();
		String actionPattern = test.getActionPattern();
		String url = "http://" + agent_info.getAgent_ip() + ":" + agent_info.getAgent_port() + webContext;
		//调用工具类方法给agent发消息，并获取agent响应
		HttpClient.ResponseValue resVal = new HttpClient().post(url + actionPattern);
		ActionResult ar = JsonUtil.toObject(resVal.getBodyString(), ActionResult.class);
		//返回到前端的信息
		JSONObject data = (JSONObject) ar.getData();
		data.put("localdate", DateUtil.getSysDate());
		data.put("localtime", DateUtil.getSysTime());
		//3.对象采集id不为空则表示当前操作为编辑，获取对象采集设置表信息
		if (object_collect.getOdc_id() != null) {
			Optional<Object_collect> query_object_collect_info = Dbo.queryOneObject(Object_collect.class,
					"SELECT * FROM object_collect WHERE odc_id = ?", object_collect.getOdc_id());
			Object_collect object_collect_info = query_object_collect_info.orElseThrow(() -> new BusinessException(
					"根据odc_id" + object_collect.getOdc_id() + "查询不到object_collect表信息"));
			data.put("object_collect_info", object_collect_info);
			data.put("is_add", IsFlag.Fou.getCode());
		} else {
			data.put("is_add", IsFlag.Shi.getCode());
		}
		return data;
	}

	/**
	 * description: 保存或更新半结构化文件采集页面信息到对象采集设置表对象，同时返回对象采集id  <br>
	 * date: 2019/9/16 15:28 <br>
	 * author: zxz <br>
	 * version: 5.0 <br>
	 * 步骤：
	 * 1.根据obj_collect_name查询半结构化任务名称是否重复
	 * 2.保存或更新object_collect表
	 * 3.返回到前端
	 *
	 * @param object_collect Object_collect
	 *                       含义：对象采集设置表对象
	 *                       取值范围：不可为空
	 * @param is_add         String
	 *                       含义：是否新增半结构化采集
	 *                       取值范围：不可为空
	 * @return long
	 * 含义：对象采集设置表id
	 * 取值范围：不会为空
	 */
	public long saveObjectCollect(@RequestBean Object_collect object_collect, String is_add) {
		//新增逻辑
		if (IsFlag.Shi.getCode().equals(is_add)) {
			//1.根据obj_collect_name查询半结构化任务名称是否重复
			OptionalLong optionalLong = Dbo.queryNumber("SELECT count(1) count FROM object_collect " +
					"WHERE obj_collect_name = ?", object_collect.getObj_collect_name());
			if (optionalLong.getAsLong() > 0) {
				throw new BusinessException("半结构化采集任务名称重复");
			} else {
				object_collect.setOdc_id(PrimayKeyGener.getNextId());
				if (object_collect.add(Dbo.db()) != 1)
					throw new BusinessException("新增数据失败！data=" + object_collect);
				return object_collect.getOdc_id();
			}
		} else {//编辑逻辑
			//根据obj_collect_name查询半结构化任务名称是否与其他采集任务名称重复
			OptionalLong optionalLong = Dbo.queryNumber("SELECT count(1) count FROM object_collect" +
					" WHERE obj_collect_name = ? AND odc_id != ?",
					object_collect.getObj_collect_name(), object_collect.getOdc_id());
			if (optionalLong.getAsLong() > 0) {
				throw new BusinessException("半结构化采集任务名称重复");
			} else {
				if (object_collect.update(Dbo.db()) != 1)
					throw new BusinessException("更新数据失败！data=" + object_collect);
				return object_collect.getOdc_id();
			}
		}
	}

	/**
	 * description: 根据对象采集id查询对象采集对应信息的合集 <br>
	 * date: 2019/9/16 16:30 <br>
	 * author: zxz <br>
	 * version: 5.0 <br>
	 * 步骤：
	 * 1.根据对象采集id查询对象采集对应信息表
	 * 2.将查询结果集返回到前端
	 *
	 * @param odc_id long
	 *               含义：对象采集id
	 *               取值范围：不能为空
	 * @return fd.ng.db.resultset.Result
	 * 含义：对象采集对应信息的合集
	 * 取值范围：可能为空
	 */
	public Result searchObjectCollectTask(long odc_id) {
		Result result = Dbo.queryResult("SELECT * FROM object_collect_task WHERE odc_id = ?", odc_id);
		return result;
	}

	/**
	 * description: 对象采集任务编号删除对象采集对应信息表 <br>
	 * date: 2019/9/16 16:45 <br>
	 * author: zxz <br>
	 * version: 5.0 <br>
	 * 步骤：
	 * 1.获取对象采集任务编号
	 * 2.删除对象采集对应信息表
	 *
	 * @param ocs_id long
	 *               含义：对象采集任务编号
	 *               取值范围：可为空
	 * @return void
	 */
	public void deleteObjectCollectTask(long ocs_id) {
		//根据对象采集任务编号删除对象采集对应信息表
		Dbo.execute("DELETE FROM object_collect_task WHERE ocs_id = ?", ocs_id);
	}

	/**
	 * description: 保存对象采集对应信息表 <br>
	 * date: 2019/9/16 16:50 <br>
	 * author: zxz <br>
	 * version: 5.0 <br>
	 * 步骤：
	 * 1.获取对象采集对应信息表list进行遍历
	 * 2.根据对象采集对应信息表id判断是新增还是编辑
	 * 3.新增或更新数据库
	 *
	 * @param object_collect_tasks List<Object_collect_task>
	 *                             含义：对象采集对应信息表集合
	 *                             取值范围：不能为空
	 * @return void
	 */
	public void saveObjectCollectTask(@RequestBean List<Object_collect_task> object_collect_tasks) {
		//遍历对象采集对应信息表list
		for (Object_collect_task object_collect_task : object_collect_tasks) {
			if (object_collect_task.getOcs_id() == null) {
				//新增
				object_collect_task.setOcs_id(PrimayKeyGener.getNextId());
				if (object_collect_task.add(Dbo.db()) != 1)
					throw new BusinessException("新增数据失败！data=" + object_collect_task);
			} else {
				//更新
				if (object_collect_task.update(Dbo.db()) != 1)
					throw new BusinessException("更新数据失败！data=" + object_collect_task);
			}
		}
	}

	/**
	 * description: 根据ocs_idc查询对象采集任务对应对象采集结构信息 <br>
	 * date: 2019/9/16 16:59 <br>
	 * author: zxz <br>
	 * version: 5.0 <br>
	 * 步骤：
	 * 1.获取对象采集任务编号
	 * 2.查询对应对象采集结构信息表
	 * 3.返回前端
	 *
	 * @param ocs_id long
	 *               含义：对象采集任务编号
	 *               取值范围：不可为空
	 * @return fd.ng.db.resultset.Result
	 * 含义：对象采集任务对应对象采集结构信息的集合(新增时为空)
	 * 取值范围：可能为空
	 */
	public Result searchObject_collect_struct(long ocs_id) {
		Result result = Dbo.queryResult("SELECT * FROM object_collect_struct WHERE ocs_id = ?", ocs_id);
		return result;
	}

	/**
	 * description: 根据结构信息id删除对象采集结构信息表 <br>
	 * date: 2019/9/16 17:14 <br>
	 * author: zxz <br>
	 * version: 5.0 <br>
	 * 步骤：
	 * 1.获取结构信息id
	 * 2.删除对象采集结构信息表
	 *
	 * @param struct_id long
	 *                  含义：结构信息id
	 *                  取值范围：不可为空
	 * @return void
	 */
	public void deleteObject_collect_struct(long struct_id) {
		//根据结构信息id删除对象采集结构信息表
		Dbo.execute("DELETE FROM object_collect_struct WHERE struct_id = ?", struct_id);
	}

	/**
	 * description: 保存对象采集对应结构信息表 <br>
	 * date: 2019/9/16 17:16 <br>
	 * author: zxz <br>
	 * version: 5.0 <br>
	 * 步骤：
	 * 1.获取对象采集结构信息list进行遍历
	 * 2.根据对象采集结构信息id判断是新增还是编辑
	 * 3.新增或更新数据库
	 *
	 * @param object_collect_structs List<Object_collect_struct>
	 *                               含义：对象采集结构信息list
	 *                               取值范围：不可为空
	 * @return void
	 */
	public void saveObject_collect_struct(@RequestBean List<Object_collect_struct> object_collect_structs) {
		//遍历对象采集对应信息表list
		for (Object_collect_struct object_collect_struct : object_collect_structs) {
			if (object_collect_struct.getStruct_id() == null) {
				//新增
				object_collect_struct.setStruct_id(PrimayKeyGener.getNextId());
				if (object_collect_struct.add(Dbo.db()) != 1)
					throw new BusinessException("新增数据失败！data=" + object_collect_struct);
			} else {
				//更新
				if (object_collect_struct.update(Dbo.db()) != 1)
					throw new BusinessException("更新数据失败！data=" + object_collect_struct);
			}
		}
	}

	/**
	 * description: 根据对象采集id查询对象采集任务存储设置 <br>
	 * date: 2019/9/16 17:34 <br>
	 * author: zxz <br>
	 * version: 5.0 <br>
	 * 步骤：
	 * 1.获取对象采集id
	 * 2.查询对象采集任务及每个任务对象的存储设置
	 * 3.返回到前端
	 *
	 * @param odc_id long
	 *               含义：对象采集id
	 *               取值范围：不可为空
	 * @return fd.ng.db.resultset.Result
	 * 含义：采集任务及每个任务的存储设置
	 * 取值范围：不会为空
	 */
	public Result searchObject_storage(long odc_id) {
		Result result = Dbo.queryResult("SELECT * FROM object_collect_task t1 left join object_storage" +
				" t2 on t1.ocs_id = t2.ocs_id WHERE odc_id = ?", odc_id);
		return result;
	}

	/**
	 * description: 保存对象采集存储设置表 <br>
	 * date: 2019/9/16 17:51 <br>
	 * author: zxz <br>
	 * version: 5.0 <br>
	 * 步骤：
	 * 1.根据对象采集id查询对象采集任务编号
	 * 2.根据对象采集任务编号删除对象采集存储设置表
	 * 3.保存对象采集存储设置表
	 * 4.跟新对象采集设置表的字段是否完成设置并发送成功为是
	 *
	 * @param object_storages List<Object_storage>
	 *                        含义：对象采集存储设置对象的集合
	 *                        取值范围：不能为空
	 * @param odc_id          long
	 *                        含义：对象采集id
	 *                        取值范围：不能为空
	 * @return void
	 */
	public void saveObject_storage(@RequestBean List<Object_storage> object_storages, long odc_id) {
		//根据对象采集id查询对象采集任务编号
		Result result = searchObjectCollectTask(odc_id);
		for (int i = 0; i < result.getRowCount(); i++) {
			//根据对象采集任务编号删除对象采集存储设置表
			Dbo.execute("DELETE FROM object_storage WHERE ocs_id = ?", result.getLong(i, "ocs_id"));
		}
		for (Object_storage object_storage : object_storages) {
			//新增
			object_storage.setObj_stid(PrimayKeyGener.getNextId());
			if (object_storage.add(Dbo.db()) != 1)
				throw new BusinessException("新增数据失败！data=" + object_storage);
		}
		//更新对象采集设置表
		Optional<Object_collect> object_collectResult = Dbo.queryOneObject(Object_collect.class,
				"SELECT * FROM object_collect WHERE odc_id = ? ", odc_id);
		Object_collect object_collect = object_collectResult.orElseThrow(() -> new BusinessException("根据odc_id" +
				odc_id + "查询不到Object_collect表信息"));
		object_collect.setIs_sendok(IsFlag.Shi.getCode());
		if (object_collect.update(Dbo.db()) != 1)
			throw new BusinessException("更新数据失败！data=" + object_collect);
	}
}
