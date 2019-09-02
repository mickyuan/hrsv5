package hrds.agent.job.biz.dataclean.tableclean;

import org.apache.parquet.example.data.Group;

import java.util.List;
import java.util.Map;

/*
* 表清洗规则接口，定义了按表清洗的4种清洗规则
* */
public interface TableCleanInterface {

    /**
     * 字符替换
     * @param replaceMap {@link Map<String, String>}
     * 		key : 原字符串  value : 新字符串
     * @param columnValue {@link String} 列值
     * @return {@link String} 清洗后的内容
     */
    public String replace(Map<String, String> replaceMap, String columnValue);

    /**
     * 字符补齐
     * @param completeSB {@link StringBuilder}
     * 		格式为：补齐长度^补齐方式^要补齐的字符串
     * @param columnValue {@link String} 列值
     * @return {@link String} 清洗后的内容
     */
    public String complete(StringBuilder completeSB, String columnValue);

    /**
     * 首尾去空
     * @param flag
     * @param columnValue
     * @return
     */
    public String trim(Boolean flag, String columnValue);

    /**
     * 列合并
     * @param mergeRule 列合并规则
     * @param columnsValue 所有进行过字段清洗，表清洗(除列合并)的列的值
     * @param columnsName 所有列的列名
     * @param group 用于写Parquet文件
     * @param lineData 用于写ORC文件
     * @param fileType 卸数落地数据文件的格式
     * @return
     */
    public String merge(Map<String, String> mergeRule, String[] columnsValue, String[] columnsName, Group group, List<Object> lineData, String fileType);
}
