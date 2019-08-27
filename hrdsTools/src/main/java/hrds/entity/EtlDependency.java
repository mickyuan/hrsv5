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
@Table(tableName = "etl_dependency")
public class EtlDependency extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "etl_dependency";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("etl_sys_cd");
		__tmpPKS.add("etl_job");
		__tmpPKS.add("pre_etl_sys_cd");
		__tmpPKS.add("pre_etl_job");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String main_serv_sync;
	private String etl_sys_cd;
	private String etl_job;
	private String pre_etl_sys_cd;
	private String pre_etl_job;
	private String status;

	public String getMain_serv_sync() { return main_serv_sync; }
	public void setMain_serv_sync(String main_serv_sync) {
		if(main_serv_sync==null) addNullValueField("main_serv_sync");
		this.main_serv_sync = main_serv_sync;
	}

	public String getEtl_sys_cd() { return etl_sys_cd; }
	public void setEtl_sys_cd(String etl_sys_cd) {
		if(etl_sys_cd==null) throw new BusinessException("Entity : EtlDependency.etl_sys_cd must not null!");
		this.etl_sys_cd = etl_sys_cd;
	}

	public String getEtl_job() { return etl_job; }
	public void setEtl_job(String etl_job) {
		if(etl_job==null) throw new BusinessException("Entity : EtlDependency.etl_job must not null!");
		this.etl_job = etl_job;
	}

	public String getPre_etl_sys_cd() { return pre_etl_sys_cd; }
	public void setPre_etl_sys_cd(String pre_etl_sys_cd) {
		if(pre_etl_sys_cd==null) throw new BusinessException("Entity : EtlDependency.pre_etl_sys_cd must not null!");
		this.pre_etl_sys_cd = pre_etl_sys_cd;
	}

	public String getPre_etl_job() { return pre_etl_job; }
	public void setPre_etl_job(String pre_etl_job) {
		if(pre_etl_job==null) throw new BusinessException("Entity : EtlDependency.pre_etl_job must not null!");
		this.pre_etl_job = pre_etl_job;
	}

	public String getStatus() { return status; }
	public void setStatus(String status) {
		if(status==null) addNullValueField("status");
		this.status = status;
	}

}