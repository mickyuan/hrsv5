package hrds.b.biz.agent.dbagentconf.fileconf;

import com.alibaba.fastjson.JSONArray;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.resultset.Result;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.DataExtractType;
import hrds.commons.codes.FileFormat;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Data_extraction_def;
import hrds.commons.entity.Table_info;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.key.PrimayKeyGener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "定义卸数文件配置", author = "WangZhengcheng")
public class FileConfStepAction extends BaseAction {

  @Method(desc = "根据数据库设置ID获得定义卸数文件页面初始信息", logicStep = "" + "1、根据数据库设置ID去数据库中查询与数据抽取相关的信息")
  @Param(name = "colSetId", desc = "数据库设置ID，源系统数据库设置表主键，数据库对应表外键", range = "不为空")
  @Return(desc = "查询结果集", range = "不为空")
  public Result getInitInfo(long colSetId) {
    Result result =
        Dbo.queryResult(
            " select * "
                + " from "
                + Table_info.TableName
                + " ti left join "
                + Data_extraction_def.TableName
                + " ded "
                + " on ti.table_id = ded.table_id where ti.database_id = ?",
            colSetId);
    if (!result.isEmpty()) {
      for (int i = 0; i < result.getRowCount(); i++) {
        if (StringUtil.isNotBlank(result.getString(i, "row_separator"))) {
          result.setValue(
              i, "row_separator", StringUtil.unicode2String(result.getString(i, "row_separator")));
        }
        if (StringUtil.isNotBlank(result.getString(i, "database_separatorr"))) {
          result.setValue(
              i,
              "database_separatorr",
              StringUtil.unicode2String(result.getString(i, "database_separatorr")));
        }
      }
    }
    return result;
  }

  @Method(
      desc = "根据数据抽取方式返回卸数文件格式",
      logicStep =
          ""
              + "1、如果是仅做数据抽取，那么卸数文件格式为定长，非定长，CSV"
              + "2、如果是抽取并入库，那么卸数文件格式为非定长，CSV，ORC，PARQUET，SEQUENCEFILE")
  @Param(name = "extractType", desc = "数据抽取方式", range = "请从DataExtractType代码项取值")
  @Return(desc = "map集合", range = "key为文件格式名称，用于在下拉框中显示" + "value为文件格式code，用于保存时向后台接口传参")
  public Map<String, String> getFileFormatByExtractType(String extractType) {
    DataExtractType dataExtractType = DataExtractType.ofEnumByCode(extractType);
    Map<String, String> formatMap = new HashMap<>();
    formatMap.put(FileFormat.FeiDingChang.getValue(), FileFormat.FeiDingChang.getCode());
    formatMap.put(FileFormat.CSV.getValue(), FileFormat.CSV.getCode());
    // 1、如果是仅做数据抽取，那么卸数文件格式为定长，非定长，CSV
    if (dataExtractType == DataExtractType.ShuJuKuChouQuLuoDi) {
      formatMap.put(FileFormat.DingChang.getValue(), FileFormat.DingChang.getCode());
    }
    // 2、如果是抽取并入库，那么卸数文件格式为非定长，CSV，ORC，PARQUET，SEQUENCEFILE
    else {
      formatMap.put(FileFormat.ORC.getValue(), FileFormat.ORC.getCode());
      formatMap.put(FileFormat.PARQUET.getValue(), FileFormat.PARQUET.getCode());
      formatMap.put(FileFormat.SEQUENCEFILE.getValue(), FileFormat.SEQUENCEFILE.getCode());
    }
    return formatMap;
  }

  @Method(
      desc = "保存卸数文件配置",
      logicStep =
          ""
              + "1、将传入的json格式的字符串转换为List<Data_extraction_def>集合"
              + "2、遍历集合，对集合中的内容调用方法进行校验"
              + "3、根据table_id去data_extraction_def表中删除尝试删除该表曾经的卸数文件配置，不关心删除数目"
              + "4、保存数据")
  @Param(
      name = "extractionDefString",
      desc =
          "存有待保存信息的Data_extraction_def实体数组格式字符串"
              + "注意："
              //							+ "(1)、数据抽取方式请从DataExtractType代码项取值"
              + "(1)、数据字符集请从DataBaseCode代码项取值"
              + "(2)、抽取数据存储方式请从FileFormat代码项取值",
      range =
          "每张表的抽取方式(dbfile_format)为多种, 抽取方式为"
              + "非定长,定长,CSV : 请传递数数据 换行符(row_separator),数据分隔符(database_separatorr),数据字符集(database_code),落地目录(plane_url);"
              + "ORC,PARQUET,SEQUENCEFILE : 请传递数据 数据字符集(database_code),落地目录(plane_url); "
              + "举例假如数据格式有俩种(非定长,ORC)则数据格式如下: "
              + "[{表名ID:A,抽取方式:非定长,换行符:xxx,数据分隔符:xxx,数据字符集:xxx,落地目录:xxx},"
              + "{表名ID:A,抽取方式:ORC,数据字符集:xxx,落地目录:xxx}"
              + "],有几种抽取方式就有几个值",
      isBean = true)
  @Param(name = "colSetId", desc = "数据库采集设置表ID", range = "不为空")
  @Return(desc = "返回数据库设置ID，方便下一个页面能够通过这个参数加载初始化设置", range = "不为空")
  public long saveFileConf(Data_extraction_def[] extractionDefString, long colSetId) {
    // 1、将传入的json格式的字符串转换为List<Data_extraction_def>集合
    //		List<Data_extraction_def> dataExtractionDefs = JSONArray.parseArray(extractionDefString,
    //				Data_extraction_def.class);

    if (extractionDefString == null || extractionDefString.length != 0) {
      throw new BusinessException("未获取到卸数文件信息");
    }

    // 2、遍历集合，对集合中的内容调用方法进行校验
    verifySeqConf(extractionDefString);
    for (int i = 0; i < extractionDefString.length; i++) {

      Data_extraction_def def = extractionDefString[i];

      // 3、根据table_id去data_extraction_def表中删除尝试删除该表曾经的卸数文件配置，不关心删除数目
      Dbo.execute(
          "delete from " + Data_extraction_def.TableName + " where table_id = ?",
          def.getTable_id());
      def.setDed_id(PrimayKeyGener.getNextId());
      // 将卸数分隔符(行，列)转换为unicode码
      if (StringUtil.isBlank(def.getDbfile_format())) {
        throw new BusinessException("第 " + (i + 1) + " 条的数据抽取方式不能为空!");
      }

      FileFormat fileFormat = FileFormat.ofEnumByCode(def.getDbfile_format());
      if (fileFormat == FileFormat.DingChang
          || fileFormat == FileFormat.FeiDingChang
          || fileFormat == FileFormat.CSV) {
        // 检查行分隔符不能为空
        if (StringUtil.isBlank(def.getRow_separator())) {
          throw new BusinessException("第 " + (i + 1) + " 条的数据行分割符不能为空!");
        } else {
          // 如果不为空则使用Unicode进行转码入库
          def.setRow_separator(StringUtil.string2Unicode(def.getRow_separator()));
        }
        // 检查数据分隔符不能为空
        if (StringUtil.isBlank(def.getDatabase_separatorr())) {
          throw new BusinessException("第 " + (i + 1) + " 条的数据分隔符不能为空!");
        } else {
          // 如果不为空则使用Unicode进行转码入库
          def.setRow_separator(StringUtil.string2Unicode(def.getRow_separator()));
        }
      }

      if (StringUtil.isBlank(def.getDatabase_code())) {
        throw new BusinessException("第 " + (i + 1) + " 条的数据字符集不能为空!");
      }

      if (StringUtil.isBlank(def.getPlane_url())) {
        throw new BusinessException("第 " + (i + 1) + " 条的数据落地目录不能为空!");
      }

      // TODO data_extraction_def表is_header字段设置默认值为"是"，后期可能会修改
      def.setIs_header(IsFlag.Shi.getCode());
      def.add(Dbo.db());
    }
    // 4、保存数据
    return colSetId;
  }

  @Method(
      desc = "在保存表分隔符设置的时候，传入实体，根据数据抽取存储方式，来校验其他的内容",
      logicStep =
          ""
              + "1、校验保存数据必须关联表"
              + "2、校验采集的方式如果是仅抽取"
              + "   2-1、文件格式如果是非定长，用户必须填写行分隔符和列分隔符"
              + "   2-2、文件格式如果是定长/CSV，那么行分隔符和列分隔符，用户可以填，可以不填"
              + "3、校验采集的方式如果是抽取并入库"
              + "   3-1、如果是ORC/PARQUET/SEQUENCEFILE，不允许用户填写行分隔符和列分隔符"
              + "   3-2、如果是TEXTFILE，则校验，用户必须填写行分隔符和列分隔符"
              + "   3-3、如果是CSV，则不进行校验，即如果用户不填写，就卸成标准CSV，否则，按照用户指定的列分隔符写文件"
              + "6、如果校验出现问题，直接抛出异常")
  @Param(name = "def", desc = "用于对待保存的数据进行校验", range = "数据抽取定义实体类对象")
  private void verifySeqConf(Data_extraction_def[] dataExtractionDefs) {
    for (int i = 0; i < dataExtractionDefs.length; i++) {
      Data_extraction_def def = dataExtractionDefs[i];
      // 1、校验保存数据必须关联表
      if (def.getTable_id() == null) {
        throw new BusinessException("保存卸数文件配置，第" + (i + 1) + "数据必须关联表ID");
      }
      // 2、校验采集的方式如果是仅抽取
      DataExtractType extractType = DataExtractType.ofEnumByCode(def.getData_extract_type());
      FileFormat fileFormat = FileFormat.ofEnumByCode(def.getDbfile_format());
      if (extractType == DataExtractType.ShuJuKuChouQuLuoDi) {
        // 如果数据抽取方式是仅抽取，那么校验存储方式不能是ORC，PARQUET，SEQUENCEFILE
        if (fileFormat == FileFormat.ORC
            || fileFormat == FileFormat.PARQUET
            || fileFormat == FileFormat.SEQUENCEFILE) {
          throw new BusinessException("仅抽取操作，只能指定非定长|定长|CSV三种存储格式");
        }
        // 2-1、文件格式如果是非定长，用户必须填写行分隔符和列分隔符
        if (fileFormat == FileFormat.FeiDingChang) {
          if (StringUtil.isEmpty(def.getRow_separator())) {
            throw new BusinessException("数据抽取保存为非定长文件，请填写行分隔符");
          }
          if (StringUtil.isEmpty(def.getDatabase_separatorr())) {
            throw new BusinessException("数据抽取保存为非定长文件，请填写列分隔符");
          }
        }
        // 2-2、文件格式如果是定长/CSV，那么行分隔符和列分隔符，用户可以填，可以不填
      }
      // 3、校验采集的方式如果是抽取并入库
//      if (extractType == DataExtractType.ShuJuChouQuJiRuKu) {
//        // 如果数据抽取方式是抽取及入库，那么校验存储方式不能是定长
//        if (fileFormat == FileFormat.DingChang) {
//          throw new BusinessException("抽取并入库操作，不能保存为定长文件");
//        }
//        // 3-1、如果是ORC/PARQUET/SEQUENCEFILE，不允许用户填写行分隔符和列分隔符
//        if (fileFormat == FileFormat.ORC
//            || fileFormat == FileFormat.PARQUET
//            || fileFormat == FileFormat.SEQUENCEFILE) {
//          if (StringUtil.isNotEmpty(def.getRow_separator())) {
//            throw new BusinessException("数据抽取并入库，保存格式为ORC/PARQUET/SEQUENCEFILE，" + "不能指定行分隔符");
//          }
//          if (StringUtil.isNotEmpty(def.getDatabase_separatorr())) {
//            throw new BusinessException("数据抽取并入库，保存格式为ORC/PARQUET/SEQUENCEFILE，" + "不能指定列分隔符");
//          }
//        }
//        // 3-2、如果是非定长，则校验，用户必须填写行分隔符和列分隔符
//        if (fileFormat == FileFormat.FeiDingChang) {
//          if (StringUtil.isEmpty(def.getRow_separator())) {
//            throw new BusinessException("数据抽取并入库，保存格式为非定长，请指定行分隔符");
//          }
//          if (StringUtil.isEmpty(def.getDatabase_separatorr())) {
//            throw new BusinessException("数据抽取并入库，保存格式为非定长，请指定列分隔符");
//          }
//        }
//        // 3-3、如果是CSV，则不进行校验，即如果用户不填写，就卸成标准CSV，否则，按照用户指定的列分隔符写文件
//      }
    }
  }
}
