package hrds.b.biz.agent.semistructured.collectstoragelayerconf;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.Validator;
import fd.ng.db.resultset.Result;
import fd.ng.web.util.Dbo;
import hrds.b.biz.agent.tools.CommonUtils;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.JobExecuteState;
import hrds.commons.codes.StoreLayerDataSource;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.DboExecute;

import java.util.List;

@DocClass(desc = "半结构化采集存储层配置", author = "dhw", createdate = "2020/6/12 18:09")
public class CollectStorageLayerConfAction extends BaseAction {

	@Method(desc = "获取半结构化采集存储层配置初始化信息",
			logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
					"2.判断当前半结构化采集任务是否已不存在" +
					"3.关联查询半结构化采集对应表信息与表对应存储信息获取存储配置信息")
	@Param(name = "odc_id", desc = "对象采集ID", range = "新增对象采集配置时生成")
	@Return(desc = "返回存储配置信息", range = "无限制")
	public Result getCollectStorageLayerInfo(long odc_id) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		// 2.判断当前半结构化采集任务是否已不存在
		CommonUtils.isObjectCollectExist(odc_id);
		// 3.关联查询半结构化采集对应表信息与表对应存储信息获取存储配置信息
		Result collectStorageLayerInfo = Dbo.queryResult(
				"select * from " + Object_collect_task.TableName + " oct left join "
						+ Dtab_relation_store.TableName + " drs on oct.ocs_id=drs.tab_id "
						+ " where oct.odc_id=? and drs.data_source=?",
				odc_id, StoreLayerDataSource.OBJ.getCode());
		// 4.判断存储配置信息是否为空，为空说明新增直接返回对象采集对应信息
		if (collectStorageLayerInfo.isEmpty()) {
			return Dbo.queryResult("select * from " + Object_collect_task.TableName + " where odc_id=?",
					odc_id);
		}
		// 5.返回存储配置信息
		return collectStorageLayerInfo;
	}

	@Method(desc = "根据对象采集任务编号获取存储目的地数据信息",
			logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
					"2.判断当前对象采集对应信息是否存在" +
					"3.获取当前表对应存储信息，为空说明之前没有定义过存储目的地")
	@Param(name = "ocs_id", desc = "对象采集对应表信息主键ID", range = "新增对应采集对应信息时生成")
	@Return(desc = "返回根据对象采集任务编号获取存储目的地数据信息", range = "无限制，为空说明之前没有定义过存储目的地")
	public Result getStorageLayerDestById(long ocs_id) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		// 2.判断当前对象采集对应信息是否存在
		CommonUtils.isObjectCollectTaskExist(ocs_id);
		// 3.获取当前表对应存储信息，为空说明之前没有定义过存储目的地
		return Dbo.queryResult(
				"select * from " + Object_collect_task.TableName + " oct left join "
						+ Dtab_relation_store.TableName + " drs on oct.ocs_id=drs.tab_id "
						+ " where ocs_id=? and drs.data_source=?",
				ocs_id, StoreLayerDataSource.OBJ.getCode());
	}

	@Method(desc = "根据存储层配置ID获取当前存储层配置属性信息",
			logicStep = "1.判断当前数据存储层配置表信息是否存在" +
					"2.根据存储层配置ID获取当前存储层配置属性信息")
	@Param(name = "dsl_id", desc = "存储层配置ID", range = "新增存储层配置信息时生成")
	@Return(desc = "返回根据存储层配置ID获取当前存储层配置属性信息", range = "无限制")
	public Result getStorageLayerAttrById(long dsl_id) {
		// 1.判断当前数据存储层配置表信息是否存在
		CommonUtils.isDataStoreLayerExist(dsl_id);
		// 2.根据存储层配置ID获取当前存储层配置属性信息
		return Dbo.queryResult(
				"select * from " + Data_store_layer_attr.TableName + " where dsl_id=?", dsl_id);
	}

	@Method(desc = "获取当前表对应列存储信息", logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
			"2.判断当前数据存储层配置表信息是否存在" +
			"3.判断当前对象采集对应信息是否存在" +
			"4.查询当前表对应列存储信息" +
			"5.判断列存储信息是否为空，不为空直接返回列存储信息" +
			"6.查询半结构化采集结构信息" +
			"7.查询存储层附加属性信息" +
			"8.设置附加属性到列结构信息中" +
			"9.返回列存储信息为空时的列信息")
	@Param(name = "dsl_id", desc = "存储层配置ID", range = "新增存储层配置信息时生成")
	@Param(name = "ocs_id", desc = "对象采集对应表信息主键ID", range = "新增对应采集对应信息时生成")
	@Return(desc = "返回获取当前表对应列存储信息", range = "无限制")
	public Result getColumnStorageLayerInfo(long dsl_id, long ocs_id) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		// 2.判断当前数据存储层配置表信息是否存在
		CommonUtils.isDataStoreLayerExist(dsl_id);
		// 3.判断当前对象采集对应信息是否存在
		CommonUtils.isObjectCollectTaskExist(ocs_id);
		// 4.查询当前表对应列存储信息
		Result columnStorageLayerInfo = Dbo.queryResult(
				"select t1.struct_id,t1.column_name,t1.data_desc,t2.csi_number,t3.dsla_storelayer from "
						+ Object_collect_struct.TableName + " t1 left join "
						+ Dcol_relation_store.TableName + " t2 on t1.struct_id=t2.col_id left join "
						+ Data_store_layer_added.TableName + " t3 on t2.dslad_id=t3.dslad_id " +
						" where t1.ocs_id=? and t3.dsl_id=? and t2.data_source=?",
				ocs_id, dsl_id, StoreLayerDataSource.OBJ.getCode());
		// 5.判断列存储信息是否为空，不为空直接返回列存储信息
		if (!columnStorageLayerInfo.isEmpty()) {
			return columnStorageLayerInfo;
		}
		// 6.查询半结构化采集结构信息
		Result objCollectStructResult = Dbo.queryResult(
				"select ocs_id,struct_id,column_name,data_desc from " + Object_collect_struct.TableName
						+ " where ocs_id=?",
				ocs_id);
		// 7.查询存储层附加属性信息
		List<String> dslaStorelayerList = Dbo.queryOneColumnList(
				"select t1.dsla_storelayer from "
						+ Data_store_layer_added.TableName
						+ " t1 join "
						+ Data_store_layer.TableName
						+ " t2 on t1.dsl_id = t2.dsl_id where t2.dsl_id = ?",
				dsl_id);
		// 8.设置附加属性到列结构信息中
		for (int i = 0; i < objCollectStructResult.getRowCount(); i++) {
			objCollectStructResult.setObject(i, "dsla_storelayer", dslaStorelayerList);
		}
		// 9.返回列存储信息为空时的列信息
		return objCollectStructResult;
	}

	@Method(desc = "保存列存储层附加信息", logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
			"2.判断列存储信息是否为空,为空说明该表对应字段没有附加信息" +
			"3、在每保存一个字段的存储目的地前，先在dcol_relation_store表中删除该表所有列的信息，不关心删除多少条" +
			"4.遍历列存储附加信息并保存入库" +
			"5.判断附加属性ID信息是否为空，为空说明没有选择附加信息" +
			"6.遍历该列附加信息ID并保存数据字段存储关系入库")
	@Param(name = "ocs_id", desc = "对象采集对应表信息主键ID", range = "新增对象采集对应信息时生成")
	@Param(name = "dcolRelationStores", desc = "数据字段存储关系实体对象数组", range = "与对应数据库表字段规则一致",
			isBean = true)
	public void saveColRelationStoreInfo(long ocs_id, Dcol_relation_store[] dcolRelationStores) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		// 2.判断列存储信息是否为空,为空说明该表对应字段没有附加信息
		if (dcolRelationStores != null && dcolRelationStores.length != 0) {
			// 3、在每保存一个字段的存储目的地前，先在dcol_relation_store表中删除该表所有列的信息，不关心删除多少条
			Dbo.execute(
					"delete from " + Dcol_relation_store.TableName + " where col_id in " +
							"(select struct_id from " + Object_collect_struct.TableName + " where ocs_id = ?)"
							+ " AND data_source = ?",
					ocs_id, StoreLayerDataSource.OBJ.getCode());
			// 4.遍历列存储附加信息并保存入库
			for (Dcol_relation_store dcol_relation_store : dcolRelationStores) {
				Validator.notNull(dcol_relation_store.getDslad_id(), "附加信息ID不能为空");
				Validator.notNull(dcol_relation_store.getCol_id(), "结构信息ID不能为空");
				dcol_relation_store.setData_source(StoreLayerDataSource.OBJ.getCode());
				// 2.新增数据字段存储关系表信息
				dcol_relation_store.add(Dbo.db());
			}
		}
	}

	@Method(desc = "在配置字段存储信息时，更新字段中文名",
			logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制"
					+ "2、对集合的长度进行校验，如果集合为空说明获取字段信息失败"
					+ "3、遍历集合，更新每个字段的中文名")
	@Param(name = "objectCollectStructs", desc = "对象采集结构信息表实体数组", range = "与数据库对应实体字段规则一致"
			, isBean = true)
	public void updateColumnZhName(Object_collect_struct[] objectCollectStructs) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		// 2、对集合的长度进行校验，如果集合为空说明获取字段信息失败
		if (objectCollectStructs == null || objectCollectStructs.length == 0) {
			throw new BusinessException("获取字段信息失败");
		}
		// 3、遍历集合，更新每个字段的中文名
		for (Object_collect_struct objectCollectStruct : objectCollectStructs) {
			DboExecute.updatesOrThrow("更新字段" + objectCollectStruct.getColumn_name() + "的中文名失败",
					"update " + Object_collect_struct.TableName + " set data_desc=? where struct_id=?",
					objectCollectStruct.getData_desc(), objectCollectStruct.getStruct_id());
		}
	}

	@Method(desc = "保存数据表存储关系表信息", logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
			"2.判断当前任务是否已不存在" +
			"3.判断表存储目的地信息是否存在" +
			"4.查询半结构化采集所有入库的表" +
			"5.判断入库的表与选择存储目的地的表个数是否相同，保证入库的表都选择了存储目的地" +
			"6.先删除原来该半结构化采集任务下的表存储信息，不关心删除几条" +
			"7.循环新增数据表存储关系表信息入库")
	@Param(name = "odc_id", desc = "对象采集ID", range = "新增对象采集配置时生成")
	@Param(name = "dtabRelationStores", desc = "数据表存储关系实体对象", range = "与数据库对应实体字段规则一致",
			isBean = true)
	public void saveDtabRelationStoreInfo(long odc_id, Dtab_relation_store[] dtabRelationStores) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		// 2.判断当前任务是否已不存在
		CommonUtils.isObjectCollectExist(odc_id);
		// 3.判断表存储目的地信息是否存在
		if (dtabRelationStores == null || dtabRelationStores.length == 0) {
			throw new BusinessException("未获取到表存储目的地信息");
		}
		// 4.查询半结构化采集所有入库的表
		long num = Dbo.queryNumber(
				"select count(*) from " + Object_collect_task.TableName + " where odc_id=?", odc_id)
				.orElseThrow(() -> new BusinessException("sql查询错误"));
		// 5.判断入库的表与选择存储目的地的表个数是否相同，保证入库的表都选择了存储目的地
		if (num != dtabRelationStores.length) {
			throw new BusinessException("请确保入库的表都选择了存储目的地");
		}
		// 6.先删除原来该半结构化采集任务下的表存储信息，不关心删除几条
		Dbo.execute(
				"delete from " + Dtab_relation_store.TableName + " where tab_id in"
						+ " (select ocs_id from " + Object_collect_task.TableName + " where odc_id = ?)"
						+ " AND data_source = ?",
				odc_id, StoreLayerDataSource.OBJ.getCode());
		for (int i = 0; i < dtabRelationStores.length; i++) {
			Dtab_relation_store dtabRelationStore = dtabRelationStores[i];
			Validator.notNull(dtabRelationStore.getDsl_id(), "第" + (i + 1) + "张表未选择存储层");
			Validator.notNull(dtabRelationStore.getTab_id(), "第" + (i + 1) + "张表ID为空");
			dtabRelationStore.setData_source(StoreLayerDataSource.OBJ.getCode());
			dtabRelationStore.setIs_successful(JobExecuteState.DengDai.getCode());
			// 7.循环新增数据表存储关系表信息入库
			dtabRelationStore.add(Dbo.db());
		}
	}

	@Method(desc = "在配置字段存储信息时，更新表中文名",
			logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制"
					+ "2、对集合的长度进行校验，如果集合为空，获取表信息失败"
					+ "3、遍历集合，更新每张表的中文名")
	@Param(name = "objectCollectTasks", desc = "对象采集对应信息表实体数组", range = "与数据库对应实体字段规则一致"
			, isBean = true)
	public void updateTableZhName(Object_collect_task[] objectCollectTasks) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		// 2、对集合的长度进行校验，如果集合为空，说明获取表信息失败
		if (objectCollectTasks == null || objectCollectTasks.length == 0) {
			throw new BusinessException("获取表信息失败");
		}
		for (int i = 0; i < objectCollectTasks.length; i++) {
			Object_collect_task object_collect_task = objectCollectTasks[i];
			Validator.notNull(object_collect_task.getOcs_id(),
					"保存第" + (i + 1) + "张表的任务编号不能为空");
			Validator.notBlank(object_collect_task.getEn_name(),
					"保存第" + (i + 1) + "张表的表英文名必须填写");
			Validator.notBlank(object_collect_task.getZh_name(),
					"保存第" + (i + 1) + "张表的表中文名必须填写");
			// 3、遍历集合，更新每张表的中文名
			DboExecute.updatesOrThrow(
					"保存第" + (i + 1) + "张表名称信息失败",
					"update " + Object_collect_task.TableName + " set zh_name = ? " +
							" where ocs_id = ? and en_name=?",
					object_collect_task.getZh_name(), object_collect_task.getOcs_id(),
					object_collect_task.getEn_name());
		}
	}

	@Method(desc = "查询数据存储层配置信息",
			logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
					"2.查询所有数据存储层配置信息")
	@Return(desc = "返回关联查询数据存储层信息", range = "无限制")
	public Result searchDataStore() {
		// 1.数据可访问权限处理方式，该方法不需要权限控制
		// 2.查询所有数据存储层配置信息
		return Dbo.queryResult("select * from " + Data_store_layer.TableName);
	}
}
