package hrds.b.biz.agent.resourcerecod.register;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import fd.ng.core.utils.Validator;
import fd.ng.db.resultset.Result;
import fd.ng.web.util.Dbo;
import hrds.b.biz.agent.CheckParam;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.AgentType;
import hrds.commons.codes.CleanType;
import hrds.commons.codes.DatabaseType;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Agent_info;
import hrds.commons.entity.Collect_job_classify;
import hrds.commons.entity.Database_set;
import hrds.commons.entity.fdentity.ProjectTableEntity.EntityDealZeroException;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.key.PrimayKeyGener;

@DocClass(desc = "贴源登记管理", author = "Mr.Lee", createdate = "2020-07-06 10:02")
public class ResourceRecodingAction extends BaseAction {

	private static final JSONObject CLEAN_OBJ;

	static {
		CLEAN_OBJ = new JSONObject(true);
		CLEAN_OBJ.put(CleanType.ZiFuBuQi.getCode(), 1);
		CLEAN_OBJ.put(CleanType.ZiFuTiHuan.getCode(), 2);
		CLEAN_OBJ.put(CleanType.ShiJianZhuanHuan.getCode(), 3);
		CLEAN_OBJ.put(CleanType.MaZhiZhuanHuan.getCode(), 4);
		CLEAN_OBJ.put(CleanType.ZiFuHeBing.getCode(), 5);
		CLEAN_OBJ.put(CleanType.ZiFuChaiFen.getCode(), 6);
		CLEAN_OBJ.put(CleanType.ZiFuTrim.getCode(), 7);
	}

	@Method(desc = "新增贴源登记信息", logicStep = "1: 根据用户和Agent信息进行关联查询上次为配置完成的任务信息,如果没有则说明是新增")
	@Param(name = "source_id", range = "不可为空", desc = "数据源ID编号")
	@Param(name = "agent_id", range = "不可为空", desc = "Agent ID编号")
	@Return(desc = "返回配置的贴源信息", range = "可以为空,为空表示首次配置")
	public Result getInitStorageData(long source_id, long agent_id) {

		return Dbo.queryResult(
			"SELECT t1.*, t2.classify_id, t2.classify_num, "
				+ " t2.classify_name, t2.remark "
				+ " FROM "
				+ Database_set.TableName
				+ " t1 "
				+ " JOIN "
				+ Collect_job_classify.TableName
				+ " t2 ON "
				+ " t1.classify_id = t2.classify_id  JOIN "
				+ Agent_info.TableName
				+ " ai ON t1.agent_id = ai.agent_id "
				+ "WHERE  t1.is_sendok = ? AND ai.agent_type = ? AND ai.user_id = ? "
				+ "AND ai.source_id = ? AND ai.agent_id = ? AND t1.is_reg = ?",
			IsFlag.Fou.getCode(),
			AgentType.ShuJuKu.getCode(),
			getUserId(),
			source_id,
			agent_id, IsFlag.Shi.getCode());
	}

	@Method(
		desc = "根据数据库采集任务ID查询贴源信息",
		logicStep =
			""
				+ "1、在数据库设置表(database_set)中，根据databaseId判断是否查询到数据，如果查询不到，抛异常给前端"
				+ "2、根据采集任务的ID,查询贴源配置信息,分类信息"
				+ "3: 根据任务ID信息查找表对应的存储层配置信息,这里的一次任务只能对应一个存储层,如果没有查询到结果集,则抛出异常信息")
	@Param(name = "databaseId", desc = "源系统数据库设置表主键", range = "不为空")
	@Return(desc = "贴源配置数据信息集合", range = "不能为空")
	public Result editStorageData(long databaseId) {
		// 1、在数据库设置表(database_set)中，根据databaseId判断是否查询到数据，如果查询不到，抛异常给前端
		long countNum = Dbo.queryNumber(
			"SELECT count(1) "
				+ " FROM "
				+ Database_set.TableName
				+ " das "
				+ " JOIN "
				+ Agent_info.TableName
				+ " ai ON ai.agent_id = das.agent_id "
				+ " WHERE das.database_id = ? AND ai.user_id = ? AND das.is_sendok = ?",
			databaseId,
			getUserId(), IsFlag.Shi.getCode())
			.orElseThrow(() -> new BusinessException("SQL查询错误"));
		if (countNum != 1) {
			CheckParam.throwErrorMsg("根据用户ID(%s),未找到任务ID为(%s)的数据信息", getUserId(), databaseId);
		}
		//2、根据采集任务的ID,查询贴源配置信息,分类信息
		return Dbo.queryResult(
			"SELECT t1.*, t2.classify_id, t2.classify_num,t2.classify_name, t2.remark "
				+ " FROM "
				+ Database_set.TableName
				+ " t1 "
				+ " JOIN "
				+ Collect_job_classify.TableName
				+ " t2 ON "
				+ " t1.classify_id = t2.classify_id  WHERE database_id = ? AND t1.is_sendok = ? AND t1.is_reg = ?",
			databaseId, IsFlag.Shi.getCode(), IsFlag.Shi.getCode());
	}

	@Method(desc = "保存贴源登记的数据信息", logicStep = ""
		+ "1: 校验实体每个必须字段的数据不能为空 "
		+ "2: 检查任务名称不能重复 "
		+ "3: 检查作业编号不能重复"
		+ "4: 返回此次任务的采集ID")
	@Param(name = "databaseSet", desc = "贴源数据的实体信息", range = "不可以为空", isBean = true)
	@Return(desc = "返回此次保存后生成的任务ID", range = "不可为空")
	public Long saveRegisterData(Database_set databaseSet) {
		//1: 校验实体每个必须字段的数据不能为空
		verifyDatabaseSetEntity(databaseSet);
		//2: 检查任务名称不能重复
		long val =
			Dbo.queryNumber(
				"SELECT COUNT(1) FROM " + Database_set.TableName + " WHERE task_name = ?",
				databaseSet.getTask_name())
				.orElseThrow(() -> new BusinessException("SQL查询错误"));
		if (val != 0) {
			CheckParam.throwErrorMsg("任务名称(%s)重复，请重新定义任务名称", databaseSet.getTask_name());
		}
		//3: 检查作业编号不能重复
		if (StringUtil.isNotBlank(databaseSet.getDatabase_number())) {
			val =
				Dbo.queryNumber(
					"SELECT COUNT(1) FROM " + Database_set.TableName + " WHERE database_number = ?",
					databaseSet.getDatabase_number())
					.orElseThrow(() -> new BusinessException("SQL查询错误"));
			if (val != 0) {
				CheckParam.throwErrorMsg("作业编号(%s)重复，请重新定义作业编号", databaseSet.getDatabase_number());
			}
		}
		databaseSet.setDatabase_id(PrimayKeyGener.getNextId());
		databaseSet.setIs_reg(IsFlag.Shi.getCode());
		databaseSet.setDb_agent(IsFlag.Fou.getCode());
		databaseSet.setIs_sendok(IsFlag.Fou.getCode());
		databaseSet.setCp_or(CLEAN_OBJ.toJSONString());
		databaseSet.add(Dbo.db());
		// 4: 返回此次任务的采集ID
		return databaseSet.getDatabase_id();
	}

	@Method(desc = "编辑保存贴源登记的数据信息", logicStep = ""
		+ "1: 校验实体每个必须字段的数据不能为空 "
		+ "2: 检查任务名称不能重复 "
		+ "3: 检查作业编号不能重复"
		+ "4: 更新此次任务"
		+ "5: 返回此次任务的采集ID")
	@Param(name = "databaseSet", desc = "贴源数据的实体信息", range = "不可以为空", isBean = true)
	@Return(desc = "返回此次保存后生成的任务ID", range = "不可为空")
	public Long updateRegisterData(Database_set databaseSet) {

		Validator.notNull(databaseSet.getDatabase_id(), "更新时未获取到主键ID信息");
		//1: 校验实体每个必须字段的数据不能为空
		verifyDatabaseSetEntity(databaseSet);
		//2: 检查任务名称不能重复
		long val =
			Dbo.queryNumber(
				"SELECT COUNT(1) from " + Database_set.TableName + " WHERE task_name = ? AND database_id != ?",
				databaseSet.getTask_name(), databaseSet.getDatabase_id())
				.orElseThrow(() -> new BusinessException("SQL查询错误"));
		if (val != 0) {
			CheckParam.throwErrorMsg("任务名称(%s)重复，请重新定义任务名称", databaseSet.getTask_name());
		}
		// 3: 检查作业编号不能重复
		if (StringUtil.isNotBlank(databaseSet.getDatabase_number())) {
			val =
				Dbo.queryNumber(
					"SELECT COUNT(1) from " + Database_set.TableName + " WHERE database_number = ? AND database_id != ?",
					databaseSet.getDatabase_number(), databaseSet.getDatabase_id())
					.orElseThrow(() -> new BusinessException("SQL查询错误"));
			if (val != 0) {
				CheckParam.throwErrorMsg("作业编号(%s)重复，请重新定义作业编号", databaseSet.getDatabase_number());
			}
		}
		//4: 更新此次任务
		try {
			databaseSet.update(Dbo.db());
		} catch (Exception e) {
			if (!(e instanceof EntityDealZeroException)) {
				throw new BusinessException(e.getMessage());
			}
		}
		//5: 返回此次任务的采集ID
		return databaseSet.getDatabase_id();
	}

	private void verifyDatabaseSetEntity(Database_set databaseSet) {
		// 1、校验database_type不能为空，并且取值范围必须在DatabaseType代码项中
		Validator.notBlank(databaseSet.getDatabase_type(), "保存贴源登记信息时数据库类型不能为空");
		DatabaseType.ofEnumByCode(databaseSet.getDatabase_type());
		// 2、校验classify_id不能为空
		Validator.notNull(databaseSet.getClassify_id(), "保存贴源登记信息时分类信息不能为空");
//		// 3、校验作业编号不为能空，并且长度不能超过10
//		Validator.notBlank(databaseSet.getDatabase_number(), "保存贴源登记信息时作业编号不为能空，并且长度不能超过10");
		// 4、校验数据库驱动不能为空
		Validator.notBlank(databaseSet.getDatabase_drive(), "保存贴源登记信息时数据库驱动不能为空");
//		// 5、校验数据库名称不能为空
//		Validator.notBlank(databaseSet.getDatabase_name(), "保存贴源登记信息时数据库名称不能为空");
//		// 6、校验数据库IP不能为空
//		Validator.notBlank(databaseSet.getDatabase_ip(), "保存贴源登记信息时数据库IP地址不能为空");
//		// 7、校验数据库端口号不能为空
//		Validator.notBlank(databaseSet.getDatabase_port(), "保存贴源登记信息时数据库端口号不能为空");
		// 8、校验用户名不能为空
		Validator.notBlank(databaseSet.getUser_name(), "保存贴源登记信息时数据库用户名不能为空");
		// 9、校验数据库密码不能为空
		Validator.notBlank(databaseSet.getDatabase_pad(), "保存贴源登记信息时数据库密码不能为空");
		// 10、校验JDBCURL不能为空
		Validator.notBlank(databaseSet.getJdbc_url(), "保存贴源登记信息时数据库连接URL不能为空");
		// 11、校验agent_id不能为空
		Validator.notNull(databaseSet.getAgent_id(), "保存贴源登记信息时必须关联Agent信息不能为空");
		// 12、校验存储层ID不能为空
		Validator.notNull(databaseSet.getDsl_id(), "保存贴源登记信息时存储层信息不能为空");
	}

}
