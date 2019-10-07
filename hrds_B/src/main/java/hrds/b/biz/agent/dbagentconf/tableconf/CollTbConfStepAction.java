package hrds.b.biz.agent.dbagentconf.tableconf;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.resultset.Result;
import fd.ng.web.annotation.RequestBean;
import fd.ng.web.annotation.RequestParam;
import fd.ng.web.util.Dbo;
import hrds.b.biz.agent.tools.SendMsgUtil;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.CleanType;
import hrds.commons.codes.CountNum;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.Constant;
import hrds.commons.utils.DboExecute;
import hrds.commons.utils.key.PrimayKeyGener;

import java.util.*;

/**
 * @description: 步骤二、配置采集表页面后台服务类
 * @author: WangZhengcheng
 * @create: 2019-09-06 14:23
 **/
public class CollTbConfStepAction extends BaseAction {

	private static final long DEFAULT_TABLE_ID = 999999L;

	/**
	 * 根据colSetId加载页面初始化数据
	 *
	 * 1、查询数据并返回
	 *
	 * @Param: colSetId long
	 *         含义：数据库设置ID,database_set表主键,table_info表外键
	 *         取值范围：不为空
	 * @return: fd.ng.db.resultset.Result
	 *          含义：查询结果集，查询出的结果可能有0-N条
	 *          取值范围：不会为null
	 *
	 * */
	public Result getInitInfo(long colSetId) {
			//1、查询数据并返回
			return Dbo.queryResult(" select ti.table_id,ti.table_name,ti.table_ch_name," +
					" ti.table_count,ti.is_md5 FROM "+ Table_info.TableName +" ti " +
					" WHERE ti.database_id = ? AND ti.valid_e_date = ? AND ti.is_user_defined = ? ", colSetId,
					Constant.MAXDATE, IsFlag.Fou.getCode());
			//数据可访问权限处理方式
			//以上table_info表中都没有user_id字段，解决方式待讨论
	}

	/**
	 * 根据模糊表名和数据库设置id和agentId得到表相关信息
	 *
	 * 1、根据colSetId去数据库中获取数据库设置相关信息
	 * 2、将查询结果转换成json，并追加模糊查询表名
	 * 3、和Agent端进行交互，得到Agent返回的数据
	 * 4、对获取到的数据进行处理，获得模糊查询到的表名
	 * 5、根据表名和colSetId获取界面需要显示的信息
	 * 6、返回信息
	 *
	 * @Param: agentId long
	 *         含义：agentID,agent_info表主键
	 *         取值范围：不为空
	 * @Param: colSetId long
	 *         含义：数据库设置ID,database_set表主键,table_info表外键
	 *         取值范围：不为空
	 * @Param: inputString long
	 *         含义：用户界面输入的字符串  //FIXME 废话
	 *         取值范围:
	 *
	 * @return: List<Result>
	 *          含义：查询结果集
	 *          取值范围：如果模糊查询没有查询到数据，list中没有数据，如果模糊查询查到了N条数据，list.size()为N
	 * */
	public List<Result> getTableInfo(long agentId, long colSetId, String inputString){
		//1、根据colSetId去数据库中获取数据库设置相关信息
		Result result = getDatabaseSetInfo(colSetId, getUserId());

		//数据可访问权限处理方式
		//以上SQL中使用user_id作为过滤条件，达到了访问权限控制的目的

		//2、将查询结果转换成json，并追加模糊查询表名
		JSONObject resultObj = JSON.parseObject(result.toJSON());
		resultObj.put("FuzzyQueryTableName", inputString);
		//3、与Agent端进行交互，得到Agent返回的数据
		//TODO 由于Agent端服务方法暂时还没有，所以这里使用的是fakeMethodName，后期会改为取AgentActionUtil类中的静态常量
		String fakeMethodName = "FAKEMETHODNAME";
		String respMsg = SendMsgUtil.sendMsg(agentId, getUserId(), resultObj.toJSONString(), fakeMethodName);
		//4、对获取到的数据进行处理，获得模糊查询到的表名
		JSONObject respObj = JSON.parseObject(respMsg);//FIXME 为什么要用JSON？能不能改成BEAN对象
		List<String> tableNames = (List<String>) respObj.get("tableName");
		List<String> rightTables = new ArrayList<>();
		if(!tableNames.isEmpty()){
			String[] inputTableNames = inputString.split("\\|");
			for(String inputTableName : inputTableNames){
				for(String tableName : tableNames){
					if(tableName.toLowerCase().equals(inputTableName.toLowerCase())){
						rightTables.add(tableName);
					}
				}
			}
		}
		//FIXME 以上整个逻辑，用一句sql where in不就解决了吗？

		//FIXME 以下整个循环，用一句sql where in不就解决了吗？
		List<Result> returnList = new ArrayList<>();
		//5、根据表名和colSetId获取界面需要显示的信息
		if(!rightTables.isEmpty()){
			//根据String的自然顺序(字母a-z)对表名进行升序排序
			Collections.sort(rightTables);
			for(String tableName : rightTables){
				Result tableResult = Dbo.queryResult(" select ti.table_id,ti.table_name,ti.table_ch_name," +
						" ti.table_count,ti.is_md5 FROM "+ Table_info.TableName +" ti " +
						" WHERE ti.database_id = ? AND ti.valid_e_date = ? AND ti.table_name = ? " +
						" AND ti.is_user_defined = ? ", colSetId, Constant.MAXDATE, tableName, IsFlag.Fou.getCode());
				//tableResult结果可能有数据，也可能没有数据
				//有数据的情况：编辑采集任务，模糊查询的这张表已经存在于本次采集任务中
				//没有数据的情况1：新增采集任务，那么所有的表都无法在table_info中查到数据
				if(!tableResult.isEmpty()){
					if(tableResult.getRowCount() != 1){
						throw new BusinessException(tableName + "表详细信息不唯一");
					}
					returnList.add(tableResult);
				}

				if(tableResult.isEmpty()){
					tableResult.setValue(0, "table_name", tableName);
					tableResult.setValue(0, "table_ch_name", tableName);
					tableResult.setValue(0, "table_count", "100000");
					tableResult.setValue(0, "is_md5", IsFlag.Shi.getCode());

					returnList.add(tableResult);
				}
			}
		}
		//6、返回信息
		return returnList;
	}

	/**
	 * 根据数据库设置id和agentId得到所有表相关信息
	 *
	 * 1、根据colSetId去数据库中获取数据库设置相关信息
	 * 2、将查询结果转换成json
	 * 3、和Agent端进行交互，得到Agent返回的数据
	 * 4、根据表名和colSetId获取界面需要显示的信息
	 * 5、返回信息
	 *
	 * @Param: agentId long
	 *         含义：agentID,agent_info表主键
	 *         取值范围：不为空
	 * @Param: colSetId long
	 *         含义：数据库设置ID,database_set表主键,table_info表外键
	 *         取值范围：不为空
	 * @Param: inputString long
	 *         含义：用户界面输入的字符串
	 *         取值范围:
	 *
	 * @return: List<Result>
	 *          含义：查询结果集
	 *          取值范围：如果模糊查询没有查询到数据，list中没有数据，如果模糊查询查到了N条数据，list.size()为N
	 * */
	public List<Result> getAllTableInfo(long agentId, long colSetId){
		//2、根据colSetId去数据库中获取数据库设置相关信息
		Result result = getDatabaseSetInfo(colSetId, getUserId());
		//3、将查询结果转换成json，并追加模糊查询表名
		JSONObject resultObj = JSON.parseObject(result.toJSON());
		//注意：查看所有表向Agent端传递的信息中FuzzyQueryTableName为NOTHING
		resultObj.put("FuzzyQueryTableName", "NOTHING");
		//4、和Agent端进行交互，得到Agent返回的数据
		//TODO 由于Agent端服务方法暂时还没有，所以这里使用的是fakeMethodName，后期会改为取AgentActionUtil类中的静态常量
		String fakeMethodName = "FAKEMETHODNAME";
		String respMsg = SendMsgUtil.sendMsg(agentId, getUserId(), resultObj.toJSONString(), fakeMethodName);
		//5、对获取到的数据进行处理，获得模糊查询到的表名
		JSONObject respObj = JSON.parseObject(respMsg);
		List<String> tableNames = (List<String>) respObj.get("tableName");
		//FIXME 以下整个循环，用一句sql where in不就解决了吗？
		List<Result> returnList = new ArrayList<>();
		if(!tableNames.isEmpty()){
			//根据String的自然顺序(字母a-z)对表名进行升序排序
			Collections.sort(tableNames);
			for(String tableName : tableNames){
				Result tableResult = Dbo.queryResult(" select ti.table_id,ti.table_name,ti.table_ch_name," +
						" ti.table_count,ti.is_md5 FROM "+ Table_info.TableName +" ti " +
						" WHERE ti.database_id = ? AND ti.valid_e_date = ? AND ti.table_name = ? " +
						" AND ti.is_user_defined = ? ", colSetId, Constant.MAXDATE, tableName, IsFlag.Fou.getCode());
				//tableResult结果可能有数据，也可能没有数据
				//有数据的情况：编辑采集任务，模糊查询的这张表已经存在于本次采集任务中
				//没有数据的情况1：新增采集任务，那么所有的表都无法在table_info中查到数据
				if(!tableResult.isEmpty()){
					if(tableResult.getRowCount() != 1){
						throw new BusinessException(tableName + "表详细信息不唯一");
					}
					returnList.add(tableResult);
				}

				if(tableResult.isEmpty()){
					tableResult.setValue(0, "table_name", tableName);
					tableResult.setValue(0, "table_ch_name", tableName);
					tableResult.setValue(0, "table_count", "100000");
					tableResult.setValue(0, "is_md5", IsFlag.Shi.getCode());

					returnList.add(tableResult);
				}
			}
		}
		return returnList;
	}

	/**
	 * SQL查询设置页面，保存按钮后台方法
	 *
	 * 1、将前端传过来的参数转为List<Table_info>集合
	 * 2、使用databaseId在table_info表中删除所有自定义SQL采集的记录
	 * 3、遍历list,给每条记录生成ID，设置有效开始日期、有效结束日期、是否自定义SQL采集(是)、是否使用MD5(是)、
	 *    是否仅登记(是)
	 * 4、保存数据进库
	 *
	 * @Param: tableInfoArray String
	 *         含义：table_info表的JSONArray格式的字符串，数组中的每一个对象必须包含table_name,table_ch_name,sql
	 *         取值范围：不为空
	 * @Param: colSetId long
	 *         含义：数据库设置ID,database_set表主键,table_info表外键
	 *         取值范围：不为空
	 * */
	public void saveAllSQL(String tableInfoArray, long databaseId){
		//1、将前端传过来的参数转为List<Table_info>集合
		List<Table_info> tableInfos = JSONArray.parseArray(tableInfoArray, Table_info.class);
		//2、使用databaseId在table_info表中删除所有自定义SQL采集的记录，不关注是否删除成功，结果可以是0-N
		Dbo.execute("delete from " + Table_info.TableName + " database_id = ? AND is_user_defined = ? ",
				databaseId, IsFlag.Shi.getCode());
		//3、遍历list,给每条记录生成ID，设置有效开始日期、有效结束日期、是否自定义SQL采集(是)、是否使用MD5(是)、
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
			//TODO 待讨论：这个字段的值
			tableInfo.setIs_register(IsFlag.Shi.getCode());
			//4、保存数据进库
			tableInfo.add(Dbo.db());
			//数据可访问权限处理方式
			//以上table_info表中都没有user_id字段，解决方式待讨论
		}
	}

	/**
	 * SQL查询设置页面操作栏，删除按钮后台方法
	 *
	 * 1、根据tableId在table_info表中找到该条记录
	 * 2、根据tableId在table_info表中删除该条记录
	 *
	 * @Param: tableId String
	 *         含义：table_info表主键
	 *         取值范围：不为空
	 * */
	public void deleteSQLConf(long tableId){
		long count = Dbo.queryNumber(" select count(1) from " + Table_info.TableName + " where table_id = ? "
				, tableId).orElseThrow(() -> new BusinessException("查询结果必须有且仅有一条"));
		if(count != 1){
			throw new BusinessException("待删除的自定义SQL设置不存在");
		}
		DboExecute.deletesOrThrow("删除自定义SQL数据失败",
				" delete from "+ Table_info.TableName +" where table_id = ? ", tableId);
		//数据可访问权限处理方式
		//以上table_info表中都没有user_id字段，解决方式待讨论
	}

	/**
	 * 配置采集表页面，SQL设置按钮后台方法，用于回显已经设置的SQL
	 *
	 * 1、根据colSetId在table_info表中查询数据并返回
	 *
	 * @Param: colSetId long
	 *         含义：数据库设置ID,databse_set表主键，table_info表外键
	 *         取值范围：不为空
	 *
	 * @return: Result
	 *          含义：查询结果集
	 *          取值范围：如果用户在SQL设置界面设置了自定义查询SQL，那么List集合中就有数据，否则list.size()为0
	 * */
	public List<Table_info> getAllSQLs(long colSetId){
		return Dbo.queryList(Table_info.class, " SELECT * FROM "+ Table_info.TableName +
				" WHERE database_id = ? " + "AND is_user_defined = ? ", colSetId, IsFlag.Shi.getCode());
		//数据可访问权限处理方式
		//以上table_info表中都没有user_id字段，解决方式待讨论
	}

	/**
	 * 配置采集表页面,定义过滤按钮后台方法，用于回显已经对单表定义好的SQL
	 *
	 * 1、根据colSetId和tableName在table_info表中获取数据并返回
	 *
	 * @Param: colSetId long
	 *         含义：数据库设置ID,databse_set表主键，table_info表外键
	 *         取值范围：不为空
	 * @Param: tableName String
	 *         含义：表名，table_info表的table_name字段
	 *         取值范围：不为空
	 *
	 * @return: Result
	 *          含义：查询结果集
	 *          取值范围：不为空，如果单表定义的SQL过滤，那么Result中就有数据，否则，就没有数据
	 * */
	public Result getSingleTableSQL(long colSetId, String tableName){
		return Dbo.queryResult("SELECT table_id,table_name,table_ch_name,storage_type,table_count,sql " +
				" FROM "+ Table_info.TableName +" WHERE database_id = ? AND valid_e_date = ? AND table_name = ? ",
				colSetId, Constant.MAXDATE, tableName);
		//数据可访问权限处理方式
		//以上table_info表中都没有user_id字段，解决方式待讨论
	}

	/**
	 * 配置采集表页面,选择列按钮后台方法
	 *
	 * 1、判断tableId的值
	 * 2、若tableId为999999，表示要获取当前采集任务中不存在的表的所有列，需要和agent进行交互
	 *      2-1、根据colSetId去数据库中查出DB连接信息
	 *      2-2、和Agent交互，获取表中的列信息
	 * 3、若tableId不为999999，表示要获取当前采集任务中存在的表的所有列，直接在table_column表中查询即可
	 *      3-1、根据tableId在table_column表中获取列信息
	 * 4、返回
	 *
	 * @Param: agentId long
	 *         含义：agentID,agent_info表主键
	 *         取值范围：不为空
	 * @Param: tableName String
	 *         含义：要获取所有列的表的名字，table_info表的table_name字段
	 *         取值范围：不为空
	 * @Param: colSetId long
	 *         含义：数据库设置ID，databse_set表主键，table_info表外键
	 *         取值范围：不为空
	 * @Param: tableId long
	 *         含义：table_info表主键,table_column表外键
	 *         取值范围：可以为空，表示当前采集任务中不包含这张表，如果为空，默认值为999999
	 *
	 * @return: Map<String, Object>
	 *          含义：
	 *          取值范围：不为空
	 * */
	public Map<String, Object> getColumnInfo(long agentId, String tableName, long colSetId,
	                                         @RequestParam(nullable = true, valueIfNull = "999999") long tableId){

		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("tableName", tableName);
		//1、判断tableId的值
		//2、如果要获取当前采集任务中不存在的表的所有列，需要和agent进行交互
		if(tableId != DEFAULT_TABLE_ID){
			//2-1、根据colSetId去数据库中查出DB连接信息
			Result result = getDatabaseSetInfo(colSetId, getUserId());
			JSONObject resultObj = JSON.parseObject(result.toJSON());
			resultObj.put("basedTableName", tableName);
			//2-2、和Agent交互，获取表中的列信息
			//TODO 由于Agent端服务方法暂时还没有，所以这里使用的是fakeMethodName，后期会改为取AgentActionUtil类中的静态常量
			String fakeMethodName = "FAKEMETHODNAME";
			//这里获得的respMsg必须是在Agent端以List<Table_column>序列化成的JSONArray形式的字符串
			String respMsg = SendMsgUtil.sendMsg(agentId, getUserId(), resultObj.toJSONString(), fakeMethodName);
			List<Table_column> tableColumns = JSONObject.parseArray(respMsg, Table_column.class);
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

	/**
	 * 如果页面既有基于配置的采集表，又有自定义SQL查询采集，保存单个表的采集信息，如果要采集多张表，前端多次调用该方法
	 *
	 * 1、校验Table_info对象中的信息是否合法
	 * 2、给Table_info对象设置基本信息(valid_s_date,valid_e_date,is_user_defined,is_register)
	 * 3、不论新增采集表还是编辑采集表，页面上所有的内容都可能被修改，所以直接执行SQL，按database_id删除table_info表中
	 *    所有非自定义采集SQL的数据
	 * 4、获取Table_info对象的table_id属性，如果该属性没有值，说明这张采集表是新增的，否则这张采集表在当前采集任务中
	 *    已经存在，且有可能经过了修改
	 * 5、不论新增还是修改，构造默认的表清洗优先级
	 * 6、如果是新增采集表
	 *      6-1、生成table_id，并存入Table_info对象中
	 *      6-2、保存Table_info对象
	 *      6-3、保存该表中所有字段的信息进入table_column表
	 * 7、如果是修改采集表
	 *      7-1、保留原有的table_id，为当前数据设置新的table_id
	 *      7-2、保存Table_info对象
	 *      7-3、所有关联了原table_id的表，找到对应的字段，为这些字段设置新的table_id
	 *           7-3-1、更新table_storage_info表对应条目的table_id字段
	 *           7-3-2、更新table_clean表对应条目的table_id字段
	 *           7-3-3、更新column_merge表对应条目的table_id字段
	 * 8、不是新增采集表还是编辑采集表，都需要将该表要采集的列信息保存到相应的表里面
	 *
	 * @Param: tableInfo Table_info
	 *         含义：一个Table_info对象必须包含table_name,table_ch_name，
	 *         table_count,is_md5,如果是新增的采集表，table_id为空，如果是编辑修改采集表，table_id不能为空
	 *         用于过滤的sql页面没定义就是空，页面定义了就不为空
	 *         取值范围：不为空，Table_info类的实体类对象
	 * @Param: colSetId long
	 *         含义：数据库设置ID，databse_set表主键，table_info表外键
	 *         取值范围：不为空
	 * @Param: agentId long
	 *         含义：agentID,agent_info表主键
	 *         取值范围：不为空
	 * @Param: collColumn String
	 *         含义：该表要采集的
	 *         取值范围：不为空
	 *
	 * @return: long
	 *          含义：保存成功后返回database_id，用于下一个页面能够拿到上一个页面的信息
	 *          取值范围：不为空
	 * */
	public long saveCollSingleTbInfo(@RequestBean Table_info tableInfo, long colSetId, long agentId, String collColumn,
	                           String columnSort){
		//1、校验Table_info对象中的信息是否合法
		if(StringUtil.isBlank(tableInfo.getTable_name())){
			throw new BusinessException("保存采集表配置，表名不能为空!");
		}
		if(StringUtil.isBlank(tableInfo.getTable_ch_name())){
			throw new BusinessException("保存采集表配置，表中文名不能为空!");
		}
		if(StringUtil.isBlank(tableInfo.getTable_count())){
			throw new BusinessException("保存采集表配置，表记录数不能为空!");
		}
		if(StringUtil.isBlank(tableInfo.getIs_md5())){
			throw new BusinessException("保存采集表配置，是否计算MD5不能为空!");
		}
		if(IsFlag.ofEnumByCode(tableInfo.getIs_md5()) == null){
			throw new BusinessException("是否计算MD5配置值错误，系统无法识别");
		}
		//2、给Table_info对象设置基本信息(valid_s_date,valid_e_date,is_user_defined,is_register)
		tableInfo.setValid_s_date(DateUtil.getSysDate());
		tableInfo.setValid_e_date(Constant.MAXDATE);
		tableInfo.setIs_user_defined(IsFlag.Fou.getCode());
		//TODO 这一项是否需要设置待讨论，如果允许页面上配置，那么就不需要设置
		tableInfo.setIs_register(IsFlag.Shi.getCode());
		//3、不论新增采集表还是编辑采集表，页面上所有的内容都可能被修改，所以直接执行SQL，按database_id删除table_info表中,
		//所有非自定义采集SQL的数据，不关心删除数据的条数
		Dbo.execute(" DELETE FROM table_info WHERE database_id =? AND valid_e_date = ? AND is_user_defined = ? ",
				colSetId, Constant.MAXDATE, IsFlag.Fou.getCode());
		//4、获取Table_info对象的table_id属性，如果该属性没有值，说明这张采集表是新增的，否则这张采集表在当前采集任务中
		//已经存在，且有可能经过了修改
		//5、不论新增还是修改，构造默认的表清洗优先级(JSON格式的字符串，保存进入数据库)
		JSONObject tableCleanOrder = new JSONObject();
		tableCleanOrder.put(CleanType.ZiFuBuQi.getCode(), 1);
		tableCleanOrder.put(CleanType.ZiFuTiHuan.getCode(), 2);
		tableCleanOrder.put(CleanType.ShiJianZhuanHuan.getCode(), 3);
		tableCleanOrder.put(CleanType.MaZhiZhuanHuan.getCode(), 4);
		tableCleanOrder.put(CleanType.ZiFuHeBing.getCode(), 5);
		tableCleanOrder.put(CleanType.ZiFuChaiFen.getCode(), 6);
		tableCleanOrder.put(CleanType.ZiFuTrim.getCode(), 7);
		//6、如果是新增采集表
		if(tableInfo.getTable_id() == null){
			//6-1、生成table_id，并存入Table_info对象中
			tableInfo.setTable_id(PrimayKeyGener.getNextId());
			tableInfo.setTi_or(tableCleanOrder.toJSONString());
			//6-2、保存Table_info对象
			tableInfo.add(Dbo.db());
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
			List<Object> storageIdList = Dbo.queryOneColumnList(" select storage_id from table_storage_info where " +
					"table_id = ? ", oldID);
			for(Object obj : storageIdList){
				long storageId = (long)obj;
				Dbo.execute("update table_storage_info set table_id = ? where storage_id = ?", newID, storageId);
			}
			//7-3-2、更新table_clean表对应条目的table_id字段
			List<Object> tableCleanIdList = Dbo.queryOneColumnList(" select c_id from table_clean where " +
					" table_id = ? ", oldID);
			for(Object obj : tableCleanIdList){
				long tableCleanId = (long)obj;
				Dbo.execute("update table_clean set table_id = ? where c_id = ?", newID, tableCleanId);
			}
			//7-3-3、更新column_merge表对应条目的table_id字段
			List<Object> colMergeIdList = Dbo.queryOneColumnList(" select col_id from column_merge " +
					"where table_id = ? ", oldID);
			for(Object obj : colMergeIdList){
				long colMergeId = (long)obj;
				Dbo.execute("update column_merge set table_id = ? where col_id = ?", newID, colMergeId);
			}
		}
		//8、不是新增采集表还是编辑采集表，都需要将该表要采集的列信息保存到相应的表里面

		return colSetId;
	}

	/**
	 * 如果页面只有自定义SQL查询采集，保存该界面配置的所有信息
	 *
	 * 1、因为自定义表已经入库了，所以要在table_info表中删除不是自定义的表，删除的条数可能为0-N
	 *
	 *
	 * @Param: colSetId long
	 *         含义：数据库设置ID，databse_set表主键，table_info表外键
	 *         取值范围：不为空
	 *
	 * @return: long
	 *          含义：保存成功后返回database_id，用于下一个页面能够拿到上一个页面的信息
	 *          取值范围：不为空
	 * */
	public long saveCustomizeCollTbInfo(long colSetId){
		//1、因为自定义表已经入库了，所以要在table_info表中删除不是自定义的表，删除的条数可能为0-N
		Dbo.execute(" DELETE FROM table_info WHERE database_id =? AND valid_e_date = ? AND is_user_defined = ? ",
				colSetId, Constant.MAXDATE, IsFlag.Fou.getCode());
		return colSetId;
	}

	/**
	 * 根据colSetId去数据库中查出DB连接信息
	 *
	 * 1、根据colSetId和userId去数据库中查出DB连接信息
	 *
	 *
	 * @Param: colSetId long
	 *         含义：数据库设置ID，databse_set表主键，table_info表外键
	 *         取值范围：不为空
	 * @Param: userId long
	 *         含义：当前登录用户ID，sys_user表主键
	 *         取值范围：不为空
	 *
	 * @return: Result
	 *          含义：查询结果集
	 *          取值范围：不为空
	 * */
	private Result getDatabaseSetInfo(long colSetId, long userId){
		//1、根据colSetId和userId去数据库中查出DB连接信息
		return Dbo.queryResult(" select t1.*, t2.* from "+ Database_set.TableName +" t1" +
				" left join "+ Agent_info.TableName +" ai on ai.agent_id = t1.agent_id" +
				" left join "+ Collect_job_classify.TableName +" t2 on t1.classify_id = t2.classify_id" +
				" where t1.database_id = ? and ai.user_id = ? ", colSetId, userId);
	}

	/**
	 * 处理保存采集表信息时表中列信息的保存
	 *
	 * 1、
	 *
	 *
	 * @Param: colSetId long
	 *         含义：数据库设置ID，databse_set表主键，table_info表外键
	 *         取值范围：不为空
	 *
	 * */
	private void saveTableColumnInfo(){

	}
}
