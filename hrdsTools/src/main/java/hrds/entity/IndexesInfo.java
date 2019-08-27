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
@Table(tableName = "indexes_info")
public class IndexesInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "indexes_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("indexes_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String indexes_select_field;
	private String indexes_type;
	private BigDecimal indexes_id;
	private String indexes_field;
	private String file_id;

	public String getIndexes_select_field() { return indexes_select_field; }
	public void setIndexes_select_field(String indexes_select_field) {
		if(indexes_select_field==null) addNullValueField("indexes_select_field");
		this.indexes_select_field = indexes_select_field;
	}

	public String getIndexes_type() { return indexes_type; }
	public void setIndexes_type(String indexes_type) {
		if(indexes_type==null) throw new BusinessException("Entity : IndexesInfo.indexes_type must not null!");
		this.indexes_type = indexes_type;
	}

	public BigDecimal getIndexes_id() { return indexes_id; }
	public void setIndexes_id(BigDecimal indexes_id) {
		if(indexes_id==null) throw new BusinessException("Entity : IndexesInfo.indexes_id must not null!");
		this.indexes_id = indexes_id;
	}

	public String getIndexes_field() { return indexes_field; }
	public void setIndexes_field(String indexes_field) {
		if(indexes_field==null) throw new BusinessException("Entity : IndexesInfo.indexes_field must not null!");
		this.indexes_field = indexes_field;
	}

	public String getFile_id() { return file_id; }
	public void setFile_id(String file_id) {
		if(file_id==null) throw new BusinessException("Entity : IndexesInfo.file_id must not null!");
		this.file_id = file_id;
	}

}