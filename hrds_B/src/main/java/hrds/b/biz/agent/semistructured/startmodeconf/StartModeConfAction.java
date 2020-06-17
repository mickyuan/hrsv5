package hrds.b.biz.agent.semistructured.startmodeconf;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.Validator;
import fd.ng.web.util.Dbo;
import hrds.b.biz.agent.bean.JobStartConf;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.Constant;
import hrds.commons.utils.DboExecute;
import hrds.commons.utils.etl.EtlJobUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@DocClass(desc = "半结构化采集定义启动方式配置类", author = "dhw", createdate = "2020/6/16 11:13")
public class StartModeConfAction extends BaseAction {

	@Method(desc = "获取半结构化采集作业配置", logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
			"2.关联查询作业定义与对象作业关系表查询作业配置信息" +
			"3.封装上游作业到作业配置集合中" +
			"4.获取任务表预览作业名称全部集合" +
			"5.此次任务的采集作业表信息" +
			"6.上次存在的表数据作业信息" +
			"7.获取差集,删除的作业" +
			"8.从作业配置信息集合中去除删除的作业" +
			"9.获取差集,从预览作业中新增新增的作业" +
			"10.合并两个集合并返回")
	@Param(name = "odc_id", desc = "对象采集ID", range = "新增半结构化采集配置时生成")
	@Return(desc = "", range = "")
	public List<Map<String, Object>> getEtlJobConfInfoFromObj(long odc_id) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		// 2.关联查询作业定义与对象作业关系表查询作业配置信息
		List<Map<String, Object>> etlRelationJobList = Dbo.queryList(
				"SELECT t1.ocs_id,t1.odc_id,t2.* from "
						+ Obj_relation_etl.TableName + " t1 JOIN "
						+ Etl_job_def.TableName + " t2 ON t1.etl_job = t2.etl_job"
						+ " WHERE t1.etl_sys_cd = t2.etl_sys_cd AND t1.sub_sys_cd = t2.sub_sys_cd"
						+ " AND t1.odc_id = ? ", odc_id);
		// 3.封装上游作业到作业配置集合中
		setPreJobList(etlRelationJobList);
		// 4.获取任务表预览作业名称全部集合
		List<Map<String, Object>> previewJobList = getPreviewJob(odc_id);
		// 5.此次任务的采集作业表信息
		List<Object> defaultEtlJob =
				previewJobList.stream().map(item -> item.get("etl_job")).collect(Collectors.toList());
		// 6.上次存在的表数据作业信息
		List<Object> etlRelationJobData =
				etlRelationJobList.stream().map(item -> item.get("etl_job")).collect(Collectors.toList());
		// 7.获取差集,删除的作业
		List<Object> reduceDeleteList = defaultEtlJob.stream().filter(item -> !etlRelationJobData.contains(item))
				.collect(Collectors.toList());
		// 8.从作业配置信息集合中去除删除的作业
		etlRelationJobList.removeIf(
				itemMap -> {
					if (reduceDeleteList.contains(itemMap.get("etl_job"))) {
						Dbo.execute(
								"DELETE FROM " + Obj_relation_etl.TableName + " WHERE etl_job = ?",
								itemMap.get("etl_job"));
						Dbo.execute(
								"DELETE FROM "
										+ Etl_job_def.TableName
										+ " WHERE etl_job = ? AND etl_sys_cd = ? AND sub_sys_cd = ?",
								itemMap.get("etl_job"),
								itemMap.get("etl_sys_cd"),
								itemMap.get("sub_sys_cd"));
						return true;
					} else {
						return false;
					}
				});
		// 9.获取差集,从预览作业中新增新增的作业
		List<Object> reduceAddList = etlRelationJobData.stream().filter(item -> !defaultEtlJob.contains(item))
				.collect(Collectors.toList());
		previewJobList.removeIf(item -> !reduceAddList.contains(item.get("etl_job")));
		// 10.合并两个集合并返回
		etlRelationJobList.addAll(previewJobList);
		return etlRelationJobList;
	}

	@Method(desc = "封装上游作业到作业配置集合中", logicStep = "1.封装上游作业到作业配置集合中")
	@Param(name = "etlJobList", desc = "作业配置集合", range = "无限制")
	private void setPreJobList(List<Map<String, Object>> etlJobList) {
		// 1.封装上游作业到作业配置集合中
		etlJobList.forEach(
				itemMap -> {
					List<Object> preJobList =
							Dbo.queryOneColumnList(
									"SELECT pre_etl_job FROM "
											+ Etl_dependency.TableName
											+ " WHERE etl_sys_cd = ? AND etl_job = ?",
									itemMap.get("etl_sys_cd").toString(),
									itemMap.get("etl_job").toString());
					itemMap.put("pre_etl_job", preJobList);
				});
	}

	@Method(desc = "获取当前半结构化采集任务下的作业信息",
			logicStep = "1: 检查该任务是否存在,"
					+ "2: 查询任务的配置信息,"
					+ "3: 检查任务下是否存在表的信息,"
					+ "4: 查询任务下的表信息,"
					+ "5: 将表的信息和任务的信息进行组装成作业信息,组合的形式为 "
					+ "作业名的组合形式为 数据源编号_agentID_分类编号_表名_文件类型"
					+ "作业描述的组合形式为 : 数据源名称_agent名称_分类名称_表中文名_文件类型")
	@Param(name = "odc_id", desc = "对象采集ID", range = "新增半结构化采集配置信息时生成")
	@Return(desc = "组合后的作业信息集合", range = "不为空")
	public List<Map<String, Object>> getPreviewJob(long odc_id) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		// 2.检查该任务是否存在
		long countNum = Dbo.queryNumber(
				"SELECT COUNT(1) FROM " + Object_collect.TableName + " WHERE odc_id = ?", odc_id)
				.orElseThrow(() -> new BusinessException("SQL查询错误"));
		if (countNum == 0) {
			throw new BusinessException("当前任务(" + odc_id + ")不存在");
		}
		// 3.检查任务下是否存在表的信息
		countNum = Dbo.queryNumber(
				"SELECT COUNT(1) FROM " + Object_collect_task.TableName + " WHERE odc_id = ?", odc_id)
				.orElseThrow(() -> new BusinessException("SQL查询错误"));
		if (countNum < 1) {
			throw new BusinessException("当前任务(" + odc_id + ")下不存在表信息");
		}
		// 4.查询当前半结构化采集任务下的表信息
		List<Map<String, Object>> tableList = Dbo.queryList(
				"select oct.ocs_id,oct.en_name,oct.zh_name,ai.agent_type,ore.ocs_id,ds.datasource_number,"
						+ "ds.datasource_name,oc.obj_number,oc.obj_collect_name,ai.agent_id from "
						+ Object_collect_task.TableName + " oct left join "
						+ Obj_relation_etl.TableName + " ore on oct.ocs_id = ore.ocs_id join "
						+ Object_collect.TableName + " oc on oc.odc_id = oct.odc_id join "
						+ Agent_info.TableName + " ai on oc.agent_id = ai.agent_id join "
						+ Data_source.TableName + " ds on ai.source_id=ai.source_id "
						+ " where oct.odc_id = ? ORDER BY oct.en_name", odc_id);
	    /*
		     5.将表的信息和任务的信息进行组装成作业信息,组合的形式为
		     作业名的组合形式为 数据源编号_agentID_对象采集设置编号_表名
		     作业描述的组合形式为 : 数据源名称_agent名称_对象采集任务名称_表中文名
	     */
		tableList.forEach(
				itemMap -> setObjCollectJobParam(odc_id, itemMap));
		return tableList;
	}

	@Method(desc = "设置半结构化采集作业配置默认信息", logicStep = "1.设置作业名称" +
			"2.设置作业描述" +
			"3.设置作业参数" +
			"4.设置调度频率的默认值" +
			"5.设置默认的作业优先级" +
			"6.设置默认的调度时间位移")
	@Param(name = "odc_id", desc = "采集任务ID", range = "新增半结构化采集配置时生成")
	@Param(name = "tableItemMap", desc = "采集表数据信息", range = "不可为空")
	private void setObjCollectJobParam(long odc_id, Map<String, Object> tableItemMap) {
		// 1.设置作业名称
		String pro_name = tableItemMap.get("datasource_number")
				+ Constant.SPLITTER
				+ tableItemMap.get("agent_id")
				+ Constant.SPLITTER
				+ tableItemMap.get("obj_number")
				+ Constant.SPLITTER
				+ tableItemMap.get("en_name")
				+ Constant.SPLITTER;
		tableItemMap.put("etl_job", pro_name);
		// 2.设置作业描述
		String etl_job_desc = tableItemMap.get("datasource_name")
				+ Constant.SPLITTER
				+ tableItemMap.get("agent_name")
				+ Constant.SPLITTER
				+ tableItemMap.get("obj_collect_name")
				+ Constant.SPLITTER
				+ tableItemMap.get("zh_name")
				+ Constant.SPLITTER;
		tableItemMap.put("etl_job_desc", etl_job_desc);
		// 3.设置作业参数
		String pro_para = odc_id
				+ Constant.ETLPARASEPARATOR
				+ tableItemMap.get("en_name")
				+ Constant.ETLPARASEPARATOR
				+ tableItemMap.get("agent_type")
				+ Constant.ETLPARASEPARATOR
				+ Constant.BATCH_DATE
				+ Constant.ETLPARASEPARATOR;
		tableItemMap.put("pro_para", pro_para);
		// 4.设置调度频率的默认值
		tableItemMap.put("disp_freq", Dispatch_Frequency.DAILY.getCode());
		// 5.设置默认的作业优先级
		tableItemMap.put("job_priority", 0);
		// 6.设置默认的调度时间位移
		tableItemMap.put("disp_offset", 0);
	}

	@Method(desc = "获取任务Agent的部署路径及日志目录",
			logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制"
					+ "2.检查该任务是否存在"
					+ "3.获取任务部署的Agent路径及日志地址,并将程序类型,名称的默认值返回 "
					+ "4.获取任务存在着抽取作业关系,如果存在就获取一条信息就可以, 因为同个任务的作业工程编号,任务编号是一个")
	@Param(name = "odc_id", desc = "采集任务ID", range = "新增半结构化采集配置时生成")
	@Return(desc = "返回Agent部署的程序目录", range = "不可为空")
	public Map<String, Object> getAgentPath(long odc_id) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		// 2.检查该任务是否存在
		long countNum = Dbo.queryNumber(
				"SELECT COUNT(1) FROM " + Object_collect.TableName + " WHERE odc_id = ?", odc_id)
				.orElseThrow(() -> new BusinessException("SQL查询错误"));

		if (countNum != 1) {
			throw new BusinessException("当前任务(" + odc_id + ")不再存在");
		}
		// 3.获取任务部署的Agent路径及日志地址,并将程序类型,名称的默认值返回
		Map<String, Object> map = Dbo.queryOneObject(
				"SELECT t3.ai_desc pro_dic,t3.log_dir log_dic FROM "
						+ Object_collect.TableName + " t1 JOIN "
						+ Agent_info.TableName + " t2 ON t1.agent_id = t2.agent_id JOIN "
						+ Agent_down_info.TableName
						+ " t3 ON t2.agent_ip = t3.agent_ip AND t2.agent_port = t3.agent_port "
						+ " WHERE t1.odc_id = ?",
				odc_id);
		map.put("pro_type", Pro_Type.SHELL.getCode());
		map.put("pro_name", Constant.SHELLCOMMAND);
		// 4.获取对象采集关系表信息，因为同一个任务下工程编号，任务编号都是一致的
		Map<String, Object> objRelationEtlMap = Dbo.queryOneObject(
				"SELECT * FROM " + Obj_relation_etl.TableName + " WHERE odc_id = ? LIMIT 1",
				odc_id);
		map.putAll(objRelationEtlMap);
		return map;
	}

	@Method(desc = "保存半结构化采集启动方式配置", logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
			"2.删除当前采集任务下的全部作业信息" +
			"3.检查作业系统参数的作业程序目录，不存在则添加" +
			"4.检查作业系统参数的作业日志是否存在，不存在则添加" +
			"5.默认增加一个资源类型,先检查是否存在,不存在则添加" +
			"6.获取作业资源关系信息" +
			"7.先获取当前作业调度工程任务下的作业名称" +
			"8.检查表名是否存在,存在更新，不存在新增" +
			"9.保存每个作业的上游依赖关系" +
			"10.对每个采集作业定义资源分配 ,检查作业所需资源是否存在,如果存在则跳过" +
			"11.获取对象作业关系信息" +
			"12.保存对象作业关系表,检查作业名称是否存在,如果存在则更新,反之新增" +
			"13.把是否发送状态改为是，表示为当前的配置任务完成")
	@Param(name = "odc_id", desc = "采集任务ID", range = "新增半结构化采集配置时生成")
	@Param(name = "etlJobDefs", desc = "作业定义实体数组", range = "与数据库对应表字段规则一致", isBean = true)
	@Param(name = "jobStartConfs", desc = "作业启动配置实体数组", range = "无限制", isBean = true)
	public void saveStartModeConfData(long odc_id, Etl_job_def[] etlJobDefs, JobStartConf[] jobStartConfs) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		// 2.删除当前采集任务下的全部作业信息
		Dbo.execute(
				"DELETE FROM " + Etl_job_def.TableName + " WHERE etl_job in"
						+ " (SELECT t2.etl_job from " + Obj_relation_etl.TableName + " t1 JOIN "
						+ Etl_job_def.TableName + " t2 ON t1.etl_job = t2.etl_job "
						+ " WHERE t1.etl_sys_cd = t2.etl_sys_cd "
						+ " AND t1.sub_sys_cd = t2.sub_sys_cd AND t1.odc_id = ?)",
				odc_id);
		for (JobStartConf jobStartConf : jobStartConfs) {
			Obj_relation_etl obj_relation_etl = jobStartConf.getObj_relation_etl();
			// 3.检查作业系统参数的作业程序目录，不存在则添加
			EtlJobUtil.setDefaultEtlParaConf(obj_relation_etl.getEtl_sys_cd(), Constant.PARA_HYRENBIN,
					jobStartConf.getPro_dic() + File.separator);
			// 4.检查作业系统参数的作业日志是否存在，不存在则添加
			EtlJobUtil.setDefaultEtlParaConf(obj_relation_etl.getEtl_sys_cd(), Constant.PARA_HYRENLOG,
					jobStartConf.getLog_dic());
			// 5.默认增加一个资源类型,先检查是否存在,不存在则添加
			EtlJobUtil.setDefaultEtlResource(obj_relation_etl.getEtl_sys_cd());
			// 6.获取作业资源关系信息
			List<String> jobResource = EtlJobUtil.getJobResource(obj_relation_etl.getEtl_sys_cd());
			// 7.先获取当前作业调度工程任务下的作业名称
			List<String> etlJobList = EtlJobUtil.getEtlJob(obj_relation_etl.getEtl_sys_cd(),
					obj_relation_etl.getSub_sys_cd());
			for (Etl_job_def etl_job_def : etlJobDefs) {
				Validator.notBlank(etl_job_def.getEtl_job(), "作业名称不能为空!!!");
				Validator.notBlank(etl_job_def.getEtl_sys_cd(), "工程编号不能为空!!!");
				Validator.notBlank(etl_job_def.getSub_sys_cd(), "任务编号不能为空!!!");
				Validator.notBlank(etl_job_def.getPro_type(), "作业程序类型不能为空!!!");
				// 作业的程序路径
				etl_job_def.setPro_dic(jobStartConf.getPro_dic() + File.separator);
				// 作业的日志程序路径
				etl_job_def.setLog_dic(Constant.HYRENLOG);
				// 默认作业都是有效的
				etl_job_def.setJob_eff_flag(Job_Effective_Flag.YES.getCode());
				// 默认当天调度作业信息
				etl_job_def.setToday_disp(Today_Dispatch_Flag.YES.getCode());
				// 作业的更新信息时间
				etl_job_def.setUpd_time(
						DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate())
								+ " "
								+ DateUtil.parseStr2TimeWith6Char(DateUtil.getSysTime()));

				// 8.检查表名是否存在,存在更新，不存在新增
				if (etlJobList.contains(etl_job_def.getEtl_job())) {
					etl_job_def.update(Dbo.db());
				} else {
					// 新增
					etl_job_def.add(Dbo.db());
				}
				// 9.保存每个作业的上游依赖关系
				saveEtlDependencies(obj_relation_etl.getEtl_sys_cd(), etl_job_def.getEtl_job(),
						jobStartConf.getPre_etl_job());
				// 10.对每个采集作业定义资源分配 ,检查作业所需资源是否存在,如果存在则跳过
				EtlJobUtil.setEtl_job_resource_rela(obj_relation_etl.getEtl_sys_cd(), etl_job_def, jobResource);
				// 11.获取对象作业关系信息
				List<String> relationEtl = getObjRelationEtl(odc_id);
				// 12.保存对象作业关系表,检查作业名称是否存在,如果存在则更新,反之新增
				setObjRelationEtl(etl_job_def, relationEtl, etlJobList, obj_relation_etl);
			}

			// 13.把是否发送状态改为是，表示为当前的配置任务完成
			DboExecute.updatesOrThrow(
					"此次采集任务配置完成,更新发送状态失败",
					"UPDATE " + Object_collect.TableName + " SET is_sendok = ? WHERE odc_id = ?",
					IsFlag.Shi.getCode(), odc_id);
		}
	}

	@Method(desc = "保存作业所需的资源信息", logicStep = "1.获取差集,删除的作业" +
			"2.判断当前的作业信息是否存在,如果不存在则添加")
	@Param(name = "etlJobList", desc = "当前作业调度工程任务下的作业名称集合", range = "无限制")
	@Param(name = "etl_job_def", desc = "作业资源的信息集合", range = "不可为空", isBean = true)
	@Param(name = "relationEtl", desc = "抽数作业关系表信息集合", range = "可为空")
	@Param(name = "obj_relation_etl", desc = "对象作业关系表实体对象", range = "与数据库对应表字段规则一致",
			isBean = true)
	private void setObjRelationEtl(Etl_job_def etl_job_def, List<String> relationEtl,
	                               List<String> etlJobList, Obj_relation_etl obj_relation_etl) {
		// 1.获取差集,删除不存在的作业
		List<String> reduceDeleteList = relationEtl.stream().filter(item -> !etlJobList.contains(item))
				.collect(Collectors.toList());
		reduceDeleteList.forEach(etl_job ->
				DboExecute.deletesOrThrow("删除作业" + etl_job + "失败",
						"delete from " + Obj_relation_etl.TableName + " where etl_job =?", etl_job));
		// 2.判断当前的作业信息是否存在,如果不存在则添加
		if (!relationEtl.contains(etl_job_def.getEtl_job())) {
			obj_relation_etl.add(Dbo.db());
		}
	}

	@Method(desc = "获取当前同个数据源下表对象作业关系表", logicStep = "防止同个数据源下的半结构化采集任务出现重复的作业信息")
	@Param(name = "odc_id", desc = "对象采集ID", range = "新增半结构化采集配置时生成")
	@Return(desc = "返回抽数作业关系表下作业名称集合", range = "可以为空.为空表示没有作业存在")
	private List<String> getObjRelationEtl(long odc_id) {
		return Dbo.queryOneColumnList(
				"SELECT t1.etl_job FROM " + Obj_relation_etl.TableName + " t1 JOIN "
						+ Object_collect.TableName + " t2 ON t1.odc_id = t2.odc_id JOIN "
						+ Agent_info.TableName + " t3 ON t2.agent_id = t3.agent_id JOIN "
						+ Data_source.TableName + " t4 ON t3.source_id = t3.source_id JOIN "
						+ " WHERE t4.odc_id = ?", odc_id);
	}

	@Method(
			desc = "保存作业的依赖关系",
			logicStep = "1: 根据工程编号,作业名称删除当前作业和当前作业有关系的依赖作业.不关心删除条数"
					+ "2.封装批量入库依赖数据集合"
					+ "3.批量插入数据")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "不可为空")
	@Param(name = "status", desc = "作业有效标识", range = "不可为空")
	@Param(name = "jobRelation", desc = "上游作业名称", range = "不可为空")
	private void saveEtlDependencies(String etl_sys_cd, String etl_job, String[] pre_etl_job) {
		// 1.根据工程编号,作业名称删除当前作业的依赖.不关心删除条数
		Dbo.execute(
				"DELETE FROM " + Etl_dependency.TableName
						+ " WHERE (etl_job = ? OR pre_etl_job = ?) AND etl_sys_cd = ? ",
				etl_job, etl_job, etl_sys_cd);
		// 2.封装批量入库依赖数据集合
		List<Object[]> etlDepList = new ArrayList<>();
		if (pre_etl_job != null && pre_etl_job.length != 0) {
			for (String preEtlJob : pre_etl_job) {
				Object[] objects = new Object[6];
				objects[0] = etl_sys_cd;
				objects[1] = etl_sys_cd;
				objects[2] = etl_job;
				objects[3] = preEtlJob;
				objects[4] = Status.TRUE.getCode();
				objects[5] = Main_Server_Sync.YES.getCode();
				etlDepList.add(objects);
			}
		}
		// 3.批量插入数据
		Dbo.executeBatch("insert into " + Etl_dependency.TableName
				+ "(etl_sys_cd,pre_etl_sys_cd,etl_job,pre_etl_job,status,main_serv_sync)"
				+ " values(?,?,?,?,?,?)", etlDepList);
	}
}
