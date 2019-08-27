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
@Table(tableName = "signal_file")
public class SignalFile extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "signal_file";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("signal_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String is_fullindex;
	private BigDecimal database_id;
	private String is_mpp;
	private BigDecimal signal_id;
	private String is_compression;
	private String is_solr_hbase;
	private String file_format;
	private String is_cbd;
	private String is_into_hbase;
	private String is_into_hive;
	private String table_type;

	public String getIs_fullindex() { return is_fullindex; }
	public void setIs_fullindex(String is_fullindex) {
		if(is_fullindex==null) throw new BusinessException("Entity : SignalFile.is_fullindex must not null!");
		this.is_fullindex = is_fullindex;
	}

	public BigDecimal getDatabase_id() { return database_id; }
	public void setDatabase_id(BigDecimal database_id) {
		if(database_id==null) throw new BusinessException("Entity : SignalFile.database_id must not null!");
		this.database_id = database_id;
	}

	public String getIs_mpp() { return is_mpp; }
	public void setIs_mpp(String is_mpp) {
		if(is_mpp==null) throw new BusinessException("Entity : SignalFile.is_mpp must not null!");
		this.is_mpp = is_mpp;
	}

	public BigDecimal getSignal_id() { return signal_id; }
	public void setSignal_id(BigDecimal signal_id) {
		if(signal_id==null) throw new BusinessException("Entity : SignalFile.signal_id must not null!");
		this.signal_id = signal_id;
	}

	public String getIs_compression() { return is_compression; }
	public void setIs_compression(String is_compression) {
		if(is_compression==null) throw new BusinessException("Entity : SignalFile.is_compression must not null!");
		this.is_compression = is_compression;
	}

	public String getIs_solr_hbase() { return is_solr_hbase; }
	public void setIs_solr_hbase(String is_solr_hbase) {
		if(is_solr_hbase==null) throw new BusinessException("Entity : SignalFile.is_solr_hbase must not null!");
		this.is_solr_hbase = is_solr_hbase;
	}

	public String getFile_format() { return file_format; }
	public void setFile_format(String file_format) {
		if(file_format==null) throw new BusinessException("Entity : SignalFile.file_format must not null!");
		this.file_format = file_format;
	}

	public String getIs_cbd() { return is_cbd; }
	public void setIs_cbd(String is_cbd) {
		if(is_cbd==null) throw new BusinessException("Entity : SignalFile.is_cbd must not null!");
		this.is_cbd = is_cbd;
	}

	public String getIs_into_hbase() { return is_into_hbase; }
	public void setIs_into_hbase(String is_into_hbase) {
		if(is_into_hbase==null) throw new BusinessException("Entity : SignalFile.is_into_hbase must not null!");
		this.is_into_hbase = is_into_hbase;
	}

	public String getIs_into_hive() { return is_into_hive; }
	public void setIs_into_hive(String is_into_hive) {
		if(is_into_hive==null) throw new BusinessException("Entity : SignalFile.is_into_hive must not null!");
		this.is_into_hive = is_into_hive;
	}

	public String getTable_type() { return table_type; }
	public void setTable_type(String table_type) {
		if(table_type==null) throw new BusinessException("Entity : SignalFile.table_type must not null!");
		this.table_type = table_type;
	}

}