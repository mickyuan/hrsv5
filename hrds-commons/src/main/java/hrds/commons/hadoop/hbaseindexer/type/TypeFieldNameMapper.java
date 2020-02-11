package hrds.commons.hadoop.hbaseindexer.type;

import hrds.commons.exception.BusinessException;
import hrds.commons.hadoop.hbaseindexer.bean.HbaseSolrField;
import org.apache.commons.lang.StringUtils;

/**
 * solr动态域字段，需要在solr的schema.xml配置文件中定义
 * <dynamicField name="FI-*" type="int" indexed="true" stored="true" />
 * <dynamicField name="FL-*" type="long" indexed="true" stored="true"/>
 * <dynamicField name="FT-*" type="text_general" indexed="true" stored="true"/>
 * <dynamicField name="FB-*" type="boolean" indexed="true" stored="true"/>
 * <dynamicField name="FF-*" type="float" indexed="true" stored="true"/>
 * <dynamicField name="FD-*" type="double" indexed="true" stored="true"/>
 * <dynamicField name="FDT-*" type="date" indexed="true" stored="true"/>
 * <dynamicField name="FP-*" type="location" indexed="true" stored="true"/>
 * <dynamicField name="F-*" type="string" indexed="true" stored="true"/>
 * 现在可能仅支持几个基本类型的转换和使用
 *
 * @author Mick
 *
 */
public class TypeFieldNameMapper implements FieldNameMapper {
	public static final String STRING_TYPE_SUFFIX = "F-";
	public static final String LONG_TYPE_SUFFIX = "FL-";
	public static final String BOOLEAN_TYPE_SUFFIX = "FB-";
	public static final String FLOAT_TYPE_SUFFIX = "FF-";
	public static final String DOUBLE_TYPE_SUFFIX = "FD-";
	public static final String DATE_TYPE_SUFFIX = "FDT-";
	public static final String INT_TYPE_SUFFIX = "FI-";

	@Override
	public void map(HbaseSolrField hsf) {

		String type = hsf.getType();
		String hbaseFieldName = hsf.getHbaseColumnName();

		if (StringUtils.isBlank(type) || StringUtils.isBlank(hbaseFieldName)) {
			throw new BusinessException("Invalid object: " + hsf.toString());
		}

		if (type.equalsIgnoreCase(SolrTypeEnum.STRING.toString())) {

			hsf.setSolrColumnName(STRING_TYPE_SUFFIX + hbaseFieldName);

		} else if (type.equalsIgnoreCase(SolrTypeEnum.LONG.toString())) {

			hsf.setSolrColumnName(LONG_TYPE_SUFFIX + hbaseFieldName);

		} else if (type.equalsIgnoreCase(SolrTypeEnum.BOOLEAN.toString())) {

			hsf.setSolrColumnName(BOOLEAN_TYPE_SUFFIX + hbaseFieldName);

		} else if (type.equalsIgnoreCase(SolrTypeEnum.FLOAT.toString())) {

			hsf.setSolrColumnName(FLOAT_TYPE_SUFFIX + hbaseFieldName);

		} else if (type.equalsIgnoreCase(SolrTypeEnum.DOUBLE.toString())) {

			hsf.setSolrColumnName(DOUBLE_TYPE_SUFFIX + hbaseFieldName);

		} else if (type.equalsIgnoreCase(SolrTypeEnum.DATE.toString())) {

			hsf.setSolrColumnName(DATE_TYPE_SUFFIX + hbaseFieldName);

		} else if (type.equalsIgnoreCase(SolrTypeEnum.INT.toString())) {

			hsf.setSolrColumnName(INT_TYPE_SUFFIX + hbaseFieldName);

		} else {

			throw new BusinessException("Can not recongnize field type : " + type);
		}
	}

}
