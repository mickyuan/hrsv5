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
@Table(tableName = "sdm_con_druid_col")
public class SdmConDruidCol extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_con_druid_col";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("druid_col_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String column_name;
	private BigDecimal druid_id;
	private String column_cname;
	private String column_tyoe;
	private BigDecimal druid_col_id;

	public String getColumn_name() { return column_name; }
	public void setColumn_name(String column_name) {
		if(column_name==null) throw new BusinessException("Entity : SdmConDruidCol.column_name must not null!");
		this.column_name = column_name;
	}

	public BigDecimal getDruid_id() { return druid_id; }
	public void setDruid_id(BigDecimal druid_id) {
		if(druid_id==null) throw new BusinessException("Entity : SdmConDruidCol.druid_id must not null!");
		this.druid_id = druid_id;
	}

	public String getColumn_cname() { return column_cname; }
	public void setColumn_cname(String column_cname) {
		if(column_cname==null) throw new BusinessException("Entity : SdmConDruidCol.column_cname must not null!");
		this.column_cname = column_cname;
	}

	public String getColumn_tyoe() { return column_tyoe; }
	public void setColumn_tyoe(String column_tyoe) {
		if(column_tyoe==null) throw new BusinessException("Entity : SdmConDruidCol.column_tyoe must not null!");
		this.column_tyoe = column_tyoe;
	}

	public BigDecimal getDruid_col_id() { return druid_col_id; }
	public void setDruid_col_id(BigDecimal druid_col_id) {
		if(druid_col_id==null) throw new BusinessException("Entity : SdmConDruidCol.druid_col_id must not null!");
		this.druid_col_id = druid_col_id;
	}

}