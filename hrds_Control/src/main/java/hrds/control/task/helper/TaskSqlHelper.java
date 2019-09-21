package hrds.control.task.helper;

import java.util.List;
import java.util.Optional;

import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.commons.exception.AppSystemException;
import hrds.control.beans.EtlJobDefBean;

/**
 * ClassName: TaskSqlHelper
 * Description: 所有跟任务/作业有关的SQL查询，都使用此类
 * Author: Tiger.Wang
 * Date: 2019/9/2 17:43
 * Since: JDK 1.8
 **/
public class TaskSqlHelper {

//	private static DatabaseWrapper db = new DatabaseWrapper();
	
	private TaskSqlHelper() {}

	/**
	 * 用于获取单例的db连接。
	 * @author Tiger.Wang
	 * @date 2019/9/16
	 * @return fd.ng.db.jdbc.DatabaseWrapper
	 */
	private static DatabaseWrapper getDbConnector() {
		return new DatabaseWrapper();
	}

	/**
	 * 根据调度系统编号获取调度系统信息。注意，该方法若无法查询到数据，则抛出AppSystemException异常。
	 * @author Tiger.Wang
	 * @date 2019/9/2
	 * @param etlSysCd	调度系统编号
	 * @return hrds.entity.Etl_sys
	 */
	public static Etl_sys getEltSysBySysCode(String etlSysCd) {

		try(DatabaseWrapper db = TaskSqlHelper.getDbConnector()) {

			Optional<Etl_sys> etlSys = SqlOperator.queryOneObject(db, Etl_sys.class,
					"SELECT * FROM etl_sys WHERE etl_sys_cd = ?", etlSysCd);

			if(!etlSys.isPresent()) {
				throw new AppSystemException("根据调度系统编号无法获取到信息：" + etlSysCd);
			}

			return etlSys.get();
		}
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

		try(DatabaseWrapper db = TaskSqlHelper.getDbConnector()) {

			List<Etl_job_resource_rela> jobNeedResources = SqlOperator.queryList(db, Etl_job_resource_rela.class,
					"SELECT * FROM etl_job_resource_rela WHERE etl_sys_cd = ? AND etl_job = ?",
					etlSysCd, etlJob);
			//TODO 为作业配置资源后才认为该作业有效，否则报错
			if(null == jobNeedResources || jobNeedResources.size() == 0) {
				throw new AppSystemException("根据调度系统编号、调度作业名获取不到该作业需要的资源 " + etlJob);
			}

			return jobNeedResources;
		}
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

		try(DatabaseWrapper db = TaskSqlHelper.getDbConnector()) {

			return SqlOperator.queryList(db, EtlJobDefBean.class,
							"SELECT * FROM etl_job_def WHERE etl_sys_cd = ? AND job_eff_flag != ?",
					etlSysCd, Job_Effective_Flag.NO.getCode());
		}
	}

	/**
	 * 根据调度系统编号获取作业依赖信息。
	 * @author Tiger.Wang
	 * @date 2019/9/3
	 * @param etlSysCd	调度系统编号
	 * @return java.util.List<hrds.entity.Etl_dependency>
	 */
	public static List<Etl_dependency> getJobDependencyBySysCode(String etlSysCd) {

		try(DatabaseWrapper db = TaskSqlHelper.getDbConnector()) {

			return SqlOperator.queryList(db, Etl_dependency.class,
							"SELECT * FROM etl_dependency WHERE etl_sys_cd = ? AND status = ?",
					etlSysCd, Status.TRUE.getCode());
		}
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

		try(DatabaseWrapper db = TaskSqlHelper.getDbConnector()) {

			int num = SqlOperator.execute(db, "UPDATE etl_sys SET main_serv_sync = ?, curr_bath_date = ? " +
						"WHERE etl_sys_cd = ?", Main_Server_Sync.YES.getCode(), currBathDate, etlSysCd);

			if(num != 1) {
				throw new AppSystemException("根据调度系统编号修改调度系统跑批日期失败" + etlSysCd);
			}

			SqlOperator.commitTransaction(db);
		}
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

		try(DatabaseWrapper db = TaskSqlHelper.getDbConnector()) {

			int num = SqlOperator.execute(db, "UPDATE etl_sys SET main_serv_sync = ?, sys_run_status = ? " +
					"WHERE etl_sys_cd = ?", Main_Server_Sync.YES.getCode(), runStatus, etlSysCd);

			if(num != 1) {
				throw new AppSystemException("根据调度系统编号修改调度系统跑批日期失败" + etlSysCd);
			}

			SqlOperator.commitTransaction(db);
		}
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

		try(DatabaseWrapper db = TaskSqlHelper.getDbConnector()) {

			SqlOperator.execute(db, "UPDATE etl_job_cur SET job_disp_status = ?, main_serv_sync = ? " +
							"WHERE (job_disp_status = ? or job_disp_status = ?) AND etl_sys_cd = ?",
					runStatus, Main_Server_Sync.YES.getCode(), Job_Status.PENDING.getCode(),
					Job_Status.WAITING.getCode(), etlSysCd);

			SqlOperator.commitTransaction(db);
		}
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

		try(DatabaseWrapper db = TaskSqlHelper.getDbConnector()) {

			SqlOperator.execute(db, "UPDATE etl_job_cur SET job_disp_status = ? " +
							"WHERE etl_sys_cd = ? AND (job_disp_status = ? OR job_disp_status = ?)",
					dispStatus, etlSysCd, Job_Status.PENDING.getCode(), Job_Status.WAITING.getCode());

			SqlOperator.commitTransaction(db);
		}
	}

	/**
	 * 根据调度系统编号修改调度系统运行状态及当前跑批日期。
	 * 若该方法修改数据失败（影响数据行数不为1），则抛出AppSystemException异常。
	 * @author Tiger.Wang
	 * @date 2019/9/4
	 * @param etlSysCd	调度系统编号
	 * @param strBathDate	当前跑批日期（yyyy-MM-dd）
	 * @param runStatus	运行状态
	 */
	public static void updateEtlSysRunStatusAndBathDate(String etlSysCd, String strBathDate, String runStatus) {

		try(DatabaseWrapper db = TaskSqlHelper.getDbConnector()) {

			int num = SqlOperator.execute(db, "UPDATE etl_sys SET main_serv_sync = ?, " +
							"curr_bath_date = ?, sys_run_status = ? WHERE etl_sys_cd = ?",
					Main_Server_Sync.YES.getCode(), strBathDate, runStatus, etlSysCd);

			if(num != 1) {
				throw new AppSystemException("根据调度系统编号修改调度系统运行状态及当前跑批日期失败 " + etlSysCd);
			}

			SqlOperator.commitTransaction(db);
		}
	}

	/**
	 * 根据调度系统编号、当前批量日期删除作业信息。
	 * 注意，该方法在系统运行中使用，不会删除作业类型为T+0且按频率调度的作业。
	 * @author Tiger.Wang
	 * @date 2019/9/3
	 * @param etlSysCd	调度系统编号
	 * @param currBathDate	当前跑批日期
	 */
	public static void deleteEtlJobByBathDate(String etlSysCd, String currBathDate) {

		try(DatabaseWrapper db = TaskSqlHelper.getDbConnector()) {

			SqlOperator.execute(db, "DELETE FROM etl_job_cur WHERE etl_sys_cd = ? " +
							"AND curr_bath_date = ? AND disp_type != ? AND disp_freq != ?",
					etlSysCd, currBathDate, Dispatch_Type.TPLUS0.getCode(), Dispatch_Frequency.PinLv.getCode());

			SqlOperator.commitTransaction(db);
		}
	}

	/**
	 * 根据调度系统编号删除作业信息。注意，该方法在系统第一次运行的时候使用。
	 * @author Tiger.Wang
	 * @date 2019/9/4
	 * @param etlSysCd	作业编号
	 */
	public static void deleteEtlJobBySysCode(String etlSysCd) {

		try(DatabaseWrapper db = TaskSqlHelper.getDbConnector()) {

			SqlOperator.execute(db, "DELETE FROM etl_job_cur WHERE etl_sys_cd = ? ", etlSysCd);

			SqlOperator.commitTransaction(db);
		}
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

		try(DatabaseWrapper db = TaskSqlHelper.getDbConnector()) {

			SqlOperator.execute(db, "DELETE FROM etl_job_cur WHERE etl_sys_cd = ? " +
							"AND curr_bath_date = ? AND job_disp_status = ? AND disp_type != ?",
					etlSysCd, currBathDate, jobStatus, Dispatch_Frequency.PinLv.getCode());

			SqlOperator.commitTransaction(db);
		}
	}

	/**
	 * 根据调度系统编号、当前跑批日期、调度作业状态来删除etl_job表的信息。
	 * @author Tiger.Wang
	 * @date 2019/9/11
	 * @param etlSysCd  调度系统编号
	 * @param currBathDate  当前跑批日期
	 * @param jobStatus 调度作业状态
	 */
	public static void deleteEtlJobByJobStatus(String etlSysCd, String currBathDate, String jobStatus) {

		try(DatabaseWrapper db = TaskSqlHelper.getDbConnector()) {

			SqlOperator.execute(db, "DELETE FROM etl_job_cur WHERE etl_sys_cd = ? " +
					"AND curr_bath_date = ? AND job_disp_status = ?",
					etlSysCd, currBathDate, jobStatus);

			SqlOperator.commitTransaction(db);
		}
	}

	/**
	 * 根据系统编号与参数编号查询参数值。
	 * @author Tiger.Wang
	 * @date 2019/9/4
	 * @param eltSysCd	系统编号
	 * @param paraCd	参数编号
	 * @return java.util.Optional<hrds.entity.Etl_para>
	 */
	public static Optional<Etl_para> getEtlParameterVal(String eltSysCd, String paraCd) {

		try(DatabaseWrapper db = TaskSqlHelper.getDbConnector()) {

			return SqlOperator.queryOneObject(db, Etl_para.class,
					"SELECT para_val FROM etl_para WHERE etl_sys_cd = ? AND para_cd = ?",
					eltSysCd, paraCd);
		}
	}

	/**
	 * 在etl_job表中新增一条数据。
	 * @author Tiger.Wang
	 * @date 2019/9/4
	 * @param job	Etl_job对象，对应etl_job表
	 */
	public static void insertIntoJobTable(Etl_job_cur job) {

		try(DatabaseWrapper db = TaskSqlHelper.getDbConnector()) {

			if(job.add(db) != 1) {
				throw new AppSystemException("在etl_job表中新增一条数据失败" + job.getEtl_job());
			}

			SqlOperator.commitTransaction(db);
		}
	}

	/**
	 * 根据调度系统编号、当前批量日期以及指定调度状态修改作业的信息。注意，
	 * 此处会将[主服务器同步标志]设置为同步。若修改数据影响的数据行数小于1，则抛出AppSystemException异常。
	 * @author Tiger.Wang
	 * @date 2019/9/4
	 * @param dispStatus	调度状态
	 * @param etlSysCd		调度系统编号
	 * @param currBathDate	当前批量日期
	 */
	public static void updateEtjJobWithDispStatus(String dispStatus, String etlSysCd, String currBathDate) {

		try(DatabaseWrapper db = TaskSqlHelper.getDbConnector()) {

			int num = SqlOperator.execute(db, "UPDATE etl_job_cur SET job_disp_status = ?, main_serv_sync = ? " +
					"WHERE etl_sys_cd = ? AND curr_bath_date = ?",
					dispStatus, Main_Server_Sync.YES.getCode(), etlSysCd, currBathDate);

			if(num < 1) {
				throw new AppSystemException("根据调度系统编号、当前批量日期修改作业信息失败" + etlSysCd);
			}

			SqlOperator.commitTransaction(db);
		}
	}

	/**
	 * 根据调度系统编号、调度作业标识、当前批量日期修改作业调度状态的作业信息。注意，
	 * 若修改数据影响的数据行数不为1，则抛出AppSystemException异常。
	 * @author Tiger.Wang
	 * @date 2019/9/5
	 * @param dispStatus	作业调度状态
	 * @param etlSysCd		调度系统编号
	 * @param etlJob		调度作业标识
	 * @param currBathDate	当前批量日期
	 */
	public static void updateEtlJobDispStatus(String dispStatus, String etlSysCd, String etlJob,
	                                             String currBathDate) {

		try(DatabaseWrapper db = TaskSqlHelper.getDbConnector()) {

			int num = SqlOperator.execute(db, "UPDATE etl_job_cur SET job_disp_status = ? WHERE etl_sys_cd = ? " +
							"AND etl_job = ? AND curr_bath_date = ?", dispStatus, etlSysCd, etlJob, currBathDate);

			if(num != 1) {
				throw new AppSystemException("根据调度系统编号、调度作业标识、当前批量日期修改作业信息失败" + etlJob);
			}

			SqlOperator.commitTransaction(db);
		}
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

		try(DatabaseWrapper db = TaskSqlHelper.getDbConnector()) {

			int num = SqlOperator.execute(db, "UPDATE etl_job_cur SET job_disp_status = ?, main_serv_sync = ? " +
							"WHERE etl_sys_cd = ? AND etl_job = ?",
					dispStatus, Main_Server_Sync.YES.getCode(), etlSysCd, etlJob);

			if(num != 1) {
				throw new AppSystemException("根据调度系统编号、调度作业标识修改作业信息失败" + etlJob);
			}

			SqlOperator.commitTransaction(db);
		}
	}

	/**
	 * 根据调度系统编号、调度作业标识，进行修改当前执行时间。注意，
	 * 若修改数据影响的数据行数不为1，则抛出AppSystemException异常。
	 * @author Tiger.Wang
	 * @date 2019/9/5
	 * @param currStTime	开始执行时间（yyyy-MM-dd HH:mm:ss）
	 * @param etlSysCd	调度系统编号
	 * @param etlJob	调度作业标识
	 */
	public static void updateEtlJobRunTime(String currStTime, String etlSysCd, String etlJob) {

		try(DatabaseWrapper db = TaskSqlHelper.getDbConnector()) {

			int num = SqlOperator.execute(db, "UPDATE etl_job_cur SET curr_st_time = ?, main_serv_sync = ? " +
							"WHERE etl_sys_cd = ? AND etl_job = ?",
					currStTime, Main_Server_Sync.YES.getCode(), etlSysCd, etlJob);

			if(num != 1) {
				throw new AppSystemException("根据调度系统编号、调度作业标识，进行修改当前执行时间失败" + etlJob);
			}

			SqlOperator.commitTransaction(db);
		}
	}

	/**
	 * 根据调度系统编号、调度作业标识、当前跑批日期修改调度作业信息。
	 * @author Tiger.Wang
	 * @date 2019/9/5
	 * @param etlSysCd	调度系统编号
	 * @param eltJob	调度作业标识
	 * @param currBathDate	当前跑批日期
	 * @param currStTime	当前执行开始日期时间（yyyy-MM-dd HH:mm:ss）
	 * @param currEndTime	当前执行结束日期时间（yyyy-MM-dd HH:mm:ss）
	 */
	public static void updateVirtualJob(String etlSysCd, String eltJob, String currBathDate,
										   String currStTime, String currEndTime) {

		try(DatabaseWrapper db = TaskSqlHelper.getDbConnector()) {

			SqlOperator.execute(db, "UPDATE etl_job_cur SET main_serv_sync = ?, job_disp_status = ?, " +
							"curr_st_time = ?, curr_end_time = ? WHERE etl_sys_cd = ? AND etl_job = ? " +
							"AND curr_bath_date = ?", Main_Server_Sync.YES.getCode(), Job_Status.DONE.getCode(),
					currStTime, currEndTime, etlSysCd, eltJob, currBathDate);

			SqlOperator.commitTransaction(db);
		}
	}

	/**
	 * 在调度系统在续跑模式时，根据调度系统编号及当前跑批日期，修改调度系统表信息。
	 * 注意，此处会修改作业调度状态、主服务器同步标志，但会根据条件修改调度系统信息：
	 * 指定的条件、作业调度状态不在挂起、结束状态、当天调度。
	 * @author Tiger.Wang
	 * @date 2019/9/4
	 * @param etlSysCd	调度系统编号
	 * @param currBathDate	当前跑批日期
	 */
	public static void updateEtjJobByResumeRun(String etlSysCd, String currBathDate) {

		try(DatabaseWrapper db = TaskSqlHelper.getDbConnector()) {

			SqlOperator.execute(db, "UPDATE etl_job_cur SET job_disp_status = ?, main_serv_sync = ? " +
							"WHERE etl_sys_cd = ? AND curr_bath_date = ? AND job_disp_status NOT IN (?, ?) AND " +
							"today_disp = ?", Job_Status.PENDING.getCode(), Main_Server_Sync.YES.getCode(),
					etlSysCd, currBathDate, Job_Status.PENDING.getCode(), Job_Status.DONE.getCode(),
					Today_Dispatch_Flag.YES.getCode());

			SqlOperator.commitTransaction(db);
		}
	}

	/**
	 * 根据调度系统编号、当前跑批日期，来获取到达调度日期的作业
	 * @author Tiger.Wang
	 * @date 2019/9/4
	 * @param etlSysCd	调度系统编号
	 * @param currBathDate	当前跑批日期
	 * @return java.util.Optional<hrds.entity.Etl_job>
	 */
	public static List<Etl_job_cur> getEtlJobs(String etlSysCd, String currBathDate) {

		try(DatabaseWrapper db = TaskSqlHelper.getDbConnector()) {

			return SqlOperator.queryList(db, Etl_job_cur.class,
					"SELECT * FROM etl_job_cur WHERE etl_sys_cd = ? AND curr_bath_date <= ?",
					etlSysCd, currBathDate);
		}
	}

	/**
	 * 根据调度系统编号、调度作业标识、当前跑批日期获取调度作业信息。
	 * 注意，当无法获取到数据时抛出AppSystemException异常。
	 * @author Tiger.Wang
	 * @date 2019/9/5
	 * @param etlSysCd	调度系统编号
	 * @param etlJob	调度作业标识
	 * @param currBathDate	当前跑批日期
	 * @return hrds.entity.Etl_job
	 */
	public static Etl_job_cur getEtlJob(String etlSysCd, String etlJob, String currBathDate) {

		try(DatabaseWrapper db = TaskSqlHelper.getDbConnector()) {

			Optional<Etl_job_cur> etlJobCur = SqlOperator.queryOneObject(db, Etl_job_cur.class,
					"SELECT * FROM etl_job_cur WHERE etl_sys_cd = ? AND etl_job = ? AND curr_bath_date = ?",
					etlSysCd, etlJob, currBathDate);

			if(!etlJobCur.isPresent()) {
				throw new AppSystemException("根据调度系统编号、调度作业标识、当前跑批日期获取调度作业信息失败 "
						+ etlSysCd);
			}

			return etlJobCur.get();
		}
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

		try(DatabaseWrapper db = TaskSqlHelper.getDbConnector()) {

			Optional<Etl_job_cur> etlJobCur = SqlOperator.queryOneObject(db, Etl_job_cur.class,
					"SELECT * FROM etl_job_cur WHERE etl_sys_cd = ? AND etl_job = ?", etlSysCd, etlJob);

			if(!etlJobCur.isPresent()) {
				throw new AppSystemException("根据调度系统编号、调度作业标识获取调度作业信息失败"
						+ etlSysCd);
			}

			return etlJobCur.get();
		}
	}

	/**
	 * 根据调度系统编号获取系统资源信息。注意，若无法查询出数据则抛出AppSystemException异常。
	 * @author Tiger.Wang
	 * @date 2019/9/5
	 * @param etlSysCd	调度系统编号
	 * @return java.util.List<hrds.commons.entity.Etl_resource>	系统资源信息集合
	 */
	public static List<Etl_resource> getEtlSystemResources(String etlSysCd) {

		try(DatabaseWrapper db = TaskSqlHelper.getDbConnector()) {

			List<Etl_resource> etlResources = SqlOperator.queryList(db, Etl_resource.class,
					"SELECT * FROM etl_resource WHERE etl_sys_cd = ?", etlSysCd);

			if(null == etlResources || etlResources.size() == 0) {
				throw new AppSystemException("根据调度系统编号获取系统资源信息失败" + etlSysCd);
			}

			return etlResources;
		}
	}

	/**
	 * 根据调度系统编号来更新[资源使用数]。注意，若更新数据影响数据行数小于1，则抛出AppSystemException异常。
	 * @author Tiger.Wang
	 * @date 2019/9/4
	 * @param etlSysCd	调度系统编号
	 * @param used	资源使用数
	 */
	public static void updateEtlResourceUsed(String etlSysCd, int used) {

		try(DatabaseWrapper db = TaskSqlHelper.getDbConnector()) {

			int num = SqlOperator.execute(db, "UPDATE etl_resource SET resource_used = ? " +
					"WHERE etl_sys_cd = ?", used, etlSysCd);

			if(num < 1) {
				throw new AppSystemException("根据调度系统编号来更新[资源使用数]失败" + etlSysCd);
			}

			SqlOperator.commitTransaction(db);
		}
	}

	/**
	 * 根据调度系统编号、资源类型修改[已使用资源]为指定数。
	 * 注意，若更新数据影响数据行数小于1，则抛出AppSystemException异常。
	 * @author Tiger.Wang
	 * @date 2019/9/5
	 * @param etlSysCd	调度系统编号
	 * @param resourceType	资源类型
	 * @param used	已使用资源数
	 */
	public static void updateEtlResourceUsedByResourceType(String etlSysCd, String resourceType, int used) {

		try(DatabaseWrapper db = TaskSqlHelper.getDbConnector()) {

			int num = SqlOperator.execute(db, "UPDATE etl_resource SET resource_used = ? " +
					"WHERE etl_sys_cd = ? AND resource_type = ?", used, etlSysCd, resourceType);

			if(num < 1) {
				throw new AppSystemException("据调度系统编号、资源类型修改[已使用资源]失败 " + etlSysCd);
			}

			SqlOperator.commitTransaction(db);
		}
	}

	/**
	 * 根据调度系统编号获取调度作业干预信息
	 * @author Tiger.Wang
	 * @date 2019/9/6
	 * @param etlSyscd  调度系统编号
	 * @return java.util.List<hrds.commons.entity.Etl_job_hand>
	 */
	public static List<Etl_job_hand> getEtlJobHands(String etlSyscd) {

		try(DatabaseWrapper db = TaskSqlHelper.getDbConnector()) {

			return SqlOperator.queryList(db, Etl_job_hand.class, "SELECT * FROM etl_job_hand WHERE " +
					"hand_status = ? AND etl_sys_cd = ?", Meddle_status.TRUE.getCode(), etlSyscd);
		}
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

		try(DatabaseWrapper db = TaskSqlHelper.getDbConnector()) {

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
	}

	/**
	 * 在调度作业干预历史表中新增一条数据（etl_job_hand_his）。注意，若新增数据失败则抛出AppSystemException异常。
	 * @author Tiger.Wang
	 * @date 2019/9/9
	 * @param etlJobHandHis    Etl_job_hand对象
	 */
	public static void insertIntoEtlJobHandleHistory(Etl_job_hand_his etlJobHandHis) {

		try(DatabaseWrapper db = TaskSqlHelper.getDbConnector()) {

			if(etlJobHandHis.add(db) != 1) {
				throw new AppSystemException("新增干预历史（etl_job_hand_his）失败" + etlJobHandHis.getEtl_job());
			}

			SqlOperator.commitTransaction(db);
		}
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

		try(DatabaseWrapper db = TaskSqlHelper.getDbConnector()) {

			SqlOperator.execute(db, "DELETE FROM etl_job_hand WHERE etl_sys_cd = ? " +
							"AND etl_job = ? AND etl_hand_type = ?",
					etlSysCd, etlJob, etlHandType);

			SqlOperator.commitTransaction(db);
		}
	}

	/**
	 * 根据调度系统编号，获取需要调度的作业（作业状态为挂起或者等待）
	 * @author Tiger.Wang
	 * @date 2019/9/9
	 * @param etlSysCd  调度系统编号
	 * @return java.util.List<hrds.commons.entity.Etl_job>
	 */
	public static List<Etl_job_cur> getReadyEtlJobs(String etlSysCd) {

		try(DatabaseWrapper db = TaskSqlHelper.getDbConnector()) {

			return SqlOperator.queryList(db, Etl_job_cur.class, "SELECT * FROM etl_job_cur WHERE " +
					"(job_disp_status = ? OR job_disp_status = ?) AND etl_sys_cd = ?",
					Job_Status.PENDING.getCode(), Job_Status.WAITING.getCode(), etlSysCd);
		}
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

		try(DatabaseWrapper db = TaskSqlHelper.getDbConnector()) {

			return SqlOperator.queryList(db, Etl_job_cur.class, "SELECT * FROM etl_job_cur WHERE " +
							"etl_sys_cd = ? AND job_disp_status = ?", etlSysCd, jobStatus);
		}
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

		try(DatabaseWrapper db = TaskSqlHelper.getDbConnector()) {

			int num = SqlOperator.execute(db, "UPDATE etl_job_cur SET job_disp_status = ?, main_serv_sync = ? " +
					"job_priority_curr = job_priority WHERE etl_sys_cd = ? AND today_disp = ?",
					Job_Status.PENDING.getCode(), Main_Server_Sync.YES.getCode(), etlSysCd,
					Today_Dispatch_Flag.YES.getCode());

			if(num < 1) {
				throw new AppSystemException("根据调度系统编号，将该系统下的作业置为挂起状态失败" + etlSysCd);
			}

			SqlOperator.commitTransaction(db);
		}
	}

	/**
	 * 根据调度系统编号，将作业状态在[停止]以及[错误]下的作业修改为指定状态。
	 * @author Tiger.Wang
	 * @date 2019/9/10
	 * @param etlSysCd  调度系统编号
	 * @param jobStatus 调度作业状态
	 */
	public static void updateEtlJobToPendingInResume(String etlSysCd, String jobStatus) {

		try(DatabaseWrapper db = TaskSqlHelper.getDbConnector()) {

			SqlOperator.execute(db, "UPDATE etl_job_cur SET job_disp_status = ?, main_serv_sync = ? " +
							"job_priority_curr = job_priority WHERE (job_disp_status = ? or job_disp_status = ?) " +
							"AND etl_sys_cd = ?", jobStatus, Main_Server_Sync.YES.getCode(),
					Job_Status.STOP.getCode(), Job_Status.ERROR.getCode(), etlSysCd);

			SqlOperator.commitTransaction(db);
		}
	}

	/**
	 * 根据调度系统编号、调度作业编号、当前跑批日期修改指定的当前作业优先级。注意，此处仅会最多修改一条数据。
	 * 若修改的数据影响的数据行数不为1，则抛出AppSystemException异常。
	 * @author Tiger.Wang
	 * @date 2019/9/10
	 * @param jobPriorityCurr   当前作业优先级
	 * @param etlSysCd  调度系统编号
	 * @param etlJob    调度作业标识
	 * @param currBathDate  当前跑批日期
	 */
	public static void updateEtlJobCurrPriority(int jobPriorityCurr, String etlSysCd, String etlJob,
	                                               String currBathDate) {

		try(DatabaseWrapper db = TaskSqlHelper.getDbConnector()) {

			int num = SqlOperator.execute(db, "UPDATE etl_job_cur SET job_priority_curr = ? " +
							"WHERE etl_sys_cd = ? AND etl_job = ? AND curr_bath_date = ?",
					jobPriorityCurr, etlSysCd, etlJob, currBathDate);

			if(num != 1) {
				throw new AppSystemException("根据调度系统编号、调度作业编号、当前跑批日期修改当前作业优先级失败"
						+ etlJob);
			}

			SqlOperator.commitTransaction(db);
		}
	}

	/**
	 * 在etl_job_hand表中新增一条数据。注意，此方法会根据传入的对象所携带的参数来新增数据。
	 * 若新增数据失败，则抛出AppSystemException异常。
	 * @author Tiger.Wang
	 * @date 2019/9/11
	 * @param jobHand   Etl_job_hand对象，表示一个作业干预信息
	 */
	public static void insertIntoEtlJobHand(Etl_job_hand jobHand) {

		try(DatabaseWrapper db = TaskSqlHelper.getDbConnector()) {

			if(jobHand.add(db) != 1) {
				throw new AppSystemException("在etl_job_hand表中新增一条数据失败" + jobHand.getEtl_job());
			}

			SqlOperator.commitTransaction(db);
		}
	}

	/**
	 * 在etl_job_disp_his表中新增一条数据。注意，此方法会根据传入的对象所携带的参数来新增数据。
	 * 若新增数据失败，则抛出AppSystemException异常。
	 * @author Tiger.Wang
	 * @date 2019/9/16
	 * @param etlJobDispHis Etl_job_disp_his对象，表示跑批历史
	 */
	public static void insertIntoEtlJobDispHis(Etl_job_disp_his etlJobDispHis) {

		try(DatabaseWrapper db = TaskSqlHelper.getDbConnector()) {

			if(etlJobDispHis.add(db) != 1) {
				throw new AppSystemException("在etl_job_disp_his表中新增一条数据失败" + etlJobDispHis.getEtl_job());
			}

			SqlOperator.commitTransaction(db);
		}
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

		try(DatabaseWrapper db = TaskSqlHelper.getDbConnector()) {

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
	}

	/**
	 * 根据参数标识查询参数信息。注意，该方法主要用于验证所使用的参数是否正确。
	 * 在无法查询出时间时会抛出AppSystemException异常。
	 * @author Tiger.Wang
	 * @date 2019/9/18
	 * @param para  所使用的参数
	 * @return hrds.commons.entity.Etl_para
	 */
	public static Etl_para getParaByPara(String para) {

		try(DatabaseWrapper db = TaskSqlHelper.getDbConnector()) {

			Optional<Etl_para> etlPara = SqlOperator.queryOneObject(db, Etl_para.class,
					"SELECT * FROM etl_para WHERE para_cd = ?", para);

			if(!etlPara.isPresent()) {
				throw new AppSystemException("所使用的参数标识不存在" + para);
			}

			return etlPara.get();
		}
	}
}
