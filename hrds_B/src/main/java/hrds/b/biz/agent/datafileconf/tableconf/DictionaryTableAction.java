package hrds.b.biz.agent.datafileconf.tableconf;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.web.util.Dbo;
import hrds.b.biz.agent.datafileconf.CheckParam;
import hrds.b.biz.agent.tools.SendMsgUtil;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Agent_info;
import hrds.commons.entity.Database_set;
import hrds.commons.entity.Table_column;
import hrds.commons.entity.Table_info;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.AgentActionUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@DocClass(desc = "数据文件采集表配置", author = "Mr.Lee", createdate = "2020-04-13 14:41")
public class DictionaryTableAction extends BaseAction {

  // 日志打印
  private static final Log logger = LogFactory.getLog(DictionaryTableAction.class);

  // 数据表的默认有效日期时间
  private static final String VALID_E_DATE = "99991231";

  @Method(
      desc = "获取表信息",
      logicStep =
          ""
              + "1: 检查任务信息是否存在"
              + "2: 获取任务下的表信息"
              + "3: 获取数据字典的表信息"
              + "4: 如果数据库中的表记录是空的,则判断数据字典中是否有表信息,如果有则处理数据字典中的表,并添加一些默认值"
              + "5: 判断数据字典和数据库中的表进行对比,找出被删除的表,新增的表和还存在的表"
              + "6: 将删除的表在数据库表信息集合中剔除掉,然后再将数据字典新增的表和数据库的表进行合并"
              + "   6-1: 在数据库表的集合中剔除被删除的表"
              + "   6-2: 在数据字典中剔除新增意外的表,然后设置默认的字段信息"
              + "   6-3: 将数据字典新增的表和数据库中的表集合进行合并"
              + "7: 返回表的信息")
  @Param(name = "colSetId", desc = "采集任务ID", range = "不可为空")
  @Return(desc = "返回数据字典和数据库的表信息", range = "可以为空,为空表示数据字典及数据库无表记录信息")
  public List<Table_info> getTableData(long colSetId) {

    //    1: 检查任务信息是否存在
    long countNum =
        Dbo.queryNumber("SELECT count(1) FROM database_set WHERE database_id = ?", colSetId)
            .orElseThrow(
                () -> {
                  return new BusinessException("SQL查询异常");
                });

    if (countNum == 0) {
      CheckParam.throwErrorMsg("采集任务( %s ),不存在!!!", colSetId);
    }
    //    2: 获取任务下的表信息
    List<Table_info> databaseTableList =
        Dbo.queryList(
            Table_info.class,
            "SELECT * FROM " + Table_info.TableName + " WHERE database_id = ? AND valid_e_date = ?",
            VALID_E_DATE);

    //    3: 获取数据字典的表信息
    List<Table_info> dirTableList = getDirTableData(colSetId);

    //    4: 如果数据库中的表记录是空的,则判断数据字典中是否有表信息,如果有则处理数据字典中的表,并添加一些默认值
    if (databaseTableList == null || databaseTableList.isEmpty()) {
      // 判断数据字典的表是否存在
      if (dirTableList == null || dirTableList.isEmpty()) {
        CheckParam.throwErrorMsg("数据字典与数据库中的表信息为空");
      }

      //      5: 判断数据字典和数据库中的表进行对比,找出被删除的表和新增的表
      Map<String, List<String>> differenceInfo =
          getDifferenceInfo(getTableName(dirTableList), getTableName(databaseTableList));

      //        6: 将删除的表在数据库表信息集合中剔除掉,然后再将数据字典新增的表和数据库的表进行合并
      //      6-1: 在数据库表的集合中剔除被删除的表
      removeTableBean(differenceInfo.get("delete"), databaseTableList, true);

      //      6-2: 在数据字典中找到新增的表,然后设置默认的字段信息
      removeTableBean(differenceInfo.get("add"), dirTableList, false);
      setDirTableDefaultData(dirTableList, colSetId);

      //      6-3: 将数据字典新增的表和数据库中的表集合进行合并
      dirTableList.addAll(databaseTableList);

      // 7: 返回表的信息
      return dirTableList;
    } else {
      // 判断数据字典的表是否存在
      if (dirTableList == null || dirTableList.isEmpty()) {
        CheckParam.throwErrorMsg("数据字典与数据库中的表信息为空");
      }
      // 设置数据字典表默认数据信息
      setDirTableDefaultData(dirTableList, colSetId);

      // 7: 返回表的信息
      return dirTableList;
    }
  }

  @Method(desc = "根据表ID获取列信息", logicStep = "获取列信息")
  @Param(name = "colSetId", desc = "采集任务ID", range = "不可为空")
  @Param(name = "table_id", desc = "表ID", range = "不可为空")
  @Return(desc = "返回表列数据信息", range = "不可为空,为空表示无列信息,那么在保存列的信息时就未做校验")
  public List<Table_column> getTableColumnByTableId(long colSetId, long table_id) {

    long countNum =
        Dbo.queryNumber(
                "SELECT COUNT(1) FROM " + Table_info.TableName + " WHERE database_id = ?", colSetId)
            .orElseThrow(() -> new BusinessException("SQL查询异常"));
    if (countNum == 0) {
      CheckParam.throwErrorMsg("当前任务( %s )不存在采集表信息!!!");
    }

    return Dbo.queryList(
        Table_column.class,
        "SELECT t1.* from "
            + Table_column.TableName
            + " t1 JOIN "
            + Table_info.TableName
            + " t2 ON t1.table_id = t2.table_id WHERE t2.database_id = ? AND t1.table_id = ?"
            + " AND t2.valid_e_date = ? AND t1.valid_e_date = ? ",
        colSetId,
        table_id,
        VALID_E_DATE,
        VALID_E_DATE);
  }

  @Method(desc = "根据表I名称获取列信息", logicStep = "获取列信息")
  @Param(name = "colSetId", desc = "采集任务ID", range = "不可为空")
  @Param(name = "table_name", desc = "表名称", range = "不可为空")
  @Return(desc = "返回表列数据信息", range = "可以为空,为空表示数据字典为配置列信息")
  public List<Table_column> getTableColumnByTableName(long colSetId, String table_name) {

    Map<String, Object> databaseInfo = getDatabaseSetInfo(colSetId);

    String respMsg =
        SendMsgUtil.getAllTableName(
            (Long) databaseInfo.get("agent_id"),
            this.getUserId(),
            databaseInfo,
            AgentActionUtil.GETTABLECOLUMN);

    return JSON.parseObject(respMsg, new TypeReference<List<Table_column>>() {});
  }

  @Method(desc = "根据colSetId去数据库中查出DB连接信息", logicStep = "1、根据colSetId和userId去数据库中查出DB连接信息")
  @Param(name = "colSetId", desc = "采集任务ID", range = "不为空")
  @Return(desc = "查询结果集", range = "不为空")
  private List<Table_info> getDirTableData(long colSetId) {

    Map<String, Object> databaseInfo = getDatabaseSetInfo(colSetId);

    String respMsg =
        SendMsgUtil.getAllTableName(
            (Long) databaseInfo.get("agent_id"),
            this.getUserId(),
            databaseInfo,
            AgentActionUtil.GETDATABASETABLE);

    return JSON.parseObject(respMsg, new TypeReference<List<Table_info>>() {});
  }

  @Param(name = "dirTableList", desc = "数据字典的表信息集合", range = "不为空", isBean = true)
  @Param(name = "colSetId", desc = "采集任务ID", range = "不为空")
  private void setDirTableDefaultData(List<Table_info> dirTableList, long colSetId) {
    dirTableList.forEach(
        (table_info) -> {
          table_info.setDatabase_id(colSetId);
          table_info.setRec_num_date(DateUtil.getSysDate());
          table_info.setValid_s_date(DateUtil.getSysDate());
          table_info.setIs_md5(IsFlag.Fou.getCode());
          table_info.setIs_register(IsFlag.Shi.getCode());
          table_info.setIs_customize_sql(IsFlag.Fou.getCode());
          table_info.setIs_parallel(IsFlag.Fou.getCode());
          table_info.setIs_user_defined(IsFlag.Fou.getCode());
        });
  }

  @Method(desc = "根据colSetId去数据库中查出DB连接信息", logicStep = "1、根据colSetId和userId去数据库中查出DB连接信息")
  @Param(name = "colSetId", desc = "采集任务ID", range = "不为空")
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

  @Method(desc = "获取集合Bean中的表名称", logicStep = "获取表名称")
  @Param(name = "tableBeanList", desc = "集合Table_info数据集合", range = "可以为空")
  @Return(desc = "返回处理后的数据信息集合,只要表的名称", range = "可以为空")
  private List<String> getTableName(List<Table_info> tableBeanList) {
    List<String> tableNameList = new ArrayList<>();
    tableBeanList.forEach(
        table_info -> {
          tableNameList.add(table_info.getTable_name());
        });
    return tableNameList;
  }

  @Method(desc = "找出数据库和数据字典的差异表信息", logicStep = "获取表名称")
  @Param(name = "dicTableList", desc = "数据字典表集合信息", range = "可以为空")
  @Param(name = "databaseTableNames", desc = "数据库表集合信息", range = "可以为空")
  @Return(desc = "返回还存在和已删除的表信息", range = "可以为空")
  private Map<String, List<String>> getDifferenceInfo(
      List<String> dicTableList, List<String> databaseTableNames) {

    logger.info("数据字典的 " + dicTableList);
    logger.info("数据库的 " + databaseTableNames);
    List<String> exists = new ArrayList<String>(); // 存在的信息
    List<String> delete = new ArrayList<String>(); // 不存在的信息
    Map<String, List<String>> differenceMap = new HashedMap();
    for (String databaseTableName : databaseTableNames) {
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

  @Method(desc = "根据不同情况删除表的信息", logicStep = "删除表信息")
  @Param(name = "tableNameList", desc = "被依据的表名称集合", range = "可以为空")
  @Param(name = "tableList", desc = "需要处理的表集合", range = "可以为空")
  @Param(name = "deleteType", desc = "区分是数据库的表集合处理还是数据字典的表集合处理", range = "可以为空")
  private void removeTableBean(
      List<String> tableNameList, List<Table_info> tableList, boolean deleteType) {
    tableList.forEach(
        table_info -> {
          if (deleteType) {
            // 如果删除的表名称和数据库中记录的表名一样,则删除(数据库的)
            if (tableNameList.contains(table_info.getTable_name())) {
              tableList.remove(table_info);
            }
          } else {
            // 删除还存在的表信息,保留新增的表信息(数据字典的)
            if (!tableNameList.contains(table_info.getTable_name())) {
              tableList.remove(table_info);
            }
          }
        });
  }
}
