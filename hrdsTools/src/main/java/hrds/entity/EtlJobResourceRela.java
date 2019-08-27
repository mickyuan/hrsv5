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
@Table(tableName = "etl_job_resource_rela")
public class EtlJobResourceRela extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "etl_job_resource_rela";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("etl_job");
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

	private String resource_type;
	private String etl_sys_cd;
	private String etl_job;
	private Integer resource_req;

	public String getResource_type() { return resource_type; }
	public void setResource_type(String resource_type) {
		if(resource_type==null) addNullValueField("resource_type");
		this.resource_type = resource_type;
	}

	public String getEtl_sys_cd() { return etl_sys_cd; }
	public void setEtl_sys_cd(String etl_sys_cd) {
		if(etl_sys_cd==null) throw new BusinessException("Entity : EtlJobResourceRela.etl_sys_cd must not null!");
		this.etl_sys_cd = etl_sys_cd;
	}

	public String getEtl_job() { return etl_job; }
	public void setEtl_job(String etl_job) {
		if(etl_job==null) throw new BusinessException("Entity : EtlJobResourceRela.etl_job must not null!");
		this.etl_job = etl_job;
	}

	public Integer getResource_req() { return resource_req; }
	public void setResource_req(Integer resource_req) {
		if(resource_req==null) addNullValueField("resource_req");
		this.resource_req = resource_req;
	}

}