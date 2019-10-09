package hrds.agent.job.biz.dataclean.columnclean;

/**
 * ClassName: ColSplitImpl <br/>
 * Function: 数据库直连采集列清洗首尾去空实现类 <br/>
 * Reason: 继承AbstractCleanRule抽象类，只针对一个首尾去空方法进行实现
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public class ColTrimImpl extends AbstractColumnClean {
	/**
	 * 列清洗首尾去空实现
	 *
	 * 1、判断是否进行首尾去空
	 * 2、调用方法进行首尾去空
	 *
	 * @Param: flag Boolean
	 *         含义：是否进行首尾去空
	 *         取值范围：true(进行去空) false(不进行去空)
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
	public String trim(Boolean flag, String columnValue) {
		//1、判断是否进行首尾去空
		if (flag) {
			//2、调用方法进行首尾去空
			columnValue = columnValue.trim();
		}
		return columnValue;
	}
}
