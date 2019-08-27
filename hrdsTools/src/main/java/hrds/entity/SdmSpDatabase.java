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
@Table(tableName = "sdm_sp_database")
public class SdmSpDatabase extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_sp_database";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("ssd_info_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String ssd_database_name;
	private String ssd_user_password;
	private String ssd_ip;
	private String ssd_jdbc_url;
	private String ssd_database_type;
	private String ssd_table_name;
	private BigDecimal ssd_info_id;
	private String ssd_database_drive;
	private String ssd_port;
	private String ssd_user_name;
	private BigDecimal sdm_info_id;

	public String getSsd_database_name() { return ssd_database_name; }
	public void setSsd_database_name(String ssd_database_name) {
		if(ssd_database_name==null) throw new BusinessException("Entity : SdmSpDatabase.ssd_database_name must not null!");
		this.ssd_database_name = ssd_database_name;
	}

	public String getSsd_user_password() { return ssd_user_password; }
	public void setSsd_user_password(String ssd_user_password) {
		if(ssd_user_password==null) throw new BusinessException("Entity : SdmSpDatabase.ssd_user_password must not null!");
		this.ssd_user_password = ssd_user_password;
	}

	public String getSsd_ip() { return ssd_ip; }
	public void setSsd_ip(String ssd_ip) {
		if(ssd_ip==null) throw new BusinessException("Entity : SdmSpDatabase.ssd_ip must not null!");
		this.ssd_ip = ssd_ip;
	}

	public String getSsd_jdbc_url() { return ssd_jdbc_url; }
	public void setSsd_jdbc_url(String ssd_jdbc_url) {
		if(ssd_jdbc_url==null) throw new BusinessException("Entity : SdmSpDatabase.ssd_jdbc_url must not null!");
		this.ssd_jdbc_url = ssd_jdbc_url;
	}

	public String getSsd_database_type() { return ssd_database_type; }
	public void setSsd_database_type(String ssd_database_type) {
		if(ssd_database_type==null) throw new BusinessException("Entity : SdmSpDatabase.ssd_database_type must not null!");
		this.ssd_database_type = ssd_database_type;
	}

	public String getSsd_table_name() { return ssd_table_name; }
	public void setSsd_table_name(String ssd_table_name) {
		if(ssd_table_name==null) throw new BusinessException("Entity : SdmSpDatabase.ssd_table_name must not null!");
		this.ssd_table_name = ssd_table_name;
	}

	public BigDecimal getSsd_info_id() { return ssd_info_id; }
	public void setSsd_info_id(BigDecimal ssd_info_id) {
		if(ssd_info_id==null) throw new BusinessException("Entity : SdmSpDatabase.ssd_info_id must not null!");
		this.ssd_info_id = ssd_info_id;
	}

	public String getSsd_database_drive() { return ssd_database_drive; }
	public void setSsd_database_drive(String ssd_database_drive) {
		if(ssd_database_drive==null) throw new BusinessException("Entity : SdmSpDatabase.ssd_database_drive must not null!");
		this.ssd_database_drive = ssd_database_drive;
	}

	public String getSsd_port() { return ssd_port; }
	public void setSsd_port(String ssd_port) {
		if(ssd_port==null) throw new BusinessException("Entity : SdmSpDatabase.ssd_port must not null!");
		this.ssd_port = ssd_port;
	}

	public String getSsd_user_name() { return ssd_user_name; }
	public void setSsd_user_name(String ssd_user_name) {
		if(ssd_user_name==null) throw new BusinessException("Entity : SdmSpDatabase.ssd_user_name must not null!");
		this.ssd_user_name = ssd_user_name;
	}

	public BigDecimal getSdm_info_id() { return sdm_info_id; }
	public void setSdm_info_id(BigDecimal sdm_info_id) {
		if(sdm_info_id==null) throw new BusinessException("Entity : SdmSpDatabase.sdm_info_id must not null!");
		this.sdm_info_id = sdm_info_id;
	}

}