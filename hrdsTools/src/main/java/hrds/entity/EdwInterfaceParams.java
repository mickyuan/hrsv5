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
@Table(tableName = "edw_interface_params")
public class EdwInterfaceParams extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "edw_interface_params";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("src_table");
		__tmpPKS.add("src_cd");
		__tmpPKS.add("src_column");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String src_system;
	private String dest_desc;
	private String dest_table;
	private String src_table;
	private String src_column;
	private String src_desc;
	private String dest_cd;
	private String src_cd;

	public String getSrc_system() { return src_system; }
	public void setSrc_system(String src_system) {
		if(src_system==null) throw new BusinessException("Entity : EdwInterfaceParams.src_system must not null!");
		this.src_system = src_system;
	}

	public String getDest_desc() { return dest_desc; }
	public void setDest_desc(String dest_desc) {
		if(dest_desc==null) addNullValueField("dest_desc");
		this.dest_desc = dest_desc;
	}

	public String getDest_table() { return dest_table; }
	public void setDest_table(String dest_table) {
		if(dest_table==null) addNullValueField("dest_table");
		this.dest_table = dest_table;
	}

	public String getSrc_table() { return src_table; }
	public void setSrc_table(String src_table) {
		if(src_table==null) throw new BusinessException("Entity : EdwInterfaceParams.src_table must not null!");
		this.src_table = src_table;
	}

	public String getSrc_column() { return src_column; }
	public void setSrc_column(String src_column) {
		if(src_column==null) throw new BusinessException("Entity : EdwInterfaceParams.src_column must not null!");
		this.src_column = src_column;
	}

	public String getSrc_desc() { return src_desc; }
	public void setSrc_desc(String src_desc) {
		if(src_desc==null) addNullValueField("src_desc");
		this.src_desc = src_desc;
	}

	public String getDest_cd() { return dest_cd; }
	public void setDest_cd(String dest_cd) {
		if(dest_cd==null) addNullValueField("dest_cd");
		this.dest_cd = dest_cd;
	}

	public String getSrc_cd() { return src_cd; }
	public void setSrc_cd(String src_cd) {
		if(src_cd==null) throw new BusinessException("Entity : EdwInterfaceParams.src_cd must not null!");
		this.src_cd = src_cd;
	}

}