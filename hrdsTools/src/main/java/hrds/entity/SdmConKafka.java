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
@Table(tableName = "sdm_con_kafka")
public class SdmConKafka extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_con_kafka";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("kafka_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal batch_size;
	private String kafka_bus_class;
	private String acks;
	private String compression_type;
	private String sdm_partition_name;
	private BigDecimal kafka_id;
	private String bootstrap_servers;
	private String linger_ms;
	private String sync;
	private String max_request_size;
	private BigDecimal retries;
	private String interceptor_classes;
	private String buffer_memory;
	private String kafka_bus_type;
	private String topic;
	private BigDecimal sdm_des_id;
	private String sdm_partition;

	public BigDecimal getBatch_size() { return batch_size; }
	public void setBatch_size(BigDecimal batch_size) {
		if(batch_size==null) throw new BusinessException("Entity : SdmConKafka.batch_size must not null!");
		this.batch_size = batch_size;
	}

	public String getKafka_bus_class() { return kafka_bus_class; }
	public void setKafka_bus_class(String kafka_bus_class) {
		if(kafka_bus_class==null) addNullValueField("kafka_bus_class");
		this.kafka_bus_class = kafka_bus_class;
	}

	public String getAcks() { return acks; }
	public void setAcks(String acks) {
		if(acks==null) throw new BusinessException("Entity : SdmConKafka.acks must not null!");
		this.acks = acks;
	}

	public String getCompression_type() { return compression_type; }
	public void setCompression_type(String compression_type) {
		if(compression_type==null) throw new BusinessException("Entity : SdmConKafka.compression_type must not null!");
		this.compression_type = compression_type;
	}

	public String getSdm_partition_name() { return sdm_partition_name; }
	public void setSdm_partition_name(String sdm_partition_name) {
		if(sdm_partition_name==null) addNullValueField("sdm_partition_name");
		this.sdm_partition_name = sdm_partition_name;
	}

	public BigDecimal getKafka_id() { return kafka_id; }
	public void setKafka_id(BigDecimal kafka_id) {
		if(kafka_id==null) throw new BusinessException("Entity : SdmConKafka.kafka_id must not null!");
		this.kafka_id = kafka_id;
	}

	public String getBootstrap_servers() { return bootstrap_servers; }
	public void setBootstrap_servers(String bootstrap_servers) {
		if(bootstrap_servers==null) throw new BusinessException("Entity : SdmConKafka.bootstrap_servers must not null!");
		this.bootstrap_servers = bootstrap_servers;
	}

	public String getLinger_ms() { return linger_ms; }
	public void setLinger_ms(String linger_ms) {
		if(linger_ms==null) throw new BusinessException("Entity : SdmConKafka.linger_ms must not null!");
		this.linger_ms = linger_ms;
	}

	public String getSync() { return sync; }
	public void setSync(String sync) {
		if(sync==null) throw new BusinessException("Entity : SdmConKafka.sync must not null!");
		this.sync = sync;
	}

	public String getMax_request_size() { return max_request_size; }
	public void setMax_request_size(String max_request_size) {
		if(max_request_size==null) throw new BusinessException("Entity : SdmConKafka.max_request_size must not null!");
		this.max_request_size = max_request_size;
	}

	public BigDecimal getRetries() { return retries; }
	public void setRetries(BigDecimal retries) {
		if(retries==null) throw new BusinessException("Entity : SdmConKafka.retries must not null!");
		this.retries = retries;
	}

	public String getInterceptor_classes() { return interceptor_classes; }
	public void setInterceptor_classes(String interceptor_classes) {
		if(interceptor_classes==null) addNullValueField("interceptor_classes");
		this.interceptor_classes = interceptor_classes;
	}

	public String getBuffer_memory() { return buffer_memory; }
	public void setBuffer_memory(String buffer_memory) {
		if(buffer_memory==null) throw new BusinessException("Entity : SdmConKafka.buffer_memory must not null!");
		this.buffer_memory = buffer_memory;
	}

	public String getKafka_bus_type() { return kafka_bus_type; }
	public void setKafka_bus_type(String kafka_bus_type) {
		if(kafka_bus_type==null) throw new BusinessException("Entity : SdmConKafka.kafka_bus_type must not null!");
		this.kafka_bus_type = kafka_bus_type;
	}

	public String getTopic() { return topic; }
	public void setTopic(String topic) {
		if(topic==null) throw new BusinessException("Entity : SdmConKafka.topic must not null!");
		this.topic = topic;
	}

	public BigDecimal getSdm_des_id() { return sdm_des_id; }
	public void setSdm_des_id(BigDecimal sdm_des_id) {
		if(sdm_des_id==null) throw new BusinessException("Entity : SdmConKafka.sdm_des_id must not null!");
		this.sdm_des_id = sdm_des_id;
	}

	public String getSdm_partition() { return sdm_partition; }
	public void setSdm_partition(String sdm_partition) {
		if(sdm_partition==null) throw new BusinessException("Entity : SdmConKafka.sdm_partition must not null!");
		this.sdm_partition = sdm_partition;
	}

}