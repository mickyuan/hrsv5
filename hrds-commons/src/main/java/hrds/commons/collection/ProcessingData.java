package hrds.commons.collection;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.codes.CollectType;
import hrds.commons.codes.DataSourceType;
import hrds.commons.codes.Store_type;
import hrds.commons.entity.*;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.DruidParseQuerySql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.*;

@DocClass(desc = "数据处理类，获取表的存储等信息", author = "xchao", createdate = "2020年3月31日 16:32:43")
public abstract class ProcessingData {

	private static final String SAMEJDBC = "SAMEJDBC";
	private static final String UNSAMEJDBC = "UNSAMEJDBC";
	private static final String OTHER = "OTHER";

	public void Extracting() {

	}

	@Method(desc = "根据表面获取该表相应的存储信息", logicStep = "1")
	@Param(name = "tableName", desc = "数据表明", range = "只能是集市、采集等表中的tablename字段")
	@Param(name = "db", desc = "数据库db操作对象", range = "不可为空")
	@Return(desc = "查询出来的rs", range = "数据")
	public void getSQLEngine(String sql, DatabaseWrapper db) {

		List<String> listTable = DruidParseQuerySql.parseSqlTableToList(sql);
		/**
		 * 获取所有表的存储层，记录所有表的存储层ID
		 */
		Map<String, Map<String, Object>> allTableLayer = new HashMap<>();
		for (String tableName : listTable) {
			Map<String, Object> tableLayer = getTableLayer(tableName, db);
			//更加table获取每张表不同的存储信息，有可能一张表存储不同的目的地，所以这里是list
			List<Map<String, Object>> ll = (List<Map<String, Object>>) tableLayer.get(tableName);
			for (Map<String, Object> objectMap : ll) {
				String layer_id = String.valueOf(objectMap.get("dsl_id"));
				Map<String, Object> layer = allTableLayer.get(layer_id);
				/**
				 * 根据id获取map中是否有存储层信息，如果没有，直接添加表的list将存储层信息添加
				 * 如果有，将表表的信息添加的list中就ok
				 */
				if (layer != null) {
					List<String> arrayTable = (List<String>) layer.get("tableName");
					if (!arrayTable.contains(tableName)) arrayTable.add(tableName);
					layer.put("tableName", arrayTable);
					allTableLayer.put(layer_id, layer);
				} else {
					List<String> arrayTable = new ArrayList<>();
					arrayTable.add(tableName);
					objectMap.put("tableName", arrayTable);
					allTableLayer.put(layer_id, objectMap);
				}
			}
		}
		/**
		 * 这里开始判断每个表在哪里存储
		 */
		String isType = OTHER;
		Map<String, Object> useMapLayer = new HashMap<>();
		Iterator<String> iter = allTableLayer.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			Map<String, Object> objectMap = allTableLayer.get(key);
			String store_type = objectMap.get("store_type").toString();
			List<String> tableName = (List<String>) objectMap.get("tableName");
			/**
			 * 1、如果有一个list中的表个数和解析的表个数一样，也就是说所有的表都在一个存储层中存在，所有直接用这个存储层的信息即可
			 * 2、如果不一样，所有的都是jdbc
			 */
			if (tableName.size() == listTable.size()) {
				isType = SAMEJDBC;
				useMapLayer = objectMap;
				break;
			} else if (Store_type.DATABASE != Store_type.ofEnumByCode(store_type) && Store_type.HIVE != Store_type.ofEnumByCode(store_type)) {
				isType = UNSAMEJDBC;
			} else {
				isType = OTHER;
			}
		}
		//第一、判断所有的表是不是使用了同一个存储层，直接使用sql的方式就可以完成
		//判断所有表的layer_id是不是一个，如给一个，就简单了
		if (isType.equals(SAMEJDBC)) {
			String store_type = useMapLayer.get("store_type").toString();
			Long dsl_id = (Long) useMapLayer.get("dsl_id");
			//判断是不是关系型数据库和hive，因为这两个是支持jdbc的连接的
			if (Store_type.DATABASE == Store_type.ofEnumByCode(store_type) || Store_type.HIVE == Store_type.ofEnumByCode(store_type)) {
				getResultSet(sql, db, dsl_id);
			} else if (Store_type.HBASE == Store_type.ofEnumByCode(store_type)) {
				 //TODO 数据都在hbase的查询实现方式
			} else if (Store_type.MONGODB == Store_type.ofEnumByCode(store_type)) {
				//TODO 数据都在MONGODB的查询实现方式
			}
		}
		//第二、判断所有的表是不是都使用的jdbc的方式
		else if (isType.equals(UNSAMEJDBC)) {
			//TODO 数据都在关系型数据库，也就说都可以使用jdbc的方式的实现方式
		}
		//第三、其他，包含了不同的存储，如jdbc、hbase、solr等不同给情况
		else {
			// TODO 混搭模式
		}
	}
	/**
	 * 获取表的存储位置
	 * @param tableName {@link String} 表名
	 * @param db        {@link DatabaseWrapper} db
	 */
	private static Map<String, Object> getTableLayer(String tableName, DatabaseWrapper db) {
		Map<String, Object> mapTaberLayer = new HashMap<>();
		/**
		 * 查询贴元表信息，也就是通过数据采集过来的数据表
		 */
		SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance()
				.addSql("select * from " + Data_store_reg.TableName + " where collect_type in (?,?) and hyren_name = ?")
				.addParam(CollectType.DBWenJianCaiJi.getCode()).addParam(CollectType.ShuJuKuCaiJi.getCode())
				.addParam(tableName);
		Optional<Data_store_reg> opdsr = SqlOperator.queryOneObject(db, Data_store_reg.class, asmSql.sql(), asmSql.params());
		if (opdsr.isPresent()) {
			Data_store_reg dsr = opdsr.get();
			Long table_id = dsr.getTable_id();
			List<Map<String, Object>> maps = SqlOperator.queryList(db,
					"select dsl.dsl_id,dsl.dsl_name,dsl.store_type from "
							+ Table_storage_info.TableName + " tsi join " + Data_relation_table.TableName + " drt "
							+ "on tsi.storage_id = drt.storage_id join " + Data_store_layer.TableName + " dsl "
							+ "on drt.dsl_id = dsl.dsl_id where tsi.table_id = ?", table_id);
			//记录数据表在哪个系统存储层
			mapTaberLayer.put("dataSourceType", DataSourceType.DCL.getCode());
			mapTaberLayer.put(tableName, maps);
			return mapTaberLayer;
		}
		/**
		 * 查询集市表信息，通过数据集市产生的数表
		 */
		List<Map<String, Object>> dslMap = SqlOperator.queryList(db,
				"select dsl.dsl_id,dsl.dsl_name,dsl.store_type from "
						+ Dm_datatable.TableName + " dd join  " + Dm_relation_datatable.TableName + " drd " +
						"on dd.datatable_id = drd.datatable_id join " + Data_store_layer.TableName + " dsl " +
						"on drd.dsl_id = dsl.dsl_id where datatable_en_name = ?", tableName);
		if (dslMap.size() != 0) {
			mapTaberLayer.put("dataSourceType", DataSourceType.DML.getCode());
			mapTaberLayer.put(tableName, dslMap);//key 为表名+存储层ID
			return mapTaberLayer;
		}
		/**
		 * TODO 这里以后需要添加加工数据、机器学习、流数据、系统管理维护的表、系统管理等
		 */
		return null;
	}
	/**
	 * 实现数据库查询的方式
	 * @param sql {@like String} 查询的sql语句
	 * @param db
	 * @param dsl_id
	 */
	private void getResultSet(String sql, DatabaseWrapper db, long dsl_id) {
		List<Map<String, Object>> dataStoreConfBean = SqlOperator.queryList(db,
				"select * from data_store_layer_attr where dsl_id = ?", dsl_id);
		try (DatabaseWrapper dbDataConn = ConnectionTool.getDBWrapper(dataStoreConfBean)) {
			ResultSet rs = dbDataConn.queryGetResultSet(sql);
			ResultSetMetaData meta = rs.getMetaData();
			int cols = meta.getColumnCount();
			while (rs.next()){
				Map<String, Object> result = new HashMap<String, Object>();
				for (int i = 0; i < cols; i++) {
					String columnName = meta.getColumnName(i+1).toLowerCase();
					result.put(columnName, rs.getObject(i+1));
				}
				dealLine(result);
			}
		} catch (Exception e) {
			throw new AppSystemException("系统不支持该数据库类型",e);
		}
	}
	public abstract void dealLine(Map<String,Object> map) throws Exception;



	/**
	 * 根据表名获取存储层的信息
	 *
	 * @param tableName
	 * @param db
	 */
	public static List<Map<String, String>> getLayerByTable(String tableName, DatabaseWrapper db) {
		Map<String, Object> tableLayer = getTableLayer(tableName, db);
		DataSourceType dataSourceType = DataSourceType.ofEnumByCode(tableLayer.get("dataSourceType").toString());
		List<Map<String, Object>> dslMap = (List<Map<String, Object>>) tableLayer.get(tableName);
		List<Map<String, String>> arryLayer = new ArrayList<>();
		for (Map<String, Object> objectMap : dslMap) {
			List<Map<String, Object>> dataStoreConfBean =
					SqlOperator.queryList(db, "select * from data_store_layer_attr where dsl_id = ?", objectMap.get("dsl_id"));
			Map<String, String> layerMap = ConnectionTool.getLayerMap(dataStoreConfBean);
			arryLayer.add(layerMap);
		}
		return arryLayer;
	}
}