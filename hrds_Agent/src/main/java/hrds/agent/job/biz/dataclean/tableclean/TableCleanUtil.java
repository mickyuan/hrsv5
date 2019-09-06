package hrds.agent.job.biz.dataclean.tableclean;

import hrds.commons.exception.AppSystemException;
import org.apache.parquet.example.data.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * ClassName: TableCleanUtil <br/>
 * Function: 数据库直连采集表清洗工具类 <br/>
 * Reason:
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public class TableCleanUtil {

	private final static Logger LOGGER = LoggerFactory.getLogger(TableCleanUtil.class);

	/**
	 * @param columnValue    : 列值
	 * @param columnName     : 列名
	 * @param group          : 写Parquet文件使用
	 * @param colType        : 列类型(长度,精度)
	 * @param fileType       : 数据落地生成文件的格式
	 * @param tableCleanRule : 清洗规则
	 * @return
	 */
	/*
	 * 1、校验入参合法性
	 * 2、根据列名拿到该表的清洗规则
	 * 3、按照清洗优先级，从大到小对该表所有数据进行数据清洗
	 * */
	public static String tbDataClean(String columnValue, String columnName, Group group, String colType, String fileType, Map<String, Object> tableCleanRule) {
		//1、校验入参合法性
		if (columnValue == null || columnName == null) {
			throw new AppSystemException("表清洗需要字段名和字段值");
		}
		if (colType == null) {
			throw new AppSystemException("表清洗需要字段类型");
		}
		if (fileType == null) {
			throw new AppSystemException("表清洗需要数据文件类型");
		}
		if (tableCleanRule == null) {
			throw new AppSystemException("表清洗规则不能为空");
		}
		//2、根据列名拿到该表的清洗规则
		Map<Integer, String> clean_order = (Map<Integer, String>) tableCleanRule.get("clean_order");
		TableCleanInterface rule = null;
		//3、从后往前遍历，目的是按照优先级的从大到小，进行数据清洗
		for (int i = clean_order.size(); i >= 1; i--) {
			switch (clean_order.get(i)) {
				//字符替换
				case "replacement":
					rule = new TbReplaceImpl();
					columnValue = rule.replace((Map<String, String>) tableCleanRule.get("replace"), columnValue);
					break;
				//字符补齐
				case "complement":
					rule = new TbCompleteImpl();
					columnValue = rule.complete((StringBuilder) tableCleanRule.get("complete"), columnValue);
					break;
				//首尾去空
				case "trim":
					rule = new TbTrimImpl();
					columnValue = rule.trim((Boolean) tableCleanRule.get("trim"), columnValue);
					break;
			}
		}
		return columnValue;
	}
}
