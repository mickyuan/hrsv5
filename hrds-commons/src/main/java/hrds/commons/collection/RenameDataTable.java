package hrds.commons.collection;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.conf.Dbtype;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.codes.Store_type;
import hrds.commons.collection.bean.LayerBean;
import hrds.commons.entity.Data_store_layer;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.Constant;

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
		try (DatabaseWrapper dbDataConn = ConnectionTool.getDBWrapper(db, layerBean.getDsl_id())) {
			//设置无效表名
			String invalid_table_name = Constant.DQC_INVALID_TABLE + srcTableName;
			//初始化重命名sql
			String alterTableNameSQL;
			//获取当前操作数据层的数据层类型
			Store_type store_type = Store_type.ofEnumByCode(layerBean.getStore_type());
			//set_invalid:设置无效(重命名为带无效标签的表名)
			if (operation_type.equals(Constant.DM_SET_INVALID_TABLE)) {
				alterTableNameSQL = getAlterTableNameSQL(store_type, dbDataConn, tableSpace, srcTableName, invalid_table_name);
			}
			//restore:恢复(重命名为带有效的表名)
			else if (operation_type.equals(Constant.DM_RESTORE_TABLE)) {
				alterTableNameSQL = getAlterTableNameSQL(store_type, dbDataConn, tableSpace, invalid_table_name, srcTableName);
			}
			//customize:自定义重命名表
			else if (operation_type.equalsIgnoreCase(Constant.CUSTOMIZE)) {
				alterTableNameSQL = getAlterTableNameSQL(store_type, dbDataConn, tableSpace, srcTableName, destTableName);
			} else {
				throw new BusinessException("未知的重命名表操作类型! see@{remove:删除,restore:恢复,customize:自定义表名}");
			}
			//执行sql
			if (StringUtil.isBlank(alterTableNameSQL)) {
				throw new BusinessException("修改数据表名称的SQL为空!");
			}
			//执行修改sql
			int execute = SqlOperator.execute(dbDataConn, alterTableNameSQL);
			//校验修改结果
			if (execute != 0) {
				throw new BusinessException("修改数据表名称的SQL,执行失败!");
			}
			SqlOperator.commitTransaction(dbDataConn);
		}
	}

	@Method(desc = "获取重命名表名的sql", logicStep = "获取重命名表名的sql")
	@Param(name = "store_type", desc = "存储层类型", range = "存储层类型")
	@Param(name = "dbDataConn", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
	@Param(name = "tableSpace", desc = "表空间", range = "String类型")
	@Param(name = "old_table_name", desc = "原始表名", range = "原始表名")
	@Param(name = "new_table_name", desc = "新表名", range = "新表名")
	@Return(desc = "重命名表名的sql", range = "重命名表名的sql")
	private static String getAlterTableNameSQL(Store_type store_type, DatabaseWrapper dbDataConn, String tableSpace,
	                                           String old_table_name, String new_table_name) {
		String alterTableNameSQL = "";
		//关系型数据库
		if (store_type == Store_type.DATABASE) {
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
		} else if (store_type == Store_type.HIVE) {
			//TODO 重命名 HIVE 层表配置暂未实现!
			throw new BusinessException("重命名 HIVE 类型表配置暂未实现!!");
		} else if (store_type == Store_type.HBASE) {
			//TODO 重命名 HBASE 层表配置暂未实现!
			throw new BusinessException("重命名 HBASE 类型表配置暂未实现!!");
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
		return alterTableNameSQL;
	}
}
