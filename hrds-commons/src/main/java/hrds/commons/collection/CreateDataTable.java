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
import hrds.commons.hadoop.hadoop_helper.HBaseHelper;
import hrds.commons.hadoop.readconfig.ConfigReader;
import hrds.commons.utils.Constant;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.io.compress.Compression;
import org.apache.hadoop.hbase.io.encoding.DataBlockEncoding;
import org.apache.hadoop.hbase.regionserver.BloomType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
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
//			createHBaseTable(db, layerBean, dqTableInfo, dqTableColumns);
			throw new BusinessException("创建 HBase 类型存储层数表，暂未实现!");
			//TODO 暂不支持
		} else if (store_type == Store_type.SOLR) {
			//TODO 暂不支持
			throw new BusinessException("创建 SOLR 类型存储层数表，暂未实现!");
		} else if (store_type == Store_type.ElasticSearch) {
			//TODO 暂不支持
			throw new BusinessException("创建 ElasticSearch 类型存储层数表，暂未实现!");
		} else if (store_type == Store_type.MONGODB) {
			//TODO 暂不支持
			throw new BusinessException("创建 MONGODB 类型存储层数表，暂未实现!");
		} else {
			throw new BusinessException("创建存储层数表时,未找到匹配的存储层类型!");
		}

	}

	@Method(desc = "创建关系型数据库数据表", logicStep = "创建关系型数据库数据表")
	@Param(name = "db", desc = "配置库DatabaseWrapper对象", range = "配置库DatabaseWrapper对象")
	@Param(name = "layerBean", desc = "LayerBean对象", range = "LayerBean对象")
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
	@Param(name = "layerBean", desc = "LayerBean对象", range = "LayerBean对象")
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
				String column_separator = "|"; //TODO Dq_table_info没有保存字段分隔符信息
				createTableSQL.append(" ROW FORMAT SERDE 'org.apache.hadoop.hive.contrib.serde2.MultiDelimitSerDe' WITH  " +
					"SERDEPROPERTIES (\"field.delim\"=\"").append(column_separator).append("\") stored as textfile");
			} else if (fileFormat == FileFormat.CSV) {
				createTableSQL.append(" ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.OpenCSVSerde' stored as TEXTFILE");
			} else if (fileFormat == FileFormat.DingChang) {
				throw new BusinessException("创建Hive类型表,暂不支持定长类型");
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

	@Method(desc = "创建HBase存储类型的数据表", logicStep = "创建HBase存储类型的数据表")
	@Param(name = "db", desc = "配置库DatabaseWrapper对象", range = "配置库DatabaseWrapper对象")
	@Param(name = "layerBean", desc = "LayerBean对象", range = "LayerBean对象")
	@Param(name = "dqTableInfo", desc = "表信息", range = "Dq_table_info")
	@Param(name = "dqTableColumns", desc = "待创建表的表字段信息", range = "List<Dq_table_column> 字段信息")
	private static void createHBaseTable(DatabaseWrapper db, LayerBean layerBean, Dq_table_info dqTableInfo,
	                                     List<Dq_table_column> dqTableColumns) {
		//获取表空间
		String name_space = dqTableInfo.getTable_space();
		//获取需要创建的HBase表名和表中文名
		String hbase_table_name = dqTableInfo.getTable_name();
		String hbase_table_ch_name = dqTableInfo.getCh_name();
		//校验不允许在系统命名空间下创建表
		if (name_space.equalsIgnoreCase(NamespaceDescriptor.SYSTEM_NAMESPACE_NAME_STR)) {
			throw new BusinessException("不允许在系统命名空间下创建表");
		}
		//如果表空间为空，则使用默认的表空间
		if (StringUtil.isBlank(name_space)) {
			name_space = NamespaceDescriptor.DEFAULT_NAMESPACE_NAME_STR;
		}
		//根据存储层信息获取存储层的 Configuration
		Configuration conf = ConfigReader.getConfiguration(layerBean);
		//创建HBase表
		try (HBaseHelper helper = HBaseHelper.getHelper(conf)) {
			//如果新表名存在,则抛出已经存在的异常
			if (helper.existsTable(name_space + ":" + hbase_table_name)) {
				throw new BusinessException("待创建的HBase表已经在存储层中存在! 表名: " + name_space + ":" + hbase_table_name);
			}
			//Admin
			Admin admin = helper.getAdmin();
			TableName tableName;
			//如果表空间不是默认的表空间: default ,则创建表空间
			if (!name_space.equalsIgnoreCase(NamespaceDescriptor.DEFAULT_NAMESPACE_NAME_STR)) {
				try {
					NamespaceDescriptor namespaceDescriptor = NamespaceDescriptor.create(name_space).build();
					admin.createNamespace(namespaceDescriptor);
				} catch (IOException ioe) {
					throw new BusinessException("创建HBase命名空间失败!" + name_space);
				}
				tableName = TableName.valueOf(name_space, hbase_table_name);
			} else {
				tableName = TableName.valueOf(hbase_table_name);
			}
			//数表详细信息
			HTableDescriptor hTableDescriptor = new HTableDescriptor(tableName);
			String[] column_familie_s = new String[]{"cf1", "cf2"}; //TODO column_familie_s Dq_table_info没有保存是列族信息信息
			for (String column_familie : column_familie_s) {
				if (StringUtil.isBlank(column_familie)) {
					continue;
				}
				//字段详细信息
				HColumnDescriptor col_desc = new HColumnDescriptor(column_familie);
				//设置最大版本数
				int max_version = 10;
				col_desc.setMaxVersions(max_version);
				//设置压缩(snappy)
				IsFlag is_compress = IsFlag.ofEnumByCode("1");//TODO Dq_table_info缺失is_compress信息
				if (is_compress == IsFlag.Shi) {
					col_desc.setCompressionType(Compression.Algorithm.SNAPPY);
				}
				//是否使用bloomFilter
				// -支持BloomType为(ROW: 根据KeyValue中的row来过滤storefile; ROWCOL: 根据KeyValue中的row+qualifier来过滤storefile)
				IsFlag is_use_bloom_filter = IsFlag.ofEnumByCode("1");//TODO Dq_table_info缺失is_use_bloom_filter信息
				if (is_use_bloom_filter == IsFlag.Shi) {
					String bloom_filter_type = "ROW"; //TODO Dq_table_info缺失bloom_filter_type信息
					BloomType bloomType = BloomType.valueOf(bloom_filter_type);
					if (bloomType == BloomType.ROW) {
						col_desc.setBloomFilterType(BloomType.ROW);
					} else if (bloomType == BloomType.ROWCOL) {
						col_desc.setBloomFilterType(BloomType.ROWCOL);
					} else {
						throw new BusinessException("BloomType类型不合法!"
							+ " {ROW: 根据KeyValue中的row来过滤storefile; ROWCOL: 根据KeyValue中的row+qualifier来过滤storefile}");
					}
				} else if (is_use_bloom_filter == IsFlag.Fou) {
					col_desc.setBloomFilterType(BloomType.NONE);
				}
				//设置HFile数据块大小,单位b（默认64 kb=65536 b）
				int block_size = 65536;
				col_desc.setBlocksize(block_size);
				//设置进缓存，优先考虑将该列族放入块缓存中，针对随机读操作相对较多的列族可以设置该属性为true
				IsFlag is_in_memory = IsFlag.ofEnumByCode("1");//TODO Dq_table_info缺失is_in_memory信息
				if (is_in_memory == IsFlag.Shi) {
					col_desc.setInMemory(Boolean.TRUE);
				} else if (is_in_memory == IsFlag.Fou) {
					col_desc.setInMemory(Boolean.FALSE);
				} else {
					throw new BusinessException("列族: " + column_familie
						+ ", 是否加入块缓存的配置不合法! is_in_memory: " + is_in_memory.getCode());
				}
				//设置数据块编码方式设置,如果不设置默认为 NONE
				String data_block_encoding = "NONE"; //TODO Dq_table_info缺失data_block_encoding信息
				DataBlockEncoding dataBlockEncoding = DataBlockEncoding.valueOf(data_block_encoding);
				if (dataBlockEncoding == DataBlockEncoding.NONE || dataBlockEncoding == DataBlockEncoding.PREFIX
					|| dataBlockEncoding == DataBlockEncoding.DIFF || dataBlockEncoding == DataBlockEncoding.FAST_DIFF
					|| dataBlockEncoding == DataBlockEncoding.PREFIX_TREE) {
					col_desc.setDataBlockEncoding(dataBlockEncoding);
				} else {
					throw new BusinessException("数据块编码方式不合法! data_block_encoding=" + dataBlockEncoding.name());
				}
				hTableDescriptor.addFamily(col_desc);
			}
			//设置预分区配置信息
			String pre_split = "SPLITNUM"; //TODO 代码项PrePartition(SPLITNUM,SPLITPOINS) Dq_table_info缺失pre_parm信息
			String pre_parm = "2000000"; //TODO Dq_table_info缺失pre_parm信息
			byte[][] splitKeys = null;
			if (StringUtil.isNotBlank(pre_split)) {
				splitKeys = null; //TODO 代码项没有 根据代码项和分区参数设置预分区信息
			}
			if( splitKeys != null ) {
				admin.createTable(hTableDescriptor, splitKeys);
			}else {
				admin.createTable(hTableDescriptor);
			}
			//创建Hbase映射hive表


		} catch (IOException e) {
			e.printStackTrace();
			throw new BusinessException("执行HBase类型存储层创建表失败! table_name=" + hbase_table_name);
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
