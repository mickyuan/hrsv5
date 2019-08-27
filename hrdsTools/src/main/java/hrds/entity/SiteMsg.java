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
@Table(tableName = "site_msg")
public class SiteMsg extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "site_msg";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("site_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal site_id;
	private String site_name;
	private String site_url;
	private String site_remark;

	public BigDecimal getSite_id() { return site_id; }
	public void setSite_id(BigDecimal site_id) {
		if(site_id==null) throw new BusinessException("Entity : SiteMsg.site_id must not null!");
		this.site_id = site_id;
	}

	public String getSite_name() { return site_name; }
	public void setSite_name(String site_name) {
		if(site_name==null) throw new BusinessException("Entity : SiteMsg.site_name must not null!");
		this.site_name = site_name;
	}

	public String getSite_url() { return site_url; }
	public void setSite_url(String site_url) {
		if(site_url==null) throw new BusinessException("Entity : SiteMsg.site_url must not null!");
		this.site_url = site_url;
	}

	public String getSite_remark() { return site_remark; }
	public void setSite_remark(String site_remark) {
		if(site_remark==null) addNullValueField("site_remark");
		this.site_remark = site_remark;
	}

}