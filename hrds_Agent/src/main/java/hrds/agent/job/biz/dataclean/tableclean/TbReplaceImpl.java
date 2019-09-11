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
	 * @Description: 列清洗字符替换实现
	 * @Param: [replaceMap : key : 原字符串  value : 新字符串, 取值范围 : Map<String, String>]
	 * @Param: [columnValue : 列值, 取值范围 : String]
	 * @return: java.lang.String
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/11
	 * 步骤：
	 * 1、判断replaceMap是否为空，不为空则表示要进行字符替换
	 * 2、遍历replaceMap，调用方法进行字符替换
	 */
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
