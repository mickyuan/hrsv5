package hrds.h.biz.config;

import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.FileUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.core.utils.Validator;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.*;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.Constant;
import hrds.commons.utils.DruidParseQuerySql;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.List;
import java.util.Optional;

/**
 * 配置加载查询工具类
 *
 * @Author: Mick Yuan
 * @Date: 20-4-1 上午10:52
 * @Since jdk1.8
 */
public class MarketConfUtils {
    private static final Logger log = LogManager.getLogger(MarketConfUtils.class);
    /**
     * 自添加HYREN字段类型
     */
    public static final String DEFAULT_STRING_TYPE = "string";
    /**
     * 查询为空后异常格式化语句
     */
    private static final String nullQueryExceptString = "无法从 %s 表中查询出 %s = %s 的记录";

    static void checkArguments(String datatableId, String etldate) {
        Validator.notBlank(datatableId, String.format("集市信息id不可为空: %s", datatableId));
        if (!DateUtil.validDateStr(etldate)) {
            throw new IllegalArgumentException(String.format("批量日期不合法: %s", etldate));
        }
    }


    /**
     * 从元数据库中查出所有相关的实体信息
     */
    static void initBeans(MarketConf marketConf) {

        Long datatableId = Long.parseLong(marketConf.getDatatableId());

        try (DatabaseWrapper db = new DatabaseWrapper()) {

            /*
             根据主键 datatable_id 查询 Dm_datatable 实体
             */
            Dm_datatable dmDatatable = SqlOperator.queryOneObject(db, Dm_datatable.class,
                    "select * from dm_datatable where datatable_id = ?", datatableId)
                    .orElseThrow(() -> new AppSystemException(String.format(nullQueryExceptString,
                            Dm_datatable.TableName, "datatable_id", datatableId)));

            marketConf.setDmDatatable(dmDatatable);
            marketConf.setTableName(dmDatatable.getDatatable_en_name());
            marketConf.setMultipleInput(IsFlag.ofEnumByCode(dmDatatable.getRepeat_flag()) == IsFlag.Shi);

            // TODO getorelse
            /*
            根据主键 datatable_id 查询 字段 实体
             */
            List<Datatable_field_info> datatableFields = SqlOperator.queryList(db, Datatable_field_info.class,
                    "select * from datatable_field_info where datatable_id = ?", datatableId);
            Validator.notEmpty(String.format(nullQueryExceptString,
                    Datatable_field_info.TableName, "datatable_id", datatableId));

            //添加自定义HYREN字段，字段全部转小写
            handleFields(datatableFields, marketConf.isMultipleInput());
            marketConf.setDatatableFields(datatableFields);

            /*
              根据主键 datatable_id 查询 需要执行的sql，并进行替换
             */
            Dm_operation_info dmOperationInfo = SqlOperator.queryOneObject(db, Dm_operation_info.class,
                    "select * from dm_operation_info where datatable_id = ?", datatableId)
                    .orElseThrow(() -> new AppSystemException(String.format(nullQueryExceptString,
                            Dm_operation_info.TableName, "datatable_id", datatableId)));

            marketConf.setCompleteSql(replaceView(fillSqlWithParams(dmOperationInfo.getExecute_sql()
                    , marketConf.getSqlParams())));

            /*
              根据主键 datatable_id 查询出 集市表存储关系表
             */
            Dm_relation_datatable dmRelationDatatable = SqlOperator.queryOneObject(db, Dm_relation_datatable.class,
                    "select * from dm_relation_datatable where datatable_id = ?", datatableId)
                    .orElseThrow(() -> new AppSystemException(String.format(nullQueryExceptString,
                            Dm_relation_datatable.TableName, "datatable_id", datatableId)));

            marketConf.setDmRelationDatatable(dmRelationDatatable);

            //存储层配置id
            Long dslId = dmRelationDatatable.getDsl_id();
            /*
              根据 存储层配置id 查询出 数据存储层配置表
             */
            Data_store_layer dataStoreLayer = SqlOperator.queryOneObject(db, Data_store_layer.class,
                    "select * from data_store_layer where dsl_id = ?", dslId)
                    .orElseThrow(() -> new AppSystemException(String.format(nullQueryExceptString,
                            Data_store_layer.TableName, "dsl_id", datatableId)));

            marketConf.setDataStoreLayer(dataStoreLayer);

            /*
            根据主键 存储层配置id 查询 数据存储层配置属性表
             */
            List<Data_store_layer_attr> dataStoreLayerAttrs = SqlOperator.queryList(db, Data_store_layer_attr.class,
                    "select * from data_store_layer_attr where dsl_id = ?", dslId);

            Validator.notEmpty(dataStoreLayerAttrs, String.format(nullQueryExceptString,
                    Data_store_layer_attr.TableName, "dsl_id", dslId));

            marketConf.setDataStoreLayerAttrs(dataStoreLayerAttrs);

            /*
              根据主键 datatable_id 查询出 集市表存储关系表
             */
            Optional<Dm_relevant_info> dmRelevantInfo = SqlOperator.queryOneObject(db, Dm_relevant_info.class,
                    "select * from dm_relevant_info where datatable_id = ?", datatableId);
            dmRelevantInfo.ifPresent(dm_relevant_info -> marketConf.setFinalSql(dm_relevant_info.getPost_work()));
        }

    }

    /**
     * 添加三个 hyren 字段
     * 字段全部转小写
     *
     * @param datatableFields 所有字段实体
     */
    private static void handleFields(List<Datatable_field_info> datatableFields, boolean isMultipleInput) {
        if (datatableFields.size() == 0) {
            throw new AppSystemException("状态错误,字段数量为0");
        }

        if (isMultipleInput) {
            //添加 HYREN_MD5_VAL
            Datatable_field_info tableIdField = new Datatable_field_info();
            tableIdField.setField_en_name(Constant.TABLE_ID_NAME);
            tableIdField.setField_type(DEFAULT_STRING_TYPE);
            datatableFields.add(tableIdField);
        }

        //添加 HYREN_S_DATE
        Datatable_field_info sDateField = new Datatable_field_info();
        sDateField.setField_en_name(Constant.SDATENAME);
        sDateField.setField_type(DEFAULT_STRING_TYPE);
        datatableFields.add(sDateField);
        //添加 HYREN_E_DATE
        Datatable_field_info eDateField = new Datatable_field_info();
        eDateField.setField_en_name(Constant.EDATENAME);
        eDateField.setField_type(DEFAULT_STRING_TYPE);
        datatableFields.add(eDateField);
        //添加 HYREN_MD5_VAL
        Datatable_field_info md5Field = new Datatable_field_info();
        md5Field.setField_en_name(Constant.MD5NAME);
        md5Field.setField_type(DEFAULT_STRING_TYPE);
        datatableFields.add(md5Field);

        //字段全部转小写
        datatableFields.forEach(datatableField ->
                datatableField.setField_en_name(datatableField.getField_en_name().toLowerCase()));

    }

    /**
     * 将可能带有集市视图的sql中的视图转换为子查询sql
     *
     * @param perhapsWithViewSql 能带有集市视图的sql
     * @return 不带有集市视图的sql
     */
    private static String replaceView(String perhapsWithViewSql) {

        return new DruidParseQuerySql().GetNewSql(perhapsWithViewSql);
    }

    /**
     * 用sql的动态参数 把带有替换符的sql 完整
     *
     * @param incompleteSql 有替换符的sql
     * @param sqlParams     sql的动态参数
     */
    private static String fillSqlWithParams(String incompleteSql, String sqlParams) {
        log.info(String.format("SQL动态参数： [%s]", sqlParams));
        String sql = incompleteSql;
        if (StringUtil.isBlank(sqlParams)) {
            return sql;
        }
        for (String param : StringUtil.split(sqlParams, ";")) {
            List<String> params = StringUtil.split(param, "=");
            if (params.size() != 2) {
                throw new IllegalArgumentException("自定义动态参数键值对错误：" + params);
            }
            sql = StringUtil.replace(sql, "#{" + params.get(0).trim() + "}", params.get(1));
        }
        return sql;
    }

    /**
     * 当前跑批日期等同于元数据库存储的上次的跑批日期
     *
     * @param conf    集市作业配置类实体
     * @param etlDate 跑批日期
     */
    static void checkReRun(MarketConf conf, String etlDate) {
        conf.setRerun(etlDate.equals(conf.getDmDatatable().getEtl_date()));
    }

    /**
     * 集市作业配置类实体序列化路径前缀
     */
    private static final String MARKET_CONF_SERIALIZATION_PATH = FileUtil.TEMP_DIR_NAME +
            "market-serialize" + FileUtil.PATH_SEPARATOR_CHAR;

    static {
        try {
            FileUtil.forceMkdir(new File(MARKET_CONF_SERIALIZATION_PATH));
        } catch (IOException e) {
            throw new AppSystemException(e);
        }
    }

    public static void serialize(MarketConf conf) {

        File serializeFile = FileUtil.getFile(MARKET_CONF_SERIALIZATION_PATH, conf.getDatatableId());

        try {
            FileUtil.forceDelete(serializeFile);
        } catch (IOException warn) {
            log.warn("删除之前序列化文件失败", warn);
        }

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(serializeFile))) {
            out.writeObject(conf);
            log.info(String.format("将 %s 对象序列化进 %s 成功！",
                    MarketConf.class.getSimpleName(), serializeFile.getAbsolutePath()));
        } catch (IOException e) {
            throw new AppSystemException(String.format("将 %s 对象序列化进 %s 失败 :",
                    MarketConf.class.getSimpleName(), serializeFile.getAbsolutePath()), e);
        }
    }

    public static MarketConf deserialize(String datatableId) {

        String serializeFilePath = MARKET_CONF_SERIALIZATION_PATH + datatableId;

        Object o;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(serializeFilePath))) {
            o = in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new AppSystemException("反序列化对象失败：" + MarketConf.class.getName(), e);
        }

        if (o instanceof MarketConf) {
            return (MarketConf) o;
        } else {
            throw new AppSystemException("文件 " + serializeFilePath + " 非 MarketConf 对象序列化文件");
        }
    }
}
