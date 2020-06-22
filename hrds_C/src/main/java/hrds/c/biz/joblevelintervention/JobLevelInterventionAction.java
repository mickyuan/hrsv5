package hrds.c.biz.joblevelintervention;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.core.utils.Validator;
import fd.ng.db.jdbc.DefaultPageImpl;
import fd.ng.db.jdbc.Page;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.util.Dbo;
import hrds.c.biz.bean.JobHandBean;
import hrds.c.biz.util.ETLJobUtil;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.Job_Status;
import hrds.commons.codes.Main_Server_Sync;
import hrds.commons.codes.Meddle_status;
import hrds.commons.codes.Meddle_type;
import hrds.commons.entity.Etl_job_cur;
import hrds.commons.entity.Etl_job_hand;
import hrds.commons.entity.Etl_job_hand_his;
import hrds.commons.entity.Etl_sub_sys_list;
import hrds.commons.exception.AppSystemException;
import hrds.commons.exception.BusinessException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "作业调度作业级干预", author = "dhw", createdate = "2019/12/10 11:56")
public class JobLevelInterventionAction extends BaseAction {

	@Method(desc = "查询作业级干预作业情况",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
					"2.判断作业名称是否为空，不为空加条件查询" +
					"3.判断任务名称是否为空，不为空加条件查询" +
					"4.判断作业调度状态是否为空，不为空加条件查询" +
					"5.分页查询作业级干预作业情况" +
					"6.存放查询作业级干预作业情况相关信息集合封装数据并返回")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Param(name = "etl_job", desc = "作业名称", range = "新增作业时生成", nullable = true)
	@Param(name = "sub_sys_desc", desc = "任务名称", range = "新增任务时生成", nullable = true)
	@Param(name = "job_status", desc = "作业调度状态", range = "使用（Job_Status）代码项", nullable = true)
	@Param(name = "currPage", desc = "分页查询当前页", range = "大于0的正整数", valueIfNull = "1")
	@Param(name = "pageSize", desc = "分页查询每页显示记录数", range = "大于0的正整数", valueIfNull = "5")
	@Return(desc = "返回作业级干预作业情况", range = "无限制")
	public Map<String, Object> searchJobLevelIntervention(String etl_sys_cd, String etl_job, String sub_sys_desc,
	                                                      String job_status, int currPage, int pageSize) {
		// 1.数据可访问权限处理方式，通过user_id进行权限控制
		if (!ETLJobUtil.isEtlSysExistById(etl_sys_cd, getUserId())) {
			throw new BusinessException("当前工程已不存在");
		}
		SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
		asmSql.clean();
		asmSql.addSql("SELECT t1.etl_sys_cd,t1.etl_job,");
		asmSql.addSql("CONCAT(t2.sub_sys_desc,'(',t2.sub_sys_cd,')') AS subSysName,");
		asmSql.addSql("t1.curr_bath_date,t1.job_disp_status FROM " + Etl_job_cur.TableName + " t1 LEFT JOIN ");
		asmSql.addSql(Etl_sub_sys_list.TableName + " t2 ON t1.etl_sys_cd=t2.etl_sys_cd");
		asmSql.addSql(" AND t1.sub_sys_cd=t2.sub_sys_cd WHERE t1.etl_sys_cd=?");
		asmSql.addParam(etl_sys_cd);
		// 2.判断作业名称是否为空，不为空加条件查询
		if (StringUtil.isNotBlank(etl_job)) {
			asmSql.addLikeParam("t1.etl_job", "%" + etl_job + "%");
		}
		// 3.判断任务名称是否为空，不为空加条件查询
		if (StringUtil.isNotBlank(sub_sys_desc)) {
			asmSql.addLikeParam("t2.sub_sys_desc", "%" + sub_sys_desc + "%");
		}
		// 4.判断作业调度状态是否为空，不为空加条件查询
		if (StringUtil.isNotBlank(job_status)) {
			asmSql.addSql(" AND t1.job_disp_status=?");
			asmSql.addParam(job_status);
		}
		asmSql.addSql(" order by etl_job");
		// 5.分页查询作业级干预作业情况
		Page page = new DefaultPageImpl(currPage, pageSize);
		List<Map<String, Object>> etlJobInfoList = Dbo.queryPagedList(page, asmSql.sql(), asmSql.params());
		// 6.存放查询作业级干预作业情况相关信息集合封装数据并返回
		Map<String, Object> etlJobInfoMap = new HashMap<>();
		etlJobInfoMap.put("totalSize", page.getTotalSize());
		etlJobInfoMap.put("etlJobInfoList", etlJobInfoList);
		return etlJobInfoMap;
	}

	@Method(desc = "查询作业级当前干预情况",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
					"2.分页查询系统级当前干预情况" +
					"3.返回作业级干预当前干预情况")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Param(name = "currPage", desc = "分页查询当前页", range = "大于0的正整数", valueIfNull = "1")
	@Param(name = "pageSize", desc = "分页查询每页显示记录数", range = "大于0的正整数", valueIfNull = "5")
	@Return(desc = "返回查询作业级当前干预情况", range = "无限制")
	public Map<String, Object> searchJobLevelCurrInterventionByPage(String etl_sys_cd, int currPage,
	                                                                int pageSize) {
		// 1.数据可访问权限处理方式，通过user_id进行权限控制
		if (!ETLJobUtil.isEtlSysExistById(etl_sys_cd, getUserId())) {
			throw new BusinessException("当前工程已不存在");
		}
		// 2.分页查询查询作业级当前干预情况
		Page page = new DefaultPageImpl(currPage, pageSize);
		List<Map<String, Object>> currInterventionList = Dbo.queryPagedList(page,
				"SELECT t1.event_id,t1.etl_sys_cd,t1.etl_job,t1.etl_hand_type," +
						"t1.pro_para,hand_status,st_time,warning,CONCAT(t3.sub_sys_desc,'(',t3.sub_sys_cd,')') " +
						" AS subSysName FROM " + Etl_job_hand.TableName + " t1 left join " + Etl_job_cur.TableName +
						" t2 on t1.etl_job=t2.etl_job and t1.etl_sys_cd=t2.etl_sys_cd left join "
						+ Etl_sub_sys_list.TableName + " t3 on t2.sub_sys_cd=t3.sub_sys_cd " +
						" and t2.etl_sys_cd=t3.etl_sys_cd WHERE t1.etl_sys_cd=? AND t1.etl_job<>?",
				etl_sys_cd, "[NOTHING]");
		// 3.返回作业级干预当前干预情况
		Map<String, Object> currInterventionMap = new HashMap<>();
		currInterventionMap.put("totalSize", page.getTotalSize());
		currInterventionMap.put("currInterventionList", currInterventionList);
		return currInterventionMap;
	}

	@Method(desc = "分页查询作业级历史干预情况",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
					"2.判断工程是否存在" +
					"3.分页查询系统级历史干预情况" +
					"4.创建存放分页查询作业级历史干预情况以及总记录数的集合并返回")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Param(name = "currPage", desc = "分页查询当前页", range = "大于0的正整数", valueIfNull = "1")
	@Param(name = "pageSize", desc = "分页查询每页显示记录数", range = "大于0的正整数", valueIfNull = "10")
	@Return(desc = "返回存放分页查询作业级历史干预情况以及总记录数的集合", range = "无限制")
	public Map<String, Object> searchJobLeverHisInterventionByPage(String etl_sys_cd, int currPage,
	                                                               int pageSize) {
		// 1.数据可访问权限处理方式，通过user_id进行权限控制
		if (!ETLJobUtil.isEtlSysExistById(etl_sys_cd, getUserId())) {
			throw new BusinessException("当前工程已不存在");
		}
		// 2.分页查询系统级历史干预情况
		Page page = new DefaultPageImpl(currPage, pageSize);
		List<Map<String, Object>> handHisList = Dbo.queryPagedList(page, "SELECT t1.event_id,t1.etl_sys_cd," +
				"t1.etl_job,t1.etl_hand_type,t1.pro_para,hand_status,st_time,warning," +
				"CONCAT(t3.sub_sys_desc,'(',t3.sub_sys_cd,')') AS subSysName FROM " + Etl_job_hand_his.TableName
				+ " t1 left join " + Etl_job_cur.TableName + " t2 on t1.etl_job=t2.etl_job " +
				" and t1.etl_sys_cd=t2.etl_sys_cd left join " + Etl_sub_sys_list.TableName + " t3 " +
				"on t2.sub_sys_cd=t3.sub_sys_cd and t2.etl_sys_cd=t3.etl_sys_cd WHERE t1.etl_sys_cd=? " +
				" AND t1.etl_job<>?", etl_sys_cd, "[NOTHING]");
		// 3.创建存放分页查询系统干预历史干预情况以及总记录数的集合并返回
		Map<String, Object> handHisMap = new HashMap<>();
		handHisMap.put("handHisList", handHisList);
		handHisMap.put("totalSize", page.getTotalSize());
		return handHisMap;
	}

	@Method(desc = "作业级干预操作",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
					"2.封装作业干预实体属性" +
					"3.判断工程下是否有作业正在干预" +
					"4.根据不同的干预类型处理不同情况" +
					"4.1停止" +
					"4.2跳过" +
					"4.3重跑" +
					"4.4强制执行" +
					"4.5设置优先级")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Param(name = "etl_job", desc = "作业名称", range = "新增作业时生成")
	@Param(name = "etl_hand_type", desc = "干预类型", range = "使用（Meddle_type）代码项")
	@Param(name = "curr_bath_date", desc = "当前批量日期", range = "yyyyMMdd格式")
	@Param(name = "job_priority", desc = "作业优先级", range = "取值范围", nullable = true)
	public void jobLevelInterventionOperate(String etl_sys_cd, String etl_job, String etl_hand_type,
	                                        String curr_bath_date, Integer job_priority) {
		// 1.数据可访问权限处理方式，通过user_id进行权限控制
		if (!ETLJobUtil.isEtlSysExistById(etl_sys_cd, getUserId())) {
			throw new BusinessException("当前工程已不存在");
		}
		// 2.封装作业干预实体属性
		Etl_job_hand etl_job_hand = new Etl_job_hand();
		etl_job_hand.setEtl_sys_cd(etl_sys_cd);
		etl_job_hand.setEtl_hand_type(etl_hand_type);
		etl_job_hand.setEtl_job(etl_job);
		etl_job_hand.setEvent_id(DateUtil.getSysDate() + " " +
				DateUtil.parseStr2TimeWith6Char(DateUtil.getSysTime()));
		etl_job_hand.setSt_time(DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()) + " " +
				DateUtil.parseStr2TimeWith6Char(DateUtil.getSysTime()));
		etl_job_hand.setHand_status(Meddle_status.TRUE.getCode());
		etl_job_hand.setMain_serv_sync(Main_Server_Sync.YES.getCode());
		if (curr_bath_date.length() == 10 && curr_bath_date.contains("-")) {
			curr_bath_date = StringUtil.replace(curr_bath_date, "-", "");
		}
		etl_job_hand.setPro_para(etl_sys_cd + "," + etl_job + "," + curr_bath_date);
		// 3.判断工程下是否有作业正在干预
		if (ETLJobUtil.isEtlJobHandExistByJob(etl_sys_cd, etl_job)) {
			throw new BusinessException("工程下有作业" + etl_job + "正在干预！");
		}
		// 4.根据不同的干预类型处理不同情况
		if (Meddle_type.JOB_STOP == (Meddle_type.ofEnumByCode(etl_hand_type))) {
			// 4.1停止
			String[] jobStatus = {Job_Status.DONE.getCode(), Job_Status.ERROR.getCode(), Job_Status.STOP.getCode()};
			// 根据工程代码作业名获取作业状态
			long count = getEtlJobStatus(etl_sys_cd, etl_job, jobStatus);
			// 根据作业状态处理不同情况
			if (count > 0) {
				// 数据回滚
				Dbo.rollbackTransaction();
				throw new BusinessException("作业状态为完成或错误或停止的不可以停止，etl_job=" + etl_job);
			} else {
				// 正常干预
				etl_job_hand.add(Dbo.db());
			}
		} else if (Meddle_type.JOB_JUMP == Meddle_type.ofEnumByCode(etl_hand_type)) {
			// 4.2跳过
			String[] jobStatus = {Job_Status.DONE.getCode(), Job_Status.RUNNING.getCode()};
			// 根据工程代码作业名获取作业状态
			long count = getEtlJobStatus(etl_sys_cd, etl_job, jobStatus);
			// 根据作业状态处理不同情况
			if (count > 0) {
				// 数据回滚
				Dbo.rollbackTransaction();
				throw new BusinessException("作业状态为运行或完成的不可以跳过，etl_job=" + etl_job);
			} else {
				// 正常干预
				etl_job_hand.add(Dbo.db());
			}
		} else if (Meddle_type.JOB_RERUN == Meddle_type.ofEnumByCode(etl_hand_type)) {
			// 4.3重跑
			String[] jobStatus = {Job_Status.PENDING.getCode(), Job_Status.RUNNING.getCode(),
					Job_Status.WAITING.getCode()};
			// 根据工程代码作业名获取作业状态
			long count = getEtlJobStatus(etl_sys_cd, etl_job, jobStatus);
			// 根据作业状态处理不同情况
			if (count > 0) {
				// 数据回滚
				Dbo.rollbackTransaction();
				throw new BusinessException("作业状态为挂起或运行或等待的不可以重跑，etl_job=" + etl_job);
			} else {
				// 正常干预
				etl_job_hand.add(Dbo.db());
			}
		} else if (Meddle_type.JOB_TRIGGER == Meddle_type.ofEnumByCode(etl_hand_type)) {
			// 4.4强制执行
			String[] jobStatus = {Job_Status.DONE.getCode(), Job_Status.ERROR.getCode(),
					Job_Status.RUNNING.getCode(), Job_Status.STOP.getCode()};
			// 根据工程代码作业名获取作业状态
			long count = getEtlJobStatus(etl_sys_cd, etl_job, jobStatus);
			// 根据作业状态处理不同情况
			if (count > 0) {
				// 数据回滚
				Dbo.rollbackTransaction();
				throw new BusinessException("作业状态为完成、错误、运行、停止不可以强制执行，etl_job=" + etl_job);
			} else {
				// 正常干预
				etl_job_hand.add(Dbo.db());
			}
		} else if (Meddle_type.JOB_PRIORITY == Meddle_type.ofEnumByCode(etl_hand_type)) {
			Validator.notNull(job_priority, "干预类型为调整优先级时，作业优先级不能为空");
			// 4.5设置优先级
			String[] jobStatus = {Job_Status.RUNNING.getCode()};
			// 根据工程代码作业名获取作业状态
			long count = getEtlJobStatus(etl_sys_cd, etl_job, jobStatus);
			// 根据作业状态处理不同情况
			if (count > 0) {
				// 数据回滚
				Dbo.rollbackTransaction();
				throw new BusinessException("作业状态为运行不可以临时调整优先级，etl_job=" + etl_job);
			} else {
				// 如果作业优先级不为空则设置作业程序参数
				if (StringUtil.isNotBlank(String.valueOf(job_priority))) {
					etl_job_hand.setPro_para(etl_sys_cd + "," + etl_job + "," + curr_bath_date + "," + job_priority);
				}
				// 正常干预
				etl_job_hand.add(Dbo.db());
			}
		} else {
			throw new AppSystemException("暂时不支持该干预类型！");
		}
	}

	@Method(desc = "作业批量干预",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
					"2.获取存放作业干预作业情况信息" +
					"3.遍历获取批量干预作业名称以及当前批量日期" +
					"4.循环干预作业")
	@Param(name = "jobHandBeans", desc = "作业干预自定义实体对象", range = "包括作业干预实体与当前批量日期",
			isBean = true)
	@Param(name = "job_priority", desc = "作业优先级", range = "无限制", nullable = true)
	public void batchJobLevelInterventionOperate(JobHandBean[] jobHandBeans, Integer job_priority) {
		// 1.数据可访问权限处理方式，通过user_id进行权限控制
		if (jobHandBeans == null || jobHandBeans.length == 0) {
			// 2.判断批量干预值是否为空
			throw new BusinessException("干预作业为空，请检查");
		}
		// 3.遍历获取批量干预作业名称以及当前批量日期
		for (JobHandBean jobHandBean : jobHandBeans) {
			Validator.notBlank(jobHandBean.getEtl_job(), "作业名称不能为空");
			Validator.notBlank(jobHandBean.getEtl_hand_type(), "干预类型不能为空");
			Validator.notBlank(jobHandBean.getEtl_sys_cd(), "工程编号不能为空");
			Validator.notBlank(jobHandBean.getCurr_bath_date(), "批量日期不能为空");
			// 4.循环干预作业
			jobLevelInterventionOperate(jobHandBean.getEtl_sys_cd(), jobHandBean.getEtl_job(),
					jobHandBean.getEtl_hand_type(), jobHandBean.getCurr_bath_date(), job_priority);
		}
	}

	@Method(desc = "根据工程代码作业名获取作业状态",
			logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
					"2.判断传入状态的数组是否为空" +
					"3.查询根据工程代码作业名获取作业状态")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Param(name = "etl_job", desc = "作业名称", range = "新增作业时生成")
	@Param(name = "jobStatus", desc = "传入的状态的数组", range = "使用（Job_Status）代码项")
	@Return(desc = "返回查询根据工程代码作业名获取作业状态", range = "取值范围")
	private long getEtlJobStatus(String etl_sys_cd, String etl_job, String[] jobStatus) {
		// 1.数据可访问权限处理方式，该方法不需要权限控制
		SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
		asmSql.clean();
		asmSql.addSql("SELECT count(1) from " + Etl_job_cur.TableName + " where etl_job=? AND etl_sys_cd=?");
		asmSql.addParam(etl_job);
		asmSql.addParam(etl_sys_cd);
		// 2.判断传入状态的数组是否为空
		if (jobStatus != null && jobStatus.length != 0) {
			asmSql.addORParam("job_disp_status", jobStatus);
		}
		// 3.查询根据工程代码作业名获取作业状态
		return Dbo.queryNumber(asmSql.sql(), asmSql.params()).orElseThrow(() ->
				new BusinessException("sql查询错误！"));
	}

}
