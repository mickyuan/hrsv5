package hrds.entity;

import fd.ng.db.entity.TableEntity;
import fd.ng.db.entity.anno.Column;
import fd.ng.db.entity.anno.Table;
import hrds.exception.BusinessException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 实体类中所有属性都应定义为对象，不要使用int等主类型，方便对null值的操作
 */
@Table(tableName = "edw_sparksql_gram")
public class EdwSparksqlGram extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "edw_sparksql_gram";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("esg_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String jar_url;
	private String is_sparksql;
	private String function_name;
	private String function_desc;
	private String class_url;
	private String remark;
	private String is_udf;
	private String function_example;
	private String hivedb_name;
	private BigDecimal esg_id;
	private String is_available;

	public String getJar_url() { return jar_url; }
	public void setJar_url(String jar_url) {
		if(jar_url==null) addNullValueField("jar_url");
		this.jar_url = jar_url;
	}

	public String getIs_sparksql() { return is_sparksql; }
	public void setIs_sparksql(String is_sparksql) {
		if(is_sparksql==null) throw new BusinessException("Entity : EdwSparksqlGram.is_sparksql must not null!");
		this.is_sparksql = is_sparksql;
	}

	public String getFunction_name() { return function_name; }
	public void setFunction_name(String function_name) {
		if(function_name==null) throw new BusinessException("Entity : EdwSparksqlGram.function_name must not null!");
		this.function_name = function_name;
	}

	public String getFunction_desc() { return function_desc; }
	public void setFunction_desc(String function_desc) {
		if(function_desc==null) throw new BusinessException("Entity : EdwSparksqlGram.function_desc must not null!");
		this.function_desc = function_desc;
	}

	public String getClass_url() { return class_url; }
	public void setClass_url(String class_url) {
		if(class_url==null) addNullValueField("class_url");
		this.class_url = class_url;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public String getIs_udf() { return is_udf; }
	public void setIs_udf(String is_udf) {
		if(is_udf==null) throw new BusinessException("Entity : EdwSparksqlGram.is_udf must not null!");
		this.is_udf = is_udf;
	}

	public String getFunction_example() { return function_example; }
	public void setFunction_example(String function_example) {
		if(function_example==null) throw new BusinessException("Entity : EdwSparksqlGram.function_example must not null!");
		this.function_example = function_example;
	}

	public String getHivedb_name() { return hivedb_name; }
	public void setHivedb_name(String hivedb_name) {
		if(hivedb_name==null) addNullValueField("hivedb_name");
		this.hivedb_name = hivedb_name;
	}

	public BigDecimal getEsg_id() { return esg_id; }
	public void setEsg_id(BigDecimal esg_id) {
		if(esg_id==null) throw new BusinessException("Entity : EdwSparksqlGram.esg_id must not null!");
		this.esg_id = esg_id;
	}

	public String getIs_available() { return is_available; }
	public void setIs_available(String is_available) {
		if(is_available==null) throw new BusinessException("Entity : EdwSparksqlGram.is_available must not null!");
		this.is_available = is_available;
	}

}