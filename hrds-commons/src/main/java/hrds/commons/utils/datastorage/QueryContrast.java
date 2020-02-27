package hrds.commons.utils.datastorage;

import fd.ng.core.annotation.DocClass;
import hrds.commons.utils.datastorage.dcl.LengthContrast;
import hrds.commons.utils.datastorage.dcl.TypeContrast;
import java.util.List;
import java.util.Map;

@DocClass(desc = "存储层数据类型对照表信息", author = "Mr.Lee", createdate = "2020-01-13 15:38")
public class QueryContrast {
  public static final String CONF_FILE_NAME = "contrast.conf";
  public static Map<String, List<Map<String, Object>>> getDclContrast() {

    Map<String, List<Map<String, Object>>> typeContrastMap = new TypeContrast().yamlDataFormat();
    typeContrastMap.putAll(new LengthContrast().yamlDataFormat());

    return typeContrastMap;
  }

}
