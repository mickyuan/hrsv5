package hrds.b.biz.agent.datafileconf;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.utils.StringUtil;
import hrds.commons.exception.BusinessException;

@DocClass(desc = "检查参数的信息", author = "Mr.Lee", createdate = "2020-04-13 15:04")
public class CheckParam {

  @Method(desc = "检查非空参数的公共方法", logicStep = "检查当前数据是否为null或者为空,如果是则提示异常信息")
  @Param(name = "errorMsg", desc = "提示的错误信息", range = "可以为空")
  @Param(name = "columnData", desc = "检查的数据", range = "可以为空")
  @Param(name = "errorParam", desc = "错误信息里面的占位参数值", range = "可以为空")
  public static void checkData(String errorMsg, String columnData, Object... errorParam) {

    if (columnData == null || StringUtil.isBlank(columnData))
      throwErrorMsg(String.format(errorMsg, errorParam));
  }

  @Method(desc = "异常抛出的提示", logicStep = "提示异常信息")
  @Param(
      name = "errorMsg",
      desc = "提示的错误信息",
      range = "可以为空",
      example = "填写如: 表( %s,%s )不存在,需要传递2个占位的参数值")
  @Param(name = "errorParam", desc = "错误信息里面的占位参数值", range = "可以为空")
  public static void throwErrorMsg(String errorMsg, Object... errorParam) {

    throw new BusinessException(String.format(errorMsg, errorParam));
  }
}
