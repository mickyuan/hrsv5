package hrds.commons.utils;

import fd.ng.core.annotation.DocClass;

@DocClass(desc = "项目中经常用到的变量值", author = "BY-HLL", createdate = "2020/1/7 0007 上午 10:38")
public class CommonVariables {

	private static final String HD = "HD";

	//是否有大数据环境
	public static final boolean HAS_HADOOP_ENV = HD.equalsIgnoreCase
			(PropertyParaValue.getString("ver_type", HD));//是否有大数据环境
}
