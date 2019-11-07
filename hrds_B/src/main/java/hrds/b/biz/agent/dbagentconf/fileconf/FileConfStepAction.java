package hrds.b.biz.agent.dbagentconf.fileconf;

import com.alibaba.fastjson.JSONArray;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
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
				"ded.row_separator, ded.database_separatorr, ded.database_code " +
				" from " + Table_info.TableName + " ti left join " + Data_extraction_def.TableName + " ded " +
				" on ti.table_id = ded.table_id where ti.database_id = ? ", colSetId);
		returnMap.put("fileConf", result);
		returnMap.put("totalSize", page.getTotalSize());

		return returnMap;
	}

	/*
	 * 根据数据库设置ID回显所有表分隔符设置
	public Result getAllTbSepConf(long colSetId){
		return null;
	}
	 * */

	/*
	 * 保存所有表分隔符设置
	public void saveAllTbSepConf(){

	}
    * */
	@Method(desc = "保存定义卸数文件配置", logicStep = "" +
			"1、将页面传过来的JSON字符串解析为List集合" +
			"2、遍历集合，给每一个对象生成主键等信息" +
			"3、保存数据，并返回数据库设置ID")
	@Param(name = "colSetId", desc = "数据库设置ID，源系统数据库设置表主键，数据库对应表外键", range = "不为空")
	@Param(name = "fileConfString", desc = "页面配置信息(JSON格式)", range = "能够被反序列化为数据抽取定义实体对象")
	@Return(desc = "数据库设置ID", range = "便于下一个页面通过传递这个值，查询到之前设置的信息")
	public long saveFileConf(long colSetId, String fileConfString){
		List<Data_extraction_def> dataExtractionDefs = JSONArray.parseArray(fileConfString, Data_extraction_def.class);
		for(Data_extraction_def dataExtractionDef : dataExtractionDefs){
			dataExtractionDef.setDed_id(PrimayKeyGener.getNextId());
			//TODO 定义卸数文件页面，没有体现是否仅抽取还是抽取并入库
			//TODO 定义卸数文件页面，是否定义表头字段值如何设置
			//TODO 数据库中，落地文件格式不全，和页面上抽取数据存储方式不匹配
			dataExtractionDef.add(Dbo.db());
		}
		return colSetId;
	}
}
