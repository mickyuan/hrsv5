package hrds.commons.utils.etl;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.conf.WebinfoConf;
import fd.ng.web.util.Dbo;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.Constant;
import hrds.commons.utils.PinyinUtil;
import hrds.commons.utils.PropertyParaValue;

import java.io.File;
import java.util.List;

@DocClass(desc = "作业调度工具类", author = "BY-HLL", createdate = "2020/4/8 0008 下午 01:38")
public class EtlJobUtil {

    //工程名
    private static final String ETL_SYS_CD = "HYREN";
    //海云数服相关作业
    private static final String ETL_SYS_NAME = "海云数服相关作业";
    //资源类型
    private static final String RESOURCE_TYPE = "XS_ZT";
    //最大资源数
    private static final String RESOURCE_MAX = "5";
    //调度跑批日期
    private static final String BATCH_DATE = "#{txdate}";
    //数据管控-质量管理常量
    public static final String QUALITY_MANAGE = "quality_manage";
    public static final String MARTPRONAME = "datamart.sh";

    @Method(desc = "保存作业", logicStep = "保存作业")
    public static void saveJob() {
        saveJob("", DataSourceType.DCL, ETL_SYS_CD, "", AgentType.FTP);
        saveJob("", DataSourceType.DCL, ETL_SYS_CD, "", AgentType.DuiXiang);
        saveJob("", DataSourceType.DCL, ETL_SYS_CD, "", AgentType.WenJianXiTong);
        saveJob("", DataSourceType.DCL, ETL_SYS_CD, "", AgentType.DBWenJian);
        saveJob("", DataSourceType.DCL, ETL_SYS_CD, "", AgentType.ShuJuKu);
        saveJob("", DataSourceType.DPL, ETL_SYS_CD, "", null);
        saveJob("", DataSourceType.DML, ETL_SYS_CD, "", null);
    }

    @Method(desc = "保存作业",
            logicStep = "保存作业")
    @Param(name = "pkId", desc = "作业id", range = "long类型,该值唯一")
    @Param(name = "dataSourceType", desc = "DataSourceType数据源类型", range = "DataSourceType类型")
    @Param(name = "etl_sys_cd", desc = "工程id", range = "String类型")
    @Param(name = "sub_sys_cd", desc = "任务id", range = "String类型")
    @Param(name = "collectType", desc = "采集类型", range = "String类型")
    public static int saveJob(String pkId, DataSourceType dataSourceType, String etl_sys_cd, String sub_sys_cd,
                              AgentType agentType) {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            //初始化查询sql
            SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
            //DCL层
            if (dataSourceType == DataSourceType.DCL) {
                if (agentType == AgentType.FTP) {
                    throw new BusinessException("暂未实现!");
                }
                if (agentType == AgentType.DuiXiang) {
                    throw new BusinessException("暂未实现!");
                }
                if (agentType == AgentType.WenJianXiTong) {
                    throw new BusinessException("暂未实现!");
                }
                if (agentType == AgentType.ShuJuKu || agentType == AgentType.DBWenJian) {
                    throw new BusinessException("暂未实现!");
                }
            }
            //DPL层
            if (dataSourceType == DataSourceType.DPL) {
                throw new BusinessException("暂未实现!");
            }
            //DML层
            if (dataSourceType == DataSourceType.DML) {
                asmSql.clean();
                Dm_datatable dm_datatable = new Dm_datatable();
                dm_datatable.setDatatable_id(pkId);
                asmSql.addSql("SELECT * FROM " + Dm_datatable.TableName + " WHERE datatable_id = ?");
                asmSql.addParam(dm_datatable.getDatatable_id());
                dm_datatable = Dbo.queryOneObject(Dm_datatable.class, asmSql.sql(), asmSql.params())
                        .orElseThrow(() -> (new BusinessException("获取规则校验信息的SQL出错!")));
                //TODO
                //sub_name 和 sub_sys_desc 有啥意义？
                String sub_name = sub_sys_cd;
                String sub_sys_desc = sub_sys_cd;
                String etl_job = dm_datatable.getDatatable_en_name();
                String etl_job_desc = dm_datatable.getDatatable_en_name();
                String param = pkId + "@" + BATCH_DATE;
                jobCommonMethod(sub_name, sub_sys_desc, etl_job, etl_job_desc, param, dataSourceType, MARTPRONAME,
                        etl_sys_cd, sub_sys_cd, db);
//                throw new BusinessException("暂未实现!");
            }
            //DQC
            if (dataSourceType == DataSourceType.DQC) {
                asmSql.clean();
                Dq_definition dq_definition = new Dq_definition();
                dq_definition.setReg_num(pkId);
                //获取规则配置信息
                asmSql.addSql("SELECT * FROM " + Dq_definition.TableName + " WHERE reg_num = ?");
                asmSql.addParam(dq_definition.getReg_num());
                Dq_definition dq_definition_rs = Dbo.queryOneObject(Dq_definition.class, asmSql.sql(), asmSql.params())
                        .orElseThrow(() -> (new BusinessException("获取规则校验信息的SQL出错!")));
                String target_tab = dq_definition_rs.getTarget_tab();
                String sub_name = dq_definition_rs.getReg_name();
                String ct_name = dq_definition_rs.getReg_name();
                String etl_job = dq_definition_rs.getReg_num().toString();
                String param = etl_job + "@" + BATCH_DATE;
                //获取变量配置信息
                asmSql.clean();
                asmSql.addSql("SELECT * FROM " + Dq_sys_cfg.TableName);
                List<Dq_sys_cfg> dq_sys_cfg_s = Dbo.queryList(Dq_sys_cfg.class, asmSql.sql(), asmSql.params());
                for (Dq_sys_cfg cfg : dq_sys_cfg_s) {
                    if (StringUtil.isNotBlank(target_tab)) {
                        target_tab = target_tab.replace(cfg.getVar_name(), cfg.getVar_value());
                    }
                    if (StringUtil.isNotBlank(sub_name)) {
                        sub_name = sub_name.replace(cfg.getVar_name(), cfg.getVar_value());
                    }
                    if (StringUtil.isNotBlank(ct_name)) {
                        ct_name = ct_name.replace(cfg.getVar_name(), cfg.getVar_value());
                    }
                }
                jobCommonMethod(target_tab, sub_name, etl_job, ct_name, param, dataSourceType, QUALITY_MANAGE,
                        etl_sys_cd, sub_sys_cd, db);
            }
            //提交数据库操作
            db.commit();
            //保存成功返回值为0
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            //保存失败返回值为-1
            return -1;
        }
    }

    @Method(desc = "作业公共方法", logicStep = "作业公共方法")
    @Param(name = "sub_name", desc = "任务名称", range = "数据采集(任务编号) 数据加工(工程名称) 集市加工(集市名称)")
    @Param(name = "sub_sys_desc", desc = "任务描述", range = "数据采集(任务名) 数据加工(空) 集市加工(空)")
    @Param(name = "etl_job", desc = "作业名", range = "数据采集(表名) 数据加工(作业名) 集市集市(数据表表名) 数据管控(id)")
    @Param(name = "etl_job_desc", desc = "作业描述",
            range = "数据采集(表中文名) 数据加工(模型表的中文描述) 集市加工(数据表中文名) 数据管控(名称)")
    @Param(name = "param", desc = "作业参数",
            range = "数据采集(作业主键@作业名称@消息头@表名@#{txdate}) 数据加工(作业名@#{txdate}) 集市加工(作业参数)" +
                    " 数据管控(作业名@#{txdate})")
    @Param(name = "dataSourceType", desc = "DataSourceType数据源类型", range = "DataSourceType类型")
    @Param(name = "pro_name", desc = "作业程序名称", range = "String类型")
    @Param(name = "etl_sys_cd", desc = "工程id", range = "String类型")
    @Param(name = "sub_sys_cd", desc = "任务id", range = "String类型")
    @Param(name = "db", desc = "DatabaseWrapper连接信息", range = "DatabaseWrapper类型")
    public static void jobCommonMethod(String sub_name, String sub_sys_desc, String etl_job, String etl_job_desc,
                                       String param, DataSourceType dataSourceType, String pro_name, String etl_sys_cd,
                                       String sub_sys_cd, DatabaseWrapper db) {
        //创建工程
        createEtl_sys(db, etl_sys_cd);
        //作业程序目录
        String pro_dic = "!{HYSHELLBIN}";
        //DCL贴源层
        if (dataSourceType == DataSourceType.DCL) {
            Etl_sub_sys_list etl_sub_sys_list = new Etl_sub_sys_list();
            etl_sub_sys_list.setEtl_sys_cd(etl_sys_cd);
            etl_sub_sys_list.setSub_sys_cd(sub_name); //任务编号
            etl_sub_sys_list.setSub_sys_desc(sub_sys_desc);
            if (StringUtil.isBlank(sub_sys_cd)) {
                sub_sys_cd = createEtl_sub_sys_list(etl_sub_sys_list, db);
            }
            //创建作业
            createEtl_job_def(etl_sys_cd, sub_sys_cd, etl_job, etl_job_desc, param, pro_name, db, pro_dic);
        }
        //DPL加工层
        else if (dataSourceType == DataSourceType.DPL) {
            //获取中文任务名称的的首字母
            String subSysCd = new PinyinUtil().toFixPinYin(sub_name) + "_" + dataSourceType.getCode();
            Etl_sub_sys_list etl_sub_sys_list = new Etl_sub_sys_list();
            etl_sub_sys_list.setEtl_sys_cd(etl_sys_cd);
            etl_sub_sys_list.setSub_sys_cd(subSysCd);
            etl_sub_sys_list.setSub_sys_desc(sub_sys_desc);
            if (StringUtil.isBlank(sub_sys_cd)) {
                sub_sys_cd = createEtl_sub_sys_list(etl_sub_sys_list, db);
            }
            //创建作业
            createEtl_job_def(etl_sys_cd, sub_sys_cd, etl_job, etl_job_desc, param, pro_name, db, pro_dic);
        }
        //DML集市层
        else if (dataSourceType == DataSourceType.DML) {
            //获取中文任务名称的的首字母
            String subSysCd = new PinyinUtil().toFixPinYin(sub_name) + "_" + dataSourceType.getCode();
            Etl_sub_sys_list etl_sub_sys_list = new Etl_sub_sys_list();
            etl_sub_sys_list.setEtl_sys_cd(etl_sys_cd);
            etl_sub_sys_list.setSub_sys_cd(subSysCd);
            etl_sub_sys_list.setSub_sys_desc(sub_sys_desc);
            if (StringUtil.isBlank(sub_sys_cd)) {
                sub_sys_cd = createEtl_sub_sys_list(etl_sub_sys_list, db);
            }
            //创建作业
            createEtl_job_def(etl_sys_cd, sub_sys_cd, subSysCd + "_" + etl_job, etl_job_desc, param, pro_name, db, pro_dic);
        }
        //DQC管控层
        else if (dataSourceType == DataSourceType.DQC) {
            //获取中文任务名称的的首字母
            String subSysCd = new PinyinUtil().toFixPinYin(sub_name) + "_" + dataSourceType.getCode();
            Etl_sub_sys_list etl_sub_sys_list = new Etl_sub_sys_list();
            etl_sub_sys_list.setEtl_sys_cd(etl_sys_cd);
            etl_sub_sys_list.setSub_sys_cd(subSysCd);
            etl_sub_sys_list.setSub_sys_desc(sub_sys_desc);
            if (StringUtil.isBlank(sub_sys_cd)) {
                sub_sys_cd = createEtl_sub_sys_list(etl_sub_sys_list, db);
            }
            //TODO 此处的路径不应该写死在这里
            //pro_dic = "/opt/hyrenserv/server/tomcat7/webapps/K/";
            pro_dic = WebinfoConf.FileUpload_SavedDirName + File.separator + "pro_dic" + File.separator;
            //创建作业
            createEtl_job_def(etl_sys_cd, sub_sys_cd, subSysCd + "_" + etl_job, etl_job_desc, param, pro_name, db, pro_dic);
        }
    }

    @Method(desc = "创建作业", logicStep = "创建作业")
    @Param(name = "etl_sys_cd", desc = "工程id", range = "String类型")
    @Param(name = "sub_sys_cd", desc = "任务id", range = "String类型")
    @Param(name = "etl_job", desc = "作业名", range = "数据采集(表名) 数据加工(作业名) 集市集市(数据表表名) 数据管控(id)")
    @Param(name = "etl_job_desc", desc = "作业描述",
            range = "数据采集(表中文名) 数据加工(模型表的中文描述) 集市加工(数据表中文名) 数据管控(名称)")
    @Param(name = "param", desc = "作业参数", range = "数据采集(作业主键@作业名称@消息头@表名@#{txdate}) 数据加工(作业名@#{txdate})" +
            " 集市加工(作业参数) 数据管控(作业名@#{txdate})")
    @Param(name = "pro_name", desc = "作业程序名称", range = "String类型")
    @Param(name = "db", desc = "DatabaseWrapper连接信息", range = "DatabaseWrapper类型")
    @Param(name = "pro_dic", desc = "作业程序目录", range = "String类型")
    private static void createEtl_job_def(String etl_sys_cd, String subSysCd, String etl_job, String etl_job_desc,
                                          String param, String Pro_name, DatabaseWrapper db, String pro_dic) {

        Etl_job_def etl_job_def = new Etl_job_def();
        //工程代码
        etl_job_def.setEtl_sys_cd(etl_sys_cd);
        //作业名
        etl_job_def.setEtl_job(etl_job);
        //任务编号
        etl_job_def.setSub_sys_cd(subSysCd);
        //作业描述
        etl_job_def.setEtl_job_desc(etl_job_desc);
        //作业程序类型
        etl_job_def.setPro_type(Pro_Type.SHELL.getCode());
        //作业程序目录
        etl_job_def.setPro_dic(pro_dic);
        //作业程序名称
        etl_job_def.setPro_name(Pro_name);
        //作业程序参数
        etl_job_def.setPro_para(param);
        //日志目录
        etl_job_def.setLog_dic("!{HYLOG}@" + BATCH_DATE + "@!{HYXX}");
        //调度频率
        etl_job_def.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
        //调度位移时间
        etl_job_def.setDisp_offset("0");
        //调度触发方式
        etl_job_def.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
        //作业有效标志
        etl_job_def.setJob_eff_flag(Job_Effective_Flag.YES.getCode());
        //作业优先级
        etl_job_def.setJob_priority("0");
        //是否当天调度
        etl_job_def.setToday_disp(Today_Dispatch_Flag.YES.getCode());
        etl_job_def.setMain_serv_sync(Main_Server_Sync.YES.getCode());
        //初始化查询sql
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("select count(*) from etl_job_def where etl_sys_cd = ? and etl_job = ?");
        asmSql.addParam(etl_job_def.getEtl_sys_cd());
        asmSql.addParam(etl_job_def.getEtl_job());
        //校验工程作业是否存在,不存在则创建
        if (Dbo.queryNumber(db, asmSql.sql(), asmSql.params()).orElseThrow(() -> (new BusinessException(
                "检查工程作业定义信息的SQL错误!"))) == 1) {
            throw new BusinessException("作业已经存在于该工程!");
        } else {
            etl_job_def.add(db);
            Etl_job_resource_rela etl_job_resource_rela = new Etl_job_resource_rela();
            etl_job_resource_rela.setEtl_sys_cd(etl_sys_cd);
            etl_job_resource_rela.setEtl_job(etl_job);
            etl_job_resource_rela.setResource_type(RESOURCE_TYPE);
            etl_job_resource_rela.setResource_req(1);
            asmSql.clean();
            asmSql.addSql("select count(*) from " + Etl_job_resource_rela.TableName + " where etl_sys_cd = ? and etl_job = ?");
            asmSql.addParam(etl_job_resource_rela.getEtl_sys_cd());
            asmSql.addParam(etl_job_resource_rela.getEtl_job());
            //校验改工程作业的资源关系是否存在
            if (Dbo.queryNumber(db, asmSql.sql(), asmSql.params()).orElseThrow(() -> (new BusinessException(
                    "检查工程作业资源信息的SQL错误!"))) != 1) {
                etl_job_resource_rela.add(db);
            }
        }
    }

    @Method(desc = "添加任务",
            logicStep = "添加任务")
    @Param(name = "etl_sub_sys_list", desc = "Etl_sub_sys_list实体", range = "Etl_sub_sys_list实体对象")
    @Param(name = "db", desc = "DatabaseWrapper连接信息", range = "DatabaseWrapper类型")
    @Return(desc = "子系统代码", range = "String类型")
    private static String createEtl_sub_sys_list(Etl_sub_sys_list etl_sub_sys_list, DatabaseWrapper db) {

        if (StringUtil.isBlank(etl_sub_sys_list.getEtl_sys_cd())) {
            throw new BusinessException("工程代码为空!");
        }
        if (StringUtil.isBlank(etl_sub_sys_list.getSub_sys_cd())) {
            throw new BusinessException("子系统代码为空!");
        }
        //初始化查询sql
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("select count(1) count from etl_sub_sys_list where etl_sys_cd = ? and sub_sys_cd = ?");
        asmSql.addParam(etl_sub_sys_list.getEtl_sys_cd());
        asmSql.addParam(etl_sub_sys_list.getSub_sys_cd());
        if (Dbo.queryNumber(db, asmSql.sql(), asmSql.params()).orElseThrow(() -> (new BusinessException(
                "检查工程子系统定义信息的SQL错误!"))) != 1) {
            etl_sub_sys_list.add(db);
        }
        return etl_sub_sys_list.getSub_sys_cd();
    }

    @Method(desc = "判断工程是否存在，不存在则添加",
            logicStep = "判断工程是否存在，不存在则添加")
    @Param(name = "db", desc = "DatabaseWrapper连接信息", range = "DatabaseWrapper类型")
    @Param(name = "etl_sys_cd", desc = "工程id", range = "String类型")
    private static void createEtl_sys(DatabaseWrapper db, String etl_sys_cd) {
        //初始化sql
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        //判断工程是否是HYREN
        if (ETL_SYS_CD.equals(etl_sys_cd)) {
            asmSql.clean();
            asmSql.addSql("select count(1) count from etl_sys where etl_sys_cd = ?");
            asmSql.addParam(etl_sys_cd);
            //判断已经存在,不存在则添加
            if (Dbo.queryNumber(db, asmSql.sql(), asmSql.params()).orElseThrow(() -> (new BusinessException(
                    "检查工程是否存在的SQL错误!"))) != 1) {
                //添加作业工程登记表信息
                asmSql.clean();
                Etl_sys etl_sys = new Etl_sys();
                etl_sys.setEtl_sys_cd(etl_sys_cd);
                etl_sys.setEtl_sys_name(ETL_SYS_NAME);
                etl_sys.setEtl_serv_ip(PropertyParaValue.getString("hyren_host", ""));
                etl_sys.setEtl_serv_port(PropertyParaValue.getString("hyren_port", ""));
                etl_sys.setMain_serv_sync(Main_Server_Sync.YES.toString());
                etl_sys.setSys_run_status(Job_Status.STOP.toString());
                etl_sys.setCurr_bath_date(DateUtil.getSysDate());
                etl_sys.add(db);
                //添加参数登记信息(默认的)
                asmSql.clean();
                Etl_para etl_para = new Etl_para();
                etl_para.setEtl_sys_cd(etl_sys_cd);
                etl_para.setPara_cd("!HYSHELLBIN");
                //TODO 此处的路径不应该写死在这里
                etl_para.setPara_val(WebinfoConf.FileUpload_SavedDirName + File.separator + "pro_dic" + File.separator);
                etl_para.setPara_type("url");
                etl_para.add(db);
                etl_para.setEtl_sys_cd(etl_sys_cd);
                etl_para.setPara_cd("!HYXX");
                etl_para.setPara_val("/");
                etl_para.setPara_type("url");
                etl_para.add(db);
                etl_para.setEtl_sys_cd(etl_sys_cd);
                etl_para.setPara_cd("!HYLOG");
                //TODO 此处的路径不应该写死在这里
                etl_para.setPara_val(WebinfoConf.FileUpload_SavedDirName + File.separator + "etllog");
                etl_para.setPara_type("url");
                etl_para.add(db);
                //添加资源登记信息
                Etl_resource etl_resource = new Etl_resource();
                etl_resource.setEtl_sys_cd(etl_sys_cd);
                etl_resource.setResource_type(RESOURCE_TYPE);
                etl_resource.setResource_max(RESOURCE_MAX);
                etl_resource.setMain_serv_sync(Main_Server_Sync.YES.toString());
                etl_resource.add(db);
            }
        }
        //不是HYREN
        else {
            //添加参数登记信息(默认的)
            Etl_para etl_para = new Etl_para();
            etl_para.setEtl_sys_cd(etl_sys_cd);
            etl_para.setPara_cd("!HYSHELLBIN");
            etl_para.setPara_val(WebinfoConf.FileUpload_SavedDirName + File.separator + "pro_dic" + File.separator);
            etl_para.setPara_type("url");
            asmSql.clean();
            asmSql.addSql("select count(*) from etl_para where etl_sys_cd = ? and para_cd = ?");
            asmSql.addParam(etl_sys_cd);
            asmSql.addParam(etl_para.getPara_cd());
            if (Dbo.queryNumber(db, asmSql.sql(), asmSql.params()).orElseThrow(() -> (new BusinessException(
                    "检查参数登记信息的SQL错误!"))) != 1) {
                etl_para.add(db);
            }
            etl_para.setEtl_sys_cd(etl_sys_cd);
            etl_para.setPara_cd("!HYXX");
            etl_para.setPara_val("/");
            etl_para.setPara_type("url");
            asmSql.cleanParams();
            asmSql.addParam(etl_sys_cd);
            asmSql.addParam(etl_para.getPara_cd());
            if (Dbo.queryNumber(db, asmSql.sql(), asmSql.params()).orElseThrow(() -> (new BusinessException(
                    "检查参数登记信息的SQL错误!"))) != 1) {
                etl_para.add(db);
            }
            etl_para.setEtl_sys_cd(etl_sys_cd);
            etl_para.setPara_cd("!HYLOG");
            etl_para.setPara_val("/data4/hyrendata/logs/hrdsagent/etllog/");
            etl_para.setPara_type("url");
            asmSql.cleanParams();
            asmSql.addParam(etl_sys_cd);
            asmSql.addParam(etl_para.getPara_cd());
            if (Dbo.queryNumber(db, asmSql.sql(), asmSql.params()).orElseThrow(() -> (new BusinessException(
                    "检查参数登记信息的SQL错误!"))) != 1) {
                etl_para.add(db);
            }
            //添加资源登记信息
            asmSql.clean();
            Etl_resource etl_resource = new Etl_resource();
            etl_resource.setEtl_sys_cd(etl_sys_cd);
            etl_resource.setResource_type(RESOURCE_TYPE);
            etl_resource.setResource_max(RESOURCE_MAX);
            etl_resource.setMain_serv_sync(Main_Server_Sync.YES.getCode());
            asmSql.addSql("select count(*) from etl_resource where etl_sys_cd = ? and resource_type = ?");
            asmSql.addParam(etl_sys_cd);
            asmSql.addParam(etl_resource.getResource_type());
            if (Dbo.queryNumber(db, asmSql.sql(), asmSql.params()).orElseThrow(() -> (new BusinessException(
                    "检查参数登记信息的SQL错误!"))) != 1) {
                etl_resource.add(db);
            }
        }
    }

    @Method(desc = "获取作业工程信息", logicStep = "获取作业工程信息")
    @Return(desc = "作业工程信息", range = "作业工程信息")
    @Param(name = "user_id", desc = "用户ID", range = "新增用户时生成")
    public static List<Etl_sys> getProInfo(long user_id) {
        return Dbo.queryList(Etl_sys.class,
                "select * from " + Etl_sys.TableName + " where user_id=? order by etl_sys_cd",
                user_id);
    }

    @Method(desc = "获取作业某个工程下的任务信息",
            logicStep = "获取作业某个工程下的任务信息")
    @Param(name = "etl_sys_cd", desc = "工程代码", range = "String类型")
    @Return(desc = "工程下的任务信息", range = "工程下的任务信息")
    public static List<Etl_sub_sys_list> getTaskInfo(String etl_sys_cd) {
        return Dbo.queryList(Etl_sub_sys_list.class, "select * from " + Etl_sub_sys_list.TableName + " where" +
                " etl_sys_cd =? order by sub_sys_cd", etl_sys_cd);
    }

    @Method(desc = "对程序作业的作业系统参数经行检查添加", logicStep = "1: 检查当前的作业系统参数是否存在" +
            "2: 如果不存在则添加")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "不能为空")
    @Param(name = "para_cd", desc = "工程系统参数变量名称", range = "不能为空")
    @Param(name = "pro_val", desc = "工程系统参数变量值", range = "不能为空")
    public static void setDefaultEtlParaConf(String etl_sys_cd, String para_cd, String pro_val) {
        // 1: 检查当前的作业系统参数是否存在
        long resourceNum = Dbo.queryNumber(
                "SELECT COUNT(1) FROM " + Etl_para.TableName
                        + " WHERE etl_sys_cd = ? AND para_cd = ?",
                etl_sys_cd, para_cd)
                .orElseThrow(() -> new BusinessException("SQL查询错误"));
        // 2: 如果不存在则添加
        if (resourceNum == 0) {
            Etl_para etl_para = new Etl_para();
            etl_para.setEtl_sys_cd(etl_sys_cd);
            etl_para.setPara_cd(para_cd);
            etl_para.setPara_val(pro_val);
            etl_para.setPara_type(ParamType.LuJing.getCode());
            etl_para.add(Dbo.db());
        }
    }

    @Method(desc = "设置资源登记信息", logicStep = "1.查询资源类型是否存在" +
            "2.不存在默认增加一个资源")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "不可为空")
    public static void setDefaultEtlResource(String etl_sys_cd) {
        // 1.查询资源类型是否存在
        long resourceNum = Dbo.queryNumber(
                "SELECT COUNT(1) FROM " + Etl_resource.TableName
                        + " WHERE resource_type = ? AND etl_sys_cd = ?",
                Constant.RESOURCE_THRESHOLD, etl_sys_cd)
                .orElseThrow(() -> new BusinessException("SQL查询错误"));
        // 2.不存在默认增加一个资源
        if (resourceNum == 0) {
            Etl_resource etl_resource = new Etl_resource();
            etl_resource.setEtl_sys_cd(etl_sys_cd);
            etl_resource.setResource_type(Constant.RESOURCE_THRESHOLD);
            etl_resource.setResource_max(Constant.RESOURCE_NUM);
            etl_resource.setMain_serv_sync(Main_Server_Sync.YES.getCode());
            etl_resource.add(Dbo.db());
        }
    }

    @Method(desc = "获取作业信息", logicStep = "")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "不可为空")
    @Param(name = "sub_sys_cd", desc = "任务编号", range = "不可为空")
    @Return(desc = "返回作业定义下的作业名称集合", range = "可以为空.为空表示没有作业信息存在")
    public static List<String> getEtlJob(String etl_sys_cd, String sub_sys_cd) {
        return Dbo.queryOneColumnList(
                "SELECT etl_job FROM " + Etl_job_def.TableName + " WHERE etl_sys_cd = ? AND sub_sys_cd = ?",
                etl_sys_cd, sub_sys_cd);
    }

    @Method(desc = "获取作业资源分配的作业信息", logicStep = "")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "不可为空")
    @Return(desc = "返回作业资源下的作业名称集合", range = "可以为空.为空表示没有作业资源信息存在")
    public static List<String> getJobResource(String etl_sys_cd) {
        return Dbo.queryOneColumnList(
                "SELECT etl_job FROM " + Etl_job_resource_rela.TableName + " WHERE etl_sys_cd = ? ",
                etl_sys_cd);
    }

    @Method(desc = "保存作业所需的资源信息", logicStep = "1: 判断当前的作业信息是否存在,如果不存在则添加")
    @Param(name = "etl_sys_cd", desc = "作业工程编号", range = "不可为空")
    @Param(name = "etl_job_def", desc = "作业资源的信息集合", range = "不可为空", isBean = true)
    @Param(name = "jobResource", desc = "作业资源的信息名称集合", range = "可为空")
    public static void setEtl_job_resource_rela(
            String etl_sys_cd, Etl_job_def etl_job_def, List<String> jobResource) {
        //    1: 判断当前的作业信息是否存在,如果不存在则添加
        if (!jobResource.contains(etl_job_def.getEtl_job())) {
            Etl_job_resource_rela etl_job_resource_rela = new Etl_job_resource_rela();
            etl_job_resource_rela.setEtl_sys_cd(etl_sys_cd);
            etl_job_resource_rela.setEtl_job(etl_job_def.getEtl_job());
            etl_job_resource_rela.setResource_type(Constant.RESOURCE_THRESHOLD);
            etl_job_resource_rela.setResource_req(Constant.JOB_RESOURCE_NUM);
            etl_job_resource_rela.add(Dbo.db());
        }
    }

}
