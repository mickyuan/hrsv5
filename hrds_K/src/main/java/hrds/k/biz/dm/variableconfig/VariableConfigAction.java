package hrds.k.biz.dm.variableconfig;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.core.utils.Validator;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.entity.Dq_sys_cfg;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.key.PrimayKeyGener;

import java.util.List;

@DocClass(desc = "数据管控-变量配置类", author = "BY-HLL", createdate = "2020/4/3 0003 下午 05:08")
public class VariableConfigAction extends BaseAction {

    @Method(desc = "添加变量配置数据信息", logicStep = "添加变量配置数据信息")
    @Param(name = "dq_sys_cfg", desc = "Dq_sys_cfg实体", range = "Dq_sys_cfg实体", isBean = true)
    public void addVariableConfigDat(Dq_sys_cfg dq_sys_cfg) {
        //数据校验
        Validator.notBlank(dq_sys_cfg.getVar_name(), "变量名为空!");
        Validator.notBlank(dq_sys_cfg.getVar_value(), "变量值为空!");
        if (checkVarNameIsRepeat(dq_sys_cfg.getVar_name())) {
            throw new BusinessException("变量名重复!");
        }
        //设置属性
        dq_sys_cfg.setSys_var_id(PrimayKeyGener.getNextId());
        dq_sys_cfg.setApp_updt_dt(DateUtil.getSysDate());
        dq_sys_cfg.setApp_updt_ti(DateUtil.getSysTime());
        dq_sys_cfg.setUser_id(getUserId());
        //更新数据
        dq_sys_cfg.add(Dbo.db());
    }

    @Method(desc = "删除变量配置数据", logicStep = "删除变量配置数据")
    @Param(name = "sys_var_id_s", desc = "变量配置id数组", range = "long类型数组")
    public void deleteVariableConfigData(Long[] sys_var_id_s) {
        //初始化sql
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("delete from " + Dq_sys_cfg.TableName + " where user_id=?");
        asmSql.addParam(getUserId());
        asmSql.addORParam("sys_var_id ", sys_var_id_s);
        Dbo.execute(asmSql.sql(), asmSql.params());
    }

    @Method(desc = "修改变量配置数据信息", logicStep = "修改变量配置数据信息")
    @Param(name = "dq_sys_cfg", desc = "Dq_sys_cfg实体", range = "Dq_sys_cfg实体", isBean = true)
    public void updateVariableConfigData(Dq_sys_cfg dq_sys_cfg) {
        //数据校验
        Validator.notBlank(dq_sys_cfg.getSys_var_id().toString(), "变量id为空!");
        Validator.notBlank(dq_sys_cfg.getVar_name(), "变量名为空!");
        Validator.notBlank(dq_sys_cfg.getVar_value(), "变量值为空!");
        //设置属性
        dq_sys_cfg.setApp_updt_dt(DateUtil.getSysDate());
        dq_sys_cfg.setApp_updt_ti(DateUtil.getSysTime());
        dq_sys_cfg.setUser_id(getUserId());
        //更新数据
        dq_sys_cfg.update(Dbo.db());
    }

    @Method(desc = "获取所有变量配置数据信息", logicStep = "获取所有变量配置数据信息(变量不会有很多,这里不做分页)")
    @Return(desc = "变量配置数据信息", range = "变量配置数据信息")
    public List<Dq_sys_cfg> getVariableConfigDataInfos() {
        return Dbo.queryList(Dq_sys_cfg.class, "SELECT * FROM " + Dq_sys_cfg.TableName + " where user_id=?", getUserId());
    }

    @Method(desc = "获取变量配置数据信息", logicStep = "获取变量配置数据信息")
    @Param(name = "sys_var_id", desc = "变量配置id", range = "long类型")
    @Return(desc = "变量配置数据信息", range = "变量配置数据信息")
    public Dq_sys_cfg getVariableConfigDataInfo(long sys_var_id) {
        return Dbo.queryOneObject(Dq_sys_cfg.class, "SELECT * FROM " + Dq_sys_cfg.TableName + " where sys_var_id=?",
                sys_var_id).orElseThrow(() -> new BusinessException("检查变量名称否重复的SQL编写错误"));
    }

    @Method(desc = "搜索变量信息", logicStep = "搜索变量信息")
    @Param(name = "var_name", desc = "变量名", range = "String类型", nullable = true)
    @Param(name = "var_value", desc = "变量值", range = "String类型", nullable = true)
    @Param(name = "start_date", desc = "检索开始日期", range = "String类型", nullable = true)
    @Param(name = "end_date", desc = "检索结束日期", range = "String类型", nullable = true)
    @Return(desc = "变量配置数据信息", range = "变量配置数据信息")
    public List<Dq_sys_cfg> searchVariableConfigData(String var_name, String var_value, String start_date,
                                                     String end_date) {
        //初始化sql
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("select * from " + Dq_sys_cfg.TableName + " where user_id = ?");
        asmSql.addParam(getUserId());
        if (StringUtil.isNotBlank(var_name)) {
            asmSql.addLikeParam(" var_name", '%' + var_name + '%');
        }
        if (StringUtil.isNotBlank(var_value)) {
            asmSql.addLikeParam("var_value", '%' + var_value + '%');
        }
        if (StringUtil.isNotBlank(start_date)) {
            asmSql.addSql(" and app_updt_dt >= ?").addParam(start_date);
        }
        if (StringUtil.isNotBlank(end_date)) {
            asmSql.addSql(" and app_updt_dt <= ?").addParam(end_date);
        }
        return Dbo.queryList(Dq_sys_cfg.class, asmSql.sql(), asmSql.params());
    }

    @Method(desc = "检查变量名是否存在", logicStep = "检查变量名是否存在")
    @Param(name = "var_name", desc = "变量名", range = "String类型，该值唯一")
    @Return(desc = "变量名是否存在", range = "true：存在，false：不存在")
    private boolean checkVarNameIsRepeat(String var_name) {
        return Dbo.queryNumber("select count(var_name) count from " + Dq_sys_cfg.TableName + " WHERE var_name =?",
                var_name).orElseThrow(() -> new BusinessException("检查变量名称否重复的SQL编写错误")) != 0;
    }

}
