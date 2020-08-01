package hrds.h.biz.manage.version;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.Validator;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.DataSourceType;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Datatable_field_info;
import hrds.commons.entity.Dm_operation_info;
import hrds.commons.exception.BusinessException;
import hrds.commons.tree.background.query.DMLDataQuery;
import hrds.commons.tree.background.query.TreeDataQuery;
import hrds.commons.tree.background.utils.DataConvertedNodeData;
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
    @Param(name = "参数名", desc = "参数描述", range = "参数例子")
    @Return(desc = "结果说明", range = "结果描述")
    public List<Node> getMarketVerManageTreeData() {
        //设置版本管理树菜单信息 目前只显示集市层(DML)
        DataSourceType[] dataSourceTypes = new DataSourceType[1];
        dataSourceTypes[0] = DataSourceType.DML;
        //设置源菜单信息节点数据
        List<Map<String, Object>> dataList = new ArrayList<>(TreeDataQuery.getSourceTreeInfos(dataSourceTypes));
//        //初始化树配置
//        TreeConf treeConf = new TreeConf();
//        TreeNodeDataQuery.getDMLDataList(getUser(), dataList, treeConf);
        //获取DML层下工程信息列表
        List<Map<String, Object>> dmlDataInfos = DMLDataQuery.getDMLDataInfos(getUser());
        dataList.addAll(DataConvertedNodeData.conversionDMLDataInfos(dmlDataInfos));
        //根据DML层工程信息获取工程下的分类信息
        for (Map<String, Object> dmlDataInfo : dmlDataInfos) {
            long data_mart_id = (long) dmlDataInfo.get("data_mart_id");
            //获取集市工程下分类信息
            List<Map<String, Object>> dmlCategoryInfos = DMLDataQuery.getDMLCategoryInfos(data_mart_id);
            if (!dmlCategoryInfos.isEmpty()) {
                //添加分类信息到树数据列表
                dataList.addAll(DataConvertedNodeData.conversionDMLCategoryInfos(dmlCategoryInfos));
                for (Map<String, Object> dmlCategoryInfo : dmlCategoryInfos) {
                    long category_id = (long) dmlCategoryInfo.get("category_id");
                    //获取集市工程分类下表信息
                    List<Map<String, Object>> dmlTableInfos = DMLDataQuery.getDMLTableInfos(category_id);
                    if (!dmlTableInfos.isEmpty()) {
                        //添加分类下表信息到树数据列表
                        dataList.addAll(DataConvertedNodeData.conversionDMLTableInfos(dmlTableInfos));
                        for (Map<String, Object> dmlTableInfo : dmlTableInfos) {
                            long datatable_id = (long) dmlTableInfo.get("datatable_id");
                            //根据集市表获取表的版本数据
                            List<Map<String, Object>> dmlTableVersionInfos = getDMLTableVersionInfos(datatable_id);
                            if (!dmlTableVersionInfos.isEmpty()) {
                                dataList.addAll(conversionDMLTableVersionInfos(dmlTableVersionInfos));
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
            asmSql.addSql("select dfi.field_en_name,dfi.field_cn_name,dfi.field_type from" +
                    " datatable_field_info dfi where start_date=?").addParam(version_date);
            asmSql.addSql("or (start_date<? and end_date=?)").addParam(version_date).addParam(Constant.MAXDATE);
            asmSql.addSql("or (start_date<? and end_date!=?").addParam(version_date).addParam(Constant.MAXDATE);
            asmSql.addSql("and end_date>?)").addParam(version_date);
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
                                && d.getField_type().equalsIgnoreCase(new_dfi.getField_type())) {
                            dfi_map.put("is_same", IsFlag.Shi.getCode());
                            //如果找到共同的字段,设置标记为一样,然后跳出,如果不跳出下次处理会把已经设置标记得数据改掉
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
//        System.out.println(JsonUtil.toJson(dataTableStructureInfoMap));
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
        for (String version_date : version_date_s) {
            //根据版本日期和集市表id获取表的版本信息
            Dm_operation_info dm_operation_info = Dbo.queryOneObject(Dm_operation_info.class,
                    "select * from dm_operation_info where datatable_id=? and start_date=?",
                    datatable_id, version_date).orElseThrow(()
                    -> (new BusinessException("根据数据表id和版本日期获取表sql版本信息失败!")));
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
//        System.out.println(JsonUtil.toJson(sql_version_info_map));
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
        //设置查询sql
        asmSql.addSql("SELECT DISTINCT * FROM (");
        asmSql.addSql(" SELECT dd.data_mart_id,dd.category_id,dd.datatable_en_name,dd.datatable_cn_name,dd.datatable_id," +
                " dd.datatable_desc,doi.start_date AS version_data FROM dm_operation_info doi" +
                " JOIN dm_datatable dd ON doi.datatable_id=dd.datatable_id WHERE dd.datatable_id=?" +
                " and end_date != ?").addParam(datatable_id).addParam(Constant.MAXDATE);
        asmSql.addSql(" UNION ALL");
        asmSql.addSql(" SELECT dd.data_mart_id,dd.category_id,dd.datatable_en_name,dd.datatable_cn_name,dd.datatable_id," +
                " dd.datatable_desc,doi.end_date AS version_data FROM dm_operation_info doi" +
                " JOIN dm_datatable dd ON doi.datatable_id=dd.datatable_id WHERE dd.datatable_id=?" +
                " and end_date != ?").addParam(datatable_id).addParam(Constant.MAXDATE);
        asmSql.addSql(" ) aa ORDER BY version_data DESC");
        return Dbo.queryList(asmSql.sql(), asmSql.params());
    }

    @Method(desc = "集市层工程分类表版本信息转换", logicStep = "1.集市层工程分类表版本信息转换")
    @Param(name = "dmlTableVersionInfos", desc = "表版本信息", range = "取值范围说明")
    private static List<Map<String, Object>> conversionDMLTableVersionInfos(List<Map<String, Object>> dmlTableVersionInfos) {
        List<Map<String, Object>> dmlTableVersionNodes = new ArrayList<>();
        dmlTableVersionInfos.forEach(dmlTableVersionInfo -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", dmlTableVersionInfo.get("version_data"));
            map.put("label", dmlTableVersionInfo.get("version_data"));
            map.put("parent_id", dmlTableVersionInfo.get("datatable_id"));
            map.put("classify_id", dmlTableVersionInfo.get("category_id"));
            map.put("file_id", dmlTableVersionInfo.get("datatable_id"));
            map.put("table_name", dmlTableVersionInfo.get("datatable_en_name"));
            map.put("hyren_name", dmlTableVersionInfo.get("datatable_en_name"));
            map.put("original_name", dmlTableVersionInfo.get("datatable_cn_name"));
            map.put("data_layer", DataSourceType.DML.getCode());
            map.put("description", "" +
                    "表英文名：" + dmlTableVersionInfo.get("datatable_en_name") + "\n" +
                    "表中文名：" + dmlTableVersionInfo.get("datatable_cn_name") + "\n" +
                    "版本日期：" + dmlTableVersionInfo.get("version_data") + "\n" +
                    "表描述：" + dmlTableVersionInfo.get("datatable_desc"));
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
        System.out.println(JsonUtil.toJson(dataTableStructureInfoMap));
        return dataTableStructureInfoMap;
    }
}
