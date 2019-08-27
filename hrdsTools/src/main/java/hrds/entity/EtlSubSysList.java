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
@Table(tableName = "etl_sub_sys_list")
public class EtlSubSysList extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "etl_sub_sys_list";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("sub_sys_cd");
		__tmpPKS.add("etl_sys_cd");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String sub_sys_cd;
	private String etl_sys_cd;
	private String comments;
	private String sub_sys_desc;

	public String getSub_sys_cd() { return sub_sys_cd; }
	public void setSub_sys_cd(String sub_sys_cd) {
		if(sub_sys_cd==null) throw new BusinessException("Entity : EtlSubSysList.sub_sys_cd must not null!");
		this.sub_sys_cd = sub_sys_cd;
	}

	public String getEtl_sys_cd() { return etl_sys_cd; }
	public void setEtl_sys_cd(String etl_sys_cd) {
		if(etl_sys_cd==null) throw new BusinessException("Entity : EtlSubSysList.etl_sys_cd must not null!");
		this.etl_sys_cd = etl_sys_cd;
	}

	public String getComments() { return comments; }
	public void setComments(String comments) {
		if(comments==null) addNullValueField("comments");
		this.comments = comments;
	}

	public String getSub_sys_desc() { return sub_sys_desc; }
	public void setSub_sys_desc(String sub_sys_desc) {
		if(sub_sys_desc==null) addNullValueField("sub_sys_desc");
		this.sub_sys_desc = sub_sys_desc;
	}

}