package hrds.b.biz.agent.objectcollect;

import com.alibaba.fastjson.JSONArray;
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
import java.util.Map;

/**
 * 对象采集接口类，处理对象采集的增删改查
 * date: 2019/9/16 15:02
 * author: zxz
 */
public class ObjectCollectAction extends BaseAction {

	/**
	 * 获取半结构化采集配置页面初始化的值，当odc_id不为空时，则同时返回object_collect表的值
	 * <p>
	 * 1.根据前端传过来的agent_id获取agent的ip和端口
	 * 2.根据Agent的ip和端口远程调用Agent的后端代码获取采集服务器上的日期、时间、操作系统类型和主机名等基本信息
	 * 3.对象采集id不为空则获取对象采集设置表信息
	 *
	 * @param object_collect Object_collect
	 *                       含义：对象采集设置表对象，接收页面传过来的参数Agent_id和odc_id(对象采集id)
	 *                       取值范围：agent_id不可为空，odc_id可为空
	 * @return Map<String, Object>
	 * 含义：Agent所在服务器的基本信息、对象采集设置表信息
	 * 取值范围：不会为空
	 */
	public Map<String, Object> searchObjectCollect(@RequestBean Object_collect object_collect) {
		//1.根据前端传过来的agent_id获取agent的ip和端口
		//数据可访问权限处理方式：传入用户需要有Agent信息表对应数据的访问权限
		Agent_info agent_info = Dbo.queryOneObject(Agent_info.class,
				"SELECT * FROM " + Agent_info.TableName + " WHERE agent_id = ? "
						+ " AND user_id = ?", object_collect.getAgent_id()
				, getUserId()).orElseThrow(() -> new BusinessException(
				"根据Agent_id" + object_collect.getAgent_id() + "查询不到Agent_info表信息"));
		//2.根据Agent的ip和端口远程调用Agent的后端代码获取采集服务器上的日期、时间、操作系统类型和主机名等基本信息
		//TODO 这里调用方法后面需要修改
		HttpServerConfBean test = HttpServerConf.getHttpServer("agentServerInfo");
		String webContext = test.getWebContext();
		String actionPattern = test.getActionPattern();
		String url = "http://" + agent_info.getAgent_ip() + ":" + agent_info.getAgent_port() + webContext;
		//调用工具类方法给agent发消息，并获取agent响应
		HttpClient.ResponseValue resVal = new HttpClient().post(url + actionPattern);
		ActionResult ar = JsonUtil.toObject(resVal.getBodyString(), ActionResult.class);
		if (!ar.isSuccess()) {
			throw new BusinessException("远程连接" + agent_info.getAgent_ip() + ":"
					+ agent_info.getAgent_port() + "的Agent失败");
		}
		//返回到前端的信息
		Map<String, Object> map = ar.getDataForMap();
		map.put("localdate", DateUtil.getSysDate());
		map.put("localtime", DateUtil.getSysTime());
		//3.对象采集id不为空则获取对象采集设置表信息
		if (object_collect.getOdc_id() != null) {
			Object_collect object_collect_info = Dbo.queryOneObject(Object_collect.class,
					"SELECT * FROM " + Object_collect.TableName + " WHERE odc_id = ?"
					, object_collect.getOdc_id()).orElseThrow(() -> new BusinessException(
					"根据odc_id" + object_collect.getOdc_id() + "查询不到object_collect表信息"));
			map.put("object_collect_info", object_collect_info);
		}
		return map;
	}

	/**
	 * 保存半结构化文件采集页面信息到对象采集设置表对象，同时返回对象采集id
	 * <p>
	 * 1.根据obj_collect_name查询半结构化任务名称是否重复
	 * 2.保存object_collect表
	 *
	 * @param object_collect Object_collect
	 *                       含义：对象采集设置表对象，对象中不能为空的字段必须有值
	 *                       取值范围：不可为空
	 * @return long
	 * 含义：对象采集设置表id，新建的id后台生成的所以要返回到前端
	 * 取值范围：不会为空
	 */
	public long addObjectCollect(@RequestBean Object_collect object_collect) {
		//数据可访问权限处理方式：该表没有对应的用户访问权限限制
		//TODO 应该使用一个公共的校验类进行校验
		//1.根据obj_collect_name查询半结构化任务名称是否重复
		long count = Dbo.queryNumber("SELECT count(1) count FROM " + Object_collect.TableName
				+ " WHERE obj_collect_name = ?", object_collect.getObj_collect_name())
				.orElseThrow(() -> new BusinessException("查询得到的数据必须有且只有一条"));
		if (count > 0) {
			throw new BusinessException("半结构化采集任务名称重复");
		} else {
			object_collect.setOdc_id(PrimayKeyGener.getNextId());
			//2.保存object_collect表
			if (object_collect.add(Dbo.db()) != 1)
				throw new BusinessException("新增数据失败！data=" + object_collect);
			return object_collect.getOdc_id();
		}
	}

	/**
	 * 更新半结构化文件采集页面信息到对象采集设置表对象，同时返回对象采集id
	 * <p>
	 * 1.根据obj_collect_name查询半结构化任务名称是否与其他采集任务名称重复
	 * 2.更新object_collect表
	 *
	 * @param object_collect Object_collect
	 *                       含义：对象采集设置表对象
	 *                       取值范围：不可为空
	 */
	public void updateObjectCollect(@RequestBean Object_collect object_collect) {
		//数据可访问权限处理方式：该表没有对应的用户访问权限限制
		//TODO 应该使用一个公共的校验类进行校验
		if (object_collect.getOdc_id() == null) {
			throw new BusinessException("主键odc_id不能为空");
		}
		//1.根据obj_collect_name查询半结构化任务名称是否与其他采集任务名称重复
		long count = Dbo.queryNumber("SELECT count(1) count FROM " + Object_collect.TableName
						+ " WHERE obj_collect_name = ? AND odc_id != ?",
				object_collect.getObj_collect_name(), object_collect.getOdc_id())
				.orElseThrow(() -> new BusinessException("查询得到的数据必须有且只有一条"));
		if (count > 0) {
			throw new BusinessException("半结构化采集任务名称重复");
		} else {
			//2.更新object_collect表
			if (object_collect.update(Dbo.db()) != 1)
				throw new BusinessException("更新数据失败！data=" + object_collect);
		}
	}

	/**
	 * 根据对象采集id查询对象采集对应信息的合集
	 * <p>
	 * 1.根据对象采集id查询对象采集对应信息表返回到前端
	 *
	 * @param odc_id long
	 *               含义：对象采集id
	 *               取值范围：不能为空
	 * @return fd.ng.db.resultset.Result
	 * 含义：对象采集对应信息的合集
	 * 取值范围：可能为空
	 */
	public Result searchObjectCollectTask(long odc_id) {
		//数据可访问权限处理方式：该表没有对应的用户访问权限限制
		//1.根据对象采集id查询对象采集对应信息表返回到前端
		return Dbo.queryResult("SELECT * FROM " + Object_collect_task.TableName
				+ " WHERE odc_id = ?", odc_id);
	}

	/**
	 * 对象采集任务编号删除对象采集对应信息表
	 * <p>
	 * 1.根据对象采集任务编号查询对象采集存储设置表是否有数据，有数据不能删除
	 * 2.根据对象采集任务编号查询对象对应的对象采集结构信息表是否有数据，有数据不能删除
	 * 3.根据对象采集任务编号删除对象采集对应信息表
	 *
	 * @param ocs_id long
	 *               含义：对象采集任务编号
	 *               取值范围：不可为空
	 */
	public void deleteObjectCollectTask(long ocs_id) {
		//数据可访问权限处理方式：该表没有对应的用户访问权限限制
		//1.根据对象采集任务编号查询对象采集存储设置表是否有数据，有数据不能删除
		if (Dbo.queryNumber(" SELECT count(1) count FROM " + Object_storage.TableName
				+ " WHERE ocs_id = ?", ocs_id).orElseThrow(()
				-> new BusinessException("查询得到的数据必须有且只有一条")) > 0) {
			// 此对象对应的对象采集存储设置下有数据，不能删除
			throw new BusinessException("此对象对应的对象采集存储设置下有数据，不能删除");
		}
		//2.根据对象采集任务编号查询对象对应的对象采集结构信息表是否有数据，有数据不能删除
		if (Dbo.queryNumber(" SELECT count(1) count FROM " + Object_collect_struct.TableName
				+ " WHERE ocs_id = ?", ocs_id).orElseThrow(()
				-> new BusinessException("查询得到的数据必须有且只有一条")) > 0) {
			// 此对象对应的对象采集结构信息表下有数据，不能删除
			throw new BusinessException("此对象对应的对象采集结构信息表下有数据，不能删除");
		}
		//3.根据对象采集任务编号删除对象采集对应信息表
		int num = Dbo.execute("DELETE FROM " + Object_collect_task.TableName
				+ " WHERE ocs_id = ?", ocs_id);
		if (num != 1) {
			// 判断库里是否没有这条数据
			if (num == 0) {
				throw new BusinessException("删除object_collect_task表信息失败，数据库里没有此条数据，"
						+ "ocs_id=" + ocs_id);
			}
			throw new BusinessException("删除object_collect_task表信息异常，ocs_id=" + ocs_id);
		}
	}

	/**
	 * 保存对象采集对应信息表
	 * <p>
	 * 1.获取json数组转成对象采集对应信息表的集合
	 * 2.获取对象采集对应信息表list进行遍历
	 * 3.根据对象采集对应信息表id判断是新增还是编辑
	 * 4.根据en_name查询对象采集对应信息表的英文名称是否重复
	 *
	 * @param object_collect_task_array String
	 *                                  含义：对象采集对应信息表的JSONArray格式的字符串，其中object_collect_task
	 *                                  表不能为空的列所对应的值不能为空
	 *                                  取值范围：不能为空
	 */
	public void saveObjectCollectTask(String object_collect_task_array) {
		//数据可访问权限处理方式：该表没有对应的用户访问权限限制
		//1.获取json数组转成对象采集对应信息表的集合
		List<Object_collect_task> object_collect_tasks = JSONArray
				.parseArray(object_collect_task_array, Object_collect_task.class);
		//2.获取对象采集对应信息表list进行遍历
		for (Object_collect_task object_collect_task : object_collect_tasks) {
			//TODO 使用公共方法校验数据的正确性
			//XXX 这里新增和编辑是放在一起的，因为这里面是保存一个列表的数据，可能为一条或者多条。
			//XXX 这一条或者多条数据会有新增也会有编辑，所以对应在一个方法里面了
			//3.根据对象采集对应信息表id判断是新增还是编辑
			if (object_collect_task.getOcs_id() == null) {
				//新增
				//4.根据en_name查询对象采集对应信息表的英文名称是否重复
				long count = Dbo.queryNumber("SELECT count(1) count FROM " + Object_collect_task.TableName
						+ " WHERE en_name = ?", object_collect_task.getEn_name()).orElseThrow(()
						-> new BusinessException("查询得到的数据必须有且只有一条"));
				if (count > 0) {
					throw new BusinessException("对象采集对应信息表的英文名称重复");
				} else {
					object_collect_task.setOcs_id(PrimayKeyGener.getNextId());
					if (object_collect_task.add(Dbo.db()) != 1)
						throw new BusinessException("新增数据失败！data=" + object_collect_task);
				}
			} else {
				//更新
				//4.根据en_name查询对象采集对应信息表的英文名称是否重复
				long count = Dbo.queryNumber("SELECT count(1) count FROM "
								+ Object_collect_task.TableName + " WHERE en_name = ? AND ocs_id != ?",
						object_collect_task.getEn_name(), object_collect_task.getOcs_id())
						.orElseThrow(() -> new BusinessException("查询得到的数据必须有且只有一条"));
				if (count > 0) {
					throw new BusinessException("对象采集对应信息表的英文名称重复");
				} else {
					if (object_collect_task.update(Dbo.db()) != 1)
						throw new BusinessException("更新数据失败！data=" + object_collect_task);
				}
			}
		}
	}

	/**
	 * 根据ocs_idc查询对象采集任务对应对象采集结构信息
	 * <p>
	 * 1.查询对应对象采集结构信息表，返回前端
	 *
	 * @param ocs_id long
	 *               含义：对象采集任务编号
	 *               取值范围：不可为空
	 * @return fd.ng.db.resultset.Result
	 * 含义：对象采集任务对应对象采集结构信息的集合
	 * 取值范围：可能为空
	 */
	public Result searchObject_collect_struct(long ocs_id) {
		//数据可访问权限处理方式：该表没有对应的用户访问权限限制
		//1.查询对应对象采集结构信息表，返回前端
		return Dbo.queryResult("SELECT * FROM " + Object_collect_struct.TableName
				+ " WHERE ocs_id = ?", ocs_id);
	}

	/**
	 * 根据结构信息id删除对象采集结构信息表
	 * <p>
	 * 1.获取结构信息id，删除对象采集结构信息表
	 * 2.判断库里是否没有这条数据
	 *
	 * @param struct_id long
	 *                  含义：结构信息id
	 *                  取值范围：不可为空
	 */
	public void deleteObject_collect_struct(long struct_id) {
		//数据可访问权限处理方式：该表没有对应的用户访问权限限制
		//1.获取结构信息id，删除对象采集结构信息表
		int num = Dbo.execute("DELETE FROM " + Object_collect_struct.TableName
				+ " WHERE struct_id = ?", struct_id);
		if (num != 1) {
			// 2.判断库里是否没有这条数据
			if (num == 0) {
				throw new BusinessException("删除object_collect_struct表信息失败，数据库里没有此条数据，"
						+ "struct_id = " + struct_id);
			}
			throw new BusinessException("删除object_collect_struct表信息异常，struct_id = " + struct_id);
		}
	}

	/**
	 * 保存对象采集对应结构信息表
	 * <p>
	 * 1.获取json数组转成对象采集结构信息表的集合
	 * 2.获取对象采集结构信息list进行遍历
	 * 3.根据对象采集结构信息id判断是新增还是编辑
	 * 4.判断同一个对象采集任务下，对象采集结构信息表的coll_name有没有重复
	 * 5.新增或更新数据库
	 *
	 * @param object_collect_struct_array String
	 *                                    含义：对象采集对应结构信息表的JSONArray格式的字符串，其中
	 *                                    object_collect_struct表不能为空的列所对应的值不能为空
	 *                                    取值范围：不可为空
	 */
	public void saveObject_collect_struct(String object_collect_struct_array) {
		//数据可访问权限处理方式：该表没有对应的用户访问权限限制
		//1.获取json数组转成对象采集结构信息表的集合
		List<Object_collect_struct> object_collect_structs = JSONArray
				.parseArray(object_collect_struct_array, Object_collect_struct.class);
		//2.获取对象采集结构信息list进行遍历
		for (Object_collect_struct object_collect_struct : object_collect_structs) {
			//TODO 应该使用一个公共的校验类进行校验
			//XXX 这里新增和编辑是放在一起的，因为这里面是保存一个列表的数据，可能为一条或者多条。
			//XXX 这一条或者多条数据会有新增也会有编辑，所以对应在一个方法里面了
			//3.根据对象采集结构信息id判断是新增还是编辑
			if (object_collect_struct.getStruct_id() == null) {
				//4.判断同一个对象采集任务下，对象采集结构信息表的coll_name有没有重复
				long count = Dbo.queryNumber("SELECT count(1) count FROM "
								+ Object_collect_struct.TableName + " WHERE coll_name = ? AND ocs_id = ?"
						, object_collect_struct.getColl_name(), object_collect_struct.getOcs_id())
						.orElseThrow(() -> new BusinessException("有且只有一个返回值"));
				if (count > 0) {
					throw new BusinessException("同一个对象采集任务下，对象采集结构信息表的coll_name不能重复");
				}
				//新增
				object_collect_struct.setStruct_id(PrimayKeyGener.getNextId());
				//5.新增或更新数据库
				if (object_collect_struct.add(Dbo.db()) != 1)
					throw new BusinessException("新增数据失败！data=" + object_collect_struct);
			} else {
				long count = Dbo.queryNumber("SELECT count(1) count FROM "
								+ Object_collect_struct.TableName + " WHERE coll_name = ? AND ocs_id = ? " +
								" AND struct_id != ?", object_collect_struct.getColl_name()
						, object_collect_struct.getOcs_id(), object_collect_struct.getStruct_id())
						.orElseThrow(() -> new BusinessException("有且只有一个返回值"));
				if (count > 0) {
					throw new BusinessException("同一个对象采集任务下，对象采集结构信息表的coll_name不能重复");
				}
				//更新
				if (object_collect_struct.update(Dbo.db()) != 1)
					throw new BusinessException("更新数据失败！data=" + object_collect_struct);
			}
		}
	}

	/**
	 * 根据对象采集id查询对象采集任务存储设置
	 * <p>
	 * 1.根据对象采集id，查询对象采集任务及每个任务对象的存储设置
	 *
	 * @param odc_id long
	 *               含义：对象采集id
	 *               取值范围：不可为空
	 * @return fd.ng.db.resultset.Result
	 * 含义：采集任务及每个任务的存储设置
	 * 取值范围：不会为空
	 */
	public Result searchObject_storage(long odc_id) {
		//数据可访问权限处理方式：该表没有对应的用户访问权限限制
		//1.根据对象采集id，查询对象采集任务及每个任务对象的存储设置
		Result result = Dbo.queryResult("SELECT * FROM " + Object_collect_task.TableName + " t1 left join "
				+ Object_storage.TableName + " t2 on t1.ocs_id = t2.ocs_id WHERE odc_id = ?", odc_id);
		if (result.isEmpty()) {
			throw new BusinessException("odc_id =" + odc_id + "查询不到对象采集任务表的数据");
		}
		return result;
	}

	/**
	 * 保存对象采集存储设置表
	 * <p>
	 * 1.获取json数组转成对象采集结构信息表的集合
	 * 2.根据对象采集存储设置表id是否为空判断是编辑还是新增
	 * 3.保存对象采集存储设置表
	 * 4.更新对象采集设置表的字段是否完成设置并发送成功为是
	 *
	 * @param object_storage_array String
	 *                             含义：对象采集存储设置表的JSONArray格式的字符串，其中
	 *                             object_storage表不能为空的列所对应的值不能为空
	 *                             取值范围：不能为空
	 * @param odc_id               long
	 *                             含义：对象采集id
	 *                             取值范围：不能为空
	 */
	public void saveObject_storage(String object_storage_array, long odc_id) {
		//数据可访问权限处理方式：该表没有对应的用户访问权限限制
		//1.获取json数组转成对象采集结构信息表的集合
		List<Object_storage> object_storages = JSONArray.parseArray(object_storage_array, Object_storage.class);
		for (Object_storage object_storage : object_storages) {
			//2.根据对象采集存储设置表id是否为空判断是编辑还是新增
			//TODO 应该使用一个公共的校验类进行校验
			//XXX 这里新增和编辑是放在一起的，因为这里面是保存一个列表的数据，可能为一条或者多条。
			//XXX 这一条或者多条数据会有新增也会有编辑，所以对应在一个方法里面了
			if (object_storage.getObj_stid() == null) {
				//新增
				object_storage.setObj_stid(PrimayKeyGener.getNextId());
				//3.保存对象采集存储设置表
				if (object_storage.add(Dbo.db()) != 1)
					throw new BusinessException("新增数据失败！data=" + object_storage);
			} else {
				if (object_storage.update(Dbo.db()) != 1)
					throw new BusinessException("更新数据失败！data=" + object_storage);
			}
		}
		//4.更新对象采集设置表的字段是否完成设置并发送成功为是
		int num = Dbo.execute("UPDATE " + Object_collect.TableName + " SET is_sendok = ?"
				+ " WHERE odc_id = ? ", IsFlag.Shi.getCode(), odc_id);
		if (num != 1) {
			throw new BusinessException("更新表" + Object_collect.TableName + "失败");
		}
	}
}
