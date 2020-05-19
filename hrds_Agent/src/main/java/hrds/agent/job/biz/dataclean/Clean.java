package hrds.agent.job.biz.dataclean;

import hrds.commons.entity.Column_split;
import org.apache.parquet.example.data.Group;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Clean {

	private DataCleanInterface allclean;
	private final Map<String, Map<String, String>> deleSpecialSpace;
	private final Map<String, Map<String, Column_split>> splitIng;
	private final Map<String, String> strFilling;

	private final Map<String, String> dating;
	private final Map<String, String> codeIng;
	private final Map<String, String> Triming;
	Map<String, Map<Integer, String>> ordering;

	@SuppressWarnings("unchecked")
	public Clean(Map<String, Object> parseJson, DataCleanInterface allclean) {

		this.allclean = allclean;
		deleSpecialSpace = (Map<String, Map<String, String>>) parseJson.get("deleSpecialSpace");//字符替换
		splitIng = (Map<String, Map<String, Column_split>>) parseJson.get("splitIng");//字段拆分
		strFilling = (Map<String, String>) parseJson.get("strFilling");//字符补齐
		dating = (Map<String, String>) parseJson.get("dating");//日期格式化
		codeIng = (Map<String, String>) parseJson.get("codeIng");//字段码值处理
		Triming = (Map<String, String>) parseJson.get("Triming");//字段trim处理
		ordering = (Map<String, Map<Integer, String>>) parseJson.get("ordering");//字段清洗顺序
	}

	/**
	 * @param columndata : 列的数据
	 * @param columnname : 列名称
	 * @param group      : 组信息
	 * @param type       : 字段类型
	 * @param fileType   : 文件的数据类型(目前四种格式:CSV,ORC,SEQUENCE,PARQUET),后面需要转换为当前文件写入的具体数据类型
	 * @param list
	 * @return
	 */
	public String cleanColumn(String columndata, String columnname, Group group, String type, String fileType,
	                          List<Object> list, String database_code, String database_separatorr) {

		if (!ordering.isEmpty()) {
			Map<Integer, String> colMap = ordering.get(columnname.toUpperCase());
			for (int i = colMap.size(); i >= 1; i--) {
				switch (colMap.get(i)) {
					case "2":
						columndata = allclean.replace(deleSpecialSpace, columndata, columnname);//字符替换
						break;
					case "1":
						columndata = allclean.filling(strFilling, columndata, columnname);//字符补齐
						break;
					case "4":
						columndata = allclean.codeTrans(codeIng, columndata, columnname);//码值转换
						break;
					case "6":
						columndata = allclean.split(splitIng, columndata, columnname, group, type, fileType,
								list, database_code, database_separatorr);//字段拆分;
						break;
					case "7":
						columndata = allclean.trim(Triming, columndata, columnname);//去空
						break;
					case "3":
						columndata = allclean.dateing(dating, columndata, columnname);//日期转换
						break;
				}
			}
		}
		return columndata;
	}
}
