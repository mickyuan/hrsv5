package hrds.agent.job.biz.utils;

/**
 * @description: 用于校验列类型中是否包含以下特殊类型
 * @author: WangZhengcheng
 * @create: 2019-08-29 12:01
 **/
public class DataTypeCheck {

	private static final String INT = "int";
	private static final String INTEGER = "integer";
	private static final String SMALLINT = "smallint";
	private static final String TINYINT = "tinyint";
	private static final String BIGINT = "bigint";
	private static final String REAL = "real";
	private static final String FLOAT = "float";
	private static final String DECIMAL = "decimal";
	private static final String NUMBER = "number";
	private static final String NUMERIC = "numeric";
	private static final String DATE = "date";

	public static boolean checkType(String str) {
		str = str.toLowerCase();
		return str.indexOf("int") > -1 || str.indexOf("integer") > -1 ||
				str.indexOf("smallint") > -1 || str.indexOf("tinyint") > -1 ||
				str.indexOf("bigint") > -1 || str.indexOf("real") > -1 ||
				str.indexOf("float") > -1 || str.indexOf("decimal") > -1 ||
				str.indexOf("number") > -1 || str.indexOf("numeric") > -1 || str.indexOf("date") > -1;
	}

}
