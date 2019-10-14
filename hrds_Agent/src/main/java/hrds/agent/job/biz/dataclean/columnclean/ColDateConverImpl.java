package hrds.agent.job.biz.dataclean.columnclean;

import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import hrds.agent.job.biz.constant.JobConstant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * ClassName: ColDateConverImpl <br/>
 * Function: 数据库直连采集列清洗日期格式化实现类 <br/>
 * Reason: 继承AbstractColumnClean抽象类，只针对一个日期格式化方法进行实现
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public class ColDateConverImpl extends AbstractColumnClean {
	@Method(desc = "列清洗日期格式化实现", logicStep = "" +
			"1、判断dateSB是否为空，如果不为空，表示要进行日期格式化" +
			"2、按照分隔符分割dateSB，获得新格式和原格式" +
			"3、进行格式化")
	@Param(name = "dateSB", desc = "用于日期转换", range = "不为空, 格式为：新格式`老格式")
	@Param(name = "columnValue", desc = "待清洗字段值", range = "不为空")
	@Return(desc = "清洗后的字段值", range = "不会为null")
	@Override
	public String dateConver(StringBuilder dateSB, String columnValue) throws ParseException {
		//1、判断dateSB是否为空，如果不为空，表示要进行日期格式化
		if (dateSB != null) {
			//2、按照分隔符分割dateSB，获得新格式和原格式
			List<String> strings = StringUtil.split(dateSB.toString(), JobConstant.CLEAN_SEPARATOR);
			SimpleDateFormat newformat = new SimpleDateFormat(strings.get(0));
			SimpleDateFormat oldformat = new SimpleDateFormat(strings.get(1));
			//3、进行格式化
			Date parse = oldformat.parse(columnValue);
			columnValue = newformat.format(parse);
		}
		return columnValue;
	}
}
