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
@Table(tableName = "sdm_topic_info")
public class SdmTopicInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_topic_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("topic_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String create_time;
	private BigDecimal user_id;
	private String sdm_zk_host;
	private BigDecimal topic_id;
	private BigDecimal sdm_replication;
	private String create_date;
	private String sdm_top_cn_name;
	private String sdm_top_value;
	private BigDecimal sdm_partition;
	private String sdm_top_name;

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : SdmTopicInfo.create_time must not null!");
		this.create_time = create_time;
	}

	public BigDecimal getUser_id() { return user_id; }
	public void setUser_id(BigDecimal user_id) {
		if(user_id==null) throw new BusinessException("Entity : SdmTopicInfo.user_id must not null!");
		this.user_id = user_id;
	}

	public String getSdm_zk_host() { return sdm_zk_host; }
	public void setSdm_zk_host(String sdm_zk_host) {
		if(sdm_zk_host==null) throw new BusinessException("Entity : SdmTopicInfo.sdm_zk_host must not null!");
		this.sdm_zk_host = sdm_zk_host;
	}

	public BigDecimal getTopic_id() { return topic_id; }
	public void setTopic_id(BigDecimal topic_id) {
		if(topic_id==null) throw new BusinessException("Entity : SdmTopicInfo.topic_id must not null!");
		this.topic_id = topic_id;
	}

	public BigDecimal getSdm_replication() { return sdm_replication; }
	public void setSdm_replication(BigDecimal sdm_replication) {
		if(sdm_replication==null) throw new BusinessException("Entity : SdmTopicInfo.sdm_replication must not null!");
		this.sdm_replication = sdm_replication;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : SdmTopicInfo.create_date must not null!");
		this.create_date = create_date;
	}

	public String getSdm_top_cn_name() { return sdm_top_cn_name; }
	public void setSdm_top_cn_name(String sdm_top_cn_name) {
		if(sdm_top_cn_name==null) throw new BusinessException("Entity : SdmTopicInfo.sdm_top_cn_name must not null!");
		this.sdm_top_cn_name = sdm_top_cn_name;
	}

	public String getSdm_top_value() { return sdm_top_value; }
	public void setSdm_top_value(String sdm_top_value) {
		if(sdm_top_value==null) addNullValueField("sdm_top_value");
		this.sdm_top_value = sdm_top_value;
	}

	public BigDecimal getSdm_partition() { return sdm_partition; }
	public void setSdm_partition(BigDecimal sdm_partition) {
		if(sdm_partition==null) throw new BusinessException("Entity : SdmTopicInfo.sdm_partition must not null!");
		this.sdm_partition = sdm_partition;
	}

	public String getSdm_top_name() { return sdm_top_name; }
	public void setSdm_top_name(String sdm_top_name) {
		if(sdm_top_name==null) throw new BusinessException("Entity : SdmTopicInfo.sdm_top_name must not null!");
		this.sdm_top_name = sdm_top_name;
	}

}