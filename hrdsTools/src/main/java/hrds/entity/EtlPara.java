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
@Table(tableName = "etl_para")
public class EtlPara extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "etl_para";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("para_cd");
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

	private String para_desc;
	private String para_type;
	private String etl_sys_cd;
	private String para_cd;
	private String para_val;

	public String getPara_desc() { return para_desc; }
	public void setPara_desc(String para_desc) {
		if(para_desc==null) addNullValueField("para_desc");
		this.para_desc = para_desc;
	}

	public String getPara_type() { return para_type; }
	public void setPara_type(String para_type) {
		if(para_type==null) addNullValueField("para_type");
		this.para_type = para_type;
	}

	public String getEtl_sys_cd() { return etl_sys_cd; }
	public void setEtl_sys_cd(String etl_sys_cd) {
		if(etl_sys_cd==null) throw new BusinessException("Entity : EtlPara.etl_sys_cd must not null!");
		this.etl_sys_cd = etl_sys_cd;
	}

	public String getPara_cd() { return para_cd; }
	public void setPara_cd(String para_cd) {
		if(para_cd==null) throw new BusinessException("Entity : EtlPara.para_cd must not null!");
		this.para_cd = para_cd;
	}

	public String getPara_val() { return para_val; }
	public void setPara_val(String para_val) {
		if(para_val==null) addNullValueField("para_val");
		this.para_val = para_val;
	}

}