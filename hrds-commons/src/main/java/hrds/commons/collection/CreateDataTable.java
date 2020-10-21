package hrds.commons.collection;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.conf.Dbtype;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.codes.FileFormat;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.StoreLayerAdded;
import hrds.commons.codes.Store_type;
import hrds.commons.collection.bean.LayerBean;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.Constant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "创建数据表", author = "BY-HLL", createdate = "2020/7/2 0002 下午 03:41")
public class CreateDataTable {

	private static final Logger logger = LogManager.getLogger();


	@Method(desc = "创建表", logicStep = "创建表")
	@Param(name = "db", desc = "配置库DatabaseWrapper对象", range = "配置库DatabaseWrapper对象")
	@Param(name = "dsl_id", desc = "存储层配置id", range = "long类型")
	@Param(name = "dqTableInfo", desc = "待创建表的表信息", range = "Dq_table_info待创建表的表信息")
	@Param(name = "dqTableColumns", desc = "待创建表的表字段信息", range = "List<Dq_table_column> 字段信息")
	public static void createDataTableByStorageLayer(DatabaseWrapper db, long dsl_id, Dq_table_info dqTableInfo,
	                                                 List<Dq_table_column> dqTableColumns) {
		//获取存储层信息
		LayerBean layerBean = SqlOperator.queryOneObject(db, LayerBean.class, "select * from " + Data_store_layer.TableName +
			" where dsl_id=?", dsl_id).orElseThrow(() -> (new BusinessException("获取存储层数据信息的SQL失败!")));
		//根据存储层定义创建数据表
		createDataTableByStorageLayer(db, layerBean, dqTableInfo, dqTableColumns);
	}

	@Method(desc = "创建表", logicStep = "创建表")
	@Param(name = "db", desc = "配置库DatabaseWrapper对象", range = "配置库DatabaseWrapper对象")
	@Param(name = "layerBean", desc = "LayerBean对象", range = "LayerBean对象")
	@Param(name = "dqTableInfo", desc = "待创建表的表信息", range = "Dq_table_info待创建表的表信息")
	@Param(name = "dqTableColumns", desc = "待创建表的表字段信息", range = "List<Dq_table_column> 字段信息")
	public static void createDataTableByStorageLayer(DatabaseWrapper db, LayerBean layerBean, Dq_table_info dqTableInfo,
	                                                 List<Dq_table_column> dqTableColumns) {
		Store_type store_type = Store_type.ofEnumByCode(layerBean.getStore_type());
		if (store_type == Store_type.DATABASE) {
			//创建关系型数据库数据表
			createDatabaseTable(db, layerBean, dqTableInfo, dqTableColumns);
		} else if (store_type == Store_type.HIVE) {
			//创建Hive存储类型的数据表
			createHiveTable(db, layerBean, dqTableInfo, dqTableColumns);
		} else if (store_type == Store_type.HBASE) {
			//TODO 暂不支持
		} else if (store_type == Store_type.SOLR) {
			//TODO 暂不支持
		} else if (store_type == Store_type.ElasticSearch) {
			//TODO 暂不支持
		} else if (store_type == Store_type.MONGODB) {
			//TODO 暂不支持
		} else {
			throw new BusinessException("获取创建存储层表SQL时,未找到匹配的存储层类型!");
		}

	}

	@Method(desc = "创建关系型数据库数据表", logicStep = "创建关系型数据库数据表")
	@Param(name = "db", desc = "配置库DatabaseWrapper对象", range = "配置库DatabaseWrapper对象")
	@Param(name = "dbDataConn", desc = "存储层DatabaseWrapper对象", range = "存储层DatabaseWrapper对象")
	@Param(name = "dqTableInfo", desc = "表信息", range = "Dq_table_info")
	@Param(name = "dqTableColumns", desc = "待创建表的表字段信息", range = "List<Dq_table_column> 字段信息")
	private static void createDatabaseTable(DatabaseWrapper db, LayerBean layerBean, Dq_table_info dqTableInfo,
	                                        List<Dq_table_column> dqTableColumns) {
		//获取表空间
		String table_space = dqTableInfo.getTable_space();
		//获取表名
		String table_name = dqTableInfo.getTable_name();
		//使用存储层配置自定义Bean创建存储层链接
		DatabaseWrapper dbDataConn = null;
		try {
			dbDataConn = ConnectionTool.getDBWrapper(db, layerBean.getDsl_id());
			//根据表信息和字段信息设置建表语句
			StringBuilder createTableSQL = new StringBuilder();
			//ORACLE
			if (dbDataConn.getDbtype() == Dbtype.ORACLE) {
				//根据配置的表空间创建SCHEMA
				if (StringUtil.isNotBlank(table_space)) {
					int i = dbDataConn.execute("CREATE SCHEMA IF NOT EXISTS " + table_space);
					if (i != 0) {
						throw new BusinessException("创建表空间失败! table_space: " + table_space);
					}
				}
				//数据库类型是oarcle,判断表名长度不能大于30
				if (table_name.length() > 30) {
					throw new BusinessException("oracle数据库下表名长度不能超过30位! table_name: " + table_name);
				}
				//检查数据表是否在存储层中存在
				tableIsExistsStorageLayer(dbDataConn, table_space, table_name);
				//设置建表语句
				createTableSQL.append("CREATE TABLE");
				if (StringUtil.isNotBlank(table_space)) {
					createTableSQL.append(" ").append(table_space).append(".");
				}
				createTableSQL.append(" ").append(table_name);
				createTableSQL.append(" (");
				//主键字段信息
				List<String> pk_column_s = new ArrayList<>();
				for (Dq_table_column dqTableColumn : dqTableColumns) {
					//获取字段的附加信息
					List<Map<String, Object>> dcol_info_s = SqlOperator.queryList(db,
						"SELECT * FROM " + Dcol_relation_store.TableName + " dcol_rs" +
							" JOIN " + Data_store_layer_added.TableName + " dsl_add ON dcol_rs" + ".dslad_id=dsl_add.dslad_id " +
							" WHERE col_id=?", dqTableColumn.getField_id());
					//设置主键信息
					for (Map<String, Object> dcol_info : dcol_info_s) {
						StoreLayerAdded storeLayerAdded = StoreLayerAdded.ofEnumByCode(dcol_info.get("dsla_storelayer").toString());
						if (storeLayerAdded == StoreLayerAdded.ZhuJian) {
							pk_column_s.add(dqTableColumn.getColumn_name());
						}
					}
					//字段名
					String table_column = dqTableColumn.getColumn_name();
					//字段类型
					String column_type = dqTableColumn.getColumn_type();
					//字段长度
					String column_length = dqTableColumn.getColumn_length();
					//设置建表语句的字段信息
					createTableSQL.append(table_column).append(Constant.SPACE).append(column_type);
					if (!StringUtil.isEmpty(column_length) && !column_type.equals("int")
						&& !column_type.equals("boolean")) {
						createTableSQL.append("(").append(column_length).append(")");
					}
					//是否可为空标识
					IsFlag is_null = IsFlag.ofEnumByCode(dqTableColumn.getIs_null());
					if (is_null == IsFlag.Shi) {
						createTableSQL.append(Constant.SPACE).append("NULL");
					} else if (is_null == IsFlag.Fou) {
						createTableSQL.append(Constant.SPACE).append("NOT NULL");
					} else {
						throw new BusinessException("字段: column_name=" + table_column + " 的是否标记信息不合法!");
					}
					//拼接字段分隔 ","
					createTableSQL.append(",");
				}
				//根据字段选择主键标记设置建表语句
				if (!pk_column_s.isEmpty()) {
					createTableSQL.append("CONSTRAINT").append(Constant.SPACE);
					createTableSQL.append(table_name).append("_PK").append(Constant.SPACE);
					createTableSQL.append("PRIMARY KEY(").append(String.join(",", pk_column_s)).append(")");
					createTableSQL.append(",");
				}
			}
			//通用创建表语句
			else {
				//根据配置的表空间创建SCHEMA
				if (StringUtil.isNotBlank(table_space)) {
					int i = dbDataConn.execute("CREATE SCHEMA IF NOT EXISTS " + table_space);
					if (i != 0) {
						throw new BusinessException("创建表空间失败! table_space: " + table_space);
					}
				}
				//检查数据表是否在存储层中存在
				tableIsExistsStorageLayer(dbDataConn, table_space, table_name);
				//设置建表语句
				createTableSQL.append("CREATE TABLE IF NOT EXISTS");
				if (StringUtil.isNotBlank(table_space)) {
					createTableSQL.append(" ").append(table_space).append(".");
				}
				createTableSQL.append(" ").append(table_name);
				createTableSQL.append(" (");
				//主键字段信息
				List<String> pk_column_s = new ArrayList<>();
				for (Dq_table_column dqTableColumn : dqTableColumns) {
					//获取字段的附加信息
					List<Map<String, Object>> dcol_info_s = SqlOperator.queryList(db,
						"SELECT * FROM " + Dcol_relation_store.TableName + " dcol_rs" +
							" JOIN " + Data_store_layer_added.TableName + " dsl_add ON dcol_rs" + ".dslad_id=dsl_add.dslad_id " +
							" WHERE col_id=?", dqTableColumn.getField_id());
					//设置主键信息
					for (Map<String, Object> dcol_info : dcol_info_s) {
						StoreLayerAdded storeLayerAdded = StoreLayerAdded.ofEnumByCode(dcol_info.get("dsla_storelayer").toString());
						if (storeLayerAdded == StoreLayerAdded.ZhuJian) {
							pk_column_s.add(dqTableColumn.getColumn_name());
						}
					}
					//字段名
					String table_column = dqTableColumn.getColumn_name();
					//字段类型
					String column_type = dqTableColumn.getColumn_type();
					//字段长度
					String column_length = dqTableColumn.getColumn_length();
					//设置建表语句的字段信息
					createTableSQL.append(table_column).append(Constant.SPACE).append(column_type);
					if (!StringUtil.isEmpty(column_length) && !column_type.equals("int")
						&& !column_type.equals("boolean")) {
						createTableSQL.append("(").append(column_length).append(")");
					}
					//是否可为空标识
					IsFlag is_null = IsFlag.ofEnumByCode(dqTableColumn.getIs_null());
					if (is_null == IsFlag.Shi) {
						createTableSQL.append(Constant.SPACE).append("NULL");
					} else if (is_null == IsFlag.Fou) {
						createTableSQL.append(Constant.SPACE).append("NOT NULL");
					} else {
						throw new BusinessException("字段: column_name=" + table_column + " 的是否标记信息不合法!");
					}
					//拼接字段分隔 ","
					createTableSQL.append(",");
				}
				//根据字段选择主键标记设置建表语句
				if (!pk_column_s.isEmpty()) {
					createTableSQL.append("CONSTRAINT").append(Constant.SPACE);
					createTableSQL.append(table_name).append("_PK").append(Constant.SPACE);
					createTableSQL.append("PRIMARY KEY(").append(String.join(",", pk_column_s)).append(")");
					createTableSQL.append(",");
				}
			}
			//删除最后一个 ","
			createTableSQL.deleteCharAt(createTableSQL.length() - 1);
			//拼接结束的 ")"
			createTableSQL.append(")");
			//执行创建语句
			String execute_sql = String.valueOf(createTableSQL);
			logger.info("执行关系型数据库创建语句,SQL内容：" + execute_sql);
			int i = dbDataConn.ExecDDL(execute_sql);
			if (i != 0) {
				logger.error("指定关系型数据库存储层创建表失败! table_name: " + table_name);
				throw new BusinessException("表已经存在! table_name: " + table_name);
			}
			//提交db操作
			dbDataConn.commit();
			logger.info("指定关系型数据库存储层创建表成功! table_name: " + table_name);
		} catch (Exception e) {
			if (null != dbDataConn) {
				dbDataConn.rollback();
				logger.info("关系型数据库创建表时发生异常,回滚此次存储层的db操作!");
			}
			e.printStackTrace();
			throw new BusinessException("创建存储层数表发生异常!" + e.getMessage());
		} finally {
			if (null != dbDataConn) {
				dbDataConn.close();
				logger.info("关闭存储层db连接成功!");
			}
		}
	}


	@Method(desc = "创建Hive存储类型的数据表", logicStep = "创建Hive存储类型的数据表")
	@Param(name = "db", desc = "配置库DatabaseWrapper对象", range = "配置库DatabaseWrapper对象")
	@Param(name = "dbDataConn", desc = "存储层DatabaseWrapper对象", range = "存储层DatabaseWrapper对象")
	@Param(name = "dqTableInfo", desc = "表信息", range = "Dq_table_info")
	@Param(name = "dqTableColumns", desc = "待创建表的表字段信息", range = "List<Dq_table_column> 字段信息")
	private static void createHiveTable(DatabaseWrapper db, LayerBean layerBean, Dq_table_info dqTableInfo,
	                                    List<Dq_table_column> dqTableColumns) {
		//获取表空间
		String table_space = dqTableInfo.getTable_space();
		//获取表名
		String table_name = dqTableInfo.getTable_name();
		String table_ch_name = dqTableInfo.getCh_name();
		//使用存储层配置自定义Bean创建存储层链接
		DatabaseWrapper dbDataConn = null;
		try {
			dbDataConn = ConnectionTool.getDBWrapper(db, layerBean.getDsl_id());
			//根据表信息和字段信息设置建表语句
			StringBuilder createTableSQL = new StringBuilder();
			//根据配置的表空间创建SCHEMA
			if (StringUtil.isNotBlank(table_space)) {
				int i = dbDataConn.execute("CREATE SCHEMA IF NOT EXISTS " + table_space);
				if (i != 0) {
					throw new BusinessException("创建表空间失败! table_space: " + table_space);
				}
			}
			//检查数据表是否在存储层中存在
			tableIsExistsStorageLayer(dbDataConn, table_space, table_name);
			//设置建表语句
			createTableSQL.append("CREATE ");
			//判断是否为外表
			IsFlag is_external = IsFlag.ofEnumByCode("0");
			if (is_external == IsFlag.Shi) { //TODO Dq_table_info没有保存是否为外表信息
				createTableSQL.append(" EXTERNAL ");
			}
			createTableSQL.append(" TABLE IF NOT EXISTS");
			if (StringUtil.isNotBlank(table_space)) {
				createTableSQL.append(" ").append(table_space).append(".");
			}
			createTableSQL.append(" ").append(table_name);
			createTableSQL.append(" (");
			//主键字段信息
			Map<String, String> partition_column_map = new HashMap<>();
			for (Dq_table_column dqTableColumn : dqTableColumns) {
				//字段名
				String table_column = dqTableColumn.getColumn_name();
				String table_ch_column = dqTableColumn.getField_ch_name();
				//字段类型
				String column_type = dqTableColumn.getColumn_type();
				//字段长度
				String column_length = dqTableColumn.getColumn_length();
				//获取字段的附加信息
				List<Map<String, Object>> dcol_info_s = SqlOperator.queryList(db,
					"SELECT * FROM " + Dcol_relation_store.TableName + " dcol_rs" +
						" JOIN " + Data_store_layer_added.TableName + " dsl_add ON dcol_rs" + ".dslad_id=dsl_add.dslad_id " +
						" WHERE col_id=?", dqTableColumn.getField_id());
				//设置分区列信息
				for (Map<String, Object> dcol_info : dcol_info_s) {
					StoreLayerAdded storeLayerAdded = StoreLayerAdded.ofEnumByCode(dcol_info.get("dsla_storelayer").toString());
					//提出分区列
					if (storeLayerAdded == StoreLayerAdded.FenQuLie) {
						//如果字段类型是CHAR或者VARCHAR,则设置字段长度
						if (column_type.equalsIgnoreCase("CHAR") || column_type.equalsIgnoreCase("VARCHAR")) {
							column_type = column_type + "(" + column_length + ")";
						}
						//分区字段描述信息
						column_type = column_type + " COMMENT '" + table_ch_column + "'";
						partition_column_map.put(table_column, column_type);
					}
				}
				//设置建表语句的普通字段信息(分区列不包含的列)
				if (!partition_column_map.containsKey(table_column)) {
					createTableSQL.append(table_column).append(Constant.SPACE).append(column_type);
					if (column_type.equalsIgnoreCase("CHAR") || column_type.equalsIgnoreCase("VARCHAR")) {
						createTableSQL.append("(").append(column_length).append(")");
					}
					//字段描述信息
					createTableSQL.append(" COMMENT '").append(table_ch_column).append("'");
					//拼接字段分隔 ","
					createTableSQL.append(",");
				}
			}
			createTableSQL.deleteCharAt(createTableSQL.length() - 1);
			createTableSQL.append(')');
			//表中文名
			createTableSQL.append(" COMMENT '").append(table_ch_name).append("'");
			//根据字段选择分区标记设置建表语句,分区列有则创建,反之跳过
			if (!partition_column_map.isEmpty()) {
				createTableSQL.append(" PARTITIONED BY ").append("(");
				for (Map.Entry<String, String> entry : partition_column_map.entrySet()) {
					String patition_name = entry.getKey();
					String patition_type = entry.getValue();
					createTableSQL.append(patition_name).append(Constant.SPACE).append(patition_type).append(',');
				}
				createTableSQL.deleteCharAt(createTableSQL.length() - 1);
				createTableSQL.append(')');
			}
			//根据存储类型设置sql
			FileFormat fileFormat = FileFormat.ofEnumByCode(FileFormat.FeiDingChang.getCode()); //TODO Dq_table_info没有保存存储类型信息
			if (fileFormat == FileFormat.SEQUENCEFILE || fileFormat == FileFormat.PARQUET || fileFormat == FileFormat.ORC) {
				String hive_stored_as_type;
				if (fileFormat == FileFormat.PARQUET) {
					hive_stored_as_type = "parquet";
				} else if (fileFormat == FileFormat.ORC) {
					hive_stored_as_type = "orc";
				} else {
					hive_stored_as_type = "sequencefile";
				}
				createTableSQL.append(" stored as ").append(hive_stored_as_type);
			} else if (fileFormat == FileFormat.FeiDingChang) {
				String column_separator = "|";
				createTableSQL.append(" ROW FORMAT SERDE 'org.apache.hadoop.hive.contrib.serde2.MultiDelimitSerDe' WITH  " +
					"SERDEPROPERTIES (\"field.delim\"=\"").append(column_separator).append("\") stored as textfile");
			} else if (fileFormat == FileFormat.CSV) {
				createTableSQL.append(" ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.OpenCSVSerde' stored as TEXTFILE");
			} else if (fileFormat == FileFormat.DingChang) {
				throw new BusinessException("暂不支持定长类型直接加载到hive表");
			} else {
				throw new BusinessException("未找到匹配的存储类型! " + fileFormat);
			}
			//如果是外部表,拼接外部文件路径
			if (is_external == IsFlag.Shi) {
				String storage_path = "/hrds/hll/test/text"; //TODO 存储路径 /hrds/hll/test/ Dq_table_info没有保存是否包含表头信息
				if (StringUtil.isNotBlank(storage_path)) {
					createTableSQL.append(" location ").append("'").append(storage_path).append("'");
				}
			}
			//执行创建语句
			String execute_sql = String.valueOf(createTableSQL);
			logger.info("执行Hive创建语句,SQL内容：" + execute_sql);
			int i = dbDataConn.ExecDDL(execute_sql);
			if (i != 0) {
				logger.error("指定Hive数据库存储层创建表失败! table_name: " + table_name);
				throw new BusinessException("表已经存在! table_name: " + table_name);
			}
			logger.info("指定Hive存储层创建表成功! table_name: " + table_name);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException("创建Hive存储层数表发生异常!" + e.getMessage());
		} finally {
			if (null != dbDataConn) {
				dbDataConn.close();
				logger.info("关闭Hive存储层db连接成功!");
			}
		}
	}

	@Method(desc = "判断表是否在指定存储已经存在", logicStep = "判断表是否在指定存储已经存在,存在就抛出异常")
	private static void tableIsExistsStorageLayer(DatabaseWrapper dbDataConn, String tableSpace, String table_name) {
		boolean isExists;
		try {
			if (StringUtil.isNotBlank(tableSpace)) {
				isExists = dbDataConn.isExistTable(tableSpace + "." + table_name);
			} else {
				isExists = dbDataConn.isExistTable(table_name);
			}
			//如果已经存在,则抛出异常
			if (isExists) {
				throw new BusinessException("待创建的表在存储层中已经存在! table_name: " + table_name);
			}
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
}
