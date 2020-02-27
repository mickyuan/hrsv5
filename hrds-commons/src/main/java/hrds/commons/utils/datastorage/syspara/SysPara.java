package hrds.commons.utils.datastorage.syspara;

import fd.ng.core.annotation.DocClass;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.entity.Sys_para;
import hrds.commons.utils.datastorage.yamldata.YamlDataFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "系统参数Yaml数据", author = "Mr.Lee", createdate = "2020-01-15 11:22")
public class SysPara implements YamlDataFormat {
  private static final String PREFIX = "param";
  public static final String CONF_FILE_NAME = "sysparam.conf";

  @Override
  public Map<String, List<Map<String, Object>>> yamlDataFormat() {

    Map<String, List<Map<String, Object>>> map = new LinkedHashMap<>();
    Map<String, Object> sysMap = new HashMap<>();
    getSysPara()
        .forEach(
            item -> {
              sysMap.put(((String) item.get("para_name")), item.get("para_value"));
            });

    List<Map<String, Object>> list = new ArrayList<>();
    list.add(sysMap);
    map.put(PREFIX, list);
    return map;
  }

  private List<Map<String, Object>> getSysPara() {

    try (DatabaseWrapper db = new DatabaseWrapper()) {
      return SqlOperator.queryList(db, "SELECT * FROM " + Sys_para.TableName);
    }
  }
}
