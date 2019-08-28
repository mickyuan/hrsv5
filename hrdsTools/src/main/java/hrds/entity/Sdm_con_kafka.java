package hrds.entity;
/**Auto Created by VBScript Do not modify!*/
import fd.ng.db.entity.TableEntity;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.entity.anno.Column;
import fd.ng.db.entity.anno.Table;
import hrds.exception.BusinessException;
import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 * 数据消费至kafka
 */
@Table(tableName = "sdm_con_kafka")
public class Sdm_con_kafka extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_con_kafka";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据消费至kafka */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("kafka_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long kafka_id; //kafka_id
	private String sdm_partition; //分区方式
	private String sdm_partition_name; //自定义分区类
	private String topic; //消息主题
	private String bootstrap_servers; //流服务主机
	private String acks; //成功确认等级
	private Long retries; //重试次数
	private String max_request_size; //单条记录阀值
	private Long batch_size; //批量大小
	private String linger_ms; //批处理等待时间
	private String buffer_memory; //缓存大小
	private String compression_type; //压缩类型
	private String sync; //是否同步
	private String interceptor_classes; //拦截器
	private Long sdm_des_id; //配置id
	private String kafka_bus_class; //kafka业务处理类
	private String kafka_bus_type; //kafka业务类类型

	/** 取得：kafka_id */
	public Long getKafka_id(){
		return kafka_id;
	}
	/** 设置：kafka_id */
	public void setKafka_id(Long kafka_id){
		this.kafka_id=kafka_id;
	}
	/** 设置：kafka_id */
	public void setKafka_id(String kafka_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(kafka_id)){
			this.kafka_id=new Long(kafka_id);
		}
	}
	/** 取得：分区方式 */
	public String getSdm_partition(){
		return sdm_partition;
	}
	/** 设置：分区方式 */
	public void setSdm_partition(String sdm_partition){
		this.sdm_partition=sdm_partition;
	}
	/** 取得：自定义分区类 */
	public String getSdm_partition_name(){
		return sdm_partition_name;
	}
	/** 设置：自定义分区类 */
	public void setSdm_partition_name(String sdm_partition_name){
		this.sdm_partition_name=sdm_partition_name;
	}
	/** 取得：消息主题 */
	public String getTopic(){
		return topic;
	}
	/** 设置：消息主题 */
	public void setTopic(String topic){
		this.topic=topic;
	}
	/** 取得：流服务主机 */
	public String getBootstrap_servers(){
		return bootstrap_servers;
	}
	/** 设置：流服务主机 */
	public void setBootstrap_servers(String bootstrap_servers){
		this.bootstrap_servers=bootstrap_servers;
	}
	/** 取得：成功确认等级 */
	public String getAcks(){
		return acks;
	}
	/** 设置：成功确认等级 */
	public void setAcks(String acks){
		this.acks=acks;
	}
	/** 取得：重试次数 */
	public Long getRetries(){
		return retries;
	}
	/** 设置：重试次数 */
	public void setRetries(Long retries){
		this.retries=retries;
	}
	/** 设置：重试次数 */
	public void setRetries(String retries){
		if(!fd.ng.core.utils.StringUtil.isEmpty(retries)){
			this.retries=new Long(retries);
		}
	}
	/** 取得：单条记录阀值 */
	public String getMax_request_size(){
		return max_request_size;
	}
	/** 设置：单条记录阀值 */
	public void setMax_request_size(String max_request_size){
		this.max_request_size=max_request_size;
	}
	/** 取得：批量大小 */
	public Long getBatch_size(){
		return batch_size;
	}
	/** 设置：批量大小 */
	public void setBatch_size(Long batch_size){
		this.batch_size=batch_size;
	}
	/** 设置：批量大小 */
	public void setBatch_size(String batch_size){
		if(!fd.ng.core.utils.StringUtil.isEmpty(batch_size)){
			this.batch_size=new Long(batch_size);
		}
	}
	/** 取得：批处理等待时间 */
	public String getLinger_ms(){
		return linger_ms;
	}
	/** 设置：批处理等待时间 */
	public void setLinger_ms(String linger_ms){
		this.linger_ms=linger_ms;
	}
	/** 取得：缓存大小 */
	public String getBuffer_memory(){
		return buffer_memory;
	}
	/** 设置：缓存大小 */
	public void setBuffer_memory(String buffer_memory){
		this.buffer_memory=buffer_memory;
	}
	/** 取得：压缩类型 */
	public String getCompression_type(){
		return compression_type;
	}
	/** 设置：压缩类型 */
	public void setCompression_type(String compression_type){
		this.compression_type=compression_type;
	}
	/** 取得：是否同步 */
	public String getSync(){
		return sync;
	}
	/** 设置：是否同步 */
	public void setSync(String sync){
		this.sync=sync;
	}
	/** 取得：拦截器 */
	public String getInterceptor_classes(){
		return interceptor_classes;
	}
	/** 设置：拦截器 */
	public void setInterceptor_classes(String interceptor_classes){
		this.interceptor_classes=interceptor_classes;
	}
	/** 取得：配置id */
	public Long getSdm_des_id(){
		return sdm_des_id;
	}
	/** 设置：配置id */
	public void setSdm_des_id(Long sdm_des_id){
		this.sdm_des_id=sdm_des_id;
	}
	/** 设置：配置id */
	public void setSdm_des_id(String sdm_des_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(sdm_des_id)){
			this.sdm_des_id=new Long(sdm_des_id);
		}
	}
	/** 取得：kafka业务处理类 */
	public String getKafka_bus_class(){
		return kafka_bus_class;
	}
	/** 设置：kafka业务处理类 */
	public void setKafka_bus_class(String kafka_bus_class){
		this.kafka_bus_class=kafka_bus_class;
	}
	/** 取得：kafka业务类类型 */
	public String getKafka_bus_type(){
		return kafka_bus_type;
	}
	/** 设置：kafka业务类类型 */
	public void setKafka_bus_type(String kafka_bus_type){
		this.kafka_bus_type=kafka_bus_type;
	}
}
