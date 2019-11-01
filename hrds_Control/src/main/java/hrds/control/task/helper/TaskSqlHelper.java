package hrds.control.task.helper;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.commons.exception.AppSystemException;
import hrds.control.beans.EtlJobBean;
import hrds.control.beans.EtlJobDefBean;

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
	 * 用于获取单例的db连接。<br>
	 * 1.构造DatabaseWrapper实例。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @return fd.ng.db.jdbc.DatabaseWrapper
	 *          含义：标示一个数据库连接。
	 *          取值范围：不会为null。
	 */
	private static DatabaseWrapper getDbConnector() {

		//1.构造DatabaseWrapper实例。
		DatabaseWrapper db = _dbBox.get();
		if(db == null) {
			db = new DatabaseWrapper();
			_dbBox.set(db);
		}

		return db;
	}

	/**
	 * 用于关闭db连接。<br>
	 * 1.关闭DatabaseWrapper实例。
	 * @author Tiger.Wang
	 * @date 2019/9/23
	 */
	public static void closeDbConnector() {

		DatabaseWrapper db = TaskSqlHelper.getDbConnector();
		SqlOperator.rollbackTransaction(db);
		db.close();
		_dbBox.remove();
		logger.info("-------------- 调度服务DB连接已经关闭 --------------");
	}

	/**
	 * 根据调度系统编号获取调度系统信息。注意，
	 * 该方法若无法查询到数据，则抛出AppSystemException异常。<br>
	 * 1.数据库查询，并转换为实体。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param etlSysCd
	 *          含义：调度系统编号。
	 *          取值范围：不能为null。
	 * @return hrds.commons.entity.Etl_sys
	 *          含义：标示一个调度系统。
	 *          取值范围：不会为null。
	 */
	public static Etl_sys getEltSysBySysCode(String etlSysCd) {

		//1.数据库查询，并转换为实体。
		Object[] row = SqlOperator.queryArray(
				TaskSqlHelper.getDbConnector(), "SELECT etl_sys_cd, etl_sys_name, " +
						"etl_serv_ip, etl_serv_port, contact_person, contact_phone, comments, " +
						"curr_bath_date, bath_shift_time, main_serv_sync, sys_run_status, " +
						"user_name, user_pwd, serv_file_path, remarks FROM " + Etl_sys.TableName +
						" WHERE etl_sys_cd = ?", etlSysCd);

		if(0 == row.length) {
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
	 * 根据调度系统编号、调度作业名获取该作业需要的资源。注意，
	 * 该方法若无法查询到数据，则抛出AppSystemException异常。<br>
	 * 1.查询数据库获取数据。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param etlSysCd
	 *          含义：调度系统编号。
	 *          取值范围：不能为null。
	 * @param etlJob
	 *          含义：作业标识。
	 *          取值范围：不能为null。
	 * @return java.util.List<hrds.commons.entity.Etl_job_resource_rela>
	 *          含义：作业与资源关联关系集合。
	 *          取值范围：不会为null。
	 */
	public static List<Etl_job_resource_rela> getJobNeedResources(String etlSysCd, String etlJob) {

		//1.查询数据库获取数据。
		List<Etl_job_resource_rela> jobNeedResources
				= SqlOperator.queryList(TaskSqlHelper.getDbConnector(), Etl_job_resource_rela.class,
				"SELECT * FROM " + Etl_job_resource_rela.TableName +
						" WHERE etl_sys_cd = ? AND etl_job = ?", etlSysCd, etlJob);
		//TODO 为作业配置资源后才认为该作业有效，否则报错
		if(null == jobNeedResources || jobNeedResources.size() == 0) {
			throw new AppSystemException("根据调度系统编号、调度作业名获取不到该作业需要的资源 "
					+ etlJob);
		}

		return jobNeedResources;
	}

	/**
	 * 根据调度系统编号获取作业定义信息，无效作业不会被查询命中。注意，
	 * 此处不返回Etl_job_def，而返回EtlJobBean。EtlJobBean重载了前者，增加了设置资源的功能。<br>
	 * 1.查询数据库获取数据。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param etlSysCd
	 *          含义：调度系统编号。
	 *          取值范围：不能为null。
	 * @return java.util.List<hrds.control.beans.EtlJobDefBean>
	 *          含义：作业信息数组（来自作业定义表）。
	 *          取值范围：不会为null。
	 */
	public static List<EtlJobDefBean> getAllDefJob(String etlSysCd) {

		//1.查询数据库获取数据。
		return SqlOperator.queryList(TaskSqlHelper.getDbConnector(), EtlJobDefBean.class,
						"SELECT * FROM " + Etl_job_def.TableName +
								" WHERE etl_sys_cd = ? AND job_eff_flag != ?",
				etlSysCd, Job_Effective_Flag.NO.getCode());
	}

	/**
	 * 根据调度系统编号获取作业依赖信息。<br>
	 * 1.查询数据库获取数据。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param etlSysCd
	 *          含义：调度系统编号。
	 *          取值范围：不能为null。
	 * @return java.util.List<hrds.commons.entity.Etl_dependency>
	 *          含义：作业依赖集合。
	 *          取值范围：不会为null。
	 */
	public static List<Etl_dependency> getJobDependencyBySysCode(String etlSysCd) {

		//1.查询数据库获取数据。
		return SqlOperator.queryList(TaskSqlHelper.getDbConnector(), Etl_dependency.class,
						"SELECT * FROM " + Etl_dependency.TableName +
								" WHERE etl_sys_cd = ? AND status = ?",
				etlSysCd, Status.TRUE.getCode());
	}

	/**
	 * 根据调度系统编号修改调度系统跑批日期。注意，此处会将[主服务器同步标志]设置为同步。
	 * 若该方法修改数据失败（影响数据行数不为1），则抛出AppSystemException异常。<br>
	 * 1.更新数据库表数据。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param etlSysCd
	 *          含义：调度系统编号。
	 *          取值范围：不能为null。
	 * @param currBathDate
	 *          含义：当前跑批日期。
	 *          取值范围：yyyyMMdd格式字符串，不能为null。
	 */
	public static void updateEtlSysBathDate(String etlSysCd, String currBathDate) {

		//1.更新数据库表数据。
		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		int num = SqlOperator.execute(db, "UPDATE " + Etl_sys.TableName +
						" SET main_serv_sync = ?, curr_bath_date = ? WHERE etl_sys_cd = ?",
				Main_Server_Sync.YES.getCode(), currBathDate, etlSysCd);

		if(num != 1) {
			SqlOperator.rollbackTransaction(db);
			throw new AppSystemException("根据调度系统编号修改调度系统跑批日期失败" + etlSysCd);
		}

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号修改调度系统运行状态。注意，此处会将[主服务器同步标志]设置为同步。
	 * 若该方法修改数据失败（影响数据行数不为1），则抛出AppSystemException异常。<br>
	 * 1.更新数据库表数据。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param etlSysCd
	 *          含义：调度系统编号。
	 *          取值范围：不能为null。
	 * @param runStatus
	 *          含义：系统运行状态。
	 *          取值范围：Job_Status枚举值，不能为null。
	 */
	public static void updateEtlSysRunStatus(String etlSysCd, String runStatus) {

		//1.更新数据库表数据。
		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		int num = SqlOperator.execute(db, "UPDATE " + Etl_sys.TableName +
						" SET main_serv_sync = ?, sys_run_status = ? WHERE etl_sys_cd = ?",
				Main_Server_Sync.YES.getCode(), runStatus, etlSysCd);

		if(num != 1) {
			//FIXME 这里要执行回滚！
			// 另外，所有抛出Runtime异常的情况，都会导致程序退出，那么，DB连接是如何关闭的？
			// 将会在ControlManageServer类中设置顶级捕捉异常，在异常处理中关闭db连接，
			// 关闭连接的方法也将会回滚
			throw new AppSystemException("根据调度系统编号修改调度系统跑批日期失败" + etlSysCd);
		}

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号，修改准备运行的作业（调度状态为挂起、等待）的作业状态到指定作业状态。
	 * 注意，此方法只修改在[挂起]和[等待]状态下的作业，并且限制[主服务器同步标识]为[是]。<br>
	 * 1.更新数据库表数据。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param etlSysCd
	 *          含义：调度系统编号。
	 *          取值范围：不能为null。
	 * @param runStatus
	 *          含义：作业状态。
	 *          取值范围：Job_Status枚举值，不能为null。
	 */
	public static void updateReadyEtlJobStatus(String etlSysCd, String runStatus) {

		//1.更新数据库表数据。
		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		//FIXME 这个更新不需要判断结果吗？ 下面的很多方法都没有判断结果
		// 因为这个程序是动态的，在作业运行的过程中在有些情况下没法知道当时符合条件的作业是几个
		SqlOperator.execute(db, "UPDATE " + Etl_job_cur.TableName + " SET job_disp_status = ?, " +
						"main_serv_sync = ? WHERE (job_disp_status = ? or job_disp_status = ?) " +
						"AND etl_sys_cd = ?", runStatus, Main_Server_Sync.YES.getCode(),
				Job_Status.PENDING.getCode(), Job_Status.WAITING.getCode(), etlSysCd);

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号，修改准备运行的作业（调度状态为挂起、等待）的作业状态到指定作业状态。
	 * 注意，此方法只修改在[挂起]和[等待]状态下的作业，不限制[主服务器同步标识]。<br>
	 * 1.更新数据库表数据。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param etlSysCd
	 *          含义：调度系统编号。
	 *          取值范围：不能为null。
	 * @param dispStatus
	 *          含义：作业状态。
	 *          取值范围：Job_Status枚举值，不能为null。
	 */
	public static void updateReadyEtlJobsDispStatus(String etlSysCd, String dispStatus) {

		//1.更新数据库表数据。
		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		SqlOperator.execute(db, "UPDATE " + Etl_job_cur.TableName + " SET job_disp_status = ? " +
						"WHERE etl_sys_cd = ? AND (job_disp_status = ? OR job_disp_status = ?)",
				dispStatus, etlSysCd, Job_Status.PENDING.getCode(), Job_Status.WAITING.getCode());

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号修改调度系统运行状态及当前跑批日期。
	 * 若该方法修改数据失败（影响数据行数不为1），则抛出AppSystemException异常。<br>
	 * 1.更新数据库表数据。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param etlSysCd
	 *          含义：调度系统编号。
	 *          取值范围：不能为null。
	 * @param strBathDate
	 *          含义：当前跑批日期。
	 *          取值范围：yyyyMMdd格式字符串，不能为null。
	 * @param runStatus
	 *          含义：运行状态。
	 *          取值范围：Job_Status枚举值，不能为null。
	 */
	public static void updateEtlSysRunStatusAndBathDate(String etlSysCd, String strBathDate,
	                                                    String runStatus) {

		//1.更新数据库表数据。
		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		int num = SqlOperator.execute(db, "UPDATE " + Etl_sys.TableName +
						" SET main_serv_sync = ?, curr_bath_date = ?, sys_run_status = ?" +
						" WHERE etl_sys_cd = ?", Main_Server_Sync.YES.getCode(), strBathDate,
				runStatus, etlSysCd);

		if(num != 1) {
			throw new AppSystemException("根据调度系统编号修改调度系统运行状态及当前跑批日期失败 " +
					etlSysCd);
		}

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号、当前批量日期删除作业信息。
	 * 注意，该方法在系统运行中使用，不会删除作业类型为T+0且按频率调度的作业。<br>
	 * 1.更新数据库表数据。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param etlSysCd
	 *          含义：调度系统编号。
	 *          取值范围：不能为null。
	 * @param currBathDate
	 *          含义：当前跑批日期。
	 *          取值范围：yyyyMMdd格式字符串，不能为null。
	 */
	public static void deleteEtlJobByBathDate(String etlSysCd, String currBathDate) {

		//1.更新数据库表数据。
		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		SqlOperator.execute(db, "DELETE FROM " + Etl_job_cur.TableName + " WHERE etl_sys_cd = ? " +
						"AND curr_bath_date = ? AND disp_type != ? AND disp_freq != ?",
				etlSysCd, currBathDate, Dispatch_Type.TPLUS0.getCode(),
				Dispatch_Frequency.PinLv.getCode());

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号删除作业信息。注意，该方法在系统第一次运行的时候使用。<br>
	 * 1.更新数据库表数据。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param etlSysCd
	 *          含义：作业编号。
	 *          取值范围：不能为null。
	 */
	public static void deleteEtlJobBySysCode(String etlSysCd) {

		//1.更新数据库表数据。
		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		SqlOperator.execute(db, "DELETE FROM " + Etl_job_cur.TableName +
				" WHERE etl_sys_cd = ? ", etlSysCd);

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号、当前跑批日期、调度作业状态来删除etl_job表的信息。
	 * 注意，该方法不会删除调度触发方式为[频率]的作业。<br>
	 * 1.更新数据库表数据。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param etlSysCd
	 *          含义：调度系统编号。
	 *          取值范围：不能为null。
	 * @param currBathDate
	 *          含义：当前跑批日期。
	 *          取值范围：yyyyMMdd格式字符串，不能为null。
	 * @param jobStatus
	 *          含义：调度作业状态。
	 *          取值范围：Job_Status枚举值，不能为null。
	 */
	public static void deleteEtlJobWithoutFrequency(String etlSysCd, String currBathDate,
	                                                String jobStatus) {

		//1.更新数据库表数据。
		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		SqlOperator.execute(db, "DELETE FROM " + Etl_job_cur.TableName + " WHERE etl_sys_cd = ? " +
						"AND curr_bath_date = ? AND job_disp_status = ? AND disp_type != ?",
				etlSysCd, currBathDate, jobStatus, Dispatch_Frequency.PinLv.getCode());

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号、当前跑批日期、调度作业状态来删除etl_job表的信息。<br>
	 * 1.更新数据库表数据。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param etlSysCd
	 *          含义：调度系统编号。
	 *          取值范围：不能为null。
	 * @param currBathDate
	 *          含义：当前跑批日期。
	 *          取值范围：yyyyMMdd格式字符串，不能为null。
	 * @param jobStatus
	 *          含义：。
	 *          取值范围：Job_Status枚举值，不能为null。
	 */
	public static void deleteEtlJobByJobStatus(String etlSysCd, String currBathDate,
	                                           String jobStatus) {

		//1.更新数据库表数据。
		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		SqlOperator.execute(db, "DELETE FROM " + Etl_job_cur.TableName + " WHERE etl_sys_cd = ? " +
				"AND curr_bath_date = ? AND job_disp_status = ?",
				etlSysCd, currBathDate, jobStatus);

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据系统编号与参数编号查询参数值。注意，该方法若无法查询出数据，则抛出AppSystemException异常。<br>
	 * 1.查询数据库获取数据。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param eltSysCd
	 *          含义：系统编号。
	 *          取值范围：不能为null。
	 * @param paraCd
	 *          含义：参数编号。
	 *          取值范围：不能为null。
	 * @return java.lang.String
	 *          含义：数据库etl_para表的para_val字段数据。
	 *          取值范围：任意值。
	 */
	public static String getEtlParameterVal(String eltSysCd, String paraCd) {

		//1.查询数据库获取数据。
		Object[] row = SqlOperator.queryArray(TaskSqlHelper.getDbConnector(),
				"SELECT para_val FROM " + Etl_para.TableName +
						" WHERE etl_sys_cd = ? AND para_cd = ?", eltSysCd, paraCd);

		if(row.length == 0) {
			throw new AppSystemException(String.format("找不到对应的变量[%s]", paraCd));
		}

		return (String) row[0];
	}

	/**
	 * 在etl_job_cur表中新增一条数据。<br>
	 * 1.更新数据库表数据。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param job
	 *          含义：表示数据库etl_job_cur表的一条数据。
	 *          取值范围：对象中的关键属性不能为null。
	 */
	public static void insertIntoJobTable(Etl_job_cur job) {

		//1.更新数据库表数据。
		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		job.add(db);

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号、当前批量日期以及指定调度状态修改作业的信息。注意，
	 * 此处会将[主服务器同步标志]设置为同步。若修改数据影响的数据行数小于1，
	 * 则抛出AppSystemException异常。<br>
	 * 1.更新数据库表数据。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param dispStatus
	 *          含义：调度状态。
	 *          取值范围：Job_Status枚举值，不能为null。
	 * @param etlSysCd
	 *          含义：调度系统编号。
	 *          取值范围：不能为null。
	 * @param currBathDate
	 *          含义：当前批量日期。
	 *          取值范围：yyyyMMdd格式字符串，不能为null。
	 */
	public static void updateEtjJobWithDispStatus(String dispStatus, String etlSysCd,
	                                              String currBathDate) {

		//1.更新数据库表数据。
		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		int num = SqlOperator.execute(db, "UPDATE " + Etl_job_cur.TableName +
						" SET job_disp_status = ?, main_serv_sync = ? WHERE etl_sys_cd = ?" +
						" AND curr_bath_date = ?", dispStatus, Main_Server_Sync.YES.getCode(),
				etlSysCd, currBathDate);

		if(num < 1) {
			throw new AppSystemException("根据调度系统编号、当前批量日期修改作业信息失败" + etlSysCd);
		}

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号、调度作业标识、当前批量日期修改作业调度状态的作业信息。注意，
	 * 若修改数据影响的数据行数不为1，则抛出AppSystemException异常。<br>
	 * 1.更新数据库表数据。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param dispStatus
	 *          含义：作业调度状态。
	 *          取值范围：Job_Status枚举值，不能为null。
	 * @param etlSysCd
	 *          含义：调度系统编号。
	 *          取值范围：不能为null。
	 * @param etlJob
	 *          含义：调度作业标识。
	 *          取值范围：不能为null。
	 * @param currBathDate
	 *          含义：当前批量日期。
	 *          取值范围：yyyyMMdd格式字符串，不能为null。
	 */
	public static void updateEtlJobDispStatus(String dispStatus, String etlSysCd, String etlJob,
	                                             String currBathDate) {

		//1.更新数据库表数据。
		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		int num = SqlOperator.execute(db, "UPDATE " + Etl_job_cur.TableName +
				" SET job_disp_status = ? WHERE etl_sys_cd = ? AND etl_job = ?" +
				" AND curr_bath_date = ?", dispStatus, etlSysCd, etlJob, currBathDate);

		if(num != 1) {
			throw new AppSystemException("根据调度系统编号、调度作业标识、" +
					"当前批量日期修改作业信息失败" + etlJob);
		}

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号、调度作业标识、当前批量日期修改作业调度状态的作业信息。注意，
	 * etlJobCurs变量中必须含有调度作业标识、当前批量日期；
	 * 若修改数据影响的数据行数不为1，则抛出AppSystemException异常。<br>
	 * 1.批量更新数据库数据。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param dispStatus
	 *          含义：作业调度状态。
	 *          取值范围：Job_Status枚举值，不能为null。
	 * @param etlSysCd
	 *          含义：调度系统编号。
	 *          取值范围：不能为null。
	 * @param etlJobCurs
	 *          含义：EtlJobBean集合，表示一组作业。
	 *          取值范围：不能为null。
	 */
	public static void updateEtlJobDispStatus(String dispStatus, String etlSysCd,
	                                          List<EtlJobBean> etlJobCurs) {

		//1.批量更新数据库数据。
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

		int[] nums = SqlOperator.executeBatch(db, "UPDATE " + Etl_job_cur.TableName +
				" SET job_disp_status = ? WHERE etl_sys_cd = ? AND etl_job = ?" +
				" AND curr_bath_date = ?", params);

		for(int i = 0 ; i < nums.length ; i++) {
			if(nums[i] != 1) {
				//params.get(i)[2]，数字2表示etl_job
				throw new AppSystemException("根据调度系统编号、调度作业标识、" +
						"当前批量日期修改作业信息失败" + params.get(i)[2]);
			}
		}

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 据调度系统编号、调度作业标识修改作业的作业状态为指定内容。注意，
	 * 此处会将[主服务器同步标识]设置为[是]。修改数据影响的数据行数不为1，
	 * 则抛出AppSystemException异常。<br>
	 * 1.更新数据库数据。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param dispStatus
	 *          含义：作业状态。
	 *          取值范围：Job_Status枚举值，不能为null。
	 * @param etlSysCd
	 *          含义：调度系统编号。
	 *          取值范围：不能为null。
	 * @param etlJob
	 *          含义：调度作业标识。
	 *          取值范围：不能为null。
	 */
	public static void updateEtlJobDispStatus(String dispStatus, String etlSysCd, String etlJob) {

		//1.更新数据库数据。
		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		int num = SqlOperator.execute(db, "UPDATE " + Etl_job_cur.TableName +
						" SET job_disp_status = ?, main_serv_sync = ? WHERE etl_sys_cd = ?" +
						" AND etl_job = ?",
				dispStatus, Main_Server_Sync.YES.getCode(), etlSysCd, etlJob);

		if(num != 1) {
			throw new AppSystemException("根据调度系统编号、调度作业标识修改作业信息失败" + etlJob);
		}

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号、调度作业标识，进行修改当前执行时间。注意，
	 * 若修改数据影响的数据行数不为1，则抛出AppSystemException异常。<br>
	 * 1.更新数据库数据。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param currStTime
	 *          含义：开始执行时间。
	 *          取值范围：yyyyMMdd HHmmss格式的字符串，不能为null。
	 * @param etlSysCd
	 *          含义：调度系统编号。
	 *          取值范围：不能为null。
	 * @param etlJob
	 *          含义：调度作业标识。
	 *          取值范围：不能为null。
	 */
	public static void updateEtlJobRunTime(String currStTime, String etlSysCd, String etlJob) {

		//1.更新数据库数据。
		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		int num = SqlOperator.execute(db, "UPDATE " + Etl_job_cur.TableName +
						" SET curr_st_time = ?, main_serv_sync = ? WHERE etl_sys_cd = ?" +
						" AND etl_job = ?", currStTime, Main_Server_Sync.YES.getCode(), etlSysCd,
				etlJob);

		if(num != 1) {
			throw new AppSystemException("根据调度系统编号、调度作业标识，进行修改当前执行时间失败" + etlJob);
		}

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号、调度作业标识、当前跑批日期修改调度作业信息。<br>
	 * 1.更新数据库数据。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param etlSysCd
	 *          含义：调度系统编号。
	 *          取值范围：不能为null。
	 * @param eltJob
	 *          含义：调度作业标识。
	 *          取值范围：不能为null。
	 * @param currBathDate
	 *          含义：当前跑批日期。
	 *          取值范围：yyyyMMdd格式字符串，不能为null。
	 * @param currStTime
	 *          含义：currStTime。
	 *          取值范围：yyyyMMdd HHmmss格式字符串，不能为null。
	 * @param currEndTime
	 *          含义：currEndTime。
	 *          取值范围：yyyyMMdd HHmmss格式字符串，不能为null。
	 */
	public static void updateVirtualJob(String etlSysCd, String eltJob, String currBathDate,
										   String currStTime, String currEndTime) {

		//1.更新数据库数据。
		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		SqlOperator.execute(db, "UPDATE " + Etl_job_cur.TableName + " SET main_serv_sync = ?," +
						" job_disp_status = ?, curr_st_time = ?, curr_end_time = ?" +
						" WHERE etl_sys_cd = ? AND etl_job = ? AND curr_bath_date = ?",
				Main_Server_Sync.YES.getCode(), Job_Status.DONE.getCode(),
				currStTime, currEndTime, etlSysCd, eltJob, currBathDate);

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 在调度系统在续跑模式时，根据调度系统编号及当前跑批日期，修改调度系统表信息。
	 * 注意，此处会修改作业调度状态、主服务器同步标志，但会根据条件修改调度系统信息：
	 * 指定的条件、作业调度状态不在挂起、结束状态、当天调度。<br>
	 * 1.更新数据库数据。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param etlSysCd
	 *          含义：调度系统编号。
	 *          取值范围：不能为null。
	 * @param currBathDate
	 *          含义：当前跑批日期。
	 *          取值范围：yyyyMMdd格式字符串，不能为null。
	 */
	public static void updateEtjJobByResumeRun(String etlSysCd, String currBathDate) {

		//1.更新数据库数据。
		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		SqlOperator.execute(db, "UPDATE " + Etl_job_cur.TableName + " SET job_disp_status = ?," +
						" main_serv_sync = ? WHERE etl_sys_cd = ? AND curr_bath_date = ?" +
						" AND job_disp_status NOT IN (?, ?) AND today_disp = ?",
				Job_Status.PENDING.getCode(), Main_Server_Sync.YES.getCode(),
				etlSysCd, currBathDate, Job_Status.PENDING.getCode(), Job_Status.DONE.getCode(),
				Today_Dispatch_Flag.YES.getCode());

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 据调度系统编号、当前跑批日期，来获取到达调度日期（已登记）的作业。<br>
	 * 1.查询数据库获取数据。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param etlSysCd
	 *          含义：调度系统编号。
	 *          取值范围：不能为null。
	 * @param currBathDate
	 *          含义：当前跑批日期。
	 *          取值范围：yyyyMMdd格式字符串，不能为null。
	 * @return java.util.List<hrds.commons.entity.Etl_job_cur>
	 *          含义：已登记的作业集合。
	 *          取值范围：不会为null。
	 */
	public static List<Etl_job_cur> getEtlJobs(String etlSysCd, String currBathDate) {

		//1.查询数据库获取数据。
		return SqlOperator.queryList(TaskSqlHelper.getDbConnector(), Etl_job_cur.class,
				"SELECT * FROM " + Etl_job_cur.TableName + " WHERE etl_sys_cd = ?" +
						" AND curr_bath_date <= ?", etlSysCd, currBathDate);
	}

	/**
	 * 根据调度系统编号、调度作业标识、当前跑批日期获取调度作业信息。
	 * 注意，当无法获取到数据时抛出AppSystemException异常。<br>
	 * 1.查询数据库获取数据，并转换为Etl_job_cur实体。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param etlSysCd
	 *          含义：调度系统编号。
	 *          取值范围：不能为null。
	 * @param etlJob
	 *          含义：调度作业标识。
	 *          取值范围：不能为null。
	 * @param currBathDate
	 *          含义：当前跑批日期。
	 *          取值范围：yyyyMMdd格式字符串，不能为null。
	 * @return hrds.commons.entity.Etl_job_cur
	 *          含义：表示一个已登记执行的作业。
	 *          取值范围：不会为null。
	 */
	public static Etl_job_cur getEtlJob(String etlSysCd, String etlJob, String currBathDate) {

		//1.查询数据库获取数据，并转换为Etl_job_cur实体。
		Object[] row = SqlOperator.queryArray(TaskSqlHelper.getDbConnector(),
				"SELECT etl_sys_cd, etl_job, sub_sys_cd, etl_job_desc, pro_type, pro_dic," +
						" pro_name, pro_para, log_dic, disp_freq, disp_offset, disp_type," +
						" disp_time, job_eff_flag, job_priority, job_disp_status, curr_st_time," +
						" curr_end_time, overlength_val, overtime_val, curr_bath_date, comments," +
						" today_disp, main_serv_sync, job_process_id, job_priority_curr," +
						" job_return_val, exe_frequency, exe_num, com_exe_num, last_exe_time," +
						" star_time, end_time FROM " + Etl_job_cur.TableName +
						" WHERE etl_sys_cd = ? AND etl_job = ? AND curr_bath_date = ?",
				etlSysCd, etlJob, currBathDate);

		if(row.length == 0) {
			throw new AppSystemException("根据调度系统编号、调度作业标识、" +
					"当前跑批日期获取调度作业信息失败 " + etlSysCd);
		}

		return TaskSqlHelper.obj2EtlJobCur(row);
	}

	/**
	 * 根据调度系统编号、调度作业标识获取调度作业信息。注意，当无法获取到数据时抛出AppSystemException异常。<br>
	 * 1.查询数据库获取数据，并转换为Etl_job_cur实体。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param etlSysCd
	 *          含义：调度系统编号。
	 *          取值范围：不能为null。
	 * @param etlJob
	 *          含义：调度作业标识。
	 *          取值范围：不能为null。
	 * @return hrds.commons.entity.Etl_job_cur
	 *          含义：表示一个已登记执行的作业。
	 *          取值范围：不会为null。
	 */
	public static Etl_job_cur getEtlJob(String etlSysCd, String etlJob) {

		//1.查询数据库获取数据，并转换为Etl_job_cur实体。
		Object[] etlJobCurObj = SqlOperator.queryArray(TaskSqlHelper.getDbConnector(),
				"SELECT etl_sys_cd, etl_job, sub_sys_cd, etl_job_desc, pro_type, pro_dic," +
						" pro_name, pro_para, log_dic, disp_freq, disp_offset, disp_type," +
						" disp_time, job_eff_flag, job_priority, job_disp_status, curr_st_time," +
						" curr_end_time, overlength_val, overtime_val, curr_bath_date, comments," +
						" today_disp, main_serv_sync, job_process_id, job_priority_curr," +
						" job_return_val, exe_frequency, exe_num, com_exe_num, last_exe_time," +
						" star_time, end_time FROM " + Etl_job_cur.TableName +
						" WHERE etl_sys_cd= ? AND etl_job = ?", etlSysCd, etlJob);

		if(0 == etlJobCurObj.length) {
			throw new AppSystemException("根据调度系统编号、调度作业标识获取调度作业信息失败 "
					+ etlSysCd);
		}

		return TaskSqlHelper.obj2EtlJobCur(etlJobCurObj);
	}

	/**
	 * 用于将SqlOperator.queryArray查询出来的对象转换为Etl_job_cur对象，这样做避免了反射。
	 * 注意，该方法在传入的参数为null或数组个数不为33则会抛出AppSystemException异常。<br>
	 * 1.检测传入的对象数组参数元素个数是否为33个；<br>
	 * 2.对象数组转换为实体对象。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param row
	 *          含义：数据库中表的一行数据。
	 *          取值范围：不能为null。
	 * @return hrds.commons.entity.Etl_job_cur
	 *          含义：表示一个已登记执行的作业。
	 *          取值范围：不会为null。
	 */
	private static Etl_job_cur obj2EtlJobCur(Object[] row) {

		//1.检测传入的对象数组参数元素个数是否为33个；
		if(row.length != 33 ) {
			throw new AppSystemException("Object[]转换为Etl_job_cur对象失败，元素个数不为33");
		}

		//2.对象数组转换为实体对象。
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
	 * 根据调度系统编号获取系统资源信息。注意，若无法查询出数据则抛出AppSystemException异常。<br>
	 * 1.查询数据库获取数据，并转换为Etl_resource实体。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param etlSysCd
	 *          含义：调度系统编号。
	 *          取值范围：不能为null。
	 * @return java.util.List<hrds.commons.entity.Etl_resource>
	 *          含义：系统资源信息集合。
	 *          取值范围：不会为null。
	 */
	public static List<Etl_resource> getEtlSystemResources(String etlSysCd) {

		//1.查询数据库获取数据，并转换为Etl_resource实体。
		List<Object[]> rows = SqlOperator.queryArrayList(TaskSqlHelper.getDbConnector(),
				"SELECT etl_sys_cd, resource_type, resource_max, resource_used, main_serv_sync" +
						" FROM " + Etl_resource.TableName + " WHERE etl_sys_cd = ?", etlSysCd);

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
	 * 根据调度系统编号来更新[资源使用数]。注意，
	 * 若更新数据影响数据行数小于1，则抛出AppSystemException异常。<br>
	 * 1.更新数据库数据。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param etlSysCd
	 *          含义：调度系统编号。
	 *          取值范围：不能为null。
	 * @param used
	 *          含义：资源使用数。
	 *          取值范围：int范围内任何数值。
	 */
	public static void updateEtlResourceUsed(String etlSysCd, int used) {

		//1.更新数据库数据。
		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		int num = SqlOperator.execute(db, "UPDATE " + Etl_resource.TableName + " SET" +
				" resource_used = ? WHERE etl_sys_cd = ?", used, etlSysCd);

		if(num < 1) {
			throw new AppSystemException("根据调度系统编号来更新[资源使用数]失败" + etlSysCd);
		}

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号、资源类型批量修改[已使用资源]为指定数。
	 * 注意，etlResources变量中必须含有已使用资源数、资源类型；
	 * 若修改数据影响的数据行数不为1，则抛出AppSystemException异常。<br>
	 * 1.批量更新数据库数据。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param etlSysCd
	 *          含义：调度系统编号。
	 *          取值范围：不能为null。
	 * @param etlResources
	 *          含义：系统资源集合。
	 *          取值范围：不能为null。
	 */
	public static void updateEtlResourceUsedByResourceType(String etlSysCd,
	                                                       List<Etl_resource> etlResources) {

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

		int[] nums = SqlOperator.executeBatch(db, "UPDATE " + Etl_resource.TableName +
				" SET resource_used = ? WHERE etl_sys_cd = ? AND resource_type = ?", params);

		for(int i = 0 ; i < nums.length ; i++) {
			//params.get(i)[2]表示resource_type
			if(nums[i] != 1) {
				throw new AppSystemException(String.format("据调度系统编号%s、" +
								"资源类型%s修改[已使用资源]失败", etlSysCd, params.get(i)[2]));
			}
		}

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号、资源类型批量修改[已使用资源]为指定数。
	 * 若修改数据影响的数据行数不为1，则抛出AppSystemException异常。<br>
	 * 1.更新数据库数据。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param etlSysCd
	 *          含义：调度系统编号。
	 *          取值范围：不能为null。
	 * @param resourceType
	 *          含义：资源类型。
	 *          取值范围：不能为null。
	 * @param used
	 *          含义：资源使用数。
	 *          取值范围：int范围内任何数值。
	 */
	public static void updateEtlResourceUsedByResourceType(String etlSysCd, String resourceType,
	                                                       int used) {

		//1.更新数据库数据。
		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		int num = SqlOperator.execute(db, "UPDATE " + Etl_resource.TableName +
				" SET resource_used = ? WHERE etl_sys_cd = ? AND resource_type = ?",
				used, etlSysCd, resourceType);

		if(num != 1) {
			throw new AppSystemException(String.format("据调度系统编号%s、" +
							"资源类型%s修改[已使用资源]失败", etlSysCd, resourceType));
		}

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号获取调度作业干预信息。
	 * 1.查询数据库获取数据。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param etlSyscd
	 *          含义：调度系统编号。
	 *          取值范围：不能为null。
	 * @return java.util.List<hrds.commons.entity.Etl_job_hand>
	 *          含义：系统/作业干预集合。
	 *          取值范围：不会为null。
	 */
	public static List<Etl_job_hand> getEtlJobHands(String etlSyscd) {

		//1.查询数据库获取数据。
		//虽然该SQL一直在查询，但是干预的情况不多，不需要不用反射
		return SqlOperator.queryList(TaskSqlHelper.getDbConnector(), Etl_job_hand.class,
				"SELECT * FROM " + Etl_job_hand.TableName +
						" WHERE hand_status = ? AND etl_sys_cd = ?",
				Meddle_status.TRUE.getCode(), etlSyscd);
	}

	/**
	 * 修改调度作业干预表（etl_job_hand）。注意，此处会使用参数中的etl_job_hand、hand_status、
	 * main_serv_sync、end_time、warning、etl_sys_cd、etl_job、etl_hand_type。
	 * 若更新数据影响数据行数小于1，则抛出AppSystemException异常。<br>
	 * 1.更新数据库数据。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param etlJobHand
	 *          含义：表示一条系统/作业干预信息。
	 *          取值范围：不能为null。
	 */
	public static void updateEtlJobHandle(Etl_job_hand etlJobHand) {

		//1.更新数据库数据。
		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		int num = SqlOperator.execute(db, "UPDATE " + Etl_job_hand.TableName +
						" SET hand_status = ?, main_serv_sync = ?, end_time = ?, warning = ?" +
						" WHERE etl_sys_cd = ? AND etl_job = ? AND etl_hand_type = ?",
				etlJobHand.getHand_status(), etlJobHand.getMain_serv_sync(),
				etlJobHand.getEnd_time(), etlJobHand.getWarning(), etlJobHand.getEtl_sys_cd(),
				etlJobHand.getEtl_job(), etlJobHand.getEtl_hand_type());

		if(num < 1) {
			throw new AppSystemException("修改调度作业干预表（etl_job_hand）失败 " +
					etlJobHand.getEtl_job());
		}

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 在调度作业干预历史表中新增一条数据（etl_job_hand_his）。注意，
	 * 若新增数据失败则抛出AppSystemException异常。<br>
	 * 1.更新数据库数据。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param etlJobHandHis
	 *          含义：表示一条系统/作业干预历史。
	 *          取值范围：不能为null。
	 */
	public static void insertIntoEtlJobHandleHistory(Etl_job_hand_his etlJobHandHis) {

		//1.更新数据库数据。
		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		etlJobHandHis.add(db);

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号、调度作业标识、干预类型删除调度干预表（etl_job_hand）信息。<br>
	 * 1.更新数据库数据。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param etlSysCd
	 *          含义：调度系统编号。
	 *          取值范围：不能为null。
	 * @param etlJob
	 *          含义：调度作业标识。
	 *          取值范围：不能为null。
	 * @param etlHandType
	 *          含义：干预类型。
	 *          取值范围：TaskJobHandleHelper类中的固定值，不能为null。
	 */
	public static void deleteEtlJobHand(String etlSysCd, String etlJob, String etlHandType) {

		//1.更新数据库数据。
		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		SqlOperator.execute(db, "DELETE FROM " + Etl_job_hand.TableName +
				" WHERE etl_sys_cd = ? AND etl_job = ? AND etl_hand_type = ?",
				etlSysCd, etlJob, etlHandType);

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号，获取需要调度的作业（作业状态为挂起或者等待）。<br>
	 * 1.查询数据库获取数据。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param etlSysCd
	 *          含义：调度系统编号。
	 *          取值范围：不能为null。
	 * @return java.util.List<hrds.commons.entity.Etl_job_cur>
	 *          含义：一组已经登记执行的作业集合。
	 *          取值范围：不会为null。
	 */
	public static List<Etl_job_cur> getReadyEtlJobs(String etlSysCd) {

		//1.查询数据库获取数据。
		return SqlOperator.queryList(TaskSqlHelper.getDbConnector(), Etl_job_cur.class,
				"SELECT * FROM " + Etl_job_cur.TableName +
						" WHERE (job_disp_status = ? OR job_disp_status = ?) AND etl_sys_cd = ?",
				Job_Status.PENDING.getCode(), Job_Status.WAITING.getCode(), etlSysCd);
	}

	/**
	 * 根据调度系统编号，获取指定作业状态的作业。<br>
	 * 1.查询数据库获取数据。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param etlSysCd
	 *          含义：调度系统编号。
	 *          取值范围：不能为null。
	 * @param jobStatus
	 *          含义：作业状态。
	 *          取值范围：Job_Status枚举值，不能为null。
	 * @return java.util.List<hrds.commons.entity.Etl_job_cur>
	 *          含义：一组已经登记执行的作业集合。
	 *          取值范围：不会为null。
	 */
	public static List<Etl_job_cur> getEtlJobsByJobStatus(String etlSysCd, String jobStatus) {

		//1.查询数据库获取数据。
		return SqlOperator.queryList(TaskSqlHelper.getDbConnector(), Etl_job_cur.class,
				"SELECT * FROM " + Etl_job_cur.TableName +
						" WHERE etl_sys_cd = ? AND job_disp_status = ?", etlSysCd, jobStatus);
	}

	/**
	 * 根据调度系统编号，将该系统下的作业置为挂起状态。注意，此处会将作业状态设置为[挂起]，
	 * 主服务器同步标志设置为[是]，且将作业优先级更新到当前作业优先级。
	 * 修改的数据，影响的数据行数小于1，则抛出AppSystemException异常。<br>
	 * 1.更新数据库数据。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param etlSysCd
	 *          含义：调度系统编号。
	 *          取值范围：不能为null。
	 */
	public static void updateEtlJobToPending(String etlSysCd) {

		//1.更新数据库数据。
		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		int num = SqlOperator.execute(db, "UPDATE " + Etl_job_cur.TableName +
						" SET job_disp_status = ?, main_serv_sync = ?," +
						" job_priority_curr = job_priority WHERE etl_sys_cd= ? AND today_disp = ?",
				Job_Status.PENDING.getCode(), Main_Server_Sync.YES.getCode(), etlSysCd,
				Today_Dispatch_Flag.YES.getCode());

		if(num < 1) {
			throw new AppSystemException("根据调度系统编号，将该系统下的作业置为挂起状态失败" +
					etlSysCd);
		}

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号，将作业状态在[停止]以及[错误]下的作业修改为指定状态。<br>
	 * 1.更新数据库数据。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param etlSysCd
	 *          含义：调度系统编号。
	 *          取值范围：不能为null。
	 * @param jobStatus
	 *          含义：作业状态。
	 *          取值范围：Job_Status枚举值，不能为null。
	 */
	public static void updateEtlJobToPendingInResume(String etlSysCd, String jobStatus) {

		//1.更新数据库数据。
		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		SqlOperator.execute(db, "UPDATE " + Etl_job_cur.TableName + " SET job_disp_status = ?," +
						" main_serv_sync = ?, job_priority_curr = job_priority WHERE" +
						" (job_disp_status = ? OR job_disp_status = ?) AND etl_sys_cd = ?",
				jobStatus, Main_Server_Sync.YES.getCode(), Job_Status.STOP.getCode(),
				Job_Status.ERROR.getCode(), etlSysCd);

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号、调度作业编号、当前跑批日期修改指定的当前作业优先级。注意，
	 * 此处仅会最多修改一条数据。若修改的数据影响的数据行数不为1，则抛出AppSystemException异常。<br>
	 * 1.更新数据库数据。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param jobPriorityCurr
	 *          含义：当前作业优先级。
	 *          取值范围：int范围内的任何数值。
	 * @param etlSysCd
	 *          含义：调度系统编号。
	 *          取值范围：不能为null。
	 * @param etlJob
	 *          含义：调度作业标识。
	 *          取值范围：不能为null。
	 * @param currBathDate
	 *          含义：当前跑批日期。
	 *          取值范围：yyyyMMdd格式的字符串，不能为null。
	 */
	public static void updateEtlJobCurrPriority(int jobPriorityCurr, String etlSysCd, String etlJob,
	                                               String currBathDate) {

		//1.更新数据库数据。
		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		int num = SqlOperator.execute(db, "UPDATE " + Etl_job_cur.TableName +
						" SET job_priority_curr = ? WHERE etl_sys_cd = ? AND etl_job = ?" +
						" AND curr_bath_date = ?", jobPriorityCurr, etlSysCd, etlJob, currBathDate);

		if(num != 1) {
			throw new AppSystemException("根据调度系统编号、调度作业编号、" +
					"当前跑批日期修改当前作业优先级失败" + etlJob);
		}

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 在etl_job_hand表中新增一条数据。注意，此方法会根据传入的对象所携带的参数来新增数据。
	 * 若新增数据失败，则抛出AppSystemException异常。<br>
	 * 1.更新数据库数据。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param jobHand
	 *          含义：表示一个作业干预信息。
	 *          取值范围：不能为null。
	 */
	public static void insertIntoEtlJobHand(Etl_job_hand jobHand) {

		//1.更新数据库数据。
		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		jobHand.add(db);

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 在etl_job_disp_his表中新增一条数据。注意，此方法会根据传入的对象所携带的参数来新增数据。
	 * 若新增数据失败，则抛出AppSystemException异常。<br>
	 * 1.更新数据库数据。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param etlJobDispHis 
	 *          含义：表示作业跑批历史。
	 *          取值范围：不能为null。
	 */
	public static void insertIntoEtlJobDispHis(Etl_job_disp_his etlJobDispHis) {

		//1.更新数据库数据。
		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		etlJobDispHis.add(db);

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据系统编号、作业标识、当前作业跑批日期更新作业信息。注意，该方法用到的参数有job_disp_status、
	 * curr_end_time、job_return_val、etl_sys_cd、etl_job、curr_bath_date，
	 * 该方法会将main_serv_sync设置为y。若更新数据所影响的数据行数不为1，则抛出AppSystemException异常。<br>
	 * 1.更新数据库数据。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param etlJobCur
	 *          含义：表示一个已登记到etl_job_cur表的作业。
	 *          取值范围：不能为null。
	 */
	public static void updateEtlJobFinished(Etl_job_cur etlJobCur) {

		//1.更新数据库数据。
		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		int num = SqlOperator.execute(db, "UPDATE " + Etl_job_cur.TableName +
						" SET main_serv_sync = ?, job_disp_status = ?, curr_end_time = ?," +
						" job_return_val = ? WHERE etl_sys_cd = ? AND etl_job = ?" +
						" AND curr_bath_date = ?", Main_Server_Sync.YES.getCode(),
				etlJobCur.getJob_disp_status(), etlJobCur.getCurr_end_time(),
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
	 * 在无法查询出时间时会抛出AppSystemException异常。<br>
	 * 1.查询数据库获取数据。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param para
	 *          含义：所使用的参数主键。
	 *          取值范围：不能为null。
	 * @return java.lang.String
	 *          含义：数据库etl_para表的para_cd字段数据。
	 *          取值范围：不会为null。
	 */
	public static String getParaByPara(String para) {

		Object[] row = SqlOperator.queryArray(TaskSqlHelper.getDbConnector(),
				"SELECT para_cd FROM " + Etl_para.TableName + " WHERE para_cd = ?", para);

		if(row.length == 0) {
			throw new AppSystemException("所使用的参数标识不存在" + para);
		}

		return (String) row[0];
	}
}
