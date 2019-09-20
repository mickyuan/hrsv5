package hrds.agent.job.biz.dataclean.columnclean;

import hrds.agent.job.biz.bean.ColumnSplitBean;
import org.apache.parquet.example.data.Group;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/*
 * 列清洗规则接口，定义了按列清洗的6种清洗规则
 * */
public interface ColumnCleanInterface {

	/**
	* @Description:  字符替换
	* @Param: [replaceMap : key : 原字符串  value : 新字符串, 取值范围 : Map<String, String>]
	* @Param: [columnValue : 列值, 取值范围 : String]
	* @return: java.lang.String 替换后的内容
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
	* @Description:  日期转换
	* @Param: [dateSB : 用于日期转换,格式为：新格式`老格式, 取值范围 : StringBuilder]
	* @Param: [columnValue : 列值, 取值范围 : String]
	* @return: java.lang.String
	* @Author: WangZhengcheng
	* @Date: 2019/9/11
	*/
	String dateConver(StringBuilder dateSB, String columnValue) throws ParseException;

	/**
	* @Description:  码值转换
	* @Param: [ruleMap : 用于码值转换的map,  key : searchString  value : replacement, 取值范围 : Map<String, String> ]
	* @Param: [columnValue : 列值, 取值范围 : String]
	* @return: java.lang.String
	* @Author: WangZhengcheng
	* @Date: 2019/9/11
	*/
	String CVConver(Map<String, String> ruleMap, String columnValue);

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
	* @Description:  字段拆分
	* @Param: [rule : 字段拆分规则, 取值范围 : ColumnSplitBean类对象]
	* @Param: [columnValue : 列值, 取值范围 : String]
	* @Param: [columnName : 列名, 取值范围 : String]
	* @Param: [group : 用于写Parquet, 取值范围 : org.apache.parquet.example.data.Group对象]
	* @Param: [colType : 列类型, 取值范围 : String]
	* @Param: [fileType : 卸数落地数据文件的格式, 取值范围 : String]
	* @Param: [lineData : 用于写ORC, 取值范围 : List]
	* @return: java.lang.String
	* @Author: WangZhengcheng
	* @Date: 2019/9/11
	*/
	String split(List<ColumnSplitBean> rule, String columnValue, String columnName,
	             Group group, String colType, String fileType, List<Object> lineData);
}
