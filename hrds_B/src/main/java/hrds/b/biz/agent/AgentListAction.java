package hrds.b.biz.agent;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.resultset.Result;
import fd.ng.web.annotation.RequestParam;
import fd.ng.web.util.Dbo;
import fd.ng.web.util.RequestUtil;
import fd.ng.web.util.ResponseUtil;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.AgentType;
import hrds.commons.codes.DataBaseCode;
import hrds.commons.codes.IsFlag;
import hrds.commons.exception.AppSystemException;
import hrds.commons.exception.BusinessException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 数据库采集应用管理端数据源Agent列表后台服务类
 * @author: WangZhengcheng
 * @create: 2019-09-03 14:17
 **/
public class AgentListAction extends BaseAction {

	private static final Logger logger = LogManager.getLogger();

	/**
	 * @Description: 获取数据源Agent列表信息
	 * @return: fd.ng.db.resultset.Result
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/3
	 * 步骤：
	 * 1、获取用户ID
	 * 2、根据用户ID去数据库中查询数据源信息
	 */
	public Result getAgentSetUpInfo() {
		//1、获取用户ID
		Long userId = getUserId();
		//2、根据用户ID去数据库中查询数据源信息
		return Dbo.queryResult("SELECT datas.source_id,datas.datasource_name " +
				"FROM agent_info age JOIN data_source datas ON age.source_id = datas.SOURCE_ID " +
				"WHERE age.user_id = ? GROUP BY datas.source_id,datas.datasource_name", userId);
	}

	/**
	 * @Description: 根据sourceId和agentType获取相应信息
	 * @Param: [sourceId : 数据源ID, 取值范围 : long]
	 * @Param: [agentType : agent类型(数据库、数据文件、非结构化、非结构化。FTP), 取值范围 : 1-5(char)]
	 * @return: fd.ng.db.resultset.Result
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/3
	 * 步骤：
	 * 1、根据sourceId和agentType查询数据库获取相应信息
	 */
	public Result getAgentInfo(long sourceId, String agentType) {
		//1、根据sourceId和agentType查询数据库获取相应信息
		return Dbo.queryResult("SELECT * FROM agent_info WHERE source_id = ? AND agent_type = ?",
				sourceId, agentType);
	}

	/**
	 * @Description: 根据sourceId和agentId获取某agent下所有任务的信息
	 * @Param: [sourceId : 数据源ID, 取值范围 : long]
	 * @Param: [agentId : AgentID, 取值范围 : long]
	 * @return: fd.ng.db.resultset.Result
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/3
	 * 步骤：
	 * 1、获取用户ID
	 * 2、判断在当前用户，当前数据源下，某一类型的agent是否存在
	 * 3、如果存在，查询结果中应该有且只有一条数据
	 * 4、判断该agent是那种类型，并且根据类型，到对应的数据库表中查询采集任务管理详细信息
	 * 5、对详细信息中的采集频率进行格式化
	 * 6、返回结果
	 */
	//TODO 采集频率格式化未完成
	public Result getTaskInfo(long sourceId, long agentId) {
		//1、获取用户ID
		Long userId = getUserId();
		//2、判断在当前用户，当前数据源下，某一类型的agent是否存在
		Result result = Dbo.queryResult("select ai.* from data_source ds " +
				" left join agent_info ai on ds.SOURCE_ID = ai.SOURCE_ID " +
				" where ds.source_id=? AND ai.user_id = ? " +
				" AND ai.agent_id = ?", sourceId, userId, agentId);
		//3、如果存在，查询结果中应该有且只有一条数据
		if (result.isEmpty()) {
			throw new BusinessException("未找到Agent");
		}
		if (result.getRowCount() != 1) {
			throw new BusinessException("找到的的Agent不唯一");
		}

		//4、判断该agent是那种类型，并且根据类型，到对应的数据库表中查询采集任务管理详细信息
		StringBuilder sqlSB = new StringBuilder();
		//数据库直连采集Agent
		if (AgentType.ShuJuKu == AgentType.getCodeObj(result.getString(0, "agent_type"))) {
			sqlSB.append(" SELECT ds.DATABASE_ID ID,ds.task_name task_name,ds.AGENT_ID AGENT_ID, ")
					.append(" cf.execute_time execute_time, cf.fre_week fre_week,cf.run_way run_way,cf.fre_month fre_month, ")
					.append(" cf.fre_day fre_day,gi.source_id source_id,cf.collect_type ")
					.append(" FROM database_set ds JOIN collect_frequency cf ON ds.database_id = cf.COLLECT_SET_ID ")
					.append(" LEFT JOIN agent_info gi ON ds.Agent_id = gi.Agent_id ")
					.append(" where ds.Agent_id=? and ds.is_sendok = ? ");
		}
		//数据文件Agent
		else if (AgentType.DBWenJian == AgentType.getCodeObj(result.getString(0, "agent_type"))) {
			sqlSB.append(" SELECT ds.DATABASE_ID ID,ds.task_name task_name,ds.AGENT_ID AGENT_ID, ")
					.append(" cf.execute_time execute_time, cf.fre_week fre_week,cf.run_way run_way,cf.fre_month fre_month, ")
					.append(" cf.fre_day fre_day,gi.source_id source_id,cf.collect_type ")
					.append(" FROM database_set ds JOIN collect_frequency cf ON ds.database_id = cf.COLLECT_SET_ID ")
					.append(" LEFT JOIN agent_info gi ON ds.Agent_id = gi.Agent_id ")
					.append(" where ds.Agent_id=? and ds.is_sendok = ? ");
		}
		//半结构化采集Agent
		else if (AgentType.DuiXiang == AgentType.getCodeObj(result.getString(0, "agent_type"))) {
			sqlSB.append(" SELECT fs.odc_id id,fs.obj_collect_name task_name,fs.AGENT_ID AGENT_ID, ")
					.append(" cf.execute_time execute_time,cf.fre_week fre_week,cf.run_way run_way,cf.fre_month fre_month, ")
					.append(" cf.fre_day fre_day,gi.source_id,cf.collect_type  ")
					.append(" FROM object_collect fs JOIN collect_frequency cf ")
					.append(" ON fs.odc_id = cf.COLLECT_SET_ID LEFT JOIN agent_info gi ON gi.Agent_id = cf.Agent_id ")
					.append(" WHERE fs.Agent_id = ? AND fs.is_sendok = ? ");
		}
		//FtpAgent
		else if (AgentType.FTP == AgentType.getCodeObj(result.getString(0, "agent_type"))) {
			sqlSB.append(" SELECT fs.ftp_id id,fs.ftp_name task_name,fs.AGENT_ID AGENT_ID,cf.execute_time execute_time, ")
					.append(" cf.fre_week fre_week,cf.run_way run_way,cf.fre_month fre_month, ")
					.append(" cf.fre_day fre_day,gi.source_id,cf.collect_type ")
					.append(" FROM ftp_collect fs JOIN collect_frequency cf ON fs.ftp_id = cf.COLLECT_SET_ID LEFT JOIN agent_info gi ")
					.append(" ON gi.Agent_id = cf.Agent_id ")
					.append(" WHERE fs.Agent_id = ? and fs.is_sendok = ? ");
		}
		//非结构化Agent
		else {
			sqlSB.append(" SELECT fs.fcs_id id,fs.fcs_name task_name,fs.AGENT_ID AGENT_ID, ")
					.append(" cf.execute_time execute_time,cf.fre_week fre_week,cf.run_way run_way,cf.fre_month fre_month, ")
					.append(" cf.fre_day fre_day,gi.source_id,cf.collect_type ")
					.append(" FROM file_collect_set fs JOIN collect_frequency cf ON fs.fcs_id = cf.COLLECT_SET_ID ")
					.append(" LEFT JOIN agent_info gi ON gi.Agent_id = cf.Agent_id ")
					.append(" where fs.Agent_id=? and fs.is_sendok = ? ");
		}
		Result agentInfo = Dbo.queryResult(sqlSB.toString(), result.getLong(0, "agent_id"),
				IsFlag.Shi.getCode());
		//5、对详细信息中的采集频率进行格式化
		fromatFrequency(agentInfo);
		//6、返回结果
		return agentInfo;
	}

	/**
	 * @Description: 查看任务日志
	 * @Param: [agentId : agentID, 取值范围 : long]
	 * @Param: [logType : 日志类型(完整日志、错误日志), 取值范围 : String(All, Wrong)]
	 * @Param: [readNum : 查看的行数, 取值范围 : int]
	 * @return: java.lang.String
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/3
	 * 步骤：
	 * 1、对显示日志条数做处理，该方法在加载页面时被调用，readNum可以不传，则默认显示100条，如果用户在页面上进行了选择并点击查看按钮，
	 *    则最多给用户显示1000条日志
	 * 2、调用方法读取日志并返回
	 */
	//TODO 调用方法获取日志,目前工具类(ReadLog, I18nMessage)不存在
	public String viewTaskLog(long agentId, String logType,
	                          @RequestParam(nullable = true, valueIfNull = "100") int readNum) {
		//1、对显示日志条数做处理，该方法在加载页面时被调用，readNum可以不传，则默认显示100条，
		// 如果用户在页面上进行了选择并点击查看按钮，则最多给用户显示1000条日志
		int num = 0;
		if ((readNum > 1000)) {
			num = 1000;
		} else {
			num = readNum;
		}
		//2、调用方法读取日志并返回
		return getTaskLog(agentId, getUserId(), logType, readNum).get("log");
	}

	/**
	 * @Description: 任务日志下载
	 * @Param: [agentId : agentID, 取值范围 : long]
	 * @Param: [logType : 日志类型(完整日志、错误日志), 取值范围 : String(All, Wrong)]
	 * @Param: [readNum : 查看的行数, 取值范围 : int]
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/3
	 * 步骤：
	 * 1、对显示日志条数做处理，该方法在加载页面时被调用，readNum可以不传，则默认显示100条，如果用户在页面上进行了选择并点击查看按钮，
	 *    如果用户输入的条目多于1000，则给用户显示3000条
	 * 2、调用方法读取日志，获得日志信息和日志文件路径
	 * 3、将日志信息由字符串转为byte[]
	 * 4、创建日志文件
	 * 5、得到本次http交互的request和response
	 * 6、设置响应头信息
	 * 7、使用response获得输出流，完成文件下载
	 */
	//TODO 调用方法获取日志,目前工具类(ReadLog, I18nMessage)不存在
	public void downloadTaskLog(long agentId, String logType,
	                            @RequestParam(nullable = true, valueIfNull = "100") int readNum) {
		try {
			//1、对显示日志条数做处理，该方法在加载页面时被调用，readNum可以不传，则默认显示100条，
			// 如果用户在页面上进行了选择并点击查看按钮，如果用户输入的条目多于1000，则给用户显示3000条
			int num = 0;
			if ((readNum > 1000)) {
				num = 3000;
			} else {
				num = readNum;
			}
			//2、调用方法读取日志，获得日志信息和日志文件路径
			Map<String, String> taskLog = getTaskLog(agentId, getUserId(), logType, readNum);

			//3、将日志信息由字符串转为byte[]
			byte[] bytes = taskLog.get("log").getBytes();
			//4、创建日志文件
			File file = new File(taskLog.get("filePath"));

			//5、得到本次http交互的request和response
			HttpServletResponse response = ResponseUtil.getResponse();
			HttpServletRequest request = RequestUtil.getRequest();

			//6、设置响应头信息
			response.reset();
			if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {
				// 对firefox浏览器做特殊处理
				response.setHeader("content-disposition", "attachment;filename=" +
						new String(taskLog.get("filePath").getBytes(DataBaseCode.UTF_8.getValue()), DataBaseCode.ISO_8859_1.getValue()));
			} else {
				response.setHeader("content-disposition", "attachment;filename=" +
						URLEncoder.encode(taskLog.get("filePath"), DataBaseCode.UTF_8.getValue()));
			}
			response.setContentType("APPLICATION/OCTET-STREAM");
			OutputStream out = response.getOutputStream();
			//7、使用response获得输出流，完成文件下载
			out.write(bytes);
			out.flush();
		} catch (IOException e) {
			throw new AppSystemException(e);
		}
	}

	/**
	 * @Description: 根据ID删除半结构化采集任务数据
	 * @Param: [collectSetId : 源文件属性表ID, 取值范围 : long]
	 * @return: void
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/10
	 * 步骤：
	 * 1、根据collectSetId在源文件属性表(source_file_attribute)中获得采集的原始表名(table_name)，可能有多条
	 * 2、调用IOnWayCtrl.checkExistsTask()方法对将要删除的信息进行检查
	 * 3、在对象采集设置表(object_collect)中删除该条数据
	 * 4、在卸数作业参数表(collect_frequency)中删除该条数据
	 */
	public void deletehalfStructTask(long collectSetId) {
		//1、根据collectSetId在源文件属性表(source_file_attribute)中获得采集的原始表名(table_name)，可能有多条
		List<Map<String, Object>> maps = Dbo.queryList(
				"select table_name from source_file_attribute where collect_set_id = ?", collectSetId);
		if (maps.isEmpty()) {
			throw new BusinessException("源文件属性表中未找到采集的原始表名");
		}
		List<String> list = new ArrayList<>();
		for (Map<String, Object> map : maps) {
			list.add((String) map.get("table_name"));
		}
		//2、调用IOnWayCtrl.checkExistsTask()方法对将要删除的信息进行检查
		//IOnWayCtrl.checkExistsTask(list, DataSourceType.DML.toString(), db);
		//3、在对象采集设置表(object_collect)中删除该条数据，有且只有一条
		int firNum = Dbo.execute("delete from object_collect where odc_id = ?", collectSetId);
		if (firNum != 1) {
			if (firNum == 0) throw new BusinessException("object_collect表中没有数据被删除!");
			else throw new BusinessException("object_collect表删除数据异常!");
		}
		//4、在卸数作业参数表(collect_frequency)中删除该条数据，有且只有一条
		int secExecute = Dbo.execute("delete from collect_frequency where collect_set_id = ?", collectSetId);
		if (secExecute != 1) {
			if (secExecute == 0) throw new BusinessException("collect_frequency表中没有数据被删除!");
			else throw new BusinessException("collect_frequency表删除数据异常!");
		}
	}

	/**
	 * @Description: 根据ID删除FTP采集任务数据
	 * @Param: [collectSetId : 源文件属性表ID, 取值范围 : long]
	 * @return: void
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/10
	 * 步骤：
	 * 1、根据collectSetId在源文件属性表(source_file_attribute)中获得采集的原始表名(table_name)，可能有多条
	 * 2、调用IOnWayCtrl.checkExistsTask()方法对将要删除的信息进行检查
	 * 3、在FTP采集设置表(ftp_collect)中删除该条数据
	 * 4、在卸数作业参数表(collect_frequency)中删除该条数据
	 */
	public void deleteFTPTask(long collectSetId) {
		//1、根据collectSetId在源文件属性表(source_file_attribute)中获得采集的原始表名(table_name)，可能有多条
		List<Map<String, Object>> maps = Dbo.queryList(
				"select table_name from source_file_attribute where collect_set_id = ?", collectSetId);
		if (maps.isEmpty()) {
			throw new BusinessException("源文件属性表中未找到采集的原始表名");
		}
		List<String> list = new ArrayList<>();
		for (Map<String, Object> map : maps) {
			list.add((String) map.get("table_name"));
		}
		//2、调用IOnWayCtrl.checkExistsTask()方法对将要删除的信息进行检查
		//IOnWayCtrl.checkExistsTask(list, DataSourceType.DML.toString(), db);
		//3、在FTP采集设置表(ftp_collect)中删除该条数据，有且只有一条
		int firNum = Dbo.execute("delete from ftp_collect where odc_id = ?", collectSetId);
		if (firNum != 1) {
			if (firNum == 0) throw new BusinessException("ftp_collect表中没有数据被删除!");
			else throw new BusinessException("ftp_collect表删除数据异常!");
		}
		//4、在卸数作业参数表(collect_frequency)中删除该条数据，有且只有一条
		int secExecute = Dbo.execute("delete from collect_frequency where collect_set_id = ?", collectSetId);
		if (secExecute != 1) {
			if (secExecute == 0) throw new BusinessException("collect_frequency表中没有数据被删除!");
			else throw new BusinessException("collect_frequency表删除数据异常!");
		}
	}

	/**
	 * @Description: 根据ID和Agent类型删除数据库直连，非结构化文件，DB文件采集任务数据
	 * @Param: [collectSetId : 源文件属性表ID, 取值范围 : long]
	 * @Param: [agentType : Agent类型(数据库、数据文件、非结构化、非结构化。FTP), 取值范围 : 1-5(char)]
	 * @return: void
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/10
	 * 步骤：
	 * 1、判断Agent类型
	 * 2、如果是数据库直连采集任务
	 *      2-1、在数据库设置删除对应的记录
	 *      2-2、在表对应字段表中找到对应的记录并删除
	 *      2-3、在数据库对应表删除对应的记录
	 * 3、如果是结构化文件、DB文件
	 *      3-1、在文件系统设置表删除对应的记录
	 *      3-2、在文件源设置表删除对应的记录
	 * 4、对其他类型的任务进行统一处理
	 *      4-1、在卸数作业参数表删除对应的记录
	 *      4-2、在压缩作业参数表删除对应的记录
	 *      4-3、在传送作业参数表删除对应的记录
	 *      4-4、在清洗作业参数表删除对应的记录
	 *      4-5、在hdfs存储作业参数表删除对应的记录
	 */
	public void deleteOtherTask(long collectSetId, String agentType) {
		//1、判断Agent类型
		if (AgentType.ShuJuKu == AgentType.getCodeObj(agentType)) {
			//2、如果是数据库直连采集任务
			List<Map<String, Object>> maps = Dbo.queryList(
					"select hbase_name from source_file_attribute where collect_set_id = ?", collectSetId);
			if (maps.isEmpty()) {
				throw new BusinessException("源文件属性表中未找到系统内对应表名");
			}
			List<String> list = new ArrayList<>();
			for (Map<String, Object> map : maps) {
				list.add((String) map.get("hbase_name"));
			}
			//IOnWayCtrl.checkExistsTask(list, DataSourceType.DML.toString(), db);
			//2-1、在数据库设置表删除对应的记录，有且只有一条
			int firNum = Dbo.execute("delete from database_set where database_id =?", collectSetId);
			if (firNum != 1) {
				if (firNum == 0) throw new BusinessException("database_set表中没有数据被删除!");
				else throw new BusinessException("database_set表删除数据异常!");
			}

			//2-2、在表对应字段表中找到对应的记录并删除，可能会有多条
			int secNum = Dbo.execute("delete from table_column where EXISTS" +
					"(select 1 from table_info ti where database_id = ? and table_column.table_id=ti.table_id)",
					collectSetId);
			if (secNum == 0) {
				throw new BusinessException("table_column表中没有数据被删除!");
			}

			//2-3、在数据库对应表删除对应的记录,可能会有多条
			int thiExecute = Dbo.execute("delete from table_info where database_id = ?", collectSetId);
			if (thiExecute == 0) {
				throw new BusinessException("table_info表中没有数据被删除!");
			}
		}
		//3、如果是结构化文件、DB文件
		else {
			//3-1、在文件系统设置表删除对应的记录，有且只有一条
			int fouNum = Dbo.execute("delete  from file_collect_set where fcs_id =?", collectSetId);
			if (fouNum != 1) {
				if (fouNum == 0) throw new BusinessException(String.format("file_collect_set表中没有数据被删除!"));
				else throw new BusinessException("file_collect_set表删除数据异常!");
			}
			//3-2、在文件源设置表删除对应的记录，有且只有一条
			int fifNum = Dbo.execute("delete  from file_source where fcs_id =?", collectSetId);
			if (fifNum != 1) {
				if (fifNum == 0) throw new BusinessException("file_source表中没有数据被删除!");
				else throw new BusinessException("file_source表删除数据异常!");
			}
		}
		//4、对其他类型的任务进行统一处理
		//4-1、在卸数作业参数表删除对应的记录,有且只有一条
		int sixNum = Dbo.execute("delete  from collect_frequency where collect_set_id =?", collectSetId);
		if (sixNum != 1) {
			if (sixNum == 0) throw new BusinessException("collect_frequency表中没有数据被删除!");
			else throw new BusinessException("collect_frequency表删除数据异常!");
		}
		//4-2、在压缩作业参数表删除对应的记录,有且只有一条
		int sevNum = Dbo.execute("delete  from collect_reduce where collect_set_id =?", collectSetId);
		if (sevNum != 1) {
			if (sevNum == 0) throw new BusinessException("collect_reduce表中没有数据被删除!");
			else throw new BusinessException("collect_reduce表删除数据异常!");
		}
		//4-3、在传送作业参数表删除对应的记录,有且只有一条
		int eigNum = Dbo.execute("delete  from collect_transfer where collect_set_id =?", collectSetId);
		if (eigNum != 1) {
			if (eigNum == 0) throw new BusinessException("collect_transfer表中没有数据被删除!");
			else throw new BusinessException("collect_transfer表删除数据异常!");
		}
		//4-4、在清洗作业参数表删除对应的记录,有且只有一条
		int ninNum = Dbo.execute("delete  from collect_clean where collect_set_id =?", collectSetId);
		if (ninNum != 1) {
			if (ninNum == 0) throw new BusinessException("collect_clean表中没有数据被删除!");
			else throw new BusinessException("collect_clean表删除数据异常!");
		}
		//4-5、在hdfs存储作业参数表删除对应的记录,有且只有一条
		int tenNum = Dbo.execute("delete  from collect_hdfs where collect_set_id =?", collectSetId);
		if (tenNum != 1) {
			if (tenNum == 0) throw new BusinessException("collect_hdfs表中没有数据被删除!");
			else throw new BusinessException("collect_hdfs表删除数据异常!");
		}
	}

	/**
	 * @Description: 数据采集任务生成作业
	 * @return: java.lang.String
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/6
	 * 步骤：
	 * 1、调用EtlJobUtil工具类，得到结果并返回
	 */
	public String buildJob() {
		String proHtml = "";
		//proHtml = EtlJobUtil.proHtml();
		return proHtml;
	}

	/**
	 * @Description: 根据taskId获得工程信息
	 * @Param: [taskId : 任务ID, 取值范围 : String]
	 * @return: java.lang.String
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/6
	 * 步骤：
	 * 1、调用EtlJobUtil工具类，得到结果并返回
	 */
	public String selectProject(String taskId) {
		String proHtml = "";
		//proHtml = EtlJobUtil.taskHtml(taskId);
		return proHtml;
	}

	/**
	 * @Description: 在生成作业时保存工程信息
	 * @Param: [proId : 工程ID, 取值范围 : String]
	 * @Param: [taskId : 任务ID, 取值范围 : String]
	 * @Param: [jobId : 作业ID, 取值范围 : String]
	 * @Param: [jobType : 作业类型, 取值范围 : String(CollectType枚举Code)]
	 * @return: void
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/6
	 * 步骤：
	 * 1、调用EtlJobUtil工具类，保存job
	 */
	public void saveProjectInfo(String proId, String taskId, String jobId, String jobType) {
		//EtlJobUtil.saveJob(jobId, DataSourceType.DCL.toString(), proId, taskId, jobType);
	}

	/**
	 * @Description: 根据任务ID发送半结构化文件采集任务
	 * @Param: [taskId : 任务ID, 取值范围 : String]
	 * @return: void
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/10
	 */
	public void sendHalfStructTask(String taskId) {
		//SendMsg.sendObjectCollect2Agent(taskId);
	}

	/**
	 * @Description: 根据任务ID发送FTP采集任务
	 * @Param: [taskId : 任务ID, 取值范围 : String]
	 * @return: void
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/10
	 */
	public void sendFTPTask(String taskId) {
		//SendMsg.sendFTP2Agent(taskId);
	}

	/**
	 * @Description: 根据任务ID发送数据库直连、DB文件、非结构化文件采集任务
	 * @Param: [taskId : 任务ID, 取值范围 : String]
	 * @return: void
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/10
	 */
	public void sendOtherTask(String taskId) {
		//SendMsg.sendMsg2Agent(taskId);
	}

	/**
	 * @Description: 根据sourceId查询出设置完成的数据库采集任务和DB文件采集任务的任务ID
	 * @Param: [sourceId : 数据源ID, 取值范围 : long]
	 * @return: fd.ng.db.resultset.Result
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/10
	 */
	public Result getDBAndDFTaskBySourceId(long sourceId) {
		return Dbo.queryResult("SELECT database_id " +
				"FROM data_source ds " +
				"JOIN agent_info ai ON ds.source_id = ai.source_id " +
				"JOIN database_set das ON ai.agent_id = das.agent_id " +
				"WHERE ds.source_id = ? AND das.is_sendok = ?", sourceId, IsFlag.Shi.getCode());
	}

	/**
	 * @Description: 根据sourceId查询出设置完成的非结构化文件采集任务的任务ID
	 * @Param: [sourceId : 数据源ID, 取值范围 : long]
	 * @return: fd.ng.db.resultset.Result
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/10
	 */
	public Result getNonStructTaskBySourceId(long sourceId) {
		return Dbo.queryResult("SELECT fcs_id " +
				"FROM data_source ds " +
				"JOIN agent_info ai ON ds.source_id = ai.source_id " +
				"JOIN file_collect_set fcs ON ai.agent_id = fcs.agent_id " +
				"WHERE ds.source_id = ? AND fcs.is_sendok = ?", sourceId, IsFlag.Shi.getCode());
	}

	/**
	 * @Description: 根据sourceId查询出设置完成的半结构化文件采集任务的任务ID
	 * @Param: [sourceId : 数据源ID, 取值范围 : long]
	 * @return: fd.ng.db.resultset.Result
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/10
	 */
	public Result getHalfStructTaskBySourceId(long sourceId) {
		return Dbo.queryResult("SELECT odc_id " +
				"FROM data_source ds " +
				"JOIN agent_info ai ON ds.source_id = ai.source_id " +
				"JOIN object_collect fcs ON ai.agent_id = fcs.agent_id " +
				"WHERE ds.source_id = ? AND fcs.is_sendok = ?", sourceId, IsFlag.Shi.getCode());
	}

	/**
	 * @Description: 根据sourceId查询出设置完成的FTP采集任务的任务ID
	 * @Param: [sourceId : 数据源ID, 取值范围 : long]
	 * @return: fd.ng.db.resultset.Result
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/10
	 */
	public Result getFTPTaskBySourceId(long sourceId) {
		return Dbo.queryResult("SELECT ftp_id " +
				"FROM data_source ds " +
				"JOIN agent_info ai ON ds.source_id = ai.source_id " +
				"JOIN ftp_collect fcs ON ai.agent_id = fcs.agent_id " +
				"WHERE ds.source_id = ?", sourceId, IsFlag.Shi.getCode());
	}

	/**
	 * @Description: 格式化采集频率，将数据库中存储的ALL信息格式化为每月-每天
	 * @Param: [agentInfo : agent详细信息, 取值范围 : Result类型对象]
	 * @return: void
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/3
	 * 步骤：
	 * 1、在查询结果中获取执行频率
	 * 2、分别对月、周、天进行处理
	 *      2-1、如果freMonth的查询结果是ALL，则格式化为每月
	 *      2-2、否则，调用Tools工具类对月频率做国际化处理
	 *      2-3、如果freWeek的查询结果是ALL，则格式化为每周
	 *      2-4、否则，调用Tools工具类对周频率做国际化处理
	 *      2-5、如果freDay的查询结果是ALL，则格式化为每天
	 *      2-6、否则，调用Tools工具类对日频率做国际化处理
	 *      2-7、将数据放入查询结果中
	 */
	//TODO 暂未实现，I18nMessage，Tools工具类未迁移
	private void fromatFrequency(Result agentInfo) {
		//处理数据中的日期数据
		if (!agentInfo.isEmpty()) {
			//1、在查询结果中获取执行频率
			for (int i = 0; i < agentInfo.getRowCount(); i++) {
				//2、分别对月、周、天进行处理
				String freMonth = agentInfo.getString(i, "fre_month");
				String freWeek = agentInfo.getString(i, "fre_week");
				String freDay = agentInfo.getString(i, "fre_day");
				StringBuilder buffer = new StringBuilder();

				if (StringUtil.isNotBlank(freMonth)) {
					if ("ALL".equals(freMonth)) {
						//2-1、如果freMonth的查询结果是ALL，则格式化为每月
						//buffer.append(I18nMessage.getMessage("agentSetUpManager.selectTask.meiyue"));
					} else {
						//2-2、否则，调用Tools工具类对月频率做国际化处理
						JSONArray monthArr = new JSONArray();
						//monthArr = Tools.getMonth(freMonth);
						if (monthArr.size() > 0) {
							for (int j = 0; j < monthArr.size(); j++) {
								JSONObject obj = (JSONObject) monthArr.get(j);
								buffer.append(obj.get("month"));
								if (j < monthArr.size()) {
									buffer.append(',');
								}
							}
						}
					}
				}
				//周
				if (StringUtil.isNotBlank(freWeek)) {
					if ("ALL".equals(freWeek)) {
						//2-3、如果freWeek的查询结果是ALL，则格式化为每周
						buffer.append("--");
						//buffer.append(I18nMessage.getMessage("agentSetUpManager.selectTask.meixingqi"));
					} else {
						//2-4、否则，调用Tools工具类对周频率做国际化处理
						JSONArray weekArr = new JSONArray();
						//weekArr = Tools.getWeek(freWeek);
						if (weekArr.size() > 0) {
							buffer.append("--");
							for (int j = 0; j < weekArr.size(); j++) {
								JSONObject obj = (JSONObject) weekArr.get(j);
								buffer.append(obj.get("week"));
								if (j < weekArr.size()) {
									buffer.append(',');
								}
							}
						}
					}
				}
				//天
				if (StringUtil.isNotBlank(freDay)) {
					if ("ALL".equals(freDay)) {
						//2-5、如果freDay的查询结果是ALL，则格式化为每天
						buffer.append("--");
						//buffer.append(I18nMessage.getMessage("agentSetUpManager.selectTask.meitian"));
					} else {
						//2-6、否则，调用Tools工具类对日频率做国际化处理
						//buffer.append("--").append(freDay).append(I18nMessage.getMessage("agentSetUpManager.selectTask.tian"));
					}
				}
				//2-7、将数据放入查询结果中
				agentInfo.setObject(i, "formatDate", buffer.toString());
			}
		}
	}

	/**
	 * @Description: 根据参数获得任务日志信息
	 * @Param: [agentId : AgentID, 取值范围 : long]
	 * @Param: [userId : 用户ID, 取值范围 : long]
	 * @Param: [logType : 日志类型(完整日志、错误日志), 取值范围 : String(All, Wrong)]
	 * @Param: [readNum : 查看的行数, 取值范围 : int]
	 * @return: java.lang.String
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/10
	 * 步骤：
	 * 1、根据agent_id和user_id获取agent信息
	 * 2、在agent信息中获取日志目录
	 * 3、调用方法获取日志,目前工具类不存在
	 * 4、将日志信息和日志文件的路径封装成map
	 * 5、返回map
	 */
	private Map<String, String> getTaskLog(long agentId, long userId, String logType, int readNum) {
		//1、根据agent_id和user_id获取agent信息
		Map<String, Object> result = Dbo.queryOneObject(
				"select * from agent_down_info where agent_id = ? and user_id = ?", agentId, userId);
		String agentIP = (String) result.get("agent_ip");
		//2、在agent信息中获取日志目录
		String logDir = (String) result.get("log_dir");
		String userName = (String) result.get("user_name");
		String passWord = (String) result.get("passwd");
		if (StringUtil.isNotBlank(logDir)) {
			throw new BusinessException("日志文件不存在" + logDir);
		}

		if (StringUtil.isNotBlank(agentIP)) {
			throw new BusinessException("AgentIP错误" + agentIP);
		}

		//用户选择查看错误日志
		if (logType.equals("Wrong")) {
			logDir = logDir.substring(0, logDir.lastIndexOf(File.separator) + 1) + "error.log";
		}

		String taskLog = "";
		//3、调用方法获取日志,目前工具类不存在
		// agentLog = ReadLog.readAgentLog(logDir, agentIP, Constant.SFTP_PORT, userName, passWord, num);
		if (StringUtil.isBlank(taskLog)) {
			//agentLog = I18nMessage.getMessage("rizhixinxi");
		}
		//4、将日志信息和日志文件的路径封装成map
		Map<String, String> map = new HashMap<>();
		map.put("log", taskLog);
		map.put("filePath", logDir);
		//5、返回map
		return map;
	}
}
