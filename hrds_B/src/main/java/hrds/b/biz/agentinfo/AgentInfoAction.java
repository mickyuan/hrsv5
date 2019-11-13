package hrds.b.biz.agentinfo;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.AgentStatus;
import hrds.commons.codes.AgentType;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.DboExecute;
import hrds.commons.utils.key.PrimayKeyGener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "agent增删改查类", author = "dhw", createdate = "2019-9-23 10:32:16")
public class AgentInfoAction extends BaseAction {

    @Method(desc = "查询所有agent信息,agent页面展示",
            logicStep = "1.数据可访问权限处理方式，通过user_id关联进行权限控制" +
                    "2.验证此数据源是否还存在" +
                    "3.通过agent_info,agent_down_info,sys_user三张表关联查询所有类型agent信息" +
                    "4.创建存放agent信息的集合并封装不同类型agent信息" +
                    "5.将封装不同类型agent信息的集合返回")
    @Param(name = "source_id", desc = "data_source表主键，source_relation_dep表外键",
            range = "10位数字，新增时自动生成")
    @Param(name = "datasource_name", desc = "数据源名称", range = "不为空")
    @Return(desc = "存放封装不同类型agent信息的集合", range = "无限制")
    public Map<String, Object> searchDatasourceAndAgentInfo(long source_id, String datasource_name) {
        // 1.数据可访问权限处理方式，通过user_id关联进行权限控制
        // 2.验证此数据源是否还存在
        isDatasourceExist(source_id);
        // 3.通过agent_info,agent_down_info,sys_user三张表关联查询所有类型agent信息
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.addSql("select ai.agent_id,ai.source_id,ai.agent_name,ai.agent_ip,ai.agent_port,su.user_name," +
                "su.user_id,adi.deploy,(case adi.deploy when ? then 'yes' else 'no' end) agentStatus from "
                + Agent_info.TableName + " ai LEFT JOIN " + Agent_down_info.TableName +
                " adi ON ai.agent_ip = adi.agent_ip AND ai.agent_port = adi.agent_port " +
                " AND ai.agent_id=adi.agent_id left join " + Sys_user.TableName + " su on ai.user_id=su.user_id"
                + " where ai.source_id = ? and ai.agent_type = ? and ai.user_id=? order by ai.agent_id");
        asmSql.addParam(IsFlag.Shi.getCode());
        asmSql.addParam(source_id);
        asmSql.addParam(AgentType.ShuJuKu.getCode());
        asmSql.addParam(getUserId());
        List<Map<String, Object>> sjkAgentList = Dbo.queryList(asmSql.sql(), asmSql.params());
        asmSql.cleanParams();
        // 文件系统Agent
        asmSql.addParam(IsFlag.Shi.getCode());
        asmSql.addParam(source_id);
        asmSql.addParam(AgentType.WenJianXiTong.getCode());
        asmSql.addParam(getUserId());
        List<Map<String, Object>> fileSystemAgentList = Dbo.queryList(asmSql.sql(), asmSql.params());
        asmSql.cleanParams();
        // DB文件Agent
        asmSql.addParam(IsFlag.Shi.getCode());
        asmSql.addParam(source_id);
        asmSql.addParam(AgentType.DBWenJian.getCode());
        asmSql.addParam(getUserId());
        List<Map<String, Object>> dbWjAgentList = Dbo.queryList(asmSql.sql(), asmSql.params());
        asmSql.cleanParams();
        // 对象Agent
        asmSql.addParam(IsFlag.Shi.getCode());
        asmSql.addParam(source_id);
        asmSql.addParam(AgentType.DuiXiang.getCode());
        asmSql.addParam(getUserId());
        List<Map<String, Object>> dxAgentList = Dbo.queryList(asmSql.sql(), asmSql.params());
        asmSql.cleanParams();
        // FTP Agent
        asmSql.addParam(IsFlag.Shi.getCode());
        asmSql.addParam(source_id);
        asmSql.addParam(AgentType.FTP.getCode());
        asmSql.addParam(getUserId());
        List<Map<String, Object>> ftpAgentList = Dbo.queryList(asmSql.sql(), asmSql.params());
        // 4.创建存放agent信息的集合并封装不同类型agent信息
        Map<String, Object> map = new HashMap<>();
        map.put("sjkAgent", sjkAgentList);
        map.put("dbFileAgent", dbWjAgentList);
        map.put("fileSystemAgent", fileSystemAgentList);
        map.put("dxAgent", dxAgentList);
        map.put("ftpAgent", ftpAgentList);
        map.put("datasource_name", datasource_name);
        map.put("source_id", source_id);
        // 5.将封装不同类型agent信息的集合返回
        return map;
    }

    @Method(desc = "验证agent对应的数据源是否存在",
            logicStep = "1.数据可访问权限处理方式，通过user_id进行权限验证" +
                    "2.验证该agent对应的数据源是否还存在")
    @Param(name = "source_id", desc = "data_source表主键ID", range = "不为空，10位数字新增时生成")
    private void isDatasourceExist(long source_id) {
        // 1.数据可访问权限处理方式，通过user_id进行权限验证
        // 2.验证该agent对应的数据源是否还存在
        if (Dbo.queryNumber("select count(1) from " + Data_source.TableName + " where source_id = ? " +
                " and create_user_id=?", source_id, getUserId()).
                orElseThrow(() -> new BusinessException("sql查询错误！")) == 0) {
            throw new BusinessException("该agent对应的数据源已不存在，source_id=" + source_id);
        }
    }

    @Method(desc = "新增保存agent信息",
            logicStep = "1.数据可访问权限处理方式，新增时会设置创建用户ID，会获取当前用户ID，所以不需要权限验证" +
                    "2.字段合法性验证" +
                    "3.判断端口是否被占用，被占用抛异常，否则正常保存" +
                    "4.初始化AgentInfo的一些非页面传值" +
                    "5.检查数据源是否还存在以及判断数据源下相同的IP地址中是否包含相同的端口" +
                    "6.保存agent信息")
    @Param(name = "agentInfo", desc = "agent_info表实体对象", range = "与数据库agent_info表字段定义规则一致",
            isBean = true)
    public void saveAgent(Agent_info agentInfo) {
        // 1.数据可访问权限处理方式，新增时会设置创建用户ID，会获取当前用户ID，所以不需要权限验证
        // 2.字段合法性验证
        fieldLegalityValidation(agentInfo.getAgent_name(), agentInfo.getAgent_type(), agentInfo.getAgent_ip(),
                agentInfo.getAgent_port());
        // 3.判断端口是否被占用
        boolean flag = isPortOccupied(agentInfo.getAgent_ip(), Integer.parseInt(agentInfo.getAgent_port()));
        if (flag) {
            // 端口被占用，不可使用
            throw new BusinessException("端口被占用，agent_port=" + agentInfo.getAgent_port() + "," +
                    "agent_ip =" + agentInfo.getAgent_ip());
        }
        // 4.初始化AgentInfo的一些非页面传值
        agentInfo.setAgent_id(PrimayKeyGener.getNextId());
        agentInfo.setAgent_status(AgentStatus.WeiLianJie.getCode());
        agentInfo.setCreate_time(DateUtil.getSysTime());
        agentInfo.setCreate_date(DateUtil.getSysDate());
        // 5.检查数据源是否还存在以及判断数据源下相同的IP地址中是否包含相同的端口
        isDatasourceAndAgentExist(agentInfo.getSource_id(), agentInfo.getAgent_type(),
                agentInfo.getAgent_ip(), agentInfo.getAgent_port());
        // 6.保存agent信息
        agentInfo.add(Dbo.db());
    }

    @Method(desc = "检查数据源是否还存在以及数据源下相同的IP地址中是否包含相同的端口",
            logicStep = "1.数据可访问权限处理方式，这是一个私有方法，不会单独被调用，所以不需要权限验证" +
                    "2.验证数据源是否还存在,查到至少一条数据，查不到为0" +
                    "3.判断数据源下相同的IP地址中是否包含相同的端口,查到至少一条数据，查不到为0")
    @Param(name = "source_id", desc = "data_source表主键", range = "10位数字，新增时自动生成")
    @Param(name = "agent_type", desc = "agent类型", range = "使用agent类型代码项（agentType）")
    @Param(name = "agent_ip", desc = "agent所在服务器ip", range = "合法IP地址", example = "127.0.0.1")
    @Param(name = "agent_port", desc = "agent连接端口", range = "1024-65535")
    private void isDatasourceAndAgentExist(long source_id, String agent_type, String agent_ip, String agent_port) {
        // 1.数据可访问权限处理方式，这是一个私有方法，不会单独被调用，所以不需要权限验证
        // 2.验证数据源是否还存在,查到至少一条数据，查不到为0
        isDatasourceExist(source_id);
        // 3.判断数据源下相同的IP地址中是否包含相同的端口,查到至少一条数据，查不到为0
        if (Dbo.queryNumber("SELECT count(1) FROM " + Agent_info.TableName + " WHERE source_id=?" +
                        " AND agent_type=? AND agent_ip=? AND agent_port=?", source_id, agent_type, agent_ip,
                agent_port).orElseThrow(() -> new BusinessException("sql查询错误！")) > 0) {
            throw new BusinessException("该agent对应的数据源下相同的IP地址中包含相同的端口，" +
                    "source_id=" + source_id);
        }
    }

    @Method(desc = "更新agent信息并返回更新后的最新agent类型对应agent信息",
            logicStep = "1.数据可访问权限处理方式，通过关联agent_id与user_id检查" +
                    "2.字段合法性验证" +
                    "3.检查数据源是否还存在以及判断数据源下相同的IP地址中是否包含相同的端口" +
                    "4.创建agent_info实体对象，同时封装值" +
                    "5.更新agent信息")
    @Param(name = "agent_id", desc = "agent_info主键ID", range = "10位数字，新增时自动生成")
    @Param(name = "agent_name", desc = "data_source表主键", range = "10位数字，新增时自动生成")
    @Param(name = "agent_type", desc = "agent类型", range = "使用agent类型代码项（AgentType）")
    @Param(name = "agent_ip", desc = "agent所在服务器ip", range = "合法IP地址", example = "127.0.0.1")
    @Param(name = "agent_port", desc = "agent连接端口", range = "1024-65535")
    @Param(name = "source_id", desc = "agent_info表外键ID，data_source表主键ID,定义为Long目的是判null",
            range = "10位数字，新增时自动生成")
    @Param(name = "user_id", desc = "数据采集用户ID,定义为Long目的是判null", range = "四位数字，新增用户时自动生成")
    public void updateAgent(Long agent_id, String agent_name, String agent_type,
                            String agent_ip, String agent_port, long source_id,
                            long user_id) {
        // 1.数据可访问权限处理方式，通过关联agent_id与user_id检查
        if (Dbo.queryNumber("select count(1) from " + Agent_info.TableName + " where agent_id=?" +
                " and user_id=?", agent_id, getUserId()).orElseThrow(() ->
                new BusinessException("sql查询错误")) == 0) {
            throw new BusinessException("数据权限校验失败，数据不可访问！");
        }
        // 2.字段合法性验证
        fieldLegalityValidation(agent_name, agent_type, agent_ip, agent_port);
        // 3.检查数据源是否还存在以及判断数据源下相同的IP地址中是否包含相同的端口
        isDatasourceAndAgentExist(source_id, agent_type, agent_ip, agent_port);
        // 4.创建agent_info实体对象，同时封装值
        Agent_info agentInfo = new Agent_info();
        agentInfo.setAgent_id(agent_id);
        agentInfo.setUser_id(user_id);
        agentInfo.setSource_id(source_id);
        agentInfo.setAgent_ip(agent_ip);
        agentInfo.setAgent_port(agent_port);
        agentInfo.setAgent_type(agent_type);
        agentInfo.setAgent_name(agent_name);
        // 5.更新agent信息
        agentInfo.update(Dbo.db());
    }

    @Method(desc = "agent信息表字段合法性验证",
            logicStep = "1.数据可访问权限处理方式，这是个私有方法，不会单独被调用，所以不需要权限验证" +
                    "2.验证agent_type是否为空或空格" +
                    "3. 验证agent_name是否为空或空格" +
                    "4.判断agent_ip是否是一个合法的ip" +
                    "5.判断agent_port是否是一个有效的端口")
    @Param(name = "agent_name", desc = "agent名称", range = "10位数字，新增时自动生成")
    @Param(name = "agent_type", desc = "agent类型", range = "使用agent类型代码项（AgentType）")
    @Param(name = "agent_ip", desc = "agent所在服务器ip", range = "合法IP地址", example = "127.0.0.1")
    @Param(name = "agent_port", desc = "agent连接端口", range = "1024-65535")
    private void fieldLegalityValidation(String agent_name, String agent_type, String agent_ip,
                                         String agent_port) {
        // 1.数据可访问权限处理方式，这是个私有方法，不会单独被调用，所以不需要权限验证
        // 2.验证agent_type是否合法，不合法该方法会直接抛异常
        AgentType.ofEnumByCode(agent_type);
        // 3.验证agent_name是否为空或空格
        if (StringUtil.isBlank(agent_name)) {
            throw new BusinessException("agent_name不为空且不为空格，agent_name=" + agent_name);
        }
        // 4.判断agent_ip是否是一个合法的ip
        String[] split = agent_ip.split("\\.");
        for (String agentIp : split) {
            if (Integer.parseInt(agentIp) < 0 || Integer.parseInt(agentIp) > 255) {
                throw new BusinessException("agent_ip不是一个为空或空格的ip地址," +
                        "agent_ip=" + agent_ip);
            }
        }
        // 5.判断agent_port是否是一个有效的端口
        if (StringUtil.isBlank(agent_port) || Integer.parseInt(agent_port) < 1024 ||
                Integer.parseInt(agent_port) > 65535) {
            throw new BusinessException("agent_port端口不是有效的端口，不在取值范围内，agent_port=" + agent_port);
        }
    }

    @Method(desc = "监控agent端口是否被占用",
            logicStep = "1.数据可访问权限处理方式，这是一个私有方法，不会单独被调用，所以这里不需要做权限验证" +
                    "2.通过http方式去测试端口连通情况，测通则被占用，不通则可以使用")
    @Param(name = "agent_ip", desc = "agent所在服务器ip", range = "合法IP地址", example = "127.0.0.1")
    @Param(name = "agent_port", desc = "agent连接端口", range = "1024-65535")
    @Return(desc = "返回端口是否被占用信号", range = "false,true")
    private boolean isPortOccupied(String agent_ip, int agent_port) {
        // FIXME （后期移动到hrds-commons下）
        // 1.数据可访问权限处理方式，这是一个私有方法，不会单独被调用，所以这里不需要做权限验证
        // 2.通过http方式去测试端口连通情况，测通则被占用，不通则可以使用
        String url = "http://".concat(agent_ip).concat(":").concat(agent_port + "");
        HttpClient.ResponseValue post = new HttpClient().post(url);
        // 状态值
        int code = 200;
        if (post.getCode() != code) {
            // 未连通，端口可用
            return false;
        } else {
            // 连通，端口被使用中
            return true;
        }
    }

    @Method(desc = "根据agent ID以及agent类型查询Agent信息",
            logicStep = "1.数据可访问权限处理方式，根据user_id进行权限控制" +
                    "2.验证代码项是否存在" +
                    "3.根据agent_id与agent_type查询该agent信息")
    @Param(name = "agent_id", desc = "agent_info表主键", range = "10位数字，新增时生成")
    @Param(name = "agent_type", desc = "agent类型", range = "使用agent类别（AgentType）")
    @Return(desc = "返回根据agent_id与agent_type查询该agent_info信息集合", range = "无限制")
    public Map<String, Object> searchAgent(long agent_id, String agent_type) {
        // 1.数据可访问权限处理方式，根据user_id进行权限控制
        // 2.验证代码项是否存在
        AgentType.ofEnumByCode(agent_type);
        // 3.根据agent_id与agent_type查询该agent信息
        Map<String, Object> agentInfo = Dbo.queryOneObject("select ai.user_id,ai.agent_id,ai.source_id,"
                + " ai.agent_name,ai.agent_ip,ai.agent_port,su.user_name from " + Agent_info.TableName
                + " ai left join " + Sys_user.TableName + " su on ai.user_id=su.user_id where ai.agent_id=?"
                + " and ai.agent_type=? and ai.user_id=? order by ai.agent_id", agent_id, agent_type, getUserId());
        return agentInfo;
    }

    @Method(desc = "删除agent",
            logicStep = "1.数据可访问权限处理方式，以下SQL关联user_id检查" +
                    "2.删除前查询此agent是否已部署，已部署不能删除" +
                    "3.判断此数据源与agent下是否有任务，有任务不能删除" +
                    "4.删除agent")
    @Param(name = "source_id", desc = "data_source表主键，agent_info表外键", range = "10位数字，新增数据源时生成")
    @Param(name = "agent_id", desc = "agent_info表主键", range = "10位数字，新增agent时生成")
    @Param(name = "agent_type", desc = "agent类型", range = "使用agent类别（AgentType）")
    public void deleteAgent(long source_id, long agent_id, String agent_type) {
        // 1.数据可访问权限处理方式，通过agent_id,agent_type,user_id关联检查
        if (Dbo.queryNumber("select count(*) from " + Agent_info.TableName + " where agent_id=? " +
                "and user_id=? and agent_type=?", agent_id, getUserId(), agent_type)
                .orElseThrow(() -> new BusinessException("sql查询错误！")) == 0) {
            throw new BusinessException("数据可访问权限校验失败，数据不可访问");
        }
        // 2.删除前查询此agent是否已部署
        if (Dbo.queryNumber("select count(1) from " + Agent_down_info.TableName + " where agent_id=?",
                agent_id).orElseThrow(() -> new BusinessException("sql查询错误！")) > 0) {
            // 此agent已部署不能删除
            throw new BusinessException("此agent已部署不能删除");
        }
        // 3.判断此数据源与agent下是否有任务
        if (Dbo.queryNumber(" SELECT count(1) FROM " + Agent_info.TableName + " t1 join "
                + Database_set.TableName + " t2 on t1.agent_id=t2.agent_id WHERE  t1.agent_id=? " +
                " and t1.agent_type=?", agent_id, agent_type).
                orElseThrow(() -> new BusinessException("sql查询错误！")) > 0) {
            // 此数据源与agent下有任务，不能删除
            throw new BusinessException("此数据源对应的agent下有任务，不能删除");
        }
        // 4.删除agent
        DboExecute.deletesOrThrow("删除表信息失败，agent_id=" + agent_id + ",agent_type=" + agent_type,
                "delete from " + Agent_info.TableName + " where agent_id=?", agent_id);
    }

}

