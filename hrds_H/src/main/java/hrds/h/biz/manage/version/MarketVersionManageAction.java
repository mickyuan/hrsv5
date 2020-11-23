package hrds.h.biz.manage.version;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.Validator;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.DataSourceType;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Datatable_field_info;
import hrds.commons.entity.Dm_datatable;
import hrds.commons.entity.Dm_operation_info;
import hrds.commons.exception.BusinessException;
import hrds.commons.tree.background.query.DMLDataQuery;
import hrds.commons.tree.background.query.TreeDataQuery;
import hrds.commons.tree.background.utils.DataConvertedNodeData;
import hrds.commons.tree.commons.TreePageSource;
import hrds.commons.utils.Constant;
import hrds.commons.utils.DruidParseQuerySql;
import hrds.commons.utils.tree.Node;
import hrds.commons.utils.tree.NodeDataConvertedTreeList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

@DocClass(desc = "集市-版本管理", author = "BY-HLL", createdate = "2020/7/29 0029 上午 10:22")
public class MarketVersionManageAction extends BaseAction {

	private static final Logger logger = LogManager.getLogger();

	@Method(desc = "获取版本管理树数据", logicStep = "获取版本管理树数据")
	@Return(desc = "版本管理树数据", range = "版本管理树数据")
	public List<Node> getMarketVerManageTreeData() {
		//设置源菜单信息节点数据
		List<Map<String, Object>> dataList = new ArrayList<>(TreeDataQuery.getSourceTreeInfos(TreePageSource.MARKET_VERSION_MANAGE));
		//获取DML层下工程信息列表
		List<Map<String, Object>> dmlDataInfos = DMLDataQuery.getDMLDataInfos(getUser());
		dataList.addAll(DataConvertedNodeData.conversionDMLDataInfos(dmlDataInfos));
		//根据DML层工程信息获取工程下的分类信息
		for (Map<String, Object> dmlDataInfo : dmlDataInfos) {
			long data_mart_id = (long) dmlDataInfo.get("data_mart_id");
			//获取集市工程下分类信息
			List<Map<String, Object>> dmlCategoryInfos = DMLDataQuery.getDMLCategoryInfos(data_mart_id);
			if (!dmlCategoryInfos.isEmpty()) {
				for (Map<String, Object> dmlCategoryInfo : dmlCategoryInfos) {
					//添加存在表的分类信息到树数据列表
					dataList.add(DataConvertedNodeData.conversionDMLCategoryInfos(dmlCategoryInfo));
					long category_id = (long) dmlCategoryInfo.get("category_id");
					//获取集市工程分类下表信息,如果getDMLTableInfos()传入的执行状态为null,则查询所有状态
					List<Map<String, Object>> dmlTableInfos = DMLDataQuery.getDMLTableInfos(category_id, null);
					if (!dmlTableInfos.isEmpty()) {
						//添加分类下表信息到树数据列表
						dataList.addAll(DataConvertedNodeData.conversionDMLTableInfos(dmlTableInfos));
						for (Map<String, Object> dmlTableInfo : dmlTableInfos) {
							long datatable_id = (long) dmlTableInfo.get("datatable_id");
							//根据集市表获取表的版本数据
							List<Map<String, Object>> dmlTableVersionInfos = getDMLTableVersionInfos(datatable_id);
							if (!dmlTableVersionInfos.isEmpty()) {
								dataList.addAll(conversionDMLTableVersionInfos(datatable_id, dmlTableVersionInfos));
							}
						}
					}
				}
			}
		}
		return NodeDataConvertedTreeList.dataConversionTreeInfo(dataList);
	}

	@Method(desc = "获取集市数据表结构的版本信息列表", logicStep = "获取集市数据表结构的版本信息列表")
	@Param(name = "datatable_id", desc = "集市表id,该值唯一", range = "long类型")
	@Param(name = "version_date_s", desc = "版本日期数组", range = "String[]类型")
	public Map<String, Object> getDataTableStructureInfos(long datatable_id, String[] version_date_s) {
		//数据校验
		Validator.notBlank(String.valueOf(datatable_id));
		if (null == version_date_s || version_date_s.length == 0) {
			throw new BusinessException("版本日期列表不能为空!");
		}
		//初始化数据表结果版本数据信息
		Map<String, List<Datatable_field_info>> datatableFieldInfos_s_map = new HashMap<>();
		List<List<Datatable_field_info>> datatableFieldInfos_s = new ArrayList<>();
		//检测版本中所有英文字段列表
		Set<String> field_en_nam_s = new HashSet<>();
		//初始化sql
		SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
		for (String version_date : version_date_s) {
			//获取选中版本的字段信息
			asmSql.clean();
			//如果选中的版本日志是"00000000",则查询已经修改但未运行成功的字段信息
			if (version_date.equalsIgnoreCase(Constant.INITDATE)) {
				asmSql.addSql("select dfi.field_en_name,dfi.field_cn_name,dfi.field_type,dfi.field_length from "
					+ Datatable_field_info.TableName + " dfi where dfi.datatable_id=?").addParam(datatable_id);
				asmSql.addSql("and dfi.end_date in (?,?)").addParam(Constant.INITDATE).addParam(Constant.MAXDATE);
			}
			//如果查看版本刚好是今天,则需要取开始日期为今天,结束日期为00000000的字段
			else if (version_date.equalsIgnoreCase(DateUtil.getSysDate())) {
				asmSql.addSql("select dfi.field_en_name,dfi.field_cn_name,dfi.field_type,dfi.field_length from "
					+ Datatable_field_info.TableName + " dfi where dfi.datatable_id=?").addParam(datatable_id);
				asmSql.addSql("and start_date=? and dfi.end_date!=?")
					.addParam(version_date).addParam(Constant.INITDATE);
			}
			//否则获取指定版本的字段信息
			else {
				asmSql.addSql("select dfi.field_en_name,dfi.field_cn_name,dfi.field_type,dfi.field_length from "
					+ Datatable_field_info.TableName + " dfi where dfi.datatable_id=?").addParam(datatable_id);
				asmSql.addSql("and dfi.start_date<=? and dfi.end_date>? and dfi.end_date!=?")
					.addParam(version_date).addParam(version_date).addParam(Constant.INVDATE);
				asmSql.addSql(" UNION ALL");
				asmSql.addSql("select dfi.field_en_name,dfi.field_cn_name,dfi.field_type,dfi.field_length from "
					+ Datatable_field_info.TableName + " dfi where dfi.datatable_id=?").addParam(datatable_id);
				asmSql.addSql("and dfi.start_date=? and dfi.end_date=?")
					.addParam(version_date).addParam(Constant.INITDATE);
			}
			List<Datatable_field_info> selectedVersionFieldList =
				Dbo.queryList(Datatable_field_info.class, asmSql.sql(), asmSql.params());
			//设置数据表结果版本数据信息map,用于设置多版本显示信息
			datatableFieldInfos_s_map.put(version_date, new ArrayList<>(selectedVersionFieldList));
			//设置数据表结果版本数据信息list,用于取多版本的并集
			datatableFieldInfos_s.add(selectedVersionFieldList);
			//设置当前版本的英文字段信息
			selectedVersionFieldList.forEach(datatable_field_info -> field_en_nam_s.add(datatable_field_info.getField_en_name()));
		}
		//处理获取的版本数据,取不同版本字段信息的并集
		List<Datatable_field_info> fieldUnionResult = datatableFieldInfos_s.parallelStream()
			.filter(datatable_field_infos -> datatable_field_infos != null && datatable_field_infos.size() != 0)
			.reduce((a, b) -> {
				a.retainAll(b);
				return a;
			}).orElseThrow(() -> (new BusinessException("取不同版本字段信息的并集失败!")));
		//初始化待返回的数据Map
		Map<String, Object> dataTableStructureInfoMap = new HashMap<>();
		//循环处理每个版本的数据
		datatableFieldInfos_s_map.forEach((version_date, selectedVersionFieldList) -> {
			//初始化当前版本字段信息的map集合
			List<Map<String, Object>> dfi_map_s = new ArrayList<>();
			//处理需要返回的信息
			field_en_nam_s.forEach(field_en_name -> {
				//初始化存储字段信息的map
				Map<String, Object> dfi_map = new HashMap<>();
				Datatable_field_info new_dfi = new Datatable_field_info();
				//设置默认值
				new_dfi.setField_en_name("--");
				new_dfi.setField_cn_name("--");
				new_dfi.setField_type("--");
				new_dfi.setField_length("--");
				//处理当前版本的每一条记录
				for (Datatable_field_info datatable_field_info : selectedVersionFieldList) {
					if (field_en_name.equalsIgnoreCase(datatable_field_info.getField_en_name())) {
						new_dfi = datatable_field_info;
					}
				}
				dfi_map.put("datatable_field_info", new_dfi);
				//处理每个版本的字段详细信息,如果并集信息存在,根据并集信息设置公共的数据
				if (!fieldUnionResult.isEmpty()) {
					for (Datatable_field_info d : fieldUnionResult) {
						if (d.getField_en_name().equalsIgnoreCase(new_dfi.getField_en_name())
							&& d.getField_cn_name().equalsIgnoreCase(new_dfi.getField_cn_name())
							&& d.getField_type().equalsIgnoreCase(new_dfi.getField_type())
							&& d.getField_length().equalsIgnoreCase(new_dfi.getField_length())) {
							//如果需要比较的信息一致,设置标记为一样,然后跳出,如果不跳出下次处理会把已经设置标记的数据改掉
							dfi_map.put("is_same", IsFlag.Shi.getCode());
							break;
						} else {
							dfi_map.put("is_same", IsFlag.Fou.getCode());
						}
					}
				}
				//并集信息为空,代表选择的版本没有一条相同,全部设置为否
				else {
					dfi_map.put("is_same", IsFlag.Fou.getCode());
				}
				dfi_map_s.add(dfi_map);
			});
			dataTableStructureInfoMap.put(version_date, dfi_map_s);
		});
		return dataTableStructureInfoMap;
	}


	@Method(desc = "获取集市数据表Mapping的版本信息列表", logicStep = "获取集市数据表Mapping的版本信息列表")
	@Param(name = "datatable_id", desc = "集市表id,该值唯一", range = "long类型")
	@Param(name = "version_date_s", desc = "版本日期数组", range = "String[]类型")
	public Map<String, Object> getDataTableMappingInfos(long datatable_id, String[] version_date_s) {
		//数据校验
		Validator.notBlank(String.valueOf(datatable_id));
		if (null == version_date_s || version_date_s.length == 0) {
			throw new BusinessException("版本日期列表不能为空!");
		}
		//初始sql版本解析信息
		Map<String, Object> sql_version_info_map = new HashMap<>();
		//根据版本日期和集市表id获取表的版本信息
		for (String version_date : version_date_s) {
			//如果选中的版本日志是"00000000",则查询已经修改但未运行成功sql信息
			Dm_operation_info dm_operation_info;
			if (version_date.equalsIgnoreCase(Constant.INITDATE)) {
				dm_operation_info = Dbo.queryOneObject(Dm_operation_info.class,
					"select * from dm_operation_info where datatable_id=? and end_date in (?,?)",
					datatable_id, version_date, Constant.MAXDATE).orElseThrow(()
					-> (new BusinessException("根据数据表id和版本日期获取表sql版本信息失败!")));
			} else {
				dm_operation_info = Dbo.queryOneObject(Dm_operation_info.class,
					"select * from dm_operation_info where datatable_id=? and start_date<=? and end_date>?",
					datatable_id, version_date, version_date).orElseThrow(()
					-> (new BusinessException("根据数据表id和版本日期获取表sql版本信息失败!")));
			}
			//获取当前版本执行sql
			String execute_sql = dm_operation_info.getExecute_sql();
			Validator.notBlank(execute_sql, "执行sql为空!");
			//初始当前版本信息的map
			Map<String, Object> current_version_info_map = new HashMap<>();
			//设置执行sql
			current_version_info_map.put("execute_sql", execute_sql);
			//初始化需要高亮的list列表
			List<Object> highlight_list = new ArrayList<>();
			//执行sql的解析结果
			DruidParseQuerySql sql_parse_info = new DruidParseQuerySql(execute_sql);
			//设置查询表名
			List<String> table_name_s = DruidParseQuerySql.parseSqlTableToList(execute_sql);
			highlight_list.addAll(table_name_s);
			//设置查询字段名
			highlight_list.addAll(sql_parse_info.selectList);
			//获取查询条件信息
//            highlight_list.add(sql_parse_info.whereInfo);
			//获取分组信息
//            List<SQLExpr> groupByItems;
//            if (null != sql_parse_info.groupByInfo) {
//                highlight_list.addAll(sql_parse_info.groupByInfo.getItems());
//            }
			//获取排序信息
//            List<SQLSelectOrderByItem> sortByInfo;
//            if (null != sql_parse_info.sortByInfo) {
//                highlight_list.addAll(sql_parse_info.sortByInfo);
//            }
			current_version_info_map.put("highlight_list", highlight_list);
			sql_version_info_map.put(version_date, current_version_info_map);
		}
		//处理获取的版本数据,取不同版本字段信息的并集
		//初始化数据表Mapping版本数据信息列表
//        List<Map<String, Object>> dataTableMappingInfos = new ArrayList<>();
		return sql_version_info_map;
	}

	@Method(desc = "获取集市表版本信息列表", logicStep = "获取集市表版本信息列表")
	@Param(name = "datatable_id", desc = "集市表id,该值唯一", range = "long类型")
	private static List<Map<String, Object>> getDMLTableVersionInfos(long datatable_id) {
		//初始化sql
		SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
		asmSql.clean();
		//设置查询sql,版本需要取sql版本和字段版本的并集
		asmSql.addSql("SELECT DISTINCT * FROM (");
		//获取sql版本
		asmSql.addSql(" SELECT doi.start_date AS version_date FROM " + Dm_operation_info.TableName + " doi" +
			" JOIN " + Dm_datatable.TableName + " dd ON doi.datatable_id=dd.datatable_id" +
			" WHERE dd.datatable_id=? and end_date!=?").addParam(datatable_id).addParam(Constant.INITDATE);
		asmSql.addSql(" UNION ALL");
		asmSql.addSql(" SELECT doi.end_date AS version_date FROM  " + Dm_operation_info.TableName + " doi" +
			" JOIN " + Dm_datatable.TableName + " dd ON doi.datatable_id=dd.datatable_id" +
			" WHERE dd.datatable_id=? and end_date not in (?,?)").addParam(datatable_id)
			.addParam(Constant.MAXDATE).addParam(Constant.INVDATE);
		//获取字段版本
		asmSql.addSql(" UNION ALL");
		asmSql.addSql(" SELECT dfi.start_date AS version_date FROM " + Datatable_field_info.TableName + " dfi" +
			" JOIN " + Dm_datatable.TableName + " dd ON dd.datatable_id=dfi.datatable_id" +
			" WHERE dd.datatable_id=? and end_date!=? and start_date!=?")
			.addParam(datatable_id).addParam(Constant.INITDATE).addParam(DateUtil.getSysDate());
		asmSql.addSql(" UNION ALL");
		asmSql.addSql(" SELECT dfi.end_date AS version_date FROM " + Datatable_field_info.TableName + " dfi" +
			" JOIN " + Dm_datatable.TableName + " dd ON dd.datatable_id=dfi.datatable_id" +
			" WHERE dd.datatable_id=? and end_date not in (?,?)").addParam(datatable_id)
			.addParam(Constant.MAXDATE).addParam(Constant.INVDATE);
		asmSql.addSql(" ) aa ORDER BY version_date DESC");
		return Dbo.queryList(asmSql.sql(), asmSql.params());
	}

	@Method(desc = "集市层工程分类表版本信息转换", logicStep = "1.集市层工程分类表版本信息转换")
	@Param(name = "datatable_id", desc = "集市表id,该值唯一", range = "long类型")
	@Param(name = "dmlTableVersionInfos", desc = "表版本信息", range = "取值范围说明")
	private static List<Map<String, Object>> conversionDMLTableVersionInfos(long datatable_id,
	                                                                        List<Map<String, Object>> dmlTableVersionInfos) {
		//获取集市表信息
		Dm_datatable dm_datatable = Dbo.queryOneObject(Dm_datatable.class,
			"SELECT * FROM " + Dm_datatable.TableName + " dd WHERE dd.datatable_id=?", datatable_id)
			.orElseThrow(() -> (new BusinessException("获取集市表信息的SQL失败!")));
		//设置集市表版本信息
		List<Map<String, Object>> dmlTableVersionNodes = new ArrayList<>();
		dmlTableVersionInfos.forEach(dmlTableVersionInfo -> {
			String file_id = String.valueOf(dm_datatable.getDatatable_id());
			String version_date = String.valueOf(dmlTableVersionInfo.get("version_date"));
			Map<String, Object> map = new HashMap<>();
			map.put("id", file_id + "_" + version_date);
			map.put("label", version_date);
			map.put("parent_id", dm_datatable.getDatatable_id());
			map.put("classify_id", dm_datatable.getCategory_id());
			map.put("file_id", file_id);
			map.put("table_name", dm_datatable.getDatatable_en_name());
			map.put("hyren_name", dm_datatable.getDatatable_en_name());
			map.put("original_name", dm_datatable.getDatatable_cn_name());
			map.put("data_layer", DataSourceType.DML.getCode());
			map.put("tree_page_source", TreePageSource.MARKET_VERSION_MANAGE);
			map.put("description", "" +
				"表英文名：" + dm_datatable.getDatatable_en_name() + "\n" +
				"表中文名：" + dm_datatable.getDatatable_cn_name() + "\n" +
				"版本日期：" + version_date + "\n" +
				"表描述：" + dm_datatable.getDatatable_desc());
			dmlTableVersionNodes.add(map);
		});
		return dmlTableVersionNodes;
	}

	@Method(desc = "获取集市数据表结构的版本信息列表", logicStep = "获取集市数据表结构的版本信息列表")
	@Param(name = "datatable_id", desc = "集市表id,该值唯一", range = "long类型")
	@Param(name = "version_date_s", desc = "版本日期数组", range = "String[]类型")
	@Deprecated
	public Map<String, Object> getDataTableStructureInfos_columu(long datatable_id, String[] version_date_s) {
		//数据校验
		Validator.notBlank(String.valueOf(datatable_id));
		if (null == version_date_s || version_date_s.length == 0) {
			throw new BusinessException("版本日期列表不能为空!");
		}
		//初始化数据表结果版本数据信息
		Map<String, List<Datatable_field_info>> datatableFieldInfos_s_map = new HashMap<>();
		//检测版本中所有英文字段列表
		Set<String> field_en_nam_s = new HashSet<>();
		//初始化sql
		SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
		for (String version_date : version_date_s) {
			//获取选中版本的字段信息
			asmSql.clean();
			asmSql.addSql("select dfi.field_en_name,dfi.field_cn_name,dfi.field_type from" +
				" datatable_field_info_bak dfi where start_date=?").addParam(version_date);
			asmSql.addSql("or (start_date<? and end_date=?)").addParam(version_date).addParam(Constant.MAXDATE);
			asmSql.addSql("or (start_date<? and end_date!=?").addParam(version_date).addParam(Constant.MAXDATE);
			asmSql.addSql("and end_date>?)").addParam(version_date);
			List<Datatable_field_info> selectedVersionFieldList =
				Dbo.queryList(Datatable_field_info.class, asmSql.sql(), asmSql.params());
			//设置数据表结果版本数据信息map,用于设置多版本显示信息
			datatableFieldInfos_s_map.put(version_date, new ArrayList<>(selectedVersionFieldList));
			//设置当前版本的英文字段信息
			selectedVersionFieldList.forEach(datatable_field_info -> field_en_nam_s.add(datatable_field_info.getField_en_name()));
		}
		//初始化待返回的数据Map
		Map<String, Object> dataTableStructureInfoMap = new HashMap<>();
		//循环处理每个版本的数据
		datatableFieldInfos_s_map.forEach((version_date, selectedVersionFieldList) -> {
			//初始化当前版本字段信息的map集合
			List<Map<String, Map<String, String>>> dfi_map_s = new ArrayList<>();
			//处理需要返回的信息
			field_en_nam_s.forEach(field_en_name -> {
				//初始化存储字段信息的map
				Map<String, Map<String, String>> dfi_map = new HashMap<>();
				//初始化存储字段英文属性的map,并设置默认值
				Map<String, String> field_en_name_map = new HashMap<>();
				field_en_name_map.put("field_en_name", "-");
				field_en_name_map.put("is_same", IsFlag.Fou.getCode());
				//初始化存储字段中文属性的map,并设置默认值
				Map<String, String> field_cn_name_map = new HashMap<>();
				field_cn_name_map.put("field_cn_name", "-");
				field_cn_name_map.put("is_same", IsFlag.Fou.getCode());
				//初始化存储字段类型属性的map,并设置默认值
				Map<String, String> field_type_map = new HashMap<>();
				field_type_map.put("field_type", "-");
				field_type_map.put("is_same", IsFlag.Fou.getCode());
				//处理当前版本的每一条记录
				for (Datatable_field_info datatable_field_info : selectedVersionFieldList) {
					if (field_en_name.equalsIgnoreCase(datatable_field_info.getField_en_name())) {
						field_en_name_map.put("field_en_name", datatable_field_info.getField_en_name());
						field_cn_name_map.put("field_cn_name", datatable_field_info.getField_cn_name());
						field_type_map.put("field_type", datatable_field_info.getField_type());
					}
					//处理当前版本字段英文名map信息
					if (datatable_field_info.getField_en_name().equalsIgnoreCase(field_en_name_map.get("field_en_name"))) {
						field_en_name_map.put("is_same", IsFlag.Shi.getCode());
					}
					//处理当前版本字段中文名map信息
					if (datatable_field_info.getField_cn_name().equalsIgnoreCase(field_cn_name_map.get("field_cn_name"))) {
						field_cn_name_map.put("is_same", IsFlag.Shi.getCode());
					}
					//处理当前版本字段英文名map信息
					if (datatable_field_info.getField_type().equalsIgnoreCase(field_type_map.get("field_type"))) {
						field_type_map.put("is_same", IsFlag.Shi.getCode());
					}
				}
				dfi_map.put("field_en_name_map", field_en_name_map);
				dfi_map.put("field_cn_name_map", field_cn_name_map);
				dfi_map.put("field_type_map", field_type_map);
				dfi_map_s.add(dfi_map);
			});
			dataTableStructureInfoMap.put(version_date, dfi_map_s);
		});
		return dataTableStructureInfoMap;
	}
}
