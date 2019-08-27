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
@Table(tableName = "sdm_user_permission")
public class SdmUserPermission extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_user_permission";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("app_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal sdm_receive_id;
	private String application_status;
	private BigDecimal produce_user;
	private BigDecimal consume_user;
	private String remark;
	private BigDecimal topic_id;
	private BigDecimal app_id;

	public BigDecimal getSdm_receive_id() { return sdm_receive_id; }
	public void setSdm_receive_id(BigDecimal sdm_receive_id) {
		if(sdm_receive_id==null) throw new BusinessException("Entity : SdmUserPermission.sdm_receive_id must not null!");
		this.sdm_receive_id = sdm_receive_id;
	}

	public String getApplication_status() { return application_status; }
	public void setApplication_status(String application_status) {
		if(application_status==null) throw new BusinessException("Entity : SdmUserPermission.application_status must not null!");
		this.application_status = application_status;
	}

	public BigDecimal getProduce_user() { return produce_user; }
	public void setProduce_user(BigDecimal produce_user) {
		if(produce_user==null) throw new BusinessException("Entity : SdmUserPermission.produce_user must not null!");
		this.produce_user = produce_user;
	}

	public BigDecimal getConsume_user() { return consume_user; }
	public void setConsume_user(BigDecimal consume_user) {
		if(consume_user==null) throw new BusinessException("Entity : SdmUserPermission.consume_user must not null!");
		this.consume_user = consume_user;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public BigDecimal getTopic_id() { return topic_id; }
	public void setTopic_id(BigDecimal topic_id) {
		if(topic_id==null) throw new BusinessException("Entity : SdmUserPermission.topic_id must not null!");
		this.topic_id = topic_id;
	}

	public BigDecimal getApp_id() { return app_id; }
	public void setApp_id(BigDecimal app_id) {
		if(app_id==null) throw new BusinessException("Entity : SdmUserPermission.app_id must not null!");
		this.app_id = app_id;
	}

}