package hrds.agent.job.biz.dataclean.columnclean;

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

	@Override
	public String dateConver(StringBuilder dateSB, String columnValue) throws ParseException {
		if (dateSB != null) {
			List<String> strings = StringUtil.split(dateSB.toString(), JobConstant.CLEAN_SEPARATOR);
			//新格式
			SimpleDateFormat newformat = new SimpleDateFormat(strings.get(0));
			//原格式
			SimpleDateFormat oldformat = new SimpleDateFormat(strings.get(1));
			Date parse = oldformat.parse(columnValue);
			//格式化成自定义
			columnValue = newformat.format(parse);
		}
		return columnValue;
	}
}
