package hrds.agent.job.biz.dataclean.columnclean;

import hrds.agent.job.biz.bean.ColumnSplitBean;
import org.apache.parquet.example.data.Group;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/*
 * 列清洗规则接口，定义了按列清洗的6种清洗规则
 * */
public interface ColumnCleanInterface {

	/**
	 * 字符替换
	 *
	 * @Param: replaceMap Map<String, String>
	 *         含义：存放有字符替换规则的map集合
	 *         取值范围：不为空，key : 原字符串  value : 新字符串
	 * @Param: columnValue String
	 *         含义：待清洗字段值
	 *         取值范围：不为空
	 *
	 * @return: String
	 *          含义：清洗后的字段值
	 *          取值范围：不会为null
	 *
	 * */
	String replace(Map<String, String> replaceMap, String columnValue);

	/**
	 * 字符补齐
	 *
	 * @Param: completeSB StringBuilder
	 *         含义：用于字符补齐
	 *         取值范围：不为空, 格式为：补齐长度`补齐方式`要补齐的字符串
	 * @Param: columnValue String
	 *         含义：待清洗字段值
	 *         取值范围：不为空
	 *
	 * @return: String
	 *          含义：清洗后的字段值
	 *          取值范围：不会为null
	 *
	 * */
	String complete(StringBuilder completeSB, String columnValue);

	/**
	 * 日期转换
	 *
	 * @Param: dateSB StringBuilder
	 *         含义：用于日期转换
	 *         取值范围：不为空, 格式为：新格式`老格式
	 * @Param: columnValue String
	 *         含义：待清洗字段值
	 *         取值范围：不为空
	 *
	 * @return: String
	 *          含义：清洗后的字段值
	 *          取值范围：不会为null
	 *
	 * */
	String dateConver(StringBuilder dateSB, String columnValue) throws ParseException;

	/**
	 * 码值转换
	 *
	 * @Param: ruleMap Map<String, String>
	 *         含义：用于码值转换的map
	 *         取值范围：不为空, key : searchString  value : replacement
	 * @Param: columnValue String
	 *         含义：待清洗字段值
	 *         取值范围：不为空
	 *
	 * @return: String
	 *          含义：清洗后的字段值
	 *          取值范围：不会为null
	 *
	 * */
	String CVConver(Map<String, String> ruleMap, String columnValue);

	/**
	 * 首尾去空
	 *
	 * @Param: flag Boolean
	 *         含义：是否进行首尾去空
	 *         取值范围：true(进行去空) false(不进行去空)
	 * @Param: columnValue String
	 *         含义：待清洗字段值
	 *         取值范围：不为空
	 *
	 * @return: String
	 *          含义：清洗后的字段值
	 *          取值范围：不会为null
	 *
	 * */
	String trim(Boolean flag, String columnValue);

	/**
	 * 字段拆分
	 *
	 * @Param: rule List<ColumnSplitBean>
	 *         含义：存放字段拆分规则的List集合
	 *         取值范围：不为空
	 * @Param: columnValue String
	 *         含义：待清洗字段值
	 *         取值范围：不为空
	 * @Param: columnName String
	 *         含义：列名
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
	 * @Param: lineData List<Object>
	 *         含义：用于写ORC
	 *         取值范围：不为空
	 *
	 * @return: String
	 *          含义：清洗后的字段值
	 *          取值范围：不会为null
	 *
	 * */
	String split(List<ColumnSplitBean> rule, String columnValue, String columnName,
	             Group group, String colType, String fileType, List<Object> lineData);

}
