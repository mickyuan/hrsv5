package hrds.b.biz.collectmonitor;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.FileUtil;
import fd.ng.db.resultset.Result;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.AgentType;
import hrds.commons.codes.CollectType;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Agent_info;
import hrds.commons.entity.Collect_case;
import hrds.commons.entity.Source_file_attribute;
import hrds.commons.exception.BusinessException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "采集首页的监控信息", author = "Mr.Lee", createdate = "2019-09-04 12:09")
public class CollectMonitorAction extends BaseAction {

  @Method(
      desc = "查询Agent,数据源配置数量(数据源存在着Agent的时候才会显示具体数量)信息",
      logicStep = "查询Agent,数据源配置数量(数据源存在着Agent的时候才会显示具体数量)信息")
  @Return(desc = "查询的Agent集合数据", range = "可以为空,为空表示为获取到Agent信息")
  public Map<String, Object> getAgentNumAndSourceNum() {

    return Dbo.queryOneObject(
        "SELECT COUNT(agent_id) agentNum,COUNT(DISTINCT(source_id)) sourceNum FROM "
            + Agent_info.TableName
            + " WHERE user_id = ?",
        getUserId());
  }

  @Method(desc = "获取当前用户采集的任务信息", logicStep = "获取当前用户采集的任务信息")
  @Return(desc = "采集任务信息", range = "不能为空")
  public List<Map<String, Object>> getDatabaseSet() {

    return Dbo.queryList(
        "select task_name taskname ,database_id taskid,task.Agent_id,agent_type from database_set task "
            + "join agent_info ai on task.Agent_id = ai.agent_id where user_id = ? and "
            + "task.is_sendok = ? and agent_type in (?,?) order by taskid desc ",
        getUserId(),
        IsFlag.Shi.getCode(),
        AgentType.ShuJuKu.getCode(),
        AgentType.DBWenJian.getCode());
  }

  @Method(
      desc = "获取数据采集信息总况",
      logicStep =
          "1 : 获取已采集的文件及DB文件和数据库采集大小,这里显示的bytes可能需要进行转换单位"
              + "2 : 获取当前用户的全部采集任务数(只要是整个流程配置完成的)"
              + "3 : 将数据信息合并,并返回")
  @Return(desc = "数据采集信息总况", range = "可以为空")
  public Result getDataCollectInfo() {

    // 1 : 获取已采集的文件及DB文件和数据库采集大小,这里显示的bytes可能需要进行转换单位
    Result result =
        Dbo.queryResult(
            " select sum((case when collect_type = ? then file_size else 0 end)) fileSize,"
                + "sum((case when collect_type in (?,?) then file_size else 0 end)) dbSize FROM "
                + Source_file_attribute.TableName
                + " sfa join  agent_info ai on sfa.agent_id = ai.agent_id where user_id = ?",
            CollectType.WenJianCaiJi.getCode(),
            CollectType.ShuJuKuCaiJi.getCode(),
            CollectType.DBWenJianCaiJi.getCode(),
            getUserId());
    result.setObject(
        0, "filesize", FileUtil.fileSizeConversion(result.getLongDefaultZero(0, "filesize")));
    result.setObject(
        0, "dbsize", FileUtil.fileSizeConversion(result.getLongDefaultZero(0, "dbsize")));

    // 2 : 获取当前用户的全部采集任务数(只要是整个流程配置完成的)
    long taskNum =
        Dbo.queryNumber(
                "SELECT COUNT( 1 ) taskNum FROM ( SELECT database_id id, agent_id, is_sendok FROM database_set "
                    + "UNION ALL SELECT fcs_id id, agent_id, is_sendok FROM file_collect_set ) A"
                    + " WHERE EXISTS ( SELECT 1 FROM agent_info ai WHERE ai.user_id = ? "
                    + "AND ai.agent_id = A.Agent_id ) AND is_sendok = ?",
                getUserId(),
                IsFlag.Shi.getCode())
            .orElseThrow(() -> new BusinessException("未获取到采集任务数量"));
    // 3 : 将数据信息合并,并返回
    result.setObject(0, "taskNum", taskNum);
    return result;
  }

  @Method(desc = "获取当前任务的采集作业信息", logicStep = "1 : 获取相应的采集情况" + "2 : 将作业的数据信息处理为有错误的优先显示在前面")
  @Param(name = "database_id", desc = "任务ID", range = "不能为空,否则将无法查询表信息")
  @Return(desc = "返回采集任务表信息", range = "可以为空, 表示未有采集表信息")
  public List<Map<String, String>> getCurrentTaskJob(long database_id) {

    // 1: 获取相应的采集情况
    List<Collect_case> collectJobList =
        Dbo.queryList(
            Collect_case.class,
            "SELECT table_name,collect_type,job_type,execute_state,collect_s_date,collect_s_time,"
                + "collect_e_date,collect_e_time,cc_remark FROM collect_case WHERE collect_set_id = ?"
                + " AND collect_s_date = (select max(collect_s_date) from collect_case where "
                + "collect_set_id = ? ) ORDER BY table_name",
            database_id,
            database_id);
    // 2 : 将作业的数据信息处理为有错误的优先显示在前面
    List<Map<String, String>> tableDetails = JobTableDetails.getTableDetails(collectJobList);

    return tableDetails;
  }

  @Method(
      desc = "获取采集作业最近15天信息",
      logicStep =
          "1: 查询历史任务采集的数据量(文件采集量,数据采集量,其中数据采集量包含:数据库采集,数据文件采集),倒序查询15条数据"
              + "2: 查询到结果,将日期,数据采集量,文件采集量经行处理,否则直接返回"
              + "3: 将处理后的数据信息返回")
  @Return(desc = "返回历史采集处理后的数据", range = "可以为空,为空表示没有历史采集数据信息")
  public List<Map<String, String>> getHoStoryCollect() {

    // 1: 查询历史任务采集的数据量(文件采集量,数据采集量,其中数据采集量包含:数据库采集,数据文件采集),倒序查询15条数据
    Result result =
        Dbo.queryResult(
            "SELECT * FROM (SELECT SUM(CAST(cf.collet_database_size AS NUMERIC)) dbsize,cf.etl_date AS dbdate FROM "
                + Agent_info.TableName
                + " ai JOIN "
                + Collect_case.TableName
                + " cf ON cf.agent_id = ai.agent_id WHERE "
                + " ai.user_id = ? AND cf.collect_type IN ( ?, ? ) GROUP BY cf.etl_date ORDER BY cf.etl_date DESC) aa "
                + "FULL JOIN (SELECT SUM( file_size ) filesize,cc.etl_date AS filedate FROM ( SELECT * FROM collect_case "
                + "WHERE collect_type = ? ) cc JOIN ( SELECT * FROM "
                + Source_file_attribute.TableName
                + " WHERE "
                + "collect_type = ? ) sfa ON cc.agent_id = sfa.agent_id AND cc.collect_set_id = sfa.collect_set_id JOIN "
                + Agent_info.TableName
                + " ai ON cc.agent_id = ai.agent_id WHERE ai.user_id = ? GROUP BY"
                + " cc.etl_date ORDER BY cc.etl_date DESC) bb ON aa.dbdate = bb.filedate LIMIT 15",
            getUserId(),
            CollectType.DBWenJianCaiJi.getCode(),
            CollectType.ShuJuKuCaiJi.getCode(),
            CollectType.WenJianCaiJi.getCode(),
            CollectType.WenJianCaiJi.getCode(),
            getUserId());

    // 2: 查询到结果,将日期,数据采集量,文件采集量经行处理,否则直接返回
    if (result.isEmpty()) {
      return null;
    }
    // 返回的数据集合Map
    List<Map<String, String>> resultMap = new ArrayList<Map<String, String>>();
    Map<String, String> itemMap = null;
    for (int i = 0; i < result.getRowCount(); i++) {
      itemMap = new LinkedHashMap<>();
      // 获取日期数据并处理为带有分割符的日期数据
      itemMap.put(
          "date", DateUtil.parseStr2DateWith8Char(result.getString(i, "dbdate")).toString());
      // 获取数据采集数据,并进行单位统一转换为 MB,并保留2位小数
      itemMap.put("data", formatFileSizeMB(result.getLongDefaultZero(i, "dbsize")));
      // 获取文件采集数据,并进行单位统一转换为 MB,并保留2位小数
      itemMap.put("file", formatFileSizeMB(result.getLongDefaultZero(i, "filesize")));

      resultMap.add(itemMap);
    }
    // 3: 将处理后的数据信息返回
    return resultMap;
  }

  @Method(desc = "将bytes没转换为MB", logicStep = "1: 先算出MB的值. 2: 然后转换为保留2位数,并返回")
  @Param(name = "size", desc = "文件的bytes", range = "不为空")
  @Return(desc = "返回转换后的大小(MB)", range = "不为空")
  private String formatFileSizeMB(long size) {
    // 1: 先算出MB的值
    double f = (double) size / (1024 * 1024);
    // 2: 然后转换为保留2位数,并返回
    return String.format("%.2f", f);
  }
}
