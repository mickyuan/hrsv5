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
@Table(tableName = "sdm_con_to_db")
public class SdmConToDb extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_con_to_db";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("sdm_con_db_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String sdm_db_pwd;
	private String sdm_db_type;
	private String sdm_db_ip;
	private String db_bus_class;
	private String sdm_db_port;
	private BigDecimal sdm_consum_id;
	private BigDecimal sdm_con_db_id;
	private String db_bus_type;
	private String remark;
	private String sdm_tb_name_cn;
	private String sdm_tb_name_en;
	private String sdm_db_user;
	private String sdm_db_num;
	private String sdm_db_code;
	private BigDecimal sdm_des_id;
	private String sdm_sys_type;
	private String sdm_db_driver;
	private String sdm_db_name;

	public String getSdm_db_pwd() { return sdm_db_pwd; }
	public void setSdm_db_pwd(String sdm_db_pwd) {
		if(sdm_db_pwd==null) addNullValueField("sdm_db_pwd");
		this.sdm_db_pwd = sdm_db_pwd;
	}

	public String getSdm_db_type() { return sdm_db_type; }
	public void setSdm_db_type(String sdm_db_type) {
		if(sdm_db_type==null) addNullValueField("sdm_db_type");
		this.sdm_db_type = sdm_db_type;
	}

	public String getSdm_db_ip() { return sdm_db_ip; }
	public void setSdm_db_ip(String sdm_db_ip) {
		if(sdm_db_ip==null) addNullValueField("sdm_db_ip");
		this.sdm_db_ip = sdm_db_ip;
	}

	public String getDb_bus_class() { return db_bus_class; }
	public void setDb_bus_class(String db_bus_class) {
		if(db_bus_class==null) addNullValueField("db_bus_class");
		this.db_bus_class = db_bus_class;
	}

	public String getSdm_db_port() { return sdm_db_port; }
	public void setSdm_db_port(String sdm_db_port) {
		if(sdm_db_port==null) addNullValueField("sdm_db_port");
		this.sdm_db_port = sdm_db_port;
	}

	public BigDecimal getSdm_consum_id() { return sdm_consum_id; }
	public void setSdm_consum_id(BigDecimal sdm_consum_id) {
		if(sdm_consum_id==null) addNullValueField("sdm_consum_id");
		this.sdm_consum_id = sdm_consum_id;
	}

	public BigDecimal getSdm_con_db_id() { return sdm_con_db_id; }
	public void setSdm_con_db_id(BigDecimal sdm_con_db_id) {
		if(sdm_con_db_id==null) throw new BusinessException("Entity : SdmConToDb.sdm_con_db_id must not null!");
		this.sdm_con_db_id = sdm_con_db_id;
	}

	public String getDb_bus_type() { return db_bus_type; }
	public void setDb_bus_type(String db_bus_type) {
		if(db_bus_type==null) throw new BusinessException("Entity : SdmConToDb.db_bus_type must not null!");
		this.db_bus_type = db_bus_type;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public String getSdm_tb_name_cn() { return sdm_tb_name_cn; }
	public void setSdm_tb_name_cn(String sdm_tb_name_cn) {
		if(sdm_tb_name_cn==null) throw new BusinessException("Entity : SdmConToDb.sdm_tb_name_cn must not null!");
		this.sdm_tb_name_cn = sdm_tb_name_cn;
	}

	public String getSdm_tb_name_en() { return sdm_tb_name_en; }
	public void setSdm_tb_name_en(String sdm_tb_name_en) {
		if(sdm_tb_name_en==null) throw new BusinessException("Entity : SdmConToDb.sdm_tb_name_en must not null!");
		this.sdm_tb_name_en = sdm_tb_name_en;
	}

	public String getSdm_db_user() { return sdm_db_user; }
	public void setSdm_db_user(String sdm_db_user) {
		if(sdm_db_user==null) addNullValueField("sdm_db_user");
		this.sdm_db_user = sdm_db_user;
	}

	public String getSdm_db_num() { return sdm_db_num; }
	public void setSdm_db_num(String sdm_db_num) {
		if(sdm_db_num==null) addNullValueField("sdm_db_num");
		this.sdm_db_num = sdm_db_num;
	}

	public String getSdm_db_code() { return sdm_db_code; }
	public void setSdm_db_code(String sdm_db_code) {
		if(sdm_db_code==null) addNullValueField("sdm_db_code");
		this.sdm_db_code = sdm_db_code;
	}

	public BigDecimal getSdm_des_id() { return sdm_des_id; }
	public void setSdm_des_id(BigDecimal sdm_des_id) {
		if(sdm_des_id==null) throw new BusinessException("Entity : SdmConToDb.sdm_des_id must not null!");
		this.sdm_des_id = sdm_des_id;
	}

	public String getSdm_sys_type() { return sdm_sys_type; }
	public void setSdm_sys_type(String sdm_sys_type) {
		if(sdm_sys_type==null) addNullValueField("sdm_sys_type");
		this.sdm_sys_type = sdm_sys_type;
	}

	public String getSdm_db_driver() { return sdm_db_driver; }
	public void setSdm_db_driver(String sdm_db_driver) {
		if(sdm_db_driver==null) addNullValueField("sdm_db_driver");
		this.sdm_db_driver = sdm_db_driver;
	}

	public String getSdm_db_name() { return sdm_db_name; }
	public void setSdm_db_name(String sdm_db_name) {
		if(sdm_db_name==null) addNullValueField("sdm_db_name");
		this.sdm_db_name = sdm_db_name;
	}

}