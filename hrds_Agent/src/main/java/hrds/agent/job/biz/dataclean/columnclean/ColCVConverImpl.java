package hrds.agent.job.biz.dataclean.columnclean;

import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
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
	@Method(desc = "列清洗码值转换实现", logicStep = "" +
			"1、判断ruleMap是否为空，如果不为空，表示要进行码值转换操作" +
			"2、调用方法进行码值转换")
	@Param(name = "ruleMap", desc = "用于码值转换的map", range = "不为空, key : searchString  value : replacement")
	@Param(name = "columnValue", desc = "待清洗字段值", range = "不为空")
	@Return(desc = "清洗后的字段值", range = "不会为null")
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
