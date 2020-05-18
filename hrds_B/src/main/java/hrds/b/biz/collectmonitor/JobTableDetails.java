package hrds.b.biz.collectmonitor;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import hrds.commons.codes.ExecuteState;
import hrds.commons.entity.Collect_case;
import hrds.commons.enumtools.StageConstant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "处理采集任务表的信息", author = "Mr.Lee", createdate = "2019-11-01")
public class JobTableDetails {

  @Method(
      desc = "处理采集作业的信息",
      logicStep = "1: 将集合中,每个表的N中采集状态剥离出来．因为每个表的采集步骤有很多布，如：(卸数,上传,数据加载,计算增量,数据登记) ")
  @Param(name = "collectJobList", desc = "任务采集表信息的集合", range = "可以为空,如果为空表示当前做任务下没有采集表信息存在")
  @Return(desc = "返回处理后的数据信息", range = "可以为空,如果为空表示没有采集表信息存在")
  static List<Map<String, Object>> getTableDetails(List<Collect_case> collectJobList) {

    // 存放处理后的数据信息
    Map<String, Map<String, Object>> detailsMap = new LinkedHashMap<String, Map<String, Object>>();

    //      1: 将集合中,每个表的N中采集状态剥离出来．因为每个表的采集步骤，如：(卸数,上传,数据加载,计算增量,数据登记) ")
    collectJobList.forEach(
        collect_case -> {

          // 表采集步骤的开始时间
          String collect_s_date =
              DateUtil.parseStr2DateWith8Char(collect_case.getCollect_s_date()).toString()
                  + ' '
                  + DateUtil.parseStr2TimeWith6Char(collect_case.getCollect_s_time()).toString();

          // 表采集步骤的结束时间
          String collect_e_date =
              DateUtil.parseStr2DateWith8Char(String.valueOf((collect_case.getCollect_e_date())))
                      .toString()
                  + ' '
                  + DateUtil.parseStr2TimeWith6Char(
                          StringUtil.isBlank(collect_case.getCollect_e_time())
                              ? "000001"
                              : collect_case.getCollect_e_time())
                      .toString();
          // 采集原表名称
          String table_name = collect_case.getTask_classify();
          /*
           * 作业步骤的类型
           *  1 : 卸数
           *  2 : 上传
           *  3 : 数据加载
           *  4 : 计算增量
           *  5 : 数据登记
           */
          String job_type = collect_case.getJob_type();
          String stateStr = null;
          if (StageConstant.UNLOADDATA.getCode() == Integer.parseInt(job_type)) {
            stateStr = "B001";
          } else if (StageConstant.UPLOAD.getCode() == Integer.parseInt(job_type)) {
            stateStr = "B002";
          } else if (StageConstant.DATALOADING.getCode() == Integer.parseInt(job_type)) {
            stateStr = "UHDFS";
          } else if (StageConstant.CALINCREMENT.getCode() == Integer.parseInt(job_type)) {
            stateStr = "LHIVE";
          } else {
            stateStr = "SOURCE";
          }
          // 如果前表已经存在了
          if (detailsMap.containsKey(table_name)) {
            Map<String, Object> map = detailsMap.get(table_name);
            map.put(stateStr + "_S_TITLE", collect_s_date);
            map.put(stateStr + "_E_TITLE", collect_e_date);
            map.put(stateStr, collect_case.getExecute_state());
            map.put(stateStr + "error", collect_case.getCc_remark());
            // 这里只考虑采集错误的信息...因为最终要把含有错误的作业信息优先显示在最前面
            //            if
            // (ExecuteState.YunXingShiBai.getCode().equals(collect_case.getExecute_state())) {
            //              map.put("errorNum", (int) map.get("errorNum") + 1);
            //            }
          } else {
            Map<String, Object> map = new HashMap<>();
            map.put("table_name", table_name);
            map.put(stateStr + "_S_TITLE", collect_s_date);
            map.put(stateStr + "_E_TITLE", collect_e_date);
            map.put(stateStr, collect_case.getExecute_state());
            map.put(stateStr + "error", collect_case.getCc_remark());

            // 这里只考虑采集错误的信息...因为最终要把含有错误的作业信息优先显示在最前面
            //            if
            // (ExecuteState.YunXingShiBai.getCode().equals(collect_case.getExecute_state())) {
            //              map.put("errorNum", 1);
            //            }
            //            else if
            // (ExecuteState.YunXingWanCheng.getCode().equals(collect_case.getExecute_state())){
            //              map.put("secussNum", 1);
            //            }

            detailsMap.put(table_name, map);
          }
        });

    // 存放处理后的数据结果集信息
    List<Map<String, Object>> collectTableResult = new ArrayList<Map<String, Object>>();
    detailsMap.forEach(
        (table_name, v) -> {
          collectTableResult.add(v);
        });

    return collectTableResult;
  }
}
