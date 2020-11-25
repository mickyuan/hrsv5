package hrds.h.biz.market;

import com.sun.org.apache.bcel.internal.generic.RETURN;
import fd.ng.core.utils.DateUtil;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.*;
import hrds.commons.entity.Datatable_field_info;
import hrds.commons.entity.Dm_category;
import hrds.commons.entity.Dm_datatable;
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

public class MappingExcelImExport extends BaseAction {
	private static final Logger logger = LogManager.getLogger();

	private final static String classification = "ExcelImport";

	/**
	 * 导入excel
	 *
	 * @param workBook
	 * @param data_mart_id
	 */
	public void importExcel(Workbook workBook, String data_mart_id) {
		Sheet sheet1 = workBook.getSheet("映射模式");
		//判断是否存在sheet
		if (sheet1 == null) {
			throw new BusinessException("导入的excel不存在映射模式sheet页，请检查");
		}
		String sql = "select ";
		for (int i = 0; i < sheet1.getLastRowNum(); i++) {
			Row row = sheet1.getRow(i);
			Cell cell = row.getCell(0);
			if (cell == null) {
				continue;
			} else {
				String stringCellValue = cell.getStringCellValue();
				if (StringUtils.isEmpty(stringCellValue)) {
					continue;
				} else if (stringCellValue.equals("输入项")) {
					i = i + 2;
					//表名
					String tablename = getCellValue(sheet1.getRow(i), 1, "表名", true);
					i = i + 2;
					String loadPolicy = getCellValue(sheet1.getRow(i), 1, "加载策略", true);
					//加载策略(入库方式)
					String storageType = getStorageType(loadPolicy);
					i = i + 3;
					Long datatable_id = saveDmDatatable(tablename, storageType, data_mart_id);
					Long count = 0L;
					while (getCellValue(sheet1.getRow(i), 1, "", false) != null) {
						row = sheet1.getRow(i);
						saveDatatableFieldInfo(row, datatable_id,count);
						sql+=getCellValue(row,9,"映射规则",true)+" as "+getCellValue(row,1,"字段名",true)+",";
						count++;
						i++;
					}
					//去除,
					sql = sql.substring(0,sql.length()-1);
					sql += " from ";
				}else if(stringCellValue.equals("表关联")){

				}

			}
		}
	}

	/**
	 * 封装一个保存集市表字段的方法
	 *
	 * @param row
	 * @param datatable_id
	 */
	private void saveDatatableFieldInfo(Row row, Long datatable_id,Long count) {
		Datatable_field_info datatable_field_info = new Datatable_field_info();
		Long nextId = PrimayKeyGener.getNextId();
		datatable_field_info.setDatatable_field_id(nextId);
		datatable_field_info.setDatatable_id(datatable_id);
		String field_cn_name = getCellValue(row, 2, "字段描述", true);
		datatable_field_info.setField_cn_name(field_cn_name);
		String field_en_name = getCellValue(row, 1, "字段英文名", true);
		datatable_field_info.setField_en_name(field_en_name);
		String field_type = getCellValue(row, 3, "字段类型", true);
		field_type = getField_type(field_type);
		datatable_field_info.setField_type(field_type);
		datatable_field_info.setField_process(ProcessType.YingShe.getCode());
		datatable_field_info.setProcess_mapping(field_en_name);
		String field_Length = getField_length(field_type);
		datatable_field_info.setField_length(field_Length);
		datatable_field_info.setField_seq(count);
		datatable_field_info.setStart_date(DateUtil.getSysDate());
		datatable_field_info.setEnd_date(Constant.MAXDATE);
		datatable_field_info.add(Dbo.db());
	}

	/**
	 * 封装一个获取字段长度的方法
	 * @param field_type
	 * @return
	 */
	private String getField_length(String field_type) {
		String fieldLength = "";
		if (field_type.contains("(") && field_type.contains(")") && field_type.indexOf("(") + 1 < field_type.indexOf(")")) {
			fieldLength = field_type.substring(field_type.indexOf("（") + 1, field_type.indexOf(")"));
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
			field_type = field_type.substring(0, field_type.indexOf("（"));
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
	private Long saveDmDatatable(String tablename, String storageType, String data_mart_id) {
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
		Long classificationId = getClassificationId(data_mart_id);
		dm_datatable.setCategory_id(classificationId);
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
	private Long getClassificationId(String data_mart_id) {
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
			dm_category.setCreate_id(getUserId());
			dm_category.setParent_category_id(nextId);
			dm_category.add(Dbo.db());
			return nextId;
		} else {
			Long category_id = dm_categories.get(0).getCategory_id();
			return category_id;
		}

	}
}
