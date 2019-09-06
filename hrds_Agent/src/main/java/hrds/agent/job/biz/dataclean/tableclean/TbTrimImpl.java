package hrds.agent.job.biz.dataclean.tableclean;

/**
 * ClassName: TbTrimImpl <br/>
 * Function: 数据库直连采集表清洗首尾去空实现类 <br/>
 * Reason: 继承AbstractTableClean抽象类，只针对一个首尾去空方法进行实现
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public class TbTrimImpl extends AbstractTableClean {

	@Override
	public String trim(Boolean flag, String columnValue) {
		if (flag) {
			columnValue = columnValue.trim();
		}
		return columnValue;
	}
}
