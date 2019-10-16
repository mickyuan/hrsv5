package hrds.agent.job.biz.dataclean.columnclean;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import hrds.agent.job.biz.bean.ColumnSplitBean;
import org.apache.parquet.example.data.Group;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

@DocClass(desc = "数据库直连采集列清洗规则接口适配器,提供接口中所有抽象方法的空实现，" +
		"请子类继承抽象类后按功能点给出方法的具体实现", author = "WangZhengcheng")
public abstract class AbstractColumnClean implements ColumnCleanInterface {

	@Method(desc = "字符替换,这里给一个空实现，后面具体的列清洗实现类只需按需实现某个清洗方法", logicStep = "")
	@Param(name = "replaceMap", desc = "存放有字符替换规则的map集合", range = "不为空，key : 原字符串  value : 新字符串")
	@Param(name = "columnValue", desc = "待清洗字段值", range = "不为空")
	@Return(desc = "清洗后的字段值", range = "不会为null")
	@Override
	public String replace(Map<String, String> replaceMap, String columnValue) {
		throw new IllegalStateException("这是一个空实现");
	}

	@Method(desc = "字符补齐,这里给一个空实现，后面具体的列清洗实现类只需按需实现某个清洗方法", logicStep = "")
	@Param(name = "completeSB", desc = "用于字符补齐", range = "不为空, 格式为：补齐长度`补齐方式`要补齐的字符串")
	@Param(name = "columnValue", desc = "待清洗字段值", range = "不为空")
	@Return(desc = "清洗后的字段值", range = "不会为null")
	@Override
	public String complete(StringBuilder completeSB, String columnValue)
	{
		throw new IllegalStateException("这是一个空实现");
	}

	@Method(desc = "日期转换,这里给一个空实现，后面具体的列清洗实现类只需按需实现某个清洗方法", logicStep = "")
	@Param(name = "dateSB", desc = "用于日期转换", range = "不为空, 格式为：新格式`老格式")
	@Param(name = "columnValue", desc = "待清洗字段值", range = "不为空")
	@Return(desc = "清洗后的字段值", range = "不会为null")
	@Override
	public String dateConver(StringBuilder dateSB, String columnValue) throws ParseException {
		throw new IllegalStateException("这是一个空实现");
	}

	@Method(desc = "码值转换,这里给一个空实现，后面具体的列清洗实现类只需按需实现某个清洗方法", logicStep = "")
	@Param(name = "ruleMap", desc = "用于码值转换的map", range = "不为空, key : searchString  value : replacement")
	@Param(name = "columnValue", desc = "待清洗字段值", range = "不为空")
	@Return(desc = "清洗后的字段值", range = "不会为null")
	@Override
	public String CVConver(Map<String, String> ruleMap, String columnValue) {
		throw new IllegalStateException("这是一个空实现");
	}

	@Method(desc = "首尾去空,这里给一个空实现，后面具体的列清洗实现类只需按需实现某个清洗方法", logicStep = "")
	@Param(name = "flag", desc = "是否进行首尾去空", range = "true(进行去空) false(不进行去空)")
	@Param(name = "columnValue", desc = "待清洗字段值", range = "不为空")
	@Return(desc = "清洗后的字段值", range = "不会为null")
	@Override
	public String trim(Boolean flag, String columnValue) {
		throw new IllegalStateException("这是一个空实现");
	}

	@Method(desc = "字段拆分,这里给一个空实现，后面具体的列清洗实现类只需按需实现某个清洗方法", logicStep = "")
	@Param(name = "rule", desc = "存放字段拆分规则的List集合", range = "不为空")
	@Param(name = "columnValue", desc = "待清洗字段值", range = "不为空")
	@Param(name = "columnName", desc = "列名", range = "不为空")
	@Param(name = "group", desc = "用于写Parquet的一行数据", range = "不为空")
	@Param(name = "colType", desc = "列类型", range = "不为空")
	@Param(name = "fileType", desc = "卸数落地数据文件的格式", range = "不为空，FileFormatConstant代码项的code")
	@Param(name = "lineData", desc = "用于写ORC", range = "不为空")
	@Return(desc = "清洗后的字段值", range = "不会为null")
	@Override
	public String split(List<ColumnSplitBean> rule, String columnValue, String columnName,
	                    Group group, String colType, String fileType, List<Object> lineData) {
		throw new IllegalStateException("这是一个空实现");
	}
}
