package hrds.agent.job.biz.dataclean.tableclean;

import org.apache.parquet.example.data.Group;

import java.util.List;
import java.util.Map;

/*
 * 表清洗规则接口，定义了按表清洗的4种清洗规则
 * */
public interface TableCleanInterface {

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
	 * 列合并
	 *
	 * TODO 未完成
	 *
	 * @Param: mergeRule Map<String, String>
	 *         含义：存放有列合并规则的map集合
	 *         取值范围：不为空，key为合并后的列名`合并后的列类型，value为原列的列名
	 * @Param: columnsValue String[]
	 *         含义：待合并的若干列的列值
	 *         取值范围：不为空
	 * @Param: columnsName String[]
	 *         含义：待合并的若干列的列名
	 *         取值范围：不为空
	 * @Param: group Group
	 *         含义：用于写Parquet的一行数据
	 *         取值范围：不为空
	 * @Param: lineData List<Object>
	 *         含义：用于写ORC
	 *         取值范围：不为空
	 * @Param: fileType String
	 *         含义：卸数落地数据文件的格式
	 *         取值范围：不为空，FileFormatConstant代码项的code
	 *
	 * @return: String
	 *          含义：清洗后的字段值
	 *          取值范围：不会为nulll
	 *
	 * */
	String merge(Map<String, String> mergeRule, String[] columnsValue,
	             String[] columnsName, Group group, List<Object> lineData, String fileType);
}
