package hrds.b.biz.agentdepoly;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.resultset.Result;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.AgentStatus;
import hrds.commons.codes.AgentType;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Agent_down_info;
import hrds.commons.entity.Agent_info;
import hrds.commons.entity.Data_source;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.PropertyParaValue;
import hrds.commons.utils.jsch.AgentDeploy;
import hrds.commons.utils.jsch.ChineseUtil;
import hrds.commons.utils.key.PrimayKeyGener;
import java.io.File;
import java.util.List;
import java.util.Map;

@DocClass(desc = "Agent部署管理", author = "Mr.Lee", createdate = "2019-08-30 10:01")
public class AgentDeployAction extends BaseAction {

  @Method(desc = "获取当前用户的数据源信息", logicStep = "查询当前用户的数据源信息")
  @Return(desc = "返回用户的数据源信息", range = "可以为空,为空表示该用户没有数据源信息")
  public Result getDataSourceInfo() {
    // 1: 查询当前用户的数据源信息
    return Dbo.queryResult(
        "select ds.source_id, ds.datasource_name, "
            + " sum(case ai.agent_type when ? then 1 else 0 end) as dbflag, "
            + " sum(case ai.agent_type when ? then 1 else 0 end) as dfflag, "
            + " sum(case ai.agent_type when ? then 1 else 0 end) as nonstructflag,"
            + " sum(case ai.agent_type when ? then 1 else 0 end) as halfstructflag,"
            + " sum(case ai.agent_type when ? then 1 else 0 end) as ftpflag"
            + " from "
            + Data_source.TableName
            + " ds "
            + " left join "
            + Agent_info.TableName
            + " ai "
            + " on ds.source_id = ai.source_id"
            + " where ai.user_id = ?"
            + " group by ds.source_id order by datasource_name",
        AgentType.ShuJuKu.getCode(),
        AgentType.DBWenJian.getCode(),
        AgentType.WenJianXiTong.getCode(),
        AgentType.DuiXiang.getCode(),
        AgentType.FTP.getCode(),
        getUserId());
  }

  @Method(desc = "获取数据源下对应某种Agent的信息", logicStep = "根据选择的数据源及Agent类型查询其对应的Agent")
  @Param(name = "source_id", desc = "数据源ID", range = "不能为空的整数")
  @Param(name = "agent_type", desc = "Agent类型", range = "不能为空的字符串")
  @Return(desc = "查询的Agent集合数据", range = "可以为空,为空表示为获取到Agent信息")
  public List<Map<String, Object>> getAgentInfo(long source_id, String agent_type) {

    return Dbo.queryList(
        "SELECT *,"
            + " ( CASE WHEN agent_type = ? THEN ? "
            + "   WHEN agent_type = ? THEN ?"
            + "   WHEN agent_type = ? THEN ? "
            + "   WHEN agent_type = ? THEN ? "
            + "   WHEN agent_type = ? THEN ? END) agent_zh_name,"
            + "(CASE WHEN agent_status = ? THEN ? "
            + "  WHEN agent_status = ? THEN ? END) connection_status"
            + " FROM agent_info WHERE source_id = ? AND agent_type = ? AND user_id = ?",
        AgentType.ShuJuKu.getCode(),
        AgentType.ShuJuKu.getValue(),
        AgentType.DBWenJian.getCode(),
        AgentType.DBWenJian.getValue(),
        AgentType.DuiXiang.getCode(),
        AgentType.DuiXiang.getValue(),
        AgentType.WenJianXiTong.getCode(),
        AgentType.WenJianXiTong.getValue(),
        AgentType.FTP.getCode(),
        AgentType.FTP.getValue(),
        AgentStatus.WeiLianJie.getCode(),
        AgentStatus.WeiLianJie.getValue(),
        AgentStatus.YiLianJie.getCode(),
        AgentStatus.YiLianJie.getValue(),
        source_id,
        agent_type,
        getUserId());
  }

  @Method(
      desc = "获取当前部署的Agent信息",
      logicStep = "1 : 根据选择的数据源及Agent类型查询其对应的Agent信息" + "2 : 将系统默认路径放入")
  @Param(name = "agent_id", desc = "数据源ID", range = "不能为空的整数")
  @Param(name = "agent_type", desc = "Agent类型 (AgentType)", range = "不能为空的字符串")
  @Return(desc = "当前Agent的部署信息", range = "可以为空,因为会出现是第一次部署")
  public Map<String, Object> getAgentDownInfo(long agent_id, String agent_type) {

    /* 1 : 根据选择的数据源及Agent类型查询其对应的Agent信息 */
    Map<String, Object> queryOneObject =
        Dbo.queryOneObject(
            "SELECT * FROM "
                + Agent_down_info.TableName
                + " WHERE "
                + "agent_id = ? AND agent_type = ? AND user_id = ?",
            agent_id,
            agent_type,
            getUserId());
    /* 2 : 将系统默认路径放入 */
    queryOneObject.put(
        "agentDeployPath", PropertyParaValue.getString("agentDeployPath", "/home/hyshf/"));
    return queryOneObject;
  }

  @Method(
      desc = "保存此次部署Agent的信息",
      logicStep =
          "1 : 部署路径以页面选择的为基准,页面选择为两种(系统默认-0/自定义-1),如果为系统默认则取系统参数的路径,反之使用自定义路径"
              + "2 : 检查当前部署的信息是否含有Down_id,如果有表示为编辑,否则为新增")
  @Param(name = "agent_down_info", desc = "保存部署Agent信息", range = "不能为空的整数", isBean = true)
  @Param(
      name = "customPath",
      desc = "自定义路径",
      range = "不能为空(0-表示系统默认路径,1相反)",
      valueIfNull = {"0"})
  @Param(name = "oldAgentDir", desc = "Agent旧的部署目录", range = "可以为空,为空表示为第一次部署", nullable = true)
  @Param(name = "oldLogPath", desc = "Agent旧日志信息", range = "可以为空,为空表示为第一次部署", nullable = true)
  public void saveAgentDownInfo(
      Agent_down_info agent_down_info, String customPath, String oldAgentDir, String oldLogPath) {

    /* 1 : 部署路径以页面选择的为基准,页面选择为两种(系统默认-0/自定义-1),如果为系统默认则取系统参数的路径,反之使用自定义路径 */
    if (StringUtil.isNotBlank(customPath)) {
      // 自定义路径设置
      if (IsFlag.Fou.getCode().equals(customPath)) {
        // 这里取得海云用户默认的安装路径
        agent_down_info.setSave_dir(PropertyParaValue.getString("agentDeployPath", "/home/hyshf/"));
        //将Agent的名称转换为 拼音_端口 作为Agent的目录
        String agentDirName =
            ChineseUtil.getPingYin(agent_down_info.getAgent_name())
                + "_"
                + agent_down_info.getAgent_port();
        agent_down_info.setLog_dir(
            PropertyParaValue.getString("agentDeployPath", "/home/hyshf/")
                + File.separator
                + "running"
                + File.separator
                + agentDirName
                + File.separator
                + "running.log");
      }
    }
    // Agent开始部署
    String deployFinalDir = AgentDeploy.agentConfDeploy(agent_down_info, oldAgentDir, oldLogPath);

    agent_down_info.setAi_desc(deployFinalDir);
    /* 2 : 检查当前部署的信息是否含有Down_id,如果有表示为编辑,否则为新增 */
    if (agent_down_info.getDown_id() == null) {
      agent_down_info.setDown_id(PrimayKeyGener.getNextId());
      if (agent_down_info.add(Dbo.db()) != 1) {
        throw new BusinessException("Agent部署信息保存失败");
      }
    } else {
      if (agent_down_info.update(Dbo.db()) != 1) {
        throw new BusinessException("重新部署Agent (" + agent_down_info.getAgent_name() + ") 失败");
      }
    }
  }
}
