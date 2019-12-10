package hrds.b.biz.agent.dbagentconf.tableconf;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import fd.ng.web.util.Dbo;
import hrds.b.biz.agent.bean.CollTbConfParam;
import hrds.b.biz.agent.tools.SendMsgUtil;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.CleanType;
import hrds.commons.codes.CountNum;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.*;
import hrds.commons.exception.AppSystemException;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.AgentActionUtil;
import hrds.commons.utils.Constant;
import hrds.commons.utils.DboExecute;
import hrds.commons.utils.key.PrimayKeyGener;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

@DocClass(desc = "定义表抽取属性", author = "WangZhengcheng")
public class CollTbConfStepAction extends BaseAction {

	private static final long DEFAULT_TABLE_ID = 999999L;
	private static final JSONObject DEFAULT_TABLE_CLEAN_ORDER;
	private static final JSONObject DEFAULT_COLUMN_CLEAN_ORDER;

	static {
		DEFAULT_TABLE_CLEAN_ORDER = new JSONObject();
		DEFAULT_TABLE_CLEAN_ORDER.put(CleanType.ZiFuBuQi.getCode(), 1);
		DEFAULT_TABLE_CLEAN_ORDER.put(CleanType.ZiFuTiHuan.getCode(), 2);
		DEFAULT_TABLE_CLEAN_ORDER.put(CleanType.ZiFuHeBing.getCode(), 3);
		DEFAULT_TABLE_CLEAN_ORDER.put(CleanType.ZiFuTrim.getCode(), 4);

		//TODO 按照目前agent程序的逻辑是，列合并永远是排在清洗顺序的最后一个去做，因此这边定义默认的清洗顺序的时候，只定义了6种，而没有定义列合并
		//TODO 所以是否前端需要把列合并去掉
		DEFAULT_COLUMN_CLEAN_ORDER = new JSONObject();
		DEFAULT_COLUMN_CLEAN_ORDER.put(CleanType.ZiFuBuQi.getCode(), 1);
		DEFAULT_COLUMN_CLEAN_ORDER.put(CleanType.ZiFuTiHuan.getCode(), 2);
		DEFAULT_COLUMN_CLEAN_ORDER.put(CleanType.ShiJianZhuanHuan.getCode(), 3);
		DEFAULT_COLUMN_CLEAN_ORDER.put(CleanType.MaZhiZhuanHuan.getCode(), 4);
		DEFAULT_COLUMN_CLEAN_ORDER.put(CleanType.ZiFuChaiFen.getCode(), 5);
		DEFAULT_COLUMN_CLEAN_ORDER.put(CleanType.ZiFuTrim.getCode(), 6);
	}

	@Method(desc = "根据数据库采集设置表ID加载页面初始化数据", logicStep = "1、查询数据并返回")
	@Param(name = "colSetId", desc = "数据库设置ID,源系统数据库设置表主键,数据库对应表外键", range = "不为空")
	@Return(desc = "查询结果集" , range = "不会为null")
	public Result getInitInfo(long colSetId) {
		//1、查询数据并返回
		return Dbo.queryResult(" select ti.table_id,ti.table_name,ti.table_ch_name, ti.is_parallel" +
						" FROM " + Table_info.TableName + " ti " +
						" WHERE ti.database_id = ? AND ti.is_user_defined = ? ", colSetId, IsFlag.Fou.getCode());
		//数据可访问权限处理方式
		//以上table_info表中都没有user_id字段，解决方式待讨论
	}

	@Method(desc = "根据模糊表名和数据库设置id得到表相关信息，如果搜索框中用户输入了内容，就直接调用该接口", logicStep = "" +
			"1、根据colSetId去数据库中获取数据库设置相关信息" +
			"2、和Agent端进行交互，得到Agent返回的数据" +
			"3、对获取到的数据进行处理，获得模糊查询到的表名" +
			"4、根据表名和colSetId获取界面需要显示的信息并返回")
	@Param(name = "colSetId", desc = "数据库设置ID,源系统数据库设置表主键,数据库对应表外键", range = "不为空")
	@Param(name = "inputString", desc = "用户界面输入用于模糊查询的关键词", range = "不为空")
	@Return(desc = "查询结果集", range = "不为空")
	//查询按钮
	public List<Map<String, Object>> getTableInfo(long colSetId, String inputString){
		//1、根据colSetId去数据库中获取数据库设置相关信息
		Map<String, Object> databaseInfo = getDatabaseSetInfo(colSetId, getUserId());
		//数据可访问权限处理方式
		//以上SQL中使用user_id作为过滤条件，达到了访问权限控制的目的
		if(databaseInfo.isEmpty()){
			throw new BusinessException("未找到数据库采集任务");
		}
		//2、与Agent端进行交互，得到Agent返回的数据
		String methodName = AgentActionUtil.GETDATABASETABLE;
		long agentId = (long)databaseInfo.get("agent_id");
		String respMsg = SendMsgUtil.searchTableName(agentId, getUserId(), databaseInfo, inputString, methodName);
		//3、对获取到的数据进行处理，获得模糊查询到的表名
		List<String> rightTables = JSON.parseObject(respMsg, new TypeReference<List<String>>() {});
		//4、根据表名和colSetId获取界面需要显示的信息并返回
		return getTableInfoByTableName(rightTables, colSetId);
	}

	@Method(desc = "根据数据库设置id得到目标数据库中所有表相关信息，如果用户没有输入内容就点击查询，那么就调用该接口获取所有表", logicStep = "" +
			"1、根据colSetId去数据库中获取数据库设置相关信息" +
			"2、和Agent端进行交互，得到Agent返回的数据" +
			"3、对获取到的数据进行处理，根据表名和colSetId获取界面需要显示的信息并返回")
	@Param(name = "colSetId", desc = "数据库设置ID,源系统数据库设置表主键,数据库对应表外键", range = "不为空")
	@Return(desc = "查询结果集", range = "不为空")
	public List<Map<String, Object>> getAllTableInfo(long colSetId){
		//1、根据colSetId去数据库中获取数据库设置相关信息
		Map<String, Object> databaseInfo = getDatabaseSetInfo(colSetId, getUserId());
		if(databaseInfo.isEmpty()){
			throw new BusinessException("未找到数据库采集任务");
		}
		//2、和Agent端进行交互，得到Agent返回的数据
		String methodName = AgentActionUtil.GETDATABASETABLE;
		long agentId = (long) databaseInfo.get("agent_id");
		String respMsg = SendMsgUtil.getAllTableName(agentId, getUserId(), databaseInfo, methodName);
		//3、对获取到的数据进行处理，根据表名和colSetId获取界面需要显示的信息并返回
		List<String> tableNames = JSON.parseObject(respMsg, new TypeReference<List<String>>() {});
		return getTableInfoByTableName(tableNames, colSetId);
	}

	@Method(desc = "根据表ID获取给该表定义的分页抽取SQL", logicStep = "" +
			"1、去数据库中根据table_id查出该表定义的分页抽取SQL" +
			"2、返回")
	@Param(name = "tableId", desc = "数据库对应表ID", range = "不为空")
	@Return(desc = "分页SQL", range = "只要正常返回肯定不是空字符串")
	public Result getPageSQL(long tableId){
		Result result = Dbo.queryResult("select ti.table_id, ti.page_sql from " + Table_info.TableName +
				" ti where ti.table_id = ?", tableId);
		if(result.isEmpty()){
			throw new BusinessException("获取分页抽取SQL失败");
		}
		return result;
	}

	@Method(desc = "保存自定义抽取数据SQL，如果页面上没有数据，则tableInfoArray参数传空字符串", logicStep = "" +
			"1、根据colSetId去数据库中查询该数据库采集任务是否存在" +
			"2、使用colSetId在table_info表中删除所有自定义SQL采集的记录，不关注删除的数目，结果可以是0-N" +
			"3、将前端传过来的参数(JSON)转为List<Table_info>集合" +
			"4、如果List集合不为空，遍历list,给每条记录生成ID，设置有效开始日期、有效结束日期、是否自定义SQL采集(是)、是否使用MD5(是)、" +
			"   是否仅登记(是)" +
			"5、保存数据进库" +
			"6、保存数据自定义采集列相关信息进入table_column表")
	@Param(name = "tableInfoArray", desc = "List<Table_info>的JSONArray格式的字符串，" +
			"每一个Table_info对象必须包含table_name,table_ch_name,sql", range = "不为空")
	@Param(name = "colSetId", desc = "数据库设置ID,源系统数据库设置表主键,数据库对应表外键", range = "不为空")
	//使用SQL抽取数据页面，保存按钮后台方法
	public long saveAllSQL(String tableInfoArray, long colSetId){
		//1、根据databaseId去数据库中查询该数据库采集任务是否存在
		long dbSetCount = Dbo.queryNumber("select count(1) from " + Database_set.TableName + " where database_id = ?"
				, colSetId).orElseThrow(() -> new BusinessException("SQL查询错误"));
		if(dbSetCount != 1){
			throw new BusinessException("数据库采集任务未找到");
		}

		List<Object> tableIds = Dbo.queryOneColumnList("select table_id from " + Table_info.TableName +
				" where database_id = ? and is_user_defined = ?", colSetId, IsFlag.Shi.getCode());

		//2、使用colSetId在table_info表中删除所有自定义SQL采集的记录，不关注删除的数目，结果可以是0-N
		Dbo.execute("delete from " + Table_info.TableName + " where database_id = ? AND is_user_defined = ? ",
				colSetId, IsFlag.Shi.getCode());

		//3、将前端传过来的参数转为List<Table_info>集合
		List<Table_info> tableInfos = JSONArray.parseArray(tableInfoArray, Table_info.class);
		/*
		* 4、如果List集合不为空，遍历list,给每条记录生成ID，设置有效开始日期、有效结束日期、是否自定义SQL采集(是)、是否使用MD5(是)、
		* 是否仅登记(是)
		* */
		if(tableInfos != null && !tableInfos.isEmpty()){
			for(int i = 0; i < tableInfos.size(); i++){
				Table_info tableInfo = tableInfos.get(i);
				if(StringUtil.isBlank(tableInfo.getTable_name())){
					throw new BusinessException("保存SQL抽取数据配置，第"+ (i + 1) +"条数据表名不能为空!");
				}
				if(StringUtil.isBlank(tableInfo.getTable_ch_name())){
					throw new BusinessException("保存SQL抽取数据配置，第"+ (i + 1) +"条数据表中文名不能为空!");
				}
				if(StringUtil.isBlank(tableInfo.getSql())){
					throw new BusinessException("保存SQL抽取数据配置，第"+ (i + 1) +"条数据查询SQL语句不能为空!");
				}
				tableInfo.setTable_count(CountNum.YiWan.getCode());
				tableInfo.setDatabase_id(colSetId);
				tableInfo.setValid_s_date(DateUtil.getSysDate());
				tableInfo.setValid_e_date(Constant.MAXDATE);
				//是否自定义采集为是
				tableInfo.setIs_user_defined(IsFlag.Shi.getCode());
				tableInfo.setIs_md5(IsFlag.Shi.getCode());
				tableInfo.setIs_register(IsFlag.Fou.getCode());
				//是否并行抽取设置为否
				tableInfo.setIs_parallel(IsFlag.Fou.getCode());
				//如果是新增采集表
				if(tableInfo.getTable_id() == null){
					tableInfo.setTable_id(PrimayKeyGener.getNextId());
					tableInfo.setTi_or(DEFAULT_TABLE_CLEAN_ORDER.toJSONString());
					//5、保存数据进库
					tableInfo.add(Dbo.db());
					//6、保存数据自定义采集列相关信息进入table_column表
					saveCustomSQLColumnInfoForAdd(tableInfo);
				}
				//如果是修改采集表
				else{
					long oldID = tableInfo.getTable_id();
					String newID = PrimayKeyGener.getNextId();
					tableInfo.setTable_id(newID);
					tableInfo.setTi_or(DEFAULT_TABLE_CLEAN_ORDER.toJSONString());
					//5、保存数据进库
					tableInfo.add(Dbo.db());
					//6、保存数据自定义采集列相关信息进入table_column表
					saveCustomSQLColumnInfoForUpdate(tableInfo, oldID);
				}
			}
		}
		//如果List集合为空，表示使用SQL抽取没有设置，那么就要把当前数据库采集任务中所有的自定义抽取设置作为脏数据全部删除
		else{
			if(!tableIds.isEmpty()){
				for(Object tableId : tableIds){
					deleteDirtyDataOfTb((long) tableId);
				}
			}
		}
		return colSetId;
	}

	@Method(desc = "测试并行抽取SQL", logicStep = "" +
			"1、根据colSetId在database_set表中获得数据库设置信息" +
			"2、调用AgentActionUtil工具类获取访问Agent端服务的url" +
			"3、构建http请求访问Agent端服务" +
			"4、如果响应失败，则抛出异常")
	@Param(name = "colSetId", desc = "数据库设置ID,源系统数据库设置表主键,数据库对应表外键", range = "不为空")
	@Param(name = "pageSql", desc = "用户设置的并行采集SQL", range = "不为空")
	public void testParallelExtraction(long colSetId, String pageSql){
		//1、根据colSetId在database_set表中获得数据库设置信息
		Map<String, Object> resultMap = Dbo.queryOneObject("select agent_id, user_name, database_pad, database_drive," +
				" database_type, jdbc_url from " + Database_set.TableName + " where database_id = ?", colSetId);
		if(resultMap.isEmpty()){
			throw new BusinessException("未找到数据库采集任务");
		}
		//2、调用AgentActionUtil工具类获取访问Agent端服务的url
		String url = AgentActionUtil.getUrl((Long) resultMap.get("agent_id"), getUserId(),
				AgentActionUtil.TESTPARALLELSQL);
		//3、构建http请求访问Agent端服务
		HttpClient.ResponseValue resVal = new HttpClient()
				.addData("database_drive", (String) resultMap.get("database_drive"))
				.addData("jdbc_url", (String) resultMap.get("jdbc_url"))
				.addData("user_name", (String) resultMap.get("user_name"))
				.addData("database_pad", (String) resultMap.get("database_pad"))
				.addData("database_type", (String) resultMap.get("database_type"))
				.addData("pageSql", pageSql)
				.post(url);
		ActionResult actionResult = JsonUtil.toObjectSafety(resVal.getBodyString(), ActionResult.class).
				orElseThrow(() -> new BusinessException("应用管理端与" + url + "服务交互异常"));
		//4、如果响应失败，则抛出异常
		if(!actionResult.isSuccess()){
			throw new BusinessException("并行抽取SQL测试失败");
		}
		boolean resultData = (boolean) actionResult.getData();
		//5、如果返回false，表示根据分页SQL没有取到数据，抛出异常给前端
		if(!resultData){
			throw new BusinessException("根据并行抽取SQL未能获取到数据");
		}
	}

	@Method(desc = "根据数据库设置ID获得用户自定义抽取SQL", logicStep = "" +
			"1、根据colSetId在table_info表中查询数据并返回")
	@Param(name = "colSetId", desc = "数据库设置ID,源系统数据库设置表主键，数据库对应表外键", range = "不为空")
	@Return(desc = "查询结果集", range = "不为空，是否有数据视查询结果而定")
	//配置采集表页面，使用SQL抽取数据Tab页后台方法，用于回显已经设置的SQL
	public List<Table_info> getAllSQLs(long colSetId){
		return Dbo.queryList(Table_info.class, " SELECT table_id, table_name, table_ch_name, sql " +
				" FROM "+ Table_info.TableName +
				" WHERE database_id = ? AND is_user_defined = ? order by table_id", colSetId, IsFlag.Shi.getCode());
		//数据可访问权限处理方式
		//以上table_info表中都没有user_id字段，解决方式待讨论
	}

	@Method(desc = "根据数据库设置ID和表名，获取在该表采集时使用的过滤SQL", logicStep = "" +
			"1、根据colSetId和tableName在table_info表中获取数据并返回")
	@Param(name = "colSetId", desc = "数据库设置ID,源系统数据库设置表主键，数据库对应表外键", range = "不为空")
	@Param(name = "tableName", desc = "表名", range = "不为空")
	@Return(desc = "查询结果集", range = "不为空，是否有数据视实际情况而定")
	//配置采集表页面,定义过滤按钮后台方法，用于回显已经对单表定义好的SQL
	public Result getSingleTableSQL(long colSetId, String tableName){
		return Dbo.queryResult("SELECT table_id,table_name,table_ch_name,table_count,sql " +
				" FROM "+ Table_info.TableName +" WHERE database_id = ? AND table_name = ? ",
				colSetId, tableName);
		//数据可访问权限处理方式
		//以上table_info表中都没有user_id字段，解决方式待讨论
	}

	@Method(desc = "根据参数获取表的所有列信息", logicStep = "" +
			"1、判断tableId的值" +
			"2、若tableId为999999，表示要获取当前采集任务中不存在的表的所有列，需要和agent进行交互" +
			"      2-1、根据colSetId去数据库中查出DB连接信息" +
			"      2-2、和Agent交互，获取表中的列信息" +
			"3、若tableId不为999999，表示要获取当前采集任务中存在的表的所有列，直接在table_column表中查询即可" +
			"      3-1、根据tableId在table_column表中获取列信息" +
			"4、返回")
	@Param(name = "tableName", desc = "表名", range = "不为空")
	@Param(name = "colSetId", desc = "数据库设置ID，源系统数据库设置表主键，数据库对应表外键", range = "不为空")
	@Param(name = "tableId", desc = "数据库对应表主键,表对应的字段表外键",
			range = "如果要获取不是当前采集中已经存在的表的列信息，这个参数可以不传", nullable = true,
			valueIfNull = "999999")
	@Return(desc = "key为table_name，value为表名；key为columnInfo，value为List<Table_column> 表示列的信息"
			, range = "不为空")
	//配置采集表页面,选择列按钮后台方法
	public Map<String, Object> getColumnInfo(String tableName, long colSetId, long tableId){
		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("tableName", tableName);
		//1、判断tableId的值
		//2、如果要获取当前采集任务中不存在的表的所有列，需要和agent进行交互
		if(tableId == DEFAULT_TABLE_ID){
			//2-1、根据colSetId去数据库中查出DB连接信息
			//2-2、和Agent交互，获取表中的列信息
			List<Table_column> tableColumns = getColumnInfoByTableName(colSetId, getUserId(), tableName);
			returnMap.put("columnInfo", tableColumns);
		}
		//3、如果要获取当前采集任务中存在的表的所有列，直接在table_column表中查询即可
		else{
			//3-1、根据tableId在table_column表中获取列信息
			List<Table_column> tableColumns = Dbo.queryList(Table_column.class, " SELECT * FROM "+
					Table_column.TableName + " WHERE table_id = ? order by cast(remark as integer)", tableId);
			returnMap.put("columnInfo", tableColumns);
		}
		//4、返回
		return returnMap;
	}

	@Method(desc = "保存单表查询画面配置的所有表采集信息，如果页面上没有数据，则tableInfoString和collTbConfParamString" +
			"参数传空字符串", logicStep = "" +
			"所有表信息放在一起是为了本次保存在一个事务中，同时成功或同时失败" +
			"1、不论新增采集表还是编辑采集表，页面上所有的内容都可能被修改，所以直接执行SQL，" +
			"按database_id删除table_info表中所有非自定义采集SQL的数据" +
			"2、校验Table_info对象中的信息是否合法" +
			"3、给Table_info对象设置基本信息(valid_s_date,valid_e_date,is_user_defined,is_register)" +
			"4、获取Table_info对象的table_id属性，如果该属性没有值，说明这张采集表是新增的，" +
			"否则这张采集表在当前采集任务中，已经存在，且有可能经过了修改" +
			"5、不论新增还是修改，构造默认的表清洗优先级和列清洗优先级" +
			"6、如果是新增采集表" +
			"       6-1、生成table_id，并存入Table_info对象中" +
			"       6-2、保存Table_info对象" +
			"       6-3、保存该表中所有字段的信息进入table_column表" +
			"7、如果是修改采集表" +
			"       7-1、保留原有的table_id，为当前数据设置新的table_id" +
			"       7-2、保存Table_info对象" +
			"       7-3、所有关联了原table_id的表，找到对应的字段，为这些字段设置新的table_id" +
			"           7-3-1、更新table_storage_info表对应条目的table_id字段" +
			"           7-3-2、更新table_clean表对应条目的table_id字段" +
			"           7-3-3、更新column_merge表对应条目的table_id字段" +
			"           7-3-4、更新data_extraction_def表对应条目的table_id" +
			"           7-3-5、编辑采集表，将该表要采集的列信息保存到相应的表里面" +
			"8、不管是新增采集表还是编辑采集表，都需要将该表要采集的列信息保存到相应的表里面")
	@Param(name = "tableInfoString", desc = "当前数据库采集任务要采集的所有的表信息组成的json格式的字符串"
			, range = "不为空，json数组格式字符串" +
			"如果是新增的采集表，table_id为空，如果是编辑修改采集表，table_id不能为空，一个json对象中还应该包括表名(table_name)、" +
			"是否并行抽取(is_parallel)、表中文名(table_ch_name)、如果用户定义了并行抽取，那么应该还有page_sql，" +
			"如果用户定义了SQL过滤，那么还应该有sql")
	@Param(name = "colSetId", desc = "数据库设置ID，源系统数据库设置表主键，数据库对应表外键", range = "不为空")
	@Param(name = "collTbConfParamString", desc = "采集表对应采集字段配置参数", range = "" +
			"key为collColumnString，表示被采集的列，json数组格式的字符串" +
			" 一个json对象中应该包括列名(colume_name)、字段类型(column_type)、列中文名(colume_ch_name)、是否采集(is_get)" +
			"如果用户没有选择采集列，则传空字符串，系统默认采集该张表所有列" +
			"key为columnSortString，表示列的采集顺序，json数组格式的字符串" +
			"一个json对象中，key为columnName，value为列名" +
			"key为sort，value为顺序" +
			"注意：tableInfoString和collTbConfParamString中，对象的顺序和数目要保持一致，比如：" +
			"tableInfoString：[{A表表信息},{B表表信息}]" +
			"collTbConfParamString：[{A表字段配置参数},{B表字段配置参数}]")
	@Return(desc = "保存成功后返回当前采集任务ID", range = "不为空")
	public long saveCollTbInfo(String tableInfoString, long colSetId, String collTbConfParamString){
		Map<String, Object> resultMap = Dbo.queryOneObject("select * from " + Database_set.TableName
				+ " where database_id = ?", colSetId);
		if(resultMap.isEmpty()){
			throw new BusinessException("未找到数据库采集任务");
		}

		//1、不论新增采集表还是编辑采集表，页面上所有的内容都可能被修改，所以直接执行SQL，按database_id删除table_info表
		// 中,所有非自定义采集SQL的数据，不关心删除数据的条数
		Dbo.execute(" DELETE FROM "+ Table_info.TableName +" WHERE database_id = ? AND is_user_defined = ? ",
				colSetId, IsFlag.Fou.getCode());

		List<Table_info> tableInfos = JSONArray.parseArray(tableInfoString, Table_info.class);
		List<CollTbConfParam> tbConfParams = JSONArray.parseArray(collTbConfParamString, CollTbConfParam.class);

		if(tableInfos != null && tbConfParams != null){
			if(tableInfos.size() != tbConfParams.size()){
				throw new BusinessException("请在传参时确保采集表数组和配置采集字段信息一一对应");
			}
			for(int i = 0; i < tableInfos.size(); i++){
				Table_info tableInfo = tableInfos.get(i);
				String collColumn = tbConfParams.get(i).getCollColumnString();
				String columnSort = tbConfParams.get(i).getColumnSortString();
				//2、校验Table_info对象中的信息是否合法
				if(StringUtil.isBlank(tableInfo.getTable_name())){
					throw new BusinessException("保存采集表配置，第"+ (i + 1) +"条数据表名不能为空!");
				}
				if(StringUtil.isBlank(tableInfo.getTable_ch_name())){
					throw new BusinessException("保存采集表配置，第"+ (i + 1) +"条数据表中文名不能为空!");
				}
				IsFlag isFlag = IsFlag.ofEnumByCode(tableInfo.getIs_parallel());
				if(isFlag == IsFlag.Shi){
					if(StringUtil.isBlank(tableInfo.getPage_sql())){
						throw new BusinessException("保存采集表配置，第"+ (i + 1) +"条数据分页抽取SQL不能为空!");
					}
				}
				//3、给Table_info对象设置基本信息(valid_s_date,valid_e_date,is_user_defined,is_register)
				tableInfo.setValid_s_date(DateUtil.getSysDate());
				tableInfo.setValid_e_date(Constant.MAXDATE);
				//是否自定义采集为否
				tableInfo.setIs_user_defined(IsFlag.Fou.getCode());
				//数据采集，该字段就是否
				tableInfo.setIs_register(IsFlag.Fou.getCode());
				//TODO 是否使用MD5，目前页面没有供用户配置的选项，所以在保存是，后台给一个默认值为是
				tableInfo.setIs_md5(IsFlag.Shi.getCode());

				//4、获取Table_info对象的table_id属性，如果该属性没有值，说明这张采集表是新增的，否则这张采集表在当前采集任务中
				//已经存在，且有可能经过了修改
				//5、不论新增还是修改，使用默认的表清洗优先级和列清洗优先级(JSON格式的字符串，保存进入数据库)
				//6、如果是新增采集表
				if(tableInfo.getTable_id() == null){
					//6-1、生成table_id，并存入Table_info对象中
					tableInfo.setTable_id(PrimayKeyGener.getNextId());
					tableInfo.setTi_or(DEFAULT_TABLE_CLEAN_ORDER.toJSONString());
					//6-2、保存Table_info对象
					tableInfo.add(Dbo.db());
					//6-3、新增采集表，将该表要采集的列信息保存到相应的表里面
					saveTableColumnInfoForAdd(tableInfo, collColumn, columnSort, colSetId,
							DEFAULT_COLUMN_CLEAN_ORDER.toJSONString());
				}
				//7、如果是修改采集表
				else{
					//7-1、保留原有的table_id，为当前数据设置新的table_id
					long oldID = tableInfo.getTable_id();
					String newID = PrimayKeyGener.getNextId();
					tableInfo.setTable_id(newID);
					tableInfo.setTi_or(DEFAULT_TABLE_CLEAN_ORDER.toJSONString());
					//7-2、保存Table_info对象
					tableInfo.add(Dbo.db());
					//7-3、所有关联了原table_id的表，找到对应的字段，为这些字段设置新的table_id
					updateTableId(Long.parseLong(newID), oldID);
					//7-3-5、编辑采集表，将该表要采集的列信息保存到相应的表里面
					saveTableColumnInfoForUpdate(tableInfo, collColumn);
				}
			}
		}
		//如果List集合为空，表示单表采集没有设置，那么就要把当前数据库采集任务中所有的单表采集设置作为脏数据全部删除
		else{
			List<Object> tableIds = Dbo.queryOneColumnList("select table_id from " + Table_info.TableName +
					" where database_id = ? and is_user_defined = ?", colSetId, IsFlag.Fou.getCode());
			if(!tableIds.isEmpty()){
				for(Object tableId : tableIds){
					deleteDirtyDataOfTb((long) tableId);
				}
			}
		}
		return colSetId;
	}

	@Method(desc = "根据数据库设置ID获取对采集表配置的SQL过滤，分页SQL", logicStep = "" +
			"1、根据数据库设置ID在数据库设置表中查询是否有这样一个数据库采集任务" +
			"2、如果没有，或者查询结果大于1，抛出异常给前端" +
			"3、根据数据库设置ID在数据库采集对应表中获取SQL过滤，分页SQL，返回给前端")
	@Param(name = "colSetId", desc = "数据库设置ID，源系统数据库设置表主键，数据库对应表外键", range = "不为空")
	@Return(desc = "查询结果集", range = "不为空，其中sql字段表示过滤SQL，page_sql字段表示分页SQL，" +
			"is_parallel字段表示是否并行抽取")
	public Result getSQLInfoByColSetId(long colSetId){
		//1、根据数据库设置ID在数据库设置表中查询是否有这样一个数据库采集任务
		long count = Dbo.queryNumber("select count(1) from " + Database_set.TableName + " where database_id = ?"
				, colSetId).orElseThrow(() -> new BusinessException("SQL查询错误"));
		//2、如果没有，或者查询结果大于1，抛出异常给前端
		if(count != 1){
			throw new BusinessException("未找到数据库采集任务");
		}
		//3、根据数据库设置ID在数据库采集对应表中获取SQL过滤，分页SQL，返回给前端
		return Dbo.queryResult("select table_id, table_name, sql, is_parallel, page_sql from " + Table_info.TableName
				+ " where database_id = ? and is_user_defined = ?", colSetId, IsFlag.Fou.getCode());
	}

	@Method(desc = "根据数据库设置ID获取表的字段信息", logicStep = "" +
			"1、根据数据库设置ID在数据库设置表中查询是否有这样一个数据库采集任务" +
			"2、如果没有，或者查询结果大于1，抛出异常给前端" +
			"3、根据数据库设置ID查询当前数据库采集任务中所有非自定义采集的table_id<结果集1>" +
			"4、如果<结果集1>为空，则返回空的集合给前端" +
			"5、如果<结果集1>不为空，则遍历<结果集1>，用每一个table_id得到列的相关信息<结果集2>" +
			"6、以table_name为key，以<结果集2>为value，将数据封装起来返回给前端")
	@Param(name = "colSetId", desc = "数据库设置ID，源系统数据库设置表主键，数据库对应表外键", range = "不为空")
	@Return(desc = "查询结果集", range = "不为空，key为table_id，value为该表下所有字段的信息")
	public Map<String, List<Map<String, Object>>> getColumnInfoByColSetId(long colSetId){
		//1、根据数据库设置ID在数据库设置表中查询是否有这样一个数据库采集任务
		long count = Dbo.queryNumber("select count(1) from " + Database_set.TableName + " where database_id = ?"
				, colSetId).orElseThrow(() -> new BusinessException("SQL查询错误"));
		//2、如果没有，或者查询结果大于1，抛出异常给前端
		if(count != 1){
			throw new BusinessException("未找到数据库采集任务");
		}
		//3、根据数据库设置ID查询当前数据库采集任务中所有非自定义采集的table_id<结果集1>
		Result tableInfos = Dbo.queryResult("select table_id, table_name from " + Table_info.TableName
				+ " where database_id = ? and is_user_defined = ?", colSetId, IsFlag.Fou.getCode());
		//4、如果<结果集1>为空，则返回空的集合给前端
		if(tableInfos.isEmpty()){
			return Collections.emptyMap();
		}
		//5、如果<结果集1>不为空，则遍历<结果集1>，用每一个table_id得到列的相关信息<结果集2>
		Map<String, List<Map<String, Object>>> returnMap = new HashMap<>();
		//6、以table_name为key，以<结果集2>为value，将数据封装起来返回给前端
		for(int i = 0; i < tableInfos.getRowCount(); i++){
			Result result = Dbo.queryResult("select * from "
					+ Table_column.TableName + " where table_id = ?", tableInfos.getLong(i, "table_id"));
			if(result.isEmpty()){
				throw new BusinessException("获取表字段信息失败");
			}
			returnMap.put(tableInfos.getString(i, "table_name"), result.toList());
		}
		return returnMap;
	}

	@Method(desc = "处理新增采集表信息时表中列信息的保存", logicStep = "" +
			"1、判断columnSort是否为空字符串，如果不是空字符串，解析columnSort为json对象" +
			"2、判断collColumn参数是否为空字符串" +
			"   1-1、是，表示用户没有选择采集列，则应用管理端同Agent端交互，获取该表的列信息" +
			"   1-2、否，表示用户自定义采集列，则解析collColumn为List集合" +
			"3、设置主键，外键等信息，如果columnSort不为空，则将Table_column对象的remark属性设置为该列的采集顺序" +
			"4、保存这部分数据")
	@Param(name = "tableInfo", desc = "一个Table_info对象必须包含table_name,table_ch_name和是否并行抽取，" +
			"如果并行抽取，那么并行抽取SQL也要有；如果是新增的采集表，table_id为空，如果是编辑修改采集表，table_id不能为空" +
			"用于过滤的sql页面没定义就是空，页面定义了就不为空", range = "不为空，Table_info类的实体类对象")
	@Param(name = "collColumn", desc = "该表要采集的字段信息", range = "如果用户没有选择采集列，这个参数可以不传，" +
			"表示采集这张表的所有字段;" +
			"参数格式：json" +
			"内容：是否主键(is_primary_key)" +
			"      列名(colume_name)" +
			"      字段类型(column_type)" +
			"      列中文名(colume_ch_name)")
	@Param(name = "columnSort", desc = "该表要采集的字段的排序", range = "如果用户没有自定义采集字段排序，该参数可以不传")
	@Param(name = "colSetId", desc = "数据库设置ID，源系统数据库设置表主键，数据库对应表外键", range = "不为空")
	@Param(name = "columnCleanOrder", desc = "列清洗默认优先级", range = "不为空，json格式的字符串")
	private void saveTableColumnInfoForAdd(Table_info tableInfo, String collColumn, String columnSort,
	                                       long colSetId, String columnCleanOrder){
		//1、判断columnSort是否为空字符串，如果不是空字符串，解析columnSort为json对象
		JSONArray sortArr = null;
		if(StringUtil.isNotBlank(columnSort)){
			sortArr = JSON.parseArray(columnSort);
		}
		//2、判断collColumn参数是否为空字符串
		List<Table_column> tableColumns;
		if(StringUtil.isBlank(collColumn)){
			//2-1、是，表示用户没有选择采集列，则应用管理端同Agent端交互,获取该表所有列进行采集
			tableColumns = getColumnInfoByTableName(colSetId, getUserId(), tableInfo.getTable_name());
			//同agent交互得到的数据，is_get字段默认都设置为是,如果没有取到
			if(!tableColumns.isEmpty()){
				for(Table_column tableColumn : tableColumns){
					tableColumn.setIs_get(IsFlag.Shi.getCode());
				}
			}
		}else{
			//1-2、否，表示用户自定义采集列，则解析collColumn为List集合
			tableColumns = JSON.parseArray(collColumn, Table_column.class);
		}
		//3、设置主键，外键等信息，如果columnSort不为空，则将Table_column对象的remark属性设置为该列的采集顺序
		if(tableColumns != null && !tableColumns.isEmpty()){
			for(Table_column tableColumn : tableColumns){
				if(StringUtil.isBlank(tableColumn.getColume_name())){
					throw new BusinessException("保存" + tableInfo.getTable_name() + "采集列时，字段名不能为空");
				}
				if(StringUtil.isBlank(tableColumn.getColumn_type())){
					throw new BusinessException("保存" + tableInfo.getTable_name() + "采集列时，字段类型不能为空");
				}
				if(StringUtil.isBlank(tableColumn.getIs_get())){
					throw new BusinessException("保存" + tableInfo.getTable_name() + "采集列时，是否采集标识位不能为空");
				}
				//设置主键
				tableColumn.setColumn_id(PrimayKeyGener.getNextId());
				//设置外键
				tableColumn.setTable_id(tableInfo.getTable_id());
				//默认所有采集列都不是主键
				tableColumn.setIs_primary_key(IsFlag.Fou.getCode());
				//设置有效开始时间和有效结束时间
				tableColumn.setValid_s_date(DateUtil.getSysDate());
				tableColumn.setValid_e_date(Constant.MAXDATE);
				//设置清洗列默认优先级
				tableColumn.setTc_or(columnCleanOrder);
				//设置是否保留原字段为是
				tableColumn.setIs_alive(IsFlag.Shi.getCode());
				//是否为变化生成设为否
				tableColumn.setIs_new(IsFlag.Fou.getCode());
				if(sortArr != null){
					for(int j = 0; j < sortArr.size(); j++){
						JSONObject jsonObject = sortArr.getJSONObject(j);
						if(jsonObject.getString("columnName").equalsIgnoreCase(tableColumn.getColume_name())){
							tableColumn.setRemark(String.valueOf(jsonObject.get("sort")));
						}
					}
				}
				//4、保存这部分数据
				tableColumn.add(Dbo.db());
			}
		}else{
			throw new BusinessException("保存" + tableInfo.getTable_name() + "的采集字段失败");
		}
	}

	@Method(desc = "处理修改采集表信息时表中列信息的保存和更新", logicStep = "" +
			"1、将collColumn反序列化为Table_column的List集合" +
			"2、如果List集合为空，抛出异常" +
			"3、否则，遍历集合，获取每一个Table_column对象，设置新的table_id，并更新到数据库中" +
			"4、这样做的目的是为了保证清洗页面配置的字段清洗和存储目的地页面定义的特殊用途字段(主键，索引列)不会丢失")
	@Param(name = "tableInfo", desc = "封装有新的表ID和表名等信息", range = "不为空，Table_info类的实体类对象", isBean = true)
	@Param(name = "collColumn", desc = "该表要采集的字段信息", range = "如果用户没有选择采集列，这个参数可以不传，" +
			"表示采集这张表的所有字段;" +
			"参数格式：json" +
			"内容：是否主键(is_primary_key)" +
			"      列名(colume_name)" +
			"      字段类型(column_type)" +
			"      列中文名(colume_ch_name)" +
			"      是否采集(is_get)" +
			"      表ID(table_id)" +
			"      有效开始日期(valid_s_date)" +
			"      有效结束日期(valid_e_date)" +
			"      是否保留原字段(is_alive)" +
			"      是否为变化生成(is_new)" +
			"      清洗顺序(tc_or)" +
			"      备注(remark)")
	private void saveTableColumnInfoForUpdate(Table_info tableInfo, String collColumn){
		//1、将collColumn反序列化为Table_column的List集合
		List<Table_column> tableColumns = JSONArray.parseArray(collColumn, Table_column.class);
		//2、如果List集合为空，抛出异常
		if(tableColumns == null || tableColumns.isEmpty()){
			throw new BusinessException("未获取到" + tableInfo.getTable_name() +"表的字段信息");
		}
		//3、否则，遍历集合，获取每一个Table_column对象，设置新的table_id，并更新到数据库中
		for(Table_column tableColumn : tableColumns){
			tableColumn.setTable_id(tableInfo.getTable_id());
			int count = tableColumn.update(Dbo.db());
			if(count != 1){
				throw new BusinessException("更新" + tableInfo.getTable_name() + "表的" + tableColumn.getColume_name() + "字段失败");
			}
		}
	}

	@Method(desc = "根据colSetId, userId和表名与Agent端交互得到该表的列信息", logicStep = "" +
			"1、根据colSetId和userId去数据库中查出DB连接信息")
	@Param(name = "colSetId", desc = "数据库设置ID，源系统数据库设置表主键，数据库对应表外键", range = "不为空")
	@Param(name = "userId", desc = "当前登录用户ID，sys_user表主键", range = "不为空")
	@Param(name = "tableName", desc = "要获取列的表名", range = "不为空")
	@Return(desc = "在Agent端获取到的该表的列信息", range = "不为空，" +
			"一个在Agent端封装好的Table_column对象的is_primary_key,colume_name,column_type属性必须有值")
	private List<Table_column> getColumnInfoByTableName(long colSetId, long userId, String tableName){
		//1、根据colSetId和userId去数据库中查出DB连接信息
		Map<String, Object> databaseInfo = getDatabaseSetInfo(colSetId, userId);
		if(databaseInfo.isEmpty()){
			throw new BusinessException("未找到数据库采集任务");
		}
		String methodName = AgentActionUtil.GETTABLECOLUMN;
		long agentId = (long) databaseInfo.get("agent_id");
		String respMsg = SendMsgUtil.getColInfoByTbName(agentId, getUserId(), databaseInfo, tableName, methodName);
		JSONArray columnInfos = JSONObject.parseArray(respMsg);
		List<Table_column> tableColumns = new ArrayList<>();
		for(int i = 0; i < columnInfos.size(); i++){
			JSONObject columnInfo = columnInfos.getJSONObject(i);
			Table_column tableColumn = new Table_column();
			tableColumn.setColume_name(columnInfo.getString("column_name"));
			tableColumn.setColume_ch_name(columnInfo.getString("column_ch_name"));
			tableColumn.setColumn_type(columnInfo.getString("type"));

			tableColumns.add(tableColumn);
		}
		return tableColumns;
	}

	@Method(desc = "根据colSetId去数据库中查出DB连接信息", logicStep = "1、根据colSetId和userId去数据库中查出DB连接信息")
	@Param(name = "colSetId", desc = "数据库设置ID，源系统数据库设置表主键，数据库对应表外键", range = "不为空")
	@Param(name = "userId", desc = "当前登录用户ID，sys_user表主键", range = "不为空")
	@Return(desc = "查询结果集", range = "不为空")
	private Map<String, Object> getDatabaseSetInfo(long colSetId, long userId){
		//1、根据colSetId和userId去数据库中查出DB连接信息
		return Dbo.queryOneObject(" select t1.database_type, t1.database_ip, t1.database_port, t1.database_name, " +
				" t1.database_pad, t1.user_name, t1.database_drive, t1.jdbc_url, t1.agent_id, t1.db_agent, t1.plane_url" +
				" from "+ Database_set.TableName +" t1" +
				" left join "+ Agent_info.TableName +" ai on ai.agent_id = t1.agent_id" +
				" where t1.database_id = ? and ai.user_id = ? ", colSetId, userId);
	}

	@Method(desc = "根据表名和数据库设置表主键获得表详细信息", logicStep = "" +
			"1、如果集合为空，则直接返回空集合" +
			"2、如果集合不是空，则进行如下处理" +
			"2-1、根据String的自然顺序(字母a-z)对表名进行升序排序" +
			"2-2、tableResult结果可能有数据，也可能没有数据" +
			"2-3、有数据的情况：编辑采集任务，模糊查询的这张表已经存在于本次采集任务中" +
			"2-4、没有数据的情况：新增采集任务，那么所有的表都无法在table_info中查到数据，给是否并行抽取默认值为否")
	@Param(name = "tableNames", desc = "需要获取详细信息的表名集合", range = "不限")
	@Param(name = "colSetId", desc = "数据库设置ID", range = "不为空")
	@Return(desc = "存有表详细信息的List集合", range = "查到信息则不为空，没有查到信息则为空")
	private List<Map<String, Object>> getTableInfoByTableName(List<String> tableNames, long colSetId){
		//1、如果集合为空，则直接返回空集合
		if(tableNames.isEmpty()){ return Collections.emptyList(); }
		//2、如果集合不是空，则进行如下处理
		List<Map<String, Object>> results = new ArrayList<>();
		//2-1、根据String的自然顺序(字母a-z)对表名进行升序排序
		Collections.sort(tableNames);
		for(String tableName : tableNames){
			Map<String, Object> tableResult = Dbo.queryOneObject(" select ti.table_id,ti.table_name," +
					" ti.table_ch_name, ti.is_parallel" +
					" FROM " + Table_info.TableName + " ti " +
					" WHERE ti.database_id = ? AND ti.table_name = ? " +
					" AND ti.is_user_defined = ? ", colSetId, tableName, IsFlag.Fou.getCode());
			//2-2、tableResult结果可能有数据，也可能没有数据
			//2-3、有数据的情况：编辑采集任务，模糊查询的这张表已经存在于本次采集任务中
			//2-4、没有数据的情况：新增采集任务，那么所有的表都无法在table_info中查到数据，给是否并行抽取默认值为否
			if(tableResult.isEmpty()){
				Map<String, Object> map = new HashMap<>();
				map.put("table_name", tableName);
				map.put("table_ch_name", tableName);
				//给是否并行抽取默认值为否
				map.put("is_parallel", IsFlag.Fou.getCode());
				results.add(map);
			}else{
				results.add(tableResult);
			}
		}
		return results;
	}

	@Method(desc = "获取列数据类型和长度/精度", logicStep = "" +
			"1、考虑到有些类型在数据库中在获取数据类型的时候就会带有(),同时还能获取到数据的长度/精度，" +
			"因此我们要对所有数据库进行统一处理，去掉()中的内容，使用JDBC提供的方法读取的长度和精度进行拼接" +
			"2、对不包含长度和精度的数据类型进行处理，返回数据类型" +
			"3、对包含长度和精度的数据类型进行处理，返回数据类型(长度,精度)" +
			"4、对只包含长度的数据类型进行处理，返回数据类型(长度)")
	@Param(name = "columnType", desc = "数据库类型", range = "不为null,java.sql.Types对象实例")
	@Param(name = "columnTypeName", desc = "字符串形式的数据库类型，通过调用ResultSetMetaData.getColumnTypeName()得到"
			, range = "不为null")
	@Param(name = "precision", desc = "对于数字类型，precision表示的是数字的精度，对于字符类型，这里表示的是长度，" +
			"调用ResultSetMetaData.getPrecision()得到", range = "不限")
	@Param(name = "scale", desc = "列数据类型小数点右边的指定列的位数，调用ResultSetMetaData.getScale()得到"
			, range = "不限，对于不适用小数位数的数据类型，返回0")
	@Return(desc = "经过处理后的数据类型", range = "" +
			"1、对不包含长度和精度的数据类型进行处理，返回数据类型" +
			"2、对包含长度和精度的数据类型进行处理，返回数据类型(长度,精度)" +
			"3、对只包含长度的数据类型进行处理，返回数据类型(长度)")
	private String getColTypeAndPreci(int columnType, String columnTypeName, int precision, int scale) {
		/*
		* 1、考虑到有些类型在数据库中在获取数据类型的时候就会带有(),同时还能获取到数据的长度和精度，
		* 因此我们要对所有数据库进行统一处理，去掉()中的内容，使用JDBC提供的方法读取的长度/精度进行拼接
		* */
		if (precision != 0) {
			int index = columnTypeName.indexOf("(");
			if (index != -1) {
				columnTypeName = columnTypeName.substring(0, index);
			}
		}
		String colTypeAndPreci;
		if (Types.INTEGER == columnType || Types.TINYINT == columnType || Types.SMALLINT == columnType ||
				Types.BIGINT == columnType) {
			//2、上述数据类型不包含长度和精度
			colTypeAndPreci = columnTypeName;
		} else if (Types.NUMERIC == columnType || Types.FLOAT == columnType ||
				Types.DOUBLE == columnType || Types.DECIMAL == columnType) {
			//上述数据类型包含长度和精度，对长度和精度进行处理，返回(长度,精度)
			//1、当一个数的整数部分的长度 > p-s 时，Oracle就会报错
			//2、当一个数的小数部分的长度 > s 时，Oracle就会舍入。
			//3、当s(scale)为负数时，Oracle就对小数点左边的s个数字进行舍入。
			//4、当s > p 时, p表示小数点后第s位向左最多可以有多少位数字，如果大于p则Oracle报错，小数点后s位向右的数字被舍入
			if (precision > precision - Math.abs(scale) || scale > precision || precision == 0) {
				precision = 38;
				scale = 12;
			}
			colTypeAndPreci = columnTypeName + "(" + precision + "," + scale + ")";
		} else {
			//处理字符串类型，只包含长度,不包含精度
			if ("char".equalsIgnoreCase(columnTypeName) && precision > 255) {
				columnTypeName = "varchar";
			}
			colTypeAndPreci = columnTypeName + "(" + precision + ")";
		}
		return colTypeAndPreci;
	}

	@Method(desc = "在修改采集表的时候，用新的tableId更新其他表中的旧的tableId", logicStep = "" +
			"1、更新table_storage_info表对应条目的table_id字段,一个table_id在该表中的数据存在情况为0-1" +
			"2、更新table_clean表对应条目的table_id字段，一个table_id在table_clean中可能有0-N条数据" +
			"3、更新data_extraction_def表对应条目的table_id，一个table_id在该表中的数据存在情况为0-1" +
			"4、更新column_merge表对应条目的table_id字段，一个table_id在该表中的数据存在情况为0-N")
	@Param(name = "newID", desc = "新的tableId", range = "不为空")
	@Param(name = "oldID", desc = "旧的tableId", range = "不为空")
	private void updateTableId(long newID, long oldID){
		//1、更新table_storage_info表对应条目的table_id字段,一个table_id在该表中的数据存在情况为0-1
		List<Object> storageIdList = Dbo.queryOneColumnList(" select storage_id from "+
				Table_storage_info.TableName + " where table_id = ? ", oldID);
		if(storageIdList.size() > 1){
			throw new BusinessException("表存储信息不唯一");
		}
		if(!storageIdList.isEmpty()){
			long storageId = (long)storageIdList.get(0);
			DboExecute.updatesOrThrow("更新表存储信息失败",
					"update "+ Table_storage_info.TableName +" set table_id = ? where storage_id = ?",
					newID, storageId);
		}

		//2、更新table_clean表对应条目的table_id字段，一个table_id在table_clean中可能有0-N条数据
		List<Object> tableCleanIdList = Dbo.queryOneColumnList(" select table_clean_id from "
				+ Table_clean.TableName + " where table_id = ? ", oldID);
		if(!tableCleanIdList.isEmpty()){
			StringBuilder tableCleanBuilder = new StringBuilder("update "+ Table_clean.TableName
					+" set table_id = ? where table_clean_id in ( ");
			for(int j = 0; j < tableCleanIdList.size(); j++){
				tableCleanBuilder.append((long)tableCleanIdList.get(j));
				if (j != tableCleanIdList.size() - 1)
					tableCleanBuilder.append(",");
			}
			tableCleanBuilder.append(" )");
			Dbo.execute(tableCleanBuilder.toString(), newID);
		}

		//3、更新data_extraction_def表对应条目的table_id，一个table_id在该表中的数据存在情况为0-1
		List<Object> extractDefIdList = Dbo.queryOneColumnList(" select ded_id from "
				+ Data_extraction_def.TableName + " where table_id = ? ", oldID);
		if(extractDefIdList.size() > 1){
			throw new BusinessException("数据抽取定义信息不唯一");
		}
		if(!extractDefIdList.isEmpty()){
			long dedId = (long)extractDefIdList.get(0);
			DboExecute.updatesOrThrow("更新数据抽取定义信息失败",
					"update "+ Data_extraction_def.TableName +" set table_id = ? where ded_id = ?",
					newID, dedId);
		}

		//4、更新column_merge表对应条目的table_id字段，一个table_id在该表中的数据存在情况为0-N
		List<Object> colMergeIdList = Dbo.queryOneColumnList(" select col_merge_id from "
				+ Column_merge.TableName + " where table_id = ? ", oldID);
		if(!colMergeIdList.isEmpty()){
			StringBuilder colMergeBuilder = new StringBuilder("update "+ Column_merge.TableName +
					" set table_id = ? where col_merge_id in ( ");
			for(int j = 0; j < colMergeIdList.size(); j++){
				colMergeBuilder.append((long)colMergeIdList.get(j));
				if (j != colMergeIdList.size() - 1)
					colMergeBuilder.append(",");
			}
			colMergeBuilder.append(" )");
			Dbo.execute(colMergeBuilder.toString(), newID);
		}
	}

	@Method(desc = "保存新增自定义SQL采集字段信息", logicStep = "" +
			"1、执行自定义SQL，获取执行结果集" +
			"2、根据结果集拿到该结果集的meta信息" +
			"3、遍历meta信息，封装成Table_column类型对象" +
			"4、保存")
	@Param(name = "tableInfo", desc = "存有table_id和自定义采集SQL的实体类对象", range = "Table_info实体类对象，" +
			"其中table_id和sql两个属性不能为空", isBean = true)
	private void saveCustomSQLColumnInfoForAdd(Table_info tableInfo){
		try {
			//1、执行自定义SQL，获取执行结果集
			ResultSet rs = Dbo.db().queryGetResultSet(tableInfo.getSql());
			//2、根据结果集拿到该结果集的meta信息
			ResultSetMetaData metaData = rs.getMetaData();
			//3、遍历meta信息，封装成Table_column类型对象
			for(int j = 0; j < metaData.getColumnCount(); j++){
				Table_column tableColumn = new Table_column();
				tableColumn.setColumn_id(PrimayKeyGener.getNextId());
				tableColumn.setTable_id(tableInfo.getTable_id());
				//是否采集设置为是
				tableColumn.setIs_get(IsFlag.Shi.getCode());
				//是否是主键，默认设置为否
				tableColumn.setIs_primary_key(IsFlag.Fou.getCode());
				tableColumn.setColume_name(metaData.getColumnName(j + 1));
				//对列类型做特殊处理，处理成varchar(512), numeric(10,4)
				String colTypeAndPreci = getColTypeAndPreci(metaData.getColumnType(j + 1),
						metaData.getColumnTypeName(j + 1), metaData.getPrecision(j + 1),
						metaData.getScale(j + 1));
				tableColumn.setColumn_type(colTypeAndPreci);
				//列中文名默认设置为列英文名
				tableColumn.setColume_ch_name(metaData.getColumnName(j + 1));
				tableColumn.setValid_s_date(DateUtil.getSysDate());
				tableColumn.setValid_e_date(Constant.MAXDATE);
				tableColumn.setIs_alive(IsFlag.Shi.getCode());
				tableColumn.setIs_new(IsFlag.Fou.getCode());
				tableColumn.setTc_or(DEFAULT_COLUMN_CLEAN_ORDER.toJSONString());

				//4、保存
				tableColumn.add(Dbo.db());
			}
		} catch (SQLException e) {
			throw new AppSystemException(e);
		}
	}

	@Method(desc = "保存修改自定义SQL采集字段信息", logicStep = "" +
			"1、调用方法删除脏数据" +
			"2、调用saveCustomSQLColumnInfoForAdd方法，按照新增的逻辑来保存自定义SQL采集字段信息" +
			"3、这样做的目的是把用户修改的自定义SQL当做全新的表进行采集")
	@Param(name = "tableInfo", desc = "存有table_id和自定义采集SQL的实体类对象", range = "Table_info实体类对象，" +
			"其中table_id和sql两个属性不能为空", isBean = true)
	@Param(name = "oldTableId", desc = "修改前的table_id", range = "不为空")
	private void saveCustomSQLColumnInfoForUpdate(Table_info tableInfo, long oldTableId){
		//1、调用方法删除脏数据
		deleteDirtyDataOfTb(oldTableId);
		//2、调用saveCustomSQLColumnInfoForAdd方法，按照新增的逻辑来保存自定义SQL采集字段信息
		saveCustomSQLColumnInfoForAdd(tableInfo);
	}

	@Method(desc = "删除tableId为外键的表脏数据", logicStep = "" +
			"1、删除的同时，删除column_id做外键的的表脏数据" +
			"2、删除旧的tableId在采集字段表中做外键的数据，不关注删除的数目" +
			"3、删除旧的tableId在数据抽取定义表做外键的数据，不关注删除的数目" +
			"4、删除旧的tableId在存储信息表做外键的数据，不关注删除的数目，同时，其对应的存储目的地关联关系也要删除" +
			"5、删除旧的tableId在列合并表做外键的数据，不关注删除的数目" +
			"6、删除旧的tableId在表清洗规则表做外键的数据，不关注删除的数目")
	@Param(name = "tableId", desc = "数据库对应表ID，" +
			"数据抽取定义表、表存储信息表、列合并表、表清洗规则表、表对应字段表表外键", range = "不为空")
	private void deleteDirtyDataOfTb(long tableId){
		//1、删除的同时，删除column_id做外键的的表脏数据
		List<Object> columnIds = Dbo.queryOneColumnList("select column_id from " + Table_column.TableName + " WHERE table_id = ?", tableId);
		if(!columnIds.isEmpty()){
			for(Object columnId : columnIds){
				deleteDirtyDataOfCol((long) columnId);
			}
		}
		//2、删除旧的tableId在采集字段表中做外键的数据，不关注删除的数目
		Dbo.execute(" DELETE FROM "+ Table_column.TableName +" WHERE table_id = ? ", tableId);
		//3、删除旧的tableId在数据抽取定义表做外键的数据，不关注删除的数目
		Dbo.execute(" DELETE FROM "+ Data_extraction_def.TableName +" WHERE table_id = ? ", tableId);
		//4、删除旧的tableId在存储信息表做外键的数据，不关注删除的数目，同时，其对应的存储目的地关联关系也要删除
		Dbo.execute(" DELETE FROM " + Data_relation_table.TableName + " WHERE storage_id = " +
				"(SELECT storage_id FROM " + Table_storage_info.TableName + " WHERE table_id = ?) ", tableId);
		Dbo.execute(" DELETE FROM "+ Table_storage_info.TableName +" WHERE table_id = ? ", tableId);
		//5、删除旧的tableId在列合并表做外键的数据，不关注删除的数目
		Dbo.execute(" DELETE FROM "+ Column_merge.TableName +" WHERE table_id = ? ", tableId);
		//6、删除旧的tableId在表清洗规则表做外键的数据，不关注删除的数目
		Dbo.execute(" DELETE FROM "+ Table_clean.TableName +" WHERE table_id = ? ", tableId);
	}

	@Method(desc = "删除columnId为外键的表脏数据", logicStep = "" +
			"1、删除旧的columnId在字段存储信息表中做外键的数据，不关注删除的数目" +
			"2、删除旧的columnId在列清洗信息表做外键的数据，不关注删除的数目" +
			"3、删除旧的columnId在列拆分信息表做外键的数据，不关注删除的数目")
	@Param(name = "columnId", desc = "表对应字段表ID，" +
			"字段存储信息表、列清洗信息表、列拆分信息表外键", range = "不为空")
	private void deleteDirtyDataOfCol(long columnId){
		//1、删除旧的columnId在字段存储信息表中做外键的数据，不关注删除的数目
		Dbo.execute("delete from " + Column_storage_info.TableName + " where column_id = ?", columnId);
		//2、删除旧的columnId在列清洗信息表做外键的数据，不关注删除的数目
		Dbo.execute("delete from " + Column_clean.TableName + " where column_id = ?", columnId);
		//3、删除旧的columnId在列拆分信息表做外键的数据，不关注删除的数目
		Dbo.execute("delete from " + Column_split.TableName + " where column_id = ?", columnId);
	}
}
