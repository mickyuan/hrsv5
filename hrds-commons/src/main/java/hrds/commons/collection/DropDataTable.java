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
import hrds.commons.exception.AppSystemException;
import hrds.commons.exception.BusinessException;

import java.util.List;

@DocClass(desc = "删除数据类", author = "BY-HLL", createdate = "2020/5/22 0022 下午 03:18")
public class DropDataTable {

	@Method(desc = "删除存储层下的数据表", logicStep = "删除存储层下的数据表")
	@Param(name = "tableName", desc = "查询数据的sql", range = "String类型")
	@Param(name = "db", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
	public static void dropTableByDataLayer(String tableName, DatabaseWrapper db) {
		//获取sql中解析出来的表属于的存储实体Bean
		List<LayerBean> layerBeans = ProcessingData.getLayerByTable(tableName, db);
		//根据存储层删除对应存储层下的表
		layerBeans.forEach(layerBean -> dropTableByDataLayer(db, layerBean, "", tableName));
	}

	@Method(desc = "根据存储层id删除表", logicStep = "根据存储层id删除表")
	@Param(name = "db", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
	@Param(name = "dsl_id", desc = "存储层配置id", range = "long类型")
	@Param(name = "tableSpace", desc = "表空间", range = "String类型")
	@Param(name = "tableName", desc = "查询数据的sql", range = "String类型")
	public static void dropTableByDataLayer(DatabaseWrapper db, long dsl_id, String tableSpace, String tableName) {
		//获取存储层信息
		LayerBean layerBean = SqlOperator.queryOneObject(db, LayerBean.class, "select * from " + Data_store_layer.TableName +
				" where dsl_id=?", dsl_id).orElseThrow(() -> (new BusinessException("获取存储层数据信息的SQL失败!")));
		dropTableByDataLayer(db, layerBean, "", tableName);
	}

	@Method(desc = "根据定义的存储层删除表", logicStep = "根据定义的存储层删除表")
	@Param(name = "tableName", desc = "查询数据的sql", range = "String类型")
	@Param(name = "db", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
	@Param(name = "layerBean", desc = "LayerBean对象", range = "LayerBean对象")
	public static void dropTableByDataLayer(DatabaseWrapper db, LayerBean layerBean, String tableSpace, String tableName) {
		try (DatabaseWrapper dbDataConn = ConnectionTool.getDBWrapper(db, layerBean.getDsl_id())) {
			//获取当前操作存储层类型
			Store_type store_type = Store_type.ofEnumByCode(layerBean.getStore_type());
			//根据表名获取删除表的SQL语句
			String dropTableSQL = getDropTableSQL(store_type, dbDataConn, tableSpace, tableName);
			//执行删除SQL
			int execute = SqlOperator.execute(dbDataConn, dropTableSQL);
			//校验修改结果
			if (execute != 0) {
				throw new BusinessException("修改关系型数据库表失败!");
			}
			//提交操作
			SqlOperator.commitTransaction(dbDataConn);
		} catch (Exception e) {
			throw new AppSystemException("删除表的sql执行失败!,请检查表名是否存在!", e);
		}
	}

	@Method(desc = "获取删除表的sql", logicStep = "获取删除表的sql")
	@Param(name = "store_type", desc = "存储层类型", range = "存储层类型")
	@Param(name = "dbDataConn", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
	@Param(name = "tableSpace", desc = "表空间", range = "String类型")
	@Param(name = "table_name", desc = "表名", range = "表名")
	@Return(desc = "删除表的sql", range = "删除表的sql")
	private static String getDropTableSQL(Store_type store_type, DatabaseWrapper dbDataConn, String tableSpace, String table_name) {
		String dropTableSQL = "";
		//关系型数据库
		if (store_type == Store_type.DATABASE) {
			//DB2V1 和 DB2V2
			if (dbDataConn.getDbtype() == Dbtype.DB2V1 || dbDataConn.getDbtype() == Dbtype.DB2V2) {
				if (StringUtil.isBlank(tableSpace)) {
					dropTableSQL = "DROP " + table_name;
				} else {
					dropTableSQL = "DROP " + tableSpace + "." + table_name;
				}
			}
			//TERADATA
			else if (dbDataConn.getDbtype() == Dbtype.TERADATA) {
				if (StringUtil.isBlank(tableSpace)) {
					dropTableSQL = "DROP TABLE " + table_name;
				} else {
					dropTableSQL = "DROP TABLE " + tableSpace + "." + table_name;
				}
			}
			//通用数据库语句
			else {
				if (StringUtil.isBlank(tableSpace)) {
					dropTableSQL = "DROP TABLE " + table_name;
				} else {
					dropTableSQL = "DROP TABLE " + tableSpace + "." + table_name;
				}
			}
		} else if (store_type == Store_type.HIVE) {
			if (StringUtil.isBlank(tableSpace)) {
				dropTableSQL = "DROP TABLE " + table_name;
			} else {
				dropTableSQL = "DROP TABLE " + tableSpace + "." + table_name;
			}
		} else if (store_type == Store_type.HBASE) {
			//TODO 删除 HBASE 层表配置暂未实现!
		} else if (store_type == Store_type.SOLR) {
			//TODO 删除 SOLR 层表配置暂未实现!
		} else if (store_type == Store_type.ElasticSearch) {
			//TODO 删除 ElasticSearch 层表配置暂未实现!
		} else if (store_type == Store_type.MONGODB) {
			//TODO 删除 MONGODB 层表配置暂未实现!
		} else {
			throw new BusinessException("获取删除存储层表SQL时,未找到匹配的存储层类型!");
		}
		return dropTableSQL;
	}
}
