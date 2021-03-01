package hrds.a.biz.logreview;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DefaultPageImpl;
import fd.ng.db.jdbc.Page;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.meta.MetaOperator;
import fd.ng.db.meta.TableMeta;
import fd.ng.web.conf.WebinfoConf;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.entity.Login_operation_info;
import hrds.commons.exception.AppSystemException;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.ExcelUtil;
import hrds.commons.utils.FileDownloadUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@DocClass(desc = "日志审查类", author = "dhw", createdate = "2020/4/24 13:46")
public class SystemLogReview extends BaseAction {
	private static final Logger logger = LogManager.getLogger();

	@Method(desc = "查询系统日志信息",
		logicStep = "1.数据可访问权限处理方式，该方法不需要进行权限控制" +
			"2.判断用户ID是否为空，不为空加条件查询" +
			"3.判断请求日期是否为空，不为空加条件查询" +
			"4.分页查询系统日志信息封装总记录数并返回")
	@Param(name = "user_id", desc = "用户ID", range = "新增用户时生成", nullable = true)
	@Param(name = "request_date", desc = "请求日期", range = "新增日志信息时生成", nullable = true)
	@Param(name = "currPage", desc = "分页当前页", range = "大于0的正整数", nullable = true)
	@Param(name = "pageSize", desc = "分页每页显示条数", range = "大于0的正整数", nullable = true)
	@Return(desc = "返回查询系统日志信息结果", range = "无限制")
	private List<Map<String, Object>> searchSystemLogInfo(Long user_id, String request_date, Integer currPage,
	                                                      Integer pageSize) {
		// 1.数据可访问权限处理方式，该方法不需要进行权限控制
		SqlOperator.Assembler assembler = SqlOperator.Assembler.newInstance();
		assembler.clean();
		assembler.addSql("select * from " + Login_operation_info.TableName + " where 1=1 ");
		// 2.判断用户ID是否为空，不为空加条件查询
		if (user_id != null) {
			assembler.addSql(" and user_id=?").addParam(user_id);
		}
		// 3.判断请求日期是否为空，不为空加条件查询
		if (StringUtil.isNotBlank(request_date)) {
			assembler.addSql(" and request_date=?").addParam(request_date);
		}
		assembler.addSql(" order by log_id");
		List<Map<String, Object>> operationInfoList;
		// 判断是否分页查询
		if (currPage == null || pageSize == null) {
			operationInfoList = Dbo.queryList(assembler.sql(), assembler.params());
		} else {
			Page page = new DefaultPageImpl(currPage, pageSize);
			// 4.分页查询系统日志信息封装总记录数并返回
			operationInfoList = Dbo.queryPagedList(page, assembler.sql(), assembler.params());
			if (!operationInfoList.isEmpty()) {
				operationInfoList.get(0).put("totalSize", page.getTotalSize());
			}
		}
		return operationInfoList;
	}

	@Method(desc = "根据用户ID或请求日期分页查询系统日志信息",
		logicStep = "1.数据可访问权限处理方式，该方法不需要进行权限控制" +
			"2.分页查询系统日志信息并返回")
	@Param(name = "user_id", desc = "用户ID", range = "新增用户时生成", nullable = true)
	@Param(name = "request_date", desc = "请求日期", range = "新增日志信息时生成", nullable = true)
	@Param(name = "currPage", desc = "分页当前页", range = "大于0的正整数", valueIfNull = "1")
	@Param(name = "pageSize", desc = "分页每页显示条数", range = "大于0的正整数", valueIfNull = "10")
	@Return(desc = "返回分页查询系统日志信息", range = "无限制")
	public List<Map<String, Object>> searchSystemLogByIdOrDate(Long user_id, String request_date,
	                                                           Integer currPage, Integer pageSize) {
		// 1.数据可访问权限处理方式，该方法不需要进行权限控制
		// 2.分页查询系统日志信息并返回
		return searchSystemLogInfo(user_id, request_date, currPage, pageSize);
	}

	@Method(desc = "分页查询系统日志信息", logicStep = "1.数据可访问权限处理方式，该方法不需要进行权限控制" +
		"2.分页查询系统日志信息并返回")
	@Param(name = "currPage", desc = "分页当前页", range = "大于0的正整数", valueIfNull = "1")
	@Param(name = "pageSize", desc = "分页每页显示条数", range = "大于0的正整数", valueIfNull = "10")
	@Return(desc = "返回分页查询系统日志信息", range = "无限制")
	public List<Map<String, Object>> searchSystemLogByPage(Integer currPage, Integer pageSize) {
		// 1.数据可访问权限处理方式，该方法不需要进行权限控制
		// 2.分页查询系统日志信息并返回
		return searchSystemLogInfo(null, "", currPage, pageSize);
	}

	@Method(desc = "下载系统日志", logicStep = "1.获取对应表数据" +
		"2.获取文件存放目录" +
		"3.判断数据量是否大于1000000,如果大于一百万则写成csv文件否则写成excel文件" +
		"3.1写成csv文件" +
		"3.2写成excel文件" +
		"4.下载文件" +
		"5.下载完删除下载文件")
	@Param(name = "user_id", desc = "用户ID", range = "新增用户时生成", nullable = true)
	@Param(name = "request_date", desc = "请求日期", range = "新增日志信息时生成", nullable = true)
	public void downloadSystemLog(Long user_id, String request_date) {
		// 数据可访问权限处理方式，该方法不需要权限验证
		try {
			// 1.获取对应表数据
			List<Map<String, Object>> systemLogInfoList = searchSystemLogInfo(user_id, request_date,
				null, null);
			// 2.获取文件存放目录
			String savedDirName = WebinfoConf.FileUpload_SavedDirName + File.separator;
			// 3.判断数据量是否大于1000000,如果大于一百万则写成csv文件否则写成excel文件
			File file;
			if (systemLogInfoList.size() > 1000000) {
				// 3.1写成csv文件
				file = new File(savedDirName + "logReview.csv");
				writeFile(systemLogInfoList, file);
			} else {
				file = new File(savedDirName + "logReview." + ExcelUtil.XLSX);
				// 3.2写成excel文件
				generateExcel(user_id, request_date, systemLogInfoList, file);
			}
			// 4.下载文件
			FileDownloadUtil.downloadFile(file.getName());
			// 5.下载完删除下载文件
			Files.delete(new File(savedDirName + "logReview.txt").toPath());
		} catch (IOException e) {
			throw new BusinessException("删除文件失败" + e.getMessage());
		}
	}

	private void writeFile(List<Map<String, Object>> systemLogInfoList, File file) {
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;
		try {
			fos = new FileOutputStream(file);
			osw = new OutputStreamWriter(fos);
			bw = new BufferedWriter(osw);
			List<TableMeta> tableMetas = MetaOperator.getTablesWithColumns(Dbo.db(), Login_operation_info.TableName);
			Set<String> columnNames = tableMetas.get(0).getColumnNames();
			List<String> data = new ArrayList<>();
			data.add(String.join(",", columnNames));
			if (!systemLogInfoList.isEmpty()) {
				List<List<String>> columnValList = getColumnValueList(columnNames, systemLogInfoList);
				for (List<String> list : columnValList) {
					data.add(String.join(",", list));
				}
				for (String datum : data) {
					bw.write(datum);
					bw.newLine();
					bw.flush();
				}
			}
		} catch (IOException e) {
			logger.error(e);
			throw new BusinessException("写文件失败");
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
			} catch (IOException e) {
				logger.error("关闭流失败", e);
			}
			try {
				if (osw != null) {
					osw.close();
				}
			} catch (IOException e) {
				logger.error("关闭输出流失败", e);
			}
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				logger.error("关闭文件输出流失败", e);
			}
		}
	}

	@Method(desc = "生成日志审查excel文件", logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
		"2.验证当前用户下的工程是否存在" +
		"3.创建工作簿对象" +
		"4.创建单元格对象,批注插入到一行" +
		"5.判断文件是否存在" +
		"6.创建输出流" +
		"7.获取Excel的头列信息" +
		"8.遍历列名设置头信息" +
		"9.获取表对应所有列的值信息" +
		"10.循环出需要的数据,并添加到excel头下方" +
		"11.写进Excel表格")
	@Param(name = "user_id", desc = "用户ID", range = "新增用户时生成", nullable = true)
	@Param(name = "request_date", desc = "请求日期", range = "新增日志信息时生成", nullable = true)
	@Param(name = "systemLogInfoList", desc = "系统日志数据", range = "无限制")
	@Param(name = "file", desc = "生成文件目录", range = "无限制")
	private void generateExcel(Long user_id, String request_date,
	                           List<Map<String, Object>> systemLogInfoList, File file) {
		// 1.数据可访问权限处理方式，通过user_id进行权限控制
		FileOutputStream out = null;
		XSSFWorkbook workbook = null;
		try {
			// 2.创建工作簿对象
			workbook = new XSSFWorkbook();
			// 3.创建工作表对象
			XSSFSheet sheet = workbook.createSheet("sheet1");
			// 4.创建单元格对象,批注插入到一行
			XSSFRow headRow = sheet.createRow(0);
			// 5.判断文件是否存在不存在创建
			if (!file.exists()) {
				if (!file.createNewFile()) {
					throw new BusinessException("创建文件失败，文件目录可能不存在！");
				}
			}
			// 6.创建输出流
			out = new FileOutputStream(file);
			// 7.获取Excel的头列信息
			List<TableMeta> tableMetas = MetaOperator.getTablesWithColumns(Dbo.db(), Login_operation_info.TableName);
			Set<String> columnNames = tableMetas.get(0).getColumnNames();
			int cellNum = 0;
			// 8.遍历列名设置头信息
			for (String columnName : columnNames) {
				XSSFCell createCell = headRow.createCell(cellNum);
				createCell.setCellValue(columnName);
				cellNum++;
			}
			// 9.获取表对应所有列的值信息
			List<List<String>> columnValList = getColumnValueList(columnNames, systemLogInfoList);
			// 10.循环出需要的数据,并添加到excel头下方
			if (!columnValList.isEmpty()) {
				for (int i = 0; i < columnValList.size(); i++) {
					headRow = sheet.createRow(i + 1);
					List<String> valueList = columnValList.get(i);
					for (int j = 0; j < valueList.size(); j++) {
						headRow.createCell(j).setCellValue(valueList.get(j));
					}
				}
			}
			// 11.写进Excel表格
			workbook.write(out);
		} catch (FileNotFoundException e) {
			logger.error(e);
			throw new BusinessException("文件不存在！");
		} catch (IOException e) {
			logger.error(e);
			throw new BusinessException("生成excel文件失败！");
		} catch (Exception e) {
			throw new AppSystemException(e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				logger.error("关闭输出流失败", e);
			}
			ExcelUtil.close(workbook);
		}
	}

	private List<List<String>> getColumnValueList(Set<String> columnNames,
	                                              List<Map<String, Object>> systemLogInfoList) {
		List<List<String>> columnValList = new ArrayList<>();
		for (Map<String, Object> systemLogInfo : systemLogInfoList) {
			// 1.存放表每列信息
			List<String> columnInfoList = new ArrayList<>();
			for (String columnName : columnNames) {
				// 2.设置每列信息
				if (systemLogInfo.get(columnName) != null) {
					columnInfoList.add(systemLogInfo.get(columnName).toString());
				} else {
					columnInfoList.add("");
				}
			}
			// 3.封装每列信息
			columnValList.add(columnInfoList);
		}
		// 4.返回表对应所有列的值信息
		return columnValList;
	}
}
