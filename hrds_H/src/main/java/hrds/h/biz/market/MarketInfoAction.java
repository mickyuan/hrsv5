package hrds.h.biz.market;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.CodecUtil;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.util.Dbo;
import fd.ng.web.util.RequestUtil;
import fd.ng.web.util.ResponseUtil;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.*;
import hrds.commons.collection.ProcessingData;
import hrds.commons.collection.bean.LayerBean;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.tree.foreground.ForegroundTreeUtil;
import hrds.commons.tree.foreground.bean.TreeDataInfo;
import hrds.commons.utils.DruidParseQuerySql;
import hrds.commons.utils.key.PrimayKeyGener;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

//import hrds.h.biz.SqlAnalysis.HyrenOracleTableVisitor;
//import com.alibaba.druid.

@DocClass(desc = "集市信息查询类", author = "BY-HLL", createdate = "2019/10/31 0031 下午 04:17")
/**
 * author:TBH
 * Time:2020.4.10
 */
public class MarketInfoAction extends BaseAction {

    private static final SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
    private static final String Mart_Storage_path = "";
    private static final String Final_Date = "99991231";
    private static final String Zero = "0";
    private static final String ZeroDate = "00000000";
    private static final String LimitNumber = "10";
    private static final String alias = "TempTable";
    private static final String TargetColumn = "targecolumn";
    private static final String SourceColumn = "sourcecolumn";
    //TODO 由于目前不存在集市分类 所以随便写的一个ID 为了满足入库需求 之后将去除
    private static final String Category_id = "1000025018";
    private static final String jdbc_url = "jdbc_url";

    /**
     * 封装一个检查字段正确的方法
     *
     * @param column
     * @param columname
     */
    private void CheckColummn(String column, String columname) {
        if (StringUtil.isBlank(column) || StringUtil.isBlank(column.trim())) {
            throw new BusinessException(columname + "不为空且不为空格，" + columname + "=" + column);
        }
    }

    @Method(desc = "获取登录用户集市所有表加起来的存储量大小",
            logicStep = "根据用户ID进行搜索")
    @Return(desc = "获取登录用户集市所有表加起来的存储量大小", range = "返回值取值范围")
    public List<Map<String, Object>> getTotalStorage() {
        return Dbo.queryList("SELECT (CASE WHEN sources IS NULL THEN 0 ELSE sources END) sources FROM" +
                " (SELECT SUM(soruce_size) sources FROM " + Dm_datatable.TableName + " where data_mart_id" +
                " in (select data_mart_id from " + Dm_info.TableName + " where create_id = ? )) t", getUserId());
    }

    @Method(desc = "获取登录用户集市的所有Hbase表的个数",
            logicStep = "根据用户ID进行搜索")
    @Return(desc = "用户集市的所有Hbase表的个数", range = "返回值取值范围")
    public List<Map<String, Long>> getHbaseStorage() {
        List<Map<String, Long>> list = new ArrayList<>();
        Map<String, Long> map = new HashMap<String, Long>();
        Long count = Dbo.queryNumber("SELECT count(*) as count FROM " + Dm_datatable.TableName + " t1 " +
                        "LEFT JOIN " + Dm_relation_datatable.TableName + " t2 ON t1.datatable_id = t2.datatable_id " +
                        "LEFT JOIN " + Data_store_layer.TableName + " t3 ON t2.dsl_id = t3.dsl_id " +
                        "WHERE t3.store_type = ? and is_successful = ?" +
                        " and t1.data_mart_id in(select data_mart_id from " + Dm_info.TableName + " where create_id = ?)",
                Store_type.HBASE.getCode(), IsFlag.Shi.getCode(), getUserId()).orElseThrow(() -> new BusinessException("sql查询错误！"));
        map.put("count", count);
        list.add(map);
        return list;
    }

    @Method(desc = "获取登录用户集市的所有Hive表的个数",
            logicStep = "根据用户ID进行搜索")
    @Return(desc = "用户集市的所有Hive表的个数", range = "返回值取值范围")
    public List<Map<String, Long>> getHyRenDBStorage() {
        List<Map<String, Long>> list = new ArrayList<>();
        Map<String, Long> map = new HashMap<String, Long>();
        Long count = Dbo.queryNumber("SELECT count(*) FROM " + Dm_datatable.TableName + " t1 " +
                        "LEFT JOIN " + Dm_relation_datatable.TableName + " t2 ON t1.datatable_id = t2.datatable_id " +
                        "LEFT JOIN " + Data_store_layer.TableName + " t3 ON t2.dsl_id = t3.dsl_id " +
                        "WHERE t3.store_type = ? and is_successful = ?" +
                        " and t1.data_mart_id in(select data_mart_id from " + Dm_info.TableName + " where create_id = ?)",
                Store_type.HIVE.getCode(), IsFlag.Shi.getCode(), getUserId()).orElseThrow(() -> new BusinessException("sql查询错误！"));
        map.put("count", count);
        list.add(map);
        return list;
    }

    @Method(desc = "获取登录用户集市的所有Solr表的个数",
            logicStep = "根据用户ID进行搜索")
    @Return(desc = "用户集市的所有Solr表的个数", range = "返回值取值范围")
    public List<Map<String, Long>> getSolrDBStorage() {
        List<Map<String, Long>> list = new ArrayList<>();
        Map<String, Long> map = new HashMap<String, Long>();
        Long count = Dbo.queryNumber("SELECT count(*) FROM " + Dm_datatable.TableName + " t1 " +
                        "LEFT JOIN " + Dm_relation_datatable.TableName + " t2 ON t1.datatable_id = t2.datatable_id " +
                        "LEFT JOIN " + Data_store_layer.TableName + " t3 ON t2.dsl_id = t3.dsl_id " +
                        "WHERE t3.store_type = ? and is_successful = ?" +
                        " and t1.data_mart_id in(select data_mart_id from " + Dm_info.TableName + " where create_id = ?)",
                Store_type.SOLR.getCode(), IsFlag.Shi.getCode(), getUserId()).orElseThrow(() -> new BusinessException("sql查询错误！"));
        map.put("count", count);
        list.add(map);
        return list;
    }

    @Method(desc = "获取登录用户集市占用存储量前三的集市工程名和存储量大小",
            logicStep = "根据用户ID进行搜索")
    @Return(desc = "用户集市占用存储量前三的集市工程名和存储量大小", range = "返回值取值范围")
    public List<Map<String, Object>> getMarketTakesUpTop3Storage() {
        return Dbo.queryList("SELECT t1.data_mart_id, mart_name, SUM(soruce_size) source_size " +
                " FROM " + Dm_datatable.TableName + " t1 left join  " + Dm_info.TableName + " t2 on t1.data_mart_id = t2.data_mart_id " +
                " WHERE t1.data_mart_id IN ( SELECT data_mart_id FROM " + Dm_info.TableName + " WHERE create_id = ? ) " +
                " GROUP BY t1.data_mart_id, mart_name ORDER BY source_size DESC limit 3", getUserId());
    }

    @Method(desc = "获取登录用户集市Hive表占用存储前三的集市工程",
            logicStep = "根据用户ID进行搜索")
    @Return(desc = "用户集市Hive表占用存储前三的集市工程", range = "返回值取值范围")
    public List<Map<String, Object>> getMarketHyRenDbTop3Storage() {
        return Dbo.queryList("SELECT t1.data_mart_id, mart_name, SUM(soruce_size) source_size " +
                " FROM " + Dm_datatable.TableName + " t1 left join  " + Dm_info.TableName + " t2 on t1.data_mart_id = t2.data_mart_id " +
                " left join " + Dm_relation_datatable.TableName + " t3 on t1.datatable_id = t3.datatable_id " +
                " left join " + Data_store_layer.TableName + " t4 on t4.dsl_id = t3.dsl_id " +
                "WHERE t1.data_mart_id IN ( SELECT data_mart_id FROM " + Dm_info.TableName + " WHERE create_id = ? and t4.store_type = ?) " +
                " GROUP BY t1.data_mart_id, mart_name ORDER BY source_size DESC limit 3", getUserId(), Store_type.HIVE.getCode());
    }

    @Method(desc = "获取登录用户集市Hbase表占用存储前三的集市工程",
            logicStep = "根据用户ID进行搜索")
    @Return(desc = "用户集市Hbase表占用存储前三的集市工程", range = "返回值取值范围")
    public List<Map<String, Object>> getMarketHbaseTop3Storage() {
        return Dbo.queryList("SELECT t1.data_mart_id, mart_name, SUM(soruce_size) source_size " +
                " FROM " + Dm_datatable.TableName + " t1 left join  " + Dm_info.TableName + " t2 on t1.data_mart_id = t2.data_mart_id " +
                " left join " + Dm_relation_datatable.TableName + " t3 on t1.datatable_id = t3.datatable_id " +
                " left join " + Data_store_layer.TableName + " t4 on t4.dsl_id = t3.dsl_id " +
                "WHERE t1.data_mart_id IN ( SELECT data_mart_id FROM " + Dm_info.TableName + " WHERE create_id = ? and t4.store_type = ?) " +
                " GROUP BY t1.data_mart_id, mart_name ORDER BY source_size DESC limit 3", getUserId(), Store_type.HBASE.getCode());
    }

    @Method(desc = "获取登录用户集市Solr表占用存储前三的集市工程",
            logicStep = "根据用户ID进行搜索")
    @Return(desc = "用户集市Solr表占用存储前三的集市工程", range = "返回值取值范围")
    public List<Map<String, Object>> getMarketSolrDBTop3Storage() {
        return Dbo.queryList("SELECT t1.data_mart_id, mart_name, SUM(soruce_size) source_size " +
                " FROM " + Dm_datatable.TableName + " t1 left join  " + Dm_info.TableName + " t2 on t1.data_mart_id = t2.data_mart_id " +
                " left join " + Dm_relation_datatable.TableName + " t3 on t1.datatable_id = t3.datatable_id " +
                " left join " + Data_store_layer.TableName + " t4 on t4.dsl_id = t3.dsl_id " +
                "WHERE t1.data_mart_id IN ( SELECT data_mart_id FROM " + Dm_info.TableName + " WHERE create_id = ? and t4.store_type = ?) " +
                " GROUP BY t1.data_mart_id, mart_name ORDER BY source_size DESC limit 3", getUserId(), Store_type.SOLR.getCode());
    }

    @Method(desc = "获取登录用户数据集市首页信息",
            logicStep = "根据用户ID进行搜索")
    @Return(desc = "获取登录用户数据集市首页信息", range = "返回值取值范围")
    public List<Dm_info> getMarketInfo() {
        return Dbo.queryList(Dm_info.class, "SELECT mart_name,data_mart_id FROM " + Dm_info.TableName + " where create_id = ? order by " +
                "data_mart_id asc", getUserId());
    }

    @Method(desc = "新增集市工程",
            logicStep = "1.检查数据合法性" +
                    "2.新增前查询集市编号是否已存在" +
                    "3.对dm_info初始化一些非页面传值" +
                    "4.保存data_source信息")
    @Param(name = "dm_info", desc = "Dm_info", range = "与Dm_info表字段规则一致",
            isBean = true)
    public void addMarket(Dm_info dm_info) {
        //1.检查数据合法性
        String mart_name = dm_info.getMart_name();
        String mart_number = dm_info.getMart_number();
        CheckColummn(mart_name, "集市名称");
        CheckColummn(mart_number, "集市编号");
        //2.新增前查询集市编号是否已存在
        if (Dbo.queryNumber("select count(*) from " + Dm_info.TableName + " where  mart_number = ?", mart_number)
                .orElseThrow(() -> new BusinessException("sql查询错误！")) != 0) {
            throw new BusinessException("集市编号重复，请重新填写");
        }
        if (Dbo.queryNumber("select count(*) from " + Dm_info.TableName + " where mart_name = ? ", mart_name)
                .orElseThrow(() -> new BusinessException("sql查询错误！")) != 0) {
            throw new BusinessException("集市名称重复，请重新填写");
        }
        //3.对dm_info初始化一些非页面传值
        dm_info.setData_mart_id(PrimayKeyGener.getNextId());
        dm_info.setMart_storage_path(Mart_Storage_path);
        dm_info.setCreate_date(DateUtil.getSysDate());
        dm_info.setCreate_time(DateUtil.getSysTime());
        dm_info.setCreate_id(getUserId());
        //4.保存data_source信息
        dm_info.add(Dbo.db());
    }


    @Method(desc = "获取登录用户查询数据集市工程下的所有集市表",
            logicStep = "根据数据集市工程ID进行查询")
    @Param(name = "data_mart_id", desc = "data_mart_id", range = "data_mart_id")
    @Return(desc = "当前集市工程下创建的所有集市表", range = "返回值取值范围")
    public List<Map<String, Object>> queryDMDataTableByDataMartID(String data_mart_id) {
        Dm_datatable dm_datatable = new Dm_datatable();
        dm_datatable.setData_mart_id(data_mart_id);
        return Dbo.queryList("SELECT * ,case when datatable_id in (select datatable_id from " + Datatable_field_info.TableName + ") then true else false end as isadd from "
                + Dm_datatable.TableName + " where data_mart_id = ? order by " + "datatable_id asc", dm_datatable.getData_mart_id());
    }

    @Method(desc = "删除集市表及其相关的所有信息",
            logicStep = "1、删除数据表信息" +
                    "2、删除数据操作信息表" +
                    "3、删除数据表已选数据源信息" +
                    "4、删除结果映射信息表" +
                    "5、删除数据源表字段" +
                    "6、删除数据表字段信息" +
                    "7、删除集市表存储关系表" +
                    "8、删除集市字段存储信息")
    @Param(name = "datatable_id", desc = "datatable_id", range = "datatable_id")
    public void deleteDMDataTable(String datatable_id) {
        Dm_datatable dm_datatable = new Dm_datatable();
        dm_datatable.setDatatable_id(datatable_id);
        //1、删除数据表信息
        Dbo.execute("delete from " + Dm_datatable.TableName + " where datatable_id = ?", dm_datatable.getDatatable_id());
        //2、删除数据操作信息表
        Dbo.execute("delete from " + Dm_operation_info.TableName + " where datatable_id = ?", dm_datatable.getDatatable_id());
        //3、删除数据表已选数据源信息
        Dbo.execute("delete from " + Dm_datatable_source.TableName + " where datatable_id = ?", dm_datatable.getDatatable_id());
        //4、删除结果映射信息表
        Dbo.execute("delete from " + Dm_etlmap_info.TableName + " where datatable_id = ?", dm_datatable.getDatatable_id());
        //5、删除数据源表字段
        Dbo.execute("delete from " + Own_source_field.TableName + " where own_dource_table_id in " +
                "(select own_dource_table_id from " + Dm_datatable_source.TableName + " where datatable_id = ? )", dm_datatable.getDatatable_id());
        //6、删除数据表字段信息
        Dbo.execute("delete from " + Datatable_field_info.TableName + " where datatable_id = ?", dm_datatable.getDatatable_id());
        //7、删除集市表存储关系表
        Dbo.execute("delete from " + Dm_relation_datatable.TableName + " where datatable_id = ?", dm_datatable.getDatatable_id());
        //8、删除集市字段存储信息
        Dbo.execute("delete from " + Dm_column_storage.TableName + " where datatable_field_id in " +
                "(select datatable_field_id from " + Datatable_field_info.TableName + " where datatable_id = ?)", dm_datatable.getDatatable_id());
    }

    @Method(desc = "获取登录用户集市查询存储配置表",
            logicStep = "数据权限验证: 根据登录用户进行数据验证")
    @Return(desc = "获取登录用户集市查询存储配置表", range = "返回值取值范围")
    public List<Data_store_layer> searchDataStore() {
        return Dbo.queryList(Data_store_layer.class, "SELECT * from " + Data_store_layer.TableName);
    }

    @Method(desc = "获取登录用户集市查询存储配置表（模糊查询）",
            logicStep = "数据权限验证: 根据登录用户进行数据验证")
    @Param(name = "fuzzyqueryitem", desc = "fuzzyqueryitem", range = "模糊查询字段", nullable = true)
    @Return(desc = "用户集市查询存储配置表", range = "返回值取值范围")
    public List<Data_store_layer> searchDataStoreByFuzzyQuery(String fuzzyqueryitem) {
        return Dbo.queryList(Data_store_layer.class, "select * from " + Data_store_layer.TableName + " where dsl_name like ?",
                "%" + fuzzyqueryitem + "%");
    }


    @Method(desc = "保存集市添加表页面1的信息，新增集市表",
            logicStep = "1.检查数据合法性" +
                    "2.新增时前查询集市表英文名是否已存在" +
                    "3.新增时对Dm_datatable初始化一些非页面传值" +
                    "4.保存Dm_datatable信息" +
                    "5.新增数据至dm_relation_datatable" +
                    "6 返回主键datatable_id")
    @Param(name = "dm_datatable", desc = "dm_datatable", range = "与dm_datatable表字段规则一致",
            isBean = true)
    @Param(name = "dsl_id", desc = "dsl_id", range = "与Dm_info表字段规则一致")
    public Map<String, String> addDMDataTable(Dm_datatable dm_datatable, String dsl_id) {
        Map<String, String> map = new HashMap<String, String>();
        //1检查数据合法性
        CheckColummn(dm_datatable.getDatatable_en_name(), "表英文名");
        CheckColummn(dm_datatable.getDatatable_cn_name(), "表中文名");
        CheckColummn(dm_datatable.getSql_engine(), "Sql执行引擎");
        CheckColummn(dm_datatable.getTable_storage(), "数据存储方式");
        CheckColummn(dm_datatable.getStorage_type(), "进数方式");
        CheckColummn(dm_datatable.getDatatable_lifecycle(), "数据生命周期");
        if (TableLifeCycle.LinShi.getCode().equalsIgnoreCase(dm_datatable.getDatatable_lifecycle())) {
            CheckColummn(dm_datatable.getDatatable_due_date(), "数据表到期日期");
            dm_datatable.setDatatable_due_date(dm_datatable.getDatatable_due_date().substring(0, 10).replace("-", ""));
        } else {
            dm_datatable.setDatatable_due_date(Final_Date);
        }
        CheckColummn(dsl_id, "数据存储");
        //2检查表名重复
        if (Dbo.queryNumber("select count(*) from " + Dm_datatable.TableName + " where  datatable_en_name = ?", dm_datatable.getDatatable_en_name())
                .orElseThrow(() -> new BusinessException("sql查询错误！")) != 0) {
            throw new BusinessException("表英文名重复，请重新填写");
        }
        //3.对Dm_datatable初始化一些非页面传值
        String datatable_id = PrimayKeyGener.getNextId();
        dm_datatable.setDatatable_id(datatable_id);
        dm_datatable.setDatatable_create_date(DateUtil.getSysDate());
        dm_datatable.setDatatable_create_time(DateUtil.getSysTime());
        dm_datatable.setDdlc_date(DateUtil.getSysDate());
        dm_datatable.setDdlc_time(DateUtil.getSysTime());
        dm_datatable.setDatac_date(DateUtil.getSysTime());
        dm_datatable.setDatac_time(DateUtil.getSysTime());
        //TODO 目前先全部使用默认 之后在修改
        dm_datatable.setSql_engine(SqlEngine.MOREN.getCode());
        dm_datatable.setSoruce_size(Zero);
        dm_datatable.setEtl_date(ZeroDate);
        //TODO 见Category_id处的注释
        dm_datatable.setCategory_id(PrimayKeyGener.getNextId());
        //4.保存dm_datatable信息
        dm_datatable.add(Dbo.db());
        //5.新增数据至dm_relation_datatable
        Dm_relation_datatable dm_relation_datatable = new Dm_relation_datatable();
        dm_relation_datatable.setDsl_id(dsl_id);
        dm_relation_datatable.setDatatable_id(datatable_id);
        dm_relation_datatable.setIs_successful(IsFlag.Fou.getCode());
        dm_relation_datatable.add(Dbo.db());
        //6 返回主键datatable_id
        map.put("datatable_id", datatable_id);
        return map;

    }

    @Method(desc = "编辑更新集市添加表页面1的信息，更新集市表",
            logicStep = "1.检查数据合法性" +
                    "3.新增时对Dm_datatable初始化一些非页面传值" +
                    "4.保存或者更新Dm_datatable信息" +
                    "4.编辑时，删除Dm_datatable中原有信息" +
                    "5.新增数据至dm_relation_datatable" +
                    "6 返回主键datatable_id")
    @Param(name = "dm_datatable", desc = "dm_datatable", range = "与dm_datatable表字段规则一致",
            isBean = true)
    @Param(name = "dsl_id", desc = "dsl_id", range = "与Dm_info表字段规则一致")
    public Map<String, String> updateDMDataTable(Dm_datatable dm_datatable, String dsl_id) {
        Map<String, String> map = new HashMap<String, String>();
        //1检查数据合法性
        CheckColummn(dm_datatable.getDatatable_en_name(), "表英文名");
        CheckColummn(dm_datatable.getDatatable_cn_name(), "表中文名");
        CheckColummn(dm_datatable.getSql_engine(), "Sql执行引擎");
        CheckColummn(dm_datatable.getTable_storage(), "数据存储方式");
        CheckColummn(dm_datatable.getStorage_type(), "进数方式");
        CheckColummn(dm_datatable.getDatatable_lifecycle(), "数据生命周期");
        if (TableLifeCycle.LinShi.getCode().equalsIgnoreCase(dm_datatable.getDatatable_lifecycle())) {
            CheckColummn(dm_datatable.getDatatable_due_date(), "数据表到期日期");
            dm_datatable.setDatatable_due_date(dm_datatable.getDatatable_due_date().substring(0, 10).replace("-", ""));
        } else {
            dm_datatable.setDatatable_due_date(Final_Date);
        }
        //TODO 目前先全部使用默认 之后在修改
        dm_datatable.setSql_engine(SqlEngine.MOREN.getCode());
        //TODO 见Category_id处的注释
        dm_datatable.setCategory_id(PrimayKeyGener.getNextId());
        //4.dm_datatable
        dm_datatable.update(Dbo.db());
        //删除之前dm_relation_datatable库中的数据
        Dbo.execute("delete from " + Dm_relation_datatable.TableName + " where datatable_id = ? ", dm_datatable.getDatatable_id());
        //5.新增数据至dm_relation_datatable
        Dm_relation_datatable dm_relation_datatable = new Dm_relation_datatable();
        dm_relation_datatable.setDsl_id(dsl_id);
        dm_relation_datatable.setDatatable_id(dm_datatable.getDatatable_id());
        dm_relation_datatable.setIs_successful(IsFlag.Fou.getCode());
        dm_relation_datatable.add(Dbo.db());
        //6 返回主键datatable_id
        map.put("datatable_id", String.valueOf(dm_datatable.getDatatable_id()));
        return map;

    }

    @Method(desc = "获取集市表的信息",
            logicStep = "根据数据集市表ID进行查询")
    @Param(name = "datatable_id", desc = "datatable_id", range = "datatable_id")
    @Return(desc = "当前集市表的信息", range = "返回值取值范围")
    public List<Map<String, Object>> queryDMDataTableByDataTableId(String datatable_id) {
        Dm_datatable dm_datatable = new Dm_datatable();
        dm_datatable.setDatatable_id(datatable_id);
        return Dbo.queryList("select * from " + Dm_datatable.TableName + " t1 left join " + Dm_relation_datatable.TableName + " t2 " +
                "on t1.datatable_id = t2.datatable_id where t1.datatable_id= ?", dm_datatable.getDatatable_id());
    }

    //TODO
    @Method(desc = "根据SQL获取采集数据，默认显示10条",
            logicStep = "1.处理SQL" +
                    "2.查询SQL")
    @Param(name = "querysql", desc = "查询SQL", range = "String类型SQL")
    @Param(name = "sqlparameter", desc = "SQL参数", range = "String类型参数", nullable = true)
    @Param(name = "datatable_id", desc = "datatable_id", range = "String类型集市表ID ")
    @Return(desc = "查询返回结果集", range = "无限制")
    public Map<String, Object> getDataBySQL(String querysql, String sqlparameter,String datatable_id) {
        //1.处理SQL
        DruidParseQuerySql druidParseQuerySql = new DruidParseQuerySql();
        querysql = druidParseQuerySql.GetNewSql(querysql);
        if (!StringUtils.isEmpty(sqlparameter)) {
            // 获取参数组
            String[] singlePara = StringUtils.split(sqlparameter, ';');// 获取单个动态参数
            for (int i = 0; i < singlePara.length; i++) {
                String[] col_val = StringUtils.split(singlePara[i], '=');
                if (col_val.length > 1) {
                    // 按顺序从左到右对原始sql中的(0)进行替换
                    querysql = StringUtils.replace(querysql, "#{" + col_val[0].trim() + "}", col_val[1]);
                }
            }
        }
        querysql = querysql.trim();
        if (querysql.endsWith(";")) {
            //去除分号
            querysql = querysql.substring(0, querysql.length() - 1);
        }
        querysql = getlimitsql(querysql,datatable_id);
        //2.查询SQL TODO
        Map<String, Object> resultmap = new HashMap<String, Object>();
        try {
            List<Map<String, Object>> maps = new ArrayList<>();
            ProcessingData processingData = new ProcessingData() {
                @Override
                public void dealLine(Map<String, Object> map) throws Exception {
                    //因为限制了limit 100所以此处可以将数据存放在内存中进行处理
                    maps.add(map);
                }
            };
            processingData.getDataLayer(querysql,Dbo.db());
            resultmap.put("result", maps);
            resultmap.put("success", true);
        } catch (Exception e) {
            resultmap.put("success", false);
            resultmap.put("message", e.getMessage());
        }
        return resultmap;
    }

    /**
     * 处理oracle部分的limit问题
     * @param querysql
     * @param datatable_id
     * @return
     */
    private String getlimitsql(String querysql,String datatable_id) {
        Dm_datatable dm_datatable = new Dm_datatable();
        dm_datatable.setDatatable_id(datatable_id);
        List<Map<String, Object>> maps = Dbo.queryList("select t1.* from data_store_layer_attr t1 left join dm_relation_datatable t2 on t1.dsl_id = t2.dsl_id " +
                "where t2.datatable_id = ? and lower(t1.storage_property_key) = ? and t1.storage_property_val like ?",dm_datatable.getDatatable_id(),jdbc_url,"%oracle%");
        if(maps.isEmpty()){
            querysql = "select * from (" + querysql + ") as " + alias + " limit " + LimitNumber;
        }else{
            querysql = "select * from (" + querysql + ") as " + alias + " where rownum =  " + LimitNumber;
        }
        return querysql;
    }

    @Method(desc = "根据数据表ID,获取数据库类型，获取选中数据库的附件属性字段",
            logicStep = "查询数据库，返回结果")
    @Param(name = "datatable_id", desc = "datatable_id", range = "String类型集市表ID")
    @Return(desc = "查询返回结果集", range = "无限制")
    public List<Map<String, Object>> getColumnMore(String datatable_id) {
        Dm_datatable dm_datatable = new Dm_datatable();
        dm_datatable.setDatatable_id(datatable_id);
        return Dbo.queryList("select dslad_id,dsla_storelayer from " + Data_store_layer_added.TableName + " t1 " +
                "left join " + Dm_relation_datatable.TableName + " t2 on t1.dsl_id = t2.dsl_id " +
                "where t2.datatable_id = ? order by dsla_storelayer", dm_datatable.getDatatable_id());
    }

    @Method(desc = "根据SQL获取列结构",
            logicStep = "1.根据SQL解析获取所有字段" +
                    "2.设置默认的字段类型" +
                    "3.根据血缘来分析目标字段来源字段的字段类型，并且转换字段类型" +
                    "4.设置默认附加字段属性不勾选" +
                    "5.返回结果")
    @Param(name = "querysql", desc = "查询SQL", range = "String类型SQL")
    @Param(name = "datatable_id", desc = "datatable_id", range = "String类型集市表ID")
    @Param(name = "sqlparameter", desc = "SQL参数", range = "String类型参数", nullable = true)
    @Return(desc = "列结构", range = "无限制")
    public Map<String, Object> getColumnBySql(String querysql, String datatable_id, String sqlparameter) {
        Map<String, Object> resultmap = new HashMap<>();
        List<Map<String, Object>> resultlist = new ArrayList<Map<String, Object>>();
        CheckColummn(querysql, "查询sql");
        Dm_datatable dm_datatable = new Dm_datatable();
        dm_datatable.setDatatable_id(datatable_id);
        List<Map<String, Object>> storeTypeList = Dbo.queryList("select store_type from " + Data_store_layer.TableName + " t1 left join " + Dm_relation_datatable.TableName + " t2 on t1.dsl_id = t2.dsl_id " +
                "where t2.datatable_id = ? limit 1", dm_datatable.getDatatable_id());
        String storeType = storeTypeList.get(0).get("store_type").toString();
        String field_type = getDefaultFieldType(storeType);
        List<String> columnNameList = new ArrayList<>();
        HashMap<String, Object> bloodRelationMap = new HashMap<>();
        //此处进行获取血缘关系map 如果sql写的不规范 会存在报错
        try {
            DruidParseQuerySql druidParseQuerySql = new DruidParseQuerySql(querysql);
            columnNameList = druidParseQuerySql.parseSelectAliasField();
            DruidParseQuerySql dpqs = new DruidParseQuerySql();
            bloodRelationMap = dpqs.getBloodRelationMap(querysql);
        } catch (Exception e) {
            String message = e.getMessage();
            if (!StringUtils.isEmpty(message)) {
                resultmap.put("success", false);
                resultmap.put("message", e.getMessage());
                return resultmap;
            }
            //如果druid解析错误 并且没有返回信息 说明sql存在问题 用获取sql查询结果的方法返回错误信息
            else {
                return getDataBySQL(querysql, sqlparameter,datatable_id);
            }
        }
        String targetfield_type = "";
        for (String everyColumnName : columnNameList) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("field_en_name", everyColumnName);
            map.put("field_cn_name", everyColumnName);
            Object object = bloodRelationMap.get(everyColumnName);
            if (null == object) {
                targetfield_type = field_type;
            } else {
                ArrayList<HashMap<String, Object>> list = (ArrayList<HashMap<String, Object>>) object;
                if (list.size() == 1) {
                    HashMap<String, Object> stringObjectHashMap = list.get(0);
                    String sourcetable = stringObjectHashMap.get(DruidParseQuerySql.sourcetable).toString();
                    String sourcecolumn = stringObjectHashMap.get(DruidParseQuerySql.sourcecolumn).toString();
                    String dsl_id = Dbo.queryList("select dsl_id from dm_relation_datatable where datatable_id = ?", dm_datatable.getDatatable_id())
                            .get(0).get("dsl_id").toString();
                    targetfield_type = getFieldType(sourcetable, sourcecolumn, field_type, dsl_id).get("targettype");
                } else {
                    targetfield_type = field_type;
                }
            }
            map.put("field_type", targetfield_type);
            map.put("field_process", ProcessType.YingShe.getCode());
            //将所有勾选的 附加字段属性 默认选为不勾选
            List<Map<String, Object>> dslaStorelayerList = Dbo.queryList("select dslad_id,dsla_storelayer from " + Data_store_layer_added.TableName + " t1 " +
                    "left join " + Dm_relation_datatable.TableName + " t2 on t1.dsl_id = t2.dsl_id " +
                    "where t2.datatable_id = ? order by dsla_storelayer", dm_datatable.getDatatable_id());
            for (Map<String, Object> dslaStorelayeMap : dslaStorelayerList) {
                map.put(StoreLayerAdded.ofValueByCode(dslaStorelayeMap.get("dsla_storelayer").toString()), false);
            }
            resultlist.add(map);
        }
        resultmap.put("result", resultlist);
        resultmap.put("success", true);
        return resultmap;
    }

    /**
     * @param sourcetable
     * @param sourcecolumn
     * @param field_type   默认的字段类型 String/Varchar
     * @param dsl_id
     * @return
     */
    private Map<String, String> getFieldType(String sourcetable, String sourcecolumn, String field_type, String dsl_id) {
        Map<String, String> resultmap = new HashMap<>();
        List<LayerBean> layerByTable = ProcessingData.getLayerByTable(sourcetable, Dbo.db());
        if (layerByTable == null || layerByTable.isEmpty()) {
            //如果没有找到该表属于哪一层 则返回原始类型
            resultmap.put("sourcetype", field_type);
            resultmap.put("targettype", field_type);
            return resultmap;
        } else {
            String dataSourceType = layerByTable.get(0).getDst();
            if (dataSourceType.equals(DataSourceType.DCL.getCode())) {
                List<Map<String, Object>> maps = Dbo.queryList("select t2.column_type,t4.dsl_id from " + Data_store_reg.TableName + " t1 left join " + Table_column.TableName + " t2 on t1.table_id = t2.table_id" +
                                " left join " + Table_storage_info.TableName + " t3 on t1.table_id = t3.table_id left join " +
                                Data_relation_table.TableName + " t4 on t4.storage_id = t3.storage_id " +
                                "where lower(t2.column_name) = ? and lower(t1.hyren_name) = ? limit 1",
                        sourcecolumn.toLowerCase(), sourcetable.toLowerCase());
                String column_type = maps.get(0).get("column_type").toString();
                String DCLdsl_id = maps.get(0).get("dsl_id").toString();
                //如果为空，说明字段不存在
                if (StringUtils.isEmpty(column_type)) {
                    resultmap.put("sourcetype", field_type);
                    resultmap.put("targettype", field_type);
                    return resultmap;
                } else {
                    //如果是来自帖源的话 就需要做两次转换
                    resultmap.put("sourcetype", column_type);
                    column_type = transFormColumnType(column_type, DCLdsl_id);
                    column_type = transFormColumnType(column_type, dsl_id);
                    resultmap.put("targettype", column_type);
                    return resultmap;
                }
            } else if (dataSourceType.equals(DataSourceType.DML.getCode())) {
                List<Map<String, Object>> maps = Dbo.queryList("select field_type from " + Datatable_field_info.TableName + " t1 left join " + Dm_datatable.TableName +
                        " t2 on t1.datatable_id = t2.datatable_id where lower(t2.datatable_en_name) = ? and lower(t1.field_en_name) = ?", sourcetable.toLowerCase(), sourcecolumn);
                String DMLfield_type = maps.get(0).get("field_type").toString();
                if (StringUtils.isEmpty(DMLfield_type)) {
                    resultmap.put("sourcetype", field_type);
                    resultmap.put("targettype", field_type);
                    return resultmap;
                } else {
                    resultmap.put("sourcetype", DMLfield_type);
                    DMLfield_type = transFormColumnType(DMLfield_type, dsl_id);
                    resultmap.put("targettype", DMLfield_type);
                    return resultmap;
                }
            } else {
                resultmap.put("sourcetype", field_type);
                resultmap.put("targettype", field_type);
                return resultmap;
            }
            //TODO 之后层级加入 还需要补充
        }
    }

    /**
     * 根据数据类型转换表 进行字段类型的转换
     *
     * @param column_type
     * @param dsl_id
     * @return
     */
    private String transFormColumnType(String column_type, String dsl_id) {
        if (StringUtils.isEmpty(dsl_id)) {
            return column_type;
        }
        if (column_type.contains("(")) {
            column_type = column_type.substring(0, column_type.indexOf("("));
        }
        Data_store_layer data_store_layer = new Data_store_layer();
        data_store_layer.setDsl_id(dsl_id);
        List<Map<String, Object>> maps = Dbo.queryList("select target_type from " + Type_contrast.TableName + " t1 left join " + Data_store_layer.TableName + " t2 on t1.dtcs_id = t2.dtcs_id " +
                "where t2.dsl_id = ? and lower(t1.source_type) = ?", data_store_layer.getDsl_id(), column_type);
        if (maps.isEmpty()) {
            return column_type;
        } else {
            return maps.get(0).get("target_type").toString();
        }
    }


    @Method(desc = "回显新增集市页面2中记录在数据库中的字段信息",
            logicStep = "1.查询所有字段" +
                    "2.判断附加属性是否勾选" +
                    "3.返回结果")
    @Param(name = "datatable_id", desc = "datatable_id", range = "String类型集市表ID")
    @Return(desc = "列结构", range = "无限制")
    public List<Map<String, Object>> getColumnFromDatabase(String datatable_id) {
        Dm_datatable dm_datatable = new Dm_datatable();
        dm_datatable.setDatatable_id(datatable_id);
        List<Map<String, Object>> list = Dbo.queryList("select * from " + Datatable_field_info.TableName +
                " where datatable_id = ? order by field_seq", dm_datatable.getDatatable_id());
        Datatable_field_info datatable_field_info = new Datatable_field_info();
        for (Map<String, Object> map : list) {
            String datatable_field_id = map.get("datatable_field_id").toString();
            datatable_field_info.setDatatable_field_id(datatable_field_id);
            List<Map<String, Object>> list2 = Dbo.queryList("select dsla_storelayer from " + Data_store_layer_added.TableName + "" +
                            " t1 left join " + Dm_column_storage.TableName + " t2 on t1.dslad_id = t2.dslad_id where t2.datatable_field_id = ?",
                    datatable_field_info.getDatatable_field_id());
            if (list2 != null) {
                for (Map<String, Object> everymap : list2) {
                    String dsla_storelayer = everymap.get("dsla_storelayer").toString();
                    map.put(StoreLayerAdded.ofValueByCode(dsla_storelayer), true);
                }
            }
        }
        return list;
    }

    @Method(desc = "根据集市表ID,获取字段类型的所有类型",
            logicStep = "1.获取所有字段类型" +
                    "2.判断默认类型是否包含在所有字段类型中" +
                    "3.返回结果")
    @Param(name = "datatable_id", desc = "datatable_id", range = "String类型集市表ID")
    @Return(desc = "查询返回结果集", range = "无限制")
    public List<Map<String, Object>> getAllField_Type(String datatable_id) {
        Dm_datatable dm_datatable = new Dm_datatable();
        dm_datatable.setDatatable_id(datatable_id);
        List<Map<String, Object>> storeTypeList = Dbo.queryList("select store_type from " + Data_store_layer.TableName + " t1 left join " + Dm_relation_datatable.TableName + " t2 on t1.dsl_id = t2.dsl_id " +
                "where t2.datatable_id = ? ", dm_datatable.getDatatable_id());
        String storeType = storeTypeList.get(0).get("store_type").toString();
        String field_type = getDefaultFieldType(storeType);
        List<Map<String, Object>> targetTypeList = Dbo.queryList("SELECT distinct lower(replace(replace(trim(t1.target_type),'(',''),')','')) as target_type " +
                "FROM " + Type_contrast.TableName + " t1 LEFT JOIN " + Data_store_layer.TableName + " t2 ON t1.dtcs_id = t2.dtcs_id " +
                "LEFT JOIN " + Dm_relation_datatable.TableName + " t3 ON t2.dsl_id=t3.dsl_id" +
                " WHERE t3.datatable_id = ?", dm_datatable.getDatatable_id());
        Boolean flag = true;
        Map<String, Object> tempmap = new HashMap<>();
        tempmap.put("target_type", field_type);
        if (!targetTypeList.contains(tempmap)) {
            targetTypeList.add(tempmap);
        }
        return targetTypeList;

    }

    /**
     * 设置一个默认的字段类型 以便于对于多字段合成的字段类型进行初始化
     *
     * @param storeType
     * @return
     */
    private String getDefaultFieldType(String storeType) {
        //TODO 分类讨论 目前只考虑关系性数据库、hive、hbase这三种情况
        String field_type = "";
        if (storeType.equals(Store_type.DATABASE.getCode())) {
            field_type = "varchar";
        } else if (storeType.equals(Store_type.HIVE.getCode())) {
            field_type = "string";
        } else if (storeType.equals(Store_type.HBASE.getCode())) {
            field_type = "string";
        }
        return field_type;
    }

    @Method(desc = "保存新增集市2的数据",
            logicStep = "1.检查页面数据合法性" +
                    "2.删除相关6张表中的数据" +
                    "3.保存数据进入数据库")
    @Param(name = "datatable_field_info", desc = "datatable_field_info", range = "与Datatable_field_info表字段规则一致",
            isBean = true)
    @Param(name = "dm_column_storage", desc = "dm_column_storage", range = "与Dm_column_storage表字段规则一致",
            isBean = true)
    @Param(name = "datatable_id", desc = "datatable_id", range = "String类型集市表ID")
    @Param(name = "querysql", desc = "querysql", range = "String类型集市查询SQL")
    @Param(name = "hbasesort", desc = "hbasesort", range = "hbaserowkey的排序")
    public void addDFInfo(Datatable_field_info[] datatable_field_info, String datatable_id, Dm_column_storage[] dm_column_storage, String querysql, String hbasesort) {
        for (int i = 0; i < datatable_field_info.length; i++) {
            Datatable_field_info df_info = datatable_field_info[i];
            CheckColummn(df_info.getField_en_name(), "字段英文名第" + (i + 1) + "个");
            CheckColummn(df_info.getField_cn_name(), "字段中文名" + (i + 1) + "个");
            CheckColummn(df_info.getField_type(), "字段类型" + (i + 1) + "个");
            CheckColummn(df_info.getField_process(), "字段处理方式" + (i + 1) + "个");
        }
        Dm_datatable dm_datatable = new Dm_datatable();
        dm_datatable.setDatatable_id(datatable_id);
        List<Map<String, Object>> maps2 = Dbo.queryList("select execute_sql from " + Dm_operation_info.TableName + " where datatable_id = ?", dm_datatable.getDatatable_id());
        Object object_execute_sql = maps2.get(0).get("execute_sql");
        if (object_execute_sql == null) {
            dm_datatable.setDdlc_date(DateUtil.getSysDate());
            dm_datatable.setDdlc_time(DateUtil.getSysTime());
            dm_datatable.update(Dbo.db());
        } else {
            String execute_sql = object_execute_sql.toString();
            if (!execute_sql.equals(querysql)) {
                dm_datatable.setDdlc_date(DateUtil.getSysDate());
                dm_datatable.setDdlc_time(DateUtil.getSysTime());
                dm_datatable.update(Dbo.db());
            }
        }
        Dbo.execute("delete from " + Dm_column_storage.TableName + " where datatable_field_id in (select datatable_field_id from " +
                Datatable_field_info.TableName + " where datatable_id = ?)", dm_datatable.getDatatable_id());
        Dbo.execute("delete from " + Datatable_field_info.TableName + " where datatable_id = ?", dm_datatable.getDatatable_id());
        Dbo.execute("delete from " + Dm_operation_info.TableName + " where datatable_id = ?", dm_datatable.getDatatable_id());
        Dbo.execute("delete from " + Own_source_field.TableName + " where own_dource_table_id in " +
                "(select own_dource_table_id from dm_datatable_source where datatable_id =  ?)", dm_datatable.getDatatable_id());
        Dbo.execute("delete from " + Dm_datatable_source.TableName + " where datatable_id = ?", dm_datatable.getDatatable_id());
        Dbo.execute("delete from " + Dm_etlmap_info.TableName + " where datatable_id = ?", dm_datatable.getDatatable_id());
        for (int i = 0; i < datatable_field_info.length; i++) {
            Datatable_field_info df_info = datatable_field_info[i];
            String datatable_field_id = PrimayKeyGener.getNextId();
            df_info.setDatatable_field_id(datatable_field_id);
            df_info.setDatatable_id(datatable_id);
            df_info.setField_seq(String.valueOf(i));
            df_info.add(Dbo.db());
        }
        for (int i = 0; i < dm_column_storage.length; i++) {
            Dm_column_storage dc_storage = dm_column_storage[i];
            dc_storage.setDatatable_field_id(datatable_field_info[dc_storage.getCsi_number().intValue()].getDatatable_field_id());
            dc_storage.add(Dbo.db());
        }
        //排序dc_storage
        JSONArray jsonarray = JSONArray.parseArray(hbasesort);
        List<Map<String, Object>> maps = Dbo.queryList("select distinct t1.dslad_id,t2.dsla_storelayer from " + Dm_column_storage.TableName
                + " t1 left join " + Data_store_layer_added.TableName + " t2 on t1.dslad_id = t2.dslad_id where datatable_field_id in " +
                "(select datatable_field_id from " + Datatable_field_info.TableName + " where datatable_id = ? )", dm_datatable.getDatatable_id());
        for (Map<String, Object> everymap : maps) {
            String dslad_id = everymap.get("dslad_id").toString();
            String dsla_storelayer = everymap.get("dsla_storelayer").toString();
            Dm_column_storage dcs = new Dm_column_storage();
            dcs.setDslad_id(dslad_id);
            //如果是rowkey的话 排序的时候 需要根据hbasesort来排序
            if (dsla_storelayer.equals(StoreLayerAdded.RowKey.getCode())) {
                for (int i = 0; i < jsonarray.size(); i++) {
                    JSONObject jsonObject = jsonarray.getJSONObject(i);
                    String field_en_name = jsonObject.getString("field_en_name");
                    Datatable_field_info datatable_field_info1 = new Datatable_field_info();
                    datatable_field_info1.setField_en_name(field_en_name);
                    List<Map<String, Object>> maps1 = Dbo.queryList("select * from " + Dm_column_storage.TableName + " where datatable_field_id = " +
                                    "(select datatable_field_id from " + Datatable_field_info.TableName + " where datatable_id = ? and field_en_name = ? )" +
                                    " and dslad_id = ? ",
                            dm_datatable.getDatatable_id(), datatable_field_info1.getField_en_name(), dcs.getDslad_id());
                    Map<String, Object> everymap1 = maps1.get(0);
                    Dm_column_storage dc_storage = new Dm_column_storage();
                    dc_storage.setDatatable_field_id(everymap1.get("datatable_field_id").toString());
                    dc_storage.setDslad_id(everymap1.get("dslad_id").toString());
                    dc_storage.setCsi_number(String.valueOf(i));
                    dc_storage.update(Dbo.db());
                }
            }
            //如果不是rowkey 那么排序的时候 只需简单排序即可
            else {
                List<Map<String, Object>> maps1 = Dbo.queryList("select * from " + Dm_column_storage.TableName + " where datatable_field_id in " +
                                "(select datatable_field_id from " + Datatable_field_info.TableName + " where datatable_id = ? ) and dslad_id = ? order by csi_number",
                        dm_datatable.getDatatable_id(), dcs.getDslad_id());
                for (int i = 0; i < maps1.size(); i++) {
                    Map<String, Object> everymap1 = maps1.get(i);
                    Dm_column_storage dc_storage = new Dm_column_storage();
                    dc_storage.setDatatable_field_id(everymap1.get("datatable_field_id").toString());
                    dc_storage.setDslad_id(everymap1.get("dslad_id").toString());
                    dc_storage.setCsi_number(String.valueOf(i));
                    dc_storage.update(Dbo.db());
                }
            }
        }
        Dm_operation_info dm_operation_info = new Dm_operation_info();
        dm_operation_info.setId(PrimayKeyGener.getNextId());
        dm_operation_info.setDatatable_id(datatable_id);
        dm_operation_info.setExecute_sql(querysql);
        dm_operation_info.add(Dbo.db());

        DruidParseQuerySql dpqs = new DruidParseQuerySql();
        HashMap<String, Object> bloodRelationMap = dpqs.getBloodRelationMap(querysql);
        Iterator<Map.Entry<String, Object>> iterator = bloodRelationMap.entrySet().iterator();
        Map<String, Object> tableMap = new HashMap<>();
        //重新整理数据结构，原本的map key是目标字段 新的tableMap的数据结构key为来源表的表名
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String columnname = entry.getKey();
            ArrayList<HashMap<String, Object>> list = (ArrayList<HashMap<String, Object>>) entry.getValue();
            for (HashMap<String, Object> map : list) {
                String sourcecolumn = map.get(DruidParseQuerySql.sourcecolumn).toString().toLowerCase();
                String sourcetable = map.get(DruidParseQuerySql.sourcetable).toString().toLowerCase();
                List<Map<String, Object>> templist = new ArrayList<>();
                if (!tableMap.containsKey(sourcetable)) {
                    tableMap.put(sourcetable, templist);
                } else {
                    templist = (ArrayList<Map<String, Object>>) tableMap.get(sourcetable);
                }
                Map<String, Object> tempmap = new HashMap<>();
                tempmap.put(TargetColumn, columnname.toLowerCase());
                tempmap.put(SourceColumn, sourcecolumn.toLowerCase());
                templist.add(tempmap);
                tableMap.put(sourcetable, templist);
            }
        }
        iterator = tableMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String tablename = entry.getKey();
            String dataSourceType = "";
            List<LayerBean> layerByTable = ProcessingData.getLayerByTable(tablename, Dbo.db());
            //TODO 如果所涉及到的表找不到层级 则使用UDL(自定义层）
            if (layerByTable == null || layerByTable.isEmpty()) {
                dataSourceType = DataSourceType.UDL.getCode();
            } else {
                dataSourceType = layerByTable.get(0).getDst();
            }
            Dm_datatable_source dm_datatable_source = new Dm_datatable_source();
            String own_dource_table_id = PrimayKeyGener.getNextId();
            dm_datatable_source.setOwn_dource_table_id(own_dource_table_id);
            dm_datatable_source.setDatatable_id(datatable_id);
            dm_datatable_source.setOwn_source_table_name(tablename);
            dm_datatable_source.setSource_type(dataSourceType);
            dm_datatable_source.add(Dbo.db());
            List<Map<String, Object>> templist = (ArrayList<Map<String, Object>>) tableMap.get(tablename.toLowerCase());
            for (Map<String, Object> map : templist) {
                String targetcolumn = map.get(TargetColumn).toString();
                String sourcecolumn = map.get(SourceColumn).toString();
                Dm_etlmap_info dm_etlmap_info = new Dm_etlmap_info();
                dm_etlmap_info.setEtl_id(PrimayKeyGener.getNextId());
                dm_etlmap_info.setDatatable_id(datatable_id);
                dm_etlmap_info.setOwn_dource_table_id(own_dource_table_id);
                dm_etlmap_info.setSourcefields_name(sourcecolumn);
                dm_etlmap_info.setTargetfield_name(targetcolumn);
                dm_etlmap_info.add(Dbo.db());
                Own_source_field own_source_field = new Own_source_field();
                own_source_field.setOwn_dource_table_id(own_dource_table_id);
                own_source_field.setOwn_field_id(PrimayKeyGener.getNextId());
                own_source_field.setField_name(sourcecolumn);
                String target_type = "string";
                Map<String, String> fieldType = getFieldType(tablename, sourcecolumn, target_type, "");
                String sourcetype = fieldType.get("sourcetype");
                own_source_field.setField_type(sourcetype);
                own_source_field.add(Dbo.db());
            }
        }


    }

    @Method(desc = "根据集市表ID,获取SQL回显",
            logicStep = "返回查询结果")
    @Param(name = "datatable_id", desc = "datatable_id", range = "String类型集市表ID")
    @Return(desc = "查询返回结果集", range = "无限制")
    public List<Map<String, Object>> getQuerySql(String datatable_id) {
        Dm_datatable dm_datatable = new Dm_datatable();
        dm_datatable.setDatatable_id(datatable_id);
        return Dbo.queryList("select execute_sql as querysql from " + Dm_operation_info.TableName + " t1 where " +
                "datatable_id = ?", dm_datatable.getDatatable_id());
    }

    @Method(desc = "根据集市表ID，判断是否是进入Hbase的目的地",
            logicStep = "判断目的地是否为hbase")
    @Param(name = "datatable_id", desc = "datatable_id", range = "String类型集市表ID")
    @Return(desc = "返回true或者false", range = "无限制")
    public Map<String, Object> getIfHbase(String datatable_id) {
        Map<String, Object> map = new HashMap<>();
        Dm_datatable dm_datatable = new Dm_datatable();
        dm_datatable.setDatatable_id(datatable_id);
        List<Map<String, Object>> maps = Dbo.queryList("select * from " + Data_store_layer.TableName + " t1 left join " + Dm_relation_datatable.TableName + " t2 " +
                "on t1.dsl_id = t2.dsl_id where t2.datatable_id = ? and t1.store_type = ? ", dm_datatable.getDatatable_id(), Store_type.HBASE.getCode());
        if (maps.size() > 0) {
            map.put("result", true);
            return map;
        } else {
            map.put("result", false);
            return map;
        }
    }

    @Method(desc = "回显hbase的rowkey排序",
            logicStep = "1.查询结果" +
                    "2.与页面选中的字段名称进行匹配，如果匹配到，就顺序放在前面，如果匹配不到，就顺序放到后面" +
                    "3.返回结果")
    @Param(name = "datatable_id", desc = "datatable_id", range = "String类型集市表ID")
    @Param(name = "hbasesort", desc = "hbasesort", range = "hbaserowkey的排序")
    @Return(desc = "排序完成后的hbasesort", range = "无限制")
    public List<Map<String, Object>> sortHbae(String datatable_id, String hbasesort) {
        JSONArray jsonArray = JSONArray.parseArray(hbasesort);
        List<String> enNameList = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String field_en_name = jsonObject.getString("field_en_name");
            enNameList.add(field_en_name);
        }
        Datatable_field_info datatable_field_info = new Datatable_field_info();
        datatable_field_info.setDatatable_id(datatable_id);
        List<Map<String, Object>> list = Dbo.queryList("SELECT t1.csi_number , t3.field_en_name FROM " + Dm_column_storage.TableName +
                " t1 LEFT JOIN " + Data_store_layer_added.TableName + " t2 ON t1.dslad_id = t2.dslad_id " +
                " LEFT JOIN " + Datatable_field_info.TableName + " t3 ON t1.datatable_field_id = t3.datatable_field_id " +
                " WHERE t2.dsla_storelayer = ? AND t1.datatable_field_id IN " +
                " ( SELECT datatable_field_id FROM Datatable_field_info WHERE datatable_id = ?) " +
                " order by csi_number", StoreLayerAdded.RowKey.getCode(), datatable_field_info.getDatatable_id());

        List<Map<String, Object>> resultlist = new ArrayList<>();
        for (Map<String, Object> map : list) {
            Map<String, Object> resultmap = new HashMap<>();
            String field_en_name = map.get("field_en_name").toString();
            String csi_number = map.get("csi_number").toString();
            if (enNameList.contains(field_en_name)) {
                resultmap.put("field_en_name", field_en_name);
                resultlist.add(resultmap);
                enNameList.remove(field_en_name);
            }
        }
        for (String field_en_name : enNameList) {
            Map<String, Object> resultmap = new HashMap<>();
            resultmap.put("field_en_name", field_en_name);
            resultlist.add(resultmap);
            enNameList.remove(field_en_name);
        }
        return resultlist;
    }


    @Method(desc = "获取树的数据信息",
            logicStep = "1.声明获取到 zTreeUtil 的对象" +
                    "2.设置树实体" +
                    "3.调用ZTreeUtil的getTreeDataInfo获取treeData的信息")
    @Param(name = "agent_layer", desc = "数据层类型", range = "String类型", nullable = true)
    @Param(name = "source_id", desc = "数据源id", range = "String类型", nullable = true)
    @Param(name = "classify_id", desc = "分类id", range = "String类型", nullable = true)
    @Param(name = "data_mart_id", desc = "集市id", range = "String类型", nullable = true)
    @Param(name = "category_id", desc = "分类编号", range = "String类型", nullable = true)
    @Param(name = "systemDataType", desc = "系统数据类型", range = "String类型", nullable = true)
    @Param(name = "kafka_id", desc = "kafka数据id", range = "String类型", nullable = true)
    @Param(name = "batch_id", desc = "批量数据id", range = "String类型", nullable = true)
    @Param(name = "groupId", desc = "分组id", range = "String类型", nullable = true)
    @Param(name = "sdm_consumer_id", desc = "消费id", range = "String类型", nullable = true)
    @Param(name = "parent_id", desc = "父id", range = "String类型", nullable = true)
    @Param(name = "tableSpace", desc = "表空间", range = "String类型", nullable = true)
    @Param(name = "database_type", desc = "数据库类型", range = "String类型", nullable = true)
    @Param(name = "isFileCo", desc = "是否文件采集", range = "String类型", valueIfNull = "false")
    @Param(name = "tree_menu_from", desc = "树菜单来源", range = "String类型", nullable = true)
    @Param(name = "isPublicLayer", desc = "公共层", range = "IsFlag代码项1:是,0:否", valueIfNull = "1")
    @Param(name = "isRootNode", desc = "是否为树的根节点标志", range = "IsFlag代码项1:是,0:否", valueIfNull = "1")
    @Return(desc = "树数据Map信息", range = "无限制")
    public Map<String, Object> getTreeDataInfo(String agent_layer, String source_id, String classify_id,
                                               String data_mart_id, String category_id, String systemDataType,
                                               String kafka_id, String batch_id, String groupId, String sdm_consumer_id,
                                               String parent_id, String tableSpace, String database_type,
                                               String isFileCo, String tree_menu_from, String isPublicLayer,
                                               String isRootNode) {
        //1.声明获取到 zTreeUtil 的对象
        ForegroundTreeUtil foregroundTreeUtil = new ForegroundTreeUtil();
        //2.设置树实体
        TreeDataInfo treeDataInfo = new TreeDataInfo();
        treeDataInfo.setAgent_layer(agent_layer);
        treeDataInfo.setSource_id(source_id);
        treeDataInfo.setClassify_id(classify_id);
        treeDataInfo.setData_mart_id(data_mart_id);
        treeDataInfo.setCategory_id(category_id);
        treeDataInfo.setSystemDataType(systemDataType);
        treeDataInfo.setKafka_id(kafka_id);
        treeDataInfo.setBatch_id(batch_id);
        treeDataInfo.setGroupId(groupId);
        treeDataInfo.setSdm_consumer_id(sdm_consumer_id);
        treeDataInfo.setParent_id(parent_id);
        treeDataInfo.setTableSpace(tableSpace);
        treeDataInfo.setDatabaseType(database_type);
        treeDataInfo.setIsFileCo(isFileCo);
        treeDataInfo.setPage_from(tree_menu_from);
        treeDataInfo.setIsPublic(isPublicLayer);
        treeDataInfo.setIsShTable(isRootNode);
        //3.调用ZTreeUtil的getTreeDataInfo获取树数据信息
        Map<String, Object> treeSourcesMap = new HashMap<>();
        treeSourcesMap.put("tree_sources", foregroundTreeUtil.getTreeDataInfo(getUser(), treeDataInfo));
        return treeSourcesMap;
    }


    @Method(desc = "根据集市表ID,获取SQL回显",
            logicStep = "返回查询结果")
    @Param(name = "source", desc = "source", range = "String类型表来源")
    @Param(name = "id", desc = "id", range = "String类型id")
    @Return(desc = "查询返回结果集", range = "无限制")
    public Map<String, Object> queryAllColumnOnTableName(String source, String id) {
        Map<String, Object> resultmap = new HashMap<>();
        if (source.equals(DataSourceType.DCL.getCode())) {
            Table_column table_column = new Table_column();
            table_column.setTable_id(id);
            List<Map<String, Object>> maps = Dbo.queryList("select column_name as columnname,column_type as columntype,false as selectionState from " + Table_column.TableName + " where table_id = ?", table_column.getTable_id());
            resultmap.put("columnresult", maps);
            List<Map<String, Object>> tablenamelist = Dbo.queryList("select hyren_name as tablename from " + Data_store_reg.TableName + " where table_id = ?", table_column.getTable_id());
            resultmap.put("tablename", tablenamelist.get(0).get("tablename"));
            return resultmap;
        } else if (source.equals(DataSourceType.DML.getCode())) {
            Datatable_field_info datatable_field_info = new Datatable_field_info();
            datatable_field_info.setDatatable_id(id);
            List<Map<String, Object>> maps = Dbo.queryList("select field_en_name as columnname,field_type as columntype,false as selectionState from " + Datatable_field_info.TableName + " where datatable_id = ?", datatable_field_info.getDatatable_id());
            resultmap.put("columnresult", maps);
            List<Map<String, Object>> tablenamelist = Dbo.queryList("select datatable_en_name as tablename  from " + Dm_datatable.TableName + " where datatable_id = ?", datatable_field_info.getDatatable_id());
            resultmap.put("tablename", tablenamelist.get(0).get("tablename"));
            return resultmap;
        }
        //TODO 新的层加进来后 还需要补充
        return null;
    }


    @Method(desc = "执行集市作业",
            logicStep = "")
    @Param(name = "datatable_id", desc = "datatable_id", range = "String类型集市表ID")
    @Param(name = "date", desc = "date", range = "String类型跑批日期")
    @Param(name = "parameter", desc = "parameter", range = "动态参数", nullable = true)
    public void excutMartJob(String datatable_id, String date, String parameter) {
        date = date.substring(0, 10).replace("-", "");

    }

    @Method(desc = "查询所有作业调度工程",
            logicStep = "返回查询结果g")
    @Return(desc = "查询返回结果集", range = "无限制")
    public List<Map<String, Object>> queryAllEtlSys() {
        return Dbo.queryList("SELECT * from " + Etl_sys.TableName);
    }


    @Method(desc = "查询所有作业调度工程",
            logicStep = "返回查询结果g")
    @Param(name = "etl_sys_cd", desc = "etl_sys_cd", range = "String类型作业调度工程主键")
    @Return(desc = "查询返回结果集", range = "无限制")
    public List<Map<String, Object>> queryEtlTaskByEtlSys(String etl_sys_cd) {
        Etl_sys etl_sys = new Etl_sys();
        etl_sys.setEtl_sys_cd(etl_sys_cd);
        return Dbo.queryList("select distinct * from " + Etl_sub_sys_list.TableName + " where etl_sys_cd = ?", etl_sys.getEtl_sys_cd());
    }

    @Method(desc = "控制响应头下载工程的hrds信息",
            logicStep = "")
    @Param(name = "data_mart_id", desc = "data_mart_id", range = "String类型集市工程主键")
    @Return(desc = "查询返回结果集", range = "无限制")
    public void downLoadMart(String data_mart_id) {
        String fileName = data_mart_id+".hrds";
        try{
            ResponseUtil.getResponse().reset();
            // 4.设置响应头，控制浏览器下载该文件
            if (RequestUtil.getRequest().getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {
                // 4.1firefox浏览器
                ResponseUtil.getResponse().setHeader("content-disposition", "attachment;filename="
                        + new String(fileName.getBytes(CodecUtil.UTF8_CHARSET), DataBaseCode.ISO_8859_1.getCode()));
            } else {
                // 4.2其它浏览器
                ResponseUtil.getResponse().setHeader("content-disposition", "attachment;filename="
                        + Base64.getEncoder().encodeToString(fileName.getBytes(CodecUtil.UTF8_CHARSET)));
            }
            ResponseUtil.getResponse().setContentType("APPLICATION/OCTET-STREAM");
            // 6.创建输出流
            OutputStream out = ResponseUtil.getResponse().getOutputStream();
            //2.通过文件id获取文件的 byte
            byte[] bye = getdownloadFile(data_mart_id);
            if (bye == null) {
                throw new BusinessException("集市工程下载错误");
            }
            //3.写入输出流，返回结果
            out.write(bye);
            out.flush();
            out.close();
        } catch (IOException e) {
            throw new BusinessException("集市工程下载错误");
        }
    }

    /**
     * 根据data_mart_id 返回工程下的所有信息
     *
     * @param data_mart_id
     * @return
     */
    private byte[] getdownloadFile(String data_mart_id) {
        Map<String, Object> resultmap = new HashMap<>();
        Dm_info dm_info = new Dm_info();
        dm_info.setData_mart_id(data_mart_id);
        //集市工程表
        List<Dm_info> dm_infos = Dbo.queryList(Dm_info.class,"select * from " + Dm_info.TableName + " where data_mart_id = ?", dm_info.getData_mart_id());
        //集市表表
        List<Dm_datatable> dm_datatables = Dbo.queryList(Dm_datatable.class,"select * from " + Dm_datatable.TableName + " where data_mart_id = ?", dm_info.getData_mart_id());
        //sql表
        List<Dm_operation_info> dm_operation_infos = Dbo.queryList(Dm_operation_info.class,"select * from " + Dm_operation_info.TableName + " where datatable_id in " +
                "(select datatable_id from " + Dm_datatable.TableName + " where data_mart_id =  ? )", dm_info.getData_mart_id());
        //血缘表1
        List<Dm_datatable_source> dm_datatable_sources = Dbo.queryList(Dm_datatable_source.class,"select * from " + Dm_datatable_source.TableName + " where datatable_id in " +
                "(select datatable_id from " + Dm_datatable.TableName + " where data_mart_id =  ? )", dm_info.getData_mart_id());
        //血缘表2
        List<Dm_etlmap_info> dm_etlmap_infos = Dbo.queryList(Dm_etlmap_info.class,"select * from " + Dm_etlmap_info.TableName + " where datatable_id in " +
                "(select datatable_id from " + Dm_datatable.TableName + " where data_mart_id =  ? )", dm_info.getData_mart_id());
        //血缘表3
        List<Own_source_field> own_source_fields = Dbo.queryList(Own_source_field.class,"select * from " + Own_source_field.TableName + " where own_dource_table_id in (" +
                "select own_dource_table_id from " + Dm_datatable_source.TableName + " where datatable_id in " +
                "(select datatable_id from " + Dm_datatable.TableName + " where data_mart_id =  ? ))", dm_info.getData_mart_id());
        //字段表
        List<Datatable_field_info> datatable_field_infos = Dbo.queryList(Datatable_field_info.class,"select * from " + Datatable_field_info.TableName + " where datatable_id in " +
                "(select datatable_id from " + Dm_datatable.TableName + " where data_mart_id =  ? )", dm_info.getData_mart_id());
        List<Dm_relation_datatable> dm_relation_datatables = Dbo.queryList(Dm_relation_datatable.class,"select * from " + Dm_relation_datatable.TableName + " where datatable_id in " +
                "(select datatable_id from " + Dm_datatable.TableName + " where data_mart_id =  ? )", dm_info.getData_mart_id());
        List<Dm_column_storage> dm_column_storages = Dbo.queryList(Dm_column_storage.class,"select * from " + Dm_column_storage.TableName + " where datatable_field_id in (" +
                "select datatable_field_id from " + Datatable_field_info.TableName + " where datatable_id in " +
                "(select datatable_id from " + Dm_datatable.TableName + " where data_mart_id =  ? ))", dm_info.getData_mart_id());
        resultmap.put("dm_infos",dm_infos);
        resultmap.put("dm_datatables",dm_datatables);
        resultmap.put("dm_operation_infos",dm_operation_infos);
        resultmap.put("dm_datatable_sources",dm_datatable_sources);
        resultmap.put("dm_etlmap_infos",dm_etlmap_infos);
        resultmap.put("own_source_fields",own_source_fields);
        resultmap.put("datatable_field_infos",datatable_field_infos);
        resultmap.put("dm_relation_datatables",dm_relation_datatables);
        resultmap.put("dm_column_storages",dm_column_storages);
        byte[] bytes = JSON.toJSONString(resultmap).getBytes();
        Map<String, Object> map = JSON.parseObject(new String(bytes));
        return bytes;
    }
}


