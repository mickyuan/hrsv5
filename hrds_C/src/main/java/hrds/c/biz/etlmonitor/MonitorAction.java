package hrds.c.biz.etlmonitor;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.db.resultset.Result;
import fd.ng.web.util.Dbo;
import hrds.c.biz.util.ETLJobUtil;
import hrds.commons.base.BaseAction;
import hrds.commons.entity.Etl_dependency;
import hrds.commons.entity.Etl_job_cur;
import hrds.commons.entity.Etl_job_def;
import hrds.commons.entity.Etl_job_resource_rela;
import hrds.commons.exception.BusinessException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "作业调度监控类", author = "dhw", createdate = "2019/12/12 14:37")
public class MonitorAction extends BaseAction {
    @Method(desc = "监控当前作业信息",
            logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
                    "2.判断工程是否存在" +
                    "3.查询当前工程下作业信息" +
                    "4.查询当前工程下作业资源分配信息" +
                    "5.返回该工程下作业以及作业资源分配信息")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
    @Param(name = "etl_job", desc = "作业名称", range = "新增作业时生成")
    @Return(desc = "返回该工程下作业以及作业资源分配信息", range = "无限制")
    public Map<String, Object> monitorCurrJobInfo(String etl_sys_cd, String etl_job) {
        // 1.数据可访问权限处理方式，通过user_id进行权限控制
        // 2.判断工程是否存在
        if (!ETLJobUtil.isEtlSysExist(etl_sys_cd, getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 3.查询当前工程下作业信息
        Map<String, Object> etlJobCur = Dbo.queryOneObject("select * from " + Etl_job_cur.TableName +
                " where etl_sys_cd=? and etl_job=? order by curr_bath_date desc", etl_sys_cd, etl_job);
        // 4.查询当前工程下作业资源分配信息
        Map<String, Object> resourceRelation = Dbo.queryOneObject("select resource_type,resource_req from "
                + Etl_job_resource_rela.TableName + " where etl_sys_cd=? and etl_job=?",etl_sys_cd,etl_job);
        etlJobCur.put("resourceRelation", resourceRelation);
        // 5.返回该工程下作业以及作业资源分配信息
        return etlJobCur;
    }

    @Method(desc = "监控作业依赖信息",
            logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
                    "2.判断工程是否存在")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
    @Param(name = "etl_job", desc = "作业名称", range = "新增作业时生成")
    @Return(desc = "返回该工程下作业以及作业资源分配信息", range = "无限制")
    public Map<String, Object> monitorJobDependencyInfo(String etl_sys_cd, String etl_job) {
        // 1.数据可访问权限处理方式，通过user_id进行权限控制
        // 2.判断工程是否存在
        if (!ETLJobUtil.isEtlSysExist(etl_sys_cd, getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 3.封装作业依赖属性信息
        Etl_dependency etl_dependency = new Etl_dependency();
        etl_dependency.setEtl_sys_cd(etl_sys_cd);
        etl_dependency.setEtl_job(etl_job);
        etl_dependency.setPre_etl_sys_cd(etl_sys_cd);
        etl_dependency.setPre_etl_job(etl_job);
        // 4.获取上游作业信息
        List<Map<String, Object>> topJobInfoList = topEtlJobDependencyInfo(etl_dependency);
        // 5.获取下游作业信息
        List<Map<String, Object>> downJobInfoList = downEtlJobDependencyInfo(etl_dependency);
        // 将上游作业信息封装入下游作业中
        if (!topJobInfoList.isEmpty()) {
            for (Map<String, Object> topJobInfo : topJobInfoList) {
                downJobInfoList.add(topJobInfo);
            }
        }
        Map<String, Object> dataInfo = new HashMap<>();
        dataInfo.put("id", "0");
        dataInfo.put("topic", etl_job);
        dataInfo.put("aid", "999");
        dataInfo.put("children", downJobInfoList);
        return dataInfo;
    }
    @Method(desc = "获取上游作业依赖信息",
            logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
                    "2.获取上游作业依赖关系" +
                    "3.如果上游作业依赖关系不为空，设置字节点" +
                    "4.返回上游作业依赖关系")
    @Param(name = "etl_dependency", desc = "作业依赖实体对象", range = "与数据库对应表字段规则一致",isBean = true)
    @Return(desc = "返回上游作业依赖关系", range = "无限制")
    private List<Map<String, Object>> topEtlJobDependencyInfo(Etl_dependency etl_dependency) {
        // 1.数据可访问权限处理方式，通过user_id进行权限控制
        // 2.获取上游作业依赖关系
        Result topJob = Dbo.queryResult("select * from " + Etl_dependency.TableName +
                        " ed join " + Etl_job_def.TableName + " ejd on ed.etl_job=ejd.etl_job " +
                        " and ed.etl_sys_cd=ejd.etl_sys_cd where ed.etl_job=? and ed.etl_sys_cd=?",
                etl_dependency.getEtl_job(), etl_dependency.getEtl_sys_cd());
        // 3.如果上游作业依赖关系不为空，设置字节点
        if (!topJob.isEmpty()) {
            for (int i = 0; i < topJob.getRowCount(); i++) {
                topJob.setObject(i, "id", topJob.getString(i, "pre_etl_job"));
                topJob.setObject(i, "topic", topJob.getString(i, "pre_etl_job"));
                topJob.setObject(i, "direction", "left");
                // FIXME 目前只做一层
            }
        }
        // 4.返回上游作业依赖关系
        return topJob.toList();
    }
    @Method(desc = "获取下游作业依赖信息",
            logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
                    "2.查询依赖于当前作业的信息(下游)" +
                    "3.如果下游作业依赖关系不为空，设置字节点" +
                    "4.返回下游作业依赖关系")
    @Param(name = "etl_dependency", desc = "作业依赖实体对象", range = "与数据库对应表字段规则一致",isBean = true)
    @Return(desc = "返回下游作业依赖关系", range = "无限制")
    private List<Map<String, Object>> downEtlJobDependencyInfo(Etl_dependency etl_dependency) {
        // 1.数据可访问权限处理方式，通过user_id进行权限控制
        // 2.查询依赖于当前作业的信息(下游)
        Result downJob = Dbo.queryResult("select ed.*,ejd.* from " + Etl_dependency.TableName +
                        " ed left join " + Etl_job_def.TableName + " ejd on ed.etl_sys_cd=ejd.etl_sys_cd " +
                        " and ed.etl_job=ejd.etl_job WHERE ed.pre_etl_sys_cd=? and ed.pre_etl_job=? " +
                        " order by ejd.job_priority DESC,ed.etl_sys_cd,ed.etl_job",
                etl_dependency.getPre_etl_sys_cd(), etl_dependency.getEtl_job());
        // 3.如果下游作业依赖关系不为空，设置字节点
        if (!downJob.isEmpty()) {
            for (int i = 0; i < downJob.getRowCount(); i++) {
                downJob.setObject(i, "id", downJob.getString(i, "etl_job"));
                downJob.setObject(i, "topic", downJob.getString(i, "etl_job"));
                downJob.setObject(i, "direction", "right");
                // FIXME 目前只做一层
            }
        }
        // 4.返回下游作业依赖关系
        return downJob.toList();
    }
}
