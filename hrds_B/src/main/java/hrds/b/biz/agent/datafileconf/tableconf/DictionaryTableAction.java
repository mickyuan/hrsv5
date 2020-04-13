package hrds.b.biz.agent.datafileconf.tableconf;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.web.util.Dbo;
import hrds.b.biz.agent.datafileconf.CheckParam;
import hrds.b.biz.agent.tools.SendMsgUtil;
import hrds.commons.base.BaseAction;
import hrds.commons.entity.Agent_info;
import hrds.commons.entity.Database_set;
import hrds.commons.entity.Table_info;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.AgentActionUtil;
import java.util.List;
import java.util.Map;

@DocClass(desc = "数据文件采集数据字典的表配置", author = "Mr.Lee", createdate = "2020-04-13 14:41")
public class DictionaryTableAction extends BaseAction {

  @Method(desc = "获取表信息", logicStep = "根据任务获取该任务下的表及数据字典中的表")
  @Param(name = "colSetId", desc = "采集任务ID", range = "不可为空")
  @Return(desc = "返回数据字典和数据库的表信息", range = "可以为空,为空表示数据字典及数据库无表记录信息")
  public void dictionTableData(long colSetId) {
    long countNum =
        Dbo.queryNumber(
                "SELECT count(1) FROM " + Database_set.TableName + " WHERE database_id = ?",
                colSetId)
            .orElseThrow(() -> new BusinessException("SQL查询异常"));
    if (countNum == 0) {
      CheckParam.throwErrorMsg("采集任务( %s ),不存在!!!", colSetId);
    }
    // 获取数据库已存在的表
    List<Map<String, Object>> list =
        Dbo.queryList(
            Table_info.TableName,
            "SELECT * FROM " + Table_info.TableName + " WHERE database_id = ?",
            colSetId);
    // 获取数据字典目录下的数据表
    // 1、根据colSetId去数据库中获取数据库设置相关信息
    Map<String, Object> databaseInfo = getDatabaseSetInfo(colSetId);
    if (databaseInfo.size() > 0) {
      throw new BusinessException("未找到数据库采集任务");
    }
    // 2、和Agent端进行交互，得到Agent返回的表名数据
    String respMsg =
        SendMsgUtil.getAllTableName(
            ((long) databaseInfo.get("agent_id")),
            getUserId(),
            databaseInfo,
            AgentActionUtil.GETDATABASETABLE);
    // 3、对获取到的数据进行处理，根据表名和colSetId获取界面需要显示的信息并返回
    List<Table_info> tableNames =
        JSON.parseObject(respMsg, new TypeReference<List<Table_info>>() {});

    // 找出已在数据字典中存在表

  }

  @Method(desc = "根据colSetId去数据库中查出DB连接信息", logicStep = "1、根据colSetId和userId去数据库中查出DB连接信息")
  @Param(name = "colSetId", desc = "数据库设置ID，源系统数据库设置表主键，数据库对应表外键", range = "不为空")
  @Param(name = "userId", desc = "当前登录用户ID，sys_user表主键", range = "不为空")
  @Return(desc = "查询结果集", range = "不为空")
  private Map<String, Object> getDatabaseSetInfo(long colSetId) {

    long databaseNum =
        Dbo.queryNumber(
                "SELECT COUNT(1) FROM " + Database_set.TableName + " WHERE database_id = ?",
                colSetId)
            .orElseThrow(() -> new BusinessException("SQL查询异常"));
    if (databaseNum == 0) {
      throw new BusinessException("任务(" + colSetId + ")不存在!!!");
    }
    // 1、根据colSetId和userId去数据库中查出DB连接信息
    return Dbo.queryOneObject(
        " select t1.database_type, t1.database_ip, t1.database_port, t1.database_name, "
            + " t1.database_pad, t1.user_name, t1.database_drive, t1.jdbc_url, t1.agent_id, t1.db_agent, t1.plane_url"
            + " from "
            + Database_set.TableName
            + " t1"
            + " left join "
            + Agent_info.TableName
            + " ai on ai.agent_id = t1.agent_id"
            + " where t1.database_id = ? and ai.user_id = ? ",
        colSetId,
        getUserId());
  }
}
