package hrds.c.biz.util;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import hrds.commons.exception.BusinessException;

import java.util.HashMap;
import java.util.Map;

@DocClass(desc = "字段名称转换为中文字段描述", author = "dhw", createdate = "2019/12/24 11:05")
public class ConvertColumnNameToChinese {

    @Method(desc = "根据字段名称获取中文字段描述信息",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.获取所有中文名称" +
                    "3.返回对应列名的中文名")
    @Param(name = "列字段名称", desc = "column_name", range = "不为空")
    @Return(desc = "返回内容描述", range = "无限制")
    public static String getZh_name(String column_name) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.获取所有中文名称
        Map<String, String> column_Zh_name = getColumn_Zh_name();
        if (StringUtil.isBlank(column_name)) {
            throw new BusinessException("列字段名称不能为空！");
        }
        // 3.返回对应列名的中文名
        return column_Zh_name.get(column_name);
    }

    @Method(desc = "获取列名称对应中文名称集合",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.设置列名称对应中文名称" +
                    "3.返回列名称对应中文名称集合")
    @Return(desc = "返回列名称对应中文名称集合", range = "无限制")
    private static Map<String, String> getColumn_Zh_name() {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.设置列名称对应中文名称
        Map<String, String> map = new HashMap<>();
        map.put("etl_sys_cd", "工程编号");
        map.put("para_cd", "变量编号");
        map.put("para_val", "变量值");
        map.put("para_type", "变量类型");
        map.put("para_desc", "作业描述");
        map.put("resource_max", "资源阀值");
        map.put("resource_used", "已使用数");
        map.put("main_serv_sync", "主服务器同步标志");
        map.put("etl_job", "作业名");
        map.put("sub_sys_cd", "任务编号");
        map.put("etl_job_desc", "作业描述");
        map.put("pro_type", "作业程序类型");
        map.put("pro_dic", "作业程序目录");
        map.put("pro_name", "作业程序名称");
        map.put("pro_para", "作业程序参数");
        map.put("log_dic", "日志目录");
        map.put("disp_freq", "调度频率");
        map.put("disp_offset", "调度时间位移");
        map.put("disp_type", "调度触发方式");
        map.put("disp_time", "调度触发时间");
        map.put("job_eff_flag", "作业有效标志");
        map.put("job_priority", "作业优先级");
        map.put("job_disp_status", "作业调度状态");
        map.put("curr_st_time", "开始时间");
        map.put("curr_end_time", "结束时间");
        map.put("overlength_val", "超长阀值");
        map.put("overtime_val", "超时阀值");
        map.put("curr_bath_date", "当前批量日期");
        map.put("comments", "备注信息");
        map.put("today_disp", "当天是否调度");
        map.put("job_process_id", "作业进程号");
        map.put("job_priority_curr", "作业当前优先级");
        map.put("job_return_val", "作业返回值");
        map.put("upd_time", "更新日期");
        map.put("pre_etl_sys_cd", "上游系统代码");
        map.put("pre_etl_job", "上游作业名");
        map.put("status", "状态");
        map.put("resource_type", "资源使用类型");
        map.put("resource_req", "资源需求数");
        map.put("sub_sys_desc", "子系统描述");
        map.put("exe_frequency", "每隔(分钟)执行");
        map.put("exe_num", "执行次数");
        map.put("com_exe_num", "已经执行次数");
        map.put("last_exe_time", "上次执行时间");
        map.put("star_time", "开始执行时间");
        map.put("end_time", "结束执行时间");
        // 3.返回列名称对应中文名称集合
        return map;
    }
}
