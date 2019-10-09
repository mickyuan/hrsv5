package hrds.agent.job.biz.dataclean.tableclean;

import fd.ng.core.utils.StringUtil;

import java.util.Map;

/**
 * ClassName: TbReplaceImpl <br/>
 * Function: 数据库直连采集表清洗字符替换实现类 <br/>
 * Reason: 继承AbstractTableClean抽象类，只针对一个字符替换方法进行实现
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public class TbReplaceImpl extends AbstractTableClean {
	/**
	 * 表清洗字符替换实现
	 *
	 * 1、判断replaceMap是否为空，不为空则表示要进行字符替换
	 * 2、遍历replaceMap，调用方法进行字符替换
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
		//1、判断replaceMap是否为空，不为空则表示要进行字符替换
		if (replaceMap != null && !(replaceMap.isEmpty())) {
			//2、遍历replaceMap，调用方法进行字符替换
			for (String OriField : replaceMap.keySet()) {
				String newField = replaceMap.get(OriField);
				columnValue = StringUtil.replace(columnValue, OriField, newField);
			}
		}
		return columnValue;
	}
}
