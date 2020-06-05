package hrds.b.biz.agent.datafileconf.datatransfer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import fd.ng.web.util.Dbo;
import hrds.b.biz.agent.datafileconf.CheckParam;
import hrds.b.biz.agent.tools.SendMsgUtil;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.DataExtractType;
import hrds.commons.codes.FileFormat;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Agent_info;
import hrds.commons.entity.Data_extraction_def;
import hrds.commons.entity.Database_set;
import hrds.commons.entity.Table_info;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.AgentActionUtil;
import hrds.commons.utils.key.PrimayKeyGener;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@DocClass(desc = "数据转存配置管理", author = "Mr.Lee", createdate = "2020-04-19 14:15")
public class DataTransferAction extends BaseAction {

	// 日志打印
	//  private static final Log logger = LogFactory.getLog(DictionaryTableAction.class);

	@Method(
		desc = "数据转存初始化数据获取",
		logicStep =
			""
				+ "1: 检查当前任务是否存在 "
				+ "2: 获取每张表的转存配置信息"
				+ "   2-1: 对数据中的行分隔符进行转换数据库中存储的是Unicode"
				+ "   2-2: 对数据中的,列分隔符进行转换,数据库存储的是Unicode"
				+ "3: 如果数据库的数据是空的,则使用数据字典解析后的xml做为数据集"
				+ "   3-1: 对数据中的行分隔符进行转换数据字典解析的xml中的是Unicode"
				+ "   3-2: 对数据中的,列分隔符进行转换,数据字典解析的xml中的是Unicode")
	@Param(name = "colSetId", desc = "采集任务ID", range = "不可为空")
	@Return(desc = "返回表的数据转存配置信息", range = "可以为空")
	public List<Map<String, Object>> getInitDataTransfer(long colSetId) {

		//  1: 检查当前任务是否存在
		long countNum =
			Dbo.queryNumber(
				"SELECT COUNT(1) FROM " + Database_set.TableName + " WHERE database_id = ?",
				colSetId)
				.orElseThrow(() -> new BusinessException("SQL查询错误"));
		if (countNum == 0) {
			CheckParam.throwErrorMsg("采集任务( %s ), 不存在", colSetId);
		}

		countNum =
			Dbo.queryNumber(
				"SELECT COUNT(1) FROM " + Table_info.TableName + " WHERE database_id = ?", colSetId)
				.orElseThrow(() -> new BusinessException("SQL查询错误"));
		if (countNum == 0) {
			CheckParam.throwErrorMsg("采集任务( %s ), 不存在表信息", colSetId);
		}

		// 2: 获取每张表的转存配置信息
		List<Map<String, Object>> dataBaseTransDataList =
			Dbo.queryList(
				"  SELECT t1.database_id,t1.table_id,t1.table_name,t1.table_ch_name, is_archived FROM "
					+ Table_info.TableName
					+ " t1 "
					+ " LEFT JOIN "
					+ Data_extraction_def.TableName
					+ " t2 ON t1.table_id = t2.table_id WHERE "
					+ " t1.database_id = ? ORDER BY t1.table_name",
				colSetId);

		// 获取xml的数据结果集
		List<Map<String, Object>> xmlDataTransfer = getDataTransfer(colSetId);

		// 获取数据库存在的表对应抽取配置信息
		dataBaseTransDataList.forEach(
			databaseItemMap -> {
				// 这里只能存在一条,因为只支持一种文件的抽取定义
				Map<String, Object> extractionMap =
					Dbo.queryOneObject(
						"SELECT t1.* FROM "
							+ Data_extraction_def.TableName
							+ " t1 JOIN "
							+ Table_info.TableName
							+ " t2 ON t1.table_id = t2.table_id "
							+ " WHERE t2.database_id = ? AND  t2.table_id = ?",
						colSetId,
						//         data_extract_type = ? AND
						// DataExtractType.YuanShuJuGeShi.getCode(),
						databaseItemMap.get("table_id"));

          /*
           这里的如果和数据字典里面定义的相同,则优先将数据库定义的放在第一个位置
          */
				xmlDataTransfer.forEach(
					itemMap -> {
						// 如果数据库表名在数据字典的xml中出现,则处理抽取定义的数据
						if (itemMap.get("table_name").equals(databaseItemMap.get("table_name"))) {
							//                  List<Map<String, Object>> storageList =
							//                      (List<Map<String, Object>>) itemMap.get("storage");
							List<Map<String, Object>> storageList =
								JSON.parseObject(
									itemMap.get("storage").toString(),
									new TypeReference<List<Map<String, Object>>>() {
									});

							if (storageList.isEmpty()) {
								CheckParam.throwErrorMsg("表(%s)下的抽取方式为空,请检查确认!!!", itemMap.get("table_name"));
							}

							if (!extractionMap.isEmpty()) {
								Iterator<Map<String, Object>> iterator = storageList.iterator();
								while (iterator.hasNext()) {
									Map<String, Object> storageMap = iterator.next();

									// 如果数据库里面的抽取文件类型和数据字典中的一致则将数据库记录的类容修改为数据文件中最新的,
									// 这样在保存的时候会修正为数据字典的文件路径等, 并将数据字典的数据删除掉,
									// 把修改后的数据库记录放入到对应表的抽取定义下
									String storageFormat = String.valueOf(storageMap.get("dbfile_format"));

									if (storageFormat.equals(extractionMap.get("dbfile_format"))) {
										extractionMap.put("is_header", storageMap.get("is_header"));
										extractionMap.put(
											"database_separator",
											StringUtil.unicode2String(
												String.valueOf(storageMap.get("database_separator"))));
										extractionMap.put("plane_url", storageMap.get("plane_url"));
										extractionMap.put("database_code", storageMap.get("database_code"));
										extractionMap.put(
											"row_separator", String.valueOf(storageMap.get("row_separator")));
										iterator.remove();
										break;
									}
								}

								// 使用数据库中的数据
								storageList.add(0, extractionMap);
								itemMap.put("storage", storageDataFormat(storageList));
							} else {
								itemMap.put("storage", storageDataFormat(storageList));
							}
							itemMap.put("table_name", databaseItemMap.get("table_name"));
							itemMap.put("ded_id", extractionMap.get("ded_id"));
							itemMap.put("table_ch_name", databaseItemMap.get("table_ch_name"));
							itemMap.put("table_id", databaseItemMap.get("table_id"));
							//这里的是否转存以数据库查询的为标准
							itemMap.put("is_archived", databaseItemMap.get("is_archived"));
						}
					});
			});

		return xmlDataTransfer;
		//    }
	}

	@Method(desc = "将数据中的Unicode转换为字符串", logicStep = "Unicode数据转换")
	@Param(name = "storageData", desc = "需要转换的List数据", range = "不可为空")
	private List<Map<String, Object>> storageDataFormat(List<Map<String, Object>> storageData) {

		storageData.forEach(
			itemMap -> {
				// 2-1: 对数据中的行分隔符进行转换 Unicode-->String
				if (StringUtil.isNotBlank(String.valueOf(itemMap.get("row_separator")))) {
					String separator =
						StringUtil.unicode2String(String.valueOf(itemMap.get("row_separator")));
					if ("\r\n".equals(separator)) {
						itemMap.put("row_separator", "\\r\\n");
					} else if ("\n".equals(separator)) {
						itemMap.put("row_separator", "\\n");
					} else if ("\r".equals(separator)) {
						itemMap.put("row_separator", "\\r");
					} else {
						itemMap.put("row_separator", "");
					}
				}
				// 2-2: 对数据中的,列分隔符进行转换 Unicode-->String
				if (StringUtil.isNotBlank(String.valueOf(itemMap.get("database_separatorr")))) {
					itemMap.put(
						"database_separatorr",
						StringUtil.unicode2String(String.valueOf(itemMap.get("database_separatorr"))));
				} else {
					itemMap.put("database_separatorr", "");
				}
			});

		return storageData;
	}

	@Method(desc = "根据colSetId去数据库中查出DB连接信息", logicStep = "1、根据colSetId和userId去数据库中查出DB连接信息")
	@Param(name = "colSetId", desc = "采集任务ID", range = "不为空")
	@Return(desc = "查询结果集", range = "不为空")
	private List<Map<String, Object>> getDataTransfer(long colSetId) {

		Map<String, Object> databaseInfo = getDatabaseSetInfo(colSetId);

		String respMsg =
			SendMsgUtil.getAllTableName(
				(long) databaseInfo.get("agent_id"),
				getUserId(),
				databaseInfo,
				AgentActionUtil.GETALLTABLESTORAGE);

		return JSON.parseObject(respMsg, new TypeReference<List<Map<String, Object>>>() {
		});
	}

	@Method(desc = "根据colSetId去数据库中查出DB连接信息", logicStep = "1、根据colSetId和userId去数据库中查出DB连接信息")
	@Param(name = "colSetId", desc = "采集任务ID", range = "不为空")
	@Return(desc = "查询结果集", range = "不为空")
	private Map<String, Object> getDatabaseSetInfo(long colSetId) {

		long databaseNum =
			Dbo.queryNumber(
				"SELECT COUNT(1) FROM " + Database_set.TableName + " WHERE database_id = ?",
				colSetId)
				.orElseThrow(() -> new BusinessException("SQL查询异常"));
		if (databaseNum == 0) {
			CheckParam.throwErrorMsg("任务(%s)不存在!!!", colSetId);
		}
		// 1、根据colSetId和userId去数据库中查出DB连接信息
		return Dbo.queryOneObject(
			" SELECT t1.database_type, t1.database_ip, t1.database_port, t1.database_name, "
				+ " t1.database_pad, t1.user_name, t1.database_drive, t1.jdbc_url, t1.agent_id, t1.db_agent, t1.plane_url"
				+ " FROM "
				+ Database_set.TableName
				+ " t1"
				+ " LEFT JOIN "
				+ Agent_info.TableName
				+ " ai ON ai.agent_id = t1.agent_id"
				+ " WHERE t1.database_id = ? and ai.user_id = ? ",
			colSetId,
			getUserId());
	}

	@Method(
		desc = "保存数据转存配置信息",
		logicStep =
			""
				+ "1: 检查当前任务是否存在,如果不存在则抛出异常 "
				+ "2: 保存表的数据转存配置信息"
				+ "   2-1: 如果存在数据转存的ID信息,则表示为编辑状态"
				+ "   2-2: 没有ID信息存在则视为新增")
	@Param(name = "colSetId", desc = "采集任务ID", range = "不可为空")
	@Param(
		name = "dataExtractionDefs",
		desc = "数据转存的数据数组字符串",
		range = "不可为空",
		isBean = true,
		example =
			"数据格式如:["
				+ "{'ded_id' : 1000012176,'table_id' : 1000012174,'data_extract_type' : 1,'is_header' : 1,"
				+ "'database_code' : 1,'row_separator' : '\r\n','database_separatorr' : '&','dbfile_format' : 0,'"
				+ "plane_url' : '/home/hyshf','file_suffix' : '','ded_remark' : ''}"
				+ "],数组里面的对象为必传项...其中 ded_id如果没有值可以被忽略...切记没有是千万不要写 ded_id:'' ")
	@Param(
		name = "tableInfos",
		desc = "表的信息,这里的表中文名可能发生改变",
		range = "可以为空",
		isBean = true,
		example =
			"数据格式如:["
				+ "{'table_id' : 1000012174,'table_name' : 'sys_para','table_ch_name' : '系统参数表'}"
				+ "],数组里面的对象为必传项...",
		nullable = true)
	@Return(desc = "返回采集任务的ID", range = "不可为空")
	public long saveDataTransferData(
		long colSetId, Data_extraction_def[] dataExtractionDefs, Table_info[] tableInfos) {

		// 1: 检查当前任务是否存在,如果不存在则抛出异常
		long countNum =
			Dbo.queryNumber(
				"SELECT count(*) FROM " + Database_set.TableName + " WHERE database_id = ?", colSetId)
				.orElseThrow(() -> new BusinessException("SQL查询错误"));
		if (countNum == 0) {
			CheckParam.throwErrorMsg("采集任务ID(%s), 不存在", colSetId);
		}

		// 更新表的中文信息
		for (Table_info tableInfo : tableInfos) {
			Dbo.execute(
				"UPDATE "
					+ Table_info.TableName
					+ "  SET table_ch_name = ? WHERE table_id = ? AND database_id = ?",
				tableInfo.getTable_ch_name(),
				tableInfo.getTable_id(),
				colSetId);
		}
		// 记录是第几张表配置，防止出现未配置参数出现的错误提示不明确
		int index = 1;
		for (Data_extraction_def dataExtractionDef : dataExtractionDefs) {
			CheckParam.checkData(
				"第(%s)张表,表关联的表ID不能为空", String.valueOf(dataExtractionDef.getTable_id()), index);
			CheckParam.checkData("第(%s)张表,是否需要表头不能为空", dataExtractionDef.getIs_header(), index);
			CheckParam.checkData("第(%s)张表,数据抽取落地编码不能为空", dataExtractionDef.getDatabase_code(), index);
			CheckParam.checkData("第(%s)张表,数据落地格式不能为空", dataExtractionDef.getDbfile_format(), index);
			CheckParam.checkData("第(%s)张表,数据是否转存不能为空", dataExtractionDef.getIs_archived(), index);

			// 只有定长或者非定长才检查,数据分隔符和行分隔符
			FileFormat fileFormat = FileFormat.ofEnumByCode(dataExtractionDef.getDbfile_format());
			if (fileFormat == FileFormat.DingChang
				|| fileFormat == FileFormat.FeiDingChang) {
				// 行分隔符转为Unicode编码
				String row_separator = dataExtractionDef.getRow_separator();
				if (StringUtil.isNotBlank(row_separator)) {
					dataExtractionDef.setRow_separator(StringUtil.string2Unicode(row_separator));
				} else {
					CheckParam.throwErrorMsg("第(%s)张表,行分隔符不能为空", index);
				}

				// 列分隔符转为Unicode编码
				String database_separatorr = dataExtractionDef.getDatabase_separatorr();
				if (StringUtil.isNotBlank(database_separatorr)) {
					dataExtractionDef.setDatabase_separatorr(StringUtil.string2Unicode(database_separatorr));
				} else {
					CheckParam.throwErrorMsg("第(%s)张表,列分割符不能为空", index);
				}
			}

			// 根据是否转存设置数据文件源头(转存设置为数据加载格式,否则设置为源数据格式) FIXME 这里默认都存原数据格式
			//      if (dataExtractionDef.getIs_archived().equals(IsFlag.Shi.getCode())) {
			//        dataExtractionDef.setData_extract_type(DataExtractType.ShuJuJiaZaiGeShi.getCode());
			//      } else {
			dataExtractionDef.setData_extract_type(DataExtractType.YuanShuJuGeShi.getCode());
			//      }

			// FIXME 这里设置抽取的方式 是否需要表头设置为 否
			dataExtractionDef.setIs_header(IsFlag.Fou.getCode());

			//      2-2: 没有ID信息存在则视为新增,并将数据文件源头设置为源数据加载格式
			if (dataExtractionDef.getDed_id() == null) {
				dataExtractionDef.setDed_id(PrimayKeyGener.getNextId());
				//
				// dataExtractionDef.setData_extract_type(DataExtractType.ShuJuJiaZaiGeShi.getCode());
				dataExtractionDef.add(Dbo.db());
			} else {
				//        2-1: 如果存在数据转存的ID信息,则表示为编辑状态
				dataExtractionDef.update(Dbo.db());
			}
			index++;
		}
		return colSetId;
	}

}
