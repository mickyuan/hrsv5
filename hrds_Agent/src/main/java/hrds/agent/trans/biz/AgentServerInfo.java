package hrds.agent.trans.biz;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.utils.DateUtil;
import org.apache.commons.lang3.SystemUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * description: 获取当前程序所在的服务器信息的接口类<br>
 * date: 2019/9/11 17:38 <br>
 * author: zxz <br>
 * version: 5.0 <br>
 */
public class AgentServerInfo {
	
	/**
	 * description: 获取当前服务器的基本信息返回到前端 <br>
	 * date: 2019/9/11 17:58 <br>
	 * author: zxz <br>
	 * version: 5.0 <br>
	 *
	 * @param  
	 * @return com.alibaba.fastjson.JSONObject
	 */ 
	public Map<String,String> getServerInfo(){
		Map<String,String> map = new HashMap<String,String>();
		map.put("agentdate", DateUtil.getSysDate());
		map.put("agenttime", DateUtil.getSysTime());
		map.put("osName", SystemUtils.OS_NAME);
		map.put("userName", SystemUtils.USER_NAME);
		return map;
	}
}
