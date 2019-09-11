package hrds.b.biz.agent.unstructuredfilecollect;

import fd.ng.core.utils.JsonUtil;
import fd.ng.netclient.http.HttpClient;
import fd.ng.netserver.conf.HttpServerConf;
import fd.ng.netserver.conf.HttpServerConfBean;
import fd.ng.web.action.ActionResult;
import fd.ng.web.annotation.RequestBean;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.entity.Agent_info;
import hrds.commons.entity.File_collect_set;
import hrds.commons.exception.BusinessException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * description: 服务端非结构化文件采集接口类 <br>
 * date: 2019/9/11 14:47 <br>
 * author: zxz <br>
 * version: 5.0 <br>
 */
public class UnstructuredFileCollectAction extends BaseAction {

	/**
	 * description: 该方法在页面点击添加非结构化文件采集时调用，获取非结构化采集配置页面初始化的值，当为编辑时，则同时返回回显的值 <br>
	 * date: 2019/9/11 16:29 <br>
	 * author: zxz <br>
	 * version: 5.0 <br>
	 *
	 * @param file_collect_set 文件系统设置表对象，接收页面传过来的参数Agent_id和fcs_id(文件系统采集ID)
	 * @return void
	 * 步骤：
	 * 1、根据前端传过来的agent_id获取agent的ip和端口等基本信息
	 * 2、调用远程Agent的后端代码获取采集服务器上的日期、时间、操作系统类型和主机名等基本信息
	 * 3、返回到前端
	 */
	public Map<String,Object> addFileCollect(@RequestBean File_collect_set file_collect_set) {
		//1、根据前端传过来的agent_id获取agent的ip和端口等基本信息
		Optional<Agent_info> agent_infoResult = Dbo.queryOneObject(Agent_info.class,
				"SELECT * FROM agent_info WHERE agent_id = ? ", file_collect_set.getAgent_id());
		Agent_info agent_info = agent_infoResult.orElseThrow(() -> new BusinessException("根据Agent_id" +
				file_collect_set.getAgent_id() + "查询不到Agent_info表信息"));
		//2、调用远程Agent的后端代码获取采集服务器上的日期、时间、操作系统类型和主机名等基本信息
		HttpServerConfBean test = HttpServerConf.getHttpServer("agentServerInfo");
		String webContext = test.getWebContext();
		String actionPattern = test.getActionPattern();
		String url = "http://" + agent_info.getAgent_ip() + ":" + agent_info.getAgent_port() + webContext;
		//调用工具类方法给agent发消息，并获取agent响应
		HttpClient.ResponseValue resVal = new HttpClient().post(url + actionPattern);
		ActionResult ar = JsonUtil.toObject(resVal.getBodyString(), ActionResult.class);
		//返回到前端的信息
		Map<String,Object> data = (HashMap<String,Object>)ar.getData();
		//文件系统采集ID不为空则表示当前操作为编辑，获取文件系统设置表信息
		if(file_collect_set.getFcs_id() != null){
			Optional<File_collect_set> file_collect_set_info = Dbo.queryOneObject(File_collect_set.class,
					"SELECT * FROM file_collect_set WHERE fcs_id = ?", file_collect_set.getFcs_id());
			File_collect_set set_info = file_collect_set_info.orElseThrow(() -> new BusinessException("根据fcs_id" +
					file_collect_set.getAgent_id() + "查询不到file_collect_set表信息"));
			data.put("file_collect_set_info",set_info);
		}
		return data;
	}
}
