package hrds.b.biz.agent.dbagentconf.tableconf;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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

import java.util.*;

@DocClass(desc = "数据库直连采集步骤二、配置采集表页面后台服务类", author = "WangZhengcheng")
public class CollTbConfStepAction extends BaseAction {

	private static final long DEFAULT_TABLE_ID = 999999L;

	@Method(desc = "根据colSetId加载页面初始化数据", logicStep = "1、查询数据并返回")
	@Param(name = "colSetId", desc = "数据库设置ID,database_set表主键,table_info表外键", range = "不为空")
	@Return(desc = "查询结果集，查询出的结果可能有0-N条", range = "不会为null")
	//TODO 按照新的原型设计，这个方法应该查询的内容为table_id，,table_name,table_ch_name,是否并行抽取(这个字段目前还没有)
	public Result getInitInfo(long colSetId) {
			//1、查询数据并返回
			return Dbo.queryResult(" select ti.table_id,ti.table_name,ti.table_ch_name" +
					" FROM "+ Table_info.TableName +" ti " +
					" WHERE ti.database_id = ? AND ti.valid_e_date = ? AND ti.is_user_defined = ? ", colSetId,
					Constant.MAXDATE, IsFlag.Fou.getCode());
			//数据可访问权限处理方式
			//以上table_info表中都没有user_id字段，解决方式待讨论
	}

	//TODO 该方法是为前台界面的模糊查询功能提供的，关于这个功能要满足的要求，希望可以讨论一下
	@Method(desc = "根据模糊表名和数据库设置id得到表相关信息，即查询按钮", logicStep = "" +
			"1、根据colSetId去数据库中获取数据库设置相关信息" +
			"2、将查询结果转换成json，并追加模糊查询表名" +
			"3、和Agent端进行交互，得到Agent返回的数据" +
			"4、对获取到的数据进行处理，获得模糊查询到的表名" +
			"5、根据表名和colSetId获取界面需要显示的信息" +
			"6、返回信息")
	@Param(name = "colSetId", desc = "数据库设置ID,database_set表主键,table_info表外键", range = "不为空")
	@Param(name = "inputString", desc = "用户界面输入用于模糊查询的关键词", range = "不为空")
	@Return(desc = "查询结果集", range = "如果模糊查询没有查询到数据，list中没有数据，如果模糊查询查到了N条数据，" +
			"list.size()为N")
	public List<Result> getTableInfo(long colSetId, String inputString){
		//1、根据colSetId去数据库中获取数据库设置相关信息
		Result result = getDatabaseSetInfo(colSetId, getUserId());
		//数据可访问权限处理方式
		//以上SQL中使用user_id作为过滤条件，达到了访问权限控制的目的
		if(result.isEmpty()){
			throw new BusinessException("未找到数据库采集任务");
		}
		//2、将查询结果转换成json，并追加模糊查询表名
		JSONObject resultObj = JSON.parseObject(result.toJSON());
		resultObj.put("FuzzyQueryTableName", inputString);
		//3、与Agent端进行交互，得到Agent返回的数据
		//TODO 由于Agent端服务方法暂时还没有，所以这里使用的是fakeMethodName，后期会改为取AgentActionUtil类中的静态常量
		String fakeMethodName = "FAKEMETHODNAME";
		String respMsg = SendMsgUtil.sendMsg(result.getLong(0, "agent_id"), getUserId(),
				resultObj.toJSONString(), fakeMethodName);
		//4、对获取到的数据进行处理，获得模糊查询到的表名
		JSONObject respObj = JSON.parseObject(respMsg);//FIXME 为什么要用JSON？能不能改成BEAN对象
		List<String> rightTables = (List<String>) respObj.get("tableName");
		//FIXME 以上整个逻辑，用一句sql where in不就解决了吗？

		//FIXME 以下整个循环，用一句sql where in不就解决了吗？
		List<Result> returnList = new ArrayList<>();
		//5、根据表名和colSetId获取界面需要显示的信息
		if(!rightTables.isEmpty()){
			//根据String的自然顺序(字母a-z)对表名进行升序排序
			Collections.sort(rightTables);
			for(String tableName : rightTables){
				//TODO 按照新的原型设计，这个方法应该查询的内容为table_id，,table_name,table_ch_name,是否并行抽取(这个字段目前还没有)
				Result tableResult = Dbo.queryResult(" select ti.table_id,ti.table_name,ti.table_ch_name" +
						" FROM "+ Table_info.TableName +" ti " +
						" WHERE ti.database_id = ? AND ti.valid_e_date = ? AND ti.table_name = ? " +
						" AND ti.is_user_defined = ? ", colSetId, Constant.MAXDATE, tableName, IsFlag.Fou.getCode());
				//tableResult结果可能有数据，也可能没有数据
				//有数据的情况：编辑采集任务，模糊查询的这张表已经存在于本次采集任务中
				if(!tableResult.isEmpty()){
					if(tableResult.getRowCount() != 1){
						throw new BusinessException(tableName + "表详细信息不唯一");
					}
					returnList.add(tableResult);
				}
				//没有数据的情况：新增采集任务，那么所有的表都无法在table_info中查到数据，给是否并行抽取默认值为否
				//TODO 但是由于数据库表没有修改，所以Table_info实体类里面没有这个property
				if(tableResult.isEmpty()){
					tableResult.setValue(0, "table_name", tableName);
					tableResult.setValue(0, "table_ch_name", tableName);
					//TODO 给是否并行抽取默认值为否
					returnList.add(tableResult);
				}
			}
		}
		//6、返回信息
		return returnList;
	}

	@Method(desc = "根据数据库设置id得到所有表相关信息，即查看所有表", logicStep = "" +
			"1、根据colSetId去数据库中获取数据库设置相关信息" +
			"2、将查询结果转换成json" +
			"3、和Agent端进行交互，得到Agent返回的数据" +
			"4、根据表名和colSetId获取界面需要显示的信息" +
			"5、返回信息")
	@Param(name = "colSetId", desc = "数据库设置ID,database_set表主键,table_info表外键", range = "不为空")
	@Return(desc = "查询结果集", range = "如果模糊查询没有查询到数据，list中没有数据，如果模糊查询查到了N条数据，" +
			"list.size()为N")
	public List<Result> getAllTableInfo(long colSetId){
		//2、根据colSetId去数据库中获取数据库设置相关信息
		Result result = getDatabaseSetInfo(colSetId, getUserId());
		if(result.isEmpty()){
			throw new BusinessException("未找到数据库采集任务");
		}
		//3、将查询结果转换成json，并追加模糊查询表名
		JSONObject resultObj = JSON.parseObject(result.toJSON());
		//注意：查看所有表向Agent端传递的信息中FuzzyQueryTableName为NOTHING
		resultObj.put("FuzzyQueryTableName", "NOTHING");
		//4、和Agent端进行交互，得到Agent返回的数据
		//TODO 由于Agent端服务方法暂时还没有，所以这里使用的是fakeMethodName，后期会改为取AgentActionUtil类中的静态常量
		String fakeMethodName = "FAKEMETHODNAME";
		String respMsg = SendMsgUtil.sendMsg(result.getLong(0, "agent_id"), getUserId(),
				resultObj.toJSONString(), fakeMethodName);
		//5、对获取到的数据进行处理，获得模糊查询到的表名
		JSONObject respObj = JSON.parseObject(respMsg);
		List<String> tableNames = (List<String>) respObj.get("tableName");
		//FIXME 以下整个循环，用一句sql where in不就解决了吗？待讨论：和getTableInfo()方法原因一样
		List<Result> returnList = new ArrayList<>();
		if(!tableNames.isEmpty()){
			//根据String的自然顺序(字母a-z)对表名进行升序排序
			Collections.sort(tableNames);
			for(String tableName : tableNames){
				//TODO 按照新的原型设计，这个方法应该查询的内容为table_id，,table_name,table_ch_name,是否并行抽取(这个字段目前还没有)
				Result tableResult = Dbo.queryResult(" select ti.table_id,ti.table_name,ti.table_ch_name" +
						" FROM "+ Table_info.TableName +" ti " +
						" WHERE ti.database_id = ? AND ti.valid_e_date = ? AND ti.table_name = ? " +
						" AND ti.is_user_defined = ? ", colSetId, Constant.MAXDATE, tableName, IsFlag.Fou.getCode());
				//tableResult结果可能有数据，也可能没有数据
				//有数据的情况：编辑采集任务，模糊查询的这张表已经存在于本次采集任务中
				if(!tableResult.isEmpty()){
					if(tableResult.getRowCount() != 1){
						throw new BusinessException(tableName + "表详细信息不唯一");
					}
					returnList.add(tableResult);
				}

				//没有数据的情况：新增采集任务，那么所有的表都无法在table_info中查到数据，给是否并行抽取默认值为否
				if(tableResult.isEmpty()){
					tableResult.setValue(0, "table_name", tableName);
					tableResult.setValue(0, "table_ch_name", tableName);
					//TODO 给是否并行抽取默认值为否

					returnList.add(tableResult);
				}
			}
		}
		return returnList;
	}

	@Method(desc = "测试并行抽取SQL", logicStep = "" +
			"1、根据colSetId在database_set表中获得数据库设置信息" +
			"2、调用AgentActionUtil工具类获取访问Agent端服务的url" +
			"3、构建http请求访问Agent端服务" +
			"4、如果响应失败，则抛出异常")
	@Param(name = "colSetId", desc = "数据库设置ID,database_set表主键,table_info表外键", range = "不为空")
	@Param(name = "sql", desc = "前端画面传过来的用户设置的并行采集SQL", range = "不为空")
	public void testParallelExtraction(long colSetId, String sql){
		//1、根据colSetId在database_set表中获得数据库设置信息
		Map<String, Object> resultMap = Dbo.queryOneObject("select database_name, database_pad, database_drive, " +
				"database_type, jdbc_url from " + Database_set.TableName + "where database_id = ?", colSetId);
		if(resultMap.isEmpty()){
			throw new BusinessException("未找到数据库采集任务");
		}
		//2、调用AgentActionUtil工具类获取访问Agent端服务的url
		//TODO 因为Agent端还没有用于测试并行抽取SQL的方法，所以这边暂时命名为fakemethodname，后面会取AgentActionUtil中定义的静态常量
		String url = AgentActionUtil.getUrl((Long) resultMap.get("agent_id"), getUserId(), "FAKEMETHODNAME");
		//3、构建http请求访问Agent端服务
		//TODO 注意addData()的最后一个参数的名字，目前暂定为sql
		HttpClient.ResponseValue resVal = new HttpClient()
				.addData("driver", (String) resultMap.get("database_drive"))
				.addData("url", (String) resultMap.get("jdbc_url"))
				.addData("username", (String) resultMap.get("user_name"))
				.addData("password", (String) resultMap.get("database_pad"))
				.addData("dbtype", (String) resultMap.get("database_type"))
				.addData("sql", sql)
				.post(url);
		ActionResult actionResult = JsonUtil.toObjectSafety(resVal.getBodyString(), ActionResult.class).
				orElseThrow(() -> new BusinessException("应用管理端与" + url + "服务交互异常"));
		//4、如果响应失败，则抛出异常
		if(!actionResult.isSuccess()){
			throw new BusinessException("并行抽取SQL测试失败");
		}
	}

	@Method(desc = "使用SQL抽取数据页面，保存按钮后台方法", logicStep = "" +
			"1、根据databaseId去数据库中查询该数据库采集任务是否存在" +
			"2、将前端传过来的参数(JSON)转为List<Table_info>集合" +
			"3、使用databaseId在table_info表中删除所有自定义SQL采集的记录" +
			"4、遍历list,给每条记录生成ID，设置有效开始日期、有效结束日期、是否自定义SQL采集(是)、是否使用MD5(是)、" +
			"   是否仅登记(是)" +
			"5、保存数据进库")
	@Param(name = "tableInfoArray", desc = "List<Table_info>的JSONArray格式的字符串，" +
			"数组中的每一个对象必须包含table_name,table_ch_name,sql", range = "不为空")
	@Param(name = "databaseId", desc = "数据库设置ID,database_set表主键,table_info表外键", range = "不为空")
	public void saveAllSQL(String tableInfoArray, long databaseId){
		//1、根据databaseId去数据库中查询该数据库采集任务是否存在
		long dbSetCount = Dbo.queryNumber("select count(1) from database_set where database_id = ?"
				, databaseId).orElseThrow(() -> new BusinessException("必须有且只有一条数据"));
		if(dbSetCount != 1){
			throw new BusinessException("数据库采集任务未找到");
		}
		//2、将前端传过来的参数转为List<Table_info>集合
		List<Table_info> tableInfos = JSONArray.parseArray(tableInfoArray, Table_info.class);
		//3、使用databaseId在table_info表中删除所有自定义SQL采集的记录，不关注是否删除成功，结果可以是0-N
		Dbo.execute("delete from " + Table_info.TableName + " database_id = ? AND is_user_defined = ? ",
				databaseId, IsFlag.Shi.getCode());
		//4、遍历list,给每条记录生成ID，设置有效开始日期、有效结束日期、是否自定义SQL采集(是)、是否使用MD5(是)、
		// 是否仅登记(是)
		for(int i = 0; i < tableInfos.size(); i++){
			Table_info tableInfo = tableInfos.get(i);
			if(StringUtil.isBlank(tableInfo.getTable_name())){
				throw new BusinessException("第" + (i + 1) + "条数据表名不能为空");
			}
			if(StringUtil.isBlank(tableInfo.getTable_ch_name())){
				throw new BusinessException("第" + (i + 1) + "条数据表中文名不能为空");
			}
			if(StringUtil.isBlank(tableInfo.getSql())){
				throw new BusinessException("第" + (i + 1) + "条数据自定义SQL语句不能为空");
			}
			tableInfo.setTable_id(PrimayKeyGener.getNextId());
			tableInfo.setTable_count(CountNum.YiWan.getCode());
			tableInfo.setDatabase_id(databaseId);
			tableInfo.setValid_s_date(DateUtil.getSysDate());
			tableInfo.setValid_e_date(Constant.MAXDATE);
			tableInfo.setIs_user_defined(IsFlag.Shi.getCode());
			tableInfo.setIs_md5(IsFlag.Shi.getCode());
			//TODO 待讨论：这个字段的值，如果画面上允许配置，就不需要设置默认值
			tableInfo.setIs_register(IsFlag.Shi.getCode());
			//5、保存数据进库
			tableInfo.add(Dbo.db());
			//数据可访问权限处理方式
			//以上table_info表中都没有user_id字段，解决方式待讨论
		}
	}

	@Method(desc = "使用SQL抽取数据页面操作栏，删除按钮后台方法", logicStep = "" +
			"1、根据tableId在table_info表中找到该条记录" +
			"2、根据tableId在table_info表中删除该条记录")
	@Param(name = "tableId", desc = "table_info表主键", range = "不为空")
	public void deleteSQLConf(long tableId){
		//1、根据tableId在table_info表中找到该条记录
		long count = Dbo.queryNumber(" select count(1) from " + Table_info.TableName + " where table_id = ? " +
						"and is_user_defined = ?", tableId, IsFlag.Shi.getCode())
				.orElseThrow(() -> new BusinessException("查询结果必须有且仅有一条"));
		if(count != 1){
			throw new BusinessException("待删除的自定义SQL设置不存在");
		}
		//2、根据tableId在table_info表中删除该条记录
		DboExecute.deletesOrThrow("删除自定义SQL数据失败",
				" delete from "+ Table_info.TableName +" where table_id = ? and is_user_defined = ?"
				, tableId, IsFlag.Shi.getCode());
		//数据可访问权限处理方式
		//以上table_info表中都没有user_id字段，解决方式待讨论
	}

	@Method(desc = "配置采集表页面，使用SQL抽取数据Tab页后台方法，用于回显已经设置的SQL", logicStep = "" +
			"1、根据colSetId在table_info表中查询数据并返回")
	@Param(name = "colSetId", desc = "数据库设置ID,databse_set表主键，table_info表外键", range = "不为空")
	@Return(desc = "查询结果集", range = "如果用户在SQL设置界面设置了自定义查询SQL，那么List集合中就有数据，" +
			"否则list.size()为0")
	public List<Table_info> getAllSQLs(long colSetId){
		return Dbo.queryList(Table_info.class, " SELECT * FROM "+ Table_info.TableName +
				" WHERE database_id = ? " + "AND is_user_defined = ? order by table_id", colSetId, IsFlag.Shi.getCode());
		//数据可访问权限处理方式
		//以上table_info表中都没有user_id字段，解决方式待讨论
	}

	@Method(desc = "配置采集表页面,定义过滤按钮后台方法，用于回显已经对单表定义好的SQL", logicStep = "" +
			"1、根据colSetId和tableName在table_info表中获取数据并返回")
	@Param(name = "colSetId", desc = "数据库设置ID,databse_set表主键，table_info表外键", range = "不为空")
	@Param(name = "tableName", desc = "表名，table_info表的table_name字段", range = "不为空")
	@Return(desc = "查询结果集", range = "不为空，如果单表定义的SQL过滤，那么Result中就有数据，否则，就没有数据")
	public Result getSingleTableSQL(long colSetId, String tableName){
		return Dbo.queryResult("SELECT table_id,table_name,table_ch_name,storage_type,table_count,sql " +
				" FROM "+ Table_info.TableName +" WHERE database_id = ? AND valid_e_date = ? AND table_name = ? ",
				colSetId, Constant.MAXDATE, tableName);
		//数据可访问权限处理方式
		//以上table_info表中都没有user_id字段，解决方式待讨论
	}

	@Method(desc = "配置采集表页面,选择列按钮后台方法", logicStep = "" +
			"1、判断tableId的值" +
			"2、若tableId为999999，表示要获取当前采集任务中不存在的表的所有列，需要和agent进行交互" +
			"      2-1、根据colSetId去数据库中查出DB连接信息" +
			"      2-2、和Agent交互，获取表中的列信息" +
			"3、若tableId不为999999，表示要获取当前采集任务中存在的表的所有列，直接在table_column表中查询即可" +
			"      3-1、根据tableId在table_column表中获取列信息" +
			"4、返回")
	@Param(name = "tableName", desc = "要获取所有列的表的名字，table_info表的table_name字段", range = "不为空")
	@Param(name = "colSetId", desc = "数据库设置ID，databse_set表主键，table_info表外键", range = "不为空")
	@Param(name = "tableId", desc = "table_info表主键,table_column表外键",
			range = "可以为空，表示当前采集任务中不包含这张表，如果为空，默认值为999999", nullable = true,
			valueIfNull = "999999")
	@Return(desc = "列信息，有两对Entry，一对的key为table_name，value为要获得列信息的表的表名" +
			"另一对的key为columnInfo，value为List<Table_column> 表示列的信息", range = "不为空")
	public Map<String, Object> getColumnInfo(String tableName, long colSetId, long tableId){
		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("tableName", tableName);
		//1、判断tableId的值
		//2、如果要获取当前采集任务中不存在的表的所有列，需要和agent进行交互
		if(tableId != DEFAULT_TABLE_ID){
			//2-1、根据colSetId去数据库中查出DB连接信息
			//2-2、和Agent交互，获取表中的列信息
			List<Table_column> tableColumns = getColumnInfoByTableName(colSetId, getUserId(), tableName);
			returnMap.put("columnInfo", tableColumns);
		}
		//3、如果要获取当前采集任务中存在的表的所有列，直接在table_column表中查询即可
		else{
			//3-1、根据tableId在table_column表中获取列信息
			List<Table_column> tableColumns = Dbo.queryList(Table_column.class, " SELECT * FROM "+
					Table_column.TableName + "WHERE table_id = ? order by remark", tableId);
			returnMap.put("columnInfo", tableColumns);
		}
		//4、返回
		return returnMap;
	}

	@Method(desc = "如果页面既有基于配置的采集表，又有自定义SQL查询采集，保存单个表的采集信息，如果要采集多张表，" +
			"前端多次调用该方法，自定义SQL查询采集信息已经调用saveAllSQL()方法保存了", logicStep = "" +
			"1、校验Table_info对象中的信息是否合法" +
			"2、给Table_info对象设置基本信息(valid_s_date,valid_e_date,is_user_defined,is_register)" +
			"3、不论新增采集表还是编辑采集表，页面上所有的内容都可能被修改，所以直接执行SQL，" +
			"按database_id删除table_info表中所有非自定义采集SQL的数据" +
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
			"8、不是新增采集表还是编辑采集表，都需要将该表要采集的列信息保存到相应的表里面")
	//TODO 注意这边，目前是否并行抽取和并且抽取SQL还没有
	@Param(name = "tableInfo", desc = "一个Table_info对象必须包含table_name,table_ch_name和是否并行抽取，如果并行抽取，" +
			"那么并行抽取SQL也要有，如果是新增的采集表，table_id为空，如果是编辑修改采集表，table_id不能为空" +
			"用于单表过滤的sql页面没定义就是空，页面定义了就不为空", range = "不为空，Table_info类的实体类对象"
			, isBean = true)
	@Param(name = "colSetId", desc = "数据库设置ID，databse_set表主键，table_info表外键", range = "不为空")
	@Param(name = "collColumn", desc = "该表要采集的字段信息", range = "可以为空，如果用户没有选择采集列，" +
			"则这个参数可以不传，默认值为空字符串,如果用户指定了采集某张表的某些字段，则这个参数不为空，json数组格式，" +
			" 一个json对象内容包括是否主键(is_primary_key)，列名(colume_name)，字段类型(column_type)" +
			"，列中文名(colume_ch_name)", nullable = true, valueIfNull = "")
	@Param(name = "columnSort", desc = "该表要采集的字段的排序", range = "可以为空，如果用户没有自定义采集字段排序，" +
			"则该参数可以不传，默认值为空字符串", nullable = true, valueIfNull = "")
	@Return(desc = "保存成功后返回database_id，用于下一个页面能够拿到上一个页面的信息", range = "不为空")
	public long saveCollSingleTbInfo(Table_info tableInfo, long colSetId, String collColumn, String columnSort){
		//1、校验Table_info对象中的信息是否合法
		if(StringUtil.isBlank(tableInfo.getTable_name())){
			throw new BusinessException("保存采集表配置，表名不能为空!");
		}
		if(StringUtil.isBlank(tableInfo.getTable_ch_name())){
			throw new BusinessException("保存采集表配置，表中文名不能为空!");
		}
		/*
		TODO 下面两个应该改为校验是否并行抽取，如果并行抽取，则是否配置了并行抽取SQL
		if(StringUtil.isBlank(tableInfo.getIs_md5())){
			throw new BusinessException("保存采集表配置，是否计算MD5不能为空!");
		}
		if(IsFlag.ofEnumByCode(tableInfo.getIs_md5()) == null){
			throw new BusinessException("是否计算MD5配置值错误，系统无法识别");
		}
		*/
		Map<String, Object> resultMap = Dbo.queryOneObject("select * from " + Database_set.TableName
				+ "where database_id = ?", colSetId);
		if(resultMap.isEmpty()){
			throw new BusinessException("未找到数据库采集任务");
		}
		//2、给Table_info对象设置基本信息(valid_s_date,valid_e_date,is_user_defined,is_register)
		tableInfo.setValid_s_date(DateUtil.getSysDate());
		tableInfo.setValid_e_date(Constant.MAXDATE);
		tableInfo.setIs_user_defined(IsFlag.Fou.getCode());
		//TODO 这一项是否需要设置待讨论，如果允许页面上配置，那么就不需要设置默认值
		tableInfo.setIs_register(IsFlag.Shi.getCode());
		//3、不论新增采集表还是编辑采集表，页面上所有的内容都可能被修改，所以直接执行SQL，按database_id删除table_info表
		// 中,所有非自定义采集SQL的数据，不关心删除数据的条数
		Dbo.execute(" DELETE FROM "+ Table_info.TableName +" WHERE database_id =? AND valid_e_date = ? " +
						"AND is_user_defined = ? ", colSetId, Constant.MAXDATE, IsFlag.Fou.getCode());
		//4、获取Table_info对象的table_id属性，如果该属性没有值，说明这张采集表是新增的，否则这张采集表在当前采集任务中
		//已经存在，且有可能经过了修改
		//5、不论新增还是修改，构造默认的表清洗优先级和列清洗优先级(JSON格式的字符串，保存进入数据库)
		JSONObject tableCleanOrder = new JSONObject();
		tableCleanOrder.put(CleanType.ZiFuBuQi.getCode(), 1);
		tableCleanOrder.put(CleanType.ZiFuTiHuan.getCode(), 2);
		tableCleanOrder.put(CleanType.ZiFuHeBing.getCode(), 3);
		tableCleanOrder.put(CleanType.ZiFuTrim.getCode(), 4);

		JSONObject columnCleanOrder = new JSONObject();
		columnCleanOrder.put(CleanType.ZiFuBuQi.getCode(), 1);
		columnCleanOrder.put(CleanType.ZiFuTiHuan.getCode(), 2);
		columnCleanOrder.put(CleanType.ShiJianZhuanHuan.getCode(), 3);
		columnCleanOrder.put(CleanType.MaZhiZhuanHuan.getCode(), 4);
		columnCleanOrder.put(CleanType.ZiFuChaiFen.getCode(), 5);
		columnCleanOrder.put(CleanType.ZiFuTrim.getCode(), 6);
		//6、如果是新增采集表
		if(tableInfo.getTable_id() == null){
			//6-1、生成table_id，并存入Table_info对象中
			tableInfo.setTable_id(PrimayKeyGener.getNextId());
			tableInfo.setTi_or(tableCleanOrder.toJSONString());
			//6-2、保存Table_info对象
			tableInfo.add(Dbo.db());
			//6-3、新增采集表，将该表要采集的列信息保存到相应的表里面
			saveTableColumnInfoForAdd(tableInfo, collColumn, columnSort, colSetId,
					columnCleanOrder.toJSONString());
		}
		//7、如果是修改采集表
		else{
			//7-1、保留原有的table_id，为当前数据设置新的table_id
			long oldID = tableInfo.getTable_id();
			String newID = PrimayKeyGener.getNextId();
			tableInfo.setTable_id(newID);
			tableInfo.setTi_or(tableCleanOrder.toJSONString());
			//7-2、保存Table_info对象
			tableInfo.add(Dbo.db());
			//7-3、所有关联了原table_id的表，找到对应的字段，为这些字段设置新的table_id
			//7-3-1、更新table_storage_info表对应条目的table_id字段
			List<Object> storageIdList = Dbo.queryOneColumnList(" select storage_id from "+
					Table_storage_info.TableName + "where table_id = ? ", oldID);
			for(Object obj : storageIdList){
				long storageId = (long)obj;
				Dbo.execute("update "+ Table_storage_info.TableName +" set table_id = ? where storage_id = ?",
						newID, storageId);
			}
			//7-3-2、更新table_clean表对应条目的table_id字段
			List<Object> tableCleanIdList = Dbo.queryOneColumnList(" select c_id from "+ Table_clean.TableName
					+ " where table_id = ? ", oldID);
			for(Object obj : tableCleanIdList){
				long tableCleanId = (long)obj;
				Dbo.execute("update "+ Table_clean.TableName +" set table_id = ? where c_id = ?",
						newID, tableCleanId);
			}
			//7-3-3、更新column_merge表对应条目的table_id字段
			List<Object> colMergeIdList = Dbo.queryOneColumnList(" select col_id from "
					+ Column_merge.TableName + "where table_id = ? ", oldID);
			for(Object obj : colMergeIdList){
				long colMergeId = (long)obj;
				Dbo.execute("update "+ Column_merge.TableName +" set table_id = ? where col_id = ?",
						newID, colMergeId);
			}
			//7-3-4、编辑采集表，将该表要采集的列信息保存到相应的表里面
			saveTableColumnInfoForUpdate(tableInfo, oldID, collColumn, columnSort, colSetId,
					columnCleanOrder.toJSONString());
		}

		return colSetId;
	}

	@Method(desc = "如果页面只有自定义SQL查询采集，保存该界面配置的所有信息", logicStep = "" +
			"1、因为自定义表已经入库了，所以要在table_info表中删除不是自定义的表，删除的条数可能为0-N")
	@Param(desc = "数据库设置ID，databse_set表主键，table_info表外键", range = "不为空")
	@Return(desc = "保存成功后返回database_id，用于下一个页面能够拿到上一个页面的信息", range = "不为空")
	public long saveCustomizeCollTbInfo(long colSetId){
		//1、因为自定义表已经入库了，所以要在table_info表中删除不是自定义SQL的表信息，删除的条数可能为0-N
		Dbo.execute(" DELETE FROM "+ Table_info.TableName +" WHERE database_id = ? AND valid_e_date = ? " +
						"AND is_user_defined = ? ", colSetId, Constant.MAXDATE, IsFlag.Fou.getCode());
		return colSetId;
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
	@Param(name = "collColumn", desc = "该表要采集的字段信息", range = "可以为空，如果用户没有选择采集列，" +
			"则这个参数可以不传，默认值为空字符串;如果用户指定了采集某张表的某些字段，则这个参数不为空，json数组格式，" +
			"一个json对象内容包括是否主键(is_primary_key)，列名(colume_name)，字段类型(column_type)，列中文名(colume_ch_name)")
	@Param(name = "columnSort", desc = "该表要采集的字段的排序", range = "" +
			"可以为空，如果用户没有自定义采集字段排序，则该参数可以不传，默认值为空字符串" +
			"如果用户定义了，key为列名,value为列的排序")
	@Param(name = "colSetId", desc = "数据库设置ID，databse_set表主键，table_info表外键", range = "不为空")
	@Param(name = "columnCleanOrder", desc = "列清洗默认优先级", range = "不为空，json格式的字符串")
	private void saveTableColumnInfoForAdd(Table_info tableInfo, String collColumn, String columnSort,
	                                       long colSetId, String columnCleanOrder){
		//1、判断columnSort是否为空字符串，如果不是空字符串，解析columnSort为json对象
		JSONObject sortObj = null;
		if(StringUtil.isNotBlank(columnSort)){
			sortObj = JSON.parseObject(columnSort);
		}
		//2、判断collColumn参数是否为空字符串
		List<Table_column> tableColumns;
		if(StringUtil.isBlank(collColumn)){
			//2-1、是，表示用户没有选择采集列，则应用管理端同Agent端交互
			tableColumns = getColumnInfoByTableName(colSetId, getUserId(), tableInfo.getTable_name());
		}else{
			//1-2、否，表示用户自定义采集列，则解析collColumn为List集合
			tableColumns = JSON.parseArray(collColumn, Table_column.class);
		}
		//3、设置主键，外键等信息，如果columnSort不为空，则将Table_column对象的remark属性设置为该列的采集顺序
		for(Table_column tableColumn : tableColumns){
			//设置主键
			tableColumn.setColumn_id(PrimayKeyGener.getNextId());
			//设置外键
			tableColumn.setTable_id(tableInfo.getTable_id());
			//设置有效开始时间和有效结束时间
			tableColumn.setValid_s_date(DateUtil.getSysDate());
			tableColumn.setValid_e_date(Constant.MAXDATE);
			//设置清洗列默认优先级
			tableColumn.setTc_or(columnCleanOrder);
			if(sortObj != null){
				tableColumn.setRemark((String) sortObj.get(tableColumn.getColume_name()));
			}
			//4、保存这部分数据
			tableColumn.add(Dbo.db());
		}
	}

	@Method(desc = "处理修改采集表信息时表中列信息的保存和更新", logicStep = "" +
			"1、在修改采集表信息时，由于table_column表中之前已经保存过这张表之前的列信息，所以要使用oldTableID进行一次删除" +
			"2、按照新增的逻辑，重新插入table_column本次修改的数据")
	@Param(name = "tableInfo", desc = "一个Table_info对象必须包含table_name,table_ch_name和是否并行抽取" +
			"如果并行抽取，那么并行抽取SQL也要有,如果是新增的采集表，table_id为空，如果是编辑修改采集表，table_id不能为空" +
			"用于过滤的sql页面没定义就是空，页面定义了就不为空", range = "不为空，Table_info类的实体类对象")
	@Param(name = "oldTableID", desc = "修改前的table_info表ID", range = "不为空")
	@Param(name = "collColumn", desc = "该表要采集的字段信息", range = "" +
			"可以为空，如果用户没有选择采集列，则这个参数可以不传，默认值为空字符串" +
			"如果用户指定了采集某张表的某些字段，则这个参数不为空，json数组格式，" +
			"一个json对象内容包括是否主键(is_primary_key)，列名(colume_name)，" +
			"字段类型(column_type)，列中文名(colume_ch_name)")
	@Param(name = "columnSort", desc = "该表要采集的字段的排序", range = "不为空")
	@Param(name = "colSetId", desc = "数据库设置ID，databse_set表主键，table_info表外键", range = "不为空")
	@Param(name = "columnCleanOrder", desc = "默认的表清洗优先级", range = "不为空，JSON字符串")
	private void saveTableColumnInfoForUpdate(Table_info tableInfo, long oldTableID, String collColumn,
	                                          String columnSort, long colSetId,
	                                          String columnCleanOrder){
		//1、在修改采集表信息时，由于table_column表中之前已经保存过这张表之前的列信息，所以要使用oldTableID进行一次删除，
		// 不关注删除的数目
		Dbo.execute(" DELETE FROM "+ Table_column.TableName +" WHERE table_id = ? ", oldTableID);
		//2、按照新增的逻辑，重新插入table_column本次修改的数据
		saveTableColumnInfoForAdd(tableInfo, collColumn, columnSort, colSetId, columnCleanOrder);
	}

	@Method(desc = "根据colSetId, userId和表名与Agent端交互得到该表的列信息", logicStep = "" +
			"1、根据colSetId和userId去数据库中查出DB连接信息")
	@Param(name = "colSetId", desc = "数据库设置ID，databse_set表主键，table_info表外键", range = "不为空")
	@Param(name = "userId", desc = "当前登录用户ID，sys_user表主键", range = "不为空")
	@Param(name = "tableName", desc = "要获取列的表名", range = "不为空")
	@Return(desc = "在Agent端获取到的该表的列信息", range = "不为空，" +
			"一个在Agent端封装好的Table_column对象的is_primary_key,colume_name,column_type属性必须有值")
	private List<Table_column> getColumnInfoByTableName(long colSetId, long userId, String tableName){
		Result result = getDatabaseSetInfo(colSetId, userId);
		if(result.isEmpty()){
			throw new AppSystemException("未找到数据库采集任务");
		}
		JSONObject resultObj = JSON.parseObject(result.toJSON());
		resultObj.put("basedTableName", tableName);
		//TODO 由于Agent端服务方法暂时还没有，所以这里使用的是fakeMethodName，后期会改为取AgentActionUtil类中的静态常量
		String fakeMethodName = "FAKEMETHODNAME";
		String respMsg = SendMsgUtil.sendMsg(result.getLong(0, "agent_id"), getUserId(),
				resultObj.toJSONString(), fakeMethodName);
		return JSONObject.parseArray(respMsg, Table_column.class);
	}

	@Method(desc = "根据colSetId去数据库中查出DB连接信息", logicStep = "1、根据colSetId和userId去数据库中查出DB连接信息")
	@Param(name = "colSetId", desc = "数据库设置ID，databse_set表主键，table_info表外键", range = "不为空")
	@Param(name = "userId", desc = "当前登录用户ID，sys_user表主键", range = "不为空")
	@Return(desc = "查询结果集", range = "不为空")
	private Result getDatabaseSetInfo(long colSetId, long userId){
		//1、根据colSetId和userId去数据库中查出DB连接信息
		return Dbo.queryResult(" select t1.*, t2.* from "+ Database_set.TableName +" t1" +
				" left join "+ Agent_info.TableName +" ai on ai.agent_id = t1.agent_id" +
				" left join "+ Collect_job_classify.TableName +" t2 on t1.classify_id = t2.classify_id" +
				" where t1.database_id = ? and ai.user_id = ? ", colSetId, userId);
	}
}
