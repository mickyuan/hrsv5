package hrds.commons.zTree.bean;

import fd.ng.core.annotation.DocClass;
import hrds.commons.utils.PathUtil;

import java.util.HashMap;
import java.util.Map;

@DocClass(desc = "树页面来源配置类", author = "BY-HLL", createdate = "2019/12/24 0024 上午 10:05")
public class TreePageSource {
	// 进HBase
	public static final String INTOHBASE = "intoHBase";
	// 二级索引
	public static final String SECONDARYINDEX = "secondaryIndex";
	// webSQL
	public static final String WEBSQL = "webSQL";
	// 集市
	public static final String MARKET = "market";
	// 加工
	public static final String MACHINING = "machining";
	// 接口
	public static final String INTERFACE = "interface";
	// 报表
	public static final String REPORT = "report";
	// 机器学习
	public static final String MACHINELEARNING = "machineLearning";
	// 外部
	public static final String EXTERNAL = "external";
	// 数据管控
	public static final String DATAMANAGEMENT = "dataManagement";

	// 集市,加工,数据管控 树菜单列表
	private static String[] DATAMANAGEMENTARRAY = new String[]{PathUtil.DCL, PathUtil.DPL, PathUtil.DML,
			PathUtil.DQC, PathUtil.UDL};
	// webSQL树菜单列表
	private static String[] WEBSQLARRAY = new String[]{PathUtil.DCL};
	//数据进HBase,接口,报表,外部 树菜单列表
	private static String[] HIREARRAY = new String[]{PathUtil.DCL, PathUtil.DPL, PathUtil.DML, PathUtil.UDL};
	private static String[] SECONDARYINDEXARRAY = new String[]{PathUtil.DCL, PathUtil.DPL, PathUtil.DML,
			PathUtil.UDL};
	private static String[] MACHINELEARNINGARRAY = new String[]{PathUtil.DCL, PathUtil.DPL, PathUtil.DML,
			PathUtil.UDL};
	//自定义 树菜单列表
	public static String[] CUSTOMIZE = new String[]{PathUtil.DCL};

	public static Map<String, String[]> TREESOURCE = new HashMap<>();

	// 初始化树页面类型
	static {
		// 进HBase
		TREESOURCE.put(INTOHBASE, HIREARRAY);
		// 二级索引
		TREESOURCE.put(SECONDARYINDEX, SECONDARYINDEXARRAY);
		// WEB SQL
		TREESOURCE.put(WEBSQL, WEBSQLARRAY);
		// 集市
		TREESOURCE.put(MARKET, DATAMANAGEMENTARRAY);
		// 加工
		TREESOURCE.put(MACHINING, DATAMANAGEMENTARRAY);
		// 接口
		TREESOURCE.put(INTERFACE, HIREARRAY);
		// 报表
		TREESOURCE.put(REPORT, HIREARRAY);
		// 机器学习
		TREESOURCE.put(MACHINELEARNING, MACHINELEARNINGARRAY);
		// 外部
		TREESOURCE.put(EXTERNAL, HIREARRAY);
		// 数据管控
		TREESOURCE.put(DATAMANAGEMENT, DATAMANAGEMENTARRAY);
	}
}
