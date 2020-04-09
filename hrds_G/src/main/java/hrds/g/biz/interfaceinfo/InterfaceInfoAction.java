package hrds.g.biz.interfaceinfo;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.web.fileupload.disk.DiskFileItemFactory;
import fd.ng.web.fileupload.servlet.ServletFileUpload;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.entity.Interface_use;
import hrds.commons.entity.Sysreg_parameter_info;
import hrds.commons.entity.Table_use_info;
import hrds.commons.utils.PropertyParaValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@DocClass(desc = "接口/数据表信息", author = "dhw", createdate = "2020/3/30 13:39")
public class InterfaceInfoAction extends BaseAction {

	private static final Logger logger = LogManager.getLogger();

	private static final Long INTERFACE_UPLOAD_FILE_MAXSIZE = Long.valueOf(
			PropertyParaValue.getString("interface.upload.file.maxsize", "104857600"));
	private static final Long INTERFACE_UPLOAD_FORMDATA_MAXSIZE = Long.valueOf(
			PropertyParaValue.getString("interface.upload.form.maxsize", "157286400"));

	private static final DiskFileItemFactory factory = new DiskFileItemFactory(1024000, null);
	private static final ServletFileUpload upload = new ServletFileUpload(factory);

	static {
		// 指定单个上传文件的最大尺寸,单位:字节，这里设为100Mb
		upload.setFileSizeMax(INTERFACE_UPLOAD_FILE_MAXSIZE);

		// 指定一次上传多个文件的总尺寸,单位:字节，这里设为150Mb
		upload.setSizeMax(INTERFACE_UPLOAD_FORMDATA_MAXSIZE);
		upload.setHeaderEncoding("UTF-8");
	}

	@Method(desc = "查询接口信息", logicStep = "1.数据可访问权限处理方式：该方法通过user_id进行访问权限限制" +
			"2.判断接口名称是否为空，不为空加条件查询" +
			"3.查询接口使用信息" +
			"4.返回查询接口使用信息")
	@Param(name = "interface_name", desc = "接口名称", range = "无限制")
	@Return(desc = "返回查询接口使用信息", range = "无限制")
	public Result searchInterfaceInfo(String interface_name) {
		// 1.数据可访问权限处理方式：该方法通过user_id进行访问权限限制
		SqlOperator.Assembler assembler = SqlOperator.Assembler.newInstance();
		assembler.clean();
		assembler.addSql("SELECT interface_use_id,interface_name,use_valid_date,start_use_date,url,user_id FROM "
				+ Interface_use.TableName + " WHERE user_id = ?").addParam(getUserId());
		// 2.判断接口名称是否为空，不为空加条件查询
		if (StringUtil.isNotBlank(interface_name)) {
			assembler.addLikeParam("interface_name", interface_name);
		}
		assembler.addSql(" order by interface_use_id");
		// 3.查询接口使用信息
		Result interfaceResult = Dbo.queryResult(assembler.sql(), assembler.params());
		for (int i = 0; i < interfaceResult.getRowCount(); i++) {
			interfaceResult.setObject(i, "start_use_date", DateUtil.parseStr2DateWith8Char(
					interfaceResult.getString(i, "start_use_date")));
			interfaceResult.setObject(i, "use_valid_date", DateUtil.parseStr2DateWith8Char(
					interfaceResult.getString(i,
							"use_valid_date")));
		}
		// 4.返回查询接口使用信息
		return interfaceResult;
	}

	@Method(desc = "查询数据表信息", logicStep = "1.数据可访问权限处理方式：该方法通过user_id进行访问权限限制" +
			"2.判断系统登记表名是否为空，不为空加条件查询" +
			"3.查询表使用信息" +
			"4.返回查询表使用信息")
	@Param(name = "sysreg_name", desc = "系统登记表名", range = "无限制", nullable = true)
	@Return(desc = "返回查询表使用信息", range = "无限制")
	public Result searchDataTableInfo(String sysreg_name) {
		// 1.数据可访问权限处理方式：该方法通过user_id进行访问权限限制
		SqlOperator.Assembler assembler = SqlOperator.Assembler.newInstance();
		assembler.clean();
		assembler.addSql("SELECT sysreg_name,original_name,use_id FROM " + Table_use_info.TableName
				+ " WHERE user_id = ?").addParam(getUserId());
		// 2.判断系统登记表名是否为空，不为空加条件查询
		if (StringUtil.isNotBlank(sysreg_name)) {
			assembler.addLikeParam("sysreg_name", sysreg_name.toUpperCase());
		}
		// 3.查询表使用信息
		Result tableResult = Dbo.queryResult(assembler.sql(), assembler.params());
		for (int i = 0; i < tableResult.getRowCount(); i++) {
			tableResult.setObject(i, "start_use_data", DateUtil.parseStr2DateWith8Char(
					tableResult.getString(i, "start_use_data")));
		}
		// 4.返回查询表使用信息
		return tableResult;
	}

	@Method(desc = "根据表使用ID查询当前用户对应的列信息",
			logicStep = "1.数据可访问权限处理方式：该方法通过user_id进行访问权限限制" +
					"2.返回根据表使用ID查询当前用户对应的列信息")
	@Param(name = "use_id", desc = "接口使用ID", range = "新增接口使用信息时生成")
	@Return(desc = "返回根据表使用ID查询当前用户对应的列信息", range = "无限制")
	public List<Object> searchColumnInfoById(long use_id) {
		// 1.数据可访问权限处理方式：该方法通过user_id进行访问权限限制
		// 2.返回根据表使用ID查询当前用户对应的列信息
		return Dbo.queryOneColumnList("SELECT table_column_name FROM " + Sysreg_parameter_info.TableName +
				" WHERE use_id = ? and user_id=?", use_id, getUserId());
	}

}
