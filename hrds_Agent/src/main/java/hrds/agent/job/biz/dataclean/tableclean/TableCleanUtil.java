package hrds.agent.job.biz.dataclean.tableclean;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import hrds.commons.exception.AppSystemException;
import org.apache.parquet.example.data.Group;

import java.util.Map;

@DocClass(desc = "数据库直连采集表清洗工具类", author = "WangZhengcheng")
public class TableCleanUtil {

	@Method(desc = "表清洗入口方法", logicStep = "" +
			"1、校验入参合法性" +
			"2、根据列名拿到该表的清洗规则" +
			"3、按照清洗优先级，从大到小对该表所有数据进行数据清洗")
	@Param(name = "columnValue", desc = "待清洗字段值", range = "不为空")
	@Param(name = "columnName", desc = "待清洗列名", range = "不为空")
	@Param(name = "group", desc = "用于写Parquet的一行数据", range = "不为空")
	@Param(name = "colType", desc = "列类型", range = "不为空")
	@Param(name = "fileType", desc = "卸数落地数据文件的格式", range = "不为空，FileFormatConstant代码项的code")
	@Param(name = "tableCleanRule", desc = "存放有表清洗规则的map集合", range = "不为空，key为清洗项名称，value是清洗规则")
	@Return(desc = "", range = "")
	public static String tbDataClean(String columnValue, String columnName, Group group,
	                                 String colType, String fileType, Map<String, Object> tableCleanRule) {
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
		TableCleanInterface rule;
		//3、从后往前遍历，目的是按照优先级的从大到小，进行数据清洗
		for (int i = clean_order.size(); i >= 1; i--) {
			switch (clean_order.get(i)) {
				//字符替换
				case "replacement":
					rule = new TbReplaceImpl();
					columnValue = rule.replace((Map<String, String>) tableCleanRule.get("replace"),
							columnValue);
					break;
				//字符补齐
				case "complement":
					rule = new TbCompleteImpl();
					columnValue = rule.complete((StringBuilder) tableCleanRule.get("complete"),
							columnValue);
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
