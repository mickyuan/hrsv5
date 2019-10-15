package hrds.agent.job.biz.dataclean.columnclean;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import hrds.agent.job.biz.constant.CompleteTypeConstant;
import hrds.agent.job.biz.constant.JobConstant;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@DocClass(desc = "数据库直连采集列清洗字符补齐实现类,继承AbstractColumnClean抽象类，只针对一个字符补齐方法进行实现",
		author = "WangZhengcheng")
public class ColCompleteImpl extends AbstractColumnClean {

	@Method(desc = "列清洗字符补齐实现", logicStep = "" +
			"1、判断completeSB是否为空，如果不为空，表示要进行字符补齐" +
			"2、获取补齐的长度" +
			"3、获取补齐的类型(前补齐、后补齐)" +
			"4、获取补齐字符" +
			"5、调用方法进行补齐操作")
	@Param(name = "completeSB", desc = "用于字符补齐", range = "不为空, 格式为：补齐长度`补齐方式`要补齐的字符串")
	@Param(name = "columnValue", desc = "待清洗字段值", range = "不为空")
	@Return(desc = "清洗后的字段值", range = "不会为null")
	@Override
	public String complete(StringBuilder completeSB, String columnValue) {
		//1、判断completeSB是否为空，如果不为空，表示要进行字符补齐
		if (completeSB != null) {
			List<String> strings = StringUtil.split(completeSB.toString(),
					JobConstant.CLEAN_SEPARATOR);
			//2、获取补齐的长度
			int completeLength = Integer.parseInt(strings.get(0));
			//3、获取补齐的类型(前补齐、后补齐)
			String completeType = strings.get(1);
			//4、获取补齐字符
			String completeCharacter = strings.get(2);
			//5、调用方法进行补齐操作
			if (CompleteTypeConstant.BEFORE.getCode() == Integer.parseInt(completeType)) {
				// 前补齐
				columnValue = StringUtils.leftPad(columnValue, completeLength, completeCharacter);
			} else if (CompleteTypeConstant.AFTER.getCode() == Integer.parseInt(completeType)) {
				// 后补齐
				columnValue = StringUtils.rightPad(columnValue, completeLength, completeCharacter);
			}
		}
		return columnValue;
	}
}
