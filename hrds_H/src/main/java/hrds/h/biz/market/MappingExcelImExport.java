package hrds.h.biz.market;

import com.sun.org.apache.bcel.internal.generic.RETURN;
import fd.ng.core.utils.DateUtil;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.Constant;
import hrds.commons.utils.key.PrimayKeyGener;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;
import java.util.Map;

public class MappingExcelImExport {
	private static final Logger logger = LogManager.getLogger();

	private final static String classification = "ExcelImport";
	private final static String preFlag = "PRE";
	private final static String postFlag = "POST";


	public void importExcel(Workbook workBook, Long data_mart_id, Long user_id) {
		Sheet sheet1 = workBook.getSheet("映射模式");
		Sheet sheet2 = workBook.getSheet("映射模式-子表");
		Long maindatatable_id = importSheet(sheet1, data_mart_id, user_id, 0L);
		importSheet(sheet2, data_mart_id, user_id, maindatatable_id);
	}

	/**
	 * 导入sheet
	 *
	 * @param sheet
	 * @param data_mart_id
	 * @param user_id
	 */
	public Long importSheet(Sheet sheet, Long data_mart_id, Long user_id, Long maindatatable_id) {
		Long datatable_id = 0L;
		//判断是否存在sheet
		if (sheet == null) {
			throw new BusinessException("导入的excel不存在映射模式sheet页，请检查");
		}
		String sql = new String();
		for (int i = 0; i < sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			if (row == null) {
				if (i == sheet.getLastRowNum() - 1) {
					saveDmOperationInfo(datatable_id, sql);
				}
				continue;
			}
			Cell cell = row.getCell(0);
			if (cell == null) {
				if (i == sheet.getLastRowNum() - 1) {
					saveDmOperationInfo(datatable_id, sql);
				}
				continue;
			}
			String stringCellValue = cell.getStringCellValue();
			if (StringUtils.isEmpty(stringCellValue)) {
				continue;
			} else if (stringCellValue.equals("输入项")) {
				if (datatable_id != 0L) {
					saveDmOperationInfo(datatable_id, sql);
				}
				sql = "select ";
				i = i + 2;
				//表名
				String tablename = getCellValue(sheet.getRow(i), 1, "表名", true);
				i = i + 2;
				String loadPolicy = getCellValue(sheet.getRow(i), 1, "加载策略", true);
				//加载策略(入库方式)
				String storageType = getStorageType(loadPolicy);
				i = i + 3;
				datatable_id = saveDmDatatable(tablename, storageType, data_mart_id, user_id, maindatatable_id);
				saveDtabRelationStore(datatable_id);
				Long count = 0L;
				while (getCellValue(sheet.getRow(i), 1, "", false) != null) {
					row = sheet.getRow(i);
					saveDatatableFieldInfo(row, datatable_id, count);
					sql += getCellValue(row, 9, "映射规则", true) + " as " + getCellValue(row, 1, "字段名", true) + ",";
					count++;
					i++;
				}
				//去除,
				sql = sql.substring(0, sql.length() - 1);
				sql += " from ";
			} else if (stringCellValue.equals("表关联")) {
				i++;
				while (getCellValue(sheet.getRow(i), 2, "", false) != null) {
					row = sheet.getRow(i);
					String databasename = getCellValue(row, 1, "模式变量", false) != null
							? getCellValue(row, 1, "模式变量", false).replace(" ", "") + "." : "";
					String tablename = getCellValue(row, 2, "源表名", true);
					String alias = getCellValue(row, 3, "别名", true);
					String jointype = getCellValue(row, 4, "关联类型", false) != null
							? getCellValue(row, 4, "关联类型", false) : "";
					String joincondition = getCellValue(row, 5, "关联条件", false) != null
							? getCellValue(row, 5, "关联条件", false) : "";
					sql += jointype + Constant.SPACE + databasename + tablename + Constant.SPACE + alias + Constant.SPACE + joincondition + Constant.SPACE;
					i++;
				}
			} else if (stringCellValue.equals("其它声明")) {
				row = sheet.getRow(i);
				String wherecondition = getCellValue(row, 1, "其它声明", false) != null
						? getCellValue(row, 1, "其它声明", false) : "";
				sql += Constant.SPACE + wherecondition;
				sql = sql.trim();
				if (sql.endsWith(";")) {
					sql = sql.substring(0, sql.length() - 1);
				}
			} else if (stringCellValue.equals("作业前置SQL")) {
				row = sheet.getRow(i);
				String precondition = getCellValue(row, 1, "前置SQL", false) != null
						? getCellValue(row, 1, "前置SQL", false) : "";
				saveDmRelevantInfo(preFlag, precondition, datatable_id);
			} else if (stringCellValue.equals("作业后置SQL")) {
				row = sheet.getRow(i);
				String postcondition = getCellValue(row, 1, "后置SQL", false) != null
						? getCellValue(row, 1, "后置SQL", false) : "";
				saveDmRelevantInfo(postFlag, postcondition, datatable_id);
			}
			if (i == sheet.getLastRowNum() - 1) {
				saveDmOperationInfo(datatable_id, sql);
			}

		}
		return datatable_id;
	}

	private void saveDtabRelationStore(Long datatable_id) {
		Dtab_relation_store dtab_relation_store = new Dtab_relation_store();
		List<Data_store_layer> data_store_layers = Dbo.queryList(Data_store_layer.class, "select * from " + Data_store_layer.TableName);
		if (data_store_layers.isEmpty()) {
			throw new BusinessException("请先新建任意一个存储层");
		} else {
			Data_store_layer data_store_layer = data_store_layers.get(0);
			Long dsl_id = data_store_layer.getDsl_id();
			dtab_relation_store.setDsl_id(dsl_id);
			dtab_relation_store.setTab_id(datatable_id);
			dtab_relation_store.setData_source(StoreLayerDataSource.DM.getCode());
			dtab_relation_store.setIs_successful(JobExecuteState.DengDai.getCode());
			dtab_relation_store.add(Dbo.db());
		}

	}

	/**
	 * 封装一个保存sql表的方法
	 *
	 * @param datatable_id
	 * @param sql
	 */
	private void saveDmOperationInfo(Long datatable_id, String sql) {
		Dm_operation_info dm_operation_info = new Dm_operation_info();
		dm_operation_info.setDatatable_id(datatable_id);
		Long nextId = PrimayKeyGener.getNextId();
		dm_operation_info.setId(nextId);
		dm_operation_info.setExecute_sql(sql);
		dm_operation_info.setView_sql(sql);
		dm_operation_info.setStart_date(DateUtil.getSysDate());
		dm_operation_info.setEnd_date(Constant.MAXDATE);
		dm_operation_info.add(Dbo.db());
	}

	/**
	 * 封装一个存储前后置sql的方法
	 *
	 * @param flag
	 * @param condition
	 * @param datatable_id
	 */
	private void saveDmRelevantInfo(String flag, String condition, Long datatable_id) {
		List<Dm_relevant_info> dm_relevant_infos = Dbo.queryList(Dm_relevant_info.class, "select * from " + Dm_relevant_info.TableName + " where datatable_id = ?", datatable_id);
		if (dm_relevant_infos.isEmpty()) {
			Dm_relevant_info dm_relevant_info = new Dm_relevant_info();
			Long nextId = PrimayKeyGener.getNextId();
			dm_relevant_info.setRel_id(nextId);
			dm_relevant_info.setDatatable_id(datatable_id);
			if (flag.equals(preFlag)) {
				dm_relevant_info.setPre_work(condition);
				dm_relevant_info.add(Dbo.db());
			} else if (flag.equals(postFlag)) {
				dm_relevant_info.setPost_work(condition);
				dm_relevant_info.add(Dbo.db());
			}
		} else {
			Dm_relevant_info dm_relevant_info = dm_relevant_infos.get(0);
			if (flag.equals(preFlag)) {
				dm_relevant_info.setPre_work(condition);
				dm_relevant_info.update(Dbo.db());
			} else if (flag.equals(postFlag)) {
				dm_relevant_info.setPost_work(condition);
				dm_relevant_info.update(Dbo.db());
			}
		}
	}

	/**
	 * 封装一个保存集市表字段的方法
	 *
	 * @param row
	 * @param datatable_id
	 */
	private void saveDatatableFieldInfo(Row row, Long datatable_id, Long count) {
		Datatable_field_info datatable_field_info = new Datatable_field_info();
		Long nextId = PrimayKeyGener.getNextId();
		datatable_field_info.setDatatable_field_id(nextId);
		datatable_field_info.setDatatable_id(datatable_id);
		String field_cn_name = getCellValue(row, 2, "字段描述", true);
		datatable_field_info.setField_cn_name(field_cn_name);
		String field_en_name = getCellValue(row, 1, "字段英文名", true);
		datatable_field_info.setField_en_name(field_en_name);
		String field_type = getCellValue(row, 3, "字段类型", true);
		String field_Length = getField_length(field_type);
		field_type = getField_type(field_type);
		datatable_field_info.setField_type(field_type);
		datatable_field_info.setField_process(ProcessType.YingShe.getCode());
		datatable_field_info.setProcess_mapping(field_en_name);
		datatable_field_info.setField_length(field_Length);
		datatable_field_info.setField_seq(count);
		datatable_field_info.setStart_date(DateUtil.getSysDate());
		datatable_field_info.setEnd_date(Constant.MAXDATE);
		datatable_field_info.add(Dbo.db());
	}

	/**
	 * 封装一个获取字段长度的方法
	 *
	 * @param field_type
	 * @return
	 */
	private String getField_length(String field_type) {
		String fieldLength = "";
		if (field_type.contains("(") && field_type.contains(")") && field_type.indexOf("(") + 1 < field_type.indexOf(")")) {
			fieldLength = field_type.substring(field_type.indexOf("(") + 1, field_type.indexOf(")"));
		}
		return fieldLength;
	}

	/**
	 * 封装一个获取字段类型的方法
	 *
	 * @param field_type
	 * @return
	 */
	private String getField_type(String field_type) {
		if (field_type.contains("(")) {
			field_type = field_type.substring(0, field_type.indexOf("("));
		}
		return field_type;
	}

	/**
	 * 封装一个保存集市表的方法
	 *
	 * @param tablename
	 * @param storageType
	 * @param data_mart_id
	 */
	private Long saveDmDatatable(String tablename, String storageType, Long data_mart_id, Long user_id, Long maindatatable_id) {
		Dm_datatable dm_datatable = new Dm_datatable();
		Long nextId = PrimayKeyGener.getNextId();
		dm_datatable.setDatatable_id(nextId);
		dm_datatable.setData_mart_id(data_mart_id);
		dm_datatable.setDatatable_en_name(tablename);
		dm_datatable.setDatatable_cn_name(tablename);
		dm_datatable.setDatatable_create_date(DateUtil.getSysDate());
		dm_datatable.setDatatable_create_time(DateUtil.getSysTime());
		dm_datatable.setDatatable_due_date(Constant.MAXDATE);
		dm_datatable.setDdlc_date(DateUtil.getSysDate());
		dm_datatable.setDdlc_time(DateUtil.getSysTime());
		dm_datatable.setDatac_date(DateUtil.getSysDate());
		dm_datatable.setDatac_time(DateUtil.getSysTime());
		dm_datatable.setDatatable_lifecycle(TableLifeCycle.YongJiu.getCode());
		dm_datatable.setSoruce_size("0");
		dm_datatable.setEtl_date(DateUtil.getSysDate());
		dm_datatable.setSql_engine(SqlEngine.JDBC.getCode());
		dm_datatable.setStorage_type(storageType);
		dm_datatable.setTable_storage(TableStorage.ShuJuBiao.getCode());
		dm_datatable.setRepeat_flag(IsFlag.Fou.getCode());
		Long classificationId = getClassificationId(data_mart_id, user_id);
		dm_datatable.setCategory_id(classificationId);
		dm_datatable.setRemark(String.valueOf(maindatatable_id));
		dm_datatable.add(Dbo.db());
		return nextId;
	}

	private String getStorageType(String loadPolicy) {
		String storageType = StorageType.TiHuan.getCode();
		if (loadPolicy.startsWith("I") || loadPolicy.contains("增量")) {
			storageType = StorageType.ZhuiJia.getCode();
		} else if (loadPolicy.startsWith("F1") || loadPolicy.contains("全删全插")) {
			storageType = StorageType.TiHuan.getCode();
		}
		return storageType;
	}

	private String getCellValue(Row row, int cellnum, String cellDesc, boolean ifcheck) {
		if (row == null) {
			if (ifcheck) {
				throw new BusinessException(cellDesc + "没有值，请检查");
			} else {
				return null;
			}
		}
		Cell cell = row.getCell(cellnum);
		if (cell == null) {
			if (ifcheck) {
				throw new BusinessException(cellDesc + "没有值，请检查");
			} else {
				return null;
			}
		} else {
			String stringCellValue = cell.getStringCellValue();
			if (StringUtils.isEmpty(stringCellValue)) {
				if (ifcheck) {
					throw new BusinessException(cellDesc + "没有值，请检查");
				} else {
					return null;
				}
			} else {
				return stringCellValue;
			}
		}

	}

	/**
	 * 封装一个获取分类ID的方法
	 *
	 * @param data_mart_id
	 * @return
	 */
	private Long getClassificationId(Long data_mart_id, Long user_id) {
		List<Dm_category> dm_categories = Dbo.queryList(Dm_category.class, "select * from " + Dm_category.TableName + " where category_name = ? and data_mart_id = ?"
				, classification, data_mart_id);
		if (dm_categories.isEmpty()) {
			Dm_category dm_category = new Dm_category();
			Long nextId = PrimayKeyGener.getNextId();
			dm_category.setCategory_id(nextId);
			dm_category.setData_mart_id(data_mart_id);
			dm_category.setCategory_name(classification);
			dm_category.setCreate_date(DateUtil.getSysDate());
			dm_category.setCreate_time(DateUtil.getSysTime());
			dm_category.setCategory_num(classification);
			dm_category.setCreate_id(user_id);
			dm_category.setParent_category_id(data_mart_id);
			dm_category.add(Dbo.db());
			return nextId;
		} else {
			Long category_id = dm_categories.get(0).getCategory_id();
			return category_id;
		}

	}
}
