package hrds.commons.tree.commons;

import fd.ng.core.annotation.DocClass;
import hrds.commons.codes.DataSourceType;
import hrds.commons.codes.Store_type;

import java.util.*;

@DocClass(desc = "树页面来源配置类", author = "BY-HLL", createdate = "2019/12/24 0024 上午 10:05")
public class TreePageSource {
	//进HBase
	public static final String INTO_HBASE = "intoHBase";
	//二级索引
	public static final String SECOND_INDEX = "secondIndex";
	//webSQL
	public static final String WEB_SQL = "webSQL";
	//集市
	public static final String MARKET = "market";
	//加工
	public static final String MACHINING = "machining";
	//接口
	public static final String INTERFACE = "interface";
	//报表
	public static final String REPORT = "report";
	//机器学习
	public static final String MACHINE_LEARNING = "machineLearning";
	//外部
	public static final String EXTERNAL = "external";
	//数据管控
	public static final String DATA_MANAGEMENT = "dataManagement";
	//数据对标
	public static final String DATA_BENCHMARKING = "dataBenchmarking";

	public static List<String> treeSourceList = new ArrayList<>(Arrays.
			asList(INTO_HBASE, SECOND_INDEX, WEB_SQL, MARKET, MACHINING, INTERFACE, REPORT, MACHINE_LEARNING,
					EXTERNAL, DATA_MANAGEMENT, DATA_BENCHMARKING));

	//集市,加工,数据管控 树菜单列表
	private static DataSourceType[] DATA_MANAGEMENT_ARRAY = new DataSourceType[]{DataSourceType.DCL,
			DataSourceType.DML, DataSourceType.DQC, DataSourceType.UDL};
	//webSQL树菜单列表
	private static DataSourceType[] WEB_SQL_ARRAY = new DataSourceType[]{DataSourceType.DCL,
			DataSourceType.DML, DataSourceType.DQC, DataSourceType.UDL};
	//数据进HBase,接口,报表,外部 树菜单列表
	private static DataSourceType[] HIRE_ARRAY = new DataSourceType[]{DataSourceType.DCL,
			DataSourceType.DML, DataSourceType.DQC, DataSourceType.UDL};
	//二级索引
	private static DataSourceType[] SECOND_INDEX_ARRAY = new DataSourceType[]{DataSourceType.DCL,
			DataSourceType.DML, DataSourceType.DQC, DataSourceType.UDL};
	//机器学习
	private static DataSourceType[] MACHINE_LEARNING_ARRAY = new DataSourceType[]{DataSourceType.DCL,
			DataSourceType.DML, DataSourceType.DQC, DataSourceType.UDL};
	//数据对标
	private static DataSourceType[] DATA_BENCHMARKING_ARRAY = new DataSourceType[]{DataSourceType.DCL,
			DataSourceType.DML, DataSourceType.DQC, DataSourceType.UDL};

	public static Map<String, DataSourceType[]> TREE_SOURCE = new HashMap<>();

	//初始化树页面类型
	static {
		//进HBase
		TREE_SOURCE.put(INTO_HBASE, HIRE_ARRAY);
		//二级索引
		TREE_SOURCE.put(SECOND_INDEX, SECOND_INDEX_ARRAY);
		//WEB SQL
		TREE_SOURCE.put(WEB_SQL, WEB_SQL_ARRAY);
		//集市
		TREE_SOURCE.put(MARKET, DATA_MANAGEMENT_ARRAY);
		//加工
		TREE_SOURCE.put(MACHINING, DATA_MANAGEMENT_ARRAY);
		//接口
		TREE_SOURCE.put(INTERFACE, HIRE_ARRAY);
		//报表
		TREE_SOURCE.put(REPORT, HIRE_ARRAY);
		//机器学习
		TREE_SOURCE.put(MACHINE_LEARNING, MACHINE_LEARNING_ARRAY);
		//外部
		TREE_SOURCE.put(EXTERNAL, HIRE_ARRAY);
		//数据管控
		TREE_SOURCE.put(DATA_MANAGEMENT, DATA_MANAGEMENT_ARRAY);
		//数据对标
		TREE_SOURCE.put(DATA_BENCHMARKING, DATA_BENCHMARKING_ARRAY);
	}
}
