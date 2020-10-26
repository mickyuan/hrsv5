package hrds.agent.job.biz.core;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.commons.exception.AppSystemException;
import hrds.commons.hadoop.hadoop_helper.HBaseHelper;

import java.io.IOException;

@DocClass(desc = "作业阶段接口适配器，请每种类型任务的每个阶段继承该类,提供setNextStage()和getNextStage()的默认实现," +
		"这两个方法的作用是设置和返回责任链中当前环节的下一环节", author = "WangZhengcheng")
public abstract class AbstractJobStage implements JobStageInterface {

	//	protected static final String TERMINATED_MSG = "脚本执行完成";
//	protected static final String FAILD_MSG = "脚本执行失败";
	private JobStageInterface nextStage;

	@Method(desc = "设置当前阶段的下一处理阶段，该方法在AbstractJobStage抽象类中做了默认实现，请每种类型任务的每个阶段实现类不要覆盖该方法", logicStep = "")
	@Param(name = "stage", desc = "stage代表下一阶段", range = "JobStageInterface的实例，也就是JobStageInterface的具体实现类对象")
	@Override
	public void setNextStage(JobStageInterface stage) {
		this.nextStage = stage;
	}

	@Method(desc = "获得当前阶段的下一处理阶段，该方法在AbstractJobStage抽象类中做了默认实现，请每种类型任务的每个阶段实现类不要覆盖该方法", logicStep = "")
	@Return(desc = "当前处理阶段的下一个阶段", range = "JobStageInterface的实例，也就是JobStageInterface的具体实现类对象")
	@Override
	public JobStageInterface getNextStage() {
		return nextStage;
	}

	/**
	 * 备份表上次执行进数的数据
	 *
	 * @param todayTableName 上次执行进数的表名
	 * @param db             数据库连接
	 */
	public static void backupToDayTable(String todayTableName, DatabaseWrapper db) {
		if (db.isExistTable(todayTableName)) {
			if (db.isExistTable(todayTableName + "b")) {
				db.execute("DROP TABLE " + todayTableName + "b");
			}
			//如果表存在
			db.execute(db.getDbtype().ofRenameSql(todayTableName, todayTableName + "b"));
		}
	}

	/**
	 * 备份表上次执行进数的数据
	 *
	 * @param todayTableName 上次执行进数的表名
	 * @param helper         HBase操作表的对象
	 */
	public static void backupToDayTable(String todayTableName, HBaseHelper helper) {
		try {
			if (helper.existsTable(todayTableName)) {
				if (helper.existsTable(todayTableName + "b")) {
					helper.dropTable(todayTableName + "b");
				}
				//如果表存在
				helper.renameTable(todayTableName, todayTableName + "b");
			}
		} catch (IOException e) {
			throw new AppSystemException("加载进HBase，备份表上次执行进数的数据失败", e);
		}
	}

	/**
	 * 根据表存储期限备份每张表存储期限内进数的数据
	 *
	 * @param collectTableBean 表配置信息
	 * @param db               数据库连接
	 */
	public static void backupPastTable(CollectTableBean collectTableBean, DatabaseWrapper db) {
		//获取存储期限值，根据存储期限值对当天卸数的数据进行保存
		Long storage_time = collectTableBean.getStorage_time();
		//数据进库之后的表名
		String hbase_name = collectTableBean.getHbase_name();
		//判断程序最后一次入库日期等于跑批日期，则认为本次执行是重跑
		if (collectTableBean.getEtlDate().equals(collectTableBean.getStorage_date()) || storage_time == 1) {
			//重跑，或者数据只保留一天，直接删除今天表的备份表
			if (db.isExistTable(hbase_name + "_1b")) {
				db.execute("DROP TABLE " + hbase_name + "_1b");
			}
		} else {
			//非重跑,根据执行期限去修改过去几次执行保存的表，删除最早一次执行的表
			for (long i = storage_time; i > 1; i--) {
				if (db.isExistTable(hbase_name + "_" + i)) {
					if (i == storage_time) {
						db.execute("DROP TABLE " + hbase_name + "_" + i);
					} else {
						if (db.isExistTable(hbase_name + "_" + (i + 1))) {
							db.execute("DROP TABLE " + hbase_name + "_" + (i + 1));
						}
						db.execute(db.getDbtype().ofRenameSql(hbase_name + "_" + i, hbase_name
								+ "_" + (i + 1)));
					}
				}
			}
			if (db.isExistTable(hbase_name + "_1b")) {
				if (db.isExistTable(hbase_name + "_" + 2)) {
					db.execute("DROP TABLE " + hbase_name + "_" + 2);
				}
				//修改备份的表
				db.execute(db.getDbtype().ofRenameSql(hbase_name + "_1b", hbase_name + "_" + 2));
			}
		}
	}

	/**
	 * 根据表存储期限备份每张表存储期限内进数的数据
	 *
	 * @param collectTableBean 表配置信息
	 * @param helper           HBase操作表的对象
	 */
	public static void backupPastTable(CollectTableBean collectTableBean, HBaseHelper helper) {
		try {
			//获取存储期限值，根据存储期限值对当天卸数的数据进行保存
			Long storage_time = collectTableBean.getStorage_time();
			//数据进库之后的表名
			String hbase_name = collectTableBean.getHbase_name();
			//判断程序最后一次入库日期等于跑批日期，则认为本次执行是重跑
			if (collectTableBean.getEtlDate().equals(collectTableBean.getStorage_date()) || storage_time == 1) {
				//重跑，或者数据只保留一天，直接删除今天表的备份表
				if (helper.existsTable(hbase_name + "_1b")) {
					helper.dropTable(hbase_name + "_1b");
				}
			} else {
				//非重跑,根据执行期限去修改过去几次执行保存的表，删除最早一次执行的表
				for (long i = storage_time; i > 1; i--) {
					if (helper.existsTable(hbase_name + "_" + i)) {
						if (i == storage_time) {
							helper.dropTable(hbase_name + "_" + i);
						} else {
							if (helper.existsTable(hbase_name + "_" + (i + 1))) {
								helper.dropTable(hbase_name + "_" + (i + 1));
							}
							helper.renameTable(hbase_name + "_" + i, hbase_name
									+ "_" + (i + 1));
						}
					}
				}
				if (helper.existsTable(hbase_name + "_1b")) {
					if (helper.existsTable(hbase_name + "_" + 2)) {
						helper.dropTable(hbase_name + "_" + 2);
					}
					//修改备份的表
					helper.renameTable(hbase_name + "_1b", hbase_name + "_" + 2);
				}
			}
		} catch (IOException e) {
			throw new AppSystemException("加载进HBase，根据表存储期限备份每张表存储期限内进数的数据失败", e);
		}
	}

	/**
	 * 执行失败，恢复上次进数的数据
	 *
	 * @param todayTableName 上次执行进数的表名
	 * @param db             数据库连接
	 */
	public static void recoverBackupToDayTable(String todayTableName, DatabaseWrapper db) {
		if (db.isExistTable(todayTableName + "b")) {
			if (db.isExistTable(todayTableName)) {
				//判断todayTableName表已经创建，删除
				db.execute("DROP TABLE " + todayTableName);
			}
			//如果表存在
			db.execute(db.getDbtype().ofRenameSql(todayTableName + "b", todayTableName));
		}
	}

	/**
	 * 执行失败，恢复上次进数的数据
	 *
	 * @param todayTableName 上次执行进数的表名
	 * @param helper         HBase操作表的对象
	 */
	public static void recoverBackupToDayTable(String todayTableName, HBaseHelper helper) {
		try {
			if (helper.existsTable(todayTableName + "b")) {
				if (helper.existsTable(todayTableName)) {
					//判断todayTableName表已经创建，删除
					helper.dropTable(todayTableName);
				}
				//如果表存在
				helper.renameTable(todayTableName + "b", todayTableName);
			}
		} catch (IOException e) {
			throw new AppSystemException("加载进HBase执行失败，恢复上次进数的数据失败", e);
		}
	}
}
