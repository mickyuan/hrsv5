package hrds.control.task.helper;

import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.control.beans.EtlJobDefBean;

import java.util.List;
import java.util.Optional;

/**
 * ClassName: TaskSqlHelper
 * Description: 所有跟任务/作业有关的SQL查询，都使用此类
 * Author: Tiger.Wang
 * Date: 2019/9/2 17:43
 * Since: JDK 1.8
 **/
public class TaskSqlHelper {

	private TaskSqlHelper() {}

	/**
	 * 根据调度系统编号获取调度系统信息
	 * @author Tiger.Wang
	 * @date 2019/9/2
	 * @param etlSysCd	调度系统编号
	 * @return java.util.Optional<hrds.entity.Etl_sys>
	 */
	public static Optional<Etl_sys> getEltSysBySysCode(String etlSysCd) {

		try(DatabaseWrapper db = new DatabaseWrapper()) {

			return SqlOperator.queryOneObject(db, Etl_sys.class,
							"SELECT * FROM etl_sys WHERE etl_sys_cd = ?", etlSysCd);
		}
	}

	/**
	 * 根据调度系统编号、调度作业名获取该作业需要的资源
	 * @author Tiger.Wang
	 * @date 2019/9/2
	 * @param etlSysCd	调度系统编号
	 * @param etlJob	调度作业名（调度作业标识）
	 * @return java.util.List<hrds.entity.Etl_job_resource_rela>	作业资源数组
	 */
	public static List<Etl_job_resource_rela> getJobNeedResources(String etlSysCd, String etlJob) {

		try(DatabaseWrapper db = new DatabaseWrapper()) {

			return SqlOperator.queryList(db, Etl_job_resource_rela.class,
							"SELECT * FROM etl_job_resource_rela WHERE etl_sys_cd = ? AND etl_job = ?",
					etlSysCd, etlJob);
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

		try(DatabaseWrapper db = new DatabaseWrapper()) {

			return SqlOperator.queryList(db, EtlJobDefBean.class,
							"SELECT * FROM etl_job_def WHERE etl_sys_cd = ? AND job_eff_flag != ?",
					etlSysCd, Job_Effective_Flag.NO.getCode());
		}
	}

	/**
	 * 根据调度系统编号、调度作业名获取作业定义信息
	 * @author Tiger.Wang
	 * @date 2019/9/2
	 * @param etlSysCd	调度系统编号
	 * @param etlJob	调度作业名（调度作业标识）
	 * @return java.util.List<hrds.control.beans.EtlJobBean>
	 */
	public static List<Etl_job_def> getDefJobByJobId(String etlSysCd, String etlJob) {

		try(DatabaseWrapper db = new DatabaseWrapper()) {

			return SqlOperator.queryList(db, Etl_job_def.class,
							"SELECT * FROM etl_job_def WHERE etl_sys_cd = ? AND etl_job = ?",
					etlSysCd, etlJob);
		}
	}

	/**
	 * 根据调度系统编号获取作业依赖信息
	 * @author Tiger.Wang
	 * @date 2019/9/3
	 * @param etlSysCd	调度系统编号
	 * @return java.util.List<hrds.entity.Etl_dependency>
	 */
	public static List<Etl_dependency> getJobDependencyBySysCode(String etlSysCd) {

		try(DatabaseWrapper db = new DatabaseWrapper()) {

			return SqlOperator.queryList(db, Etl_dependency.class,
							"SELECT * FROM etl_dependency WHERE etl_sys_cd = ? AND status = ?",
					etlSysCd, Status.TRUE.getCode());
		}
	}

	/**
	 * 根据调度系统编号修改调度系统跑批日期。注意，此处会将[主服务器同步标志]设置为同步。
	 * @author Tiger.Wang
	 * @date 2019/9/3
	 * @param etlSysCd	调度系统编号
	 * @param currBathDate	当前跑批日期
	 * @return boolean	是否有数据被修改
	 */
	public static boolean updateEtlSysBathDate(String etlSysCd, String currBathDate) {

		try(DatabaseWrapper db = new DatabaseWrapper()) {

			int num = SqlOperator.execute(db, "UPDATE etl_sys SET main_serv_sync = ?, curr_bath_date = ? " +
						"WHERE etl_sys_cd = ?", Main_Server_Sync.YES.getCode(), currBathDate, etlSysCd);

			return num == 1;
		}
	}

	/**
	 * 根据调度系统编号修改调度系统运行状态。
	 * @author Tiger.Wang
	 * @date 2019/9/4
	 * @param etlSysCd	调度系统编号
	 * @param runStatus	系统运行状态
	 * @return boolean	是否有数据被修改
	 */
	public static boolean updateEtlSysRunStatus(String etlSysCd, String runStatus) {

		try(DatabaseWrapper db = new DatabaseWrapper()) {

			int num = SqlOperator.execute(db, "UPDATE etl_sys SET main_serv_sync = ?, sys_run_status = ? " +
					"WHERE etl_sys_cd = ?", Main_Server_Sync.YES.getCode(), runStatus, etlSysCd);

			return num == 1;
		}
	}

	/**
	 * 根据调度系统编号修改调度系统运行状态及当前跑批日期。
	 * @author Tiger.Wang
	 * @date 2019/9/4
	 * @param etlSysCd	调度系统编号
	 * @param strBathDate	当前跑批日期（yyyy-MM-dd）
	 * @param runStatus	运行状态
	 * @return boolean	是否有数据被修改
	 */
	public static boolean updateEtlSysRunStatusAndBathDate(String etlSysCd, String strBathDate, String runStatus) {

		try(DatabaseWrapper db = new DatabaseWrapper()) {

			int num = SqlOperator.execute(db, "UPDATE etl_sys SET main_serv_sync = ?, curr_bath_date = ?" +
					", sys_run_status = ? WHERE etl_sys_cd = ?",
					Main_Server_Sync.YES.getCode(), strBathDate, runStatus, etlSysCd);

			return num == 1;
		}
	}

	/**
	 * 根据调度系统编号、当前批量日期删除作业信息。注意，该方法不会删除作业类型为T+0且按频率调度的作业。
	 * @author Tiger.Wang
	 * @date 2019/9/3
	 * @param etlSysCd	调度系统编号
	 * @param currBathDate	当前跑批日期
	 * @return boolean	是否有数据被删除
	 */
	public static boolean deleteEtlSysByBathDate(String etlSysCd, String currBathDate) {

		try(DatabaseWrapper db = new DatabaseWrapper()) {
			//TODO 此处较原版改动：不再判断frequnecy来决定是否删除作业类型为T+0且按频率调度的作业，
			// 因为该类型作业在整体流程逻辑上来说永远都不会删除
			int num = SqlOperator.execute(db, "DELETE FROM etl_job WHERE etl_sys_cd = ? " +
					"AND curr_bath_date = ? AND disp_type != ? AND disp_freq != ?",
					etlSysCd, currBathDate, Dispatch_Type.ZTIMING.getCode(), Dispatch_Frequency.PinLv.getCode());

			return num > 0;
		}
	}

	/**
	 * 根据调度系统编号删除作业信息。
	 * @author Tiger.Wang
	 * @date 2019/9/4
	 * @param etlSysCd	作业编号
	 * @return boolean	是否有数据被删除
	 */
	public static boolean deleteEtlSysBySysCode(String etlSysCd) {

		try(DatabaseWrapper db = new DatabaseWrapper()) {
			//TODO 此处较原版改动：不再判断frequnecy来决定是否删除作业类型为T+0且按频率调度的作业，
			// 因为该类型作业在整体流程逻辑上来说永远都要删除
			int num = SqlOperator.execute(db, "DELETE FROM etl_job WHERE etl_sys_cd = ? ",
					etlSysCd);

			return num > 0;
		}
	}

	/**
	 * 根据系统编号与参数编号查询参数值
	 * @author Tiger.Wang
	 * @date 2019/9/4
	 * @param eltSysCd	系统编号
	 * @param paraCd	参数编号
	 * @return java.util.Optional<hrds.entity.Etl_para>
	 */
	public static Optional<Etl_para> getEtlParameterVal(String eltSysCd, String paraCd) {

		try(DatabaseWrapper db = new DatabaseWrapper()) {

			return SqlOperator.queryOneObject(db, Etl_para.class,
					"SELECT para_val FROM etl_para WHERE etl_sys_cd = ? AND para_cd = ?",
					eltSysCd, paraCd);
		}
	}

	/**
	 * 在etl_job表中新增一条数据
	 * @author Tiger.Wang
	 * @date 2019/9/4
	 * @param job	Etl_job对象，对应etl_job表
	 * @return boolean	是否有数据被新增
	 */
	public static boolean insertIntoJobTable(Etl_job job) {

		try(DatabaseWrapper db = new DatabaseWrapper()) {

			return job.add(db) > 0;
		}
	}

	/**
	 * 根据调度系统编号、当前批量日期以及指定调度状态修改作业的信息。
	 * 注意，此处会将[主服务器同步标志]设置为同步。
	 * @author Tiger.Wang
	 * @date 2019/9/4
	 * @param dispStatus	调度状态
	 * @param etlSysCd		调度系统编号
	 * @param currBathDate	当前批量日期
	 * @return boolean	是否有数据被修改
	 */
	public static boolean updateEtjJobWithDispStatus(String dispStatus, String etlSysCd, String currBathDate) {

		try(DatabaseWrapper db = new DatabaseWrapper()) {

			int num = SqlOperator.execute(db, "UPDATE etl_job SET job_disp_status = ?, main_serv_sync = ? " +
					"WHERE etl_sys_cd = ? AND curr_bath_date = ?",
					dispStatus, Main_Server_Sync.YES.getCode(), etlSysCd, currBathDate);

			return num > 0;
		}
	}

	/**
	 * 根据调度系统编号、调度作业标识、当前批量日期修改作业调度状态的作业信息
	 * @author Tiger.Wang
	 * @date 2019/9/5
	 * @param dispStatus	作业调度状态
	 * @param etlSysCd		调度系统编号
	 * @param etlJob		调度作业标识
	 * @param currBathDate	当前批量日期
	 * @return boolean	是否有数据被修改
	 */
	public static boolean updateEtlJobDispStatus(String dispStatus, String etlSysCd, String etlJob, String currBathDate) {

		try(DatabaseWrapper db = new DatabaseWrapper()) {

			int num = SqlOperator.execute(db, "UPDATE etl_job SET job_disp_status = ? " +
							"WHERE etl_sys_cd = ? AND etl_job = ? AND curr_bath_date = ?",
					dispStatus, etlSysCd, etlJob, currBathDate);

			return num > 0;
		}
	}

	/**
	 * 根据调度系统编号、调度作业标识，来修改当前执行时间
	 * @author Tiger.Wang
	 * @date 2019/9/5
	 * @param currStTime	开始执行时间（yyyy-MM-dd HH:mm:ss）
	 * @param etlSysCd	调度系统编号
	 * @param etlJob	调度作业标识
	 * @return boolean	是否有数据被修改
	 */
	public static boolean updateEtlJobRunTime(String currStTime, String etlSysCd, String etlJob) {

		try(DatabaseWrapper db = new DatabaseWrapper()) {

			int num = SqlOperator.execute(db, "UPDATE etl_job SET curr_st_time = ?, main_serv_sync = ? " +
							"WHERE etl_sys_cd = ? AND etl_job = ?",
					currStTime, Main_Server_Sync.YES.getCode(), etlSysCd, etlJob);

			return num > 0;
		}
	}

	/**
	 * 根据调度系统编号、调度作业标识、当前跑批日期修改调度作业信息
	 * @author Tiger.Wang
	 * @date 2019/9/5
	 * @param etlSysCd	调度系统编号
	 * @param eltJob	调度作业标识
	 * @param currBathDate	当前跑批日期
	 * @param currStTime	当前执行开始日期时间（yyyy-MM-dd HH:mm:ss）
	 * @param currEndTime	当前执行结束日期时间（yyyy-MM-dd HH:mm:ss）
	 * @return boolean	是否有数据被修改
	 */
	public static boolean updateVirtualJob(String etlSysCd, String eltJob, String currBathDate,
										   String currStTime, String currEndTime) {

		try(DatabaseWrapper db = new DatabaseWrapper()) {

			int num = SqlOperator.execute(db, "UPDATE etl_job SET main_serv_sync = ?, job_disp_status = ?, " +
							"curr_st_time = ?, curr_end_time = ? WHERE etl_sys_cd = ? AND etl_job = ? " +
							"AND curr_bath_date = ?", Main_Server_Sync.YES.getCode(), Job_Status.DONE.getCode(),
					currStTime, currEndTime, etlSysCd, eltJob, currBathDate);

			return num > 0;
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
	 * @return boolean	是否有数据被修改
	 */
	public static boolean updateEtjJobByResumeRun(String etlSysCd, String currBathDate) {

		try(DatabaseWrapper db = new DatabaseWrapper()) {

			int num = SqlOperator.execute(db, "UPDATE etl_job SET job_disp_status = ?, main_serv_sync = ? WHERE " +
							"etl_sys_cd = ? AND curr_bath_date = ? AND job_disp_status NOT IN (?, ?) AND today_disp = ?",
					Job_Status.PENDING.getCode(), Main_Server_Sync.YES.getCode(), etlSysCd, currBathDate,
					Job_Status.PENDING.getCode(), Job_Status.DONE.getCode(), Today_Dispatch_Flag.YES.getCode());

			return num > 0;
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
	public static List<Etl_job> getEtlJobs(String etlSysCd, String currBathDate) {

		try(DatabaseWrapper db = new DatabaseWrapper()) {

			return SqlOperator.queryList(db, Etl_job.class,
					"SELECT * FROM etl_job WHERE etl_sys_cd = ? AND curr_bath_date <= ?",
					etlSysCd, currBathDate);
		}
	}

	/**
	 * 根据调度系统编号、调度作业标识、当前跑批日期获取调度作业信息。
	 * @author Tiger.Wang
	 * @date 2019/9/5
	 * @param etlSysCd	调度系统编号
	 * @param etlJob	调度作业标识
	 * @param currBathDate	当前跑批日期
	 * @return java.util.Optional<hrds.entity.Etl_job>
	 */
	public static Optional<Etl_job> getEtlJob(String etlSysCd, String etlJob, String currBathDate) {

		try(DatabaseWrapper db = new DatabaseWrapper()) {

			return SqlOperator.queryOneObject(db, Etl_job.class, "SELECT * FROM etl_job WHERE etl_sys_cd = ? AND " +
					"etl_job = ? AND curr_bath_date = ?", etlSysCd, etlJob, currBathDate);
		}
	}

	/**
	 * 根据调度系统编号获取系统资源信息
	 * @author Tiger.Wang
	 * @date 2019/9/5
	 * @param etlSysCd	调度系统编号
	 * @return java.util.List<hrds.commons.entity.Etl_resource>	系统资源信息集合
	 */
	public static List<Etl_resource> getEtlSystemResources(String etlSysCd) {

		try(DatabaseWrapper db = new DatabaseWrapper()) {

			return SqlOperator.queryList(db, Etl_resource.class,
					"SELECT * FROM etl_resource WHERE etl_sys_cd = ?", etlSysCd);
		}
	}

	/**
	 * 根据调度系统编号来更新[资源使用数]
	 * @author Tiger.Wang
	 * @date 2019/9/4
	 * @param etlSysCd	调度系统编号
	 * @param used	资源使用数
	 * @return boolean	是否有数据被修改
	 */
	public static boolean updateEtlResourceUsed(String etlSysCd, int used) {

		try(DatabaseWrapper db = new DatabaseWrapper()) {

			int num = SqlOperator.execute(db, "UPDATE etl_resource SET resource_used = ? " +
					"WHERE etl_sys_cd = ?", used, etlSysCd);

			return num > 0;
		}
	}

	/**
	 * 根据调度系统编号、资源类型修改[已使用资源]为指定数
	 * @author Tiger.Wang
	 * @date 2019/9/5
	 * @param etlSysCd	调度系统编号
	 * @param resourceType	资源类型
	 * @param used	已使用资源数
	 * @return boolean	是否有数据被修改
	 */
	public static boolean updateEtlResourceUsedByResourceType(String etlSysCd, String resourceType, int used){

		try(DatabaseWrapper db = new DatabaseWrapper()) {

			int num = SqlOperator.execute(db, "UPDATE etl_resource SET resource_used = ? " +
					"WHERE etl_sys_cd = ? AND resource_type = ?", used, resourceType, etlSysCd);

			return num > 0;
		}
	}
}
