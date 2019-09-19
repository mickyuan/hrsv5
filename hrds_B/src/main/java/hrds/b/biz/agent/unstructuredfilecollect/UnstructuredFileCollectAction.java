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
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.key.PrimayKeyGener;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

/**
 * description: 非结构化文件采集接口类，处理非机构化采集的增改查
 * date: 2019/9/11 14:47
 * author: zxz
 * version: 5.0
 */
public class UnstructuredFileCollectAction extends BaseAction {

	/**
	 * description: 该方法在页面点击添加非结构化文件采集时调用，获取非结构化采集配置页面初始化的值,
	 * 当为编辑时，则同时返回回显的值
	 * date: 2019/9/11 16:29
	 * author: zxz
	 * version: 5.0
	 * 步骤：
	 * 1.根据前端传过来的agent_id获取agent的ip和端口等基本信息
	 * 2.调用远程Agent的后端代码获取采集服务器上的日期、时间、操作系统类型和主机名等基本信息
	 * 3.返回到前端
	 *
	 * @param file_collect_set File_collect_set
	 *                         含义：文件系统设置表对象，接收页面传过来的参数Agent_id和fcs_id(文件系统采集ID)
	 *                         取值范围：不可空
	 * @return com.alibaba.fastjson.JSONObject
	 * 含义：页面需要的Agent所在服务器的基本信息、文件系统设置表信息
	 * 取值范围：不会为空
	 */
	public JSONObject searchFileCollect(@RequestBean File_collect_set file_collect_set) {
		//1.根据前端传过来的agent_id获取agent的ip和端口等基本信息
		Optional<Agent_info> agent_infoResult = Dbo.queryOneObject(Agent_info.class,
				"SELECT * FROM agent_info WHERE agent_id = ? ", file_collect_set.getAgent_id());
		Agent_info agent_info = agent_infoResult.orElseThrow(() -> new BusinessException("根据Agent_id" +
				file_collect_set.getAgent_id() + "查询不到Agent_info表信息"));
		//2.调用远程Agent的后端代码获取采集服务器上的日期、时间、操作系统类型和主机名等基本信息
		HttpServerConfBean test = HttpServerConf.getHttpServer("agentServerInfo");
		String webContext = test.getWebContext();
		String actionPattern = test.getActionPattern();
		String url = "http://" + agent_info.getAgent_ip() + ":" + agent_info.getAgent_port() + webContext;
		//调用工具类方法给agent发消息，并获取agent响应
		HttpClient.ResponseValue resVal = new HttpClient().post(url + actionPattern);
		ActionResult ar = JsonUtil.toObject(resVal.getBodyString(), ActionResult.class);
		//返回到前端的信息
//		Map<String, Object> data = (HashMap<String, Object>) ar.getData();
		JSONObject data = (JSONObject) ar.getData();
		//文件系统采集ID不为空则表示当前操作为编辑，获取文件系统设置表信息
		if (file_collect_set.getFcs_id() != null) {
			Optional<File_collect_set> query_file_collect_set_info = Dbo.queryOneObject(File_collect_set.class,
					"SELECT * FROM file_collect_set WHERE fcs_id = ?", file_collect_set.getFcs_id());
			File_collect_set file_collect_set_info = query_file_collect_set_info.orElseThrow(() -> new BusinessException(
					"根据fcs_id" + file_collect_set.getFcs_id() + "查询不到file_collect_set表信息"));
			data.put("file_collect_set_info", file_collect_set_info);
			data.put("is_add", IsFlag.Fou.getCode());
		} else {
			data.put("is_add", IsFlag.Shi.getCode());
		}
		return data;
	}

	/**
	 * description: 保存或更新非结构化文件采集页面信息到文件系统设置表对象，同时返回文件系统采集表id
	 * date: 2019/9/12 11:07
	 * author: zxz
	 * version: 5.0
	 * 步骤：
	 * 1.根据fcs_name查询非结构化任务名称是否重复
	 * 2.保存或更新File_collect_set表
	 * 3.返回到前端
	 *
	 * @param file_collect_set File_collect_set
	 *                         含义：文件系统设置表对象
	 *                         取值范围：不可为空
	 * @param is_add           String
	 *                         含义：是否新增非结构化采集
	 *                         取值范围：不可为空
	 * @return long
	 * 含义：文件系统采集表主键、fcs_id
	 * 取值范围：不会为空
	 */
	public long saveFileCollect(@RequestBean File_collect_set file_collect_set, String is_add) {
		//新增逻辑
		if (IsFlag.Shi.getCode().equals(is_add)) {
			//1.根据fcs_name查询非结构化任务名称是否重复
			OptionalLong optionalLong = Dbo.queryNumber("SELECT count(1) count FROM file_collect_set " +
					"WHERE fcs_name = ?", file_collect_set.getFcs_name());
			if (optionalLong.getAsLong() > 0) {
				throw new BusinessException("非结构化任务名称重复");
			} else {
				file_collect_set.setFcs_id(PrimayKeyGener.getNextId());
				if (file_collect_set.add(Dbo.db()) != 1)
					throw new BusinessException("新增数据失败！data=" + file_collect_set);
				return file_collect_set.getFcs_id();
			}
		} else {//编辑逻辑
			//根据fcs_name查询非结构化任务名称是否与其他采集任务名称重复
			OptionalLong optionalLong = Dbo.queryNumber("SELECT count(1) count FROM file_collect_set WHERE " +
					"fcs_name = ? AND fcs_id != ?", file_collect_set.getFcs_name(), file_collect_set.getFcs_id());
			if (optionalLong.getAsLong() > 0) {
				throw new BusinessException("非结构化任务名称重复");
			} else {
				if (file_collect_set.update(Dbo.db()) != 1)
					throw new BusinessException("更新数据失败！data=" + file_collect_set);
				return file_collect_set.getFcs_id();
			}
		}
	}

	/**
	 * description: 根据文件系统设置表id获取源文件设置表的合集
	 * date: 2019/9/12 15:17
	 * author: zxz
	 * version: 5.0
	 * 步骤：
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
		Result result = Dbo.queryResult("SELECT * FROM file_source WHERE fcs_id = ?", fcs_id);
		return result;
	}

	/**
	 * description: 选择文件夹
	 * date: 2019/9/16 14:08
	 * author: zxz
	 * version: 5.0
	 * 步骤：
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
	 * @return com.alibaba.fastjson.JSONObject
	 * 含义：路径下文件夹的集合和服务器操作系统的名称
	 * 取值范围：可能为空
	 */
	public JSONObject selectPath(long agent_id, @RequestParam(valueIfNull = "") String path) {
		//根据前端传过来的agent_id获取agent的ip和端口等基本信息
		Optional<Agent_info> agent_infoResult = Dbo.queryOneObject(Agent_info.class,
				"SELECT * FROM agent_info WHERE agent_id = ? ", agent_id);
		Agent_info agent_info = agent_infoResult.orElseThrow(() -> new BusinessException("根据Agent_id:" +
				agent_id + "查询不到Agent_info表信息"));
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
		//返回到前端的信息
		JSONObject data = (JSONObject) ar.getData();
		return data;
	}

	/**
	 * description: 将页面选择的需要采集的源文件路径保存到数据库
	 * date: 2019/9/12 14:57
	 * author: zxz
	 * version: 5.0
	 * 步骤：
	 * 1.获取json数组转成源文件设置表的集合
	 * 2.根据文件系统设置表id删除源文件设置表
	 * 3.遍历源文件设置表的集合，将对象的数据插入到源文件设置表
	 * 4.更新文件系统设置表
	 *
	 * @param file_sources_array String
	 *                           含义：源文件设置表的JSON数组的字符串，一条或多条
	 *                           取值范围：不能为空
	 * @return void
	 */
	public void saveFileSource(String file_sources_array) {
		//1.获取json数组转成源文件设置表的集合
		List<File_source> file_sources = new JSONArray().parseArray(file_sources_array, File_source.class);
		//获取fcs_id
		long fcs_id = file_sources.get(0).getFcs_id();
		//2.根据文件系统设置表id删除源文件设置表
		Dbo.execute("DELETE FROM file_source WHERE fcs_id = ?", fcs_id);
		//3.遍历源文件设置表的集合，将对象的数据插入到源文件设置表
		for (File_source file_source : file_sources) {
			//新增
			file_source.setFile_source_id(PrimayKeyGener.getNextId());
			if (file_source.add(Dbo.db()) != 1)
				throw new BusinessException("新增数据失败！data=" + file_source);
		}
		//4.更新文件系统设置表
		Optional<File_collect_set> file_collect_setResult = Dbo.queryOneObject(File_collect_set.class,
				"SELECT * FROM file_collect_set WHERE fcs_id = ? ", fcs_id);
		File_collect_set file_collect_set = file_collect_setResult.orElseThrow(() ->
				new BusinessException("根据fcs_id" + fcs_id + "查询不到file_collect_set表信息"));
		file_collect_set.setIs_sendok(IsFlag.Shi.getCode());
		if (file_collect_set.update(Dbo.db()) != 1)
			throw new BusinessException("更新数据失败！data=" + file_collect_set);
	}

}
