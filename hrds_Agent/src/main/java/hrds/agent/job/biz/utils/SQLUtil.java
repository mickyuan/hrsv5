package hrds.agent.job.biz.utils;

import hrds.agent.job.biz.constant.JobConstant;
import hrds.commons.codes.DatabaseType;

import java.util.Set;

/**
 * ClassName: SQLUtil <br/>
 * Function: 根据表名和列名获取采集SQL <br/>
 * Reason: 数据库直连采集
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public class SQLUtil {
	/**
	 * @Description: 根据表名和列名获取采集SQL, 根据不同数据库的类型对SQL语句中的列名进行处理
	 * @Param: tableName:表名, 取值范围 : String
	 * @Param: columnName:要采集的列名, 取值范围 : Set<String>
	 * @Param: dbType:数据库类型, 取值范围 : String
	 * @return:String
	 * @Author: WangZhengcheng
	 * @Date: 2019/8/13
	 * 步骤：
	 *      1、获得数据库类型的枚举
	 *      2、判断数据库类型，在每一列后面加逗号
	 *          2-1、如果数据库类型是MySQL,则对每一列用飘号包裹
	 *          2-2、对其他类型的数据库，除了加逗号之外不做特殊处理
	 *      3、去掉最后一列的最后一个逗号
	 *      4、组装完整的SQL语句
	 *          4-1、如果数据库类型是MySQL,则对表名用飘号包裹
	 *          4-2、如果数据库类型是Oracle，则进行特殊处理
	 *          4-3、除上述两种数据库，其他数据库不做特殊处理
	 */
	public static String getCollectSQL(String tableName, Set<String> columnName, String dbType) {
		//1、获得数据库类型的枚举
		DatabaseType typeConstant = DatabaseType.ofEnumByCode(dbType);
		StringBuilder columnSB = new StringBuilder();
		//2、判断数据库类型，在每一列后面加逗号
		for (String s : columnName) {
			//2-1、如果数据库类型是MySQL,则对每一列用飘号包裹
			if (typeConstant == DatabaseType.MYSQL) {
				columnSB.append(JobConstant.CLEAN_SEPARATOR).append(s).append(JobConstant.CLEAN_SEPARATOR).append(JobConstant.COLUMN_SEPARATOR);
			} else {
				//2-2、对其他类型的数据库，除了加逗号之外不做特殊处理
				columnSB.append(s).append(JobConstant.COLUMN_SEPARATOR);
			}
		}
		//3、去掉最后一列的最后一个逗号
		String column = columnSB.toString().substring(0, columnSB.toString().length() - 1);
		//4、组装完整的SQL语句
		if (typeConstant == DatabaseType.MYSQL) {
			//4-1、如果数据库类型是MySQL,则对表名用飘号包裹
			return "select " + column + " from " + JobConstant.CLEAN_SEPARATOR + tableName + JobConstant.CLEAN_SEPARATOR;
		} else if (typeConstant == DatabaseType.Oracle9i || typeConstant == DatabaseType.Oracle10g) {
			//4-2、如果数据库类型是Oracle，则进行如下处理
			return "select /*+parallel(" + tableName + ",4)*/ " + column + " from " + tableName;
		} else {
			//4-3、对其他数据库，不做特殊处理
			return "select " + column + " from " + tableName;
		}
	}
}
