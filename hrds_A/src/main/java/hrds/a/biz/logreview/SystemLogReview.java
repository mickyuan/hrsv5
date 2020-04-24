package hrds.a.biz.logreview;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.CodecUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DefaultPageImpl;
import fd.ng.db.jdbc.Page;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.util.Dbo;
import fd.ng.web.util.RequestUtil;
import fd.ng.web.util.ResponseUtil;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.DataBaseCode;
import hrds.commons.entity.Login_operation_info;
import hrds.commons.exception.BusinessException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "日志审查类", author = "dhw", createdate = "2020/4/24 13:46")
public class SystemLogReview extends BaseAction {

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
			logicStep = "1.数据可访问权限处理方式，该方法不需要权限验证" +
					"2.获取本地文件路径" +
					"3.设置响应头，控制浏览器下载该文件" +
					"3.1firefox浏览器" +
					"3.2其它浏览器" +
					"4.创建输出流" +
					"5.获取要下载日志信息" +
					"6.判断日志信息是否为空，不为空下载" +
					"7.关闭流")
	@Param(name = "user_id", desc = "用户ID", range = "新增用户时生成", nullable = true)
	@Param(name = "request_date", desc = "请求日期", range = "新增日志信息时生成", nullable = true)
	public void downloadSystemLog(Long user_id, String request_date) {
		// 1.数据可访问权限处理方式，该方法不需要权限验证
		OutputStream outputStream;
		// 2.获取下载文件的文件名
		String fileName = "SystemOperation.doc";
		try {
			// 3.设置响应头，控制浏览器下载该文件
			if (RequestUtil.getRequest().getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {
				// 3.1firefox浏览器
				ResponseUtil.getResponse().setHeader("content-disposition", "attachment;filename="
						+ new String(fileName.getBytes(CodecUtil.UTF8_CHARSET), DataBaseCode.ISO_8859_1.getCode()));
			} else {
				// 3.2其它浏览器
				ResponseUtil.getResponse().setHeader("content-disposition", "attachment;filename="
						+ Base64.getEncoder().encodeToString(fileName.getBytes(CodecUtil.UTF8_CHARSET)));
			}
			ResponseUtil.getResponse().setContentType("APPLICATION/OCTET-STREAM");
			// 4.创建输出流
			outputStream = ResponseUtil.getResponse().getOutputStream();
			// 5.获取要下载日志信息
			List<Map<String, Object>> logInfoList = searchSystemLogInfo(user_id, request_date,
					null, null);
			// 6.判断日志信息是否为空，不为空下载
			if (!logInfoList.isEmpty()) {
				for (int i = 0; i < logInfoList.size(); i++) {
					if (i % 50000 == 0) {
						outputStream.flush();
					}
					outputStream.write(logInfoList.get(i).toString().getBytes());
				}
				outputStream.flush();
			}
			// 7.关闭流
			outputStream.close();
		} catch (UnsupportedEncodingException e) {
			throw new BusinessException("不支持的编码异常");
		} catch (FileNotFoundException e) {
			throw new BusinessException("文件不存在，可能目录不存在！");
		} catch (IOException e) {
			throw new BusinessException("下载文件失败！");
		}
	}
}
