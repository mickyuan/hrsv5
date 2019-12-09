package hrds.c.biz.etlintervention;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.db.jdbc.DefaultPageImpl;
import fd.ng.db.jdbc.Page;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.util.Dbo;
import hrds.c.biz.util.ETLJobUtil;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.Job_Status;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "作业调度系统干预处理类", author = "dhw", createdate = "2019/12/9 10:18")
public class SysLevelInterventionAction extends BaseAction {

    // 拼接sql对象
    private static final SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();

    @Method(desc = "查询系统级干预作业信息",
            logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
                    "2.判断工程是否存在" +
                    "3.获取工程名称" +
                    "4.获取批量日期" +
                    "5.获取系统批量情况" +
                    "6.创建存放系统批量情况相关信息的集合" +
                    "7.返回存放系统批量情况相关信息的集合")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
    @Return(desc = "返回内容描述", range = "取值范围")
    public Map<String, Object> searchSysLevelInterventionInfo(String etl_sys_cd) {
        // 1.数据可访问权限处理方式，通过user_id进行权限控制
        // 2.判断工程是否存在
        if (!ETLJobUtil.isEtlSysExist(etl_sys_cd, getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 3.获取工程名称
        String etl_sys_name = ETLJobUtil.getEtlSysName(etl_sys_cd, getUserId());
        // 4.获取批量日期
        List<String> currBathDateList = Dbo.queryOneColumnList("select curr_bath_date from " +
                Etl_sys.TableName + " where etl_sys_cd=?", etl_sys_cd);
        String curr_bath_date = currBathDateList.get(0);
        // 5.获取系统批量情况
        Map<String, Object> etlJobCurrMap = Dbo.queryOneObject("select sum(case when job_disp_status=? " +
                        " then 1 else 0 end) as done_num ," +
                        "sum(case when job_disp_status=? then 1 else 0 end) as pending_num,sum(case when " +
                        " job_disp_status='A' then 1 else 0 end) as alarm_num,sum(case when " +
                        " job_disp_status=? then 1 else 0 end) as error_num,sum(case when " +
                        " job_disp_status=? then 1 else 0 end) as waiting_num,sum(case when " +
                        " job_disp_status=? then 1 else 0 end) as running_num,sum(case when " +
                        " job_disp_status=? then 1 else 0 end) as stop_num from " + Etl_job_cur.TableName +
                        " where etl_sys_cd=?", Job_Status.DONE.getCode(), Job_Status.PENDING.getCode(),
                Job_Status.ERROR.getCode(), Job_Status.WAITING.getCode(), Job_Status.RUNNING.getCode(),
                Job_Status.STOP.getCode(), etl_sys_cd);
        // 6.创建存放系统批量情况相关信息的集合
        etlJobCurrMap.put("curr_bath_date", curr_bath_date);
        etlJobCurrMap.put("etl_sys_name", etl_sys_name);
        etlJobCurrMap.put("etl_sys_cd", etl_sys_cd);
        // 7.返回存放系统批量情况相关信息的集合
        return etlJobCurrMap;
    }

    @Method(desc = "查询系统级当前干预情况",
            logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
                    "2.判断工程是否存在" +
                    "3.查询系统级当前干预情况")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
    @Return(desc = "返回查询系统级当前干预情况", range = "无限制")
    public List<Map<String, Object>> searchSysLevelCurrInterventionInfo(String etl_sys_cd) {
        // 1.数据可访问权限处理方式，通过user_id进行权限控制
        // 2.判断工程是否存在
        if (!ETLJobUtil.isEtlSysExist(etl_sys_cd, getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 3.查询系统级当前干预情况
        return Dbo.queryList("SELECT t1.event_id,t1.etl_sys_cd,t1.etl_job,t1.etl_hand_type," +
                "t1.pro_para,hand_status,st_time,warning,CONCAT(t3.sub_sys_desc,'(',t3.sub_sys_cd,')') " +
                " AS subSysName FROM " + Etl_job_hand.TableName + " t1 left join " + Etl_job_cur.TableName +
                " t2 on t1.etl_job=t2.etl_job and t1.etl_sys_cd=t2.etl_sys_cd left join "
                + Etl_sub_sys_list.TableName + " t3 on t2.sub_sys_cd=t3.sub_sys_cd " +
                " and t2.etl_sys_cd=t3.etl_sys_cd WHERE t1.etl_sys_cd=?", etl_sys_cd);
    }

    @Method(desc = "分页查询系统级历史干预情况",
            logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
                    "2.判断工程是否存在" +
                    "3.分页查询系统级历史干预情况" +
                    "4.创建存放分页查询系统干预历史干预情况以及总记录数的集合并返回")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
    @Param(name = "currPage", desc = "分页查询当前页", range = "大于0的正整数", valueIfNull = "1")
    @Param(name = "pageSize", desc = "分页查询每页显示记录数", range = "大于0的正整数", valueIfNull = "5")
    @Return(desc = "返回存放分页查询系统干预历史干预情况以及总记录数的集合", range = "无限制")
    public Map<String, Object> searchSysLeverHisInterventionByPage(String etl_sys_cd, int currPage,
                                                                   int pageSize) {
        // 1.数据可访问权限处理方式，通过user_id进行权限控制
        // 2.判断工程是否存在
        if (!ETLJobUtil.isEtlSysExist(etl_sys_cd, getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 3.分页查询系统级历史干预情况
        Page page = new DefaultPageImpl(currPage, pageSize);
        List<Map<String, Object>> handHisList = Dbo.queryPagedList(page, "SELECT t1.event_id,t1.etl_sys_cd," +
                "t1.etl_job,t1.etl_hand_type,t1.pro_para,hand_status,st_time,warning," +
                "CONCAT(t3.sub_sys_desc,'(',t3.sub_sys_cd,')') AS subSysName FROM " + Etl_job_hand_his.TableName
                + " t1 left join " + Etl_job_cur.TableName + " t2 on t1.etl_job=t2.etl_job " +
                " and t1.etl_sys_cd=t2.etl_sys_cd left join " + Etl_sub_sys_list.TableName + " t3 " +
                "on t2.sub_sys_cd=t3.sub_sys_cd and t2.etl_sys_cd=t3.etl_sys_cd WHERE t1.etl_sys_cd=?", etl_sys_cd);
        // 4.创建存放分页查询系统干预历史干预情况以及总记录数的集合并返回
        Map<String, Object> handHisMap = new HashMap<>();
        handHisMap.put("handHisList", handHisList);
        handHisMap.put("totalSize", page.getTotalSize());
        return handHisMap;
    }
}
