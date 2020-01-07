package hrds.commons.zTree.utils;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.web.util.Dbo;
import hrds.commons.utils.CommonVariables;
import hrds.commons.utils.PathUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "获取公共层的数据信息", author = "BY-HLL", createdate = "2019/12/24 0024 上午 09:33")
public class PublicLayer {

	private static final SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();

	private static final String[] databaseTy = {"Hive", "HBase", "Mpp", "Spark"};
	static final String HIVE = databaseTy[0];
	static final String HBASE = databaseTy[1];
	static final String MPP = databaseTy[2];
	static final String CARBONDATA = databaseTy[3];

	@Method(desc = "添加默认的2个数据库类型信息",
			logicStep = "1.判断是否有hadoop环境,有则添加hadoop数据存储信息,没有则添加海云库信息")
	@Return(desc = "数据库类型信息的list", range = "无限制")
	static List<Map<String, Object>> getDatabasesType() {

		List<Map<String, Object>> dbObjMapList = new ArrayList<>();
		if (CommonVariables.HAS_HADOOP_ENV) {
			for (String s : databaseTy) {
				Map<String, Object> dbObjMap = new HashMap<>();
				dbObjMap.put("name", s);
				dbObjMap.put("id", s);
				dbObjMap.put("source", PathUtil.UDL);
				dbObjMapList.add(dbObjMap);
			}
		} else {
			Map<String, Object> dbObjMap = new HashMap<>();
			String type = "海云库";
			dbObjMap.put("name", type);
			dbObjMap.put("id", "hyshf_" + PathUtil.UDL);
			dbObjMap.put("source", PathUtil.UDL);
			dbObjMapList.add(dbObjMap);
		}
		return dbObjMapList;
	}

	@Method(desc = "Public数据库表空间(Hive/HBase/Mpp)",
			logicStep = "1.hive库下的表空间" +
					"2.HBase库下的表空间" +
					"3.mpp库下的表空间" +
					"4.carbonData库下的表空间")
	@Param(name = "database_type", desc = "存储库类型", range = "hive,HBase,mpp,Spark")
	@Return(desc = "表空间信息List", range = "无限制")
	static List<Map<String, Object>> dataBasesInfo(String database_type) {
		Result rs;
		if ("hive".equalsIgnoreCase(database_type)) {
			rs = Dbo.queryResult("select *,info_id as id,table_space as space from sys_table_info WHERE " +
					"table_type='0'");
		} else if ("HBase".equalsIgnoreCase(database_type)) {
			rs = Dbo.queryResult("select *,info_id as id,table_space as space from sys_table_info WHERE " +
					"table_type='1'");
		} else if ("mpp".equalsIgnoreCase(database_type)) {
			rs = Dbo.queryResult("select *,info_id as id,table_space as space from sys_table_info WHERE " +
					"table_type='2'");
		} else {
			rs = Dbo.queryResult("select *,info_id as id,table_space as space from sys_table_info WHERE " +
					"table_type='3'");
		}
		return rs.toList();
	}

	//数据库类型信息
	@Method(desc = "数据库类型信息",
			logicStep = "1.获取数据库类型信息")
	@Param(name = "dbTypes", desc = "数据库类型", range = "数据库类型列表")
	@Param(name = "dbInfos", desc = "数据库类型信息", range = "数据库类型信息列表")
	@Param(name = "rootName", desc = "数据存储层", range = "数据存储层")
	@Return(desc = "返回值说明", range = "返回值取值范围")
	static List<Map<String, String>> getDataBasesType(List<Map<String, String>> dbTypes,
	                                                  List<Map<String, String>> dbInfos, String rootName) {
		for (Map<String, String> dbType : dbTypes) {
			String dbName = dbType.get("name");
			String id = dbType.get("id");
			dbType.put("description", dbName);
			dbType.put("rootName", id);
			dbType.put("pId", rootName);
			dbType.put("id", dbName);
			dbType.put("agent_layer", id);
			dbType.put("source", PathUtil.UDL);
			dbType.put("isParent", "true");
			dbInfos.add(dbType);
		}
		return dbInfos;
	}

	@Method(desc = "数据库表空间信息",
			logicStep = "1.数据表空间列表为空,返回数据表空间列表List" +
					"2.设置数据表空间信息")
	@Param(name = "dbsList", desc = "数据表空间名列表", range = "List集合数据")
	@Param(name = "dbsInfos", desc = "数据表空间信息列表", range = "List集合数据")
	@Param(name = "rootName", desc = "数据存储库名称", range = "hive,Hbase,mpp,Spark")
	@Param(name = "parent_id", desc = "数据存储层名称", range = "PathUtil.UDL")
	@Return(desc = "数据库表空间信息的List集合", range = "无限制")
	static List<Map<String, Object>> getDataBaseTableSpaceInfo(List<Map<String, Object>> dbsList,
	                                                           List<Map<String, Object>> dbsInfos,
	                                                           String rootName, String parent_id) {
		return getDataBaseTableSpaceInfo(dbsList, dbsInfos, rootName, parent_id);
	}

	@Method(desc = "数据库表空间信息",
			logicStep = "1.数据表空间列表为空,返回数据表空间列表List" +
					"2.设置数据表空间信息")
	@Param(name = "dbsList", desc = "数据表空间名列表", range = "List集合数据")
	@Param(name = "dbsInfos", desc = "数据表空间信息列表", range = "List集合数据")
	@Param(name = "rootName", desc = "数据存储库名称", range = "hive,Hbase,mpp,Spark")
	@Param(name = "parent_id", desc = "数据存储层名称", range = "PathUtil.UDL")
	@Param(name = "type", desc = "数据存储层名称", range = "PathUtil.UDL", nullable = true)
	@Return(desc = "数据库表空间信息的List集合", range = "无限制")
	public static List<Map<String, Object>> getDataBaseTableSpaceInfo(List<Map<String, Object>> dbsList,
	                                                                  List<Map<String, Object>> dbsInfos,
	                                                                  String rootName, String parent_id, String type) {
		//1.数据表空间列表为空,返回数据表空间列表List
		if (dbsList.isEmpty()) {
			return dbsInfos;
		}
		//2.设置数据表空间信息
		Map<String, String> map = new HashMap<String, String>();
		for (Map<String, Object> dbsObj : dbsList) {
			String dbSpace = (String) dbsObj.get("space");
			if (!map.containsKey(dbSpace)) {
				dbsObj.put("name", dbSpace);
				dbsObj.put("description", dbSpace);
				dbsObj.put("space_name", dbSpace);
				dbsObj.put("parent_id", parent_id);
				dbsObj.put("show", true);
				if (StringUtil.isNotBlank(type)) {
					dbsObj.put("rootName", type);
				} else {
					dbsObj.put("rootName", rootName);
				}
				dbsObj.put("rootType", rootName);
				dbsObj.put("id", rootName.concat("_").concat(dbSpace));
				dbsObj.put("isParent", true);
				dbsObj.put("source", PathUtil.UDL);
				map.put(dbSpace, dbSpace);
				dbsInfos.add(dbsObj);
			}
		}
		return dbsList;
	}

	@Method(desc = "获取表空间下的所有表名称",
			logicStep = "1.获取表空间下的所有表名称")
	@Param(name = "id", desc = "表空间id", range = "hyshf,default")
	@Param(name = "type", desc = "表存储库类型", range = "hive,HBase,mpp,Spark")
	@Return(desc = "表空间下的所有表名称List", range = "无限制")
	static List<Map<String, Object>> getTableNameInfo(String id, String type) {
		//1.获取表空间下的所有表名称
		return getTableNameInfo(id, type, null);
	}

	@Method(desc = "获取表空间下的表名称",
			logicStep = "1.获取表空间名称" +
					"2.获取表空间下的表名称,如果表空间id为空,则根据表名检索")
	@Param(name = "id", desc = "表空间id", range = "hyshf,default")
	@Param(name = "type", desc = "表存储库类型", range = "hive,HBase,mpp,Spark")
	@Param(name = "searchName", desc = "表名", range = "检索表名", nullable = true)
	@Return(desc = "检索出来的表名称List", range = "无限制")
	public static List<Map<String, Object>> getTableNameInfo(String id, String type, String searchName) {
		//1.获取表空间名称
		String space = StringUtil.split(id, "_").get(1);
		//2.获取表空间下的表名称,如果表空间id为空,则根据表名检索
		Result rs;
		asmSql.clean();
		asmSql.addSql("select *,ch_name as table_ch_name from sys_table_info WHERE table_type = ?");
		if (PublicLayer.HIVE.equals(type)) {
			asmSql.addParam("'0'");
		} else if (PublicLayer.HBASE.equals(type)) {
			asmSql.addParam("'1'");
		} else if (PublicLayer.MPP.equals(type)) {
			asmSql.addParam("'2'");
		} else {
			asmSql.addParam("'3'");
		}
		if (StringUtil.isBlank(id)) {
			asmSql.addSql(" AND ( table_name LIKE ? OR ch_name LIKE ? )").addParam('%' + searchName + '%')
					.addParam('%' + searchName + '%');
		} else {
			asmSql.addSql("AND table_space = ?").addParam(space);
		}
		rs = Dbo.queryResult(asmSql.sql(), asmSql.params());
		return rs.toList();
	}

	@Method(desc = "获取表空间下的表信息",
			logicStep = "1.数据表列表为空,返回数据表列表List" +
					"2.设置表信息列表")
	@Param(name = "tables", desc = "表列表", range = "List集合数据")
	@Param(name = "tableInfos", desc = "表信息列表", range = "List集合数据")
	@Param(name = "spaceTable", desc = "表空间名", range = "String类型,无限制")
	@Param(name = "parent_id", desc = "表空间名", range = "String类型,无限制")
	@Return(desc = "返回值说明", range = "返回值取值范围")
	static List<Map<String, String>> getTableInfo(List<Map<String, String>> tables,
	                                              List<Map<String, String>> tableInfos,
	                                              String spaceTable, String parent_id) {
		//1.数据表列表为空,返回数据表列表List
		if (tables.isEmpty()) {
			return tableInfos;
		}
		//2.设置表信息列表
		for (Map<String, String> tableObj : tables) {
			String table_name = tableObj.get("table_name");
			String table_ch_name = tableObj.get("table_ch_name");
			table_ch_name = StringUtil.isNotBlank(table_ch_name) ? table_ch_name : table_name;
			String data_space;
			if (PublicLayer.HIVE.equalsIgnoreCase(parent_id)) {
				data_space = tableObj.get("table_space");
			} else if (PublicLayer.HBASE.equalsIgnoreCase(parent_id)) {
				data_space = tableObj.get("table_space");
			} else if (PublicLayer.MPP.equalsIgnoreCase(parent_id)) {
				data_space = "default";
			} else {
				data_space = StringUtil.isEmpty(tableObj.get("table_space")) ? CARBONDATA : tableObj.get(
						"table_space");
			}
			String id = tableObj.get("id");
			tableObj.put("name", table_ch_name);
			tableObj.put("table_name", table_name);
			tableObj.put("description", table_name);
			tableObj.put("pId", parent_id.concat("_").concat(data_space));
			tableObj.put("file_id", id);
			tableObj.put("parent_id", parent_id);
			tableObj.put("rootName", parent_id);
			tableObj.put("tableToSpace", spaceTable);
			tableObj.put("source", PathUtil.UDL);
			tableObj.put("isParent", "false");
			tableObj.put("show", "true");
			tableInfos.add(tableObj);
		}
		return tableInfos;
	}
}
