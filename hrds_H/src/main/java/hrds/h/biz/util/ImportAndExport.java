package hrds.h.biz.util;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;

@DocClass(desc = "集市加工元数据导入导出接口", author = "BY-HLL", createdate = "2019/11/1 0001 下午 04:01")
public interface ImportAndExport {

	//导出接口
	void dataExport(long id);

	//导入接口
	void dataImport(String filePath, long userId);
}
