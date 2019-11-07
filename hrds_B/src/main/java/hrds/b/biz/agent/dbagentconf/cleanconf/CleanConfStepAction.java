package hrds.b.biz.agent.dbagentconf.cleanconf;

import com.alibaba.fastjson.JSONArray;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DefaultPageImpl;
import fd.ng.db.resultset.Result;
import fd.ng.web.util.Dbo;
import hrds.b.biz.agent.bean.ColumnCleanParam;
import hrds.b.biz.agent.bean.TableCleanParam;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.CharSplitType;
import hrds.commons.codes.CleanType;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.Constant;
import hrds.commons.utils.DboExecute;
import hrds.commons.utils.key.PrimayKeyGener;

import java.util.List;

@DocClass(desc = "配置清洗规则", author = "WangZhengcheng")
public class CleanConfStepAction extends BaseAction{

	/*
	 * 从上一个页面跳转过来，拿到在配置数据清洗页面显示信息(agentStep2&agentStep2SDO)
	 * */
	@Method(desc = "根据数据库设置ID获得清洗规则配置页面初始信息", logicStep = "" +
			"1、根据colSetId在table_info表中获取上一个页面配置好的采集表id" +
			"2、如果没有查询到结果，返回空的Result" +
			"3、否则根据采集表ID在table_info表和table_clean表中查出页面所需的信息")
	@Param(name = "colSetId", desc = "数据库设置ID，源系统数据库设置表主键，数据库对应表外键", range = "不为空")
	@Param(name = "currPage", desc = "分页当前页", range = "大于0的正整数", nullable = true, valueIfNull = "1")
	@Param(name = "pageSize", desc = "分页查询每页显示条数", range = "大于0的正整数", nullable = true, valueIfNull = "10")
	@Return(desc = "查询结果集", range = "不为空，其中compflag/replaceflag/trimflag三个字段的值，" +
			"不为0表示该表做了相应的清洗设置，0表示没有做相应的设置")
	public Result getCleanConfInfo(long colSetId, int currPage, int pageSize){
		//1、根据colSetId在table_info表中获取上一个页面配置好的采集表id
		List<Object> tableIds = Dbo.queryOneColumnList("SELECT table_id FROM " + Table_info.TableName +
				" WHERE database_id = ? and is_user_defined = ?", colSetId, IsFlag.Fou.getCode());
		//2、如果没有查询到结果，返回空的Result
		if(tableIds.isEmpty()){
			return new Result();
		}
		//3、否则根据采集表ID在table_info表和table_clean表中查出页面所需的信息
		StringBuilder strSB = new StringBuilder("SELECT ti.table_id, ti.table_name, ti.table_ch_name, " +
				" sum(CASE tc.clean_type WHEN ? THEN 1 ELSE 0 END) as compflag, " +
				" sum(CASE tc.clean_type WHEN ? THEN 1 ELSE 0 END) as replaceflag, " +
				" sum(CASE tc.clean_type WHEN ? THEN 1 ELSE 0 END) as trimflag " +
				" FROM "+ Table_info.TableName +" ti LEFT JOIN "+ Table_clean.TableName +" tc " +
				" ON ti.table_id = tc.table_id " +
				" where ti.table_id in ( ");
		for(int i = 0; i < tableIds.size(); i++){
			strSB.append(tableIds.get(i));
			if (i != tableIds.size() - 1)
				strSB.append( ",");
		}
		strSB.append(" ) GROUP BY ti.table_id ");

		return Dbo.queryPagedResult(new DefaultPageImpl(currPage, pageSize),
				strSB.toString(), CleanType.ZiFuBuQi.getCode(), CleanType.ZiFuTiHuan.getCode(),
				CleanType.ZiFuTrim.getCode());
	}

	/*
	 * 配置数据清洗页面，字符补齐保存按钮，针对单个表(tableCleanSDO)
	 * */
	@Method(desc = "保存单表字符补齐规则", logicStep = "" +
			"1、校验入参合法性" +
			"2、在table_clean表中根据table_id删除该表原有的字符补齐设置，不关注删除数据的数目" +
			"3、设置主键" +
			"4、对补齐的特殊字符转为unicode码保存" +
			"5、执行保存")
	@Param(name = "charCompletion", desc = "待保存Table_clean实体类对象", range = "不为空,注意清洗方式的代码项" +
			"1：字符补齐" +
			"注意补齐方式：" +
			"1、前补齐" +
			"2、后补齐", isBean = true)
	public void saveSingleTbCompletionInfo(Table_clean charCompletion){
		//1、校验入参合法性，补齐字符应该不能为Null
		if(StringUtil.isEmpty(charCompletion.getCharacter_filling())){
			throw new BusinessException("保存整表字符补齐规则时，补齐字符不能为空");
		}
		if(charCompletion.getFilling_length() == null){
			throw new BusinessException("保存整表字符补齐规则时，补齐长度不能为空");
		}
		if(StringUtil.isBlank(charCompletion.getFilling_type())){
			throw new BusinessException("保存整表字符补齐规则时，必须选择补齐方式");
		}
		if(charCompletion.getTable_id() == null){
			throw new BusinessException("保存整表字符补齐规则是，必须关联表信息");
		}
		//2、在table_clean表中根据table_id删除该表原有的字符补齐设置，不关注删除数据的数目
		Dbo.execute("DELETE FROM "+ Table_clean.TableName +" WHERE table_id = ? AND clean_type = ?",
				charCompletion.getTable_id(), CleanType.ZiFuBuQi.getCode());
		//3、设置主键
		charCompletion.setTable_clean_id(PrimayKeyGener.getNextId());
		charCompletion.setClean_type(CleanType.ZiFuBuQi.getCode());
		//4、对补齐的特殊字符转为unicode码保存
		charCompletion.setCharacter_filling(StringUtil.string2Unicode(charCompletion.getCharacter_filling()));
		//5、执行保存
		charCompletion.add(Dbo.db());
	}

	/*
	 * 在列清洗设置中保存字符补齐(columnLenSDO)
	 * */
	@Method(desc = "保存一列的字符补齐规则", logicStep = "" +
			"1、校验入参合法性，补齐字符应该不能为Null" +
			"2、在column_clean表中根据column_id删除该表原有的字符补齐设置，不关注删除数据的数目" +
			"3、设置主键" +
			"4、对补齐的特殊字符转为unicode码保存" +
			"5、执行保存")
	@Param(name = "charCompletion", desc = "待保存Column_clean实体类对象", range = "不为空,注意清洗方式的代码项" +
			"1：字符补齐" +
			"注意补齐方式：" +
			"1、前补齐" +
			"2、后补齐", isBean = true)
	public void saveColCompletionInfo(Column_clean charCompletion){
		//1、校验入参合法性，补齐字符应该不能为Null
		if(StringUtil.isEmpty(charCompletion.getCharacter_filling())){
			throw new BusinessException("保存列字符补齐规则时，补齐字符不能为空");
		}
		if(charCompletion.getFilling_length() == null){
			throw new BusinessException("保存列字符补齐规则时，补齐长度不能为空");
		}
		if(StringUtil.isBlank(charCompletion.getFilling_type())){
			throw new BusinessException("保存列字符补齐规则时，必须选择补齐方式");
		}
		if(charCompletion.getColumn_id() == null){
			throw new BusinessException("保存列字符补齐规则是，必须关联列信息");
		}
		//2、在column_clean表中根据column_id删除该表原有的字符补齐设置，不关注删除数据的数目
		Dbo.execute("DELETE FROM "+ Column_clean.TableName +" WHERE column_id = ? AND clean_type = ?",
				charCompletion.getColumn_id(), CleanType.ZiFuBuQi.getCode());
		//3、设置主键
		charCompletion.setCol_clean_id(PrimayKeyGener.getNextId());
		charCompletion.setClean_type(CleanType.ZiFuBuQi.getCode());
		//4、对补齐的特殊字符转为unicode码保存
		charCompletion.setCharacter_filling(StringUtil.string2Unicode(charCompletion.getCharacter_filling()));
		//5、执行保存
		charCompletion.add(Dbo.db());
	}

	/*
	 * 列清洗页面，字符补齐列，点击设置，回显已经该字段已经设置好的字符补齐信息(colLengthSDO)
	 * */
	@Method(desc = "根据列ID获得列字符补齐信息", logicStep = "" +
			"1、根据columnId在column_clean中查询该表的字符补齐信息" +
			"2、如果查询到，则将补齐字符解码后返回前端" +
			"3、如果没有列字符补齐信息，则根据columnId查其所在表是否配置了整表字符补齐，如果查询到，" +
			"   则将补齐字符解码后返回前端" +
			"4、如果整表字符补齐信息也没有，返回空的Result")
	@Param(name = "columnId", desc = "列ID，表对应字段表主键，列清洗信息表外键", range = "不为空")
	@Return(desc = "查询结果集", range = "不为空")
	public Result getColCompletionInfo(long columnId){
		//1、根据columnId在column_clean中查询该表的字符补齐信息
		Result columnResult = Dbo.queryResult("select col_clean_id, filling_type, character_filling, " +
				" filling_length, column_id from "+ Column_clean.TableName + " where column_id = ? and clean_type = ?"
				, columnId, CleanType.ZiFuBuQi.getCode());
		//2、如果查询到，则将补齐字符解码后返回前端
		if(!columnResult.isEmpty()){
			columnResult.setObject(0, "character_filling", StringUtil.unicode2String(
					columnResult.getString(0, "character_filling")));
			return columnResult;
		}
		//3、如果没有列字符补齐信息，则根据columnId查其所在表是否配置了整表字符补齐，如果查询到，则将补齐字符解码后返回前端
		Result tableResult = Dbo.queryResult("SELECT tc.table_clean_id, tc.filling_type, tc.character_filling," +
				" tc.filling_length FROM "+ Table_clean.TableName +" tc" +
				" WHERE tc.table_id = (SELECT table_id FROM table_column WHERE column_id = ?)" +
				" AND tc.clean_type = ?", columnId, CleanType.ZiFuBuQi.getCode());
		//4、如果整表字符补齐信息也没有，返回空的Result
		if(tableResult.isEmpty()){
			return tableResult;
		}
		tableResult.setObject(0, "character_filling",
				StringUtil.unicode2String(tableResult.getString(0, "character_filling")));
		return tableResult;
	}

	/*
	 * 配置数据清洗页面，字符补齐列，点击设置，回显已经该表已经设置好的字符补齐信息(searchCharacterSDO)
	 * */
	@Method(desc = "根据表ID获取该表的字符补齐信息", logicStep = "" +
			"1、根据tableId去table_clean表中查询字符补齐规则" +
			"2、如果没有查询到数据，返回空的Result" +
			"3、如果查到了数据，将补齐字符解码之后返回前端")
	@Param(name = "tableId", desc = "数据库对应表主键，表对应字段表外键，表清洗参数表外键", range = "不为空")
	@Return(desc = "查询结果集", range = "不为空")
	public Result getTbCompletionInfo(long tableId){
		//1、根据tableId去table_clean表中查询字符补齐规则
		Result result = Dbo.queryResult("SELECT table_clean_id, filling_type, character_filling, " +
				"filling_length  FROM "+ Table_clean.TableName +" WHERE table_id = ? AND clean_type = ?"
				, tableId, CleanType.ZiFuBuQi.getCode());
		//2、如果没有查询到数据，返回空的Result
		if(result.isEmpty()){
			return result;
		}
		//3、如果查到了数据，将补齐字符解码之后返回前端
		result.setObject(0, "character_filling",
				StringUtil.unicode2String(result.getString(0, "character_filling")));
		return result;
	}

	/*
	 * 配置数据清洗页面，字符替换保存按钮，针对单个表(tableColCleanSDO)
	 * */
	@Method(desc = "保存单个表的字符替换规则", logicStep = "" +
			"1、使用table_id在table_clean表中删除之前对该表定义过的字符替换规则，不关心删除数目" +
			"2、遍历replaceList" +
			"2-1、为每一个Table_clean对象设置主键" +
			"2-2、原字符串和替换字符串转为Unicode码" +
			"2-3、保存")
	@Param(name = "replaceString", desc = "存放有待保存信息的JSON数组", range = "不为空")
	@Param(name = "tableId", desc = "数据库对应表主键，表清洗参数表外键", range = "不为空")
	public void saveSingleTbReplaceInfo(String replaceString, long tableId){
		List<Table_clean> replaceList = JSONArray.parseArray(replaceString, Table_clean.class);
		//1、使用tableId在table_clean表中删除之前对该表定义过的字符替换规则，不关心删除数目
		Dbo.execute("DELETE FROM "+ Table_clean.TableName +" WHERE table_id = ? AND clean_type = ?", tableId,
				CleanType.ZiFuTiHuan.getCode());
		//2、遍历replaceList
		for(Table_clean tableClean : replaceList){
			//2-1、为每一个Table_clean对象设置主键
			tableClean.setTable_clean_id(PrimayKeyGener.getNextId());
			tableClean.setClean_type(CleanType.ZiFuTiHuan.getCode());
			//2-2、原字符串和替换字符串转为Unicode码
			tableClean.setField(StringUtil.string2Unicode(tableClean.getField()));
			tableClean.setReplace_feild(StringUtil.string2Unicode(tableClean.getReplace_feild()));
			//2-3、保存
			tableClean.add(Dbo.db());
		}
	}

	/*
	 * 列清洗页面，字符替换保存按钮(columnCleanSDO)
	 * */
	@Method(desc = "保存单个字段的字符替换规则", logicStep = "" +
			"1、使用columnId在column_clean表中删除之前对该字段定义过的字符替换规则，不关心删除数目" +
			"2、遍历replaceList" +
			"2-1、为每一个Column_clean对象设置主键" +
			"2-2、原字符串和替换字符串转为Unicode码" +
			"2-3、保存")
	@Param(name = "replaceString", desc = "存放有待保存信息的JSON数组", range = "不为空")
	@Param(name = "columnId", desc = "表对应字段表主键，列清洗参数表外键", range = "不为空")
	public void saveColReplaceInfo(String replaceString, long columnId){
		List<Column_clean> replaceList = JSONArray.parseArray(replaceString, Column_clean.class);
		//1、使用columnId在column_clean表中删除之前对该字段定义过的字符替换规则，不关心删除数目
		Dbo.execute("DELETE FROM "+ Column_clean.TableName +" WHERE column_id = ? AND clean_type = ?", columnId,
				CleanType.ZiFuTiHuan.getCode());
		//2、遍历replaceList
		for(Column_clean columnClean : replaceList){
			//2-1、为每一个Column_clean对象设置主键
			columnClean.setCol_clean_id(PrimayKeyGener.getNextId());
			columnClean.setClean_type(CleanType.ZiFuTiHuan.getCode());
			//2-2、原字符串和替换字符串转为Unicode码
			columnClean.setField(StringUtil.string2Unicode(columnClean.getField()));
			columnClean.setReplace_feild(StringUtil.string2Unicode(columnClean.getReplace_feild()));
			//2-3、保存
			columnClean.add(Dbo.db());
		}
	}

	/*
	 * 配置数据清洗页面，点击设置，弹框回显针对该表的字符替换规则(replaceSDO)
	 * */
	@Method(desc = "根据表ID获取针对该表定义的字符替换规则", logicStep = "" +
			"1、根据tableId去table_clean表中查询该表的字符替换规则" +
			"2、如果没有查询到,直接空的Result" +
			"3、如果查询到了，对原字符串和替换字符串进行解码，然后返回")
	@Param(name = "tableId", desc = "数据库对应表主键，表清洗参数表外键", range = "不为空")
	@Return(desc = "查询结果集", range = "不为空")
	public Result getSingleTbReplaceInfo(long tableId){
		//1、根据tableId去table_clean表中查询该表的字符替换规则
		Result result = Dbo.queryResult("SELECT table_clean_id, field, replace_feild FROM "+
				Table_clean.TableName +" WHERE table_id = ? AND clean_type = ?", tableId,
				CleanType.ZiFuTiHuan.getCode());
		//2、如果没有查询到,直接空的Result
		if(result.isEmpty()){
			return result;
		}
		//3、如果查询到了，对原字符串和替换字符串进行解码，然后返回
		result.setObject(0, "field", StringUtil.unicode2String(result.getString(0,
				"field")));
		result.setObject(0, "replace_feild",
				StringUtil.unicode2String(result.getString(0, "replace_feild")));
		return result;
	}

	/*
	 * 列清洗页面，点击设置，弹框回显针对该列的字符替换规则(colChartSDO)
	 * */
	@Method(desc = "根据列ID获得列字符替换信息", logicStep = "" +
			"1、根据columnId在column_clean中查询该表的字符替换信息" +
			"2、如果查询到，则将替换信息解码后返回前端" +
			"3、如果没有列字符替换信息，则根据columnId查其所在表是否配置了整表字符替换，如果查询到，" +
			"   则将替换字符解码后返回前端" +
			"4、如果整表字符替换信息也没有，返回空的Result")
	@Param(name = "columnId", desc = "列ID，表对应字段表主键，列清洗信息表外键", range = "不为空")
	@Return(desc = "查询结果集", range = "不为空")
	public Result getColReplaceInfo(long columnId){
		//1、根据columnId在column_clean中查询该表的字符替换信息
		Result columnResult = Dbo.queryResult("select col_clean_id, field, replace_feild, column_id" +
				" from "+ Column_clean.TableName +" where column_id = ? and clean_type = ?", columnId,
				CleanType.ZiFuTiHuan.getCode());
		//2、如果查询到，则将源字符和替换字符解码后返回前端
		if(!columnResult.isEmpty()){
			columnResult.setObject(0, "field",
					StringUtil.unicode2String(columnResult.getString(0, "field")));
			columnResult.setObject(0, "replace_feild",
					StringUtil.unicode2String(columnResult.getString(0, "replace_feild")));
			return columnResult;
		}
		//3、如果没有列字符补齐信息，则根据columnId查其所在表是否配置了整表字符替换，如果查询到，则将补齐字符解码后返回前端
		Result tableResult = Dbo.queryResult("SELECT tc.table_clean_id, tc.field, tc.replace_feild " +
				" FROM "+ Table_clean.TableName +" tc" +
				" WHERE tc.table_id = (SELECT table_id FROM table_column WHERE column_id = ?" +
				" AND tc.clean_type = ?)", columnId, CleanType.ZiFuTiHuan.getCode());
		//4、如果整表字符替换信息也没有，返回空的Result
		if(tableResult.isEmpty()){
			return tableResult;
		}
		tableResult.setObject(0, "field", StringUtil.unicode2String(tableResult.getString(0,
				"field")));
		tableResult.setObject(0, "replace_feild",
				StringUtil.unicode2String(tableResult.getString(0, "replace_feild")));
		return tableResult;
	}

	/*
	 * 点击选择列按钮，查询列信息(columnSDO)
	 * */
	@Method(desc = "根据表ID获取该表所有的列清洗信息", logicStep = "" +
			"1、根据tableId去到table_column表中查询采集的列的列ID" +
			"2、如果没有找到采集列，直接返回一个空的集合" +
			"3、如果找到了，再进行关联查询，查询出页面需要显示的信息" +
			"4、返回")
	@Param(name = "tableId", desc = "数据库对应表主键，表清洗参数表外键", range = "不为空")
	@Param(name = "currPage", desc = "分页当前页", range = "大于0的正整数", nullable = true, valueIfNull = "1")
	@Param(name = "pageSize", desc = "分页查询每页显示条数", range = "大于0的正整数", nullable = true,
			valueIfNull = "10")
	@Return(desc = "查询结果集", range = "不为空，数据的条数视实际情况而定" +
			"注意compflag/replaceflag/formatflag/splitflag/codevalueflag/trimflag这六个字段的值" +
			"不为0表示该列做了相应的清洗设置，0表示没有列相应的设置")
	public Result getColumnInfo(long tableId, int currPage, int pageSize){
		//1、根据tableId去到table_column表中查询采集的,并且不是变化而生成的列ID
		List<Object> columnIds = Dbo.queryOneColumnList("select column_id from " + Table_column.TableName +
				" where table_id = ? and is_get = ? and is_new = ?", tableId, IsFlag.Shi.getCode(),
				IsFlag.Fou.getCode());
		//2、如果没有找到采集列，直接返回一个空的集合
		if(columnIds.isEmpty()){
			return new Result();
		}
		//3、如果找到了，再进行关联查询，查询出页面需要显示的信息
		StringBuilder sqlSB = new StringBuilder("SELECT t1.column_id,t1.colume_name,t1.colume_ch_name," +
				" t2.table_name," +
				" sum(case t3.clean_type when ? then 1 else 0 end) as compflag, " +
				" sum(case t3.clean_type when ? then 1 else 0 end) as replaceflag, " +
				" sum(case t3.clean_type when ? then 1 else 0 end ) as formatflag, " +
				" sum(case t3.clean_type when ? then 1 else 0 end) as splitflag, " +
				" sum(case t3.clean_type when ? then 1 else 0 end) as codevalueflag, " +
				" sum(case t3.clean_type when ? then 1 else 0 end) as trimflag " +
				" FROM "+ Table_column.TableName +" t1 JOIN "+ Table_info.TableName +
				" t2 ON t1.table_id = t2.table_id " +
				" left join "+ Column_clean.TableName +" t3 on t1.column_id = t3.column_id " +
				" WHERE t1.column_id in ( ");
		for(int i = 0; i < columnIds.size(); i++){
			sqlSB.append(columnIds.get(i));
			if (i != columnIds.size() - 1)
				sqlSB.append(",");
		}
		sqlSB.append(" ) GROUP BY t1.column_id, t2.table_name order by cast(t1.remark as integer) asc ");
		//4、返回
		return Dbo.queryPagedResult(new DefaultPageImpl(currPage, pageSize),
				sqlSB.toString(), CleanType.ZiFuBuQi.getCode(), CleanType.ZiFuTiHuan.getCode(),
				CleanType.ShiJianZhuanHuan.getCode(), CleanType.ZiFuChaiFen.getCode(),
				CleanType.MaZhiZhuanHuan.getCode(),CleanType.ZiFuTrim.getCode());
	}

	/*
	 * 保存所有表清洗设置字符补齐和字符替换(saveJobCleanSDO)
	 * */
	@Method(desc = "保存所有表清洗设置字符补齐和字符替换", logicStep = "" +
			"1、根据colSetId在清洗参数属性表中删除记录，不关心是否删除到相应的数据" +
			"2、如果配置了字符补齐" +
			"2-2、保存字符补齐信息" +
			"3、如果配置了字符替换" +
			"3-1、构建Clean_parameter对象，设置原字段，替换后字段" +
			"3-2、保存字符替换信息")
	@Param(name = "colSetId", desc = "数据库设置ID，源系统数据库设置表主键，清洗参数属性表外键", range = "不为空")
	@Param(name = "compFlag", desc = "是否设置字符补齐标识位", range = "1：是，0：否")
	@Param(name = "replaceFlag", desc = "是否设置字符替换标识位", range = "1：是，0：否")
	@Param(name = "compType", desc = "字符补齐类型", range = "1：前补齐，2：后补齐", nullable = true, valueIfNull = "0")
	@Param(name = "compChar", desc = "补齐字符", range = "如果要进行字符补齐，该参数不为空", nullable = true,
			valueIfNull = "")
	@Param(name = "compLen", desc = "补齐长度", range = "如果要进行字符补齐，该参数不为空", nullable = true,
			valueIfNull = "")
	@Param(name = "oriFieldArr", desc = "原字符", range = "如果要进行字符替换，该参数不为空", nullable = true,
			valueIfNull = "")
	@Param(name = "replaceFeildArr", desc = "替换后字符", range = "如果要进行字符替换，该参数不为空", nullable = true,
			valueIfNull = "")
	public void saveAllTbCleanConfigInfo(long colSetId, String compFlag, String replaceFlag, String compType,
	                                     String compChar, String compLen, String[] oriFieldArr,
	                                     String[] replaceFeildArr){
		//1、根据colSetId在清洗参数属性表中删除记录，不关心是否删除到相应的数据
		Dbo.execute("DELETE FROM clean_parameter WHERE database_id = ?", colSetId);
		//2、如果配置了字符补齐
		if(IsFlag.ofEnumByCode(compFlag) == IsFlag.Shi){
			//这里表示校验补齐方式，1代表前补齐，2代表后补齐，目前没有代码项
			if(Integer.parseInt(compType) != 1 && Integer.parseInt(compType) != 2){
				throw new BusinessException("字符补齐方式错误");
			}
			//2-1、构建Clean_parameter对象，设置主键，存储字符补齐信息，将补齐字符转为unicode编码
			Clean_parameter allTbClean = new Clean_parameter();
			allTbClean.setC_id(PrimayKeyGener.getNextId());
			allTbClean.setDatabase_id(colSetId);
			allTbClean.setClean_type(CleanType.ZiFuBuQi.getCode());
			allTbClean.setFilling_type(compType);
			allTbClean.setCharacter_filling(StringUtil.string2Unicode(compChar));
			allTbClean.setFilling_length(compLen);
			//2-2、保存字符补齐信息
			allTbClean.add(Dbo.db());
		}

		//3、如果配置了字符替换
		if(IsFlag.ofEnumByCode(replaceFlag) == IsFlag.Shi){
			if(oriFieldArr.length == 0 || replaceFeildArr.length == 0){
				throw new BusinessException("保存字符替换时，请填写原字符和替换后字符");
			}
			for(int i = 0; i < oriFieldArr.length; i++){
				//3-1、构建Clean_parameter对象，设置原字段，替换后字段
				Clean_parameter allTbClean = new Clean_parameter();
				allTbClean.setC_id(PrimayKeyGener.getNextId());
				allTbClean.setDatabase_id(colSetId);
				allTbClean.setClean_type(CleanType.ZiFuTiHuan.getCode());
				allTbClean.setField(StringUtil.string2Unicode(oriFieldArr[i]));
				allTbClean.setReplace_feild(StringUtil.string2Unicode(replaceFeildArr[i]));
				//3-2、保存字符替换信息
				allTbClean.add(Dbo.db());
			}
		}
	}

	/*
	 * 点击所有表清洗设置，回显所有表清洗设置字符补齐和字符替换规则(jobCleanSDO)
	 * */
	@Method(desc = "根据数据库设置ID查询所有表清洗设置字符替换规则", logicStep = "" +
			"1、根据colSetId在清洗参数属性表中获取字符替换规则" +
			"2、将原字符和替换后字符解码" +
			"3、返回")
	@Param(name = "colSetId", desc = "数据库设置ID，源系统数据库设置表主键，清洗参数属性表外键", range = "不为空")
	@Return(desc = "查询结果集", range = "不为空")
	public Result getAllTbCleanReplaceInfo(long colSetId){
		//1、根据colSetId在清洗参数属性表中获取字符替换规则
		Result replaceResult = Dbo.queryResult("SELECT c_id, field, replace_feild FROM "+
				Clean_parameter.TableName + " WHERE database_id = ? AND clean_type = ?", colSetId,
				CleanType.ZiFuTiHuan.getCode());

		if(!replaceResult.isEmpty()){
			//2、将原字符和替换后字符解码
			for(int i = 0; i < replaceResult.getRowCount(); i++){
				replaceResult.setObject(i, "field", StringUtil.unicode2String(
						replaceResult.getString(i, "field")));
				replaceResult.setObject(i, "replace_feild", StringUtil.unicode2String(
						replaceResult.getString(i, "replace_feild")));
			}
		}
		//3、返回
		return replaceResult;
	}

	@Method(desc = "根据数据库设置ID查询所有表清洗设置字符补齐规则", logicStep = "" +
			"1、根据colSetId在清洗参数属性表中获取字符补齐规则" +
			"2、将补齐字符解码" +
			"3、返回")
	@Param(name = "colSetId", desc = "数据库设置ID，源系统数据库设置表主键，清洗参数属性表外键", range = "不为空")
	@Return(desc = "查询结果集", range = "不为空")
	public Result getAllTbCleanCompInfo(long colSetId){
		//3、根据colSetId在清洗参数属性表中获取字符补齐规则
		Result compResult = Dbo.queryResult("SELECT c_id, filling_type, character_filling, filling_length " +
						" FROM "+ Clean_parameter.TableName +" WHERE database_id = ? AND clean_type = ?"
				, colSetId, CleanType.ZiFuBuQi.getCode());

		if(compResult.getRowCount() > 1){
			throw new BusinessException("对所有表设置的字符补齐规则不唯一");
		}

		if(!compResult.isEmpty()){
			//4、将补齐字符解码
			compResult.setObject(0, "character_filling",
					StringUtil.unicode2String(compResult.getString(0, "character_filling")));
		}
		return compResult;
	}

	/*
	 * 列清洗页面，点击日期格式化列，设置按钮，回显针对该列设置的日期格式化规则(lookColDateSDO)
	 * */
	@Method(desc = "根据列ID获取针对该列设置的日期格式化规则", logicStep = "" +
			"1、根据columnId在column_clean表中查询日期格式化规则并返回")
	@Param(name = "columnId", desc = "列ID，表对应字段表主键，列清洗信息表外键", range = "不为空")
	@Return(desc = "查询结果集", range = "不为空")
	public Result getDateFormatInfo(long columnId){
		//1、根据columnId在column_clean表中查询日期格式化规则并返回
		return Dbo.queryResult("select col_clean_id, old_format, convert_format FROM "+ Column_clean.TableName
				+ " WHERE column_id = ? AND clean_type = ?", columnId, CleanType.ShiJianZhuanHuan.getCode());
	}

	/*
	 * 列清洗页面，点击日期格式化列设置按钮，对该列配置日期格式化规则，保存按钮(saveColDateSDO)
	 * */
	@Method(desc = "保存列清洗日期格式化", logicStep = "" +
			"1、如果之前针对该列设置过日期格式化，要删除之前的设置" +
			"2、设置主键" +
			"3、保存")
	@Param(name = "dateFormat", desc = "待保存的Column_clean类对象", range = "不为空，注意清洗方式代码项：" +
			"3：时间转换", isBean = true)
	public void saveDateFormatInfo(Column_clean dateFormat){
		if(StringUtil.isBlank(dateFormat.getOld_format())){
			throw new BusinessException("请填写原日期格式");
		}
		if(StringUtil.isBlank(dateFormat.getConvert_format())){
			throw new BusinessException("请填写转换后日期格式");
		}
		//1、如果之前针对该列设置过日期格式化，要删除之前的设置
		Dbo.execute("DELETE FROM "+ Column_clean.TableName +" WHERE column_id = ? AND clean_type = ?"
				, dateFormat.getColumn_id(), CleanType.ShiJianZhuanHuan.getCode());
		//2、设置主键
		dateFormat.setCol_clean_id(PrimayKeyGener.getNextId());
		dateFormat.setClean_type(CleanType.ShiJianZhuanHuan.getCode());
		//3、保存
		dateFormat.add(Dbo.db());
	}

	/*
	 * 列清洗页面，点击列拆分列设置按钮，回显针对该列设置的列拆分信息(codeSplitLookSDO)
	 * */
	@Method(desc = "根据columnId查询列拆分信息", logicStep = "" +
			"1.使用columnId在column_split表中查询数据" +
			"2、如果没有查到，直接返回空的List" +
			"3、如果查到了，需要把拆分分隔符解码")
	@Param(name = "columnId", desc = "列ID，表对应字段表主键，列拆分表外键", range = "不为空")
	@Return(desc = "查询结果集", range = "不为空")
	public Result getColSplitInfo(long columnId){
		//1.使用columnId在column_split表中查询数据
		Result result = Dbo.queryResult("select * from " +
				Column_split.TableName + " WHERE column_id = ?", columnId);
		//2、如果没有查到，直接返回空的List
		if(result.isEmpty()){
			return result;
		}
		//3、如果查到了，需要把拆分分隔符解码
		for(int i = 0; i < result.getRowCount(); i++){
			result.setObject(i, "split_sep", StringUtil.unicode2String(result.
					getString(i, "split_sep")));
		}
		return result;
	}

	/*
	 * 列清洗页面，点击列拆分列设置按钮，列拆分弹框操作栏，删除按钮(deletesplitSDO)
	 * */
	@Method(desc = "删除一条列拆分规则", logicStep = "" +
			"1、在table_column表中找到拆分生成的新列，并删除,应该删除一条数据" +
			"2、column_split表中根据colSplitId找到数据并删除，应该只有一条数据被删除" +
			"3、如果该列在列拆分表中已经没有数据，则在column_clean表中根据colCleanId删除类型为列拆分的数据，" +
			"如果删除，应该删除一条数据")
	@Param(name = "colSplitId", desc = "列拆分信息表主键", range = "不为空")
	@Param(name = "colCleanId", desc = "列清洗参数信息表主键", range = "不为空")
	public void deleteColSplitInfo(long colSplitId, long colCleanId){
		//1、在table_column表中找到拆分生成的新列，并删除,应该删除一条数据
		DboExecute.deletesOrThrow("列拆分规则删除失败", "delete from "+ Table_column.TableName  +
				" where colume_name = (select t1.colume_name from table_column t1 " +
				" JOIN "+ Column_split.TableName +" t2 ON t1.colume_name = t2.col_name " +
				" JOIN "+ Column_clean.TableName +" t3 ON t2.col_clean_id = t3.col_clean_id " +
				" WHERE t2.col_clean_id = ? and  t2.col_split_id = ? and t1.is_new = ?)",
				colCleanId, colSplitId, IsFlag.Shi.getCode());
		//2、column_split表中根据colSplitId找到数据并删除，应该只有一条数据被删除
		DboExecute.deletesOrThrow("列拆分规则删除失败",
				"delete from "+ Column_split.TableName +" where col_split_id = ?", colSplitId);
		//3、如果该列在列拆分表中已经没有数据，则在column_clean表中根据colCleanId删除类型为列拆分的数据，如果删除，应该删除一条数据
		long splitCount = Dbo.queryNumber("select count(1) from column_split where col_clean_id = ?",
				colCleanId).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
		if(splitCount == 0){
			DboExecute.deletesOrThrow("列拆分规则删除失败", "delete from "+ Column_clean.TableName +
					" where col_clean_id = ? and clean_type = ?", colCleanId, CleanType.ZiFuChaiFen.getCode());
		}
	}

	/*
	 * 列清洗页面，点击列拆分列设置按钮，列拆分弹框操作栏，保存按钮(codeSplitCleanSDO)
	 * */
	@Method(desc = "保存列拆分规则", logicStep = "" +
			"1、首先，在column_clean表中，保存该列的列清洗信息" +
			"2、如果之前这个字段做过列拆分，需要在table_column表中找到拆分生成的新列，并删除,不关心删除的数目" +
			"3、如果这个字段之前做过列拆分，需要在column_split表中根据column_id找到数据并删除，不关心数目" +
			"4、为Column_split实体类对象中必须有值的属性设置值" +
			"5、保存Column_split实体类对象" +
			"6、将本次拆分生成的新列保存到table_column表中")
	@Param(name = "columnClean", desc = "待保存的列清洗信息", range = "Column_clean实体类对象，不为空" +
			"注意清洗方式：" +
			"字符拆分(6)", isBean = true)
	@Param(name = "columnSplitString", desc = "待保存的列拆分信息", range = "json字符串，不为空，注意拆分方式：" +
			"偏移量(1)" +
			"自定符号(2)")
	@Param(name = "tableId", desc = "数据库对应表主键，表清洗参数表外键", range = "不为空")
	public void saveColSplitInfo(Column_clean columnClean, String columnSplitString, long tableId){
		//1、首先，在column_clean表中，保存该列的列清洗信息
		if(columnClean.getCol_clean_id() != null){
			//id有值，表示修改对该列设置的列拆分
			columnClean.setClean_type(CleanType.ZiFuChaiFen.getCode());
			columnClean.update(Dbo.db());

			//2、如果之前这个字段做过列拆分，需要在table_column表中找到拆分生成的新列，并删除,不关心删除的数目
			Dbo.execute("delete from "+ Table_column.TableName +" where colume_name in " +
					" (select t1.colume_name from table_column t1 " +
					" JOIN "+ Column_split.TableName +" t2 ON t1.colume_name = t2.col_name " +
					" JOIN "+ Column_clean.TableName +" t3 ON t2.col_clean_id = t3.col_clean_id " +
					" WHERE t2.col_clean_id = ? and t2.column_id = ? and t1.table_id = ? and t1.is_new = ?)",
					columnClean.getCol_clean_id(), columnClean.getColumn_id(), tableId, IsFlag.Shi.getCode());

			//3、如果这个字段之前做过列拆分，需要在column_split表中根据column_id找到该列并删除，不关心数目
			Dbo.execute("delete from "+ Column_split.TableName +" where column_id = ?",
					columnClean.getColumn_id());
		}else{
			//id没有值，表示新增
			columnClean.setCol_clean_id(PrimayKeyGener.getNextId());
			columnClean.setClean_type(CleanType.ZiFuChaiFen.getCode());
			columnClean.add(Dbo.db());
		}
		List<Column_split> columnSplits = JSONArray.parseArray(columnSplitString, Column_split.class);
		for(Column_split columnSplit : columnSplits){
			//4、为Column_split实体类对象中必须有值的属性设置值
			columnSplit.setCol_split_id(PrimayKeyGener.getNextId());
			columnSplit.setColumn_id(columnClean.getColumn_id());
			columnSplit.setCol_clean_id(columnClean.getCol_clean_id());
			columnSplit.setValid_s_date(DateUtil.getSysDate());
			columnSplit.setValid_e_date(Constant.MAXDATE);

			if(CharSplitType.ofEnumByCode(columnSplit.getSplit_type()) == CharSplitType.ZhiDingFuHao){
				columnSplit.setSplit_sep(StringUtil.string2Unicode(columnSplit.getSplit_sep()));
			}
			//5、保存Column_split实体类对象
			columnSplit.add(Dbo.db());
			//6、将本次拆分生成的新列保存到table_column表中
			Table_column tableColumn = new Table_column();
			tableColumn.setTable_id(tableId);
			tableColumn.setIs_new(IsFlag.Shi.getCode());
			tableColumn.setColumn_id(PrimayKeyGener.getNextId());
			tableColumn.setIs_primary_key(IsFlag.Fou.getCode());
			tableColumn.setColume_name(columnSplit.getCol_name());
			tableColumn.setColumn_type(columnSplit.getCol_type());
			tableColumn.setColume_ch_name(columnSplit.getCol_zhname());
			tableColumn.setValid_s_date(DateUtil.getSysDate());
			tableColumn.setValid_e_date(Constant.MAXDATE);

			tableColumn.add(Dbo.db());
		}
	}

	/*
	 * 列清洗页面，点击码值转换列设置按钮，回显针对该列设置的码值转换信息(sysCodeSDO)
	 * TODO column_clean表中只能找到码值系统和码值名称，页面上需要展示的信息还不知道去哪里查
	public Result getCVConversionInfo(long columnId){
		return null;
	}
	* */

	/*
	 * 列清洗页面，点击码值转换列设置按钮，码值转换弹框确定按钮(codeValueChangeCleanSDO)
	 * TODO 页面上的码值信息不知道往哪里存
	public void saveCVConversionInfo(){

	}
	* */

	/*
	 * 列清洗页面，点击列合并按钮，回显之前对该表设置的列合并信息(codeMergeLookSDO)
	 * */
	@Method(desc = "根据表ID查询针对该表设置的列合并信息", logicStep = "" +
			"1、去column_merge表中按照table_id查询出数据直接返回")
	@Param(name = "tableId", desc = "数据库对应表主键，列合并表外键", range = "不为空")
	@Return(desc = "查询结果集", range = "Column_merge实体类对象，不为空")
	public Result getColMergeInfo(long tableId){
		//1、去column_merge表中按照table_id查询出数据直接返回
		return Dbo.queryResult("select * from "+ Column_merge.TableName +
				" where table_id = ?", tableId);
	}

	/*
	 * 列清洗页面，点击列合并按钮，弹出列合并弹框，保存列合并信息(codeMergeCleanSDO)
	 * */
	@Method(desc = "保存列合并信息", logicStep = "" +
			"1、在table_column表中找到因配置过列合并而生成的列并删除，不关注删除的数目" +
			"2、在column_merge表中，按照table_id删除该表配置的所有列合并信息" +
			"3、为Column_merge实体类对象属性中设置必填的值" +
			"4、保存Column_merge实体类对象" +
			"5、将合并出来的列保存到table_column表中")
	@Param(name = "columnMergeString", desc = "待保存的列合并信息", range = "不为空，json格式字符串")
	@Param(name = "tableId", desc = "数据库对应表主键，列合并表外键", range = "不为空")
	public void saveColMergeInfo(String columnMergeString, long tableId){
		//1、在table_column表中找到因配置过列合并而生成的列并删除，不关注删除的数目
		Dbo.execute("delete from "+ Table_column.TableName +" where colume_name in " +
				" (select t1.colume_name from "+ Table_column.TableName +" t1 " +
				" JOIN "+ Column_merge.TableName +" t2 ON t1.table_id=t2.table_id " +
				" and t1.colume_name = t2.col_name " +
				" where t2.table_id = ? and t1.is_new = ? )", tableId, IsFlag.Shi.getCode());
		//2、在column_merge表中，按照table_id删除该表配置的所有列合并信息
		Dbo.execute("delete from "+ Column_merge.TableName +" where table_id = ?", tableId);
		//3、为Column_merge实体类对象属性中设置必填的值
		List<Column_merge> columnMerges = JSONArray.parseArray(columnMergeString, Column_merge.class);
		for(Column_merge columnMerge : columnMerges){
			//4、保存Column_merge实体类对象
			columnMerge.setTable_id(tableId);
			columnMerge.setCol_merge_id(PrimayKeyGener.getNextId());
			columnMerge.setValid_s_date(DateUtil.getSysDate());
			columnMerge.setValid_e_date(Constant.MAXDATE);

			columnMerge.add(Dbo.db());

			//5、将合并出来的列保存到table_column表中
			Table_column tableColumn = new Table_column();
			tableColumn.setTable_id(tableId);
			tableColumn.setIs_new(IsFlag.Shi.getCode());
			tableColumn.setColumn_id(PrimayKeyGener.getNextId());
			tableColumn.setIs_primary_key(IsFlag.Fou.getCode());
			tableColumn.setColume_name(columnMerge.getCol_name());
			tableColumn.setColumn_type(columnMerge.getCol_type());
			tableColumn.setColume_ch_name(columnMerge.getCol_zhname());
			tableColumn.setValid_s_date(DateUtil.getSysDate());
			tableColumn.setValid_e_date(Constant.MAXDATE);

			tableColumn.add(Dbo.db());
		}
	}

	/*
	 * 列清洗页面，点击列合并按钮，弹出列合并弹框，操作栏删除按钮，删除列合并信息(deletemergeSDO)
	 * */
	@Method(desc = "删除一条列合并信息", logicStep = "" +
			"1、在table_column表中删除因合并生成的新列，删除的应该有且只有一条" +
			"2、在column_merge表中按ID删除一条列合并信息")
	@Param(name = "colMergeId", desc = "列合并信息表主键", range = "不为空")
	public void deleteColMergeInfo(long colMergeId){
		//1、在table_column表中删除因合并生成的新列，删除的应该有且只有一条
		DboExecute.deletesOrThrow("删除列合并失败", "delete from "+ Table_column.TableName +
				" where colume_name = " +
				" (select t1.colume_name " +
				" from "+ Table_column.TableName +" t1 " +
				" JOIN "+ Column_merge.TableName +" t2 ON t1.table_id = t2.table_id " +
				" and t1.colume_name = t2.col_name " +
				" where t2.col_merge_id = ?)", colMergeId);
		//2、在column_merge表中按ID删除一条列合并信息
		DboExecute.deletesOrThrow("删除列合并失败", "delete from "+ Column_merge.TableName +
						" where col_merge_id = ?", colMergeId);
	}

	/*
	 * 全表清洗优先级保存按钮(saveClearSortSDO)，针对本次采集任务的所有表保存清洗优先级
	 * */
	@Method(desc = "保存所有表清洗优先级", logicStep = "" +
			"1、根据table_id,在table_info表中找到对应的记录，将sort更新进去")
	@Param(name = "tableIds", desc = "存放本次采集任务中所有表ID的数组", range = "不为空")
	@Param(name = "sort", desc = "所有表清洗优先级，JSON格式", range = "不为空，" +
			"如：{\"complement\":1,\"replacement\":2,\"formatting\":3,\"conversion\":4,\"merge\":5,\"split\":6,\"trim\":7}" +
			"注意：json的key请务必按照示例中给出的写")
	public void saveAllTbCleanOrder(long[] tableIds, String sort){
		//1、根据table_id,在table_info表中找到对应的记录，将sort更新进去
		StringBuilder sqlSB = new StringBuilder("update " + Table_info.TableName +
				" set ti_or = ? where table_id in ( ");
		for(int i = 0; i < tableIds.length; i++){
			sqlSB.append(tableIds[i]);
			if (i != tableIds.length - 1)
				sqlSB.append(",");
		}
		sqlSB.append(" )");
		DboExecute.updatesOrThrow(tableIds.length, "保存全表清洗优先级失败", sqlSB.toString(), sort);
	}

	/*
	 * 列清洗页面，整表优先级设置，对单个表的所有字段设置清洗优先级
	 * */
	@Method(desc = "保存整表清洗优先级", logicStep = "" +
			"1、根据table_id,在table_info表中找到对应的表，将sort更新进去")
	@Param(name = "tableId", desc = "数据库对应表主键", range = "不为空")
	@Param(name = "sort", desc = "所有表清洗优先级，JSON格式", range = "不为空，" +
			"如：{\"complement\":1,\"replacement\":2,\"formatting\":3,\"conversion\":4,\"merge\":5,\"split\":6,\"trim\":7}" +
			"注意：json的key请务必按照示例中给出的写")
	public void saveSingleTbCleanOrder(long tableId, String sort){
		//1、根据table_id,在table_info表中找到对应的表，将sort更新进去
		DboExecute.updatesOrThrow("保存整表清洗优先级失败",
				"update "+ Table_info.TableName +" set ti_or = ? where table_id = ?", sort, tableId);
	}

	/*
	 * 列清洗页面，优先级调整设置，对单个字段设置清洗优先级
	 * */
	@Method(desc = "保存单个字段清洗优先级", logicStep = "" +
			"1、根据columnId,在table_column表中找到对应的字段，将清洗顺序设置进去")
	@Param(name = "columnId", desc = "表对应字段表主键", range = "不为空")
	@Param(name = "sort", desc = "字段清洗优先级，JSON格式", range = "不为空，" +
			"如：{\"complement\":1,\"replacement\":2,\"formatting\":3,\"conversion\":4,\"merge\":5,\"split\":6,\"trim\":7}" +
			"注意：json的key请务必按照示例中给出的来命名")
	public void saveColCleanOrder(long columnId, String sort){
		//1、根据columnId,在table_column表中找到对应的字段，将清洗顺序设置进去
		DboExecute.updatesOrThrow("保存列清洗优先级失败",
				"update "+ Table_column.TableName +" set tc_or = ? where column_id = ?", sort, columnId);
	}

	/*
	 * 列清洗页面，点击保存，由于字符补齐、字符替换、日期格式化、列拆分、码值转换都已经保存入库了，所以这里处理的逻辑只保存列首尾去空
	 * 但是必须将页面上每个字段是否补齐，是否替换，是否码值，是否日期也都传过来，如果用户配置了，但是有取消了勾选，要在这个方法里面做处理
	 * */
	@Method(desc = "保存列清洗信息", logicStep = "" +
			"1、将colCleanString反序列化为List<ColumnCleanParam>" +
			"2、遍历List集合" +
			"2-1、判断最终保存时，是否选择了字符补齐，否，则根据columnId去column_clean表中删除一条记录" +
			"2-2、判断最终保存时，是否选择了字符替换，否，则根据columnId去column_clean表中删除一条记录" +
			"2-3、判断最终保存时，是否选择了日期格式化，否，则根据columnId去column_clean表中删除一条记录" +
			"2-4、判断最终保存时，是否选择了码值转换，否，则进行删除当前列码值转换的处理，目前没搞清楚码值转换的保存逻辑，所以这个处理暂时没有" +
			"2-5、判断最终保存时，是否选择了列拆分，否，则进行删除列拆分的操作" +
			"2-6、判断最终保存时，是否选择了列首尾去空，进行首尾去空的保存处理")
	@Param(name = "colCleanString", desc = "所有列的列清洗参数信息,JSON格式", range = "不为空，" +
			"如：[{\"columnId\":1001,\"complementFlag\":true,\"replaceFlag\":true,\"formatFlag\":true,\"conversionFlag\":4,\"spiltFlag\":false,\"trimFlag\":true}," +
			"{\"columnId\":1002,\"complementFlag\":true,\"replaceFlag\":true,\"formatFlag\":true,\"conversionFlag\":4,\"spiltFlag\":false,\"trimFlag\":true}]" +
			"注意：请务必按照示例来命名")
	public void saveColCleanConfig(String colCleanString){
		//1、将colCleanString反序列化为List<ColumnCleanParam>
		List<ColumnCleanParam> columnCleanParams = JSONArray.parseArray(colCleanString, ColumnCleanParam.class);
		//2、遍历List集合
		for(ColumnCleanParam param : columnCleanParams){
			//2-1、判断最终保存时，是否选择了字符补齐，否，则根据columnId去column_clean表中尝试删除记录，不关心具体的数目
			if(!param.isComplementFlag()){
				Dbo.execute("DELETE FROM "+ Column_clean.TableName +" WHERE column_id = ? AND clean_type = ?"
						, param.getColumnId(), CleanType.ZiFuBuQi.getCode());
			}
			//2-2、判断最终保存时，是否选择了字符替换，否，则根据columnId去column_clean表中尝试删除记录，不关心具体的数目
			if(!param.isReplaceFlag()){
				Dbo.execute("DELETE FROM "+ Column_clean.TableName +" WHERE column_id = ? AND clean_type = ?"
						, param.getColumnId(), CleanType.ZiFuTiHuan.getCode());
			}
			//2-3、判断最终保存时，是否选择了日期格式化，否，则根据columnId去column_clean表中尝试删除记录，不关心具体的数目
			if(!param.isFormatFlag()){
				Dbo.execute( "DELETE FROM " + Column_clean.TableName +" WHERE column_id = ? AND clean_type = ?"
						, param.getColumnId(), CleanType.ShiJianZhuanHuan.getCode());
			}
			//TODO 2-4、判断最终保存时，是否选择了码值转换，否，则进行删除当前列码值转换的处理，目前没搞清楚码值转换的保存逻辑，所以这个处理暂时没有

			//2-5、判断最终保存时，是否选择了列拆分，否，则进行删除列拆分的操作
			if(!param.isSpiltFlag()){
				Result colSplitInfo = getColSplitInfo(param.getColumnId());
				if(!colSplitInfo.isEmpty()){
					for(int i = 0; i < colSplitInfo.getRowCount(); i++){
						deleteColSplitInfo(colSplitInfo.getLong(i, "col_split_id"),
								colSplitInfo.getLong(i, "col_clean_id"));
					}
				}
			}
			//2-6、判断最终保存时，是否选择了列首尾去空，进行首尾去空的保存处理
			if(param.isTrimFlag()){
				Dbo.execute("delete from "+ Column_clean.TableName +" where column_id = ? and clean_type = ?",
						param.getColumnId(), CleanType.ZiFuTrim.getCode());
				Column_clean trim = new Column_clean();
				trim.setCol_clean_id(PrimayKeyGener.getNextId());
				trim.setClean_type(CleanType.ZiFuTrim.getCode());
				trim.setColumn_id(param.getColumnId());

				trim.add(Dbo.db());
			}
		}
	}

	/*
	 * 点击下一步按钮，保存该页面所有信息(其实经过上面所有方法的处理后，配置数据清洗保存的只有首尾去空这一项信息了)，
	 * 但是必须将页面上是否整表补齐，是否整表替换信息也传过来，如果用户配置了，但是又取消了勾选，要在这个方法里面做处理
	 * */
	@Method(desc = "保存配置数据清洗页面信息", logicStep = "" +
			"1、将tbCleanString反序列化为List<TableCleanParam>" +
			"2、遍历List集合" +
			"2-1、判断最终保存时，是否选择了字符补齐，否，则根据tableId去table_clean表中删除一条记录" +
			"2-2、判断最终保存时，是否选择了字符替换，否，则根据tableId去table_clean表中删除一条记录" +
			"2-3、判断最终保存时，是否选择了列首尾去空，进行首尾去空的保存处理")
	@Param(name = "colSetId", desc = "数据库设置ID，源系统数据库设置表主键，数据库对应表外键", range = "不为空")
	@Param(name = "tbCleanString", desc = "所有表的清洗参数信息,JSON格式", range = "不为空，" +
			"如：[{\"tableId\":1001,\"tableName\":\"table_info\",\"complementFlag\":true,\"replaceFlag\":true,trimFlag:true},{\"tableId\":1002,\"tableName\":\"table_column\",\"complementFlag\":true,\"replaceFlag\":true,trimFlag:true}]" +
			"注意：请务必按照示例中给出的方式命名")
	@Return(desc = "数据库设置ID", range = "便于下一个页面通过传递这个值，查询到之前设置的信息")
	public long saveDataCleanConfig(long colSetId, String tbCleanString){
		//1、将tbCleanString反序列化为List<TableCleanParam>
		List<TableCleanParam> tableCleanParams = JSONArray.parseArray(tbCleanString, TableCleanParam.class);
		//2、遍历List集合
		for(TableCleanParam param : tableCleanParams){
			//2-1、判断最终保存时，是否选择了字符补齐，否，则根据tableId去table_clean表中尝试删除记录，不关心删除的数目
			if(!param.isComplementFlag()){
				Dbo.execute("DELETE FROM "+ Table_clean.TableName +" WHERE table_id = ? AND clean_type = ?"
						, param.getTableId(), CleanType.ZiFuBuQi.getCode());
			}
			//2-2、判断最终保存时，是否选择了字符替换，否，则根据tableId去table_clean表中尝试删除记录，不关心删除的数目
			if(!param.isReplaceFlag()){
				Dbo.execute("DELETE FROM "+ Table_clean.TableName +" WHERE table_id = ? AND clean_type = ?"
						, param.getTableId(), CleanType.ZiFuTiHuan.getCode());
			}
			//2-3、判断最终保存时，是否选择了列首尾去空，进行首尾去空的保存处理
			if(param.isTrimFlag()){
				Dbo.execute("delete from "+ Table_clean.TableName +" where table_id = ? and clean_type = ?",
						param.getTableId(), CleanType.ZiFuTrim.getCode());
				Table_clean trim = new Table_clean();
				trim.setTable_clean_id(PrimayKeyGener.getNextId());
				trim.setClean_type(CleanType.ZiFuTrim.getCode());
				trim.setTable_id(param.getTableId());

				trim.add(Dbo.db());
			}
		}
		return colSetId;
	}
}
