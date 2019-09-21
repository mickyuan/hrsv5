package hrds.agent.job.biz.dataclean.tableclean;

import org.apache.parquet.example.data.Group;

import java.util.List;
import java.util.Map;

/*
 * 表清洗规则接口，定义了按表清洗的4种清洗规则
 * */
public interface TableCleanInterface {

	/**
	* @Description: 字符替换
	* @Param: [replaceMap : key : 原字符串  value : 新字符串, 取值范围 : Map<String, String>]
	* @Param: [columnValue : 列值, 取值范围 : String]
	* @return: java.lang.String
	* @Author: WangZhengcheng
	* @Date: 2019/9/11
	*/
	String replace(Map<String, String> replaceMap, String columnValue);

	/**
	 * @Description:  字符补齐
	 * @Param: [completeSB : 用于字符补齐, 格式为：补齐长度`补齐方式`要补齐的字符串, 取值范围 : StringBuilder]
	 * @Param: [columnValue : 列值, 取值范围 : String]
	 * @return: java.lang.String 补齐后的内容
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/11
	 */
	String complete(StringBuilder completeSB, String columnValue);

	/**
	 * @Description:  首尾去空
	 * @Param: [flag : 是否进行首尾去空, 取值范围 : true(进行去空) false(不进行去空)]
	 * @Param: [columnValue : 列值, 取值范围 : String]
	 * @return: java.lang.String
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/11
	 */
	String trim(Boolean flag, String columnValue);

	/**
	* @Description: 列合并
	* @Param: [mergeRule : 列合并规则, 取值范围 : Map<String, String>]
	* @Param: [columnsValue : 要合并的列的值, 取值范围 : String[]]
	* @Param: [columnsName : 要合并的列名, 取值范围 : String[]]
	* @Param: [group : 用于写Parquet, 取值范围 : org.apache.parquet.example.data.Group对象]
	* @Param: [lineData : 用于写ORC, 取值范围 : List]
	* @Param: [fileType : 落地文件格式(CSV， PARQUET， ORC,SEQUENCE), 取值范围 : String]
	* @return: java.lang.String
	* @Author: WangZhengcheng
	* @Date: 2019/9/11
	*/
	String merge(Map<String, String> mergeRule, String[] columnsValue,
	             String[] columnsName, Group group, List<Object> lineData, String fileType);
}
