package hrds.commons.hadoop.hbaseindexer.type;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * solr类型转换，现在只支持转换成： string long int float double
 *
 * @author Mick
 */
public class DataTypeTransformSolr {
	private static final Log logger = LogFactory.getLog(DataTypeTransformSolr.class);
	private static final Map<String, String> map_solr = new HashMap<>();
	private static final Map<String, String> map_solr_customize_type = new HashMap<>();
	private static final String LKH = "(";

	static {
		JSONObject jsonTypeObj = null;
		//TODO 下面的配置文件需要去看
//		try {
//			String jsonType = PropertyUtil.getMessage("solrdatatrans");
//			jsonTypeObj = JSON.parseObject(jsonType);
//
//		} catch (Exception e) {
//		}
		if (null != jsonTypeObj) {
			Set<String> keys = jsonTypeObj.keySet();
			for (String key : keys) {
				map_solr.put(key, jsonTypeObj.getString(key));
			}
			map_solr.put("STRING", "STRING");
		} else {
			logger.info("application配置文件中未读取到solrdatatrans项，读取默认类型配置");
			// 字符串类
			map_solr.put("STRING", "STRING");
			map_solr.put("VARCHAR", "STRING");
			// 数值类
			map_solr.put("INT", "INT");
			map_solr.put("LONG", "LONG");
			map_solr.put("DOUBLE", "DOUBLE");
			map_solr.put("FLOAT", "FLOAT");
			map_solr.put("DECIMAL", "STRING");
			// 布尔类
			map_solr.put("BOOLEAN", "BOOLEAN");
			// 日期类
			map_solr.put("DATE", "STRING");
			map_solr.put("TIMESTAMP", "STRING");
		}
		initializeSolrCustomizeType();
	}

	/**
	 * 在配置文件中可以填写的支持的所有类型
	 * 映射出真正的自定义类型
	 */
	private static void initializeSolrCustomizeType() {
		map_solr_customize_type.put("STRING", "customization.hyren.type.Hstring");
		map_solr_customize_type.put("INT", "customization.hyren.type.Hint");
		map_solr_customize_type.put("BIGDECIMAL", "customization.hyren.type.Hbigdecimal");
		map_solr_customize_type.put("BOOLEAN", "customization.hyren.type.Hboolean");
		map_solr_customize_type.put("DOUBLE", "customization.hyren.type.Hdouble");
		map_solr_customize_type.put("FLOAT", "customization.hyren.type.Hfloat");
		map_solr_customize_type.put("LONG", "customization.hyren.type.Hlong");
		map_solr_customize_type.put("SHORT", "customization.hyren.type.Hshort");
	}

	/**
	 * 转换关系型数据库类型为hbase-indexer支持类型
	 *
	 * @param type 源类型
	 * @return 兼容类型
	 */
	public static String transform(String type) {

		type = type.trim().toUpperCase();
		String key = type.contains(LKH) ? type.substring(0, type.indexOf(LKH)) : type;
		String transformedType = map_solr.get(key);
		if (StringUtils.isBlank(transformedType)) {
			logger.info("no configuration type: " + key + ", using default type STRING");
			transformedType = "STRING";
		}

		return transformedType;
	}

	/**
	 * 转换为solr中真正的自定义类型，转换两次是不想在配置文件中让人去写 很长的自定义类路径
	 *
	 * @param transformedType
	 * @return
	 */
	public static String transformToRealTypeInSolr(String transformedType) {
		// 这是真正在solr中的类型转换的自定义类
		String customizeTypeInSolr = map_solr_customize_type.get(transformedType);
		if (StringUtils.isBlank(customizeTypeInSolr)) {
			logger.info(
					"Can not transform type " + transformedType
							+ " to customize type in solr, using default customize type "
							+ map_solr_customize_type.get("STRING"));
			return map_solr_customize_type.get("STRING");
		}
		return customizeTypeInSolr;
	}

	/**
	 * 集合类型转换
	 *
	 * @param types 源类型集合
	 * @return 兼容类型集合
	 */
	public static List<String> tansform(List<String> types) {

		List<String> transformedTypeList = new ArrayList<>();
		for (String string : types) {
			transformedTypeList.add(transform(string));
		}
		return transformedTypeList;
	}

	public static void main(String[] args) {
		System.out.println(transform("Integer(20,3)"));
	}
}
