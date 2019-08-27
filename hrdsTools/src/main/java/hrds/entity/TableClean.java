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
@Table(tableName = "table_clean")
public class TableClean extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "table_clean";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("c_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal filling_length;
	private String field;
	private String filling_type;
	private BigDecimal c_id;
	private String character_filling;
	private BigDecimal table_id;
	private String replace_feild;
	private String clean_type;

	public BigDecimal getFilling_length() { return filling_length; }
	public void setFilling_length(BigDecimal filling_length) {
		if(filling_length==null) addNullValueField("filling_length");
		this.filling_length = filling_length;
	}

	public String getField() { return field; }
	public void setField(String field) {
		if(field==null) addNullValueField("field");
		this.field = field;
	}

	public String getFilling_type() { return filling_type; }
	public void setFilling_type(String filling_type) {
		if(filling_type==null) addNullValueField("filling_type");
		this.filling_type = filling_type;
	}

	public BigDecimal getC_id() { return c_id; }
	public void setC_id(BigDecimal c_id) {
		if(c_id==null) throw new BusinessException("Entity : TableClean.c_id must not null!");
		this.c_id = c_id;
	}

	public String getCharacter_filling() { return character_filling; }
	public void setCharacter_filling(String character_filling) {
		if(character_filling==null) addNullValueField("character_filling");
		this.character_filling = character_filling;
	}

	public BigDecimal getTable_id() { return table_id; }
	public void setTable_id(BigDecimal table_id) {
		if(table_id==null) throw new BusinessException("Entity : TableClean.table_id must not null!");
		this.table_id = table_id;
	}

	public String getReplace_feild() { return replace_feild; }
	public void setReplace_feild(String replace_feild) {
		if(replace_feild==null) addNullValueField("replace_feild");
		this.replace_feild = replace_feild;
	}

	public String getClean_type() { return clean_type; }
	public void setClean_type(String clean_type) {
		if(clean_type==null) throw new BusinessException("Entity : TableClean.clean_type must not null!");
		this.clean_type = clean_type;
	}

}