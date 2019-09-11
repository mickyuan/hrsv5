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
	/**
	 * @Description: 列清洗日期格式化实现
	 * @Param: [dateSB : 用于日期转换,格式为：新格式`老格式, 取值范围 : StringBuilder]
	 * @Param: [columnValue : 列值, 取值范围 : String]
	 * @return: java.lang.String
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/11
	 * 步骤：
	 * 1、判断dateSB是否为空，如果不为空，表示要进行日期格式化
	 * 2、按照分隔符分割dateSB，获得新格式和原格式
	 * 3、进行格式化
	 */
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
