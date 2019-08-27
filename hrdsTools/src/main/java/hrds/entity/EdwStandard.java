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
@Table(tableName = "edw_standard")
public class EdwStandard extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "edw_standard";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("table_id");
		__tmpPKS.add("colname");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String colname;
	private BigDecimal table_id;
	private String coltra;
	private String tratype;
	private String colformat;

	public String getColname() { return colname; }
	public void setColname(String colname) {
		if(colname==null) throw new BusinessException("Entity : EdwStandard.colname must not null!");
		this.colname = colname;
	}

	public BigDecimal getTable_id() { return table_id; }
	public void setTable_id(BigDecimal table_id) {
		if(table_id==null) throw new BusinessException("Entity : EdwStandard.table_id must not null!");
		this.table_id = table_id;
	}

	public String getColtra() { return coltra; }
	public void setColtra(String coltra) {
		if(coltra==null) addNullValueField("coltra");
		this.coltra = coltra;
	}

	public String getTratype() { return tratype; }
	public void setTratype(String tratype) {
		if(tratype==null) addNullValueField("tratype");
		this.tratype = tratype;
	}

	public String getColformat() { return colformat; }
	public void setColformat(String colformat) {
		if(colformat==null) addNullValueField("colformat");
		this.colformat = colformat;
	}

}