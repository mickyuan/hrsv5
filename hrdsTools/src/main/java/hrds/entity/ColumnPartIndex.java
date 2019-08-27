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
@Table(tableName = "column_part_index")
public class ColumnPartIndex extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "column_part_index";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("part_index_id");
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
	private String is_primary_key;
	private String is_unique_index;
	private String is_partition;
	private BigDecimal table_id;
	private BigDecimal part_index_id;
	private String is_normal_index;

	public BigDecimal getColumn_id() { return column_id; }
	public void setColumn_id(BigDecimal column_id) {
		if(column_id==null) throw new BusinessException("Entity : ColumnPartIndex.column_id must not null!");
		this.column_id = column_id;
	}

	public String getIs_primary_key() { return is_primary_key; }
	public void setIs_primary_key(String is_primary_key) {
		if(is_primary_key==null) throw new BusinessException("Entity : ColumnPartIndex.is_primary_key must not null!");
		this.is_primary_key = is_primary_key;
	}

	public String getIs_unique_index() { return is_unique_index; }
	public void setIs_unique_index(String is_unique_index) {
		if(is_unique_index==null) throw new BusinessException("Entity : ColumnPartIndex.is_unique_index must not null!");
		this.is_unique_index = is_unique_index;
	}

	public String getIs_partition() { return is_partition; }
	public void setIs_partition(String is_partition) {
		if(is_partition==null) throw new BusinessException("Entity : ColumnPartIndex.is_partition must not null!");
		this.is_partition = is_partition;
	}

	public BigDecimal getTable_id() { return table_id; }
	public void setTable_id(BigDecimal table_id) {
		if(table_id==null) addNullValueField("table_id");
		this.table_id = table_id;
	}

	public BigDecimal getPart_index_id() { return part_index_id; }
	public void setPart_index_id(BigDecimal part_index_id) {
		if(part_index_id==null) throw new BusinessException("Entity : ColumnPartIndex.part_index_id must not null!");
		this.part_index_id = part_index_id;
	}

	public String getIs_normal_index() { return is_normal_index; }
	public void setIs_normal_index(String is_normal_index) {
		if(is_normal_index==null) throw new BusinessException("Entity : ColumnPartIndex.is_normal_index must not null!");
		this.is_normal_index = is_normal_index;
	}

}