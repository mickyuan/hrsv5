package hrds.h.biz.spark.dealdataset.dataprocesser;

import hrds.commons.entity.Datatable_field_info;
import hrds.commons.utils.Constant;
import hrds.h.biz.config.MarketConf;
import org.apache.commons.lang.StringUtils;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.functions;

import java.util.ArrayList;
import java.util.List;

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

    private MarketConf marketConf = null;

    public AddColumnsForDataSet(MarketConf conf) {

        this.marketConf = conf;
    }

    /**
     * @param dataSet
     * @return
     */
    @Override
    public Dataset<Row> process(Dataset<Row> dataSet) {

        //元数据库中的所有的字段信息（页面可以看到的字段，以及额外添加的日期md5字段）
        List<Datatable_field_info> fields = marketConf.getDatatableFields();
        List<Datatable_field_info> realFields = new ArrayList<>();

        /*
         * 自己添加的列，不需要做处理（HYREN_S_DATE,HYREN_E_DATE,HYREN_MD5_VAL），也不需放到md5值计算
         */
        for (Datatable_field_info field : fields) {
            if (!field.getField_en_name().startsWith(HYREN_COLUMN_SUFFIX)) {
                realFields.add(field);
            }
        }
        String[] originalSqlcolumnName = dataSet.columns();//未经过处理的dataset的列名（即存在于sql的select字段）
        List<String> keepColumnName = new ArrayList<String>();
        Column[] sqlcolumn = new Column[realFields.size()];//用户定义的真实的Column对象数组（包括HYREN_S_DATE,HYREN_E_DATE,HYREN_MD5_VAL等）
        List<Column> columnForMD5 = new ArrayList<Column>();//拼接作为计算md5的列
        //int mapColumnIndex = 0;//用户定义的映射字段按照顺序消耗掉原始字段（因为用户定义的映射字段与原始字段（即sql查询出来的字段）应该是一一对应的）
        for (int i = 0; i < realFields.size(); i++) {
            Datatable_field_info datatable_field_info = realFields.get(i);

            String columnName = datatable_field_info.getField_en_name();
            String columnType = datatable_field_info.getField_type();
            if ("increment".equalsIgnoreCase(datatable_field_info.getField_process())) {//递增
                sqlcolumn[i] = functions.monotonically_increasing_id();
                //sqlcolumn[i] = functions.row_number().over(Window.orderBy(originalSqlcolumnName[0]));
            } else if ("map".equalsIgnoreCase(datatable_field_info.getField_process())) {//映射
                //sqlcolumn[i] = new Column(originalSqlcolumnName[mapColumnIndex]);
                Integer ii = Integer.parseInt(datatable_field_info.getProcess_para()) - 1;
                sqlcolumn[i] = new Column(originalSqlcolumnName[ii]);//将映射的字段给需要保存的列信息
                keepColumnName.add(originalSqlcolumnName[ii]);//將需要的字段提取先放到list中
                //mapColumnIndex++;
                columnForMD5.add(new Column(columnName));//只有映射列可以作为算md5的列
            } else {//定值
                sqlcolumn[i] = functions.lit(datatable_field_info.getProcess_para());
            }
            columnType = "varchar".equals(columnType) || "char".equals(columnType) ? "string" : columnType;
            sqlcolumn[i] = sqlcolumn[i].cast(columnType).name(columnName.toUpperCase());//改为定义的列名和类型
        }
        //定义不需要的列新，drop需要不映射的字段信息
        List<String> dropcolumn = new ArrayList<String>();
        for (int i = 0; i < originalSqlcolumnName.length; i++) {
            if (!keepColumnName.contains(originalSqlcolumnName[i])) {
                dropcolumn.add(originalSqlcolumnName[i]);
            }
        }
        dataSet = dataSet.drop(StringUtils.join(dropcolumn, ","));//删除不要的列信息
        dataSet = dataSet.select(sqlcolumn);

        Column[] md5Array = columnForMD5.toArray(new Column[columnForMD5.size()]);

        /*
         * 添加HYREN_S_DATE,HYREN_E_DATE,HYREN_MD5_VAL
         */
        dataSet = dataSet
                .withColumn(Constant.SDATENAME, functions.lit(marketConf.getEtlData()))
                .withColumn(Constant.EDATENAME, functions.lit(Constant.MAXDATE))
                .withColumn(Constant.MD5NAME,functions.md5(functions.array(md5Array).cast("string")));

        dataSet.printSchema();


        return dataSet;
    }

}
