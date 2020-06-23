package hrds.b.biz.agent.dbagentconf.fileconf;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import fd.ng.web.util.Dbo;
import hrds.b.biz.agent.datafileconf.CheckParam;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.DataExtractType;
import hrds.commons.codes.FileFormat;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Data_extraction_def;
import hrds.commons.entity.Etl_dependency;
import hrds.commons.entity.Etl_job_def;
import hrds.commons.entity.Etl_job_resource_rela;
import hrds.commons.entity.Table_info;
import hrds.commons.entity.Take_relation_etl;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.key.PrimayKeyGener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "定义卸数文件配置", author = "WangZhengcheng")
public class FileConfStepAction extends BaseAction {

	@Method(desc = "根据数据库设置ID获得定义卸数文件页面初始信息", logicStep = "" + "1、根据数据库设置ID去数据库中查询与数据抽取相关的信息")
	@Param(name = "colSetId", desc = "数据库设置ID，源系统数据库设置表主键，数据库对应表外键", range = "不为空")
	@Return(desc = "查询结果集", range = "不为空")
	public List<Map<String, Object>> getInitInfo(long colSetId) {

		List<Map<String, Object>> table_infos =
			Dbo.queryList(
				"SELECT table_id,table_name,table_ch_name FROM "
					+ Table_info.TableName
					+ " WHERE database_id = ?",
				colSetId);

		table_infos.forEach(
			tableInfo -> {
				List<Map<String, Object>> list =
					Dbo.queryList(
						" select * "
							+ " from "
							+ Table_info.TableName
							+ " ti left join "
							+ Data_extraction_def.TableName
							+ " ded "
							+ " on ti.table_id = ded.table_id where ti.database_id = ? AND ti.table_id = ? ",
						colSetId,
						(long) tableInfo.get("table_id"));
				list.forEach(
					item -> {
                /*
                把unicode转换为字符串
                1: 行分隔符  2: 数据分隔符
                 */
						//            1: 行分隔符
						if (item.get("row_separator") != null) {
							item.put(
								"row_separator",
								StringUtil.unicode2String(String.valueOf(item.get("row_separator"))));
						}
						//            2: 数据分隔符
						if (item.get("database_separatorr") != null) {
							item.put(
								"database_separatorr",
								StringUtil.unicode2String(String.valueOf(item.get("database_separatorr"))));
						}
					});
				tableInfo.put("tableData", list);
			});

		return table_infos;
	}


	@Method(
		desc = "保存卸数文件配置",
		logicStep =
			""
				+ "1、将传入的json格式的字符串转换为List<Data_extraction_def>集合"
				+ "2、遍历集合，对集合中的内容调用方法进行校验"
				+ "3、根据table_id去data_extraction_def表中删除尝试删除该表曾经的卸数文件配置，不关心删除数目"
				+ "4、保存数据")
	@Param(
		name = "extractionDefString",
		desc = "1: 检查当前传入的数据是否为空" + "2: 删除上次任务的卸数配置信息" + "3: 循环卸数配置信息并进行保存",
		range =
			"每张表的抽取方式(dbfile_format)为多种, 抽取方式为"
				+ "非定长,定长,CSV : 请传递数数据 换行符(row_separator),数据分隔符(database_separatorr),数据字符集(database_code),落地目录(plane_url);"
				+ "ORC,PARQUET,SEQUENCEFILE : 请传递数据 数据字符集(database_code),落地目录(plane_url); "
				+ "举例假如数据格式有俩种(非定长,ORC)则数据格式如下: "
				+ "[{表名ID:A,抽取方式:非定长,换行符:xxx,数据分隔符:xxx,数据字符集:xxx,落地目录:xxx},"
				+ "{表名ID:A,抽取方式:ORC,数据字符集:xxx,落地目录:xxx}"
				+ "],有几种抽取方式就有几个值",
		isBean = true)
	@Param(name = "colSetId", desc = "数据库采集设置表ID", range = "不为空")
	@Param(name = "dedId", desc = "表数据抽取定义主键信息", range = "可为空,为空表示没有要删除的表抽取数据信息...不为空时,多个参数使用 ^ 分割", nullable = true, valueIfNull = "")
	@Return(desc = "返回数据库设置ID，方便下一个页面能够通过这个参数加载初始化设置", range = "不为空")
	public long saveFileConf(Data_extraction_def[] extractionDefString, long colSetId, String dedId) {

		//    1: 检查当前传入的数据是否为空
		if (extractionDefString == null || extractionDefString.length == 0) {
			throw new BusinessException("未获取到卸数文件信息");
		}

		//如果存在要删除的表数据抽取信息,则删除
		if (StringUtil.isNotBlank(dedId)) {
			List<String> split = StringUtil.split(dedId, "^");
			deleteDataExtractionDef(split);
		}

		// 2: 删除上次任务的卸数配置信息
		Dbo.execute(
			"delete from "
				+ Data_extraction_def.TableName
				+ " where table_id in (select table_id from " + Table_info.TableName + " where database_id = ?)",
			colSetId);

		//    3: 循环卸数配置信息并进行保存
//	    verifySeqConf(extractionDefString);
			for (int i = 0; i < extractionDefString.length; i++) {

			Data_extraction_def def = extractionDefString[i];

			def.setDed_id(PrimayKeyGener.getNextId());
			// 校验保存数据必须关联表
			if (def.getTable_id() == null) {
				throw new BusinessException("保存卸数文件配置，第" + (i + 1) + "数据必须关联表ID");
			} else {
				updateOldDedId(def.getDed_id(), def.getTable_id());
			}
			// 将卸数分隔符(行，列)转换为unicode码
			if (StringUtil.isBlank(def.getDbfile_format())) {
				throw new BusinessException("第 " + (i + 1) + " 条的数据抽取方式不能为空!");
			}

			FileFormat fileFormat = FileFormat.ofEnumByCode(def.getDbfile_format());
			if (fileFormat == FileFormat.DingChang
				|| fileFormat == FileFormat.FeiDingChang) {
				// 检查行分隔符不能为空
				String row_separator = def.getRow_separator();
				if (StringUtil.isBlank(row_separator)) {
					throw new BusinessException("第 " + (i + 1) + " 条的数据行分割符不能为空!");
				} else {
					// 如果不为空则使用Unicode进行转码入库
					def.setRow_separator(StringUtil.string2Unicode(row_separator));
				}
				// 检查数据分隔符不能为空
				String database_separatorr = def.getDatabase_separatorr();
				if (StringUtil.isBlank(database_separatorr)) {
					throw new BusinessException("第 " + (i + 1) + " 条的数据分隔符不能为空!");
				} else {
					// 如果不为空则使用Unicode进行转码入库
					def.setDatabase_separatorr(StringUtil.string2Unicode(database_separatorr));
				}
			}
			// 如果是一下文件格式,不需要数据的分隔符和数据的行分隔符...这里卸数的时候使用的标准的文件格式
			else if (fileFormat == FileFormat.ORC
				|| fileFormat == FileFormat.SEQUENCEFILE
				|| fileFormat == FileFormat.PARQUET
				|| fileFormat == FileFormat.CSV) {
				def.setDatabase_separatorr("");
				def.setRow_separator("");
			} else {
				throw new BusinessException("传递的数据文件格式不存在在代码项中,此次获取的数据文件格式是: " + def.getDbfile_format());
			}

			if (StringUtil.isBlank(def.getDatabase_code())) {
				throw new BusinessException("第 " + (i + 1) + " 条的数据字符集不能为空!");
			}

			if (StringUtil.isBlank(def.getPlane_url())) {
				throw new BusinessException("第 " + (i + 1) + " 条的数据落地目录不能为空!");
			}

			// TODO data_extraction_def表is_header字段设置默认值为"否"，后期可能会修改
			def.setIs_header(IsFlag.Fou.getCode());
			def.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
			def.add(Dbo.db());
		}
		// 4、保存数据
		return colSetId;
	}

	@Method(desc = "将旧的ded_id更新为此次新生成的", logicStep = "不关心更新条数,直接进行更新")
	@Param(name = "table_id", desc = "表的ID", range = "不可为空")
	@Param(name = "newDedId", desc = "新生成的ded_id", range = "不可为空")
	private void updateOldDedId(long newDedId, long table_id) {
		//更新抽数作业关系表的ded_id信息
		Dbo.execute("UPDATE " + Take_relation_etl.TableName + " SET ded_id = ? WHERE ded_id in("
			+ "SELECT ded_id FROM "
			+ Data_extraction_def.TableName
			+ " WHERE table_id = ?)", newDedId, table_id);
	}

	@Method(
		desc = "在保存表分隔符设置的时候，传入实体，根据数据抽取存储方式，来校验其他的内容",
		logicStep =
			""
				+ "1、校验保存数据必须关联表"
				+ "2、校验采集的方式如果是仅抽取"
				+ "   2-1、文件格式如果是非定长，用户必须填写行分隔符和列分隔符"
				+ "   2-2、文件格式如果是定长/CSV，那么行分隔符和列分隔符，用户可以填，可以不填"
				+ "3、校验采集的方式如果是抽取并入库"
				+ "   3-1、如果是ORC/PARQUET/SEQUENCEFILE，不允许用户填写行分隔符和列分隔符"
				+ "   3-2、如果是TEXTFILE，则校验，用户必须填写行分隔符和列分隔符"
				+ "   3-3、如果是CSV，则不进行校验，即如果用户不填写，就卸成标准CSV，否则，按照用户指定的列分隔符写文件"
				+ "6、如果校验出现问题，直接抛出异常")
	@Param(name = "def", desc = "用于对待保存的数据进行校验", range = "数据抽取定义实体类对象")
	private void verifySeqConf(Data_extraction_def[] dataExtractionDefs) {
		for (int i = 0; i < dataExtractionDefs.length; i++) {
			Data_extraction_def def = dataExtractionDefs[i];
			// 1、校验保存数据必须关联表
			if (def.getTable_id() == null) {
				throw new BusinessException("保存卸数文件配置，第" + (i + 1) + "数据必须关联表ID");
			}
			// 2、校验采集的方式如果是仅抽取
			DataExtractType extractType = DataExtractType.ofEnumByCode(def.getData_extract_type());
			FileFormat fileFormat = FileFormat.ofEnumByCode(def.getDbfile_format());
			if (extractType == DataExtractType.ShuJuKuChouQuLuoDi) {
				// 如果数据抽取方式是仅抽取，那么校验存储方式不能是ORC，PARQUET，SEQUENCEFILE
				if (fileFormat == FileFormat.ORC
					|| fileFormat == FileFormat.PARQUET
					|| fileFormat == FileFormat.SEQUENCEFILE) {
					throw new BusinessException("仅抽取操作，只能指定非定长|定长|CSV三种存储格式");
				}
				// 2-1、文件格式如果是非定长，用户必须填写行分隔符和列分隔符
				if (fileFormat == FileFormat.FeiDingChang) {
					if (StringUtil.isEmpty(def.getRow_separator())) {
						throw new BusinessException("数据抽取保存为非定长文件，请填写行分隔符");
					}
					if (StringUtil.isEmpty(def.getDatabase_separatorr())) {
						throw new BusinessException("数据抽取保存为非定长文件，请填写列分隔符");
					}
				}
				// 2-2、文件格式如果是定长/CSV，那么行分隔符和列分隔符，用户可以填，可以不填
			}
			// 3、校验采集的方式如果是抽取并入库
			//      if (extractType == DataExtractType.ShuJuChouQuJiRuKu) {
			//        // 如果数据抽取方式是抽取及入库，那么校验存储方式不能是定长
			//        if (fileFormat == FileFormat.DingChang) {
			//          throw new BusinessException("抽取并入库操作，不能保存为定长文件");
			//        }
			//        // 3-1、如果是ORC/PARQUET/SEQUENCEFILE，不允许用户填写行分隔符和列分隔符
			//        if (fileFormat == FileFormat.ORC
			//            || fileFormat == FileFormat.PARQUET
			//            || fileFormat == FileFormat.SEQUENCEFILE) {
			//          if (StringUtil.isNotEmpty(def.getRow_separator())) {
			//            throw new BusinessException("数据抽取并入库，保存格式为ORC/PARQUET/SEQUENCEFILE，" +
			// "不能指定行分隔符");
			//          }
			//          if (StringUtil.isNotEmpty(def.getDatabase_separatorr())) {
			//            throw new BusinessException("数据抽取并入库，保存格式为ORC/PARQUET/SEQUENCEFILE，" +
			// "不能指定列分隔符");
			//          }
			//        }
			//        // 3-2、如果是非定长，则校验，用户必须填写行分隔符和列分隔符
			//        if (fileFormat == FileFormat.FeiDingChang) {
			//          if (StringUtil.isEmpty(def.getRow_separator())) {
			//            throw new BusinessException("数据抽取并入库，保存格式为非定长，请指定行分隔符");
			//          }
			//          if (StringUtil.isEmpty(def.getDatabase_separatorr())) {
			//            throw new BusinessException("数据抽取并入库，保存格式为非定长，请指定列分隔符");
			//          }
			//        }
			//        // 3-3、如果是CSV，则不进行校验，即如果用户不填写，就卸成标准CSV，否则，按照用户指定的列分隔符写文件
			//      }
		}
	}

	@Method(desc = "删除表的抽取定义数据信息", logicStep = "")
	@Param(name = "dedId", desc = "抽取定义表的主键信息", range = "不可为空")
	private void deleteDataExtractionDef(List<String> dedId) {

		for (int i = 0; i < dedId.size(); i++) {
			long ded_id = Long.parseLong(dedId.get(i));
			long countNum = Dbo
				.queryNumber("SELECT COUNT(1) FROM " + Data_extraction_def.TableName + " WHERE ded_id = ?",
					ded_id)
				.orElseThrow(() -> new BusinessException("SQL查询异常"));
			if (countNum == 0) {
				CheckParam.throwErrorMsg("数据抽取定义方式不存在");
			}

			//获取删除表抽取定义的数据信息
			Map<String, Object> map = Dbo.queryOneObject("SELECT etl_job,etl_sys_cd,sub_sys_cd FROM "
					+ Data_extraction_def.TableName
					+ " t1 join "
					+ Take_relation_etl.TableName
					+ " t2 on t1.ded_id = t2.ded_id WHERE t1.ded_id = ?",
				ded_id);

			//删除表的定义作业数据信息
			Dbo.execute("DELETE FROM " + Etl_job_def.TableName + " WHERE etl_job = ( SELECT etl_job FROM "
					+ Data_extraction_def.TableName
					+ " t1 join "
					+ Take_relation_etl.TableName
					+ " t2 on t1.ded_id = t2.ded_id WHERE t1.ded_id = ?) AND etl_sys_cd = ?",
				ded_id, map.get("etl_sys_cd"));

			//删除依赖的作业信息
			Dbo.execute("DELETE FROM " + Etl_dependency.TableName + " WHERE pre_etl_job = ( SELECT etl_job FROM "
					+ Data_extraction_def.TableName
					+ " t1 join "
					+ Take_relation_etl.TableName
					+ " t2 on t1.ded_id = t2.ded_id WHERE t1.ded_id = ?) AND pre_etl_sys_cd = ? ",
				ded_id, map.get("etl_sys_cd"));

			//删除作业的资源信息
			Dbo.execute("DELETE FROM " + Etl_job_resource_rela.TableName + " WHERE etl_job = ( SELECT etl_job FROM "
					+ Data_extraction_def.TableName
					+ " t1 join "
					+ Take_relation_etl.TableName
					+ " t2 on t1.ded_id = t2.ded_id WHERE t1.ded_id = ?) AND etl_sys_cd = ?",
				ded_id, map.get("etl_sys_cd"));

			//删除表抽取定义的表信息
			Dbo.execute("DELETE FROM " + Data_extraction_def.TableName + " WHERE ded_id = ?",
				ded_id);
			//删除抽取定义表的作业配置信息
			Dbo.execute("DELETE FROM " + Take_relation_etl.TableName + " WHERE ded_id = ?",
				ded_id);
		}

	}
}
