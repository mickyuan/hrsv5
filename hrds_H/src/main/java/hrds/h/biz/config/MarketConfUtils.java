package hrds.h.biz.config;

import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.FileUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.entity.*;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.Constant;
import hrds.commons.utils.DruidParseQuerySql;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
    private static final Log log = LogFactory.getLog(MarketConfUtils.class);

    static void checkArguments(String datatableId, String etldate) {
        if (StringUtil.isBlank(datatableId)) {
            throw new IllegalArgumentException(String.format("集市信息id不可为空: %s", datatableId));
        }
        if (!DateUtil.validDateStr(etldate)) {
            throw new IllegalArgumentException(String.format("批量日期不合法: %s", etldate));
        }
    }

    private static final String nullQueryExceptString = "无法从 %s 表中查询出 %s = %s 的记录";

    /**
     * 从元数据库中查出所有相关的实体信息
     */
    static void initBeans(MarketConf marketConf) {

        Long datatableId = Long.parseLong(marketConf.getDatatableId());

        try (DatabaseWrapper db = new DatabaseWrapper()) {

            /*
             根据主键 datatable_id 查询 Dm_datatable 实体
             */
            Optional<Dm_datatable> dmDatatable = SqlOperator.queryOneObject(db, Dm_datatable.class,
                    "select * from dm_datatable where datatable_id = ?", datatableId);
            if (!dmDatatable.isPresent()) {
                throw new AppSystemException(String.format(nullQueryExceptString,
                        Dm_datatable.TableName, "datatable_id", datatableId));
            }
            marketConf.setDmDatatable(dmDatatable.get());
            marketConf.setTableName(dmDatatable.get().getDatatable_en_name());

            /*
            根据主键 datatable_id 查询 字段 实体
             */
            List<Datatable_field_info> datatableFields = SqlOperator.queryList(db, Datatable_field_info.class,
                    "select * from datatable_field_info where datatable_id = ?", datatableId);
            if (datatableFields.isEmpty()) {
                throw new AppSystemException(String.format(nullQueryExceptString,
                        Datatable_field_info.TableName, "datatable_id", datatableId));
            }
            //添加字段，字段全部转小写
            handleFields(datatableFields);
            marketConf.setDatatableFields(datatableFields);

            /*
              根据主键 datatable_id 查询 需要执行的sql，并进行替换
             */
            Optional<Dm_operation_info> dmOperationInfo = SqlOperator.queryOneObject(db, Dm_operation_info.class,
                    "select * from dm_operation_info where datatable_id = ?", datatableId);
            if (!dmOperationInfo.isPresent()) {
                throw new AppSystemException(String.format(nullQueryExceptString,
                        Dm_operation_info.TableName, "datatable_id", datatableId));
            }
            marketConf.setCompleteSql(replaceView(fillSqlWithParams(dmOperationInfo.get().getExecute_sql()
                    , marketConf.getSqlParams())));

            /*
              根据主键 datatable_id 查询出 集市表存储关系表
             */
            Optional<Dm_relation_datatable> dmRelationDatatable = SqlOperator.queryOneObject(db, Dm_relation_datatable.class,
                    "select * from dm_relation_datatable where datatable_id = ?", datatableId);
            if (!dmRelationDatatable.isPresent()) {
                throw new AppSystemException(String.format(nullQueryExceptString,
                        Dm_relation_datatable.TableName, "datatable_id", datatableId));
            }
            marketConf.setDmRelationDatatable(dmRelationDatatable.get());

            //存储层配置id
            Long dslId = dmRelationDatatable.get().getDsl_id();
            /*
              根据 存储层配置id 查询出 数据存储层配置表
             */
            Optional<Data_store_layer> dataStoreLayer = SqlOperator.queryOneObject(db, Data_store_layer.class,
                    "select * from data_store_layer where dsl_id = ?", dslId);
            if (!dataStoreLayer.isPresent()) {
                throw new AppSystemException(String.format(nullQueryExceptString,
                        Data_store_layer.TableName, "dsl_id", datatableId));
            }
            marketConf.setDataStoreLayer(dataStoreLayer.get());

            /*
            根据主键 存储层配置id 查询 数据存储层配置属性表
             */
            List<Data_store_layer_attr> dataStoreLayerAttrs = SqlOperator.queryList(db, Data_store_layer_attr.class,
                    "select * from data_store_layer_attr where dsl_id = ?", dslId);
            if (dataStoreLayerAttrs.isEmpty()) {
                throw new AppSystemException(String.format(nullQueryExceptString,
                        Data_store_layer_attr.TableName, "dsl_id", dslId));
            }
            marketConf.setDataStoreLayerAttrs(dataStoreLayerAttrs);

        }

    }

    /**
     * 添加三个 hyren 字段
     * 字段全部转小写
     *
     * @param datatableFields 所有字段实体
     */
    private static void handleFields(List<Datatable_field_info> datatableFields) {
        if (datatableFields.size() == 0) {
            throw new AppSystemException("状态错误,字段数量为0");
        }

        //添加 HYREN_S_DATE
        Datatable_field_info sDateField = new Datatable_field_info();
        sDateField.setField_en_name(Constant.SDATENAME);
        sDateField.setField_type("varchar");
        datatableFields.add(sDateField);
        //添加 HYREN_E_DATE
        Datatable_field_info eDateField = new Datatable_field_info();
        eDateField.setField_en_name(Constant.EDATENAME);
        eDateField.setField_type("varchar");
        datatableFields.add(eDateField);
        //添加 HYREN_MD5_VAL
        Datatable_field_info md5Field = new Datatable_field_info();
        md5Field.setField_en_name(Constant.MD5NAME);
        md5Field.setField_type("varchar");
        datatableFields.add(md5Field);

        //字段全部转小写
        datatableFields.forEach(datatableField ->
                datatableField.setField_en_name(datatableField.getField_en_name().toLowerCase()));

    }

    /**
     * 将可能带有集市视图的sql中的视图转换为子查询sql
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
        String sql = incompleteSql;
        if (StringUtil.isBlank(sqlParams)) {
            log.info(String.format("SQL动态参数： [%s]", sqlParams));
            return sql;
        }
        for (String param : StringUtil.split(sqlParams, ";")) {
            List<String> paramKV = StringUtil.split(param, "=");
            if (paramKV.size() > 1) {
                sql = StringUtil.replace(sql, "#{" + paramKV.get(0).trim() + "}", paramKV.get(1));
            }
        }
        return sql;
    }

    /**
     * 如果元数据库中存的跑批日期是 8个0，则表示这个任务只是设置过，还没运行过
     * 因为运行完之后会更新这个跑批日期
     */
    private static final String FIRST_LOAD_DATE = "00000000";

    static void checkFirstLoad(MarketConf conf) {
        conf.setFirstLoad(FIRST_LOAD_DATE.equals(conf.getDmDatatable().getEtl_date()));
        log.info("isFirstLoad: " + conf.isFirstLoad());
    }

    static void checkReRun(MarketConf conf, String etlDate) {
        conf.setRerun(etlDate.equals(conf.getDmDatatable().getEtl_date()));
        log.info("reRun: " + conf.isRerun());
    }

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
        } catch (IOException e) {
            throw new AppSystemException(e);
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
