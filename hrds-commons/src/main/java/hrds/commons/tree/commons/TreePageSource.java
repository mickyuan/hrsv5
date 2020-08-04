package hrds.commons.tree.commons;

import fd.ng.core.annotation.DocClass;
import hrds.commons.codes.DataSourceType;

import java.util.*;

@DocClass(desc = "树页面来源配置类", author = "BY-HLL", createdate = "2019/12/24 0024 上午 10:05")
public class TreePageSource {
    //webSQL
    public static final String WEB_SQL = "webSQL";
    //集市
    public static final String MARKET = "market";
    //接口
    public static final String INTERFACE = "interface";
    //数据管控
    public static final String DATA_MANAGEMENT = "dataManagement";
    //数据对标
    public static final String DATA_BENCHMARKING = "dataBenchmarking";
    //集市-版本管理
    public static final String MARKET_VERSION_MANAGE = "market_version_manage";

    public static final List<String> treeSourceList = new ArrayList<>
            (Arrays.asList(WEB_SQL, MARKET, INTERFACE, DATA_MANAGEMENT, DATA_BENCHMARKING, MARKET_VERSION_MANAGE));

    //集市,加工,数据管控
    private static DataSourceType[] DATA_MANAGEMENT_ARRAY = new DataSourceType[]{DataSourceType.DCL,
            DataSourceType.DML, DataSourceType.DQC, DataSourceType.UDL};
    //webSQL
    private static DataSourceType[] WEB_SQL_ARRAY = new DataSourceType[]{DataSourceType.DCL,
            DataSourceType.DML, DataSourceType.DQC, DataSourceType.UDL};
    //数据接口服务
    private static DataSourceType[] HIRE_ARRAY = new DataSourceType[]{DataSourceType.DCL,
            DataSourceType.DML, DataSourceType.DQC, DataSourceType.UDL};
    //数据对标
    private static DataSourceType[] DATA_BENCHMARKING_ARRAY = new DataSourceType[]{DataSourceType.DCL,
            DataSourceType.DML, DataSourceType.DQC, DataSourceType.UDL};
    //集市-版本管理
    private static DataSourceType[] MARKET_VERSION_MANAGE_ARR = new DataSourceType[]{DataSourceType.DML};

    public static Map<String, DataSourceType[]> TREE_SOURCE = new HashMap<>();

    //初始化树页面类型
    static {
        //WEB SQL
        TREE_SOURCE.put(WEB_SQL, WEB_SQL_ARRAY);
        //集市
        TREE_SOURCE.put(MARKET, DATA_MANAGEMENT_ARRAY);
        //接口
        TREE_SOURCE.put(INTERFACE, HIRE_ARRAY);
        //数据管控
        TREE_SOURCE.put(DATA_MANAGEMENT, DATA_MANAGEMENT_ARRAY);
        //数据对标
        TREE_SOURCE.put(DATA_BENCHMARKING, DATA_BENCHMARKING_ARRAY);
        //集市版本管理
        TREE_SOURCE.put(MARKET_VERSION_MANAGE, MARKET_VERSION_MANAGE_ARR);
    }
}
