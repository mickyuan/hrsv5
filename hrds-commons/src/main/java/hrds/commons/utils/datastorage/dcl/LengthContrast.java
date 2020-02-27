package hrds.commons.utils.datastorage.dcl;

import fd.ng.core.annotation.DocClass;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.entity.Length_contrast;
import hrds.commons.entity.Length_contrast_sum;
import hrds.commons.utils.datastorage.yamldata.YamlDataFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "储层数据类型长度对照表", author = "Mr.Lee", createdate = "2020-01-13 15:50")
public class LengthContrast implements YamlDataFormat {

  /** prefix : 前缀名称 */
  public static final String PREFIX = "lengthcontrast";
  /** name : 类型对照名称 */
  public static final String NAME = "NAME";

  @Override
  public Map<String, List<Map<String, Object>>> yamlDataFormat() {
    Map<String, Map<String, Object>> contrastMap = new LinkedHashMap<>();
    getLengthContrast()
        .forEach(
            item -> {
              String dtcs_name = ((String) item.get("dlcs_name")).toUpperCase();
              String source_type = ((String) item.get("dlc_type")).toUpperCase();
              Object target_type = item.get("dlc_length");

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
    map.put(PREFIX, typeContrastAll);

    return map;
  }

  private List<Map<String, Object>> getLengthContrast() {
    try (DatabaseWrapper db = new DatabaseWrapper()) {
      return SqlOperator.queryList(
          db,
          "SELECT t2.dlcs_name,t1.dlc_type,t1.dlc_length FROM "
              + Length_contrast.TableName
              + " t1 LEFT JOIN "
              + Length_contrast_sum.TableName
              + " t2 ON t1.dlcs_id = t2.dlcs_id");
    }
  }
}
