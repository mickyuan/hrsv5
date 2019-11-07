package hrds.commons.entity;
/**
 * Auto Created by VBScript Do not modify!
 */

import fd.ng.core.utils.StringUtil;
import hrds.commons.entity.fdentity.ProjectTableEntity;

import java.util.HashSet;
import java.util.Set;

/**
 * 系统表创建信息
 */
public class Sys_table_info extends ProjectTableEntity {
	public static final String TableName = "sys_table_info";
	private Long info_id; //信息id
	private String table_space; //表空间名称
	private String table_name; //表名
	private String col_family; //列族
	private String in_memory; //IN_MEMORY
	private String bloom_filter; //Bloom过滤器
	private String compress; //是否压缩
	private Integer block_size; //数据块大小
	private String data_block_encoding; //数据块编码
	private Integer versions; //版本数
	private String pre_split; //预分区规则
	private String split_parm; //分区数
	private String remark; //备注
	private String ch_name; //表中文名称
	private String is_external; //是否为外部表
	private String storage_path; //存储位置
	private String storage_type; //存储类型
	private String line_separator; //行分隔符
	private String column_separator; //列分隔符
	private String quote_character; //引用符
	private String escape_character; //转义符
	private String table_type; //表的类型
	private String create_date; //开始日期
	private String end_date; //结束日期
	private String bucketnumber; //指定要创建的桶的数量
	private String table_pattern; //carbondta表模式
	private String source_format; //流表来源
	private String kafka_subscribe; //kafka主题
	private String bootstrap_servers; //kafka流服务主机
	private String record_format; //记录格式
	private String interval; //间隔时间
	private String hdfs_path; //hdfs路径
	private String file_format; //HDFS文件格式
	private Long create_id; //用户ID
	private String storagedata; //进数方式
	private String is_trace; //是否数据溯源

	/**
	 * 取得：信息id
	 */
	public Long getInfo_id() {
		return info_id;
	}

	/**
	 * 设置：信息id
	 */
	public void setInfo_id(Long info_id) {
		this.info_id = info_id;
	}

	/**
	 * 设置：信息id
	 */
	public void setInfo_id(String info_id) {
		if (!StringUtil.isEmpty(info_id))
			this.info_id = new Long(info_id);
	}

	/**
	 * 取得：表空间名称
	 */
	public String getTable_space() {
		return table_space;
	}

	/**
	 * 设置：表空间名称
	 */
	public void setTable_space(String table_space) {
		this.table_space = table_space;
	}

	/**
	 * 取得：表名
	 */
	public String getTable_name() {
		return table_name;
	}

	/**
	 * 设置：表名
	 */
	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}

	/**
	 * 取得：列族
	 */
	public String getCol_family() {
		return col_family;
	}

	/**
	 * 设置：列族
	 */
	public void setCol_family(String col_family) {
		this.col_family = col_family;
	}

	/**
	 * 取得：IN_MEMORY
	 */
	public String getIn_memory() {
		return in_memory;
	}

	/**
	 * 设置：IN_MEMORY
	 */
	public void setIn_memory(String in_memory) {
		this.in_memory = in_memory;
	}

	/**
	 * 取得：Bloom过滤器
	 */
	public String getBloom_filter() {
		return bloom_filter;
	}

	/**
	 * 设置：Bloom过滤器
	 */
	public void setBloom_filter(String bloom_filter) {
		this.bloom_filter = bloom_filter;
	}

	/**
	 * 取得：是否压缩
	 */
	public String getCompress() {
		return compress;
	}

	/**
	 * 设置：是否压缩
	 */
	public void setCompress(String compress) {
		this.compress = compress;
	}

	/**
	 * 取得：数据块大小
	 */
	public Integer getBlock_size() {
		return block_size;
	}

	/**
	 * 设置：数据块大小
	 */
	public void setBlock_size(Integer block_size) {
		this.block_size = block_size;
	}

	/**
	 * 设置：数据块大小
	 */
	public void setBlock_size(String block_size) {
		if (!StringUtil.isEmpty(block_size))
			this.block_size = new Integer(block_size);
	}

	/**
	 * 取得：数据块编码
	 */
	public String getData_block_encoding() {
		return data_block_encoding;
	}

	/**
	 * 设置：数据块编码
	 */
	public void setData_block_encoding(String data_block_encoding) {
		this.data_block_encoding = data_block_encoding;
	}

	/**
	 * 取得：版本数
	 */
	public Integer getVersions() {
		return versions;
	}

	/**
	 * 设置：版本数
	 */
	public void setVersions(Integer versions) {
		this.versions = versions;
	}

	/**
	 * 设置：版本数
	 */
	public void setVersions(String versions) {
		if (!StringUtil.isEmpty(versions))
			this.versions = new Integer(versions);
	}

	/**
	 * 取得：预分区规则
	 */
	public String getPre_split() {
		return pre_split;
	}

	/**
	 * 设置：预分区规则
	 */
	public void setPre_split(String pre_split) {
		this.pre_split = pre_split;
	}

	/**
	 * 取得：分区数
	 */
	public String getSplit_parm() {
		return split_parm;
	}

	/**
	 * 设置：分区数
	 */
	public void setSplit_parm(String split_parm) {
		this.split_parm = split_parm;
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
	 * 取得：表中文名称
	 */
	public String getCh_name() {
		return ch_name;
	}

	/**
	 * 设置：表中文名称
	 */
	public void setCh_name(String ch_name) {
		this.ch_name = ch_name;
	}

	/**
	 * 取得：是否为外部表
	 */
	public String getIs_external() {
		return is_external;
	}

	/**
	 * 设置：是否为外部表
	 */
	public void setIs_external(String is_external) {
		this.is_external = is_external;
	}

	/**
	 * 取得：存储位置
	 */
	public String getStorage_path() {
		return storage_path;
	}

	/**
	 * 设置：存储位置
	 */
	public void setStorage_path(String storage_path) {
		this.storage_path = storage_path;
	}

	/**
	 * 取得：存储类型
	 */
	public String getStorage_type() {
		return storage_type;
	}

	/**
	 * 设置：存储类型
	 */
	public void setStorage_type(String storage_type) {
		this.storage_type = storage_type;
	}

	/**
	 * 取得：行分隔符
	 */
	public String getLine_separator() {
		return line_separator;
	}

	/**
	 * 设置：行分隔符
	 */
	public void setLine_separator(String line_separator) {
		this.line_separator = line_separator;
	}

	/**
	 * 取得：列分隔符
	 */
	public String getColumn_separator() {
		return column_separator;
	}

	/**
	 * 设置：列分隔符
	 */
	public void setColumn_separator(String column_separator) {
		this.column_separator = column_separator;
	}

	/**
	 * 取得：引用符
	 */
	public String getQuote_character() {
		return quote_character;
	}

	/**
	 * 设置：引用符
	 */
	public void setQuote_character(String quote_character) {
		this.quote_character = quote_character;
	}

	/**
	 * 取得：转义符
	 */
	public String getEscape_character() {
		return escape_character;
	}

	/**
	 * 设置：转义符
	 */
	public void setEscape_character(String escape_character) {
		this.escape_character = escape_character;
	}

	/**
	 * 取得：表的类型
	 */
	public String getTable_type() {
		return table_type;
	}

	/**
	 * 设置：表的类型
	 */
	public void setTable_type(String table_type) {
		this.table_type = table_type;
	}

	/**
	 * 取得：开始日期
	 */
	public String getCreate_date() {
		return create_date;
	}

	/**
	 * 设置：开始日期
	 */
	public void setCreate_date(String create_date) {
		this.create_date = create_date;
	}

	/**
	 * 取得：结束日期
	 */
	public String getEnd_date() {
		return end_date;
	}

	/**
	 * 设置：结束日期
	 */
	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}

	/**
	 * 取得：指定要创建的桶的数量
	 */
	public String getBucketnumber() {
		return bucketnumber;
	}

	/**
	 * 设置：指定要创建的桶的数量
	 */
	public void setBucketnumber(String bucketnumber) {
		this.bucketnumber = bucketnumber;
	}

	/**
	 * 取得：carbondta表模式
	 */
	public String getTable_pattern() {
		return table_pattern;
	}

	/**
	 * 设置：carbondta表模式
	 */
	public void setTable_pattern(String table_pattern) {
		this.table_pattern = table_pattern;
	}

	/**
	 * 取得：流表来源
	 */
	public String getSource_format() {
		return source_format;
	}

	/**
	 * 设置：流表来源
	 */
	public void setSource_format(String source_format) {
		this.source_format = source_format;
	}

	/**
	 * 取得：kafka主题
	 */
	public String getKafka_subscribe() {
		return kafka_subscribe;
	}

	/**
	 * 设置：kafka主题
	 */
	public void setKafka_subscribe(String kafka_subscribe) {
		this.kafka_subscribe = kafka_subscribe;
	}

	/**
	 * 取得：kafka流服务主机
	 */
	public String getBootstrap_servers() {
		return bootstrap_servers;
	}

	/**
	 * 设置：kafka流服务主机
	 */
	public void setBootstrap_servers(String bootstrap_servers) {
		this.bootstrap_servers = bootstrap_servers;
	}

	/**
	 * 取得：记录格式
	 */
	public String getRecord_format() {
		return record_format;
	}

	/**
	 * 设置：记录格式
	 */
	public void setRecord_format(String record_format) {
		this.record_format = record_format;
	}

	/**
	 * 取得：间隔时间
	 */
	public String getInterval() {
		return interval;
	}

	/**
	 * 设置：间隔时间
	 */
	public void setInterval(String interval) {
		this.interval = interval;
	}

	/**
	 * 取得：hdfs路径
	 */
	public String getHdfs_path() {
		return hdfs_path;
	}

	/**
	 * 设置：hdfs路径
	 */
	public void setHdfs_path(String hdfs_path) {
		this.hdfs_path = hdfs_path;
	}

	/**
	 * 取得：HDFS文件格式
	 */
	public String getFile_format() {
		return file_format;
	}

	/**
	 * 设置：HDFS文件格式
	 */
	public void setFile_format(String file_format) {
		this.file_format = file_format;
	}

	/**
	 * 取得：用户ID
	 */
	public Long getCreate_id() {
		return create_id;
	}

	/**
	 * 设置：用户ID
	 */
	public void setCreate_id(Long create_id) {
		this.create_id = create_id;
	}

	/**
	 * 设置：用户ID
	 */
	public void setCreate_id(String create_id) {
		if (!StringUtil.isEmpty(create_id))
			this.create_id = new Long(create_id);
	}

	/**
	 * 取得：进数方式
	 */
	public String getStoragedata() {
		return storagedata;
	}

	/**
	 * 设置：进数方式
	 */
	public void setStoragedata(String storagedata) {
		this.storagedata = storagedata;
	}

	/**
	 * 取得：是否数据溯源
	 */
	public String getIs_trace() {
		return is_trace;
	}

	/**
	 * 设置：是否数据溯源
	 */
	public void setIs_trace(String is_trace) {
		this.is_trace = is_trace;
	}

	private Set primaryKeys = new HashSet();

	public boolean isPrimaryKey(String name) {
		return primaryKeys.contains(name);
	}

	public String getPrimaryKey() {
		return primaryKeys.iterator().next().toString();
	}

	/**
	 * 系统表创建信息
	 */
	public Sys_table_info() {
		primaryKeys.add("info_id");
	}
}
