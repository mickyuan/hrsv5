package hrds.trigger.task.helper;

import hrds.commons.entity.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.codes.Main_Server_Sync;
import hrds.commons.exception.AppSystemException;

import java.util.Optional;

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
	public static Etl_sys getEltSysBySysCode(final String etlSysCd) {

		//1.数据库查询，并转换为实体。
		Object[] row = SqlOperator.queryArray(
				TaskSqlHelper.getDbConnector(), "SELECT etl_sys_cd, etl_sys_name," +
						" etl_serv_ip, etl_serv_port, contact_person, contact_phone, comments," +
						" curr_bath_date, bath_shift_time, main_serv_sync, sys_run_status," +
						" user_name, user_pwd, serv_file_path, remarks FROM " + Etl_sys.TableName +
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
	public static Etl_job_cur getEtlJob(final String etlSysCd, final String etlJob,
	                                    final String currBathDate) {

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
	 * 根据调度系统编号、作业标识，修改该作业的开始执行时间。
	 * @author Tiger.Wang
	 * @date 2019/10/25
	 * @param etlSysCode
	 *          含义：调度系统编号。
	 *          取值范围：不能为null。
	 * @param etlJob
	 *          含义：作业标识。
	 *          取值范围：不能为null。
	 * @param currStTime
	 *          含义：开始执行时间。
	 *          取值范围：不能为null。
	 */
	public static void updateEtlJob2Running(final String etlSysCode, final String etlJob,
	                                        final String currStTime) {

		//1.更新数据库数据。
		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		SqlOperator.execute(db, "UPDATE " + Etl_job_cur.TableName + " SET curr_st_time = ?," +
						" main_serv_sync = ? WHERE etl_sys_cd = ? AND etl_job = ?",
				currStTime, Main_Server_Sync.YES.getCode(), etlSysCode, etlJob);

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 新增一条作业调度历史数据。
	 * @author Tiger.Wang
	 * @date 2019/10/25
	 * @param etlJobDispHis
	 *          含义：表示一个作业调度历史。
	 *          取值范围：不能为null。
	 */
	public static void insertIntoEltJobDispHis(final Etl_job_disp_his etlJobDispHis) {

		//1.更新数据库数据。
		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		etlJobDispHis.add(db);

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 在作业调度完成的情况下，修改作业的调度信息。
	 * @author Tiger.Wang
	 * @date 2019/10/25
	 * @param jobDispStatus
	 *          含义：调度作业状态。
	 *          取值范围：不能为null。
	 * @param currEndTime
	 *          含义：结束日期时间。
	 *          取值范围：不能为null。
	 * @param jobReturnVal
	 *          含义：作业返回值。
	 *          取值范围：不能为null。
	 * @param lastExeTime
	 *          含义：最近执行日期时间。
	 *          取值范围：不能为null。
	 * @param etlSysCode
	 *          含义：调度系统编号。
	 *          取值范围：不能为null。
	 * @param etlJob
	 *          含义：作业标识。
	 *          取值范围：不能为null。
	 * @param currBathDate
	 *          含义：当前跑批日期。
	 *          取值范围：不能为null。
	 */
	public static void updateEtlJob2Complete(final String jobDispStatus, final String currEndTime,
	                                         final Integer jobReturnVal, final String lastExeTime,
	                                         final String etlSysCode, final String etlJob,
	                                         final String currBathDate) {
		//1.更新数据库数据。
		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		SqlOperator.execute(db, "UPDATE " + Etl_job_cur.TableName + " SET job_disp_status = ?," +
						" main_serv_sync = ?, curr_end_time = ?, job_return_val = ?," +
						" last_exe_time = ?, com_exe_num = com_exe_num + 1" +
						" WHERE etl_sys_cd = ? AND etl_job = ? AND curr_bath_date = ?",
				jobDispStatus, Main_Server_Sync.YES.getCode(), currEndTime, jobReturnVal,
				lastExeTime, etlSysCode, etlJob, currBathDate);

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号、作业标识，修改作业的最近执行日期时间。
	 * @author Tiger.Wang
	 * @date 2019/10/25
	 * @param lastExeTime
	 *          含义：最近执行日期时间。
	 *          取值范围：不能为null。
	 * @param etlSysCode
	 *          含义：调度系统编号。
	 *          取值范围：不能为null。
	 * @param etlJob
	 *          含义：作业标识。
	 *          取值范围：不能为null。
	 */
	public static void updateEtlJobDefLastExeTime(final String lastExeTime, final String etlSysCode,
	                                              final String etlJob) {
		//1.更新数据库数据。
		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		SqlOperator.execute(db, "UPDATE " + Etl_job_def.TableName + " SET last_exe_time = ?," +
						" com_exe_num = com_exe_num + 1 WHERE etl_sys_cd = ? AND etl_job = ?",
				lastExeTime, etlSysCode, etlJob);

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号、作业标识，更新作业的进程编号。
	 * @author Tiger.Wang
	 * @date 2019/10/25
	 * @param processId
	 *          含义：进程编号。
	 *          取值范围：不能为null。
	 * @param etlSysCode
	 *          含义：调度系统编号。
	 *          取值范围：不能为null。
	 * @param etlJob
	 *          含义：作业标识。
	 *          取值范围：不能为null。
	 */
	public static void updateEtlJobProcessId(final String processId, final String etlSysCode,
	                                         final String etlJob) {

		//1.更新数据库数据。
		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		SqlOperator.execute(db, "UPDATE " + Etl_job_cur.TableName + " SET job_process_id = ?" +
						" WHERE etl_sys_cd = ? AND etl_job = ?", processId, etlSysCode, etlJob);

		SqlOperator.commitTransaction(db);
	}

	/**
	 * 根据调度系统编号、作业标识、干预类型查询干预信息。
	 * @author Tiger.Wang
	 * @date 2019/10/25
	 * @param etlSysCode
	 *          含义：调度系统编号。
	 *          取值范围：不能为null。
	 * @param etlJob
	 *          含义：作业标识。
	 *          取值范围：不能为null。
	 * @param handType
	 *          含义：干预类型。
	 *          取值范围：不能为null。
	 * @return java.util.Optional<hrds.commons.entity.Etl_job_hand>
	 *          含义：一条干预信息数据。
	 *          取值范围：不会为null。
	 */
	public static Optional<Etl_job_hand> getEtlJobHandle(final String etlSysCode, final String etlJob,
	                                                     final String handType) {

		return SqlOperator.queryOneObject(TaskSqlHelper.getDbConnector(), Etl_job_hand.class,
				"SELECT * FROM " + Etl_job_hand.TableName +
						" WHERE etl_sys_cd = ? AND etl_job = ? AND etl_hand_type = ?",
				etlSysCode, etlJob, handType);

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
	public static void updateEtlJobHandle(final Etl_job_hand etlJobHand) {

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
	public static void insertIntoEtlJobHandleHistory(final Etl_job_hand_his etlJobHandHis) {

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
	public static void deleteEtlJobHand(final String etlSysCd, final String etlJob,
	                                    final String etlHandType) {

		//1.更新数据库数据。
		DatabaseWrapper db = TaskSqlHelper.getDbConnector();

		SqlOperator.execute(db, "DELETE FROM " + Etl_job_hand.TableName +
				" WHERE etl_sys_cd = ? AND etl_job = ? AND etl_hand_type = ?",
				etlSysCd, etlJob, etlHandType);

		SqlOperator.commitTransaction(db);
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
	private static Etl_job_cur obj2EtlJobCur(final Object[] row) {

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
}
