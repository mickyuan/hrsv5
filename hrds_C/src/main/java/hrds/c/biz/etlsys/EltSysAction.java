package hrds.c.biz.etlsys;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.resultset.Result;
import fd.ng.web.util.Dbo;
import hrds.c.biz.util.ETLJobUtil;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.Job_Status;
import hrds.commons.codes.Main_Server_Sync;
import hrds.commons.codes.Pro_Type;
import hrds.commons.entity.Etl_resource;
import hrds.commons.entity.Etl_sys;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.DboExecute;
import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@DocClass(desc = "作业调度工程", author = "dhw", createdate = "2019/11/25 15:48")
public class EltSysAction extends BaseAction {

    @Method(desc = "查询作业调度工程信息",
            logicStep = "1.数据可访问权限处理方式，根据user_id进行权限控制" +
                    "2.查询工程信息")
    @Return(desc = "返回工程信息", range = "无限制")
    public Result searchEtlSys() {
        // 1.数据可访问权限处理方式，根据user_id进行权限控制
        // 2.查询工程信息
        return Dbo.queryResult("select etl_sys_cd,etl_sys_name,comments,curr_bath_date,sys_run_status from "
                + Etl_sys.TableName + " where user_id=? order by etl_sys_cd", getUserId());
    }

    @Method(desc = "根据工程编号查询作业调度工程信息",
            logicStep = "1.数据可访问权限处理方式，根据user_id进行权限控制" +
                    "2.验证当前工程是否存在" +
                    "3.根据工程编号查询工程信息" +
                    "4.判断remarks是否为空，不为空则分割获取部署工程的redis ip与port并封装数据返回")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
    @Return(desc = "返回根据工程编号查询的工程信息", range = "无限制")
    public Map<String, Object> searchEtlSysById(String etl_sys_cd) {
        // 1.数据可访问权限处理方式，根据user_id进行权限控制
        // 2.验证当前工程是否存在
        if (!ETLJobUtil.isEtlSysExist(etl_sys_cd, getUserId())) {
            throw new BusinessException("当前工程已不存在，可能被删除！");
        }
        // 3.根据工程编号查询工程信息
        Map<String, Object> etlSys = Dbo.queryOneObject("select etl_sys_cd,etl_sys_name,comments," +
                "etl_serv_ip,user_name,user_pwd,serv_file_path,remarks from " + Etl_sys.TableName +
                " where user_id=? and etl_sys_cd=? order by etl_sys_cd", getUserId(), etl_sys_cd);
        // 4.判断remarks是否为空，不为空则分割获取部署工程的redis ip与port并封装数据返回
        String remarks = String.valueOf(etlSys.get("remarks"));
        if (StringUtils.isNotBlank(remarks)) {
            String[] ip_port = remarks.split(":");
            etlSys.put("redisIp", ip_port[0]);
            etlSys.put("redisPort", ip_port[1]);
        }
        return etlSys;
    }

    @Method(desc = "新增保存作业调度工程信息",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.检查作业调度工程字段合法性" +
                    "3.设置一些默认参数" +
                    "4.检查工程编号是否已存在，存在不能新增" +
                    "5.新增保存工程" +
                    "6.每个工程创建时,都默认创建2种资源类型" +
                    "7.遍历获取资源类型" +
                    "8.循环新增保存资源")
    @Param(name = "etl_sys", desc = "作业调度工程登记表", range = "与数据库对应表规则一致", isBean = true)
    public void addEtlSys(Etl_sys etl_sys) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.检查作业调度工程字段合法性
        checkEtlSysField(etl_sys.getEtl_sys_cd(), etl_sys.getEtl_sys_name());
        // 3.设置一些默认参数
        etl_sys.setUser_id(getUserId());
        etl_sys.setBath_shift_time(DateUtil.getSysDate());
        etl_sys.setMain_serv_sync(Main_Server_Sync.YES.getCode());
        etl_sys.setSys_run_status(Job_Status.STOP.getCode());
        // 跑批日期默认为当天
        etl_sys.setCurr_bath_date(DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString());
        // 4.检查工程编号是否已存在，存在不能新增,这里user_id传值为空，因为不管是什么用户工程编号都不能为空
        if (ETLJobUtil.isEtlSysExist(etl_sys.getEtl_sys_cd(), null)) {
            throw new BusinessException("工程编号已存在，不能新增！");
        }
        // 5.新增保存工程
        etl_sys.add(Dbo.db());
        // 6.每个工程创建时,都默认创建2种资源类型
        String[] paraType = {Pro_Type.Thrift.getCode(), Pro_Type.Yarn.getCode()};
        // 7.遍历获取资源类型
        for (String para_type : paraType) {
            Etl_resource resource = new Etl_resource();
            resource.setEtl_sys_cd(etl_sys.getEtl_sys_cd());
            resource.setResource_type(para_type);
            // 默认服务器同步标志位同步
            resource.setMain_serv_sync(Main_Server_Sync.YES.getCode());
            // 默认分配最大资源为10
            resource.setResource_max(10);
            // 8.循环新增保存资源
            resource.add(Dbo.db());
        }
    }

    @Method(desc = "检查作业调度工程字段合法性",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.验证工程名称是否为空" +
                    "3.验证工程编号是否为数字英文下划线")
    @Param(name = "etl_sys_cd", desc = "作业调度工程登记表主键ID", range = "新增工程时生成")
    @Param(name = "etl_sys_name", desc = "工程名称", range = "新增工程时生成")
    private void checkEtlSysField(String etl_sys_cd, String etl_sys_name) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.验证工程名称是否为空
        if (StringUtil.isBlank(etl_sys_name)) {
            throw new BusinessException("作业调度工程名不能为空！");
        }
        // 3.验证工程编号是否为数字英文下划线
        Pattern pattern = Pattern.compile("^[0-9a-zA-Z_]{1,}$");
        Matcher matcher = pattern.matcher(etl_sys_cd);
        if (!matcher.matches()) {
            throw new BusinessException("工程编号只能为数字英文下划线！");
        }
    }

    @Method(desc = "更新保存作业调度工程",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.更新保存工程")
    @Param(name = "etl_sys_cd", desc = "作业调度工程登记表主键ID", range = "新增工程时生成")
    @Param(name = "etl_sys_name", desc = "工程名称", range = "新增工程时生成")
    @Param(name = "comments", desc = "工程描述", range = "无限制")
    public void updateEtlSys(String etl_sys_cd, String etl_sys_name, String comments) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.更新保存工程
        DboExecute.updatesOrThrow("更新保存失败!", "update " + Etl_sys.TableName +
                        " set etl_sys_name=?,comments=? where etl_sys_cd=? and user_id=?",
                etl_sys_name, comments, etl_sys_cd, getUserId());

    }

    public void deployEtlJobScheduleProject(){

    }
}
