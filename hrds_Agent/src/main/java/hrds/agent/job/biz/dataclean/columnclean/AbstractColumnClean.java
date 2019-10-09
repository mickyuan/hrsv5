package hrds.agent.job.biz.dataclean.columnclean;

import hrds.agent.job.biz.bean.ColumnSplitBean;
import org.apache.parquet.example.data.Group;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * ClassName: AbstractCleanRule <br/>
 * Function: 数据库直连采集列清洗规则接口适配器 <br/>
 * Reason: 抽象类中提供接口中所有抽象方法的空实现，请子类继承抽象类后按功能点给出方法的具体实现
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public abstract class AbstractColumnClean implements ColumnCleanInterface {

	/**
	 * 字符替换,这里给一个空实现，后面具体的列清洗实现类只需按需实现某个清洗方法
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
	@Override
	public String replace(Map<String, String> replaceMap, String columnValue) {
		throw new IllegalStateException("这是一个空实现");
	}

	/**
	 * 字符补齐,这里给一个空实现，后面具体的列清洗实现类只需按需实现某个清洗方法
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
	@Override
	public String complete(StringBuilder completeSB, String columnValue)
	{
		throw new IllegalStateException("这是一个空实现");
	}

	/**
	 * 日期转换,这里给一个空实现，后面具体的列清洗实现类只需按需实现某个清洗方法
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
	@Override
	public String dateConver(StringBuilder dateSB, String columnValue) throws ParseException {
		throw new IllegalStateException("这是一个空实现");
	}

	/**
	 * 码值转换,这里给一个空实现，后面具体的列清洗实现类只需按需实现某个清洗方法
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
	@Override
	public String CVConver(Map<String, String> ruleMap, String columnValue) {
		throw new IllegalStateException("这是一个空实现");
	}

	/**
	 * 首尾去空,这里给一个空实现，后面具体的列清洗实现类只需按需实现某个清洗方法
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
	@Override
	public String trim(Boolean flag, String columnValue) {
		throw new IllegalStateException("这是一个空实现");
	}

	/**
	 * 字段拆分,这里给一个空实现，后面具体的列清洗实现类只需按需实现某个清洗方法
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
	@Override
	public String split(List<ColumnSplitBean> rule, String columnValue, String columnName,
	                    Group group, String colType, String fileType, List<Object> lineData) {
		throw new IllegalStateException("这是一个空实现");
	}
}
