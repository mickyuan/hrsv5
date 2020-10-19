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
import org.apache.hadoop.conf.Configuration;

import java.io.IOException;
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
		//添加存储层配置信息到 layerBean
		layerBean.setLayerAttr(ConnectionTool.getLayerMap(db, layerBean.getDsl_id()));
		dropTableByDataLayer(db, layerBean, "", tableName);
	}

	@Method(desc = "根据定义的存储层删除表", logicStep = "根据定义的存储层删除表")
	@Param(name = "db", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
	@Param(name = "layerBean", desc = "LayerBean对象", range = "LayerBean对象")
	@Param(name = "tableSpace", desc = "表空间", range = "String类型")
	@Param(name = "tableName", desc = "查询数据的sql", range = "String类型")
	public static void dropTableByDataLayer(DatabaseWrapper db, LayerBean layerBean, String tableSpace, String tableName) {
		try (DatabaseWrapper dbDataConn = ConnectionTool.getDBWrapper(db, layerBean.getDsl_id())) {
			//获取当前操作存储层类型
			Store_type store_type = Store_type.ofEnumByCode(layerBean.getStore_type());
			//关系型数据库
			String dropTableSQL;
			if (store_type == Store_type.DATABASE) {
				//DB2V1 和 DB2V2
				if (dbDataConn.getDbtype() == Dbtype.DB2V1 || dbDataConn.getDbtype() == Dbtype.DB2V2) {
					if (StringUtil.isBlank(tableSpace)) {
						dropTableSQL = "DROP " + tableName;
					} else {
						dropTableSQL = "DROP " + tableSpace + "." + tableName;
					}
				}
				//TERADATA
				else if (dbDataConn.getDbtype() == Dbtype.TERADATA) {
					if (StringUtil.isBlank(tableSpace)) {
						dropTableSQL = "DROP TABLE " + tableName;
					} else {
						dropTableSQL = "DROP TABLE " + tableSpace + "." + tableName;
					}
				}
				//通用数据库语句
				else {
					if (StringUtil.isBlank(tableSpace)) {
						dropTableSQL = "DROP TABLE " + tableName;
					} else {
						dropTableSQL = "DROP TABLE " + tableSpace + "." + tableName;
					}
				}
				//执行删除SQL
				int execute = SqlOperator.execute(dbDataConn, dropTableSQL);
				//校验修改结果
				if (execute != 0) {
					throw new BusinessException("修改关系型数据库表失败!");
				}
				//提交操作
				SqlOperator.commitTransaction(dbDataConn);
			} else if (store_type == Store_type.HIVE) {
				if (StringUtil.isBlank(tableSpace)) {
					dropTableSQL = "DROP TABLE " + tableName;
				} else {
					dropTableSQL = "DROP TABLE " + tableSpace + "." + tableName;
				}
				//执行删除SQL
				int execute = SqlOperator.execute(dbDataConn, dropTableSQL);
				//校验修改结果
				if (execute != 0) {
					throw new BusinessException("删除Hive存储类型的数据表失败!");
				}
			} else if (store_type == Store_type.HBASE) {
				//根据存储层信息获取存储层的 Configuration
				Configuration conf = ConfigReader.getConfiguration(layerBean);
				//获取HBaseHelper
				try (HBaseHelper helper = HBaseHelper.getHelper(conf)) {
					//1.判断需要重命名的表是否存在,如果存在则判断修改后的表名是否存在
					if (helper.existsTable(tableName)) {
						//重命名表
						helper.dropTable(tableName);
					}
				} catch (IOException e) {
					e.printStackTrace();
					throw new BusinessException("删除HBase存储类型的数据表失败!");
				}
			} else if (store_type == Store_type.SOLR) {
				//TODO 删除 SOLR 层表配置暂未实现!
				throw new BusinessException("删除 SOLR 层表配置暂未实现!!");
			} else if (store_type == Store_type.ElasticSearch) {
				//TODO 删除 ElasticSearch 层表配置暂未实现!
				throw new BusinessException("删除 ElasticSearch 层表配置暂未实现!!");
			} else if (store_type == Store_type.MONGODB) {
				//TODO 删除 MONGODB 层表配置暂未实现!
				throw new BusinessException("删除 MONGODB 层表配置暂未实现!!");
			} else {
				throw new BusinessException("获取删除存储层表SQL时,未找到匹配的存储层类型!");
			}
		}
	}
}
