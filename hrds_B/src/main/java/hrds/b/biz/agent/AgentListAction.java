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
import hrds.commons.codes.IsFlag;
import hrds.commons.exception.BusinessException;
import hrds.commons.exception.ExceptionEnum;
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
 * @description: 步骤一、数据库采集应用管理端后台类
 * @author: WangZhengcheng
 * @create: 2019-09-03 14:17
 **/
public class AgentListAction extends BaseAction {

	private static final Logger logger = LogManager.getLogger();

	/**
	 * @Description: 获取数据源Agent列表信息
	 * @Param: [sourceId : 数据源ID]
	 * @return: fd.ng.db.resultset.Result
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/3
	 * 步骤：
	 *      1、获取用户ID
	 *      2、根据用户ID去数据库中查询数据源信息
	 *      3、封装Map并返回
	 */
	public Map<String, Object> getAgentSetUp() {
		//1、获取用户ID
		Long userId = getUserId();
		//2、根据用户ID去数据库中查询数据源信息
		Result result = Dbo.queryResult("SELECT datas.source_id,datas.datasource_name " +
				"FROM agent_info age JOIN data_source datas ON age.source_id = datas.SOURCE_ID " +
				"WHERE age.user_id = ? GROUP BY datas.source_id,datas.datasource_name", userId);
		//3、封装Map并返回
		Map<String, Object> map = new HashMap<>();
		map.put("source", result);

		map.put("shi", IsFlag.Shi.getCode());
		map.put("fou", IsFlag.Fou.getCode());

		map.put("dbFileAgentCode", AgentType.DBWenJian.getCode());
		map.put("dbCollAgentCode", AgentType.ShuJuKu.getCode());
		map.put("nonStructAgentCode", AgentType.WenJianXiTong.getCode());
		map.put("halfStructAgentCode", AgentType.DuiXiang.getCode());
		map.put("ftpAgentCode", AgentType.FTP.getCode());

		map.put("dbFileAgentName", AgentType.DBWenJian.getValue());
		map.put("dbCollAgentCode", AgentType.ShuJuKu.getValue());
		map.put("nonStructAgentCode", AgentType.WenJianXiTong.getValue());
		map.put("halfStructAgentCode", AgentType.DuiXiang.getValue());
		map.put("ftpAgentCode", AgentType.FTP.getValue());

		return map;
	}

	/**
	 * @Description: 界面点击任务配置按钮，根据sourceId和agentType获取相应信息
	 * @Param: [sourceId : 数据源ID]
	 * @Param: [agentType : agent类型(数据库、数据文件、非结构化、非结构化。FTP)]
	 * @return: fd.ng.db.resultset.Result
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/3
	 * 步骤：
	 *      1、根据sourceId和agentType查询数据库获取相应信息
	 *      2、组装Map并返回
	 */
	public Map<String, Object> getAgentInfo(String sourceId, String agentType) {
		//1、根据sourceId和agentType查询数据库获取相应信息
		Result result = Dbo.queryResult("SELECT * FROM agent_info WHERE source_id = ? AND agent_type = ?", sourceId, agentType);
		//2、组装Map并返回
		Map<String, Object> map = new HashMap<>();
		map.put("agentInfo", result);

		map.put("dbFileAgentCode", AgentType.DBWenJian.getCode());
		map.put("dbCollAgentCode", AgentType.ShuJuKu.getCode());
		map.put("nonStructAgentCode", AgentType.WenJianXiTong.getCode());
		map.put("halfStructAgentCode", AgentType.DuiXiang.getCode());
		map.put("ftpAgentCode", AgentType.FTP.getCode());

		return map;
	}

	/**
	 * @Description: 任务管理按钮后台服务类
	 * @Param: [sourceId, agentType, agentId]
	 * @return: fd.ng.db.resultset.Result
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/3
	 * 步骤：
	 *      1、获取用户ID
	 *      2、判断在当前用户，当前数据源下，某一类型的agent是否存在
	 *      3、如果存在，查询结果中应该有且只有一条数据
	 *      4、判断该agent是那种类型，并且根据类型，到对应的数据库表中查询采集任务管理详细信息
	 *      5、对详细信息中的采集频率进行格式化
	 *      6、返回结果
	 */
	//TODO 采集频率格式化未完成
	public Result getTaskInfo(String sourceId, String agentType, String agentId) {
		//1、获取用户ID
		Long userId = getUserId();
		//2、判断在当前用户，当前数据源下，某一类型的agent是否存在
		Result result = Dbo.queryResult("select ai.* from data_source ds left join agent_info ai on ds.SOURCE_ID = ai.SOURCE_ID " +
				"where ds.source_id=? AND ai.user_id = ? " +
				"AND ai.agent_type = ? AND agent_id = ?", sourceId, userId, agentType, agentId);
		//3、如果存在，查询结果中应该有且只有一条数据
		if (!result.isEmpty()) {
			if (result.getRowCount() == 1) {
				//4、判断该agent是那种类型，并且根据类型，到对应的数据库表中查询采集任务管理详细信息
				StringBuilder sqlSB = new StringBuilder();
				//数据库直连采集Agent
				if (AgentType.ShuJuKu == AgentType.getCodeObj(result.getString(0, "agent_type"))) {
					sqlSB.append(" SELECT 'db' db_type,ds.DATABASE_ID ID,ds.task_name task_name,ds.AGENT_ID AGENT_ID, ")
							.append(" cf.execute_time execute_time, cf.fre_week fre_week,cf.run_way run_way,cf.fre_month fre_month, ")
							.append(" cf.fre_day fre_day,gi.source_id source_id,cf.collect_type ")
							.append(" FROM database_set ds JOIN collect_frequency cf ON ds.database_id = cf.COLLECT_SET_ID ")
							.append(" LEFT JOIN agent_info gi ON ds.Agent_id = gi.Agent_id ")
							.append(" where ds.Agent_id=? and ds.is_sendok = ? ");
				}
				//数据文件Agent
				else if (AgentType.DBWenJian == AgentType.getCodeObj(result.getString(0, "agent_type"))) {
					sqlSB.append(" SELECT 'DB' db_type,ds.DATABASE_ID ID,ds.task_name task_name,ds.AGENT_ID AGENT_ID, ")
							.append(" cf.execute_time execute_time, cf.fre_week fre_week,cf.run_way run_way,cf.fre_month fre_month, ")
							.append(" cf.fre_day fre_day,gi.source_id source_id,cf.collect_type ")
							.append(" FROM database_set ds JOIN collect_frequency cf ON ds.database_id = cf.COLLECT_SET_ID ")
							.append(" LEFT JOIN agent_info gi ON ds.Agent_id = gi.Agent_id ")
							.append(" where ds.Agent_id=? and ds.is_sendok = ? ");
				}
				//半结构化采集Agent
				else if (AgentType.DuiXiang == AgentType.getCodeObj(result.getString(0, "agent_type"))) {
					sqlSB.append(" SELECT 'dx' db_type, fs.odc_id id,fs.obj_collect_name task_name,fs.AGENT_ID AGENT_ID, ")
							.append(" cf.execute_time execute_time,cf.fre_week fre_week,cf.run_way run_way,cf.fre_month fre_month, ")
							.append(" cf.fre_day fre_day,gi.source_id,cf.collect_type  ")
							.append(" FROM object_collect fs JOIN collect_frequency cf ")
							.append(" ON fs.odc_id = cf.COLLECT_SET_ID LEFT JOIN agent_info gi ON gi.Agent_id = cf.Agent_id ")
							.append(" WHERE fs.Agent_id = ? AND fs.is_sendok = ? ");
				}
				//FtpAgent
				else if (AgentType.FTP == AgentType.getCodeObj(result.getString(0, "agent_type"))) {
					sqlSB.append(" SELECT 'ftp' db_type,fs.ftp_id id,fs.ftp_name task_name,fs.AGENT_ID AGENT_ID,cf.execute_time execute_time, ")
							.append(" cf.fre_week fre_week,cf.run_way run_way,cf.fre_month fre_month,cf.fre_day fre_day,gi.source_id,cf.collect_type ")
							.append(" FROM ftp_collect fs JOIN collect_frequency cf ON fs.ftp_id = cf.COLLECT_SET_ID LEFT JOIN agent_info gi ")
							.append(" ON gi.Agent_id = cf.Agent_id ")
							.append(" WHERE fs.Agent_id = ? and fs.is_sendok = ? ");
				}
				//非结构化Agent
				else {
					sqlSB.append(" SELECT 'file' db_type,fs.fcs_id id,fs.fcs_name task_name,fs.AGENT_ID AGENT_ID, ")
							.append(" cf.execute_time execute_time,cf.fre_week fre_week,cf.run_way run_way,cf.fre_month fre_month, ")
							.append(" cf.fre_day fre_day,gi.source_id,cf.collect_type ")
							.append(" FROM file_collect_set fs JOIN collect_frequency cf ON fs.fcs_id = cf.COLLECT_SET_ID ")
							.append(" LEFT JOIN agent_info gi ON gi.Agent_id = cf.Agent_id ")
							.append(" where fs.Agent_id=? and fs.is_sendok = ? ");
				}
				Result agentInfo = Dbo.queryResult(sqlSB.toString(), result.getLong(0, "agent_id"), IsFlag.Shi.getCode());
				//5、对详细信息中的采集频率进行格式化
				fromatFrequency(agentInfo);
				//6、返回结果
				return agentInfo;
			} else {
				throw new BusinessException("Agent不唯一");
			}
		} else {
			throw new BusinessException(ExceptionEnum.DATA_NOT_EXIST);
		}
	}

	/**
	 * @Description: 获取任务日志
	 * @Param: [agentId : agentID]
	 * @Param: [logType : 日志类型(错误日志、完整日志)]
	 * @Param: [readNum : 查看的行数]
	 * @return: java.lang.String
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/3
	 * 步骤：
	 *      1、获取当前用户id
	 *      2、对显示日志条数做处理，该方法在加载页面时被调用，readNum可以不传，则默认显示100条，如果用户在页面上进行了选择并点击查看按钮，则最多给用户显示1000条日志
	 *      3、根据agent_id和user_id获取agent信息
	 *      4、在agent信息中获取日志目录
	 *      5、调用方法读取日志
	 *      6、返回日志
	 */
	//TODO 调用方法获取日志,目前工具类(ReadLog, I18nMessage)不存在
	public String getTaskLog(String agentId, String logType, @RequestParam(nullable = true, valueIfNull = "100") Integer readNum) {
		//1、获取当前用户id
		Long userId = getUserId();
		//2、对显示日志条数做处理，该方法在加载页面时被调用，readNum可以不传，则默认显示100条，如果用户在页面上进行了选择并点击查看按钮，则最多给用户显示1000条日志
		int num = 0;
		if ((readNum > 1000)) {
			num = 1000;
		} else {
			num = readNum;
		}
		//3、根据agent_id和user_id获取agent信息
		Result result = Dbo.queryResult("select * from agent_down_info where agent_id = ? and user_id = ?", agentId, userId);
		if (!result.isEmpty()) {
			if (result.getRowCount() == 1) {
				String agentLog = "";
				String agentIP = result.getString(0, "agent_ip");
				//4、在agent信息中获取日志目录
				String logDir = result.getString(0, "log_dir");
				String userName = result.getString(0, "user_name");
				String passWord = result.getString(0, "passwd");
				if (StringUtil.isNotBlank(logDir) && StringUtil.isNotBlank(agentIP)) {
					//用户选择查看错误日志
					if (logType.equals("1")) {
						logDir = logDir.substring(0, logDir.lastIndexOf(File.separator) + 1) + "error.log";
					}
					//5、调用方法获取日志,目前工具类不存在
					//agentLog = ReadLog.readAgentLog(logDir, agentIP, Constant.SFTP_PORT, userName, passWord, num);

					if (StringUtil.isBlank(agentLog)) {
						//agentLog = I18nMessage.getMessage("rizhixinxi");
					}
					//6、返回日志
					return agentLog;
				} else {
					throw new BusinessException("日志文件不存在或AgentIP错误");
				}
			} else {
				throw new BusinessException("Agent不唯一");
			}
		} else {
			throw new BusinessException(ExceptionEnum.DATA_NOT_EXIST);
		}
	}

	/**
	 * @Description: 任务日志下载
	 * @Param: [agentId : agentID]
	 * @Param: [logType : 日志类型(错误日志、完整日志)]
	 * @Param: [readNum : 查看的行数]
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/3
	 * 步骤：
	 *      1、获取当前用户id
	 *      2、对显示日志条数做处理，该方法在加载页面时被调用，readNum可以不传，则默认显示100条，如果用户在页面上进行了选择并点击查看按钮，如果用户输入的条目多于1000，则给用户显示3000条
	 *      3、根据agent_id和user_id获取agent信息
	 *      4、在agent信息中获取日志目录
	 *      5、调用方法读取日志
	 *      6、将日志信息由字符串转为byte[]
	 *      7、创建日志文件
	 *      8、得到本次http交互的request和response
	 *      9、设置响应头信息
	 *      10、使用response获得输出流，完成文件下载
	 */
	//TODO 调用方法获取日志,目前工具类(ReadLog, I18nMessage)不存在
	public void downloadTaskLog(String agentId, String logType, @RequestParam(nullable = true, valueIfNull = "100") Integer readNum) throws IOException{
		//1、获取当前用户id
		Long userId = getUserId();
		//2、对显示日志条数做处理，该方法在加载页面时被调用，readNum可以不传，则默认显示100条，如果用户在页面上进行了选择并点击查看按钮，如果用户输入的条目多于1000，则给用户显示3000条
		int num = 0;
		if ((readNum > 1000)) {
			num = 3000;
		} else {
			num = readNum;
		}
		//3、根据agent_id和user_id获取agent信息
		Result result = Dbo.queryResult("select * from agent_down_info where agent_id = ? and user_id = ?", agentId, userId);
		if (result.getRowCount() == 1) {
			String agentLog = "";
			String agentIP = result.getString(0, "agent_ip");
			//4、在agent信息中获取日志目录
			String logDir = result.getString(0, "log_dir");
			String userName = result.getString(0, "user_name");
			String passWord = result.getString(0, "passwd");
			if (StringUtil.isNotBlank(logDir) && StringUtil.isNotBlank(agentIP)) {
				//用户选择查看错误日志
				if (logType.equals("1")) {
					logDir = logDir.substring(0, logDir.lastIndexOf(File.separator) + 1) + "error.log";
				}
				//5、调用方法获取日志,目前工具类不存在
				//agentLog = ReadLog.readAgentLog(logDir, agentIP, Constant.SFTP_PORT, userName, passWord, num);
				if (StringUtil.isBlank(agentLog)) {
					//agentLog = I18nMessage.getMessage("rizhixinxi");
				}

				//6、将日志信息由字符串转为byte[]
				byte[] bytes = agentLog.getBytes();
				//7、创建日志文件
				File file = new File(logDir);

				//8、得到本次http交互的request和response
				HttpServletResponse response = ResponseUtil.getResponse();
				HttpServletRequest request = RequestUtil.getRequest();

				//9、设置响应头信息
                response.reset();
                if( request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0 ) {
                    // 对firefox浏览器做特殊处理
                    response.setHeader("content-disposition", "attachment;filename=" + new String(logDir.getBytes("UTF-8"), "ISO8859-1"));
                }
                else {
                    response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(logDir, "UTF-8"));
                }
                response.setContentType("APPLICATION/OCTET-STREAM");
				OutputStream out = response.getOutputStream();
                //10、使用response获得输出流，完成文件下载
                out.write(bytes);
                out.flush();
			} else {
				throw new BusinessException("日志文件不存在或AgentIP错误");
			}
		} else {
			throw new BusinessException("当前用户下数据源不存在");
		}
	}

	/**
	 * @Description: 数据采集任务管理页面，操作栏，删除按钮后台方法
	 * @Param: [collectSetId : 数据库设置ID或文件设置id]
	 * @Param: [agentType : Agent类型]
	 * @return: void
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/4
	 * 步骤：
	 *      1、判断Agent类型
	 *      2、根据collectSetId在源文件属性表(source_file_attribute)中获得采集的原始表名(table_name)
	 *      3、调用IOnWayCtrl.checkExistsTask()方法对将要删除的信息进行检查
	 *      4、对不同类型的采集任务做不同的处理
	 *          4-1、半结构化文件采集任务
	 *              4-1-1、在对象采集设置表(object_collect)中删除该条数据
	 *              4-1-2、在卸数作业参数表(collect_frequency)中删除该条数据
	 *          4-2、FTP采集任务
	 *              4-2-1、在FTP采集设置表(ftp_collect)中删除该条数据
	 *              4-2-2、在卸数作业参数表(collect_frequency)中删除该条数据
	 *          4-3、其他任务
	 *              4-3-1、如果是数据库直连采集任务，要对其做特殊处理
	 *              4-3-2、结构化文件、DB文件，做特殊处理
	 *              4-3-3、对其他类型的任务进行统一处理
	 */
	//TODO 工具类IOnWayCtrl不存在
	public void deleteTask(String collectSetId, String agentType) {
		//1、判断Agent类型
		if (AgentType.DuiXiang == AgentType.getCodeObj(agentType)) {
			//2、根据collectSetId在源文件属性表(source_file_attribute)中获得采集的原始表名(table_name)
			Result result = Dbo.queryResult("select table_name from source_file_attribute where collect_set_id = ?", collectSetId);
			if (!result.isEmpty()) {
				List<String> list = new ArrayList<>();
				for (int i = 0; i < result.getRowCount(); i++) {
					list.add(result.getString(i, "table_name"));
				}
				//3、调用IOnWayCtrl.checkExistsTask()方法对将要删除的信息进行检查
				//IOnWayCtrl.checkExistsTask(list, DataSourceType.DML.toString(), db);

				//4-1-1、在对象采集设置表(object_collect)中删除该条数据
				int firNum = Dbo.execute("delete from object_collect where odc_id = ?", collectSetId);
				if (firNum != 1) {
					if (firNum == 0) throw new BusinessException("object_collect表中没有数据被删除!");
					else throw new BusinessException("object_collect表删除数据异常!");
				}

				//4-1-2、在卸数作业参数表(collect_frequency)中删除该条数据
				int secExecute = Dbo.execute("delete from collect_frequency where collect_set_id = ?", collectSetId);
				if (secExecute != 1) {
					if (secExecute == 0) throw new BusinessException("collect_frequency表中没有数据被删除!");
					else throw new BusinessException("collect_frequency表删除数据异常!");
				}
			} else {
				throw new BusinessException(ExceptionEnum.DATA_NOT_EXIST);
			}
		}
		//2-2、FTP采集任务
		else if (AgentType.FTP == AgentType.getCodeObj(agentType)) {
			Result result = Dbo.queryResult("select table_name from source_file_attribute where collect_set_id = ?", collectSetId);
			if (!result.isEmpty()) {
				List<String> list = new ArrayList<>();
				for (int i = 0; i < result.getRowCount(); i++) {
					list.add(result.getString(i, "table_name"));
				}

				//IOnWayCtrl.checkExistsTask(list, DataSourceType.DML.toString(), db);

				//4-2-1、在FTP采集设置表(ftp_collect)中删除该条数据
				int firNum = Dbo.execute("delete from ftp_collect where odc_id = ?", collectSetId);
				if (firNum != 1) {
					if (firNum == 0) throw new BusinessException("ftp_collect表中没有数据被删除!");
					else throw new BusinessException("ftp_collect表删除数据异常!");
				}

				//4-2-2、在卸数作业参数表(collect_frequency)中删除该条数据
				int secExecute = Dbo.execute("delete from collect_frequency where collect_set_id = ?", collectSetId);
				if (secExecute != 1) {
					if (secExecute == 0) throw new BusinessException("collect_frequency表中没有数据被删除!");
					else throw new BusinessException("collect_frequency表删除数据异常!");
				}
			} else {
				throw new BusinessException(ExceptionEnum.DATA_NOT_EXIST);
			}
		} else {
			//4-3-1、如果是数据库直连采集任务，要对其做特殊处理
			if (AgentType.ShuJuKu == AgentType.getCodeObj(agentType)) {
				Result result = Dbo.queryResult("select hbase_name from source_file_attribute where collect_set_id = ?", collectSetId);
				if (!result.isEmpty()) {
					List<String> list = new ArrayList<>();
					for (int i = 0; i < result.getRowCount(); i++) {
						list.add(result.getString(i, "hbase_name"));
					}

					//IOnWayCtrl.checkExistsTask(list, DataSourceType.DML.toString(), db);

					//在数据库设置删除对应的记录
					int firNum = Dbo.execute("delete from database_set where database_id =?", collectSetId);
					if (firNum != 1) {
						if (firNum == 0) throw new BusinessException("database_set表中没有数据被删除!");
						else throw new BusinessException("database_set表删除数据异常!");
					}

					//在表对应字段表中找到对应的记录并删除
					int secNum = Dbo.execute("delete  from table_column where EXISTS" +
							"(select 1 from table_info ti where database_id = ? and table_column.table_id=ti.table_id)", collectSetId);
					if(secNum == 0){
						throw new BusinessException("table_column表中没有数据被删除!");
					}

					//在数据库对应表删除对应的记录
					int thiExecute = Dbo.execute("delete  from table_info where database_id = ?", collectSetId);
					if(thiExecute == 0){
						throw new BusinessException("table_info表中没有数据被删除!");
					}
				} else {
					throw new BusinessException(ExceptionEnum.DATA_NOT_EXIST);
				}
			}
			//4-3-2、结构化文件、DB文件，做特殊处理
			else {
				//在文件系统设置表删除对应的记录
				int fouNum = Dbo.execute("delete  from file_collect_set where fcs_id =?", collectSetId);
				if (fouNum != 1) {
					if (fouNum == 0) throw new BusinessException(String.format("file_collect_set表中没有数据被删除!"));
					else throw new BusinessException("file_collect_set表删除数据异常!");
				}
				//在文件源设置表删除对应的记录
				int fifNum = Dbo.execute("delete  from file_source where fcs_id =?", collectSetId);
				if (fifNum != 1) {
					if (fifNum == 0) throw new BusinessException("file_source表中没有数据被删除!");
					else throw new BusinessException("file_source表删除数据异常!");
				}
			}
			//4-3-3、对其他类型的任务进行统一处理
			//在卸数作业参数表删除对应的记录
			int sixNum = Dbo.execute("delete  from collect_frequency where collect_set_id =?", collectSetId);
			if (sixNum != 1) {
				if (sixNum == 0) throw new BusinessException("collect_frequency表中没有数据被删除!");
				else throw new BusinessException("collect_frequency表删除数据异常!");
			}
			//在压缩作业参数表删除对应的记录
			int sevNum = Dbo.execute("delete  from collect_reduce where collect_set_id =?", collectSetId);
			if (sevNum != 1) {
				if (sevNum == 0) throw new BusinessException("collect_reduce表中没有数据被删除!");
				else throw new BusinessException("collect_reduce表删除数据异常!");
			}
			//在传送作业参数表删除对应的记录
			int eigNum = Dbo.execute("delete  from collect_transfer where collect_set_id =?", collectSetId);
			if (eigNum != 1) {
				if (eigNum == 0) throw new BusinessException("collect_transfer表中没有数据被删除!");
				else throw new BusinessException("collect_transfer表删除数据异常!");
			}
			//在清洗作业参数表删除对应的记录
			int ninNum = Dbo.execute("delete  from collect_clean where collect_set_id =?", collectSetId);
			if (ninNum != 1) {
				if (ninNum == 0) throw new BusinessException("collect_clean表中没有数据被删除!");
				else throw new BusinessException("collect_clean表删除数据异常!");
			}
			//在hdfs存储作业参数表删除对应的记录
			int tenNum = Dbo.execute("delete  from collect_hdfs where collect_set_id =?", collectSetId);
			if (tenNum != 1) {
				if (tenNum == 0) throw new BusinessException("collect_hdfs表中没有数据被删除!");
				else throw new BusinessException("collect_hdfs表删除数据异常!");
			}
		}
	}

	/** 
	* @Description: 数据采集任务管理界面操作栏，生成作业按钮后台方法
	* @Param: [] 
	* @return: java.lang.String 
	* @Author: WangZhengcheng 
	* @Date: 2019/9/6
	 * 步骤：
	 *      1、调用EtlJobUtil工具类，得到结果并返回
	*/ 
	public String buildJob(){
		String proHtml = "";
		//proHtml = EtlJobUtil.proHtml();
		return proHtml;
	}

	/** 
	* @Description: 生成作业弹框，选择工程下拉框后台方法
	* @Param: [taskId] 
	* @return: java.lang.String 
	* @Author: WangZhengcheng 
	* @Date: 2019/9/6
	 * 步骤：
	 *      1、调用EtlJobUtil工具类，得到结果并返回
	*/
	public String selectProject(String taskId){
		String proHtml = "";
		//proHtml = EtlJobUtil.taskHtml(taskId);
		return proHtml;
	}

	/**
	* @Description: 生成作业弹框,确定按钮后台方法
	* @Param: [proId : 工程ID]
	* @Param: [taskId : 任务ID]
	* @Param: [jobId : 作业ID]
	* @Param: [jobType : 作业类型]
	* @return: void
	* @Author: WangZhengcheng
	* @Date: 2019/9/6
	 * 步骤：
	 *      1、调用EtlJobUtil工具类，保存job
	*/
	public void saveProjectInfo(String proId, String taskId, String jobId, String jobType){
		//EtlJobUtil.saveJob(jobId, DataSourceType.DCL.toString(), proId, taskId, jobType);
	}

	/**
	 * @Description: 发送单个任务
	 * @Param: [agentId : AgentId]
	 * @Param: [agentType : Agent类型]
	 * @return: void
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/5
	 * 步骤：
	 *      1、调用方法向agent发送信息
	 */
	public void sendTask(String taskId, String agentType) {
		//半结构化文件采集
		if (AgentType.DuiXiang == AgentType.getCodeObj(agentType)) {
			//SendMsg.sendObjectCollect2Agent(taskId);
		}
		//FTP文件采集
		else if (AgentType.FTP == AgentType.getCodeObj(agentType)) {
			//SendMsg.sendFTP2Agent(taskId);
		}
		//其他类型采集
		else {
			//SendMsg.sendMsg2Agent(taskId);
		}
	}

	/**
	 * @Description: 发送全部任务
	 * @Param: [sourceId]
	 * @return: void
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/5
	 * 步骤：
	 *      1、根据sourceId和是否设置完毕的状态查询出设置完成的数据库采集任务和DB文件采集任务的任务ID(database_id)
	 *      2、根据sourceId和是否设置完毕的状态查询出设置完成的非结构化采集任务的任务ID(fcs_id)
	 *      3、根据sourceId和是否设置完毕的状态查询出设置完成的半结构化采集任务的任务ID(odc_id)
	 *      4、根据sourceId和是否设置完毕的状态查询出设置完成的FTP采集任务的任务ID(ftp_id)
	 *      5、调用方法按照类型发送单个任务
	 */
	public void sendAllTask(String sourceId) {
		//1、根据sourceId和是否设置完毕的状态查询出设置完成的数据库采集任务和DB文件采集任务的任务ID(database_id)
		Result dbAndDbFileResult = Dbo.queryResult("SELECT database_id " +
				"FROM data_source ds " +
				"JOIN agent_info ai ON ds.source_id = ai.source_id " +
				"JOIN database_set das ON ai.agent_id = das.agent_id " +
				"WHERE ds.source_id = ? AND das.is_sendok = ?", sourceId, IsFlag.Shi.getCode());
		//2、根据sourceId和是否设置完毕的状态查询出设置完成的非结构化采集任务的任务ID(fcs_id)
		Result halfStructResult = Dbo.queryResult("SELECT fcs_id " +
				"FROM data_source ds " +
				"JOIN agent_info ai ON ds.source_id = ai.source_id " +
				"JOIN file_collect_set fcs ON ai.agent_id = fcs.agent_id " +
				"WHERE ds.source_id = ? AND fcs.is_sendok = ?", sourceId, IsFlag.Shi.getCode());
		//3、根据sourceId和是否设置完毕的状态查询出设置完成的半结构化采集任务的任务ID(odc_id)
		Result nonStructResult = Dbo.queryResult("SELECT odc_id " +
				"FROM data_source ds " +
				"JOIN agent_info ai ON ds.source_id = ai.source_id " +
				"JOIN object_collect fcs ON ai.agent_id = fcs.agent_id " +
				"WHERE ds.source_id = ? AND fcs.is_sendok = ?", sourceId, IsFlag.Shi.getCode());
		//4、根据sourceId和是否设置完毕的状态查询出设置完成的FTP采集任务的任务ID(ftp_id)
		Result ftpResult = Dbo.queryResult("SELECT ftp_id " +
				"FROM data_source ds " +
				"JOIN agent_info ai ON ds.source_id = ai.source_id " +
				"JOIN ftp_collect fcs ON ai.agent_id = fcs.agent_id " +
				"WHERE ds.source_id = ?", sourceId, IsFlag.Shi.getCode());

		//5、调用方法按照类型发送任务
		if (!dbAndDbFileResult.isEmpty()) {
			for (int i = 0; i < dbAndDbFileResult.getRowCount(); i++) {
				//SendMsg.sendMsg2Agent(dbAndDbFileResult.getString(i, "database_id"));
			}
		}

		if (!halfStructResult.isEmpty()) {
			for (int i = 0; i < halfStructResult.getRowCount(); i++) {
				//SendMsg.sendMsg2Agent(halfStructResult.getString(i, "fcs_id"));
			}
		}

		if (!nonStructResult.isEmpty()) {
			for (int i = 0; i < nonStructResult.getRowCount(); i++) {
				//SendMsg.sendObjectCollect2Agent(nonStructResult.getString(i, "odc_id"));
			}
		}

		if (!ftpResult.isEmpty()) {
			for (int i = 0; i < ftpResult.getRowCount(); i++) {
				//SendMsg.sendFTP2Agent(ftpResult.getString(i, "ftp_id"));
			}
		}

	}

	/**
	 * @Description: 格式化采集频率，如每月-每天
	 * @Param: [agentInfo : agent详细信息]
	 * @return: void
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/3
	 * 步骤：
	 *      1、在查询结果中获取执行频率
	 *      2、分别对月、周、天进行处理
	 *             2-1、如果freMonth的查询结果是ALL，则格式化为每月
	 *             2-2、否则，调用Tools工具类对月频率做国际化处理
	 *             2-3、如果freWeek的查询结果是ALL，则格式化为每周
	 *             2-4、否则，调用Tools工具类对周频率做国际化处理
	 *             2-5、如果freDay的查询结果是ALL，则格式化为每天
	 *             2-6、否则，调用Tools工具类对日频率做国际化处理
	 *             2-7、将数据放入查询结果中
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

}
