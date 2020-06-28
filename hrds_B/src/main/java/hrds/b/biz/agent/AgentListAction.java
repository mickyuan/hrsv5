package hrds.b.biz.agent;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.CodecUtil;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.resultset.Result;
import fd.ng.web.util.Dbo;
import fd.ng.web.util.RequestUtil;
import fd.ng.web.util.ResponseUtil;
import hrds.b.biz.agent.tools.SendMsgUtil;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.AgentType;
import hrds.commons.codes.CleanType;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Agent_down_info;
import hrds.commons.entity.Agent_info;
import hrds.commons.entity.Collect_job_classify;
import hrds.commons.entity.Column_clean;
import hrds.commons.entity.Column_merge;
import hrds.commons.entity.Column_split;
import hrds.commons.entity.Column_storage_info;
import hrds.commons.entity.Data_extraction_def;
import hrds.commons.entity.Data_relation_table;
import hrds.commons.entity.Data_source;
import hrds.commons.entity.Data_store_layer;
import hrds.commons.entity.Data_store_layer_added;
import hrds.commons.entity.Data_store_layer_attr;
import hrds.commons.entity.Data_store_reg;
import hrds.commons.entity.Database_set;
import hrds.commons.entity.Etl_sub_sys_list;
import hrds.commons.entity.Etl_sys;
import hrds.commons.entity.File_collect_set;
import hrds.commons.entity.File_source;
import hrds.commons.entity.Ftp_collect;
import hrds.commons.entity.Length_contrast_sum;
import hrds.commons.entity.Object_collect;
import hrds.commons.entity.Orig_code_info;
import hrds.commons.entity.Table_clean;
import hrds.commons.entity.Table_column;
import hrds.commons.entity.Table_info;
import hrds.commons.entity.Table_storage_info;
import hrds.commons.entity.Type_contrast_sum;
import hrds.commons.exception.AppSystemException;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.AgentActionUtil;
import hrds.commons.utils.Constant;
import hrds.commons.utils.DboExecute;
import hrds.commons.utils.ReadLog;
import hrds.commons.utils.jsch.SFTPDetails;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@DocClass(desc = "获取数据源Agent列表", author = "WangZhengcheng")
public class AgentListAction extends BaseAction {

	@Method(desc = "获取数据源Agent列表信息", logicStep = "1、获取用户ID并根据用户ID去数据库中查询数据源信息")
	@Return(desc = "数据源信息查询结果集", range = "不会为null" + "如果该数据源下面有相应的Agent，则XXXFlag值不为0,否则为0")
	public Result getAgentInfoList() {
		// 1、获取用户ID并根据用户ID去数据库中查询数据源信息
		return Dbo.queryResult(
			"select ds.source_id, ds.datasource_name, "
				+ " sum(case ai.agent_type when ? then 1 else 0 end) as dbflag, "
				+ " sum(case ai.agent_type when ? then 1 else 0 end) as dfflag, "
				+ " sum(case ai.agent_type when ? then 1 else 0 end) as nonstructflag,"
				+ " sum(case ai.agent_type when ? then 1 else 0 end) as halfstructflag,"
				+ " sum(case ai.agent_type when ? then 1 else 0 end) as ftpflag"
				+ " from "
				+ Data_source.TableName
				+ " ds "
				+ " left join "
				+ Agent_info.TableName
				+ " ai "
				+ " on ds.source_id = ai.source_id"
				+ " where ai.user_id = ?"
				+ " group by ds.source_id order by datasource_name",
			AgentType.ShuJuKu.getCode(),
			AgentType.DBWenJian.getCode(),
			AgentType.WenJianXiTong.getCode(),
			AgentType.DuiXiang.getCode(),
			AgentType.FTP.getCode(),
			getUserId());
		// 数据可访问权限处理方式
		// 以上SQL中，通过当前用户ID进行关联查询，达到了数据权限的限制
	}

	@Method(desc = "根据数据源ID、agent类型获取Agent信息", logicStep = "" + "1、获取用户ID并根据用户ID去数据库中查询数据源信息")
	@Param(name = "sourceId", desc = "数据源ID,数据源表主键，agent信息表外键", range = "不为空")
	@Param(name = "agentType", desc = "agent类型", range = "AgentType代码项")
	@Return(desc = "Agent信息查询结果集", range = "不会为null")
	public Result getAgentInfo(long sourceId, String agentType) {
		// 1、根据sourceId和agentType查询数据库获取相应信息
		return Dbo.queryResult(
			"select ai.*, ds.datasource_name FROM "
				+ Agent_info.TableName
				+ " ai "
				+ " join "
				+ Data_source.TableName
				+ " ds "
				+ " on ai.source_id = ds.source_id "
				+ " WHERE ds.source_id = ? "
				+ "AND ai.agent_type = ? AND ai.user_id = ?",
			sourceId,
			agentType,
			getUserId());
		// 数据可访问权限处理方式
		// 以上SQL中，通过当前用户ID进行关联查询，达到了数据权限的限制
	}

	@Method(
		desc = "根据数据源ID和AgentId获取该agent下所有任务的信息",
		logicStep =
			""
				+ "1、获取用户ID, 判断在当前用户，当前数据源下，某一类型的agent是否存在"
				+ "2、如果存在，查询结果中应该有且只有一条数据"
				+ "3、判断该agent是那种类型，并且根据类型，到对应的数据库表中查询采集任务管理详细信息"
				+ "4、返回结果")
	@Param(name = "sourceId", desc = "数据源ID,数据源表主键，agent信息表外键", range = "不为空")
	@Param(name = "agentId", desc = "agentID,agent信息表主键", range = "不为空")
	@Return(desc = "查询结果集", range = "不会为null")
	// TODO 采集频率目前暂未拿到
	public Result getTaskInfo(long sourceId, long agentId) {
		// 1、判断在当前用户，当前数据源下，agent是否存在
		Map<String, Object> agentInfo =
			Dbo.queryOneObject(
				"select ai.agent_type,ai.agent_id from "
					+ Data_source.TableName
					+ " ds  left join "
					+ Agent_info.TableName
					+ " ai on ds.SOURCE_ID = ai.SOURCE_ID  where ds.source_id = ? AND ai.user_id = ? "
					+ " AND ai.agent_id = ?",
				sourceId,
				getUserId(),
				agentId);

		// 数据可访问权限处理方式
		// 以上SQL中，通过当前用户ID进行关联查询，达到了数据权限的限制

		// 2、如果存在，查询结果中应该有且只有一条数据
		if (agentInfo.isEmpty()) {
			throw new BusinessException("未找到Agent");
		}

		// 3、判断该agent是那种类型，并且根据类型，到对应的数据库表中查询采集任务管理详细信息
		String sqlStr;
		AgentType agentType = AgentType.ofEnumByCode(String.valueOf(agentInfo.get("agent_type")));
		// 数据库直连采集Agent
		if (AgentType.ShuJuKu == agentType) {
			sqlStr =
				" SELECT ds.DATABASE_ID ID,ds.task_name task_name,ds.AGENT_ID AGENT_ID,"
					+ " gi.source_id source_id"
					+ " FROM "
					+ Database_set.TableName
					+ " ds "
					+ " LEFT JOIN "
					+ Agent_info.TableName
					+ " gi ON ds.Agent_id = gi.Agent_id "
					+ " where ds.Agent_id = ? AND ds.is_sendok = ? ";
		}
		// 数据文件Agent
		else if (AgentType.DBWenJian == agentType) {
			sqlStr =
				" SELECT ds.DATABASE_ID ID,ds.task_name task_name,ds.AGENT_ID AGENT_ID,"
					+ " gi.source_id source_id"
					+ " FROM "
					+ Database_set.TableName
					+ " ds "
					+ " LEFT JOIN "
					+ Agent_info.TableName
					+ " gi ON ds.Agent_id = gi.Agent_id "
					+ " where ds.Agent_id = ?  AND ds.is_sendok = ?";
		}
		// 半结构化采集Agent
		else if (AgentType.DuiXiang == agentType) {
			sqlStr =
				" SELECT fs.odc_id id,fs.obj_collect_name task_name,fs.AGENT_ID AGENT_ID,gi.source_id"
					+ " FROM "
					+ Object_collect.TableName
					+ " fs "
					+ " LEFT JOIN "
					+ Agent_info.TableName
					+ " gi ON gi.Agent_id = fs.Agent_id "
					+ " WHERE fs.Agent_id = ? AND fs.is_sendok = ?";
		}
		// FtpAgent
		else if (AgentType.FTP == agentType) {
			sqlStr =
				" SELECT fs.ftp_id id,fs.ftp_name task_name,fs.AGENT_ID AGENT_ID,gi.source_id"
					+ " FROM "
					+ Ftp_collect.TableName
					+ " fs "
					+ " LEFT JOIN "
					+ Agent_info.TableName
					+ " gi ON gi.Agent_id = fs.Agent_id "
					+ " WHERE fs.Agent_id = ? AND fs.is_sendok = ?";
		}
		// 非结构化Agent
		else if (AgentType.WenJianXiTong == agentType) {
			sqlStr =
				" SELECT fs.fcs_id id,fs.fcs_name task_name,fs.AGENT_ID AGENT_ID,gi.source_id"
					+ " FROM "
					+ File_collect_set.TableName
					+ " fs "
					+ " LEFT JOIN "
					+ Agent_info.TableName
					+ " gi ON gi.Agent_id = fs.Agent_id "
					+ " where fs.Agent_id = ? AND fs.is_sendok = ?";
		} else {
			throw new BusinessException("从数据库中取到的Agent类型不合法");
		}
		sqlStr += " ORDER BY task_name";
		// 5、返回结果
		return Dbo.queryResult(sqlStr, agentInfo.get("agent_id"), IsFlag.Shi.getCode());
	}

	@Method(
		desc = "查看任务日志",
		logicStep =
			""
				+ "1、对显示日志条数做处理，该方法在加载页面时被调用，readNum可以不传，则默认显示100条，"
				+ "如果用户在页面上进行了选择并点击查看按钮，则最多给用户显示1000条日志"
				+ "2、调用方法读取日志并返回")
	@Param(name = "agentId", desc = "数据源ID,data_source表主键，agent_info表外键", range = "不为空")
	//  @Param(name = "logType", desc = "日志类型(完整日志、错误日志)", range = "All : 完整日志, Wrong : 错误日志") logType
	@Param(name = "readNum", desc = "查看日志条数", range = "该参数可以不传", nullable = true, valueIfNull = "100")
	@Return(desc = "日志信息", range = "不会为null")
	public String viewTaskLog(long agentId, int readNum) {
		// 1、对显示日志条数做处理，该方法在加载页面时被调用，readNum可以不传，则默认显示100条，
		// 如果用户在页面上进行了选择并点击查看按钮，则最多给用户显示1000条日志
		if (readNum > 1000) {
			readNum = 1000;
		}
		// 2、调用方法读取日志并返回
		return getTaskLog(agentId, getUserId(), readNum).get("log");
		// 数据可访问权限处理方式
		// 在getTaskLog()方法中做了处理
	}

	@Method(
		desc = "任务日志下载",
		logicStep =
			""
				+ "1、对显示日志条数做处理，该方法在加载页面时被调用，readNum可以不传，则默认显示100条，"
				+ "如果用户在页面上进行了选择并点击查看按钮，如果用户输入的条目多于1000，则给用户显示3000条"
				+ "2、调用方法读取日志，获得日志信息和日志文件路径"
				+ "3、将日志信息由字符串转为byte[]"
				+ "4、得到本次http交互的request和response"
				+ "5、设置响应头信息"
				+ "6、使用response获得输出流，完成文件下载")
	@Param(name = "agentId", desc = "数据源ID,data_source表主键，agent_info表外键", range = "不为空")
	//  @Param(name = "logType", desc = "日志类型(完整日志、错误日志)", range = "All : 完整日志, Wrong : 错误日志") logType
	@Param(name = "readNum", desc = "查看日志条数", range = "该参数可以不传", nullable = true, valueIfNull = "10000")
	public void downloadTaskLog(long agentId, int readNum) {
		// 1、对显示日志条数做处理，该方法在加载页面时被调用，readNum可以不传，则默认显示100条，
		// 如果用户在页面上进行了选择并点击查看按钮，如果用户输入的条目多于1000，则给用户显示3000条
//    if (readNum > 1000) readNum = 3000;
		// 2、调用方法读取日志，获得日志信息和日志文件路径
		Map<String, String> taskLog = getTaskLog(agentId, getUserId(), readNum);

		// 3、将日志信息由字符串转为byte[]
		byte[] bytes = taskLog.get("log").getBytes();
		File downloadFile = new File(taskLog.get("filePath"));
		responseFile(downloadFile.getName(), bytes);
	}

	private void responseFile(String fileName, byte[] bytes) {
		// 4、得到本次http交互的request和response
		HttpServletResponse response = ResponseUtil.getResponse();
		HttpServletRequest request = RequestUtil.getRequest();

		try (OutputStream out = response.getOutputStream()) {
			// 5、设置响应头信息
			response.reset();
			if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {
				// 对firefox浏览器做特殊处理
				response.setHeader(
					"content-disposition",
					"attachment;filename="
						+ new String(fileName.getBytes(), CodecUtil.GBK_STRING));
			} else {
				response.setHeader(
					"content-disposition",
					"attachment;filename="
						+ URLEncoder.encode(fileName, CodecUtil.UTF8_STRING));
			}
			response.setContentType("APPLICATION/OCTET-STREAM");
			// 6、使用response获得输出流，完成文件下载
			out.write(bytes);
			out.flush();
			// 数据可访问权限处理方式
			// 在getTaskLog()方法中做了处理
		} catch (IOException e) {
			throw new AppSystemException(e);
		}
	}

	@Method(
		desc = "根据ID删除半结构化采集任务数据",
		logicStep = "" + "1、根据collectSetId和user_id判断是否有这样一条数据" + "2、在对象采集设置表(object_collect)中删除该条数据")
	@Param(name = "collectSetId", desc = "半结构化采集设置表ID", range = "不为空")
	// TODO IOnWayCtrl.checkExistsTask()暂时先不管
	public void deleteHalfStructTask(long collectSetId) {
    /*
    //1、根据collectSetId在源文件属性表(source_file_attribute)中获得采集的原始表名(table_name)，可能有多条
    List<Object> tableNames = Dbo.queryOneColumnList(
    		"select table_name from source_file_attribute where collect_set_id = ?",
    		collectSetId);
    if (tableNames.isEmpty()) {
    	throw new BusinessException("源文件属性表中未找到采集的原始表名");
    }
    //2、调用IOnWayCtrl.checkExistsTask()方法对将要删除的信息进行检查
    //IOnWayCtrl.checkExistsTask(tableNames, DataSourceType.DML.toString(), db);
    */

		// 1、根据collectSetId和user_id判断是否有这样一条数据
		long val =
			Dbo.queryNumber(
				"select count(1) from "
					+ Data_source.TableName
					+ " ds "
					+ " join "
					+ Agent_info.TableName
					+ " ai on ai.source_id = ds.source_id "
					+ " join "
					+ Object_collect.TableName
					+ " oc on ai.Agent_id = oc.Agent_id "
					+ " where ai.user_id = ? and oc.odc_id = ?",
				getUserId(),
				collectSetId)
				.orElseThrow(() -> new BusinessException("SQL查询错误"));
		if (val != 1) {
			throw new BusinessException("要删除的半结构化文件采集任务不存在");
		}
		// 数据可访问权限处理方式
		// 该方法首先使用user_id和collectSetId去数据库中查找要删除的数据是否存在

		// 2、在对象采集设置表(object_collect)中删除该条数据，有且只有一条
		DboExecute.deletesOrThrow(
			"删除半结构化采集任务异常!",
			"delete from " + Object_collect.TableName + " where odc_id = ?",
			collectSetId);
	}

	@Method(
		desc = "根据ID删除FTP采集任务数据",
		logicStep = "" + "1、根据collectSetId和user_id判断是否有这样一条数据" + "2、在FTP采集设置表(ftp_collect)中删除该条数据")
	@Param(name = "collectSetId", desc = "FTP采集设置表ID", range = "不为空")
	// TODO IOnWayCtrl.checkExistsTask()暂时先不管
	public void deleteFTPTask(long collectSetId) {
    /*
    //1、根据collectSetId在源文件属性表(source_file_attribute)中获得采集的原始表名(table_name)，可能有多条
    List<Object> tableNames = Dbo.queryOneColumnList(
    		"select table_name from source_file_attribute where collect_set_id = ?",
    		collectSetId);
    if (tableNames.isEmpty()) {
    	throw new BusinessException("源文件属性表中未找到采集的原始表名");
    }
    //2、调用IOnWayCtrl.checkExistsTask()方法对将要删除的信息进行检查
    //IOnWayCtrl.checkExistsTask(tableNames, DataSourceType.DML.toString(), db);
    */

		// 1、根据collectSetId和user_id判断是否有这样一条数据
		long val =
			Dbo.queryNumber(
				"select count(1) from "
					+ Data_source.TableName
					+ " ds "
					+ " join "
					+ Agent_info.TableName
					+ " ai on ai.source_id = ds.source_id "
					+ " join "
					+ Ftp_collect.TableName
					+ " fc on ai.Agent_id = fc.Agent_id "
					+ " where ai.user_id = ? and fc.ftp_id = ?",
				getUserId(),
				collectSetId)
				.orElseThrow(() -> new BusinessException("SQL查询错误"));
		if (val != 1) {
			throw new BusinessException("要删除的FTP采集任务不存在");
		}
		// 数据可访问权限处理方式
		// 该方法首先使用user_id和collectSetId去数据库中查找要删除的数据是否存在

		// 2、在FTP采集设置表(ftp_collect)中删除该条数据，有且只有一条
		DboExecute.deletesOrThrow(
			"删除FTP采集任务数据异常!",
			"delete from " + Ftp_collect.TableName + " where ftp_id = ?",
			collectSetId);
	}

	@Method(
		desc = "根据ID删除数据库直连采集任务",
		logicStep =
			""
				+ "1、根据collectSetId和user_id判断是否有这样一条数据"
				+ "2、在数据库设置表中删除对应的记录"
				+ "3、删除该数据库采集任务中所有的表信息和列信息(table_id做外键和column_id做外键的相关表的记录全部作为脏数据删除)"
				+ "4、在数据库采集对应表中删除相应的数据")
	@Param(name = "collectSetId", desc = "源系统数据库设置表ID", range = "不为空")
	// TODO IOnWayCtrl.checkExistsTask()暂时先不管
	public void deleteDBTask(long collectSetId) {
		// 1、根据collectSetId和user_id判断是否有这样一条数据
		long val =
			Dbo.queryNumber(
				"select count(1) from "
					+ Data_source.TableName
					+ " ds "
					+ " join "
					+ Agent_info.TableName
					+ " ai on ai.source_id = ds.source_id "
					+ " join "
					+ Database_set.TableName
					+ " dbs on ai.Agent_id = dbs.Agent_id "
					+ " where ai.user_id = ? and dbs.database_id = ?",
				getUserId(),
				collectSetId)
				.orElseThrow(() -> new BusinessException("SQL查询错误"));
		if (val != 1) {
			throw new BusinessException("要删除的数据库直连采集任务不存在");
		}
		// 数据可访问权限处理方式
		// 该方法首先使用user_id和collectSetId去数据库中查找要删除的数据是否存在

		// 2、在数据库设置表中删除对应的记录，有且只有一条
		DboExecute.deletesOrThrow(
			"删除数据库直连采集任务异常!",
			"delete from " + Database_set.TableName + " where database_id = ? ",
			collectSetId);
		// 3、删除该数据库采集任务中所有的表信息和列信息(table_id做外键和column_id做外键的相关表的记录全部作为脏数据删除)
		List<Object> tableIds =
			Dbo.queryOneColumnList(
				"select table_id from " + Table_info.TableName + " where database_id = ?",
				collectSetId);
		if (!tableIds.isEmpty()) {
			for (Object tableId : tableIds) {
				deleteDirtyDataOfTb((long) tableId);
			}
		}
		// 4、在数据库采集对应表中删除相应的数据，删除的数据可能是1-N
		Dbo.execute("delete from " + Table_info.TableName + " where database_id = ? ", collectSetId);
	}

	@Method(
		desc = "根据ID删除DB文件采集任务数据",
		logicStep = "" + "1、根据collectSetId和user_id判断是否有这样一条数据" + "2、在数据库设置表中删除对应的记录")
	@Param(name = "collectSetId", desc = "源系统数据库设置表ID", range = "不为空")
	// TODO IOnWayCtrl.checkExistsTask()暂时先不管
	public void deleteDFTask(long collectSetId) {
		// 1、根据collectSetId和user_id判断是否有这样一条数据
		long val =
			Dbo.queryNumber(
				"select count(1) from "
					+ Data_source.TableName
					+ " ds "
					+ " join "
					+ Agent_info.TableName
					+ " ai on ai.source_id = ds.source_id "
					+ " join "
					+ Database_set.TableName
					+ " dbs on ai.Agent_id = dbs.Agent_id "
					+ " where ai.user_id = ? and dbs.database_id = ?",
				getUserId(),
				collectSetId)
				.orElseThrow(() -> new BusinessException("SQL查询错误"));
		if (val != 1) {
			throw new BusinessException("要删除的数据文件采集任务不存在");
		}
		// 数据可访问权限处理方式
		// 该方法首先使用user_id和collectSetId去数据库中查找要删除的数据是否存在
		// 2、在数据库设置表删除对应的记录，有且只有一条
		DboExecute.deletesOrThrow(
			"删除数据文件采集任务异常!",
			"delete from " + Database_set.TableName + " where database_id =?",
			collectSetId);
	}

	@Method(
		desc = "根据ID删除非结构化文件采集任务数据",
		logicStep =
			"" + "1、根据collectSetId和user_id判断是否有这样一条数据" + "2、在文件系统设置表删除对应的记录" + "3、在文件源设置表删除对应的记录")
	@Param(name = "collectSetId", desc = "非结构化采集设置表ID", range = "不为空")
	// TODO IOnWayCtrl.checkExistsTask()暂时先不管
	public void deleteNonStructTask(long collectSetId) {
		// 1、根据collectSetId和user_id判断是否有这样一条数据
		long val =
			Dbo.queryNumber(
				"select count(1) from "
					+ Data_source.TableName
					+ " ds "
					+ " join "
					+ Agent_info.TableName
					+ " ai on ai.source_id = ds.source_id "
					+ " join "
					+ File_collect_set.TableName
					+ " fcs on ai.Agent_id = fcs.Agent_id "
					+ " where ai.user_id = ? and fcs.fcs_id = ?",
				getUserId(),
				collectSetId)
				.orElseThrow(() -> new BusinessException("SQL查询错误"));
		if (val != 1) {
			throw new BusinessException("要删除的非结构化文件采集任务不存在");
		}
		// 数据可访问权限处理方式
		// 该方法首先使用user_id和collectSetId去数据库中查找要删除的数据是否存在

		// 2、在文件系统设置表删除对应的记录，有且只有一条数据
		DboExecute.deletesOrThrow(
			"删除非结构化采集任务数据异常!",
			"delete  from " + File_collect_set.TableName + " where fcs_id = ? ",
			collectSetId);
		// 3、在文件源设置表删除对应的记录，可以有多条
		int secNum =
			Dbo.execute("delete  from " + File_source.TableName + " where fcs_id = ?", collectSetId);
		if (secNum == 0) {
			throw new BusinessException("删除非结构化采集任务异常!");
		}
	}

	@Method(
		desc = "查询工程信息",
		logicStep = "" + "1、根据用户ID在工程登记表(etl_sys)中查询工程代码(etl_sys_cd)和工程名称(etl_sys_name)并返回")
	@Return(desc = "查询结果集", range = "不会为null")
	public Result getProjectInfo() {
		// 1、根据用户ID在工程登记表(etl_sys)中查询工程代码(etl_sys_cd)和工程名称(etl_sys_name)并返回
		return Dbo.queryResult(
			"select etl_sys_cd,etl_sys_name from " + Etl_sys.TableName + " where user_id = ?",
			getUserId());
		// 数据可访问权限处理方式
		// 该方法首先使用user_id去数据库中查询相应的数据
	}

	@Method(
		desc = "根据Id获得某个工程下的任务信息",
		logicStep =
			"" + "1、根据工程代码在子系统定义表(etl_sub_sys_list)中查询子系统代码(sub_sys_cd)和子系统描述(sub_sys_desc)并返回")
	@Param(name = "taskId", desc = "任务ID, 子系统定义表主键", range = "不为空")
	@Return(desc = "查询结果集", range = "不会为null")
	public Result getTaskInfoByTaskId(String taskId) {
		return Dbo.queryResult(
			" select ess.sub_sys_cd,ess.sub_sys_desc from "
				+ Etl_sub_sys_list.TableName
				+ " ess join "
				+ Etl_sys.TableName
				+ " es on es.etl_sys_cd = ess.etl_sys_cd"
				+ "where es.user_id = ? and ess.etl_sys_cd = ? ",
			getUserId(),
			taskId);
		// 数据可访问权限处理方式
		// 该方法首先使用user_id去数据库中查询相应的数据
	}

	@Method(
		desc = "根据数据源ID查询出设置完成的数据库采集任务和DB文件采集任务的任务ID",
		logicStep = "" + "1、根据数据源ID和用户ID查询出设置完成的数据库采集任务和DB文件采集任务的任务ID并返回")
	@Param(name = "sourceId", desc = "数据源表主键, agent信息表外键", range = "不为空")
	@Return(desc = "查询结果集", range = "不会为null")
	// 获得设置完成的任务，用于发送
	public Result getDBAndDFTaskBySourceId(long sourceId) {
		// 1、根据数据源ID和用户ID查询出设置完成的数据库采集任务和DB文件采集任务的任务ID并返回
		return Dbo.queryResult(
			"SELECT das.database_id "
				+ "FROM "
				+ Data_source.TableName
				+ " ds "
				+ "JOIN "
				+ Agent_info.TableName
				+ " ai ON ds.source_id = ai.source_id "
				+ "JOIN "
				+ Database_set.TableName
				+ " das ON ai.agent_id = das.agent_id "
				+ "WHERE ds.source_id = ? AND das.is_sendok = ? AND ai.user_id = ?",
			sourceId,
			IsFlag.Shi.getCode(),
			getUserId());
		// 数据可访问权限处理方式
		// 以上SQL中，通过当前用户ID进行关联查询，达到了数据权限的限制
	}

	@Method(
		desc = "根据数据源ID查询出设置完成的非结构化文件采集任务的任务ID",
		logicStep = "" + "1、根据数据源ID和用户ID查询出设置完成的非结构化文件采集任务的任务ID并返回")
	@Param(name = "sourceId", desc = "数据源表主键, agent信息表外键", range = "不为空")
	@Return(desc = "查询结果集", range = "不会为null")
	// 获得设置完成的任务，用于发送
	public Result getNonStructTaskBySourceId(long sourceId) {
		// 1、根据数据源ID和用户ID查询出设置完成的非结构化文件采集任务的任务ID并返回
		return Dbo.queryResult(
			"SELECT fcs.fcs_id "
				+ "FROM "
				+ Data_source.TableName
				+ " ds "
				+ "JOIN "
				+ Agent_info.TableName
				+ " ai ON ds.source_id = ai.source_id "
				+ "JOIN "
				+ File_collect_set.TableName
				+ " fcs ON ai.agent_id = fcs.agent_id "
				+ "WHERE ds.source_id = ? AND fcs.is_sendok = ? AND ai.user_id = ?",
			sourceId,
			IsFlag.Shi.getCode(),
			getUserId());
		// 数据可访问权限处理方式
		// 以上SQL中，通过当前用户ID进行关联查询，达到了数据权限的限制
	}

	@Method(
		desc = "根据数据源ID查询出设置完成的半结构化文件采集任务的任务ID",
		logicStep = "" + "1、根据数据源ID和用户ID查询出设置完成的半结构化文件采集任务的任务ID并返回")
	@Param(name = "sourceId", desc = "数据源表主键, agent信息表外键", range = "不为空")
	@Return(desc = "查询结果集", range = "不会为null")
	// 获得设置完成的任务，用于发送
	public Result getHalfStructTaskBySourceId(long sourceId) {
		// 1、根据数据源ID和用户ID查询出设置完成的半结构化文件采集任务的任务ID并返回
		return Dbo.queryResult(
			"SELECT fcs.odc_id "
				+ "FROM "
				+ Data_source.TableName
				+ " ds "
				+ "JOIN "
				+ Agent_info.TableName
				+ " ai ON ds.source_id = ai.source_id "
				+ "JOIN "
				+ Object_collect.TableName
				+ " fcs ON ai.agent_id = fcs.agent_id "
				+ "WHERE ds.source_id = ? AND fcs.is_sendok = ? AND ai.user_id = ?",
			sourceId,
			IsFlag.Shi.getCode(),
			getUserId());
		// 数据可访问权限处理方式
		// 以上SQL中，通过当前用户ID进行关联查询，达到了数据权限的限制
	}

	@Method(desc = "根据数据源ID查询出FTP采集任务的任务ID", logicStep = "" + "1、根据数据源ID和用户ID查询出FTP采集任务的任务ID并返回")
	@Param(name = "sourceId", desc = "数据源表主键, agent信息表外键", range = "不为空")
	@Return(desc = "查询结果集", range = "不会为null")
	// 获得设置完成的任务，用于发送
	public Result getFTPTaskBySourceId(long sourceId) {
		// 1、根据数据源ID和用户ID查询出FTP采集任务的任务ID并返回
		return Dbo.queryResult(
			"SELECT fcs.ftp_id "
				+ "FROM "
				+ Data_source.TableName
				+ " ds "
				+ "JOIN "
				+ Agent_info.TableName
				+ " ai ON ds.source_id = ai.source_id "
				+ "JOIN "
				+ Ftp_collect.TableName
				+ " fcs ON ai.agent_id = fcs.agent_id "
				+ "WHERE ds.source_id = ? AND fcs.is_sendok = ? AND ai.user_id = ? ",
			sourceId,
			IsFlag.Shi.getCode(),
			getUserId());
		// 数据可访问权限处理方式
		// 以上SQL中，通过当前用户ID进行关联查询，达到了数据权限的限制
	}

	@Method(
		desc = "根据数据库设置ID查询数据，向agent端发送任务信息",
		logicStep =
			""
				+ "1、根据数据库设置ID，在源系统数据库设置表中查询该任务是否存在"
				+ "2、任务存在，则查询数据，开始拼接要发送到agent端的信息"
				+ "   2-1、首先根据数据库设置ID查询源系统数据库信息"
				+ "   2-2、将结果集转换为JSONObject，方便往里面塞数据"
				+ "   2-3、信号文件信息暂时没有，所以先设置一个空的集合，后期要去singal_file表里面查"
				+ "   2-4、查询并组装采集表配置信息数组，除数据库中查询出的内容，还需要组装表采集字段集合、列合并参数信息、表存储配置信息"
				+ "       2-4-1、查询当前数据库采集任务下每张表的列合并信息，放入对应的表的JSONObject中"
				+ "       2-4-2、查询表采集字段集合，放入对应表的JSONObject中，并且只查询采集的列"
				+ "       2-4-3、遍历要采集的每个列，将每个列的清洗信息查询出来"
				+ "       2-4-4、判断是否有码值转换信息，如果有，需要把码值转换信息解析出来，拼成json字符串"
				+ "       2-4-5、判断是否有列拆分信息，如果有，需要把列拆分信息放入其中"
				+ "       2-4-6、查询每张表的存储配置信息，一张表可以选择进入多个存储目的地"
				+ "       2-4-7、遍历存储目的地，得到存储层配置ID，根据存储层配置ID在数据存储层配置属性表中，查询配置属性或配置文件属性"
				+ "       2-4-8、遍历该表保存进入响应存储目的地的附加字段，组装附加字段信息"
				+ "3、调用工具类，发送信息，接收agent端响应状态码，如果发送失败，则抛出异常给前端")
	@Param(name = "colSetId", desc = "源系统数据库设置表ID", range = "不为空")
	@Param(name = "is_download", range = "可以为空,默认为不下载", desc = "是否为数据字典下载", nullable = true, valueIfNull = "false")
	public void sendJDBCCollectTaskById(long colSetId, String is_download) {
		// 1、根据数据库设置ID，在源系统数据库设置表中查询该任务是否存在
		long count =
			Dbo.queryNumber(
				"select count(1) from " + Database_set.TableName + " where database_id = ?",
				colSetId)
				.orElseThrow(() -> new BusinessException("SQL查询错误"));
		if (count != 1) {
			throw new BusinessException("未找到数据库采集任务");
		}
		// 2、任务存在，则查询数据，开始拼接要发送到agent端的信息
		// 2-1、首先根据数据库设置ID查询源系统数据库信息
		Result sourceDBConfResult =
			Dbo.queryResult(
				"SELECT dbs.agent_id, dbs.database_id, dbs.task_name,"
					+ " dbs.database_name, dbs.database_pad,"
					+ " dbs.database_drive, dbs.database_type, dbs.user_name, dbs.database_ip, dbs.database_port,"
					+ " dbs.host_name, dbs.system_type, dbs.is_sendok, dbs.database_number, dbs.db_agent, dbs.plane_url,"
					+ " dbs.database_separatorr, dbs.row_separator, dbs.classify_id,  dbs.jdbc_url, ds.datasource_number,"
					+ " cjc.classify_num FROM "
					+ Data_source.TableName
					+ " ds"
					+ " JOIN "
					+ Agent_info.TableName
					+ " ai ON ds.source_id = ai.source_id"
					+ " JOIN "
					+ Database_set.TableName
					+ " dbs ON ai.agent_id = dbs.agent_id"
					+ " left join "
					+ Collect_job_classify.TableName
					+ " cjc on dbs.classify_id = cjc.classify_id"
					+ " where dbs.database_id = ?",
				colSetId);

		// 2-2、将结果集转换为JSONObject，方便往里面塞数据
		if (sourceDBConfResult.getRowCount() != 1) {
			throw new BusinessException("根据数据库采集任务ID查询到的任务配置信息不唯一");
		}
		JSONArray array = JSONArray.parseArray(sourceDBConfResult.toJSON());
		JSONObject sourceDBConfObj = array.getJSONObject(0);

		// 2-3、TODO 信号文件信息暂时没有，所以先设置一个空的集合，后期要去singal_file表里面查
		sourceDBConfObj.put("signal_file_list", new ArrayList<>());

		// 2-4、查询并组装采集表配置信息数组，除数据库中查询出的内容，还需要组装表采集字段集合、列合并参数信息、表存储配置信息
		Result collectTableResult =
			Dbo.queryResult(
				"SELECT dbs.database_id, ti.table_id, ti.table_name, "
					+ "ti.table_ch_name, ti.table_count, ti.source_tableid, ti.valid_s_date, ti.valid_e_date, ti.sql, "
					+ "ti.remark, ti.is_user_defined, ti.is_md5,ti.is_register,ti.is_parallel,ti.page_sql,ti.rec_num_date,"
					+ "ti.unload_type,ti.is_customize_sql,ti.pageparallels, ti.dataincrement,tsi.storage_type, "
					+ "tsi.storage_time, tsi.is_zipper, ds.datasource_number || '_' || cjc.classify_num || '_' || "
					+ "ti.table_name as hbase_name, ds.datasource_name, ai.agent_name, ai.agent_id,ai.user_id, ds.source_id"
					+ " FROM "
					+ Data_source.TableName
					+ " ds "
					+ " JOIN "
					+ Agent_info.TableName
					+ " ai ON ds.source_id = ai.source_id"
					+ " JOIN "
					+ Database_set.TableName
					+ " dbs ON ai.agent_id = dbs.agent_id"
					+ " LEFT JOIN "
					+ Table_info.TableName
					+ " ti on ti.database_id = dbs.database_id"
					+ " LEFT JOIN "
					+ Collect_job_classify.TableName
					+ " cjc on dbs.classify_id = cjc.classify_id"
					+
					//				" LEFT JOIN " + Data_extraction_def.TableName + " ded on ti.table_id =
					// ded.table_id" +
					" LEFT JOIN "
					+ Table_storage_info.TableName
					+ " tsi on tsi.table_id = ti.table_id"
					+ " WHERE dbs.database_id = ?",
				colSetId);

		// collectTables是从数据库中查询出的当前数据库采集任务要采集的表部分配置信息
		JSONArray collectTables = JSONArray.parseArray(collectTableResult.toJSON());

		for (int i = 0; i < collectTables.size(); i++) {
			JSONObject collectTable = collectTables.getJSONObject(i);
			//			collectTable.put("etlDate", DateUtil.getSysDate());
			Long tableId = collectTable.getLong("table_id");
			// 查询数据抽取定义，一张表对应多种数据抽取格式，放到对应的表的JSONObject中
			List<Data_extraction_def> data_extraction_defs =
				Dbo.queryList(
					Data_extraction_def.class,
					"select * from " + Data_extraction_def.TableName + " where table_id = ?",
					tableId);
			if (!data_extraction_defs.isEmpty()) {
				collectTable.put("data_extraction_def_list", data_extraction_defs);
			} else {
				collectTable.put("data_extraction_def_list", new ArrayList<>());
			}
			// 2-4-1、查询当前数据库采集任务下每张表的列合并信息，放入对应的表的JSONObject中
			List<Column_merge> columnMerges =
				Dbo.queryList(
					Column_merge.class,
					"select * from " + Column_merge.TableName + " where table_id = ?",
					tableId);
			if (!columnMerges.isEmpty()) {
				collectTable.put("column_merge_list", columnMerges);
			} else {
				collectTable.put("column_merge_list", new ArrayList<>());
			}
			// 2-4-2、查询表采集字段集合，放入对应表的JSONObject中，并且只查询采集的列
			Result tableColResult =
				Dbo.queryResult(
					"select tc.column_id, tc.is_primary_key, tc.column_name, tc.column_ch_name, "
						+ " tc.valid_s_date, tc.valid_e_date, tc.is_get, tc.column_type, tc.tc_remark, tc.is_alive, "
						+ " tc.is_new, tc.tc_or from "
						+ Table_column.TableName
						+ " tc where tc.table_id = ? and tc.is_get = ?",
					tableId,
					IsFlag.Shi.getCode());
			// tableColArray是从数据库找那中查询出的当前采集表字段信息
			JSONArray tableColArray = JSONArray.parseArray(tableColResult.toJSON());
			// 2-4-3、遍历要采集的每个列，将每个列的清洗信息查询出来
			for (int j = 0; j < tableColArray.size(); j++) {
				JSONObject tableColObj = tableColArray.getJSONObject(j);
				Long columnId = tableColObj.getLong("column_id");
				Result columnCleanResult =
					Dbo.queryResult(
						"select cc.col_clean_id, cc.clean_type ,cc.character_filling, cc.filling_length, "
							+ " cc.field, cc.replace_feild, cc.filling_type, cc.convert_format, cc.old_format "
							+ " from "
							+ Column_clean.TableName
							+ " cc where cc.column_id = ?",
						columnId);
				JSONArray columnCleanArray = JSONArray.parseArray(columnCleanResult.toJSON());

				// 2-4-4、判断是否有码值转换信息，如果有，需要把码值转换信息解析出来，拼成json字符串
				long CVCount =
					Dbo.queryNumber(
						"select count(1) from "
							+ Column_clean.TableName
							+ " where clean_type = ? and column_id = ?",
						CleanType.MaZhiZhuanHuan.getCode(),
						columnId)
						.orElseThrow(() -> new BusinessException("SQL查询错误"));
				if (CVCount > 0) {
					for (int k = 0; k < columnCleanArray.size(); k++) {
						JSONObject columnCleanObj = columnCleanArray.getJSONObject(k);
						Long colCleanId = columnCleanObj.getLong("col_clean_id");
						Result CVCResult =
							Dbo.queryResult(
								"select codename,codesys from "
									+ Column_clean.TableName
									+ " where col_clean_id = ? and clean_type = ?",
								colCleanId,
								CleanType.MaZhiZhuanHuan.getCode());
						if (CVCResult.getRowCount() == 1) {
							Result CVResult =
								Dbo.queryResult(
									"select code_value,orig_value from "
										+ Orig_code_info.TableName
										+ " where code_classify = ? and orig_sys_code = ?",
									CVCResult.getString(0, "codename"),
									CVCResult.getString(0, "codesys"));
							JSONArray CVArray = JSONArray.parseArray(CVResult.toJSON());
							columnCleanObj.put("codeTransform", CVArray);
						} else {
							columnCleanObj.put("codeTransform", "");
						}
					}
				}

				// 2-4-5、判断是否有列拆分信息，如果有，需要把列拆分信息放入其中
				long splitCount =
					Dbo.queryNumber(
						"select count(1) from "
							+ Column_clean.TableName
							+ " where clean_type = ? and column_id = ?",
						CleanType.ZiFuChaiFen.getCode(),
						columnId)
						.orElseThrow(() -> new BusinessException("SQL查询错误"));
				if (splitCount > 0) {
					for (int k = 0; k < columnCleanArray.size(); k++) {
						JSONObject columnCleanObj = columnCleanArray.getJSONObject(k);
						Long colCleanId = columnCleanObj.getLong("col_clean_id");
						List<Column_split> columnSplits =
							Dbo.queryList(
								Column_split.class,
								"select * from " + Column_split.TableName + " where col_clean_id = ?",
								colCleanId);
						if (!columnSplits.isEmpty()) {
							columnCleanObj.put("column_split_list", columnSplits);
						} else {
							columnCleanObj.put("column_split_list", new ArrayList<>());
						}
					}
				}
				tableColObj.put("columnCleanBeanList", columnCleanArray);
			}
			collectTable.put("collectTableColumnBeanList", tableColArray);

			//			//2-4-6、查询每张表的存储配置信息，一张表可以选择进入多个存储目的地
			//			Result dataStoreResult = Dbo.queryResult("select dsl.dsl_id, dsl.dsl_name,
			// dsl.store_type, " +
			//					" dsl.is_hadoopclient, tcs.dtcs_name, lcs.dlcs_name " +
			//					" from " + Data_store_layer.TableName + " dsl" +
			//					" left join " + Type_contrast_sum.TableName + " tcs on dsl.dtcs_id = tcs.dtcs_id" +
			//					" left join " + Length_contrast_sum.TableName + " lcs on dsl.dlcs_id = lcs.dlcs_id" +
			//					" where dsl.dsl_id = (select drt.dsl_id from " + Data_relation_table.TableName + " drt
			// where drt.storage_id = " +
			//					" (select storage_id from " + Table_storage_info.TableName + " tsi where tsi.table_id =
			// ?))", tableId);
			//
			//			JSONArray dataStoreArray = JSONArray.parseArray(dataStoreResult.toJSON());
			//
			//			//2-4-7、遍历存储目的地，得到存储层配置ID，根据存储层配置ID在数据存储层配置属性表中，查询配置属性或配置文件属性
			//			for (int m = 0; m < dataStoreArray.size(); m++) {
			//				JSONObject dataStore = dataStoreArray.getJSONObject(m);
			//				Long dslId = dataStore.getLong("dsl_id");
			//				Result result = Dbo.queryResult("select storage_property_key, storage_property_val,
			// is_file from "
			//						+ Data_store_layer_attr.TableName + " where dsl_id = ?", dslId);
			//				if (result.isEmpty()) {
			//					throw new BusinessException("根据存储层配置ID" + dslId + "未获取到存储层配置属性信息");
			//				}
			//				Map<String, String> dataStoreConnectAttr = new HashMap<>();
			//				Map<String, String> dataStoreLayerFile = new HashMap<>();
			//				for (int n = 0; n < result.getRowCount(); n++) {
			//					IsFlag fileFlag = IsFlag.ofEnumByCode(result.getString(n, "is_file"));
			//					if (fileFlag == IsFlag.Shi) {
			//						dataStoreLayerFile.put(result.getString(n, "storage_property_key"),
			//								result.getString(n, "storage_property_val"));
			//					} else {
			//						dataStoreConnectAttr.put(result.getString(n, "storage_property_key"),
			//								result.getString(n, "storage_property_val"));
			//					}
			//				}
			//				dataStore.put("data_store_connect_attr", dataStoreConnectAttr);
			//				dataStore.put("data_store_layer_file", dataStoreLayerFile);
			//
			//				//2-4-8、遍历该表保存进入响应存储目的地的附加字段，组装附加字段信息
			//				List<Object> storeLayers = Dbo.queryOneColumnList("select dsla.dsla_storelayer from " +
			// Column_storage_info.TableName + " csi" +
			//						" left join " + Data_store_layer_added.TableName + " dsla" +
			//						" on dsla.dslad_id = csi.dslad_id" +
			//						" where csi.column_id in" +
			//						" (select column_id from " + Table_column.TableName + " where table_id = ?) " +
			//						" and dsla.dsl_id = ?", tableId, dslId);
			//
			//				Result columnResult = Dbo.queryResult("select dsla.dsla_storelayer, csi.csi_number,
			// tc.column_name " +
			//						" from " + Column_storage_info.TableName + " csi" +
			//						" left join " + Data_store_layer_added.TableName + " dsla" +
			//						" on dsla.dslad_id = csi.dslad_id" +
			//						" join " + Table_column.TableName + " tc " +
			//						" on csi.column_id = tc.column_id" +
			//						" where csi.column_id in (select column_id from " + Table_column.TableName + " " +
			//						" where table_id = ?) and dsla.dsl_id = ?", tableId, dslId);
			//
			//				Map<String, Map<String, Integer>> additInfoFieldMap = new HashMap<>();
			//				if (!columnResult.isEmpty() && !storeLayers.isEmpty()) {
			//					for (Object obj : storeLayers) {
			//						Map<String, Integer> fieldMap = new HashMap<>();
			//						String storeLayer = (String) obj;
			//						for (int h = 0; h < columnResult.getRowCount(); h++) {
			//							String dslaStoreLayer = columnResult.getString(h, "dsla_storelayer");
			//							if (storeLayer.equals(dslaStoreLayer)) {
			//								fieldMap.put(columnResult.getString(h, "column_name"),
			//										columnResult.getInteger(h, "csi_number"));
			//							}
			//						}
			//						additInfoFieldMap.put(storeLayer, fieldMap);
			//					}
			//				}
			//				dataStore.put("additInfoFieldMap", additInfoFieldMap);
			//			}
			//			collectTable.put("dataStoreConfBean", dataStoreArray);
		}
		// 到此为止，向agent发送的数据全部组装完毕
		sourceDBConfObj.put("collectTableBeanArray", collectTables);
		// return sourceDBConfObj.toJSONString();
		// 3、调用工具类，发送信息，接收agent端响应状态码，如果发送失败，则抛出异常给前端
		String methodName = AgentActionUtil.SENDJDBCCOLLECTTASKINFO;
		if (Boolean.parseBoolean(is_download)) {
			methodName = AgentActionUtil.GETDICTIONARYJSON;
		}
		// TODO 前端调用这个方法应该传入跑批日期，作业调度同样
		// TODO 由于目前定义作业还没有原型，因此暂时手动将跑批日期设为当前日期
		String dataDic = (String) SendMsgUtil.sendDBCollectTaskInfo(
			sourceDBConfObj.getLong("database_id"),
			sourceDBConfObj.getLong("agent_id"),
			getUserId(),
			sourceDBConfObj.toJSONString(),
			methodName,
			DateUtil.getSysDate());

		if (Boolean.parseBoolean(is_download)) {
			responseFile("dd_json.json", dataDic.getBytes());
		}
	}

	@Method(
		desc = "根据数据库设置ID查询数据，向agent端发送任务信息",
		logicStep =
			""
				+ "1、根据数据库设置ID，在源系统数据库设置表中查询该任务是否存在"
				+ "2、任务存在，则查询数据，开始拼接要发送到agent端的信息"
				+ "   2-1、首先根据数据库设置ID查询源系统数据库信息"
				+ "   2-2、将结果集转换为JSONObject，方便往里面塞数据"
				+ "   2-3、信号文件信息暂时没有，所以先设置一个空的集合，后期要去singal_file表里面查"
				+ "   2-4、查询并组装采集表配置信息数组，除数据库中查询出的内容，还需要组装表采集字段集合、列合并参数信息、表存储配置信息"
				+ "       2-4-1、查询当前数据库采集任务下每张表的列合并信息，放入对应的表的JSONObject中"
				+ "       2-4-2、查询表采集字段集合，放入对应表的JSONObject中，并且只查询采集的列"
				+ "       2-4-3、遍历要采集的每个列，将每个列的清洗信息查询出来"
				+ "       2-4-4、判断是否有码值转换信息，如果有，需要把码值转换信息解析出来，拼成json字符串"
				+ "       2-4-5、判断是否有列拆分信息，如果有，需要把列拆分信息放入其中"
				+ "       2-4-6、查询每张表的存储配置信息，一张表可以选择进入多个存储目的地"
				+ "       2-4-7、遍历存储目的地，得到存储层配置ID，根据存储层配置ID在数据存储层配置属性表中，查询配置属性或配置文件属性"
				+ "       2-4-8、遍历该表保存进入响应存储目的地的附加字段，组装附加字段信息"
				+ "3、调用工具类，发送信息，接收agent端响应状态码，如果发送失败，则抛出异常给前端")
	@Param(name = "colSetId", desc = "源系统数据库设置表ID", range = "不为空")
	public void sendDBCollectTaskById(long colSetId) {
		// 1、根据数据库设置ID，在源系统数据库设置表中查询该任务是否存在
		long count =
			Dbo.queryNumber(
				"select count(1) from " + Database_set.TableName + " where database_id = ?",
				colSetId)
				.orElseThrow(() -> new BusinessException("SQL查询错误"));
		if (count != 1) {
			throw new BusinessException("未找到数据库采集任务");
		}
		// 2、任务存在，则查询数据，开始拼接要发送到agent端的信息
		// 2-1、首先根据数据库设置ID查询源系统数据库信息
		Result sourceDBConfResult =
			Dbo.queryResult(
				"SELECT dbs.agent_id, dbs.database_id, dbs.task_name,"
					+ " dbs.database_name, dbs.database_pad,"
					+ " dbs.database_drive, dbs.database_type, dbs.user_name, dbs.database_ip, dbs.database_port,"
					+ " dbs.host_name, dbs.system_type, dbs.is_sendok, dbs.database_number, dbs.db_agent, dbs.plane_url,"
					+ " dbs.database_separatorr, dbs.row_separator, dbs.classify_id,  dbs.jdbc_url, ds.datasource_number,"
					+ " cjc.classify_num FROM "
					+ Data_source.TableName
					+ " ds"
					+ " JOIN "
					+ Agent_info.TableName
					+ " ai ON ds.source_id = ai.source_id"
					+ " JOIN "
					+ Database_set.TableName
					+ " dbs ON ai.agent_id = dbs.agent_id"
					+ " left join "
					+ Collect_job_classify.TableName
					+ " cjc on dbs.classify_id = cjc.classify_id"
					+ " where dbs.database_id = ?",
				colSetId);

		// 2-2、将结果集转换为JSONObject，方便往里面塞数据
		if (sourceDBConfResult.getRowCount() != 1) {
			throw new BusinessException("根据数据库采集任务ID查询到的任务配置信息不唯一");
		}
		JSONArray array = JSONArray.parseArray(sourceDBConfResult.toJSON());
		JSONObject sourceDBConfObj = array.getJSONObject(0);

		// 2-3、TODO 信号文件信息暂时没有，所以先设置一个空的集合，后期要去singal_file表里面查
		sourceDBConfObj.put("signal_file_list", new ArrayList<>());

		// 2-4、查询并组装采集表配置信息数组，除数据库中查询出的内容，还需要组装表采集字段集合、列合并参数信息、表存储配置信息
		Result collectTableResult =
			Dbo.queryResult(
				"SELECT dbs.database_id, ti.table_id, ti.table_name, "
					+ "ti.table_ch_name, ti.table_count, ti.source_tableid, ti.valid_s_date, ti.valid_e_date, ti.sql, "
					+ "ti.remark, ti.is_user_defined, ti.is_md5,ti.is_register,ti.is_parallel,ti.page_sql,ti.rec_num_date,"
					+ "ti.unload_type,ti.is_customize_sql,ti.pageparallels, ti.dataincrement,tsi.storage_type, "
					+ "tsi.storage_time, tsi.is_zipper, tsi.hyren_name as hbase_name, ds.datasource_name, ai.agent_name, ai.agent_id, ds.source_id, "
					+ "ai.user_id , dsr.storage_date FROM "
					+ Data_source.TableName
					+ " ds "
					+ " JOIN "
					+ Agent_info.TableName
					+ " ai ON ds.source_id = ai.source_id"
					+ " JOIN "
					+ Database_set.TableName
					+ " dbs ON ai.agent_id = dbs.agent_id"
					+ " LEFT JOIN "
					+ Table_info.TableName
					+ " ti on ti.database_id = dbs.database_id"
					+ " LEFT JOIN "
					+ Collect_job_classify.TableName
					+ " cjc on dbs.classify_id = cjc.classify_id"
					+
					//				" LEFT JOIN " + Data_extraction_def.TableName + " ded on ti.table_id =
					// ded.table_id" +
					" LEFT JOIN "
					+ Table_storage_info.TableName
					+ " tsi on tsi.table_id = ti.table_id"
					+ " LEFT JOIN "
					+ Data_store_reg.TableName
					+ " dsr on dsr.table_id = ti.table_id "
					+ " WHERE dbs.database_id = ?",
				colSetId);

		// collectTables是从数据库中查询出的当前数据库采集任务要采集的表部分配置信息
		JSONArray collectTables = JSONArray.parseArray(collectTableResult.toJSON());

		for (int i = 0; i < collectTables.size(); i++) {
			JSONObject collectTable = collectTables.getJSONObject(i);
			//			collectTable.put("etlDate", DateUtil.getSysDate());
			Long tableId = collectTable.getLong("table_id");
			// 查询数据抽取定义，一张表对应多种数据抽取格式，放到对应的表的JSONObject中
			List<Data_extraction_def> data_extraction_defs =
				Dbo.queryList(
					Data_extraction_def.class,
					"select * from " + Data_extraction_def.TableName + " where table_id = ?",
					tableId);
			if (!data_extraction_defs.isEmpty()) {
				collectTable.put("data_extraction_def_list", data_extraction_defs);
			} else {
				collectTable.put("data_extraction_def_list", new ArrayList<>());
			}
			// 2-4-1、查询当前数据库采集任务下每张表的列合并信息，放入对应的表的JSONObject中
			List<Column_merge> columnMerges =
				Dbo.queryList(
					Column_merge.class,
					"select * from " + Column_merge.TableName + " where table_id = ?",
					tableId);
			if (!columnMerges.isEmpty()) {
				collectTable.put("column_merge_list", columnMerges);
			} else {
				collectTable.put("column_merge_list", new ArrayList<>());
			}
			// 2-4-2、查询表采集字段集合，放入对应表的JSONObject中，并且只查询采集的列
			Result tableColResult =
				Dbo.queryResult(
					"select tc.column_id, tc.is_primary_key, tc.column_name, tc.column_ch_name, "
						+ " tc.valid_s_date, tc.valid_e_date, tc.is_get, tc.column_type, tc.tc_remark, tc.is_alive, "
						+ " tc.is_new, tc.tc_or from "
						+ Table_column.TableName
						+ " tc where tc.table_id = ? and tc.is_get = ?",
					tableId,
					IsFlag.Shi.getCode());
			// tableColArray是从数据库找那中查询出的当前采集表字段信息
			JSONArray tableColArray = JSONArray.parseArray(tableColResult.toJSON());
			// 2-4-3、遍历要采集的每个列，将每个列的清洗信息查询出来
			for (int j = 0; j < tableColArray.size(); j++) {
				JSONObject tableColObj = tableColArray.getJSONObject(j);
				Long columnId = tableColObj.getLong("column_id");
				Result columnCleanResult =
					Dbo.queryResult(
						"select cc.col_clean_id, cc.clean_type ,cc.character_filling, cc.filling_length, "
							+ " cc.field, cc.replace_feild, cc.filling_type, cc.convert_format, cc.old_format "
							+ " from "
							+ Column_clean.TableName
							+ " cc where cc.column_id = ?",
						columnId);
				JSONArray columnCleanArray = JSONArray.parseArray(columnCleanResult.toJSON());

				// 2-4-4、判断是否有码值转换信息，如果有，需要把码值转换信息解析出来，拼成json字符串
				long CVCount =
					Dbo.queryNumber(
						"select count(1) from "
							+ Column_clean.TableName
							+ " where clean_type = ? and column_id = ?",
						CleanType.MaZhiZhuanHuan.getCode(),
						columnId)
						.orElseThrow(() -> new BusinessException("SQL查询错误"));
				if (CVCount > 0) {
					for (int k = 0; k < columnCleanArray.size(); k++) {
						JSONObject columnCleanObj = columnCleanArray.getJSONObject(k);
						Long colCleanId = columnCleanObj.getLong("col_clean_id");
						Result CVCResult =
							Dbo.queryResult(
								"select codename,codesys from "
									+ Column_clean.TableName
									+ " where col_clean_id = ? and clean_type = ?",
								colCleanId,
								CleanType.MaZhiZhuanHuan.getCode());
						if (CVCResult.getRowCount() == 1) {
							Result CVResult =
								Dbo.queryResult(
									"select code_value,orig_value from "
										+ Orig_code_info.TableName
										+ " where code_classify = ? and orig_sys_code = ?",
									CVCResult.getString(0, "codename"),
									CVCResult.getString(0, "codesys"));
							JSONArray CVArray = JSONArray.parseArray(CVResult.toJSON());
							columnCleanObj.put("codeTransform", CVArray);
						} else {
							columnCleanObj.put("codeTransform", "");
						}
					}
				}

				// 2-4-5、判断是否有列拆分信息，如果有，需要把列拆分信息放入其中
				long splitCount =
					Dbo.queryNumber(
						"select count(1) from "
							+ Column_clean.TableName
							+ " where clean_type = ? and column_id = ?",
						CleanType.ZiFuChaiFen.getCode(),
						columnId)
						.orElseThrow(() -> new BusinessException("SQL查询错误"));
				if (splitCount > 0) {
					for (int k = 0; k < columnCleanArray.size(); k++) {
						JSONObject columnCleanObj = columnCleanArray.getJSONObject(k);
						Long colCleanId = columnCleanObj.getLong("col_clean_id");
						List<Column_split> columnSplits =
							Dbo.queryList(
								Column_split.class,
								"select * from " + Column_split.TableName + " where col_clean_id = ?",
								colCleanId);
						if (!columnSplits.isEmpty()) {
							columnCleanObj.put("column_split_list", columnSplits);
						} else {
							columnCleanObj.put("column_split_list", new ArrayList<>());
						}
					}
				}
				tableColObj.put("columnCleanBeanList", columnCleanArray);
			}
			collectTable.put("collectTableColumnBeanList", tableColArray);

			// 2-4-6、查询每张表的存储配置信息，一张表可以选择进入多个存储目的地
			Result dataStoreResult =
				Dbo.queryResult(
					"select dsl.dsl_id, dsl.dsl_name, dsl.store_type, "
						+ " dsl.is_hadoopclient, tcs.dtcs_name, lcs.dlcs_name "
						+ " from "
						+ Data_store_layer.TableName
						+ " dsl"
						+ " left join "
						+ Type_contrast_sum.TableName
						+ " tcs on dsl.dtcs_id = tcs.dtcs_id"
						+ " left join "
						+ Length_contrast_sum.TableName
						+ " lcs on dsl.dlcs_id = lcs.dlcs_id"
						+ " where dsl.dsl_id in (select drt.dsl_id from "
						+ Data_relation_table.TableName
						+ " drt where drt.storage_id = "
						+ " (select storage_id from "
						+ Table_storage_info.TableName
						+ " tsi where tsi.table_id = ?))",
					tableId);

			JSONArray dataStoreArray = JSONArray.parseArray(dataStoreResult.toJSON());

			// 2-4-7、遍历存储目的地，得到存储层配置ID，根据存储层配置ID在数据存储层配置属性表中，查询配置属性或配置文件属性
			for (int m = 0; m < dataStoreArray.size(); m++) {
				JSONObject dataStore = dataStoreArray.getJSONObject(m);
				Long dslId = dataStore.getLong("dsl_id");
				Result result =
					Dbo.queryResult(
						"select storage_property_key, storage_property_val, is_file from "
							+ Data_store_layer_attr.TableName
							+ " where dsl_id = ?",
						dslId);
				if (result.isEmpty()) {
					throw new BusinessException("根据存储层配置ID" + dslId + "未获取到存储层配置属性信息");
				}
				Map<String, String> dataStoreConnectAttr = new HashMap<>();
				Map<String, String> dataStoreLayerFile = new HashMap<>();
				for (int n = 0; n < result.getRowCount(); n++) {
					IsFlag fileFlag = IsFlag.ofEnumByCode(result.getString(n, "is_file"));
					if (fileFlag == IsFlag.Shi) {
						dataStoreLayerFile.put(
							result.getString(n, "storage_property_key"),
							result.getString(n, "storage_property_val"));
					} else {
						dataStoreConnectAttr.put(
							result.getString(n, "storage_property_key"),
							result.getString(n, "storage_property_val"));
					}
				}
				dataStore.put("data_store_connect_attr", dataStoreConnectAttr);
				dataStore.put("data_store_layer_file", dataStoreLayerFile);

				// 2-4-8、遍历该表保存进入响应存储目的地的附加字段，组装附加字段信息
				List<Object> storeLayers =
					Dbo.queryOneColumnList(
						"select dsla.dsla_storelayer from "
							+ Column_storage_info.TableName
							+ " csi"
							+ " left join "
							+ Data_store_layer_added.TableName
							+ " dsla"
							+ " on dsla.dslad_id = csi.dslad_id"
							+ " where csi.column_id in"
							+ " (select column_id from "
							+ Table_column.TableName
							+ " where table_id = ?) "
							+ " and dsla.dsl_id = ?",
						tableId,
						dslId);

				Result columnResult =
					Dbo.queryResult(
						"select dsla.dsla_storelayer, csi.csi_number, tc.column_name "
							+ " from "
							+ Column_storage_info.TableName
							+ " csi"
							+ " left join "
							+ Data_store_layer_added.TableName
							+ " dsla"
							+ " on dsla.dslad_id = csi.dslad_id"
							+ " join "
							+ Table_column.TableName
							+ " tc "
							+ " on csi.column_id = tc.column_id"
							+ " where csi.column_id in (select column_id from "
							+ Table_column.TableName
							+ " "
							+ " where table_id = ?) and dsla.dsl_id = ?",
						tableId,
						dslId);

				Map<String, Map<String, Integer>> additInfoFieldMap = new HashMap<>();
				if (!columnResult.isEmpty() && !storeLayers.isEmpty()) {
					for (Object obj : storeLayers) {
						Map<String, Integer> fieldMap = new HashMap<>();
						String storeLayer = (String) obj;
						for (int h = 0; h < columnResult.getRowCount(); h++) {
							String dslaStoreLayer = columnResult.getString(h, "dsla_storelayer");
							if (storeLayer.equals(dslaStoreLayer)) {
								fieldMap.put(
									columnResult.getString(h, "column_name"),
									columnResult.getInteger(h, "csi_number"));
							}
						}
						additInfoFieldMap.put(storeLayer, fieldMap);
					}
				}
				dataStore.put("additInfoFieldMap", additInfoFieldMap);
			}
			collectTable.put("dataStoreConfBean", dataStoreArray);
		}
		// 到此为止，向agent发送的数据全部组装完毕
		sourceDBConfObj.put("collectTableBeanArray", collectTables);
		// return sourceDBConfObj.toJSONString();
		// 3、调用工具类，发送信息，接收agent端响应状态码，如果发送失败，则抛出异常给前端
		String methodName = AgentActionUtil.SENDDBCOLLECTTASKINFO;
		// TODO 前端调用这个方法应该传入跑批日期，作业调度同样
		// TODO 由于目前定义作业还没有原型，因此暂时手动将跑批日期设为当前日期
		SendMsgUtil.sendDBCollectTaskInfo(
			sourceDBConfObj.getLong("database_id"),
			sourceDBConfObj.getLong("agent_id"),
			getUserId(),
			sourceDBConfObj.toJSONString(),
			methodName,
			DateUtil.getSysDate());
	}

	@Method(
		desc = "根据参数获得任务日志信息",
		logicStep =
			""
				+ "1、根据agent_id和user_id获取agent信息"
				+ "2、在agent信息中获取日志目录"
				+ "3、调用方法获取日志,目前工具类不存在"
				+ "4、将日志信息和日志文件的路径封装成map"
				+ "5、返回map")
	@Param(
		name = "agentId",
		desc =
			""
				+ "1: 检查当前的agent信息是否存在 "
				+ "2: 获取agent的信息 "
				+ "3: 查询agent的部署信息,这里可能出现重复agent的情况..所以使用agent ip 和端口进行查询,只要有部署过就可以,否则抛出异常错误"
				+ "4: 读取日志信息提供给页面展示"
				+ "   4-1: 检查当前的部署日志路径是否存在"
				+ "   4-2: 检查Agent的IP是否为空"
				+ "   4-3: 检查Agent的端口是否为空"
				+ "   4-4: 检查Agent的部署用户名是否为空"
				+ "   4-5: 检查Agent的部署密码是否为空"
				+ "5: 使用工具类,读取日志信息"
				+ "6: 将读取的日志信息返回",
		range = "不为空")
	@Param(name = "userId", desc = "用户ID，用户表主键, agent下载信息表外键", range = "不为空")
	@Param(name = "readNum", desc = "查看日志条数", range = "不为空")
	@Return(desc = "存放文件内容和日志文件路径的map集合", range = "获取文件内容，key为log，" + "获取文件路径,key为filePath")
	private Map<String, String> getTaskLog(long agentId, long userId, int readNum) {

		// 1: 检查当前的agent信息是否存在
		long countNum =
			Dbo.queryNumber(
				"SELECT COUNT(1) FROM " + Agent_info.TableName + " WHERE agent_id = ?", agentId)
				.orElseThrow(() -> new BusinessException("查询Agent信息的SQL错误"));
		if (countNum == 0) {
			throw new BusinessException("为获取到Agent(" + agentId + ")的信息");
		}

		//    2: 获取agent的信息
		Agent_info agent_info =
			Dbo.queryOneObject(
				Agent_info.class,
				"SELECT * FROM " + Agent_info.TableName + " WHERE agent_id = ?",
				agentId)
				.orElseThrow(() -> new BusinessException("根据AgentId(" + agentId + ")获取的信息出现了多条"));

		//    3: 查询agent的部署信息,这里可能出现重复agent的情况..所以使用agent ip 和端口进行查询,只要有部署过就可以,否则抛出异常错误
		List<Agent_down_info> list =
			Dbo.queryList(
				Agent_down_info.class,
				"SELECT * FROM " + Agent_down_info.TableName + " WHERE agent_ip = ? AND agent_port = ?",
				agent_info.getAgent_ip(),
				agent_info.getAgent_port());

		if (list.isEmpty()) {
			throw new BusinessException(
				"根据Agent IP("
					+ agent_info.getAgent_ip()
					+ "),端口("
					+ agent_info.getAgent_port()
					+ "),为获取到对应的部署信息");
		}
		// 4: 获取一条部署的信息即可
		Agent_down_info agent_down_info = list.get(0);
		//    4: 读取日志信息提供给页面展示
		//    4-1: 检查当前的部署日志路径是否存在
		if (StringUtil.isEmpty(agent_down_info.getLog_dir())) {
			throw new BusinessException("Agent部署的日志路径是空的");
		}
		//    4-2: 检查Agent的IP是否为空
		if (StringUtil.isEmpty(agent_down_info.getAgent_ip())) {
			throw new BusinessException("Agent部署的IP是空的");
		}
		//    4-3: 检查Agent的端口是否为空
		if (StringUtil.isEmpty(agent_down_info.getAgent_port())) {
			throw new BusinessException("Agent部署的端口是空的");
		}
		//    4-4: 检查Agent的部署用户名是否为空
		if (StringUtil.isEmpty(agent_down_info.getUser_name())) {
			throw new BusinessException("Agent部署用户名是空的");
		}
		//    4-5: 检查Agent的部署密码是否为空
		if (StringUtil.isEmpty(agent_down_info.getPasswd())) {
			throw new BusinessException("Agent部署的用户密码是空的");
		}

		SFTPDetails sftpDetails = new SFTPDetails();
		sftpDetails.setHost(agent_down_info.getAgent_ip());
		sftpDetails.setPort(Integer.parseInt(Constant.SFTP_PORT));
		sftpDetails.setUser_name(agent_down_info.getUser_name());
		sftpDetails.setPwd(agent_down_info.getPasswd());
		// 5: 使用工具类,读取日志信息
		String taskLog = ReadLog.readAgentLog(agent_down_info.getLog_dir(), sftpDetails, readNum);
		if (StringUtil.isBlank(taskLog)) {
			taskLog = "未获取到日志";
		}
		// 5: 将读取的日志信息返回
		Map<String, String> map = new HashMap<>();
		map.put("log", taskLog);
		map.put("filePath", agent_down_info.getLog_dir());

		return map;
	}

	@Method(
		desc = "删除tableId为外键的表脏数据",
		logicStep =
			""
				+ "1、删除column_id做外键的的表脏数据"
				+ "2、删除旧的tableId在采集字段表中做外键的数据，不关注删除的数目"
				+ "3、删除旧的tableId在数据抽取定义表做外键的数据，不关注删除的数目"
				+ "4、删除旧的tableId在存储信息表做外键的数据，不关注删除的数目，同时，其对应的存储目的地关联关系也要删除"
				+ "5、删除旧的tableId在列合并表做外键的数据，不关注删除的数目"
				+ "6、删除旧的tableId在表清洗规则表做外键的数据，不关注删除的数目")
	@Param(
		name = "tableId",
		desc = "数据库对应表ID，" + "数据抽取定义表、表存储信息表、列合并表、表清洗规则表、表对应字段表表外键",
		range = "不为空")
	private void deleteDirtyDataOfTb(long tableId) {
		// 1、删除column_id做外键的的表脏数据
		List<Object> columnIds =
			Dbo.queryOneColumnList(
				"select column_id from " + Table_column.TableName + " WHERE table_id = ?", tableId);
		if (!columnIds.isEmpty()) {
			for (Object columnId : columnIds) {
				deleteDirtyDataOfCol((long) columnId);
			}
		}
		// 2、删除旧的tableId在采集字段表中做外键的数据，不关注删除的数目
		Dbo.execute(" DELETE FROM " + Table_column.TableName + " WHERE table_id = ? ", tableId);
		// 3、删除旧的tableId在数据抽取定义表做外键的数据，不关注删除的数目
		Dbo.execute(" DELETE FROM " + Data_extraction_def.TableName + " WHERE table_id = ? ", tableId);
		// 4、删除旧的tableId在存储信息表做外键的数据，不关注删除的数目，同时，其对应的存储目的地关联关系也要删除
		Dbo.execute(
			" DELETE FROM "
				+ Data_relation_table.TableName
				+ " WHERE storage_id = "
				+ "(SELECT storage_id FROM "
				+ Table_storage_info.TableName
				+ " WHERE table_id = ?) ",
			tableId);
		Dbo.execute(" DELETE FROM " + Table_storage_info.TableName + " WHERE table_id = ? ", tableId);
		// 5、删除旧的tableId在列合并表做外键的数据，不关注删除的数目
		Dbo.execute(" DELETE FROM " + Column_merge.TableName + " WHERE table_id = ? ", tableId);
		// 6、删除旧的tableId在表清洗规则表做外键的数据，不关注删除的数目
		Dbo.execute(" DELETE FROM " + Table_clean.TableName + " WHERE table_id = ? ", tableId);
	}

	@Method(
		desc = "删除columnId为外键的表脏数据",
		logicStep =
			""
				+ "1、删除旧的columnId在字段存储信息表中做外键的数据，不关注删除的数目"
				+ "2、删除旧的columnId在列清洗信息表做外键的数据，不关注删除的数目"
				+ "3、删除旧的columnId在列拆分信息表做外键的数据，不关注删除的数目")
	@Param(name = "columnId", desc = "表对应字段表ID，" + "字段存储信息表、列清洗信息表、列拆分信息表外键", range = "不为空")
	private void deleteDirtyDataOfCol(long columnId) {
		// 1、删除旧的columnId在字段存储信息表中做外键的数据，不关注删除的数目
		Dbo.execute("delete from " + Column_storage_info.TableName + " where column_id = ?", columnId);
		// 2、删除旧的columnId在列清洗信息表做外键的数据，不关注删除的数目
		Dbo.execute("delete from " + Column_clean.TableName + " where column_id = ?", columnId);
		// 3、删除旧的columnId在列拆分信息表做外键的数据，不关注删除的数目
		Dbo.execute("delete from " + Column_split.TableName + " where column_id = ?", columnId);
	}

	@Method(
		desc = "查询Agent的部署信息,查看日志使用",
		logicStep = "1: 检查Agent信息是否存在 2: 获取Agent信息 3: 检查Agent是否部署过 4:查询部署的信息,并返回")
	@Param(name = "agent_id", desc = "AgentID", range = "不可为空")
	@Return(desc = "返回Agent的部署信息", range = "不可为空,为空在验证时就会抛出错误信息")
	public List<Agent_down_info> agentDeployData(long agent_id) {

		//    1: 检查Agent信息是否存在
		long countNum =
			Dbo.queryNumber(
				"SELECT COUNT(1) FROM " + Agent_info.TableName + " WHERE agent_id = ?", agent_id)
				.orElseThrow(() -> new BusinessException("SQL查询异常"));
		if (countNum == 0) {
			throw new BusinessException("未找到AgentId(" + agent_id + ")的信息");
		}
		//    2: 获取Agent信息
		Map<String, Object> agentMap =
			Dbo.queryOneObject(
				"SELECT agent_ip,agent_port FROM " + Agent_info.TableName + " WHERE agent_id = ?",
				agent_id);

		//    3: 检查Agent是否部署过
		countNum =
			Dbo.queryNumber(
				"SELECT COUNT(1) FROM "
					+ Agent_down_info.TableName
					+ " WHERE agent_ip = ? AND agent_port = ?",
				agentMap.get("agent_ip"),
				agentMap.get("agent_port"))
				.orElseThrow(() -> new BusinessException("SQL查询异常"));
		if (countNum == 0) {
			throw new BusinessException(
				String.format(
					"未找到Agent的部署信息,IP: %s,端口: %s", agentMap.get("agnet_ip"), agentMap.get("agent_port")));
		}
		//    4:查询部署的信息,并返回
		return Dbo.queryList(
			Agent_down_info.class,
			"SELECT agent_ip,log_dir FROM "
				+ Agent_down_info.TableName
				+ " WHERE agent_ip = ? AND agent_port = ?",
			agentMap.get("agent_ip"),
			agentMap.get("agent_port"));
	}
}
