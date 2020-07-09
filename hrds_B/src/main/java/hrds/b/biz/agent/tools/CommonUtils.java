package hrds.b.biz.agent.tools;

import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.web.util.Dbo;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;

public class CommonUtils {
	@Method(desc = "判断agent是否存在", logicStep = "1.判断agent是否存在")
	@Param(name = "agent_id", desc = "agent ID", range = "新增agent时通过主键生成")
	public static void isAgentExist(long agent_id, long user_id) {
		// 1.判断agent是否存在
		if (Dbo.queryNumber(
				"SELECT count(*) FROM " + Agent_down_info.TableName + " t1 join " + Agent_info.TableName
						+ " t2 on t1.agent_ip = t2.agent_ip and t1.agent_port=t2.agent_port " +
						" where  t2.agent_id= ? and t2.user_id = ?",
				agent_id, user_id)
				.orElseThrow(() -> new BusinessException("sql查询错误")) != 1) {
			throw new BusinessException("agent未部署或者agent已不存在，agent_id=" + agent_id);
		}
	}

	@Method(desc = "判断当前半结构化采集任务是否还存在", logicStep = "1.判断当前半结构化采集任务是否还存在")
	@Param(name = "odc_id", desc = "对象采集id", range = "新增对象采集配置信息时生成")
	public static void isObjectCollectExist(long odc_id) {
		// 1.判断当前半结构化采集任务是否还存在
		if (Dbo.queryNumber(
				"select count(*) from " + Object_collect.TableName + " where odc_id=?",
				odc_id).orElseThrow(() -> new BusinessException("sql查询错误！")) == 0) {
			throw new BusinessException("任务" + odc_id + "已不存在，请检查");
		}
	}

	@Method(desc = "判断当前对象采集对应信息是否存在", logicStep = "1.判断当前对象采集对应信息是否存在")
	@Param(name = "ocs_id", desc = "对象采集任务编号", range = "新增对象采集任务时生成")
	public static void isObjectCollectTaskExist(long ocs_id) {
		// 1.判断当前对象采集对应信息是否存在
		if (Dbo.queryNumber(
				"select count(*) from " + Object_collect_task.TableName + " where ocs_id=?",
				ocs_id)
				.orElseThrow(() -> new BusinessException("sql查询错误")) == 0) {
			throw new BusinessException("当前对象采集对应信息已不存在，ocs_id=" + ocs_id);
		}
	}

	@Method(desc = "判断当前数据存储层配置表信息是否存在", logicStep = "1.判断当前数据存储层配置表信息是否存在")
	@Param(name = "dsl_id", desc = "存储层配置ID", range = "新增存储层配置信息时生成")
	public static void isDataStoreLayerExist(long dsl_id) {
		// 1.判断当前数据存储层配置表信息是否存在
		if (Dbo.queryNumber(
				"select count(*) from " + Data_store_layer.TableName + " where dsl_id=?",
				dsl_id)
				.orElseThrow(() -> new BusinessException("sql查询错误")) == 0) {
			throw new BusinessException("当前存储层配置信息已不存在，dsl_id=" + dsl_id);
		}
	}

	@Method(desc = "判断当前表对应的列信息是否存在", logicStep = "1.判断当前表对应的列信息是否存在")
	@Param(name = "ocs_id", desc = "对象采集对应表信息主键ID", range = "新增对应采集对应信息时生成")
	public static void isObjectCollectStructExist(long ocs_id) {
		// 1.判断当前表对应的列信息是否存在
		if (Dbo.queryNumber(
				"select count(*) from " + Object_collect_struct.TableName + " where ocs_id=?",
				ocs_id).orElseThrow(() -> new BusinessException("sql查询错误")) == 0) {
			throw new BusinessException("当前表对应的列信息不存在，请检查");
		}
	}

}
