package hrds.k.biz.algorithms.impl;

import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.web.util.Dbo;
import hrds.commons.entity.Dbm_analysis_schedule_tab;
import hrds.commons.exception.AppSystemException;
import hrds.commons.exception.BusinessException;
import hrds.commons.hadoop.readconfig.ConfigReader;
import hrds.commons.utils.Constant;
import hrds.k.biz.algorithms.conf.AlgorithmsConf;
import hrds.k.biz.algorithms.helper.Logger;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ImportHyFdData {
	public static void importDataToDatabase(AlgorithmsConf algorithmsConf, DatabaseWrapper db) {
		Logger.getInstance().writeln("开始导入数据入库...");
		try {
			//先清除上次执行的结果
			db.execute("DELETE FROM dbm_function_dependency_tab WHERE sys_class_code = ? AND table_schema = ? " +
							"AND table_code = ?", algorithmsConf.getSys_code(), algorithmsConf.getDatabase_name(),
					algorithmsConf.getTable_name());
			//获取文件，写数据入库
			List<String> strings = new ArrayList<>();
			String outPath = algorithmsConf.getOutputFilePath() + Constant.HYFD_RESULT_PATH_NAME + "/part-00000";
			FileSystem fs = FileSystem.get(URI.create(outPath), ConfigReader.getConfiguration());
			BufferedReader reader = new BufferedReader(new InputStreamReader(fs.open(new Path(outPath)), StandardCharsets.UTF_8));
//			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(outPath),
//					StandardCharsets.UTF_8));
			String line;
			while ((line = reader.readLine()) != null) {
				strings.add(line);
			}
			String insertSql = "INSERT INTO dbm_function_dependency_tab(sys_class_code,table_schema,table_code,left_columns," +
					"right_columns,proc_dt,fd_level) VALUES (?,?,?,?,?,?,?)";
			List<Object[]> pool = new ArrayList<>();
			for (String str : strings) {
				String[] strings1 = str.split(":");
				if (strings1.length > 1) {
					if (!"[]".equals(strings1[0])) {
						String left_columns = strings1[0].substring(1, strings1[0].length() - 1);
						int fd_level = left_columns.split(",").length;
						String[] strings2 = strings1[1].split(",");
						for (String right_columns : strings2) {
							Object[] objects = new Object[7];
							objects[0] = algorithmsConf.getSys_code();
							objects[1] = algorithmsConf.getDatabase_name();
							objects[2] = algorithmsConf.getTable_name();
							objects[3] = left_columns;
							objects[4] = right_columns;
							objects[5] = new SimpleDateFormat("yy-MM-dd HH:mm:ss").
									format(Calendar.getInstance().getTime());
							objects[6] = fd_level;
							pool.add(objects);
						}
					}else{
						String[] strings2 = strings1[1].split(",");
						for (String right_columns : strings2) {
							Object[] objects = new Object[7];
							objects[0] = algorithmsConf.getSys_code();
							objects[1] = algorithmsConf.getDatabase_name();
							objects[2] = algorithmsConf.getTable_name();
							objects[3] = "";
							objects[4] = right_columns;
							objects[5] = new SimpleDateFormat("yy-MM-dd HH:mm:ss").
									format(Calendar.getInstance().getTime());
							objects[6] = 0;
							pool.add(objects);
						}
					}
				}
			}
			if (pool.size() > 0) {
				int[] ints = db.execBatch(insertSql, pool);
				Logger.getInstance().writeln("导入数据表function_dependency_tab,数据入库..." + ints.length + "条");
			}
			//更新进度表
			Dbm_analysis_schedule_tab schedule_tab = Dbo.queryOneObject(db, Dbm_analysis_schedule_tab.class,
					"SELECT * FROM " + Dbm_analysis_schedule_tab.TableName + " WHERE sys_class_code = ? AND "
							+ "ori_table_code = ?", algorithmsConf.getSys_code(), algorithmsConf.getTable_name())
					.orElseThrow(() -> new BusinessException("根据系统分类编号和表名在进度表查询不到数据"));
			schedule_tab.setFd_sche("1");
			schedule_tab.setFd_start_date(new SimpleDateFormat("yy-MM-dd HH:mm:ss").
					format(Calendar.getInstance().getTime()));
			schedule_tab.setFd_end_date(new SimpleDateFormat("yy-MM-dd HH:mm:ss").
					format(Calendar.getInstance().getTime()));
			schedule_tab.update(db);
			db.commit();
		} catch (Exception e) {
			throw new AppSystemException("函数依赖保存入库失败", e);
		}
	}

	public static void main(String[] args) {
		String[] tables = ("S10_I_AGENT_ACCOUNTS,S10_I_AGENT_ACCOUNTS_RECORD,S10_I_ATTACHMENT_FILES," +
				"S10_I_AUDIT_VALUATE_COND,S10_I_AUDIT_VALUATE_COND_OPER,S10_I_BILL_APP_DETAIL,S10_I_BILL_BASE_INFO," +
				"S10_I_BILL_APP_RANGE,S10_I_BILL_REPAIR_INFO,S10_I_BLUEPRINT,S10_I_BLUEPRINT_DRAFT," +
				"S10_I_CHECK_RESULT_NOT_BALANCE,S10_I_CHOU_ACCT").split(",");
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			for (String tab : tables) {
				AlgorithmsConf algorithmsConf = new AlgorithmsConf();
				algorithmsConf.setOutputFilePath("D:/algorithms_result_root_path/"+tab+"/");
				algorithmsConf.setSys_code("zzz33");
				algorithmsConf.setDatabase_name("HYSHF");
				algorithmsConf.setTable_name(tab);
				importDataToDatabase(algorithmsConf,db);
			}
		}
	}

}
