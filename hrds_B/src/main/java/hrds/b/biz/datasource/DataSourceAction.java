package hrds.b.biz.datasource;

import com.alibaba.fastjson.TypeReference;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.CodecUtil;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DefaultPageImpl;
import fd.ng.db.jdbc.Page;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.web.annotation.UploadFile;
import fd.ng.web.util.Dbo;
import fd.ng.web.util.FileUploadUtil;
import fd.ng.web.util.ResponseUtil;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.AuthType;
import hrds.commons.codes.UserType;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.DboExecute;
import hrds.commons.utils.key.PrimayKeyGener;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@DocClass(desc = "数据源增删改查，导入、下载类", author = "dhw", createdate = "2019-9-20 09:23:06")
public class DataSourceAction extends BaseAction {

	@Method(desc = "查询数据源，部门、agent,申请审批,业务用户和采集用户,部门与数据源关系表信息，首页展示",
			logicStep = "1.数据可访问权限处理方式，以下sql通过user_id关联进行权限检查" +
					"2.查询当前用户下的所有数据源信息" +
					"3.判断结果集是否为空，不为空获取数据源对应agent个数封装到结果集" +
					"4.返回放数据源以及数据源对应agent个数信息")
	@Return(desc = "4.返回放数据源以及数据源对应agent个数信息", range = "无限制")
	public Result searchDataSourceAndAgentCount() {
		// 1.数据可访问权限处理方式，以下sql通过user_id关联进行权限检查
		// 2.查询当前用户下的所有数据源信息
		Result dsResult = Dbo.queryResult("SELECT source_id,datasource_name FROM " + Data_source.TableName +
				" WHERE create_user_id=?", getUserId());
		// 3.判断结果集是否为空，不为空获取数据源对应agent个数封装到结果集
		if (!dsResult.isEmpty()) {
			for (int i = 0; i < dsResult.getRowCount(); i++) {
				long number = Dbo.queryNumber("select count(*) from " + Agent_info.TableName
						+ " where source_id=?", dsResult.getLong(i, "source_id"))
						.orElseThrow(() -> new BusinessException("sql查询错误！"));
				dsResult.setObject(i, "sumAgent", number);
			}
		}
		// 4.返回放数据源以及数据源对应agent个数信息
		return dsResult;
	}

	@Method(desc = "数据管理列表，分页查询获取数据申请审批信息的集合",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
					"2.获取所有的source_id" +
					"3.查询数据源申请审批信息集合并返回")
	@Param(name = "currPage", desc = "分页当前页", range = "大于0的正整数", valueIfNull = "1")
	@Param(name = "pageSize", desc = "分页查询每页显示条数", range = "大于0的正整数", valueIfNull = "5")
	@Return(desc = "存放数据申请审批信息的集合,分页查询总记录数会放在第一个结果集中（totalSize）", range = "无限制")
	public Result getDataAuditInfoForPage(int currPage, int pageSize) {
		// 1.数据可访问权限处理方式，通过user_id进行权限控制
		// 2.获取所有的source_id
		List<Long> sourceIdList = Dbo.queryOneColumnList("select source_id from " + Data_source.TableName
				+ " where create_user_id=?", getUserId());
		// 3.查询数据源申请审批信息集合
		SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
		asmSql.addSql("select da.DA_ID,da.APPLY_DATE,da.APPLY_TIME,da.APPLY_TYPE,da.AUTH_TYPE," +
				"sfa.original_name, sfa.file_suffix,sfa.file_type,su.user_name FROM " + Data_auth.TableName
				+ " da join " + Sys_user.TableName + " su on da.user_id=su.user_id join "
				+ Source_file_attribute.TableName + " sfa on da.file_id= sfa.file_id  " +
				" where su.create_id in (select user_id from sys_user where user_type=? or user_id = ?) ")
				.addParam(UserType.XiTongGuanLiYuan.getCode()).addParam(getUserId())
				.addORParam("sfa.source_id", sourceIdList.toArray()).addSql(" ORDER BY da_id desc");
		Page page = new DefaultPageImpl(currPage, pageSize);
		// 4.分页查询数据源申请审批信息集合并返回
		Result result = Dbo.queryPagedResult(page, asmSql.sql(), asmSql.params());
		result.setObject(0, "totalSize", page.getTotalSize());
		return result;
	}

	@Method(desc = "数据权限管理，分页查询数据源及部门关系信息",
			logicStep = "1.数据可访问权限处理方式，以下sql通过user_id关联进行权限检查" +
					"2.分页查询数据源及部门关系" +
					"3.判断数据源是否为空" +
					"4.循环数据源" +
					"5.创建存放数据源对应部门集合" +
					"6.查询获取数据源对应部门结果集" +
					"7.判断数据源对应的部门结果集是否为空" +
					"8.循环部门获取部门名称" +
					"9.将各个数据源对应的部门名称加入list" +
					"10.封装部门名称到结果集" +
					"11.封装分页查询数据源与部门关系信息以及总数" +
					"12.返回结果集")
	@Param(name = "currPage", desc = "分页当前页", range = "大于0的正整数", valueIfNull = "1")
	@Param(name = "pageSize", desc = "分页查询每页显示条数", range = "大于0的正整数", valueIfNull = "5")
	@Return(desc = "返回分页查询数据源及部门关系,分页查询总记录数会放在第一个结果集中（totalSize）", range = "无限制")
	public Result searchSourceRelationDepForPage(int currPage, int pageSize) {

		// 1.数据可访问权限处理方式，以下sql通过user_id关联进行权限检查
		// 2.分页查询数据源及部门关系
		Page page = new DefaultPageImpl(currPage, pageSize);
		Result dsResult = Dbo.queryPagedResult(page, "SELECT source_id,datasource_name " +
				" from " + Data_source.TableName + " where create_user_id=? " +
				" order by create_date desc,create_time desc", getUserId());
		// 3.判断数据源是否为空
		if (!dsResult.isEmpty()) {
			// 4.循环数据源
			for (int i = 0; i < dsResult.getRowCount(); i++) {
				// 5.获取数据源对应部门名称所有值,不需要权限控制
				Result depNameAndId = getDepNameAndId(dsResult.getLong(i, "source_id"));
				if (!depNameAndId.isEmpty()) {
					StringBuilder sb = new StringBuilder();
					for (int j = 0; j < depNameAndId.getRowCount(); j++) {
						sb.append(depNameAndId.getString(j, "dep_name")).append(",");
					}
					dsResult.setObject(i, "dep_name", sb.deleteCharAt(sb.length() - 1).toString());
				}
			}
		}
		// 8.封装分页查询数据源与部门关系信息以及总数
		dsResult.setObject(0, "totalSize", page.getTotalSize());
		// 9.返回结果集
		return dsResult;
	}

	@Method(desc = "数据权限管理，更新数据源关系部门信息",
			logicStep = "1.数据可访问权限处理方式，通过sourceId与user_id关联检查" +
					"2.先删除数据源与部门关系信息,删除几条数据不确定，一个数据源对应多个部门，所以不能用DboExecute" +
					"3.建立新关系，保存source_relation_dep表信息")
	@Param(name = "source_id", desc = "data_source表主键ID", range = "不为空的十位数字，新增时通过主键生成规则自动生成")
	@Param(name = "dep_id", desc = "存储source_relation_dep表主键ID的数组", range = "不为空以及不为空格")
	public void updateAuditSourceRelationDep(long source_id, long[] dep_id) {
		// 1.数据可访问权限处理方式，通过sourceId与user_id关联检查
		if (Dbo.queryNumber("select count(1) from " + Data_source.TableName + " ds left join " +
				Source_relation_dep.TableName + " srd on ds.source_id=srd.source_id where ds.source_id=?" +
				" and ds.create_user_id=?", source_id, getUserId())
				.orElseThrow(() -> new BusinessException("sql查询错误！")) == 0) {
			throw new BusinessException("数据权限校验失败，数据不可访问！");
		}
		// 2.先删除数据源与部门关系信息,删除几条数据不确定，一个数据源对应多个部门，所以不能用DboExecute
		int num = Dbo.execute("delete from " + Source_relation_dep.TableName + " where source_id=?",
				source_id);
		if (num < 1) {
			throw new BusinessException("编辑时会先删除原数据源与部门关系信息，删除错旧关系时错误，" +
					"sourceId=" + source_id);
		}
		// 3.建立新关系，保存source_relation_dep表信息
		saveSourceRelationDep(source_id, dep_id);
	}

	@Method(desc = "数据管理列表，数据申请审批",
			logicStep = "1.数据可访问权限处理方式，根据user_id进行权限控制" +
					"2.authType代码项合法性验证，如果不存在该方法直接会抛异常" +
					"3.根据数据权限设置ID查询数据申请审批信息，确认要审批的信息一定存在" +
					"4.根据数据权限设置ID以及权限类型进行审批")
	@Param(name = "da_id", desc = "数据权限设置ID，表data_auth表主键", range = "不为空的十位数字，" +
			"新增时通过主键生成规则自动生成")
	@Param(name = "auth_type", desc = "权限类型", range = "使用权限类型代码项(authType)")
	public void dataAudit(long da_id, String auth_type) {
		// 1.数据可访问权限处理方式，根据user_id进行权限控制
		// 2.authType代码项合法性验证，如果不存在该方法直接会抛异常
		AuthType.ofEnumByCode(auth_type);
		// 3.根据数据权限设置ID查询数据申请审批信息，确认要审批的信息一定存在
		if (Dbo.queryNumber("select count(*) from " + Data_auth.TableName + " where da_id=? and " +
				" user_id=?", da_id, getUserId()).orElseThrow(() ->
				new BusinessException("sql查询错误")) == 0) {
			throw new BusinessException("此申请已取消或不存在！");
		}
		// 4.根据数据权限设置ID以及权限类型进行审批
		Data_auth dataAuth = new Data_auth();
		dataAuth.setAudit_date(DateUtil.getSysDate());
		dataAuth.setAudit_time(DateUtil.getSysTime());
		dataAuth.setAudit_userid(getUserId());
		dataAuth.setAudit_name(getUserName());
		dataAuth.setAuth_type(auth_type);
		dataAuth.setDa_id(da_id);
		dataAuth.update(Dbo.db());
	}

	@Method(desc = "根据权限设置ID进行权限回收并将最新数据申请审批信息返回",
			logicStep = "1.数据可访问权限处理方式，根据user_id进行权限控制" +
					"2.权限回收" +
					"3.查询审批后的最新数据申请审批信息并返回")
	@Param(name = "da_id", desc = "数据权限设置ID，表data_auth表主键",
			range = "不为空的十位数字，新增时通过主键生成规则自动生成")
	@Return(desc = "存放数据申请审批信息的集合", range = "无限制")
	public void deleteAudit(long da_id) {
		// 1.数据可访问权限处理方式，根据user_id进行权限控制
		// 2.权限回收
		DboExecute.deletesOrThrow("权限回收成功!", "delete from " + Data_auth.TableName +
				" where da_id = ? and user_id=?", da_id, getUserId());
	}

	@Method(desc = "新增数据源",
			logicStep = "1.数据可访问权限处理方式，新增时会设置创建用户ID，会获取当前用户ID，所以不需要权限验证" +
					"2.字段合法性检查" +
					"3.新增前查询数据源编号是否已存在" +
					"4.对data_source初始化一些非页面传值" +
					"5.保存data_source信息" +
					"6.保存source_relation_dep信息")
	@Param(name = "dataSource", desc = "data_source表实体对象", range = "与data_source表字段规则一致",
			isBean = true)
	@Param(name = "dep_id", desc = "存储source_relation_dep表主键ID的数组", range = "不为空以及不为空格")
	public void saveDataSource(Data_source dataSource, long[] dep_id) {
		// 1.数据可访问权限处理方式，新增时会设置创建用户ID，会获取当前用户ID，所以不需要权限验证
		// 2.字段做合法性检查
		fieldLegalityValidation(dataSource.getDatasource_name(), dataSource.getDatasource_number(), dep_id);
		// 3.新增前查询数据源编号是否已存在
		isExistDataSourceNumber(dataSource.getDatasource_number());
		// 4.对data_source初始化一些非页面传值
		// 数据源主键ID
		dataSource.setSource_id(PrimayKeyGener.getNextId());
		// 数据源创建用户
		dataSource.setCreate_user_id(getUserId());
		// 数据源创建日期
		dataSource.setCreate_date(DateUtil.getSysDate());
		// 数据源创建时间
		dataSource.setCreate_time(DateUtil.getSysTime());
		// 5.保存data_source信息
		dataSource.add(Dbo.db());
		// 6.保存source_relation_dep信息
		saveSourceRelationDep(dataSource.getSource_id(), dep_id);
	}

	@Method(desc = "更新数据源信息",
			logicStep = "1.数据可访问权限处理方式，通过sourceId与user_id关联检查" +
					"2.字段合法性检查" +
					"3.将data_source实体数据封装" +
					"4.更新数据源信息" +
					"5.先删除数据源与部门关系信息,删除几条数据不确定，一个数据源对应多个部门，所以不能用DboExecute" +
					"6.保存source_relation_dep表信息")
	@Param(name = "source_id", desc = "data_source表主键，source_relation_dep表外键",
			range = "10位数字,新增时生成")
	@Param(name = "source_remark", desc = "备注，source_relation_dep表外键", range = "无限制", nullable = true)
	@Param(name = "datasource_name", desc = "数据源名称", range = "不为空且不为空格")
	@Param(name = "datasource_number", desc = "数据源编号", range = "以字母开头的不超过四位数的字母数字组合")
	@Param(name = "dep_id", desc = "source_relation_dep表主键ID的数组", range = "不为空以及不为空格")
	public void updateDataSource(Long source_id, String source_remark, String datasource_name,
	                             String datasource_number, long[] dep_id) {
		// 1.数据可访问权限处理方式，通过source_id与user_id关联检查
		if (Dbo.queryNumber("select count(1) from " + Data_source.TableName +
				" where source_id=? and create_user_id=?", source_id, getUserId())
				.orElseThrow(() -> new BusinessException("sql查询错误！")) == 0) {
			throw new BusinessException("数据权限校验失败，数据不可访问！");
		}
		// 2.字段合法性检查
		fieldLegalityValidation(datasource_name, datasource_number, dep_id);
		// 3.将data_source实体数据封装
		Data_source dataSource = new Data_source();
		dataSource.setSource_id(source_id);
		dataSource.setDatasource_name(datasource_name);
		dataSource.setDatasource_number(datasource_number);
		dataSource.setSource_remark(source_remark);
		// 4.更新数据源信息
		dataSource.update(Dbo.db());
		// 5.先删除数据源与部门关系信息,删除几条数据不确定，一个数据源对应多个部门，所以不能用DboExecute
		int num = Dbo.execute("delete from " + Source_relation_dep.TableName + " where source_id=?",
				dataSource.getSource_id());
		if (num < 1) {
			throw new BusinessException("编辑时会先删除原数据源与部门关系信息，删除错旧关系时错误，" +
					"sourceId=" + dataSource.getSource_id());
		}
		// 6.保存source_relation_dep表信息
		saveSourceRelationDep(source_id, dep_id);
	}

	@Method(desc = "数据源表字段合法性验证",
			logicStep = "1.数据可访问权限处理方式，这是个私有方法，不会单独被调用，所以不需要权限验证" +
					"2.循环遍历获取source_relation_dep主键ID，验证dep_id合法性" +
					"2.1判断部门ID是否为空或者空格" +
					"2.2判断部门是否存在" +
					"3.验证datasource_name是否合法" +
					"4.验证datasource_number为以字母开头的数字、26个英文字母或者下划线组成的字符串")
	@Param(name = "datasource_name", desc = "数据源名称", range = "不为空且不为空格")
	@Param(name = "datasource_umber", desc = "数据源编号", range = "不为空且不为空格，长度不超过四位")
	@Param(name = "dep_id", desc = "source_relation_dep表主键ID的数组", range = "不为空以及不为空格")
	private void fieldLegalityValidation(String datasource_name, String datasource_umber, long[] dep_id) {
		// 1.数据可访问权限处理方式，通过create_user_id检查
		// 2.循环遍历获取source_relation_dep主键ID，验证dep_id合法性
		for (long depId : dep_id) {
			// 2.1判断部门ID是否为空或者空格
			if (StringUtil.isBlank(String.valueOf(depId))) {
				throw new BusinessException("部门不能为空或者空格，新增部门时通过主键生成!");
			}
			// 2.2判断部门是否存在
			isExistDepartment(depId);
		}
		// 3.验证datasource_name是否合法
		if (StringUtil.isBlank(datasource_name)) {
			throw new BusinessException("数据源名称不能为空以及不能为空格，datasource_name=" + datasource_name);
		}
		// 4.验证datasource_number为以字母开头的数字、26个英文字母或者下划线组成的字符串
		Matcher matcher = Pattern.compile("^[a-zA-Z]\\w+$").matcher(datasource_umber);
		if (StringUtil.isBlank(datasource_umber) || !matcher.matches()) {
			throw new BusinessException("数据源编号只能是以字母开头的不超过四位数的字母数字组合，"
					+ "datasource_number=" + datasource_umber);
		}
	}

	@Method(desc = "判断数据源编号是否已存在",
			logicStep = "1.数据可访问权限处理方式，通过create_user_id检查" +
					"2.判断数据源编号是否重复")
	@Param(name = "datasource_umber", desc = "数据源编号", range = "以字母开头的不超过四位数的字母数字组合")
	private void isExistDataSourceNumber(String datasource_umber) {
		// 1.数据可访问权限处理方式，通过create_user_id检查
		// 2.判断数据源编号是否重复
		if (Dbo.queryNumber("select count(1) from " + Data_source.TableName + " where datasource_number=?"
				+ " and create_user_id=?", datasource_umber, getUserId()).orElseThrow(() ->
				new BusinessException("sql查询错误！")) > 0) {
			throw new BusinessException("数据源编号重复,datasource_number=" + datasource_umber);
		}
	}

	@Method(desc = "判断部门是否存在",
			logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
					"2.判断部门是否存在")
	@Param(name = "dep_id", desc = "department_info表主键", range = "新增部门时生成")
	private void isExistDepartment(long dep_id) {
		// 1.数据可访问权限处理方式，该方法不需要权限控制
		// 2.判断部门是否存在
		if (Dbo.queryNumber("select count(*) from " + Department_info.TableName + " where dep_id=?",
				dep_id).orElseThrow(() -> new BusinessException("sql查询错误！")) != 1) {
			throw new BusinessException("该部门ID对应的部门不存在！");
		}
	}

	@Method(desc = "保存数据源与部门关系表信息",
			logicStep = "1.数据可访问权限处理方式，这是一个私有方法，不会单独被调用，所以这里不需要做权限验证" +
					"2.验证传递的部门ID对应的部门信息是否存在" +
					"3.创建source_relation_dep对象，并封装数据" +
					"4.循环遍历存储部门ID的数组并保存source_relation_dep表信息")
	@Param(name = "source_id", desc = "source_relation_dep表外键ID", range = "不能为空以及不能为空格")
	@Param(name = "dep_id", desc = "存储source_relation_dep表主键ID的数组",
			range = "不为空以及不为空格")
	private void saveSourceRelationDep(long source_id, long[] dep_id) {
		// 1.数据可访问权限处理方式，这是一个私有方法，不会单独被调用，所以这里不需要做权限验证
		// 2.验证传递的部门ID对应的部门信息是否存在
		for (long depId : dep_id) {
			isExistDepartment(depId);
		}
		// 3.创建source_relation_dep对象，并封装数据
		Source_relation_dep sourceRelationDep = new Source_relation_dep();
		sourceRelationDep.setSource_id(source_id);
		// 4.循环遍历存储部门ID的数组并保存source_relation_dep表信息
		for (long depId : dep_id) {
			sourceRelationDep.setDep_id(depId);
			sourceRelationDep.add(Dbo.db());
		}
	}

	@Method(desc = "根据数据源编号查询数据源以及对应部门名称信息",
			logicStep = "1.数据可访问权限处理方式，以下SQL关联sourceId与user_id检查" +
					"2.关联查询data_source表信息" +
					"3.获取数据源对应部门名称所有值，不需要权限控制" +
					"4.返回数据源以及对应部门名称信息")
	@Param(name = "source_id", desc = "data_source表主键ID，source_relation_dep表外键ID", range = "新增数据源时生成")
	@Return(desc = "返回关联查询data_source表与source_relation_dep表信息结果以及部门信息",
			range = "无限制")
	public Map<String, Object> searchDataSourceById(long source_id) {
		// 1.数据可访问权限处理方式，以下SQL关联sourceId与user_id检查
		// 2.关联查询data_source表信息
		Map<String, Object> datasourceMap = Dbo.queryOneObject("select * from " + Data_source.TableName +
				" where source_id=? and create_user_id=?", source_id, getUserId());
		// 3.获取数据源对应部门名称所有值,不需要权限控制
		Result depNameAndId = getDepNameAndId(source_id);
		datasourceMap.put("depNameAndId", depNameAndId.toList());
		// 4.返回数据源以及对应部门名称信息
		return datasourceMap;
	}

	@Method(desc = "查询部门信息",
			logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
					"2.查询部门信息并返回")
	@Return(desc = "返回查询部门信息", range = "无限制")
	public List<Department_info> searchDepartmentInfo() {
		// 1.数据可访问权限处理方式，该方法不需要权限控制
		// 2.查询部门信息并返回
		return Dbo.queryList(Department_info.class, "select * from " + Department_info.TableName);
	}

	@Method(desc = "根据数据源ID获取部门名称",
			logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
					"2.根据数据源ID获取部门名称" +
					"3.判断部门名称集合是否为空，不为空返回部门信息" +
					"4.为空，返回null")
	@Param(name = "参数名称", desc = "参数描述", range = "取值范围")
	@Return(desc = "返回内容描述", range = "取值范围")
	private Result getDepNameAndId(long source_id) {
		// 1.数据可访问权限处理方式，该方法不需要权限控制
		// 2.根据数据源ID获取部门名称
		List<String> depIdList = Dbo.queryOneColumnList("select dep_id from " + Source_relation_dep.TableName
				+ " where source_id=?", source_id);
		if (depIdList.isEmpty()) {
			throw new BusinessException("当前数据源对应部门不存在！");
		}
		SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
		asmSql.clean();
		asmSql.addSql("select * from " + Department_info.TableName + " where ");
		asmSql.addORParam("dep_id", depIdList.toArray());
		return Dbo.queryResult(asmSql.sql().replace("and", " "), asmSql.params());
	}

	@Method(desc = "删除数据源信息",
			logicStep = "1.数据可访问权限处理方式，以下SQL关联sourceId与user_id检查" +
					"2.先查询该datasource下是否还有agent,有不能删除，没有，可以删除" +
					"3.删除data_source表信息" +
					"4.删除source_relation_dep信息,因为一个数据源可能对应多个部门，所以这里无法使用DboExecute的删除方法" +
					"5.删除source_relation_dep信息")
	@Param(name = "source_id", desc = "source_relation_dep表外键ID", range = "不能为空以及不能为空格")
	public void deleteDataSource(long source_id) {

		// 1.数据可访问权限处理方式，以下SQL关联sourceId与user_id检查
		// 2.先查询该datasource下是否还有agent
		// FIXME: orElse用法有误，逻辑有问题，用orElseThrow  已解决
		if (Dbo.queryNumber("SELECT count(1) FROM " + Agent_info.TableName + " WHERE source_id=? " +
				" and user_id=?", source_id, getUserId()).orElseThrow(() ->
				new BusinessException("sql查询错误！")) > 0) {
			throw new BusinessException("此数据源下还有agent，不能删除,sourceId=" + source_id);
		}
		// 3.删除data_source表信息
		DboExecute.deletesOrThrow("删除数据源信息表失败，sourceId=" + source_id,
				"delete from " + Data_source.TableName + " where source_id=? and create_user_id=?",
				source_id, getUserId());
		// 4.删除source_relation_dep信息,因为一个数据源可能对应多个部门，所以这里无法使用DboExecute的删除方法
		int srdNum = Dbo.execute("delete from " + Source_relation_dep.TableName + " where source_id=?",
				source_id);
		if (srdNum < 1) {
			// 如果数据源存在，那么部门一定存在，所以这里不需要判断等于0的情况
			throw new BusinessException("删除该数据源下数据源与部门关系表数据错误，sourceId="
					+ source_id);
		}
	}

	@Method(desc = "查询数据采集用户信息",
			logicStep = "1.数据可访问权限处理方式，此方法不需要权限验证" +
					"2.查询数据采集用户信息并返回查询结果")
	@Return(desc = "存放数据采集用户信息的集合", range = "无限制")
	public List<Sys_user> searchDataCollectUser() {
		// 1.数据可访问权限处理方式，此方法不需要权限验证，没有用户访问限制
		// 2.查询数据采集用户信息并返回查询结果
		// FIXME: 为什么用union all以及为什么用like,注释说明      已解决
		// 一个用户会有多种用户功能类型，默认用户功能只会有一种，我们需要的是用用户功能类型中包含采集用户的所有用户，所以用like
		return Dbo.queryList(Sys_user.class, "select user_id,user_name from " + Sys_user.TableName + " where dep_id=?"
						+ " and usertype_group like ? ", getUser().getDepId(),
				"%" + UserType.CaiJiYongHu.getCode() + "%");
	}

	@Method(desc = "导入数据源，数据源下载文件提供的文件中涉及到的所有表的数据导入数据库中对应的表中",
			logicStep = "1.数据可访问权限处理方式，此方法不需要权限验证，不涉及用户权限" +
					"2.验证agent_ip,agent_port是否有效" +
					"3.通过文件名称获取文件" +
					"4.使用base64对数据进行解码" +
					"5.导入数据源数据，将涉及到的所有表的数据导入数据库中对应的表中")
	@Param(name = "agent_ip", desc = "agent地址", range = "不能为空，服务器ip地址", example = "127.0.0.1")
	@Param(name = "agent_port", desc = "agent端口", range = "1024-65535")
	@Param(name = "user_id", desc = "数据采集用户ID，指定谁可以查看该用户对应表信息", range = "不能为空以及空格，页面传值")
	@Param(name = "file", desc = "上传文件名称（全路径），上传要导入的数据源", range = "不能为空以及空格")
	@UploadFile
	public void uploadFile(String agent_ip, String agent_port, Long user_id, String file) {
		try {
			// 1.数据可访问权限处理方式，此方法不需要权限验证，不涉及用户权限
			// 2.验证agent_ip,agent_port是否有效
			checkAgentField(agent_ip, agent_port);
			// 3.通过文件名称获取文件
			File uploadedFile = FileUploadUtil.getUploadedFile(file);
			if (!uploadedFile.exists()) {
				throw new BusinessException("上传文件不存在！");
			}
			// 4.使用base64解码
			String strTemp = new String(Base64.getDecoder().decode(Files.readAllBytes(
					uploadedFile.toPath())));
			// 5.导入数据源数据，将涉及到的所有表的数据导入数据库对应的表中
			importDataSource(strTemp, agent_ip, agent_port, user_id, getUserId());
		} catch (IOException e) {
			throw new BusinessException("上传文件失败！");
		}
	}

	@Method(desc = "校验字段agent_ip,agent_port是否合法",
			logicStep = "1.数据可访问权限处理方式，此方法不需要权限验证" +
					"2.判断agent_ip是否是一个合法的ip" +
					"3.判断agent_port是否是一个有效的端口")
	@Param(name = "agent_ip", desc = "agent地址", range = "不能为空，服务器ip地址", example = "127.0.0.1")
	@Param(name = "agent_port", desc = "agent端口", range = "1024-65535")
	@Return(desc = "返回内容描述", range = "取值范围")
	private void checkAgentField(String agent_ip, String agent_port) {
		// 1.数据可访问权限处理方式，此方法不需要权限验证
		// 2.判断agent_ip是否是一个合法的ip
		Pattern pattern = Pattern.compile("^(\\d|[1-9]\\d|1\\d{2}|2[0-5][0-5])\\.(\\d|[1-9]\\d|1\\d{2}" +
				"|2[0-5][0-5])\\.(\\d|[1-9]\\d|1\\d{2}|2[0-5][0-5])\\.(\\d|[1-9]\\d|1\\d{2}|2[0-5][0-5])$");
		Matcher matcher = pattern.matcher(agent_ip);
		if (!matcher.matches()) {
			throw new BusinessException("agent_ip不是一个有效的ip地址,agent_ip=" + agent_ip);
		}
		// 3.判断agent_port是否是一个有效的端口
		pattern = Pattern.compile("^([0-9]|[1-9]\\d{1,3}|[1-5]\\d{4}|6[0-4]\\d{4}|65[0-4]\\d{2}|655[0-2]" +
				"\\d|6553[0-5])$");
		matcher = pattern.matcher(agent_port);
		if (!matcher.matches()) {
			throw new BusinessException("agent_port端口不是有效的端口,agent_port=" + agent_port);
		}
	}

	@Method(desc = "导入数据源数据，将涉及到的所有表的数据导入数据库中对应的表中",
			logicStep = "1.获取文件对应所有表信息的map" +
					"2.遍历并解析拿到每张表的信息，map里封装的是所有数据源相关的表信息" +
					"3.获取数据源data_source信息并插入数据库" +
					"4.将department_info表数据插入数据库" +
					"5.将source_relation_dep数据插入数据库" +
					"6.将agent_info表数据插入数据库" +
					"7.将Agent_down_info表数据插入数据库" +
					"8.将collect_job_classify表数据插入数据库" +
					"9.将ftp采集设置ftp_collect表数据插入数据库" +
					"10.将ftp已传输表ftp_transfered表数据插入数据库" +
					"11.将对象采集设置object_collect表数据插入数据库" +
					"12.将对象采集对应信息object_collect_task表数据插入数据库" +
					"13.将对象采集存储设置object_storage表数据插入数据库" +
					"14.将对象采集结构信息object_collect_struct表数据插入数据库" +
					"15.将数据库设置database_set表数据插入数据库" +
					"16.将文件系统设置file_collect_set表数据插入数据库" +
					"17.将文件源设置file_source表数据插入数据库" +
					"18.将信号文件入库信息signal_file表数据插入数据库" +
					"19.将数据库对应的表table_info表数据插入数据库" +
					"20.将列合并信息column_merge表数据插入数据库" +
					"21.将表存储信息table_storage_info表数据插入数据库" +
					"22.将表清洗参数信息table_clean表数据插入数据库" +
					"23.将表对应的字段table_column表数据插入数据库" +
					"24.将列清洗参数信息column_clean表数据插入数据库" +
					"25.将列拆分信息表column_split表数据插入数据库")
	@Param(name = "strTemp", desc = "涉及数据源文件下载相关的所有表进行base64编码后的信息", range = "不能为空")
	@Param(name = "agent_ip", desc = "agent地址", range = "不能为空，服务器ip地址", example = "127.0.0.1")
	@Param(name = "agent_port", desc = "agent端口", range = "1024-65535")
	@Param(name = "user_id", desc = "数据采集用户ID，指定谁可以查看该用户对应表信息",
			range = "不能为空以及空格，页面传值,新增时生成")
	@Param(name = "create_user_id", desc = "data_source表数据源创建用户ID，代表数据是由谁创建的",
			range = "4位数字，新增用户时生成")
	private void importDataSource(String strTemp, String agent_ip, String agent_port, long
			user_id, long create_user_id) {
		Type type = new TypeReference<Map<String, Object>>() {
		}.getType();
		// 1.获取文件对应所有表信息的map
		Map<String, Object> collectMap = JsonUtil.toObject(strTemp, type);
		// 2.遍历并解析拿到每张表的信息，map里封装的是所有数据源相关的表信息
		// 3.获取重新生成的数据源ID集合并将数据源信息插入数据库
		String source_id = getDataSource(create_user_id, collectMap);
		// 4.将department_info表数据插入数据库
		Map<Long, String> departmentInfo = addDepartmentInfo(collectMap);
		// 5.将source_relation_dep数据插入数据库
		addSourceRelationDep(collectMap, source_id, departmentInfo);
		// 6.将agent_info表数据插入数据库并返回存放新旧agentID的集合
		Map<Long, String> agentIdMap = addAgentInfo(agent_ip, agent_port, user_id, source_id, collectMap);
		// 7.将Agent_down_info表数据插入数据库
		addAgentDownInfo(agent_ip, agent_port, user_id, collectMap, agentIdMap);
		// 8.将collect_job_classify表数据插入数据库
		Map<String, String> classifyAndAgentId = addCollectJobClassify(create_user_id, collectMap, agentIdMap);
		// 9.将ftp采集设置ftp_collect表数据插入数据库
		Map<Long, String> ftpIdMap = addFtpCollect(collectMap, agentIdMap);
		// 10.将ftp已传输表ftp_transfered表数据插入数据库
		addFtpTransfered(collectMap, ftpIdMap);
		// 11.将对象采集设置object_collect表数据插入数据库并返回agent ID以及对象采集ID的集合
		Map<String, String> odcMap = addObjectCollect(collectMap, agentIdMap);
		// 12.将对象采集对应信息object_collect_task表数据插入数据库并返回新旧对象采集任务编号的集合
		Map<Long, String> ocsIdMap = addObjectCollectTask(collectMap, odcMap);
		// 14.将对象采集结构信息object_collect_struct表数据插入数据库
		addObjectCollectStruct(collectMap, ocsIdMap);
		// 15.将数据库设置database_set表数据插入数据库
		Map<Long, String> databaseIdMap = addDatabaseSet(collectMap, classifyAndAgentId);
		// 16.将文件系统设置file_collect_set表数据插入数据库
		Map<String, String> agentAndFcsIdMap = addFileCollectSet(collectMap, agentIdMap);
		// 17.将文件源设置file_source表数据插入数据库
		addFileSource(collectMap, agentAndFcsIdMap);
		// 18.将信号文件入库信息signal_file表数据插入数据库
		addSignalFile(collectMap, databaseIdMap);
		// 19.将数据库对应的表table_info表数据插入数据库
		Map<Long, String> tableIdMap = addTableInfo(collectMap, databaseIdMap);
		// 20.将列合并信息column_merge表数据插入数据库
		addColumnMerge(collectMap, tableIdMap);
		// 21.将表存储信息table_storage_info表数据插入数据库
		addTableStorageInfo(collectMap, tableIdMap);
		// 22.将表清洗参数信息table_clean表数据插入数据库
		addTableClean(collectMap, tableIdMap);
		// 23.将表对应的字段table_column表数据插入数据库
		Map<Long, String> columnIdMap = addTableColumn(collectMap, tableIdMap);
		// 24.将列清洗参数信息column_clean表数据插入数据库
		Map<String, String> columnAndColIdMap = addColumnClean(collectMap, columnIdMap);
		// 25.将列拆分信息表column_split表数据插入数据库
		addColumnSplit(collectMap, columnAndColIdMap);
	}

	@Method(desc = "将table_storage_info表数据插入数据库",
			logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
					"2.判断map中的key值是否为table_storage_info对应表数据" +
					"3.获取表存储信息table_storage_info信息" +
					"4.遍历table_storage_info表数据" +
					"5.用新的数据库对应表信息ID替换旧的数据库对应表信息ID" +
					"6.将table_storage_info表数据循环入数据库")
	@Param(name = "collectMap", desc = "所有表数据的map的实体", range = "不能为空")
	@Param(name = "tableIdMap", desc = "存放新旧表ID的集合（key为表旧ID，value为表新ID）", range = "无限制")
	private void addTableStorageInfo(Map<String, Object> collectMap, Map<Long, String> tableIdMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为table_storage_info对应表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			if (Table_storage_info.TableName.equals(entry.getKey())) {
				Type tsiType = new TypeReference<List<Table_storage_info>>() {
				}.getType();
				// 3.获取表存储信息table_storage_info信息
				List<Table_storage_info> tsiList = JsonUtil.toObject(entry.getValue().toString(), tsiType);
				if (!tsiList.isEmpty()) {
					// 4.遍历table_storage_info表数据
					for (Table_storage_info tableStorageInfo : tsiList) {
						// 5.用新的数据库对应表信息ID替换旧的数据库对应表信息ID
						for (Map.Entry<Long, String> tableIdEntry : tableIdMap.entrySet()) {
							if (tableIdEntry.getKey().longValue() == tableStorageInfo.getTable_id().longValue()) {
								tableStorageInfo.setTable_id(tableIdEntry.getValue());
								tableStorageInfo.setStorage_id(PrimayKeyGener.getNextId());
								// 6.将table_storage_info表数据循环入数据库
								tableStorageInfo.add(Dbo.db());
							}
						}
					}
				}
			}
		}
	}

	@Method(desc = "将column_merge表数据插入数据库",
			logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
					"2.判断map中的key值是否为column_merge对应表数据" +
					"3.获取列合并信息column_merge信息" +
					"4.遍历column_merge表数据" +
					"5.用新的数据库对应表信息ID替换旧的数据库对应表信息ID" +
					"6.将column_merge表数据循环入数据库")
	@Param(name = "collectMap", desc = "所有表数据的map的实体", range = "不能为空")
	@Param(name = "tableIdMap", desc = "存放新旧表ID的集合（key为表旧ID，value为表新ID）", range = "无限制")
	private void addColumnMerge(Map<String, Object> collectMap, Map<Long, String> tableIdMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为column_merge对应表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			if (Column_merge.TableName.equals(entry.getKey())) {
				Type cmType = new TypeReference<List<Column_merge>>() {
				}.getType();
				// 3.获取列合并信息column_merge信息
				List<Column_merge> columnMergeList = JsonUtil.toObject(entry.getValue().toString(), cmType);
				if (!columnMergeList.isEmpty()) {
					// 4.遍历column_merge表数据
					for (Column_merge columnMerge : columnMergeList) {
						// 5.用新的数据库对应表信息ID替换旧的数据库对应表信息ID
						for (Map.Entry<Long, String> tableIdEntry : tableIdMap.entrySet()) {
							if (tableIdEntry.getKey().longValue() == columnMerge.getTable_id().longValue()) {
								columnMerge.setTable_id(tableIdEntry.getValue());
								columnMerge.setCol_merge_id(PrimayKeyGener.getNextId());
								// 6.将column_merge表数据循环入数据库
								columnMerge.add(Dbo.db());
							}
						}
					}
				}
			}
		}
	}

	@Method(desc = "将column_split表数据插入数据库",
			logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
					"2.判断map中的key值是否为column_split对应表数据" +
					"3.获取列拆分信息表column_split信息" +
					"4.遍历column_split表数据循" +
					"5.获取新旧字段ID与列清洗参数编号，第一个值为旧值，第二个值为新值" +
					"6.用新字段ID与列清洗参数编号替换旧字段ID与列清洗参数编号，保证关联关系不变" +
					"7.循环保存列拆分信息入数据库")
	@Param(name = "collectMap", desc = "所有表数据的map的实体", range = "不能为空")
	@Param(name = "columnAndColIdMap", desc = "新旧字段ID与列清洗参数编号的集合（key为旧的字段ID与列清洗参数编号，value为新值）",
			range = "不能为空")
	private void addColumnSplit(Map<String, Object> collectMap, Map<String, String> columnAndColIdMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为column_split对应表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			if (Column_split.TableName.equals(entry.getKey())) {
				Type csType = new TypeReference<List<Column_split>>() {
				}.getType();
				// 3.获取列拆分信息表column_split信息
				List<Column_split> columnSplitList = JsonUtil.toObject(entry.getValue().toString(), csType);
				if (!columnSplitList.isEmpty()) {
					// 4.遍历column_split表数据循
					for (Column_split columnSplit : columnSplitList) {
						for (Map.Entry<String, String> idEntry : columnAndColIdMap.entrySet()) {
							// 5.获取新旧字段ID与列清洗参数编号，第一个值为旧值，第二个值为新值
							String[] oldId = idEntry.getKey().split(",");
							String[] newId = idEntry.getValue().split(",");
							// 6.用新字段ID与列清洗参数编号替换旧字段ID与列清洗参数编号，保证关联关系不变
							if (oldId[0].equals(String.valueOf(columnSplit.getColumn_id())) &&
									oldId[1].equals(String.valueOf(columnSplit.getCol_clean_id()))) {
								columnSplit.setColumn_id(newId[0]);
								columnSplit.setCol_clean_id(newId[1]);
								columnSplit.setCol_split_id(PrimayKeyGener.getNextId());
								// 7.循环保存列拆分信息入数据库
								columnSplit.add(Dbo.db());
							}
						}
					}
				}
			}
		}
	}

	@Method(desc = "将column_clean表数据插入数据库",
			logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
					"2.判断map中的key值是否为column_clean对应表数据" +
					"3.获取列清洗参数信息column_clean信息" +
					"4.遍历column_clean表数据循" +
					"5.用新的字段ID替换旧的字段ID，保证关联关系不变" +
					"6.保存新旧字段ID与列清洗参数编号（key为旧的字段ID与列清洗参数编号，value为新值）" +
					"7.循环保存列清洗参数信息column_clean信息入数据库" +
					"8.返回新旧字段ID与列清洗参数编号的集合（key为旧的字段ID与列清洗参数编号，value为新值）")
	@Param(name = "collectMap", desc = "所有表数据的map的实体", range = "不能为空")
	@Param(name = "columnIdMap", desc = "新旧字段ID的集合（key为旧的字段ID，value为新的字段ID）", range = "无限制")
	private Map<String, String> addColumnClean(Map<String, Object> collectMap, Map<Long, String> columnIdMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		Map<String, String> columnAndColIdMap = new HashMap<>();
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为column_clean对应表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			if (Column_clean.TableName.equals(entry.getKey())) {
				Type ccType = new TypeReference<List<Column_clean>>() {
				}.getType();
				// 3.获取列清洗参数信息column_clean信息
				List<Column_clean> columnCleanList = JsonUtil.toObject(entry.getValue().toString(), ccType);
				if (!columnCleanList.isEmpty()) {
					// 4.遍历column_clean表数据循
					for (Column_clean columnClean : columnCleanList) {
						for (Map.Entry<Long, String> columnIdEntry : columnIdMap.entrySet()) {
							// 5.用新的字段ID替换旧的字段ID，保证关联关系不变
							if (columnIdEntry.getKey().longValue() == columnClean.getColumn_id().longValue()) {
								columnClean.setColumn_id(columnIdEntry.getValue());
								long colCleanId = PrimayKeyGener.getNextId();
								// 6.保存新旧字段ID与列清洗参数编号（key为旧的字段ID与列清洗参数编号，value为新值）
								columnAndColIdMap.put(columnIdEntry.getKey() + "," + columnClean.getCol_clean_id(),
										columnIdEntry.getValue() + "," + colCleanId);
								columnClean.setCol_clean_id(colCleanId);
								// 7.循环保存列清洗参数信息column_clean信息入数据库
								columnClean.add(Dbo.db());
							}
						}
					}
				}
			}
		}
		// 8.返回新旧字段ID与列清洗参数编号的集合（key为旧的字段ID与列清洗参数编号，value为新值）
		return columnAndColIdMap;
	}

	@Method(desc = "将table_column表数据插入数据库",
			logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
					"2.判断map中的key值是否为table_column对应表数据" +
					"3.获取表对应的字段table_column信息" +
					"4.遍历table_column表数据" +
					"5.用新的数据库设置ID替换旧的数据库设置ID,保证关联关系不变" +
					"6.保存新旧字段ID（key为旧的字段ID，value为新的字段ID）" +
					"7.将table_column表数据循环入数据库" +
					"8.返回新旧字段ID的集合（key为旧的字段ID，value为新的字段ID）")
	@Param(name = "collectMap", desc = "所有表数据的map的实体", range = "不能为空")
	@Param(name = "tableIdMap", desc = "存放新旧表ID的集合（key为表旧ID，value为表新ID）", range = "无限制")
	private Map<Long, String> addTableColumn(Map<String, Object> collectMap, Map<Long, String> tableIdMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		Map<Long, String> columnIdMap = new HashMap<>();
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为table_column对应表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			if (Table_column.TableName.equals(entry.getKey())) {
				Type tcnType = new TypeReference<List<Table_column>>() {
				}.getType();
				// 3.获取表对应的字段table_column信息
				List<Table_column> tableColumnList = JsonUtil.toObject(entry.getValue().toString(), tcnType);
				if (!tableColumnList.isEmpty()) {
					// 4.遍历table_column表数据
					for (Table_column tableColumn : tableColumnList) {
						for (Map.Entry<Long, String> tableIdEntry : tableIdMap.entrySet()) {
							// 5.用新的数据库设置ID替换旧的数据库设置ID，保证关联关系不变
							if (tableIdEntry.getKey().longValue() == tableColumn.getTable_id().longValue()) {
								String column_id = String.valueOf(PrimayKeyGener.getNextId());
								// 6.保存新旧字段ID（key为旧的字段ID，value为新的字段ID）
								columnIdMap.put(tableColumn.getColumn_id(), column_id);
								tableColumn.setColumn_id(column_id);
								tableColumn.setTable_id(tableIdEntry.getValue());
								// 7.将table_column表数据循环入数据库
								tableColumn.add(Dbo.db());
							}
						}
					}
				}
			}
		}
		// 8.返回新旧字段ID的集合（key为旧的字段ID，value为新的字段ID）
		return columnIdMap;
	}

	@Method(desc = "将table_clean表数据插入数据库",
			logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
					"2.判断map中的key值是否为table_column对应表数据" +
					"3.获取表对应的字段table_column信息" +
					"4.遍历table_clean表数据" +
					"5.用新的数据库对应表ID替换旧的ID,保证关联关系不变" +
					"6.循环保存表清洗参数信息入数据库")
	@Param(name = "collectMap", desc = "所有表数据的map的实体", range = "不能为空")
	@Param(name = "tableIdMap", desc = "存放新旧表ID的集合（key为表旧ID，value为表新ID）", range = "无限制")
	private void addTableClean(Map<String, Object> collectMap, Map<Long, String> tableIdMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为table_clean对应表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			if (Table_clean.TableName.equals(entry.getKey())) {
				Type tcType = new TypeReference<List<Table_clean>>() {
				}.getType();
				// 3.获取表清洗参数信息table_clean信息
				List<Table_clean> tableCleanList = JsonUtil.toObject(entry.getValue().toString(), tcType);
				if (!tableCleanList.isEmpty()) {
					// 4.遍历table_clean表数据
					for (Table_clean tableClean : tableCleanList) {
						for (Map.Entry<Long, String> tableIdEntry : tableIdMap.entrySet()) {
							// 5.用新的数据库对应表ID替换旧的ID,保证关联关系不变
							if (tableIdEntry.getKey().longValue() == tableClean.getTable_id().longValue()) {
								tableClean.setTable_id(tableIdEntry.getValue());
								tableClean.setTable_clean_id(PrimayKeyGener.getNextId());
								// 6.循环保存表清洗参数信息入数据库
								tableClean.add(Dbo.db());
							}
						}
					}
				}
			}
		}
	}

	@Method(desc = "将table_info表数据插入数据库",
			logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
					"2.判断map中的key值是否为table_info对应表数据" +
					"3.获取数据库对应的表table_info信息" +
					"4.遍历table_info表数据" +
					"5.用新的数据库设置ID替换旧的数据库设置ID，保证关联关系不变" +
					"6.将新旧表ID保存，（key为表旧ID，value为表新ID）" +
					"7.循环将数据库对应表信息存入数据库" +
					"8.返回存放新旧表ID的集合（key为表旧ID，value为表新ID）")
	@Param(name = "collectMap", desc = "所有表数据的map的实体", range = "不能为空")
	@Param(name = "databaseIdMap", desc = "存放新旧数据库设置ID（key为旧值，value为新值）的集合", range = "不能为空")
	private Map<Long, String> addTableInfo(Map<String, Object> collectMap, Map<Long, String> databaseIdMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		Map<Long, String> tableIdMap = new HashMap<>();
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为table_info对应表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			if (Table_info.TableName.equals(entry.getKey())) {
				Type tiType = new TypeReference<List<Table_info>>() {
				}.getType();
				// 3.获取数据库对应的表table_info信息
				List<Table_info> tableInfoList = JsonUtil.toObject(entry.getValue().toString(), tiType);
				// 4.遍历table_info表数据
				if (!tableInfoList.isEmpty()) {
					// 5.用新的数据库设置ID替换旧的数据库设置ID，保证关联关系不变
					for (Table_info tableInfo : tableInfoList) {
						for (Map.Entry<Long, String> databaseIdEntry : databaseIdMap.entrySet()) {
							if (databaseIdEntry.getKey().longValue() == tableInfo.getDatabase_id().longValue()) {
								tableInfo.setDatabase_id(databaseIdEntry.getValue());
								String table_id = String.valueOf(PrimayKeyGener.getNextId());
								// 6.将新旧表ID保存，（key为表旧ID，value为表新ID）
								tableIdMap.put(tableInfo.getTable_id(), table_id);
								tableInfo.setTable_id(table_id);
								// 7.循环将数据库对应表信息存入数据库
								tableInfo.add(Dbo.db());
							}
						}
					}
				}
			}
		}
		// 8.返回存放新旧表ID的集合（key为表旧ID，value为表新ID）
		return tableIdMap;
	}

	@Method(desc = "将signal_file表数据插入数据库",
			logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
					"2.判断map中的key值是否为signal_file对应表数据" +
					"3.获取信号文件入库信息signal_file信息" +
					"4.遍历signal_file表数据" +
					"5.用新的数据库设置ID替换旧的数据库设置ID，保证关联关系不变" +
					"6.将信号文件信息循环入数据库")
	@Param(name = "collectMap", desc = "所有表数据的map的实体", range = "不能为空")
	@Param(name = "databaseIdMap", desc = "存放新旧数据库设置ID（key为旧值，value为新值）的集合", range = "不能为空")
	private void addSignalFile(Map<String, Object> collectMap, Map<Long, String> databaseIdMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为signal_file对应表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			if (Signal_file.TableName.equals(entry.getKey())) {
				Type sfType = new TypeReference<List<Signal_file>>() {
				}.getType();
				// 3.获取信号文件入库信息signal_file信息
				List<Signal_file> signalFileList = JsonUtil.toObject(entry.getValue().toString(), sfType);
				if (!signalFileList.isEmpty()) {
					// 4.遍历signal_file表数据
					for (Signal_file signalFile : signalFileList) {
						for (Map.Entry<Long, String> databaseIdEntry : databaseIdMap.entrySet()) {
							// 5.用新的数据库设置ID替换旧的数据库设置ID，保证关联关系不变
							if (databaseIdEntry.getKey().longValue() == signalFile.getDatabase_id().longValue()) {
								signalFile.setDatabase_id(databaseIdEntry.getValue());
								signalFile.setSignal_id(PrimayKeyGener.getNextId());
								// 6.将信号文件信息循环入数据库
								signalFile.add(Dbo.db());
							}
						}
					}
				}
			}
		}
	}

	@Method(desc = "将file_source表数据插入数据库",
			logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
					"2.判断map中的key值是否为file_source对应表数据" +
					"3.获取文件源设置file_source信息" +
					"4.将file_source表数据循环入数据库" +
					"5.获取存放新旧agent与文件采集系统ID,第一个值为agent与文件采集系统旧ID，第二个为新ID" +
					"6.用新的agent与文件采集系统ID替换旧的agent与文件采集系统ID，保证关联关系不变" +
					"7.循环保存文件源设置信息入数据库")
	@Param(name = "collectMap", desc = "所有表数据的map的实体", range = "不能为空")
	@Param(name = "agentAndFcsIdMap", desc = "存放新旧agent与文件采集系统ID（key为agent与文件采集系统旧ID，value为新ID）的集合",
			range = "不能为空")
	private void addFileSource(Map<String, Object> collectMap, Map<String, String> agentAndFcsIdMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为file_source对应表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			if (File_source.TableName.equals(entry.getKey())) {
				Type fsType = new TypeReference<List<File_source>>() {
				}.getType();
				// 3.获取文件源设置file_source信息
				List<File_source> fileSourceList = JsonUtil.toObject(entry.getValue().toString(), fsType);
				if (!fileSourceList.isEmpty()) {
					// 4.遍历file_source表数据
					for (File_source fileSource : fileSourceList) {
						for (Map.Entry<String, String> idEntry : agentAndFcsIdMap.entrySet()) {
							// 5.获取存放新旧agent与文件采集系统ID,第一个值为agent与文件采集系统旧ID，第二个为新ID
							String[] oldId = idEntry.getKey().split(",");
							String[] newId = idEntry.getValue().split(",");
							// 6.用新的agent与文件采集系统ID替换旧的agent与文件采集系统ID，保证关联关系不变
							if (oldId[0].equals(String.valueOf(fileSource.getAgent_id())) &&
									oldId[1].equals(String.valueOf(fileSource.getFcs_id()))) {
								fileSource.setAgent_id(newId[0]);
								fileSource.setFcs_id(newId[1]);
								fileSource.setFile_source_id(PrimayKeyGener.getNextId());
								// 7.循环保存文件源设置信息入数据库
								fileSource.add(Dbo.db());
							}
						}
					}
				}
			}
		}
	}

	@Method(desc = "将file_collect_set表数据插入数据库",
			logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
					"2.判断map中的key值是否为file_collect_set对应表数据" +
					"3.获取文件系统设置file_collect_set信息" +
					"4.遍历file_collect_set表数" +
					"5.用新的agent ID替换旧的agent ID，保证关联关系不变" +
					"6.将新旧agent与文件采集系统ID（key为agent与文件采集系统旧ID，value为新ID）保存" +
					"7.循环保存文件系统设置表信息入数据库" +
					"8.返回存放新旧agent与文件采集系统ID（key为agent与文件采集系统旧ID，value为新ID）的集合")
	@Param(name = "collectMap", desc = "所有表数据的map的实体", range = "不能为空")
	@Param(name = "agentIdMap", desc = "存放新旧agent ID的集合", range = "不能为空")
	private Map<String, String> addFileCollectSet(Map<String, Object> collectMap, Map<Long, String> agentIdMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		Map<String, String> agentAndFcsIdMap = new HashMap<>();
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为file_collect_set对应表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			if (File_collect_set.TableName.equals(entry.getKey())) {
				Type fcsType = new TypeReference<List<File_collect_set>>() {
				}.getType();
				// 3.获取文件系统设置file_collect_set信息
				List<File_collect_set> fcsList = JsonUtil.toObject(entry.getValue().toString(), fcsType);
				if (!fcsList.isEmpty()) {
					// 4.遍历file_collect_set表数据
					for (File_collect_set file_collect_set : fcsList) {
						for (Map.Entry<Long, String> agentIdEntry : agentIdMap.entrySet()) {
							// 5.用新的agent ID替换旧的agent ID，保证关联关系不变
							if (agentIdEntry.getKey().longValue() == file_collect_set.getAgent_id().longValue()) {
								long fcs_id = PrimayKeyGener.getNextId();
								// 6.将新旧agent与文件采集系统ID（key为agent与文件采集系统旧ID，value为新ID）保存
								agentAndFcsIdMap.put(agentIdEntry.getKey() + "," + file_collect_set.getFcs_id(),
										agentIdEntry.getValue() + "," + fcs_id);
								file_collect_set.setFcs_id(fcs_id);
								file_collect_set.setAgent_id(agentIdEntry.getValue());
								// 7.循环保存文件系统设置表信息入数据库
								file_collect_set.add(Dbo.db());
							}
						}
					}
				}
			}
		}

		// 8.返回存放新旧agent与文件采集系统ID（key为agent与文件采集系统旧ID，value为新ID）的集合
		return agentAndFcsIdMap;
	}

	@Method(desc = "将database_set表数据插入数据库",
			logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
					"2.判断map中的key值是否为database_set对应表数据" +
					"3.获取数据库设置database_set信息" +
					"4.遍历database_set表数据" +
					"5.获取存放新旧agent与分类ID的数组，第一个值是agent与分类旧值，第二个值是agent与分类新值" +
					"6.用新agent与分类ID替换旧agent与分类ID" +
					"7.将新旧数据库设置ID（key为旧值，value为新值）保存" +
					"8.循环保存数据库设置表信息" +
					"9.返回存放新旧数据库设置ID（key为旧值，value为新值）的集合")
	@Param(name = "collectMap", desc = "所有表数据的map的实体", range = "不能为空")
	@Param(name = "classifyAndAgentId", desc = "新旧agent与分类ID（key为agent与分类旧ID，value为agent与分类新ID）的集合",
			range = "不能为空")
	@Return(desc = "存放新旧数据库设置ID（key为旧值，value为新值）的集合", range = "无限制")
	private Map<Long, String> addDatabaseSet(Map<String, Object> collectMap, Map<String, String> classifyAndAgentId) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		Map<Long, String> databaseIdMap = new HashMap<>();
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为database_set对应表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			if (Database_set.TableName.equals(entry.getKey())) {
				Type dsType = new TypeReference<List<Database_set>>() {
				}.getType();
				// 3.获取数据库设置database_set信息
				List<Database_set> databaseSetList = JsonUtil.toObject(entry.getValue().toString(), dsType);
				if (!databaseSetList.isEmpty()) {
					// 4.遍历database_set表数据
					for (Database_set database_set : databaseSetList) {
						for (Map.Entry<String, String> idEntry : classifyAndAgentId.entrySet()) {
							// 5.获取存放新旧agent与分类ID的数组，第一个值是agent与分类旧值，第二个值是agent与分类新值
							String[] oldId = idEntry.getKey().split(",");
							String[] newId = idEntry.getValue().split(",");
							// 6.用新agent与分类ID替换旧agent与分类ID
							if (oldId[0].equals(String.valueOf(database_set.getAgent_id()))
									&& oldId[1].equals(String.valueOf(database_set.getClassify_id()))) {
								database_set.setAgent_id(newId[0]);
								database_set.setClassify_id(newId[1]);
								String database_id = String.valueOf(PrimayKeyGener.getNextId());
								// 7.将新旧数据库设置ID（key为旧值，value为新值）保存
								databaseIdMap.put(database_set.getDatabase_id(), database_id);
								database_set.setDatabase_id(database_id);
								// 8.循环保存数据库设置表信息
								database_set.add(Dbo.db());
							}
						}
					}
				}
			}
		}
		// 9.返回存放新旧数据库设置ID（key为旧值，value为新值）的集合
		return databaseIdMap;
	}

	@Method(desc = "将object_collect_struct表数据插入数据库",
			logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
					"2.判断map中的key值是否为object_collect_struct对应表数据" +
					"3.获取对象采集结构信息object_collect_struct信息" +
					"4.遍历object_collect_struct表数据" +
					"5.用新的对象采集任务编号替换新的对象采集任务编号,保证关联关系不变" +
					"6.将object_collect_struct表数据循环入数据库")
	@Param(name = "collectMap", desc = "所有表数据的map的实体", range = "不能为空")
	@Param(name = "ocsIdMap", desc = "存放新旧对象采集任务编号(key为旧的ID，value为新的ID）的集合", range = "不能为空")
	private void addObjectCollectStruct(Map<String, Object> collectMap, Map<Long, String> ocsIdMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为object_collect_struct对应表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			if (Object_collect_struct.TableName.equals(entry.getKey())) {
				Type ocsType = new TypeReference<List<Object_collect_struct>>() {
				}.getType();
				// 3.获取对象采集结构信息object_collect_struct信息
				List<Object_collect_struct> ocsList = JsonUtil.toObject(entry.getValue().toString(), ocsType);
				if (!ocsList.isEmpty()) {
					// 4.遍历object_collect_struct表数据
					for (Object_collect_struct object_collect_struct : ocsList) {
						for (Map.Entry<Long, String> ocsIdEntry : ocsIdMap.entrySet()) {
							// 5.用新的对象采集任务编号替换新的对象采集任务编号,保证关联关系不变
							if (ocsIdEntry.getKey().longValue() == object_collect_struct.getOcs_id().longValue()) {
								object_collect_struct.setStruct_id(PrimayKeyGener.getNextId());
								object_collect_struct.setOcs_id(ocsIdEntry.getValue());
								// 6.将object_collect_struct表数据循环入数据库
								object_collect_struct.add(Dbo.db());
							}
						}
					}
				}
			}
		}
	}

	@Method(desc = "将object_collect_task表数据插入数据库",
			logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
					"2.判断map中的key值是否为object_collect_task对应表数据" +
					"3.获取对象采集对应信息object_collect_task信息" +
					"4.遍历object_collect_task表数据" +
					"5.获取新旧对象采集与agent ID的数组,第一个值为对象采集ID，第二个值为agent ID" +
					"6.用新的对象采集与agent ID替换旧的对象采集与agent ID,保证关联关系不变" +
					"7.将新旧对象采集任务编号保存" +
					"8.循环保存对象采集任务信息入数据库" +
					"9.返回存放新旧对象采集任务编号(key为旧的ID，value为新的ID）的集合")
	@Param(name = "collectMap", desc = "所有表数据的map的实体", range = "不能为空")
	@Param(name = "odcMap", desc = "存放新旧对象采集任务编号(key为旧的ID，value为新的ID）的集合", range = "无限制")
	@Return(desc = "返回存放新旧对象采集任务编号(key为旧的ID，value为新的ID）的集合", range = "无限制")
	private Map<Long, String> addObjectCollectTask(Map<String, Object> collectMap, Map<String, String> odcMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		Map<Long, String> ocsIdMap = new HashMap<>();
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为object_collect_task对应表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			if (Object_collect_task.TableName.equals(entry.getKey())) {
				Type octType = new TypeReference<List<Object_collect_task>>() {
				}.getType();
				// 3.获取对象采集对应信息object_collect_task信息
				List<Object_collect_task> octList = JsonUtil.toObject(entry.getValue().toString(), octType);
				if (!octList.isEmpty()) {
					// 4.遍历object_collect_task表数据
					for (Object_collect_task octTask : octList) {
						for (Map.Entry<String, String> odcEntry : odcMap.entrySet()) {
							// 5.获取新旧对象采集与agent ID的数组,第一个值为对象采集ID，第二个值为agent ID
							String[] oldOdcAndAgentId = odcEntry.getKey().split(",");
							String[] newOdcAndAgentId = odcEntry.getValue().split(",");
							// 6.用新的对象采集与agent ID替换旧的对象采集与agent ID
							if (oldOdcAndAgentId[0].equals(String.valueOf(octTask.getOdc_id())) &&
									oldOdcAndAgentId[1].equals(String.valueOf(octTask.getAgent_id()))) {
								String ocs_id = String.valueOf(PrimayKeyGener.getNextId());
								// 7.将新旧对象采集任务编号保存
								ocsIdMap.put(octTask.getOcs_id(), ocs_id);
								octTask.setOcs_id(ocs_id);
								octTask.setAgent_id(newOdcAndAgentId[1]);
								octTask.setOdc_id(newOdcAndAgentId[0]);
								// 8.循环保存对象采集任务信息入数据库
								octTask.add(Dbo.db());
							}
						}
					}
				}
			}
		}
		// 9.返回存放新旧对象采集任务编号(key为旧的ID，value为新的ID）的集合
		return ocsIdMap;
	}

	@Method(desc = "将object_collect表数据插入数据库",
			logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
					"2.判断map中的key值是否为object_collect对应表数据" +
					"3.获取对象采集设置object_collect信息" +
					"4.将object_collect表数据循环入数据库" +
					"5.用新的agent ID替换旧的agent ID,,保证关联关系不变" +
					"6.存放新旧对象采集ID（key为旧的ID，value为新的采集ID）的集合" +
					"7.object_collect表数据循环入数据库" +
					"8.返回存放新旧对象采集ID（key为旧的ID，value为新的采集ID）的集合")
	@Param(name = "collectMap", desc = "所有表数据的map的实体", range = "不能为空")
	@Param(name = "agentIdMap", desc = "存放新旧agnet ID（新的ID作为value，旧的agent ID作为key）的集合",
			range = "无限制")
	@Return(desc = "返回存放新旧对象采集与agent ID（key为旧的ID，value为新的ID）的集合", range = "无限制")
	private Map<String, String> addObjectCollect(Map<String, Object> collectMap, Map<Long, String> agentIdMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		Map<String, String> odcMap = new HashMap<>();
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为object_collect对应表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			if (Object_collect.TableName.equals(entry.getKey())) {
				Type ocType = new TypeReference<List<Object_collect>>() {
				}.getType();
				// 3.获取对象采集设置object_collect信息
				List<Object_collect> objCollectList = JsonUtil.toObject(entry.getValue().toString(), ocType);
				if (!objCollectList.isEmpty()) {
					// 4 .遍历object_collect表数据
					for (Object_collect objCollect : objCollectList) {
						for (Map.Entry<Long, String> agentIdEntry : agentIdMap.entrySet()) {
							// 5.用新的agent ID替换旧的agent ID,,保证关联关系不变
							if (agentIdEntry.getKey().longValue() == objCollect.getAgent_id().longValue()) {
								long odc_id = PrimayKeyGener.getNextId();
								// 6.存放新旧对象采集ID（key为旧的ID，value为新的采集ID）的集合
								odcMap.put(objCollect.getOdc_id() + "," + agentIdEntry.getKey(),
										odc_id + "," + agentIdEntry.getValue());
								objCollect.setOdc_id(odc_id);
								objCollect.setAgent_id(agentIdEntry.getValue());
								// 7.object_collect表数据循环入数据库
								objCollect.add(Dbo.db());
							}
						}
					}
				}
			}
		}
		// 8.返回存放新旧对象采集ID（key为旧的ID，value为新的采集ID）的集合
		return odcMap;
	}

	@Method(desc = "将ftp_transfered表数据插入数据库",
			logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
					"2.判断map中的key值是否为ftp_transfered对应表数据" +
					"3.获取tp采集设置ftp_transfered信息" +
					"4.遍历ftp_transfered表数据集合" +
					"5.用重新生成的ftp_id替换旧的ftp_id,保证关联关系不变" +
					"6.将ftp_transfered表数据循环入数据库")
	@Param(name = "collectMap", desc = "所有表数据的map的实体", range = "不能为空")
	@Param(name = "ftpIdMap", desc = "存放重新生成的ftp采集ID（作为value）,旧的ftp采集ID（作为key）的集合",
			range = "不能为空")
	private void addFtpTransfered(Map<String, Object> collectMap, Map<Long, String> ftpIdMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为ftp_transfered对应表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			if (Ftp_transfered.TableName.equals(entry.getKey())) {
				Type ftType = new TypeReference<List<Ftp_transfered>>() {
				}.getType();
				// 3.获取ftp已传输表ftp_transfered信息
				List<Ftp_transfered> transferList = JsonUtil.toObject(entry.getValue().toString(), ftType);
				// 4.遍历ftp_transfered表数据
				if (!transferList.isEmpty()) {
					for (Ftp_transfered ftp_transfered : transferList) {
						for (Map.Entry<Long, String> ftpIdEntry : ftpIdMap.entrySet()) {
							// 5.用重新生成的ftp_id替换旧的ftp_id,保证关联关系不变
							if (ftpIdEntry.getKey().longValue() == ftp_transfered.getFtp_id().longValue()) {
								ftp_transfered.setFtp_transfered_id(String.valueOf(PrimayKeyGener.getNextId()));
								ftp_transfered.setFtp_id(ftpIdEntry.getValue());
								// 6.将ftp_transfered表数据循环入数据库
								ftp_transfered.add(Dbo.db());
							}
						}
					}
				}
			}
		}
	}

	@Method(desc = "将ftp_collect表数据插入数据库",
			logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
					"2.判断map中的key值是否为ftp_collect对应表数据" +
					"3.获取tp采集设置ftp_collect信息" +
					"4.遍历ftp_collect表数据集合" +
					"5.用重新生成的agent ID替换旧的agent ID，保证关联关系不变" +
					"6.将重新生成的ftp采集ID作为value,旧的ftp采集ID作为key保存" +
					"7.将ftp_collect表数据循环入数据库" +
					"8.返回存放将重新生成的ftp采集ID（作为value）,旧的ftp采集ID（作为key）的集合")
	@Param(name = "collectMap", desc = "所有表数据的map的实体", range = "不能为空")
	@Param(name = "agentIdMap", desc = "存放重新生成的agnet ID（作为value），旧的agent ID（作为key）的集合",
			range = "无限制")
	@Return(desc = "8.返回存放将重新生成的ftp采集ID（作为value）,旧的ftp采集ID（作为key）的集合", range = "无限制")
	private Map<Long, String> addFtpCollect(Map<String, Object> collectMap, Map<Long, String> agentIdMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		Map<Long, String> ftpIdMap = new HashMap<>();
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为ftp_collect对应表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			if (Ftp_collect.TableName.equals(entry.getKey())) {
				Type fcType = new TypeReference<List<Ftp_collect>>() {
				}.getType();
				// 3.获取tp采集设置ftp_collect信息集合
				List<Ftp_collect> ftpCollectList = JsonUtil.toObject(entry.getValue().toString(), fcType);
				if (!ftpCollectList.isEmpty()) {
					// 4.遍历ftp_collect表数据
					for (Ftp_collect ftp_collect : ftpCollectList) {
						// 5.用重新生成的agent ID替换旧的agent ID
						for (Map.Entry<Long, String> agentIdEntry : agentIdMap.entrySet()) {
							if (agentIdEntry.getKey().longValue() == ftp_collect.getAgent_id().longValue()) {
								String ftp_id = String.valueOf(PrimayKeyGener.getNextId());
								// 6.将重新生成的ftp采集ID作为value,旧的ftp采集ID作为key保存
								ftpIdMap.put(ftp_collect.getFtp_id(), ftp_id);
								ftp_collect.setFtp_id(ftp_id);
								ftp_collect.setAgent_id(agentIdEntry.getValue());
								// 7.ftp_collect表数据循环入数据库
								ftp_collect.add(Dbo.db());
							}
						}
					}
				}
			}
		}
		// 8.返回存放将重新生成的ftp采集ID（作为value）,旧的ftp采集ID（作为key）的集合
		return ftpIdMap;
	}

	@Method(desc = "将agent_down_info表数据插入数据库",
			logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
					"2.判断map中的key值是否为agent_down_info对应表数据" +
					"3.获取agent_down_info表数据" +
					"4.遍历agent_down_info表数据集合" +
					"5.用重新生成的agent ID替换旧的agent ID，保证关联关系不变" +
					"6.将agent_down_info表数据循环入数据库")
	@Param(name = "agent_ip", desc = "agent地址", range = "不能为空，服务器ip地址", example = "127.0.0.1")
	@Param(name = "agent_port", desc = "agent端口", range = "1024-65535")
	@Param(name = "userCollectId", desc = "数据采集用户，代表此数据属于哪个用户", range = "新增用户时生成")
	@Param(name = "source_id", desc = "导入数据源重新生成的数据源ID", range = "无限制")
	@Param(name = "collectMap", desc = "所有表数据的map的实体", range = "无限制")
	@Param(name = "agentIdMap", desc = "存放新旧agentID的集合", range = "无限制")
	private void addAgentDownInfo(String agent_ip, String agent_port, long userCollectId,
	                              Map<String, Object> collectMap, Map<Long, String> agentIdMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为agent_down_info对应表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			if (Agent_down_info.TableName.equals(entry.getKey())) {
				Type adiType = new TypeReference<List<Agent_down_info>>() {
				}.getType();
				// 3.获取agent_down_info表数据
				List<Agent_down_info> adiList = JsonUtil.toObject(entry.getValue().toString(), adiType);
				if (!adiList.isEmpty()) {
					// 4.遍历agent_down_info表数据集合
					for (Agent_down_info agent_down_info : adiList) {
						// 5.用重新生成的agent ID替换旧的agent ID
						for (Map.Entry<Long, String> agentIdEntry : agentIdMap.entrySet()) {
							if (agent_down_info.getAgent_id().longValue() == agentIdEntry.getKey().longValue()) {
								agent_down_info.setDown_id(String.valueOf(PrimayKeyGener.getNextId()));
								agent_down_info.setUser_id(userCollectId);
								agent_down_info.setAgent_ip(agent_ip);
								agent_down_info.setAgent_port(agent_port);
								agent_down_info.setAgent_id(agentIdEntry.getValue());
								// 6.将agent_down_info表数据循环入数据库
								agent_down_info.add(Dbo.db());
							}
						}
					}
				}
			}
		}
	}

	@Method(desc = "将collect_job_classify表数据插入数据库",
			logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
					"2.判断map中的key值是否为collect_job_classify对应表数据" +
					"3.获取采集任务分类表collect_job_classify信息" +
					"4.遍历collect_job_classify表数据" +
					"5.用重新生成的agent ID替换旧的agent ID，保证关联关系不变" +
					"6.将新旧agent与分类ID保存（key为agent与分类旧ID，value为agent与分类新ID）" +
					"7.将collect_job_classify表数据循环入数据库" +
					"8.返回存放新旧分类与agent ID的集合")
	@Param(name = "userCollectId", desc = "采集用户ID", range = "不能为空")
	@Param(name = "collectMap", desc = "所有表数据的map的实体", range = "无限制")
	@Param(name = "agentIdMap", desc = "存放新旧agentID的集合", range = "无限制")
	private Map<String, String> addCollectJobClassify(long userCollectId, Map<String, Object> collectMap,
	                                                  Map<Long, String> agentIdMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		Map<String, String> classifyAndAgentIdMap = new HashMap<>();
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为collect_job_classify对应表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			if (Collect_job_classify.TableName.equals(entry.getKey())) {
				Type cjcType = new TypeReference<List<Collect_job_classify>>() {
				}.getType();
				// 3.获取采集任务分类表collect_job_classify信息
				List<Collect_job_classify> cjcList = JsonUtil.toObject(entry.getValue().toString(), cjcType);
				if (!cjcList.isEmpty()) {
					// 4.将collect_job_classify表数据循环入数据库
					for (Collect_job_classify classify : cjcList) {
						// 5.用重新生成的agent ID替换旧的agent ID
						for (Map.Entry<Long, String> agentIdEntry : agentIdMap.entrySet()) {
							if (classify.getAgent_id().longValue() == agentIdEntry.getKey().longValue()) {
								long classify_id = PrimayKeyGener.getNextId();
								// 6.将新旧agent与分类ID保存（key为agent与分类旧ID，value为agent与分类新ID）
								classifyAndAgentIdMap.put(agentIdEntry.getKey() + "," +
										classify.getClassify_id(), agentIdEntry.getValue() + "," + classify_id);
								classify.setClassify_id(classify_id);
								classify.setUser_id(userCollectId);
								classify.setAgent_id(agentIdEntry.getValue());
								// 7.将collect_job_classify表数据循环入数据库
								classify.add(Dbo.db());
							}
						}
					}
				}
			}
		}

		// 8.返回存放新旧分类与agent ID的集合
		return classifyAndAgentIdMap;
	}

	@Method(desc = "将department_info表数据插入数据库",
			logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
					"2.创建新的department_info表数据集合" +
					"3.判断map中的key值是否为对应department_info表数据" +
					"4.获取部门表department_info表数据" +
					"5.获取部门信息" +
					"6.判断部门是否已存在，如果不存在则添加部门，存在就获取部门ID作业新的部门ID" +
					"6.1相同，获取现在的dep_id作为新的部门ID,保存新旧部门主键ID" +
					"6.2.不相同，将新旧主键ID保存，新增department_info表数据入数据库" +
					"7.返回新的新旧部门主键ID集合")
	@Param(name = "collectMap", desc = "所有表数据的map的实体", range = "无限制")
	@Return(desc = "返回新的新旧部门主键ID集合", range = "不为空")
	private Map<Long, String> addDepartmentInfo(Map<String, Object> collectMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		Map<Long, String> depMap = new HashMap<>();
		// 2.创建新的department_info表数据集合
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 3.判断map中的key值是否为对应department_info表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			if (Department_info.TableName.equals(entry.getKey())) {
				Type srdType = new TypeReference<List<Department_info>>() {
				}.getType();
				// 4.获取部门表department_info表数据
				List<Department_info> depInfoList = JsonUtil.toObject(entry.getValue().toString(), srdType);
				if (!depInfoList.isEmpty()) {
					for (Department_info department_info : depInfoList) {
						// 5.获取部门信息
						Map<String, Object> depInfo = Dbo.queryOneObject("select dep_id,dep_name from "
								+ Department_info.TableName + " where dep_name=?", department_info.getDep_name());
						// 6.判断部门是否已存在，如果不存在则添加部门，存在就获取部门ID作业新的部门ID
						if (depInfo != null && !depInfo.isEmpty()) {
							// 6.1相同，获取现在的dep_id作为新的部门ID,保存新旧部门主键ID
							depMap.put(department_info.getDep_id(), depInfo.get("dep_id").toString());
						} else {
							String dep_id = String.valueOf(PrimayKeyGener.getNextId());
							// 6.2.不相同，将新旧主键ID保存，新增department_info表数据入数据库
							depMap.put(department_info.getDep_id(), dep_id);
							department_info.setDep_id(dep_id);
							department_info.add(Dbo.db());
						}
					}
				}
			}
		}
		// 7.返回新的新旧部门主键ID集合
		return depMap;
	}

	@Method(desc = "将source_relation_dep表数据插入数据库",
			logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
					"2.判断map中的key值是否为对应source_relation_dep表数据" +
					"3.获取数据源和部门关系表source_relation_dep表数据" +
					"4.遍历department_info表新旧ID" +
					"5.判断部门是否已存在" +
					"5.1部门不存，用重新生成的部门ID替换旧的部门ID，新增source_relation_dep表数据" +
					"5.2部门已存在，因为数据源ID发生了编号，所以替换数据源ID，新增数据源与部门关系")
	@Param(name = "collectMap", desc = "所有表数据的map的实体", range = "无限制")
	@Param(name = "source_id", desc = "导入数据源重新生成的数据源ID", range = "无限制")
	@Param(name = "departmentInfo", desc = "存放department_info表新旧部门ID数据的集合", range = "无限制")
	private void addSourceRelationDep(Map<String, Object> collectMap, String source_id,
	                                  Map<Long, String> departmentInfo) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为对应source_relation_dep表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			if (Source_relation_dep.TableName.equals(entry.getKey())) {
				Type srdType = new TypeReference<List<Source_relation_dep>>() {
				}.getType();
				// 3.获取数据源和部门关系表source_relation_dep表数据
				List<Source_relation_dep> srdList = JsonUtil.toObject(entry.getValue().toString(), srdType);
				if (!srdList.isEmpty()) {
					for (Source_relation_dep source_relation_dep : srdList) {
						source_relation_dep.setSource_id(source_id);
						// 4.遍历department_info表新旧ID
						for (Map.Entry<Long, String> depIdEntry : departmentInfo.entrySet()) {
							// 5.判断部门是否已存在，如果
							if (depIdEntry.getKey().longValue() == source_relation_dep.getDep_id().longValue()) {
								source_relation_dep.setDep_id(depIdEntry.getValue());
								// 5.1部门不存，用重新生成的部门ID替换旧的部门ID，新增source_relation_dep表数据
								source_relation_dep.add(Dbo.db());
							}
						}
					}
				}
			}
		}
	}

	@Method(desc = "将data_source表数据入库并返回data_source实体对象",
			logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
					"2.导入数据源需要重新生成主键ID，获取新的数据源ID" +
					"3.判断map中的key值是否为对应data_source表数据" +
					"4.获取数据源data_source表信息" +
					"5.判断数据源编号是否已存在" +
					"6.将data_source表数据插入数据库" +
					"7.返回data_source表数据对应实体对象")
	@Param(name = "userId", desc = "创建用户ID，代表此数据源由哪个用户创建", range = "不为空，创建用户时自动生成")
	@Param(name = "collectMap", desc = "所有表数据的map的实体", range = "无限制")
	@Return(desc = "返回重新生成的数据源ID", range = "无限制", isBean = true)
	private String getDataSource(long userId, Map<String, Object> collectMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		// 2.导入数据源需要重新生成主键ID，获取新的数据源ID
		String source_id = String.valueOf(PrimayKeyGener.getNextId());
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 3.判断map中的key值是否为对应data_source表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			if (Data_source.TableName.equals(entry.getKey())) {
				// 4.获取数据源data_source表信息
				Data_source data_source = JsonUtil.toObjectSafety(entry.getValue().toString(),
						Data_source.class).orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
				// 5.判断数据源编号是否已存在
				isExistDataSourceNumber(data_source.getDatasource_number());
				// 6.将data_source表数据插入数据库
				data_source.setSource_id(source_id);
				data_source.setCreate_user_id(userId);
				data_source.add(Dbo.db());
			}
		}
		// 7.返回存放新旧数据源ID的集合
		return source_id;
	}

	@Method(desc = "将agent_info表信息入数据库",
			logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
					"2.判断map中的key值是否为对应agent_info表数据" +
					"3.获取agent_info表数据" +
					"4.循环入库agent_info" +
					"5.保存新旧agent ID（key为旧的agent ID，value为新值）" +
					"6.循环保存agent信息" +
					"7.返回存放新旧agent_id的集合")
	@Param(name = "agent_ip", desc = "agent地址", range = "不为空，服务器ip地址", example = "127.0.0.1")
	@Param(name = "agent_port", desc = "agent端口", range = "不为空，1024-65535")
	@Param(name = "userCollectId", desc = "数据采集用户，代表此数据属于哪个用户", range = "4位数字，不为空,页面传值")
	@Param(name = "source_id", desc = "导入数据源重新生成的数据源ID", range = "无限制")
	@Param(name = "collectMap", desc = "所有表数据的map的实体", range = "无限制")
	private Map<Long, String> addAgentInfo(String agent_ip, String agent_port, long userCollectId,
	                                       String source_id, Map<String, Object> collectMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		Map<Long, String> agentIdMap = new HashMap<>();
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为对应agent_info表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			if (Agent_info.TableName.equals(entry.getKey())) {
				// 获取agent信息表信息
				Type aiType = new TypeReference<List<Agent_info>>() {
				}.getType();
				// 3.获取agent_info表数据
				List<Agent_info> agentInfoList = JsonUtil.toObject(entry.getValue().toString(), aiType);
				// 4.判断结果集是否为空，不为空循环入库agent_info
				if (!agentInfoList.isEmpty()) {
					for (Agent_info agent_info : agentInfoList) {
						String agent_id = String.valueOf(PrimayKeyGener.getNextId());
						// 5.保存新旧agent ID（key为旧的agent ID，value为新值）
						agentIdMap.put(agent_info.getAgent_id(), agent_id);
						agent_info.setAgent_id(agent_id);
						agent_info.setSource_id(source_id);
						agent_info.setUser_id(userCollectId);
						agent_info.setAgent_ip(agent_ip);
						agent_info.setAgent_port(agent_port);
						// 6.循环入库agent信息
						agent_info.add(Dbo.db());
					}
				}
			}
		}
		// 7.返回存放新旧agent_id的集合
		return agentIdMap;
	}

	@Method(desc = "下载文件（数据源下载功能使用，下载数据源给数据源导入提供上传文件）",
			logicStep = "1.数据可访问权限处理方式，这里是下载数据源，所以不需要数据权限验证" +
					"2.创建存放所有数据源下载，所有相关表数据库查询获取数据的map集合" +
					"3.获取data_source表信息集合，将data_source表信息封装入map" +
					"4.获取source_relation_dep表信息集合，将source_relation_dep表数据封装入map" +
					"5.获取department_info表信息集合，将department_info表数据封装入map" +
					"6.获取agent_info表信息集合，将agent_info表信息封装入map" +
					"7.获取Agent_down_info表信息集合封装入map" +
					"8.采集任务分类表collect_job_classify，获取collect_job_classify表信息集合入map" +
					"9.ftp采集设置ftp_collect,获取ftp_collect表信息集合" +
					"10.ftp已传输表ftp_transfered,获取ftp_transfered表信息集合入map" +
					"11.对象采集设置object_collect,获取object_collect表信息集合入map" +
					"12.对象采集对应信息object_collect_task,获取object_collect_task表信息集合入map" +
					"13.对象采集存储设置object_storage,获取object_storage表信息集合入map" +
					"14.对象采集结构信息object_collect_struct,获取object_collect_struct表信息集合入map" +
					"15.数据库设置database_set,获取database_set表信息集合入map" +
					"16.文件系统设置file_collect_set,获取file_collect_set表信息集合入map" +
					"17.文件源设置file_source,获取file_source表信息集合入map" +
					"18.信号文件入库信息signal_file,获取signal_file表信息集合入map" +
					"19.数据库对应的表table_info,获取table_info表信息集合入map" +
					"20.列合并信息表column_merge,获取column_merge表信息集合入map" +
					"21.表存储信息table_storage_info,获取table_storage_info表信息集合入map" +
					"22.表清洗参数信息table_clean,获取table_clean表信息集合入map" +
					"23.表对应的字段table_column,获取table_column表信息集合入map" +
					"24.列清洗参数信息column_clean,获取column_clean表信息集合入map" +
					"25.列拆分信息表column_split,获取column_split表信息集合入map" +
					"26.使用base64编码" +
					"27.判断文件是否存在" +
					"28.清空response，设置响应头，响应编码格式，控制浏览器下载该文件" +
					"29.通过流的方式写入文件")
	@Param(name = "source_id", desc = "data_source表主键", range = "不为空以及不为空格，10位数字，新增数据源时生成")
	public void downloadFile(long source_id) {
		// 1.数据可访问权限处理方式，这里是下载数据源，所以不需要数据权限验证
		HttpServletResponse response = ResponseUtil.getResponse();
		try {
			// 2.创建存放所有数据源下载，所有相关表数据库查询获取数据的map集合
			Map<String, Object> collectionMap = new HashMap<>();
			// 3.获取data_source表信息集合，将data_source表信息封装入map
			addDataSourceToMap(source_id, collectionMap);
			// 4.获取source_relation_dep表信息集合，将source_relation_dep表数据封装入map
			List<Source_relation_dep> sourceRelationDepList = addSourceRelationDepToMap(source_id,
					collectionMap);
			// 5.获取department_info表信息集合，将department_info表数据封装入map
			addDepartmentInfoToMap(collectionMap, sourceRelationDepList);
			// 6.获取agent_info表信息集合，将agent_info表信息封装入map
			List<Agent_info> agentInfoList = getAgentInfoList(source_id, collectionMap);
			// 7.获取Agent_down_info表信息集合封装入map
			addAgentDownInfoToMap(collectionMap, agentInfoList);
			// 8.采集任务分类表collect_job_classify，获取collect_job_classify表信息集合入map
			addCollectJobClassifyToMap(collectionMap, agentInfoList);
			// 9.ftp采集设置ftp_collect,获取ftp_collect表信息集合
			Result ftpCollectResult = getFtpCollectResult(collectionMap, agentInfoList);
			// 10.ftp已传输表ftp_transfered,获取ftp_transfered表信息集合入map
			addFtpTransferedToMap(collectionMap, ftpCollectResult);
			// 11.对象采集设置object_collect,获取object_collect表信息集合入map
			Result objectCollectResult = getObjectCollectResult(collectionMap, agentInfoList);
			// 12.对象采集对应信息object_collect_task,获取object_collect_task表信息集合入map
			Result objectCollectTaskResult = getObjectCollectTaskResult(collectionMap, objectCollectResult);
			// 14.对象采集结构信息object_collect_struct,获取object_collect_struct表信息集合入map
			addObjectCollectStructResultToMap(collectionMap, objectCollectTaskResult);
			// 15.数据库设置database_set,获取database_set表信息集合入map
			Result databaseSetResult = getDatabaseSetResult(collectionMap, agentInfoList);
			// 16.文件系统设置file_collect_set,获取file_collect_set表信息集合入map
			Result fileCollectSetResult = getFileCollectSetResult(collectionMap, agentInfoList);
			// 17.文件源设置file_source,获取file_source表信息集合入map
			addFileSourceToMap(collectionMap, fileCollectSetResult);
			// 18.信号文件入库信息signal_file,获取signal_file表信息集合入map
			addSignalFileToMap(collectionMap, databaseSetResult);
			// 19.数据库对应的表table_info,获取table_info表信息集合入map
			Result tableInfoResult = getTableInfoResult(collectionMap, databaseSetResult);
			// 20.列合并信息表column_merge,获取column_merge表信息集合入map
			addColumnMergeToMap(collectionMap, tableInfoResult);
			// 21.表存储信息table_storage_info,获取table_storage_info表信息集合入map
			addTableStorageInfoToMap(collectionMap, tableInfoResult);
			// 22.表清洗参数信息table_clean,获取table_clean表信息集合入map
			addTableCleanToMap(collectionMap, tableInfoResult);
			// 23.表对应的字段table_column,获取table_column表信息集合入map
			Result tableColumnResult = getTableColumnResult(collectionMap, tableInfoResult);
			// 24.列清洗参数信息 column_clean,获取column_clean表信息集合入map
			addColumnCleanToMap(collectionMap, tableColumnResult);
			// 25.列拆分信息表column_split,获取column_split表信息集合入map
			addColumnSplitToMap(collectionMap, tableColumnResult);
			// 26.使用base64编码
			byte[] bytes = Base64.getEncoder().encode(JsonUtil.toJson(collectionMap).
					getBytes(CodecUtil.UTF8_CHARSET));
			// 28.判断文件是否存在
			if (bytes == null) {
				throw new BusinessException("此文件不存在");
			}
			OutputStream outputStream = response.getOutputStream();
			// 29.清空response，设置响应编码格式,响应头，控制浏览器下载该文件
			response.reset();
			response.setCharacterEncoding(CodecUtil.UTF8_STRING);
			response.setContentType("APPLICATION/OCTET-STREAM");
			// 30.通过流的方式写入文件
			outputStream.write(bytes);
			outputStream.flush();
			outputStream.close();
		} catch (IOException e) {
			throw new BusinessException("下载文件失败");
		}
	}

	@Method(desc = "获取agent_info表数据集合",
			logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
					"2.将agent_info表数据集合存入map" +
					"3.将agent_info表数据集合返回")
	@Param(name = "sourceId", desc = "data_source表主键，source_relation_dep表外键",
			range = "不为空及空格，10位数字，新增data_source表时自动生成")
	@Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是source_relation_dep表数据集合）",
			range = "key值唯一，不为空")
	@Return(desc = "返回source_relation_dep表数据的集合", range = "不为空")
	private List<Agent_info> getAgentInfoList(long source_id, Map<String, Object> collectionMap) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		List<Agent_info> agentInfoList = Dbo.queryList(Agent_info.class, "select * from "
				+ Agent_info.TableName + " where  source_id = ?", source_id);
		// 2.将agent_info表数据集合存入map
		collectionMap.put(Agent_info.TableName, agentInfoList);
		// 3.将agent_info表数据集合返回
		return agentInfoList;
	}

	@Method(desc = "将department_info表数据加入map",
			logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
					"2.创建存放department_info表数据的集合" +
					"3.循环source_relation_dep表集合获取dep_id，通过dep_id获取department_info表数据" +
					"4.将department_info表信息加入集合" +
					"5.将department_info表数据集合入map")
	@Param(name = "collectionMap", desc = "所有表数据的map的实体", range = "不能为空")
	@Param(name = "sourceRelationDepList", desc = "存放source_relation_dep表数据集合", range = "key值唯一，不为空")
	private void addDepartmentInfoToMap(Map<String, Object> collectionMap,
	                                    List<Source_relation_dep> sourceRelationDepList) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建存放department_info表数据的集合
		List<Optional<Department_info>> departmentInfoList = new ArrayList<>();
		// 3.循环source_relation_dep表集合获取dep_id，通过dep_id获取department_info表数据
		for (Source_relation_dep sourceRelationDep : sourceRelationDepList) {
			Optional<Department_info> departmentInfo = Dbo.queryOneObject(Department_info.class
					, "select * from " + Department_info.TableName + " where dep_id=?",
					sourceRelationDep.getDep_id());
			// 4.将department_info表信息加入集合
			departmentInfoList.add(departmentInfo);
		}
		// 5.将department_info表数据集合入map
		collectionMap.put(Department_info.TableName, departmentInfoList);
	}

	@Method(desc = "将source_relation_dep表数据入map",
			logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
					"2.查询数据源与部门关系表信息" +
					"3.将source_relation_dep表数据入map" +
					"4.返回source_relation_dep表数据集合")
	@Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是source_relation_dep表数据集合）",
			range = "不能为空,key唯一")
	@Param(name = "sourceId", desc = "data_source表主键，source_relation_dep表外键",
			range = "不为空及空格，10位数字，新增data_source表时自动生成")
	@Return(desc = "返回source_relation_dep表数据的集合", range = "不为空")
	private List<Source_relation_dep> addSourceRelationDepToMap(long source_id,
	                                                            Map<String, Object> collectionMap) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.查询数据源与部门关系表信息
		List<Source_relation_dep> sourceRelationDepList = Dbo.queryList(Source_relation_dep.class,
				"select * from " + Source_relation_dep.TableName + " where source_id=?", source_id);
		// 3.将source_relation_dep表数据入map
		collectionMap.put(Source_relation_dep.TableName, sourceRelationDepList);
		// 4.返回source_relation_dep表数据集合
		return sourceRelationDepList;
	}

	@Method(desc = "将column_split表数据集合存入map",
			logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
					"2.创建存放column_split表信息的集合" +
					"3.遍历table_column结果集获取column_id,通过column_id查询column_split表信息" +
					"4.将查询到的信息封装入集合" +
					"5.将column_split表集合信息存入map")
	@Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是column_split表数据集合）",
			range = "不能为空,key唯一")
	@Param(name = "tableColumnResult", desc = "table_column表数据结果集", range = "不为空")
	private void addColumnSplitToMap(Map<String, Object> collectionMap, Result tableColumnResult) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建存放column_split表信息的集合
		Result columnSplitResult = new Result();
		// 3.遍历table_column结果集获取column_id,通过column_id查询column_split表信息
		for (int i = 0; i < tableColumnResult.getRowCount(); i++) {
			Result result = Dbo.queryResult("select * from " + Column_split.TableName +
					" where column_id = ?", tableColumnResult.getLong(i, "column_id"));
			// 4.将查询到的信息封装入集合
			columnSplitResult.add(result);
		}
		// 5.将column_split表集合信息存入map
		collectionMap.put(Column_split.TableName, columnSplitResult.toList());
	}

	@Method(desc = "将column_clean表数据集合存入map",
			logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
					"2.创建存放column_clean表信息的集合" +
					"3.遍历table_column结果集获取column_id,通过column_id查询column_clean表信息" +
					"4.将查询到的信息封装入集合" +
					"5.将column_clean表集合信息存入map")
	@Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是column_clean表数据集合）",
			range = "不能为空,key唯一")
	@Param(name = "tableColumnResult", desc = "table_column表数据结果集", range = "不为空")
	private void addColumnCleanToMap(Map<String, Object> collectionMap, Result tableColumnResult) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建存放column_clean表信息的集合
		Result columnCleanResult = new Result();
		// 3.遍历table_column结果集获取column_id,通过column_id查询column_clean表信息
		for (int i = 0; i < tableColumnResult.getRowCount(); i++) {
			Result result = Dbo.queryResult("select * from " + Column_clean.TableName +
					" where column_id=?", tableColumnResult.getLong(i, "column_id"));
			// 4.将查询到的信息封装入集合
			columnCleanResult.add(result);
		}
		// 5.将column_clean表集合信息存入map
		collectionMap.put(Column_clean.TableName, columnCleanResult.toList());
	}

	@Method(desc = "将table_column表数据集合存入map",
			logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
					"2.创建存放table_column表信息的集合" +
					"3.遍历table_info结果集获取table_id,通过table_id查询table_column表信息" +
					"4.将查询到的信息封装入集合" +
					"5.将table_column表集合信息存入map" +
					"6.将table_column表结果集返回")
	@Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是table_column表数据集合）",
			range = "不能为空,key唯一")
	@Param(name = "tableColumnResult", desc = "table_column表数据结果集", range = "不为空")
	@Return(desc = "将table_column表结果集返回", range = "不为空")
	private Result getTableColumnResult(Map<String, Object> collectionMap, Result tableInfoResult) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建存放table_column表信息的集合
		Result tableColumnResult = new Result();
		// 3.遍历table_info结果集获取table_id,通过table_id查询table_column表信息
		for (int i = 0; i < tableInfoResult.getRowCount(); i++) {
			Result result = Dbo.queryResult("select * from " + Table_column.TableName +
					" where table_id=?", tableInfoResult.getLong(i, "table_id"));
			// 4.将查询到的信息封装入结果集
			tableColumnResult.add(result);
		}
		// 5.将table_column表集合信息存入map
		collectionMap.put(Table_column.TableName, tableColumnResult.toList());
		// 6.将table_column表结果集返回
		return tableColumnResult;
	}

	@Method(desc = "将table_clean表数据集合存入map",
			logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
					"2.创建存放table_clean表信息的集合" +
					"3.遍历table_info结果集获取table_id,通过table_id查询table_clean表信息" +
					"4.将查询到的信息封装入集合" +
					"5.将table_clean表集合信息存入map")
	@Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是table_clean表数据集合）",
			range = "不能为空,key唯一")
	@Param(name = "tableColumnResult", desc = "table_column表数据结果集", range = "不为空")
	private void addTableCleanToMap(Map<String, Object> collectionMap, Result tableInfoResult) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建存放table_clean表信息的集合
		Result tableCleanResult = new Result();
		// 3.遍历table_info结果集获取table_id,通过table_id查询table_clean表信息
		for (int i = 0; i < tableInfoResult.getRowCount(); i++) {
			Result result = Dbo.queryResult("select * from " + Table_clean.TableName +
					" where table_id = ?", tableInfoResult.getLong(i, "table_id"));
			// 4.将查询到的信息封装入集合
			tableCleanResult.add(result);
		}
		// 5.将table_clean表集合信息存入map
		collectionMap.put(Table_clean.TableName, tableCleanResult.toList());
	}

	@Method(desc = "将object_collect_struct表数据集合存入map",
			logicStep = " 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
					"1.创建存放object_collect_struct表信息的集合" +
					"2.遍历object_collect_task结果集获取table_id,通过table_id查询object_collect_struct表信息" +
					"3.将查询到的信息封装入集合" +
					"4.将object_collect_struct表集合信息存入map")
	@Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是object_collect_struct表数据集合）",
			range = "不能为空,key唯一")
	@Param(name = "tableInfoResult", desc = "table_info表数据结果集", range = "不为空")
	private void addTableStorageInfoToMap(Map<String, Object> collectionMap, Result tableInfoResult) {
		Result tableStorageInfoResult = new Result();
		for (int i = 0; i < tableInfoResult.getRowCount(); i++) {
			Result result = Dbo.queryResult("select * from " + Table_storage_info.TableName +
					" where table_id = ?", tableInfoResult.getLong(i, "table_id"));
			tableStorageInfoResult.add(result);
		}
		collectionMap.put(Table_storage_info.TableName, tableStorageInfoResult.toList());
	}

	@Method(desc = "将column_merge表数据集合存入map",
			logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
					"1.创建存放column_merge表信息的集合" +
					"2.遍历table_info结果集获取table_id,通过table_id查询column_merge表信息" +
					"3.将查询到的信息封装入集合" +
					"4.将column_merge表集合信息存入map")
	@Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是column_merge表数据集合）",
			range = "不能为空,key唯一")
	@Param(name = "tableInfoResult", desc = "table_info表数据结果集", range = "不为空")
	private void addColumnMergeToMap(Map<String, Object> collectionMap, Result tableInfoResult) {
		Result columnMergeResult = new Result();
		for (int i = 0; i < tableInfoResult.getRowCount(); i++) {
			Result result = Dbo.queryResult("select * from " + Column_merge.TableName +
					" where table_id = ?", tableInfoResult.getLong(i, "table_id"));
			columnMergeResult.add(result);
		}
		collectionMap.put(Column_merge.TableName, columnMergeResult.toList());
	}

	@Method(desc = "将table_info表数据集合存入map",
			logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
					"2.创建存放table_info表信息的集合" +
					"3.遍历database_set结果集获取database_set,通过database_set查询table_info表信息" +
					"4.将查询到的信息封装入集合" +
					"5.将table_info表集合信息存入map")
	@Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是column_merge表数据集合）",
			range = "不能为空,key唯一")
	@Param(name = "databaseSetResult", desc = "database_set表数据结果集", range = "不为空")
	private Result getTableInfoResult(Map<String, Object> collectionMap, Result databaseSetResult) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建存放table_info表信息的集合
		Result tableInfoResult = new Result();
		// 3.遍历database_set结果集获取database_set,通过database_set查询table_info表信息
		for (int i = 0; i < databaseSetResult.getRowCount(); i++) {
			Result result = Dbo.queryResult("select * from " + Table_info.TableName +
					" where database_id = ?", databaseSetResult.getLong(i, "database_id"));
			tableInfoResult.add(result);
		}
		// 4.将查询到的信息封装入集合
		collectionMap.put(Table_info.TableName, tableInfoResult.toList());
		// 5.将table_info表集合信息存入map
		return tableInfoResult;
	}

	@Method(desc = "将signal_file表数据集合存入map",
			logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
					"2.创建存放signal_file表信息的集合" +
					"3.遍历database_set结果集获取database_id,通过ocs_id查询signal_file表信息" +
					"4.将查询到的信息封装入集合" +
					"5.将signal_file表集合信息存入map")
	@Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是signal_file表数据集合）",
			range = "不能为空,key唯一")
	@Param(name = "databaseSetResult", desc = "database_set表数据结果集", range = "不为空")
	private void addSignalFileToMap(Map<String, Object> collectionMap, Result databaseSetResult) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建存放signal_file表信息的集合
		Result signalFileResult = new Result();
		// 3.遍历database_set结果集获取database_id,通过ocs_id查询signal_file表信息
		for (int i = 0; i < databaseSetResult.getRowCount(); i++) {
			Result result = Dbo.queryResult("select * from " + Signal_file.TableName +
					" where database_id=?", databaseSetResult.getLong(i, "database_id"));
			// 4.将查询到的信息封装入集合
			signalFileResult.add(result);
		}
		// 5.将signal_file表集合信息存入map
		collectionMap.put(Signal_file.TableName, signalFileResult.toList());
	}

	@Method(desc = "将file_source表数据集合存入map",
			logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
					"2.创建存放file_source表信息的集合" +
					"3.遍历file_collect_set结果集获取fcs_id(文件系统采集ID),通过fcs_id查询file_source表信息" +
					"4.将查询到的信息封装入集合" +
					"5.将file_source表集合信息存入map")
	@Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是file_source表数据集合）",
			range = "不能为空,key唯一")
	@Param(name = "databaseSetResult", desc = "database_set表数据结果集", range = "不为空")
	private void addFileSourceToMap(Map<String, Object> collectionMap, Result file_collect_setResult) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建存放file_source表信息的集合
		Result fileSourceResult = new Result();
		// 3.遍历file_collect_set结果集获取fcs_id(文件系统采集ID),通过fcs_id查询file_source表信息
		for (int i = 0; i < file_collect_setResult.getRowCount(); i++) {
			Result result = Dbo.queryResult("select * from " + File_source.TableName +
					" where fcs_id=?", file_collect_setResult.getLong(i, "fcs_id"));
			// 4.将查询到的信息封装入集合
			fileSourceResult.add(result);
		}
		// 5.将file_source表集合信息存入map
		collectionMap.put(File_source.TableName, fileSourceResult.toList());
	}

	@Method(desc = "将file_collect_set表数据集合存入map",
			logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
					"2.创建存放file_collect_set表信息的集合" +
					"3.遍历agent_info结果集获取agent_id,通过agent_id查询file_collect_set表信息" +
					"4.将查询到的信息封装入集合" +
					"5.将file_collect_set表集合信息存入map" +
					"6.返回file_collect_set数据结果集")
	@Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是file_collect_set表数据集合）",
			range = "不能为空,key唯一")
	@Param(name = "agentInfoList", desc = "agent_info表数据结果集合", range = "不为空")
	private Result getFileCollectSetResult(Map<String, Object> collectionMap,
	                                       List<Agent_info> agentInfoList) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建存放file_collect_set表信息的集合
		Result fileCollectSetResult = new Result();
		// 3.遍历agent_info结果集获取agent_id,通过agent_id查询file_collect_set表信息
		for (Agent_info agent_info : agentInfoList) {
			Result result = Dbo.queryResult("select * from " + File_collect_set.TableName +
					" where agent_id=?", agent_info.getAgent_id());
			// 4.将查询到的信息封装入集合
			fileCollectSetResult.add(result);
		}
		// 5.将file_collect_set表集合信息存入map
		collectionMap.put(File_collect_set.TableName, fileCollectSetResult.toList());
		// 6.返回file_collect_set数据结果集
		return fileCollectSetResult;
	}

	@Method(desc = "将database_set表数据集合存入map",
			logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
					"2.创建存放database_set表信息的集合" +
					"3.遍历agent_info结果集获取agent_id,通过agent_id查询database_set表信息" +
					"4.将查询到的信息封装入集合" +
					"5.将database_set表集合信息存入map" +
					"6.返回database_result表数据结果集")
	@Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是database_set表数据集合）",
			range = "不能为空,key唯一")
	@Param(name = "agentInfoList", desc = "agent_info表数据结果集合", range = "不为空")
	private Result getDatabaseSetResult
			(Map<String, Object> collectionMap, List<Agent_info> agentInfoList) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建存放database_set表信息的集合
		Result databaseSetResult = new Result();
		// 3.遍历agent_info结果集获取agent_id,通过agent_id查询database_set表信息
		for (Agent_info agent_info : agentInfoList) {
			Result result = Dbo.queryResult("select * from " + Database_set.TableName +
					" where agent_id = ?", agent_info.getAgent_id());
			// 4.将查询到的信息封装入集合
			databaseSetResult.add(result);
		}
		//5.将database_set表集合信息存入map
		collectionMap.put(Database_set.TableName, databaseSetResult.toList());
		// 6.返回database_result表数据结果集
		return databaseSetResult;
	}

	@Method(desc = "将object_collect_struct表数据集合存入map",
			logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
					"2.创建存放object_collect_struct表信息的集合" +
					"3.遍历object_collect_task结果集获取ocs_id(对象采集任务id),通过ocs_id查询" +
					"object_collect_struct表信息" +
					"4.将查询到的信息封装入集合" +
					"5.将object_collect_struct表集合信息存入map")
	@Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是object_collect_struct表数据集合）",
			range = "不能为空,key唯一")
	@Param(name = "objectCollectTaskResult", desc = "object_collect_task表数据结果集", range = "不为空")
	private void addObjectCollectStructResultToMap(Map<String, Object> collectionMap,
	                                               Result objectCollectTaskResult) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建存放object_collect_struct表信息的集合
		Result objectCollectStructResult = new Result();
		// 3.遍历object_collect_task结果集获取ocs_id,通过ocs_id查询object_collect_struct表信息
		for (int i = 0; i < objectCollectTaskResult.getRowCount(); i++) {
			Result result = Dbo.queryResult("select * from " + Object_collect_struct.TableName +
					" where ocs_id =?", objectCollectTaskResult.getLong(i, "ocs_id"));
			// 4.将查询到的信息封装入集合
			objectCollectStructResult.add(result);
		}
		// 5.将object_collect_struct表集合信息存入map
		collectionMap.put(Object_collect_struct.TableName, objectCollectStructResult.toList());
	}

	@Method(desc = "封装object_collect_task表信息入map并返回object_collect_task表信息",
			logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
					"2.创建存放object_collect_task表数据的结果集对象" +
					"3.循环遍历object_collect表数据获取odc_id，根据odc_id查询object_collect_task表信息并封装" +
					"4.将object_collect_task表结果集封装入map" +
					"5.返回object_collect_task表结果集")
	@Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是object_collect_task表数据集合）",
			range = "不能为空,key唯一")
	@Param(name = "objectCollectResult", desc = "object_collect表数据结果集", range = "不为空")
	@Return(desc = "返回object_collect_task表数据集合信息", range = "不为空")
	private Result getObjectCollectTaskResult(Map<String, Object> collectionMap, Result
			objectCollectResult) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建存放object_collect_task表数据的结果集对象
		Result objectCollectTaskResult = new Result();
		// 3.循环遍历object_collect表数据获取odc_id，根据odc_id查询object_collect_task表信息并封装
		for (int i = 0; i < objectCollectResult.getRowCount(); i++) {
			Result result = Dbo.queryResult("select * from " + Object_collect_task.TableName +
					" where odc_id=?", objectCollectResult.getLong(i, "odc_id"));
			objectCollectTaskResult.add(result);
		}
		// 4.将object_collect_task表结果集封装入map
		collectionMap.put(Object_collect_task.TableName, objectCollectTaskResult.toList());
		// 5.返回object_collect_task表结果集
		return objectCollectTaskResult;
	}

	@Method(desc = "封装object_collect表信息入map并返回object_collect表信息",
			logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
					"2.创建封装object_collect信息的结果集对象" +
					"3.循环遍历agent_info表信息集合获取agent_id（agent_info表主键，object_collect表外键）" +
					"4.根据agent_id查询object_collect表获取结果集并添加到结果集对象中" +
					"5.将object_collect结果集封装入map")
	@Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是object_collect表数据集合）",
			range = "不能为空,key唯一")
	@Param(name = "agentInfoList", desc = "agent_info表数据集合", range = "不为空")
	@Return(desc = "返回object_collect表数据集合信息", range = "不为空")
	private Result getObjectCollectResult
			(Map<String, Object> collectionMap, List<Agent_info> agentInfoList) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建封装object_collect信息的结果集对象
		Result objectCollectResult = new Result();
		// 3.循环遍历agent_info表信息集合获取agent_id（agent_info表主键，object_collect表外键）
		for (Agent_info agent_info : agentInfoList) {
			// 4.根据agent_id查询object_collect表获取结果集并添加到结果集对象中
			Result object_collect = Dbo.queryResult("select * from " + Object_collect.TableName +
					" where agent_id=?", agent_info.getAgent_id());
			objectCollectResult.add(object_collect);
		}
		// 5.将object_collect结果集封装入map
		collectionMap.put(Object_collect.TableName, objectCollectResult.toList());
		// 6.返回object_collect结果集
		return objectCollectResult;
	}

	@Method(desc = "封装agent_down_info表数据集合到map",
			logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
					"2.创建封装agent_down_info表信息集合" +
					"3.遍历agent_info表信息获取agent_id（agent_info表主键，agent_down_info表外键）" +
					"4.通过agent_id查询agent_down_info表信息" +
					"5.将agent_down_info表信息放入list" +
					"6.将agent_down_info表信息入map")
	@Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是agent_down_info表数据集合）",
			range = "不能为空,key唯一")
	@Param(name = "agentInfoList", desc = "agent_info表数据集合", range = "不为空")
	private void addAgentDownInfoToMap
			(Map<String, Object> collectionMap, List<Agent_info> agentInfoList) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建封装agent_down_info表信息集合
		List<Optional<Agent_down_info>> agentDownInfoList =
				new ArrayList<>();
		// 3.遍历agent_info表信息获取agent_id（agent_info表主键，agent_down_info表外键）
		for (Agent_info agent_info : agentInfoList) {
			// 4.通过agent_id查询agent_down_info表信息
			Optional<Agent_down_info> agent_down_info = Dbo.queryOneObject(Agent_down_info.class,
					"select * from  " + Agent_down_info.TableName + " where  agent_id = ?",
					agent_info.getAgent_id());
			// 5.将agent_down_info表信息放入list
			if (agent_down_info.isPresent()) {
				agentDownInfoList.add(agent_down_info);
			}
		}
		// 6.将agent_down_info表信息入map
		collectionMap.put(Agent_down_info.TableName, agentDownInfoList);
	}

	@Method(desc = "封装data_source表数据集合到map",
			logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
					"2.根据数据源ID查询数据源data_source集合" +
					"3.判断获取到的集合是否有数据，没有抛异常，有返回数据" +
					"4.将data_source数据入map")
	@Param(name = "sourceId", desc = "data_source主键ID", range = "不为空,10位数字，新增数据源时生成")
	@Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是agent_down_info表数据集合）",
			range = "不能为空,key唯一")
	private void addDataSourceToMap(long source_id, Map<String, Object> collectionMap) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.根据数据源ID查询数据源data_source集合
		Optional<Data_source> dataSource = Dbo.queryOneObject(Data_source.class, "select * from "
				+ Data_source.TableName + " where source_id = ?", source_id);
		// 3.判断获取到的集合是否有数据，没有抛异常，有返回数据
		if (!dataSource.isPresent()) {
			throw new BusinessException("此数据源下没有数据，sourceId = ?" + source_id);
		}
		// 4.将data_source数据入map
		collectionMap.put(Data_source.TableName, dataSource);
	}

	@Method(desc = "封装ftp_transfered表数据集合到map",
			logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
					"2.创建封装ftp_transfered信息的集合" +
					"3.遍历Ftp_collect信息，获取ftp_id(ftp_transfered主键，ftp_collect外键）" +
					"4. 根据ftp_id查询ftp_transfered信息" +
					"5.将ftp_transfered表数据结果集信息入map")
	@Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是ftp_transfered表数据集合）",
			range = "不能为空,key唯一")
	@Param(name = "ftpCollectResult", desc = "ftp_collect表数据集", range = "不为空")
	private void addFtpTransferedToMap(Map<String, Object> collectionMap, Result ftpCollectResult) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建封装ftp_transfered信息的集合
		Result ftpTransferedResult = new Result();
		// 3.遍历Ftp_collect信息，获取ftp_id(ftp_transfered主键，ftp_collect外键）
		for (int i = 0; i < ftpCollectResult.getRowCount(); i++) {
			// 4. 根据ftp_id查询ftp_transfered信息
			Result result = Dbo.queryResult("select * from " + Ftp_transfered.TableName +
					" where ftp_id=?", ftpCollectResult.getLong(i, "ftp_id"));
			ftpTransferedResult.add(result);
		}
		// 5.将ftp_transfered表数据结果集信息入map
		collectionMap.put(Ftp_transfered.TableName, ftpTransferedResult.toList());
	}

	@Method(desc = "将ftp_collect表信息封装入map",
			logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
					"2.创建封装ftp_collect信息的集合" +
					"3.遍历agent_info信息，获取agent_id(agent_info主键，ftp_collect外键）" +
					"4. 根据agent_id查询ftp_collect信息" +
					"5.将ftp_collect表信息入map" +
					"6.返回ftp_collect表结果集信息")
	@Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是ftp_collect表数据集合）",
			range = "不能为空,key唯一")
	@Param(name = "agentInfoList", desc = "agent_info表数据集合", range = "不为空")
	@Return(desc = "返回ftp_collect集合信息", range = "不为空")
	private Result getFtpCollectResult
			(Map<String, Object> collectionMap, List<Agent_info> agentInfoList) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建封装ftp_collect信息的集合
		Result ftpCollectResult = new Result();
		// 3.遍历agent_info信息，获取agent_id(agent_info主键，ftp_collect外键）
		for (Agent_info agent_info : agentInfoList) {
			// 4. 根据agent_id查询ftp_collect信息
			Result result = Dbo.queryResult("select * from " + Ftp_collect.TableName +
					" where agent_id = ?", agent_info.getAgent_id());
			ftpCollectResult.add(result);
		}
		// 5.将ftp_collect表信息入map
		collectionMap.put(Ftp_collect.TableName, ftpCollectResult.toList());
		// 6.返回ftp_collect表结果集信息
		return ftpCollectResult;
	}

	@Method(desc = "封装collect_job_classify表信息入map",
			logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
					"2.创建封装Collect_job_classify信息的集合" +
					"3.遍历agent_info信息，获取agent_id(agent_info主键，Collect_job_classify外键）" +
					"4. 根据agent_id查询Collect_job_classify信息" +
					"5.将collect_job_classify表数据结果集入map")
	@Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是collect_job_classify表数据集合）",
			range = "不能为空,key唯一")
	@Param(name = "agentInfoList", desc = "agent_info表数据集合", range = "不为空")
	private void addCollectJobClassifyToMap(Map<String, Object> collectionMap,
	                                        List<Agent_info> agentInfoList) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建封装collect_job_classify信息的集合
		Result collectJobClassifyResult = new Result();
		// 3.遍历agent_info信息，获取agent_id(agent_info主键，collect_job_classify外键）
		for (Agent_info agent_info : agentInfoList) {
			// 4.根据agent_id查询Collect_job_classify信息
			Result result = Dbo.queryResult("select * from " + Collect_job_classify.TableName +
					" where agent_id=?", agent_info.getAgent_id());
			collectJobClassifyResult.add(result);
		}
		// 5.将collect_job_classify表数据结果集入map
		collectionMap.put(Collect_job_classify.TableName, collectJobClassifyResult.toList());
	}

}
