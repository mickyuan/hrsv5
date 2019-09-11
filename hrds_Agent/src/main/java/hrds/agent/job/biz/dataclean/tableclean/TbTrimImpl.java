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
	/**
	 * @Description: 列清洗首尾去空实现
	 * @Param: [flag : 是否进行首尾去空, 取值范围 : true(进行去空) false(不进行去空)]
	 * @Param: [columnValue : 列值, 取值范围 : String]
	 * @return: java.lang.String
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/11
	 * 步骤：
	 * 1、判断是否进行首尾去空
	 * 2、调用方法进行首尾去空
	 */
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
