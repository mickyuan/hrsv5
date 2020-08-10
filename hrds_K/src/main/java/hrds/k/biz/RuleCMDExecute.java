package hrds.k.biz;

import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.codes.DqcExecMode;
import hrds.commons.entity.Dq_definition;
import hrds.commons.exception.BusinessException;
import hrds.k.biz.dm.ruleconfig.bean.SysVarCheckBean;
import hrds.k.biz.dm.ruleconfig.commons.DqcExecution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

public class RuleCMDExecute {

    private static final Logger logger = LogManager.getLogger();

    /**
     * @param args 主程序入口，获取参数调用规则检测后台
     *             参数1：任务ID
     *             参数2：跑批日期
     */
    public static void main(String[] args) {
        if (args == null) {
            logger.error("必要参数不能为空!");
            logger.error("必须参数：参数1：规则ID；参数2：验证日期");
            System.exit(-1);
        }
        if (args.length < 2) {
            logger.error("必要参数不合法!");
            logger.error("必须参数：参数1：规则ID；参数2：验证日期");
            System.exit(-1);
        }
        //设置参数信息
        String reg_num = args[0];
        String verify_date = args[1];
        //触发执行方法
        logger.info("规则编号: " + reg_num + ",执行时间: " + verify_date);
        long result_num = run(reg_num, verify_date);
        logger.info("规则结果编号: " + result_num);
    }


    /**
     * 规则检测运行方法
     *
     * @param reg_num     规则编号
     * @param verify_date 验证日期
     */
    public static long run(String reg_num, String verify_date) {
        Dq_definition dq_definition = null;
        Set<SysVarCheckBean> beans = null;
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            //获取规则配置信息
            Dq_definition dqd = new Dq_definition();
            dqd.setReg_num(reg_num);
            dq_definition = SqlOperator.queryOneObject(db, Dq_definition.class, "SELECT * FROM "
                    + Dq_definition.TableName + " " + "WHERE reg_num=?", dqd.getReg_num()).orElseThrow(()
                    -> (new BusinessException("获取配置信息的SQL失败!")));
            //系统变量对应结果
            beans = DqcExecution.getSysVarCheckBean(db, dq_definition);
            if (!beans.isEmpty()) {
                logger.info("规则依赖参数信息:" + JsonUtil.toJson(beans));
            }
        } catch (NullPointerException e) {
            if (null == dq_definition) {
                logger.error("根据规则编号: " + reg_num + " 没有找到对应规则!");
                System.exit(2);
            }
        }
        //执行规则,返回执行的任务id
        return DqcExecution.executionRule(dq_definition, verify_date, beans, DqcExecMode.ZiDong.getCode());
    }
}
