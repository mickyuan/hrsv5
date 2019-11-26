package hrds.b.biz.agent.dbagentconf.fileconf;

import com.alibaba.fastjson.JSONArray;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.exception.BusinessSystemException;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.resultset.Result;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.DataExtractType;
import hrds.commons.codes.FileFormat;
import hrds.commons.codes.IsFlag;
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
	@Return(desc = "查询结果集", range = "不为空")
	public Result getInitInfo(long colSetId){
		return Dbo.queryResult(" select ti.table_id, ti.table_name, ti.table_ch_name, ded.dbfile_format, " +
				" ded.data_extract_type, ded.row_separator, ded.database_separatorr, ded.database_code " +
				" from " + Table_info.TableName + " ti left join " + Data_extraction_def.TableName + " ded " +
				" on ti.table_id = ded.table_id where ti.database_id = ?", colSetId);
	}

	@Method(desc = "根据数据抽取方式返回卸数文件格式", logicStep = "" +
			"1、如果是仅做数据抽取，那么卸数文件格式为定长，非定长，CSV" +
			"2、如果是抽取并入库，那么卸数文件格式为非定长，CSV，ORC，PARQUET，SEQUENCEFILE")
	@Param(name = "extractType", desc = "数据抽取方式", range = "请从DataExtractType代码项取值")
	@Return(desc = "map集合", range = "key为文件格式名称，用于在下拉框中显示" +
			"value为文件格式code，用于保存时向后台接口传参")
	public Map<String, String> getFileFormatByExtractType(String extractType){
		DataExtractType dataExtractType = DataExtractType.ofEnumByCode(extractType);
		Map<String, String> formatMap = new HashMap<>();
		formatMap.put(FileFormat.FeiDingChang.getValue(), FileFormat.FeiDingChang.getCode());
		formatMap.put(FileFormat.CSV.getValue(), FileFormat.CSV.getCode());
		//1、如果是仅做数据抽取，那么卸数文件格式为定长，非定长，CSV
		if(dataExtractType == DataExtractType.JinShuJuChouQu){
			formatMap.put(FileFormat.DingChang.getValue(), FileFormat.DingChang.getCode());
		}
		//2、如果是抽取并入库，那么卸数文件格式为非定长，CSV，ORC，PARQUET，SEQUENCEFILE
		else{
			formatMap.put(FileFormat.ORC.getValue(), FileFormat.ORC.getCode());
			formatMap.put(FileFormat.PARQUET.getValue(), FileFormat.PARQUET.getCode());
			formatMap.put(FileFormat.SEQUENCEFILE.getValue(), FileFormat.SEQUENCEFILE.getCode());
		}
		return formatMap;
	}

	@Method(desc = "保存卸数文件配置", logicStep = "" +
			"1、将传入的json格式的字符串转换为List<Data_extraction_def>集合" +
			"2、遍历集合，对集合中的内容调用方法进行校验" +
			"3、根据table_id去data_extraction_def表中删除尝试删除该表曾经的卸数文件配置，不关心删除数目" +
			"4、保存数据")
	@Param(name = "extractionDefString", desc = "存有待保存信息的json格式字符串" +
			"注意：(1)、数据抽取方式请从DataExtractType代码项取值" +
			"(2)、数据字符集请从DataBaseCode代码项取值" +
			"(3)、抽取数据存储方式请从FileFormat代码项取值", range = "不为空")
	@Param(name = "colSetId", desc = "数据库采集设置表ID", range = "不为空")
	@Return(desc = "返回数据库设置ID，方便下一个页面能够通过这个参数加载初始化设置", range = "不为空")
	public long saveFileConf(String extractionDefString, long colSetId){
		//1、将传入的json格式的字符串转换为List<Data_extraction_def>集合
		List<Data_extraction_def> dataExtractionDefs = JSONArray.parseArray(extractionDefString, Data_extraction_def.class);
		//2、遍历集合，对集合中的内容调用方法进行校验
		verifySeqConf(dataExtractionDefs);
		for(Data_extraction_def def : dataExtractionDefs){
			//3、根据table_id去data_extraction_def表中删除尝试删除该表曾经的卸数文件配置，不关心删除数目
			Dbo.execute("delete from " + Data_extraction_def.TableName + " where table_id = ?", def.getTable_id());
			def.setDed_id(PrimayKeyGener.getNextId());
			//TODO data_extraction_def表is_header字段设置默认值为"是"，后期可能会修改
			def.setIs_header(IsFlag.Shi.getCode());
			def.add(Dbo.db());
		}
		//4、保存数据
		return colSetId;
	}

	@Method(desc = "在保存表分隔符设置的时候，传入实体，根据数据抽取存储方式，来校验其他的内容", logicStep = "" +
			"1、校验保存数据必须关联表" +
			"2、校验采集的方式如果是仅抽取" +
			"   2-1、文件格式如果是非定长，用户必须填写行分隔符和列分隔符" +
			"   2-2、文件格式如果是定长/CSV，那么行分隔符和列分隔符，用户可以填，可以不填" +
			"3、校验采集的方式如果是抽取并入库" +
			"   3-1、如果是ORC/PARQUET/SEQUENCEFILE，不允许用户填写行分隔符和列分隔符" +
			"   3-2、如果是TEXTFILE，则校验，用户必须填写行分隔符和列分隔符" +
			"   3-3、如果是CSV，则不进行校验，即如果用户不填写，就卸成标准CSV，否则，按照用户指定的列分隔符写文件" +
			"6、如果校验出现问题，直接抛出异常")
	@Param(name = "def", desc = "用于对待保存的数据进行校验", range = "数据抽取定义实体类对象")
	private void verifySeqConf(List<Data_extraction_def> dataExtractionDefs){
		for(int i = 0; i < dataExtractionDefs.size(); i++){
			Data_extraction_def def = dataExtractionDefs.get(i);
			//1、校验保存数据必须关联表
			if(def.getTable_id() == null){
				throw new BusinessSystemException("保存卸数文件配置，第"+ (i + 1) +"数据必须关联表ID");
			}
			//2、校验采集的方式如果是仅抽取
			DataExtractType extractType = DataExtractType.ofEnumByCode(def.getData_extract_type());
			FileFormat fileFormat = FileFormat.ofEnumByCode(def.getDbfile_format());
			if(extractType == DataExtractType.JinShuJuChouQu){
				//如果数据抽取方式是仅抽取，那么校验存储方式不能是ORC，PARQUET，SEQUENCEFILE
				if(fileFormat == FileFormat.ORC || fileFormat == FileFormat.PARQUET || fileFormat == FileFormat.SEQUENCEFILE){
					throw new BusinessSystemException("仅抽取操作，只能指定非定长|定长|CSV三种存储格式");
				}
				//2-1、文件格式如果是非定长，用户必须填写行分隔符和列分隔符
				if(fileFormat == FileFormat.FeiDingChang){
					if(StringUtil.isEmpty(def.getRow_separator())){
						throw new BusinessSystemException("数据抽取保存为非定长文件，请填写行分隔符");
					}
					if(StringUtil.isEmpty(def.getDatabase_separatorr())){
						throw new BusinessSystemException("数据抽取保存为非定长文件，请填写列分隔符");
					}
				}
				//2-2、文件格式如果是定长/CSV，那么行分隔符和列分隔符，用户可以填，可以不填
			}
			//3、校验采集的方式如果是抽取并入库
			if(extractType == DataExtractType.ShuJuChouQuJiRuKu){
				//如果数据抽取方式是抽取及入库，那么校验存储方式不能是定长
				if(fileFormat == FileFormat.DingChang){
					throw new BusinessSystemException("抽取并入库操作，不能保存为定长文件");
				}
				//3-1、如果是ORC/PARQUET/SEQUENCEFILE，不允许用户填写行分隔符和列分隔符
				if(fileFormat == FileFormat.ORC || fileFormat == FileFormat.PARQUET || fileFormat == FileFormat.SEQUENCEFILE){
					if(StringUtil.isNotEmpty(def.getRow_separator())){
						throw new BusinessSystemException("数据抽取并入库，保存格式为ORC/PARQUET/SEQUENCEFILE，不能指定行分隔符");
					}
					if(StringUtil.isNotEmpty(def.getDatabase_separatorr())){
						throw new BusinessSystemException("数据抽取并入库，保存格式为ORC/PARQUET/SEQUENCEFILE，不能指定列分隔符");
					}
				}
				//3-2、如果是非定长，则校验，用户必须填写行分隔符和列分隔符
				if(fileFormat == FileFormat.FeiDingChang){
					if(StringUtil.isEmpty(def.getRow_separator())){
						throw new BusinessSystemException("数据抽取并入库，保存格式为非定长，请指定行分隔符");
					}
					if(StringUtil.isEmpty(def.getDatabase_separatorr())){
						throw new BusinessSystemException("数据抽取并入库，保存格式为非定长，请指定列分隔符");
					}
				}
				//3-3、如果是CSV，则不进行校验，即如果用户不填写，就卸成标准CSV，否则，按照用户指定的列分隔符写文件
			}
		}
	}
}
