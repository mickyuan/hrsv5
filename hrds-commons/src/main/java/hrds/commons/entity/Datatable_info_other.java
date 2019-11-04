package hrds.commons.entity;
/**
 * Auto Created by VBScript Do not modify!
 */

import fd.ng.core.utils.StringUtil;
import hrds.commons.entity.fdentity.ProjectTableEntity;

import java.util.HashSet;
import java.util.Set;

/**
 * 集市数据表外部存储信息表
 */
public class Datatable_info_other extends ProjectTableEntity {

	public static final String TableName = "datatable_info_other";
	private Long other_id; //外部存储id
	private String remark; //备注
	private String mpp_ip; //外部mpp表IP
	private String mpp_port; //外部mpp表端口
	private String mpp_database; //外部表mpp数据库
	private String mpp_name; //外部表mpp用户名
	private String mpp_pass; //外部表mpp密码
	private String slolr_collect; //外部表solr连接信息
	private String redis_ip; //外部redis连接ip
	private String redis_port; //外部redis端口
	private String external_hbase; //是否使用外部hbase
	private String external_mpp; //是否使用外部mpp
	private String external_solr; //是否外部solr
	private String external_redis; //是否外部redis
	private String exhbase_success; //外部hbase是否执行成功
	private String exmpp_success; //外部mpp是否成功
	private String exsolr_success; //外部solr是否成功
	private String exredis_success; //外部redis是否成功
	private Long datatable_id; //数据表id
	private String hbasesite_path; //外部集群配置文件路径
	private String database_type; //数据库类型
	private String database_driver; //数据库驱动类型
	private String is_kafka; //是否外部kafka
	private String kafka_ip; //kafkaIP
	private String kafka_port; //kafka端口
	private String kafka_topic; //kafkatopic
	private Long topic_number; //topic分区数
	private String zk_host; //zookeeper地址
	private Long kafka_bk_num; //kafka备份数
	private String redis_user; //redis用户
	private String redis_separator; //redis分隔符
	private String exkafka_success; //外部kafka是否成功
	private String jdbc_url; //数据连接url

	/**
	 * 取得：外部存储id
	 */
	public Long getOther_id() {
		return other_id;
	}

	/**
	 * 设置：外部存储id
	 */
	public void setOther_id(Long other_id) {
		this.other_id = other_id;
	}

	/**
	 * 设置：外部存储id
	 */
	public void setOther_id(String other_id) {
		if (!StringUtil.isEmpty(other_id))
			this.other_id = new Long(other_id);
	}

	/**
	 * 取得：备注
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * 设置：备注
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

	/**
	 * 取得：外部mpp表IP
	 */
	public String getMpp_ip() {
		return mpp_ip;
	}

	/**
	 * 设置：外部mpp表IP
	 */
	public void setMpp_ip(String mpp_ip) {
		this.mpp_ip = mpp_ip;
	}

	/**
	 * 取得：外部mpp表端口
	 */
	public String getMpp_port() {
		return mpp_port;
	}

	/**
	 * 设置：外部mpp表端口
	 */
	public void setMpp_port(String mpp_port) {
		this.mpp_port = mpp_port;
	}

	/**
	 * 取得：外部表mpp数据库
	 */
	public String getMpp_database() {
		return mpp_database;
	}

	/**
	 * 设置：外部表mpp数据库
	 */
	public void setMpp_database(String mpp_database) {
		this.mpp_database = mpp_database;
	}

	/**
	 * 取得：外部表mpp用户名
	 */
	public String getMpp_name() {
		return mpp_name;
	}

	/**
	 * 设置：外部表mpp用户名
	 */
	public void setMpp_name(String mpp_name) {
		this.mpp_name = mpp_name;
	}

	/**
	 * 取得：外部表mpp密码
	 */
	public String getMpp_pass() {
		return mpp_pass;
	}

	/**
	 * 设置：外部表mpp密码
	 */
	public void setMpp_pass(String mpp_pass) {
		this.mpp_pass = mpp_pass;
	}

	/**
	 * 取得：外部表solr连接信息
	 */
	public String getSlolr_collect() {
		return slolr_collect;
	}

	/**
	 * 设置：外部表solr连接信息
	 */
	public void setSlolr_collect(String slolr_collect) {
		this.slolr_collect = slolr_collect;
	}

	/**
	 * 取得：外部redis连接ip
	 */
	public String getRedis_ip() {
		return redis_ip;
	}

	/**
	 * 设置：外部redis连接ip
	 */
	public void setRedis_ip(String redis_ip) {
		this.redis_ip = redis_ip;
	}

	/**
	 * 取得：外部redis端口
	 */
	public String getRedis_port() {
		return redis_port;
	}

	/**
	 * 设置：外部redis端口
	 */
	public void setRedis_port(String redis_port) {
		this.redis_port = redis_port;
	}

	/**
	 * 取得：是否使用外部hbase
	 */
	public String getExternal_hbase() {
		return external_hbase;
	}

	/**
	 * 设置：是否使用外部hbase
	 */
	public void setExternal_hbase(String external_hbase) {
		this.external_hbase = external_hbase;
	}

	/**
	 * 取得：是否使用外部mpp
	 */
	public String getExternal_mpp() {
		return external_mpp;
	}

	/**
	 * 设置：是否使用外部mpp
	 */
	public void setExternal_mpp(String external_mpp) {
		this.external_mpp = external_mpp;
	}

	/**
	 * 取得：是否外部solr
	 */
	public String getExternal_solr() {
		return external_solr;
	}

	/**
	 * 设置：是否外部solr
	 */
	public void setExternal_solr(String external_solr) {
		this.external_solr = external_solr;
	}

	/**
	 * 取得：是否外部redis
	 */
	public String getExternal_redis() {
		return external_redis;
	}

	/**
	 * 设置：是否外部redis
	 */
	public void setExternal_redis(String external_redis) {
		this.external_redis = external_redis;
	}

	/**
	 * 取得：外部hbase是否执行成功
	 */
	public String getExhbase_success() {
		return exhbase_success;
	}

	/**
	 * 设置：外部hbase是否执行成功
	 */
	public void setExhbase_success(String exhbase_success) {
		this.exhbase_success = exhbase_success;
	}

	/**
	 * 取得：外部mpp是否成功
	 */
	public String getExmpp_success() {
		return exmpp_success;
	}

	/**
	 * 设置：外部mpp是否成功
	 */
	public void setExmpp_success(String exmpp_success) {
		this.exmpp_success = exmpp_success;
	}

	/**
	 * 取得：外部solr是否成功
	 */
	public String getExsolr_success() {
		return exsolr_success;
	}

	/**
	 * 设置：外部solr是否成功
	 */
	public void setExsolr_success(String exsolr_success) {
		this.exsolr_success = exsolr_success;
	}

	/**
	 * 取得：外部redis是否成功
	 */
	public String getExredis_success() {
		return exredis_success;
	}

	/**
	 * 设置：外部redis是否成功
	 */
	public void setExredis_success(String exredis_success) {
		this.exredis_success = exredis_success;
	}

	/**
	 * 取得：数据表id
	 */
	public Long getDatatable_id() {
		return datatable_id;
	}

	/**
	 * 设置：数据表id
	 */
	public void setDatatable_id(Long datatable_id) {
		this.datatable_id = datatable_id;
	}

	/**
	 * 设置：数据表id
	 */
	public void setDatatable_id(String datatable_id) {
		if (!StringUtil.isEmpty(datatable_id))
			this.datatable_id = new Long(datatable_id);
	}

	/**
	 * 取得：外部集群配置文件路径
	 */
	public String getHbasesite_path() {
		return hbasesite_path;
	}

	/**
	 * 设置：外部集群配置文件路径
	 */
	public void setHbasesite_path(String hbasesite_path) {
		this.hbasesite_path = hbasesite_path;
	}

	/**
	 * 取得：数据库类型
	 */
	public String getDatabase_type() {
		return database_type;
	}

	/**
	 * 设置：数据库类型
	 */
	public void setDatabase_type(String database_type) {
		this.database_type = database_type;
	}

	/**
	 * 取得：数据库驱动类型
	 */
	public String getDatabase_driver() {
		return database_driver;
	}

	/**
	 * 设置：数据库驱动类型
	 */
	public void setDatabase_driver(String database_driver) {
		this.database_driver = database_driver;
	}

	/**
	 * 取得：是否外部kafka
	 */
	public String getIs_kafka() {
		return is_kafka;
	}

	/**
	 * 设置：是否外部kafka
	 */
	public void setIs_kafka(String is_kafka) {
		this.is_kafka = is_kafka;
	}

	/**
	 * 取得：kafkaIP
	 */
	public String getKafka_ip() {
		return kafka_ip;
	}

	/**
	 * 设置：kafkaIP
	 */
	public void setKafka_ip(String kafka_ip) {
		this.kafka_ip = kafka_ip;
	}

	/**
	 * 取得：kafka端口
	 */
	public String getKafka_port() {
		return kafka_port;
	}

	/**
	 * 设置：kafka端口
	 */
	public void setKafka_port(String kafka_port) {
		this.kafka_port = kafka_port;
	}

	/**
	 * 取得：kafkatopic
	 */
	public String getKafka_topic() {
		return kafka_topic;
	}

	/**
	 * 设置：kafkatopic
	 */
	public void setKafka_topic(String kafka_topic) {
		this.kafka_topic = kafka_topic;
	}

	/**
	 * 取得：topic分区数
	 */
	public Long getTopic_number() {
		return topic_number;
	}

	/**
	 * 设置：topic分区数
	 */
	public void setTopic_number(Long topic_number) {
		this.topic_number = topic_number;
	}

	/**
	 * 设置：topic分区数
	 */
	public void setTopic_number(String topic_number) {
		if (!StringUtil.isEmpty(topic_number))
			this.topic_number = new Long(topic_number);
	}

	/**
	 * 取得：zookeeper地址
	 */
	public String getZk_host() {
		return zk_host;
	}

	/**
	 * 设置：zookeeper地址
	 */
	public void setZk_host(String zk_host) {
		this.zk_host = zk_host;
	}

	/**
	 * 取得：kafka备份数
	 */
	public Long getKafka_bk_num() {
		return kafka_bk_num;
	}

	/**
	 * 设置：kafka备份数
	 */
	public void setKafka_bk_num(Long kafka_bk_num) {
		this.kafka_bk_num = kafka_bk_num;
	}

	/**
	 * 设置：kafka备份数
	 */
	public void setKafka_bk_num(String kafka_bk_num) {
		if (!StringUtil.isEmpty(kafka_bk_num))
			this.kafka_bk_num = new Long(kafka_bk_num);
	}

	/**
	 * 取得：redis用户
	 */
	public String getRedis_user() {
		return redis_user;
	}

	/**
	 * 设置：redis用户
	 */
	public void setRedis_user(String redis_user) {
		this.redis_user = redis_user;
	}

	/**
	 * 取得：redis分隔符
	 */
	public String getRedis_separator() {
		return redis_separator;
	}

	/**
	 * 设置：redis分隔符
	 */
	public void setRedis_separator(String redis_separator) {
		this.redis_separator = redis_separator;
	}

	/**
	 * 取得：外部kafka是否成功
	 */
	public String getExkafka_success() {
		return exkafka_success;
	}

	/**
	 * 设置：外部kafka是否成功
	 */
	public void setExkafka_success(String exkafka_success) {
		this.exkafka_success = exkafka_success;
	}

	/**
	 * 取得：数据连接url
	 */
	public String getJdbc_url() {
		return jdbc_url;
	}

	/**
	 * 设置：数据连接url
	 */
	public void setJdbc_url(String jdbc_url) {
		this.jdbc_url = jdbc_url;
	}

	private Set primaryKeys = new HashSet();

	public boolean isPrimaryKey(String name) {
		return primaryKeys.contains(name);
	}

	public String getPrimaryKey() {
		return primaryKeys.iterator().next().toString();
	}

	/**
	 * 集市数据表外部存储信息表
	 */
	public Datatable_info_other() {
		primaryKeys.add("other_id");
	}
}
