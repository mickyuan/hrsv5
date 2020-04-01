package hrds.h.biz.market;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.util.JdbcConstants;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.tree.foreground.ForegroundTreeUtil;
import hrds.commons.tree.foreground.bean.TreeDataInfo;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.h.biz.SqlAnalysis.HyrenOracleTableVisitor;
//import com.alibaba.druid.
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.*;

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
    //TODO 由于目前不存在集市分类 所以随便写的一个ID 为了满足入库需求 之后将去除
    private static final String Category_id = "1000025018";

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
    public List<Dm_datatable> queryDMDataTableByDataMartID(String data_mart_id) {
        Dm_datatable dm_datatable = new Dm_datatable();
        dm_datatable.setData_mart_id(data_mart_id);
        return Dbo.queryList(Dm_datatable.class, "SELECT * FROM " + Dm_datatable.TableName + " where data_mart_id = ? order by " +
                "datatable_id asc", dm_datatable.getData_mart_id());
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
        return Dbo.queryList(Data_store_layer.class, "select * from " + Data_store_layer.TableName + " where dsl_name like ?", fuzzyqueryitem);
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
        CheckColummn(dm_datatable.getDatatype(), "数据类型");
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
        dm_datatable.setDatatable_due_date(Final_Date);
        dm_datatable.setDdlc_date(DateUtil.getSysDate());
        dm_datatable.setDdlc_time(DateUtil.getSysTime());
        dm_datatable.setDatac_date(DateUtil.getSysTime());
        dm_datatable.setDatac_time(DateUtil.getSysTime());
        dm_datatable.setDatatable_lifecycle(TableLifeCycle.YongJiu.getCode());
        dm_datatable.setSoruce_size(Zero);
        dm_datatable.setEtl_date(ZeroDate);
        //TODO 这里这个字段无意义
        dm_datatable.setIs_append(TableStorage.ShuJuBiao.getCode());
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
        CheckColummn(dm_datatable.getDatatype(), "数据类型");
        CheckColummn(dsl_id, "数据存储");
        //2检查表名重复
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
            logicStep = "1.初始化查询" +
                    "2.获取pgsql连接配置" +
                    "2-1.根据sql语句判断执行引擎")
    @Param(name = "querysql", desc = "查询SQL", range = "String类型SQL")
    @Return(desc = "查询返回结果集", range = "无限制")
    public Result getDataBySQL(String querysql) {
        querysql = querysql.trim();
        if (querysql.endsWith(";")) {
            //去除分号
            querysql = querysql.substring(0, querysql.length() - 1);
        }
        querysql = "select * from (" + querysql + ") as " + alias + " limit " + LimitNumber;
        return Dbo.queryResult(querysql);
    }

    @Method(desc = "根据数据表ID,获取数据库类型，获取选中数据库的附件属性字段",
            logicStep = "")
    @Param(name = "datatable_id", desc = "datatable_id", range = "String类型集市表ID")
    @Return(desc = "查询返回结果集", range = "无限制")
    public List<Map<String, Object>> getColumnMore(String datatable_id) {
        Dm_datatable dm_datatable = new Dm_datatable();
        dm_datatable.setDatatable_id(datatable_id);
        return Dbo.queryList("select dslad_id,dsla_storelayer from " + Data_store_layer_added.TableName + " t1 " +
                "left join " + Dm_relation_datatable.TableName + " t2 on t1.dsl_id = t2.dsl_id " +
                "where t2.datatable_id = ? order by dsla_storelayer", dm_datatable.getDatatable_id());
    }

    //TODO
    @Method(desc = "根据SQL获取列结构",
            logicStep = "")
    @Param(name = "querysql", desc = "查询SQL", range = "String类型SQL")
    @Return(desc = "列结构", range = "无限制")
    @Param(name = "datatable_id", desc = "datatable_id", range = "String类型集市表ID")
    public List<Map<String, Object>> getColumnBySql(String querysql, String datatable_id) {
        List<Map<String, Object>> resultlist = new ArrayList<Map<String, Object>>();
        CheckColummn(querysql,"查询sql");
        Dm_datatable dm_datatable = new Dm_datatable();
        dm_datatable.setDatatable_id(datatable_id);
        List<Map<String, Object>> targetTypeList = Dbo.queryList("SELECT distinct replace(replace(trim(t1.target_type),'(',''),')','') as target_type " +
                "FROM " + Type_contrast.TableName + " t1 LEFT JOIN " + Data_store_layer.TableName + " t2 ON t1.dtcs_id = t2.dtcs_id " +
                "LEFT JOIN " + Dm_relation_datatable.TableName + " t3 ON t2.dsl_id=t3.dsl_id" +
                " WHERE t3.datatable_id = ?", dm_datatable.getDatatable_id());
        List<Map<String, Object>>  dslaStorelayerList = Dbo.queryList("select dslad_id,dsla_storelayer from " + Data_store_layer_added.TableName + " t1 " +
                "left join " + Dm_relation_datatable.TableName + " t2 on t1.dsl_id = t2.dsl_id " +
                "where t2.datatable_id = ? order by dsla_storelayer", dm_datatable.getDatatable_id());
        List<SQLStatement> stmtList = SQLUtils.parseStatements(querysql, JdbcConstants.ORACLE);
        HyrenOracleTableVisitor visitor = new HyrenOracleTableVisitor();
        visitor.setVisitqueryblocktimes(0);
        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
        }
        List<String> columnNameList = visitor.getColumnNameList();
        for(String everyColumnName :columnNameList){
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("field_en_name",everyColumnName);
            map.put("field_cn_name",everyColumnName);
            //这里选择第一个字段类型作为默认字段类型
            map.put("field_type",targetTypeList.get(0).get("target_type"));
            //TODO 这里需要增加代码项
            map.put("field_process","map");
            for(Map<String,Object> dslaStorelayeMap : dslaStorelayerList){
                map.put(StoreLayerAdded.ofValueByCode(dslaStorelayeMap.get("dsla_storelayer").toString()),false);
            }
            resultlist.add(map);
        }
        return resultlist;
    }


    @Method(desc = "回显新增集市页面2中记录在数据库中的字段信息",
            logicStep = "")
    @Param(name = "datatable_id", desc = "datatable_id", range = "String类型集市表ID")
    @Return(desc = "列结构", range = "无限制")
    public List<Map<String, Object>> getColumnFromDatabase(String datatable_id) {
        Dm_datatable dm_datatable = new Dm_datatable();
        dm_datatable.setDatatable_id(datatable_id);
        List<Map<String, Object>> list = Dbo.queryList("select * from " + Datatable_field_info.TableName +
                " where datatable_id = ?", dm_datatable.getDatatable_id());
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
            logicStep = "")
    @Param(name = "datatable_id", desc = "datatable_id", range = "String类型集市表ID")
    @Return(desc = "查询返回结果集", range = "无限制")
    public List<Map<String, Object>> getAllField_Type(String datatable_id) {
        Dm_datatable dm_datatable = new Dm_datatable();
        dm_datatable.setDatatable_id(datatable_id);
        return Dbo.queryList("SELECT distinct replace(replace(trim(t1.target_type),'(',''),')','') as target_type " +
                "FROM " + Type_contrast.TableName + " t1 LEFT JOIN " + Data_store_layer.TableName + " t2 ON t1.dtcs_id = t2.dtcs_id " +
                "LEFT JOIN " + Dm_relation_datatable.TableName + " t3 ON t2.dsl_id=t3.dsl_id" +
                " WHERE t3.datatable_id = ?", dm_datatable.getDatatable_id());

    }

    @Method(desc = "保存新增集市2的数据",
            logicStep = "")
    @Param(name = "datatable_field_info", desc = "datatable_field_info", range = "与Datatable_field_info表字段规则一致",
            isBean = true)
    @Param(name = "dm_column_storage", desc = "dm_column_storage", range = "与Dm_column_storage表字段规则一致",
            isBean = true)
    @Param(name = "datatable_id", desc = "datatable_id", range = "String类型集市表ID")
    @Param(name = "querysql", desc = "querysql", range = "String类型集市查询SQL")
    public void addDFInfo(Datatable_field_info[] datatable_field_info, String datatable_id, Dm_column_storage[] dm_column_storage,String querysql) {
        Dm_datatable dm_datatable = new Dm_datatable();
        dm_datatable.setDatatable_id(datatable_id);
        Dbo.execute("delete from " + Dm_column_storage.TableName + " where datatable_field_id in (select datatable_field_id from " +
                Datatable_field_info.TableName + " where datatable_id = ?)", dm_datatable.getDatatable_id());
        Dbo.execute("delete from "+Datatable_field_info.TableName+" where datatable_id = ?", dm_datatable.getDatatable_id());
        Dbo.execute("delete from "+Dm_operation_info.TableName+" where datatable_id = ?", dm_datatable.getDatatable_id());
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
            dc_storage.getCsi_number().intValue();
            dc_storage.setDatatable_field_id(datatable_field_info[dc_storage.getCsi_number().intValue()].getDatatable_field_id());
            dc_storage.add(Dbo.db());
        }
        Dm_operation_info dm_operation_info = new Dm_operation_info();
        dm_operation_info.setId(PrimayKeyGener.getNextId());
        dm_operation_info.setDatatable_id(datatable_id);
        dm_operation_info.setExecute_sql(querysql);
        dm_operation_info.add(Dbo.db());
    }

    @Method(desc = "根据集市表ID,获取SQL回显",
            logicStep = "")
    @Param(name = "datatable_id", desc = "datatable_id", range = "String类型集市表ID")
    @Return(desc = "查询返回结果集", range = "无限制")
    public List<Map<String, Object>> getQuerySql(String datatable_id) {
        Dm_datatable dm_datatable = new Dm_datatable();
        dm_datatable.setDatatable_id(datatable_id);
        return Dbo.queryList("select execute_sql as querysql from "+Dm_operation_info.TableName+" t1 where " +
                "datatable_id = ?", dm_datatable.getDatatable_id());
    }


}


