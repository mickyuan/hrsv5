package hrds.a.biz.codes;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.db.resultset.Result;
import fd.ng.web.action.AbstractWebappBaseAction;
import hrds.commons.codes.AgentStatus;
import hrds.commons.codes.CodesItem;
import hrds.commons.codes.fdCode.WebCodesItem;
import hrds.commons.exception.AppSystemException;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @program: hrsv5
 * @description: 获取代码项
 * @author: xchao
 * @create: 2019-10-21 15:11
 */
@DocClass(desc = "前端获取代码项的公共方法",author = "xchao",createdate = "2019年10月21日 10:36:03")
public class GenCodesItem extends AbstractWebappBaseAction {

	@fd.ng.core.annotation.Method(desc = "根据代码项分组编号和代码项值，获取中文名称",
			logicStep = "1、根据map获取代码项；2、反射调用ofValueByCode")
	@Param(name = "category", desc = "代码项分组编号", range = "代码项分组编号")
	@Param(name = "code", desc = "代码项编号", range = "代码项编号")
	@Return(desc = "返回代码项中文含义", range = "代码项中文含义")
	public String getValue(String category, String code) {
		return WebCodesItem.getValue(category, code);
	}

	@fd.ng.core.annotation.Method(desc = "根据代码项分组编号，获取该代码项所有的信息",
			logicStep = "1、根据map获取代码项；2、反射调用调用枚举的4个方法，4、将数据放到result中返回")
	@Param(name = "category", desc = "代码项分组编号", range = "代码项分组编号")
	@Return(desc = "返回该代码项的所有信息", range = "返回该代码项的所有信息")
	public Result getCategoryItems(String category) {
		return WebCodesItem.getCategoryItems(category);
	}

	@fd.ng.core.annotation.Method(desc = "根据代码项分组编号，获取代码项code值，key为枚举类名",
			logicStep = "1、根据map获取代码项；2、反射调用调用枚举的4个方法，4、将数据放到result中返回")
	@Param(name = "category", desc = "代码项分组编号", range = "代码项分组编号")
	@Return(desc = "返回该代码项的所有信息", range = "返回该代码项的所有信息")
	public Map<String,String> getCodeItems(String category) {
		return WebCodesItem.getCodeItems(category);
	}

	@fd.ng.core.annotation.Method(desc = "获取代码项信息，提供前端一一对应进行查看",
			logicStep = "1、根据map获取代码项；2、反射调用调用枚举的4个方法，4、将数据放到result中返回")
	@Return(desc = "返回系统中所有代码项信息", range = "返回系统中所有代码项信息")
	public Map<String, Object> getAllCodeItems() {
		return WebCodesItem.getAllCodeItems();
	}
}
