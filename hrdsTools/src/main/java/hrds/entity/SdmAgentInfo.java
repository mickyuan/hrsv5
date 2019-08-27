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
@Table(tableName = "sdm_agent_info")
public class SdmAgentInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_agent_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("sdm_agent_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String sdm_agent_type;
	private String create_time;
	private BigDecimal create_id;
	private BigDecimal user_id;
	private BigDecimal sdm_agent_id;
	private BigDecimal sdm_source_id;
	private String sdm_agent_status;
	private String sdm_agent_name;
	private String sdm_agent_port;
	private String remark;
	private String sdm_agent_ip;
	private String create_date;

	public String getSdm_agent_type() { return sdm_agent_type; }
	public void setSdm_agent_type(String sdm_agent_type) {
		if(sdm_agent_type==null) throw new BusinessException("Entity : SdmAgentInfo.sdm_agent_type must not null!");
		this.sdm_agent_type = sdm_agent_type;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : SdmAgentInfo.create_time must not null!");
		this.create_time = create_time;
	}

	public BigDecimal getCreate_id() { return create_id; }
	public void setCreate_id(BigDecimal create_id) {
		if(create_id==null) throw new BusinessException("Entity : SdmAgentInfo.create_id must not null!");
		this.create_id = create_id;
	}

	public BigDecimal getUser_id() { return user_id; }
	public void setUser_id(BigDecimal user_id) {
		if(user_id==null) throw new BusinessException("Entity : SdmAgentInfo.user_id must not null!");
		this.user_id = user_id;
	}

	public BigDecimal getSdm_agent_id() { return sdm_agent_id; }
	public void setSdm_agent_id(BigDecimal sdm_agent_id) {
		if(sdm_agent_id==null) throw new BusinessException("Entity : SdmAgentInfo.sdm_agent_id must not null!");
		this.sdm_agent_id = sdm_agent_id;
	}

	public BigDecimal getSdm_source_id() { return sdm_source_id; }
	public void setSdm_source_id(BigDecimal sdm_source_id) {
		if(sdm_source_id==null) addNullValueField("sdm_source_id");
		this.sdm_source_id = sdm_source_id;
	}

	public String getSdm_agent_status() { return sdm_agent_status; }
	public void setSdm_agent_status(String sdm_agent_status) {
		if(sdm_agent_status==null) throw new BusinessException("Entity : SdmAgentInfo.sdm_agent_status must not null!");
		this.sdm_agent_status = sdm_agent_status;
	}

	public String getSdm_agent_name() { return sdm_agent_name; }
	public void setSdm_agent_name(String sdm_agent_name) {
		if(sdm_agent_name==null) throw new BusinessException("Entity : SdmAgentInfo.sdm_agent_name must not null!");
		this.sdm_agent_name = sdm_agent_name;
	}

	public String getSdm_agent_port() { return sdm_agent_port; }
	public void setSdm_agent_port(String sdm_agent_port) {
		if(sdm_agent_port==null) throw new BusinessException("Entity : SdmAgentInfo.sdm_agent_port must not null!");
		this.sdm_agent_port = sdm_agent_port;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public String getSdm_agent_ip() { return sdm_agent_ip; }
	public void setSdm_agent_ip(String sdm_agent_ip) {
		if(sdm_agent_ip==null) throw new BusinessException("Entity : SdmAgentInfo.sdm_agent_ip must not null!");
		this.sdm_agent_ip = sdm_agent_ip;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : SdmAgentInfo.create_date must not null!");
		this.create_date = create_date;
	}

}