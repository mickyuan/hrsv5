package hrds.b.biz.agent.dbagentconf.fileconf;

import com.alibaba.fastjson.JSONArray;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.exception.BusinessSystemException;
import fd.ng.db.jdbc.DefaultPageImpl;
import fd.ng.db.jdbc.Page;
import fd.ng.db.resultset.Result;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.entity.Data_extraction_def;
import hrds.commons.entity.Table_info;
import hrds.commons.utils.key.PrimayKeyGener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "定义卸数文件配置", author = "WangZhengcheng")
public class FileConfStepAction extends BaseAction{

	@Method(desc = "根据数据库设置ID获得定义卸数文件页面初始信息", logicStep = "" +
			"1、根据数据库设置ID去数据库中查询与数据抽取相关的信息")
	@Param(name = "colSetId", desc = "数据库设置ID，源系统数据库设置表主键，数据库对应表外键", range = "不为空")
	@Param(name = "currPage", desc = "分页当前页", range = "大于0的正整数", nullable = true, valueIfNull = "1")
	@Param(name = "pageSize", desc = "分页查询每页显示条数", range = "大于0的正整数", nullable = true, valueIfNull = "10")
	@Return(desc = "查询结果集", range = "不为空")
	public Map<String, Object> getInitInfo(long colSetId, int currPage, int pageSize){
		Map<String, Object> returnMap = new HashMap<>();
		Page page = new DefaultPageImpl(currPage, pageSize);
		Result result = Dbo.queryPagedResult(page, " select ti.table_id, ti.table_name, ti.table_ch_name, ded.dbfile_format, " +
				" ded.row_separator, ded.database_separatorr, ded.database_code " +
				" from " + Table_info.TableName + " ti left join " + Data_extraction_def.TableName + " ded " +
				" on ti.table_id = ded.table_id where ti.database_id = ?", colSetId);
		returnMap.put("fileConf", result.toList());
		returnMap.put("totalSize", page.getTotalSize());

		return returnMap;
	}

	@Method(desc = "保存卸数文件配置", logicStep = "" +
			"1、将传入的json格式的字符串转换为List<Data_extraction_def>集合" +
			"2、遍历集合，对集合中的内容调用方法进行校验" +
			"3、根据table_id去data_extraction_def表中删除尝试删除该表曾经的卸数文件配置，不关心删除数目" +
			"4、保存数据")
	@Param(name = "extractionDefString", desc = "存有待保存信息的json格式字符串" +
			"注意：(1)、数据抽取方式请从DataExtractType代码项取值" +
			"(2)、数据抽取落地编码请从DataBaseCode代码项取值" +
			"(3)、数据落地格式请从HiveStorageType代码项取值", range = "不为空")
	@Param(name = "colSetId", desc = "", range = "")
	@Return(desc = "", range = "")
	public long saveFileConf(String extractionDefString, long colSetId){
		//1、将传入的json格式的字符串转换为List<Data_extraction_def>集合
		List<Data_extraction_def> dataExtractionDefs = JSONArray.parseArray(extractionDefString, Data_extraction_def.class);
		//2、遍历集合，对集合中的内容调用方法进行校验
		verifySeqConf(dataExtractionDefs);
		for(Data_extraction_def def : dataExtractionDefs){
			//3、根据table_id去data_extraction_def表中删除尝试删除该表曾经的卸数文件配置，不关心删除数目
			Dbo.execute("delete from " + Data_extraction_def.TableName + " where table_id = ?", def.getTable_id());
			def.setDed_id(PrimayKeyGener.getNextId());

			def.add(Dbo.db());
		}
		//4、保存数据
		return colSetId;
	}

	@Method(desc = "在保存表分隔符设置的时候，传入实体，根据数据抽取存储方式，来校验其他的内容", logicStep = "" +
			"1、校验保存数据必须关联表" +
			"2、如果存储的格式是ORC/PARQUET/SEQUENCEFILE，则行分隔符、列分隔符不能填写，字符编码必须填写" +
			"3、如果存储的格式是定长/非定长，则行分隔符、列分隔符、字符编码必须填写" +
			"4、校验数据抽取方式是否合法" +
			"5、校验是否需要表头字段是否合法" +
			"6、如果校验出现问题，直接抛出异常")
	@Param(name = "def", desc = "用于对待保存的数据进行校验", range = "数据抽取定义实体类对象")
	private void verifySeqConf(List<Data_extraction_def> dataExtractionDefs){
		for(int i = 0; i < dataExtractionDefs.size(); i++){
			//1、校验保存数据必须关联表
			if(dataExtractionDefs.get(i).getTable_id() == null){
				throw new BusinessSystemException("保存卸数文件配置，第"+ (i + 1) +"数据必须关联表ID");
			}
			//2、如果存储的格式是ORC/PARQUET/SEQUENCEFILE，则行分隔符、列分隔符不能填写，字符编码必须填写
		}

	}

}
