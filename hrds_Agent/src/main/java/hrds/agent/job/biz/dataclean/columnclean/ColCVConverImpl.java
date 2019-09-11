package hrds.agent.job.biz.dataclean.columnclean;

import fd.ng.core.utils.StringUtil;

import java.util.Map;

/**
 * ClassName: ColCVConverImpl <br/>
 * Function: 数据库直连采集列清洗码值转换实现类 <br/>
 * Reason: 继承AbstractColumnClean抽象类，只针对一个码值转换方法进行实现
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public class ColCVConverImpl extends AbstractColumnClean {
	/**
	 * @Description: 列清洗码值转换实现
	 * @Param: [ruleMap : 用于码值转换的map,  key : searchString  value : replacement, 取值范围 : Map<String, String> ]
	 * @Param: [columnValue : 列值, 取值范围 : String]
	 * @return: java.lang.String
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/11
	 * 步骤：
	 * 1、判断ruleMap是否为空，如果不为空，表示要进行码值转换操作
	 * 2、调用方法进行码值转换
	 */
	@Override
	public String CVConver(Map<String, String> ruleMap, String columnValue) {
		//1、判断ruleMap是否为空，如果不为空，表示要进行码值转换操作
		if (ruleMap != null && !ruleMap.isEmpty()) {
			for (String key : ruleMap.keySet()) {
				if (columnValue.equalsIgnoreCase(key)) {
					//2、调用方法进行码值转换
					columnValue = StringUtil.replace(columnValue, key, ruleMap.get(key));
				}
			}
		}
		return columnValue;
	}
}
