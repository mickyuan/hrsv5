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
@Table(tableName = "datatable_info_other")
public class DatatableInfoOther extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "datatable_info_other";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("other_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String external_redis;
	private String redis_port;
	private String database_driver;
	private String exredis_success;
	private String mpp_pass;
	private String exsolr_success;
	private String exhbase_success;
	private String exkafka_success;
	private String redis_user;
	private String remark;
	private String zk_host;
	private String external_mpp;
	private String database_type;
	private String external_solr;
	private String redis_ip;
	private String kafka_ip;
	private BigDecimal other_id;
	private BigDecimal kafka_bk_num;
	private BigDecimal topic_number;
	private String is_kafka;
	private String redis_separator;
	private String mpp_ip;
	private String kafka_topic;
	private String mpp_database;
	private String jdbc_url;
	private String exmpp_success;
	private String kafka_port;
	private String slolr_collect;
	private BigDecimal datatable_id;
	private String hbasesite_path;
	private String mpp_name;
	private String mpp_port;
	private String external_hbase;

	public String getExternal_redis() { return external_redis; }
	public void setExternal_redis(String external_redis) {
		if(external_redis==null) throw new BusinessException("Entity : DatatableInfoOther.external_redis must not null!");
		this.external_redis = external_redis;
	}

	public String getRedis_port() { return redis_port; }
	public void setRedis_port(String redis_port) {
		if(redis_port==null) addNullValueField("redis_port");
		this.redis_port = redis_port;
	}

	public String getDatabase_driver() { return database_driver; }
	public void setDatabase_driver(String database_driver) {
		if(database_driver==null) addNullValueField("database_driver");
		this.database_driver = database_driver;
	}

	public String getExredis_success() { return exredis_success; }
	public void setExredis_success(String exredis_success) {
		if(exredis_success==null) addNullValueField("exredis_success");
		this.exredis_success = exredis_success;
	}

	public String getMpp_pass() { return mpp_pass; }
	public void setMpp_pass(String mpp_pass) {
		if(mpp_pass==null) addNullValueField("mpp_pass");
		this.mpp_pass = mpp_pass;
	}

	public String getExsolr_success() { return exsolr_success; }
	public void setExsolr_success(String exsolr_success) {
		if(exsolr_success==null) addNullValueField("exsolr_success");
		this.exsolr_success = exsolr_success;
	}

	public String getExhbase_success() { return exhbase_success; }
	public void setExhbase_success(String exhbase_success) {
		if(exhbase_success==null) addNullValueField("exhbase_success");
		this.exhbase_success = exhbase_success;
	}

	public String getExkafka_success() { return exkafka_success; }
	public void setExkafka_success(String exkafka_success) {
		if(exkafka_success==null) addNullValueField("exkafka_success");
		this.exkafka_success = exkafka_success;
	}

	public String getRedis_user() { return redis_user; }
	public void setRedis_user(String redis_user) {
		if(redis_user==null) addNullValueField("redis_user");
		this.redis_user = redis_user;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public String getZk_host() { return zk_host; }
	public void setZk_host(String zk_host) {
		if(zk_host==null) addNullValueField("zk_host");
		this.zk_host = zk_host;
	}

	public String getExternal_mpp() { return external_mpp; }
	public void setExternal_mpp(String external_mpp) {
		if(external_mpp==null) throw new BusinessException("Entity : DatatableInfoOther.external_mpp must not null!");
		this.external_mpp = external_mpp;
	}

	public String getDatabase_type() { return database_type; }
	public void setDatabase_type(String database_type) {
		if(database_type==null) addNullValueField("database_type");
		this.database_type = database_type;
	}

	public String getExternal_solr() { return external_solr; }
	public void setExternal_solr(String external_solr) {
		if(external_solr==null) throw new BusinessException("Entity : DatatableInfoOther.external_solr must not null!");
		this.external_solr = external_solr;
	}

	public String getRedis_ip() { return redis_ip; }
	public void setRedis_ip(String redis_ip) {
		if(redis_ip==null) addNullValueField("redis_ip");
		this.redis_ip = redis_ip;
	}

	public String getKafka_ip() { return kafka_ip; }
	public void setKafka_ip(String kafka_ip) {
		if(kafka_ip==null) addNullValueField("kafka_ip");
		this.kafka_ip = kafka_ip;
	}

	public BigDecimal getOther_id() { return other_id; }
	public void setOther_id(BigDecimal other_id) {
		if(other_id==null) throw new BusinessException("Entity : DatatableInfoOther.other_id must not null!");
		this.other_id = other_id;
	}

	public BigDecimal getKafka_bk_num() { return kafka_bk_num; }
	public void setKafka_bk_num(BigDecimal kafka_bk_num) {
		if(kafka_bk_num==null) addNullValueField("kafka_bk_num");
		this.kafka_bk_num = kafka_bk_num;
	}

	public BigDecimal getTopic_number() { return topic_number; }
	public void setTopic_number(BigDecimal topic_number) {
		if(topic_number==null) addNullValueField("topic_number");
		this.topic_number = topic_number;
	}

	public String getIs_kafka() { return is_kafka; }
	public void setIs_kafka(String is_kafka) {
		if(is_kafka==null) throw new BusinessException("Entity : DatatableInfoOther.is_kafka must not null!");
		this.is_kafka = is_kafka;
	}

	public String getRedis_separator() { return redis_separator; }
	public void setRedis_separator(String redis_separator) {
		if(redis_separator==null) addNullValueField("redis_separator");
		this.redis_separator = redis_separator;
	}

	public String getMpp_ip() { return mpp_ip; }
	public void setMpp_ip(String mpp_ip) {
		if(mpp_ip==null) addNullValueField("mpp_ip");
		this.mpp_ip = mpp_ip;
	}

	public String getKafka_topic() { return kafka_topic; }
	public void setKafka_topic(String kafka_topic) {
		if(kafka_topic==null) addNullValueField("kafka_topic");
		this.kafka_topic = kafka_topic;
	}

	public String getMpp_database() { return mpp_database; }
	public void setMpp_database(String mpp_database) {
		if(mpp_database==null) addNullValueField("mpp_database");
		this.mpp_database = mpp_database;
	}

	public String getJdbc_url() { return jdbc_url; }
	public void setJdbc_url(String jdbc_url) {
		if(jdbc_url==null) addNullValueField("jdbc_url");
		this.jdbc_url = jdbc_url;
	}

	public String getExmpp_success() { return exmpp_success; }
	public void setExmpp_success(String exmpp_success) {
		if(exmpp_success==null) addNullValueField("exmpp_success");
		this.exmpp_success = exmpp_success;
	}

	public String getKafka_port() { return kafka_port; }
	public void setKafka_port(String kafka_port) {
		if(kafka_port==null) addNullValueField("kafka_port");
		this.kafka_port = kafka_port;
	}

	public String getSlolr_collect() { return slolr_collect; }
	public void setSlolr_collect(String slolr_collect) {
		if(slolr_collect==null) addNullValueField("slolr_collect");
		this.slolr_collect = slolr_collect;
	}

	public BigDecimal getDatatable_id() { return datatable_id; }
	public void setDatatable_id(BigDecimal datatable_id) {
		if(datatable_id==null) addNullValueField("datatable_id");
		this.datatable_id = datatable_id;
	}

	public String getHbasesite_path() { return hbasesite_path; }
	public void setHbasesite_path(String hbasesite_path) {
		if(hbasesite_path==null) addNullValueField("hbasesite_path");
		this.hbasesite_path = hbasesite_path;
	}

	public String getMpp_name() { return mpp_name; }
	public void setMpp_name(String mpp_name) {
		if(mpp_name==null) addNullValueField("mpp_name");
		this.mpp_name = mpp_name;
	}

	public String getMpp_port() { return mpp_port; }
	public void setMpp_port(String mpp_port) {
		if(mpp_port==null) addNullValueField("mpp_port");
		this.mpp_port = mpp_port;
	}

	public String getExternal_hbase() { return external_hbase; }
	public void setExternal_hbase(String external_hbase) {
		if(external_hbase==null) throw new BusinessException("Entity : DatatableInfoOther.external_hbase must not null!");
		this.external_hbase = external_hbase;
	}

}