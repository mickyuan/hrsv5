package hrds.agent.job.biz.dataclean.tableclean;

import fd.ng.core.annotation.Class;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import org.apache.parquet.example.data.Group;

import java.util.List;
import java.util.Map;

@Class(desc = "数据库直连采集表清洗规则接口适配器，抽象类中提供接口中所有抽象方法的空实现，请子类继承抽象类后按功能点给出方法的具体实现", author = "WangZhengcheng")
public abstract class AbstractTableClean implements TableCleanInterface {

	@Method(desc = "字符替换，抽象类中给一个空实现，后面具体的表清洗实现类只需按需实现某个清洗方法", logicStep = "")
	@Param(name = "replaceMap", desc = "存放有字符替换规则的map集合", range = "不为空，key : 原字符串  value : 新字符串")
	@Param(name = "columnValue", desc = "待清洗字段值", range = "不为空")
	@Return(desc = "清洗后的字段值", range = "不会为null")
	@Override
	public String replace(Map<String, String> replaceMap, String columnValue) {
		throw new IllegalStateException("这是一个空实现");
	}

	@Method(desc = "字符补齐，抽象类中给一个空实现，后面具体的表清洗实现类只需按需实现某个清洗方法", logicStep = "")
	@Param(name = "completeSB", desc = "用于字符补齐", range = "不为空, 格式为：补齐长度`补齐方式`要补齐的字符串")
	@Param(name = "columnValue", desc = "待清洗字段值", range = "不为空")
	@Return(desc = "清洗后的字段值", range = "不会为null")
	@Override
	public String complete(StringBuilder completeSB, String columnValue) {
		throw new IllegalStateException("这是一个空实现");
	}

	@Method(desc = "首尾去空，抽象类中给一个空实现，后面具体的表清洗实现类只需按需实现某个清洗方法", logicStep = "")
	@Param(name = "flag", desc = "是否进行首尾去空", range = "true(进行去空) false(不进行去空)")
	@Param(name = "columnValue", desc = "待清洗字段值", range = "不为空")
	@Return(desc = "清洗后的字段值", range = "不会为null")
	@Override
	public String trim(Boolean flag, String columnValue) {
		throw new IllegalStateException("这是一个空实现");
	}

	@Method(desc = "列合并，抽象类中给一个空实现，后面具体的表清洗实现类只需按需实现某个清洗方法", logicStep = "")
	@Param(name = "mergeRule", desc = "存放有列合并规则的map集合", range = "不为空，key为合并后的列名`合并后的列类型，value为原列的列名")
	@Param(name = "columnsValue", desc = "待合并的若干列的列值", range = "不为空")
	@Param(name = "columnsName", desc = "待合并的若干列的列名", range = "不为空")
	@Param(name = "group", desc = "用于写Parquet的一行数据", range = "不为空")
	@Param(name = "lineData", desc = "用于写ORC", range = "不为空")
	@Param(name = "fileType", desc = "卸数落地数据文件的格式", range = "不为空，FileFormatConstant代码项的code")
	@Return(desc = "清洗后的字段值", range = "不会为null")
	@Override
	public String merge(Map<String, String> mergeRule, String[] columnsValue,
	                    String[] columnsName, Group group, List<Object> lineData, String fileType) {
		throw new IllegalStateException("这是一个空实现");
	}
}
