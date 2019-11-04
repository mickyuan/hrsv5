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
import hrds.commons.entity.Datatable_info;

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
		Result marketTables = new Result();
		//3.获取集市表信息
		//设置实体
		Datatable_info dataTableInfo = new Datatable_info();
		dataTableInfo.setDatatable_due_date(DateUtil.getSysDate());
		for (int i = 0; i < dataMarketInfoRs.getRowCount(); i++) {
			asmSql.clean();
			dataTableInfo.setData_mart_id(dataMarketInfoRs.getString(i, "data_mart_id"));
			asmSql.addSql("SELECT * from datatable_info where data_mart_id = ? ");
			asmSql.addParam(dataTableInfo.getData_mart_id());
			asmSql.addSql(" and datatable_due_date >=?");
			asmSql.addParam(dataTableInfo.getDatatable_due_date());
			marketTables = Dbo.queryResult(asmSql.sql(), asmSql.params());
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
		String sql = "select * from " + tableName + " limit " + queryNum;
		//2.获取hive连接配置
		//2-1.根据sql语句判断执行引擎
		//TODO 根据表名获取表数据暂未实现，(查询sql引擎)
		return null;
	}

	@Method(desc = "根据表名获取采集数据，默认显示10条",
			logicStep = "1.初始化查询" +
					"1-1.如果查询条数小于1条则显示默认10条，查询条数大于100条则显示100条，否则取传入的查询条数" +
					"2.获取hive连接配置" +
					"2-1.根据sql语句判断执行引擎")
	@Param(name = "tableName", desc = "查询表名", range = "String类型表名")
	@Param(name = "queryNum", desc = "查询条数", range = "int类型值，默认10条，最小1条，最大100")
	@Return(desc = "查询返回结果集", range = "无限制")
	public Result queryDataBasedOnSql(String tableName, int queryNum) {
		//1.初始化查询
		ResultSet resultset;
		Map<String, Object> tableMap = new HashMap<>();
		//1-1.如果查询条数小于1条则显示默认10条，查询条数大于100条则显示100条，否则取传入的查询条数
		queryNum = Math.max(1, queryNum);
		queryNum = Math.min(queryNum, 100);
		String sql = "select * from " + tableName + " limit " + queryNum;
		//2.获取hive连接配置
		//2-1.根据sql语句判断执行引擎
		//TODO 根据表名获取表数据暂未实现，(查询sql引擎)
		return null;
	}
}
