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
@Table(tableName = "etl_resource")
public class EtlResource extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "etl_resource";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("resource_type");
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
	private Integer resource_max;
	private String main_serv_sync;
	private Integer resource_used;
	private String etl_sys_cd;

	public String getResource_type() { return resource_type; }
	public void setResource_type(String resource_type) {
		if(resource_type==null) throw new BusinessException("Entity : EtlResource.resource_type must not null!");
		this.resource_type = resource_type;
	}

	public Integer getResource_max() { return resource_max; }
	public void setResource_max(Integer resource_max) {
		if(resource_max==null) addNullValueField("resource_max");
		this.resource_max = resource_max;
	}

	public String getMain_serv_sync() { return main_serv_sync; }
	public void setMain_serv_sync(String main_serv_sync) {
		if(main_serv_sync==null) throw new BusinessException("Entity : EtlResource.main_serv_sync must not null!");
		this.main_serv_sync = main_serv_sync;
	}

	public Integer getResource_used() { return resource_used; }
	public void setResource_used(Integer resource_used) {
		if(resource_used==null) addNullValueField("resource_used");
		this.resource_used = resource_used;
	}

	public String getEtl_sys_cd() { return etl_sys_cd; }
	public void setEtl_sys_cd(String etl_sys_cd) {
		if(etl_sys_cd==null) throw new BusinessException("Entity : EtlResource.etl_sys_cd must not null!");
		this.etl_sys_cd = etl_sys_cd;
	}

}