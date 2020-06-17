package hrds.b.biz.agent.semistructured.collectstoragelayerconf;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.Validator;
import fd.ng.db.resultset.Result;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.StoreLayerDataSource;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.DboExecute;

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
		if (Dbo.queryNumber("select count(*) from " + Object_collect.TableName + " where odc_id=?",
				odc_id).orElseThrow(() -> new BusinessException("sql查询错误")) == 0) {
			throw new BusinessException("odc_id=" + odc_id + "对应的半结构化采集任务已不存在");
		}
		// 3.关联查询半结构化采集对应表信息与表对应存储信息获取存储配置信息
		return Dbo.queryResult("select * from " + Object_collect_task.TableName + " oct left join "
						+ Dtab_relation_store.TableName + " drs on oct.ocs_id=drs.tab_id "
						+ " where odc_id=? and drs.data_source=?",
				odc_id, StoreLayerDataSource.OBJ.getCode());
	}

	@Method(desc = "根据对象采集任务编号获取存储目的地数据信息",
			logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
					"2.获取当前表对应存储信息，为空说明之前没有定义过存储目的地")
	@Param(name = "ocs_id", desc = "对象采集对应表信息主键ID", range = "新增对应采集对应信息时生成")
	@Return(desc = "返回根据对象采集任务编号获取存储目的地数据信息", range = "无限制，为空说明之前没有定义过存储目的地")
	public Result getStorageLayerDestById(long ocs_id) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		// 2.获取当前表对应存储信息，为空说明之前没有定义过存储目的地
		return Dbo.queryResult(
				"select * from " + Object_collect_task.TableName + " oct left join "
						+ Dtab_relation_store.TableName + " drs on oct.ocs_id=drs.tab_id "
						+ " where ocs_id=? and drs.data_source=?",
				ocs_id, StoreLayerDataSource.OBJ.getCode());
	}

	@Method(desc = "根据存储层配置ID获取当前存储层配置属性信息",
			logicStep = "1.根据存储层配置ID获取当前存储层配置属性信息")
	@Param(name = "dsl_id", desc = "存储层配置ID", range = "新增存储层配置信息时生成")
	@Return(desc = "返回根据存储层配置ID获取当前存储层配置属性信息", range = "无限制")
	public Result getStorageLayerAttrById(long dsl_id) {
		// 1.根据存储层配置ID获取当前存储层配置属性信息
		return Dbo.queryResult(
				"select * from " + Data_store_layer_attr.TableName + " where dsl_id=?", dsl_id);
	}

	@Method(desc = "获取当前表对应列存储信息", logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
			"2.判断当前表对应的列信息是否存在" +
			"3.获取当前表对应列存储信息")
	@Param(name = "dsl_id", desc = "存储层配置ID", range = "新增存储层配置信息时生成")
	@Param(name = "ocs_id", desc = "对象采集对应表信息主键ID", range = "新增对应采集对应信息时生成")
	@Return(desc = "返回获取当前表对应列存储信息", range = "无限制")
	public Result getColumnStorageLayerInfo(long dsl_id, long ocs_id) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		// 2.判断当前表对应的列信息是否存在
		if (Dbo.queryNumber(
				"select count(*) from " + Object_collect_struct.TableName + " where ocs_id=?",
				ocs_id).orElseThrow(() -> new BusinessException("sql查询错误")) == 0) {
			throw new BusinessException("当前表对应的列信息不存在，请检查");
		}
		// 3.获取当前表对应列存储信息
		return Dbo.queryResult(
				"select ocs.struct_id,ocs.column_name,ocs.data_desc,dsla.dsla_storelayer from "
						+ Object_collect_struct.TableName + " ocs left join "
						+ Dcol_relation_store.TableName + " drs on oct.struct_id=drs.col_id left join "
						+ Data_store_layer_added.TableName + " dsla on drs.dsl_id=dsla.dsl_id " +
						" where ocs_id=? and dsl_id=? and drs.data_source=?",
				ocs_id, dsl_id, StoreLayerDataSource.OBJ.getCode());
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
			for (Dcol_relation_store dcolRelationStore : dcolRelationStores) {
				// 6.遍历该列附加信息ID并保存数据字段存储关系入库
				addDcolRelationStore(dcolRelationStore);
			}
		}
	}

	@Method(desc = "新增数据字段存储关系表信息", logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
			"2.新增数据字段存储关系表信息")
	@Param(name = "dcol_relation_store", desc = "数据字段存储关系实体对象", range = "与对应数据库表字段规则一致",
			isBean = true)
	private void addDcolRelationStore(Dcol_relation_store dcol_relation_store) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		dcol_relation_store.setData_source(StoreLayerDataSource.OBJ.getCode());
		// 2.新增数据字段存储关系表信息
		dcol_relation_store.add(Dbo.db());
	}

//	@Method(desc = "根据附加信息ID获取存储层类型", logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
//			"2.通过字段存储附加信息ID获得存储层类型" +
//			"3.如果获取不到或者获取到的值有多个说明存储类型有误" +
//			"4.返回当前附加信息对应存储层类型")
//	@Param(name = "dsladId", desc = "附件信息ID", range = "新增附加信息时生成")
//	@Return(desc = "返回存储层类型", range = "不能为空")
//	private String getStoreType(long dsladId) {
//		// 1.数据可访问权限处理方式：该方法没有访问权限限制
//		// 2.通过字段存储附加信息ID获得存储层类型
//		List<String> storeTypes = Dbo.queryOneColumnList(
//				"select dsl.store_type from "
//						+ Data_store_layer.TableName + " dsl, "
//						+ Data_store_layer_added.TableName + " dsla "
//						+ " where dsl.dsl_id = dsla.dsl_id and dsla.dslad_id = ?",
//				dsladId);
//		// 3.如果获取不到或者获取到的值有多个说明存储类型有误
//		if (storeTypes.size() != 1) {
//			throw new BusinessException("通过字段存储附加信息获得存储目的地类型出错，请检查");
//		}
//		return storeTypes.get(0);
//	}

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
					objectCollectStruct.getColumn_name(), objectCollectStruct.getStruct_id());
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
		if (Dbo.queryNumber(
				"select count(*) from " + Object_collect.TableName + " where odc_id=?", odc_id)
				.orElseThrow(() -> new BusinessException("sql查询错误")) == 0) {
			throw new BusinessException("当前半结构化任务已不存在，odc_id=" + odc_id);
		}
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
			dtabRelationStore.setIs_successful(IsFlag.Fou.getCode());
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
					"保存第" + i + "张表名称信息失败",
					"update " + Object_collect_task.TableName + " set zh_name = ? " +
							" where ocs_id = ? and en_name=?",
					object_collect_task.getEn_name(), object_collect_task.getOcs_id(),
					object_collect_task.getZh_name());
		}
	}

}
