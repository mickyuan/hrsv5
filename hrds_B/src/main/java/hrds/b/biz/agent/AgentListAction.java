package hrds.b.biz.agent;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.CodecUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.resultset.Result;
import fd.ng.web.util.Dbo;
import fd.ng.web.util.RequestUtil;
import fd.ng.web.util.ResponseUtil;
import hrds.b.biz.agent.tools.LogReader;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.AgentType;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.*;
import hrds.commons.exception.AppSystemException;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.DboExecute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@DocClass(desc = "获取数据源Agent列表", author = "WangZhengcheng")
public class AgentListAction extends BaseAction {

	@Method(desc = "获取数据源Agent列表信息", logicStep = "1、获取用户ID并根据用户ID去数据库中查询数据源信息")
	@Return(desc = "数据源信息查询结果集", range = "不会为null")
	public Result getAgentInfoList() {
		//1、获取用户ID并根据用户ID去数据库中查询数据源信息
		return Dbo.queryResult("select datas.source_id,datasource_name from " + Data_source.TableName
				+ " datas " + "where datas.create_user_id = ?", getUserId());
		//数据可访问权限处理方式
		//以上SQL中，通过当前用户ID进行关联查询，达到了数据权限的限制
	}

	@Method(desc = "根据数据源ID、agent类型获取Agent信息", logicStep = "" +
			"1、获取用户ID并根据用户ID去数据库中查询数据源信息")
	@Param(name = "sourceId", desc = "数据源ID,数据源表主键，agent信息表外键", range = "不为空")
	@Param(name = "agentType", desc = "agent类型", range = "AgentType代码项的code值" +
			"1：数据库采集Agent" +
			"2：非结构化采集Agent" +
			"3：Ftp采集Agent" +
			"4：数据文件采集Agent" +
			"5：半结构化采集Agent")
	@Return(desc = "Agent信息查询结果集", range = "不会为null")
	public Result getAgentInfo(long sourceId, String agentType) {
		//1、根据sourceId和agentType查询数据库获取相应信息
		return Dbo.queryResult("SELECT * FROM "+ Agent_info.TableName +" WHERE source_id = ? " +
				"AND agent_type = ? AND user_id = ?", sourceId, agentType, getUserId());
		//数据可访问权限处理方式
		//以上SQL中，通过当前用户ID进行关联查询，达到了数据权限的限制
	}

	@Method(desc = "根据数据源ID和AgentId获取该agent下所有任务的信息", logicStep = "" +
			"1、获取用户ID, 判断在当前用户，当前数据源下，某一类型的agent是否存在" +
			"2、如果存在，查询结果中应该有且只有一条数据" +
			"3、判断该agent是那种类型，并且根据类型，到对应的数据库表中查询采集任务管理详细信息" +
			"4、返回结果")
	@Param(name = "sourceId", desc = "数据源ID,数据源表主键，agent信息表外键", range = "不为空")
	@Param(name = "agentId", desc = "agentID,agent信息表主键", range = "不为空")
	@Return(desc = "查询结果集", range = "不会为null")
	//TODO 采集频率目前暂未拿到
	public Result getTaskInfo(long sourceId, long agentId) {
		//1、判断在当前用户，当前数据源下，agent是否存在
		Map<String, Object> agentInfo = Dbo.queryOneObject("select ai.agent_type,ai.agent_id from " +
				Data_source.TableName + " ds  left join " + Agent_info.TableName +
				" ai on ds.SOURCE_ID = ai.SOURCE_ID  where ds.source_id = ? AND ai.user_id = ? " +
				" AND ai.agent_id = ?", sourceId, getUserId(), agentId);

		//数据可访问权限处理方式
		//以上SQL中，通过当前用户ID进行关联查询，达到了数据权限的限制

		//2、如果存在，查询结果中应该有且只有一条数据
		if (agentInfo.isEmpty()) {
			throw new BusinessException("未找到Agent");
		}

		//3、判断该agent是那种类型，并且根据类型，到对应的数据库表中查询采集任务管理详细信息
		String sqlStr;
		AgentType agentType = AgentType.ofEnumByCode((String) agentInfo.get("agent_type"));
		//数据库直连采集Agent
		if (AgentType.ShuJuKu == agentType) {
			sqlStr = " SELECT ds.DATABASE_ID ID,ds.task_name task_name,ds.AGENT_ID AGENT_ID," +
					" gi.source_id source_id" +
					" FROM "+ Database_set.TableName +" ds " +
					" LEFT JOIN "+ Agent_info.TableName +" gi ON ds.Agent_id = gi.Agent_id " +
					" where ds.Agent_id=? and ds.is_sendok = ? ";
		}
		//数据文件Agent
		else if (AgentType.DBWenJian == agentType){
			sqlStr = " SELECT ds.DATABASE_ID ID,ds.task_name task_name,ds.AGENT_ID AGENT_ID," +
					" gi.source_id source_id" +
					" FROM "+ Database_set.TableName +" ds " +
					" LEFT JOIN "+ Agent_info.TableName +" gi ON ds.Agent_id = gi.Agent_id " +
					" where ds.Agent_id=? and ds.is_sendok = ? ";
		}
		//半结构化采集Agent
		else if (AgentType.DuiXiang == agentType){
			sqlStr = " SELECT fs.odc_id id,fs.obj_collect_name task_name,fs.AGENT_ID AGENT_ID,gi.source_id" +
					" FROM "+ Object_collect.TableName +" fs " +
					" LEFT JOIN "+ Agent_info.TableName +" gi ON gi.Agent_id = fs.Agent_id " +
					" WHERE fs.Agent_id = ? AND fs.is_sendok = ? ";
		}
		//FtpAgent
		else if (AgentType.FTP == agentType){
			sqlStr = " SELECT fs.ftp_id id,fs.ftp_name task_name,fs.AGENT_ID AGENT_ID,gi.source_id" +
					" FROM "+ Ftp_collect.TableName +" fs " +
					" LEFT JOIN "+ Agent_info.TableName +" gi ON gi.Agent_id = fs.Agent_id " +
					" WHERE fs.Agent_id = ? and fs.is_sendok = ? ";
		}
		//非结构化Agent
		else if(AgentType.WenJianXiTong == agentType){
			sqlStr = " SELECT fs.fcs_id id,fs.fcs_name task_name,fs.AGENT_ID AGENT_ID,gi.source_id" +
					" FROM "+ File_collect_set.TableName +" fs " +
					" LEFT JOIN "+ Agent_info.TableName +" gi ON gi.Agent_id = fs.Agent_id " +
					" where fs.Agent_id=? and fs.is_sendok = ? ";
		}
		else {
			throw new BusinessException("从数据库中取到的Agent类型不合法");
		}
		//5、返回结果
		return Dbo.queryResult(sqlStr, agentInfo.get("agent_id"),
				IsFlag.Shi.getCode());
	}

	@Method(desc = "查看任务日志", logicStep = "" +
			"1、对显示日志条数做处理，该方法在加载页面时被调用，readNum可以不传，则默认显示100条，" +
			"如果用户在页面上进行了选择并点击查看按钮，则最多给用户显示1000条日志" +
			"2、调用方法读取日志并返回")
	@Param(name = "agentId", desc = "数据源ID,data_source表主键，agent_info表外键", range = "不为空")
	@Param(name = "logType", desc = "日志类型(完整日志、错误日志)", range = "All : 完整日志, Wrong : 错误日志")
	@Param(name = "readNum", desc = "查看日志条数", range = "该参数可以不传", nullable = true,
			valueIfNull = "100")
	@Return(desc = "日志信息" ,range = "不会为null")
	public String viewTaskLog(long agentId, String logType, int readNum) {
		//1、对显示日志条数做处理，该方法在加载页面时被调用，readNum可以不传，则默认显示100条，
		// 如果用户在页面上进行了选择并点击查看按钮，则最多给用户显示1000条日志
		if (readNum > 1000) readNum = 1000;
		//2、调用方法读取日志并返回
		return getTaskLog(agentId, getUserId(), logType, readNum).get("log");
		//数据可访问权限处理方式
		//在getTaskLog()方法中做了处理
	}

	@Method(desc = "任务日志下载", logicStep = "" +
			"1、对显示日志条数做处理，该方法在加载页面时被调用，readNum可以不传，则默认显示100条，" +
			"如果用户在页面上进行了选择并点击查看按钮，如果用户输入的条目多于1000，则给用户显示3000条" +
			"2、调用方法读取日志，获得日志信息和日志文件路径" +
			"3、将日志信息由字符串转为byte[]" +
			"4、得到本次http交互的request和response" +
			"5、设置响应头信息" +
			"6、使用response获得输出流，完成文件下载")
	@Param(name = "agentId", desc = "数据源ID,data_source表主键，agent_info表外键", range = "不为空")
	@Param(name = "logType", desc = "日志类型(完整日志、错误日志)", range = "All : 完整日志, Wrong : 错误日志")
	@Param(name = "readNum", desc = "查看日志条数", range = "该参数可以不传", nullable = true,
			valueIfNull = "100")
	public void downloadTaskLog(long agentId, String logType, int readNum) {
		//1、对显示日志条数做处理，该方法在加载页面时被调用，readNum可以不传，则默认显示100条，
		// 如果用户在页面上进行了选择并点击查看按钮，如果用户输入的条目多于1000，则给用户显示3000条
		if (readNum > 1000) readNum = 3000;
		//2、调用方法读取日志，获得日志信息和日志文件路径
		Map<String, String> taskLog = getTaskLog(agentId, getUserId(), logType, readNum);

		//3、将日志信息由字符串转为byte[]
		byte[] bytes = taskLog.get("log").getBytes();

		//4、得到本次http交互的request和response
		HttpServletResponse response = ResponseUtil.getResponse();
		HttpServletRequest request = RequestUtil.getRequest();

		File downloadFile = new File(taskLog.get("filePath"));

		try(OutputStream out = response.getOutputStream()) {
			//5、设置响应头信息
			response.reset();
			if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {
				// 对firefox浏览器做特殊处理
				response.setHeader("content-disposition", "attachment;filename=" +
						new String(downloadFile.getName().getBytes(), CodecUtil.GBK_STRING));
			} else {
				response.setHeader("content-disposition", "attachment;filename=" +
						URLEncoder.encode(downloadFile.getName(), CodecUtil.UTF8_STRING));
			}
			response.setContentType("APPLICATION/OCTET-STREAM");
			//6、使用response获得输出流，完成文件下载
			out.write(bytes);
			out.flush();
			//数据可访问权限处理方式
			//在getTaskLog()方法中做了处理
		} catch (IOException e) {
			throw new AppSystemException(e);
		}
	}

	@Method(desc = "根据ID删除半结构化采集任务数据", logicStep = "" +
			"1、根据collectSetId和user_id判断是否有这样一条数据" +
			"2、在对象采集设置表(object_collect)中删除该条数据")
	@Param(name = "collectSetId", desc = "半结构化采集设置表ID", range = "不为空")
	//TODO IOnWayCtrl.checkExistsTask()暂时先不管
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

		//1、根据collectSetId和user_id判断是否有这样一条数据
		long val = Dbo.queryNumber("select count(1) from " + Data_source.TableName + " ds " +
				" join " + Agent_info.TableName + " ai on ai.source_id = ds.source_id " +
				" join " + Object_collect.TableName + " oc on ai.Agent_id = oc.Agent_id " +
				" where ds.create_user_id = ? and oc.odc_id = ?", getUserId(),
				collectSetId).orElseThrow(() -> new BusinessException("查询得到的数据必须有且只有一条"));
		if(val != 1){
			throw new BusinessException("要删除的半结构化文件采集任务不存在");
		}
		//数据可访问权限处理方式
		//该方法首先使用user_id和collectSetId去数据库中查找要删除的数据是否存在

		//2、在对象采集设置表(object_collect)中删除该条数据，有且只有一条
		DboExecute.deletesOrThrow("删除半结构化采集任务异常!", "delete from "+
				Object_collect.TableName + " where odc_id = ?",collectSetId );
	}

	@Method(desc = "根据ID删除FTP采集任务数据", logicStep = "" +
			"1、根据collectSetId和user_id判断是否有这样一条数据" +
			"2、在FTP采集设置表(ftp_collect)中删除该条数据")
	@Param(name = "collectSetId", desc = "FTP采集设置表ID", range = "不为空")
	//TODO IOnWayCtrl.checkExistsTask()暂时先不管
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

		//1、根据collectSetId和user_id判断是否有这样一条数据
		long val = Dbo.queryNumber("select count(1) from " + Data_source.TableName + " ds " +
				" join " + Agent_info.TableName + " ai on ai.source_id = ds.source_id " +
				" join " + Ftp_collect.TableName + " fc on ai.Agent_id = fc.Agent_id " +
				" where ds.create_user_id = ? and fc.ftp_id = ?", getUserId(), collectSetId)
				.orElseThrow(() -> new BusinessException("查询得到的数据必须有且只有一条"));
		if(val != 1){
			throw new BusinessException("要删除的FTP采集任务不存在");
		}
		//数据可访问权限处理方式
		//该方法首先使用user_id和collectSetId去数据库中查找要删除的数据是否存在

		//2、在FTP采集设置表(ftp_collect)中删除该条数据，有且只有一条
		DboExecute.deletesOrThrow("删除FTP采集任务数据异常!", "delete from "+
				Ftp_collect.TableName +" where ftp_id = ?", collectSetId);
	}

	@Method(desc = "根据ID删除数据库直连采集任务", logicStep = "" +
			"1、根据collectSetId和user_id判断是否有这样一条数据" +
			"2、在数据库设置表中删除对应的记录" +
			"3、在表对应字段表中找到对应的记录并删除" +
			"4、在数据库对应表删除对应的记录")
	@Param(name = "collectSetId", desc = "源系统数据库设置表ID", range = "不为空")
	//TODO IOnWayCtrl.checkExistsTask()暂时先不管
	public void deleteDBTask(long collectSetId){
		//1、根据collectSetId和user_id判断是否有这样一条数据
		long val = Dbo.queryNumber("select count(1) from " + Data_source.TableName + " ds " +
				" join " + Agent_info.TableName + " ai on ai.source_id = ds.source_id " +
				" join " + Database_set.TableName + " dbs on ai.Agent_id = dbs.Agent_id " +
				" where ds.create_user_id = ? and dbs.database_id = ?", getUserId(),
				collectSetId).orElseThrow(() -> new BusinessException("查询得到的数据必须有且只有一条"));
		if(val != 1){
			throw new BusinessException("要删除的数据库直连采集任务不存在");
		}
		//数据可访问权限处理方式
		//该方法首先使用user_id和collectSetId去数据库中查找要删除的数据是否存在

		//2、在数据库设置表中删除对应的记录，有且只有一条
		DboExecute.deletesOrThrow("删除数据库直连采集任务异常!", "delete from "+
				Database_set.TableName +" where database_id = ? ", collectSetId);
		//3、在表对应字段表中找到对应的记录并删除，可能会有多条
		int secNum = Dbo.execute("delete from "+ Table_column.TableName +" tc where EXISTS" +
				"(select 1 from "+ Table_info.TableName +" ti where database_id = ? " +
				"and tc.table_id = ti.table_id)", collectSetId);
		if (secNum == 0) {
			throw new BusinessException("删除数据库直连采集任务异常!");
		}
		//4、在数据库对应表删除对应的记录,可能会有多条
		int thiExecute = Dbo.execute("delete from "+ Table_info.TableName +
						" where database_id = ?", collectSetId);
		if (thiExecute == 0) {
			throw new BusinessException("删除数据库直连采集任务异常!");
		}
	}

	@Method(desc = "根据ID删除DB文件采集任务数据", logicStep = "" +
			"1、根据collectSetId和user_id判断是否有这样一条数据" +
			"2、在数据库设置表中删除对应的记录")
	@Param(name = "collectSetId", desc = "源系统数据库设置表ID", range = "不为空")
	//TODO IOnWayCtrl.checkExistsTask()暂时先不管
	public void deleteDFTask(long collectSetId){
		//1、根据collectSetId和user_id判断是否有这样一条数据
		long val = Dbo.queryNumber("select count(1) from " + Data_source.TableName + " ds " +
				" join " + Agent_info.TableName + " ai on ai.source_id = ds.source_id " +
				" join " + Database_set.TableName + " dbs on ai.Agent_id = dbs.Agent_id " +
				" where ds.create_user_id = ? and dbs.database_id = ?", getUserId(),
				collectSetId).orElseThrow(() -> new BusinessException("查询得到的数据必须有且只有一条"));
		if(val != 1){
			throw new BusinessException("要删除的数据文件采集任务不存在");
		}
		//数据可访问权限处理方式
		//该方法首先使用user_id和collectSetId去数据库中查找要删除的数据是否存在
		//2、在数据库设置表删除对应的记录，有且只有一条
		DboExecute.deletesOrThrow("删除数据文件采集任务异常!", "delete from "+
				Database_set.TableName +" where database_id =?", collectSetId);
	}

	@Method(desc = "根据ID删除非结构化文件采集任务数据", logicStep = "" +
			"1、根据collectSetId和user_id判断是否有这样一条数据" +
			"2、在文件系统设置表删除对应的记录" +
			"3、在文件源设置表删除对应的记录")
	@Param(name = "collectSetId", desc = "非结构化采集设置表ID", range = "不为空")
	//TODO IOnWayCtrl.checkExistsTask()暂时先不管
	public void deleteNonStructTask(long collectSetId){
		//1、根据collectSetId和user_id判断是否有这样一条数据
		long val = Dbo.queryNumber("select count(1) from " + Data_source.TableName + " ds " +
				" join " + Agent_info.TableName + " ai on ai.source_id = ds.source_id " +
				" join " + File_collect_set.TableName + " fcs on ai.Agent_id = fcs.Agent_id " +
				" where ds.create_user_id = ? and fcs.fcs_id = ?", getUserId(),
				collectSetId).orElseThrow(() -> new BusinessException("查询得到的数据必须有且只有一条"));
		if(val != 1){
			throw new BusinessException("要删除的非结构化文件采集任务不存在");
		}
		//数据可访问权限处理方式
		//该方法首先使用user_id和collectSetId去数据库中查找要删除的数据是否存在

		//2、在文件系统设置表删除对应的记录，有且只有一条数据
		DboExecute.deletesOrThrow("删除非结构化采集任务数据异常!", "delete  from "+
						File_collect_set.TableName +" where fcs_id = ? ", collectSetId);
		//3、在文件源设置表删除对应的记录，可以有多条
		int secNum = Dbo.execute("delete  from "+ File_source.TableName +" where fcs_id = ?",
				collectSetId);
		if (secNum == 0) {
			throw new BusinessException("删除非结构化采集任务异常!");
		}
	}

	@Method(desc = "查询工程信息", logicStep = "" +
			"1、根据用户ID在工程登记表(etl_sys)中查询工程代码(etl_sys_cd)和工程名称(etl_sys_name)并返回")
	@Return(desc = "查询结果集", range = "不会为null")
	public Result getProjectInfo() {
		//1、根据用户ID在工程登记表(etl_sys)中查询工程代码(etl_sys_cd)和工程名称(etl_sys_name)并返回
		return Dbo.queryResult("select etl_sys_cd,etl_sys_name from "+ Etl_sys.TableName +
						" where user_id = ?", getUserId());
		//数据可访问权限处理方式
		//该方法首先使用user_id去数据库中查询相应的数据
	}

	@Method(desc = "根据Id获得某个工程下的任务信息", logicStep = "" +
			"1、根据工程代码在子系统定义表(etl_sub_sys_list)中查询子系统代码(sub_sys_cd)和子系统描述(sub_sys_desc)并返回")
	@Param(name = "taskId", desc = "任务ID, 子系统定义表主键", range = "不为空")
	@Return(desc = "查询结果集", range = "不会为null")
	public Result getTaskInfoByTaskId(String taskId) {
		return Dbo.queryResult(" select ess.sub_sys_cd,ess.sub_sys_desc from "+
				Etl_sub_sys_list.TableName + " ess join "+ Etl_sys.TableName +
				" es on es.etl_sys_cd = ess.etl_sys_cd" +
				"where es.user_id = ? and ess.etl_sys_cd = ? ", getUserId(), taskId);
		//数据可访问权限处理方式
		//该方法首先使用user_id去数据库中查询相应的数据
	}

	/*
	@Method(desc = "保存FTP采集工程信息", logicStep = "")
	@Param(name = "ftpId", desc = "ftp_collect表主键", range = "不为空")
	@Param(name = "projectCode", desc = "工程编码，etl_sub_sys_list表主键", range = "不为空")
	@Param(name = "subSysCd", desc = "子系统代码，etl_sub_sys_list表主键，etl_job_def表外键", range = "不为空")
	public void saveFTPProjectInfo(String ftpId, String projectCode, String subSysCd) {

		StringBuilder sql = new StringBuilder();
		sql.append(" select datasource_number,datasource_name, ")
				.append(" agent_name,ftp_number,ftp_name,c.ftp_id ")
				.append(" from data_source a join agent_info b on a.source_id = b.source_id ")
				.append(" join ftp_collect c on b.agent_id = c.agent_id ")
				.append(" where c.is_sendok = ? and c.ftp_id = ? and a.create_user_id = ?");

		Map<String, Object> result = Dbo.queryOneObject(sql.toString(), IsFlag.Shi.getCode()
				, ftpId, getUserId());
		if(result.isEmpty()){
			throw new BusinessException("根据ID未能找到对应的FTP采集信息");
		}
		//数据可访问权限处理方式
		//以上SQL中，通过当前用户ID进行关联查询，达到了数据权限的限制
		String dsName = (String)result.get("datasource_name");
		String dsNumber = (String)result.get("datasource_number");
		String ftpNumber = (String)result.get("ftp_number");
		String ftpName = (String)result.get("ftp_name");

		String subSysCode = dsNumber + "_" + ftpNumber;
		String subSysDesc = dsName + "_" + ftpName;
		String etlJob = subSysCode + "_" + "etljob";
		String etlJobDesc = subSysDesc + "_" + "etljob";
		String proParam = "etljob" + "@" + "B301@2008";
	}
	*/

	/*
	@Method(desc = "保存半结构化(对象)采集工程信息", logicStep = "不为空")
	@Param(name = "objCollId", desc = "object_collect表主键", range = "")
	@Param(name = "projectCode", desc = "工程编码，etl_sub_sys_list表主键", range = "不为空")
	@Param(name = "subSysCode", desc = "子系统代码，etl_sub_sys_list表主键，etl_job_def表外键", range = "不为空")
	public void saveHalfStructProjectInfo(String objCollId, String projectCode, String subSysCode){

	}
	*/

	/*
	@Method(desc = "保存非结构化(文件系统)采集工程信息", logicStep = "")
	@Param(name = "fileCollId", desc = "file_collect_set表主键", range = "不为空")
	@Param(name = "projectCode", desc = "工程编码，etl_sub_sys_list表主键", range = "不为空")
	@Param(name = "subSysCode", desc = "子系统代码，etl_sub_sys_list表主键，etl_job_def表外键", range = "不为空")
	public void saveNonStructProjectInfo(String fileCollId, String projectCode, String subSysCode){

	}
	*/

	/*
	@Method(desc = "保存数据文件采集和数据库采集采集工程信息", logicStep = "")
	@Param(name = "dataSourceId", desc = "datasource_set表主键", range = "不为空")
	@Param(name = "projectCode", desc = "工程编码，etl_sub_sys_list表主键", range = "不为空")
	@Param(name = "subSysCode", desc = "子系统代码，etl_sub_sys_list表主键，etl_job_def表外键", range = "不为空")
	public void saveDBAndDFProjectInfo(String dataSourceId, String projectCode, String subSysCode){

	}
	*/

	@Method(desc = "根据数据源ID查询出设置完成的数据库采集任务和DB文件采集任务的任务ID", logicStep = "" +
			"1、根据数据源ID和用户ID查询出设置完成的数据库采集任务和DB文件采集任务的任务ID并返回")
	@Param(name = "sourceId", desc = "数据源表主键, agent信息表外键", range = "不为空")
	@Return(desc = "查询结果集", range = "不会为null")
	//获得设置完成的任务，用于发送
	public Result getDBAndDFTaskBySourceId(long sourceId) {
		//1、根据数据源ID和用户ID查询出设置完成的数据库采集任务和DB文件采集任务的任务ID并返回
		return Dbo.queryResult("SELECT das.database_id " +
				"FROM "+ Data_source.TableName +" ds " +
				"JOIN "+ Agent_info.TableName +" ai ON ds.source_id = ai.source_id " +
				"JOIN "+ Database_set.TableName +" das ON ai.agent_id = das.agent_id " +
				"WHERE ds.source_id = ? AND das.is_sendok = ? AND ds.create_user_id = ?"
				, sourceId, IsFlag.Shi.getCode(), getUserId());
		//数据可访问权限处理方式
		//以上SQL中，通过当前用户ID进行关联查询，达到了数据权限的限制
	}

	@Method(desc = "根据数据源ID查询出设置完成的非结构化文件采集任务的任务ID", logicStep = "" +
			"1、根据数据源ID和用户ID查询出设置完成的非结构化文件采集任务的任务ID并返回")
	@Param(name = "sourceId", desc = "数据源表主键, agent信息表外键", range = "不为空")
	@Return(desc = "查询结果集", range = "不会为null")
	//获得设置完成的任务，用于发送
	public Result getNonStructTaskBySourceId(long sourceId) {
		//1、根据数据源ID和用户ID查询出设置完成的非结构化文件采集任务的任务ID并返回
		return Dbo.queryResult("SELECT fcs.fcs_id " +
				"FROM "+ Data_source.TableName +" ds " +
				"JOIN "+ Agent_info.TableName +" ai ON ds.source_id = ai.source_id " +
				"JOIN "+ File_collect_set.TableName +" fcs ON ai.agent_id = fcs.agent_id " +
				"WHERE ds.source_id = ? AND fcs.is_sendok = ? AND ds.create_user_id = ?"
				, sourceId, IsFlag.Shi.getCode(), getUserId());
		//数据可访问权限处理方式
		//以上SQL中，通过当前用户ID进行关联查询，达到了数据权限的限制
	}

	@Method(desc = "根据数据源ID查询出设置完成的半结构化文件采集任务的任务ID", logicStep = "" +
			"1、根据数据源ID和用户ID查询出设置完成的半结构化文件采集任务的任务ID并返回")
	@Param(name = "sourceId", desc = "数据源表主键, agent信息表外键", range = "不为空")
	@Return(desc = "查询结果集", range = "不会为null")
	//获得设置完成的任务，用于发送
	public Result getHalfStructTaskBySourceId(long sourceId) {
		//1、根据数据源ID和用户ID查询出设置完成的半结构化文件采集任务的任务ID并返回
		return Dbo.queryResult("SELECT fcs.odc_id " +
				"FROM "+ Data_source.TableName +" ds " +
				"JOIN "+ Agent_info.TableName +" ai ON ds.source_id = ai.source_id " +
				"JOIN "+ Object_collect.TableName +" fcs ON ai.agent_id = fcs.agent_id " +
				"WHERE ds.source_id = ? AND fcs.is_sendok = ? AND ds.create_user_id = ?"
				, sourceId, IsFlag.Shi.getCode(), getUserId());
		//数据可访问权限处理方式
		//以上SQL中，通过当前用户ID进行关联查询，达到了数据权限的限制
	}

	@Method(desc = "根据数据源ID查询出FTP采集任务的任务ID", logicStep = "" +
			"1、根据数据源ID和用户ID查询出FTP采集任务的任务ID并返回")
	@Param(name = "sourceId", desc = "数据源表主键, agent信息表外键", range = "不为空")
	@Return(desc = "查询结果集", range = "不会为null")
	//获得设置完成的任务，用于发送
	public Result getFTPTaskBySourceId(long sourceId) {
		//1、根据数据源ID和用户ID查询出FTP采集任务的任务ID并返回
		return Dbo.queryResult("SELECT fcs.ftp_id " +
				"FROM "+ Data_source.TableName +" ds " +
				"JOIN "+ Agent_info.TableName +" ai ON ds.source_id = ai.source_id " +
				"JOIN "+ Ftp_collect.TableName +" fcs ON ai.agent_id = fcs.agent_id " +
				"WHERE ds.source_id = ? AND fcs.is_sendok = ? AND ds.create_user_id = ? ",
				sourceId, IsFlag.Shi.getCode(), getUserId());
		//数据可访问权限处理方式
		//以上SQL中，通过当前用户ID进行关联查询，达到了数据权限的限制
	}

	@Method(desc = "根据参数获得任务日志信息", logicStep = "" +
			"1、根据agent_id和user_id获取agent信息" +
			"2、在agent信息中获取日志目录" +
			"3、调用方法获取日志,目前工具类不存在" +
			"4、将日志信息和日志文件的路径封装成map" +
			"5、返回map")
	@Param(name = "agentId", desc = "agent信息表主键, ftp采集表, 半结构化文件采集表, 非结构化采集表, " +
			"源系统数据库设置表外键", range = "不为空")
	@Param(name = "userId", desc = "用户ID，用户表主键, agent下载信息表外键", range = "不为空")
	@Param(name = "logType", desc = "日志类型(完整日志、错误日志)", range = "All : 完整日志, Wrong : 错误日志")
	@Param(name = "readNum", desc = "查看日志条数", range = "不为空")
	@Return(desc = "存放文件内容和日志文件路径的map集合", range = "获取文件内容，key为log，" +
			"获取文件路径的,key为filePath")
	private Map<String, String> getTaskLog(long agentId, long userId, String logType, int readNum){
		//1、根据agent_id和user_id获取agent信息
		Agent_down_info AgentDownInfo = Dbo.queryOneObject(
				Agent_down_info.class, "select * from "+ Agent_down_info.TableName +
						" where agent_id = ? and user_id = ?", agentId, userId).orElseThrow(
								() -> new BusinessException("根据AgentID和userID未能找到Agent下载信息"));
		//2、在agent信息中获取日志目录
		String agentIP = AgentDownInfo.getAgent_ip();
		String agentPort = AgentDownInfo.getAgent_port();
		String logDir = AgentDownInfo.getLog_dir();
		String userName = AgentDownInfo.getUser_name();
		String passWord = AgentDownInfo.getPasswd();

		//用户选择查看错误日志
		if (logType.equals("Wrong")) {
			logDir = logDir.substring(0, logDir.lastIndexOf(File.separator) + 1) + "error.log";
		}

		//3、调用方法获取日志,目前工具类不存在
		String taskLog = LogReader.readAgentLog(logDir, agentIP, agentPort, userName,
				passWord, readNum);
		if (StringUtil.isBlank(taskLog)) {
			taskLog = "未获取到日志";
		}
		//4、将日志信息和日志文件的路径封装成map
		Map<String, String> map = new HashMap<>();
		map.put("log", taskLog);
		map.put("filePath", logDir);
		//5、返回map
		return map;
	}

}
