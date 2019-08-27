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
@Table(tableName = "sys_table_info")
public class SysTableInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sys_table_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("info_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String storage_type;
	private String end_date;
	private String table_pattern;
	private String data_block_encoding;
	private String ch_name;
	private String remark;
	private String column_separator;
	private String is_trace;
	private String table_name;
	private String split_parm;
	private String bootstrap_servers;
	private String pre_split;
	private String record_format;
	private BigDecimal create_id;
	private String storage_path;
	private String storagedata;
	private String col_family;
	private String quote_character;
	private String escape_character;
	private String create_date;
	private Integer block_size;
	private String in_memory;
	private String file_format;
	private String table_type;
	private String source_format;
	private String compress;
	private String is_external;
	private String bloom_filter;
	private String hdfs_path;
	private String line_separator;
	private String table_space;
	private Integer versions;
	private BigDecimal info_id;
	private String bucketnumber;
	private String interval;
	private String kafka_subscribe;

	public String getStorage_type() { return storage_type; }
	public void setStorage_type(String storage_type) {
		if(storage_type==null) addNullValueField("storage_type");
		this.storage_type = storage_type;
	}

	public String getEnd_date() { return end_date; }
	public void setEnd_date(String end_date) {
		if(end_date==null) throw new BusinessException("Entity : SysTableInfo.end_date must not null!");
		this.end_date = end_date;
	}

	public String getTable_pattern() { return table_pattern; }
	public void setTable_pattern(String table_pattern) {
		if(table_pattern==null) addNullValueField("table_pattern");
		this.table_pattern = table_pattern;
	}

	public String getData_block_encoding() { return data_block_encoding; }
	public void setData_block_encoding(String data_block_encoding) {
		if(data_block_encoding==null) addNullValueField("data_block_encoding");
		this.data_block_encoding = data_block_encoding;
	}

	public String getCh_name() { return ch_name; }
	public void setCh_name(String ch_name) {
		if(ch_name==null) addNullValueField("ch_name");
		this.ch_name = ch_name;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public String getColumn_separator() { return column_separator; }
	public void setColumn_separator(String column_separator) {
		if(column_separator==null) addNullValueField("column_separator");
		this.column_separator = column_separator;
	}

	public String getIs_trace() { return is_trace; }
	public void setIs_trace(String is_trace) {
		if(is_trace==null) addNullValueField("is_trace");
		this.is_trace = is_trace;
	}

	public String getTable_name() { return table_name; }
	public void setTable_name(String table_name) {
		if(table_name==null) throw new BusinessException("Entity : SysTableInfo.table_name must not null!");
		this.table_name = table_name;
	}

	public String getSplit_parm() { return split_parm; }
	public void setSplit_parm(String split_parm) {
		if(split_parm==null) addNullValueField("split_parm");
		this.split_parm = split_parm;
	}

	public String getBootstrap_servers() { return bootstrap_servers; }
	public void setBootstrap_servers(String bootstrap_servers) {
		if(bootstrap_servers==null) addNullValueField("bootstrap_servers");
		this.bootstrap_servers = bootstrap_servers;
	}

	public String getPre_split() { return pre_split; }
	public void setPre_split(String pre_split) {
		if(pre_split==null) addNullValueField("pre_split");
		this.pre_split = pre_split;
	}

	public String getRecord_format() { return record_format; }
	public void setRecord_format(String record_format) {
		if(record_format==null) addNullValueField("record_format");
		this.record_format = record_format;
	}

	public BigDecimal getCreate_id() { return create_id; }
	public void setCreate_id(BigDecimal create_id) {
		if(create_id==null) addNullValueField("create_id");
		this.create_id = create_id;
	}

	public String getStorage_path() { return storage_path; }
	public void setStorage_path(String storage_path) {
		if(storage_path==null) addNullValueField("storage_path");
		this.storage_path = storage_path;
	}

	public String getStoragedata() { return storagedata; }
	public void setStoragedata(String storagedata) {
		if(storagedata==null) addNullValueField("storagedata");
		this.storagedata = storagedata;
	}

	public String getCol_family() { return col_family; }
	public void setCol_family(String col_family) {
		if(col_family==null) addNullValueField("col_family");
		this.col_family = col_family;
	}

	public String getQuote_character() { return quote_character; }
	public void setQuote_character(String quote_character) {
		if(quote_character==null) addNullValueField("quote_character");
		this.quote_character = quote_character;
	}

	public String getEscape_character() { return escape_character; }
	public void setEscape_character(String escape_character) {
		if(escape_character==null) addNullValueField("escape_character");
		this.escape_character = escape_character;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : SysTableInfo.create_date must not null!");
		this.create_date = create_date;
	}

	public Integer getBlock_size() { return block_size; }
	public void setBlock_size(Integer block_size) {
		if(block_size==null) addNullValueField("block_size");
		this.block_size = block_size;
	}

	public String getIn_memory() { return in_memory; }
	public void setIn_memory(String in_memory) {
		if(in_memory==null) addNullValueField("in_memory");
		this.in_memory = in_memory;
	}

	public String getFile_format() { return file_format; }
	public void setFile_format(String file_format) {
		if(file_format==null) addNullValueField("file_format");
		this.file_format = file_format;
	}

	public String getTable_type() { return table_type; }
	public void setTable_type(String table_type) {
		if(table_type==null) throw new BusinessException("Entity : SysTableInfo.table_type must not null!");
		this.table_type = table_type;
	}

	public String getSource_format() { return source_format; }
	public void setSource_format(String source_format) {
		if(source_format==null) addNullValueField("source_format");
		this.source_format = source_format;
	}

	public String getCompress() { return compress; }
	public void setCompress(String compress) {
		if(compress==null) addNullValueField("compress");
		this.compress = compress;
	}

	public String getIs_external() { return is_external; }
	public void setIs_external(String is_external) {
		if(is_external==null) addNullValueField("is_external");
		this.is_external = is_external;
	}

	public String getBloom_filter() { return bloom_filter; }
	public void setBloom_filter(String bloom_filter) {
		if(bloom_filter==null) addNullValueField("bloom_filter");
		this.bloom_filter = bloom_filter;
	}

	public String getHdfs_path() { return hdfs_path; }
	public void setHdfs_path(String hdfs_path) {
		if(hdfs_path==null) addNullValueField("hdfs_path");
		this.hdfs_path = hdfs_path;
	}

	public String getLine_separator() { return line_separator; }
	public void setLine_separator(String line_separator) {
		if(line_separator==null) addNullValueField("line_separator");
		this.line_separator = line_separator;
	}

	public String getTable_space() { return table_space; }
	public void setTable_space(String table_space) {
		if(table_space==null) addNullValueField("table_space");
		this.table_space = table_space;
	}

	public Integer getVersions() { return versions; }
	public void setVersions(Integer versions) {
		if(versions==null) addNullValueField("versions");
		this.versions = versions;
	}

	public BigDecimal getInfo_id() { return info_id; }
	public void setInfo_id(BigDecimal info_id) {
		if(info_id==null) throw new BusinessException("Entity : SysTableInfo.info_id must not null!");
		this.info_id = info_id;
	}

	public String getBucketnumber() { return bucketnumber; }
	public void setBucketnumber(String bucketnumber) {
		if(bucketnumber==null) addNullValueField("bucketnumber");
		this.bucketnumber = bucketnumber;
	}

	public String getInterval() { return interval; }
	public void setInterval(String interval) {
		if(interval==null) addNullValueField("interval");
		this.interval = interval;
	}

	public String getKafka_subscribe() { return kafka_subscribe; }
	public void setKafka_subscribe(String kafka_subscribe) {
		if(kafka_subscribe==null) addNullValueField("kafka_subscribe");
		this.kafka_subscribe = kafka_subscribe;
	}

}