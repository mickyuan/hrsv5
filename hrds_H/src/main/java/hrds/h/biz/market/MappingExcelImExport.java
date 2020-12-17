package hrds.h.biz.market;

import fd.ng.core.exception.BusinessSystemException;
import fd.ng.core.utils.DateUtil;
import fd.ng.web.util.Dbo;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.Constant;
import hrds.commons.utils.key.PrimayKeyGener;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;
import java.util.List;
import java.util.*;

public class MappingExcelImExport {
	private static final Logger logger = LogManager.getLogger();

	private final static String classification = "ExcelImport";
	private final static String preFlag = "PRE";
	private final static String postFlag = "POST";
	private final static String Separator = "@";
	private final static String destColumn = "destColumn";
	private final static String describe = "describe";
	private final static String columnType = "columnType";
	private final static String database = "database";
	private final static String sourceTableName = "sourceTableName";
	private final static String sourceColumn = "sourceColumn";
	private final static String mappingColumn = "mappingColumn";
	private final static String notes = "notes";
	private final static String alias = "alias";
	private final static String joinType = "joinType";
	private final static String joinCondition = "joinCondition";

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
		Long own_dource_table_id = 0L;
		//判断是否存在sheet
		if (sheet == null) {
			throw new BusinessException("导入的excel不存在映射模式sheet页，请检查");
		}
		String sql = new String();
		for (int i = 0; i <= sheet.getLastRowNum() + 1; i++) {
			Row row = sheet.getRow(i);
			if (row == null) {
				continue;
			}
			Cell cell = row.getCell(0);
			if (cell == null) {
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
					String destcolumn = getCellValue(row, 1, "字段名", true);
					String databasesource = getCellValue(row, 4, "模式变量", true);
					String sourcetablename = getCellValue(row, 5, "数据源表名", true);
					String sourcecolumn = getCellValue(row, 6, "数据源字段名", true);
					String mappingcolumn = getCellValue(row, 9, "映射规则", true);
					own_dource_table_id = saveDmDatatableSource(datatable_id, databasesource + Separator + sourcetablename);
					saveDmEtlmapInfo(datatable_id, own_dource_table_id, destcolumn, sourcecolumn, mappingcolumn);
					if (!mappingcolumn.trim().contains(" ") && !mappingcolumn.trim().contains("(")
							&& !mappingcolumn.trim().contains(".") && !mappingcolumn.trim().contains(")")
							&& !sourcetablename.equals("/") && !sourcetablename.isEmpty()
							&& !mappingcolumn.contains("'") && !mappingcolumn.contains("\"")) {
						sql += sourcetablename + "." + mappingcolumn + " as " + destcolumn + ",";
					} else {
						sql += mappingcolumn + " as " + destcolumn + ",";
					}
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
							? getCellValue(row, 1, "模式变量", false).replace(" ", "") : "";
					String tablename = getCellValue(row, 2, "源表名", true);
					String alias = getCellValue(row, 3, "别名", true);
					String jointype = getCellValue(row, 4, "关联类型", false) != null
							? getCellValue(row, 4, "关联类型", false) : "";
					String joincondition = getCellValue(row, 5, "关联条件", false) != null
							? getCellValue(row, 5, "关联条件", false) : "";
					if (databasename.isEmpty()) {
						sql += jointype + Constant.SPACE + tablename + Constant.SPACE + alias + Constant.SPACE + joincondition + Constant.SPACE;
					} else {
						sql += jointype + Constant.SPACE + databasename + "." + tablename + Constant.SPACE + alias + Constant.SPACE + joincondition + Constant.SPACE;
					}
					updateDmDatatableSource(datatable_id, databasename + Separator + tablename, alias, jointype, joincondition);
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
		}
		saveDmOperationInfo(datatable_id, sql);
		return datatable_id;
	}

	private void updateDmDatatableSource(Long datatable_id, String own_source_table_name, String alias, String jointype, String joincondition) {
		String remark = alias + Separator + jointype + Separator + joincondition;
		List<Dm_datatable_source> dm_datatable_sources = Dbo.queryList(Dm_datatable_source.class, "select * from " + Dm_datatable_source.TableName + " where datatable_id = ? and own_source_table_name = ?", datatable_id, own_source_table_name);
		if (dm_datatable_sources.isEmpty()) {
			Dm_datatable_source dm_datatable_source = new Dm_datatable_source();
			Long nextId = PrimayKeyGener.getNextId();
			dm_datatable_source.setOwn_dource_table_id(nextId);
			dm_datatable_source.setDatatable_id(datatable_id);
			dm_datatable_source.setOwn_source_table_name(own_source_table_name);
			dm_datatable_source.setRemark(remark);
			dm_datatable_source.setSource_type(DataSourceType.UDL.getCode());
			dm_datatable_source.add(Dbo.db());
		} else if (dm_datatable_sources.size() == 1) {
			Dm_datatable_source dm_datatable_source = dm_datatable_sources.get(0);
			dm_datatable_source.setRemark(remark);
			dm_datatable_source.update(Dbo.db());
		} else {
			throw new BusinessSystemException("保存表" + Dm_datatable_source.TableName + "错误");
		}

	}

	/**
	 * 封装一个保存DmEtlmapInfo的方法
	 *
	 * @param datatable_id
	 * @param own_dource_table_id
	 * @param destcolumn
	 * @param sourcecolumn
	 * @param mappingcolumn
	 */
	private void saveDmEtlmapInfo(Long datatable_id, Long own_dource_table_id, String destcolumn, String sourcecolumn, String mappingcolumn) {
		Dm_etlmap_info dm_etlmap_info = new Dm_etlmap_info();
		Long nextId = PrimayKeyGener.getNextId();
		dm_etlmap_info.setEtl_id(nextId);
		dm_etlmap_info.setDatatable_id(datatable_id);
		dm_etlmap_info.setOwn_dource_table_id(own_dource_table_id);
		dm_etlmap_info.setSourcefields_name(sourcecolumn);
		dm_etlmap_info.setTargetfield_name(destcolumn);
		dm_etlmap_info.setRemark(mappingcolumn);
		dm_etlmap_info.add(Dbo.db());
	}

	/**
	 * 封装一个保存DmDatatableSource的方法
	 *
	 * @param datatable_id
	 * @param sourcetablename
	 * @return
	 */
	private Long saveDmDatatableSource(Long datatable_id, String sourcetablename) {
		Optional<Dm_datatable_source> dm_datatable_source1 = Dbo.queryOneObject(Dm_datatable_source.class, "select * from " + Dm_datatable_source.TableName + " where datatable_id = ? and own_source_table_name = ?", datatable_id, sourcetablename);
		if (dm_datatable_source1.isPresent()) {
			Dm_datatable_source dm_datatable_source = dm_datatable_source1.get();
			return dm_datatable_source.getOwn_dource_table_id();
		}
		Dm_datatable_source dm_datatable_source = new Dm_datatable_source();
		Long nextId = PrimayKeyGener.getNextId();
		dm_datatable_source.setOwn_dource_table_id(nextId);
		dm_datatable_source.setDatatable_id(datatable_id);
		dm_datatable_source.setOwn_source_table_name(sourcetablename);
		dm_datatable_source.setSource_type(DataSourceType.UDL.getCode());
		dm_datatable_source.add(Dbo.db());
		return nextId;
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
//		condition = condition.replace("\n"," ").replace("\r\n"," ").replace("\r"," ");
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
		String field_desc = getCellValue(row, 10, "注释", false) == null
				? "" : getCellValue(row, 10, "注释", false);
		datatable_field_info.setField_desc(field_desc);
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

	/**
	 * 封装一个将存储类型转换的方法
	 *
	 * @param loadPolicy
	 * @return
	 */
	private String getStorageType(String loadPolicy) {
		String storageType = StorageType.TiHuan.getCode();
		if (loadPolicy.startsWith("I") || loadPolicy.contains("增量")) {
			storageType = StorageType.ZhuiJia.getCode();
		} else if (loadPolicy.startsWith("F1") || loadPolicy.contains("全删全插")) {
			storageType = StorageType.TiHuan.getCode();
		}
		return storageType;
	}

	/**
	 * 封装一个获取单元格内容的方法
	 *
	 * @param row
	 * @param cellnum
	 * @param cellDesc
	 * @param ifcheck
	 * @return
	 */
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

	/**
	 * 生成导出excel的内容
	 *
	 * @param workbook
	 * @param datatable_id
	 */
	public int generateMappingExcel(XSSFWorkbook workbook, Long datatable_id, String sheetname, int i) {
		String datatable_en_name = "";
		String storageType = "F1:全删全插";
		Dm_datatable dm_datatable = new Dm_datatable();
		dm_datatable.setDatatable_id(datatable_id);
		Optional<Dm_datatable> dm_datatable2 = Dbo.queryOneObject(Dm_datatable.class, "select * from " + Dm_datatable.TableName + " where datatable_id = ?", dm_datatable.getDatatable_id());
		if (dm_datatable2.isPresent()) {
			Dm_datatable dm_datatable1 = dm_datatable2.get();
			datatable_en_name = dm_datatable1.getDatatable_en_name();
			String storage_type = dm_datatable1.getStorage_type();
			if (storage_type.equals(StorageType.ZhuiJia.getCode())) {
				storageType = "I:增量";
			}
		} else {
			throw new BusinessException("查询表" + Dm_datatable.TableName + "错误");
		}
		XSSFSheet sheet1;
		sheet1 = workbook.getSheet(sheetname);
		if (sheet1 == null) {
			sheet1 = workbook.createSheet(sheetname);
		}
		XSSFColor xssfColor = new XSSFColor(Color.yellow);
		if (i == 0) {
			sheet1.createRow(i).createCell(0).setCellValue(sheetname);
			sheet1.getRow(i).getCell(0).getCellStyle().setFillForegroundColor(xssfColor);
		}
		i = i + 2;
		//设置颜色
		sheet1.createRow(i).createCell(0).setCellValue("输入项");
		//设置颜色
		sheet1.getRow(i).getCell(0).getCellStyle().setFillForegroundColor(xssfColor);
		//合并单元格
		CellRangeAddress region = new CellRangeAddress(i, i, 0, 6);
		sheet1.addMergedRegion(region);
		i++;
		for (int j = 0; j < 7; j++) {
			sheet1.createRow(i).createCell(j).getCellStyle().setFillForegroundColor(xssfColor);
		}
		sheet1.createRow(i).createCell(0).setCellValue("字段名称");
		sheet1.getRow(i).createCell(1).setCellValue("字段值");
		i++;
		sheet1.createRow(i).createCell(0).setCellValue("目标表名");
		sheet1.getRow(i).createCell(1).setCellValue(datatable_en_name);
		i++;
		sheet1.createRow(i).createCell(0).setCellValue("是否大表");
		sheet1.getRow(i).createCell(1).setCellValue("否");
		i++;
		sheet1.createRow(i).createCell(0).setCellValue("加载策略");
		sheet1.getRow(i).createCell(1).setCellValue(storageType);
		i++;
		sheet1.createRow(i).createCell(0).setCellValue("增量条件");
		i++;
		sheet1.createRow(i).createCell(0).setCellValue("数据映射");
		sheet1.getRow(i).createCell(1).setCellValue("字段名");
		sheet1.getRow(i).createCell(2).setCellValue("描述");
		sheet1.getRow(i).createCell(3).setCellValue("字段类型");
		sheet1.getRow(i).createCell(4).setCellValue("模式变量");
		sheet1.getRow(i).createCell(5).setCellValue("源表名");
		sheet1.getRow(i).createCell(6).setCellValue("字段名");
		sheet1.getRow(i).createCell(7).setCellValue("描述");
		sheet1.getRow(i).createCell(8).setCellValue("字段类型");
		sheet1.getRow(i).createCell(9).setCellValue("映射规则");
		sheet1.getRow(i).createCell(10).setCellValue("注释");
		for (int j = 0; j < 11; j++) {
			sheet1.getRow(i).getCell(j).getCellStyle().setFillForegroundColor(xssfColor);
		}
		List<Map<String, String>> maps = getMappingFields(datatable_id);
		i++;
		for (Map<String, String> map : maps) {
			sheet1.createRow(i).createCell(1).setCellValue(map.get(destColumn));
			sheet1.getRow(i).createCell(2).setCellValue(map.get(describe));
			sheet1.getRow(i).createCell(3).setCellValue(map.get(columnType));
			sheet1.getRow(i).createCell(4).setCellValue(map.get(database));
			sheet1.getRow(i).createCell(5).setCellValue(map.get(sourceTableName));
			sheet1.getRow(i).createCell(6).setCellValue(map.get(sourceColumn));
			sheet1.getRow(i).createCell(7).setCellValue(map.get(describe));
			sheet1.getRow(i).createCell(8).setCellValue(map.get(columnType));
			sheet1.getRow(i).createCell(9).setCellValue(map.get(mappingColumn));
			sheet1.getRow(i).createCell(10).setCellValue(map.get(notes));
			i++;
		}
		i++;
		sheet1.createRow(i).createCell(0).setCellValue("表关联");
		sheet1.getRow(i).createCell(1).setCellValue("模式变量");
		sheet1.getRow(i).createCell(2).setCellValue("源表名");
		sheet1.getRow(i).createCell(3).setCellValue("别名");
		sheet1.getRow(i).createCell(4).setCellValue("关联类型");
		sheet1.getRow(i).createCell(5).setCellValue("关联条件");
		List<Map<String, String>> frommaps = getFrom(datatable_id);
		i++;
		for (Map<String, String> map : frommaps) {
			sheet1.createRow(i).createCell(1).setCellValue(map.get(database));
			sheet1.getRow(i).createCell(2).setCellValue(map.get(sourceTableName));
			sheet1.getRow(i).createCell(3).setCellValue(map.get(alias));
			sheet1.getRow(i).createCell(4).setCellValue(map.get(joinType));
			sheet1.getRow(i).createCell(5).setCellValue(map.get(joinCondition));
			i++;
		}
		i++;
		sheet1.createRow(i).createCell(0).setCellValue("其他申明");
		String where = getWhereOrGroup(datatable_id);
		sheet1.getRow(i).createCell(1).setCellValue(where);
		if (sheetname.equals("映射模式")) {
			i = i + 2;
			Dm_relevant_info dm_relevant_info = getDm_relevant_info(datatable_id);
			sheet1.createRow(i).createCell(0).setCellValue("作业前置SQL");
			sheet1.getRow(i).createCell(1).setCellValue(dm_relevant_info.getPre_work());
			i = i + 2;
			sheet1.createRow(i).createCell(0).setCellValue("作业后置SQL");
			sheet1.getRow(i).createCell(1).setCellValue(dm_relevant_info.getPost_work());
		}
		i++;
		return i;
	}

	private List<Map<String, String>> getFrom(Long datatable_id) {
		List<Map<String, String>> resultList = new ArrayList<>();
		Dm_datatable dm_datatable = new Dm_datatable();
		dm_datatable.setDatatable_id(datatable_id);
		List<Dm_datatable_source> dm_datatable_sources = Dbo.queryList(Dm_datatable_source.class, "select * from " + Dm_datatable_source.TableName + " where datatable_id = ?", dm_datatable.getDatatable_id());
		for (Dm_datatable_source dm_datatable_source : dm_datatable_sources) {
			Map<String, String> map = new HashMap<>();
			String own_source_table_name = dm_datatable_source.getOwn_source_table_name();
			String databaseValue = own_source_table_name.split(Separator, -1)[0];
			String sourcetableValue = own_source_table_name.split(Separator, -1)[1];
			String remark = dm_datatable_source.getRemark();
			if (StringUtils.isEmpty(remark)) {
				continue;
			}
			String aliasValue = remark.split(Separator, -1)[0];
			String joinTypeValue = remark.split(Separator, -1).length > 1 ? remark.split(Separator, -1)[1] : "";
			String joinConditionValue = remark.split(Separator, -1).length > 2 ? remark.split(Separator, -1)[2] : "";
			map.put(database, databaseValue);
			map.put(sourceTableName, sourcetableValue);
			map.put(alias, aliasValue);
			map.put(joinType, joinTypeValue);
			map.put(joinCondition, joinConditionValue);
			resultList.add(map);
		}
		return resultList;
	}

	private Dm_relevant_info getDm_relevant_info(Long datatable_id) {
		Dm_datatable dm_datatable = new Dm_datatable();
		dm_datatable.setDatatable_id(datatable_id);
		Optional<Dm_relevant_info> dm_relevant_info = Dbo.queryOneObject(Dm_relevant_info.class, "select * from " + Dm_relevant_info.TableName + " where datatable_id = ?", dm_datatable.getDatatable_id());
		if (dm_relevant_info.isPresent()) {
			Dm_relevant_info dm_relevant_info1 = dm_relevant_info.get();
			return dm_relevant_info1;
		}
		return new Dm_relevant_info();
	}

	private String getWhereOrGroup(Long datatable_id) {
		Dm_datatable dm_datatable = new Dm_datatable();
		dm_datatable.setDatatable_id(datatable_id);
		Optional<Dm_operation_info> dm_operation_info = Dbo.queryOneObject(Dm_operation_info.class, "select * from " + Dm_operation_info.TableName + " where datatable_id = ?", dm_datatable.getDatatable_id());
		if (dm_operation_info.isPresent()) {
			Dm_operation_info dm_operation_info1 = dm_operation_info.get();
			String view_sql = dm_operation_info1.getView_sql().toUpperCase();
			if (view_sql.contains("WHERE")) {
				return view_sql.substring(view_sql.lastIndexOf("WHERE"));
			} else if (view_sql.contains("GROUP BY")) {
				return view_sql.substring(view_sql.lastIndexOf("GROUP BY"));
			} else {
				return "";
			}
		}
		throw new BusinessException("查询SQL错误");
	}

	private List<Map<String, String>> getMappingFields(Long datatable_id) {
		List<Map<String, String>> resultList = new ArrayList<>();
		Dm_datatable dm_datatable = new Dm_datatable();
		dm_datatable.setDatatable_id(datatable_id);
		String sql = "SELECT\n" +
				"    t1.field_en_name ,\n" +
				"    t1.field_cn_name ,\n" +
				"    t1.field_type,\n" +
				"    t1.field_length,\n" +
				"    t3.own_source_table_name,\n" +
				"    t2.sourcefields_name,\n" +
				"    t2.remark,\n" +
				"    t1.field_desc\n" +
				"FROM\n" +
				"    datatable_field_info t1\n" +
				"LEFT JOIN\n" +
				"    dm_etlmap_info t2\n" +
				"ON\n" +
				"    t1.field_en_name = t2.targetfield_name\n" +
				"LEFT JOIN\n" +
				"    dm_datatable_source t3\n" +
				"ON\n" +
				"    t2.own_dource_table_id = t3.own_dource_table_id\n" +
				"    where t1.datatable_id = ?\n" +
				"    and t2.datatable_id = ?\n" +
				"    and t3.datatable_id = ?";
		List<Map<String, Object>> maps = Dbo.queryList(sql, dm_datatable.getDatatable_id(), dm_datatable.getDatatable_id(), dm_datatable.getDatatable_id());
		for (Map<String, Object> map : maps) {
			Map<String, String> resultMap = new HashMap<>();
			String field_en_name = map.get("field_en_name").toString();
			String field_cn_name = map.get("field_cn_name").toString();
			String field_type = map.get("field_type").toString();
			String field_length = map.get("field_length").toString();
			String own_source_table_name = map.get("own_source_table_name").toString();
			String sourcefields_name = map.get("sourcefields_name").toString();
			String remark = map.get("remark").toString();
			String field_desc = map.get("field_desc").toString();
			String columnTypeResult = field_type;
			columnTypeResult += StringUtils.isEmpty(field_length) ? "" : "(" + field_length + ")";
			resultMap.put(destColumn, field_en_name);
			resultMap.put(describe, field_cn_name);
			resultMap.put(columnType, columnTypeResult);
			resultMap.put(database, own_source_table_name.split(Separator, -1)[0]);
			resultMap.put(sourceTableName, own_source_table_name.split(Separator, -1)[1]);
			resultMap.put(sourceColumn, sourcefields_name);
			resultMap.put(mappingColumn, remark);
			resultMap.put(notes, field_desc);
			resultList.add(resultMap);
		}

		return resultList;
	}
}
