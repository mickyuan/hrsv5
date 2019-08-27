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
@Table(tableName = "floder_table")
public class FloderTable extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "floder_table";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("folder_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String floder_name;
	private BigDecimal folder_id;
	private BigDecimal create_id;
	private String create_date;
	private String create_time;

	public String getFloder_name() { return floder_name; }
	public void setFloder_name(String floder_name) {
		if(floder_name==null) throw new BusinessException("Entity : FloderTable.floder_name must not null!");
		this.floder_name = floder_name;
	}

	public BigDecimal getFolder_id() { return folder_id; }
	public void setFolder_id(BigDecimal folder_id) {
		if(folder_id==null) throw new BusinessException("Entity : FloderTable.folder_id must not null!");
		this.folder_id = folder_id;
	}

	public BigDecimal getCreate_id() { return create_id; }
	public void setCreate_id(BigDecimal create_id) {
		if(create_id==null) throw new BusinessException("Entity : FloderTable.create_id must not null!");
		this.create_id = create_id;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : FloderTable.create_date must not null!");
		this.create_date = create_date;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : FloderTable.create_time must not null!");
		this.create_time = create_time;
	}

}