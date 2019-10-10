package hrds.b.biz.datasource;

import com.alibaba.fastjson.TypeReference;
import fd.ng.core.utils.CodecUtil;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DefaultPageImpl;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.web.annotation.RequestBean;
import fd.ng.web.annotation.RequestParam;
import fd.ng.web.util.Dbo;
import fd.ng.web.util.FileUploadUtil;
import fd.ng.web.util.ResponseUtil;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.ApplyType;
import hrds.commons.codes.AuthType;
import hrds.commons.codes.UserType;
import hrds.commons.entity.*;
import hrds.commons.exception.AppSystemException;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.DboExecute;
import hrds.commons.utils.key.PrimayKeyGener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.*;

/**
 * 数据源增删改，导入、下载类
 *
 * @author dhw
 * @date 2019-09-03 16:44:25
 */
public class DataSourceAction extends BaseAction {
	private static final Logger logger = LogManager.getLogger();

	/**
	 * 查询数据源，部门、agent,申请审批,业务用户和采集用户,部门与数据源关系表信息，首页展示
	 * <p>
	 * 1.数据可访问权限处理方式，以下sql通过user_id关联进行权限检查
	 * 2.查询部门信息
	 * 3.查询数据源及Agent数
	 * 4.数据权限管理，分页查询数据源及部门关系信息
	 * 5.查询申请审批信息
	 * 6.创建存放数据源，部门、agent,申请审批,业务用户和采集用户,部门与数据源关系表信息的集合并将数据进行封装
	 * 7.设置权限类型
	 * 8.返回放数据源，部门、agent,申请审批,业务用户和采集用户,部门与数据源关系表信息的集合
	 *
	 * @param currPage int
	 *                 含义：分页查询，当前页
	 *                 取值范围：大于0的正整数
	 * @param pageSize int
	 *                 含义：分页查询每页显示条数
	 *                 取值范围：大于0的正整数
	 * @return java.util.Map
	 * 含义：存放数据源，部门、agent,申请审批,业务用户和采集用户,部门与数据源关系表信息的集合
	 * 取值范围：无限制
	 */
	public Map<String, Object> searchDataSourceInfo(@RequestParam(valueIfNull = "1") int currPage,
	                                                @RequestParam(valueIfNull = "5") int pageSize) {
		// 1.数据可访问权限处理方式，以下sql通过user_id关联进行权限检查
		// 2.查询部门信息
		List<Department_info> diList = Dbo.queryList(Department_info.class, "select * from department_info");
		// 3.查询数据源及Agent数
		List<Map<String, Object>> dsAiList = Dbo.queryList("select ds.source_id,ds.datasource_name," +
				"count(ai.Agent_id) sumAgent from data_source ds left join agent_info ai on ds.source_id=" +
				"ai.source_id where ds.create_user_id=? GROUP BY ds.source_id,ds.datasource_name", getUserId());
		// 4.数据权限管理，分页查询数据源及部门关系信息
		Result dataSourceRelationDep = searchSourceRelationDepForPage(currPage, pageSize);
		// 5.查询数据申请审批信息
		// 获取下面sql中所需source_id的数组
		List<Map<String, Object>> dataAuditList = getDataAuditList();
		// 6.创建存放数据源，部门、agent,申请审批,业务用户和采集用户,部门与数据源关系表信息的集合并将数据进行封装
		Map<String, Object> dataSourceInfoMap = new HashMap<>();
		dataSourceInfoMap.put("dataSourceRelationDep", dataSourceRelationDep.toList());
		dataSourceInfoMap.put("dataAudit", dataAuditList);
		dataSourceInfoMap.put("departmentInfo", diList);
		dataSourceInfoMap.put("dataSourceAndAgentCount", dsAiList);
		// 7.设置权限类型
		dataSourceInfoMap.put("yiCi", AuthType.YiCi.getCode());
		dataSourceInfoMap.put("yunXu", AuthType.YunXu.getCode());
		dataSourceInfoMap.put("buYunXu", AuthType.BuYunXu.getCode());
		dataSourceInfoMap.put("yiCi_zh", AuthType.ofValueByCode(AuthType.YiCi.getCode()));
		dataSourceInfoMap.put("yunXu_zh", AuthType.ofValueByCode(AuthType.YunXu.getCode()));
		dataSourceInfoMap.put("buYunXu_zh", AuthType.ofValueByCode(AuthType.BuYunXu.getCode()));
		dataSourceInfoMap.put("chaKan", ApplyType.ChaKan.getCode());
		dataSourceInfoMap.put("caiJiYongHu", UserType.CaiJiYongHu.getCode());
		dataSourceInfoMap.put("yeWu", UserType.YeWuYongHu.getCode());
		// 8.返回放数据源，部门、agent,申请审批,业务用户和采集用户,部门与数据源关系表信息的集合
		return dataSourceInfoMap;
	}

	/**
	 * 获取数据申请审批信息的集合
	 * <p>
	 * 1.数据可访问权限处理方式，这是一个私有方法不需要权限控制
	 * 2.查询data_source表所有source_id封装入数组
	 * 3.查询数据源申请审批信息集合并返回
	 *
	 * @return java.util.List
	 * 含义：存放数据申请审批信息的集合
	 * 取值范围：无限制
	 */
	private List<Map<String, Object>> getDataAuditList() {
		// 1.数据可访问权限处理方式，这是一个私有方法不需要权限控制
		// 2.查询data_source表所有source_id封装入数组
		List<Long> sourceIdList = Dbo.queryOneColumnList("select source_id from data_source");
		Long[] sourceId = new Long[sourceIdList.size()];
		for (int i = 0; i < sourceIdList.size(); ++i) {
			sourceId[i] = sourceIdList.get(i);
		}
		// 3.查询数据源申请审批信息集合并返回
		SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
		asmSql.addSql("select da.DA_ID,da.APPLY_DATE,da.APPLY_TIME,da.APPLY_TYPE,da.AUTH_TYPE,da.AUDIT_DATE," +
				"da.AUDIT_TIME,da.AUDIT_USERID,da.AUDIT_NAME,da.FILE_ID,da.USER_ID,da.DEP_ID,sfa.*,su.user_name" +
				" from data_auth da join sys_user su on da.user_id=su.user_id join source_file_attribute sfa" +
				" on da.file_id= sfa.file_id  where su.create_id in (select user_id from sys_user where user_type=?" +
				" or user_id = ?) ").addParam(UserType.XiTongGuanLiYuan.getCode()).addParam(getUserId())
				.addORParam("sfa.source_id", sourceId).addSql(" ORDER BY  da_id desc");
		return Dbo.queryList(asmSql.sql(), asmSql.params());
	}

	/**
	 * 数据权限管理，分页查询数据源及部门关系信息
	 * <p>
	 * 1.数据可访问权限处理方式，以下sql通过user_id关联进行权限检查
	 * 2.分页查询数据源及部门关系
	 * 3.判断数据源是否为空
	 * 4.循环数据源
	 * 5.创建存放数据源对应部门集合
	 * 6.查询获取数据源对应部门结果集
	 * 7.判断数据源对应的部门结果集是否为空
	 * 8.循环部门获取部门名称
	 * 9.将各个数据源对应的部门名称加入list
	 * 10.封装部门名称到结果集
	 * 11.返回结果集
	 *
	 * @param currPage int
	 *                 含义：当前页
	 *                 取值范围：大于0的正常数
	 * @param pageSize int
	 *                 含义：查询每页显示数
	 *                 取值范围：大于0的正常数
	 * @return fd.ng.db.resultset.Result
	 * 含义：返回分页查询数据源及部门关系
	 * 取值范围：无限制
	 */
	public Result searchSourceRelationDepForPage(@RequestParam(valueIfNull = "1") int currPage,
	                                             @RequestParam(valueIfNull = "5") int pageSize) {

		// 1.数据可访问权限处理方式，以下sql通过user_id关联进行权限检查
		// 2.分页查询数据源及部门关系
		Result dsResult = Dbo.queryPagedResult(new DefaultPageImpl(currPage, pageSize), " SELECT * from " +
				"data_source where create_user_id=? order by create_date desc,create_time desc", getUserId());
		// 3.判断数据源是否为空
		if (!dsResult.isEmpty()) {
			// 4.循环数据源
			for (int i = 0; i < dsResult.getRowCount(); i++) {
				// 5.创建存放数据源对应部门集合
				List<String> depList = new ArrayList<>();
				// 6.查询获取数据源对应部门结果集
				Result depResult = Dbo.queryResult(" select di.* from department_info di left join " +
								" source_relation_dep srd on di.dep_id = srd.dep_id where srd.source_id=?",
						dsResult.getLong(i, "source_id"));
				// 7.判断数据源对应的部门结果集是否为空
				if (!depResult.isEmpty()) {
					// 8.循环部门获取部门名称
					for (int j = 0; j < depResult.getRowCount(); j++) {
						// 9.将各个数据源对应的部门名称加入list
						depList.add(depResult.getString(j, "dep_name"));
					}
					// 10.封装部门名称到结果集
					StringBuilder sb = new StringBuilder();
					for (int n = 0; n < depList.size(); n++) {
						if (n != depList.size() - 1) {
							sb.append(depList.get(n)).append(",");
						} else {
							sb.append(depList.get(n));
						}
					}
					dsResult.setObject(i, "dep_name", sb.toString());
				}
			}
		}
		// 11.返回结果集
		return dsResult;
	}

	/**
	 * 数据权限管理，更新数据源关系部门信息
	 * <p>
	 * 1.数据可访问权限处理方式，通过sourceId与user_id关联检查
	 * 2.先删除数据源与部门关系信息,删除几条数据不确定，一个数据源对应多个部门，所以不能用DboExecute
	 * 3.建立新关系，保存source_relation_dep表信息
	 *
	 * @param sourceId long
	 *                 含义：data_source表主键ID
	 *                 取值范围：不为空的十位数字，新增时通过主键生成规则自动生成
	 * @param depIds   Long[]
	 *                 含义：存储source_relation_dep表主键ID的数组，定义为Long是为了
	 *                 判断是否为null
	 *                 取值范围：不为空以及不为空格
	 */
	public void updateAuditSourceRelationDep(long sourceId, Long[] depIds) {
		// 1.数据可访问权限处理方式，通过sourceId与user_id关联检查
		if (Dbo.queryNumber("select count(1) from data_source ds left join source_relation_dep srd" +
						" on ds.source_id=srd.source_id where ds.source_id=? and ds.create_user_id=?",
				sourceId, getUserId()).orElseThrow(() -> new BusinessException("sql查询错误！")) == 0) {
			throw new BusinessException("数据权限校验失败，数据不可访问！");
		}
		// 2.先删除数据源与部门关系信息,删除几条数据不确定，一个数据源对应多个部门，所以不能用DboExecute
		int num = Dbo.execute("delete from source_relation_dep where source_id=?",
				sourceId);
		if (num < 1) {
			throw new BusinessException("编辑时会先删除原数据源与部门关系信息，删除错旧关系时错误，" +
					"sourceId=" + sourceId);
		}
		// 3.建立新关系，保存source_relation_dep表信息
		saveSourceRelationDep(sourceId, depIds);
	}

	/**
	 * 数据管理列表，数据申请审批并返回最新数据申请审批数据信息
	 * <p>
	 * 1.数据可访问权限处理方式，根据user_id进行权限控制
	 * 2.根据数据权限设置ID查询数据申请审批信息
	 * 3.判断查询信息是否不存在
	 * 4.根据数据权限设置ID以及权限类型进行审批
	 * 5.查询审批后的最新数据申请审批信息并返回
	 *
	 * @param daId     long
	 *                 含义：数据权限设置ID，表data_auth表主键
	 *                 取值范围：不为空的十位数字，新增时自动生成
	 * @param authType String
	 *                 含义：权限类型，0-申请<ShenQing>，1-允许<YunXu>，2-不允许<BuYunXu>，3-一次<YiCi>
	 *                 取值范围：
	 * @return java.util.List
	 * 含义：存放数据申请审批信息的集合
	 * 取值范围：无限制
	 */
	public List<Map<String, Object>> dataAudit(long daId, String authType) {
		// 1.数据可访问权限处理方式，根据user_id进行权限控制
		// authType代码项合法性验证，如果不存在该方法直接会抛异常
		AuthType.ofEnumByCode(authType);
		// 2.根据数据权限设置ID查询数据申请审批信息
		Optional<Data_auth> dataAuth = Dbo.queryOneObject(Data_auth.class, "select * from data_auth " +
				" where da_id=? and user_id=?", daId, getUserId());
		// 3.判断查询信息是否不存在
		if (!dataAuth.isPresent()) {
			// 不存在值
			throw new BusinessException("此申请已取消或不存在！");
		}
		// 4.根据数据权限设置ID以及权限类型进行审批
		dataAuth.get().setAudit_date(DateUtil.getSysDate());
		dataAuth.get().setAudit_time(DateUtil.getSysTime());
		dataAuth.get().setAudit_userid(getUserId());
		dataAuth.get().setAudit_name(getUserName());
		dataAuth.get().setAuth_type(authType);
		dataAuth.get().setDa_id(daId);
		dataAuth.get().update(Dbo.db());
		// 5.查询审批后的最新数据申请审批信息并返回
		return getDataAuditList();

	}

	/**
	 * 根据权限设置ID进行权限回收并将最新数据申请审批信息返回
	 *
	 * @param daId long
	 *             含义：数据权限设置ID，表data_auth主键
	 *             取值范围：不为空的十位数字，新增时自动生成
	 * @return java.util.List
	 * 含义：存放数据申请审批信息的集合
	 * 取值范围：无限制
	 */
	public List<Map<String, Object>> deleteAudit(long daId) {
		// 1.数据可访问权限处理方式，根据user_id进行权限控制
		// 2.权限回收
		DboExecute.deletesOrThrow("权限回收成功!", "delete from data_auth " +
				" where da_id = ? and user_id=?", daId, getUserId());
		// 3.查询审批后的最新数据申请审批信息并返回
		return getDataAuditList();
	}

	/**
	 * 新增数据源
	 * <p>
	 * 1.数据可访问权限处理方式，新增时会设置创建用户ID，会获取当前用户ID，所以不需要权限验证
	 * 2.字段合法性检查
	 * 3.对data_source初始化一些非页面传值
	 * 4.保存data_source信息
	 * 5.保存source_relation_dep表信息
	 *
	 * @param dataSource data_source
	 *                   含义：data_source表实体类
	 *                   取值范围：datasource_name不为空以及不为空格，datasource_number不为空以及不为空格
	 *                   *                   ，source_remark可为空，其余字段取系统值
	 * @param depIds     Long[]
	 *                   含义：存储source_relation_dep表主键ID的数组，定义为Long是为了
	 *                   判断是否为null
	 *                   取值范围：不为空以及不为空格
	 */
	public void saveDataSource(@RequestBean Data_source dataSource, Long[] depIds) {
		// 1.数据可访问权限处理方式，新增时会设置创建用户ID，会获取当前用户ID，所以不需要权限验证
		// 2.字段做合法性检查
		fieldLegalityValidation(dataSource.getDatasource_name(), dataSource.getDatasource_number(),
				depIds);
		// 3.对data_source初始化一些非页面传值
		// 数据源主键ID
		dataSource.setSource_id(PrimayKeyGener.getNextId());
		// 数据源创建用户
		dataSource.setCreate_user_id(getUserId());
		// 数据源创建日期
		dataSource.setCreate_date(DateUtil.getSysDate());
		// 数据源创建时间
		dataSource.setCreate_time(DateUtil.getSysTime());
		// 4.保存data_source信息
		dataSource.add(Dbo.db());
		// 5.保存source_relation_dep表信息
		saveSourceRelationDep(dataSource.getSource_id(), depIds);
	}

	/**
	 * 更新数据源信息
	 * <p>
	 * 1.数据可访问权限处理方式，通过sourceId与user_id关联检查
	 * 2.验证sourceId是否合法
	 * 3.字段合法性检查
	 * 4.将data_source实体数据封装
	 * 5.更新数据源信息
	 * 6.先删除数据源与部门关系信息,删除几条数据不确定，一个数据源对应多个部门，所以不能用DboExecute
	 * 7.保存source_relation_dep表信息
	 *
	 * @param sourceId         long
	 *                         含义：data_source表主键，source_relation_dep表外键
	 *                         取值范围：不为空，10位数字
	 * @param sourceRemark     String
	 *                         含义：备注
	 *                         取值范围：没有限制
	 * @param datasourceName   String
	 *                         含义：数据源名称
	 *                         取值范围：不为空且不为空格
	 * @param datasourceNumber String
	 *                         含义：数据源编号
	 *                         取值范围：不为空且不为空格，长度不超过四位
	 * @param depIds           Long[]
	 *                         含义：存储source_relation_dep表主键ID的数组，定义为Long是为了
	 *                         判断是否为null
	 *                         取值范围：不为空以及不为空格
	 */
	public void updateDataSource(Long sourceId, String sourceRemark, String datasourceName,
	                             String datasourceNumber, Long[] depIds) {
		// 1.数据可访问权限处理方式，通过sourceId与user_id关联检查
		if (Dbo.queryNumber("select count(1) from data_source where source_id=? and create_user_id=?",
				sourceId, getUserId()).orElseThrow(() -> new BusinessException("sql查询错误！")) == 0) {
			throw new BusinessException("数据权限校验失败，数据不可访问！");
		}
		//sourceId长度
		// 2.验证sourceId是否合法
		if (sourceId == null) {
			throw new BusinessException("sourceId不为空以及不为空格，新增时自动生成");
		}
		// 3.字段合法性检查
		fieldLegalityValidation(datasourceName, datasourceNumber, depIds);
		// 4.将data_source实体数据封装
		Data_source dataSource = new Data_source();
		dataSource.setSource_id(sourceId);
		dataSource.setDatasource_name(datasourceName);
		dataSource.setDatasource_number(datasourceNumber);
		dataSource.setSource_remark(sourceRemark);
		// 5.更新数据源信息
		dataSource.update(Dbo.db());
		// 6.先删除数据源与部门关系信息,删除几条数据不确定，一个数据源对应多个部门，所以不能用DboExecute
		int num = Dbo.execute("delete from source_relation_dep where source_id=?",
				dataSource.getSource_id());
		if (num < 1) {
			throw new BusinessException("编辑时会先删除原数据源与部门关系信息，删除错旧关系时错误，" +
					"sourceId=" + dataSource.getSource_id());
		}
		// 7.保存source_relation_dep表信息
		saveSourceRelationDep(sourceId, depIds);
	}

	/**
	 * 字段合法性验证
	 * <p>
	 * 1.数据可访问权限处理方式，这是个私有方法，不会单独被调用，所以不需要权限验证
	 * 2.循环遍历获取source_relation_dep主键ID，验证dep_id合法性
	 * 3.验证datasource_name是否合法
	 * 4.datasource_number是否合法
	 * 5.更新前查询数据源编号是否已存在
	 *
	 * @param datasourceName   String
	 *                         含义：数据源名称
	 *                         取值范围：不为空及空格
	 * @param datasourceNumber String
	 *                         含义：数据源编号
	 *                         取值范围：不为空以及不为空格，长度不超过4
	 * @param depIds           Long[]
	 *                         含义：存储source_relation_dep主键ID(dep_id)的数组,定义为Long是为了
	 *                         判断是否为null
	 *                         取值范围：10为数字
	 */
	private void fieldLegalityValidation(String datasourceName, String datasourceNumber, Long[] depIds) {
		// 1.数据可访问权限处理方式，通过create_user_id检查
		// 2.循环遍历获取source_relation_dep主键ID，验证dep_id合法性
		for (Long depId : depIds) {
			if (depId == null) {
				throw new BusinessException("部门不能为空或者空格，新增部门时通过主键生成!");
			}
		}
		// 3.验证datasource_name是否合法
		if (StringUtil.isBlank(datasourceName)) {
			throw new BusinessException("数据源名称不能为空以及不能为空格，datasource_name=" + datasourceName);
		}
		// 数据源编号长度
		int len = 4;
		// 4.datasource_number是否合法
		if (StringUtil.isBlank(datasourceNumber) || datasourceNumber.length() > len) {
			throw new BusinessException("数据源编号不能为空以及不能为空格或数据源编号长度不能超过四位，" +
					"datasource_number=" + datasourceNumber);
		}
		// 5.更新前查询数据源编号是否已存在
		if (Dbo.queryNumber("select count(1) from " + Data_source.TableName + " where datasource_number=? "
				+ " and create_user_id=?", datasourceNumber, getUserId()).orElseThrow(() ->
				new BusinessException("sql查询错误！")) > 0) {
			// 判断数据源编号是否重复
			throw new BusinessException("数据源编号重复,datasource_number=" + datasourceNumber);
		}
	}

	/**
	 * 保存数据源与部门关系表信息
	 * <p>
	 * 1.数据可访问权限处理方式，这是一个私有方法，不会单独被调用，所以这里不需要做权限验证
	 * 2.验证传递的部门ID对应的部门信息是否存在
	 * 3.创建source_relation_dep对象，并封装数据
	 * 4.循环遍历存储部门ID的数组并保存source_relation_dep表信息
	 *
	 * @param sourceId long
	 *                 含义：source_relation_dep表外键ID
	 *                 取值范围，不能为空以及不能为空格
	 * @param depIds   Long[]
	 *                 含义：存放source_relation_dep表主键ID的数组
	 *                 取值范围：不为空
	 */
	private void saveSourceRelationDep(long sourceId, Long[] depIds) {
		// 1.数据可访问权限处理方式，这是一个私有方法，不会单独被调用，所以这里不需要做权限验证
		// 2.验证传递的部门ID对应的部门信息是否存在
		for (Long depId : depIds) {
			if (Dbo.queryNumber("select count(*) from department_info where dep_id=?", depId)
					.orElseThrow(() -> new BusinessException("sql查询错误")) == 0) {
				throw new BusinessException("该部门ID对应的部门不存在，请检查！");
			}
		}
		// 3.创建source_relation_dep对象，并封装数据
		Source_relation_dep sourceRelationDep = new Source_relation_dep();
		sourceRelationDep.setSource_id(sourceId);
		// 4.循环遍历存储部门ID的数组并保存source_relation_dep表信息
		for (long depId : depIds) {
			sourceRelationDep.setDep_id(depId);
			sourceRelationDep.add(Dbo.db());
		}
	}

	/**
	 * 根据数据源编号查询数据源及数据源与部门关系信息以及部门信息
	 *
	 * <p>
	 * 1.数据可访问权限处理方式，以下SQL关联sourceId与user_id检查
	 * 2.创建并封装数据源与部门关联信息以及部门信息集合
	 * 3.判断是新增还是编辑时查询回显数据，如果是新增，只查询部门信息，如果是编辑，还需查询数据源信息
	 * 3.1关联查询data_source表与source_relation_dep表信息
	 * 3.2.将数据源信息添加入Map
	 * 4.查询部门信息，不需要用户权限控制
	 * 5.将部门信息封装入Map
	 * 6.返回封装数据源与部门关联信息以及部门信息集合
	 *
	 * @param sourceId Long
	 *                 含义：data_source表主键，source_relation_dep表外键，定义为null是为了判断是否为null
	 *                 取值范围：不能为空或空格
	 * @return java.util.List
	 * 含义：返回关联查询data_source表与source_relation_dep表信息结果以及部门信息
	 * 取值范围：无限制
	 */
	public Map<String, Object> searchDataSource(@RequestParam(nullable = true) Long sourceId) {
		// 1.数据可访问权限处理方式，以下SQL关联sourceId与user_id检查
		// 2.创建并封装数据源与部门关联信息以及部门信息集合
		Map<String, Object> map = new HashMap<>();
		// 3.判断是新增还是编辑时查询回显数据，如果是新增，只查询部门信息，如果是编辑，还需查询数据源信息
		if (sourceId != null) {
			// 编辑时查询
			// 3.1关联查询data_source表与source_relation_dep表信息
			List<Map<String, Object>> dataSourceList = Dbo.queryList("select ds.*,srd.dep_id from " +
					" data_source ds join source_relation_dep srd on ds.source_id=srd.source_id " +
					" where ds.source_id = ? and ds.create_user_id=?", sourceId, getUserId());
			// 3.2.将数据源信息添加入Map
			map.put("dataSource", dataSourceList);
		}
		// 4.查询部门信息，不需要用户权限控制
		List<Department_info> departmentInfoList = Dbo.queryList(Department_info.class,
				"select * from department_info");
		// 5.将部门信息封装入Map
		map.put("departmentInfo", departmentInfoList);
		// 6.返回封装数据源与部门关联信息以及部门信息集合
		return map;
	}

	/**
	 * 删除数据源信息
	 * <p>
	 * 1.数据可访问权限处理方式，以下SQL关联sourceId与user_id检查
	 * 2.先查询该datasource下是否还有agent,有不能删除，没有，可以删除
	 * 3.删除data_source表信息
	 * 4.删除source_relation_dep信息,因为一个数据源可能对应多个部门，所以这里无法使用DboExecute的删除方法
	 * 5.删除source_relation_dep信息
	 *
	 * @param sourceId long
	 *                 含义：data_source表主键，agent_info表外键
	 *                 取值范围：不可为空以及不可为空格
	 */
	public void deleteDataSource(long sourceId) {

		// 1.数据可访问权限处理方式，以下SQL关联sourceId与user_id检查
		// 2.先查询该datasource下是否还有agent
		// FIXME: orElse用法有误，逻辑有问题，用orElseThrow  已解决
		if (Dbo.queryNumber("SELECT count(1) FROM agent_info  WHERE source_id=? and user_id=?", sourceId,
				getUserId()).orElseThrow(() -> new BusinessException("sql查询错误！")) > 0) {
			throw new BusinessException("此数据源下还有agent，不能删除,sourceId=" + sourceId);
		}
		// 3.删除data_source表信息
		DboExecute.deletesOrThrow("删除数据源信息表data_source失败，sourceId=" + sourceId,
				"delete from data_source where source_id=? and create_user_id=?", sourceId, getUserId());
		// 4.删除source_relation_dep信息,因为一个数据源可能对应多个部门，所以这里无法使用DboExecute的删除方法
		int srdNum = Dbo.execute("delete from source_relation_dep where source_id=?", sourceId);
		if (srdNum < 1) {
			// 如果数据源存在，那么部门一定存在，所以这里不需要判断等于0的情况
			throw new BusinessException("删除该数据源下source_relation_dep表数据错误，sourceId="
					+ sourceId);
		}
	}

	/**
	 * 查询数据采集用户信息
	 * <p>
	 * 1.数据可访问权限处理方式，此方法不需要权限验证
	 * 2.查询数据采集用户信息并返回查询结果
	 *
	 * @return java.util.List
	 * 含义：存放数据采集用户信息的集合
	 * 取值范围：无限制
	 */
	public List<Sys_user> searchDataCollectUser() {
		// 1.数据可访问权限处理方式，此方法不需要权限验证，没有用户访问限制
		// 2.查询数据采集用户信息并返回查询结果
		// FIXME: 为什么用union all以及为什么用like,注释说明      已解决
		//我们需要的是当前用户类型是数据采集（前半句sql查询结果）以及数据类型组包含数据采集的用户信息（后半句sql查询结果），所以用union all,
		// 用户类型组中数据可能是多个用户类型组成的也可能是单个的，所以用like
		return Dbo.queryList(Sys_user.class, "select * from sys_user where user_type=? and" +
						" dep_id=? union all select * from sys_user where usertype_group like ?",
				UserType.CaiJiYongHu.getCode(), getUser().getDepId(),
				"%" + UserType.CaiJiYongHu.getCode() + "%");
	}

	/**
	 * 导入数据源，数据源下载文件提供的文件中涉及到的所有表的数据导入数据库中对应的表中
	 *
	 * <p>
	 * 1.数据可访问权限处理方式，此方法不需要权限验证，不涉及用户权限
	 * 2.判断agent_ip是否是一个合法的ip
	 * 3.判断agent_port是否是一个有效的端口
	 * 4.验证userCollectId是否为null
	 * 5.通过文件名称获取文件
	 * 6.使用base64对数据进行解码
	 * 7.导入数据源数据，将涉及到的所有表的数据导入数据库中对应的表中
	 *
	 * @param agentIp       String
	 *                      含义：agent地址
	 *                      取值范围：不能为空，服务器ip地址
	 * @param agentPort     String
	 *                      含义：agent端口
	 *                      取值范围：1024-65535
	 * @param userCollectId long
	 *                      含义：数据采集用户ID
	 *                      取值范围：不能为空以及空格，页面传值
	 * @param file          String
	 *                      含义：上传文件名称（全路径），上传要导入的数据源
	 *                      取值范围：不能为空以及空格
	 */
	public void uploadFile(String agentIp, String agentPort, Long userCollectId, String file) {
		try {
			// 1.数据可访问权限处理方式，此方法不需要权限验证，不涉及用户权限
			// 2.判断agent_ip是否是一个合法的ip
			String[] split = agentIp.split("\\.");
			for (int i = 0; i < split.length; i++) {
				int temp = Integer.parseInt(split[i]);
				if (temp < 0 || temp > 255) {
					throw new BusinessException("agent_ip不是一个为空或空格的ip地址," +
							"agent_ip=" + agentIp);
				}
			}
			// 3.判断agent_port是否是一个有效的端口
			// 端口范围最小值
			int min = 1024;
			// 端口范围最大值
			int max = 65535;
			if (Integer.parseInt(agentPort) < min || Integer.parseInt(agentPort) > max) {
				throw new BusinessException("agent_port端口不是有效的端口，不在取值范围内，" +
						"agent_port=" + agentPort);
			}
			// 4.验证userCollectId是否为null
			if (userCollectId == null) {
				throw new BusinessException("userCollectId不为空且不为空格");
			}
			// 5.通过文件名称获取文件
			File uploadedFile = FileUploadUtil.getUploadedFile(file);
			// 6.使用base64解码
			String strTemp = new String(Base64.getDecoder().decode(Files.readAllBytes(uploadedFile.toPath())));
			// 7.导入数据源数据，将涉及到的所有表的数据导入数据库中对应的表中
			importDataSource(strTemp, agentIp, agentPort, userCollectId, getUserId());
		} catch (Exception e) {
			throw new AppSystemException(e);
		}
	}

	/**
	 * 导入数据源数据，将涉及到的所有表的数据导入数据库中对应的表中(修改中,未完成测试）
	 *
	 * <p>
	 * 1.获取文件对应所有表信息的map
	 * 2.遍历并解析拿到每张表的信息，map里封装的是所有数据源相关的表信息
	 * 3.获取数据源data_source信息并插入数据库
	 * 4.将department_info表数据插入数据库
	 * 5.将source_relation_dep数据插入数据库
	 * 6.将agent_info表数据插入数据库
	 * 7.将Agent_down_info表数据插入数据库
	 * 8.将collect_job_classify表数据插入数据库
	 * 9.将ftp采集设置ftp_collect表数据插入数据库
	 * 10.将ftp已传输表ftp_transfered表数据插入数据库
	 * 11.将ftp目录表ftp_folder表数据插入数据库
	 * 12.将对象采集设置object_collect表数据插入数据库
	 * 13.将对象采集对应信息object_collect_task表数据插入数据库
	 * 14.将对象采集存储设置object_storage表数据插入数据库
	 * 15.将对象采集结构信息object_collect_struct表数据插入数据库
	 * 16.将数据库设置database_set表数据插入数据库
	 * 17.将文件系统设置file_collect_set表数据插入数据库
	 * 18.将文件源设置file_source表数据插入数据库
	 * 19.将信号文件入库信息signal_file表数据插入数据库
	 * 20.将数据库对应的表table_info表数据插入数据库
	 * 21.将列合并信息column_merge表数据插入数据库
	 * 22.将表存储信息table_storage_info表数据插入数据库
	 * 23.将表清洗参数信息table_clean表数据插入数据库
	 * 24.将表对应的字段table_column表数据插入数据库
	 * 25.将列清洗参数信息column_clean表数据插入数据库
	 * 26.将列拆分信息表column_split表数据插入数据库
	 *
	 * @param strTemp       String
	 *                      含义：涉及数据源文件下载相关的所有表进行base64编码后的信息
	 * @param agentIp       String
	 *                      含义：agent地址
	 *                      取值范围：不能为空，服务器ip地址
	 * @param agentPort     String
	 *                      含义：agent端口
	 *                      取值范围：1024-65535
	 * @param userCollectId long
	 *                      含义：数据采集用户ID，指定谁可以查看该用户对应表信息
	 *                      取值范围：不能为空以及空格，长度不超过10
	 * @param userId        long
	 *                      含义：data_source表数据源创建用户ID，代表数据是由谁创建的
	 *                      取值范围：不为空以及不为空格，长度不超过10位
	 */
	private void importDataSource(String strTemp, String agentIp, String agentPort, long
			userCollectId, long userId) {
		Type type = new TypeReference<Map<String, Object>>() {
		}.getType();
		// 1.获取文件对应所有表信息的map
		Map<String, Object> collectMap = JsonUtil.toObject(strTemp, type);
		// 2.遍历并解析拿到每张表的信息，map里封装的是所有数据源相关的表信息
		// 3.获取数据源data_source信息并插入数据库
		Data_source dataSource = getDataSource(userId, collectMap);
		// 4.将department_info表数据插入数据库
		List<Department_info> departmentInfoList = addDepartmentInfo(collectMap);
		// 5.将source_relation_dep数据插入数据库
		addSourceRelationDep(collectMap, dataSource, departmentInfoList);
		// 6.将agent_info表数据插入数据库
		addAgentInfo(agentIp, agentPort, userCollectId, collectMap);
		// 7.将Agent_down_info表数据插入数据库
		addAgentDownInfo(agentIp, agentPort, userCollectId, collectMap);
		// 8.将collect_job_classify表数据插入数据库
		addCollectJobClassify(userId, collectMap);
		// 9.将ftp采集设置ftp_collect表数据插入数据库
		addFtpCollect(collectMap);
		// 10.将ftp已传输表ftp_transfered表数据插入数据库
		addFtpTransfered(collectMap);
		// 11.将ftp目录表ftp_folder表数据插入数据库
		addFtpFolder(collectMap);
		// 12.将对象采集设置object_collect表数据插入数据库
		addObjectCollect(collectMap);
		// 13.将对象采集对应信息object_collect_task表数据插入数据库
		addObjectCollectTask(collectMap);
		// 14.将对象采集存储设置object_storage表数据插入数据库
		addObjectStorage(collectMap);
		// 15.将对象采集结构信息object_collect_struct表数据插入数据库
		addObjectCollectStruct(collectMap);
		// 16.将数据库设置database_set表数据插入数据库
		addDatabaseSet(collectMap);
		// 17.将文件系统设置file_collect_set表数据插入数据库
		addFileCollectSet(collectMap);
		// 18.将文件源设置file_source表数据插入数据库
		addFileSource(collectMap);
		// 19.将信号文件入库信息signal_file表数据插入数据库
		addSignalFile(collectMap);
		// 20.将数据库对应的表table_info表数据插入数据库
		addTableInfo(collectMap);
		// 21.将列合并信息column_merge表数据插入数据库
		addColumnMerge(collectMap);
		// 22.将表存储信息table_storage_info表数据插入数据库
		addTableStorageInfo(collectMap);
		// 23.将表清洗参数信息table_clean表数据插入数据库
		addTableClean(collectMap);
		// 24.将表对应的字段table_column表数据插入数据库
		addTableColumn(collectMap);
		// 25.将列清洗参数信息column_clean表数据插入数据库
		addColumnClean(collectMap);
		// 26.将列拆分信息表column_split表数据插入数据库
		addColumnSplit(collectMap);
	}

	/**
	 * 将table_storage_info表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为table_storage_info对应表数据
	 * 3.获取表存储信息table_storage_info信息
	 * 4.将table_storage_info表数据循环入数据库
	 *
	 * @param collectMap java.util.Map
	 *                   含义：所有表数据的map的实体
	 *                   取值范围：不为空
	 */
	private void addTableStorageInfo(Map<String, Object> collectMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为table_storage_info对应表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			String tsi = "tableStorageInfo";
			if (tsi.equals(entry.getKey())) {
				Type tsiType = new TypeReference<List<Table_storage_info>>() {
				}.getType();
				// 3.获取表存储信息table_storage_info信息
				List<Table_storage_info> tsiList = JsonUtil.toObject(entry.getValue().toString(), tsiType);
				// 4.将table_storage_info表数据循环入数据库
				for (Table_storage_info tableStorageInfo : tsiList) {
					tableStorageInfo.setStorage_id(PrimayKeyGener.getNextId());
					tableStorageInfo.add(Dbo.db());
				}
			}
		}
	}

	/**
	 * 将column_merge表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为column_merge对应表数据
	 * 3.获取列合并信息column_merge信息
	 * 4.将column_merge表数据循环入数据库
	 *
	 * @param collectMap java.util.Map
	 *                   含义：所有表数据的map的实体
	 *                   取值范围：不为空
	 */
	private void addColumnMerge(Map<String, Object> collectMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为column_merge对应表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			String cm = "columnMerge";
			if (cm.equals(entry.getKey())) {
				Type cmType = new TypeReference<List<Column_merge>>() {
				}.getType();
				// 3.获取列合并信息column_merge信息
				List<Column_merge> columnMergeList = JsonUtil.toObject(entry.getValue().toString(), cmType);
				// 4.将column_merge表数据循环入数据库
				for (Column_merge columnMerge : columnMergeList) {
					columnMerge.setCol_merge_id(PrimayKeyGener.getNextId());
					columnMerge.add(Dbo.db());
				}
			}
		}
	}

	/**
	 * 将column_split表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为column_split对应表数据
	 * 3.获取列拆分信息表column_split信息
	 * 4.将column_split表数据循环入数据库
	 *
	 * @param collectMap java.util.Map
	 *                   含义：所有表数据的map的实体
	 *                   取值范围：不为空
	 */
	private void addColumnSplit(Map<String, Object> collectMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为column_split对应表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			String cs = "columnSplit";
			if (cs.equals(entry.getKey())) {
				Type csType = new TypeReference<List<Column_split>>() {
				}.getType();
				// 3.获取列拆分信息表column_split信息
				List<Column_split> columnSplitList = JsonUtil.toObject(entry.getValue().toString(), csType);
				// 4.将column_split表数据循环入数据库
				for (Column_split columnSplit : columnSplitList) {
					columnSplit.setCol_split_id(PrimayKeyGener.getNextId());
					columnSplit.add(Dbo.db());
				}
			}
		}
	}

	/**
	 * 将column_clean表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为column_clean对应表数据
	 * 3.获取列清洗参数信息column_clean信息
	 * 4.将column_clean表数据循环入数据库
	 *
	 * @param collectMap java.util.Map
	 *                   含义：所有表数据的map的实体
	 *                   取值范围：不为空
	 */
	private void addColumnClean(Map<String, Object> collectMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为column_clean对应表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			String cc = "columnClean";
			if (cc.equals(entry.getKey())) {
				Type ccType = new TypeReference<List<Column_clean>>() {
				}.getType();
				// 3.获取列清洗参数信息column_clean信息
				List<Column_clean> columnCleanList = JsonUtil.toObject(entry.getValue().toString(), ccType);
				// 4.将column_clean表数据循环入数据库
				for (Column_clean columnClean : columnCleanList) {
					columnClean.setCol_clean_id(PrimayKeyGener.getNextId());
					columnClean.add(Dbo.db());
				}
			}
		}
	}

	/**
	 * 将table_column表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为table_column对应表数据
	 * 3.获取表对应的字段table_column信息
	 * 4.将table_column表数据循环入数据库
	 *
	 * @param collectMap java.util.Map
	 *                   含义：所有表数据的map的实体
	 *                   取值范围：不为空
	 */
	private void addTableColumn(Map<String, Object> collectMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为table_column对应表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			String tc = "tableColumn";
			if (tc.equals(entry.getKey())) {
				Type tcnType = new TypeReference<List<Table_column>>() {
				}.getType();
				// 3.获取表对应的字段table_column信息
				List<Table_column> tableColumnList = JsonUtil.toObject(entry.getValue().toString(), tcnType);
				// 4.将table_column表数据循环入数据库
				for (Table_column tableColumn : tableColumnList) {
					tableColumn.setColumn_id(PrimayKeyGener.getNextId());
					tableColumn.add(Dbo.db());
				}
			}
		}
	}

	/**
	 * 将table_clean表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为table_clean对应表数据
	 * 3.获取表清洗参数信息table_clean信息
	 * 4.将table_clean表数据循环入数据库
	 *
	 * @param collectMap java.util.Map
	 *                   含义：所有表数据的map的实体
	 *                   取值范围：不为空
	 */
	private void addTableClean(Map<String, Object> collectMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为table_clean对应表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			String tc = "tableClean";
			if (tc.equals(entry.getKey())) {
				Type tcType = new TypeReference<List<Table_clean>>() {
				}.getType();
				// 3.获取表清洗参数信息table_clean信息
				List<Table_clean> tableCleanList = JsonUtil.toObject(entry.getValue().toString(), tcType);
				// 4.将table_clean表数据循环入数据库
				for (Table_clean tableClean : tableCleanList) {
					tableClean.setTable_clean_id(PrimayKeyGener.getNextId());
					tableClean.add(Dbo.db());
				}
			}
		}
	}

	/**
	 * 将table_info表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为table_info对应表数据
	 * 3.获取数据库对应的表table_info信息
	 * 4.将table_info表数据循环入数据库
	 *
	 * @param collectMap java.util.Map
	 *                   含义：所有表数据的map的实体
	 *                   取值范围：不为空
	 */
	private void addTableInfo(Map<String, Object> collectMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为table_info对应表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			String ti = "tableInfo";
			if (ti.equals(entry.getKey())) {
				Type tiType = new TypeReference<List<Table_info>>() {
				}.getType();
				// 3.获取数据库对应的表table_info信息
				List<Table_info> tableInfoList = JsonUtil.toObject(entry.getValue().toString(), tiType);
				// 4.将table_info表数据循环入数据库
				for (Table_info tableInfo : tableInfoList) {
					tableInfo.setTable_id(PrimayKeyGener.getNextId());
					tableInfo.add(Dbo.db());
				}
			}
		}
	}

	/**
	 * 将signal_file表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为signal_file对应表数据
	 * 3.获取信号文件入库信息signal_file信息
	 * 4.将signal_file表数据循环入数据库
	 *
	 * @param collectMap java.util.Map
	 *                   含义：所有表数据的map的实体
	 *                   取值范围：不为空
	 */
	private void addSignalFile(Map<String, Object> collectMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为signal_file对应表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			String sf = "signalFile";
			if (sf.equals(entry.getKey())) {
				Type sfType = new TypeReference<List<Signal_file>>() {
				}.getType();
				// 3.获取信号文件入库信息signal_file信息
				List<Signal_file> signalFileList = JsonUtil.toObject(entry.getValue().toString(), sfType);
				// 4.将signal_file表数据循环入数据库
				for (Signal_file signalFile : signalFileList) {
					signalFile.setSignal_id(PrimayKeyGener.getNextId());
					signalFile.add(Dbo.db());
				}
			}
		}
	}

	/**
	 * 将file_source表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为file_source对应表数据
	 * 3.获取文件源设置file_source信息
	 * 4.将file_source表数据循环入数据库
	 *
	 * @param collectMap java.util.Map
	 *                   含义：所有表数据的map的实体
	 *                   取值范围：不为空
	 */
	private void addFileSource(Map<String, Object> collectMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为file_source对应表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			String fs = "fileSource";
			if (fs.equals(entry.getKey())) {
				Type fsType = new TypeReference<List<File_source>>() {
				}.getType();
				// 3.获取文件源设置file_source信息
				List<File_source> fileSourceList = JsonUtil.toObject(entry.getValue().toString(), fsType);
				// 4.将file_source表数据循环入数据库
				for (File_source fileSource : fileSourceList) {
					fileSource.setFile_source_id(PrimayKeyGener.getNextId());
					fileSource.add(Dbo.db());
				}
			}
		}
	}

	/**
	 * 将file_collect_set表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为file_collect_set对应表数据
	 * 3.获取文件系统设置file_collect_set信息
	 * 4.将file_collect_set表数据循环入数据库
	 *
	 * @param collectMap java.util.Map
	 *                   含义：所有表数据的map的实体
	 *                   取值范围：不为空
	 */
	private void addFileCollectSet(Map<String, Object> collectMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为file_collect_set对应表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			String fcs = "fileCollectSet";
			if (fcs.equals(entry.getKey())) {
				Type fcsType = new TypeReference<List<File_collect_set>>() {
				}.getType();
				// 3.获取文件系统设置file_collect_set信息
				List<File_collect_set> fcsList = JsonUtil.toObject(entry.getValue().toString(), fcsType);
				// 4.将file_collect_set表数据循环入数据库
				for (File_collect_set fileCollectSet : fcsList) {
					fileCollectSet.setFcs_id(PrimayKeyGener.getNextId());
					fileCollectSet.add(Dbo.db());
				}
			}
		}
	}

	/**
	 * 将database_set表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为database_set对应表数据
	 * 3.获取数据库设置database_set信息
	 * 4.将database_set表数据循环入数据库
	 *
	 * @param collectMap java.util.Map
	 *                   含义：所有表数据的map的实体
	 *                   取值范围：不为空
	 */
	private void addDatabaseSet(Map<String, Object> collectMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为database_set对应表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			String ds = "databaseSet";
			if (ds.equals(entry.getKey())) {
				Type dsType = new TypeReference<List<Database_set>>() {
				}.getType();
				// 3.获取数据库设置database_set信息
				List<Database_set> databaseSetList = JsonUtil.toObject(entry.getValue().toString(), dsType);
				// 4.将database_set表数据循环入数据库
				for (Database_set databaseSet : databaseSetList) {
					databaseSet.setDatabase_id(PrimayKeyGener.getNextId());
					databaseSet.add(Dbo.db());
				}
			}
		}
	}

	/**
	 * 将object_collect_struct表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为object_collect_struct对应表数据
	 * 3.获取对象采集结构信息object_collect_struct信息
	 * 4.将object_collect_struct表数据循环入数据库
	 *
	 * @param collectMap java.util.Map
	 *                   含义：所有表数据的map的实体
	 *                   取值范围：不为空
	 */
	private void addObjectCollectStruct(Map<String, Object> collectMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为object_collect_struct对应表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			String ocs = "objectCollectStruct";
			if (ocs.equals(entry.getKey())) {
				Type ocsType = new TypeReference<List<Object_collect_struct>>() {
				}.getType();
				// 3.获取对象采集结构信息object_collect_struct信息
				List<Object_collect_struct> ocsList = JsonUtil.toObject(entry.getValue().toString(), ocsType);
				// 4.将object_collect_struct表数据循环入数据库
				for (Object_collect_struct objectCollectStruct : ocsList) {
					objectCollectStruct.setStruct_id(PrimayKeyGener.getNextId());
					objectCollectStruct.add(Dbo.db());
				}
			}
		}
	}

	/**
	 * 将object_storage表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为object_storage对应表数据
	 * 3.获取tp采集设置object_storage信息
	 * 4.将object_storage表数据循环入数据库
	 *
	 * @param collectMap java.util.Map
	 *                   含义：所有表数据的map的实体
	 *                   取值范围：不为空
	 */
	private void addObjectStorage(Map<String, Object> collectMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为object_storage对应表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			String os = "objectStorage";
			if (os.equals(entry.getKey())) {
				Type osType = new TypeReference<List<Object_storage>>() {
				}.getType();
				// 3.获取对象采集存储设置object_storage信息
				List<Object_storage> objectStorageList = JsonUtil.toObject(entry.getValue().toString(), osType);
				// 4.将object_storage表数据循环入数据库
				for (Object_storage objectStorage : objectStorageList) {
					objectStorage.setObj_stid(PrimayKeyGener.getNextId());
					objectStorage.add(Dbo.db());
				}
			}
		}
	}

	/**
	 * 将object_collect_task表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为object_collect_task对应表数据
	 * 3.获取对象采集对应信息object_collect_task信息
	 * 4.将object_collect_task表数据循环入数据库
	 *
	 * @param collectMap java.util.Map
	 *                   含义：所有表数据的map的实体
	 *                   取值范围：不为空
	 */
	private void addObjectCollectTask(Map<String, Object> collectMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为object_collect_task对应表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			String oct = "objectCollectTask";
			if (oct.equals(entry.getKey())) {
				Type octType = new TypeReference<List<Object_collect_task>>() {
				}.getType();
				// 3.获取对象采集对应信息object_collect_task信息
				List<Object_collect_task> octList = JsonUtil.toObject(entry.getValue().toString(), octType);
				// 4.将object_collect_task表数据循环入数据库
				for (Object_collect_task objectCollectTask : octList) {
					objectCollectTask.setOcs_id(PrimayKeyGener.getNextId());
					objectCollectTask.add(Dbo.db());
				}
			}
		}
	}

	/**
	 * 将object_collect表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为object_collect对应表数据
	 * 3.获取对象采集设置object_collect信息
	 * 4.将object_collect表数据循环入数据库
	 *
	 * @param collectMap java.util.Map
	 *                   含义：所有表数据的map的实体
	 *                   取值范围：不为空
	 */
	private void addObjectCollect(Map<String, Object> collectMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为object_collect对应表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			String oc = "objectCollect";
			if (oc.equals(entry.getKey())) {
				Type ocType = new TypeReference<List<Object_collect>>() {
				}.getType();
				// 3.获取对象采集设置object_collect信息
				List<Object_collect> objectCollectList = JsonUtil.toObject(entry.getValue().toString(), ocType);
				// 4 .将object_collect表数据循环入数据库
				for (Object_collect objectCollect : objectCollectList) {
					objectCollect.setOdc_id(PrimayKeyGener.getNextId());
					objectCollect.add(Dbo.db());
				}
			}
		}
	}

	/**
	 * 将ftp_folder表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为ftp_folder对应表数据
	 * 3.获取ftp目录表ftp_folder信息
	 * 4.将ftp_folder表数据循环入数据库
	 *
	 * @param collectMap java.util.Map
	 *                   含义：所有表数据的map的实体
	 *                   取值范围：不为空
	 */
	private void addFtpFolder(Map<String, Object> collectMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为ftp_folder对应表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			String ff = "ftpFolder";
			if (ff.equals(entry.getKey())) {
				Type ffType = new TypeReference<List<Ftp_folder>>() {
				}.getType();
				// 3.获取ftp目录表ftp_folder信息
				List<Ftp_folder> ftpFolderList = JsonUtil.toObject(entry.getValue().toString(), ffType);
				// 4.将ftp_folder表数据循环入数据库
				for (Ftp_folder ftpFolder : ftpFolderList) {
					ftpFolder.setFtp_folder_id(PrimayKeyGener.getNextId());
					ftpFolder.add(Dbo.db());
				}
			}
		}
	}

	/**
	 * 将ftp_transfered表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为ftp_transfered对应表数据
	 * 3.获取tp采集设置ftp_transfered信息
	 * 4.将ftp_transfered表数据循环入数据库
	 *
	 * @param collectMap java.util.Map
	 *                   含义：所有表数据的map的实体
	 *                   取值范围：不为空
	 */
	private void addFtpTransfered(Map<String, Object> collectMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为ftp_transfered对应表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			String ft = "ftpTransfered";
			if (ft.equals(entry.getKey())) {
				Type ftType = new TypeReference<List<Ftp_transfered>>() {
				}.getType();
				// 3.获取ftp已传输表ftp_transfered信息
				List<Ftp_transfered> transferList = JsonUtil.toObject(entry.getValue().toString(), ftType);
				// 4.将ftp_transfered表数据循环入数据库
				for (Ftp_transfered ftpTransfered : transferList) {
					ftpTransfered.setFtp_transfered_id(PrimayKeyGener.getNextId());
					ftpTransfered.add(Dbo.db());
				}
			}
		}
	}

	/**
	 * 将ftp_collect表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为ftp_collect对应表数据
	 * 3.获取tp采集设置ftp_collect信息
	 * 4.将ftp_collect表数据循环入数据库
	 *
	 * @param collectMap java.util.Map
	 *                   含义：所有表数据的map的实体
	 *                   取值范围：不为空
	 */
	private void addFtpCollect(Map<String, Object> collectMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为ftp_collect对应表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			String fc = "ftpCollect";
			if (fc.equals(entry.getKey())) {
				Type fcType = new TypeReference<List<Ftp_collect>>() {
				}.getType();
				// 3.获取tp采集设置ftp_collect信息
				List<Ftp_collect> ftpCollectList = JsonUtil.toObject(entry.getValue().toString(), fcType);
				// 4.将ftp_collect表数据循环入数据库
				for (Ftp_collect ftpCollect : ftpCollectList) {
					ftpCollect.setFtp_id(PrimayKeyGener.getNextId());
					ftpCollect.add(Dbo.db());
				}
			}
		}
	}

	/**
	 * 将collect_job_classify表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为collect_job_classify对应表数据
	 * 3.获取采集任务分类表collect_job_classify信息
	 * 4.将collect_job_classify表数据循环入数据库
	 *
	 * @param userCollectId long
	 *                      含义：数据采集用户，代表此数据属于哪个用户
	 *                      取值范围：不为空以及不为空格，长度不超过10位
	 * @param collectMap    java.util.Map
	 *                      含义：所有表数据的map的实体
	 *                      取值范围：不为空
	 */
	private void addCollectJobClassify(long userCollectId, Map<String, Object> collectMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为collect_job_classify对应表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			String cjc = "collectJobClassify";
			if (cjc.equals(entry.getKey())) {
				Type cjcType = new TypeReference<List<Collect_job_classify>>() {
				}.getType();
				// 3.获取采集任务分类表collect_job_classify信息
				List<Collect_job_classify> cjcList = JsonUtil.toObject(entry.getValue().toString(), cjcType);
				// 4.将collect_job_classify表数据循环入数据库
				for (Collect_job_classify collectJobClassify : cjcList) {
					collectJobClassify.setClassify_id(PrimayKeyGener.getNextId());
					collectJobClassify.setUser_id(userCollectId);
					collectJobClassify.add(Dbo.db());
				}
			}
		}
	}

	/**
	 * 将agent_down_info表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为agent_down_info对应表数据
	 * 3.获取agent_down_info表数据
	 * 4.将agent_down_info表数据循环入数据库
	 *
	 * @param agentIp       String
	 *                      含义：agent地址
	 *                      取值范围：不能为空，服务器ip地址
	 * @param agentPort     String
	 *                      含义：agent端口
	 *                      取值范围：1024-65535
	 * @param userCollectId long
	 *                      含义：数据采集用户，代表此数据属于哪个用户
	 *                      取值范围：不为空以及不为空格，长度不超过10位
	 * @param collectMap    java.util.Map
	 *                      含义：所有表数据的map的实体
	 *                      取值范围：不为空
	 */
	private void addAgentDownInfo(String agentIp, String agentPort, long userCollectId,
	                              Map<String, Object> collectMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为agent_down_info对应表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			String adi = "agentDownInfo";
			if (adi.equals(entry.getKey())) {
				Type adiType = new TypeReference<List<Agent_down_info>>() {
				}.getType();
				// 3.获取agent_down_info表数据
				List<Agent_down_info> agentDownInfoList = JsonUtil.toObject(entry.getValue().toString(), adiType);
				// 4.将agent_down_info表数据循环入数据库
				for (Agent_down_info agentDownInfo : agentDownInfoList) {
					agentDownInfo.setDown_id(PrimayKeyGener.getNextId());
					agentDownInfo.setUser_id(userCollectId);
					agentDownInfo.setAgent_ip(agentIp);
					agentDownInfo.setAgent_port(agentPort);
					agentDownInfo.add(Dbo.db());
				}
			}
		}
	}

	/**
	 * 将department_info表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.创建新的department_info表数据集合
	 * 3.判断map中的key值是否为对应department_info表数据
	 * 4.获取部门表department_info表数据
	 * 5.将department_info表数据循环插入数据库
	 *
	 * @param collectMap java.util.Map
	 *                   含义：所有表数据的map的实体
	 *                   取值范围：不为空
	 */
	private List<Department_info> addDepartmentInfo(Map<String, Object> collectMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		List<Department_info> departmentInfoListNew = new ArrayList<>();
		// 2.创建新的department_info表数据集合
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 3.判断map中的key值是否为对应department_info表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			String di = "departmentInfo";
			if (di.equals(entry.getKey())) {
				Type srdType = new TypeReference<List<Department_info>>() {
				}.getType();
				// 4.获取部门表department_info表数据
				List<Department_info> departmentInfoList = JsonUtil.toObject(entry.getValue().toString(), srdType);
				for (Department_info departmentInfo : departmentInfoList) {
					departmentInfo.setDep_id(PrimayKeyGener.getNextId());
					// 4.将department_info表数据循环插入数据库
					departmentInfo.add(Dbo.db());
				}
			}
		}
		// 5.返回新的department_info表数据集合
		return departmentInfoListNew;
	}

	/**
	 * 将source_relation_dep表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为对应source_relation_dep表数据
	 * 3.获取数据源和部门关系表source_relation_dep表数据
	 * 4.遍历department_info表数据，目的是获取最新的部门ID，dep_id
	 * 5.将source_relation_dep表数据循环插入数据库
	 *
	 * @param collectMap         java.util.Map
	 *                           含义：所有表数据的map的实体
	 *                           取值范围：不为空
	 * @param dataSource         entity
	 *                           含义： source_relation_dep表对应实体
	 *                           取值范围：不为空
	 * @param departmentInfoList java.uti.List
	 *                           含义：存放department_info表数据的集合
	 *                           取值范围：不为空
	 */
	private void addSourceRelationDep(Map<String, Object> collectMap, Data_source dataSource,
	                                  List<Department_info> departmentInfoList) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为对应source_relation_dep表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			String srd = "sourceRelationDep";
			if (srd.equals(entry.getKey())) {
				Type srdType = new TypeReference<List<Source_relation_dep>>() {
				}.getType();
				// 3.获取数据源和部门关系表source_relation_dep表数据
				List<Source_relation_dep> srdList = JsonUtil.toObject(entry.getValue().toString(), srdType);
				for (Source_relation_dep sourceRelationDep : srdList) {
					sourceRelationDep.setSource_id(dataSource.getSource_id());
					// 4.遍历department_info表数据，目的是获取最新的部门ID，dep_id
					for (Department_info departmentInfo : departmentInfoList) {
						sourceRelationDep.setDep_id(departmentInfo.getDep_id());
						// 5.将source_relation_dep表数据循环插入数据库
						sourceRelationDep.add(Dbo.db());
					}
				}
			}
		}
	}

	/**
	 * 将data_source表数据入库并返回data_source实体对象
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.创建data_source表实体对象
	 * 3.判断map中的key值是否为对应data_source表数据
	 * 4.获取数据源data_source表信息
	 * 5.将data_source表数据插入数据库
	 * 6.返回data_source表数据对应实体对象
	 *
	 * @param userId     long
	 *                   含义：创建用户ID，代表此数据源由哪个用户创建
	 *                   取值范围：不为空，创建用户时自动生成
	 * @param collectMap java.util.Map
	 *                   含义：所有表数据的map的实体
	 *                   取值范围：不为空
	 * @return entity
	 * 含义：data_source表实体对象
	 * 取值范围：不为空
	 */
	private Data_source getDataSource(long userId, Map<String, Object> collectMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		// 2.创建data_source表实体对象
		Data_source dataSource = new Data_source();
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 3.判断map中的key值是否为对应data_source表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			String ds = "dataSource";
			if (ds.equals(entry.getKey())) {
				// 4.获取数据源data_source表信息
				dataSource = JsonUtil.toObjectSafety(entry.getValue().toString(), Data_source.class).get();
				// 5.将data_source表数据插入数据库
				dataSource.setSource_id(PrimayKeyGener.getNextId());
				dataSource.setCreate_user_id(userId);
				dataSource.add(Dbo.db());
			}
		}
		return dataSource;
		// 7.返回data_source表数据对应实体对象
	}

	/**
	 * 将agent_info表信息入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为对应agent_info表数据
	 * 3.获取agent_info表数据
	 * 4.循环入库agent_info
	 *
	 * @param agentIp       String
	 *                      含义：agent地址
	 *                      取值范围：不能为空，服务器ip地址
	 * @param agentPort     String
	 *                      含义：agent端口
	 *                      取值范围：1024-65535
	 * @param userCollectId long
	 *                      含义：数据采集用户，代表此数据属于哪个用户
	 *                      取值范围：不为空以及不为空格，长度不超过10位
	 * @param collectMap    java.util.Map
	 *                      含义：所有表数据的map的实体
	 *                      取值范围：不为空
	 */
	private void addAgentInfo(String agentIp, String agentPort, long userCollectId,
	                          Map<String, Object> collectMap) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
			// 2.判断map中的key值是否为对应agent_info表数据
			// map中key的值，也是下载数据源时对应表信息封装入map的key值
			String ai = "agentInfo";
			if (ai.equals(entry.getKey())) {
				// 获取agent信息表信息
				Type aiType = new TypeReference<List<Agent_info>>() {
				}.getType();
				// 3.获取agent_info表数据
				List<Agent_info> agentInfoList = JsonUtil.toObject(entry.getValue().toString(), aiType);
				// 4.循环入库agent_info
				for (Agent_info agentInfo : agentInfoList) {
					agentInfo.setAgent_id(PrimayKeyGener.getNextId());
					agentInfo.setUser_id(userCollectId);
					agentInfo.setAgent_ip(agentIp);
					agentInfo.setAgent_port(agentPort);
					if (agentInfo.add(Dbo.db()) != 1) {
						throw new BusinessException("添加agent_info表数据入库失败");
					}
				}
			}
		}
	}


	/**
	 * 下载文件（数据源下载功能使用，下载数据源给数据源导入提供上传文件）,暂时未完成，修改中
	 *
	 * <p>
	 * 1.数据可访问权限处理方式，这里是下载数据源，所以不需要数据权限验证
	 * 2.创建存放所有数据源下载，所有相关表数据库查询获取数据的map集合
	 * 3.获取data_source表信息集合，将data_source表信息封装入map
	 * 4.获取source_relation_dep表信息集合，将source_relation_dep表数据封装入map
	 * 5.获取department_info表信息集合，将department_info表数据封装入map
	 * 6.获取agent_info表信息集合，将agent_info表信息封装入map
	 * 7.获取Agent_down_info表信息集合封装入map
	 * 8.采集任务分类表collect_job_classify，获取collect_job_classify表信息集合入map
	 * 9.ftp采集设置ftp_collect,获取ftp_collect表信息集合
	 * 10.ftp已传输表ftp_transfered,获取ftp_transfered表信息集合入map
	 * 11.ftp目录表ftp_folder,获取ftp_folder表信息集合入map
	 * 12.对象采集设置object_collect,获取object_collect表信息集合入map
	 * 13.对象采集对应信息object_collect_task,获取object_collect_task表信息集合入map
	 * 14.对象采集存储设置object_storage,获取object_storage表信息集合入map
	 * 15.对象采集结构信息object_collect_struct,获取object_collect_struct表信息集合入map
	 * 16.数据库设置database_set,获取database_set表信息集合入map
	 * 17.文件系统设置file_collect_set,获取file_collect_set表信息集合入map
	 * 18.文件源设置file_source,获取file_source表信息集合入map
	 * 19.信号文件入库信息signal_file,获取signal_file表信息集合入map
	 * 20.数据库对应的表table_info,获取table_info表信息集合入map
	 * 21.列合并信息表column_merge,获取column_merge表信息集合入map
	 * 22.表存储信息table_storage_info,获取table_storage_info表信息集合入map
	 * 23.表清洗参数信息table_clean,获取table_clean表信息集合入map
	 * 24.表对应的字段table_column,获取table_column表信息集合入map
	 * 25.列清洗参数信息column_clean,获取column_clean表信息集合入map
	 * 26.列拆分信息表column_split,获取column_split表信息集合入map
	 * 27.使用base64编码
	 * 28.判断文件是否存在
	 * 29.清空response，设置响应头，响应编码格式，控制浏览器下载该文件
	 * 30.通过流的方式写入文件
	 *
	 * @param sourceId long
	 *                 含义：data_source表主键
	 *                 取值范围：不为空以及不为空格，长度不超过10
	 */
	public void downloadFile(long sourceId) {
		// 1.数据可访问权限处理方式，这里是下载数据源，所以不需要数据权限验证
		HttpServletResponse response = ResponseUtil.getResponse();
		try (OutputStream out = response.getOutputStream()) {
			// 2.创建存放所有数据源下载，所有相关表数据库查询获取数据的map集合
			Map<String, Object> collectionMap = new HashMap<>();
			// 3.获取data_source表信息集合，将data_source表信息封装入map
			addDataSourceToMap(sourceId, collectionMap);
			// 4.获取source_relation_dep表信息集合，将source_relation_dep表数据封装入map
			List<Source_relation_dep> sourceRelationDepList = addSourceRelationDepToMap(sourceId, collectionMap);
			// 5.获取department_info表信息集合，将department_info表数据封装入map
			addDepartmentInfoToMap(collectionMap, sourceRelationDepList);
			// 6.获取agent_info表信息集合，将agent_info表信息封装入map
			List<Agent_info> agentInfoList = getAgentInfoList(sourceId, collectionMap);
			// 7.获取Agent_down_info表信息集合封装入map
			addAgentDownInfoToMap(collectionMap, agentInfoList);
			// 8.采集任务分类表collect_job_classify，获取collect_job_classify表信息集合入map
			addCollectJobClassifyToMap(collectionMap, agentInfoList);
			// 9.ftp采集设置ftp_collect,获取ftp_collect表信息集合
			Result ftpCollectResult = getFtpCollectResult(collectionMap, agentInfoList);
			// 10.ftp已传输表ftp_transfered,获取ftp_transfered表信息集合入map
			addFtpTransferedToMap(collectionMap, ftpCollectResult);
			// 11.ftp目录表ftp_folder,获取ftp_folder表信息集合入map
			addFtpFolderToMap(collectionMap, ftpCollectResult);
			// 12.对象采集设置object_collect,获取object_collect表信息集合入map
			Result objectCollectResult = getObjectCollectResult(collectionMap, agentInfoList);
			// 13.对象采集对应信息object_collect_task,获取object_collect_task表信息集合入map
			Result objectCollectTaskResult = getObjectCollectTaskResult(collectionMap, objectCollectResult);
			// 14.对象采集存储设置object_storage,获取object_storage表信息集合入map
			addObjectStorageToMap(collectionMap, objectCollectTaskResult);
			// 15.对象采集结构信息object_collect_struct,获取object_collect_struct表信息集合入map
			addObjectCollectStructResultToMap(collectionMap, objectCollectTaskResult);
			// 16.数据库设置database_set,获取database_set表信息集合入map
			Result databaseSetResult = getDatabaseSetResult(collectionMap, agentInfoList);
			// 17.文件系统设置file_collect_set,获取file_collect_set表信息集合入map
			Result fileCollectSetResult = getFileCollectSetResult(collectionMap, agentInfoList);
			// 18.文件源设置file_source,获取file_source表信息集合入map
			addFileSourceToMap(collectionMap, fileCollectSetResult);
			// 19.信号文件入库信息signal_file,获取signal_file表信息集合入map
			addSignalFileToMap(collectionMap, databaseSetResult);
			// 20.数据库对应的表table_info,获取table_info表信息集合入map
			Result tableInfoResult = getTableInfoResult(collectionMap, databaseSetResult);
			// 21.列合并信息表column_merge,获取column_merge表信息集合入map
			addColumnMergeToMap(collectionMap, tableInfoResult);
			// 22.表存储信息table_storage_info,获取table_storage_info表信息集合入map
			addTableStorageInfoToMap(collectionMap, tableInfoResult);
			// 23.表清洗参数信息table_clean,获取table_clean表信息集合入map
			addTableCleanToMap(collectionMap, tableInfoResult);
			// 24.表对应的字段table_column,获取table_column表信息集合入map
			Result tableColumnResult = getTableColumnResult(collectionMap, tableInfoResult);
			// 25.列清洗参数信息 column_clean,获取column_clean表信息集合入map
			addColumnCleanToMap(collectionMap, tableColumnResult);
			// 26.列拆分信息表column_split,获取column_split表信息集合入map
			addColumnSplitToMap(collectionMap, tableColumnResult);
			// 27.使用base64编码
			byte[] bytes = Base64.getEncoder().encode(JsonUtil.toJson(collectionMap).
					getBytes(CodecUtil.UTF8_CHARSET));
			// 28.判断文件是否存在
			if (bytes == null) {
				throw new BusinessException("此文件不存在");
			}
			// 29.清空response，设置响应编码格式,响应头，控制浏览器下载该文件
			response.reset();
			response.setCharacterEncoding(CodecUtil.UTF8_STRING);
			response.setContentType("APPLICATION/OCTET-STREAM");
			// 30.通过流的方式写入文件
			out.write(bytes);
			out.flush();
		} catch (IOException e) {
			throw new AppSystemException(e);
		}
	}

	/**
	 * 将department_info表数据加入map
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.创建存放department_info表数据的集合
	 * 3.循环source_relation_dep表集合获取dep_id，通过dep_id获取department_info表数据
	 * 4.将department_info表信息加入集合
	 * 5.将department_info表数据集合入map
	 *
	 * @param collectionMap         java.util.Map
	 *                              含义：封装数据源下载信息（这里封装的是department_info表数据集合）
	 *                              取值范围：key唯一
	 * @param sourceRelationDepList java.util.List
	 *                              含义：存放source_relation_dep表数据集合
	 *                              取值范围：不为空
	 */
	private void addDepartmentInfoToMap(Map<String, Object> collectionMap,
	                                    List<Source_relation_dep> sourceRelationDepList) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建存放department_info表数据的集合
		List<Optional<Department_info>> departmentInfoList = new ArrayList<>();
		// 3.循环source_relation_dep表集合获取dep_id，通过dep_id获取department_info表数据
		for (Source_relation_dep sourceRelationDep : sourceRelationDepList) {
			Optional<Department_info> departmentInfo = Dbo.queryOneObject(Department_info.class
					, "select * from department_info where dep_id=?",
					sourceRelationDep.getDep_id());
			// 4.将department_info表信息加入集合
			departmentInfoList.add(departmentInfo);
		}
		// 5.将department_info表数据集合入map
		collectionMap.put("departmentInfo", departmentInfoList);
	}

	/**
	 * 获取agent_info表数据集合
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.将agent_info表数据集合存入map
	 * 3.将agent_info表数据集合返回
	 *
	 * @param sourceId      long
	 *                      含义：data_source表主键，source_relation_dep表外键
	 *                      取值范围：不为空及空格，10位数字，新增data_source表时自动生成
	 * @param collectionMap java.util.Map
	 *                      含义：封装数据源下载信息（这里封装的是source_relation_dep表数据集合）
	 *                      取值范围：key唯一
	 * @return java.util.List
	 * 含义：返回source_relation_dep表数据的集合
	 * 取值范围：不为空
	 */
	private List<Agent_info> getAgentInfoList(long sourceId, Map<String, Object> collectionMap) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		List<Agent_info> agentInfoList = Dbo.queryList(Agent_info.class, "select * from " +
				"agent_info  where source_id = ?", sourceId);
		// 2.将agent_info表数据集合存入map
		collectionMap.put("agentInfo", agentInfoList);
		// 3.将agent_info表数据集合返回
		return agentInfoList;
	}

	/**
	 * 将source_relation_dep表数据入map
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.查询数据源与部门关系表信息
	 * 3.将source_relation_dep表数据入map
	 * 4.返回source_relation_dep表数据集合
	 *
	 * @param sourceId      long
	 *                      含义：data_source表主键，source_relation_dep表外键
	 *                      取值范围：不为空及空格，10位数字，新增data_source表时自动生成
	 * @param collectionMap java.util.Map
	 *                      含义：封装数据源下载信息（这里封装的是source_relation_dep表数据集合）
	 *                      取值范围：key唯一
	 * @return java.util.List
	 * 含义：返回source_relation_dep表数据的集合
	 * 取值范围：不为空
	 */
	private List<Source_relation_dep> addSourceRelationDepToMap(long sourceId, Map<String, Object> collectionMap) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.查询数据源与部门关系表信息
		List<Source_relation_dep> sourceRelationDepList = Dbo.queryList(Source_relation_dep.class,
				"select * from source_relation_dep where source_id=?", sourceId);
		// 3.将source_relation_dep表数据入map
		collectionMap.put("sourceRelationDep", sourceRelationDepList);
		// 4.返回source_relation_dep表数据集合
		return sourceRelationDepList;
	}

	/**
	 * 将column_split表数据集合存入map
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.创建存放column_split表信息的集合
	 * 3.遍历table_column结果集获取column_id,通过column_id查询column_split表信息
	 * 4.将查询到的信息封装入集合
	 * 5.将column_split表集合信息存入map
	 *
	 * @param collectionMap     java.util.Map
	 *                          含义：封装数据源下载信息（这里封装的是column_split表数据集合）
	 *                          取值范围：key唯一
	 * @param tableColumnResult fd.ng.db.resultset.Result
	 *                          含义：table_column表数据结果集
	 *                          取值范围：不为空
	 */
	private void addColumnSplitToMap(Map<String, Object> collectionMap, Result tableColumnResult) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建存放column_split表信息的集合
		Result columnSplitResult = new Result();
		// 3.遍历table_column结果集获取column_id,通过column_id查询column_split表信息
		for (int i = 0; i < tableColumnResult.getRowCount(); i++) {
			Result result = Dbo.queryResult("select * from column_split where column_id = ?",
					tableColumnResult.getLong(i, "column_id"));
			// 4.将查询到的信息封装入集合
			columnSplitResult.add(result);
		}
		// 5.将column_split表集合信息存入map
		collectionMap.put("columnSplit", columnSplitResult.toList());
	}

	/**
	 * 将column_clean表数据集合存入map
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.创建存放column_clean表信息的集合
	 * 3.遍历table_column结果集获取column_id,通过column_id查询column_clean表信息
	 * 4.将查询到的信息封装入集合
	 * 5.将column_clean表集合信息存入map
	 *
	 * @param collectionMap     java.util.Map
	 *                          含义：封装数据源下载信息（这里封装的是column_clean表数据集合）
	 *                          取值范围：key唯一
	 * @param tableColumnResult fd.ng.db.resultset.Result
	 *                          含义：table_column表数据结果集
	 *                          取值范围：不为空
	 */
	private void addColumnCleanToMap(Map<String, Object> collectionMap, Result tableColumnResult) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建存放column_clean表信息的集合
		Result columnCleanResult = new Result();
		// 3.遍历table_column结果集获取column_id,通过column_id查询column_clean表信息
		for (int i = 0; i < tableColumnResult.getRowCount(); i++) {
			Result result = Dbo.queryResult("select * from column_clean where column_id=?",
					tableColumnResult.getLong(i, "column_id"));
			// 4.将查询到的信息封装入集合
			columnCleanResult.add(result);
		}
		// 5.将column_clean表集合信息存入map
		collectionMap.put("columnClean", columnCleanResult.toList());
	}

	/**
	 * 将table_column表数据集合存入map
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.创建存放table_column表信息的集合
	 * 3.遍历table_info结果集获取table_id,通过table_id查询table_column表信息
	 * 4.将查询到的信息封装入集合
	 * 5.将table_column表集合信息存入map
	 * 6.将table_column表结果集返回
	 *
	 * @param collectionMap   java.util.Map
	 *                        含义：封装数据源下载信息（这里封装的是table_column表数据集合）
	 *                        取值范围：key唯一
	 * @param tableInfoResult fd.ng.db.resultset.Result
	 *                        含义：table_info表数据结果集
	 *                        取值范围：不为空
	 */
	private Result getTableColumnResult(Map<String, Object> collectionMap, Result tableInfoResult) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建存放table_column表信息的集合
		Result tableColumnResult = new Result();
		// 3.遍历table_info结果集获取table_id,通过table_id查询table_column表信息
		for (int i = 0; i < tableInfoResult.getRowCount(); i++) {
			Result result = Dbo.queryResult("select * from table_column where table_id=?",
					tableInfoResult.getLong(i, "table_id"));
			// 4.将查询到的信息封装入结果集
			tableColumnResult.add(result);
		}
		// 5.将table_column表集合信息存入map
		collectionMap.put("tableColumn", tableColumnResult.toList());
		// 6.将table_column表结果集返回
		return tableColumnResult;
	}

	/**
	 * 将table_clean表数据集合存入map
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.创建存放table_clean表信息的集合
	 * 3.遍历table_info结果集获取table_id,通过table_id查询table_clean表信息
	 * 4.将查询到的信息封装入集合
	 * 5.将table_clean表集合信息存入map
	 *
	 * @param collectionMap   java.util.Map
	 *                        含义：封装数据源下载信息（这里封装的是table_clean表数据集合）
	 *                        取值范围：key唯一
	 * @param tableInfoResult fd.ng.db.resultset.Result
	 *                        含义：table_info表数据结果集
	 *                        取值范围：不为空
	 */
	private void addTableCleanToMap(Map<String, Object> collectionMap, Result tableInfoResult) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建存放table_clean表信息的集合
		Result tableCleanResult = new Result();
		// 3.遍历table_info结果集获取table_id,通过table_id查询table_clean表信息
		for (int i = 0; i < tableInfoResult.getRowCount(); i++) {
			Result result = Dbo.queryResult("select * from table_clean where table_id = ?",
					tableInfoResult.getLong(i, "table_id"));
			// 4.将查询到的信息封装入集合
			tableCleanResult.add(result);
		}
		// 5.将table_clean表集合信息存入map
		collectionMap.put("tableClean", tableCleanResult.toList());
	}

	/**
	 * 将object_collect_struct表数据集合存入map
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 1.创建存放object_collect_struct表信息的集合
	 * 2.遍历object_collect_task结果集获取table_id,通过table_id查询object_collect_struct表信息
	 * 3.将查询到的信息封装入集合
	 * 4.将object_collect_struct表集合信息存入map
	 *
	 * @param collectionMap   java.util.Map
	 *                        含义：封装数据源下载信息（这里封装的是object_collect_struct表数据集合）
	 *                        取值范围：key唯一
	 * @param tableInfoResult fd.ng.db.resultset.Result
	 *                        含义：table_info表数据结果集
	 *                        取值范围：不为空
	 */
	private void addTableStorageInfoToMap(Map<String, Object> collectionMap, Result tableInfoResult) {
		Result tableStorageInfoResult = new Result();
		for (int i = 0; i < tableInfoResult.getRowCount(); i++) {
			Result result = Dbo.queryResult("select * from table_storage_info where table_id = ?",
					tableInfoResult.getLong(i, "table_id"));
			tableStorageInfoResult.add(result);
		}
		collectionMap.put("tableStorageInfo", tableStorageInfoResult.toList());
	}

	/**
	 * 将column_merge表数据集合存入map
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 1.创建存放column_merge表信息的集合
	 * 2.遍历table_info结果集获取table_id,通过table_id查询column_merge表信息
	 * 3.将查询到的信息封装入集合
	 * 4.将object_collect_struct表集合信息存入map
	 *
	 * @param collectionMap   java.util.Map
	 *                        含义：封装数据源下载信息（这里封装的是column_merge表数据集合）
	 *                        取值范围：key唯一
	 * @param tableInfoResult fd.ng.db.resultset.Result
	 *                        含义：table_info表数据结果集
	 *                        取值范围：不为空
	 */
	private void addColumnMergeToMap(Map<String, Object> collectionMap, Result tableInfoResult) {
		Result columnMergeResult = new Result();
		for (int i = 0; i < tableInfoResult.getRowCount(); i++) {
			Result result = Dbo.queryResult("select * from column_merge where table_id = ?",
					tableInfoResult.getLong(i, "table_id"));
			columnMergeResult.add(result);
		}
		collectionMap.put("columnMerge", columnMergeResult.toList());
	}

	/**
	 * 将table_info表数据集合存入map
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.创建存放table_info表信息的集合
	 * 3.遍历database_set结果集获取database_set,通过database_set查询table_info表信息
	 * 4.将查询到的信息封装入集合
	 * 5.将table_info表集合信息存入map
	 *
	 * @param collectionMap     java.util.Map
	 *                          含义：封装数据源下载信息（这里封装的是table_info表数据集合）
	 *                          取值范围：key唯一
	 * @param databaseSetResult fd.ng.db.resultset.Result
	 *                          含义：database_set表数据结果集
	 *                          取值范围：不为空
	 */
	private Result getTableInfoResult(Map<String, Object> collectionMap, Result databaseSetResult) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建存放table_info表信息的集合
		Result tableInfoResult = new Result();
		// 3.遍历database_set结果集获取database_set,通过database_set查询table_info表信息
		for (int i = 0; i < databaseSetResult.getRowCount(); i++) {
			Result result = Dbo.queryResult("select * from table_info where database_id = ?",
					databaseSetResult.getLong(i, "database_id"));
			tableInfoResult.add(result);
		}
		// 4.将查询到的信息封装入集合
		collectionMap.put("tableInfo", tableInfoResult.toList());
		// 5.将table_info表集合信息存入map
		return tableInfoResult;
	}

	/**
	 * 将signal_file表数据集合存入map
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.创建存放signal_file表信息的集合
	 * 3.遍历database_set结果集获取database_id,通过ocs_id查询signal_file表信息
	 * 4.将查询到的信息封装入集合
	 * 5.将signal_file表集合信息存入map
	 *
	 * @param collectionMap     java.util.Map
	 *                          含义：封装数据源下载信息（这里封装的是signal_file表数据集合）
	 *                          取值范围：key唯一
	 * @param databaseSetResult fd.ng.db.resultset.Result
	 *                          含义：database_set表数据结果集
	 *                          取值范围：不为空
	 */
	private void addSignalFileToMap(Map<String, Object> collectionMap, Result databaseSetResult) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建存放signal_file表信息的集合
		Result signalFileResult = new Result();
		// 3.遍历database_set结果集获取database_id,通过ocs_id查询signal_file表信息
		for (int i = 0; i < databaseSetResult.getRowCount(); i++) {
			Result result = Dbo.queryResult("select * from signal_file where database_id=?",
					databaseSetResult.getLong(i, "database_id"));
			// 4.将查询到的信息封装入集合
			signalFileResult.add(result);
		}
		// 5.将signal_file表集合信息存入map
		collectionMap.put("signalFile", signalFileResult.toList());
	}

	/**
	 * 将file_source表数据集合存入map
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.创建存放file_source表信息的集合
	 * 3.遍历file_collect_set结果集获取fcs_id(文件系统采集ID),通过fcs_id查询file_source表信息
	 * 4.将查询到的信息封装入集合
	 * 5.将file_source表集合信息存入map
	 *
	 * @param collectionMap          java.util.Map
	 *                               含义：封装数据源下载信息（这里封装的是file_source表数据集合）
	 *                               取值范围：key唯一
	 * @param file_collect_setResult fd.ng.db.resultset.Result
	 *                               含义：file_collect_set表数据结果集
	 *                               取值范围：不为空
	 */
	private void addFileSourceToMap(Map<String, Object> collectionMap, Result file_collect_setResult) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建存放file_source表信息的集合
		Result fileSourceResult = new Result();
		// 3.遍历file_collect_set结果集获取fcs_id(文件系统采集ID),通过fcs_id查询file_source表信息
		for (int i = 0; i < file_collect_setResult.getRowCount(); i++) {
			Result result = Dbo.queryResult("select * from file_source where fcs_id=?",
					file_collect_setResult.getLong(i, "fcs_id"));
			// 4.将查询到的信息封装入集合
			fileSourceResult.add(result);
		}
		// 5.将file_source表集合信息存入map
		collectionMap.put("fileSource", fileSourceResult.toList());
	}

	/**
	 * 将file_collect_set表数据集合存入map
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.创建存放file_collect_set表信息的集合
	 * 3.遍历agent_info结果集获取agent_id,通过agent_id查询file_collect_set表信息
	 * 4.将查询到的信息封装入集合
	 * 5.将file_collect_set表集合信息存入map
	 * 6.返回file_collect_set数据结果集
	 *
	 * @param collectionMap java.util.Map
	 *                      含义：封装数据源下载信息（这里封装的是file_collect_set表数据集合）
	 *                      取值范围：key唯一
	 * @param agentInfoList java.util.List
	 *                      含义：agent_info表数据结果集合
	 *                      取值范围：不为空
	 */
	private Result getFileCollectSetResult(Map<String, Object> collectionMap, List<Agent_info> agentInfoList) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建存放file_collect_set表信息的集合
		Result fileCollectSetResult = new Result();
		// 3.遍历agent_info结果集获取agent_id,通过agent_id查询file_collect_set表信息
		for (int i = 0; i < agentInfoList.size(); i++) {
			Result result = Dbo.queryResult("select * from file_collect_set where agent_id=?",
					agentInfoList.get(i).getAgent_id());
			// 4.将查询到的信息封装入集合
			fileCollectSetResult.add(result);
		}
		// 5.将file_collect_set表集合信息存入map
		collectionMap.put("fileCollectSet", fileCollectSetResult.toList());
		// 6.返回file_collect_set数据结果集
		return fileCollectSetResult;
	}

	/**
	 * 将database_set表数据集合存入map
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.创建存放database_set表信息的集合
	 * 3.遍历agent_info结果集获取agent_id,通过agent_id查询database_set表信息
	 * 4.将查询到的信息封装入集合
	 * 5.将database_set表集合信息存入map
	 * 6.返回database_result表数据结果集
	 *
	 * @param collectionMap java.util.Map
	 *                      含义：封装数据源下载信息（这里封装的是database_set表数据集合）
	 *                      取值范围：key唯一
	 * @param agentInfoList java.util.List
	 *                      含义：agent_info表数据结果集合
	 *                      取值范围：不为空
	 */
	private Result getDatabaseSetResult(Map<String, Object> collectionMap, List<Agent_info> agentInfoList) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建存放database_set表信息的集合
		Result databaseSetResult = new Result();
		// 3.遍历agent_info结果集获取agent_id,通过agent_id查询database_set表信息
		for (int i = 0; i < agentInfoList.size(); i++) {
			Result result = Dbo.queryResult("select * from database_set where agent_id = ?",
					agentInfoList.get(i).getAgent_id());
			// 4.将查询到的信息封装入集合
			databaseSetResult.add(result);
		}
		//5.将database_set表集合信息存入map
		collectionMap.put("databaseSet", databaseSetResult.toList());
		// 6.返回database_result表数据结果集
		return databaseSetResult;
	}

	/**
	 * 将ftp_folder表数据集合存入map
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.创建存放ftp_folder表信息的集合
	 * 3.遍历ftp_collect结果集获取ftp_id(ftp采集任务编号),通过ftp_id查询ftp_folder表信息
	 * 4.将查询到的信息封装入集合
	 * 5.将ftp_folder表集合信息存入map
	 *
	 * @param collectionMap    java.util.Map
	 *                         含义：封装数据源下载信息（这里封装的是ftp_folder表数据结果集）
	 *                         取值范围：key唯一
	 * @param ftpCollectResult fd.ng.db.resultset.Result
	 *                         含义：ftp_collect表数据结果集
	 *                         取值范围：不为空
	 */
	private void addFtpFolderToMap(Map<String, Object> collectionMap, Result ftpCollectResult) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建存放ftp_folder表信息的集合
		Result ftpFolderResult = new Result();
		// 3.遍历ftp_collect结果集获取ftp_id(ftp采集任务编号),通过ftp_id查询ftp_folder表信息
		for (int i = 0; i < ftpCollectResult.getRowCount(); i++) {
			Result result = Dbo.queryResult("select * from ftp_folder where ftp_id = ?",
					ftpCollectResult.getLong(i, "ftp_id"));
			// 4.将查询到的信息封装入集合
			ftpFolderResult.add(result);
		}
		// 5.将ftp_folder表集合信息存入map
		collectionMap.put("ftpFolder", ftpFolderResult.toList());
	}

	/**
	 * 将object_collect_struct表数据集合存入map
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.创建存放object_collect_struct表信息的集合
	 * 3.遍历object_collect_task结果集获取ocs_id(对象采集任务id),通过ocs_id查询object_collect_struct表信息
	 * 4.将查询到的信息封装入集合
	 * 5.将object_collect_struct表集合信息存入map
	 *
	 * @param collectionMap           java.util.Map
	 *                                含义：封装数据源下载信息（这里封装的是object_collect_struct表数据集合）
	 *                                取值范围：key唯一
	 * @param objectCollectTaskResult fd.ng.db.resultset.Result
	 *                                含义：object_collect_task表数据结果集
	 *                                取值范围：不为空
	 */
	private void addObjectCollectStructResultToMap(Map<String, Object> collectionMap,
	                                               Result objectCollectTaskResult) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建存放object_collect_struct表信息的集合
		Result objectCollectStructResult = new Result();
		// 3.遍历object_collect_task结果集获取ocs_id,通过ocs_id查询object_collect_struct表信息
		for (int i = 0; i < objectCollectTaskResult.getRowCount(); i++) {
			Result result = Dbo.queryResult("select * from object_collect_struct where " +
					" ocs_id =?", objectCollectTaskResult.getLong(i, "ocs_id"));
			// 4.将查询到的信息封装入集合
			objectCollectStructResult.add(result);
		}
		// 5.将object_collect_struct表集合信息存入map
		collectionMap.put("objectCollectStruct", objectCollectStructResult.toList());
	}

	/**
	 * 将object_storage表数据集合存入map
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.创建存放object_storage信息结果集
	 * 3.遍历object_collect_task结果集获取ocs_id（对象采集任务id），通过ocs_id查询object_storage表信息
	 * 4.将查询到的信息封装入集合
	 * 5.将object_storage集合信息存入map
	 *
	 * @param collectionMap           java.util.Map
	 *                                含义：封装数据源下载信息（这里封装的是object_storage表数据集合）
	 *                                取值范围：key唯一
	 * @param objectCollectTaskResult fd.ng.db.resultset.Result
	 *                                含义：object_collect_task表数据结果集
	 *                                取值范围：不为空
	 */
	private void addObjectStorageToMap(Map<String, Object> collectionMap, Result objectCollectTaskResult) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建存放object_storage信息的结果集
		Result objectStorageResult = new Result();
		// 3.遍历object_collect_task结果集获取ocs_id（对象采集任务编号），通过ocs_id查询object_storage表信息
		for (int i = 0; i < objectCollectTaskResult.getRowCount(); i++) {
			Result result = Dbo.queryResult("select * from object_storage where ocs_id =?",
					objectCollectTaskResult.getLong(i, "ocs_id"));
			// 4.将查询到的信息封装入集合
			objectStorageResult.add(result);
		}
		// 5.将object_storage集合信息存入map
		collectionMap.put("objectStorage", objectStorageResult.toList());
	}

	/**
	 * 封装object_collect_task表信息入map并返回object_collect_task表信息
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.创建存放object_collect_task表数据的结果集对象
	 * 3.循环遍历object_collect表数据获取odc_id，根据odc_id查询object_collect_task表信息并封装
	 * 4.将object_collect_task表结果集封装入map
	 * 5.返回object_collect_task表结果集
	 *
	 * @param collectionMap       java.util.Map
	 *                            含义：封装数据源下载信息（这里封装的是object_collect_task表数据集合）
	 *                            取值范围：key唯一
	 * @param objectCollectResult fd.ng.db.resultset.Result
	 *                            含义：object_collect表数据结果集
	 *                            取值范围：不为空
	 * @return fd.ng.db.resultset.Result
	 * 含义：返回object_collect_task表数据集合信息
	 * 取值范围：不为空
	 */
	private Result getObjectCollectTaskResult(Map<String, Object> collectionMap, Result objectCollectResult) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建存放object_collect_task表数据的结果集对象
		Result objectCollectTaskResult = new Result();
		// 3.循环遍历object_collect表数据获取odc_id，根据odc_id查询object_collect_task表信息并封装
		for (int i = 0; i < objectCollectResult.getRowCount(); i++) {
			Result result = Dbo.queryResult("select * from object_collect_task where " +
					" odc_id=?", objectCollectResult.getLong(i, "odc_id"));
			objectCollectTaskResult.add(result);
		}
		// 4.将object_collect_task表结果集封装入map
		collectionMap.put("objectCollectTask", objectCollectTaskResult.toList());
		// 5.返回object_collect_task表结果集
		return objectCollectTaskResult;
	}

	/**
	 * 封装object_collect表信息入map并返回object_collect表信息
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.创建封装object_collect信息的结果集对象
	 * 3.循环遍历agent_info表信息集合获取agent_id（agent_info表主键，object_collect表外键）
	 * 4.根据agent_id查询object_collect表获取结果集并添加到结果集对象中
	 * 5.将object_collect结果集封装入map
	 * 6.返回object_collect结果集
	 *
	 * @param collectionMap java.util.Map
	 *                      含义：封装数据源下载信息（这里封装的是object_collect表数据集合）
	 *                      取值范围：key唯一
	 * @param agentInfoList java.util.List
	 *                      含义：agent_info表数据集合
	 *                      取值范围：不为空
	 * @return fd.ng.db.resultset.Result
	 * 含义：返回object_collect表数据集合信息
	 * 取值范围：不为空
	 */
	private Result getObjectCollectResult(Map<String, Object> collectionMap, List<Agent_info> agentInfoList) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建封装object_collect信息的结果集对象
		Result objectCollectResult = new Result();
		// 3.循环遍历agent_info表信息集合获取agent_id（agent_info表主键，object_collect表外键）
		for (int i = 0; i < agentInfoList.size(); i++) {
			// 4.根据agent_id查询object_collect表获取结果集并添加到结果集对象中
			Result object_collect = Dbo.queryResult("select * from object_collect where " +
					"agent_id=?", agentInfoList.get(i).getAgent_id());
			objectCollectResult.add(object_collect);
		}
		// 5.将object_collect结果集封装入map
		collectionMap.put("objectCollect", objectCollectResult.toList());
		// 6.返回object_collect结果集
		return objectCollectResult;
	}

	/**
	 * 封装agent_down_info表数据集合到map
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.创建封装agent_down_info表信息集合
	 * 3.遍历agent_info表信息获取agent_id（agent_info表主键，agent_down_info表外键）
	 * 4.通过agent_id查询agent_down_info表信息
	 * 5.将agent_down_info表信息放入list
	 * 6.将agent_down_info表信息入map
	 *
	 * @param collectionMap java.util.Map
	 *                      含义：封装数据源下载信息（这里封装的是agent_down_info表数据集合）
	 *                      取值范围：key唯一
	 * @param agentInfoList java.util.List
	 *                      含义：agent_info表数据集合
	 *                      取值范围：不为空
	 */
	private void addAgentDownInfoToMap(Map<String, Object> collectionMap, List<Agent_info> agentInfoList) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建封装agent_down_info表信息集合
		List<Optional<Agent_down_info>> agentDownInfoList =
				new ArrayList<>();
		// 3.遍历agent_info表信息获取agent_id（agent_info表主键，agent_down_info表外键）
		for (int i = 0; i < agentInfoList.size(); i++) {
			// 4.通过agent_id查询agent_down_info表信息
			Optional<Agent_down_info> agent_down_info = Dbo.queryOneObject(Agent_down_info.class,
					"select * from  agent_down_info where  agent_id = ?",
					agentInfoList.get(i).getAgent_id());
			// 5.将agent_down_info表信息放入list
			if (agent_down_info.isPresent()) {
				agentDownInfoList.add(agent_down_info);
			}
		}
		// 6.将agent_down_info表信息入map
		collectionMap.put("agentDownInfo", agentDownInfoList);
	}

	/**
	 * 封装data_source表数据集合到map
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.根据数据源ID查询数据源data_source集合
	 * 3.判断获取到的集合是否有数据，没有抛异常，有返回数据
	 * 4.将data_source数据入map
	 *
	 * @param sourceId      long
	 *                      含义：data_source主键ID
	 *                      取值范围：不为空以及空格，长度不超过10
	 * @param collectionMap java.util.Map
	 *                      含义：封装数据源下载信息（这里封装的是data_source表数据集合）
	 *                      取值范围：key唯一
	 */
	private void addDataSourceToMap(long sourceId, Map<String, Object> collectionMap) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.根据数据源ID查询数据源data_source集合
		Optional<Data_source> dataSource = Dbo.queryOneObject(Data_source.class, "select * from " +
				"data_source where source_id = ?", sourceId);
		// 3.判断获取到的集合是否有数据，没有抛异常，有返回数据
		if (!dataSource.isPresent()) {
			throw new BusinessException("此数据源下没有数据，sourceId = ?" + sourceId);
		}
		// 4.将data_source数据入map
		collectionMap.put("dataSource", dataSource);
	}

	/**
	 * 封装ftp_transfered表数据集合到map
	 *
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.创建封装ftp_transfered信息的集合
	 * 3.遍历Ftp_collect信息，获取ftp_id(ftp_transfered主键，ftp_collect外键）
	 * 4. 根据ftp_id查询ftp_transfered信息
	 * 5.将ftp_transfered表数据结果集信息入map
	 *
	 * @param collectionMap    java.util.Map
	 *                         含义：封装数据源下载信息（这里封装的是ftp_transfered表数据集合）
	 *                         取值范围：key唯一
	 * @param ftpCollectResult fd.ng.db.resultset.Result
	 *                         含义：ftp_collect表数据集
	 *                         取值范围：不为空
	 */
	private void addFtpTransferedToMap(Map<String, Object> collectionMap, Result ftpCollectResult) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建封装ftp_transfered信息的集合
		Result ftpTransferedResult = new Result();
		// 3.遍历Ftp_collect信息，获取ftp_id(ftp_transfered主键，ftp_collect外键）
		for (int i = 0; i < ftpCollectResult.getRowCount(); i++) {
			// 4. 根据ftp_id查询ftp_transfered信息
			Result result = Dbo.queryResult("select * from ftp_transfered where ftp_id=?",
					ftpCollectResult.getLong(i, "ftp_id"));
			ftpTransferedResult.add(result);
		}
		// 5.将ftp_transfered表数据结果集信息入map
		collectionMap.put("ftpTransfered", ftpTransferedResult.toList());
	}

	/**
	 * 获取ftp_collect表信息
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.创建封装ftp_collect信息的集合
	 * 3.遍历agent_info信息，获取agent_id(agent_info主键，ftp_collect外键）
	 * 4. 根据agent_id查询ftp_collect信息
	 * 5.将ftp_collect表信息入map
	 * 6.返回ftp_collect表结果集信息
	 *
	 * @param agentInfoList java.util.List
	 *                      含义：agent_info表数据集合
	 *                      取值范围：不为空
	 * @return fd.ng.db.resultset.Result
	 * 含义：返回ftp_collect集合信息
	 * 取值范围：不为空
	 */
	private Result getFtpCollectResult(Map<String, Object> collectionMap, List<Agent_info> agentInfoList) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建封装ftp_collect信息的集合
		Result ftpCollectResult = new Result();
		// 3.遍历agent_info信息，获取agent_id(agent_info主键，ftp_collect外键）
		for (int i = 0; i < agentInfoList.size(); i++) {
			// 4. 根据agent_id查询ftp_collect信息
			Result result = Dbo.queryResult("select * from ftp_collect where agent_id = ?",
					agentInfoList.get(i).getAgent_id());
			ftpCollectResult.add(result);
		}
		// 5.将ftp_collect表信息入map
		collectionMap.put("ftpCollect", ftpCollectResult.toList());
		// 6.返回ftp_collect表结果集信息
		return ftpCollectResult;
	}

	/**
	 * 封装collect_job_classify表信息入map
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.创建封装Collect_job_classify信息的集合
	 * 3.遍历agent_info信息，获取agent_id(agent_info主键，Collect_job_classify外键）
	 * 4. 根据agent_id查询Collect_job_classify信息
	 * 5.将collect_job_classify表数据结果集入map
	 *
	 * @param collectionMap java.util.Map
	 *                      含义：封装数据源下载信息（这里封装的是collect_job_classify表数据集合）
	 *                      取值范围：key唯一
	 * @param agentInfoList java.util.list
	 *                      含义：agent_info表信息集合
	 *                      取值范围：不为空
	 */
	private void addCollectJobClassifyToMap(Map<String, Object> collectionMap, List<Agent_info> agentInfoList) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建封装collect_job_classify信息的集合
		Result collectJobClassifyResult = new Result();
		// 3.遍历agent_info信息，获取agent_id(agent_info主键，collect_job_classify外键）
		for (int i = 0; i < agentInfoList.size(); i++) {
			// 4.根据agent_id查询Collect_job_classify信息
			Result result = Dbo.queryResult("select * from collect_job_classify where " +
					" agent_id=?", agentInfoList.get(i).getAgent_id());
			collectJobClassifyResult.add(result);
		}
		// 5.将collect_job_classify表数据结果集入map
		collectionMap.put("collectJobClassify", collectJobClassifyResult.toList());
	}

}
