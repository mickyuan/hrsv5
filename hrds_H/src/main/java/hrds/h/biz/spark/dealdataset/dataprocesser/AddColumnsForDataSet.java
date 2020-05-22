package hrds.h.biz.spark.dealdataset.dataprocesser;

import hrds.commons.codes.ProcessType;
import hrds.commons.codes.Store_type;
import hrds.commons.entity.Datatable_field_info;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.Constant;
import hrds.h.biz.config.MarketConf;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.functions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description: 对原始的dataset根据用户定义字段做一些处理
 * <p>
 * Date:2018年5月13日下午8:06:44
 * Copyright (c) 2018, yuanqi@beyondsoft.com All Rights Reserved.
 *
 * @author yuanqi
 * @since JDK 1.7
 */
public class AddColumnsForDataSet implements DataSetProcesser {

    private static final String HYREN_COLUMN_SUFFIX = "hyren_";

    private MarketConf marketConf;

    public AddColumnsForDataSet(MarketConf conf) {

        this.marketConf = conf;
    }

    /**
     * @param dataSet
     * @return
     */
    @Override
    public Dataset<Row> process(Dataset<Row> dataSet) {

        List<Datatable_field_info> fields = getNeedToHandleFields();
        //未经过处理的dataset的列名（即存在于sql的select字段）
        String[] originalSqlcolumnName = dataSet.columns();
        List<String> keepColumnName = new ArrayList<>();
        //用户定义的真实的Column对象数组（包括HYREN_S_DATE,HYREN_E_DATE,HYREN_MD5_VAL等）
        Column[] sqlColumn = new Column[fields.size()];
        //拼接作为计算md5的列
        List<Column> columnForMD5 = new ArrayList<>();
        for (int i = 0; i < fields.size(); i++) {

            Datatable_field_info field = fields.get(i);

            String columnName = field.getField_en_name();
            String columnType = field.getField_type();

            String processCode = field.getField_process();
            if (ProcessType.ZiZeng.getCode().equals(processCode)) {
                sqlColumn[i] = functions.monotonically_increasing_id();
            } else if (ProcessType.YingShe.getCode().equals(processCode)) {
                int indexInOrigin = Integer.parseInt(field.getProcess_para());
                //将映射的字段给需要保存的列信息
                sqlColumn[i] = new Column(originalSqlcolumnName[indexInOrigin]);
                //將需要的字段对象提取先放到list中
                keepColumnName.add(originalSqlcolumnName[indexInOrigin]);
                columnForMD5.add(new Column(columnName));
            } else if (ProcessType.DingZhi.getCode().equals(processCode)) {
                sqlColumn[i] = functions.lit(field.getProcess_para());
            } else {
                throw new AppSystemException("不支持处理方式码：" + processCode);
            }

            columnType = "varchar".equals(columnType) || "char".equals(columnType) ? "string" : columnType;
            //改为定义的列名和类型
            sqlColumn[i] = sqlColumn[i].name(columnName.toUpperCase());
            //关系型数据库类型，不需要设置类型，插入时自动转型
            if (!Store_type.DATABASE.getCode().equals(marketConf.getDataStoreLayer().getStore_type())) {
                sqlColumn[i] = sqlColumn[i].cast(columnType);
            }
        }

        /**
         * sql中有这个字段，但是dataset在处理之后，发现sql中的这个字段没有取到
         * 就把dataset中的这个字段给drop掉
         */
        String dropColumns = Arrays.stream(originalSqlcolumnName)
                .filter(s -> !keepColumnName.contains(s))
                .collect(Collectors.joining(","));
        //页面选择的字段，跟真实sql查询出来的列有可能不一致
        dataSet = dataSet.drop(dropColumns).select(sqlColumn);
        //用于计算MD5的列
        Column[] md5Array = columnForMD5.toArray(new Column[0]);

        /*
         * 添加HYREN_S_DATE,HYREN_E_DATE,HYREN_MD5_VAL
         */
        if (marketConf.isMultipleInput()) {
            dataSet = dataSet
                    .withColumn(Constant.TABLE_ID_NAME, functions.lit(marketConf.getDatatableId()));
        }
        dataSet = dataSet
                .withColumn(Constant.SDATENAME, functions.lit(marketConf.getEtlDate()))
                .withColumn(Constant.EDATENAME, functions.lit(Constant.MAXDATE))
                .withColumn(Constant.MD5NAME, functions.md5(functions.array(md5Array).cast("string")));

        dataSet.printSchema();
        return dataSet;
    }

    /**
     * hyren开头的自己添加的列不需要被处理
     *
     * @return 需要被处理的列
     */
    private List<Datatable_field_info> getNeedToHandleFields() {
        List<Datatable_field_info> allFields = marketConf.getDatatableFields();
        List<Datatable_field_info> needToHandleFields = new ArrayList<>();
        /*
         * 自己添加的列，不需要做处理（HYREN_S_DATE,HYREN_E_DATE,HYREN_MD5_VAL），也不需放到md5值计算
         */
        for (Datatable_field_info field : allFields) {
            if (!field.getField_en_name().startsWith(HYREN_COLUMN_SUFFIX)) {
                needToHandleFields.add(field);
            }
        }
        return needToHandleFields;
    }

}
