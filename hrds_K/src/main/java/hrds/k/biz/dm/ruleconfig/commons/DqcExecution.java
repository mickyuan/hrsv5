package hrds.k.biz.dm.ruleconfig.commons;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.util.Dbo;
import hrds.commons.codes.*;
import hrds.commons.collection.LoadingData;
import hrds.commons.collection.ProcessingData;
import hrds.commons.collection.bean.LoadingDataBean;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.BeanUtils;
import hrds.commons.utils.Constant;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.k.biz.dm.ruleconfig.bean.SysVarCheckBean;
import hrds.k.biz.utils.CheckBeanUtil;

import java.text.SimpleDateFormat;
import java.util.*;

@DocClass(desc = "数据管控-数据质量执行用到的类", author = "BY-HLL", createdate = "2020/4/10 0010 上午 10:41")
public class DqcExecution {

    @Method(desc = "执行规则调用方法", logicStep = "执行规则调用方法")
    @Param(name = "dq_definition", desc = "Dq_definition实体", range = "Dq_definition实体", isBean = true)
    @Param(name = "verify_date", desc = "验证日期", range = "String类型")
    @Param(name = "beans", desc = "List<SysVarCheckBean>", range = "自定义bean类型")
    @Param(name = "exec_method", desc = "执行方式（DqcExecMode代码项）", range = "DqcExecMode代码项")
    @Return(desc = "执行任务id", range = "执行任务id")
    public static long executionRule(Dq_definition dq_definition, String verify_date, Set<SysVarCheckBean> beans,
                                     String exec_method) {
        //数据校验
        if (StringUtil.isBlank(dq_definition.getReg_num().toString())) {
            throw new BusinessException("执行规则时,规则编号为空!");
        }
        if (StringUtil.isBlank(exec_method)) {
            throw new BusinessException("执行规则时,执行方式为空!");
        }
        //初始化数据质量规则级别为警告
        String dq_rule_level = EdRuleLevel.JingGao.getCode();
        //是否有例外情况(规则校验出现异常)
        boolean has_exception = Boolean.FALSE;
        //初始化检测记录日志信息
        Dq_result dq_result = new Dq_result();
        //初始化操作db
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            //设置检查结果
            try {
                //获取定义的数据质量规则级别
                dq_rule_level = dq_definition.getFlags();
                //将原始通用属性从原始bean中复制到目标bean中
                BeanUtils.copyProperties(dq_definition, dq_result);
                dq_result.setTask_id(PrimayKeyGener.getNextId());
                dq_result.setVerify_date(verify_date);
                dq_result.setStart_date(DateUtil.getSysDate());
                dq_result.setStart_time(DateUtil.getSysTime());
                dq_result.setExec_mode(exec_method);
                dq_result.setVerify_result(DqcVerifyResult.ZhengChang.getCode());
                dq_result.setVerify_sql(dq_definition.getSpecify_sql());
                dq_result.setErr_dtl_sql(dq_definition.getErr_data_sql());
                dq_result.setDl_stat(DqcDlStat.ZhengChang.getCode());
                //执行开始时间
                long execution_start_time = System.currentTimeMillis();
                //获取指定的验证sql
                String[] verify_sql = dq_result.getVerify_sql().split(";");
                //不在范围内的记录数
                String index1_sql;
                //检查总记录数
                String index2_sql = "";
                if (verify_sql.length == 1) {
                    index1_sql = verify_sql[0].trim();
                } else if (verify_sql.length == 2) {
                    index1_sql = verify_sql[0].trim();
                    index2_sql = verify_sql[1].trim();
                } else {
                    throw new BusinessException("请填写检查sql!");
                }
                //问题明细sql
                String index3_sql = dq_result.getErr_dtl_sql().trim();
                //替换系统变量
                for (SysVarCheckBean bean : beans) {
                    index1_sql = index1_sql.replace(bean.getName(), bean.getValue());
                    index2_sql = index2_sql.replace(bean.getName(), bean.getValue());
                    index3_sql = index3_sql.replace(bean.getName(), bean.getValue());
                }
                //运行sql:不在范围内的记录数
                Map<String, Object> map_result_1 = new HashMap<>();
                if (StringUtil.isNotBlank(index1_sql) && IsFlag.Shi.getCode().equals(dq_definition.getIs_saveindex1())) {
                    new ProcessingData() {

                        @Override
                        public void dealLine(Map<String, Object> map) {
                            map_result_1.putAll(map);
                        }
                    }.getDataLayer(index1_sql, db);
                }
                //运行sql:检查总记录数
                Map<String, Object> map_result_2 = new HashMap<>();
                if (StringUtil.isNotBlank(index2_sql) && IsFlag.Shi.getCode().equals(dq_definition.getIs_saveindex2())) {
                    new ProcessingData() {

                        @Override
                        public void dealLine(Map<String, Object> map) {
                            map_result_2.putAll(map);
                        }
                    }.getDataLayer(index2_sql, db);
                }
                //设置运行结束日期,时间
                dq_result.setEnd_date(DateUtil.getSysDate());
                dq_result.setEnd_time(DateUtil.getSysTime());
                //设置耗时
                long execution_end_time = System.currentTimeMillis();
                dq_result.setElapsed_ms((int) (execution_end_time - execution_start_time));
                //处理sql运行结果
                dq_result.setCheck_index1(map_result_1.get("index1").toString());
                dq_result.setCheck_index2(map_result_2.get("index2").toString());
            } catch (Exception e) {
                has_exception = Boolean.TRUE;
                //规则级别为严重,且发生了异常
                dq_result.setVerify_result(DqcVerifyResult.YiChang.getCode());
                dq_result.setDl_stat(DqcDlStat.DengDaiChuLi.getCode());
                dq_result.setErrno(e.getMessage());
                //如果规则级别为'严重'，则规则结果为'执行失败'
                if (EdRuleLevel.YanZhong.getCode().equals(dq_rule_level)) {
                    dq_result.setVerify_result(DqcVerifyResult.ZhiXingShiBai.getCode());
                }
            }
            //保存检查结果
            dq_result.add(db);
            db.commit();
            //如果规则级别是严重且发生了异常，则在记录异常信息后异常退出
            if (EdRuleLevel.YanZhong.getCode().equals(dq_rule_level) && has_exception) {
                //如果是手工执行抛异常
                if (DqcExecMode.ShouGong.getCode().equals(exec_method)) {
                    throw new BusinessException("规则级别为严重，且发生了异常");
                } else {
                    //作业调度执行,规则级别为严重，且发生了异常，退出
                    System.exit(-1);
                }
            }
            //执行到此处则代表程序正常运行结束，开始记录指标3的数据
            if (IsFlag.Shi.getCode().equals(dq_result.getIs_saveindex3())
                    && StringUtil.isNotBlank(dq_result.getErr_dtl_sql())) {
                recordIndicator3Data(dq_result, beans);
            }
        }
        return dq_result.getTask_id();
    }

    @Method(desc = "指定SQL（校验SQL）的系统变量对应结果bean",
            logicStep = "指定SQL（校验SQL）的系统变量对应结果bean")
    @Param(name = "dq_definition", desc = "Dq_definition实体", range = "Dq_definition实体")
    @Return(desc = "对应结果bean的List", range = "对应结果bean的List")
    public static Set<SysVarCheckBean> getSysVarCheckBean(Dq_definition dqd) {
        //获取变量信息
        List<Dq_sys_cfg> dq_sys_cfg_s = Dbo.queryList(Dq_sys_cfg.class, "select * from " + Dq_sys_cfg.TableName);
        //转换变量信息为map
        Map<String, String> dq_sys_cfg_map = new HashMap<>();
        dq_sys_cfg_s.forEach(dq_sys_cfg -> dq_sys_cfg_map.put(dq_sys_cfg.getVar_name(), dq_sys_cfg.getVar_value()));
        dq_sys_cfg_map.put("#{TX_DATE}", DateUtil.getSysDate());
        dq_sys_cfg_map.put("#{TX_DATE10}", DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString());
        //初始化系统变量检查bean的集合
        Set<SysVarCheckBean> sysVarCheckBeans = new HashSet<>();
        //根据正则表达式提取bean中的value值
        List<String> str_s = CheckBeanUtil.getBeanValueWithPattern(dqd, "(?=#\\{)(.*?)(?>\\})");
        str_s.forEach(str -> {
            SysVarCheckBean scb = new SysVarCheckBean();
            if (dq_sys_cfg_map.containsKey(str)) {
                scb.setName(str);
                scb.setIsEff(IsFlag.Shi.getValue());
                scb.setValue(dq_sys_cfg_map.get(str));
            } else {
                scb.setName(str);
                scb.setIsEff(IsFlag.Fou.getValue());
                scb.setValue(str);
            }
            sysVarCheckBeans.add(scb);
        });
        return sysVarCheckBeans;
    }

    @Method(desc = "根据规则编号获取有效依赖作业",
            logicStep = "根据规则编号获取有效依赖作业")
    @Param(name = "db", desc = "DatabaseWrapper连接信息", range = "DatabaseWrapper类型")
    @Param(name = "dq_definition", desc = "Dq_definition实体", range = "Dq_definition实体")
    @Return(desc = "有效依赖作业List", range = "有效依赖作业List")
    public static List<Map<String, Object>> getEffDepJobs(Dq_definition dq_definition) {
        if (StringUtil.isBlank(dq_definition.getReg_num().toString())) {
            throw new BusinessException("规则编号为空!");
        }
        //初始化执行sql
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("SELECT etl_sys_cd,etl_job,job_eff_flag,job_disp_status FROM " + Etl_job_def.TableName + " WHERE" +
                " job_eff_flag = ?").addParam(Job_Effective_Flag.YES.getCode());
        asmSql.addLikeParam("etl_job", '%' + dq_definition.getReg_num().toString() + '%');
        asmSql.addSql("UNION");
        asmSql.addSql("SELECT etl_sys_cd,etl_job,job_eff_flag,job_disp_status FROM " + Etl_job_cur.TableName + " WHERE" +
                " job_eff_flag = ?").addParam(Job_Effective_Flag.YES.getCode());
        asmSql.addLikeParam("etl_job", '%' + dq_definition.getReg_num().toString() + '%');
        asmSql.addSql("UNION");
        asmSql.addSql("SELECT etl_sys_cd,etl_job,job_eff_flag,job_disp_status FROM " + Etl_job_disp_his.TableName +
                " WHERE job_eff_flag = ?").addParam(Job_Effective_Flag.YES.getCode());
        asmSql.addLikeParam("etl_job", '%' + dq_definition.getReg_num().toString() + '%');
        return Dbo.queryList(asmSql.sql(), asmSql.params());
    }


    @Method(desc = "获取校验的任务编号",
            logicStep = "获取校验的任务编号" +
                    "生成规则-[A/M][yyyyMMddHHMMSS][XYZ][NUM]" +
                    "[A/M]:A为自动跑批，M为前台触发" +
                    "[yyyyMMddHHMMSS] 为日期时间" +
                    "[XYZ]毫秒数" +
                    "[NUM]3位随机数")
    @Param(name = "job_method", desc = "枚举，执行标识（JobMethod枚举）", range = "A:自动跑批,M:前台触发")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static String getRuleTaskId(String job_method) {
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        String time = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(date);
        //随机一个三位数
        int random = (int) (Math.random() * 900) + 100;
        //返回任务id
        return job_method + time + random;
    }

    @Method(desc = "记录指标3的数据", logicStep = "记录指标3的数据")
    @Param(name = "dq_definition", desc = "Dq_definition实体", range = "Dq_definition实体")
    @Param(name = "beans", desc = "List<SysVarCheckBean>", range = "自定义bean类型")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    private static boolean recordIndicator3Data(Dq_result dq_result, Set<SysVarCheckBean> beans) {
        //数据校验
        if (StringUtil.isBlank(dq_result.getTask_id().toString())) {
            throw new BusinessException("在记录指标3的数据时，传入的任务编号为空!");
        }
        //保存指标3的数据到对应数据表的存储层下
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            //指标3的sql
            String sql = dq_result.getErr_dtl_sql();
            //替换系统变量
            for (SysVarCheckBean bean : beans) {
                sql = sql.replace(bean.getName(), bean.getValue());
            }
            //设置检查结果表名,检查表名+当前时间+当前日期
            String dqc_table_name = Constant.DQC_TABLE + dq_result.getTask_id();
            //设置数据加载实体Bean
            LoadingDataBean ldbbean = new LoadingDataBean();
            ldbbean.setTableName(dqc_table_name);
            //插入指标3检查数据到存储层下,和查询的表存储层一致
            long dsl_id;
            try {
                dsl_id = new LoadingData(ldbbean).intoDataLayer(sql, db);
            } catch (Exception e) {
                throw new BusinessException("在插入指标3的全量数据记录信息时失败，任务编号为：" + dq_result.getTask_id());
            }
            //记录指标3检测结果表元信息
            Dq_index3record dq_index3record = new Dq_index3record();
            dq_index3record.setRecord_id(PrimayKeyGener.getNextId());
            dq_index3record.setTable_name(dqc_table_name);
            dq_index3record.setTable_col(dq_result.getTarget_key_fields());
            dq_index3record.setTable_size("0");
            dq_index3record.setDqc_ts("");
            dq_index3record.setFile_type("");
            dq_index3record.setFile_path("");
            dq_index3record.setRecord_date(DateUtil.getSysDate());
            dq_index3record.setRecord_time(DateUtil.getSysTime());
            dq_index3record.setTask_id(dq_result.getTask_id());
            dq_index3record.setDsl_id(dsl_id);
            dq_index3record.add(db);
            //提交数据库操作
            db.commit();
        }
        return true;
    }

    @Method(desc = "执行sql检查", logicStep = "执行sql检查")
    @Param(name = "beans", desc = "List<SysVarCheckBean>", range = "自定义Bean的集合")
    @Param(name = "sql", desc = "要检验的sql数组", range = "String类型可变长度数组")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static String executionSqlCheck(Set<SysVarCheckBean> beans, String... sql_s) {
        //数据校验
        if (StringUtil.isBlank(Arrays.toString(sql_s))) {
            throw new BusinessException("需要执行的sql为空!");
        }
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            for (String sql : sql_s) {
                //处理sql
                sql = sql.replace("\n", "");
                sql = sql.replace("\t", "");
                for (SysVarCheckBean bean : beans) {
                    sql = sql.replace(bean.getName(), bean.getValue());
                }
                //执行sql
                if (StringUtil.isBlank(sql)) {
                    throw new BusinessException("执行sql的时候,sql为空!");
                }
                try {
                    new ProcessingData() {
                        @Override
                        public void dealLine(Map<String, Object> map) {

                        }
                    }.getPageDataLayer(sql, db, 1, 10);
                } catch (Exception e) {
                    throw new BusinessException(e.getMessage());
                }
            }
        }
        return "success";
    }
}
