package hrds.c.biz.etlmonitor;

import com.jcraft.jsch.JSchException;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.CodecUtil;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.web.util.Dbo;
import hrds.c.biz.util.DownloadLogUtil;
import hrds.c.biz.util.ETLJobUtil;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.Dispatch_Frequency;
import hrds.commons.codes.Dispatch_Type;
import hrds.commons.codes.Job_Status;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.ReadLog;
import hrds.commons.utils.jsch.SFTPDetails;
import it.uniroma1.dis.wsngroup.gexf4j.core.*;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.Attribute;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeClass;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeList;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeType;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.GexfImpl;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.StaxGraphWriter;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.data.AttributeListImpl;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.viz.PositionImpl;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

@DocClass(desc = "作业调度监控类", author = "dhw", createdate = "2019/12/12 14:37")
public class MonitorAction extends BaseAction {

	private static final Logger logger = LogManager.getLogger();

	@Method(desc = "监控当前批量情况(系统运行状态）",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制"
					+ "2.当前工程批量运行状态汇总"
					+ "3.返回系统运行状态信息")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Return(desc = "返回系统运行状态信息与当前运行状态信息", range = "无限制")
	public Map<String, Object> monitorCurrentBatchInfo(String etl_sys_cd) {
		// 1.数据可访问权限处理方式，通过user_id进行权限控制
		// 2.当前工程批量运行状态汇总
		SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
		asmSql.clean();
		asmSql.clean();
		asmSql.addSql(
				"SELECT SUM(case when job_disp_status = ? then 1 else 0 end ) Pending,"
						+ " SUM(case when job_disp_status = ? then 1 else 0 end ) Waiting,"
						+ " SUM(case when job_disp_status = ? then 1 else 0 end ) Runing,"
						+ " SUM(case when job_disp_status = ? then 1 else 0 end ) Done,"
						+ " SUM(case when job_disp_status = ? then 1 else 0 end ) Suspension,"
						+ " SUM(case when job_disp_status = ? then 1 else 0 end ) Error,"
						+ " A.curr_bath_date,A.etl_sys_cd,CONCAT(A.etl_sys_cd,'(',B.etl_sys_name,')') sys_name"
						+ " FROM "
						+ Etl_job_cur.TableName
						+ " A,"
						+ Etl_sys.TableName
						+ " B WHERE  A.etl_sys_cd = B.etl_sys_cd");
		if (StringUtils.isNotBlank(etl_sys_cd)) {
			asmSql.addSql(" and  A.etl_sys_cd = ? ");
		}
		asmSql.addSql(" and A.curr_bath_date=B.curr_bath_date group by A.curr_bath_date,A.etl_sys_cd,"
				+ "B.etl_sys_name ORDER BY A.etl_sys_cd");
		addParamsToSql(etl_sys_cd, asmSql);
		// 3.返回系统运行状态信息
		return Dbo.queryOneObject(asmSql.sql(), asmSql.params());
	}

	@Method(desc = "监控当前批量情况(根据任务查询作业运行状态）",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
					"2.获取当前工程下的每个任务作业状态" +
					"3.返回根据任务查询作业运行状态信息")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Return(desc = "返回根据任务查询作业运行状态信息", range = "无限制")
	public List<Map<String, Object>> monitorCurrentBatchInfoByTask(String etl_sys_cd) {
		// 1.数据可访问权限处理方式，通过user_id进行权限控制
		// 2.获取当前工程下的每个任务作业状态
		SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
		asmSql.clean();
		asmSql.addSql("SELECT T1.sub_sys_cd,T1.etl_sys_cd,(CASE WHEN T1.sub_sys_cd = T2.sub_sys_cd THEN"
				+ " CONCAT(T2.sub_sys_desc,'(',T1.sub_sys_cd,')') ELSE T1.sub_sys_cd END) AS sub_sys_desc,"
				+ " SUM(CASE WHEN job_disp_status = ? THEN 1 ELSE 0 END ) Pending,"
				+ " SUM(CASE WHEN job_disp_status = ? THEN 1 ELSE 0 END ) Waiting,"
				+ " SUM(CASE WHEN job_disp_status = ? THEN 1 ELSE 0 END ) Runing,"
				+ " SUM(CASE WHEN job_disp_status = ? THEN 1 ELSE 0 END ) Done,"
				+ " SUM(CASE WHEN job_disp_status = ? THEN 1 ELSE 0 END ) Suspension,"
				+ " SUM(CASE WHEN job_disp_status = ? THEN 1 ELSE 0 END ) Error"
				+ " FROM " + Etl_job_cur.TableName + " T1 INNER JOIN " + Etl_sys.TableName
				+ " T3 ON T1.etl_sys_cd = T3.etl_sys_cd AND T1.curr_bath_date = T3.curr_bath_date"
				+ " LEFT JOIN " + Etl_sub_sys_list.TableName + " T2 ON T1.sub_sys_cd = T2.sub_sys_cd "
				+ " WHERE T1.etl_sys_cd = T2.etl_sys_cd");
		if (StringUtils.isNotBlank(etl_sys_cd)) {
			asmSql.addSql(" AND T1.etl_sys_cd = ? ");
		}
		asmSql.addSql(" GROUP BY T1.sub_sys_cd, T2.sub_sys_cd,T1.etl_sys_cd,sub_sys_desc");
		addParamsToSql(etl_sys_cd, asmSql);
		// 3.返回根据任务查询作业运行状态信息
		return Dbo.queryList(asmSql.sql(), asmSql.params());
	}

	private void addParamsToSql(String etl_sys_cd, SqlOperator.Assembler asmSql) {
		asmSql.addParam(Job_Status.PENDING.getCode());
		asmSql.addParam(Job_Status.WAITING.getCode());
		asmSql.addParam(Job_Status.RUNNING.getCode());
		asmSql.addParam(Job_Status.DONE.getCode());
		asmSql.addParam(Job_Status.STOP.getCode());
		asmSql.addParam(Job_Status.ERROR.getCode());
		if (StringUtils.isNotBlank(etl_sys_cd)) {
			asmSql.addParam(etl_sys_cd);
		}
	}

	@Method(
			desc = "监控当前系统运行任务下的作业信息",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
					"2.判断工程对应任务是否存在" +
					"3.查询当前系统运行任务下的作业信息")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Param(name = "sub_sys_cd", desc = "任务编号", range = "新增任务时生成")
	@Param(name = "curr_bath_date", desc = "当前批量日期", range = "yyyy-MM-dd格式的年月日")
	@Return(desc = "返回当前系统运行任务下的作业信息", range = "无限制")
	public List<Map<String, Object>> searchMonitorJobStateBySubCd(
			String etl_sys_cd, String sub_sys_cd, String curr_bath_date) {
		// 1.数据可访问权限处理方式，通过user_id进行权限控制
		// 2.判断工程对应任务是否存在
		if (!ETLJobUtil.isEtlSubSysExist(etl_sys_cd, sub_sys_cd)) {
			throw new BusinessException("当前工程对应任务已不存在！");
		}
		// 3.查询当前系统运行任务下的作业信息
		return Dbo.queryList("select etl_job,curr_st_time,curr_st_time,curr_end_time,job_disp_status,"
				+ "sub_sys_cd,curr_bath_date,etl_sys_cd FROM " + Etl_job_cur.TableName
				+ " WHERE sub_sys_cd=? AND etl_sys_cd=? AND curr_bath_date=? "
				+ " ORDER BY curr_bath_date", sub_sys_cd, etl_sys_cd, curr_bath_date);
	}

	@Method(
			desc = "监控历史批量信息",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制"
					+ "2.当批量日期为空时默认批量日期为当天，因为查看的是历史批量，所以默认展示当天批量"
					+ "3.查询历史批量信息")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Param(name = "curr_bath_date", desc = "批量日期", range = "yyyy-MM-dd格式的年月日", nullable = true)
	@Return(desc = "返回历史批量信息", range = "无限制")
	public List<Map<String, Object>> monitorHistoryBatchInfo(
			String etl_sys_cd, String curr_bath_date) {
		// 1.数据可访问权限处理方式，通过user_id进行权限控制
		// 2.当批量日期为空时默认批量日期为当天，因为查看的是历史批量，所以默认展示当天批量
		if (StringUtil.isBlank(curr_bath_date)) {
			curr_bath_date = DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString();
		}
		// 3.查询历史批量信息
		return Dbo.queryList("SELECT MAX(his.curr_end_time) AS curr_end_time,"
						+ "MIN(his.curr_st_time) AS curr_st_time,his.curr_bath_date,his.sub_sys_cd,"
						+ "CONCAT(his.sub_sys_cd,'(',list.sub_sys_desc,')') AS desc_sys FROM "
						+ Etl_job_disp_his.TableName
						+ " his left join "
						+ Etl_sub_sys_list.TableName
						+ " list ON his.sub_sys_cd=list.sub_sys_cd AND his.etl_sys_cd = list.etl_sys_cd "
						+ " WHERE his.etl_sys_cd = ? AND his.curr_bath_date=? group by his.sub_sys_cd,"
						+ " list.sub_sys_desc,his.curr_bath_date,his.sub_sys_cd order by curr_st_time",
				etl_sys_cd, curr_bath_date);
	}

	@Method(desc = "监控当前历史批量任务下的作业信息",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
					"2.判断工程下任务是否存在" +
					"3.查询历史批量作业信息")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Param(name = "sub_sys_cd", desc = "任务编号", range = "新增任务时生成")
	@Param(name = "curr_bath_date", desc = "批量日期", range = "yyyy-MM-dd格式的年月日")
	@Return(desc = "返回历史批量作业信息", range = "无限制")
	public List<Map<String, Object>> searchMonitorHisBatchJobBySubCd(
			String etl_sys_cd, String sub_sys_cd, String curr_bath_date) {
		// 1.数据可访问权限处理方式，通过user_id进行权限控制
		// 2.判断工程下任务是否存在
		if (!ETLJobUtil.isEtlSubSysExist(etl_sys_cd, sub_sys_cd)) {
			throw new BusinessException("当前工程对应的任务已不存在！");
		}
		// 3.查询历史批量信息
		return Dbo.queryList("SELECT t1.etl_sys_cd,t1.sub_sys_cd,t1.etl_job,"
						+ " MIN(curr_st_time) as curr_st_time,MAX(curr_end_time) as curr_end_time,curr_bath_date,"
						+ " job_disp_status,t1.etl_job_desc FROM ( SELECT A.* FROM (SELECT * FROM "
						+ Etl_job_disp_his.TableName
						+ " WHERE curr_bath_date=?) A INNER JOIN "
						+ "(SELECT curr_bath_date,etl_sys_cd,etl_job,sub_sys_cd,MAX(curr_st_time) AS curr_st_time"
						+ " FROM "
						+ Etl_job_disp_his.TableName
						+ " WHERE curr_bath_date=? GROUP BY curr_bath_date,"
						+ " etl_sys_cd,etl_job,sub_sys_cd) B ON A.curr_bath_date=B.curr_bath_date"
						+ " AND A.etl_sys_cd=B.etl_sys_cd AND A.etl_job=B.etl_job AND A.sub_sys_cd=B.sub_sys_cd "
						+ " AND A.curr_st_time=B.curr_st_time) t1 WHERE t1.etl_sys_cd=? and curr_bath_date=? "
						+ " AND t1.sub_sys_cd=? GROUP BY t1.etl_sys_cd,t1.sub_sys_cd,t1.etl_job,curr_bath_date,"
						+ " job_disp_status,t1.etl_job_desc order by curr_st_time asc",
				curr_bath_date, curr_bath_date, etl_sys_cd, curr_bath_date, sub_sys_cd);
	}

	@Method(desc = "监控当前作业信息",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制"
					+ "2.判断作业名称不为空时作业是否存在"
					+ "3.判断作业名称是否为空，不为空，查询当前工程下作业信息"
					+ "3.1查询当前工程下作业资源分配信息"
					+ "3.2返回该工程下作业以及作业资源分配信息"
					+ "4.作业名称为空，返回空")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Param(name = "etl_job", desc = "作业名称", range = "新增作业时生成", nullable = true)
	@Return(desc = "返回监控当前作业信息", range = "无限制")
	public Map<String, Object> monitorCurrJobInfo(String etl_sys_cd, String etl_job) {
		// 1.数据可访问权限处理方式，通过user_id进行权限控制
		// 2.判断作业名称不为空时作业是否存在
		if (StringUtil.isNotBlank(etl_job)) {
			if (!ETLJobUtil.isEtlJobDefExist(etl_sys_cd, etl_job)) {
				throw new BusinessException("当前工程对应作业名称已不存在！");
			}
		}
		// 3.判断作业名称是否为空，不为空，查询当前工程下作业信息
		if (StringUtil.isNotBlank(etl_job)) {
			Map<String, Object> etlJobCur = Dbo.queryOneObject("select * from " + Etl_job_cur.TableName
					+ " where etl_sys_cd=? and etl_job=? order by curr_bath_date desc", etl_sys_cd, etl_job);
			// 3.1查询当前工程下作业资源分配信息
			Map<String, Object> resourceRelation = Dbo.queryOneObject("select resource_type,resource_req from "
							+ Etl_job_resource_rela.TableName + " where etl_sys_cd=? and etl_job=?",
					etl_sys_cd, etl_job);
			etlJobCur.put("resourceRelation", resourceRelation);
			// 3.2返回该工程下作业以及作业资源分配信息
			return etlJobCur;
		}
		// 4.作业名称为空，返回空
		return null;
	}

	@Method(desc = "监控历史作业信息",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制"
					+ "2.判断工程是否存在"
					+ "3.判断作业名称不为空时作业是否存在"
					+ "4.判断作业名称是否为空查询作业信息,不为空返回历史作业信息，为空返回null"
					+ "5.判断isHistoryBatch是否为空处理不同情况"
					+ "6.如果作业为空，返回null")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Param(name = "etl_job", desc = "作业名称", range = "新增作业时生成", nullable = true)
	@Param(name = "start_date", desc = "开始批量日期", range = "yyyy-MM-dd格式年月日", nullable = true)
	@Param(name = "end_date", desc = "结束批量日期", range = "yyyy-MM-dd格式年月日", nullable = true)
	@Param(name = "isHistoryBatch", desc = "是否从历史批量跳转过来标志", range = "为空代表不是，不为空代表是", nullable = true)
	@Return(desc = "返回监控历史作业信息", range = "无限制")
	public List<Map<String, Object>> monitorHistoryJobInfo(String etl_sys_cd, String etl_job, String start_date,
	                                                       String end_date, String isHistoryBatch) {
		// 1.数据可访问权限处理方式，通过user_id进行权限控制
		// 2.判断作业名称不为空时作业是否存在
		if (StringUtil.isNotBlank(etl_job)) {
			if (!ETLJobUtil.isEtlJobDefExist(etl_sys_cd, etl_job)) {
				throw new BusinessException("当前工程对应作业名称已不存在！");
			}
		}
		// 3.判断作业名称是否为空查询作业信息,不为空返回历史作业信息，为空返回null
		if (StringUtil.isNotBlank(etl_job)) {
			SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
			asmSql.clean();
			asmSql.addSql("SELECT etl_job,curr_bath_date,curr_st_time,curr_end_time,(CASE WHEN job_disp_status=?"
					+ " THEN  '" + Job_Status.DONE.getValue() + "(" + Job_Status.DONE.getCode() + ")' else '' END)"
					+ " AS job_disp_status, (CASE WHEN t1.sub_sys_cd=t2.sub_sys_cd then "
					+ " CONCAT(t2.sub_sys_desc,'(',t1.sub_sys_cd,')') ELSE CONCAT(t1.sub_sys_cd) END)"
					+ " AS sub_sys_cd FROM(SELECT A.* FROM (SELECT * FROM "
					+ Etl_job_disp_his.TableName + " WHERE etl_sys_cd=? and etl_job=?");
			asmSql.addParam(Job_Status.DONE.getCode());
			asmSql.addParam(etl_sys_cd);
			asmSql.addParam(etl_job);
			// 4.判断isHistoryBatch是否为空处理不同情况
			isHistoryBatch(start_date, end_date, isHistoryBatch);
			asmSql.addSql(") A INNER JOIN (SELECT curr_bath_date,etl_sys_cd,etl_job,sub_sys_cd,"
					+ " MAX(curr_st_time) AS curr_st_time FROM " + Etl_job_disp_his.TableName +
					" WHERE etl_sys_cd=? and etl_job=?");
			asmSql.addParam(etl_sys_cd);
			asmSql.addParam(etl_job);
			isHistoryBatch(start_date, end_date, isHistoryBatch);
			asmSql.addSql(
					" GROUP BY curr_bath_date,etl_sys_cd,etl_job,sub_sys_cd) B "
							+ " ON A.curr_bath_date=B.curr_bath_date AND A.etl_sys_cd=B.etl_sys_cd " +
							" AND A.etl_job=B.etl_job AND A.sub_sys_cd=B.sub_sys_cd " +
							" AND A.curr_st_time=B.curr_st_time" + ") t1 LEFT JOIN " + Etl_sub_sys_list.TableName
							+ " t2 ON t1.sub_sys_cd=t2.sub_sys_cd  and t1.etl_sys_cd=t2.etl_sys_cd ORDER BY" +
							" curr_bath_date,curr_st_time,curr_end_time");
			return Dbo.queryList(asmSql.sql(), asmSql.params());
		}
		// 5.如果作业为空，返回null
		return null;
	}

	@Method(desc = "根据是否从历史批量的请求做不同情况处理",
			logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制"
					+ "2.判断是否从历史批量的请求标志是否为空"
					+ "2.1不为空则表示该请求数据的连接是历史批量过来的"
					+ "2.2为空则表示该请求数据的连接是历史作业查询"
					+ "2.2.1如果开始日期不为空，加条件查询"
					+ "2.2.2如果结束日期不为空，加条件查询")
	@Param(name = "start_date", desc = "开始批量日期", range = "yyyy-MM-dd格式年月日", nullable = true)
	@Param(name = "end_date", desc = "结束批量日期", range = "yyyy-MM-dd格式年月日", nullable = true)
	@Param(name = "isHistoryBatch", desc = "是否从历史批量的请求标志", range = "为空代表不是，不为空代表是", nullable = true)
	private void isHistoryBatch(String start_date, String end_date, String isHistoryBatch) {
		// 1.数据可访问权限处理方式，该方法不需要权限控制
		// 2.判断是否从历史批量的请求标志是否为空
		SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
		if (StringUtil.isNotBlank(isHistoryBatch)) {
			// 2.1不为空则表示该请求数据的连接是历史批量过来的
			asmSql.addSql(" AND curr_bath_date=?");
			asmSql.addParam(start_date);
		} else {
			// 2.2为空则表示该请求数据的连接是历史作业查询
			// 2.2.1如果开始日期不为空，加条件查询
			if (StringUtil.isNotBlank(start_date)) {
				asmSql.addSql(" and REPLACE(curr_bath_date,'-','') >= ?");
				asmSql.addParam(start_date.replace("-", ""));
			}
			// 2.2.2如果结束日期不为空，加条件查询
			if (StringUtil.isNotBlank(end_date)) {
				asmSql.addSql(" and REPLACE(curr_bath_date,'-','') <= ?");
				asmSql.addParam(end_date.replace("-", ""));
			}
		}
	}

	@Method(desc = "监控作业依赖信息（单作业搜索）",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制"
					+ "2.判断当前工程下作业是否存在"
					+ "3.封装作业依赖属性信息"
					+ "4.获取上游作业信息"
					+ "5.获取下游作业信息"
					+ "6.将上游作业信息封装入下游作业中"
					+ "7.按需要展示的格式封装数据并返回")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Param(name = "etl_job", desc = "作业名称", range = "新增作业时生成")
	@Return(desc = "返回该工程下作业以及作业资源分配信息", range = "无限制")
	public Map<String, Object> monitorJobDependencyInfo(String etl_sys_cd, String etl_job) {
		// 1.数据可访问权限处理方式，通过user_id进行权限控制
		// 2.判断当前工程下作业是否存在
		if (!ETLJobUtil.isEtlJobDefExist(etl_sys_cd, etl_job)) {
			throw new BusinessException("当前工程下的作业已不存在！");
		}
		// 3.封装作业依赖属性信息
		Etl_dependency etl_dependency = new Etl_dependency();
		etl_dependency.setEtl_sys_cd(etl_sys_cd);
		etl_dependency.setEtl_job(etl_job);
		etl_dependency.setPre_etl_sys_cd(etl_sys_cd);
		etl_dependency.setPre_etl_job(etl_job);
		// 4.获取上游作业信息
		List<Map<String, Object>> topJobInfoList =
				topEtlJobDependencyInfo(etl_dependency.getEtl_job(), etl_dependency.getEtl_sys_cd())
						.toList();
		// 5.获取下游作业信息
		List<Map<String, Object>> downJobInfoList =
				downEtlJobDependencyInfo(etl_dependency.getPre_etl_sys_cd(), etl_dependency.getEtl_job())
						.toList();
		// 6.将上游作业信息封装入下游作业中
		if (!topJobInfoList.isEmpty()) {
			downJobInfoList.addAll(topJobInfoList);
		}
		// 7.按需要展示的格式封装数据并返回
		Map<String, Object> dataInfo = new HashMap<>();
		dataInfo.put("id", "0");
		dataInfo.put("name", etl_job);
		dataInfo.put("aid", "999");
		dataInfo.put("children", downJobInfoList);
		return dataInfo;
	}

	@Method(desc = "获取下游作业依赖信息",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制"
					+ "2.查询依赖于当前作业的信息(下游)"
					+ "3.如果下游作业依赖关系不为空，设置字节点"
					+ "4.返回下游作业依赖关系")
	@Param(name = "pre_etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Param(name = "etl_job", desc = "作业名称", range = "新增作业时生成")
	@Return(desc = "返回下游作业依赖关系", range = "无限制")
	private Result downEtlJobDependencyInfo(String pre_etl_sys_cd, String etl_job) {
		// 1.数据可访问权限处理方式，通过user_id进行权限控制
		// 2.查询依赖于当前作业的信息(下游)
		Result downJob = Dbo.queryResult("select ejd.etl_sys_cd,ejd.etl_job,ed.pre_etl_job from "
						+ Etl_dependency.TableName + " ed left join " + Etl_job_def.TableName
						+ " ejd on ed.etl_sys_cd=ejd.etl_sys_cd and ed.etl_job=ejd.etl_job "
						+ " WHERE ed.pre_etl_sys_cd=? and ed.pre_etl_job=? "
						+ " order by ejd.job_priority DESC,ed.etl_sys_cd,ed.etl_job",
				pre_etl_sys_cd, etl_job);
		// 3.如果下游作业依赖关系不为空，设置字节点
		if (!downJob.isEmpty()) {
			for (int i = 0; i < downJob.getRowCount(); i++) {
				downJob.setObject(i, "id", downJob.getString(i, "etl_job"));
				downJob.setObject(i, "name", downJob.getString(i, "etl_job"));
				downJob.setObject(i, "direction", "right");
				// 目前只做一层
			}
		}
		// 4.返回下游作业依赖关系
		return downJob;
	}

	@Method(desc = "获取上游作业依赖信息",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制"
					+ "2.获取上游作业依赖关系"
					+ "3.如果上游作业依赖关系不为空，设置字节点"
					+ "4.返回上游作业依赖关系")
	@Param(name = "etl_job", desc = "作业名称", range = "新增作业时生成")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Return(desc = "返回上游作业依赖关系", range = "无限制")
	private Result topEtlJobDependencyInfo(String etl_job, String etl_sys_cd) {
		// 1.数据可访问权限处理方式，通过user_id进行权限控制
		// 2.获取上游作业依赖关系
		Result topJob = Dbo.queryResult("select ejd.etl_sys_cd,ed.pre_etl_job,ejd.etl_job FROM "
				+ Etl_dependency.TableName + " ed join " + Etl_job_def.TableName
				+ " ejd on ed.etl_job=ejd.etl_job and ed.etl_sys_cd=ejd.etl_sys_cd "
				+ " where ed.etl_job=? and ed.etl_sys_cd=?", etl_job, etl_sys_cd);
		// 3.如果上游作业依赖关系不为空，设置子节点
		if (!topJob.isEmpty()) {
			for (int i = 0; i < topJob.getRowCount(); i++) {
				topJob.setObject(i, "id", topJob.getString(i, "pre_etl_job"));
				topJob.setObject(i, "name", topJob.getString(i, "pre_etl_job"));
				topJob.setObject(i, "direction", "left");
				// 目前只做一层
			}
		}
		// 4.返回上游作业依赖关系
		return topJob;
	}

	@Method(desc = "监控依赖作业(全作业搜索)",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制"
					+ "2.设置gexf头信息"
					+ "3.创建存放node节点信息的集合"
					+ "4.创建存放上下游作业信息的集合"
					+ "5.获取所有调度方式为定时或者频率的作业"
					+ "6.遍历定时与频率作业"
					+ "7.判断调度频率是频率还是定时"
					+ "7.1调度频率是频率"
					+ "7.1.1设置Node节点属性值"
					+ "7.1.2将此节点加入nodeMap"
					+ "7.2调度频率是定时"
					+ "7.2.1设置Node节点属性值"
					+ "7.2.2将此节点加入nodeMap"
					+ "8.获取调度方式为依赖的作业"
					+ "9.遍历调度方式为依赖的作业，并且设置Node作业节点"
					+ "10.获取当前作业的上游作业"
					+ "11.获取当前作业的下游作业"
					+ "12.创建Node"
					+ "13.判断上下游作业是否为空处理不同情况"
					+ "13.1调度方式为依赖但无依赖关系作业"
					+ "13.2当前作业的上游作业为空,下游作业不为空"
					+ "13.3当前作业的上游作业不为空,下游作业为空"
					+ "13.4当前作业的上下游作业都不为空"
					+ "14.获取调度频率不为频率的作业"
					+ "15.遍历调度频率不为频率的作业并建立作业之间的依赖关系"
					+ "16.建立依赖关系"
					+ "17.写gexf格式的文件"
					+ "18.返回gexf格式的数据")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Return(desc = "返回gexf格式的xml的依赖作业数据", range = "无限制")
	public String monitorBatchEtlJobDependencyInfo(String etl_sys_cd) {
		try {
			// 1.数据可访问权限处理方式，通过user_id进行权限控制
			// 2.设置gexf头信息
			String head_1 = "<gexf xmlns=\"http://www.gexf.net/1.2draft\" version=\"1.2\"";
			String head =
					"<gexf xmlns=\"http://www.gexf.net/1.2draft\" version=\"1.2\" "
							+ "xmlns:viz=\"http://www.gexf.net/1.2draft/viz\" "
							+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
							+ "xsi:schemaLocation=\"http://www.gexf.net/1.2draft http://www.gexf.net/1.2draft/gexf.xsd\"";
			Gexf gexf = new GexfImpl();
			Calendar date = Calendar.getInstance();
			date.set(
					date.get(Calendar.YEAR), date.get(Calendar.MONTH) + 1, date.get(Calendar.DATE));
			gexf.getMetadata()
					.setLastModified(date.getTime())
					.setCreator("Gephi.org")
					.setDescription("A Web network");
			Graph graph = gexf.getGraph();
			graph.setDefaultEdgeType(EdgeType.UNDIRECTED).setMode(Mode.STATIC);
			AttributeList attrList = new AttributeListImpl(AttributeClass.NODE);
			graph.getAttributeLists().add(attrList);
			Attribute attUrl =
					attrList.createAttribute("modularity_class", AttributeType.INTEGER, "Modularity Class");
			// 3.创建存放node节点信息的集合
			Map<String, Node> nodeMap = new HashMap<>();
			// 4.创建存放上下游作业信息的集合
			List<String> list_Edge = new ArrayList<>();
			// 5.获取所有调度方式为定时或者频率的作业
			Result scheduledOrFrequency = Dbo.queryResult(
					"SELECT * FROM " + Etl_job_def.TableName + " WHERE etl_sys_cd=? AND disp_type!=?",
					etl_sys_cd, Dispatch_Type.DEPENDENCE.getCode());
			Random random = new Random();
			// 6.遍历定时与频率作业
			int number_key = 0;
			for (int i = 0; i < scheduledOrFrequency.getRowCount(); i++) {
				String etl_job = scheduledOrFrequency.getString(i, "etl_job");
				// 7.判断调度频率是频率还是定时
				Node gephi = graph.createNode(number_key + "");
				if (Dispatch_Frequency.PinLv
						== (Dispatch_Frequency.ofEnumByCode(scheduledOrFrequency.getString(i, "disp_freq")))) {
					number_key = number_key + 1;
					// 7.1调度频率是频率
					String label =
							"作业："
									+ etl_job
									+ "\n\r调度频率：频率\n\r"
									+ "隔多长时间："
									+ scheduledOrFrequency.getString(i, "exe_frequency")
									+ "\n\r执行次数："
									+ scheduledOrFrequency.getString(i, "exe_num")
									+ "\n\r开始执行时间："
									+ scheduledOrFrequency.getString(i, "star_time")
									+ "\n\r结束执行时间："
									+ scheduledOrFrequency.getString(i, "end_time");
					// 7.1.1设置Node节点属性值
					gephi.setLabel(label).getAttributeValues().addValue(attUrl, "0");
					gephi.setSize(10);
					gephi.setPosition(new PositionImpl(random.nextFloat(), random.nextFloat(), 0.0f));
					// 7.1.2将此节点加入nodeMap
					nodeMap.put(etl_job, gephi);
				} else {
					// 7.2调度频率是定时
					number_key = number_key + 1;
					String label =
							"作业："
									+ etl_job
									+ "\n\r触发方式：定时触发\n\r"
									+ "触发时间："
									+ scheduledOrFrequency.getString(i, "disp_time")
									+ "\n\r任务："
									+ scheduledOrFrequency.getString(i, "sub_sys_cd");
					// 7.2.1设置Node节点属性值
					gephi.setLabel(label).getAttributeValues().addValue(attUrl, "1");
					gephi.setSize(10);
					gephi.setPosition(new PositionImpl(random.nextFloat(), random.nextFloat(), 0.0f));
					// 7.2.2将此节点加入nodeMap
					nodeMap.put(etl_job, gephi);
				}
			}
			// 8.获取调度方式为依赖的作业
			Result dependencyJobResult =
					Dbo.queryResult(
							"SELECT * FROM " + Etl_job_def.TableName + " WHERE disp_type=? AND etl_sys_cd=?",
							Dispatch_Type.DEPENDENCE.getCode(),
							etl_sys_cd);
			// 9.遍历调度方式为依赖的作业，并且设置Node作业节点
			for (int i = 0; i < dependencyJobResult.getRowCount(); i++) {
				String etl_job = dependencyJobResult.getString(i, "etl_job");
				// 10.获取当前作业的上游作业
				Result topResult = topEtlJobDependencyInfo(etl_job, etl_sys_cd);
				// 11.获取当前作业的下游作业
				Result downResult = downEtlJobDependencyInfo(etl_sys_cd, etl_job);
				// 12.创建Node
				Node gephi = graph.createNode(number_key + "");
				// 13.判断上下游作业是否为空处理不同情况
				if (topResult.isEmpty() && downResult.isEmpty()) {
					// 13.1调度方式为依赖但无依赖关系作业
					number_key = number_key + 1;
					String label =
							"作业："
									+ etl_job
									+ "\n\r触发方式：依赖触发，无依赖关系作业\n\r"
									+ "任务："
									+ dependencyJobResult.getString(i, "sub_sys_cd");
					gephi.setLabel(label).getAttributeValues().addValue(attUrl, "1");
					gephi.setSize(10);
					gephi.setPosition(new PositionImpl(random.nextFloat(), random.nextFloat(), 0.0f));
					nodeMap.put(etl_job, gephi);
				} else if (topResult.isEmpty() && !downResult.isEmpty()) {
					// 13.2当前作业的上游作业为空,下游作业不为空
					number_key = number_key + 1;
					String label =
							"作业："
									+ etl_job
									+ "\n\r触发方式：依赖触发，无上游作业\n\r"
									+ "任务："
									+ dependencyJobResult.getString(i, "sub_sys_cd");
					gephi.setLabel(label).getAttributeValues().addValue(attUrl, "2");
					gephi.setSize(10);
					gephi.setPosition(new PositionImpl(random.nextFloat(), random.nextFloat(), 0.0f));
					nodeMap.put(etl_job, gephi);
				} else if (!topResult.isEmpty() && downResult.isEmpty()) {
					// 13.3当前作业的上游作业不为空,下游作业为空
					number_key = number_key + 1;
					String label =
							"作业："
									+ etl_job
									+ "\n\r触发方式：依赖触发,无下游作业\n\r"
									+ "任务："
									+ dependencyJobResult.getString(i, "sub_sys_cd");
					gephi.setLabel(label).getAttributeValues().addValue(attUrl, "3");
					gephi.setSize(10);
					gephi.setPosition(new PositionImpl(random.nextFloat(), random.nextFloat(), 0.0f));
					nodeMap.put(etl_job, gephi);
				} else {
					// 13.4当前作业的上下游作业都不为空
					number_key = number_key + 1;
					String label =
							"作业："
									+ etl_job
									+ "\n\r触发方式：依赖触发，上下游作业都有\n\r"
									+ "任务："
									+ dependencyJobResult.getString(i, "sub_sys_cd");
					gephi.setLabel(label).getAttributeValues().addValue(attUrl, "4");
					gephi.setSize(10);
					gephi.setPosition(new PositionImpl(random.nextFloat(), random.nextFloat(), 0.0f));
					nodeMap.put(etl_job, gephi);
				}
			}
			// 14.获取调度频率不为频率的作业
			Result dispatchFreqResult =
					Dbo.queryResult(
							"SELECT * FROM " + Etl_job_def.TableName + " WHERE disp_freq!=? AND etl_sys_cd=?",
							Dispatch_Frequency.PinLv.getCode(),
							etl_sys_cd);
			// 15.遍历调度频率不为频率的作业并建立作业之间的依赖关系
			for (int i = 0; i < dispatchFreqResult.getRowCount(); i++) {
				String etl_job = dependencyJobResult.getString(i, "etl_job");
				Result topResult = topEtlJobDependencyInfo(etl_job, etl_sys_cd);
				Result downResult = downEtlJobDependencyInfo(etl_job, etl_sys_cd);
				// 16.建立依赖关系
				buildDependencies(nodeMap, list_Edge, etl_job, topResult, downResult);
			}
			// 17.写gexf格式的文件
			StaxGraphWriter graphWriter = new StaxGraphWriter();
			StringWriter stringWriter = new StringWriter();
			graphWriter.writeToStream(gexf, stringWriter, CodecUtil.UTF8_STRING);
			String outputXml = stringWriter.toString();
			outputXml = outputXml.replaceAll(head_1, head).replace("\n", "");
			// 18.返回gexf格式的数据
			return outputXml;
		} catch (IOException e) {
			throw new BusinessException("查看全作业依赖时写gexf格式的文件失败！");
		}
	}

	@Method(desc = "根据上下游作业结果集处理不同情况建立依赖关系",
			logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制"
					+ "2.判断上游作业结果集是否为空，不为空建立依赖关系"
					+ "2.1获取上游作业"
					+ "2.2建立依赖关系"
					+ "3.判断下游作业结果集是否为空，不为空建立依赖关系"
					+ "3.1获取上游作业"
					+ "3.2建立依赖关系")
	@Param(name = "nodeMap", desc = "存放node节点信息的集合", range = "取值范围")
	@Param(name = "list_Edge", desc = "依赖关系集合", range = "无限制")
	@Param(name = "etl_job", desc = "作业名称", range = "新增作业时生成")
	@Param(name = "topResult", desc = "依赖上游作业关系结果集", range = "无限制")
	@Param(name = "downResult", desc = "依赖下游作业关系结果集", range = "无限制")
	private void buildDependencies(Map<String, Node> nodeMap, List<String> list_Edge, String etl_job,
	                               Result topResult, Result downResult) {
		// 1.数据可访问权限处理方式，该方法不需要权限控制
		// 2.判断上游作业结果集是否为空，不为空建立依赖关系
		if (!topResult.isEmpty()) {
			for (int i = 0; i < topResult.getRowCount(); i++) {
				// 2.1获取上游作业
				String pre_etl_job = topResult.getString(i, "pre_etl_job");
				// 2.2建立依赖关系
				topNodeConnectToNode(nodeMap, list_Edge, etl_job, pre_etl_job);
			}
		}
		// 3.判断下游作业结果集是否为空，不为空建立依赖关系
		if (!downResult.isEmpty()) {
			for (int i = 0; i < downResult.getRowCount(); i++) {
				// 3.1获取上游作业
				String pre_etl_job = downResult.getString(i, "etl_job");
				// 3.2建立依赖关系
				topNodeConnectToNode(nodeMap, list_Edge, etl_job, pre_etl_job);
			}
		}
	}

	@Method(desc = "建立依赖关系",
			logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制"
					+ "2.判断当前作业是否与上游作业相同，相同跳过不相同建立依赖关系"
					+ "3.获取当前作业节点信息"
					+ "4.获取上游作业节点信息"
					+ "5.判断依赖关系是否已存在，如果存在则跳过，不存在建立依赖关系")
	@Param(name = "nodeMap", desc = "存放node节点信息的集合", range = "取值范围")
	@Param(name = "list_Edge", desc = "依赖关系集合", range = "无限制")
	@Param(name = "etl_job", desc = "作业名称", range = "新增作业时生成")
	@Param(name = "pre_etl_job", desc = "上游作业名称", range = "新增作业时生成")
	private void topNodeConnectToNode(Map<String, Node> nodeMap, List<String> list_Edge, String etl_job,
	                                  String pre_etl_job) {
		// 1.数据可访问权限处理方式，该方法不需要权限控制
		// 2.判断当前作业是否与上游作业相同，相同跳过不相同建立依赖关系
		if (!etl_job.equals(pre_etl_job)) {
			// 3.获取当前作业节点信息
			Node node = nodeMap.get(etl_job);
			// 4.获取上游作业节点信息
			Node topNode = nodeMap.get(pre_etl_job);
			// 5.判断依赖关系是否已存在，如果存在则跳过，不存在建立依赖关系
			if (!list_Edge.contains(etl_job + "-" + pre_etl_job)) {
				topNode.connectTo(node);
				list_Edge.add(etl_job + "-" + pre_etl_job);
			}
		}
	}

	@Method(desc = "监控系统资源状况",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制"
					+ "2.获取资源信息"
					+ "3.获取当前工程下运行的作业"
					+ "4.创建存放系统资源信息以及当前工程下运行的作业信息的集合并封装数据"
					+ "5.返回系统资源信息以及当前工程下运行的作业信息")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Return(desc = "返回系统资源信息以及当前工程下运行的作业信息", range = "取值范围")
	public Map<String, Object> monitorSystemResourceInfo(String etl_sys_cd) {
		// 1.数据可访问权限处理方式，通过user_id进行权限控制
		// 2.获取资源信息
		List<Map<String, Object>> etlResourceList = Dbo.queryList(
				"SELECT resource_type,resource_max,"
						+ "resource_used,etl_sys_cd,(resource_max-resource_used) as free FROM "
						+ Etl_resource.TableName
						+ " where etl_sys_cd=?",
				etl_sys_cd);
		// 3.获取当前工程下运行的作业
		List<Map<String, Object>> jobRunList = Dbo.queryList(
				"SELECT T1.resource_type,"
						+ " concat(T2.sub_sys_cd,'(',t3.sub_sys_desc,')') sub_sys_cd,"
						+ " concat(T2.etl_job,'(',T2.etl_job_desc,')') etl_job,T2.job_disp_status,"
						+ " T2.curr_st_time AS curr_st_time,T1.resource_req,T1.etl_sys_cd FROM "
						+ Etl_job_cur.TableName
						+ " t2 INNER JOIN "
						+ Etl_job_resource_rela.TableName
						+ " T1 ON T1.etl_sys_cd=T2.etl_sys_cd AND T2.etl_job=T1.etl_job INNER JOIN "
						+ Etl_sub_sys_list.TableName
						+ " t3 ON t2.sub_sys_cd=t3.sub_sys_cd "
						+ " WHERE T2.job_disp_status=? AND T1.etl_sys_cd=?",
				Job_Status.RUNNING.getCode(),
				etl_sys_cd);
		// 4.创建存放系统资源信息以及当前工程下运行的作业信息的集合并封装数据
		Map<String, Object> monitorSysResource = new HashMap<>();
		monitorSysResource.put("etlResourceList", etlResourceList);
		monitorSysResource.put("jobRunList", jobRunList);
		// 5.返回系统资源信息以及当前工程下运行的作业信息
		return monitorSysResource;
	}

	@Method(desc = "查看作业日志信息",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制"
					+ "2.判断工程对应作业是否存在"
					+ "3.查询作业调度工程信息"
					+ "4.查询作业信息"
					+ "5.获取日志目录"
					+ "6.判断查询行数是否超过1000行，最大显示1000行"
					+ "7.读取日志文件返回")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Param(name = "etl_job", desc = "作业名称", range = "新增作业时生成")
	@Param(name = "readNum", desc = "查看日志行数", range = "最多显示1000行,大于0的正整数", valueIfNull = "100")
	@Return(desc = "返回日志文件信息", range = "无限制")
	public String readHistoryJobLogInfo(String etl_sys_cd, String etl_job, Integer readNum) {
		// 1.数据可访问权限处理方式，通过user_id进行权限控制
		// 2.判断工程对应作业是否存在
		if (!ETLJobUtil.isEtlJobDefExist(etl_sys_cd, etl_job)) {
			throw new BusinessException("当前工程对应作业已不存在！");
		}
		// 3.查询作业调度工程信息
		Etl_sys etl_sys = Dbo.queryOneObject(Etl_sys.class,
				"select * from " + Etl_sys.TableName + " where user_id=? and etl_sys_cd=?",
				getUserId(), etl_sys_cd).orElseThrow(() -> new BusinessException("sql查询错误！"));
		String curr_bath_date = etl_sys.getCurr_bath_date();
		if (curr_bath_date.contains("-") && curr_bath_date.length() == 10) {
			curr_bath_date = StringUtil.replace(curr_bath_date, "-", "");
		}
		// 4.查询作业信息
		List<Object> logDicList = Dbo.queryOneColumnList(
				"select log_dic from " + Etl_job_disp_his.TableName + " where etl_sys_cd=? AND etl_job=? " +
						" and curr_bath_date=? order by curr_end_time desc", etl_sys_cd, etl_job, curr_bath_date);
		// 5.获取日志目录
		if (logDicList.isEmpty()) {
			return null;
		}
		String log_dic = logDicList.get(0).toString();
		if (!log_dic.endsWith("/")) {
			log_dic = log_dic + File.separator;
		}
		String logDir = log_dic + etl_job + "_" + curr_bath_date + ".log";
		// 6.判断查询行数是否超过1000行，最大显示1000行
		if (readNum > 1000) {
			readNum = 1000;
		}

		SFTPDetails sftpDetails = new SFTPDetails();
		sftpDetails.setHost(etl_sys.getEtl_serv_ip());
		sftpDetails.setPort(Integer.parseInt(etl_sys.getEtl_serv_port()));
		sftpDetails.setUser_name(etl_sys.getUser_name());
		sftpDetails.setPwd(etl_sys.getUser_pwd());
		// 7.读取日志文件返回
		return ReadLog.readAgentLog(logDir, sftpDetails, readNum);
	}

	@Method(desc = "下载日志文件到本地",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制"
					+ "2.判断日期格式并修改成8位"
					+ "3.获取作业信息"
					+ "4.获取日志目录"
					+ "5.打包指令"
					+ "6.根据工程编号获取工程信息"
					+ "7.检查工程部署参数是否为空"
					+ "8.与ETLAgent服务交互"
					+ "9.获取远程文件名"
					+ "10.本地下载路径"
					+ "11.判断路径是否以分隔符结尾，如果不是加分隔符拼接文件名"
					+ "12.从服务器下载文件"
					+ "13.下载完删除压缩包"
					+ "14.返回文件名")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Param(name = "etl_job", desc = "作业名称", range = "新增作业时生成")
	@Param(name = "curr_bath_date", desc = "批量日期", range = "yyyyMMdd格式的年月日，如：20191219")
	public String downHistoryJobLog(String etl_sys_cd, String etl_job, String curr_bath_date) {
		try {
			// 1.数据可访问权限处理方式，通过user_id进行权限控制
			// 2.判断日期格式并修改成8位
			if (curr_bath_date.contains("-") && curr_bath_date.length() == 10) {
				curr_bath_date = StringUtil.replace(curr_bath_date, "-", "");
			}
			// 3.获取作业信息
			List<Object> logDicList = Dbo.queryOneColumnList("select log_dic FROM "
					+ Etl_job_disp_his.TableName + " where etl_sys_cd=? AND etl_job=? and " +
					" curr_bath_date=? order by curr_end_time desc", etl_sys_cd, etl_job, curr_bath_date);
			if (logDicList.isEmpty()) {
				return null;
			}
			// 4.获取日志目录
			String logDir = logDicList.get(0).toString();
			if (!logDir.endsWith("/")) {
				logDir = logDir + File.separator;
			}
			// 5.打包指令
			String compressCommand = "tar -zvcPf " + logDir + curr_bath_date + "_" + etl_job + ".tar.gz"
					+ " " + logDir + etl_job + "_" + curr_bath_date + "*.log ";
			// 6.根据工程编号获取工程信息
			Etl_sys etlSysInfo = ETLJobUtil.getEtlSysById(etl_sys_cd, getUserId());
			// 7.检查工程部署参数是否为空
			ETLJobUtil.isETLDeploy(etlSysInfo);
			SFTPDetails sftpDetails = new SFTPDetails();
			// 8.与ETLAgent服务交互
			ETLJobUtil.interactingWithTheAgentServer(compressCommand, etlSysInfo, sftpDetails);
			// 9.获取远程文件名
			String remoteFileName = curr_bath_date + "_" + etl_job + ".tar.gz";
			// 10.本地下载路径
			String localPath = ETLJobUtil.getFilePath(null);
			logger.info("==========历史日志文件下载本地路径=========" + localPath);
			// 11.判断路径是否以分隔符结尾，如果不是加分隔符拼接文件名
			if (logDir.endsWith(File.separator)) {
				logDir = logDir + File.separator + remoteFileName;
			} else {
				logDir = logDir + remoteFileName;
			}
			// 12.从服务器下载文件
			DownloadLogUtil.downloadLogFile(logDir, localPath, sftpDetails);
			// 13.下载完删除压缩包
			DownloadLogUtil.deleteLogFileBySFTP(logDir, sftpDetails);
			// 14.返回文件名
			return remoteFileName;
		} catch (JSchException e) {
			throw new BusinessException("与远端服务器进行交互，建立连接失败！");
		} catch (IOException e) {
			throw new BusinessException("下载日志文件失败！");
		}
	}

	@Method(desc = "监控所有项目图表数据",
			logicStep = "数据可访问权限处理方式，通过user_id进行权限控制"
					+ "2.获取当前用户下所有工程调度监控运行状态")
	@Return(desc = "返回当前用户下所有工程调度监控运行状态", range = "无限制")
	public Result monitorAllProjectChartsData() {
		// 1.数据可访问权限处理方式，通过user_id进行权限控制
		// 2.获取当前用户下所有工程调度监控运行状态
		return Dbo.queryResult("SELECT "
						+ " SUM(case when job_disp_status = ? then 1 else 0 end ) Pending,"
						+ " SUM(case when job_disp_status = ? then 1 else 0 end ) Waiting,"
						+ " SUM(case when job_disp_status = ? then 1 else 0 end ) Runing,"
						+ " SUM(case when job_disp_status = ? then 1 else 0 end ) Done,"
						+ " SUM(case when job_disp_status = ? then 1 else 0 end ) Suspension,"
						+ " SUM(case when job_disp_status = ? then 1 else 0 end ) Error,"
						+ " A.curr_bath_date AS bathDate,A.etl_sys_cd,B.etl_sys_name,"
						+ " CONCAT(A.etl_sys_cd,'(',B.etl_sys_name,')') sys_name FROM "
						+ Etl_job_cur.TableName
						+ " A,"
						+ Etl_sys.TableName
						+ " B where A.etl_sys_cd=B.etl_sys_cd AND B.user_id=? "
						+ " and A.curr_bath_date=B.curr_bath_date group by bathDate,A.etl_sys_cd,B.etl_sys_name"
						+ " ORDER BY A.etl_sys_cd",
				Job_Status.PENDING.getCode(),
				Job_Status.WAITING.getCode(),
				Job_Status.RUNNING.getCode(),
				Job_Status.DONE.getCode(),
				Job_Status.STOP.getCode(),
				Job_Status.ERROR.getCode(),
				getUserId());
	}
}
