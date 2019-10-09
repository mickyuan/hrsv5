package hrds.agent.job.biz.dataclean.columnclean;

import hrds.agent.job.biz.bean.ColumnSplitBean;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.commons.exception.AppSystemException;
import org.apache.commons.lang3.StringUtils;
import org.apache.parquet.example.data.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * ClassName: ColumnCleanUtil <br/>
 * Function: 数据库直连采集列清洗工具类 <br/>
 * Reason:
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public class ColumnCleanUtil {

	private final static Logger LOGGER = LoggerFactory.getLogger(ColumnCleanUtil.class);

	/**
	 * 列清洗入口方法
	 *
	 * 1、校验入参合法性
	 * 2、根据列名拿到该列的清洗规则
	 * 3、按照清洗优先级，从大到小对该列数据进行数据清洗
	 *
	 * @Param: columnValue String
	 *         含义：待清洗列值
	 *         取值范围：不为空
	 * @Param: columnName String
	 *         含义：待清洗列的列名
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
	 * @Param: colCleanRule Map<String, Map<String, Object>>
	 *         含义：存放当前采集表所有列的清洗规则的Map集合
	 *         取值范围：不为空，key为列名，value为该列对应的清洗规则
	 * @Param: lineData List<Object>
	 *         含义：用于写ORC
	 *         取值范围：不为空
	 *
	 * @return: String
	 *          含义：清洗后的字段值
	 *          取值范围：不会为null
	 *
	 * */
	public static String colDataClean(String columnValue, String columnName, Group group,
	                                  String colType, String fileType,
	                                  Map<String, Map<String, Object>> colCleanRule,
	                                  List<Object> lineData) {
		//1、校验入参合法性
		if (columnValue == null || columnName == null) {
			throw new AppSystemException("列清洗需要字段名和字段值");
		}
		if (colType == null) {
			throw new AppSystemException("列清洗需要字段类型");
		}
		if (fileType == null) {
			throw new AppSystemException("列清洗需要数据文件类型");
		}
		if (colCleanRule == null) {
			throw new AppSystemException("列清洗规则不能为空");
		}
		//2、根据列名拿到该列的清洗规则
		Map<String, Object> currColumnRule = colCleanRule.get(columnName);
		Map<Integer, String> clean_order = (Map<Integer, String>) currColumnRule.get("clean_order");
		ColumnCleanInterface rule;
		//3、从后往前遍历，目的是按照优先级的从大到小，进行数据清洗
		for (int i = clean_order.size(); i >= 1; i--) {
			switch (clean_order.get(i)) {
				//字符替换
				case "replacement":
					rule = new ColReplaceImpl();
					columnValue = rule.replace((Map<String, String>) currColumnRule.get("replace"),
							columnValue);
					break;
				//字符补齐
				case "complement":
					rule = new ColCompleteImpl();
					columnValue = rule.complete((StringBuilder) currColumnRule.get("complete"),
							columnValue);
					break;
				//码值转换
				case "conversion":
					rule = new ColCVConverImpl();
					columnValue = rule.CVConver((Map<String, String>) currColumnRule.get("CVConver"),
							columnValue);
					break;
				//列拆分
				case "split":
					rule = new ColSplitImpl();
					columnValue = rule.split((List<ColumnSplitBean>) currColumnRule.get("split"),
							columnValue, columnName, group, colType, fileType, lineData);
					break;
				//首尾去空
				case "trim":
					rule = new ColTrimImpl();
					columnValue = rule.trim((Boolean) currColumnRule.get("trim"), columnValue);
					break;
				//日期格式化
				case "formatting":
					rule = new ColDateConverImpl();
					try {
						columnValue = rule.dateConver((StringBuilder) currColumnRule.get("dateConver"),
								columnValue);
					} catch (ParseException ex) {
						StringBuilder dateConver = (StringBuilder) currColumnRule.get("dateConver");
						String[] strings = StringUtils.splitByWholeSeparatorPreserveAllTokens(
								dateConver.toString(), JobConstant.CLEAN_SEPARATOR);
						LOGGER.error("日期转换发生错误 : " + strings[1] + "类型不能转换为" + strings[0] + "类型");
					}
					break;
			}
		}
		return columnValue;
	}
}
