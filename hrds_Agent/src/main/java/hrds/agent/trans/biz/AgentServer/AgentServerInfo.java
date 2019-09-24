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
//FIXME @徐超
// Agent等程序，也要有自己的 BaseAction。因为也需要做权限控制，比如每次交互都有传递一个固定的令牌做验证
public class AgentServerInfo extends AbstractWebappBaseAction {
	//系统目录的集合
	public static final ArrayList<String> noList;

	//需要过滤的系统目录
	static {
		//FIXME 少了 dev, etc, home(除了自己外都不能访问), root, lib64, media, run
		// 太多了，所以，应该改成：默认只允许访问自己的主目录和/tmp，其他允许访问的目录在配置文件中定义
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
	//FIXME 不能用JSON
	public JSONObject getServerInfo() {
		JSONObject json = new JSONObject();
		json.put("agentdate", DateUtil.getSysDate());
		json.put("agenttime", DateUtil.getSysTime());
		//FIXME 不要用第3方包
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
	 * 1.获取前端参数，为空则给默认值
	 * 2.根据操作系统类型获取文件及文件夹
	 * 3.判断是否显示文件，将值放到list
	 * 4.返回到前端
	 * @param pathVal String
	 *                含义：页面选择的文件夹路径，为空则表示根目录
	 *                取值范围：可为空
	 * @param isFile  String
	 *                含义：是否显示当前目录下的文件，默认不显示
	 *                取值范围：可为空
	 * @return com.alibaba.fastjson.JSONObject
	 * 含义：当前文件夹下所有的目录(当isFile为true时返回当前文件夹下所有的目录和文件)
	 * 取值范围：不会为空
	 */
	//FIXME RequestParam 有 nullable
	public JSONObject getSystemFileInfo(@RequestParam(valueIfNull = "") String pathVal, @RequestParam(valueIfNull = "") String isFile) {
		//是否需要显示文件为空则默认为false不显示
		//FIXME 为什么不用 boolean 类型？
		if (StringUtils.isBlank(isFile)) {
			isFile = "false";
		}
		//获取操作系统的名称返回到前端
		String osName = SystemUtils.OS_NAME;
		JSONObject receiveMsg = new JSONObject();
		receiveMsg.put("osName", osName);
		File[] file_array = null;
		//如果需要显示文件夹的路径为空，则默认取根目录下的文件和文件夹
		if (StringUtils.isBlank(pathVal)) {
			file_array = File.listRoots();
		} else {
			file_array = new File(pathVal).listFiles();
		}
		//取到文件和文件夹则进行遍历
		if (file_array != null && file_array.length > 0) {
			List<String> list = new ArrayList<>();
			for (int i = 0; i < file_array.length; i++) {
				//是文件夹直接放到list
				if (file_array[i].isDirectory()) {
					String name = file_array[i].getName();
					String path_hy = file_array[i].getPath();
					if (!noList.contains(path_hy)) {//取消系统的一些目录
						String path = file_array[i].getPath();
						list.add(name + "^" + path + "^folder");
					}
				}
			}
			//需要显示文件且是文件则放到list
			if ("true".equals(isFile)) {
				for (int i = 0; i < file_array.length; i++) {
					if (!file_array[i].isDirectory()) {
						String name = file_array[i].getName();
						String path = file_array[i].getPath();
						list.add(name + "^" + path + "^file");
					}
				}
			}
			receiveMsg.put("filelist", list);
		}
		return receiveMsg;
	}
}
