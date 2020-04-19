package hrds.b.biz.agent.datafileconf.datatransfer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import fd.ng.web.util.Dbo;
import hrds.b.biz.agent.datafileconf.CheckParam;
import hrds.b.biz.agent.tools.SendMsgUtil;
import hrds.commons.base.BaseAction;
import hrds.commons.entity.Agent_info;
import hrds.commons.entity.Data_extraction_def;
import hrds.commons.entity.Database_set;
import hrds.commons.entity.Table_info;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.AgentActionUtil;
import hrds.commons.utils.key.PrimayKeyGener;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@DocClass(desc = "数据转存配置管理", author = "Mr.Lee", createdate = "2020-04-19 14:15")
public class DataTransferAction extends BaseAction {

  // 日志打印
  //  private static final Log logger = LogFactory.getLog(DictionaryTableAction.class);

  @Method(
      desc = "数据转存初始化数据获取",
      logicStep =
          ""
              + "1: 检查当前任务是否存在 "
              + "2: 获取每张表的转存配置信息"
              + "   2-1: 对数据中的行分隔符进行转换数据库中存储的是Unicode"
              + "   2-2: 对数据中的,列分隔符进行转换,数据库存储的是Unicode"
              + "3: 如果数据库的数据是空的,则使用数据字典解析后的xml做为数据集"
              + "   3-1: 对数据中的行分隔符进行转换数据字典解析的xml中的是Unicode"
              + "   3-2: 对数据中的,列分隔符进行转换,数据字典解析的xml中的是Unicode")
  @Param(name = "colSetId", desc = "采集任务ID", range = "不可为空")
  @Return(desc = "返回表的数据转存配置信息", range = "可以为空")
  public Map<String, List<Map<String, Object>>> getInitDataTransfer(long colSetId) {

    long countNum =
        Dbo.queryNumber("SELECT COUNT(1) FROM " + Database_set.TableName + " WHERE database_id = ?")
            .orElseThrow(() -> new BusinessException("SQL查询错误"));
    if (countNum == 0) {
      CheckParam.throwErrorMsg("采集任务( %s ), 不存在", colSetId);
    }

    List<Map<String, Object>> dataBaseTransDataList =
        Dbo.queryList(
            "SELECT t1.table_name,t1.table_ch_name,t2.*  from "
                + Table_info.TableName
                + " t1 left join "
                + Data_extraction_def.TableName
                + " t2 ON t1.table_id = t2.table_id WHERE t1.database_id = ? order by t1.table_name",
            colSetId);

    //    3: 如果数据库的数据是空的,则使用数据字典解析后的xml做为数据集
    if (dataBaseTransDataList.size() == 0) {

      Map<String, List<Map<String, Object>>> dataTransfer = getDataTransfer(colSetId);
      dataTransfer.forEach((key, itemList) -> storageDataFormat(itemList));
      return dataTransfer;
    } else {

      storageDataFormat(dataBaseTransDataList);
      return dataBaseTransDataList.stream()
          .collect(Collectors.groupingBy(itemMap -> String.valueOf(itemMap.get("table_name"))));
    }
  }

  @Method(desc = "将数据中的Unicode转换为字符串", logicStep = "Unicode数据转换")
  @Param(name = "storageData", desc = "需要转换的List数据", range = "不可为空")
  private void storageDataFormat(List<Map<String, Object>> storageData) {

    storageData.forEach(
        itemMap -> {
          // 2-1: 对数据中的行分隔符进行转换数据库中存储的是Unicode
          String row_separator = String.valueOf(itemMap.get("row_separator"));
          if (StringUtil.isNotBlank(row_separator)) {
            itemMap.put("row_separator", StringUtil.unicode2String(row_separator));
          }
          // 2-2: 对数据中的,列分隔符进行转换,数据库存储的是Unicode
          String database_separatorr = String.valueOf(itemMap.get("database_separatorr"));
          if (StringUtil.isNotBlank(database_separatorr)) {
            itemMap.put("database_separatorr", StringUtil.unicode2String(database_separatorr));
          }
        });
  }

  @Method(desc = "根据colSetId去数据库中查出DB连接信息", logicStep = "1、根据colSetId和userId去数据库中查出DB连接信息")
  @Param(name = "colSetId", desc = "采集任务ID", range = "不为空")
  @Return(desc = "查询结果集", range = "不为空")
  private Map<String, List<Map<String, Object>>> getDataTransfer(long colSetId) {

    Map<String, Object> databaseInfo = getDatabaseSetInfo(colSetId);

    String respMsg =
        SendMsgUtil.getAllTableName(
            (long) databaseInfo.get("agent_id"),
            getUserId(),
            databaseInfo,
            AgentActionUtil.GETALLTABLESTORAGE);

    return JSON.parseObject(
        respMsg, new TypeReference<Map<String, List<Map<String, Object>>>>() {});
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
      CheckParam.throwErrorMsg("任务(%s)不存在!!!", colSetId);
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

  @Method(
      desc = "保存数据转存配置信息",
      logicStep =
          ""
              + "1: 检查当前任务是否存在,如果不存在则抛出异常 "
              + "2: 保存表的数据转存配置信息"
              + "   2-1: 如果存在数据转存的ID信息,则表示为编辑状态"
              + "   2-2: 没有ID信息存在则视为新增")
  @Param(name = "colSetId", desc = "采集任务ID", range = "不可为空")
  @Param(name = "dataExtractionDefs", desc = "数据转存的数据数组字符串", range = "不可为空", isBean = true)
  @Return(desc = "返回采集任务的ID", range = "不可为空")
  public long saveDataTransferData(long colSetId, Data_extraction_def[] dataExtractionDefs) {

    // 1: 检查当前任务是否存在,如果不存在则抛出异常
    long countNum =
        Dbo.queryNumber(
                "SELECT * FROM " + Database_set.TableName + " WHERE database_id = ?", colSetId)
            .orElseThrow(() -> new BusinessException("SQL查询错误"));
    if (countNum == 0) {
      CheckParam.throwErrorMsg("采集任务ID(%s), 不存在", colSetId);
    }
    // 记录是第几张表配置，防止出现未配置参数出现的错误提示不明确
    int index = 1;
    for (Data_extraction_def dataExtractionDef : dataExtractionDefs) {
      CheckParam.checkData(
          "第(%s)张表,表关联的表ID不能为空", String.valueOf(dataExtractionDef.getTable_id()), index);
      CheckParam.checkData("第(%s)张表,是否需要表头不能为空", dataExtractionDef.getIs_header(), index);
      CheckParam.checkData("第(%s)张表,数据抽取落地编码不能为空", dataExtractionDef.getDatabase_code(), index);
      CheckParam.checkData("第(%s)张表,数据落地格式不能为空", dataExtractionDef.getDbfile_format(), index);

      // 行分隔符转为Unicode编码
      String row_separator = dataExtractionDef.getRow_separator();
      if (StringUtil.isNotBlank(row_separator)) {
        dataExtractionDef.setRow_separator(StringUtil.string2Unicode(row_separator));
      } else {
        CheckParam.throwErrorMsg("第(%s)张表,行分隔符不能为空", index);
      }

      // 列分隔符转为Unicode编码
      String database_separatorr = dataExtractionDef.getDatabase_separatorr();
      if (StringUtil.isNotBlank(database_separatorr)) {
        dataExtractionDef.setDatabase_separatorr(StringUtil.string2Unicode(database_separatorr));
      } else {
        CheckParam.throwErrorMsg("第(%s)张表,列分割符不能为空", index);
      }
      //      2-2: 没有ID信息存在则视为新增
      if (dataExtractionDef.getDed_id() == null) {

        dataExtractionDef.setDed_id(PrimayKeyGener.getNextId());
        dataExtractionDef.add(Dbo.db());
      } else {
        //        2-1: 如果存在数据转存的ID信息,则表示为编辑状态
        dataExtractionDef.update(Dbo.db());
      }
      index++;
    }

    return colSetId;
  }
}
