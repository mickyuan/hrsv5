package hrds.agent.job.biz.dataclean.tableclean;

import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import org.apache.parquet.example.data.Group;

import java.util.List;
import java.util.Map;

/*
 * 表清洗规则接口，定义了按表清洗的4种清洗规则
 * */
public interface TableCleanInterface {

	@Method(desc = "字符替换", logicStep = "")
	@Param(name = "replaceMap", desc = "存放有字符替换规则的map集合", range = "不为空，key : 原字符串  value : 新字符串")
	@Param(name = "columnValue", desc = "待清洗字段值", range = "不为空")
	@Return(desc = "清洗后的字段值", range = "不会为null")
	String replace(Map<String, String> replaceMap, String columnValue);

	@Method(desc = "字符补齐", logicStep = "")
	@Param(name = "completeSB", desc = "用于字符补齐", range = "不为空, 格式为：补齐长度`补齐方式`要补齐的字符串")
	@Param(name = "columnValue", desc = "待清洗字段值", range = "不为空")
	@Return(desc = "清洗后的字段值", range = "不会为null")
	String complete(StringBuilder completeSB, String columnValue);

	@Method(desc = "首尾去空", logicStep = "")
	@Param(name = "flag", desc = "是否进行首尾去空", range = "true(进行去空) false(不进行去空)")
	@Param(name = "columnValue", desc = "待清洗字段值", range = "不为空")
	@Return(desc = "清洗后的字段值", range = "不会为null")
	String trim(Boolean flag, String columnValue);

	@Method(desc = "列合并", logicStep = "")
	@Param(name = "mergeRule", desc = "存放有列合并规则的map集合", range = "不为空，key为合并后的列名`合并后的列类型，value为原列的列名")
	@Param(name = "columnsValue", desc = "待合并的若干列的列值", range = "不为空")
	@Param(name = "columnsName", desc = "待合并的若干列的列名", range = "不为空")
	@Param(name = "group", desc = "用于写Parquet的一行数据", range = "不为空")
	@Param(name = "lineData", desc = "用于写ORC", range = "不为空")
	@Param(name = "fileType", desc = "卸数落地数据文件的格式", range = "不为空，FileFormatConstant代码项的code")
	@Return(desc = "清洗后的字段值", range = "不会为null")
	String merge(Map<String, String> mergeRule, String[] columnsValue,
	             String[] columnsName, Group group, List<Object> lineData, String fileType);
}
