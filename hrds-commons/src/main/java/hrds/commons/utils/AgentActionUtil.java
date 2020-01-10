package hrds.commons.utils;

import fd.ng.netserver.conf.HttpServerConfBean;
import fd.ng.web.util.Dbo;
import hrds.commons.entity.Agent_down_info;
import hrds.commons.exception.BusinessException;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取调用Agent接口的Url
 * date: 2019/9/24 10:07
 * author: zxz
 */
public class AgentActionUtil {
	//所有操作方法的集合，新添加的方法必须放到集合中
	private static final List<String> list;
	//获取Agent服务器基本信息：用户名称、操作系统名称、日期、时间
	public static final String GETSERVERINFO = "/hrds/agent/trans/biz/agentserver/getServerInfo";
	//获取Agent指定目录下的文件及文件夹
	public static final String GETSYSTEMFILEINFO = "/hrds/agent/trans/biz/agentserver/getSystemFileInfo";
	//测试Agent是否可以连接某个指定的数据库
	public static final String TESTCONNECTION = "/hrds/agent/trans/biz/testConn";
	//agent调用海云服务端的保存错误信息到数据库的接口
	public static final String SAVEERRORINFO = "/hrds/server/saveErrorInfo";
	//agent连接服务端批量添加source_file_attribute
	public static final String BATCHADDSOURCEFILEATTRIBUTE = "/hrds/server/batchAddSourceFileAttribute";
	//agent连接服务端批量更新source_file_attribute
	public static final String BATCHUPDATESOURCEFILEATTRIBUTE = "/hrds/server/batchUpdateSourceFileAttribute";
	//根据数据库连接获取数据库下表信息
	public static final String GETDATABASETABLE = "/hrds/agent/trans/biz/database/getDatabaseTable";
	//根据数据库连接和表名获取表的字段信息
	public static final String GETTABLECOLUMN = "/hrds/agent/trans/biz/database/getTableColumn";
	//agent调用海云服务端保存采集情况信息表
	public static final String SAVECOLLECTCASE = "/hrds/server/saveCollectCase";
	//调用agent端执行文件采集
	public static final String EXECUTEFILECOLLECT = "/hrds/agent/trans/biz/unstructuredFileCollect/execute";
	//测试并行抽取SQL
	public static final String TESTPARALLELSQL = "/hrds/agent/trans/biz/testParallelSQL";
	//获取单表数据总量
	public static final String GETTABLECOUNT = "/hrds/agent/trans/biz/getTableCount";
	//获取自定义SQL抽取的采集列
	public static final String GETCUSTCOLUMN = "/hrds/agent/trans/biz/database/getCustColumn";
	//单表保存或者更新source_file_attribute
	public static final String ADDSOURCEFILEATTRIBUTE = "/hrds/server/addSourceFileAttribute";
	//向agent端发送一个数据库直连采集任务
	public static final String SENDDBCOLLCTTASKINFO = "/hrds/agent/trans/biz/jdbccollect/execute";

	static {
		list = new ArrayList<>();
		list.add(GETSERVERINFO);
		list.add(GETSYSTEMFILEINFO);
		list.add(TESTCONNECTION);
		list.add(SAVEERRORINFO);
		list.add(BATCHADDSOURCEFILEATTRIBUTE);
		list.add(BATCHUPDATESOURCEFILEATTRIBUTE);
		list.add(GETDATABASETABLE);
		list.add(GETTABLECOLUMN);
		list.add(SAVECOLLECTCASE);
		list.add(EXECUTEFILECOLLECT);
		list.add(TESTPARALLELSQL);
		list.add(GETTABLECOUNT);
		list.add(GETCUSTCOLUMN);
		list.add(ADDSOURCEFILEATTRIBUTE);
		list.add(SENDDBCOLLCTTASKINFO);
	}

	private AgentActionUtil() {
	}

	/**
	 * 根据agent_id、用户id和方法全路径获取访问远端Agent的接口全路径
	 * 1.判断方法名有没有被登记
	 * 2.数据可访问权限处理方式，传入用户需要有Agent对应数据的访问权限，根据agent_id获取信息
	 * 3.拼接调用需要的url
	 *
	 * @param agent_id   含义：获取部署agent信息表的唯一标识
	 *                   取值范围：不可为空
	 * @param user_id    含义：数据可访问权限处理唯一标识
	 *                   取值范围：不可为空
	 * @param methodName 含义：接口方法的全路径，调用者需要使用本方法定义的变量
	 *                   取值范围：不可为空
	 * @return String
	 * 含义：调用已经部署好的Agent接口的全路径
	 * 取值范围：不可为空
	 */
	public static String getUrl(long agent_id, long user_id, String methodName) {
		//1.判断方法名有没有被登记
		if (!list.contains(methodName)) {
			throw new BusinessException("被调用的agent接口没有登记");
		}
		//2.数据可访问权限处理方式，传入用户需要有Agent对应数据的访问权限，根据agent_id获取信息
		Agent_down_info agent_info = Dbo.queryOneObject(Agent_down_info.class, "SELECT * FROM "
						+ Agent_down_info.TableName + " WHERE agent_id = ?  AND user_id = ?",
				agent_id, user_id).orElseThrow(() ->
				new BusinessException("根据Agent_id:" + agent_id + "查询不到agent_down_info表信息"));
		//XXX 这里无法判断页面是规定按照配置文件里面一样填/action/*还是/action/还是/action
		//XXX 因此这里就判断如果是/*或者/结尾的就将结尾的那个字符去掉，保证拼接的url没有多余的字符
		if (agent_info.getAgent_pattern().endsWith("/*")) {
			agent_info.setAgent_pattern(agent_info.getAgent_pattern().substring(0,
					agent_info.getAgent_pattern().length() - 2));
		}
		if (agent_info.getAgent_pattern().endsWith("/")) {
			agent_info.setAgent_pattern(agent_info.getAgent_pattern().substring(0,
					agent_info.getAgent_pattern().length() - 1));
		}
		//3.拼接调用需要的url
		return "http://" + agent_info.getAgent_ip() + ":" + agent_info.getAgent_port()
				+ agent_info.getAgent_context() + agent_info.getAgent_pattern() + methodName;
	}

	public static String getServerUrl(HttpServerConfBean confBean, String methodName) {
		//1.判断方法名有没有被登记
		if (!list.contains(methodName)) {
			throw new BusinessException("被调用的agent接口没有登记");
		}
		//XXX 这里无法判断页面是规定按照配置文件里面一样填/action/*还是/action/还是/action
		//XXX 因此这里就判断如果是/*或者/结尾的就将结尾的那个字符去掉，保证拼接的url没有多余的字符
		if (confBean.getActionPattern().endsWith("/*")) {
			confBean.setActionPattern(confBean.getActionPattern().substring(0,
					confBean.getActionPattern().length() - 2));
		}
		if (confBean.getActionPattern().endsWith("/")) {
			confBean.setActionPattern(confBean.getActionPattern().substring(0,
					confBean.getActionPattern().length() - 1));
		}
		//2.拼接调用需要的url
		return "http://" + confBean.getHost() + ":" + confBean.getHttpPort() + confBean.getWebContext()
				+ confBean.getActionPattern() + methodName;
	}

}
