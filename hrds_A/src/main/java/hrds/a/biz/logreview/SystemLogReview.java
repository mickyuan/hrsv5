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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

	@Method(desc = "下载系统日志",
			logicStep = "1.生成日志审查excel文件" +
					"2.下载excel文件" +
					"3.下载完删除下载文件")
	@Param(name = "user_id", desc = "用户ID", range = "新增用户时生成", nullable = true)
	@Param(name = "request_date", desc = "请求日期", range = "新增日志信息时生成", nullable = true)
	public void downloadSystemLog(Long user_id, String request_date) {
		// 数据可访问权限处理方式，该方法不需要权限验证
		// 1.生成日志审查excel文件
		String file_path = generateExcel(user_id, request_date);
		// 2.下载excel文件
		FileDownloadUtil.downloadFile("logReview." + ExcelUtil.XLSX);
		// 3.下载完删除下载文件
		try {
			Files.delete(new File(file_path).toPath());
		} catch (IOException e) {
			throw new BusinessException("删除文件失败" + e.getMessage());
		}
	}

	@Method(desc = "生成日志审查excel文件", logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
			"2.验证当前用户下的工程是否存在" +
			"3.创建工作簿对象" +
			"4.创建工作表对象" +
			"5.创建单元格对象,批注插入到一行" +
			"6.得到上传文件的保存目录" +
			"7.判断文件是否存在" +
			"8.获取Excel的头列信息" +
			"9.遍历列名设置头信息" +
			"10.表对应所有列的值信息" +
			"11.获取对应表数据" +
			"12.存放表每列信息" +
			"13.设置每列信息" +
			"14.封装每列信息" +
			"15.循环出需要的数据,并添加到excel头下方" +
			"16.写进Excel表格")
	@Param(name = "user_id", desc = "用户ID", range = "新增用户时生成", nullable = true)
	@Param(name = "request_date", desc = "请求日期", range = "新增日志信息时生成", nullable = true)
	private String generateExcel(Long user_id, String request_date) {
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
			// 5.得到上传文件的保存目录
			String savePath =
					WebinfoConf.FileUpload_SavedDirName + File.separator + "logReview." + ExcelUtil.XLSX;
			File file = new File(savePath);
			// 6.判断文件是否存在不存在创建
			if (!file.exists()) {
				if (!file.createNewFile()) {
					throw new BusinessException("创建文件失败，文件目录可能不存在！");
				}
			}
			// 7.创建输出流
			out = new FileOutputStream(file);
			// 8.获取Excel的头列信息
			List<TableMeta> tableMetas = MetaOperator.getTablesWithColumns(Dbo.db(), Login_operation_info.TableName);
			Set<String> columnNames = tableMetas.get(0).getColumnNames();
			int cellNum = 0;
			// 9.遍历列名设置头信息
			for (String columnName : columnNames) {
				XSSFCell createCell = headRow.createCell(cellNum);
				createCell.setCellValue(columnName);
				cellNum++;
			}
			// 10.表对应所有列的值信息
			List<List<String>> columnValList = new ArrayList<>();
			// 11.获取对应表数据
			List<Map<String, Object>> systemLogInfoList = searchSystemLogInfo(user_id, request_date,
					null, null);
			if (!systemLogInfoList.isEmpty()) {
				for (Map<String, Object> systemLogInfo : systemLogInfoList) {
					// 12.存放表每列信息
					List<String> columnInfoList = new ArrayList<>();
					for (String columnName : columnNames) {
						// 13.设置每列信息
						if (systemLogInfo.get(columnName) != null) {
							columnInfoList.add(systemLogInfo.get(columnName).toString());
						} else {
							columnInfoList.add("");
						}
					}
					// 14.封装每列信息
					columnValList.add(columnInfoList);
				}
			}
			// 15.循环出需要的数据,并添加到excel头下方
			if (!columnValList.isEmpty()) {
				for (int i = 0; i < columnValList.size(); i++) {
					headRow = sheet.createRow(i + 1);
					List<String> valueList = columnValList.get(i);
					for (int j = 0; j < valueList.size(); j++) {
						headRow.createCell(j).setCellValue(valueList.get(j));
					}
				}
			}
			// 16.写进Excel表格
			workbook.write(out);
			return savePath;
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
}
