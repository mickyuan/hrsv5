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
@Table(tableName = "sdm_sp_stream")
public class SdmSpStream extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_sp_stream";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("sss_stream_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal sss_stream_id;
	private String sss_kafka_version;
	private String sss_bootstrap_server;
	private String sss_consumer_offset;
	private String sss_topic_name;
	private BigDecimal sdm_info_id;

	public BigDecimal getSss_stream_id() { return sss_stream_id; }
	public void setSss_stream_id(BigDecimal sss_stream_id) {
		if(sss_stream_id==null) throw new BusinessException("Entity : SdmSpStream.sss_stream_id must not null!");
		this.sss_stream_id = sss_stream_id;
	}

	public String getSss_kafka_version() { return sss_kafka_version; }
	public void setSss_kafka_version(String sss_kafka_version) {
		if(sss_kafka_version==null) throw new BusinessException("Entity : SdmSpStream.sss_kafka_version must not null!");
		this.sss_kafka_version = sss_kafka_version;
	}

	public String getSss_bootstrap_server() { return sss_bootstrap_server; }
	public void setSss_bootstrap_server(String sss_bootstrap_server) {
		if(sss_bootstrap_server==null) throw new BusinessException("Entity : SdmSpStream.sss_bootstrap_server must not null!");
		this.sss_bootstrap_server = sss_bootstrap_server;
	}

	public String getSss_consumer_offset() { return sss_consumer_offset; }
	public void setSss_consumer_offset(String sss_consumer_offset) {
		if(sss_consumer_offset==null) throw new BusinessException("Entity : SdmSpStream.sss_consumer_offset must not null!");
		this.sss_consumer_offset = sss_consumer_offset;
	}

	public String getSss_topic_name() { return sss_topic_name; }
	public void setSss_topic_name(String sss_topic_name) {
		if(sss_topic_name==null) throw new BusinessException("Entity : SdmSpStream.sss_topic_name must not null!");
		this.sss_topic_name = sss_topic_name;
	}

	public BigDecimal getSdm_info_id() { return sdm_info_id; }
	public void setSdm_info_id(BigDecimal sdm_info_id) {
		if(sdm_info_id==null) throw new BusinessException("Entity : SdmSpStream.sdm_info_id must not null!");
		this.sdm_info_id = sdm_info_id;
	}

}