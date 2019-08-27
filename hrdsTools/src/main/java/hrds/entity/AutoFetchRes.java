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
@Table(tableName = "auto_fetch_res")
public class AutoFetchRes extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_fetch_res";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("fetch_res_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal template_res_id;
	private String fetch_res_name;
	private Integer show_num;
	private BigDecimal fetch_res_id;
	private BigDecimal fetch_sum_id;

	public BigDecimal getTemplate_res_id() { return template_res_id; }
	public void setTemplate_res_id(BigDecimal template_res_id) {
		if(template_res_id==null) throw new BusinessException("Entity : AutoFetchRes.template_res_id must not null!");
		this.template_res_id = template_res_id;
	}

	public String getFetch_res_name() { return fetch_res_name; }
	public void setFetch_res_name(String fetch_res_name) {
		if(fetch_res_name==null) addNullValueField("fetch_res_name");
		this.fetch_res_name = fetch_res_name;
	}

	public Integer getShow_num() { return show_num; }
	public void setShow_num(Integer show_num) {
		if(show_num==null) addNullValueField("show_num");
		this.show_num = show_num;
	}

	public BigDecimal getFetch_res_id() { return fetch_res_id; }
	public void setFetch_res_id(BigDecimal fetch_res_id) {
		if(fetch_res_id==null) throw new BusinessException("Entity : AutoFetchRes.fetch_res_id must not null!");
		this.fetch_res_id = fetch_res_id;
	}

	public BigDecimal getFetch_sum_id() { return fetch_sum_id; }
	public void setFetch_sum_id(BigDecimal fetch_sum_id) {
		if(fetch_sum_id==null) throw new BusinessException("Entity : AutoFetchRes.fetch_sum_id must not null!");
		this.fetch_sum_id = fetch_sum_id;
	}

}