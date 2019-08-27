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
@Table(tableName = "column_clean")
public class ColumnClean extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "column_clean";

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

	private BigDecimal column_id;
	private BigDecimal filling_length;
	private String old_format;
	private String codesys;
	private String field;
	private String filling_type;
	private String codename;
	private BigDecimal c_id;
	private String character_filling;
	private String convert_format;
	private String replace_feild;
	private String clean_type;

	public BigDecimal getColumn_id() { return column_id; }
	public void setColumn_id(BigDecimal column_id) {
		if(column_id==null) throw new BusinessException("Entity : ColumnClean.column_id must not null!");
		this.column_id = column_id;
	}

	public BigDecimal getFilling_length() { return filling_length; }
	public void setFilling_length(BigDecimal filling_length) {
		if(filling_length==null) addNullValueField("filling_length");
		this.filling_length = filling_length;
	}

	public String getOld_format() { return old_format; }
	public void setOld_format(String old_format) {
		if(old_format==null) addNullValueField("old_format");
		this.old_format = old_format;
	}

	public String getCodesys() { return codesys; }
	public void setCodesys(String codesys) {
		if(codesys==null) addNullValueField("codesys");
		this.codesys = codesys;
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

	public String getCodename() { return codename; }
	public void setCodename(String codename) {
		if(codename==null) addNullValueField("codename");
		this.codename = codename;
	}

	public BigDecimal getC_id() { return c_id; }
	public void setC_id(BigDecimal c_id) {
		if(c_id==null) throw new BusinessException("Entity : ColumnClean.c_id must not null!");
		this.c_id = c_id;
	}

	public String getCharacter_filling() { return character_filling; }
	public void setCharacter_filling(String character_filling) {
		if(character_filling==null) addNullValueField("character_filling");
		this.character_filling = character_filling;
	}

	public String getConvert_format() { return convert_format; }
	public void setConvert_format(String convert_format) {
		if(convert_format==null) addNullValueField("convert_format");
		this.convert_format = convert_format;
	}

	public String getReplace_feild() { return replace_feild; }
	public void setReplace_feild(String replace_feild) {
		if(replace_feild==null) addNullValueField("replace_feild");
		this.replace_feild = replace_feild;
	}

	public String getClean_type() { return clean_type; }
	public void setClean_type(String clean_type) {
		if(clean_type==null) throw new BusinessException("Entity : ColumnClean.clean_type must not null!");
		this.clean_type = clean_type;
	}

}