package hrds.commons.utils;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import hrds.commons.exception.BusinessException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@DocClass(desc = "Excel工具类(兼容xls和xlsx)", author = "BY-HLL", createdate = "2020/2/22 0022 上午 10:47")
public class ExcelUtil {

	private static final String XLS = "xls";
	private static final String XLSX = "xlsx";
	private static final DateFormat FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	@Method(desc = "从Excel文件路径获取Workbook对象", logicStep = "从Excel文件路径获取Workbook对象")
	@Param(name = "pathName", desc = "文件路径", range = "String")
	@Return(desc = "Workbook对象", range = "Workbook对象")
	public static Workbook getWorkbookFromPathName(String pathName) throws IOException {
		File excelFile = new File(pathName);
		return getWorkbookFromExcel(excelFile);
	}

	@Method(desc = "从Excel文件获取Workbook对象", logicStep = "从Excel文件获取Workbook对象")
	@Param(name = "excelFile", desc = "Excel文件", range = "File")
	@Return(desc = "Workbook对象", range = "Workbook对象")
	public static Workbook getWorkbookFromExcel(File excelFile) throws IOException {
		try (InputStream inputStream = new FileInputStream(excelFile)) {
			return getWorkbookFromInputStream(inputStream, excelFile);
		}
	}

	@Method(desc = "从Excel文件流获取Workbook对象", logicStep = "从Excel文件获取Workbook对象")
	@Param(name = "inputStream", desc = "InputStream输入流", range = "InputStream")
	@Param(name = "excelFile", desc = "Excel文件", range = "File")
	@Return(desc = "Workbook对象", range = "Workbook对象")
	public static Workbook getWorkbookFromInputStream(InputStream inputStream, File excelFile) throws IOException {
		if (excelFile.getName().endsWith(XLS)) {
			return new HSSFWorkbook(inputStream);
		} else if (excelFile.getName().endsWith(XLSX)) {
			return new XSSFWorkbook(inputStream);
		} else {
			throw new BusinessException("文件类型错误!");
		}
	}

	@Method(desc = "输出数据到自定义模版的Excel输出流", logicStep = "输出数据到自定义模版的Excel输出流")
	@Param(name = "excelTemplateFile", desc = "自定义模版文件", range = "File")
	@Param(name = "data", desc = "数据", range = "List<List<Object>>")
	@Param(name = "outputStream", desc = "Excel输出流", range = "OutputStream")
	public static void writeDataToTemplateOutputStream(File excelTemplateFile, List<List<Object>> data,
	                                                   OutputStream outputStream) throws IOException {
		Workbook book = ExcelUtil.getWorkbookFromExcel(excelTemplateFile);
		ExcelUtil.writeDataToWorkbook(null, data, book, 0);
		ExcelUtil.writeWorkbookToOutputStream(book, outputStream);
	}

	@Method(desc = "把Workbook对象内容输出到Excel文件", logicStep = "把Workbook对象内容输出到Excel文件")
	@Param(name = "book", desc = "Workbook对象", range = "File")
	@Param(name = "file", desc = "Excel文件", range = "File")
	@Return(desc = "Workbook对象", range = "Workbook对象")
	public static void writeWorkbookToFile(Workbook book, File file) {
		if (!file.exists()) {
			boolean b = false;
			if (!file.getParentFile().exists()) {
				b = file.getParentFile().mkdirs();
			}
			if (b) {
				try {
					boolean b1;
					b1 = file.createNewFile();
					if (!b1) {
						throw new BusinessException("Excel文件创建失败!");
					}
				} catch (IOException e) {
					throw new BusinessException("Excel文件创建异常!");
				}
			}
		}
		try (OutputStream outputStream = new FileOutputStream(file)) {
			writeWorkbookToOutputStream(book, outputStream);
		} catch (IOException ignored) {
			throw new BusinessException("Excel文件写入失败!");
		}
	}

	@Method(desc = "把Workbook对象输出到Excel输出流", logicStep = "把Workbook对象输出到Excel输出流")
	@Param(name = "book", desc = "Workbook对象", range = "Workbook")
	@Param(name = "outputStream", desc = "Excel输出流", range = "OutputStream")
	public static void writeWorkbookToOutputStream(Workbook book, OutputStream outputStream) {
		try {
			book.write(outputStream);
		} catch (IOException e) {
			throw new BusinessException("Workbook对象输出到Excel输出流失败!");
		}
	}

	@Method(desc = "输出数据到Workbook对象中指定页码", logicStep = "输出数据到Workbook对象中指定页码")
	@Param(name = "title", desc = "标题，写在第一行，可传null", range = "List<String>")
	@Param(name = "data", desc = "数据", range = "List<List<Object>>")
	@Param(name = "book", desc = "Workbook对象", range = "Workbook")
	@Param(name = "sheetIndex", desc = "输出数据到Workbook指定页码的页面数", range = "int")
	public static void writeDataToWorkbook(List<String> title, List<List<Object>> data, Workbook book, int sheetIndex) {
		Sheet sheet = book.getSheetAt(sheetIndex);
		Row row;
		Cell cell;
		// 设置表头
		if (null != title && !title.isEmpty()) {
			row = sheet.getRow(0);
			if (null == row) {
				row = sheet.createRow(0);
			}
			for (int i = 0; i < title.size(); i++) {
				cell = row.getCell(i);
				if (null == cell) {
					cell = row.createCell(i);
				}
				cell.setCellValue(title.get(i));
			}
		}
		List<Object> rowData;
		for (int i = 0; i < data.size(); i++) {

			row = sheet.getRow(i + 1);
			if (null == row) {
				row = sheet.createRow(i + 1);
			}

			rowData = data.get(i);
			if (null == rowData) {
				continue;
			}
			for (int j = 0; j < rowData.size(); j++) {
				cell = row.getCell(j);
				if (null == cell) {
					cell = row.createCell(j);
				}
				setValue(cell, rowData.get(j));
			}
		}
	}

	@Method(desc = "读取Excel文件第一页", logicStep = "读取Excel文件第一页")
	@Param(name = "pathName", desc = "文件路径名", range = "String")
	@Return(desc = "第一页数据集合", range = "第一页数据集合")
	public static List<List<Object>> readExcelFirstSheet(String pathName) throws IOException {
		File file = new File(pathName);
		return readExcelFirstSheet(file);
	}

	@Method(desc = "读取Excel文件第一页", logicStep = "读取Excel文件第一页")
	@Param(name = "file", desc = "Excel文件", range = "File")
	@Return(desc = "第一页数据集合", range = "第一页数据集合")
	public static List<List<Object>> readExcelFirstSheet(File file) throws IOException {
		InputStream inputStream = new FileInputStream(file);
		if (file.getName().endsWith(XLS)) {
			return readXlsFirstSheet(inputStream);
		} else if (file.getName().endsWith(XLSX)) {
			return readXlsxFirstSheet(inputStream);
		} else {
			throw new IOException("文件类型错误");
		}
	}

	@Method(desc = "读取xls格式Excel文件第一页", logicStep = "读取xls格式Excel文件第一页")
	@Param(name = "inputStream", desc = "Excel文件输入流", range = "InputStream")
	@Return(desc = "第一页数据集合", range = "第一页数据集合")
	private static List<List<Object>> readXlsFirstSheet(InputStream inputStream) throws IOException {
		Workbook workbook = new HSSFWorkbook(inputStream);
		return readExcelFirstSheet(workbook);
	}

	@Method(desc = "读取xlsx格式Excel文件第一页", logicStep = "读取xls格式Excel文件第一页")
	@Param(name = "inputStream", desc = "Excel文件输入流", range = "InputStream")
	@Return(desc = "第一页数据集合", range = "第一页数据集合")
	private static List<List<Object>> readXlsxFirstSheet(InputStream inputStream) throws IOException {
		Workbook workbook = new XSSFWorkbook(inputStream);
		return readExcelFirstSheet(workbook);
	}

	@Method(desc = "读取Workbook第一页", logicStep = "读取Workbook第一页")
	@Param(name = "workbook", desc = "Workbook对象", range = "Workbook")
	@Return(desc = "第一页数据集合", range = "第一页数据集合")
	public static List<List<Object>> readExcelFirstSheet(Workbook workbook) {
		return readExcel(workbook, 0);
	}

	@Method(desc = "读取Excel文件第一页", logicStep = "读取Excel文件第一页")
	@Param(name = "pathName", desc = "文件路径名", range = "String")
	@Return(desc = "第一页数据集合", range = "第一页数据集合")
	public static List<List<Object>> readExcel(String pathName) throws IOException {
		File file = new File(pathName);
		return readExcel(file);
	}

	@Method(desc = "读取Excel文件指定页数据", logicStep = "读取Excel文件指定页数据")
	@Param(name = "pathName", desc = "文件路径名", range = "String")
	@Param(name = "sheetIndex", desc = "页码", range = "int")
	@Return(desc = "指定页数据集合", range = "指定页数据集合")
	public static List<List<Object>> readExcel(String pathName, int sheetIndex) throws IOException {
		File file = new File(pathName);
		return readExcel(file, sheetIndex);
	}

	@Method(desc = "读取Excel的第一页", logicStep = "读取Excel的第一页")
	@Param(name = "excelFile", desc = "Excel文件", range = "File")
	@Return(desc = "第一页数据集合", range = "第一页数据集合")
	public static List<List<Object>> readExcel(File excelFile) throws IOException {
		Workbook workbook = getWorkbookFromExcel(excelFile);
		return readExcel(workbook, 0);
	}

	@Method(desc = "读取指定页面的Excel", logicStep = "读取指定页面的Excel")
	@Param(name = "excelFile", desc = "Excel文件", range = "File")
	@Param(name = "sheetIndex", desc = "页码", range = "int")
	@Return(desc = "指定页面数据集合", range = "指定页面数据集合")
	public static List<List<Object>> readExcel(File excelFile, int sheetIndex) throws IOException {
		Workbook workbook = getWorkbookFromExcel(excelFile);
		return readExcel(workbook, sheetIndex);
	}

	@Method(desc = "读取Excel的第一页数据", logicStep = "读取Excel的第一页数据")
	@Param(name = "workbook", desc = "Workbook对象", range = "Workbook")
	@Return(desc = "指定页面数据集合", range = "指定页面数据集合")
	public static List<List<Object>> readExcel(Workbook workbook) {
		return readExcel(workbook, 0);
	}

	@Method(desc = "读取指定页面的Excel", logicStep = "读取指定页面的Excel")
	@Param(name = "workbook", desc = "Workbook对象", range = "Workbook")
	@Param(name = "sheetIndex", desc = "页码", range = "int")
	@Return(desc = "指定页面数据集合", range = "指定页面数据集合")
	public static List<List<Object>> readExcel(Workbook workbook, int sheetIndex) {
		List<List<Object>> list = new ArrayList<>();
		Sheet sheet = workbook.getSheetAt(sheetIndex);
		for (int i = 0; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			// 如果当前行为空，则加入空，保持行号一致
			if (null == row) {
				list.add(null);
				continue;
			}
			List<Object> columns = new ArrayList<>();
			for (int j = 0; j < row.getLastCellNum(); j++) {
				Cell cell = row.getCell(j);
				try {
					columns.add(getValue(cell));
				} catch (IllegalStateException e) {
					//下表从0开始,行和列提示需要+1
					throw new BusinessException("获取excel单元格数据失败!" +
							" sheet:" + sheet.getSheetName() + "行:" + ++i + "列:" + ++j);
				}
			}
			list.add(columns);
		}
		return list;
	}

	@Method(desc = "读取指定页面的Excel", logicStep = "读取指定页面的Excel")
	@Param(name = "workbook", desc = "Workbook对象", range = "Workbook")
	@Param(name = "sheetName", desc = "页名称", range = "int")
	@Return(desc = "指定页面数据集合", range = "指定页面数据集合")
	public static List<List<Object>> readExcel(Workbook workbook, String sheetName) {
		return readExcel(workbook, workbook.getSheetIndex(sheetName));

	}

	@Method(desc = "解析单元格中的值", logicStep = "解析单元格中的值")
	@Param(name = "cell", desc = "单元格", range = "Cell")
	@Return(desc = "单元格内的值", range = "单元格内的值")
	private static Object getValue(Cell cell) throws IllegalStateException {
		//如果当前单元格为空，则加入""，保持列号一致
		if (null == cell) {
			return "";
		}
		Object value;
		switch (cell.getCellType()) {
			case Cell.CELL_TYPE_NUMERIC:
				// 日期类型，转换为日期
				if (DateUtil.isCellDateFormatted(cell)) {
					value = cell.getDateCellValue();
				} else {
					// 默认返回double，创建BigDecimal返回准确值
					double doubleVal = Double.parseDouble(String.valueOf(cell.getNumericCellValue()));
					long longVal = Math.round(cell.getNumericCellValue());
					if (Double.parseDouble(longVal + ".0") == doubleVal) {
						value = longVal;
					} else {
						value = doubleVal;
					}
				}
				break;
			case Cell.CELL_TYPE_STRING:
				value = cell.getRichStringCellValue().getString();
				break;
			case Cell.CELL_TYPE_FORMULA:
				value = String.valueOf(cell.getRichStringCellValue());
				break;
			case Cell.CELL_TYPE_BLANK:
				value = "";
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				value = cell.getBooleanCellValue();
				break;
			case Cell.CELL_TYPE_ERROR:
				value = "error";
				break;
			default:
				value = cell.toString();
				break;
		}
		return value;
	}

	@Method(desc = "设置单元格值", logicStep = "设置单元格值")
	@Param(name = "cell", desc = "单元格", range = "Cell")
	@Param(name = "value", desc = "值", range = "Object")
	@Return(desc = "单元格内的值", range = "单元格内的值")
	private static void setValue(Cell cell, Object value) {
		if (null == cell) {
			return;
		}
		if (null == value) {
			cell.setCellValue((String) null);
		} else if (value instanceof Boolean) {
			cell.setCellValue((Boolean) value);
		} else if (value instanceof Date) {
			cell.setCellValue(FORMAT.format((Date) value));
		} else if (value instanceof Double) {
			cell.setCellValue((Double) value);
		} else {
			cell.setCellValue(value.toString());
		}
	}

	@Method(desc = "关闭workbook", logicStep = "")
	@Param(name = "workbook", desc = "excel工作簿对象", range = "")
	public static void close(Workbook workbook) {
		try {
			if (workbook != null) {
				workbook.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
