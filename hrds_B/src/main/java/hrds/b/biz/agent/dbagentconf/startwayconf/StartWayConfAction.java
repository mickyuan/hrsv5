package hrds.b.biz.agent.dbagentconf.startwayconf;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.web.util.Dbo;
import hrds.b.biz.agent.datafileconf.CheckParam;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.Constant;
import hrds.commons.utils.DboExecute;
import hrds.commons.utils.etl.EtlJobUtil;
import hrds.commons.utils.jsch.ChineseUtil;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@DocClass(desc = "定义启动方式配置", author = "Lee-Qiang")
public class StartWayConfAction extends BaseAction {

	// 日志打印
	private static final Log logger = LogFactory.getLog(StartWayConfAction.class);

	@Method(desc = "获取工程信息", logicStep = "获取作业调度工程信息,然后返回到前端")
	@Return(desc = "返回工程信息集合", range = "为空表示没有工程信息")
	public List<Etl_sys> getEtlSysData() {
		// 获取作业调度工程信息,然后返回到前端
		return Dbo.queryList(
				Etl_sys.class, "SELECT * FROM " + Etl_sys.TableName + " WHERE user_id = ?", getUserId());
	}

	@Method(desc = "根据工程编号获取任务列表", logicStep = "1 : 判断工程编号是否存在, 2 : 根据工程编号返回任务信息")
	@Param(name = "etl_sys_cd", range = "不可为空", desc = "选择的工程编号")
	@Return(desc = "返回工程下的任务信息", range = "可以为空,如果为空表示当前工程下没有任务信息存在")
	public List<Etl_sub_sys_list> getEtlSubSysData(String etl_sys_cd) {

		//    1 : 判断工程编号是否存在
		long countNum =
				Dbo.queryNumber(
						"SELECT COUNT(1) FROM "
								+ Etl_sys.TableName
								+ " WHERE etl_sys_cd = ? AND user_id = ?",
						etl_sys_cd,
						getUserId())
						.orElseThrow(() -> new BusinessException("SQL查询错误"));
		if (countNum != 1) {
			throw new BusinessException("当前工程编号 :" + etl_sys_cd + " 不存在");
		}
		//    2 : 根据工程编号返回任务信息
		return Dbo.queryList(
				Etl_sub_sys_list.class,
				"SELECT * FROM " + Etl_sub_sys_list.TableName + " WHERE etl_sys_cd = ?",
				etl_sys_cd);
	}

	@Method(
			desc = "获取任务下的作业信息",
			logicStep =
					""
							+ "1: 检查该任务是否存在,"
							+ "2: 查询任务的配置信息,"
							+ "3: 检查任务下是否存在表的信息,"
							+ "4: 查询任务下的表信息,"
							+ "5: 将表的信息和任务的信息进行组装成作业信息,组合的形式为 "
							+ "作业名的组合形式为 数据源编号_agentID_分类编号_表名_文件类型"
							+ "作业描述的组合形式为 : 数据源名称_agent名称_分类名称_表中文名_文件类型")
	@Param(name = "colSetId", desc = "采集任务的ID", range = "不可为空的整数")
	@Return(desc = "组合后的作业信息集合", range = "不为空")
	public List<Map<String, Object>> getPreviewJob(long colSetId) {

		// 1: 检查该任务是否存在, 2: 查询任务的配置信息
		Map<String, Object> databaseMap = getDatabaseData(colSetId);

		// 3: 检查任务下是否存在表的信息
		long countNum =
				Dbo.queryNumber(
						"SELECT COUNT(1) FROM " + Table_info.TableName + " WHERE database_id = ?", colSetId)
						.orElseThrow(() -> new BusinessException("SQL查询错误"));
		if (countNum < 1) {
			throw new BusinessException("当前任务(" + colSetId + ")下不存在表信息");
		}
		//    4: 查询任务下的表信息
		List<Map<String, Object>> tableList =
				Dbo.queryList(
						"select t1.table_id,t1.table_name,t1.table_ch_name,t2.dbfile_format,ai.agent_type,t2.ded_id from "
								+ Table_info.TableName
								+ " t1 left join "
								+ Data_extraction_def.TableName
								+ " t2 on t1.table_id = t2.table_id join "
								+ Database_set.TableName
								+ " ds on t1.database_id = ds.database_id "
								+ "join "
								+ Agent_info.TableName
								+ " ai on ds.agent_id = ai.agent_id  where t1.database_id = ? ORDER BY t1.table_name",
						colSetId);

    /*
     5: 将表的信息和任务的信息进行组装成作业信息,组合的形式为
     作业名的组合形式为 数据源编号_agentID_分类编号_表名_文件类型
     作业描述的组合形式为 : 数据源名称_agent名称_分类名称_表中文名_文件类型
    */
		tableList.forEach(
				itemMap -> {
					setCollectDataBaseParam(colSetId, itemMap, databaseMap);
				});

		return tableList;
	}

	@Method(desc = "设置采集表的默认数据信息", logicStep = "")
	@Param(name = "colSetId", desc = "采集任务ID", range = "不可为空")
	@Param(name = "tableItemMap", desc = "采集表数据信息", range = "不可为空")
	@Param(name = "databaseMap", desc = "采集任务配置信息", range = "不可为空")
	private void setCollectDataBaseParam(
			long colSetId, Map<String, Object> tableItemMap, Map<String, Object> databaseMap) {
		// 作业采集文件类型
		String dbfile_format =
				ChineseUtil.getPingYin(
						FileFormat.ofValueByCode(((String) tableItemMap.get("dbfile_format"))));
		// 作业名称
		String pro_name =
				databaseMap.get("datasource_number")
						+ Constant.SPLITTER
						+ databaseMap.get("agent_id")
						+ Constant.SPLITTER
						+ databaseMap.get("classify_num")
						+ Constant.SPLITTER
						+ tableItemMap.get("table_name")
						+ Constant.SPLITTER
						+ dbfile_format;
		tableItemMap.put("etl_job", pro_name);
		// 作业描述
		String etl_job_desc =
				databaseMap.get("datasource_name")
						+ Constant.SPLITTER
						+ databaseMap.get("agent_name")
						+ Constant.SPLITTER
						+ databaseMap.get("classify_name")
						+ Constant.SPLITTER
						+ tableItemMap.get("table_ch_name")
						+ Constant.SPLITTER
						+ dbfile_format;
		tableItemMap.put("etl_job_desc", etl_job_desc);
		// 作业参数
		String pro_para =
				colSetId
						+ Constant.ETLPARASEPARATOR
						+ tableItemMap.get("table_name")
						+ Constant.ETLPARASEPARATOR
						+ tableItemMap.get("agent_type")
						+ Constant.ETLPARASEPARATOR
						+ Constant.BATCH_DATE
						+ Constant.ETLPARASEPARATOR
						+ tableItemMap.get("dbfile_format");
		tableItemMap.put("pro_para", pro_para);

		// 设置调度的默认值
		tableItemMap.put("disp_freq", Dispatch_Frequency.DAILY.getCode());
		// 设置默认的作业优先级
		tableItemMap.put("job_priority", "0");
		// 设置默认的调度触发方式
		tableItemMap.put("disp_offset", "0");
	}

	@Method(desc = "获取任务下的作业信息", logicStep = "" + "1: 检查该任务是否存在," + "2: 查询任务的配置信息,")
	@Param(name = "colSetId", desc = "采集任务的ID", range = "不可为空的整数")
	@Return(desc = "采集任务的配置", range = "不为空")
	private Map<String, Object> getDatabaseData(long colSetId) {
		//    1: 检查该任务是否存在
		long countNum =
				Dbo.queryNumber(
						"SELECT COUNT(1) FROM " + Database_set.TableName + " WHERE database_id = ?",
						colSetId)
						.orElseThrow(() -> new BusinessException("SQL查询错误"));

		if (countNum == 0) {
			throw new BusinessException("当前任务(" + colSetId + ")不存在");
		}

		// 2: 查询任务的配置信息
		return Dbo.queryOneObject(
				"select t1.database_id,t4.datasource_number,t4.datasource_name,t3.agent_id,"
						+ "t3.agent_name,t2.classify_num,t3.agent_type,t2.classify_name from "
						+ Database_set.TableName
						+ " t1 JOIN "
						+ Collect_job_classify.TableName
						+ " t2 ON t1.classify_id = t2.classify_id JOIN "
						+ Agent_info.TableName
						+ " t3 ON t1.agent_id = t3.agent_id JOIN "
						+ Data_source.TableName
						+ " t4 ON t3.source_id = t4.source_id "
						+ " WHERE t1.database_id = ?",
				colSetId);
	}

	@Method(desc = "获取编辑时任务下的作业信息", logicStep = "1: 获取任务信息的作业信息, 2: 获取每个作业的上游作业信息")
	@Param(name = "colSetId", desc = "任务ID", range = "不可为空")
	@Return(desc = "返回作业信息", range = "可以为空..为空表示没有设置作业信息")
	public List<Map<String, Object>> getEtlJobData(long colSetId) {
		List<Map<String, Object>> etlJobList =
				Dbo.queryList(
						"SELECT t1.database_id,t1.ded_id,t2.* from "
								+ Take_relation_etl.TableName
								+ " t1 JOIN "
								+ Etl_job_def.TableName
								+ " t2 ON "
								+ "t1.etl_job = t2.etl_job WHERE t1.etl_sys_cd = t2.etl_sys_cd AND t1.sub_sys_cd = t2.sub_sys_cd "
								+ "AND t1.database_id = ? ",
						colSetId);
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

		// 获取任务表作业名称全部集合
		List<Map<String, Object>> previewJob = getPreviewJob(colSetId);
		// 此次任务的采集作业表信息
		List<Object> databaseDefaultEtlJob =
				previewJob.stream().map(item -> item.get("etl_job")).collect(Collectors.toList());
		// 上次存在的表数据作业信息
		List<Object> etlJobData =
				etlJobList.stream().map(item -> item.get("etl_job")).collect(Collectors.toList());

		Map<String, List<Object>> differenceInfo = getDifferenceInfo(databaseDefaultEtlJob, etlJobData);

		// 获取新增的作业
		List<Object> addEtlJob = differenceInfo.get("add");
		previewJob.removeIf(item -> !addEtlJob.contains(item.get("etl_job")));
		//	//删除上次存在的表信息并删除,留下新增的
		//	if (previewJob.size() >= etlJobList.size()) {
		//	  //获取已存在表的作业名称信息
		//	  List<Object> etl_job = etlJobList.stream()
		//		  .map(itemMap -> itemMap.get("etl_job")).collect(Collectors.toList());
		//	  previewJob.removeIf(itemMap -> etl_job.contains(itemMap.get("etl_job")));
		//
		//	  etlJobList.addAll(previewJob);
		//
		//	} else {
		// 获取已存在表的作业名称信息
		//	  List<Object> etl_job = previewJob.stream()
		//		  .map(itemMap -> itemMap.get("etl_job")).collect(Collectors.toList());
		// 获取删除的作业
		List<Object> delete = differenceInfo.get("delete");
		etlJobList.removeIf(
				itemMap -> {
					if (delete.contains(itemMap.get("etl_job"))) {
						Dbo.execute(
								"DELETE FROM " + Take_relation_etl.TableName + " WHERE etl_job = ?",
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
		//	}
		etlJobList.addAll(previewJob);
		return etlJobList;
	}

	@Method(
			desc = "获取任务Agent的部署路径及日志目录",
			logicStep =
					""
							+ "1: 检查当前任务是否存在; "
							+ "2: 获取任务部署的Agent路径及日志地址,并将程序类型,名称的默认值返回 "
							+ "3: : 获取任务存在着抽取作业关系.. 如果存在就获取一条信息就可以... 因为同个任务的作业工程编号,任务编号是一个"
							+ "4: 合并数据集返回数据")
	@Param(name = "colSetId", desc = "采集任务编号", range = "不可为空的整数")
	@Return(desc = "返回Agent部署的程序目录", range = "不可为空")
	public Map<String, Object> getAgentPath(long colSetId) {
		//    1: 检查该任务是否存在
		long countNum =
				Dbo.queryNumber(
						"SELECT COUNT(1) FROM " + Database_set.TableName + " WHERE database_id = ?",
						colSetId)
						.orElseThrow(() -> new BusinessException("SQL查询错误"));

		if (countNum != 1) {
			throw new BusinessException("当前任务(" + colSetId + ")不再存在");
		}
		//    2: 获取任务部署的Agent路径及日志地址,并将程序类型,名称的默认值返回
		Map<String, Object> map =
				Dbo.queryOneObject(
						"SELECT t3.ai_desc pro_dic,t3.log_dir log_dic FROM "
								+ Database_set.TableName
								+ " t1 JOIN "
								+ Agent_info.TableName
								+ " t2 ON t1.agent_id = t2.agent_id JOIN "
								+ Agent_down_info.TableName
								+ " t3 ON t2.agent_ip = t3.agent_ip AND t2.agent_port = t3.agent_port "
								+ " WHERE t1.database_id = ? LIMIT 1",
						colSetId);
		map.put("pro_type", Pro_Type.SHELL.getCode());
		map.put("pro_name", Constant.SHELLCOMMAND);

		// 3: 获取任务存在着抽取作业关系.. 如果存在就获取一条信息就可以... 因为同个任务的作业工程编号,任务编号是一个
		map.putAll(
				Dbo.queryOneObject(
						"SELECT * FROM " + Take_relation_etl.TableName + " WHERE database_id = ? LIMIT 1",
						colSetId));
		return map;
	}

	@Method(
			desc = "保存启动配置信息",
			logicStep =
					""
							+ "1: 获取任务配置信息"
							+ "2: 获取表名称"
							+ "3: 获取任务的卸数抽取作业关系信息,如果当前的任务下存在此作业信息..则提示作业名称重复"
							+ "4: "
							+ "3: 放入作业需要数据信息"
							+ "4: 将作业的信息存入数据库中"
							+ "5，这里如果都配置文采则将此次任务的 database_set表中的字段(is_sendok) 更新为是,是表示为当前的配置任务完成")
	@Param(name = "colSetId", desc = "任务的ID", range = "不可为空的整数")
	@Param(name = "etl_sys_cd", desc = "作业工程编号", range = "不可为空")
	@Param(name = "sub_sys_cd", desc = "作业任务编号", range = "不可为空")
	@Param(name = "pro_dic", desc = "agent部署目录", range = "不可为空")
	@Param(name = "log_dic", desc = "agent日志路径", range = "不可为空")
	@Param(name = "source_id", desc = "数据源ID", range = "不可为空")
	@Param(
			name = "jobRelations",
			desc = "作业的依赖关系",
			range = "可为空",
			example = "数据结构如: {aaaa:bbbb^cccc^dddd},其中 aaaa表示作业名称,bbbb,cccc,dddd分别表示为上游作业名称",
			nullable = true)
	@Param(
			name = "etlJobs",
			range =
					"作业 Etl_job_def 数组字符串,每个对象的应该都应该包含所有的实体信息如:"
							+ "{作业名(etl_job),工程代码(etl_sys_cd),子系统代码(sub_sys_cd),作业描述(etl_job_desc),"
							+ "作业程序类型(pro_type,使用代码项Pro_Type),作业程序目录(pro_dic),作业程序名称(pro_name),"
							+ "作业程序参数(pro_para),日志目录(log_dic),调度频率(disp_freq,代码项Dispatch_Frequency),"
							+ "调度时间位移(disp_offset),调度触发方式(disp_type),调度触发时间(disp_time)}",
			desc = "",
			isBean = true)
	@Param(name = "ded_arr", desc = "卸数文件的ID", range = "不可为空的字符串,多个参数之间使用 ^ 隔开")
	public void saveJobDataToDatabase(
			long colSetId,
			long source_id,
			String etl_sys_cd,
			String sub_sys_cd,
			String pro_dic,
			String log_dic,
			Etl_job_def[] etlJobs,
			String ded_arr,
			String jobRelations) {

		List<String> dedList = StringUtil.split(ded_arr, "^");
		if (etlJobs.length != dedList.size()) {
			throw new BusinessException("卸数文件的数量与作业的数量不一致!!!");
		}

		// 删除当前任务的全部作业信息
		Dbo.execute(
				"DELETE FROM "
						+ Etl_job_def.TableName
						+ " WHERE etl_job in (SELECT t2.etl_job from "
						+ Take_relation_etl.TableName
						+ " t1 JOIN "
						+ Etl_job_def.TableName
						+ " t2 ON t1.etl_job = t2.etl_job WHERE t1.etl_sys_cd = t2.etl_sys_cd AND t1.sub_sys_cd = t2.sub_sys_cd AND t1.database_id =  ?)",
				colSetId);

		// 检查作业系统参数的作业程序目录
		EtlJobUtil.setDefaultEtlParaConf(etl_sys_cd, Constant.PARA_HYRENBIN, pro_dic + File.separator);

		// 检查作业系统参数的作业日志是否存在
		EtlJobUtil.setDefaultEtlParaConf(etl_sys_cd, Constant.PARA_HYRENLOG, log_dic);

		// 默认增加一个资源类型,先检查是否存在,如果不存在则添加
		EtlJobUtil.setDefaultEtlResource(etl_sys_cd);

		// 获取作业资源关系信息
		List<String> jobResource = EtlJobUtil.getJobResource(etl_sys_cd);

		// 获取抽数关系依赖信息
		List<Object> relationEtl = getRelationEtl(source_id);

		// 先获取当前工程,任务下的作业名称
		List<String> etlJobList = EtlJobUtil.getEtlJob(etl_sys_cd, sub_sys_cd);

		// 作业定义信息
		int index = 0;
		for (Etl_job_def etl_job_def : etlJobs) {

      /*
       检查必要字段不能为空的情况
      */
			CheckParam.checkData("作业名称不能为空!!!", etl_job_def.getEtl_job());
			if (StringUtil.isBlank(etl_job_def.getEtl_job())) {
				CheckParam.throwErrorMsg("作业名称不能为空!!!");
			}
			if (StringUtil.isBlank(etl_job_def.getEtl_sys_cd())) {
				CheckParam.throwErrorMsg("工程编号不能为空!!!");
			}
			if (StringUtil.isBlank(etl_job_def.getSub_sys_cd())) {
				CheckParam.throwErrorMsg("任务编号不能为空!!!");
			}
			if (StringUtil.isBlank(etl_job_def.getPro_type())) {
				CheckParam.throwErrorMsg("作业程序类型不能为空!!!");
			}

			// 作业的程序路径
			etl_job_def.setPro_dic(pro_dic + File.separator);
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

			// 检查表名是否存在
			if (etlJobList.contains(etl_job_def.getEtl_job())) {
				etl_job_def.update(Dbo.db());
			} else {
				// 新增
				etl_job_def.add(Dbo.db());
			}

			// 解析出作业上游的关系数据
			Map jobRelationMap = null;
			if (StringUtil.isNotBlank(jobRelations)) {
				jobRelationMap =
						JsonUtil.toObjectSafety(jobRelations, Map.class)
								.orElseThrow(() -> new BusinessException("数据转换错误"));
			}
			// 保存每个作业的上游依赖关系
			if (jobRelationMap != null) {
				Object pre_job = jobRelationMap.get(etl_job_def.getEtl_job());
				if (pre_job != null) {
					saveEtlDependencies(etl_sys_cd, etl_job_def.getEtl_job(), pre_job.toString());
				}
			}

      /*
       对每个采集作业定义资源分配 ,检查作业所需资源是否存在,如果存在则跳过
      */
			EtlJobUtil.setEtl_job_resource_rela(etl_sys_cd, etl_job_def, jobResource);

      /*
       保存抽数作业关系表,检查作业名称是否存在,如果存在则更新,反之新增
      */
			setTake_relation_etl(colSetId, etl_job_def, relationEtl, dedList, index);

			index++;
		}

		// 5，这里如果都配置文采则将此次任务的 database_set表中的字段(is_sendok) 更新为是,是表示为当前的配置任务完成
		DboExecute.updatesOrThrow(
				"此次采集任务配置完成,更新状态失败",
				"UPDATE " + Database_set.TableName + " SET is_sendok = ? WHERE database_id = ?",
				IsFlag.Shi.getCode(),
				colSetId);
	}

	@Method(desc = "保存作业所需的资源信息", logicStep = "1: 判断当前的作业信息是否存在,如果不存在则添加")
	@Param(name = "etl_sys_cd", desc = "作业工程编号", range = "不可为空")
	@Param(name = "etl_job_def", desc = "作业资源的信息集合", range = "不可为空", isBean = true)
	@Param(name = "relationEtl", desc = "抽数作业关系表信息集合", range = "可为空")
	private void setTake_relation_etl(
			long colSetId,
			Etl_job_def etl_job_def,
			List<Object> relationEtl,
			List<String> dedList,
			int index) {
		if (!relationEtl.contains(etl_job_def.getEtl_job())) {
			Take_relation_etl take_relation_etl = new Take_relation_etl();
			take_relation_etl.setDed_id(dedList.get(index));
			take_relation_etl.setDatabase_id(colSetId);
			take_relation_etl.setEtl_job(etl_job_def.getEtl_job());
			take_relation_etl.setEtl_sys_cd(etl_job_def.getEtl_sys_cd());
			take_relation_etl.setSub_sys_cd(etl_job_def.getSub_sys_cd());
			take_relation_etl.add(Dbo.db());
		}
	}

	@Method(desc = "获取当前同个数据源分类下表抽数作业关系表", logicStep = "防止数据源下的同个分类出现重复的作业信息")
	@Param(name = "source_id", desc = "任务ID", range = "不可为空")
	@Return(desc = "返回抽数作业关系表下作业名称集合", range = "可以为空.为空表示没有作业存在")
	private List<Object> getRelationEtl(long source_id) {
		return Dbo.queryOneColumnList(
				"SELECT t1.etl_job FROM "
						+ Take_relation_etl.TableName
						+ " t1 JOIN "
						+ Database_set.TableName
						+ " t2 ON t1.database_id = t2.database_id "
						+ " JOIN "
						+ Collect_job_classify.TableName
						+ " t3 ON t2.classify_id = t3.classify_id JOIN "
						+ Agent_info.TableName
						+ " t4 ON "
						+ "t2.agent_id = t4.agent_id  WHERE t4.source_id = ?",
				source_id);
	}

	@Method(
			desc = "保存作业的依赖关系",
			logicStep = "1: 根据工程编号,作业名称删除当前作业和当前作业有关系的依赖作业.不关心删除条数" + "2: 根据新的依赖进行入库操作")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "不可为空")
	@Param(name = "status", desc = "作业有效标识", range = "不可为空")
	@Param(name = "jobRelation", desc = "上游作业名称", range = "不可为空")
	private void saveEtlDependencies(String etl_sys_cd, String etl_job, String jobRelation) {

		// 1: 根据工程编号,作业名称删除当前作业的依赖.不关心删除条数
		Dbo.execute(
				"DELETE FROM "
						+ Etl_dependency.TableName
						+ " WHERE (etl_job = ? OR pre_etl_job = ?) AND etl_sys_cd = ? ",
				etl_job,
				etl_job,
				etl_sys_cd);
		// 2: 根据新的依赖进行入库操作
		if (StringUtil.isNotBlank(jobRelation)) {
			StringUtil.split(jobRelation, "^")
					.forEach(
							item -> {
								Etl_dependency etl_dependency = new Etl_dependency();
								etl_dependency.setEtl_sys_cd(etl_sys_cd);
								etl_dependency.setEtl_job(etl_job);
								etl_dependency.setPre_etl_sys_cd(etl_sys_cd);
								etl_dependency.setPre_etl_job(item);
								etl_dependency.setStatus(Status.TRUE.getCode());
								etl_dependency.add(Dbo.db());
							});
		}
	}

	@Method(desc = "找出数据库和数据字典的差异表信息", logicStep = "获取表名称")
	@Param(name = "dicTableList", desc = "作业调度存在的作业信息", range = "可以为空")
	@Param(name = "databaseTableNames", desc = "数据库存在的作业信息", range = "可以为空")
	@Return(desc = "返回还存在和已删除的表信息", range = "可以为空")
	private Map<String, List<Object>> getDifferenceInfo(
			List<Object> dicTableList, List<Object> databaseTableNames) {

		logger.info("数据字典的 " + dicTableList);
		logger.info("数据库的 " + databaseTableNames);
		List<Object> exists = new ArrayList<Object>(); // 存在的信息
		List<Object> delete = new ArrayList<Object>(); // 不存在的信息
		Map<String, List<Object>> differenceMap = new HashedMap();
		for (Object databaseTableName : databaseTableNames) {
			/*
			 * 如果数据字典中包含数据库中的表,则检查表字段信息是否被更改
			 * 然后将其删除掉进行后面的检查
			 */
			if (dicTableList.contains(databaseTableName)) {
				exists.add(databaseTableName);
				dicTableList.remove(databaseTableName);
			} else {
				delete.add(databaseTableName);
			}
		}

		logger.info("数据字典存在的===>" + exists);
		differenceMap.put("exists", exists);
		logger.info("数据字典删除的===>" + delete);
		differenceMap.put("delete", delete);
		logger.info("数据字典新增的===>" + dicTableList);
		differenceMap.put("add", dicTableList);
		return differenceMap;
	}

}
