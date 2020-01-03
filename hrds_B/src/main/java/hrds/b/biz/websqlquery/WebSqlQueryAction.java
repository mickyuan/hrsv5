package hrds.b.biz.websqlquery;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.AgentType;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Agent_info;
import hrds.commons.entity.Data_source;
import hrds.commons.zTree.ZTreeUtil;
import hrds.commons.zTree.bean.TreeDataInfo;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

@DocClass(desc = "WebSql查询处理类", author = "BY-HLL", createdate = "2019/10/25 0025 下午 05:51")
public class WebSqlQueryAction extends BaseAction {

	private static final SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();

	@Method(desc = "获取登录用户部门下的数据库采集和DB文件采集所有任务信息",
			logicStep = "1.获取当前用户所在部门的所有数据源信息" +
					"2.初始化采集任务信息" +
					"3.获取数据源下采集任务信息" +
					"3-1.获取DB文件采集任务信息" +
					"3-2.获取数据库采集任务信息")
	@Return(desc = "登录用户部门的数据源所有任务信息", range = "无限制")
	public Result getCollectionTaskInfo() {
		//数据权限验证: 根据登录用户所在部门进行数据验证
		//1.获取当前用户所在部门的所有数据源信息
		Result dataSourceRs = Dbo.queryResult("SELECT ds.SOURCE_ID, ds.DATASOURCE_NAME from" +
				" source_relation_dep srd JOIN data_source ds on srd.SOURCE_ID = ds.SOURCE_ID" +
				" where srd.dep_id = ?", getUser().getDepId());
		//2.初始化采集任务信息
		Result databaseCollectionTaskRs = new Result();
		Result dbFileCollectionTaskRs = new Result();
		//3.获取数据源下采集任务信息
		//设置数据源实体
		Data_source dataSource = new Data_source();
		//设置Agent信息实体
		Agent_info agentInfo = new Agent_info();
		for (int i = 0; i < dataSourceRs.getRowCount(); i++) {
			dataSource.setSource_id(dataSourceRs.getString(i, "source_id"));
			agentInfo.setAgent_type(AgentType.ShuJuKu.getCode());
			asmSql.clean();
			asmSql.addSql("SELECT dbs.DATABASE_ID,dbs.TASK_NAME from data_source ds JOIN agent_info ai" +
					" ON ds.source_id = ai.SOURCE_ID JOIN database_set dbs ON ai.AGENT_ID = dbs.AGENT_ID" +
					" WHERE ds.SOURCE_ID = ? and ai.agent_type=? and dbs.is_sendok = ?");
			asmSql.addParam(dataSource.getSource_id());
			asmSql.addParam(agentInfo.getAgent_type());
			asmSql.addParam(IsFlag.Shi.getCode());
			databaseCollectionTaskRs = Dbo.queryResult(asmSql.sql(), asmSql.params());
			dataSourceRs.setObject(i, "databaseCollectionTaskRs", databaseCollectionTaskRs.toList());
			asmSql.cleanParams();
			agentInfo.setAgent_type(AgentType.DBWenJian.getCode());
			asmSql.addParam(dataSource.getSource_id());
			asmSql.addParam(agentInfo.getAgent_type());
			asmSql.addParam(IsFlag.Shi.getCode());
			dbFileCollectionTaskRs = Dbo.queryResult(asmSql.sql(), asmSql.params());
			dataSourceRs.setObject(i, "dbFileCollectionTaskRs", dbFileCollectionTaskRs.toList());
		}
		return dataSourceRs;
	}

	@Method(desc = "获取登录用户部门的集市表信息",
			logicStep = "1.获取当前用户所在部门的所有集市源信息" +
					"2.初始化集市信息" +
					"3.获取集市表信息")
	@Return(desc = "登录用户部门下所有集市数据表信息", range = "无限制")
	public Result getTablesFromMarket() {
		//1.获取当前用户所在部门的所有集市源信息
		Result dataMarketInfoRs = Dbo.queryResult("SELECT data_mart_id from data_mart_info");
		//2.初始化集市表信息
		Result marketTables;
		//3.获取集市表信息
		for (int i = 0; i < dataMarketInfoRs.getRowCount(); i++) {
			marketTables = Dbo.queryResult("SELECT * from datatable_info where data_mart_id = ?" +
							" and datatable_due_date >=?",
					dataMarketInfoRs.getLong(i, "data_mart_id"), DateUtil.getSysDate());
			dataMarketInfoRs.setObject(i, "marketTables", marketTables.toList());
		}
		return dataMarketInfoRs;
	}

	@Method(desc = "根据数据库设置id获取表信息",
			logicStep = "1.根据数据库设置id获取表信息")
	@Param(name = "collect_set_id", desc = "数据库设置id", range = "Integer类型，长度为10")
	@Return(desc = "集市数据表信息", range = "无限制")
	public Result getTableInfoByCollectSetId(long collect_set_id) {
		//1.根据数据库设置id获取表信息
		return Dbo.queryResult("SELECT * FROM source_file_attribute WHERE COLLECT_SET_ID = ?",
				collect_set_id);
	}

	@Method(desc = "根据表名获取采集数据，默认显示10条",
			logicStep = "1.初始化查询" +
					"1-1.如果查询条数小于1条则显示默认10条，查询条数大于100条则显示100条，否则取传入的查询条数" +
					"2.获取hive连接配置" +
					"2-1.根据sql语句判断执行引擎")
	@Param(name = "tableName", desc = "查询表名", range = "String类型表名")
	@Param(name = "queryNum", desc = "查询条数", range = "int类型值，默认10条，最小1条，最大100")
	@Return(desc = "查询返回结果集", range = "无限制")
	public Result queryDataBasedOnTableName(String tableName, int queryNum) {
		//1.初始化查询
		ResultSet resultset;
		Map<String, Object> tableMap = new HashMap<>();
		//1-1.如果查询条数小于1条则显示默认10条，查询条数大于100条则显示100条，否则取传入的查询条数
		queryNum = Math.max(1, queryNum);
		queryNum = Math.min(queryNum, 100);
		//2.获取hive连接配置
		//2-1.根据sql语句判断执行引擎
		//TODO 根据表名获取表数据暂未实现，(查询sql引擎)
		return Dbo.queryResult("select * from " + tableName + " limit " + queryNum);
	}

	@Method(desc = "根据SQL获取采集数据，默认显示10条",
			logicStep = "1.初始化查询" +
					"1-1.如果查询条数小于1条则显示默认10条，查询条数大于100条则显示100条，否则取传入的查询条数" +
					"2.获取hive连接配置" +
					"2-1.根据sql语句判断执行引擎")
	@Param(name = "querySQL", desc = "查询SQL", range = "String类型SQL")
	@Param(name = "queryNum", desc = "查询条数", range = "int类型值，默认10条，最小1条，最大100")
	@Return(desc = "查询返回结果集", range = "无限制")
	public Result queryDataBasedOnSql(String querySQL, int queryNum) {
		//1.初始化查询
		ResultSet resultset;
		Map<String, Object> tableMap = new HashMap<>();
		//1-1.如果查询条数小于1条则显示默认10条，查询条数大于100条则显示100条，否则取传入的查询条数
		queryNum = Math.max(1, queryNum);
		queryNum = Math.min(queryNum, 100);
		//2.获取hive连接配置
		//2-1.根据sql语句判断执行引擎
		//TODO 根据表名获取表数据暂未实现，(查询sql引擎)
		return Dbo.queryResult(querySQL);
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
	@Param(name = "groupid", desc = "分组id", range = "String类型", nullable = true)
	@Param(name = "sdm_consum_id", desc = "消费id", range = "String类型", nullable = true)
	@Param(name = "type_id", desc = "类型id", range = "String类型", nullable = true)
	@Param(name = "parent_id", desc = "父id", range = "String类型", nullable = true)
	@Param(name = "spaceTable", desc = "表空间", range = "String类型", nullable = true)
	@Param(name = "database_type", desc = "数据库类型", range = "String类型", nullable = true)
	@Param(name = "isFileCo", desc = "是否文件采集", range = "String类型", valueIfNull = "false")
	@Param(name = "tree_menu_from", desc = "树菜单来源", range = "String类型", nullable = true)
	@Param(name = "isPublicLayer", desc = "公共层", range = "IsFlag代码项1:是,0:否", valueIfNull = "1")
	@Param(name = "isRootNode", desc = "是否为树的根节点标志", range = "IsFlag代码项1:是,0:否", valueIfNull = "1")
	@Return(desc = "树数据Map信息", range = "无限制")
	public Map<String, Object> getTreeDataInfo(String agent_layer, String source_id, String classify_id,
	                                           String data_mart_id, String category_id, String systemDataType,
	                                           String kafka_id, String batch_id, String groupid, String sdm_consum_id,
	                                           String type_id, String parent_id, String spaceTable,
	                                           String database_type, String isFileCo, String tree_menu_from,
	                                           String isPublicLayer, String isRootNode) {
		//1.声明获取到 zTreeUtil 的对象
		ZTreeUtil zTreeUtil = new ZTreeUtil();
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
		treeDataInfo.setGroupid(groupid);
		treeDataInfo.setSdm_consum_id(sdm_consum_id);
		treeDataInfo.setType_id(type_id);
		treeDataInfo.setParent_id(parent_id);
		treeDataInfo.setSpaceTable(spaceTable);
		treeDataInfo.setDatabase_type(database_type);
		treeDataInfo.setIsFileCo(isFileCo);
		treeDataInfo.setPage_from(tree_menu_from);
		treeDataInfo.setIsPublic(isPublicLayer);
		treeDataInfo.setIsShTable(isRootNode);
		//3.调用ZTreeUtil的getTreeDataInfo获取树数据信息
		Map<String, Object> treeSourcesMap = new HashMap<>();
		treeSourcesMap.put("tree_sources", zTreeUtil.getTreeDataInfo(getUser(), treeDataInfo));
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
	public Map<String, Object> getTreeNodeSearchInfo(String tree_menu_from, String tableName, String isFileCo,
	                                                 String isRootNode) {
		//1.声明获取到 zTreeUtil 的对象
		ZTreeUtil zTreeUtil = new ZTreeUtil();
		//2.设置树实体
		TreeDataInfo treeDataInfo = new TreeDataInfo();
		treeDataInfo.setPage_from(tree_menu_from);
		treeDataInfo.setTableName(tableName);
		treeDataInfo.setIsFileCo(isFileCo);
		treeDataInfo.setIsShTable(isRootNode);
		//3.调用ZTreeUtil的getTreeNodeSearchInfo获取检索的结果的信息
		Map<String, Object> treeNodeSearchMap = new HashMap<>();
		treeNodeSearchMap.put("search_nodes", zTreeUtil.getTreeNodeSearchInfo(getUser(), treeDataInfo));
		return treeNodeSearchMap;
	}
}
