package hrds.agent.job.biz.dataclean.tableclean;

import org.apache.parquet.example.data.Group;

import java.util.List;
import java.util.Map;

/**
 * ClassName: AbstractTableClean <br/>
 * Function: 数据库直连采集表清洗规则接口适配器 <br/>
 * Reason: 抽象类中提供接口中所有抽象方法的空实现，请子类继承抽象类后按功能点给出方法的具体实现
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public abstract class AbstractTableClean implements TableCleanInterface {

    @Override
    public String replace(Map<String, String> replaceMap , String columnValue){
        throw new IllegalStateException("这是一个空实现");
    }

    @Override
    public String complete(StringBuilder completeSB, String columnValue){
        throw new IllegalStateException("这是一个空实现");
    }

    @Override
    public String trim(Boolean flag, String columnValue){
        throw new IllegalStateException("这是一个空实现");
    }

    @Override
    public String merge(Map<String, String> mergeRule, String[] columnsValue, String[] columnsName, Group group, List<Object> lineData, String fileType){
        throw new IllegalStateException("这是一个空实现");
    }
}
