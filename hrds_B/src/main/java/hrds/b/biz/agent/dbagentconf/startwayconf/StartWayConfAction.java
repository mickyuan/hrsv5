package hrds.b.biz.agent.dbagentconf.startwayconf;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.FileFormat;
import hrds.commons.codes.ParamType;
import hrds.commons.entity.Agent_down_info;
import hrds.commons.entity.Agent_info;
import hrds.commons.entity.Collect_job_classify;
import hrds.commons.entity.Data_extraction_def;
import hrds.commons.entity.Data_source;
import hrds.commons.entity.Database_set;
import hrds.commons.entity.Etl_job_def;
import hrds.commons.entity.Etl_para;
import hrds.commons.entity.Etl_sub_sys_list;
import hrds.commons.entity.Etl_sys;
import hrds.commons.entity.Table_info;
import hrds.commons.exception.BusinessException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "定义启动方式配置", author = "Lee-Qiang")
public class StartWayConfAction extends BaseAction {

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
  // FIXME 这里的作业参数需要咨询
  public List<Map<String, String>> getPreviewJob(long colSetId) {

    // 1: 检查该任务是否存在, 2: 查询任务的配置信息
    Map<String, Object> databaseMap = getDatabaseData(colSetId);

    // 3: 检查任务下是否存在表的信息
    long countNum =
        Dbo.queryNumber(
                "SELECT COUNT(1) FROM " + Table_info.TableName + " WHERE database_id = ?", colSetId)
            .orElseThrow(() -> new BusinessException("SQL查询错误"));
    if (countNum != 1) {
      throw new BusinessException("当前任务(" + colSetId + ")下不存在表信息");
    }
    //    4: 查询任务下的表信息
    List<Map<String, Object>> tableList =
        Dbo.queryList(
            "select t1.table_id,t1.table_name,t1.table_ch_name,t2.dbfile_format from "
                + Table_info.TableName
                + " t1 left join "
                + Data_extraction_def.TableName
                + " t2 on t1.table_id = t2.table_id where database_id = ? ORDER BY t1.table_name",
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
    List<String> tableIdList = new ArrayList<>();
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
          assemblyMap.put("pro_name", pro_name);
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
                  + itemMap.get("dbfile_format")
                  + "@"
                  + DateUtil.getSysDate();
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

    if (countNum != 1) {
      throw new BusinessException("当前任务(" + colSetId + ")不再存在");
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

  @Method(
      desc = "获取任务Agent的部署路径及日志目录",
      logicStep = "" + "1: 检查当前任务是否存在; 2: 回去任务部署的Agent路径及日志地址,并返回")
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
    return Dbo.queryOneObject(
        "SELECT save_dir pro_dic,log_dir log_dic FROM "
            + Database_set.TableName
            + " t1 JOIN "
            + Agent_down_info.TableName
            + " t2 ON "
            + "t1.agent_id = t2.agent_id WHERE t1.database_id = ?",
        colSetId);
  }

  @Method(
      desc = "保存启动配置信息",
      logicStep = "" + "1: 获取任务配置信息" + "2: 获取表名称" + "3: 放入作业需要数据信息" + "4: 将作业的信息存入数据库中")
  @Param(name = "colSetId", desc = "任务的ID", range = "不可为空的整数")
  @Param(name = "etl_sys_cd", desc = "作业工程编号", range = "不可为空")
  @Param(name = "pro_dic", desc = "agent部署目录", range = "不可为空")
  @Param(name = "log_dic", desc = "agent日志路径", range = "不可为空")
  @Param(
      name = "etlJobs",
      desc =
          "作业 Etl_job_def 数组字符串,每个对象的应该都应该包含所有的实体信息如:"
              + "{作业名(etl_job),工程代码(etl_sys_cd),子系统代码(sub_sys_cd),作业描述(etl_job_desc),"
              + "作业程序类型(pro_type,使用代码项Pro_Type),}",
      range = "可以为空,如果需要配置启动方式不配置表信息有啥意义",
      isBean = true)
  public void saveJobDataToDatabase(
      long colSetId, String etl_sys_cd, String pro_dic, String log_dic, Etl_job_def[] etlJobs) {

    Map<String, Object> databaseData = getDatabaseData(colSetId);
    String para_cd = "!{HYSHELLBIN}";
    // 作业系统参数
    Etl_para etl_para = new Etl_para();
    etl_para.setEtl_sys_cd(etl_sys_cd);
    etl_para.setPara_cd(para_cd);
    etl_para.setPara_cd(pro_dic);
    etl_para.setPara_type(ParamType.LuJing.getCode());
    etl_para.add(Dbo.db());

    String para_log = "!{HYLOG}";
    etl_para.setEtl_sys_cd(etl_sys_cd);
    etl_para.setPara_cd(para_log);
    etl_para.setPara_cd(log_dic);
    etl_para.setPara_type(ParamType.LuJing.getCode());
    etl_para.add(Dbo.db());

    // FIXME 作业依赖待定

    // 作业定义信息
    for (Etl_job_def etl_job_def : etlJobs) {
      etl_job_def.setPro_dic(para_cd);
      etl_job_def.setLog_dic(para_log);
      etl_job_def.add(Dbo.db());
    }
  }
}
