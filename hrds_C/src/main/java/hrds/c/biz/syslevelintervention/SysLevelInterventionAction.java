package hrds.c.biz.syslevelintervention;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.db.jdbc.DefaultPageImpl;
import fd.ng.db.jdbc.Page;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.util.Dbo;
import hrds.c.biz.util.ETLJobUtil;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.Job_Status;
import hrds.commons.codes.Main_Server_Sync;
import hrds.commons.codes.Meddle_status;
import hrds.commons.codes.Meddle_type;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "作业调度系统干预处理类", author = "dhw", createdate = "2019/12/9 10:18")
public class SysLevelInterventionAction extends BaseAction {

	@Method(desc = "查询系统级干预系统批量情况",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
					"2.判断工程是否存在" +
					"3.获取批量日期" +
					"4.获取系统批量情况" +
					"5.创建存放系统批量情况相关信息的集合" +
					"6.返回存放系统批量情况相关信息的集合")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Return(desc = "返回存放系统批量情况相关信息的集合", range = "无限制")
	public Map<String, Object> searchSystemBatchConditions(String etl_sys_cd) {
		// 1.数据可访问权限处理方式，通过user_id进行权限控制
		// 2.获取批量日期
		List<String> currBathDateList = Dbo.queryOneColumnList("select curr_bath_date from " +
				Etl_sys.TableName + " where etl_sys_cd=?", etl_sys_cd);
		String curr_bath_date = currBathDateList.get(0);
		// 3.获取系统批量情况
		List<Map<String, Object>> etlJobCurrList = Dbo.queryList("select sum(case when job_disp_status=? " +
						" then 1 else 0 end) as done_num ," +
						"sum(case when job_disp_status=? then 1 else 0 end) as pending_num,sum(case when " +
						" job_disp_status='A' then 1 else 0 end) as alarm_num,sum(case when " +
						" job_disp_status=? then 1 else 0 end) as error_num,sum(case when " +
						" job_disp_status=? then 1 else 0 end) as waiting_num,sum(case when " +
						" job_disp_status=? then 1 else 0 end) as running_num,sum(case when " +
						" job_disp_status=? then 1 else 0 end) as stop_num from " + Etl_job_cur.TableName +
						" where etl_sys_cd=?", Job_Status.DONE.getCode(), Job_Status.PENDING.getCode(),
				Job_Status.ERROR.getCode(), Job_Status.WAITING.getCode(), Job_Status.RUNNING.getCode(),
				Job_Status.STOP.getCode(), etl_sys_cd);
		// 4.创建存放系统批量情况相关信息的集合
		Map<String, Object> etlJobCurrMap = new HashMap<>();
		etlJobCurrMap.put("curr_bath_date", curr_bath_date);
		etlJobCurrMap.put("etlJobCurrList", etlJobCurrList);
		// 5.返回存放系统批量情况相关信息的集合
		return etlJobCurrMap;
	}

	@Method(desc = "查询系统级当前干预情况",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
					"2.查询系统级当前干预情况")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Return(desc = "返回查询系统级当前干预情况", range = "无限制")
	public List<Map<String, Object>> searchSysLevelCurrInterventionInfo(String etl_sys_cd) {
		// 1.数据可访问权限处理方式，通过user_id进行权限控制
		// 2.查询系统级当前干预情况
		return Dbo.queryList("SELECT t1.event_id,t1.etl_sys_cd,t1.etl_job,t1.etl_hand_type," +
				"t1.pro_para,hand_status,st_time,warning FROM " + Etl_job_hand.TableName +
				" t1 left join " + Etl_job_cur.TableName + " t2 on t1.etl_job=t2.etl_job " +
				" and t1.etl_sys_cd=t2.etl_sys_cd left join " + Etl_sub_sys_list.TableName +
				" t3 on t2.sub_sys_cd=t3.sub_sys_cd and t2.etl_sys_cd=t3.etl_sys_cd" +
				" WHERE t1.etl_sys_cd=? AND t1.etl_job=?", etl_sys_cd, "[NOTHING]");
	}

	@Method(desc = "分页查询系统级历史干预情况",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
					"2.分页查询系统级历史干预情况" +
					"3.创建存放分页查询系统干预历史干预情况以及总记录数的集合并返回")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Param(name = "currPage", desc = "分页查询当前页", range = "大于0的正整数", valueIfNull = "1")
	@Param(name = "pageSize", desc = "分页查询每页显示记录数", range = "大于0的正整数", valueIfNull = "10")
	@Return(desc = "返回存放分页查询系统干预历史干预情况以及总记录数的集合", range = "无限制")
	public Map<String, Object> searchSysLeverHisInterventionByPage(String etl_sys_cd, int currPage,
	                                                               int pageSize) {
		// 1.数据可访问权限处理方式，通过user_id进行权限控制
		// 2.分页查询系统级历史干预情况
		Page page = new DefaultPageImpl(currPage, pageSize);
		List<Map<String, Object>> handHisList = Dbo.queryPagedList(page, "SELECT t1.event_id,t1.etl_sys_cd," +
						"t1.etl_job,t1.etl_hand_type,t1.pro_para,hand_status,st_time,warning FROM "
						+ Etl_job_hand_his.TableName + " t1 left join " + Etl_job_cur.TableName +
						" t2 on t1.etl_job=t2.etl_job and t1.etl_sys_cd=t2.etl_sys_cd left join "
						+ Etl_sub_sys_list.TableName + " t3 on t2.sub_sys_cd=t3.sub_sys_cd " +
						" and t2.etl_sys_cd=t3.etl_sys_cd WHERE t1.etl_sys_cd=? AND t1.etl_job=?",
				etl_sys_cd, "[NOTHING]");
		// 3.创建存放分页查询系统干预历史干预情况以及总记录数的集合并返回
		Map<String, Object> handHisMap = new HashMap<>();
		handHisMap.put("handHisList", handHisList);
		handHisMap.put("totalSize", page.getTotalSize());
		return handHisMap;
	}

	@Method(desc = "系统级干预操作",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
					"2.封装实体字段属性值" +
					"3.判断工程下有作业是否正在干预" +
					"4.检查etl_hand_type是否合法" +
					"5.判断干预类型进行不同情况操作" +
					"5.1 重跑或续跑，查询该工程为传入的几种状态的个数" +
					"5.1.1 个数大于0，数据回滚" +
					"5.1.2 正常干预将数据插入作业干预表中" +
					"5.2 非重跑或续跑，正常干预将数据插入作业干预表中")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Param(name = "etl_hand_type", desc = "干预类型", range = "使用（Meddle_type）代码项")
	@Param(name = "curr_bath_date", desc = "当前批量日期", range = "无限制")
	public void sysLevelInterventionOperate(String etl_sys_cd, String etl_hand_type, String curr_bath_date) {
		// 1.数据可访问权限处理方式，通过user_id进行权限控制
		// 2.封装实体字段属性值
		Etl_job_hand etl_job_hand = new Etl_job_hand();
		etl_job_hand.setEtl_sys_cd(etl_sys_cd);
		etl_job_hand.setEtl_hand_type(etl_hand_type);
		etl_job_hand.setHand_status(Meddle_status.TRUE.getCode());
		etl_job_hand.setMain_serv_sync(Main_Server_Sync.YES.getCode());
		etl_job_hand.setEtl_job("[NOTHING]");
		etl_job_hand.setEvent_id(DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()) + " " +
				DateUtil.parseStr2TimeWith6Char(DateUtil.getSysTime()));
		etl_job_hand.setSt_time(DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()) + " " +
				DateUtil.parseStr2TimeWith6Char(DateUtil.getSysTime()));
		etl_job_hand.setPro_para(etl_sys_cd + "," + curr_bath_date);
		// 3.判断工程下有作业是否正在干预
		if (ETLJobUtil.isEtlJobHandExist(etl_sys_cd)) {
			throw new BusinessException("工程下有作业正在干预！");
		}
		// 4.检查etl_hand_type是否合法
		Meddle_type.ofEnumByCode(etl_hand_type);
		// 5.判断干预类型进行不同情况操作
		if (Meddle_type.SYS_ORIGINAL == (Meddle_type.ofEnumByCode(etl_hand_type)) ||
				Meddle_type.SYS_RESUME == (Meddle_type.ofEnumByCode(etl_hand_type))) {
			// 5.1 重跑或续跑，查询该工程为传入的几种状态的个数
			String[] jobStatus = {Job_Status.PENDING.getCode(), Job_Status.RUNNING.getCode(),
					Job_Status.WAITING.getCode(), Job_Status.ERROR.getCode()};
			long count = getEtlSysStatus(etl_sys_cd, jobStatus);
			if (count > 0) {
				// 5.1.1 个数大于0，数据回滚
				Dbo.rollbackTransaction();
				throw new BusinessException("工程状态为错误,挂起,运行或等待的不可以重跑!");
			} else {
				// 5.1.2 正常干预将数据插入作业干预表中
				etl_job_hand.add(Dbo.db());
			}
		} else {
			// 5.2 非重跑或续跑，正常干预将数据插入作业干预表中
			etl_job_hand.add(Dbo.db());
		}
	}

	@Method(desc = "查询该工程为传入的几种状态的个数",
			logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
					"2.判断传入状态的数组是否为空" +
					"3.查询运行状态是否在指定状态数组中")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Param(name = "jobStatus", desc = "传入的状态的数组", range = "使用（Job_Status）代码项")
	@Return(desc = "返回查询该工程为传入的几种状态的个数", range = "无限制")
	private long getEtlSysStatus(String etl_sys_cd, String[] jobStatus) {
		// 1.数据可访问权限处理方式，该方法不需要权限控制
		SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
		asmSql.clean();
		asmSql.addSql("SELECT count(1) from " + Etl_sys.TableName + " where etl_sys_cd=?");
		asmSql.addParam(etl_sys_cd);
		// 2.判断传入状态的数组是否为空
		if (jobStatus != null && jobStatus.length > 0) {
			asmSql.addORParam("sys_run_status", jobStatus);
		}
		// 3.查询该工程为传入的几种状态的个数
		return Dbo.queryNumber(asmSql.sql(), asmSql.params()).orElseThrow(() ->
				new BusinessException("sql查询错误！"));
	}
}
