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
import org.apache.commons.lang3.StringUtils;
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
import java.util.UUID;

public class ImportHyUCCData {
	public static void importDataToDatabase(AlgorithmsConf algorithmsConf, DatabaseWrapper db) {
		Logger.getInstance().writeln("开始导入数据入库...");
		int pkLength;
		try {
			//获取文件，写数据入库
			List<String> strings = new ArrayList<>();
			String outPath = algorithmsConf.getOutputFilePath() + Constant.HYUCC_RESULT_PATH_NAME + "/part-00000";
			FileSystem fs = FileSystem.get(URI.create(outPath), ConfigReader.getConfiguration());
			BufferedReader reader = new BufferedReader(new InputStreamReader(fs.open(new Path(outPath)), StandardCharsets.UTF_8));
//			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(outPath),
//					StandardCharsets.UTF_8));
			String line;
			while ((line = reader.readLine()) != null) {
				strings.add(line);
			}
			List<String> columnList = new ArrayList<>();
			//最多只会找五个联合主键
			int i = 6;
			//重新整理，根据主键的长度来进行判断，获取最短主键,list的集合
			for (String str : strings) {
				if (StringUtils.isNotBlank(str)) {
					String pk_columns = str.substring(1, str.length() - 1);
					String[] pk_column_array = pk_columns.split(",");
					if (pk_column_array.length < i) {
						//清空之前的
						columnList.clear();
						columnList.add(pk_columns);
						i = pk_column_array.length;
					} else if (pk_column_array.length == i) {
						columnList.add(pk_columns);
					}
				}
			}
			if (columnList.size() > 0) {
				//获取第一行数据，判断是更新MMM_FIELD_INFO_TAB表还是插入JOINT_PK_TAB表
				pkLength = columnList.get(0).split(",").length;
				List<Object[]> pool = new ArrayList<>();
				if (pkLength == 1) {
					//恢复之前跑过的表字段为非主键
					db.execute("UPDATE dbm_mmm_field_info_tab SET col_pk='0' WHERE sys_class_code = ? " +
							"AND table_code = ?", algorithmsConf.getSys_code(), algorithmsConf.getTable_name());
					//单一主键 更新MMM_FIELD_INFO_TAB表
					String updateSql = "UPDATE dbm_mmm_field_info_tab SET col_pk='1' WHERE sys_class_code = ? AND" +
							" col_code = ? AND table_code = ?";
					for (String pk_column : columnList) {
						Object[] objects = new Object[3];
						objects[0] = algorithmsConf.getSys_code();
						objects[1] = pk_column;
						objects[2] = algorithmsConf.getTable_name();
						pool.add(objects);
					}
					if (pool.size() > 0) {
						int[] ints = db.execBatch(updateSql, pool);
						Logger.getInstance().writeln("更新数据表mmm_field_info_tab,数据入库..." + ints.length + "条");
					}
				} else {
					//删除之前跑过的存到联合主键表的数据
					db.execute("DELETE FROM dbm_joint_pk_tab WHERE sys_class_code = ? " +
							"AND table_code = ?", algorithmsConf.getSys_code(), algorithmsConf.getTable_name());
					//联合主键 插入JOINT_PK_TAB表
					String insertSql = "INSERT INTO dbm_joint_pk_tab(sys_class_code,table_code,group_code," +
							"col_code) VALUES (?,?,?,?)";
					for (String pk_column : columnList) {
						String[] columns = pk_column.split(",");
						String groupCode = UUID.randomUUID().toString();
						for (String col : columns) {
							Object[] objects = new Object[4];
							objects[0] = algorithmsConf.getSys_code();
//							objects[1] = algorithmsConf.getDatabase_name();
							objects[1] = algorithmsConf.getTable_name();
							objects[2] = groupCode;
							objects[3] = col;
//							objects[5] = new java.sql.Date(new java.util.Date().getTime());
//							objects[6] = new java.sql.Date(new java.util.Date().getTime());
							pool.add(objects);
						}
					}
					if (pool.size() > 0) {
						int[] ints = db.execBatch(insertSql, pool);
						Logger.getInstance().writeln("导入数据表joint_pk_tab,数据入库..." + ints.length + "条");
					}
				}
			}
			//更新进度表
			Dbm_analysis_schedule_tab schedule_tab = Dbo.queryOneObject(db, Dbm_analysis_schedule_tab.class,
					"SELECT * FROM " + Dbm_analysis_schedule_tab.TableName + " WHERE sys_class_code = ? AND "
							+ "ori_table_code = ?", algorithmsConf.getSys_code(), algorithmsConf.getTable_name())
					.orElseThrow(() -> new BusinessException("根据系统分类编号和表名在进度表查询不到数据"));
			schedule_tab.setPk_sche("1");
			schedule_tab.setPk_start_date(new SimpleDateFormat("yy-MM-dd HH:mm:ss").
					format(Calendar.getInstance().getTime()));
			schedule_tab.setPk_end_date(new SimpleDateFormat("yy-MM-dd HH:mm:ss").
					format(Calendar.getInstance().getTime()));
			schedule_tab.update(db);
			db.commit();
		} catch (Exception e) {
			throw new AppSystemException("HyUCC获取文件，写数据入库失败", e);
		}
	}


}
