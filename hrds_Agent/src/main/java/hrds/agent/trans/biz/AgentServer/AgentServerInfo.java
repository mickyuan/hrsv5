package hrds.agent.trans.biz.AgentServer;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.utils.DateUtil;
import fd.ng.web.action.AbstractWebappBaseAction;
import fd.ng.web.annotation.RequestParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * description: 获取当前程序所在的服务器信息的接口类<br>
 * date: 2019/9/11 17:38 <br>
 * author: zxz <br>
 * version: 5.0 <br>
 */
public class AgentServerInfo extends AbstractWebappBaseAction {
	public static final ArrayList<String> noList;

	//需要过滤的系统目录
	static {
		noList = new ArrayList<>();
		noList.add("/bin");
		noList.add("/boot");
		noList.add("/lib");
		noList.add("/proc");
		noList.add("/sbin");
		noList.add("/srv");
		noList.add("/sys");
		noList.add("/lost+found");
		noList.add("/usr/bin");
		noList.add("/usr/sbin");
		noList.add("/var/cache");
		noList.add("/var/crash");
		noList.add("/var/lock");
		noList.add("/var/run");
	}

	/**
	 * description: 获取当前服务器的基本信息返回到前端 <br>
	 * date: 2019/9/11 17:58 <br>
	 * author: zxz <br>
	 * version: 5.0 <br>
	 * 步骤：
	 * 1.获取当前日期、时间、系统名称、用户名称
	 * 2.组成json返回到前端
	 *
	 * @param
	 * @return com.alibaba.fastjson.JSONObject
	 * 含义：包含Agent日期、时间、系统名称、用户名称的json
	 * 取值范围：不可为空
	 */
	public JSONObject getServerInfo() {
		JSONObject json = new JSONObject();
		json.put("agentdate", DateUtil.getSysDate());
		json.put("agenttime", DateUtil.getSysTime());
		json.put("osName", SystemUtils.OS_NAME);
		json.put("userName", SystemUtils.USER_NAME);
		return json;
	}

	/**
	 * description: 获取系统文件信息 <br>
	 * date: 2019/9/16 11:15 <br>
	 * author: zxz <br>
	 * version: 5.0 <br>
	 * 步骤：
	 * 1.获取前端参数、为空则给默认值
	 * 2.根据操作系统类型获取文件及文件夹返回到前端
	 *
	 * @param pathVal String
	 *                含义：页面选择的文件夹路径，为空则表示根目录
	 *                取值范围：可为空
	 * @param isFile  String
	 *                含义：是否显示当前目录下的文件，默认不显示
	 *                取值范围：可为空
	 * @return com.alibaba.fastjson.JSONObject
	 * 含义：当前文件夹下所有的目录(当isFile为true时返回当前文件夹下所有的目录和文件)
	 * 取值范围：可能为空
	 */
	public JSONObject getSystemFileInfo(@RequestParam(valueIfNull = "") String pathVal, String isFile) {
		/**
		 * 需要过滤的系统目录
		 */
		if (isFile != null) {
			isFile = "false";
		}
		String osName = SystemUtils.OS_NAME;
		JSONObject receiveMsg = new JSONObject();
		receiveMsg.put("osName", osName);
		if (StringUtils.isBlank(pathVal)) {
			File[] dvs = File.listRoots();
			List<String> list = new ArrayList<>();
			for (int i = 0; i < dvs.length; i++) {
				String name = dvs[i].getPath();
				if (!noList.contains(name)) {//取消系统的一些目录
					String path = dvs[i].getPath();
					list.add(name + "^" + path);
				}
			}
			receiveMsg.put("filelist", list);
		} else {
			File file = new File(pathVal);
			File[] array = file.listFiles();
			List<String> list = new ArrayList<>();
			for (int i = 0; i < array.length; i++) {
				if (array[i].isDirectory()) {
					String name = array[i].getName();
					String path_hy = array[i].getPath();
					if (!noList.contains(path_hy)) {//取消系统的一些目录
						String path = array[i].getPath();
						list.add(name + "^" + path + "^folder");
					}
				}
			}
			if ("true".equals(isFile)) {//是否需要显示文件
				for (int i = 0; i < array.length; i++) {
					if (!array[i].isDirectory()) {
						String name = array[i].getName();
						String path = array[i].getPath();
						list.add(name + "^" + path + "^file");
					}
				}
			}
			receiveMsg.put("filelist", list);
		}
		return receiveMsg;
	}
}
