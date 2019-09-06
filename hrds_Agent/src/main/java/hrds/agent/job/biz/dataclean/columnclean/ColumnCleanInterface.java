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
	 * @param replaceMap  {@link Map<String, String>}
	 *                    key : 原字符串  value : 新字符串
	 * @param columnValue {@link String} 列值
	 * @return {@link String} 清洗后的内容
	 */
	public String replace(Map<String, String> replaceMap, String columnValue);

	/**
	 * 字符补齐
	 *
	 * @param completeSB  {@link StringBuilder}
	 *                    格式为：补齐长度^补齐方式^要补齐的字符串
	 * @param columnValue {@link String} 列值
	 * @return {@link String} 清洗后的内容
	 */
	public String complete(StringBuilder completeSB, String columnValue);

	/**
	 * 日期转换
	 *
	 * @param dateSB      格式为：新格式^老格式
	 * @param columnValue
	 * @return
	 */
	public String dateConver(StringBuilder dateSB, String columnValue) throws ParseException;

	/**
	 * 码值转换
	 *
	 * @param ruleMap     key : searchString  value : replacement
	 * @param columnValue
	 * @return
	 */
	public String CVConver(Map<String, String> ruleMap, String columnValue);

	/**
	 * 首尾去空
	 *
	 * @param flag        true : 进行去空     false : 不进行去空
	 * @param columnValue
	 * @return
	 */
	public String trim(Boolean flag, String columnValue);

	/**
	 * 字段拆分
	 *
	 * @param rule        ColumnSplitBean：字段拆分实体类
	 * @param columnValue 列值
	 * @param columnName  列名
	 * @param group       用于写Parquet
	 * @param colType     列类型
	 * @param fileType    卸数落地数据文件的格式
	 * @param lineData    用于写ORC
	 * @return
	 */
	public String split(List<ColumnSplitBean> rule, String columnValue, String columnName, Group group, String colType, String fileType, List<Object> lineData);
}
