package hrds.commons.utils;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.web.util.Dbo;
import hrds.commons.codes.CollectType;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;

@DocClass(desc = "数据表工具类", author = "BY-HLL", createdate = "2019/11/4 0004 下午 02:35")
public class DataTableUtil {


	@Method(desc = "获取在所有存储层中是否存在该表",
			logicStep = "1.根据表名获取在所有存储层中是否存在该表")
	@Param(name = "tableName", desc = "表名", range = "String类型,不大于512字符")
	@Return(desc = "boolean", range = "报错(提示在哪个存储层重复) 或者 false: 不存在")
	public static boolean tableIsRepeat(String tableName) {
		if (tableIsExistInSourceFileAttribute(tableName)) {
			throw new BusinessException("表在源文件表中已经存在!" + tableName);
		}
//		if (tableIsExistInDatatableInfo(tableName)) {
//			throw new BusinessException("表在集市数据表中已经存在!" + tableName);
//		}
//		if (tableIsExistInEdwTable(tableName)) {
//			throw new BusinessException("表在数据仓库表中已经存在!" + tableName);
//		}
//		if (tableIsExistInSdmInnerTable(tableName)) {
//			throw new BusinessException("表在流数据内部消费信息登记表中已经存在!" + tableName);
//		}
//		if (tableIsExistInMlDatatableInfo(tableName)) {
//			throw new BusinessException("表在机器学习数据信息表中已经存在!" + tableName);
//		}
//		if (tableIsExistInSysTableInfo(tableName)) {
//			throw new BusinessException("表在系统表创建信息表中已经存在!" + tableName);
//		}
		return false;
	}

	@Method(desc = "判断表是否在源文件信息表存在",
			logicStep = "1.判断表是否在源文件信息表存在")
	@Param(name = "tableName", desc = "表名", range = "String类型,不大于512字符")
	@Return(desc = "boolean", range = "true: 存在 或者 false: 不存在")
	private static boolean tableIsExistInSourceFileAttribute(String tableName) {
		//1.判断表是否在源文件信息表存在
		return Dbo.queryNumber("SELECT count(1) count FROM " + Source_file_attribute.TableName +
						" WHERE lower(hbase_name) = ? AND collect_type IN (?,?)", tableName.toLowerCase(),
				CollectType.ShuJuKuCaiJi.getCode(), CollectType.DBWenJianCaiJi.getCode()).orElseThrow(()
				-> new BusinessException("检查表名称否重复在源文件信息表的SQL编写错误")) != 0;
	}

//	@Method(desc = "判断表是否在集市数据表存在",
//			logicStep = "1.判断表是否在集市数据表存在")
//	@Param(name = "tableName", desc = "表名", range = "String类型,不大于512字符")
//	@Return(desc = "boolean", range = "true: 存在 或者 false: 不存在")
//	private static boolean tableIsExistInDatatableInfo(String tableName) {
//		//1.判断表是否在集市数据表存在
//		return Dbo.queryNumber("SELECT count(1) count FROM " + Datatable_info.TableName +
//				" WHERE lower(datatable_en_name) = lower(?)", tableName.toLowerCase()).orElseThrow(()
//				-> new BusinessException("检查表名称否重复在集市数据表的SQL编写错误")) != 0;
//	}
//
//	@Method(desc = "判断表是否在数据仓库表存在",
//			logicStep = "1.判断表是否在数据仓库表存在")
//	@Param(name = "tableName", desc = "表名", range = "String类型,不大于512字符")
//	@Return(desc = "boolean", range = "true: 存在 或者 false: 不存在")
//	private static boolean tableIsExistInEdwTable(String tableName) {
//		//1.判断表是否在集市数据表存在
//		return Dbo.queryNumber("SELECT count(1) count FROM " + Edw_table.TableName +
//				" WHERE lower(tabname) = lower(?)", tableName.toLowerCase()).orElseThrow(()
//				-> new BusinessException("检查表名称否重复在数据仓库表的SQL编写错误")) != 0;
//	}
//
//	@Method(desc = "判断表是否在流数据内部消费信息登记表存在",
//			logicStep = "1.判断表是否在流数据内部消费信息登记表存在")
//	@Param(name = "tableName", desc = "表名", range = "String类型,不大于512字符")
//	@Return(desc = "boolean", range = "true: 存在 或者 false: 不存在")
//	private static boolean tableIsExistInSdmInnerTable(String tableName) {
//		//1.判断表是否在集市数据表存在
//		return Dbo.queryNumber("SELECT count(1) count FROM " + Sdm_inner_table.TableName +
//				" WHERE lower(table_en_name) = lower(?)", tableName.toLowerCase()).orElseThrow(()
//				-> new BusinessException("检查表名称否重复在流数据内部消费信息登记表的SQL编写错误")) != 0;
//	}
//
//	@Method(desc = "判断表是否在机器学习数据信息表存在",
//			logicStep = "1.判断表是否在机器学习数据信息表存在")
//	@Param(name = "tableName", desc = "表名", range = "String类型,不大于512字符")
//	@Return(desc = "boolean", range = "true: 存在 或者 false: 不存在")
//	private static boolean tableIsExistInMlDatatableInfo(String tableName) {
//		//1.判断表是否在集市数据表存在
//		return Dbo.queryNumber("SELECT count(1) count FROM " + Ml_datatable_info.TableName +
//				" WHERE lower(stable_en_name) = lower(?)", tableName.toLowerCase()).orElseThrow(()
//				-> new BusinessException("检查表名称否重复在机器学习数据信息表的SQL编写错误")) != 0;
//	}
//
//	@Method(desc = "判断表是否在系统表创建信息表存在",
//			logicStep = "1.判断表是否在系统表创建信息表存在")
//	@Param(name = "tableName", desc = "表名", range = "String类型,不大于512字符")
//	@Return(desc = "boolean", range = "true: 存在 或者 false: 不存在")
//	private static boolean tableIsExistInSysTableInfo(String tableName) {
//		//1.判断表是否在集市数据表存在
//		return Dbo.queryNumber("SELECT count(1) count FROM " + Sys_table_info.TableName +
//				" WHERE lower(table_name) = lower(?)", tableName.toLowerCase()).orElseThrow(()
//				-> new BusinessException("检查表名称否重复在系统表创建信息表的SQL编写错误")) != 0;
//	}
}
