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
import hrds.b.biz.agent.bean.DataStoRelaParam;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.DataExtractType;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.StorageType;
import hrds.commons.codes.StoreLayerAdded;
import hrds.commons.entity.*;
import hrds.commons.utils.DboExecute;
import hrds.commons.utils.key.PrimayKeyGener;

import java.util.List;

@DocClass(desc = "定义存储目的地配置", author = "WangZhengcheng")
public class StoDestStepConfAction extends BaseAction{

	private static final int EMPTY_RESULT_COUNT = 0;

	/*
	* 根据数据库采集任务ID获取定义存储目的地配置信息
	* * */
	@Method(desc = "根据数据库设置ID获得定义存储目的地页面初始化信息", logicStep = "" +
			"1、根据colSetId进行查询，查询出前端需要展示的数据" +
			"2、将查询到的信息返回前端")
	@Param(name = "colSetId", desc = "数据库设置ID，源系统数据库设置表主键，数据库对应表外键", range = "不为空")
	@Return(desc = "查询结果集", range = "不为空" +
			"注意data_extract_type字段，表示数据抽取方式，需要根据这个字段在选择目的地的时候展示不同的弹框" +
			"1：仅数据抽取，则选择目的地需要让用户填写数据文件的存放路径" +
			"2、数据抽取及入库，则选择目的地需要让用户选择存储的数据库，并配置列存储信息")
	public Result getInitInfo(long colSetId){
		return Dbo.queryResult(" select ti.table_id, ti.table_name, ti.table_ch_name, tsi.is_zipper, " +
				" tsi.storage_type, tsi.storage_time, ded.data_extract_type from " + Table_info.TableName + " ti" +
				" left join " + Table_storage_info.TableName + " tsi on ti.table_id = tsi.table_id" +
				" left join " + Data_extraction_def.TableName + " ded on ti.table_id = ded.table_id" +
				" where ti.database_id = ?", colSetId);
	}

	/*
	 * 对抽取并入库的表获取存储目的地
	 * */
	@Method(desc = "对抽取并入库的表获取存储目的地", logicStep = "" +
			"1、查询数据库，获取存储目的地")
	@Return(desc = "查询结果集", range = "不为空")
	public Result getStoDestInfo(){
		return Dbo.queryResult(" select dsl_id, dsl_name, store_type from " + Data_store_layer.TableName);
	}

	/*
	 * 根据存储配置主键信息获取存储目的地详细信息
	 * */
	@Method(desc = "根据存储配置主键信息获取存储目的地详细信息", logicStep = "" +
			"1、根据存储配置主键去数据库中查询存储目的地详细信息")
	@Param(name = "dslId", desc = "数据存储层配置表主键，数据存储层配置属性表外键", range = "不为空")
	@Return(desc = "查询结果集", range = "不为空")
	public Result getStoDestDetail(long dslId){
		return Dbo.queryResult(" select storage_property_key, storage_property_val from "
				+ Data_store_layer_attr.TableName + " where dsl_id = ?", dslId);
	}

	/*
	 * 对仅做数据抽取的表回显定义好的存储目的地
	 * */
	@Method(desc = "对仅做数据抽取的表回显定义好的存储目的地", logicStep = "" +
			"1、校验该表定义的数据抽取信息是否存在" +
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
		//3、若存在，则将存储目的地更新到对应的字段中
		DboExecute.updatesOrThrow("保存存储目的地失败", "update " + Data_extraction_def.TableName
				+ " set plane_url = ? where table_id = ?", stoDest, tableId);
	}

	/*
	* 根据tableId获取该表选择的存储目的地和系统中配置的所有存储目的地
	* */
	@Method(desc = "根据tableId获取该表选择的存储目的地和系统中配置的所有存储目的地", logicStep = "" +
			"1、在数据库中，根据SQL语句查出当前系统中的所有存储配置和关联表的使用情况" +
			"2、使用传入的tableId和查询出的tableId进行比较" +
			"3、如果传入的tableId和查询出的tableId相等，则给该列结果加上一个usedFlag为true" +
			"4、返回")
	@Param(name = "tableId", desc = "采集表ID，表存储信息表外键", range = "不为空")
	@Return(desc = "查询结果集", range = "不为空，注意每条数据的usedFlag字段，true表示该表配置了该存储目的地，在页面上根据单选框请勾选，" +
			"false表示该表没有配置这个存储目的地，在页面上单选框不要勾选")
	public Result getStoDestByTableId(long tableId){
		Result result = Dbo.queryResult("select dsl.dsl_name, dsl.store_type, tsi.table_id " +
				" from " + Data_store_layer.TableName + " dsl" +
				" left join " + Data_relation_table.TableName + " drt" +
				" on drt.dsl_id = dsl.dsl_id" +
				" left join " + Table_storage_info.TableName + " tsi" +
				" on tsi.storage_id = drt.storage_id");
		if(result.isEmpty()){
			throw new BusinessSystemException("获取系统存储目的地信息失败，请联系管理员");
		}
		for(int i = 0; i < result.getRowCount(); i++){
			if(result.getLong(i, "table_id") == tableId){
				result.setObject(i, "usedFlag", true);
			}else{
				result.setObject(i, "usedFlag", false);
			}
		}
		return result;
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
		Result resultOne = Dbo.queryResult("select tc.column_id, tc.colume_name, tc.colume_ch_name from " + Table_column.TableName
				+ " tc where tc.table_id = ?", tableId);
		if(resultOne.isEmpty()){
			throw new BusinessSystemException("未找到属于该表的字段");
		}
		//2、根据存储目的地ID获取附加信息(结果集2)
		Result resultTwo = Dbo.queryResult("select dsla.dsla_storelayer from " + Data_store_layer_added.TableName + " dsla" +
				" join " + Data_store_layer.TableName + " dsl on dsla.dsl_id = dsl.dsl_id where dsl.dsl_id = ?", dslId);
		//3、结果集2中查询到的行作为列添加到结果1里面，作为结果集3，形成前端页面展示的基础
		if(!resultTwo.isEmpty()){
			for(int i = 0; i < resultTwo.getRowCount(); i++){
				for(int j = 0; j < resultOne.getRowCount(); j++){
					//这里给数据存储附加信息设置一个默认值0
					resultOne.setObject(j, StoreLayerAdded.ofValueByCode(resultTwo.getString(i, "dsla_storelayer")), IsFlag.Fou.getCode());
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
				" where csi.column_id in (select column_id from table_column where table_id = ?)", tableId);
		for(int i = 0; i < resultThree.getRowCount(); i++){
			long columnIdFromCSI = resultThree.getLong(i, "column_id");
			for(int j = 0; j < resultOne.getRowCount(); j++){
				long columnIdFromTC = resultOne.getLong(j, "column_id");
				if(columnIdFromCSI == columnIdFromTC){
					resultOne.setObject(j, StoreLayerAdded.ofValueByCode(resultThree.getString(i, "dsla_storelayer")), IsFlag.Shi.getCode());
					resultOne.setObject(j, "csi_number", resultThree.getLong(i, "csi_number"));
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
			"2、遍历集合，在每保存一个字段的存储目的地前，先尝试在column_storage_info表中使用column_id删除这样一条记录，不关心是否删除到数据" +
			"3、保存")
	@Param(name = "colStoInfoString", desc = "存放待保存字段存储配置信息的json串", range = "不为空")
	public void saveColStoInfoForHbase(String colStoInfoString){
		//1、将colStoInfoString解析为List集合
		List<Column_storage_info> storageInfos = JSONArray.parseArray(colStoInfoString, Column_storage_info.class);
		//2、遍历集合
		for(Column_storage_info storageInfo : storageInfos){
			if(storageInfo.getColumn_id() == null){
				throw new BusinessSystemException("保存字段存储信息时，必须关联字段");
			}
			if(storageInfo.getDslad_id() == null){
				throw new BusinessSystemException("保存字段存储信息时，必须关联字段的特殊属性");
			}
			//在每保存一个字段的存储目的地前，先尝试在column_storage_info表中使用column_id删除这样一条记录，不关心是否删除到数据
			Dbo.execute("delete from " + Column_storage_info.TableName + " where column_id = ?",
					storageInfo.getColumn_id());
			//4、保存
			storageInfo.add(Dbo.db());
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
			"4-1、判断表储存ID是否为空，如果不为空，则删除该表原有的存储配置，重新插入新的数据" +
			"4-2、对待保存的数据设置主键等信息" +
			"4-3、在数据抽取定义表中，根据表ID把数据文件格式查询出来存入Table_storage_info对象中" +
			"4-4、获取一张表的ID" +
			"4-5、遍历dataStoRelaParams集合，找到表ID相同的对象" +
			"4-6、保存表存储信息" +
			"5、返回数据库设置ID，目的是下一个页面可以找到上一个页面配置的信息")
	@Param(name = "tbStoInfoString", desc = "存放待保存表存储配置信息的json串", range = "不为空")
	@Param(name = "colSetId", desc = "数据库采集设置表ID", range = "不为空")
	@Param(name = "dslIdString", desc = "Json格式字符串，json对象中携带的是采集表ID和存储目的地ID，注意，一张表抽取并入库的表" +
			"可以保存到多个目的地中", range = "不为空")
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
			//4-1、判断表储存ID是否为空，如果不为空，则删除该表原有的存储配置，重新插入新的数据
			if(storageInfo.getStorage_id() != null){
				//storage_id不为空，表示修改表存储信息
				//在每保存一张表的存储目的地前，先尝试在table_storage_info表中使用table_id删除记录，不关心是否删除到数据
				Dbo.execute("delete from " + Table_storage_info.TableName + " where storage_id = ?", storageInfo.getStorage_id());
				//在每保存一张表的数据存储关系前，先尝试在data_relation_table表中使用storage_id删除记录，不关心是否删除到数据
				Dbo.execute("delete from " + Data_relation_table.TableName + " where storage_id = ?", storageInfo.getStorage_id());
			}
			//4-2、对待保存的数据设置主键等信息
			String storageId = PrimayKeyGener.getNextId();
			storageInfo.setStorage_id(storageId);
			//4-3、在数据抽取定义表中，根据表ID把数据文件格式查询出来存入Table_storage_info对象中
			List<Object> list = Dbo.queryOneColumnList("select dbfile_format from " + Data_extraction_def.TableName +
					" where table_id = ?", storageInfo.getTable_id());
			if(list.isEmpty()){
				throw new BusinessSystemException("获取采集表卸数文件格式失败");
			}
			if(list.size() > 1){
				throw new BusinessSystemException("获取采集表卸数文件格式失败");
			}
			storageInfo.setFile_format((String) list.get(0));
			//4-4、获取一张表的ID
			Long tableIdFromTSI = storageInfo.getTable_id();
			//4-5、遍历dataStoRelaParams集合，找到表ID相同的对象
			for(DataStoRelaParam param : dataStoRelaParams){
				Long tableIdFromParam = param.getTableId();
				if(tableIdFromTSI.equals(tableIdFromParam)){
					//将该张表的存储目的地保存到数据存储关系表中，有几个目的地，就保存几条
					long[] dslIds = param.getDslIds();
					if(!(dslIds.length > 0)){
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
				throw new BusinessSystemException("第" + i + "条数据保存表存储配置时，请关联表");
			}
			if(StringUtil.isBlank(storageInfo.getStorage_type())){
				throw new BusinessSystemException("第" + i + "条数据保存表存储配置时，请选择进数方式");
			}
			StorageType.ofEnumByCode(storageInfo.getStorage_type());
			if(StringUtil.isBlank(storageInfo.getIs_zipper())){
				throw new BusinessSystemException("第" + i + "条数据保存表存储配置时，请选择是否拉链存储");
			}
			IsFlag.ofEnumByCode(storageInfo.getIs_zipper());
			if(storageInfo.getStorage_time() == null){
				throw new BusinessSystemException("第" + i + "条数据保存表存储配置时，请填写存储期限");
			}
		}
	}
}
