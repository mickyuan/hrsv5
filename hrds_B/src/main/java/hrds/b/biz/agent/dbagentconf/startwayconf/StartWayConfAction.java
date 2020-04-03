package hrds.b.biz.agent.dbagentconf.startwayconf;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.FileFormat;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.ParamType;
import hrds.commons.codes.Pro_Type;
import hrds.commons.entity.Agent_down_info;
import hrds.commons.entity.Agent_info;
import hrds.commons.entity.Collect_job_classify;
import hrds.commons.entity.Data_extraction_def;
import hrds.commons.entity.Data_source;
import hrds.commons.entity.Database_set;
import hrds.commons.entity.Etl_job_def;
import hrds.commons.entity.Etl_job_resource_rela;
import hrds.commons.entity.Etl_para;
import hrds.commons.entity.Etl_resource;
import hrds.commons.entity.Etl_sub_sys_list;
import hrds.commons.entity.Etl_sys;
import hrds.commons.entity.Table_info;
import hrds.commons.entity.Take_relation_etl;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.PropertyParaValue;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "定义启动方式配置", author = "Lee-Qiang")
public class StartWayConfAction extends BaseAction {

  // 默认前一天跑批日期
  private static final String BATCH_DATE = "#{txdate_pre}";

  // 默认增加一个资源类型
  private static final String RESOURCE_THRESHOLD = "XS_ZT";
  // 资源类默认的阈值
  private static final int RESOURCE_NUM = 15;
  // 单个作业的所需资源数
  private static final int JOB_RESOURCE_NUM = 1;
  // 程序目录的工程系统参数名
  private static final String HYRENBIN = "!{HYSHELLBIN}";
  // 程序日志的工程系统参数名
  private static final String HYRENLOG = "!{HYLOG}";

  @Method(desc = "获取工程信息", logicStep = "获取作业调度工程信息,然后返回到前端")
  @Return(desc = "返回工程信息集合", range = "为空表示没有工程信息")
  public List<Etl_sys> getEtlSysData() {
    // 获取作业调度工程信息,然后返回到前端
    return Dbo.queryList(Etl_sys.class, "SELECT * FROM " + Etl_sys.TableName);
  }

  @Method(desc = "根据工程编号获取任务列表", logicStep = "1 : 判断工程编号是否存在, 2 : 根据工程编号返回任务信息")
  @Param(name = "etl_sys_cd", range = "不可为空", desc = "选择的工程编号")
  @Return(desc = "返回工程下的任务信息", range = "可以为空,如果为空表示当前工程下没有任务信息存在")
  public List<Etl_sub_sys_list> getEtlSubSysData(String etl_sys_cd) {

    //    1 : 判断工程编号是否存在
    long countNum =
        Dbo.queryNumber(
                "SELECT COUNT(1) FROM " + Etl_sys.TableName + " WHERE etl_sys_cd = ?", etl_sys_cd)
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
  public List<Map<String, String>> getPreviewJob(long colSetId) {

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
    //    4: 查询任务下的表信息 FIXME 这里后续判断都是什么采集类型来获取不同的数据(目前只做了数据库采集)
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
    // 作业名称/描述之间的分割符
    String splitter = "_";
    // 存放组合后的作业信息
    List<Map<String, String>> assemblyList = new ArrayList<>();
    tableList.forEach(
        itemMap -> {
          Map<String, String> assemblyMap = new LinkedHashMap<>();

          // 作业采集文件类型
          String dbfile_format = FileFormat.ofValueByCode(((String) itemMap.get("dbfile_format")));
          // 作业名称
          String pro_name =
              databaseMap.get("datasource_number")
                  + splitter
                  + databaseMap.get("agent_id")
                  + splitter
                  + databaseMap.get("classify_num")
                  + splitter
                  + itemMap.get("table_name")
                  + splitter
                  + dbfile_format;
          assemblyMap.put("etl_job", pro_name);
          // 作业描述
          String etl_job_desc =
              databaseMap.get("datasource_name")
                  + splitter
                  + databaseMap.get("agent_name")
                  + splitter
                  + databaseMap.get("classify_name")
                  + splitter
                  + itemMap.get("table_ch_name")
                  + splitter
                  + dbfile_format;
          assemblyMap.put("etl_job_desc", etl_job_desc);
          // 作业参数
          String pro_para =
              colSetId
                  + "@"
                  + itemMap.get("table_name")
                  + "@"
                  + itemMap.get("agent_type")
                  + "@"
                  + BATCH_DATE;
          assemblyMap.put("pro_para", pro_para);

          assemblyList.add(assemblyMap);
        });

    return assemblyList;
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

  @Method(desc = "获取编辑时任务下的作业信息", logicStep = "获取任务信息")
  @Param(name = "colSetId", desc = "任务ID", range = "不可为空")
  @Return(desc = "返回作业信息", range = "可以为空..为空表示没有设置作业信息")
  public List<Map<String, Object>> getEtlJobData(long colSetId) {
    return Dbo.queryList(
        "SELECT t1.database_id,t1.ded_id,t2.* from "
            + Take_relation_etl.TableName
            + " t1 JOIN "
            + Etl_job_def.TableName
            + " t2 ON "
            + "t1.etl_job = t2.etl_job WHERE t1.etl_sys_cd = t2.etl_sys_cd AND t1.sub_sys_cd = t2.etl_sys_cd "
            + "AND t1.database_id = ? ",
        colSetId);
  }

  @Method(
      desc = "获取任务Agent的部署路径及日志目录",
      logicStep =
          ""
              + "1: 检查当前任务是否存在; "
              + "2: 获取任务部署的Agent路径及日志地址,并将程序类型,名称的默认值返回 "
              + "3: 获取任务存在着抽取作业关系"
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
            "SELECT save_dir pro_dic,log_dir log_dic FROM "
                + Database_set.TableName
                + " t1 JOIN "
                + Agent_down_info.TableName
                + " t2 ON "
                + "t1.agent_id = t2.agent_id WHERE t1.database_id = ?",
            colSetId);
    map.put("pro_type", Pro_Type.JAVA.getCode());
    map.put("pro_name", PropertyParaValue.getString("agentpath", ""));
    /*
       3: 获取任务存在着抽取作业关系
    */
    //    countNum =
    //        Dbo.queryNumber(
    //                "SELECT COUNT(1) FROM " + Take_relation_etl.TableName + " WHERE database_id =
    // ?",
    //                colSetId)
    //            .orElseThrow(() -> new BusinessException("SQL查询错误"));
    //    if (countNum != 0) {
    // 如果存在就获取一条信息就可以... 因为同个任务的作业工程编号,任务编号是一个
    map.putAll(
        Dbo.queryOneObject(
            "SELECT * FROM " + Take_relation_etl.TableName + " WHERE database_id = ? LIMIT 1",
            colSetId));
    //    }
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
              + "4: 将作业的信息存入数据库中")
  @Param(name = "colSetId", desc = "任务的ID", range = "不可为空的整数")
  @Param(name = "etl_sys_cd", desc = "作业工程编号", range = "不可为空")
  @Param(name = "sub_sys_cd", desc = "作业任务编号", range = "不可为空")
  @Param(name = "pro_dic", desc = "agent部署目录", range = "不可为空")
  @Param(name = "log_dic", desc = "agent日志路径", range = "不可为空")
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
  @Param(name = "ded_arr", desc = "卸数文件的ID", range = "不可为空的数组")
  public void saveJobDataToDatabase(
      long colSetId,
      String etl_sys_cd,
      String sub_sys_cd,
      String pro_dic,
      String log_dic,
      Etl_job_def[] etlJobs,
      String[] ded_arr) {

    if (etlJobs.length != ded_arr.length) {
      throw new BusinessException("卸数文件的数量与作业的数量不一致!!!");
    }

    // 检查作业系统参数的作业程序目录
    setDefaultEtlConf(etl_sys_cd, HYRENBIN, pro_dic);

    // 检查作业系统参数的作业日志是否存在
    setDefaultEtlConf(etl_sys_cd, HYRENLOG, log_dic);

    // FIXME 作业依赖待定

    // 默认增加一个资源类型,先检查是否存在,如果不存在则添加
    setDefaultEtlResource(etl_sys_cd);

    // 获取作业资源关系信息
    List<Object> jobResource = getJobResource(etl_sys_cd);

    // 获取抽数关系依赖信息
    List<Object> relationEtl = getRelationEtl(colSetId);

    // 先获取当前工程,任务下的作业名称
    List<Object> etlJobList = getEtlJob(etl_sys_cd, sub_sys_cd);

    // 作业定义信息
    int index = 0;
    for (Etl_job_def etl_job_def : etlJobs) {

      // 检查表名是否存在
      if (etlJobList.contains(etl_job_def.getEtl_job())) {
        throw new BusinessException("作业名称(" + etl_job_def.getEtl_job() + ")已存在");
      }

      /*
       检查必要字段不能为空的情况
      */
      if (StringUtil.isBlank(etl_job_def.getEtl_job())) {
        throw new BusinessException("作业名称不能为空!!!");
      }
      if (StringUtil.isBlank(etl_job_def.getEtl_sys_cd())) {
        throw new BusinessException("工程编号不能为空!!!");
      }
      if (StringUtil.isBlank(etl_job_def.getSub_sys_cd())) {
        throw new BusinessException("任务编号不能为空!!!");
      }
      if (StringUtil.isBlank(etl_job_def.getPro_type())) {
        throw new BusinessException("作业程序类型不能为空!!!");
      }

      // 作业的程序路径
      etl_job_def.setPro_dic(HYRENBIN);
      // 作业的日志程序路径
      etl_job_def.setLog_dic(HYRENLOG);
      // 默认作业都是有效的
      etl_job_def.setJob_eff_flag(IsFlag.Shi.getCode());
      // 默认当天调度作业信息
      etl_job_def.setToday_disp(IsFlag.Shi.getCode());
      // 作业的更新信息时间
      etl_job_def.setUpd_time(
          DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate())
              + " "
              + DateUtil.parseStr2TimeWith6Char(DateUtil.getSysTime()));
      etl_job_def.add(Dbo.db());

      /*
       对每个采集作业定义资源分配 ,检查作业所需资源是否存在,如果存在则跳过
      */
      if (!jobResource.contains(etl_job_def.getEtl_job())) {
        Etl_job_resource_rela etl_job_resource_rela = new Etl_job_resource_rela();
        etl_job_resource_rela.setEtl_sys_cd(etl_sys_cd);
        etl_job_resource_rela.setEtl_job(etl_job_def.getEtl_job());
        etl_job_resource_rela.setResource_type(RESOURCE_THRESHOLD);
        etl_job_resource_rela.setResource_req(JOB_RESOURCE_NUM);
        etl_job_resource_rela.add(Dbo.db());
      }

      /*
       保存抽数作业关系表,检查作业名称是否存在,如果存在则跳过
      */
      if (!relationEtl.contains(etl_job_def.getEtl_job())) {

        Take_relation_etl take_relation_etl = new Take_relation_etl();
        take_relation_etl.setDatabase_id(colSetId);
        take_relation_etl.setDed_id(ded_arr[index]);
        take_relation_etl.setEtl_job(etl_job_def.getEtl_job());
        take_relation_etl.setEtl_sys_cd(etl_job_def.getEtl_sys_cd());
        take_relation_etl.setSub_sys_cd(etl_job_def.getSub_sys_cd());
        take_relation_etl.add(Dbo.db());
      }

      index++;
    }
  }

  @Method(desc = "对程序作业的作业系统参数经行检查添加", logicStep = "1: 检查当前的作业系统参数是否存在 2: 如果不存在则添加")
  @Param(name = "etl_sys_cd", desc = "工程编号", range = "不能为空")
  @Param(name = "para_cd", desc = "工程系统参数变量名称", range = "不能为空")
  @Param(name = "pro_val", desc = "工程系统参数变量值", range = "不能为空")
  private void setDefaultEtlConf(String etl_sys_cd, String para_cd, String pro_val) {
    //    1: 检查当前的作业系统参数是否存在
    long resourceNum =
        Dbo.queryNumber(
                "SELECT COUNT(1) FROM "
                    + Etl_para.TableName
                    + " WHERE etl_sys_cd = ? AND para_cd = ? AND para_val = ? AND para_type = ?",
                etl_sys_cd,
                para_cd,
                pro_val,
                ParamType.LuJing.getCode())
            .orElseThrow(() -> new BusinessException("SQL查询错误"));
    //    2: 如果不存在则添加
    if (resourceNum == 0) {
      Etl_para etl_para = new Etl_para();
      etl_para.setEtl_sys_cd(etl_sys_cd);
      etl_para.setPara_cd(para_cd);
      etl_para.setPara_val(pro_val);
      etl_para.setPara_type(ParamType.LuJing.getCode());
      etl_para.add(Dbo.db());
    }
  }

  @Method(desc = "设置资源登记信息", logicStep = "")
  @Param(name = "etl_sys_cd", desc = "工程编号", range = "不可为空")
  private void setDefaultEtlResource(String etl_sys_cd) {
    long resourceNum =
        Dbo.queryNumber(
                "SELECT COUNT(1) FROM " + Etl_resource.TableName + " WHERE resource_type = ?",
                RESOURCE_THRESHOLD)
            .orElseThrow(() -> new BusinessException("SQL查询错误"));
    if (resourceNum == 0) {
      Etl_resource etl_resource = new Etl_resource();
      etl_resource.setEtl_sys_cd(etl_sys_cd);
      etl_resource.setResource_type(RESOURCE_THRESHOLD);
      etl_resource.setResource_max(RESOURCE_NUM);
      etl_resource.add(Dbo.db());
    }
  }

  @Method(desc = "获取作业信息", logicStep = "")
  @Param(name = "etl_sys_cd", desc = "工程编号", range = "不可为空")
  @Param(name = "sub_sys_cd", desc = "任务编号", range = "不可为空")
  @Return(desc = "返回作业定义下的作业名称集合", range = "可以为空.为空表示没有作业信息存在")
  private List<Object> getEtlJob(String etl_sys_cd, String sub_sys_cd) {
    return Dbo.queryOneColumnList(
        "SELECT etl_job FROM " + Etl_job_def.TableName + " WHERE etl_sys_cd = ? AND sub_sys_cd = ?",
        etl_sys_cd,
        sub_sys_cd);
  }

  @Method(desc = "获取作业资源分配的作业信息", logicStep = "")
  @Param(name = "etl_sys_cd", desc = "工程编号", range = "不可为空")
  @Return(desc = "返回作业资源下的作业名称集合", range = "可以为空.为空表示没有作业资源信息存在")
  private List<Object> getJobResource(String etl_sys_cd) {

    return Dbo.queryOneColumnList(
        "SELECT etl_job FROM " + Etl_job_resource_rela.TableName + " WHERE etl_sys_cd = ? ");
  }

  @Method(desc = "获取当前任务下表抽数作业关系表", logicStep = "")
  @Param(name = "colSetId", desc = "任务ID", range = "不可为空")
  @Return(desc = "返回抽数作业关系表下作业名称集合", range = "可以为空.为空表示没有作业存在")
  private List<Object> getRelationEtl(long colSetId) {
    return Dbo.queryOneColumnList(
        "SELECT etl_job FROM " + Take_relation_etl.TableName + " WHERE database_id = ?", colSetId);
  }
}
