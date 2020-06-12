package hrds.k.biz.dm;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.DqcVerifyResult;
import hrds.commons.entity.Data_store_layer;
import hrds.commons.entity.Dq_result;
import hrds.commons.entity.Dtab_relation_store;
import hrds.commons.exception.BusinessException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "数据管控-首页", author = "BY-HLL", createdate = "2020/3/27 0027 下午 04:29")
public class DataManageAction extends BaseAction {

    @Method(desc = "获取表统计信息", logicStep = "获取表统计信息")
    @Param(name = "statistics_layer_num", desc = "数据层数据表统计层数",
            range = "int类型值,为空默认为表最多的前6个存储层,为0或者小于0获取所有存储层", valueIfNull = "6")
    @Return(desc = "表统计信息Map", range = "表统计信息Map")
    public List<Map<String, Object>> getTableStatistics(int statistics_layer_num) {
        //初始化查询 SqlOperator
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        //设置查询sql
        asmSql.clean();
        asmSql.addSql("select dsl.dsl_id,dsl.dsl_name,count(dtrs.tab_id) from " + Data_store_layer.TableName + " dsl" +
                " left join " + Dtab_relation_store.TableName + " dtrs on dsl.dsl_id=dtrs.dsl_id group by dsl.dsl_id" +
                " order by count desc");
        //如果查询天数等于0或者小于0,则查询所有存储层统计信息
        if (statistics_layer_num > 0) {
            asmSql.addSql(" limit ?").addParam(statistics_layer_num);
        }
        //统计各存储层存储表的数量
        return Dbo.queryList(asmSql.sql(), asmSql.params());
    }


    @Method(desc = "获取各种规则统计结果", logicStep = "获取各种规则统计结果")
    @Return(desc = "规则统计结果Map", range = "规则统计结果Map")
    public Map<String, Object> getRuleStatistics() {
        //初始化规则统计结果
        Map<String, Object> ruleStatisticsMap = new HashMap<>();
        //初始化查询 SqlOperator
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        //检查通过数
        asmSql.clean();
        asmSql.addSql("SELECT COUNT(1) AS check_passes_number FROM " + Dq_result.TableName + " WHERE verify_result = ?")
                .addParam(DqcVerifyResult.ZhengChang.getCode());
        long check_passes_number = Dbo.queryNumber(asmSql.sql(), asmSql.params()).orElseThrow(() ->
                new BusinessException("统计检查通过数的SQL错误!"));
        ruleStatisticsMap.put("check_passes_number", String.valueOf(check_passes_number));
        //数据异常数
        asmSql.clean();
        asmSql.addSql("SELECT COUNT(1) AS check_exception_number FROM " + Dq_result.TableName + " WHERE verify_result = ?")
                .addParam(DqcVerifyResult.YiChang.getCode());
        long check_exception_number = Dbo.queryNumber(asmSql.sql(), asmSql.params()).orElseThrow(() ->
                new BusinessException("统计检查异常数的SQL错误!"));
        ruleStatisticsMap.put("check_exception_number", String.valueOf(check_exception_number));
        //执行失败数
        asmSql.clean();
        asmSql.addSql("SELECT COUNT(1) AS execution_failed_number FROM " + Dq_result.TableName + " WHERE verify_result = ?")
                .addParam(DqcVerifyResult.ZhiXingShiBai.getCode());
        long execution_failed_number = Dbo.queryNumber(asmSql.sql(), asmSql.params()).orElseThrow(() ->
                new BusinessException("统计执行失败数的SQL错误!"));
        ruleStatisticsMap.put("execution_failed_number", String.valueOf(execution_failed_number));
        //规则总数
        asmSql.clean();
        asmSql.addSql("SELECT COUNT(1) AS rule_total_number FROM " + Dq_result.TableName);
        long rule_total_number = Dbo.queryNumber(asmSql.sql(), asmSql.params()).orElseThrow(() ->
                new BusinessException("统计执行失败数的SQL错误!"));
        ruleStatisticsMap.put("rule_total_number", String.valueOf(rule_total_number));
        //检查通过top 5
        asmSql.clean();
        asmSql.addSql("SELECT task_id, verify_date, target_tab FROM " + Dq_result.TableName + " WHERE verify_result = ? " +
                "ORDER BY verify_date DESC limit 5").addParam(DqcVerifyResult.ZhengChang.getCode());
        List<Map<String, Object>> check_passes_top5 = Dbo.queryList(asmSql.sql(), asmSql.params());
        ruleStatisticsMap.put("check_passes_top5", check_passes_top5);
        //数据异常top 5
        asmSql.clean();
        asmSql.addSql("SELECT task_id, verify_date, target_tab FROM " + Dq_result.TableName + " WHERE verify_result = ? " +
                "ORDER BY verify_date DESC limit 5").addParam(DqcVerifyResult.YiChang.getCode());
        List<Map<String, Object>> check_exception_top5 = Dbo.queryList(asmSql.sql(), asmSql.params());
        ruleStatisticsMap.put("check_exception_top5", check_exception_top5);
        //执行失败top 5
        asmSql.clean();
        asmSql.addSql("SELECT task_id, verify_date, target_tab FROM " + Dq_result.TableName + " WHERE verify_result = ? " +
                "ORDER BY verify_date DESC limit 5").addParam(DqcVerifyResult.ZhiXingShiBai.getCode());
        List<Map<String, Object>> execution_failed_top5 = Dbo.queryList(asmSql.sql(), asmSql.params());
        ruleStatisticsMap.put("execution_failed_top5", execution_failed_top5);
        return ruleStatisticsMap;
    }
}
