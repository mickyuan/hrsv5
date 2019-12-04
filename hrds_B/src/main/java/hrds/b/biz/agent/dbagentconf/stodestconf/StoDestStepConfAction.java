package hrds.b.biz.agent.dbagentconf.stodestconf;

import com.alibaba.fastjson.JSONArray;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.exception.BusinessSystemException;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.resultset.Result;
import fd.ng.web.util.Dbo;
import hrds.b.biz.agent.bean.ColStoParam;
import hrds.b.biz.agent.bean.DataStoRelaParam;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.commons.utils.DboExecute;
import hrds.commons.utils.key.PrimayKeyGener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "定义存储目的地配置", author = "WangZhengcheng")
public class StoDestStepConfAction extends BaseAction{

	/*
	* 根据数据库采集任务ID获取定义存储目的地配置信息
	* * */
	@Method(desc = "根据数据库设置ID获得定义存储目的地页面初始化信息", logicStep = "" +
			"1、根据colSetId进行查询，查询出前端需要展示的数据" +
			"2、根据colSetId在table_info中获取该采集任务所有的采集表id集合" +
			"3、遍历这个集合，根据table_id在表存储信息表中查看该表是否定义了存储目的地" +
			"4、如果定义了，将destflag字段设为1" +
			"5、将查询到的信息返回前端")
	@Param(name = "colSetId", desc = "数据库设置ID，源系统数据库设置表主键，数据库对应表外键", range = "不为空")
	@Return(desc = "查询结果集", range = "不为空" +
			"注意data_extract_type字段，表示数据抽取方式，需要根据这个字段在选择目的地的时候展示不同的弹框" +
			"1：仅数据抽取，则选择目的地需要让用户填写数据文件的存放路径" +
			"2：数据抽取及入库，则选择目的地需要让用户选择存储的数据库，并配置列存储信息")
	public Result getInitInfo(long colSetId){
		//1、根据colSetId进行查询，查询出前端需要展示的数据
		Result result = Dbo.queryResult(" select ti.table_id, ti.table_name, ti.table_ch_name, tsi.is_zipper, " +
				" tsi.storage_type, tsi.storage_time, ded.data_extract_type, '0' as destflag " +
				" from " + Table_info.TableName + " ti" +
				" left join " + Table_storage_info.TableName + " tsi on ti.table_id = tsi.table_id" +
				" left join " + Data_extraction_def.TableName + " ded on ti.table_id = ded.table_id" +
				" where ti.database_id = ?", colSetId);
		//2、根据colSetId在table_info中获取该采集任务所有的采集表id集合
		List<Object> list = Dbo.queryOneColumnList("select table_id from " + Table_info.TableName + " where database_id = ?", colSetId);
		if(list.isEmpty()){
			throw new BusinessSystemException("未获取到数据库采集表");
		}
		//3、遍历这个集合，根据table_id在表存储信息表中查看该表是否定义了存储目的地
		for(int i = 0; i < list.size(); i++){
			Long tableIdFromTI = (Long) list.get(i);
			for(int j = 0; j < result.getRowCount(); j++){
				long tableIdFromResult = result.getLong(j, "table_id");
				if(tableIdFromTI.equals(tableIdFromResult)){
					long count = Dbo.queryNumber("select count(1) from " + Table_storage_info.TableName +
							" where table_id = ?", tableIdFromTI).orElseThrow(() -> new BusinessSystemException("SQL查询错误"));
					if(count > 0){
						//4、如果定义了，将destflag字段设为1
						result.setObject(j, "destflag", IsFlag.Shi.getCode());
					}
				}
			}
		}
		//5、将查询到的信息返回前端
		return result;
	}

	@Method(desc = "根据数据库设置ID获取当前数据库直连采集任务下所有抽取及入库的表ID", logicStep = "" +
			"1、根据数据库设置ID获取当前数据库采集任务所有抽取并入库的表ID" +
			"2、遍历结果集，根据表ID获取该表定义的存储目的地" +
			"3、以table_id为key，以存储目的地IDList集合为value，返回给前端，之所以value是List集合，是因为入库的表可以定义多个存储目的地")
	@Param(name = "colSetId", desc = "数据库设置ID，源系统数据库设置表主键，数据库对应表外键", range = "不为空")
	@Return(desc = "查询结果集", range = "不为空，key为table_id(抽取及入库的表),value为存储目的地IDList集合")
	public Map<Long, List<Object>> getTbStoDestByColSetId(long colSetId){
		//1、根据数据库设置ID获取当前数据库采集任务所有抽取并入库的表ID
		List<Object> tableIds = Dbo.queryOneColumnList("select ti.table_id from " + Table_info.TableName + " ti" +
				" join " + Data_extraction_def.TableName + " ded" +
				" on ti.table_id = ded.table_id" +
				" where ti.database_id = ? and ded.data_extract_type = ?", colSetId, DataExtractType.ShuJuChouQuJiRuKu.getCode());
		if(tableIds.isEmpty()){
			throw new BusinessSystemException("未获取到数据库采集表");
		}
		Map<Long, List<Object>> returnMap = new HashMap<>();
		//2、遍历结果集，根据表ID获取该表定义的存储目的地
		for(Object tableId : tableIds){
			List<Object> dslIds = Dbo.queryOneColumnList("select drt.dsl_id from " + Data_relation_table.TableName + " drt" +
					" where drt.storage_id = (select storage_id from " + Table_storage_info.TableName +
					" where table_id = ?)", (long)tableId);
			returnMap.put((Long)tableId, dslIds);
		}
		//3、以table_id为key，以存储目的地IDList集合为value，返回给前端，之所以value是List集合，是因为入库的表可以定义多个存储目的地
		return returnMap;
	}

	/*
	 * 根据存储配置主键信息获取存储目的地详细信息
	 * */
	@Method(desc = "根据存储配置主键信息获取存储目的地详细信息", logicStep = "" +
			"1、根据存储配置主键去数据库中查询存储目的地详细信息")
	@Param(name = "dslId", desc = "数据存储层配置表主键，数据存储层配置属性表外键", range = "不为空")
	@Return(desc = "查询结果集", range = "不为空" +
			"注意：详细信息是根据选择的存储目的地不同动态变化的" +
			"所以在显示的时候可以在页面显示两列，一列用于显示key，一列用于显示value" +
			"如：数据库名称:PGSQL" +
			"    数据库用户名:test")
	public Result getStoDestDetail(long dslId){
		return Dbo.queryResult(" select storage_property_key, storage_property_val from "
				+ Data_store_layer_attr.TableName + " where dsl_id = ?", dslId);
	}

	/*
	 * 对仅做数据抽取的表回显定义好的存储目的地
	 * */
	@Method(desc = "对仅做数据抽取的表回显定义好的存储目的地", logicStep = "" +
			"1、校验该表定义的数据抽取信息是否存在，之所以这样校验是因为配置抽取属性是上一个页面的职责，" +
			"如果有一张采集表没有配置抽取属性就进入到配置存储目的地页面，那么这样是不对的" +
			"2、查询数据抽取定义表，获取数据落地目录")
	@Param(name = "tableId", desc = "数据库采集表ID，数据抽取定义外键", range = "不为空")
	@Return(desc = "查询结果集", range = "不为空")
	public Result getStoDestForOnlyExtract(long tableId){
		//1、校验该表定义的数据抽取信息是否存在
		long count = Dbo.queryNumber("select count(1) from " + Data_extraction_def.TableName +
				" where table_id = ? and data_extract_type = ?", tableId, DataExtractType.JinShuJuChouQu.getCode())
				.orElseThrow(() -> new BusinessSystemException("查询结果必须有且只有一条"));
		if(count != 1){
			throw new BusinessSystemException("获取该表数据抽取信息异常");
		}
		//2、查询数据抽取定义表，获取数据落地目录并返回
		return Dbo.queryResult("select plane_url from " + Data_extraction_def.TableName + " where table_id = ? " +
				"and data_extract_type = ?", tableId, DataExtractType.JinShuJuChouQu.getCode());
	}

	/*
	 * 对仅做数据抽取的表保存定义好的存储目的地
	 * */
	@Method(desc = "对仅做数据抽取的表保存定义好的存储目的地", logicStep = "" +
			"1、使用tableId进行校验，判断该表是否定义过数据抽取信息，且数据抽取方式为仅抽取" +
			"2、若不存在，向前端抛异常" +
			"3、若存在，则将存储目的地更新到对应的字段中")
	@Param(name = "tableId", desc = "数据库采集对应表ID，数据抽取定义表外键", range = "不为空")
	@Param(name = "stoDest", desc = "待保存的数据落地目录", range = "不为空")
	public void saveStoDestForOnlyExtract(long tableId, String stoDest){
		//1、使用tableId进行校验，判断该表是否定义过数据抽取信息，且数据抽取方式为仅抽取
		long count = Dbo.queryNumber("select count(1) from " + Data_extraction_def.TableName +
				" where table_id = ? and data_extract_type = ?", tableId, DataExtractType.JinShuJuChouQu.getCode())
				.orElseThrow(() -> new BusinessSystemException("查询结果必须有且只有一条"));
		//2、若不存在，向前端抛异常
		if(count != 1){
			throw new BusinessSystemException("获取该表数据抽取信息异常");
		}
		//3、若存在，则将存储目的地更新到对应的字段中，必须更新一条数据，否则抛出异常
		DboExecute.updatesOrThrow("保存存储目的地失败", "update " + Data_extraction_def.TableName
				+ " set plane_url = ? where table_id = ?", stoDest, tableId);
	}

	/*
	* 根据tableId获取该表选择的存储目的地和系统中配置的所有存储目的地
	* */
	@Method(desc = "根据tableId回显该表选择的存储目的地和系统中配置的所有存储目的地", logicStep = "" +
			"1、获取所有存储目的地信息，并且追加一列usedflag，固定值为0，表示默认所有存储目的地没有被这个当前表所使用" +
			"2、如果结果集为空，表示系统中没有定义存储目的地" +
			"3、尝试获取该表定义好的存储目的地，如果之前定义过，那么就能获取到数据，否则就获取不到" +
			"4、如果获取不到，说明之前该表未定义过存储目的地，则直接返回结果集" +
			"5、否则，说明之前该表定义过存储目的地，则需要把定义好的找出来，把usedflag标识位设置1" +
			"6、返回")
	@Param(name = "tableId", desc = "采集表ID，表存储信息表外键", range = "不为空")
	@Return(desc = "查询结果集", range = "不为空，注意每条数据的usedFlag字段，true表示该表配置了该存储目的地，" +
			"在页面上根据单选框请勾选，false表示该表没有配置这个存储目的地，在页面上单选框不要勾选")
	public Result getStoDestByTableId(long tableId){
		//1、获取所有存储目的地信息，并且追加一列usedflag，固定值为0，表示默认所有存储目的地没有被这个当前表所使用
		Result result = Dbo.queryResult("select dsl_id, dsl_name, store_type, '0' as usedflag from "
				+ Data_store_layer.TableName);
		//2、如果结果集为空，表示系统中没有定义存储目的地
		if(result.isEmpty()){
			throw new BusinessSystemException("系统中未定义存储目的地信息，请联系管理员");
		}
		//3、尝试获取该表定义好的存储目的地，如果之前定义过，那么就能获取到数据，否则就获取不到
		Result tbStoRela = Dbo.queryResult("select drt.dsl_id from " + Data_relation_table.TableName + " drt" +
				" where drt.storage_id = (select storage_id from " + Table_storage_info.TableName +
				" where table_id = ?)", tableId);
		//4、如果获取不到，说明之前该表未定义过存储目的地，则直接返回结果集
		if(tbStoRela.isEmpty()){
			return result;
		}
		//5、否则，说明之前该表定义过存储目的地，则需要把定义好的找出来，把usedflag标识位设置1
		for(int i = 0; i < tbStoRela.getRowCount(); i++){
			long dslId = tbStoRela.getLong(i, "dsl_id");
			for(int j = 0;j < result.getRowCount(); j++){
				long dslIdFromResult = result.getLong(j, "dsl_id");
				if(dslId == dslIdFromResult){
					result.setObject(j, "usedflag", IsFlag.Shi.getCode());
				}
			}
		}
		//6、返回
		return result;
	}

	@Method(desc = "根据存储目的地ID获取选择列画面需要展示的表头信息", logicStep = "" +
			"1、无论是选择什么存储目的地，都需要展示列名和列中文名这两列" +
			"2、根据存储目的地ID去数据存储附加信息表中获取需要额外展示的列" +
			"3、将列的code值转换成枚举项的value放入map中" +
			"4、返回前端")
	@Param(name = "dslId", desc = "数据存储层配置表主键，数据存储附加信息表外键", range = "不为空")
	@Return(desc = "表头map集合", range = "不为空，key为列存储信息结果集的列名，value为需要显示在界面上的中文名")
	public Map<String, String> getColumnHeader(long dslId){
		Map<String, String> header = new HashMap<>();
		//1、无论是选择什么存储目的地，都需要展示列名和列中文名这两列
		header.put("colume_name", "列名");
		header.put("colume_ch_name", "列中文名");
		//2、根据存储目的地ID去数据存储附加信息表中获取需要额外展示的列
		List<Object> list = Dbo.queryOneColumnList("select dsla_storelayer from " +
				Data_store_layer_added.TableName + " where dsl_id = ?", dslId);
		if(!list.isEmpty()){
			for(Object obj : list){
				//3、将列的code值转换成枚举项的value放入map中
				header.put(StoreLayerAdded.ofValueByCode((String)obj), StoreLayerAdded.ofValueByCode((String)obj));
			}
		}
		//4、返回前端
		return header;
	}

	@Method(desc = "根据存储目的地ID获取选择列画面需要的附加属性信息ID", logicStep = "" +
			"1、根据存储目的地ID去数据存储附加信息表中获取附加属性信息ID和附加属性信息" +
			"2、封装成Map集合返回前端")
	@Param(name = "dslId", desc = "数据存储层配置表主键，数据存储附加信息表外键", range = "不为空")
	@Return(desc = "Map集合", range = "key为附加信息code，value为附加信息ID，" +
			"这个附加信息ID就是在保存列存储信息的时候需要放在数组里面传递的" +
			"如果该目的地没有附加信息，那么返回的集合就是空集合" +
			"需要前端人员根据code再去StoreLayerAdded代码项中取得value")
	public Map<String, Long> getDataStoreLayerAddedId(long dslId){
		Map<String, Long> storeAddedId = new HashMap<>();
		Result result = Dbo.queryResult("select dslad_id, dsla_storelayer from " +
				Data_store_layer_added.TableName + " where dsl_id = ?", dslId);
		if(result.isEmpty()){
			return storeAddedId;
		}
		for(int i = 0; i < result.getRowCount(); i++){
			storeAddedId.put(result.getString(i, "dsla_storelayer"),
					result.getLong(i, "dslad_id"));
		}
		return storeAddedId;
	}


	@Method(desc = "根据表ID获取该表所有的列存储信息", logicStep = "" +
			"1、根据表ID查询出该表所有采集列的列名和列中文名(结果集1)" +
			"2、根据存储目的地ID获取附加信息(结果集2)" +
			"3、结果集2中查询到的行作为列添加到结果1里面，作为结果集3，形成前端页面展示的基础" +
			"4、在字段存储信息表中，关联数据存储附加信息表，找到该表中的特殊字段，根据列ID和结果集三进行匹配，最终形成结果集4" +
			"5、返回")
	@Param(name = "tableId", desc = "数据库对应表主键，表清洗参数表外键", range = "不为空")
	@Param(name = "dslId", desc = "数据存储层配置ID,数据存储附加信息表外键", range = "不为空")
	@Return(desc = "查询结果集", range = "不为空")
	public Result getColumnStoInfo(long tableId, long dslId){
		//1、根据表ID查询出该表所有采集列的列名和列中文名(结果集1)
		Result resultOne = Dbo.queryResult("select tc.column_id, tc.colume_name, tc.colume_ch_name from "
				+ Table_column.TableName + " tc where tc.table_id = ? and tc.is_get = ?", tableId, IsFlag.Shi.getCode());
		if(resultOne.isEmpty()){
			throw new BusinessSystemException("未找到属于该表的字段");
		}
		//2、根据存储目的地ID获取附加信息(结果集2)
		Result resultTwo = Dbo.queryResult("select dsla.dsla_storelayer from " +
				Data_store_layer_added.TableName + " dsla join " + Data_store_layer.TableName
				+ " dsl on dsla.dsl_id = dsl.dsl_id where dsl.dsl_id = ?", dslId);
		//3、结果集2中查询到的行作为列添加到结果1里面，并且设置默认值为否，作为结果集3，形成前端页面展示的基础
		if(!resultTwo.isEmpty()){
			for(int i = 0; i < resultTwo.getRowCount(); i++){
				for(int j = 0; j < resultOne.getRowCount(); j++){
					//这里给数据存储附加信息设置一个默认值0
					resultOne.setObject(j, StoreLayerAdded.ofValueByCode(
							resultTwo.getString(i, "dsla_storelayer")), IsFlag.Fou.getCode());
				}
			}
		}else{
			//表示当前存储目的地没有附加信息，则直接把结果集1返回前端
			return resultOne;
		}
		//4、在字段存储信息表中，关联数据存储附加信息表，找到该表中的特殊字段，根据列ID和结果集3进行匹配，最终形成结果集4
		Result resultThree = Dbo.queryResult("select csi.column_id, dsla.dsla_storelayer, csi_number from " +
				Column_storage_info.TableName + " csi left join " + Data_store_layer_added.TableName + " dsla" +
				" on dsla.dslad_id = csi.dslad_id" +
				" where csi.column_id in (select column_id from table_column where table_id = ?) and dsla.dsl_id = ?"
				, tableId, dslId);
		for(int i = 0; i < resultThree.getRowCount(); i++){
			long columnIdFromCSI = resultThree.getLong(i, "column_id");
			for(int j = 0; j < resultOne.getRowCount(); j++){
				long columnIdFromTC = resultOne.getLong(j, "column_id");
				if(columnIdFromCSI == columnIdFromTC){
					resultOne.setObject(j, StoreLayerAdded.ofValueByCode(
							resultThree.getString(i, "dsla_storelayer")), IsFlag.Shi.getCode());
					resultOne.setObject(j, "csi_number",
							resultThree.getLong(i, "csi_number"));
				}
			}
		}
		//5、返回
		return resultOne;
	}

	/*
	* 保存字段存储信息
	* */
	@Method(desc = "保存表的字段存储信息", logicStep = "" +
			"1、将colStoInfoString解析为List集合" +
			"2、在每保存一个字段的存储目的地前，先尝试在column_storage_info表中删除该表所有列的信息，不关心删除的数据" +
			"3、如果反序列化得到的List集合不为空，则遍历集合" +
			"4、保存")
	@Param(name = "colStoInfoString", desc = "存放待保存字段存储配置信息的json串", range = "" +
			"如果在一张表中没有任何一个需要配置特殊存储属性的列，这个字段传空字符串", nullable = true, valueIfNull = "")
	@Param(name = "tableId", desc = "字段所在表ID,table_info表主键，table_column表外键", range = "不为空")
	public void saveColStoInfo(String colStoInfoString, long tableId){
		//1、将colStoInfoString解析为List集合
		List<ColStoParam> colStoParams = JSONArray.parseArray(colStoInfoString, ColStoParam.class);
		//2、在每保存一个字段的存储目的地前，先尝试在column_storage_info表中删除该表所有列的信息，不关心删除的数据
		Dbo.execute("delete from " + Column_storage_info.TableName + " where column_id in (select column_id " +
				" from " + Table_column.TableName + " where table_id = ?)", tableId);
		if(!colStoParams.isEmpty()){
			//3、如果反序列化得到的List集合不为空，则遍历集合
			for(ColStoParam param : colStoParams){
				Long columnId = param.getColumnId();
				long[] dsladIds = param.getDsladIds();
				if(dsladIds == null || !(dsladIds.length > 0)){
					throw new BusinessSystemException("请检查配置信息，并为待保存的字段选择其是否具有特殊性质");
				}
				for(long dsladId : dsladIds){
					Column_storage_info columnStorageInfo = new Column_storage_info();
					columnStorageInfo.setColumn_id(columnId);
					columnStorageInfo.setDslad_id(dsladId);
					//根据数据存储附加信息ID获取存储目的地类型
					List<Object> list = Dbo.queryOneColumnList("select dsl.store_type from " +
							Data_store_layer.TableName + " dsl, " + Data_store_layer_added.TableName +
							" dsla where dsl.dsl_id = dsla.dsl_id and dsla.dslad_id = ?", dsladId);
					//如果获取不到或者获取到的值有多个，则抛出异常
					if(list.isEmpty() || list.size() > 1){
						throw new BusinessSystemException("通过字段存储附加信息获得存储目的地信息出错");
					}
					//如果获取到的存储目的地为HBASE并且csiNumber不为空，则说明该列是作为hbase的rowkey
					if(param.getCsiNumber() != null && store_type.HBASE.getCode().equalsIgnoreCase((String) list.get(0))){
						//保存rowkey的顺序
						columnStorageInfo.setCsi_number(param.getCsiNumber());
					}
					columnStorageInfo.add(Dbo.db());
				}
			}
		}
		//如果反序列化得到的List集合为空，则说明页面上用户没有定义任何一个具有特殊存储属性的字段，于是不做任何处理
	}

	@Method(desc = "在配置字段存储信息时，更新字段中文名", logicStep = "" +
			"1、将传过来的json串反序列化为List集合" +
			"2、对集合的长度进行校验，如果集合为空，抛出异常" +
			"3、遍历集合，更新每个字段的中文名")
	@Param(name = "columnString", desc = "待更新的字段信息，json数组", range = "不为空，每个json数组中的json对象的key为" +
			"column_id：字段ID；colume_ch_name：字段中文名")
	public void updateColumnZhName(String columnString){
		//1、将传过来的json串反序列化为List集合
		List<Table_column> tableColumns = JSONArray.parseArray(columnString, Table_column.class);
		//2、对集合的长度进行校验，如果集合为空，抛出异常
		if(tableColumns.isEmpty()){
			throw new BusinessSystemException("获取字段信息失败");
		}
		//3、遍历集合，更新每个字段的中文名
		for(int i = 0; i < tableColumns.size(); i++){
			Table_column tableColumn = tableColumns.get(i);
			if(tableColumn.getColumn_id() == null){
				throw new BusinessSystemException("保存第" + i + 1 + "个字段的中文名必须关联字段ID");
			}
			DboExecute.updatesOrThrow("保存第" + i + 1 + "个字段的中文名失败", "update " +
					Table_column.TableName + " set colume_ch_name = ? where column_id = ?",
					tableColumn.getColume_ch_name(), tableColumn.getColumn_id());
		}
	}

	/*
	 * 保存表存储属性配置，仅保存抽取方式为<抽取并入库>的表
	 * */
	@Method(desc = "保存表存储属性配置，仅保存抽取方式为<抽取并入库>的表", logicStep = "" +
			"1、将tbStoInfoString反序列化为List集合，这个集合中的内容是用来保存进入表存储信息表" +
			"2、将dslIdString反序列化为List集合，这个集合中的内容是用来保存进入数据存储关系表" +
			"3、校验，每张入库的表都必须有其对应的存储目的地" +
			"4、开始执行保存操作" +
			"4-1、如果是修改表的存储信息，则删除该表原有的存储配置，重新插入新的数据" +
			"4-2、对待保存的数据设置主键等信息" +
			"4-3、在数据抽取定义表中，根据表ID把数据文件格式查询出来存入Table_storage_info对象中" +
			"4-4、获取当前保存表的ID" +
			"4-5、遍历dataStoRelaParams集合，找到表ID相同的对象" +
			"4-6、保存表存储信息" +
			"5、返回数据库设置ID，目的是下一个页面可以找到上一个页面配置的信息")
	@Param(name = "tbStoInfoString", desc = "存放待保存表存储配置信息的json串", range = "不为空")
	@Param(name = "colSetId", desc = "数据库采集设置表ID", range = "不为空")
	@Param(name = "dslIdString", desc = "Json格式字符串，json对象中携带的是采集表ID和存储目的地ID，" +
			"注意，一张表抽取并入库的表可以保存到多个目的地中", range = "不为空")
	@Return(desc = "本次数据库采集设置表ID，方便下一个页面可以根据这个ID找到之前的配置", range = "不为空")
	public long saveTbStoInfo(String tbStoInfoString, long colSetId, String dslIdString){
		//1、将tbStoInfoString反序列化为List集合，这个集合中的内容是用来保存进入表存储信息表
		List<Table_storage_info> tableStorageInfos = JSONArray.parseArray(tbStoInfoString, Table_storage_info.class);
		verifyTbStoConf(tableStorageInfos);
		//2、将dslIdString反序列化为List集合，这个集合中的内容是用来保存进入数据存储关系表
		List<DataStoRelaParam> dataStoRelaParams = JSONArray.parseArray(dslIdString, DataStoRelaParam.class);

		//3、校验，每张入库的表都必须有其对应的存储目的地
		if(tableStorageInfos.size() != dataStoRelaParams.size()){
			throw new BusinessSystemException("保存表存储信息失败，请确保入库的表都选择了存储目的地");
		}

		//4、开始执行保存操作
		for(Table_storage_info storageInfo : tableStorageInfos){
			//4-1、删除该表原有的存储配置，重新插入新的数据
			long count = Dbo.queryNumber("select count(1) from " + Table_storage_info.TableName +
					" where table_id = ?", storageInfo.getTable_id()).orElseThrow(() ->
					new BusinessSystemException("查询结果必须有且只有一条"));
			if(count == 1){
				//在table_storage_info表中查询到了数据，表示修改该表的存储信息
				/*
				 * 在每保存一张表的数据存储关系前，先尝试在data_relation_table表中使用storage_id删除记录，
				 * 由于一张表可以选择多个目的地进行存储，所以不关心删除的数目
				 * */
				Dbo.execute("delete from " + Data_relation_table.TableName + " where storage_id in " +
								"(select storage_id from " + Table_storage_info.TableName + " where table_id = ?)"
						, storageInfo.getTable_id());
				/*
				* 在每保存一张表的存储目的地前，先尝试在table_storage_info表中使用table_id删除记录，
				* 因为一张需要入库的表在table_storage_info表中只保存一条记录，所以只能删除掉一条
				* */
				DboExecute.deletesOrThrow("删除表存储信息异常，一张表入库信息只能在表存储信息表中出现一条记录",
						"delete from " + Table_storage_info.TableName + " where table_id = ?"
						, storageInfo.getTable_id());
			}
			//4-2、对待保存的数据设置主键等信息
			String storageId = PrimayKeyGener.getNextId();
			storageInfo.setStorage_id(storageId);
			//4-3、在数据抽取定义表中，根据表ID把数据文件格式查询出来存入Table_storage_info对象中
			List<Object> list = Dbo.queryOneColumnList("select dbfile_format from " +
					Data_extraction_def.TableName + " where table_id = ?", storageInfo.getTable_id());
			if(list.isEmpty()){
				throw new BusinessSystemException("获取采集表卸数文件格式失败");
			}
			if(list.size() > 1){
				throw new BusinessSystemException("获取采集表卸数文件格式失败");
			}
			storageInfo.setFile_format((String) list.get(0));
			//4-4、获取当前保存表的ID
			Long tableIdFromTSI = storageInfo.getTable_id();
			//4-5、遍历dataStoRelaParams集合，找到表ID相同的对象
			for(DataStoRelaParam param : dataStoRelaParams){
				Long tableIdFromParam = param.getTableId();
				if(tableIdFromTSI.equals(tableIdFromParam)){
					//将该张表的存储目的地保存到数据存储关系表中，有几个目的地，就保存几条
					long[] dslIds = param.getDslIds();
					if(dslIds == null || !(dslIds.length > 0)){
						throw new BusinessSystemException("请检查配置信息，并为每张入库的表选择至少一个存储目的地");
					}
					for(long dslId : dslIds){
						Data_relation_table relationTable = new Data_relation_table();
						relationTable.setStorage_id(storageId);
						relationTable.setDsl_id(dslId);

						relationTable.add(Dbo.db());
					}
				}
			}
			//4-6、保存表存储信息
			storageInfo.add(Dbo.db());
		}
		//5、返回数据库设置ID，目的是下一个页面可以找到上一个页面配置的信息
		return colSetId;
	}

	@Method(desc = "在配置表存储信息时，更新表中文名和表名", logicStep = "" +
			"1、将传过来的json串反序列化为List集合" +
			"2、对集合的长度进行校验，如果集合为空，抛出异常" +
			"3、遍历集合，更新每张表的中文名和表名")
	@Param(name = "tableString", desc = "待更新的字段信息，json数组", range = "不为空，每个json数组中的json对象的key为" +
			"table_id：表ID；table_ch_name：表中文名；table_name：表名")
	public void updateTableName(String tableString){
		//1、将传过来的json串反序列化为List集合
		List<Table_info> tableInfos = JSONArray.parseArray(tableString, Table_info.class);
		//2、对集合的长度进行校验，如果集合为空，抛出异常
		if(tableInfos.isEmpty()){
			throw new BusinessSystemException("获取表信息失败");
		}
		//3、遍历集合，更新每张表的中文名和表名
		for(int i = 0; i < tableInfos.size(); i++){
			Table_info tableInfo = tableInfos.get(i);
			if(tableInfo.getTable_id() == null){
				throw new BusinessSystemException("保存第" + i + 1 + "张表的名称信息必须关联字段ID");
			}
			if(StringUtil.isBlank(tableInfo.getTable_name())){
				throw new BusinessSystemException("第" + i + 1 + "张表的表名必须填写");
			}
			if(StringUtil.isBlank(tableInfo.getTable_ch_name())){
				throw new BusinessSystemException("第" + i + 1 + "张表的表中文名必须填写");
			}
			DboExecute.updatesOrThrow("保存第" + i + "张表名称信息失败", "update " +
							Table_info.TableName + " set table_name = ?, table_ch_name = ? where table_id = ?",
					tableInfo.getTable_name(), tableInfo.getTable_ch_name(), tableInfo.getTable_id());
		}
	}

	@Method(desc = "校验保存表存储配置信息时各个字段的合法性", logicStep = "" +
			"1、校验保存表存储配置信息时，必须关联表" +
			"2、校验保存表存储配置信息时，必须选择进数方式" +
			"3、校验保存表存储配置信息时，必须选择是否拉链存储" +
			"4、校验保存表存储配置信息时，必须填写存储期限")
	@Param(name = "tableStorageInfos", desc = "待保存的表存储配置信息", range = "不为空")
	private void verifyTbStoConf(List<Table_storage_info> tableStorageInfos){
		for(int i = 0; i < tableStorageInfos.size(); i++){
			Table_storage_info storageInfo = tableStorageInfos.get(i);
			if(storageInfo.getTable_id() == null){
				throw new BusinessSystemException("第" + i + 1 + "条数据保存表存储配置时，请关联表");
			}
			if(StringUtil.isBlank(storageInfo.getStorage_type())){
				throw new BusinessSystemException("第" + i + 1 + "条数据保存表存储配置时，请选择进数方式");
			}
			StorageType.ofEnumByCode(storageInfo.getStorage_type());
			if(StringUtil.isBlank(storageInfo.getIs_zipper())){
				throw new BusinessSystemException("第" + i + 1 + "条数据保存表存储配置时，请选择是否拉链存储");
			}
			IsFlag.ofEnumByCode(storageInfo.getIs_zipper());
			if(storageInfo.getStorage_time() == null){
				throw new BusinessSystemException("第" + i + 1 + "条数据保存表存储配置时，请填写存储期限");
			}
		}
	}
}
