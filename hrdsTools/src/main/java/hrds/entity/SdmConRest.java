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
@Table(tableName = "sdm_con_rest")
public class SdmConRest extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_con_rest";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("rest_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String rest_bus_class;
	private String rest_port;
	private String rest_bus_type;
	private String rest_ip;
	private BigDecimal sdm_des_id;
	private String remark;
	private BigDecimal rest_id;
	private String rest_parameter;

	public String getRest_bus_class() { return rest_bus_class; }
	public void setRest_bus_class(String rest_bus_class) {
		if(rest_bus_class==null) addNullValueField("rest_bus_class");
		this.rest_bus_class = rest_bus_class;
	}

	public String getRest_port() { return rest_port; }
	public void setRest_port(String rest_port) {
		if(rest_port==null) throw new BusinessException("Entity : SdmConRest.rest_port must not null!");
		this.rest_port = rest_port;
	}

	public String getRest_bus_type() { return rest_bus_type; }
	public void setRest_bus_type(String rest_bus_type) {
		if(rest_bus_type==null) throw new BusinessException("Entity : SdmConRest.rest_bus_type must not null!");
		this.rest_bus_type = rest_bus_type;
	}

	public String getRest_ip() { return rest_ip; }
	public void setRest_ip(String rest_ip) {
		if(rest_ip==null) throw new BusinessException("Entity : SdmConRest.rest_ip must not null!");
		this.rest_ip = rest_ip;
	}

	public BigDecimal getSdm_des_id() { return sdm_des_id; }
	public void setSdm_des_id(BigDecimal sdm_des_id) {
		if(sdm_des_id==null) throw new BusinessException("Entity : SdmConRest.sdm_des_id must not null!");
		this.sdm_des_id = sdm_des_id;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public BigDecimal getRest_id() { return rest_id; }
	public void setRest_id(BigDecimal rest_id) {
		if(rest_id==null) throw new BusinessException("Entity : SdmConRest.rest_id must not null!");
		this.rest_id = rest_id;
	}

	public String getRest_parameter() { return rest_parameter; }
	public void setRest_parameter(String rest_parameter) {
		if(rest_parameter==null) addNullValueField("rest_parameter");
		this.rest_parameter = rest_parameter;
	}

}