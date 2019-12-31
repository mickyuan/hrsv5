package hrds.h.biz.market;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.IsFlag;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.DboExecute;
import hrds.commons.utils.PathUtil;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.h.biz.util.ImportAndExport;
import hrds.h.biz.util.MarketInfoImportAndExportImpl;

@DocClass(desc = "集市信息查询类", author = "BY-HLL", createdate = "2019/10/31 0031 下午 04:17")
public class MarketInfoAction extends BaseAction {

	private static final SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();

	@Method(desc = "获取登录用户集市的总存储量",
			logicStep = "数据权限验证: 根据登录用户进行数据验证")
	@Return(desc = "用户集市的总存储量", range = "返回值取值范围")
	public Result getTotalStorage() {
		return Dbo.queryResult("SELECT (CASE WHEN sources IS NULL THEN 0 ELSE sources END) sources FROM" +
				" (SELECT SUM(soruce_size) sources FROM datatable_info where data_mart_id" +
				" in(select data_mart_id from data_mart_info where create_id = ? )) t", getUserId());
	}

	@Method(desc = "获取登录用户集市的总数据表(kv)数",
			logicStep = "数据权限验证: 根据登录用户进行数据验证")
	@Return(desc = "用户集市的总数据表(kv)数", range = "返回值取值范围")
	public Result getKVStorage() {
		return Dbo.queryResult("SELECT COUNT(kv_success) files FROM datatable_info WHERE kv_success = ?" +
						" and data_mart_id in(select data_mart_id from data_mart_info where create_id = ? )",
				104, getUserId());
	}

	@Method(desc = "获取登录用户集市的总HyRenDB数",
			logicStep = "数据权限验证: 根据登录用户进行数据验证")
	@Return(desc = "用户集市的总HyRenDB数", range = "返回值取值范围")
	public Result getHyRenDBStorage() {
		return Dbo.queryResult("SELECT COUNT(hy_success) typesCount FROM datatable_info WHERE hy_success = ?" +
						" and data_mart_id in(select data_mart_id from data_mart_info where create_id = ? )",
				104, getUserId());
	}

	@Method(desc = "获取登录用户集市的总SolrDB数",
			logicStep = "数据权限验证: 根据登录用户进行数据验证")
	@Return(desc = "用户集市的总SolrDB数", range = "返回值取值范围")
	public Result getSolrDBStorage() {
		return Dbo.queryResult("SELECT COUNT(solr_success) solrCount FROM datatable_info WHERE solr_success = ?" +
						" and data_mart_id in(select data_mart_id from data_mart_info where create_id = ? )",
				104, getUserId());
	}

	@Method(desc = "获取登录用户集市占用存储量前三 ",
			logicStep = "数据权限验证: 根据登录用户进行数据验证:")
	@Return(desc = "用户集市占用存储量前三 ", range = "返回值取值范围")
	public Result getMarketTakesUpTop3Storage() {
		return Dbo.queryResult("SELECT data_mart_info.data_mart_id,mart_name,sum(soruce_size) source_size FROM" +
				" datatable_info,data_mart_info WHERE data_mart_info.data_mart_id=datatable_info.data_mart_id" +
				" and data_mart_info.data_mart_id in(select data_mart_id from data_mart_info where" +
				" create_id =?) GROUP BY data_mart_info.data_mart_id,mart_name ORDER BY source_size DESC" +
				" limit 3 ", getUserId());
	}

	@Method(desc = "获取登录用户集市HyRenDB占用存储前三",
			logicStep = "数据权限验证: 根据登录用户进行数据验证")
	@Return(desc = "用户集市HyRenDB占用存储前三 ", range = "返回值取值范围")
	public Result getMarketHyRenDbTop3Storage() {
		return Dbo.queryResult("SELECT datatable_cn_name, soruce_size FROM datatable_info WHERE is_hyren_db= ? " +
						" and data_mart_id in(select data_mart_id from data_mart_info where create_id = ? )" +
						" ORDER BY soruce_size DESC limit 3",
				IsFlag.Shi.getCode(), getUserId());
	}

	@Method(desc = "获取登录用户集市ketKeyValue占用存储前三",
			logicStep = "数据权限验证: 根据登录用户进行数据验证")
	@Return(desc = "用户集市ketKeyValue占用存储前三 ", range = "返回值取值范围")
	public Result getMarketKeyValueTop3Storage() {
		return Dbo.queryResult("SELECT datatable_cn_name, soruce_size FROM datatable_info WHERE is_kv_db= ? " +
						" and data_mart_id in(select data_mart_id from data_mart_info where create_id = ? )" +
						" ORDER BY soruce_size DESC limit 3",
				IsFlag.Shi.getCode(), getUserId());
	}

	@Method(desc = "获取登录用户集市SolrDB占用存储前三",
			logicStep = "数据权限验证: 根据登录用户进行数据验证")
	@Return(desc = "用户集市SolrDB占用存储前三 ", range = "返回值取值范围")
	public Result getMarketSolrDBTop3Storage() {
		return Dbo.queryResult("SELECT datatable_cn_name, soruce_size FROM datatable_info WHERE is_solr_db= ? " +
						" and data_mart_id in(select data_mart_id from data_mart_info where create_id = ? )" +
						" ORDER BY soruce_size DESC limit 3",
				IsFlag.Shi.getCode(), getUserId());
	}

	@Method(desc = "获取登录用户查询数据集市所有信息",
			logicStep = "数据权限验证: 根据登录用户进行数据验证")
	@Return(desc = "返回值说明", range = "返回值取值范围")
	public Result getMarketInfo() {
		return Dbo.queryResult("SELECT mart_name,data_mart_id FROM data_mart_info where create_id = ? order by " +
				"data_mart_id asc", getUserId());
	}

//	@Method(desc = "新增集市",
//			logicStep = "1.校验输入参数正确性" +
//					"2.设置集市属性信息(非页面传入的值)" +
//					"3.新增集市信息")
//	@Param(name = "dataMartInfo", desc = "Data_mart_info 的实体", range = "Data_mart_info 的实体", example = "sysUser",
//			isBean = true)
//	@Return(desc = "void 无返回结果", range = "无")
//	public void addMarketInfo(Data_mart_info dataMartInfo) {
//		//数据权限校验：根据登录用户的 user_id 进行权限校验
//		//1.校验输入参数正确性
//		if (StringUtil.isBlank(dataMartInfo.getMart_name())) {
//			throw new BusinessException("集市名称不能为空! mart_name=" + dataMartInfo.getMart_name());
//		}
//		if (StringUtil.isBlank(dataMartInfo.getMart_number())) {
//			throw new BusinessException("集市编号不能为空! mart_number=" + dataMartInfo.getMart_number());
//		}
//		//2.设置集市属性信息
//		if (checkMarketNameIsRepeat(dataMartInfo.getMart_name())) {
//			throw new BusinessException("集市名称重复! mart_name=" + dataMartInfo.getMart_name());
//		}
//		if (checkMarketNumberIsRepeat(dataMartInfo.getMart_name())) {
//			throw new BusinessException("集市编号名称重复! mart_number=" + dataMartInfo.getMart_number());
//		}
//		dataMartInfo.setData_mart_id(PrimayKeyGener.getNextId());
//		dataMartInfo.setMart_storage_path(PathUtil.DMLRELEASE);
//		dataMartInfo.setCreate_date(DateUtil.getSysDate());
//		dataMartInfo.setCreate_time(DateUtil.getSysTime());
//		//3.新增集市信息
//		dataMartInfo.add(Dbo.db());
//	}

//	@Method(desc = "删除集市",
//			logicStep = "1.检查集市下是否还存在数据表" +
//					"2.删除集市")
//	@Param(name = "data_mart_id", desc = "集市id", range = "long类型值,最长19位", example = "5000000000")
//	@Return(desc = "集市导出的加密文件", range = "hrds结尾的文件")
//	public void deleteMarketInfo(long data_mart_id) {
//		//1.检查集市下是否还存在数据表
//		if (checkExistDataUnderTheMarket(data_mart_id)) {
//			throw new BusinessException("集市下还存在数据表!清先删除数据表");
//		}
//		//2.删除集市
//		DboExecute.deletesOrThrow("删除集市失败!", "DELETE FROM data_mart_info WHERE data_mart_id = ?"
//				, data_mart_id);
//	}
//
//	@Method(desc = "集市导出",
//			logicStep = "1.判断集市是否存在" +
//					"2.导出集市")
//	@Param(name = "data_mart_id", desc = "集市id", range = "long类型,最大19位")
//	@Return(desc = "集市导出的加密文件", range = "文件后缀名为hrds")
//	public void marketExport(long data_mart_id) {
//		//1.判断集市是否存在
//		if (!checkIsExistsMarket(data_mart_id)) {
//			throw new BusinessException("集市不存在! data_mart_id=" + data_mart_id);
//		}
//		//2.导出集市
//		ImportAndExport importAndExport = new MarketInfoImportAndExportImpl();
//		importAndExport.dataExport(data_mart_id);
//	}


//	@Method(desc = "集市导入",
//			logicStep = "1.导入集市")
//	@Param(name = "filePath", desc = "待导入的集市加密文件路径", range = "")
//	@Return(desc = "集市导出的加密文件", range = "文件后缀名为hrds")
//	public void marketImport(String filePath) {
//		//1.导入集市
//		ImportAndExport importAndExport = new MarketInfoImportAndExportImpl();
//		importAndExport.dataImport(filePath, getUserId());
//	}

//	@Method(desc = "检查集市名称是否重复",
//			logicStep = "1.根据 mart_name 检查集市名称是否重复")
//	@Param(name = "mart_name", desc = "集市名称", range = "String类型，长度为512，该值唯一", example = "业务集市")
//	@Return(desc = "集市名称是否重复", range = "true：重复，false：不重复")
//	public static boolean checkMarketNameIsRepeat(String mart_name) {
//		//1.根据 mart_name 检查集市名称是否重复
//		return Dbo.queryNumber("select count(data_mart_id) count from " + Data_mart_info.TableName +
//				" WHERE mart_name =?", mart_name).orElseThrow(()
//				-> new BusinessException("检查集市名称否重复的SQL编写错误")) != 0;
//	}

//	@Method(desc = "检查集市编号是否重复",
//			logicStep = "1.根据 mart_name 检查集市编号是否重复")
//	@Param(name = "mart_number", desc = "集市名称", range = "String类型，长度为512，该值唯一", example = "业务集市")
//	@Return(desc = "集市编号是否重复", range = "true：重复，false：不重复")
//	private static boolean checkMarketNumberIsRepeat(String mart_number) {
//		//1.根据 mart_name 检查集市名称是否重复
//		return Dbo.queryNumber("select count(data_mart_id) count from " + Data_mart_info.TableName +
//				" WHERE mart_number =?", mart_number).orElseThrow(()
//				-> new BusinessException("检查集市编号否重复的SQL编写错误")) != 0;
//	}

//	@Method(desc = "检查集市下是否存在数据表",
//			logicStep = "1.根据 data_mart_id 检查集市编号是否重复")
//	@Param(name = "data_mart_id", desc = "集市id", range = "long类型，长度限制19，该值唯一", example = "5000000000")
//	@Return(desc = "集市下是否还有数据表", range = "true：有，false：没有")
//	private static boolean checkExistDataUnderTheMarket(long data_mart_id) {
//		//1.根据 data_mart_id 检查集市下是否存在数据表
//		return Dbo.queryNumber("select count(data_mart_id) count from " + Datatable_info.TableName + " WHERE " +
//						"data_mart_id =?",
//				data_mart_id).orElseThrow(() -> new BusinessException("检查集市下是否存在数据的SQL编写错误")) != 0;
//	}
//
//	@Method(desc = "检查集市是否存在",
//			logicStep = "1.根据 data_mart_id 检查集市是否存在")
//	@Param(name = "data_mart_id", desc = "集市id", range = "long类型，长度限制19，该值唯一", example = "5000000000")
//	@Return(desc = "检查集市是否存在", range = "true：存在，false：不存在")
//	private static boolean checkIsExistsMarket(long data_mart_id) {
//		//1.根据 data_mart_id 检查集市是否存在
//		return Dbo.queryNumber("select count(data_mart_id) count from " + Data_mart_info.TableName + " WHERE " +
//						"data_mart_id =?",
//				data_mart_id).orElseThrow(() -> new BusinessException("检查集市下是否存在数据的SQL编写错误")) != 0;
//	}
}
