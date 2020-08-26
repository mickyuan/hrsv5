package hrds.b.biz.websqlquery;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.Validator;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.cache.CacheConfBean;
import hrds.commons.cache.CacheObj;
import hrds.commons.cache.ConcurrentHashMapCacheUtil;
import hrds.commons.collection.ProcessingData;
import hrds.commons.entity.*;
import hrds.commons.tree.background.TreeNodeInfo;
import hrds.commons.tree.background.bean.TreeConf;
import hrds.commons.tree.commons.TreePageSource;
import hrds.commons.tree.foreground.ForegroundTreeUtil;
import hrds.commons.tree.foreground.bean.TreeDataInfo;
import hrds.commons.utils.DataTableUtil;
import hrds.commons.utils.DruidParseQuerySql;
import hrds.commons.utils.tree.Node;
import hrds.commons.utils.tree.NodeDataConvertedTreeList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "WebSql查询处理类", author = "BY-HLL", createdate = "2019/10/25 0025 下午 05:51")
public class WebSqlQueryAction extends BaseAction {

	private static final Logger logger = LogManager.getLogger();

	//平台所有表及表信息Map集合
	private static final ConcurrentHashMapCacheUtil platformAllTableInfoCache = initPlatformAllTableInfo();

	@Method(desc = "根据SQL获取采集数据", logicStep = "初始化查询sql根据sql语句获取数据")
	private static ConcurrentHashMapCacheUtil initPlatformAllTableInfo() {
		logger.info("Start to initialize all table information of the platform.");
		//初始化缓存配置信息
		CacheConfBean cacheConfBean = new CacheConfBean();
		//设置缓存时间,缓存数据保存时间 {分钟 * 秒 * 毫秒}  默认值: 时间十分钟
		cacheConfBean.setCache_time(10 * 60 * 1000L);
		//设置缓存清理线程的清理频率 {分钟 * 秒 * 毫秒}
		cacheConfBean.setCache_cleaning_frequency(10 * 60 * 1000L);
		//设置缓存大小 默认值: 1000
		cacheConfBean.setCache_max_number(10000);
		ConcurrentHashMapCacheUtil platformAllTableInfoCache = new ConcurrentHashMapCacheUtil(cacheConfBean);
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//获取平台登记的表信息(不获取DQC层的表信息)
			//初始化查询Sql
			SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
			asmSql.clean();
			asmSql.addSql("SELECT * FROM (");
			//DCL DB文件采集
			asmSql.addSql("SELECT ti.table_id AS table_id,dsr.hyren_name AS table_name,tc.column_name AS column_name," +
					" tc.column_ch_name AS column_ch_name, tc.column_type AS column_type FROM " + Data_store_reg.TableName +
					" dsr JOIN " + Table_info.TableName + " ti ON dsr.database_id = ti.database_id" +
					" AND dsr.table_name = ti.table_name JOIN " + Table_column.TableName + " tc" +
					" ON ti.table_id = tc.table_id ");
			//DCL OBJ文件采集
			asmSql.addSql("UNION");
			asmSql.addSql(" SELECT oct.ocs_id AS table_id,oct.en_name AS table_name,ocs.column_name AS column_name," +
					" ocs.data_desc AS column_ch_name,ocs.column_type AS column_type FROM " + Object_collect_task.TableName + " oct" +
					" JOIN " + Object_collect_struct.TableName + " ocs ON oct.ocs_id=ocs.ocs_id" +
					" JOIN " + Dtab_relation_store.TableName + " dtab_rs ON dtab_rs.tab_id=oct.ocs_id");
			//DML
			asmSql.addSql("UNION");
			asmSql.addSql("SELECT dd.datatable_id AS table_id, dd.datatable_en_name AS table_name, dfi.field_en_name AS" +
					" column_name,dfi.field_cn_name AS column_ch_name,concat(field_type,'(',field_length,')') AS" +
					" column_type FROM " + Datatable_field_info.TableName + " dfi JOIN " + Dm_datatable.TableName + " dd ON" +
					" dd.datatable_id = dfi.datatable_id");
			//UDL
			asmSql.addSql("UNION");
			asmSql.addSql("SELECT dti.table_id AS table_id, dti.table_name AS table_name, dtc.column_name AS column_name," +
					" dtc.field_ch_name AS column_ch_name, dtc.column_type AS column_type FROM " + Dq_table_info.TableName + " dti" +
					" JOIN " + Dq_table_column.TableName + " dtc ON dti.table_id=dtc.table_id");
			asmSql.addSql(") tmp group by table_id,table_name,column_name,column_ch_name,column_type order by table_name");
			List<Map<String, Object>> tableInfoByPlatform = SqlOperator.queryList(db, asmSql.sql(), asmSql.params());
			//获取DQC层获取到表信息
			List<Dq_index3record> di_3_s = SqlOperator.queryList(db, Dq_index3record.class, "SELECT * FROM " + Dq_index3record.TableName);
			di_3_s.forEach(di_3 -> {
				String table_col_s = di_3.getTable_col();
				String[] column_s = table_col_s.split(",");
				for (String column : column_s) {
					Map<String, Object> map = new HashMap<>();
					map.put("table_id", di_3.getRecord_id());
					map.put("table_name", di_3.getTable_name());
					map.put("column_name", column);
					map.put("column_ch_name", column);
					map.put("column_type", "VARCHAR(--)");
					tableInfoByPlatform.add(map);
				}
			});
			//未查到任何成功登记的表信息
			if (tableInfoByPlatform.isEmpty()) {
				logger.warn("初始化sql补全的缓存数据时,平台没有成功登记的表!");
			}
			//处理查询的结果集
			Map<String, List<Object>> cache_map = new HashMap<>();
			tableInfoByPlatform.forEach(table_info -> {
				String table_name = table_info.get("table_name").toString();
				List<Object> column_s;
				if (cache_map.containsKey(table_name)) {
					column_s = cache_map.get(table_name);
					column_s.add(table_info.get("column_name"));
				} else {
					column_s = new ArrayList<>();
					column_s.add(table_info.get("column_name"));
					cache_map.put(table_name, column_s);
				}
			});
			//设置缓存信息
			cache_map.forEach(platformAllTableInfoCache::setCache);
		}
		logger.info("Successfully initialized all table information of the platform");
		return platformAllTableInfoCache;
	}

	@Method(desc = "获取表字段信息列表_缓存", logicStep = "获取表字段信息列表_缓存")
	@Param(name = "table_name", desc = "表名", range = "String")
	@Return(desc = "缓存中表及表对应字段的信息", range = "缓存中表及表对应字段的信息")
	public Object getTableInfoByTableName_cache(String table_name) {
		CacheObj cacheObj;
		//数据校验
		Validator.notBlank(table_name, "查询表名不能为空!");
		//根据表名在缓存中查询
		cacheObj = platformAllTableInfoCache.getCache(table_name);
		//如果在缓存中找到的结果为null代表没找到,则根据表名在配置库中查找表信息
		if (null == cacheObj) {
			//根据表名在配置库中查找表信息
			List<Map<String, Object>> columnsByTableName = DataTableUtil.getColumnByTableName(Dbo.db(), table_name);
			//如果字段信息不为空,则表示找到表信息
			List<Object> column_s = new ArrayList<>();
			if (!columnsByTableName.isEmpty()) {
				//只获取字段英文名
				columnsByTableName.forEach(column_info -> column_s.add(column_info.get("column_name")));
				//找到表信息,将该表信息添加到缓存中
				platformAllTableInfoCache.setCache(table_name, column_s);
				cacheObj = platformAllTableInfoCache.getCache(table_name);
			}
		}
		return cacheObj == null ? new Object() : cacheObj.getCacheValue();
	}

	@Method(desc = "根据表名获取采集数据，默认显示10条",
			logicStep = "初始化查询sql" +
					"获取数据")
	@Param(name = "tableName", desc = "查询表名", range = "String类型表名")
	@Return(desc = "查询返回结果集", range = "无限制")
	public List<Map<String, Object>> queryDataBasedOnTableName(String tableName) {
		//初始化查询sql
		String sql = "select * from " + tableName;
		//获取数据
		List<Map<String, Object>> query_list = new ArrayList<>();
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			new ProcessingData() {
				@Override
				public void dealLine(Map<String, Object> map) {
					query_list.add(map);
				}
			}.getPageDataLayer(sql, db, 1, 10);
		}
		return query_list;
	}

	@Method(desc = "根据SQL获取采集数据", logicStep = "初始化查询sql根据sql语句获取数据")
	@Param(name = "querySQL", desc = "查询SQL", range = "String类型SQL")
	@Return(desc = "查询返回结果集", range = "无限制")
	public List<Map<String, Object>> queryDataBasedOnSql(String querySQL) {
		//初始化查询sql
		querySQL = new DruidParseQuerySql().GetNewSql(querySQL);
		//根据sql语句获取数据
		List<Map<String, Object>> query_list = new ArrayList<>();
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			new ProcessingData() {
				@Override
				public void dealLine(Map<String, Object> map) {
					query_list.add(map);
				}
			}.getPageDataLayer(querySQL, db, 1, 100);
		}
		return query_list;
	}

	@Method(desc = "获取数据源树信息", logicStep = "获取数据源树信息")
	@Return(desc = "数据源树信息", range = "数据源树信息")
	public List<Node> getWebSQLTreeData() {
		//配置树不显示文件采集的数据
		TreeConf treeConf = new TreeConf();
		treeConf.setShowFileCollection(Boolean.FALSE);
		//根据源菜单信息获取节点数据列表
		List<Map<String, Object>> dataList = TreeNodeInfo.getTreeNodeInfo(TreePageSource.WEB_SQL, getUser(), treeConf);
		return NodeDataConvertedTreeList.dataConversionTreeInfo(dataList);
	}

	@Method(desc = "获取平台登记的所有表信息", logicStep = "获取平台登记的所有表信息")
	@Return(desc = "平台登记的所有表信息", range = "平台登记的所有表信息")
	public List<String> getAllTableNameByPlatform() {
		return DataTableUtil.getAllTableNameByPlatform(Dbo.db());
	}

	@Method(desc = "获取表字段信息列表", logicStep = "获取表字段信息列表")
	@Param(name = "table_name", desc = "表名", range = "String")
	@Return(desc = "字段信息列表", range = "字段信息列表")
	@Deprecated
	public List<Map<String, Object>> getColumnsByTableName(String table_name) {
		//数据层获取不同表结构
		Validator.notBlank(table_name, "查询表名不能为空!");
		return DataTableUtil.getColumnByTableName(Dbo.db(), table_name);
	}


	@Method(desc = "获取SQL中相关表和字段信息", logicStep = "获取SQL中相关表和字段信息")
	@Param(name = "sql", desc = "解析sql", range = "String")
	@Return(desc = "SQL中相关表和字段信息", range = "SQL中相关表和字段信息")
	public List<Map<String, Object>> getTableColumnInfoBySql(String sql) {
		//数据校验
		Validator.notBlank(sql, "解析sql不能为空");
		//初始化返回结果
		List<Map<String, Object>> tableColumnInfos = new ArrayList<>();
		//执行sql的解析结果
		List<String> table_name_s = DruidParseQuerySql.parseSqlTableToList(sql);
		if (!table_name_s.isEmpty()) {
			table_name_s.forEach(table_name -> {
				Map<String, Object> map = new HashMap<>();
				map.put("table_name", table_name);
				map.put("column_info", DataTableUtil.getColumnByTableName(Dbo.db(), table_name));
				tableColumnInfos.add(map);
			});
		}
		return tableColumnInfos;
	}

	@Method(desc = "获取树的数据信息",
			logicStep = "1.声明获取到 zTreeUtil 的对象" +
					"2.设置树实体" +
					"3.调用ZTreeUtil的getTreeDataInfo获取treeData的信息")
	@Param(name = "agent_layer", desc = "数据层类型", range = "String类型", nullable = true)
	@Param(name = "source_id", desc = "数据源id", range = "String类型", nullable = true)
	@Param(name = "classify_id", desc = "分类id", range = "String类型", nullable = true)
	@Param(name = "data_mart_id", desc = "集市id", range = "String类型", nullable = true)
	@Param(name = "category_id", desc = "分类编号", range = "String类型", nullable = true)
	@Param(name = "systemDataType", desc = "系统数据类型", range = "String类型", nullable = true)
	@Param(name = "kafka_id", desc = "kafka数据id", range = "String类型", nullable = true)
	@Param(name = "batch_id", desc = "批量数据id", range = "String类型", nullable = true)
	@Param(name = "groupId", desc = "分组id", range = "String类型", nullable = true)
	@Param(name = "sdm_consumer_id", desc = "消费id", range = "String类型", nullable = true)
	@Param(name = "parent_id", desc = "父id", range = "String类型", nullable = true)
	@Param(name = "tableSpace", desc = "表空间", range = "String类型", nullable = true)
	@Param(name = "database_type", desc = "数据库类型", range = "String类型", nullable = true)
	@Param(name = "isFileCo", desc = "是否文件采集", range = "String类型", valueIfNull = "false")
	@Param(name = "tree_menu_from", desc = "树菜单来源", range = "String类型", nullable = true)
	@Param(name = "isPublicLayer", desc = "公共层", range = "IsFlag代码项1:是,0:否", valueIfNull = "1")
	@Param(name = "isRootNode", desc = "是否为树的根节点标志", range = "IsFlag代码项1:是,0:否", valueIfNull = "1")
	@Return(desc = "树数据Map信息", range = "无限制")
	@Deprecated
	public Map<String, Object> getTreeDataInfo(String agent_layer, String source_id, String classify_id,
	                                           String data_mart_id, String category_id, String systemDataType,
	                                           String kafka_id, String batch_id, String groupId, String sdm_consumer_id,
	                                           String parent_id, String tableSpace, String database_type,
	                                           String isFileCo, String tree_menu_from, String isPublicLayer,
	                                           String isRootNode) {
		//1.声明获取到 zTreeUtil 的对象
		ForegroundTreeUtil foregroundTreeUtil = new ForegroundTreeUtil();
		//2.设置树实体
		TreeDataInfo treeDataInfo = new TreeDataInfo();
		treeDataInfo.setAgent_layer(agent_layer);
		treeDataInfo.setSource_id(source_id);
		treeDataInfo.setClassify_id(classify_id);
		treeDataInfo.setData_mart_id(data_mart_id);
		treeDataInfo.setCategory_id(category_id);
		treeDataInfo.setSystemDataType(systemDataType);
		treeDataInfo.setKafka_id(kafka_id);
		treeDataInfo.setBatch_id(batch_id);
		treeDataInfo.setGroupId(groupId);
		treeDataInfo.setSdm_consumer_id(sdm_consumer_id);
		treeDataInfo.setParent_id(parent_id);
		treeDataInfo.setTableSpace(tableSpace);
		treeDataInfo.setDatabaseType(database_type);
		treeDataInfo.setIsFileCo(isFileCo);
		treeDataInfo.setPage_from(tree_menu_from);
		treeDataInfo.setIsPublic(isPublicLayer);
		treeDataInfo.setIsShTable(isRootNode);
		//3.调用ZTreeUtil的getTreeDataInfo获取树数据信息
		Map<String, Object> treeSourcesMap = new HashMap<>();
		treeSourcesMap.put("tree_sources", foregroundTreeUtil.getTreeDataInfo(getUser(), treeDataInfo));
		return treeSourcesMap;
	}

	@Method(desc = "获取树数据搜索信息",
			logicStep = "1.声明获取到 zTreeUtil 的对象" +
					"2.设置树实体" +
					"3.调用ZTreeUtil的getTreeNodeSearchInfo获取检索的结果的信息")
	@Param(name = "tree_menu_from", desc = "树菜单来源", range = "String类型", nullable = true)
	@Param(name = "tableName", desc = "检索表名", range = "String类型", nullable = true)
	@Param(name = "isFileCo", desc = "是否文件采集", range = "String类型", valueIfNull = "false")
	@Param(name = "isRootNode", desc = "是否为树的根节点标志", range = "IsFlag代码项1:是,0:否", valueIfNull = "1")
	@Return(desc = "树数据检索结果Map信息", range = "无限制")
	@Deprecated
	public Map<String, Object> getTreeNodeSearchInfo(String tree_menu_from, String tableName, String isFileCo,
	                                                 String isRootNode) {
		//1.声明获取到 treeUtil 的对象
		ForegroundTreeUtil foregroundTreeUtil = new ForegroundTreeUtil();
		//2.设置树实体
		TreeDataInfo treeDataInfo = new TreeDataInfo();
		treeDataInfo.setPage_from(tree_menu_from);
		treeDataInfo.setTableName(tableName);
		treeDataInfo.setIsFileCo(isFileCo);
		treeDataInfo.setIsShTable(isRootNode);
		//3.调用ZTreeUtil的getTreeNodeSearchInfo获取检索的数据信息
		Map<String, Object> treeNodeSearchMap = new HashMap<>();
		treeNodeSearchMap.put("search_nodes", foregroundTreeUtil.getTreeNodeSearchInfo(getUser(), treeDataInfo));
		return treeNodeSearchMap;
	}
}
