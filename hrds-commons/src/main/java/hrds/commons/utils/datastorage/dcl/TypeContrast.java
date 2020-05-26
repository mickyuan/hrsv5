package hrds.commons.utils.datastorage.dcl;

import fd.ng.core.annotation.DocClass;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.entity.Data_store_layer;
import hrds.commons.entity.Type_contrast;
import hrds.commons.entity.Type_contrast_sum;
import hrds.commons.utils.datastorage.yamldata.YamlDataFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "存储层数据类型对照实体", author = "Mr.Lee", createdate = "2020-01-13 15:45")
public class TypeContrast implements YamlDataFormat {

  /**
   * prefix : 前缀名称
   */
  public static final String PREFIX = "typecontrast";
  /**
   * name : 类型对照名称
   */
  public static final String NAME = "NAME";

  @Override
  public Map<String, List<Map<String, Object>>> yamlDataFormat() {

	Map<String, Map<String, Object>> contrastMap = new LinkedHashMap<>();
	getTypeContrast()
		.forEach(
			item -> {
			  String dtcs_name = ((String) item.get("dsl_name"));
			  String source_type = ((String) item.get("source_type")).toUpperCase();
			  Object target_type = ((String) item.get("target_type")).toUpperCase();

			  if (contrastMap.containsKey(dtcs_name)) {
				contrastMap.get(dtcs_name).put(source_type, target_type);
			  } else {
				Map<String, Object> map = new LinkedHashMap<>();
				map.put(NAME, dtcs_name);
				map.put(source_type, target_type);
				contrastMap.put(dtcs_name, map);
			  }
			});

	List<Map<String, Object>> typeContrastAll = new ArrayList<>();
	contrastMap.forEach(
		(dtcs_name, typeContrast) -> {
		  typeContrastAll.add(typeContrast);
		});

	Map<String, List<Map<String, Object>>> map = new LinkedHashMap<>();
	map.put(TypeContrast.PREFIX, typeContrastAll);

	return map;
  }

  private List<Map<String, Object>> getTypeContrast() {
	try (DatabaseWrapper db = new DatabaseWrapper()) {
	  return SqlOperator.queryList(
		  db,
		  "select t1.dsl_name,t3.source_type,t3.target_type from " + Data_store_layer.TableName
			  + " t1 left join " + Type_contrast_sum.TableName
			  + " t2 on t1.dtcs_id = t2.dtcs_id left join " + Type_contrast.TableName
			  + " t3 on t1.dtcs_id = t3.dtcs_id order by t1.dsl_name");
	}
  }
}
