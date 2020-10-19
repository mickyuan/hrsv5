package hrds.commons.collection;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.conf.Dbtype;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.codes.Store_type;
import hrds.commons.collection.bean.LayerBean;
import hrds.commons.entity.Data_store_layer;
import hrds.commons.exception.BusinessException;
import hrds.commons.hadoop.hadoop_helper.HBaseHelper;
import hrds.commons.hadoop.readconfig.ConfigReader;
import hrds.commons.utils.Constant;
import org.apache.hadoop.conf.Configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@DocClass(desc = "重命名数据表", author = "BY-HLL", createdate = "2020/5/22 0022 下午 03:49")
public class RenameDataTable {

	@Method(desc = "根据表名重命名所有存储层下的数表", logicStep = "根据表名重命名所有存储层下的数表")
	@Param(name = "db", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
	@Param(name = "operation_type", desc = "操作类型 remove:删除,restore:恢复", range = "String了类型")
	@Param(name = "tableSpace", desc = "表空间", range = "String类型")
	@Param(name = "tableName", desc = "需要重命名的表名", range = "String类型")
	public static List<String> renameTableByDataLayer(DatabaseWrapper db, String operation_type, String tableSpace,
	                                                  String tableName) {
		//初始化返回结果集
		List<String> dsl_id_s = new ArrayList<>();
		//获取sql中解析出来的表属于的存储实体Bean
		List<LayerBean> tableLayers = ProcessingData.getLayerByTable(tableName, db);
		//根据存储层删除对应存储层下的表
		tableLayers.forEach(tableLayer -> {
			//设置返回结果集
			dsl_id_s.add(tableLayer.getDsl_id().toString());
			renameTableByDataLayer(db, tableLayer, operation_type, tableSpace, tableName);
		});
		return dsl_id_s;
	}

	@Method(desc = "根据存储层id重命名表", logicStep = "根据存储层id重命名表")
	@Param(name = "db", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
	@Param(name = "dsl_id", desc = "所属存储层id", range = "long 类型")
	@Param(name = "operation_type", desc = "操作类型 remove:删除,restore:恢复", range = "String类型")
	@Param(name = "tableSpace", desc = "表空间", range = "String类型")
	@Param(name = "tableName", desc = "需要重命名的表名", range = "String类型")
	public static void renameTableByDataLayer(DatabaseWrapper db, long dsl_id, String operation_type,
	                                          String tableSpace, String tableName) {
		//获取存储层信息
		LayerBean layerBean = SqlOperator.queryOneObject(db, LayerBean.class, "select * from " + Data_store_layer.TableName +
			" where dsl_id=?", dsl_id).orElseThrow(() -> (new BusinessException("获取存储层数据信息的SQL失败!")));
		layerBean.setLayerAttr(ConnectionTool.getLayerMap(db, layerBean.getDsl_id()));
		//重命名存储层下的表
		renameTableByDataLayer(db, layerBean, operation_type, tableSpace, tableName);
	}

	@Method(desc = "根据表名重命名存储层下的数表", logicStep = "根据表名重命名存储层下的数表")
	@Param(name = "db", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
	@Param(name = "layerBean", desc = "LayerBean对象", range = "LayerBean对象")
	@Param(name = "tableSpace", desc = "表空间", range = "String类型")
	@Param(name = "tableName", desc = "表名", range = "String类型")
	public static void renameTableByDataLayer(DatabaseWrapper db, LayerBean layerBean, String operation_type,
	                                          String tableSpace, String tableName) {
		renameTableByDataLayer(db, layerBean, operation_type, tableSpace, tableName, "");
	}

	@Method(desc = "根据表名重命名存储层下的数表", logicStep = "根据表名重命名存储层下的数表")
	@Param(name = "db", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
	@Param(name = "layerBean", desc = "LayerBean对象", range = "LayerBean对象")
	@Param(name = "operation_type", desc = "操作类型 remove:删除,restore:恢复,customize:自定义重命名表", range = "String了类型")
	@Param(name = "tableSpace", desc = "表空间", range = "String类型")
	@Param(name = "tableName", desc = "需要重命名的表名", range = "String类型")
	public static void renameTableByDataLayer(DatabaseWrapper db, LayerBean layerBean, String operation_type,
	                                          String tableSpace, String srcTableName, String destTableName) {
		//设置无效表名
		String invalid_table_name = Constant.DQC_INVALID_TABLE + srcTableName;
		//set_invalid:设置无效(重命名为带无效标签的表名)
		if (operation_type.equals(Constant.DM_SET_INVALID_TABLE)) {
			alterTableName(db, layerBean, tableSpace, srcTableName, invalid_table_name);
		}
		//restore:恢复(重命名为带有效的表名)
		else if (operation_type.equals(Constant.DM_RESTORE_TABLE)) {
			alterTableName(db, layerBean, tableSpace, invalid_table_name, srcTableName);
		}
		//customize:自定义重命名表
		else if (operation_type.equalsIgnoreCase(Constant.CUSTOMIZE)) {
			alterTableName(db, layerBean, tableSpace, srcTableName, destTableName);
		} else {
			throw new BusinessException("未知的重命名表操作类型! see@{remove:删除,restore:恢复,customize:自定义表名}");
		}
	}

	@Method(desc = "获取重命名表名的sql", logicStep = "获取重命名表名的sql")
	@Param(name = "db", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
	@Param(name = "layerBean", desc = "LayerBean对象", range = "LayerBean对象")
	@Param(name = "tableSpace", desc = "表空间", range = "String类型")
	@Param(name = "old_table_name", desc = "原始表名", range = "原始表名")
	@Param(name = "new_table_name", desc = "新表名", range = "新表名")
	private static void alterTableName(DatabaseWrapper db, LayerBean layerBean, String tableSpace,
	                                   String old_table_name, String new_table_name) {
		String alterTableNameSQL;
		//获取当前操作数据层的数据层类型
		Store_type store_type = Store_type.ofEnumByCode(layerBean.getStore_type());
		//关系型数据库
		if (store_type == Store_type.DATABASE) {
			try (DatabaseWrapper dbDataConn = ConnectionTool.getDBWrapper(db, layerBean.getDsl_id())) {
				//DB2V1 和 DB2V2
				if (dbDataConn.getDbtype() == Dbtype.DB2V1 || dbDataConn.getDbtype() == Dbtype.DB2V2) {
					if (StringUtil.isBlank(tableSpace)) {
						alterTableNameSQL = "RENAME " + old_table_name + " TO " + new_table_name;
					} else {
						alterTableNameSQL = "RENAME " + tableSpace + "." + old_table_name + " TO " + tableSpace + "." + new_table_name;
					}
				}
				//TERADATA
				else if (dbDataConn.getDbtype() == Dbtype.TERADATA) {
					if (StringUtil.isBlank(tableSpace)) {
						alterTableNameSQL = "RENAME TABLE " + old_table_name + " TO " + new_table_name;
					} else {
						alterTableNameSQL = "RENAME TABLE " + tableSpace + "." + old_table_name + " TO " + tableSpace + "." + new_table_name;
					}
				}
				//通用数据库语句
				else {
					if (StringUtil.isBlank(tableSpace)) {
						alterTableNameSQL = "ALTER TABLE " + old_table_name + " RENAME TO " + new_table_name;
					} else {
						alterTableNameSQL = "ALTER TABLE " + tableSpace + "." + old_table_name + " RENAME TO " + tableSpace + "." + new_table_name;
					}
				}
				//执行sql
				if (StringUtil.isBlank(alterTableNameSQL)) {
					throw new BusinessException("修改关系型数据库的数据表名称的SQL为空!");
				}
				//执行修改sql
				int execute = SqlOperator.execute(dbDataConn, alterTableNameSQL);
				//校验修改结果
				if (execute != 0) {
					throw new BusinessException("修改关系型数据库的数据表名称的SQL,执行失败!");
				}
				//commit
				SqlOperator.commitTransaction(dbDataConn);
			}
		} else if (store_type == Store_type.HIVE) {
			try (DatabaseWrapper dbDataConn = ConnectionTool.getDBWrapper(db, layerBean.getDsl_id())) {
				if (StringUtil.isBlank(tableSpace)) {
					alterTableNameSQL = "ALTER TABLE " + old_table_name + " RENAME TO " + new_table_name;
				} else {
					alterTableNameSQL = "ALTER TABLE " + tableSpace + "." + old_table_name + " RENAME TO " + tableSpace + "." + new_table_name;
				}
				//执行sql
				if (StringUtil.isBlank(alterTableNameSQL)) {
					throw new BusinessException("修改HIVE类型的数据表名称的SQL为空!");
				}
				//执行修改sql
				int execute = SqlOperator.execute(dbDataConn, alterTableNameSQL);
				//校验修改结果,Hive类型存储层不需要commit
				if (execute != 0) {
					throw new BusinessException("修改HIVE类型的数据表名称的SQL,执行失败!");
				}
			}
		} else if (store_type == Store_type.HBASE) {
			//根据存储层信息获取存储层的 Configuration
			Configuration conf = ConfigReader.getConfiguration(layerBean);
			//获取HBaseHelper
			try (HBaseHelper helper = HBaseHelper.getHelper(conf)) {
				//1.判断需要重命名的表是否存在,如果存在则判断修改后的表名是否存在
				if (helper.existsTable(old_table_name)) {
					//如果新表名存在,则抛出已经存在的异常
					if (helper.existsTable(new_table_name)) {
						throw new BusinessException("重命名HBase表时,修改后的表名已经存在!");
					}
					//重命名表
					helper.renameTable(old_table_name, new_table_name);
				}
			} catch (IOException e) {
				e.printStackTrace();
				throw new BusinessException("重名HBase表失败!");
			}
		} else if (store_type == Store_type.SOLR) {
			//TODO 重命名 SOLR 层表配置暂未实现!
			throw new BusinessException("重命名 SOLR 类型表配置暂未实现!!");
		} else if (store_type == Store_type.ElasticSearch) {
			//TODO 重命名 ElasticSearch 层表配置暂未实现!
			throw new BusinessException("重命名 ElasticSearch 类型表配置暂未实现!!");
		} else if (store_type == Store_type.MONGODB) {
			//TODO 重命名 MONGODB 层表配置暂未实现!
			throw new BusinessException("重命名 MONGODB 类型表配置暂未实现!!");
		} else {
			throw new BusinessException("重命名为无效表时,未找到匹配的存储层类型!");
		}
	}
}
