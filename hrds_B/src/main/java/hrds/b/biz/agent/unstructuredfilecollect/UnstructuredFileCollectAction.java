package hrds.b.biz.agent.unstructuredfilecollect;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.netserver.conf.HttpServerConf;
import fd.ng.netserver.conf.HttpServerConfBean;
import fd.ng.web.action.ActionResult;
import fd.ng.web.annotation.RequestBean;
import fd.ng.web.annotation.RequestParam;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Agent_info;
import hrds.commons.entity.File_collect_set;
import hrds.commons.entity.File_source;
import hrds.commons.entity.Object_collect;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.key.PrimayKeyGener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 非结构化文件采集接口类，处理非机构化采集的增改查
 * date: 2019/9/11 14:47
 * author: zxz
 */
public class UnstructuredFileCollectAction extends BaseAction {

	/**
	 * 该方法在页面点击添加非结构化文件采集时调用，获取非结构化采集配置页面初始化的值,当为编辑时，则同时返回回显的值
	 * <p>
	 * 1.根据前端传过来的agent_id获取agent的ip和端口等基本信息
	 * 2.调用远程Agent的后端代码获取采集服务器上的日期、时间、操作系统类型和主机名等基本信息
	 * 3.文件系统采集ID不为空则获取文件系统设置表信息
	 *
	 * @param file_collect_set File_collect_set
	 *                         含义：文件系统设置表对象，接收页面传过来的参数Agent_id和fcs_id(文件系统采集ID)
	 *                         取值范围：Agent_id不可为空，fcs_id可为空
	 * @return com.alibaba.fastjson.JSONObject
	 * 含义：页面需要的Agent所在服务器的基本信息、文件系统设置表信息
	 * 取值范围：不会为空
	 */
	//FIXME 为什么返回这东西
	public Map<String, Object> searchFileCollect(@RequestBean File_collect_set file_collect_set) {
		//TODO file_collect_set里面各字段的值需要校验，应该使用一个公共的校验类进行校验
		//1.根据前端传过来的agent_id获取agent的ip和端口等基本信息
		//FIXME 以上两句，可以合并下成如下的一句。至少，省去了一个变量。毕竟，给变量起名字是个很痛苦的事情。
		//数据可访问权限处理方式：传入用户需要有Agent信息表对应数据的访问权限
		Agent_info agent_info = Dbo.queryOneObject(Agent_info.class,
				"SELECT * FROM " + Agent_info.TableName + " WHERE agent_id = ? "
						+ " AND user_id = ?", file_collect_set.getAgent_id()
				, getUserId()).orElseThrow(() -> new BusinessException(
				"根据Agent_id" + file_collect_set.getAgent_id() + "查询不到Agent_info表信息"));
		//2.调用远程Agent的后端代码获取采集服务器上的日期、时间、操作系统类型和主机名等基本信息
		//FIXME 为什么用配置文件而不是从DB中获取？难道每部署一个agent，就重启一次服务吗
		//TODO 这里面获取Agent服务的路径的方式要重新改
		HttpServerConfBean test = HttpServerConf.getHttpServer("agentServerInfo");
		String webContext = test.getWebContext();
		String actionPattern = test.getActionPattern();
		String url = "http://" + agent_info.getAgent_ip() + ":" + agent_info.getAgent_port() + webContext;
		//调用工具类方法给agent发消息，并获取agent响应
		HttpClient.ResponseValue resVal = new HttpClient().post(url + actionPattern);
		ActionResult ar = JsonUtil.toObject(resVal.getBodyString(), ActionResult.class);
		//FIXME 对成功失败的判断呢？
		if (!ar.isSuccess()) {
			throw new BusinessException("连接" + agent_info.getAgent_ip() + "上的Agent失败");
		}
		Map<String, Object> map = ar.getDataForMap();
		//返回到前端的信息
		//3.文件系统采集ID不为空则获取文件系统设置表信息
		if (file_collect_set.getFcs_id() != null) {
			File_collect_set file_collect_set_info = Dbo.queryOneObject(File_collect_set.class,
					"SELECT * FROM " + File_collect_set.TableName + " WHERE fcs_id = ?",
					file_collect_set.getFcs_id()).orElseThrow(() -> new BusinessException(
					"根据fcs_id" + file_collect_set.getFcs_id() + "查询不到file_collect_set表信息"));
			map.put("file_collect_set_info", file_collect_set_info);
		}
		return map;
	}

	/**
	 * 保存或更新非结构化文件采集页面信息到文件系统设置表对象，同时返回文件系统采集表id
	 * <p>
	 * 1.根据fcs_name查询非结构化任务名称是否重复
	 * 2.保存File_collect_set表
	 *
	 * @param file_collect_set File_collect_set
	 *                         含义：文件系统设置表对象
	 *                         取值范围：不可为空
	 * @return long
	 * 含义：返回文件系统采集表主键，保证添加源文件设置表时可以取到主键
	 * 取值范围：不会为空
	 */
	//FIXME 为什么要返回主键？
	public long addFileCollect(@RequestBean File_collect_set file_collect_set) {
		//数据可访问权限处理方式：该表没有对应的用户访问权限限制
		//TODO file_collect_set里面各字段的值需要校验，应该使用一个公共的校验类进行校验
		//1.根据fcs_name查询非结构化任务名称是否重复
		long count = Dbo.queryNumber("SELECT count(1) count FROM " + File_collect_set.TableName
				+ " WHERE fcs_name = ?", file_collect_set.getFcs_name())
				.orElseThrow(() -> new BusinessException("查询得到的数据必须有且只有一条"));
		if (count > 0) {
			throw new BusinessException("非结构化任务名称重复");
		} else {
			//2.保存File_collect_set表
			file_collect_set.setFcs_id(PrimayKeyGener.getNextId());
			if (file_collect_set.add(Dbo.db()) != 1)
				throw new BusinessException("新增数据失败！data=" + file_collect_set);
			return file_collect_set.getFcs_id();
		}
	}

	/**
	 * 更新非结构化文件采集页面信息到文件系统设置表对象
	 * <p>
	 * 1.根据fcs_name查询非结构化任务名称是否重复
	 * 2.更新File_collect_set表
	 *
	 * @param file_collect_set File_collect_set
	 *                         含义：文件系统设置表对象
	 *                         取值范围：不可为空
	 */
	public void updateFileCollect(@RequestBean File_collect_set file_collect_set) {
		//数据可访问权限处理方式：该表没有对应的用户访问权限限制
		//TODO file_collect_set里面各字段的值需要校验，应该使用一个公共的校验类进行校验
		//新增逻辑
		if (file_collect_set.getFcs_id() == null) {
			throw new BusinessException("更新file_collect_set表时fcs_id不能为空");
		}
		//根据fcs_name查询非结构化任务名称是否与其他采集任务名称重复
		long count = Dbo.queryNumber("SELECT count(1) count FROM " + File_collect_set.TableName
						+ " WHERE " + "fcs_name = ? AND fcs_id != ?"
				, file_collect_set.getFcs_name(), file_collect_set.getFcs_id())
				.orElseThrow(() -> new BusinessException("查询得到的数据必须有且只有一条"));
		if (count > 0) {
			throw new BusinessException("非结构化任务名称重复");
		} else {
			if (file_collect_set.update(Dbo.db()) != 1)
				throw new BusinessException("更新数据失败！data=" + file_collect_set);
		}
	}

	/**
	 * 根据文件系统设置表id获取源文件设置表的合集
	 * <p>
	 * 1.根据文件系统采集id查询源文件设置表
	 * 2.将查询结果集返回到前端
	 *
	 * @param fcs_id long
	 *               含义：文件系统设置表的id
	 *               取值范围：不可为空
	 * @return Result
	 * 含义：源文件设置表基本信息
	 * 取值范围：可能为空
	 */
	public Result searchFileSource(long fcs_id) {
		//数据可访问权限处理方式：该表没有对应的用户访问权限限制
		//FIXME 数据权限检查呢？本类所有方法都没有做检查！
		return Dbo.queryResult("SELECT * FROM " + File_source.TableName + " WHERE fcs_id = ?", fcs_id);
	}

	/**
	 * 选择文件夹Agent所在服务器的文件夹
	 * <p>
	 * 1.根据前端传过来的agent_id获取agent的ip和端口等基本信息
	 * 2.调用远程Agent后端代码获取Agent服务器上文件夹路径
	 * 3.返回到前端
	 *
	 * @param agent_id long
	 *                 含义：文件采集Agent的id
	 *                 取值范围：不能为空
	 * @param path     String
	 *                 含义：选择Agent服务器所在路径下的文件夹，为空则返回根目录下的所有文件夹
	 *                 取值范围：可为空
	 * @return List<String>
	 * 含义：路径下文件夹的名称和服务器操作系统的名称的集合
	 * 取值范围：可能为空
	 */
	//FIXME 使用 nullable
	public List<String> selectPath(long agent_id, @RequestParam(valueIfNull = "") String path) {
		//TODO 根据操作系统校验文件路径，应该使用一个公共的校验类进行校验
		//根据前端传过来的agent_id获取agent的ip和端口等基本信息
		//数据可访问权限处理方式，传入用户需要有Agent对应数据的访问权限
		Agent_info agent_info = Dbo.queryOneObject(Agent_info.class, "SELECT * FROM "
						+ Agent_info.TableName + " WHERE agent_id = ?  AND user_id = ?",
				agent_id, getUserId()).orElseThrow(() ->
				new BusinessException("根据Agent_id:" + agent_id + "查询不到Agent_info表信息"));
		//调用远程Agent的后端代码获取采集服务器上的日期、时间、操作系统类型和主机名等基本信息
		HttpServerConfBean test = HttpServerConf.getHttpServer("systemFileInfo");
		String webContext = test.getWebContext();
		String actionPattern = test.getActionPattern();
		String url = "http://" + agent_info.getAgent_ip() + ":" + agent_info.getAgent_port() + webContext;
		//调用工具类方法给agent发消息，并获取agent响应
		HttpClient.ResponseValue resVal = new HttpClient()
				.addData("pathVal", path)
				.post(url + actionPattern);
		ActionResult ar = JsonUtil.toObject(resVal.getBodyString(), ActionResult.class);
		if (!ar.isSuccess()) {
			throw new BusinessException("连接远程Agent获取文件夹失败");
		}
		//返回到前端的信息
		return ar.getDataForEntityList(String.class);
	}

	/**
	 * 将页面选择的需要采集的源文件路径保存到数据库
	 * <p>
	 * 1.获取json数组转成源文件设置表的集合
	 * 2.遍历源文件设置表的集合，将对象的数据插入或更新到源文件设置表
	 * 3.根据文源文件设置表id判断是新增还是编辑
	 * 4.更新文件系统设置表
	 *
	 * @param file_sources_array String
	 *                           含义：源文件设置表的JSONArray格式的字符串，其中file_source表不能
	 *                           为空的列所对应的值不能为空
	 *                           取值范围：不能为空
	 */
	public void saveFileSource(String file_sources_array) {
		//数据可访问权限处理方式：该表没有对应的用户访问权限限制
		//1.获取json数组转成源文件设置表的集合
		List<File_source> file_sources = JSONArray.parseArray(file_sources_array, File_source.class);
		//获取fcs_id
		Long fcs_id;
		if (file_sources != null && file_sources.size() > 0) {//FIXME 什么都不干就直接get了？
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
			} else {
				file_source.setFile_source_id(PrimayKeyGener.getNextId());
				if (file_source.add(Dbo.db()) != 1)
					throw new BusinessException("新增数据失败！data=" + file_source);
			}
		}
		//FIXME 下面这么多代码，仅仅是为了更新一个字段吗？ 如果是，就要用 update SQL 来做，完全没必要多查询一次数据库
		//4.更新文件系统设置表
		int num = Dbo.execute("UPDATE " + File_collect_set.TableName + " SET is_sendok = ?"
				+ " WHERE fcs_id = ? ", IsFlag.Shi.getCode(), fcs_id);
		if (num != 1) {
			throw new BusinessException("更新表" + File_collect_set.TableName + "失败");
		}
	}

}
