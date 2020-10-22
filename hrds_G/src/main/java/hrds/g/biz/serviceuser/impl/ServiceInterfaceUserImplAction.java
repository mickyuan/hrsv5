package hrds.g.biz.serviceuser.impl;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.action.AbstractWebappBaseAction;
import fd.ng.web.util.Dbo;
import fd.ng.web.util.RequestUtil;
import hrds.commons.codes.AgentType;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.Store_type;
import hrds.commons.collection.ProcessingData;
import hrds.commons.collection.bean.LayerBean;
import hrds.commons.entity.*;
import hrds.commons.utils.*;
import hrds.commons.utils.autoanalysis.AutoAnalysisUtil;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.g.biz.bean.*;
import hrds.g.biz.commons.FileDownload;
import hrds.g.biz.commons.LocalFile;
import hrds.g.biz.enumerate.AsynType;
import hrds.g.biz.enumerate.OutType;
import hrds.g.biz.enumerate.StateType;
import hrds.g.biz.init.InterfaceManager;
import hrds.g.biz.serviceuser.ServiceInterfaceUserDefine;
import hrds.g.biz.serviceuser.common.InterfaceCommon;
import hrds.g.biz.serviceuser.query.QueryByRowkey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

@DocClass(desc = "接口服务实现类（接口api）", author = "dhw", createdate = "2020/3/30 15:39")
public class ServiceInterfaceUserImplAction extends AbstractWebappBaseAction
		implements ServiceInterfaceUserDefine {

	private static final Logger logger = LogManager.getLogger();
	// 接口使用日志是否记录标志,1：是，0：否
	private static final String isRecordInterfaceLog =
			PropertyParaValue.getString("isRecordInterfaceLog", "1");

	@Method(desc = "获取token值",
			logicStep = "1.数据可访问权限处理方式：该方法通过user_id进行访问权限限制" +
					"2.获取token值")
	@Param(name = "user_id", desc = "用户ID", range = "新增用户时生成")
	@Param(name = "user_password", desc = "密码", range = "新增用户时生成")
	@Return(desc = "返回接口响应信息", range = "无限制")
	@Override
	public Map<String, Object> getToken(Long user_id, String user_password) {
		// 1.数据可访问权限处理方式：该方法通过user_id进行访问权限限制
		// 2.获取token值
		return InterfaceCommon.getTokenById(Dbo.db(), user_id, user_password);
	}

	@Method(desc = "表使用权限查询", logicStep = "1.数据可访问权限处理方式：该方法通过user_id进行访问权限限制" +
			"2.校验接口是否有效,返回响应状态信息" +
			"3.如果响应状态不是normal返回错误响应信息" +
			"4.正常响应信息，返回有使用权限的表" +
			"5.记录接口使用日志")
	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
	@Return(desc = "返回接口响应信息", range = "无限制")
	@Override
	public Map<String, Object> tableUsePermissions(CheckParam checkParam) {
		// 1.数据可访问权限处理方式：该方法通过user_id进行访问权限限制
		long start = System.currentTimeMillis();
		Interface_use_log interface_use_log = new Interface_use_log();
		// 请求开始时间
		interface_use_log.setRequest_stime(DateUtil.getDateTime());
		// 2.校验接口是否有效,返回响应状态信息
		Map<String, Object> responseMap = InterfaceCommon.checkTokenAndInterface(Dbo.db(), checkParam);
		// 3.如果响应状态不是normal返回错误响应信息
		if (!StateType.NORMAL.name().equals(responseMap.get("status").toString())) {
			responseMap.remove("token");
			return responseMap;
		}
		QueryInterfaceInfo userByToken = InterfaceManager.getUserByToken(responseMap.get("token").toString());
		// 4.正常响应信息，返回有使用权限的表
		responseMap = StateType.getResponseInfo(StateType.NORMAL.name(),
				InterfaceManager.getTableList(userByToken.getUser_id()));
		// 5.记录接口使用日志
		if (IsFlag.Shi == IsFlag.ofEnumByCode(isRecordInterfaceLog)) {
			insertInterfaceUseLog(checkParam.getUrl(), start, interface_use_log, userByToken,
					responseMap.get("status").toString());
		}
		return responseMap;
	}

	@Method(desc = "单表普通查询", logicStep = "1.数据可访问权限处理方式：该方法通过user_id进行访问权限限制" +
			"2.token，接口权限检查" +
			"3.如果responseMap响应状态不为normal返回错误响应信息" +
			"4.检查表信息" +
			"5.返回按类型操作接口响应信息" +
			"6.记录接口使用日志")
	@Param(name = "singleTable", desc = "单表普通查询参数实体", range = "无限制", isBean = true)
	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
	@Return(desc = "返回接口响应信息", range = "无限制")
	@Override
	public Map<String, Object> generalQuery(SingleTable singleTable, CheckParam checkParam) {
		// 1.数据可访问权限处理方式：该方法通过user_id进行访问权限限制
		long start = System.currentTimeMillis();
		Interface_use_log interface_use_log = new Interface_use_log();
		// 请求开始时间
		interface_use_log.setRequest_stime(DateUtil.getDateTime());
		// 2.token，接口权限检查
		Map<String, Object> responseMap = InterfaceCommon.checkTokenAndInterface(Dbo.db(), checkParam);
		if (!StateType.NORMAL.name().equals(responseMap.get("status").toString())) {
			responseMap.remove("token");
			return responseMap;
		}
		QueryInterfaceInfo userByToken = InterfaceManager.getUserByToken(responseMap.get("token").toString());
		// 检查参数
		responseMap = InterfaceCommon.checkType(singleTable.getDataType(), singleTable.getOutType(),
				singleTable.getAsynType(), singleTable.getBackurl(), singleTable.getFilepath(),
				singleTable.getFilename());
		// 3.如果responseMap响应状态不为normal返回错误响应信息
		if (!StateType.NORMAL.name().equals(responseMap.get("status").toString())) {
			return responseMap;
		}
		// 4.检查表信息
		responseMap = InterfaceCommon.checkTable(Dbo.db(), userByToken.getUser_id(), singleTable);
		// 5.返回按类型操作接口响应信息
		responseMap = InterfaceCommon.operateInterfaceByType(singleTable.getDataType(),
				singleTable.getOutType(),
				singleTable.getAsynType(), singleTable.getBackurl(), singleTable.getFilepath(),
				singleTable.getFilename(), responseMap);
		// 6.记录接口使用日志
		if (IsFlag.Shi == IsFlag.ofEnumByCode(isRecordInterfaceLog)) {
			insertInterfaceUseLog(checkParam.getUrl(), start, interface_use_log, userByToken,
					responseMap.get("status").toString());
		}
		return responseMap;
	}

	@Method(desc = "单表数据删除接口", logicStep = "1.判断表是否存在" +
			"2.检查token以及接口是否有效" +
			"3.判断表是否有效" +
			"4.根据表名称删除表数据")
	@Param(name = "tableData", desc = "表数据接口参数实体对象", range = "自定义实体")
	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
	@Return(desc = "返回接口响应信息", range = "无限制")
	@Override
	public Map<String, Object> singleTableDataDelete(TableData tableData, CheckParam checkParam) {
		// 数据可访问权限处理方式：该方法通过user_id进行访问权限限制
		long start = System.currentTimeMillis();
		Interface_use_log interface_use_log = new Interface_use_log();
		// 请求开始时间
		interface_use_log.setRequest_stime(DateUtil.getDateTime());
		// 1.判断表是否存在
		if (isParamExist(tableData.getTableName()))
			return StateType.getResponseInfo(StateType.TABLE_NOT_EXISTENT);
		// 2.检查token以及接口是否有效
		Map<String, Object> responseMap = InterfaceCommon.checkTokenAndInterface(Dbo.db(), checkParam);
		if (!StateType.NORMAL.name().equals(responseMap.get("status").toString())) {
			responseMap.remove("token");
			return responseMap;
		}
		QueryInterfaceInfo userByToken = InterfaceManager.getUserByToken(responseMap.get("token").toString());
		// 3.判断表是否有效
		if (!InterfaceManager.existsTable(Dbo.db(), userByToken.getUser_id(), tableData.getTableName())) {
			return StateType.getResponseInfo(StateType.NO_USR_PERMISSIONS);
		}
		// 4.根据表名称删除表数据
		responseMap = InterfaceCommon.deleteTableDataByTableName(Dbo.db(), tableData, userByToken.getUser_id());
		// 5.记录接口使用日志
		if (IsFlag.Shi == IsFlag.ofEnumByCode(isRecordInterfaceLog)) {
			insertInterfaceUseLog(checkParam.getUrl(), start, interface_use_log, userByToken,
					responseMap.get("status").toString());
		}
		return responseMap;
	}


	@Method(desc = "表结构查询接口", logicStep = "1.数据可访问权限处理方式：该方法通过user_id进行访问权限限制" +
			"2.检查token以及接口是否有效" +
			"3.判断表是否有效" +
			"4.有效，根据user_id与表名获取查询接口信息" +
			"5.数据源类型为集市，关联查询数据表字段信息表以及数据表查询字段中英文信息" +
			"6.数据源类型为加工" +
			"7.数据源类型为贴源层，关联查询表对应的字段、数据库对应表、源文件属性表查询字段中英文信息" +
			"8.数据源类型为其他，查询源文件属性表信息获取字段中英文信息" +
			"9.返回接口响应信息" +
			"10.记录接口使用日志信息" +
			"11.没有表使用权限")
	@Param(name = "tableName", desc = "要查询表名", range = "无限制", nullable = true)
	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
	@Return(desc = "返回接口响应信息", range = "无限制")
	@Override
	public Map<String, Object> tableStructureQuery(String tableName, CheckParam checkParam) {
		// 1.数据可访问权限处理方式：该方法通过user_id进行访问权限限制
		long start = System.currentTimeMillis();
		Interface_use_log interface_use_log = new Interface_use_log();
		// 请求开始时间
		interface_use_log.setRequest_stime(DateUtil.getDateTime());
		if (isParamExist(tableName)) return StateType.getResponseInfo(StateType.TABLE_NOT_EXISTENT);
		// 2.检查token以及接口是否有效
		Map<String, Object> responseMap = InterfaceCommon.checkTokenAndInterface(Dbo.db(), checkParam);
		if (!StateType.NORMAL.name().equals(responseMap.get("status").toString())) {
			responseMap.remove("token");
			return responseMap;
		}
		QueryInterfaceInfo userByToken = InterfaceManager.getUserByToken(responseMap.get("token").toString());
		// 3.判断表是否有效
		if (InterfaceManager.existsTable(Dbo.db(), userByToken.getUser_id(), tableName)) {
			// 4.有效，根据user_id与表名获取查询接口信息
			QueryInterfaceInfo userTableInfo = InterfaceManager.getUserTableInfo(Dbo.db(),
					userByToken.getUser_id(), tableName);
			String type = userTableInfo.getTable_blsystem();
			Map<String, Object> res = new HashMap<>();
			res.put("table_type", type);
			List<Map<String, Object>> columns = DataTableUtil.getColumnByTableName(Dbo.db(), tableName);
			res.put("field", columns);
			// 10.记录接口使用日志信息
			if (IsFlag.Shi == IsFlag.ofEnumByCode(isRecordInterfaceLog)) {
				insertInterfaceUseLog(checkParam.getUrl(), start, interface_use_log, userByToken,
						responseMap.get("status").toString());
			}
			return StateType.getResponseInfo(StateType.NORMAL.name(), res);
		} else {
			// 11.没有表使用权限
			return StateType.getResponseInfo(StateType.NO_USR_PERMISSIONS);
		}
	}

	@Method(desc = "表结构查询-获取json信息接口", logicStep = "1.判断表名是否存在" +
			"2.检查token以及接口是否有效" +
			"3.判断表是否有使用权限" +
			"4.获取表列结构json信息" +
			"5.判断表对应存储层是否存在" +
			"6.记录接口使用日志信息" +
			"7.返回表列结构json信息")
	@Param(name = "tableName", desc = "表名", range = "无限制", nullable = true)
	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
	@Return(desc = "返回接口响应信息", range = "无限制")
	@Override
	public Map<String, Object> tableSearchGetJson(String tableName, CheckParam checkParam) {
		// 数据可访问权限处理方式：该方法通过user_id进行访问权限限制
		long start = System.currentTimeMillis();
		Interface_use_log interface_use_log = new Interface_use_log();
		// 请求开始时间
		interface_use_log.setRequest_stime(DateUtil.getDateTime());
		// 1.判断表名是否存在
		if (isParamExist(tableName)) return StateType.getResponseInfo(StateType.TABLE_NOT_EXISTENT);
		// 2.检查token以及接口是否有效
		Map<String, Object> responseMap = InterfaceCommon.checkTokenAndInterface(Dbo.db(), checkParam);
		if (!StateType.NORMAL.name().equals(responseMap.get("status").toString())) {
			responseMap.remove("token");
			return responseMap;
		}
		QueryInterfaceInfo userByToken = InterfaceManager.getUserByToken(responseMap.get("token").toString());
		// 3.判断表是否有使用权限
		if (!InterfaceManager.existsTable(Dbo.db(), userByToken.getUser_id(), tableName)) {
			return StateType.getResponseInfo(StateType.NO_USR_PERMISSIONS);
		}
		// 4.获取表列结构json信息
		List<Map<String, Object>> columns = DataTableUtil.getColumnInfoByTableName(Dbo.db(), tableName);
		// 5.判断表对应存储层是否存在
		if (columns == null) {
			return StateType.getResponseInfo(StateType.STORAGELAYER_NOT_EXIST_BY_TABLE);
		}
		// 6.记录接口使用日志信息
		if (IsFlag.Shi == IsFlag.ofEnumByCode(isRecordInterfaceLog)) {
			insertInterfaceUseLog(checkParam.getUrl(), start, interface_use_log, userByToken,
					responseMap.get("status").toString());
		}
		// 7.返回表列结构json信息
		return StateType.getResponseInfo(StateType.NORMAL.name(), columns);
	}

	private boolean isParamExist(String tableName) {
		return StringUtil.isBlank(tableName);
	}

	@Method(desc = "文件属性搜索接口", logicStep = "1.数据可访问权限处理方式：该方法通过user_id进行访问权限限制" +
			"2.token、接口权限检查" +
			"3.如果responseMap响应状态不为normal返回错误响应信息" +
			"4.定义显示条数默认值，文件大小范围默认值" +
			"5.判断显示条数是否为空，不为空处理数据获取显示条数以及其范围值" +
			"6.判断文件大小值是否为空，如果不为空处理数据获取文件大小范围值" +
			"7.判断文件大小是否为空，不为空加条件查询" +
			"8.判断文件后缀名是否为空，不为空加条件查询" +
			"9.判断采集任务路径是否为空，不为空加条件查询" +
			"10.判断采集任务id是否为空，不为空加条件查询" +
			"11.判断部门ID是否为空，不为空加条件查询" +
			"12.设置分页" +
			"13.关联查询data_source、file_collect_set、data_store_reg三张表获取文件信息" +
			"14.获取摘要" +
			"15.记录接口使用日志信息" +
			"16.判断文件属性信息是否为空，为空返回空集合，否则返回文件属性信息集合")
	@Param(name = "fileAttribute", desc = "文件屬性参数实体", range = "无限制", isBean = true)
	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
	@Return(desc = "返回接口响应信息", range = "无限制")
	@Override
	public Map<String, Object> fileAttributeSearch(FileAttribute fileAttribute, CheckParam checkParam) {
		// 1.数据可访问权限处理方式：该方法通过user_id进行访问权限限制
		long start = System.currentTimeMillis();
		Interface_use_log interface_use_log = new Interface_use_log();
		// 请求开始时间
		interface_use_log.setRequest_stime(DateUtil.getDateTime());
		// 2.token、接口权限检查
		Map<String, Object> responseMap = InterfaceCommon.checkTokenAndInterface(Dbo.db(), checkParam);
		// 3.如果responseMap响应状态不为normal返回错误响应信息
		if (!StateType.NORMAL.name().equals(responseMap.get("status").toString())) {
			responseMap.remove("token");
			return responseMap;
		}
		QueryInterfaceInfo userByToken = InterfaceManager.getUserByToken(responseMap.get("token").toString());
		// 4.定义显示条数默认值，文件大小范围默认值
		int num_start = 0, num_count = 10, fileSizeStart = 0, fileSizeEnd = 0;
		// 5.判断显示条数是否为空，不为空处理数据获取显示条数以及其范围值
		String num = fileAttribute.getNum();
		if (StringUtil.isNotBlank(num)) {
			if (num.contains(",")) {
				List<String> numList = StringUtil.split(num, ",");
				num_start = Integer.parseInt(numList.get(0));
				num_count = Integer.parseInt(numList.get(1));
			} else {
				num_count = Integer.parseInt(num);
			}
		}
		// 6.判断文件大小值是否为空，如果不为空处理数据获取文件大小范围值
		String fileSize = fileAttribute.getFilesize();
		if (StringUtil.isNotBlank(fileSize)) {
			if (fileSize.contains(",")) {
				List<String> fileSizeList = StringUtil.split(fileSize, ",");
				try {
					fileSizeStart = Integer.parseInt(fileSizeList.get(0));
					fileSizeEnd = Integer.parseInt(fileSizeList.get(1));
				} catch (NumberFormatException e) {
					return StateType.getResponseInfo(StateType.EXCEPTION.name(),
							"输入的文件大小不合法请确认");
				}
			} else {
				try {
					fileSizeStart = Integer.parseInt(fileSize);
				} catch (NumberFormatException e) {
					return StateType.getResponseInfo(StateType.EXCEPTION.name(),
							"输入的文件大小不合法请确认");
				}
			}
		}
		SqlOperator.Assembler assembler = SqlOperator.Assembler.newInstance();
		assembler.clean();
		assembler.addSql("SELECT source_path,file_suffix,file_id,storage_time,storage_date,original_update_date,"
				+ " original_update_time,file_md5,original_name,file_size,file_avro_path,file_avro_block,"
				+ " sfa.collect_set_id,sfa.source_id,sfa.agent_id,fcs_name,datasource_name,agent_name FROM  "
				+ Data_source.TableName + "  ds JOIN agent_info ai ON ds.SOURCE_ID = ai.SOURCE_ID"
				+ " JOIN " + File_collect_set.TableName + " fcs ON fcs.agent_id = ai.agent_id"
				+ " JOIN " + Source_file_attribute.TableName + " sfa ON sfa.SOURCE_ID = ds.SOURCE_ID"
				+ " and  sfa.AGENT_ID = ai.AGENT_ID and sfa.collect_set_id = fcs.FCS_ID "
				+ " where collect_type = ? ");
		assembler.addParam(AgentType.WenJianXiTong.name());
		assembler.addLikeParam("original_name", fileAttribute.getFilename());
		List<Object> sourceIdList = SqlOperator.queryOneColumnList(Dbo.db(), "select source_id from data_source ");
		assembler.addORParam("sfa.source_id", sourceIdList.toArray());
		// 7.判断文件大小是否为空，不为空加条件查询
		if (StringUtil.isNotBlank(fileSize)) {
			assembler.addSql(" and file_size >=").addParam(fileSizeStart);
			assembler.addSql(" and ile_size <=").addParam(fileSizeEnd);
		}
		// 8.判断文件后缀名是否为空，不为空加条件查询
		if (StringUtil.isNotBlank(fileAttribute.getFilesuffix())) {
			String[] split = fileAttribute.getFilesuffix().split(",");
			assembler.addORParam("file_suffix", split);
		}
		// 9.判断采集任务路径是否为空，不为空加条件查询
		String[] filepath = fileAttribute.getFilepath();
		if (filepath != null && filepath.length > 0) {
			assembler.addORParam("source_path", filepath);
		}
		// 10.判断采集任务id是否为空，不为空加条件查询
		Long[] fcs_id = fileAttribute.getFcs_id();
		if (fcs_id != null && fcs_id.length > 0) {
			assembler.addORParam("fcs_id", fcs_id);
		}
		assembler.addSql(" and storage_date=?").addParam(fileAttribute.getStoragedate());
		assembler.addSql(" and file_md5=?").addParam(fileAttribute.getFileMD5());
		assembler.addLikeParam("datasource_name", fileAttribute.getDs_name());
		assembler.addLikeParam("agent_name", fileAttribute.getAgent_name());
		assembler.addLikeParam("fcs_name", fileAttribute.getFcs_name());
		// 11.判断部门ID是否为空，不为空加条件查询
		Long[] dep_id = fileAttribute.getDep_id();
		if (dep_id != null && dep_id.length > 0) {
			assembler.addSql(" and  exists (select source_id from " + Source_relation_dep.TableName +
					" dep where dep.SOURCE_ID = ds.SOURCE_ID ").addORParam("dep_id", dep_id).addSql(" ) ");
		}
		// 12.设置分页
		assembler.addSql("limit " + num_count + " offset " + num_start);
		// 13.关联查询data_source、file_collect_set、data_store_reg三张表获取文件信息
		List<Map<String, Object>> fileAttrList = SqlOperator.queryList(Dbo.db(), assembler.sql(),
				assembler.params());
		// 14.获取摘要 fixme 待开发
		// 15.记录接口使用日志信息
		if (IsFlag.Shi == IsFlag.ofEnumByCode(isRecordInterfaceLog)) {
			insertInterfaceUseLog(checkParam.getUrl(), start, interface_use_log, userByToken,
					responseMap.get("status").toString());
		}
		// 16.判断文件属性信息是否为空，为空返回空集合，否则返回文件属性信息集合
		if (fileAttrList.isEmpty()) {
			return StateType.getResponseInfo(StateType.NORMAL.name(), new ArrayList<>());
		}
		return StateType.getResponseInfo(StateType.NORMAL.name(), fileAttrList);
	}

	@Method(desc = "sql查询接口", logicStep = "1.数据可访问权限处理方式：该方法通过user_id进行访问权限限制" +
			"2.token、接口权限检查" +
			"3.如果responseMap响应状态不为normal返回错误响应信息" +
			"4.获取当前用户ID" +
			"5.检查datatype，outtype参数是否合法" +
			"6.检查sql是否正确" +
			"7.根据sql获取表名的集合" +
			"8.校验表权限" +
			"9.获取表的有效列信息" +
			"10.如果为某些特定的用户,则不做字段的检测" +
			"11.使用sql解析获取列" +
			"12.判断查询列是否存在，支持t1.*,t2.*" +
			"13.存在，遍历列集合，判断列是否包含.,包含.说明是有别名获取别名后的列名称，否则直接获取列名称" +
			"14.判断列是否有权限" +
			"15.判断sql是否是以；结尾，如果是删除" +
			"16.根据sql查询数据" +
			"17.根据输出数据类型处理数据" +
			"18.记录接口使用日志信息")
	@Param(name = "sqlSearch", desc = "sql查询参数实体", range = "无限制", isBean = true)
	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
	@Return(desc = "返回接口响应信息", range = "无限制")
	@Override
	public Map<String, Object> sqlInterfaceSearch(SqlSearch sqlSearch, CheckParam checkParam) {
		// 1.数据可访问权限处理方式：该方法通过user_id进行访问权限限制
		long start = System.currentTimeMillis();
		Interface_use_log interface_use_log = new Interface_use_log();
		// 请求开始时间
		interface_use_log.setRequest_stime(DateUtil.getDateTime());
		// 2.token，接口权限检查
		Map<String, Object> responseMap = InterfaceCommon.checkTokenAndInterface(Dbo.db(), checkParam);
		if (!StateType.NORMAL.name().equals(responseMap.get("status").toString())) {
			responseMap.remove("token");
			return responseMap;
		}
		// 3.获取当前用户ID
		QueryInterfaceInfo userByToken = InterfaceManager.getUserByToken(responseMap.get("token").toString());
		// 4.检查参数
		responseMap = InterfaceCommon.checkType(sqlSearch.getDataType(), sqlSearch.getOutType(),
				sqlSearch.getAsynType(), sqlSearch.getBackurl(), sqlSearch.getFilepath(),
				sqlSearch.getFilename());
		// 5.如果responseMap响应状态不为normal返回错误响应信息
		if (!StateType.NORMAL.name().equals(responseMap.get("status").toString())) {
			return responseMap;
		}
		// 6.检查sql是否正确
		if (isParamExist(sqlSearch.getSql()))
			return StateType.getResponseInfo(StateType.SQL_IS_INCORRECT);
		// 7.根据sql获取表名的集合
		List<String> tableList = DruidParseQuerySql.parseSqlTableToList(sqlSearch.getSql());
		List<String> columnList = new ArrayList<>();
		for (String table : tableList) {
			// 8.校验表权限
			responseMap = InterfaceCommon.verifyTable(Dbo.db(), userByToken.getUser_id(), table);
			if (!StateType.NORMAL.name().equals(responseMap.get("status").toString())) {
				return responseMap;
			}
			// 9.获取表的有效列信息
			QueryInterfaceInfo userTableInfo = InterfaceManager.getUserTableInfo(Dbo.db(),
					userByToken.getUser_id(), table);
			columnList = StringUtil.split(userTableInfo.getTable_en_column().toLowerCase(), Constant.METAINFOSPLIT);
		}
		// 10.如果为某些特定的用户,则不做字段的检测
		if (!CommonVariables.AUTHORITY.contains(String.valueOf(userByToken.getUser_id()))) {
			// 11.使用sql解析获取列
			DruidParseQuerySql druidParseQuerySql = new DruidParseQuerySql(sqlSearch.getSql());
			List<String> sqlColumnList = druidParseQuerySql.parseSelectOriginalField();
			if (!columnList.isEmpty()) {
				// 12.判断查询列是否存在，支持t1.*,t2.*
				if (!sqlColumnList.contains(null)) {
					// 13.存在，遍历列集合，判断列是否包含.,包含.说明是有别名获取别名后的列名称，否则直接获取列名称
					for (String col : sqlColumnList) {
						if (col.contains(".")) {
							col = col.substring(col.indexOf(".") + 1).toLowerCase();
						} else {
							col = col.toLowerCase();
						}
						// 14.判断列是否有权限
						if (InterfaceCommon.columnIsExist(col, columnList)) {
							return StateType.getResponseInfo(StateType.COLUMN_DOES_NOT_EXIST.name(),
									"请求错误,查询列名" + col + "不存在");
						}
					}
				}
			}
		}
		// 15.判断sql是否是以；结尾，如果是删除
		String sqlNew = sqlSearch.getSql().trim();
		if (sqlNew.endsWith(";")) {
			sqlNew = sqlNew.substring(0, sqlNew.length() - 1);
		}
		// 16.根据sql查询数据
		responseMap = InterfaceCommon.getSqlData(Dbo.db(), sqlSearch.getOutType(),
				sqlSearch.getDataType(), sqlNew, userByToken.getUser_id(), null);
		// 17.根据输出数据类型处理数据
		responseMap = InterfaceCommon.operateInterfaceByType(sqlSearch.getDataType(),
				sqlSearch.getOutType(),
				sqlSearch.getAsynType(), sqlSearch.getBackurl(), sqlSearch.getFilepath(),
				sqlSearch.getFilename(), responseMap);
		// 18.记录接口使用日志信息
		if (IsFlag.Shi == IsFlag.ofEnumByCode(isRecordInterfaceLog)) {
			insertInterfaceUseLog(checkParam.getUrl(), start, interface_use_log, userByToken,
					responseMap.get("status").toString());
		}
		return responseMap;
	}

	@Method(desc = "UUID数据下载", logicStep = "")
	@Param(name = "uuid", desc = "uuid", range = "无限制")
	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
	@Return(desc = "返回接口响应信息", range = "无限制")
	@Override
	public Map<String, Object> uuidDownload(String uuid, CheckParam checkParam) {
		// 1.数据可访问权限处理方式：该方法通过user_id进行访问权限限制
		long start = System.currentTimeMillis();
		Interface_use_log interface_use_log = new Interface_use_log();
		// 请求开始时间
		interface_use_log.setRequest_stime(DateUtil.getDateTime());
		// 2.token、接口权限检查
		Map<String, Object> responseMap = InterfaceCommon.checkTokenAndInterface(Dbo.db(), checkParam);
		// 3.如果responseMap响应状态不为normal返回错误响应信息
		if (!StateType.NORMAL.name().equals(responseMap.get("status").toString())) {
			responseMap.remove("token");
			return responseMap;
		}
		QueryInterfaceInfo userByToken = InterfaceManager.getUserByToken(responseMap.get("token").toString());
		FileDownload fileDownload = new FileDownload();
		try {
			if (uuid != null) {
				Long user_id = InterfaceManager.getUserByToken(responseMap.get("token").toString()).getUser_id();
				// 下载文件
				HttpServletResponse response = fileDownload.downLoadFile(uuid, user_id);
				if (response.getStatus() < 300) {
					// 记录接口使用日志信息
					if (IsFlag.Shi == IsFlag.ofEnumByCode(isRecordInterfaceLog)) {
						insertInterfaceUseLog(checkParam.getUrl(), start, interface_use_log, userByToken,
								responseMap.get("status").toString());
					}
					return StateType.getResponseInfo(StateType.NORMAL.name(), "下载成功");
				} else {
					return StateType.getResponseInfo(StateType.EXCEPTION.name(), "下载失败");
				}
			} else {
				return StateType.getResponseInfo(StateType.UUID_NOT_NULL);
			}
		} catch (Exception e) {
			logger.error(e);
			return StateType.getResponseInfo(StateType.EXCEPTION.name(), "下载失败");
		}
	}

	@Method(desc = "rowkey查询", logicStep = "1.数据可访问权限处理方式：该方法通过user_id进行访问权限限制" +
			"2.token、接口权限检查" +
			"3.获取当前用户ID" +
			"4.检查参数合法性" +
			"5.根据rowkey，表名称、数据版本号获取hbase表信息,如果返回状态信息不为normal则返回错误响应信息" +
			"6.将数据写成对应的数据文件" +
			"7.判断是同步还是异步回调或者异步轮询" +
			"8.封装表英文名并返回接口响应信息")
	@Param(name = "rowKeySearch", desc = "rowkey查询参数实体", range = "无限制", isBean = true)
	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
	@Return(desc = "返回接口响应信息", range = "无限制")
	@Override
	public Map<String, Object> rowKeySearch(RowKeySearch rowKeySearch, CheckParam checkParam) {
		// 1.数据可访问权限处理方式：该方法通过user_id进行访问权限限制
		long start = System.currentTimeMillis();
		Interface_use_log interface_use_log = new Interface_use_log();
		// 请求开始时间
		interface_use_log.setRequest_stime(DateUtil.getDateTime());
		// 2.token，接口权限检查
		Map<String, Object> responseMap = InterfaceCommon.checkTokenAndInterface(Dbo.db(), checkParam);
		if (!StateType.NORMAL.name().equals(responseMap.get("status").toString())) {
			responseMap.remove("token");
			return responseMap;
		}
		// 3.获取当前用户ID
		String token = responseMap.get("token").toString();
		QueryInterfaceInfo userByToken = InterfaceManager.getUserByToken(token);
		// 4.检查参数合法性
		responseMap = InterfaceCommon.checkType(rowKeySearch.getDataType(), rowKeySearch.getOutType(),
				rowKeySearch.getAsynType(), rowKeySearch.getBackurl(), rowKeySearch.getFilepath(),
				rowKeySearch.getFilename());
		if (!StateType.NORMAL.name().equals(responseMap.get("status").toString())) {
			return responseMap;
		}
		List<LayerBean> hbaseLayerList = getLayerBeans(rowKeySearch.getEn_table());
		// 判断存储层类型为hbase的存储层是否存在
		if (hbaseLayerList.isEmpty()) {
			return StateType.getResponseInfo(StateType.TABLE_NOT_EXIST_ON_HBASE_STOREAGE);
		}
		// 如果有多个默认取第一个hbase配置
		LayerBean layerBean = hbaseLayerList.get(0);
		Map<String, String> layerAttr = layerBean.getLayerAttr();
		// 通过rowkey查询数据
		responseMap = QueryByRowkey.query(rowKeySearch.getEn_table(), rowKeySearch.getRowkey(),
				rowKeySearch.getEn_column(), rowKeySearch.getGet_version(), layerBean.getDsl_name(),
				layerAttr.get(StorageTypeKey.platform), layerAttr.get(StorageTypeKey.prncipal_name),
				layerAttr.get(StorageTypeKey.hadoop_user_name), userByToken.getUser_id(), Dbo.db());
		// 5.根据rowkey，表名称、数据版本号获取hbase表信息,如果返回状态信息不为normal则返回错误响应信息
		if (!StateType.NORMAL.name().equals(responseMap.get("status").toString())) {
			return responseMap;
		}
		// 6.将数据写成对应的数据文件
		LocalFile.writeFile(Dbo.db(), responseMap, rowKeySearch.getDataType(), rowKeySearch.getOutType(),
				userByToken.getUser_id());
		if (OutType.FILE == OutType.ofEnumByCode(rowKeySearch.getOutType())) {
			// 7.判断是同步还是异步回调或者异步轮询
			if (AsynType.ASYNCALLBACK == AsynType.ofEnumByCode(rowKeySearch.getAsynType())) {
				// 异步回调
				responseMap = InterfaceCommon.checkBackUrl(responseMap, rowKeySearch.getBackurl());
			} else if (AsynType.ASYNPOLLING == AsynType.ofEnumByCode(rowKeySearch.getAsynType())) {
				// 轮询
				responseMap = InterfaceCommon.createFile(responseMap, rowKeySearch.getFilepath(),
						rowKeySearch.getFilename());
			}
		}
		// 记录接口使用日志信息
		if (IsFlag.Shi == IsFlag.ofEnumByCode(isRecordInterfaceLog)) {
			insertInterfaceUseLog(checkParam.getUrl(), start, interface_use_log, userByToken,
					responseMap.get("status").toString());
		}
		// 8.封装表英文名并返回接口响应信息
		responseMap.put("enTable", rowKeySearch.getEn_table());
		return responseMap;
	}

	private List<LayerBean> getLayerBeans(String tableName) {
		// 通过表名获取存储层
		List<LayerBean> layerByTableList = ProcessingData.getLayerByTable(tableName,
				Dbo.db());
		// 获取存储层类型为hbase的存储层
		return layerByTableList.stream().filter(layerBean ->
				Store_type.HBASE == Store_type.ofEnumByCode(layerBean.getStore_type()))
				.collect(Collectors.toList());
	}

	@Method(desc = "单表数据批量更新接口", logicStep = "")
	@Param(name = "dataBatchUpdate", desc = "表数据批量更新参数实体", range = "无限制", isBean = true)
	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
	@Return(desc = "返回接口响应信息", range = "无限制")
	@Override
	public Map<String, Object> tableDataUpdate(DataBatchUpdate dataBatchUpdate, CheckParam
			checkParam) {
		return null;
	}

	@Method(desc = "Solr查询Hbase数据接口", logicStep = "1.校验表名是否存在" +
			"2.token，接口权限检查" +
			"3.检查参数" +
			"4.如果responseMap响应状态不为normal返回错误响应信息" +
			"5.获取当前用户表信息" +
			"5.判断表是否有使用权限" +
			"6.获取当前用户表信息" +
			"7.hbase+solr查询" +
			"8.返回按类型操作接口响应信息" +
			"9.记录接口使用日志")
	@Param(name = "hbaseSolr", desc = "HBaseSolr查询参数实体", range = "无限制", isBean = true)
	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
	@Return(desc = "返回接口响应信息", range = "无限制")
	@Override
	public Map<String, Object> hbaseSolrQuery(HbaseSolr hbaseSolr, CheckParam checkParam) {
		long start = System.currentTimeMillis();
		Interface_use_log interface_use_log = new Interface_use_log();
		// 请求开始时间
		interface_use_log.setRequest_stime(DateUtil.getDateTime());
		// 1.校验表名是否存在
		if (isParamExist(hbaseSolr.getTableName())) {
			return StateType.getResponseInfo(StateType.TABLE_NOT_EXISTENT);
		}
		// 2.token，接口权限检查
		Map<String, Object> responseMap = InterfaceCommon.checkTokenAndInterface(Dbo.db(), checkParam);
		if (!StateType.NORMAL.name().equals(responseMap.get("status").toString())) {
			responseMap.remove("token");
			return responseMap;
		}
		QueryInterfaceInfo userByToken = InterfaceManager.getUserByToken(responseMap.get("token").toString());
		// 3.检查参数
		responseMap = InterfaceCommon.checkType(hbaseSolr.getDataType(), hbaseSolr.getOutType(),
				hbaseSolr.getAsynType(), hbaseSolr.getBackurl(), hbaseSolr.getFilepath(),
				hbaseSolr.getFilename());
		// 4.如果responseMap响应状态不为normal返回错误响应信息
		if (!StateType.NORMAL.name().equals(responseMap.get("status").toString())) {
			return responseMap;
		}
		// 5.判断表是否有使用权限
		if (!InterfaceManager.existsTable(Dbo.db(), userByToken.getUser_id(), hbaseSolr.getTableName())) {
			return StateType.getResponseInfo(StateType.NO_USR_PERMISSIONS);
		}
		// 6.获取当前用户表信息
		QueryInterfaceInfo userTableInfo = InterfaceManager.getUserTableInfo(Dbo.db(),
				userByToken.getUser_id(), hbaseSolr.getTableName());
		String selectColumn = hbaseSolr.getSelectColumn();
		if (StringUtil.isBlank(selectColumn)) {
			selectColumn = userTableInfo.getTable_en_column();
		}
		if (StringUtil.isBlank(hbaseSolr.getWhereColumn())) {
			return StateType.getResponseInfo(StateType.CONDITION_ERROR);
		}
		List<LayerBean> hbaseLayerList = getLayerBeans(hbaseSolr.getTableName());
		// 判断存储层类型为hbase的存储层是否存在
		if (hbaseLayerList.isEmpty()) {
			return StateType.getResponseInfo(StateType.TABLE_NOT_EXIST_ON_HBASE_STOREAGE);
		}
		// 如果有多个默认取第一个hbase配置
		LayerBean layerBean = hbaseLayerList.get(0);
		Map<String, String> layerAttr = layerBean.getLayerAttr();
		// 7.hbase+solr查询
		responseMap = InterfaceCommon.getHbaseSolrQuery(hbaseSolr.getTableName(), hbaseSolr.getWhereColumn(),
				selectColumn, hbaseSolr.getStart(), hbaseSolr.getNum(), userTableInfo.getTable_en_column(),
				userTableInfo.getTable_type_name(), layerBean.getDsl_name(),
				layerAttr.get(StorageTypeKey.platform), layerAttr.get(StorageTypeKey.prncipal_name),
				layerAttr.get(StorageTypeKey.hadoop_user_name));
		// 8.返回按类型操作接口响应信息
		responseMap = InterfaceCommon.operateInterfaceByType(hbaseSolr.getDataType(),
				hbaseSolr.getOutType(), hbaseSolr.getAsynType(), hbaseSolr.getBackurl(),
				hbaseSolr.getFilepath(), hbaseSolr.getFilename(), responseMap);
		// 9.记录接口使用日志
		if (IsFlag.Shi == IsFlag.ofEnumByCode(isRecordInterfaceLog)) {
			insertInterfaceUseLog(checkParam.getUrl(), start, interface_use_log, userByToken,
					responseMap.get("status").toString());
		}
		return responseMap;
	}

	@Method(desc = "仪表板外部发布接口", logicStep = "2.获取组件ID" +
			"2.查询仪表盘信息" +
			"3.记录接口使用日志" +
			"4.返回仪表盘信息")
	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
	@Return(desc = "返回仪表盘信息", range = "无限制")
	public Map<String, Object> dashboardRelease(CheckParam checkParam) {
		long start = System.currentTimeMillis();
		Interface_use_log interface_use_log = new Interface_use_log();
		// 请求开始时间
		interface_use_log.setRequest_stime(DateUtil.getDateTime());
		// 1.token，接口权限检查
		Map<String, Object> responseMap = InterfaceCommon.checkTokenAndInterface(Dbo.db(), checkParam);
		if (!StateType.NORMAL.name().equals(responseMap.get("status").toString())) {
			responseMap.remove("token");
			return responseMap;
		}
		QueryInterfaceInfo userByToken = InterfaceManager.getUserByToken(responseMap.get("token").toString());
		String dashboard_id = new String(Base64.getDecoder().decode(checkParam.getInterface_code()));
		Auto_dashboard_info auto_dashboard_info = new Auto_dashboard_info();
		auto_dashboard_info.setDashboard_id(dashboard_id);
		try {
			// 2.查询仪表盘信息
			Map<String, Object> dashboardInfo = AutoAnalysisUtil.getDashboardInfoById(
					auto_dashboard_info.getDashboard_id(), Dbo.db());
			responseMap = StateType.getResponseInfo(StateType.NORMAL.name(), dashboardInfo);
		} catch (Exception e) {
			responseMap = StateType.getResponseInfo(StateType.EXCEPTION.name(),
					"查询仪表盘信息失败：" + e.getMessage());
		}
		// 3.记录接口使用日志
		if (IsFlag.Shi == IsFlag.ofEnumByCode(isRecordInterfaceLog)) {
			insertInterfaceUseLog(checkParam.getUrl(), start, interface_use_log, userByToken,
					responseMap.get("status").toString());
		}
		// 4.返回仪表盘信息
		return responseMap;
	}

	@Method(desc = "记录接口使用日志", logicStep = "1.获取接口使用信息" +
			"2.请求结束时间毫秒数" +
			"3.设置接口使用日志对象参数" +
			"4.接口使用日志信息记录入库")
	@Param(name = "url", desc = "接口请求url", range = "系统初始化时生成")
	@Param(name = "interface_use_log", desc = "接口使用日志实体对象", range = "与数据库表规则一致", isBean = true)
	@Param(name = "userByToken", desc = "根据token获取的用户信息对象", range = "无限制", isBean = true)
	@Param(name = "request_state", desc = "强求状态", range = "")
	@Param(name = "start", desc = "请求开始时间毫秒数", range = "无限制")
	private void insertInterfaceUseLog(String url, long start, Interface_use_log interface_use_log,
	                                   QueryInterfaceInfo userByToken, String request_state) {
		// 1.获取接口使用信息
		QueryInterfaceInfo interfaceUseInfo = InterfaceManager.getInterfaceUseInfo(userByToken.getUser_id(),
				url);
		// 2.请求结束时间毫秒数
		long end = System.currentTimeMillis();
		// 3.设置接口使用日志对象参数
		interface_use_log.setRequest_etime(DateUtil.getDateTime());
		interface_use_log.setUser_id(userByToken.getUser_id());
		interface_use_log.setUser_name(userByToken.getUser_name());
		interface_use_log.setInterface_use_id(interfaceUseInfo.getInterface_use_id());
		interface_use_log.setInterface_name(interfaceUseInfo.getInterface_name());
		interface_use_log.setResponse_time(end - start);
		interface_use_log.setRequest_state(request_state);
		// 获取请求时HttpClient还是浏览器
		String header = RequestUtil.getRequest().getHeader("User-Agent");
		String headerStr = header.substring(0, header.indexOf('/')).toUpperCase();
		interface_use_log.setRequest_type(headerStr);
		// userAgent中有很多获取请求信息的方法
		UserAgent userAgent = UserAgent.parseUserAgentString(header);
		// 浏览器类型(如果获取不到浏览器类型则说明是HttpClient请求)
		Browser browser = userAgent.getBrowser();
		if ("DOWNLOAD".equalsIgnoreCase(browser.toString())) {
			interface_use_log.setBrowser_type(headerStr);
		} else {
			interface_use_log.setBrowser_type(browser.toString());
		}
		// 浏览器版本
		Version browserVersion = userAgent.getBrowserVersion();
		interface_use_log.setBrowser_version(browserVersion == null ? header : browserVersion.toString());
		// 系统类型
		OperatingSystem operatingSystem = userAgent.getOperatingSystem();
		if ("UNKNOWN".equalsIgnoreCase(operatingSystem.toString())) {
			interface_use_log.setSystem_type(headerStr);
		} else {
			interface_use_log.setSystem_type(operatingSystem.toString());
		}
		// 获得客户端向服务器端传送数据的方法有GET、POST、PUT等类型
		String method = RequestUtil.getRequest().getMethod();
		interface_use_log.setRequest_mode(method);
		// 获得客户端的IP地址
		String remoteAddr = RequestUtil.getRequest().getRemoteAddr();
		interface_use_log.setRemoteaddr(remoteAddr);
		// 超文本传输协议-版本
		String protocol = RequestUtil.getRequest().getProtocol();
		interface_use_log.setProtocol(protocol);
		interface_use_log.setLog_id(PrimayKeyGener.getNextId());
		// 4.接口使用日志信息记录入库
		interface_use_log.add(Dbo.db());
	}
}

