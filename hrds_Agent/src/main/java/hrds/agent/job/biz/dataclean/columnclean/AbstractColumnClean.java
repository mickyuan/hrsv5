package hrds.agent.job.biz.dataclean.columnclean;

import hrds.agent.job.biz.bean.ColumnSplitBean;
import org.apache.parquet.example.data.Group;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * ClassName: AbstractCleanRule <br/>
 * Function: 数据库直连采集列清洗规则接口适配器 <br/>
 * Reason: 抽象类中提供接口中所有抽象方法的空实现，请子类继承抽象类后按功能点给出方法的具体实现
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public abstract class AbstractColumnClean implements ColumnCleanInterface {

    @Override
    public String replace(Map<String, String> replaceMap , String columnValue){
        throw new IllegalStateException("这是一个空实现");
    }

    @Override
    public String complete(StringBuilder completeSB, String columnValue){
        throw new IllegalStateException("这是一个空实现");
    }

    @Override
    public String dateConver(StringBuilder dateSB, String columnValue) throws ParseException {
        throw new IllegalStateException("这是一个空实现");
    }

    @Override
    public String CVConver(Map<String, String> ruleMap, String columnValue){
        throw new IllegalStateException("这是一个空实现");
    }

    @Override
    public String trim(Boolean flag, String columnValue){
        throw new IllegalStateException("这是一个空实现");
    }

    @Override
    public String split(List<ColumnSplitBean> rule, String columnValue, String columnName, Group group, String colType, String fileType, List<Object> lineData){
        throw new IllegalStateException("这是一个空实现");
    }
}
