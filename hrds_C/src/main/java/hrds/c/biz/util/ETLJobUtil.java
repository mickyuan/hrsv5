package hrds.c.biz.util;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.util.Dbo;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;

import java.util.List;

@DocClass(desc = "作业调度工程工具类", author = "dhw", createdate = "2019/11/26 11:11")
public class ETLJobUtil {

    private static final SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();

    @Method(desc = "判断当前工程是否还存在",
            logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
                    "2.判断user_id是否为空，为空添加条件" +
                    "3.判断当前工程是否还存在，存在返回true,不存在返回false")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
    @Param(name = "user_id", desc = "创建工程用户ID", range = "新增用户时生成", nullable = true)
    @Return(desc = "返回工程是否存在标志", range = "true代表存在，false代表不存在")
    public static boolean isEtlSysExist(String etl_sys_cd, Long user_id) {
        // 1.数据可访问权限处理方式，通过user_id进行权限控制
        asmSql.clean();
        asmSql.addSql("select count(*) from " + Etl_sys.TableName + " where etl_sys_cd=?");
        asmSql.addParam(etl_sys_cd);
        // 2.判断user_id是否为空，为空添加条件
        if (user_id != null) {
            asmSql.addSql(" and user_id=?");
            asmSql.addParam(user_id);
        }
        // 3.判断当前工程是否还存在，存在返回true,不存在返回false
        if (Dbo.queryNumber(asmSql.sql(), asmSql.params()).orElseThrow(() ->
                new BusinessException("sql查询错误")) > 0) {
            // 存在
            return true;
        }
        // 不存在
        return false;
    }

    @Method(desc = "确定该工程下对应的任务确实存在",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.确定该工程下对应的任务是否存在,存在返回true,不存在返回false")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "无限制")
    @Param(name = "sub_sys_cd", desc = "任务编号", range = "无限制")
    @Return(desc = "该工程下对应的任务是否存在的标志", range = "true代表存在，false代表不存在")
    public static boolean isEtlSubSysExist(String etl_sys_cd, String sub_sys_cd) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.确定该工程下对应的任务是否存在，存在返回true,不存在返回false
        if (Dbo.queryNumber("SELECT count(1) FROM " + Etl_sub_sys_list.TableName + " WHERE etl_sys_cd=?"
                + " AND sub_sys_cd=?", etl_sys_cd, sub_sys_cd).orElseThrow(() ->
                new BusinessException("sql查询错误")) != 1) {
            return false;
        }
        return true;
    }

    @Method(desc = "判断该工程对应的任务下是否还有作业",
            logicStep = "1.数据可访问权限处理方式，通过user_id关联进行权限控制" +
                    "2.判断该工程对应的任务下是否还有作业，有作业则不能删除")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
    @Param(name = "sub_sys_cd", desc = "任务编号", range = "新增任务时生成")
    public static void isEtlJobDefExistUnderEtlSubSys(String etl_sys_cd, String sub_sys_cd) {
        // 1.数据可访问权限处理方式，通过user_id关联进行权限控制
        // 2.判断该工程对应的任务下是否还有作业，有作业则不能删除
        if (Dbo.queryNumber("select count(1) from " + Etl_job_def.TableName + "  WHERE etl_sys_cd=? "
                + " AND sub_sys_cd=?", etl_sys_cd, sub_sys_cd).
                orElseThrow(() -> new BusinessException("sql查询错误！")) > 0) {
            throw new BusinessException("该工程对应的任务下还有作业，不能删除！");
        }
    }

    @Method(desc = "新增作业判断作业名称是否已存在，存在不能新增",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限验证" +
                    "2.新增作业判断作业名称是否已存在，存在不能新增")
    @Param(name = "etl_job_def", desc = "作业定义实体对象", range = "与etl_job_def数据库表定义规则一致",
            isBean = true)
    @Return(desc = "作业名称是否已存在标志", range = "true代表已存在，false代表不存在")
    public static boolean isEtlJobDefExist(String etl_sys_cd, String etl_job) {
        // 1.数据可访问权限处理方式，该方法不需要权限验证
        // 2.新增作业判断作业名称是否已存在，存在不能新增
        if (Dbo.queryNumber("SELECT count(1) FROM " + Etl_job_def.TableName + " WHERE etl_job=? " +
                " AND etl_sys_cd=?", etl_job, etl_sys_cd).orElseThrow(() ->
                new BusinessException("sql查询错误")) > 0) {
            return true;
        }
        return false;
    }

    @Method(desc = "判断是否资源需求过大",
            logicStep = "1.数据可访问权限处理方式，此方法不需要权限控制" +
                    "2.判断资源是否存在" +
                    "3.检测当前作业分配的占用资源数是否过大")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
    @Param(name = "resource_type", desc = "参数类型", range = "新增资源定义时生成")
    @Param(name = "resource_seq", desc = "资源需求数", range = "无限制")
    public static void isResourceDemandTooLarge(String etl_sys_cd, String resource_type, Integer resource_seq) {
        // 1.数据可访问权限处理方式，此方法不需要权限控制
        // 2.判断资源是否存在
        if (!isEtlResourceExist(etl_sys_cd, resource_type)) {
            throw new BusinessException("当前工程对应的资源已不存在！");
        }
        // 3.检测当前作业分配的占用资源数是否过大
        List<Integer> resource_max = Dbo.queryOneColumnList("select resource_max from "
                        + Etl_resource.TableName + " where etl_sys_cd=? AND resource_type=?",
                etl_sys_cd, resource_type);
        if (resource_seq > resource_max.get(0)) {
            throw new BusinessException("当前分配的作业资源需求数过大 ,已超过当前资源类型的最大阀值数!");
        }
    }

    @Method(desc = "判断当前工程对应作业资源分配信息是否存在",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限验证" +
                    "2.判断当前工程对应作业资源分配信息是否存在")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
    @Param(name = "etl_job", desc = "作业名称", range = "新增作业时生成")
    @Return(desc = "当前工程对应作业资源分配信息是否存在标志", range = "true代表存在，false代表不存在")
    public static boolean isEtlJobResourceRelaExist(String etl_sys_cd, String etl_job) {
        // 1.数据可访问权限处理方式，该方法不需要权限验证
        // 2.判断当前工程对应作业资源分配信息是否存在
        if (Dbo.queryNumber("select count(*) from " + Etl_job_resource_rela.TableName +
                " where etl_sys_cd=? and etl_job=?", etl_sys_cd, etl_job).orElseThrow(() ->
                new BusinessException("sql查询错误")) > 0) {
            return true;
        }
        return false;
    }

    @Method(desc = "判断资源是否存在",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.判断资源是否存在")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "无限制")
    @Param(name = "resource_type", desc = "资源类型", range = "无限制")
    @Return(desc = "资源是否存在标志", range = "true代表存在，false代表不存在")
    public static boolean isEtlResourceExist(String etl_sys_cd, String resource_type) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.判断资源是否存在
        if (Dbo.queryNumber("SELECT count(1) FROM " + Etl_resource.TableName + " WHERE resource_type=?"
                + " AND etl_sys_cd=?", resource_type, etl_sys_cd)
                .orElseThrow(() -> new BusinessException("sql查询错误！")) > 0) {
            return true;
        }
        return false;
    }

    @Method(desc = "判断当前表信息是否存在",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限验证" +
                    "2.判断作业名称与上游作业名称是否相同，相同则不能依赖" +
                    "3.判断当前工程对应作业依赖作业是否存在")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
    @Param(name = "pre_etl_sys_cd", desc = "上游工程编号", range = "目前与工程编号相同（因为暂无工程之间依赖）")
    @Param(name = "etl_job", desc = "作业名称", range = "新增作业时生成")
    @Param(name = "pre_etl_job", desc = "作业名称", range = "无限制")
    @Return(desc = "当前工程对应作业依赖作业是否存在标志", range = "true代表存在，false代表不存在")
    public static boolean isEtlDependencyExist(String etl_sys_cd, String pre_etl_sys_cd, String etl_job,
                                               String pre_etl_job) {
        // 1.数据可访问权限处理方式，该方法不需要权限验证
        // 2.判断作业名称与上游作业名称是否相同，相同则不能依赖
        if (etl_job.equals(pre_etl_job)) {
            throw new BusinessException("作业名称与上游作业名称相同不能依赖！");
        }
        // 3.判断当前工程对应作业依赖作业是否存在
        if (Dbo.queryNumber("select count(*) from " + Etl_dependency.TableName + " where etl_sys_cd=?" +
                        " And etl_job=? AND pre_etl_sys_cd=? AND pre_etl_job=?", etl_sys_cd, etl_job,
                pre_etl_sys_cd, pre_etl_job).orElseThrow(() -> new BusinessException("sql查询错误")) > 0) {
            return true;
        }
        return false;
    }

    @Method(desc = "判断作业系统参数变量名称是否已存在",
            logicStep = "1.数据可访问权限处理方式，此方法不需要权限认证" +
                    "2.判断作业系统参数变量名称是否已存在")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
    @Param(name = "para_cd", desc = "变量名称", range = "新增作业系统参数时生成")
    @Return(desc = "作业系统参数变量名称是否已存在标志", range = "true代表存在，false代表不存在")
    public static boolean isEtlParaExist(String etl_sys_cd, String para_cd) {
        // 1.数据可访问权限处理方式，此方法不需要权限认证
        // 2.判断作业系统参数变量名称是否已存在
        if (Dbo.queryNumber("select count(*) from " + Etl_para.TableName + " where etl_sys_cd=? " +
                " AND para_cd=?", etl_sys_cd, para_cd)
                .orElseThrow(() -> new BusinessException("sql查询错误！")) > 0) {
            return true;
        }
        return false;
    }

}
