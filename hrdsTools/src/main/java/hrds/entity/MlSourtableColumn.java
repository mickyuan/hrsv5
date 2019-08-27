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
@Table(tableName = "ml_sourtable_column")
public class MlSourtableColumn extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_sourtable_column";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("table_column_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String column_cn_name;
	private BigDecimal table_column_id;
	private String ocolumn_name;
	private String column_en_name;
	private BigDecimal dtable_info_id;
	private String column_type;
	private String column_desc;

	public String getColumn_cn_name() { return column_cn_name; }
	public void setColumn_cn_name(String column_cn_name) {
		if(column_cn_name==null) throw new BusinessException("Entity : MlSourtableColumn.column_cn_name must not null!");
		this.column_cn_name = column_cn_name;
	}

	public BigDecimal getTable_column_id() { return table_column_id; }
	public void setTable_column_id(BigDecimal table_column_id) {
		if(table_column_id==null) throw new BusinessException("Entity : MlSourtableColumn.table_column_id must not null!");
		this.table_column_id = table_column_id;
	}

	public String getOcolumn_name() { return ocolumn_name; }
	public void setOcolumn_name(String ocolumn_name) {
		if(ocolumn_name==null) addNullValueField("ocolumn_name");
		this.ocolumn_name = ocolumn_name;
	}

	public String getColumn_en_name() { return column_en_name; }
	public void setColumn_en_name(String column_en_name) {
		if(column_en_name==null) throw new BusinessException("Entity : MlSourtableColumn.column_en_name must not null!");
		this.column_en_name = column_en_name;
	}

	public BigDecimal getDtable_info_id() { return dtable_info_id; }
	public void setDtable_info_id(BigDecimal dtable_info_id) {
		if(dtable_info_id==null) throw new BusinessException("Entity : MlSourtableColumn.dtable_info_id must not null!");
		this.dtable_info_id = dtable_info_id;
	}

	public String getColumn_type() { return column_type; }
	public void setColumn_type(String column_type) {
		if(column_type==null) throw new BusinessException("Entity : MlSourtableColumn.column_type must not null!");
		this.column_type = column_type;
	}

	public String getColumn_desc() { return column_desc; }
	public void setColumn_desc(String column_desc) {
		if(column_desc==null) addNullValueField("column_desc");
		this.column_desc = column_desc;
	}

}