package hrds.agent.job.biz.dataclean.tableclean;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;

@DocClass(desc = "数据库直连采集表清洗首尾去空实现类，继承AbstractTableClean抽象类，只针对一个首尾去空方法进行实现", author = "WangZhengcheng")
public class TbTrimImpl extends AbstractTableClean {
	@Method(desc = "表清洗首尾去空实现", logicStep = "" +
			"1、判断是否进行首尾去空" +
			"2、调用方法进行首尾去空")
	@Param(name = "flag", desc = "是否进行首尾去空", range = "true(进行去空) false(不进行去空)")
	@Param(name = "columnValue", desc = "待清洗字段值", range = "不为空")
	@Return(desc = "清洗后的字段值", range = "不会为null")
	@Override
	public String trim(Boolean flag, String columnValue) {
		//1、判断是否进行首尾去空
		if (flag) {
			//2、调用方法进行首尾去空
			columnValue = columnValue.trim();
		}
		return columnValue;
	}
}
