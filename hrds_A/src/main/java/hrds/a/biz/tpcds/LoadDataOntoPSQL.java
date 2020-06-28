package hrds.a.biz.tpcds;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.utils.StringUtil;
import fd.ng.core.utils.Validator;
import fd.ng.db.conf.DbinfosConf;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.meta.MetaOperator;
import fd.ng.db.meta.TableMeta;
import hrds.commons.codes.DatabaseType;
import hrds.commons.collection.ConnectionTool;
import hrds.commons.collection.bean.DbConfBean;
import hrds.commons.utils.Constant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


@DocClass(desc = "加载数据到Postgres数据库中", author = "BY-HLL", createdate = "2020/6/23 0000 上午 10:44")
public class LoadDataOntoPSQL {

    //初始化日志
    private static final Logger logger = LogManager.getLogger();
    // 数据库连接配置信息Bean
    private static DbConfBean dbConfBean = new DbConfBean();
    // 测试用例依赖TPCDS测试数据目录
    private static final String TPCDS_DIR = Constant.PROJECT_BIN_DIR + File.separator + "tpcds";
    // 测试用例依赖TPCDS测试数据目录
    private static final String TPCDS_DATA_DIR = TPCDS_DIR + File.separator + "data";
    // 测试用例依赖TPCDS建表sql
    private static final String CREATE_SQL_FILE = TPCDS_DIR + File.separator + "script" + File.separator + "create-table.sql";
    // 测试用例依赖TPCDS插入数据sql
    private static final String INSERT_SQL_FILE = TPCDS_DIR + File.separator + "script" + File.separator + "insert-table.sql";
    // 测试用例需要初始化的表
    private static final String[] table_name_s = {
            "call_center", "catalog_page", "catalog_returns", "catalog_sales", "customer", "customer_address",
            "customer_demographics", "date_dim", "household_demographics", "income_band", "inventory", "item",
            "promotion", "reason", "ship_mode", "store", "store_returns", "store_sales", "time_dim", "warehouse",
            "web_page", "web_returns", "web_sales", "web_site"
    };
    // 数据文件分隔符
    private static final String TPCDS_DATA_FILE_DELIMITER = "|";

    static {
        DbinfosConf.Dbinfo dbinfo = DbinfosConf.getDatabase("tpcds");
        dbConfBean.setDatabase_drive(dbinfo.getDriver());
        dbConfBean.setJdbc_url(dbinfo.getUrl());
        dbConfBean.setUser_name(dbinfo.getUsername());
        dbConfBean.setDatabase_pad(dbinfo.getPassword());
        dbConfBean.setDatabase_type(DatabaseType.Postgresql.getCode());
    }

    /**
     * main
     */
    public static void main(String[] args) {
        // 初始化数据
        execute();
        // 清理数据
//        cleanUp();
    }

    @Method(desc = "初始化数据", logicStep = "初始化数据")
    public static void execute() {
        //初始化db
        DatabaseWrapper db = ConnectionTool.getDBWrapper(dbConfBean);
        //执行加载数据
        try {
            //创建tpcds表信息
            createTpcdsTable(db);
            //插入数据
            insertTpcdsData(db);
            //提交数据库操作
            db.commit();
        } catch (Exception e) {
            db.rollback();
        } finally {
            db.close();
        }
    }

    @Method(desc = "清理数据", logicStep = "清理数据")
    public static void cleanUp() {
        //循环删除每一张表
        //初始化db
        DatabaseWrapper db = ConnectionTool.getDBWrapper(dbConfBean);
        //执行加载数据
        try {
            dropTpcdsTable(db);
            //提交数据库操作
            db.commit();
        } catch (Exception e) {
            db.rollback();
        } finally {
            db.close();
        }
    }

    @Method(desc = "创建TPC_DS生成的24张表", logicStep = "创建TPC_DS生成的24张表")
    @Param(name = "db", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
    private static void createTpcdsTable(DatabaseWrapper db) {
        //获取sql文件内容
        String sql_str = readFileByLines();
        //执行文本的建表语句
        db.ExecDDL(sql_str);
    }

    @Method(desc = "插入TPC_DS24张表数据", logicStep = "插入TPC_DS24张表数据")
    @Param(name = "db", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
    private static void insertTpcdsData(DatabaseWrapper db) {
        //获取TPCDS目录下的所有文件
        File data_dir = new File(TPCDS_DATA_DIR);
        if (!data_dir.isDirectory()) {
            throw new RuntimeException("TPCDS的配置路径不是一个目录,需要一个目录并且存在TPCDS的数据文件!");
        } else {
            File[] data_file_s = data_dir.listFiles();
            //判断文件
            if (null == data_file_s || data_file_s.length == 0) {
                throw new RuntimeException("TPCDS配置的数据目录下不存在数据文件!");
            }
            //处理文件
            for (File data_file : data_file_s) {
                //获取表名
                String table_name = data_file.getName().substring(0, data_file.getName().lastIndexOf("."));
                //获取表的meta信息
                TableMeta table_meta = MetaOperator.getTablesWithColumns(db, table_name).get(0);
                //获取表字段列表
                List<String> column_name_s = new ArrayList<>();
                table_meta.getColumnMetas().forEach((k, v) -> column_name_s.add(k));
                //获取批量插入数据库的sql
                String batchSql = getBatchSql(table_name, column_name_s);
                //解析表对应的数据文件
                List<Object[]> file_data_list = parsingDataFile(data_file, table_meta);
                //执行插入语句
                if (null == file_data_list || file_data_list.size() == 0) {
                    throw new RuntimeException("表名: " + table_name + ",对应的数据文件为空!");
                }
                db.execBatch(batchSql, file_data_list);
            }
        }

    }

    @Method(desc = "删除TPC_DS生成的24张表", logicStep = "删除TPC_DS生成的24张表")
    @Param(name = "db", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
    private static void dropTpcdsTable(DatabaseWrapper db) {
        for (String table_name : table_name_s) {
            if (db.isExistTable(table_name)) {
                db.ExecDDL("drop table " + table_name);
            } else {
                logger.info("表:" + table_name + "已经不存在,跳过!");
            }
        }

    }

    @Method(desc = "以行为单位读取文件，常用于读面向行的格式化文件", logicStep = "以行为单位读取文件，常用于读面向行的格式化文件")
    private static String readFileByLines() {
        StringBuilder str = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(CREATE_SQL_FILE), StandardCharsets.UTF_8))) {
            String tempString;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                str.append(" ").append(tempString);
                line++;
            }
        } catch (IOException e) {
            logger.error("获取tpcds建表sql文件内容失败!");
            e.printStackTrace();
        }
        return str.toString();
    }

    @Method(desc = "解析DAT数据文件", logicStep = "解析DAT数据文件")
    private static List<Object[]> parsingDataFile(File file, TableMeta table_meta) {
        String fileName = file.getName();
        Validator.notBlank(fileName, "输入参数file错误, 必须精确到文件名.dat后缀全路径才行");
        // 获取后缀名 如果后缀名没有".",则返回""
        String suffix = fileName.lastIndexOf(".") == -1 ? "" : fileName.substring(fileName.lastIndexOf(".") + 1);
        //初始化待返回的结果集
        List<Object[]> listRows = null;
        if ("dat".equals(suffix)) {
            BufferedReader bf = null;
            String line;
            try {
                FileInputStream fis = new FileInputStream(new File(file, ""));
                bf = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
                listRows = new ArrayList<>();
                //获取数据文件的表头
                List<String> data_column_s = StringUtil.split(bf.readLine(), TPCDS_DATA_FILE_DELIMITER);
                //获取数据
                while ((line = bf.readLine()) != null) {
                    if (line.replaceAll(" ", "").length() > 0) {
                        //初始化存储数据数据对象
                        Object[] arr = new Object[data_column_s.size()];
                        //获取当前处理行数据
                        String line_list = line;
                        //循环处理每个字段的数据
                        final int[] j = {0};
                        table_meta.getColumnMetas().forEach((column, column_meta) -> {
                            for (int i = 0; i < data_column_s.size(); i++) {
                                if (column.equalsIgnoreCase(data_column_s.get(i))) {
                                    arr[j[0]] = getValue(column_meta.getTypeName(), StringUtil.split(line_list,
                                            TPCDS_DATA_FILE_DELIMITER).get(i));
                                }
                            }
                            j[0]++;
                        });
                        listRows.add(arr);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("dat文件读取失败");
            } finally {
                //在返回之前关闭流
                if (bf != null) {
                    try {
                        bf.close();
                    } catch (IOException e2) {
                        //添加日志文件吧
                        logger.error("发生异常: 文件bf关闭失败!");
                    }
                }
            }
        } else {
            logger.error("发生异常: 文件的后缀名不是以.dat结尾的!");
            return null;
        }
        return listRows;
    }

    @Method(desc = "获取批量插入sql", logicStep = "获取批量插入sql")
    private static String getBatchSql(String tableName, List<String> columns) {
        //拼接插入的sql
        StringBuilder sbAdd = new StringBuilder();
        sbAdd.append("insert into ").append(tableName).append("(");
        for (String column : columns) {
            sbAdd.append(column).append(",");
        }
        sbAdd.deleteCharAt(sbAdd.length() - 1);
        sbAdd.append(") values(");
        for (int i = 0; i < columns.size(); i++) {
            if (i != columns.size() - 1) {
                sbAdd.append("?").append(",");
            } else {
                sbAdd.append("?");
            }
        }
        sbAdd.append(")");
        return sbAdd.toString();
    }


    private static Object getValue(String type, String tmpValue) {
        Object str;
        type = type.toLowerCase();
        if (type.contains(DataTypeConstant.BOOLEAN.getMessage())) {
            // 如果取出的值为null则给空字符串
            str = StringUtil.isEmpty(tmpValue) ? null : Boolean.parseBoolean(tmpValue.trim());
        } else if (type.contains(DataTypeConstant.LONG.getMessage())
                || type.contains(DataTypeConstant.INT.getMessage())
                || type.contains(DataTypeConstant.FLOAT.getMessage())
                || type.contains(DataTypeConstant.DOUBLE.getMessage())
                || type.contains(DataTypeConstant.DECIMAL.getMessage())
                || type.contains(DataTypeConstant.NUMERIC.getMessage())) {
            // 如果取出的值为null则给空字符串
            str = StringUtil.isEmpty(tmpValue) ? null : new BigDecimal(tmpValue.trim());
        } else if (type.contains(DataTypeConstant.DATE.getMessage())) {
            // 如果取出的值为""则给null
            str = StringUtil.isEmpty(tmpValue) ? null : tmpValue;
        } else {
            // 如果取出的值为null则给空字符串
            str = StringUtil.isEmpty(tmpValue) ? "" : tmpValue;
        }
        return str;
    }
}

enum DataTypeConstant {
    STRING(0, "string"), CHAR(1, "char"), BOOLEAN(3, "boolean"),
    INT(4, "int"), DECIMAL(5, "decimal"), FLOAT(6, "float"),
    DOUBLE(7, "double"), INT8(8, "int8"), BIGINT(9, "bigint"),
    LONG(10, "long"), NUMERIC(11, "numeric"), BYTE(12, "byte"),
    TIMESTAMP(13, "timestamp"), DATE(14, "date");

    private final int code;
    private final String message;

    DataTypeConstant(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message;
    }
}
