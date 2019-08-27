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
@Table(tableName = "source_file_attribute")
public class SourceFileAttribute extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "source_file_attribute";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("file_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String storage_date;
	private BigDecimal agent_id;
	private String table_name;
	private String collect_type;
	private String file_type;
	private String file_md5;
	private String meta_info;
	private String hbase_name;
	private BigDecimal collect_set_id;
	private String is_cache;
	private String is_in_hbase;
	private String original_update_date;
	private BigDecimal seqencing;
	private BigDecimal file_size;
	private String file_avro_path;
	private String storage_time;
	private BigDecimal file_avro_block;
	private String is_big_file;
	private String original_update_time;
	private String file_suffix;
	private String file_id;
	private String original_name;
	private String source_path;
	private BigDecimal source_id;
	private BigDecimal folder_id;

	public String getStorage_date() { return storage_date; }
	public void setStorage_date(String storage_date) {
		if(storage_date==null) throw new BusinessException("Entity : SourceFileAttribute.storage_date must not null!");
		this.storage_date = storage_date;
	}

	public BigDecimal getAgent_id() { return agent_id; }
	public void setAgent_id(BigDecimal agent_id) {
		if(agent_id==null) throw new BusinessException("Entity : SourceFileAttribute.agent_id must not null!");
		this.agent_id = agent_id;
	}

	public String getTable_name() { return table_name; }
	public void setTable_name(String table_name) {
		if(table_name==null) addNullValueField("table_name");
		this.table_name = table_name;
	}

	public String getCollect_type() { return collect_type; }
	public void setCollect_type(String collect_type) {
		if(collect_type==null) throw new BusinessException("Entity : SourceFileAttribute.collect_type must not null!");
		this.collect_type = collect_type;
	}

	public String getFile_type() { return file_type; }
	public void setFile_type(String file_type) {
		if(file_type==null) throw new BusinessException("Entity : SourceFileAttribute.file_type must not null!");
		this.file_type = file_type;
	}

	public String getFile_md5() { return file_md5; }
	public void setFile_md5(String file_md5) {
		if(file_md5==null) addNullValueField("file_md5");
		this.file_md5 = file_md5;
	}

	public String getMeta_info() { return meta_info; }
	public void setMeta_info(String meta_info) {
		if(meta_info==null) addNullValueField("meta_info");
		this.meta_info = meta_info;
	}

	public String getHbase_name() { return hbase_name; }
	public void setHbase_name(String hbase_name) {
		if(hbase_name==null) throw new BusinessException("Entity : SourceFileAttribute.hbase_name must not null!");
		this.hbase_name = hbase_name;
	}

	public BigDecimal getCollect_set_id() { return collect_set_id; }
	public void setCollect_set_id(BigDecimal collect_set_id) {
		if(collect_set_id==null) throw new BusinessException("Entity : SourceFileAttribute.collect_set_id must not null!");
		this.collect_set_id = collect_set_id;
	}

	public String getIs_cache() { return is_cache; }
	public void setIs_cache(String is_cache) {
		if(is_cache==null) addNullValueField("is_cache");
		this.is_cache = is_cache;
	}

	public String getIs_in_hbase() { return is_in_hbase; }
	public void setIs_in_hbase(String is_in_hbase) {
		if(is_in_hbase==null) throw new BusinessException("Entity : SourceFileAttribute.is_in_hbase must not null!");
		this.is_in_hbase = is_in_hbase;
	}

	public String getOriginal_update_date() { return original_update_date; }
	public void setOriginal_update_date(String original_update_date) {
		if(original_update_date==null) throw new BusinessException("Entity : SourceFileAttribute.original_update_date must not null!");
		this.original_update_date = original_update_date;
	}

	public BigDecimal getSeqencing() { return seqencing; }
	public void setSeqencing(BigDecimal seqencing) {
		if(seqencing==null) throw new BusinessException("Entity : SourceFileAttribute.seqencing must not null!");
		this.seqencing = seqencing;
	}

	public BigDecimal getFile_size() { return file_size; }
	public void setFile_size(BigDecimal file_size) {
		if(file_size==null) throw new BusinessException("Entity : SourceFileAttribute.file_size must not null!");
		this.file_size = file_size;
	}

	public String getFile_avro_path() { return file_avro_path; }
	public void setFile_avro_path(String file_avro_path) {
		if(file_avro_path==null) addNullValueField("file_avro_path");
		this.file_avro_path = file_avro_path;
	}

	public String getStorage_time() { return storage_time; }
	public void setStorage_time(String storage_time) {
		if(storage_time==null) throw new BusinessException("Entity : SourceFileAttribute.storage_time must not null!");
		this.storage_time = storage_time;
	}

	public BigDecimal getFile_avro_block() { return file_avro_block; }
	public void setFile_avro_block(BigDecimal file_avro_block) {
		if(file_avro_block==null) addNullValueField("file_avro_block");
		this.file_avro_block = file_avro_block;
	}

	public String getIs_big_file() { return is_big_file; }
	public void setIs_big_file(String is_big_file) {
		if(is_big_file==null) addNullValueField("is_big_file");
		this.is_big_file = is_big_file;
	}

	public String getOriginal_update_time() { return original_update_time; }
	public void setOriginal_update_time(String original_update_time) {
		if(original_update_time==null) throw new BusinessException("Entity : SourceFileAttribute.original_update_time must not null!");
		this.original_update_time = original_update_time;
	}

	public String getFile_suffix() { return file_suffix; }
	public void setFile_suffix(String file_suffix) {
		if(file_suffix==null) throw new BusinessException("Entity : SourceFileAttribute.file_suffix must not null!");
		this.file_suffix = file_suffix;
	}

	public String getFile_id() { return file_id; }
	public void setFile_id(String file_id) {
		if(file_id==null) throw new BusinessException("Entity : SourceFileAttribute.file_id must not null!");
		this.file_id = file_id;
	}

	public String getOriginal_name() { return original_name; }
	public void setOriginal_name(String original_name) {
		if(original_name==null) throw new BusinessException("Entity : SourceFileAttribute.original_name must not null!");
		this.original_name = original_name;
	}

	public String getSource_path() { return source_path; }
	public void setSource_path(String source_path) {
		if(source_path==null) addNullValueField("source_path");
		this.source_path = source_path;
	}

	public BigDecimal getSource_id() { return source_id; }
	public void setSource_id(BigDecimal source_id) {
		if(source_id==null) throw new BusinessException("Entity : SourceFileAttribute.source_id must not null!");
		this.source_id = source_id;
	}

	public BigDecimal getFolder_id() { return folder_id; }
	public void setFolder_id(BigDecimal folder_id) {
		if(folder_id==null) addNullValueField("folder_id");
		this.folder_id = folder_id;
	}

}