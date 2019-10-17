package hrds.b.biz.agent.dbagentconf.cleanconf;

import com.alibaba.fastjson.JSONArray;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.resultset.Result;
import fd.ng.web.util.Dbo;
import hrds.commons.codes.CleanType;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.*;
import hrds.commons.utils.key.PrimayKeyGener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@DocClass(desc = "保存清洗规则", author = "WangZhengcheng")
public class CleanConfStepAction {

	/*
	 * 从上一个页面跳转过来，拿到在配置数据清洗页面显示信息(agentStep2&agentStep2SDO)
	 * */
	@Method(desc = "根据数据库设置ID获得清洗规则配置页面初始信息", logicStep = "" +
			"1、根据colSetId在table_info表中获取上一个页面配置好的采集表id" +
			"2、如果没有查询到结果，返回空的Result" +
			"3、否则根据采集表ID在table_info表和table_clean表中查出页面所需的信息")
	@Param(name = "colSetId", desc = "数据库设置ID", range = "源系统数据库设置表主键，数据库对应表外键")
	@Return(desc = "查询结果集", range = "不为空，其中compflag/replaceflag/trimflag三个字段的值，" +
			"yes表示该表做了相应的清洗设置，no表示没有做相应的设置")
	public List<Result> getInitInfo(long colSetId){
		List<Object> tableIds = Dbo.queryOneColumnList("SELECT table_id FROM " + Table_info.TableName +
				" WHERE database_id = ?", colSetId);
		if(tableIds.isEmpty()){
			return Collections.emptyList();
		}
		List<Result> resultList = new ArrayList<>();
		for(Object tableId : tableIds){
			Result result = Dbo.queryResult("select ti.table_id, ti.table_name, ti.table_ch_name," +
					"(case tc.clean_type when ? then 'yes' else 'no' end) as compflag," +
					"(case tc.clean_type when ? then 'yes' else 'no' end) as replaceflag," +
					"(case tc.clean_type when ? then 'yes' else 'no' end) as trimflag" +
					"from table_info ti " +
					"left join table_clean tc on ti.table_id = tc.table_id" +
					"where ti.table_id = ?" +
					"order by ti.table_name",
					CleanType.ZiFuBuQi.getCode(), CleanType.ZiFuTiHuan.getCode(), CleanType.ZiFuTrim.getCode(),
					(long) tableId);

			resultList.add(result);
		}
		return resultList;
	}

	/*
	 * 配置数据清洗页面，字符补齐保存按钮，针对单个表(tableCleanSDO)
	 * */
	@Method(desc = "保存单表字符补齐规则", logicStep = "" +
			"1、在table_clean表中根据table_id删除该表原有的字符补齐设置，不关注删除数据的数目" +
			"2、设置主键" +
			"3、对补齐的特殊字符转为unicode码保存" +
			"4、执行保存")
	@Param(name = "tableClean", desc = "待保存Table_clean实体类对象", range = "不为空,注意清洗方式的代码项" +
			"1：字符补齐" +
			"2：字符替换" +
			"3：日期格式化" +
			"4：码值转换" +
			"5：列合并" +
			"6：列拆分" +
			"7：首尾去空", isBean = true)
	public void saveSingleTbCompletionInfo(Table_clean tableClean){
		//1、在table_clean表中根据table_id删除该表原有的字符补齐设置，不关注删除数据的数目
		Dbo.execute("DELETE FROM table_clean WHERE table_id = ? AND clean_type = ?", tableClean.getTable_id(),
				tableClean.getClean_type());
		//2、设置主键
		tableClean.setTable_clean_id(PrimayKeyGener.getNextId());
		tableClean.setClean_type(CleanType.ZiFuBuQi.getCode());
		//3、对补齐的特殊字符转为unicode码保存
		tableClean.setCharacter_filling(StringUtil.string2Unicode(tableClean.getCharacter_filling()));
		//4、执行保存
		tableClean.add(Dbo.db());
	}

	/*
	 * 在列清洗设置中保存字符补齐(columnLenSDO)
	 * */
	@Method(desc = "保存一列的字符补齐规则", logicStep = "" +
			"1、在column_clean表中根据column_id删除该表原有的字符补齐设置，不关注删除数据的数目" +
			"2、设置主键" +
			"3、对补齐的特殊字符转为unicode码保存" +
			"4、执行保存")
	@Param(name = "columnClean", desc = "待保存Column_clean实体类对象", range = "不为空,注意清洗方式的代码项" +
			"1：字符补齐" +
			"2：字符替换" +
			"3：日期格式化" +
			"4：码值转换" +
			"5：列合并" +
			"6：列拆分" +
			"7：首尾去空", isBean = true)
	public void saveColCompletionInfo(Column_clean columnClean){
		//1、在column_clean表中根据column_id删除该表原有的字符补齐设置，不关注删除数据的数目
		Dbo.execute("DELETE FROM column_clean WHERE column_id = ? AND clean_type = ?", columnClean.getColumn_id(),
				columnClean.getClean_type());
		//2、设置主键
		columnClean.setCol_clean_id(PrimayKeyGener.getNextId());
		columnClean.setClean_type(CleanType.ZiFuBuQi.getCode());
		//3、对补齐的特殊字符转为unicode码保存
		columnClean.setCharacter_filling(StringUtil.string2Unicode(columnClean.getCharacter_filling()));
		//4、执行保存
		columnClean.add(Dbo.db());
	}

	/*
	 * 列清洗页面，字符补齐列，点击设置，回显已经该字段已经设置好的字符补齐信息(colLengthSDO)
	 * */
	@Method(desc = "根据列ID获得列字符补齐信息", logicStep = "" +
			"1、根据columnId在column_clean中查询该表的字符补齐信息" +
			"2、如果查询到，则将补齐字符解码后返回前端" +
			"3、如果没有列字符补齐信息，则根据columnId查其所在表是否配置了整表字符补齐，如果查询到，则将补齐字符解码后返回前端" +
			"4、如果整表字符补齐信息也没有，返回空的Result")
	@Param(name = "columnId", desc = "列ID，表对应字段表主键，列清洗信息表外键", range = "不为空")
	@Return(desc = "查询结果集", range = "不为空")
	public Result getColCompletionInfo(long columnId){
		//1、根据columnId在column_clean中查询该表的字符补齐信息
		Result columnResult = Dbo.queryResult("select col_clean_id, filling_type, character_filling, filling_length, column_id" +
				" from column_clean where column_id = ? and clean_type = ?", columnId, CleanType.ZiFuBuQi.getCode());
		//2、如果查询到，则将补齐字符解码后返回前端
		if(!columnResult.isEmpty()){
			columnResult.setObject(0, "character_filling", StringUtil.unicode2String(columnResult.getString(0, "character_filling")));
			return columnResult;
		}
		//3、如果没有列字符补齐信息，则根据columnId查其所在表是否配置了整表字符补齐，如果查询到，则将补齐字符解码后返回前端
		Result tableResult = Dbo.queryResult("SELECT tc.table_clean_id, tc.filling_type, tc.character_filling, " +
				"tc.filling_length FROM table_clean tc" +
				"WHERE tc.table_id = (SELECT table_id FROM table_column WHERE column_id = ?)" +
				"AND tc.clean_type = ?", columnId, CleanType.ZiFuBuQi.getCode());
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
		Result result = Dbo.queryResult("SELECT table_clean_id, filling_type, character_filling, filling_length " +
				"FROM table_clean WHERE table_id = ? AND clean_type = ?", tableId, CleanType.ZiFuBuQi.getCode());
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
	@Param(name = "replaceArray", desc = "存放有待保存信息的JSON数组", range = "不为空")
	@Param(name = "tableId", desc = "数据库对应表主键，表清洗参数表外键", range = "不为空")
	public void saveSingleTbReplaceInfo(String replaceArray, long tableId){
		List<Table_clean> replaceList = JSONArray.parseArray(replaceArray, Table_clean.class);
		//1、使用tableId在table_clean表中删除之前对该表定义过的字符替换规则，不关心删除数目
		Dbo.execute("DELETE FROM table_clean WHERE table_id = ? AND clean_type = ?", tableId,
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
	@Param(name = "replaceArray", desc = "存放有待保存信息的JSON数组", range = "不为空")
	@Param(name = "columnId", desc = "表对应字段表主键，列清洗参数表外键", range = "不为空")
	public void saveColReplaceInfo(String replaceArray, long columnId){
		List<Column_clean> replaceList = JSONArray.parseArray(replaceArray, Column_clean.class);
		//1、使用columnId在column_clean表中删除之前对该字段定义过的字符替换规则，不关心删除数目
		Dbo.execute("DELETE FROM column_clean WHERE column_id = ? AND clean_type = ?", columnId,
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
		Result result = Dbo.queryResult("SELECT table_clean_id, field, replace_feild FROM table_clean WHERE " +
				"table_id = ? AND clean_type = ?", tableId, CleanType.ZiFuTiHuan.getCode());
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
			"3、如果没有列字符替换信息，则根据columnId查其所在表是否配置了整表字符替换，如果查询到，则将替换字符解码后返回前端" +
			"4、如果整表字符替换信息也没有，返回空的Result")
	@Param(name = "columnId", desc = "列ID，表对应字段表主键，列清洗信息表外键", range = "不为空")
	@Return(desc = "查询结果集", range = "不为空")
	public Result getColReplaceInfo(long columnId){
		//1、根据columnId在column_clean中查询该表的字符替换信息
		Result columnResult = Dbo.queryResult("select col_clean_id, field, replace_feild, column_id" +
				" from column_clean where column_id = ? and clean_type = ?", columnId, CleanType.ZiFuTiHuan.getCode());
		//2、如果查询到，则将源字符和替换字符解码后返回前端
		if(!columnResult.isEmpty()){
			columnResult.setObject(0, "field", StringUtil.unicode2String(columnResult.getString(0,
					"field")));
			columnResult.setObject(0, "replace_feild",
					StringUtil.unicode2String(columnResult.getString(0, "replace_feild")));
			return columnResult;
		}
		//3、如果没有列字符补齐信息，则根据columnId查其所在表是否配置了整表字符替换，如果查询到，则将补齐字符解码后返回前端
		Result tableResult = Dbo.queryResult("SELECT tc.table_clean_id, tc.field, tc.replace_feild " +
				"FROM table_clean tc" +
				"WHERE tc.table_id = (SELECT table_id FROM table_column WHERE column_id = ?)" +
				"AND tc.clean_type = ?", columnId, CleanType.ZiFuTiHuan.getCode());
		//4、如果整表字符替换信息也没有，返回空的Result
		if(tableResult.isEmpty()){
			return tableResult;
		}
		tableResult.setObject(0, "field", StringUtil.unicode2String(columnResult.getString(0,
				"field")));
		tableResult.setObject(0, "replace_feild",
				StringUtil.unicode2String(columnResult.getString(0, "replace_feild")));
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
	@Return(desc = "存有查询结果集的list集合", range = "不为空，数据的条数视实际情况而定" +
			"注意compflag/replaceflag/formatflag/splitflag/codevalue/trimflag这六个字段的值" +
			"yes表示该列做了相应的清洗设置，no表示没有列相应的设置")
	public List<Result> getColumnInfo(long tableId){
		//1、根据tableId去到table_column表中查询采集的列的列ID
		List<Object> columnIds = Dbo.queryOneColumnList("select column_id from " + Table_column.TableName +
				" where table_id = ? and is_get = ?", tableId, IsFlag.Shi.getCode());
		//2、如果没有找到采集列，直接返回一个空的集合
		if(columnIds.isEmpty()){
			return Collections.emptyList();
		}
		List<Result> resultList = new ArrayList<>();
		//3、如果找到了，再进行关联查询，查询出页面需要显示的信息
		for(Object columnId : columnIds){
			Result result = Dbo.queryResult("SELECT t1.column_id,t1.colume_name,t1.colume_ch_name,t2.table_name," +
							"(case t3.clean_type when ? then 'yes' else 'no' end) as compflag," +
							"(case t3.clean_type when ? then 'yes' else 'no' end) as replaceflag," +
							"(case t3.clean_type when ? then 'yes' else 'no' end ) as formatflag," +
							"(case t3.clean_type when ? then 'yes' else 'no' end) as splitflag," +
							"(case t3.clean_type when ? then 'yes' else 'no' end) as codevalue," +
							"(case t3.clean_type when ? then 'yes' else 'no' end) as trimflag" +
							"FROM table_column t1 JOIN table_info t2 ON t1.table_id =t2.table_id" +
							"left join column_clean t3 on t1.column_id = t3.column_id" +
							"WHERE t1.column_id = ? order by t1.remark asc", CleanType.ZiFuBuQi.getCode(),
					CleanType.ZiFuTiHuan.getCode(), CleanType.ShiJianZhuanHuan.getCode(), CleanType.MaZhiZhuanHuan.getCode(),
					CleanType.ZiFuChaiFen.getCode(), CleanType.ZiFuTrim.getCode(), (Long)columnId);

			resultList.add(result);
		}
		//4、返回
		return resultList;
	}

	/*
	 * 保存所有表清洗设置字符补齐和字符替换(saveJobCleanSDO)
	 * */
	public void saveAllTbCleanConfigInfo(){

	}

	/*
	 * 点击所有表清洗设置，回显所有表清洗设置字符补齐和字符替换规则(jobCleanSDO)
	 * */
	public void getAllTbCleanConfInfo(){

	}

	/*
	 * 列清洗页面，点击日期格式化列，设置按钮，回显针对该列设置的日期格式化规则(lookColDateSDO)
	 * */
	public void getDateFormatInfo(){

	}

	/*
	 * 列清洗页面，点击日期格式化列设置按钮，对该列配置日期格式化规则，保存按钮(saveColDateSDO)
	 * */
	public void saveDateFormatInfo(){

	}

	/*
	 * 列清洗页面，点击列拆分列设置按钮，回显针对该列设置的列拆分信息(codeSplitLookSDO)
	 * */
	public void getColSplitInfo(){

	}

	/*
	 * 列清洗页面，点击列拆分列设置按钮，列拆分弹框操作栏，删除按钮(deletesplitSDO)
	 * */
	public void deleteColSplitInfo(){

	}

	/*
	 * 列清洗页面，点击列拆分列设置按钮，列拆分弹框操作栏，保存按钮(codeSplitCleanSDO)
	 * */
	public void saveColSplitInfo(){

	}

	/*
	 * 列清洗页面，点击码值转换列设置按钮，回显针对该列设置的码值转换信息(codeSplitLookSDO)
	 * */
	public void getCVConversionInfo(){

	}

	/*
	 * 列清洗页面，点击码值转换列设置按钮，码值转换弹框确定按钮(codeValueChangeCleanSDO)
	 * */
	public void saveCVConversionInfo(){

	}



	/*
	 * 全表清洗优先级保存按钮(saveClearSortSDO)，针对本次采集任务的所有表保存清洗优先级
	 * */
	public void saveAllTbCleanOrder(List<Long> tableIds, String sort){
		//根据table_id,在table_info表中找到对应的表，将sort设置进去
	}

	/*
	 * 列清洗页面，整表优先级设置，对单个表的所有字段设置清洗优先级
	 * */
	public void saveSingleTbCleanOrder(long tableId, String sort){
		//根据table_id,在table_info表中找到对应的表，将sort设置进去
	}

	/*
	 * 列清洗页面，优先级调整设置，对单个字段设置清洗优先级
	 * */
	public void saveColCleanOrder(long columnId, String sort){
		//根据columnId,在table_column表中找到对应的字段，将清洗顺序设置进去
	}

	/*
	 * 列清洗页面，点击保存，由于字符补齐、字符替换、日期格式化、列拆分、码值转换都已经保存入库了，所以这里处理的逻辑只保存列首尾去空
	 * 但是必须将页面上每个字段是否补齐，是否替换，是否码值，是否日期也都传过来，如果用户配置了，但是有取消了勾选，要在这个方法里面做处理
	 * */
	public void saveColCleanTrim(){

	}

	/*
	 * 点击下一步按钮，保存该页面所有信息(其实经过上面所有方法的处理后，配置数据清洗保存的只有首尾去空这一项信息了)，
	 * 但是必须将页面上是否整表补齐，是否整表替换信息也传过来，如果用户配置了，但是又取消了勾选，要在这个方法里面做处理
	 * */
	public long saveDataCleanConfig(){
		return 0L;
	}
}
