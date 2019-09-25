package hrds.control.task.helper;

import java.util.ArrayList;
import java.util.List;

import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.commons.exception.AppSystemException;
import hrds.control.beans.EtlJobBean;
import hrds.control.beans.EtlJobDefBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import scala.Int;

/**
 * ClassName: TaskSqlHelper
 * Description: 所有跟任务/作业有关的SQL查询，都使用此类
 * Author: Tiger.Wang
 * Date: 2019/9/2 17:43
 * Since: JDK 1.8
 **/
public class TaskSqlHelper {
	private static final Logger logger = LogManager.getLogger();
	private static final ThreadLocal<DatabaseWrapper> _dbBox = new ThreadLocal<>();

	private TaskSqlHelper() {}

	/**
	 * 用于获取单例的db连接。
	 * @author Tiger.Wang
	 * @date 2019/9/16
	 * @return fd.ng.db.jdbc.DatabaseWrapper
	 */
	private static DatabaseWrapper getDbConnector() {

		DatabaseWrapper db = _dbBox.get();
		if(db == null) {
			db = new DatabaseWrapper();
			_dbBox.set(db);
		}

		return db;
	}

	/**
	 * 用于关闭db连接。
	 * @author Tiger.Wang
	 * @date 2019/9/23
	 */
	public static void closeDbConnector() {

		DatabaseWrapper db = TaskSqlHelper.getDbConnector();
		if(db.isConnected()) {
			db.close();
			logger.info("-------------- 调度服务DB连接已经关闭 --------------");
		}
	}

	/**
	 * 根据调度系统编号获取调度系统信息。注意，该方法若无法查询到数据，则抛出AppSystemException异常。
	 * @author Tiger.Wang
	 * @date 2019/9/2
	 * @param etlSysCd	调度系统编号
	 * @return hrds.entity.Etl_sys
	 */
	public static Etl_sys getEltSysBySysCode(String etlSysCd) {

		Object[] row = SqlOperator.queryArray(
				TaskSqlHelper.getDbConnector(), "SELECT etl_sys_cd, etl_sys_name, etl_serv_ip, etl_serv_port, " +
						"contact_person, contact_phone, comments, curr_bath_date, bath_shift_time, main_serv_sync, " +
						"sys_run_status, user_name, user_pwd, serv_file_path, remarks FROM etl_sys " +
						"WHERE etl_sys_cd = ?", etlSysCd);

		if(row.length == 0) {
			throw new AppSystemException("无法根据调度系统编号获取系统信息 " + etlSysCd);
		}

		Etl_sys etlSys = new Etl_sys();
		etlSys.setEtl_sys_cd((String) row[0]);
		etlSys.setEtl_sys_name((String) row[1]);
		etlSys.setEtl_serv_ip((String) row[2]);
		etlSys.setEtl_serv_port((String) row[3]);
		etlSys.setContact_person((String) row[4]);
		etlSys.setContact_phone((String) row[5]);
		etlSys.setComments((String) row[6]);
		etlSys.setCurr_bath_date((String) row[7]);
		etlSys.setBath_shift_time((String) row[8]);
		etlSys.setMain_serv_sync((String) row[9]);
		etlSys.setSys_run_status((String) row[10]);
		etlSys.setUser_name((String) row[11]);
		etlSys.setUser_pwd((String) row[12]);
		etlSys.setServ_file_path((String) row[13]);
		etlSys.setRemarks((String) row[14]);

		return etlSys;
	}

	/**
	 * 根据调度系统编号、调度作业名获取该作业需要的资源。注意，该方法若无法查询到数据，则抛出AppSystemException异常。
	 * @author Tiger.Wang
	 * @date 2019/9/2
	 * @param etlSysCd	调度系统编号
	 * @param etlJob	调度作业名（调度作业标识）
	 * @return java.util.List<hrds.entity.Etl_job_resource_rela>	作业资源数组
	 */
	public static List<Etl_job_resource_rela> getJobNeedResources(String etlSysCd, String etlJob) {

		List<Etl_job_resource_rela> jobNeedResources
				= SqlOperator.queryList(TaskSqlHelper.getDbConnector(), Etl_job_resource_rela.class,
				"SELECT * FROM etl_job_resource_rela WHERE etl_sys_cd = ? AND etl_job = ?",
				etlSysCd, etlJob);
		//TODO 为作业配置资源后才认为该作业有效，否则报错
		if(null == jobNeedResources || jobNeedResources.size() == 0) {
			throw new AppSystemException("根据调度系统编号、调度作业名获取不到该作业需要的资源 " + etlJob);
		}

		return jobNeedResources;
	}

	/**
	 * 根据调度系统编号获取作业定义信息，无效作业不会被查询命中。
	 * 注意，此处不返回Etl_job_def，而返回EtlJobBean。EtlJobBean重载了前者，增加了设置资源的功能
	 * @author Tiger.Wang
	 * @date 2019/9/2
	 * @param etlSysCd	调度系统编号
	 * @return java.util.List<hrds.control.beans.EtlJobBean>	作业信息数组（来自作业定义表）
	 */
	public static List<EtlJobDefBean> getAllDefJob(String etlSysCd) {

		return SqlOperator.queryList(TaskSqlHelper.getDbConnector(), EtlJobDefBean.class,
						"SELECT * FROM etl_job_def WHERE etl_sys_cd = ? AND job_eff_flag != ?",
				etlSysCd, Job_Effective_Flag.NO.getCode());
	}

	/**
	 * 根据调度系统编号获取作业依赖信息。
	 * @author Tiger.Wang
	 * @date 2019/9/3
	 * @param etlSysCd	调度系统编号
	 * @return java.util.List<hrds.entity.Etl_dependency>
	 */
	public static List<Etl_dependency> getJobDependencyBySysCode(String etlSysCd) {

		return SqlOperator.queryList(TaskSqlHelper.getDbConnector(), Etl_dependency.class,
						"SELECT * FROM etl_dependency WHERE etl_sys_cd = ? AND status = ?",
				etlSysCd, Status.TRUE.getCode());
	}

	/**
	 * 根据调度系统编号修改调度系统跑批日期。注意，此处会将[主服务器同步标志]设置为同步。
	 * 若该方法修改数据失败（影响数据行数不为1），则抛出AppSystemException异常。
	 * @author Tiger.Wang
	 * @date 2019/9/3
	 * @param etlSysCd	调度系统编号
	 * @param currBathDate	当前跑批日期
	 */
	public static void updateEtlSysBathDate(String etlSysCd, String currBathDate) {

		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		int num = SqlOperator.execute(db, "UPDATE etl_sys SET main_serv_sync = ?, curr_bath_date = ? " +
					"WHERE etl_sys_cd = ?", Main_Server_Sync.YES.getCode(), currBathDate, etlSysCd);

		if(num != 1) {
			throw new AppSystemException("根据调度系统编号修改调度系统跑批日期失败" + etlSysCd);
		}

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号修改调度系统运行状态。注意，此处会将[主服务器同步标志]设置为同步。
	 * 若该方法修改数据失败（影响数据行数不为1），则抛出AppSystemException异常。
	 * @author Tiger.Wang
	 * @date 2019/9/4
	 * @param etlSysCd	调度系统编号
	 * @param runStatus	系统运行状态
	 */
	public static void updateEtlSysRunStatus(String etlSysCd, String runStatus) {

		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		int num = SqlOperator.execute(db, "UPDATE etl_sys SET main_serv_sync = ?, sys_run_status = ? " +
				"WHERE etl_sys_cd = ?", Main_Server_Sync.YES.getCode(), runStatus, etlSysCd);

		if(num != 1) {
			throw new AppSystemException("根据调度系统编号修改调度系统跑批日期失败" + etlSysCd);
		}

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号，修改准备运行的作业（调度状态为挂起、等待）的作业状态到指定作业状态。
	 * 注意，此方法只修改在[挂起]和[等待]状态下的作业，并且限制[主服务器同步标识]为是。
	 * @author Tiger.Wang
	 * @date 2019/9/10
	 * @param etlSysCd  调度系统编号
	 * @param runStatus 作业状态
	 */
	public static void updateReadyEtlJobStatus(String etlSysCd, String runStatus) {

		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		SqlOperator.execute(db, "UPDATE etl_job_cur SET job_disp_status = ?, main_serv_sync = ? " +
						"WHERE (job_disp_status = ? or job_disp_status = ?) AND etl_sys_cd = ?",
				runStatus, Main_Server_Sync.YES.getCode(), Job_Status.PENDING.getCode(),
				Job_Status.WAITING.getCode(), etlSysCd);

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号，修改准备运行的作业（调度状态为挂起、等待）的作业状态到指定作业状态。
	 * 注意，此方法只修改在[挂起]和[等待]状态下的作业，不限制[主服务器同步标识]。
	 * @author Tiger.Wang
	 * @date 2019/9/10
	 * @param etlSysCd  调度系统编号
	 * @param dispStatus    作业状态
	 */
	public static void updateReadyEtlJobsDispStatus(String etlSysCd, String dispStatus) {

		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		SqlOperator.execute(db, "UPDATE etl_job_cur SET job_disp_status = ? " +
						"WHERE etl_sys_cd = ? AND (job_disp_status = ? OR job_disp_status = ?)",
				dispStatus, etlSysCd, Job_Status.PENDING.getCode(), Job_Status.WAITING.getCode());

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号修改调度系统运行状态及当前跑批日期。
	 * 若该方法修改数据失败（影响数据行数不为1），则抛出AppSystemException异常。
	 * @author Tiger.Wang
	 * @date 2019/9/4
	 * @param etlSysCd	调度系统编号
	 * @param strBathDate	当前跑批日期（yyyyMMdd）
	 * @param runStatus	运行状态
	 */
	public static void updateEtlSysRunStatusAndBathDate(String etlSysCd, String strBathDate, String runStatus) {

		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		int num = SqlOperator.execute(db, "UPDATE etl_sys SET main_serv_sync = ?, " +
						"curr_bath_date = ?, sys_run_status = ? WHERE etl_sys_cd = ?",
				Main_Server_Sync.YES.getCode(), strBathDate, runStatus, etlSysCd);

		if(num != 1) {
			throw new AppSystemException("根据调度系统编号修改调度系统运行状态及当前跑批日期失败 " + etlSysCd);
		}

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号、当前批量日期删除作业信息。
	 * 注意，该方法在系统运行中使用，不会删除作业类型为T+0且按频率调度的作业。
	 * @author Tiger.Wang
	 * @date 2019/9/3
	 * @param etlSysCd	调度系统编号
	 * @param currBathDate	当前跑批日期（yyyyMMdd）
	 */
	public static void deleteEtlJobByBathDate(String etlSysCd, String currBathDate) {

		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		SqlOperator.execute(db, "DELETE FROM etl_job_cur WHERE etl_sys_cd = ? " +
						"AND curr_bath_date = ? AND disp_type != ? AND disp_freq != ?",
				etlSysCd, currBathDate, Dispatch_Type.TPLUS0.getCode(), Dispatch_Frequency.PinLv.getCode());

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号删除作业信息。注意，该方法在系统第一次运行的时候使用。
	 * @author Tiger.Wang
	 * @date 2019/9/4
	 * @param etlSysCd	作业编号
	 */
	public static void deleteEtlJobBySysCode(String etlSysCd) {

		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		SqlOperator.execute(db, "DELETE FROM etl_job_cur WHERE etl_sys_cd = ? ", etlSysCd);

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号、当前跑批日期、调度作业状态来删除etl_job表的信息。
	 * 注意，该方法不会删除调度触发方式为[频率]的作业。
	 * @author Tiger.Wang
	 * @date 2019/9/11
	 * @param etlSysCd  调度系统编号
	 * @param currBathDate  当前跑批日期
	 * @param jobStatus 调度作业状态
	 */
	public static void deleteEtlJobWithoutFrequency(String etlSysCd, String currBathDate, String jobStatus) {

		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		SqlOperator.execute(db, "DELETE FROM etl_job_cur WHERE etl_sys_cd = ? " +
						"AND curr_bath_date = ? AND job_disp_status = ? AND disp_type != ?",
				etlSysCd, currBathDate, jobStatus, Dispatch_Frequency.PinLv.getCode());

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号、当前跑批日期、调度作业状态来删除etl_job表的信息。
	 * @author Tiger.Wang
	 * @date 2019/9/11
	 * @param etlSysCd  调度系统编号
	 * @param currBathDate  当前跑批日期（yyyyMMdd）
	 * @param jobStatus 调度作业状态
	 */
	public static void deleteEtlJobByJobStatus(String etlSysCd, String currBathDate, String jobStatus) {

		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		SqlOperator.execute(db, "DELETE FROM etl_job_cur WHERE etl_sys_cd = ? " +
				"AND curr_bath_date = ? AND job_disp_status = ?",
				etlSysCd, currBathDate, jobStatus);

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据系统编号与参数编号查询参数值。注意，该方法若无法查询出数据，则抛出AppSystemException异常。
	 * @author Tiger.Wang
	 * @date 2019/9/4
	 * @param eltSysCd	系统编号
	 * @param paraCd	参数编号
	 * @return String
	 */
	public static String getEtlParameterVal(String eltSysCd, String paraCd) {

		Object[] row = SqlOperator.queryArray(TaskSqlHelper.getDbConnector(),
				"SELECT para_val FROM etl_para WHERE etl_sys_cd = ? AND para_cd = ?",
				eltSysCd, paraCd);

		if(row.length == 0) {
			throw new AppSystemException(String.format("找不到对应的变量[%s]", paraCd));
		}

		return (String) row[0];
	}

	/**
	 * 在etl_job表中新增一条数据。
	 * @author Tiger.Wang
	 * @date 2019/9/4
	 * @param job	Etl_job对象，对应etl_job表
	 */
	public static void insertIntoJobTable(Etl_job_cur job) {

		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		if(job.add(db) != 1) {
			throw new AppSystemException("在etl_job表中新增一条数据失败" + job.getEtl_job());
		}

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号、当前批量日期以及指定调度状态修改作业的信息。注意，
	 * 此处会将[主服务器同步标志]设置为同步。若修改数据影响的数据行数小于1，则抛出AppSystemException异常。
	 * @author Tiger.Wang
	 * @date 2019/9/4
	 * @param dispStatus	调度状态
	 * @param etlSysCd		调度系统编号
	 * @param currBathDate	当前批量日期（yyyyMMdd）
	 */
	public static void updateEtjJobWithDispStatus(String dispStatus, String etlSysCd, String currBathDate) {

		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		int num = SqlOperator.execute(db, "UPDATE etl_job_cur SET job_disp_status = ?, main_serv_sync = ? " +
				"WHERE etl_sys_cd = ? AND curr_bath_date = ?",
				dispStatus, Main_Server_Sync.YES.getCode(), etlSysCd, currBathDate);

		if(num < 1) {
			throw new AppSystemException("根据调度系统编号、当前批量日期修改作业信息失败" + etlSysCd);
		}

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号、调度作业标识、当前批量日期修改作业调度状态的作业信息。注意，
	 * 若修改数据影响的数据行数不为1，则抛出AppSystemException异常。
	 * @author Tiger.Wang
	 * @date 2019/9/5
	 * @param dispStatus	作业调度状态
	 * @param etlSysCd		调度系统编号
	 * @param etlJob		调度作业标识
	 * @param currBathDate	当前批量日期（yyyyMMdd）
	 */
	public static void updateEtlJobDispStatus(String dispStatus, String etlSysCd, String etlJob,
	                                             String currBathDate) {

		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		int num = SqlOperator.execute(db, "UPDATE etl_job_cur SET job_disp_status = ? WHERE etl_sys_cd = ? " +
						"AND etl_job = ? AND curr_bath_date = ?", dispStatus, etlSysCd, etlJob, currBathDate);

		if(num != 1) {
			throw new AppSystemException("根据调度系统编号、调度作业标识、当前批量日期修改作业信息失败" + etlJob);
		}

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号、调度作业标识、当前批量日期修改作业调度状态的作业信息。注意，
	 * etlJobCurs变量中必须含有调度作业标识、当前批量日期；若修改数据影响的数据行数不为1，则抛出AppSystemException异常。
	 * @author Tiger.Wang
	 * @date 2019/9/5
	 * @param dispStatus	作业调度状态
	 * @param etlSysCd		调度系统编号
	 * @param etlJobCurs    EtlJobBean集合，表示一组作业
	 */
	public static void updateEtlJobDispStatus(String dispStatus, String etlSysCd, List<EtlJobBean> etlJobCurs) {

		//TODO 在批量执行的情况下，数据只能这样组装？执行验证只能如此？
		List<Object[]> params = new ArrayList<>();
		for(EtlJobBean etlJobCur : etlJobCurs) {
			List<String> items = new ArrayList<>();
			items.add(dispStatus);
			items.add(etlSysCd);
			items.add(etlJobCur.getEtl_job());
			items.add(etlJobCur.getCurr_bath_date());
			params.add(items.toArray());
		}
		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		int[] nums = SqlOperator.executeBatch(db, "UPDATE etl_job_cur SET job_disp_status = ? " +
						"WHERE etl_sys_cd = ? AND etl_job = ? AND curr_bath_date = ?", params);

		for(int i = 0 ; i < nums.length ; i++) {
			if(nums[i] != 1) {
				//params.get(i)[2]，数字2表示etl_job
				throw new AppSystemException("根据调度系统编号、调度作业标识、当前批量日期修改作业信息失败" +
						params.get(i)[2]);
			}
		}

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号、调度作业标识修改作业的作业状态为指定内容。注意，此处会将[主服务器同步标识]设置为[是]。
	 * 若修改数据影响的数据行数不为1，则抛出AppSystemException异常。
	 * @author Tiger.Wang
	 * @date 2019/9/10
	 * @param dispStatus    作业状态
	 * @param etlSysCd  调度系统编号
	 * @param etlJob    调度作业标识
	 * @return boolean  是否有数据被修改
	 */
	public static void updateEtlJobDispStatus(String dispStatus, String etlSysCd, String etlJob) {

		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		int num = SqlOperator.execute(db, "UPDATE etl_job_cur SET job_disp_status = ?, main_serv_sync = ? " +
						"WHERE etl_sys_cd = ? AND etl_job = ?",
				dispStatus, Main_Server_Sync.YES.getCode(), etlSysCd, etlJob);

		if(num != 1) {
			throw new AppSystemException("根据调度系统编号、调度作业标识修改作业信息失败" + etlJob);
		}

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号、调度作业标识，进行修改当前执行时间。注意，
	 * 若修改数据影响的数据行数不为1，则抛出AppSystemException异常。
	 * @author Tiger.Wang
	 * @date 2019/9/5
	 * @param currStTime	开始执行时间（yyyyMMdd HHmmss）
	 * @param etlSysCd	调度系统编号
	 * @param etlJob	调度作业标识
	 */
	public static void updateEtlJobRunTime(String currStTime, String etlSysCd, String etlJob) {

		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		int num = SqlOperator.execute(db, "UPDATE etl_job_cur SET curr_st_time = ?, main_serv_sync = ? " +
						"WHERE etl_sys_cd = ? AND etl_job = ?",
				currStTime, Main_Server_Sync.YES.getCode(), etlSysCd, etlJob);

		if(num != 1) {
			throw new AppSystemException("根据调度系统编号、调度作业标识，进行修改当前执行时间失败" + etlJob);
		}

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号、调度作业标识、当前跑批日期修改调度作业信息。
	 * @author Tiger.Wang
	 * @date 2019/9/5
	 * @param etlSysCd	调度系统编号
	 * @param eltJob	调度作业标识
	 * @param currBathDate	当前跑批日期
	 * @param currStTime	当前执行开始日期时间（yyyyMMdd HHmmss）
	 * @param currEndTime	当前执行结束日期时间（yyyyMMdd HHmmss）
	 */
	public static void updateVirtualJob(String etlSysCd, String eltJob, String currBathDate,
										   String currStTime, String currEndTime) {

		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		SqlOperator.execute(db, "UPDATE etl_job_cur SET main_serv_sync = ?, job_disp_status = ?, " +
						"curr_st_time = ?, curr_end_time = ? WHERE etl_sys_cd = ? AND etl_job = ? " +
						"AND curr_bath_date = ?", Main_Server_Sync.YES.getCode(), Job_Status.DONE.getCode(),
				currStTime, currEndTime, etlSysCd, eltJob, currBathDate);

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 在调度系统在续跑模式时，根据调度系统编号及当前跑批日期，修改调度系统表信息。
	 * 注意，此处会修改作业调度状态、主服务器同步标志，但会根据条件修改调度系统信息：
	 * 指定的条件、作业调度状态不在挂起、结束状态、当天调度。
	 * @author Tiger.Wang
	 * @date 2019/9/4
	 * @param etlSysCd	调度系统编号
	 * @param currBathDate	当前跑批日期  （yyyyMMdd）
	 */
	public static void updateEtjJobByResumeRun(String etlSysCd, String currBathDate) {

		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		SqlOperator.execute(db, "UPDATE etl_job_cur SET job_disp_status = ?, main_serv_sync = ? " +
						"WHERE etl_sys_cd = ? AND curr_bath_date = ? AND job_disp_status NOT IN (?, ?) AND " +
						"today_disp = ?", Job_Status.PENDING.getCode(), Main_Server_Sync.YES.getCode(),
				etlSysCd, currBathDate, Job_Status.PENDING.getCode(), Job_Status.DONE.getCode(),
				Today_Dispatch_Flag.YES.getCode());

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号、当前跑批日期，来获取到达调度日期的作业
	 * @author Tiger.Wang
	 * @date 2019/9/4
	 * @param etlSysCd	调度系统编号
	 * @param currBathDate	当前跑批日期  （yyyyMMdd）
	 * @return java.util.List<hrds.entity.Etl_job>
	 */
	public static List<Etl_job_cur> getEtlJobs(String etlSysCd, String currBathDate) {

		return SqlOperator.queryList(TaskSqlHelper.getDbConnector(), Etl_job_cur.class,
				"SELECT * FROM etl_job_cur WHERE etl_sys_cd = ? AND curr_bath_date <= ?",
				etlSysCd, currBathDate);
	}

	/**
	 * 根据调度系统编号、调度作业标识、当前跑批日期获取调度作业信息。
	 * 注意，当无法获取到数据时抛出AppSystemException异常。
	 * @author Tiger.Wang
	 * @date 2019/9/5
	 * @param etlSysCd	调度系统编号
	 * @param etlJob	调度作业标识
	 * @param currBathDate	当前跑批日期  （yyyyMMdd）
	 * @return hrds.entity.Etl_job
	 */
	public static Etl_job_cur getEtlJob(String etlSysCd, String etlJob, String currBathDate) {

		Object[] row = SqlOperator.queryArray(TaskSqlHelper.getDbConnector(),
				"SELECT etl_sys_cd, etl_job, sub_sys_cd, etl_job_desc, pro_type, pro_dic, pro_name, pro_para, " +
						"log_dic, disp_freq, disp_offset, disp_type, disp_time, job_eff_flag, job_priority, " +
						"job_disp_status, curr_st_time, curr_end_time, overlength_val, overtime_val, " +
						"curr_bath_date, comments, today_disp, main_serv_sync, job_process_id, job_priority_curr, " +
						"job_return_val, exe_frequency, exe_num, com_exe_num, last_exe_time, star_time, end_time " +
						"FROM etl_job_cur WHERE etl_sys_cd = ? AND etl_job = ? AND curr_bath_date = ?",
				etlSysCd, etlJob, currBathDate);

		if(row.length == 0) {
			throw new AppSystemException("根据调度系统编号、调度作业标识、当前跑批日期获取调度作业信息失败 " + etlSysCd);
		}

		return TaskSqlHelper.obj2EtlJobCur(row);
	}

	/**
	 * 根据调度系统编号、调度作业标识获取调度作业信息。注意，当无法获取到数据时抛出AppSystemException异常。
	 * @author Tiger.Wang
	 * @date 2019/9/9
	 * @param etlSysCd  调度系统编号
	 * @param etlJob    调度作业标识
	 * @return hrds.commons.entity.Etl_job
	 */
	public static Etl_job_cur getEtlJob(String etlSysCd, String etlJob) {

		Object[] etlJobCurObj = SqlOperator.queryArray(TaskSqlHelper.getDbConnector(),
				"SELECT etl_sys_cd, etl_job, sub_sys_cd, etl_job_desc, pro_type, pro_dic, pro_name, pro_para, " +
						"log_dic, disp_freq, disp_offset, disp_type, disp_time, job_eff_flag, job_priority, " +
						"job_disp_status, curr_st_time, curr_end_time, overlength_val, overtime_val, " +
						"curr_bath_date, comments, today_disp, main_serv_sync, job_process_id, job_priority_curr, " +
						"job_return_val, exe_frequency, exe_num, com_exe_num, last_exe_time, star_time, end_time " +
						"FROM etl_job_cur WHERE etl_sys_cd = ? AND etl_job = ?", etlSysCd, etlJob);

		if(null == etlJobCurObj) {
			throw new AppSystemException("根据调度系统编号、调度作业标识获取调度作业信息失败 " + etlSysCd);
		}

		return TaskSqlHelper.obj2EtlJobCur(etlJobCurObj);
	}

	/**
	 * 用于将SqlOperator.queryArray查询出来的对象转换为Etl_job_cur对象，这样做避免了反射。
	 * 注意，该方法在传入的参数为null或数组个数不为32则会抛出AppSystemException异常。
	 * @author Tiger.Wang
	 * @date 2019/9/24
	 * @param row  Object[]，表示数据库中的一条数据
	 * @return hrds.commons.entity.Etl_job_cur
	 */
	private static Etl_job_cur obj2EtlJobCur(Object[] row) {

		if(row.length != 33 ) {
			throw new AppSystemException("Object[]转换为Etl_job_cur对象失败，对象为空或元素个数不为32");
		}

		Etl_job_cur etlJobCur = new Etl_job_cur();
		etlJobCur.setEtl_sys_cd((String) row[0]);
		etlJobCur.setEtl_job((String) row[1]);
		etlJobCur.setSub_sys_cd((String) row[2]);
		etlJobCur.setEtl_job_desc((String) row[3]);
		etlJobCur.setPro_type((String) row[4]);
		etlJobCur.setPro_dic((String) row[5]);
		etlJobCur.setPro_name((String) row[6]);
		etlJobCur.setPro_para((String) row[7]);
		etlJobCur.setLog_dic((String) row[8]);
		etlJobCur.setDisp_freq((String) row[9]);
		etlJobCur.setDisp_offset((int) row[10]);
		etlJobCur.setDisp_type((String) row[11]);
		etlJobCur.setDisp_time((String) row[12]);
		etlJobCur.setJob_eff_flag((String) row[13]);
		etlJobCur.setJob_priority((int) row[14]);
		etlJobCur.setJob_disp_status((String) row[15]);
		etlJobCur.setCurr_st_time((String) row[16]);
		etlJobCur.setCurr_end_time((String) row[17]);
		etlJobCur.setOverlength_val((int) row[18]);
		etlJobCur.setOvertime_val((int) row[19]);
		etlJobCur.setCurr_bath_date((String) row[20]);
		etlJobCur.setComments((String) row[21]);
		etlJobCur.setToday_disp((String) row[22]);
		etlJobCur.setMain_serv_sync((String) row[23]);
		etlJobCur.setJob_process_id((String) row[24]);
		etlJobCur.setJob_priority_curr((int) row[25]);
		etlJobCur.setJob_return_val((int) row[26]);
		etlJobCur.setExe_frequency((long) row[27]);
		etlJobCur.setExe_num((int) row[28]);
		etlJobCur.setCom_exe_num((int) row[29]);
		etlJobCur.setLast_exe_time((String) row[30]);
		etlJobCur.setStar_time((String) row[31]);
		etlJobCur.setEnd_time((String) row[32]);

		return etlJobCur;
	}

	/**
	 * 根据调度系统编号获取系统资源信息。注意，若无法查询出数据则抛出AppSystemException异常。
	 * @author Tiger.Wang
	 * @date 2019/9/5
	 * @param etlSysCd	调度系统编号
	 * @return java.util.List<hrds.commons.entity.Etl_resource>	系统资源信息集合
	 */
	public static List<Etl_resource> getEtlSystemResources(String etlSysCd) {

		List<Object[]> rows = SqlOperator.queryArrayList(TaskSqlHelper.getDbConnector(),
				"SELECT etl_sys_cd, resource_type, resource_max, resource_used, " +
						"main_serv_sync FROM etl_resource WHERE etl_sys_cd = ?", etlSysCd);

		if(rows.size() == 0) {
			throw new AppSystemException("根据调度系统编号获取系统资源信息失败" + etlSysCd);
		}

		List<Etl_resource> etlResources = new ArrayList<>();
		for(Object[] row : rows) {
			Etl_resource etlResource = new Etl_resource();
			etlResource.setEtl_sys_cd((String) row[0]);
			etlResource.setResource_type((String) row[1]);
			etlResource.setResource_max((int) row[2]);
			etlResource.setResource_used((int) row[3]);
			etlResource.setMain_serv_sync((String) row[4]);
			etlResources.add(etlResource);
		}

		return etlResources;
	}

	/**
	 * 根据调度系统编号来更新[资源使用数]。注意，若更新数据影响数据行数小于1，则抛出AppSystemException异常。
	 * @author Tiger.Wang
	 * @date 2019/9/4
	 * @param etlSysCd	调度系统编号
	 * @param used	资源使用数
	 */
	public static void updateEtlResourceUsed(String etlSysCd, int used) {

		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		int num = SqlOperator.execute(db,
				"UPDATE etl_resource SET resource_used = ? WHERE etl_sys_cd = ?", used, etlSysCd);

		if(num < 1) {
			throw new AppSystemException("根据调度系统编号来更新[资源使用数]失败" + etlSysCd);
		}

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号、资源类型批量修改[已使用资源]为指定数。
	 * 注意，etlResources变量中必须含有已使用资源数、资源类型；
	 * 若修改数据影响的数据行数不为1，则抛出AppSystemException异常。
	 * @author Tiger.Wang
	 * @date 2019/9/24
	 * @param etlSysCd  调度系统编号
	 * @param etlResources  系统资源集合
	 */
	public static void updateEtlResourceUsedByResourceType(String etlSysCd, List<Etl_resource> etlResources) {

		//TODO 在批量执行的情况下，数据只能这样组装？执行验证只能如此？
		List<Object[]> params = new ArrayList<>();
		for(Etl_resource etlResource : etlResources) {
			List<Object> items = new ArrayList<>();
			items.add(etlResource.getResource_used());
			items.add(etlSysCd);
			items.add(etlResource.getResource_type());
			params.add(items.toArray());
		}

		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		int[] nums = SqlOperator.executeBatch(db, "UPDATE etl_resource SET resource_used = ? " +
				"WHERE etl_sys_cd = ? AND resource_type = ?", params);

		for(int i = 0 ; i < nums.length ; i++) {
			//params.get(i)[2]表示resource_type
			if(nums[i] != 1) {
				throw new AppSystemException(String.format("据调度系统编号%s、资源类型%s修改[已使用资源]失败",
						etlSysCd, params.get(i)[2]));
			}
		}

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号、资源类型批量修改[已使用资源]为指定数。
	 * 若修改数据影响的数据行数不为1，则抛出AppSystemException异常。
	 * @author Tiger.Wang
	 * @date 2019/9/25
	 * @param etlSysCd  调度系统编号
	 * @param resourceType  资源类型
	 * @param used  资源使用数
	 */
	public static void updateEtlResourceUsedByResourceType(String etlSysCd, String resourceType, int used) {

		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		int num = SqlOperator.execute(db, "UPDATE etl_resource SET resource_used = ? " +
				"WHERE etl_sys_cd = ? AND resource_type = ?", used, etlSysCd, resourceType);

		if(num != 1) {
			throw new AppSystemException(String.format("据调度系统编号%s、资源类型%s修改[已使用资源]失败",
					etlSysCd, resourceType));
		}

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号获取调度作业干预信息
	 * @author Tiger.Wang
	 * @date 2019/9/6
	 * @param etlSyscd  调度系统编号
	 * @return java.util.List<hrds.commons.entity.Etl_job_hand>
	 */
	public static List<Etl_job_hand> getEtlJobHands(String etlSyscd) {
		//虽然该SQL一直在查询，但是干预的情况不多，不需要不用反射
		return SqlOperator.queryList(TaskSqlHelper.getDbConnector(), Etl_job_hand.class,
				"SELECT * FROM etl_job_hand WHERE hand_status = ? AND etl_sys_cd = ?",
				Meddle_status.TRUE.getCode(), etlSyscd);
	}

	/**
	 * 修改调度作业干预表（etl_job_hand）。注意，此处会使用参数中的etl_job_hand、hand_status、main_serv_sync、end_time、
	 * warning、etl_sys_cd、etl_job、etl_hand_type。若更新数据影响数据行数小于1，则抛出AppSystemException异常。
	 * @author Tiger.Wang
	 * @date 2019/9/9
	 * @param etlJobHand    Etl_job_hand
	 * @return boolean  是否有数据被修改
	 */
	public static void updateEtlJobHandle(Etl_job_hand etlJobHand) {

		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		int num = SqlOperator.execute(db, "UPDATE etl_job_hand SET hand_status = ?, main_serv_sync = ?, " +
				"end_time = ?, warning = ? WHERE etl_sys_cd = ? AND etl_job = ? AND etl_hand_type = ?",
				etlJobHand.getHand_status(), etlJobHand.getMain_serv_sync(), etlJobHand.getEnd_time(),
				etlJobHand.getWarning(), etlJobHand.getEtl_sys_cd(), etlJobHand.getEtl_hand_type(),
				etlJobHand.getEtl_job());

		if(num < 1) {
			throw new AppSystemException("修改调度作业干预表（etl_job_hand）失败" + etlJobHand.getEtl_job());
		}

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 在调度作业干预历史表中新增一条数据（etl_job_hand_his）。注意，若新增数据失败则抛出AppSystemException异常。
	 * @author Tiger.Wang
	 * @date 2019/9/9
	 * @param etlJobHandHis    Etl_job_hand对象
	 */
	public static void insertIntoEtlJobHandleHistory(Etl_job_hand_his etlJobHandHis) {

		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		if(etlJobHandHis.add(db) != 1) {
			throw new AppSystemException("新增干预历史（etl_job_hand_his）失败" + etlJobHandHis.getEtl_job());
		}

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号、调度作业标识、干预类型删除调度干预表（etl_job_hand）信息。
	 * @author Tiger.Wang
	 * @date 2019/9/9
	 * @param etlSysCd  调度系统编号
	 * @param etlJob    调度作业标识
	 * @param etlHandType   干预类型
	 */
	public static void deleteEtlJobHand(String etlSysCd, String etlJob, String etlHandType) {

		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		SqlOperator.execute(db, "DELETE FROM etl_job_hand WHERE etl_sys_cd = ? " +
						"AND etl_job = ? AND etl_hand_type = ?", etlSysCd, etlJob, etlHandType);

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号，获取需要调度的作业（作业状态为挂起或者等待）
	 * @author Tiger.Wang
	 * @date 2019/9/9
	 * @param etlSysCd  调度系统编号
	 * @return java.util.List<hrds.commons.entity.Etl_job>
	 */
	public static List<Etl_job_cur> getReadyEtlJobs(String etlSysCd) {

		return SqlOperator.queryList(TaskSqlHelper.getDbConnector(), Etl_job_cur.class,
				"SELECT * FROM etl_job_cur WHERE (job_disp_status = ? OR job_disp_status = ?) AND etl_sys_cd = ?",
				Job_Status.PENDING.getCode(), Job_Status.WAITING.getCode(), etlSysCd);
	}

	/**
	 * 根据调度系统编号，获取指定作业状态的作业。
	 * @author Tiger.Wang
	 * @date 2019/9/10
	 * @param etlSysCd  调度系统编号
	 * @param jobStatus 作业状态
	 * @return java.util.List<hrds.commons.entity.Etl_job>
	 */
	public static List<Etl_job_cur> getEtlJobsByJobStatus(String etlSysCd, String jobStatus) {

		return SqlOperator.queryList(TaskSqlHelper.getDbConnector(), Etl_job_cur.class,
				"SELECT * FROM etl_job_cur WHERE etl_sys_cd = ? AND job_disp_status = ?", etlSysCd, jobStatus);
	}

	/**
	 * 根据调度系统编号，将该系统下的作业置为挂起状态。注意，此处会将作业状态设置为[挂起]，
	 * 主服务器同步标志设置为[是]，且将作业优先级更新到当前作业优先级。
	 * 修改的数据，影响的数据行数小于1，则抛出AppSystemException异常。
	 * @author Tiger.Wang
	 * @date 2019/9/9
	 * @param etlSysCd  调度系统编号
	 */
	public static void updateEtlJobToPending(String etlSysCd) {

		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		int num = SqlOperator.execute(db, "UPDATE etl_job_cur SET job_disp_status = ?, main_serv_sync = ? " +
				"job_priority_curr = job_priority WHERE etl_sys_cd = ? AND today_disp = ?",
				Job_Status.PENDING.getCode(), Main_Server_Sync.YES.getCode(), etlSysCd,
				Today_Dispatch_Flag.YES.getCode());

		if(num < 1) {
			throw new AppSystemException("根据调度系统编号，将该系统下的作业置为挂起状态失败" + etlSysCd);
		}

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号，将作业状态在[停止]以及[错误]下的作业修改为指定状态。
	 * @author Tiger.Wang
	 * @date 2019/9/10
	 * @param etlSysCd  调度系统编号
	 * @param jobStatus 调度作业状态
	 */
	public static void updateEtlJobToPendingInResume(String etlSysCd, String jobStatus) {

		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		SqlOperator.execute(db, "UPDATE etl_job_cur SET job_disp_status = ?, main_serv_sync = ? " +
						"job_priority_curr = job_priority WHERE (job_disp_status = ? OR job_disp_status = ?) " +
						"AND etl_sys_cd = ?", jobStatus, Main_Server_Sync.YES.getCode(),
				Job_Status.STOP.getCode(), Job_Status.ERROR.getCode(), etlSysCd);

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号、调度作业编号、当前跑批日期修改指定的当前作业优先级。注意，此处仅会最多修改一条数据。
	 * 若修改的数据影响的数据行数不为1，则抛出AppSystemException异常。
	 * @author Tiger.Wang
	 * @date 2019/9/10
	 * @param jobPriorityCurr   当前作业优先级
	 * @param etlSysCd  调度系统编号
	 * @param etlJob    调度作业标识
	 * @param currBathDate  当前跑批日期 （yyyyMMdd）
	 */
	public static void updateEtlJobCurrPriority(int jobPriorityCurr, String etlSysCd, String etlJob,
	                                               String currBathDate) {

		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		int num = SqlOperator.execute(db, "UPDATE etl_job_cur SET job_priority_curr = ? " +
						"WHERE etl_sys_cd = ? AND etl_job = ? AND curr_bath_date = ?",
				jobPriorityCurr, etlSysCd, etlJob, currBathDate);

		if(num != 1) {
			throw new AppSystemException("根据调度系统编号、调度作业编号、当前跑批日期修改当前作业优先级失败"
					+ etlJob);
		}

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 在etl_job_hand表中新增一条数据。注意，此方法会根据传入的对象所携带的参数来新增数据。
	 * 若新增数据失败，则抛出AppSystemException异常。
	 * @author Tiger.Wang
	 * @date 2019/9/11
	 * @param jobHand   Etl_job_hand对象，表示一个作业干预信息
	 */
	public static void insertIntoEtlJobHand(Etl_job_hand jobHand) {

		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		if(jobHand.add(db) != 1) {
			throw new AppSystemException("在etl_job_hand表中新增一条数据失败" + jobHand.getEtl_job());
		}

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 在etl_job_disp_his表中新增一条数据。注意，此方法会根据传入的对象所携带的参数来新增数据。
	 * 若新增数据失败，则抛出AppSystemException异常。
	 * @author Tiger.Wang
	 * @date 2019/9/16
	 * @param etlJobDispHis Etl_job_disp_his对象，表示跑批历史
	 */
	public static void insertIntoEtlJobDispHis(Etl_job_disp_his etlJobDispHis) {

		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		if(etlJobDispHis.add(db) != 1) {
			throw new AppSystemException("在etl_job_disp_his表中新增一条数据失败" + etlJobDispHis.getEtl_job());
		}

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据系统编号、作业标识、当前作业跑批日期更新作业信息。注意，
	 * 该方法用到的参数有job_disp_status、curr_end_time、job_return_val、etl_sys_cd、etl_job、curr_bath_date，
	 * 该方法会将main_serv_sync设置为y。若更新数据所影响的数据行数不为1，则抛出AppSystemException异常。
	 * @author Tiger.Wang
	 * @date 2019/9/16
	 * @param etlJobCur Etl_job_cur对象，表示一个已登记到etl_job_cur表的作业
	 */
	public static void updateEtlJobFinished(Etl_job_cur etlJobCur) {

		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		int num = SqlOperator.execute(db, "UPDATE etl_job_cur SET main_serv_sync = ?, " +
						"job_disp_status = ?, curr_end_time = ?, job_return_val = ? " +
						"WHERE etl_sys_cd = ? AND etl_job = ? AND curr_bath_date = ?",
				Main_Server_Sync.YES.getCode(), etlJobCur.getJob_disp_status(), etlJobCur.getCurr_end_time(),
				etlJobCur.getJob_return_val(), etlJobCur.getEtl_sys_cd(), etlJobCur.getEtl_job(),
				etlJobCur.getCurr_bath_date());

		if(num != 1) {
			throw new AppSystemException("根据系统编号、作业标识、当前作业跑批日期更新作业信息失败"
					+ etlJobCur.getEtl_job());
		}

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据参数标识查询参数信息。注意，该方法主要用于验证所使用的参数是否正确。
	 * 在无法查询出时间时会抛出AppSystemException异常。
	 * @author Tiger.Wang
	 * @date 2019/9/18
	 * @param para  所使用的参数
	 * @return String para_cd
	 */
	public static String getParaByPara(String para) {

		Object[] row = SqlOperator.queryArray(TaskSqlHelper.getDbConnector(),
				"SELECT para_cd FROM etl_para WHERE para_cd = ?", para);

		if(row.length == 0) {
			throw new AppSystemException("所使用的参数标识不存在" + para);
		}

		return (String) row[0];
	}
}
