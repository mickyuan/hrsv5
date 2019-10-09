package hrds.agent.job.biz.dataclean.tableclean;

import hrds.commons.exception.AppSystemException;
import org.apache.parquet.example.data.Group;

import java.util.Map;

/**
 * ClassName: TableCleanUtil <br/>
 * Function: 数据库直连采集表清洗工具类 <br/>
 * Reason:
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public class TableCleanUtil {

	/**
	 * 表清洗入口方法
	 *
	 * 1、校验入参合法性
	 * 2、根据列名拿到该表的清洗规则
	 * 3、按照清洗优先级，从大到小对该表所有数据进行数据清洗
	 *
	 * @Param: columnValue String
	 *         含义：待清洗字段值
	 *         取值范围：不为空
	 * @Param: columnsName String
	 *         含义：待清洗列名
	 *         取值范围：不为空
	 * @Param: group Group
	 *         含义：用于写Parquet的一行数据
	 *         取值范围：不为空
	 * @Param: colType String
	 *         含义：列类型
	 *         取值范围：不为空
	 * @Param: fileType String
	 *         含义：卸数落地数据文件的格式
	 *         取值范围：不为空，FileFormatConstant代码项的code
	 * @Param: tableCleanRule Map<String, Object>
	 *         含义：存放有表清洗规则的map集合
	 *         取值范围：不为空，key为清洗项名称，value是清洗规则
	 *
	 * @return: String
	 *          含义：清洗后的字段值
	 *          取值范围：不会为null
	 *
	 * */
	public static String tbDataClean(String columnValue, String columnName, Group group,
	                                 String colType, String fileType, Map<String, Object> tableCleanRule) {
		//1、校验入参合法性
		if (columnValue == null || columnName == null) {
			throw new AppSystemException("表清洗需要字段名和字段值");
		}
		if (colType == null) {
			throw new AppSystemException("表清洗需要字段类型");
		}
		if (fileType == null) {
			throw new AppSystemException("表清洗需要数据文件类型");
		}
		if (tableCleanRule == null) {
			throw new AppSystemException("表清洗规则不能为空");
		}
		//2、根据列名拿到该表的清洗规则
		Map<Integer, String> clean_order = (Map<Integer, String>) tableCleanRule.get("clean_order");
		TableCleanInterface rule;
		//3、从后往前遍历，目的是按照优先级的从大到小，进行数据清洗
		for (int i = clean_order.size(); i >= 1; i--) {
			switch (clean_order.get(i)) {
				//字符替换
				case "replacement":
					rule = new TbReplaceImpl();
					columnValue = rule.replace((Map<String, String>) tableCleanRule.get("replace"),
							columnValue);
					break;
				//字符补齐
				case "complement":
					rule = new TbCompleteImpl();
					columnValue = rule.complete((StringBuilder) tableCleanRule.get("complete"),
							columnValue);
					break;
				//首尾去空
				case "trim":
					rule = new TbTrimImpl();
					columnValue = rule.trim((Boolean) tableCleanRule.get("trim"), columnValue);
					break;
			}
		}
		return columnValue;
	}
}
