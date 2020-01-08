package hrds.commons.zTree.bean;

import fd.ng.core.annotation.DocClass;
import hrds.commons.utils.PathUtil;

import java.util.HashMap;
import java.util.Map;

@DocClass(desc = "树页面来源配置类", author = "BY-HLL", createdate = "2019/12/24 0024 上午 10:05")
public class TreePageSource {
	//进HBase
	private static final String INTO_HBASE = "intoHBase";
	//二级索引
	private static final String SECOND_INDEX = "secondIndex";
	//webSQL
	private static final String WEB_SQL = "webSQL";
	//集市
	private static final String MARKET = "market";
	//加工
	private static final String MACHINING = "machining";
	//接口
	private static final String INTERFACE = "interface";
	//报表
	private static final String REPORT = "report";
	//机器学习
	private static final String MACHINE_LEARNING = "machineLearning";
	//外部
	private static final String EXTERNAL = "external";
	//数据管控
	private static final String DATA_MANAGEMENT = "dataManagement";
	//自定义菜单列表
	private static final String CUSTOMIZE = "customize";

	//集市,加工,数据管控 树菜单列表
	private static String[] DATA_MANAGEMENT_ARRAY = new String[]{PathUtil.DCL, PathUtil.DPL, PathUtil.DML,
			PathUtil.DQC, PathUtil.UDL};
	//webSQL树菜单列表
	private static String[] WEB_SQL_ARRAY = new String[]{PathUtil.DCL, PathUtil.UDL};
	//数据进HBase,接口,报表,外部 树菜单列表
	private static String[] HIRE_ARRAY = new String[]{PathUtil.DCL, PathUtil.DPL, PathUtil.DML, PathUtil.UDL};
	//二级索引
	private static String[] SECOND_INDEX_ARRAY = new String[]{PathUtil.DCL, PathUtil.DPL, PathUtil.DML,
			PathUtil.UDL};
	//机器学习
	private static String[] MACHINE_LEARNING_ARRAY = new String[]{PathUtil.DCL, PathUtil.DPL, PathUtil.DML,
			PathUtil.UDL};
	//自定义 树菜单列表
	private static String[] CUSTOMIZE_ARRAY = new String[]{PathUtil.DCL};

	public static Map<String, String[]> TREE_SOURCE = new HashMap<>();

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
		//自定义菜单
		TREE_SOURCE.put(CUSTOMIZE, CUSTOMIZE_ARRAY);
	}

	/* 获取自定义菜单列表 */
	public static String[] getCustomizeArray() {
		return CUSTOMIZE_ARRAY;
	}

	/* 设置自定义菜单列表 */
	public static void setCustomizeArray(String[] customizeArray) {
		CUSTOMIZE_ARRAY = customizeArray;
	}
}
