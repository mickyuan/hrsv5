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
@Table(tableName = "source_folder_attribute")
public class SourceFolderAttribute extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "source_folder_attribute";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("folder_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal folders_in_no;
	private String original_create_time;
	private String storage_date;
	private BigDecimal agent_id;
	private BigDecimal folder_size;
	private String location_in_hdfs;
	private String folder_name;
	private BigDecimal source_id;
	private BigDecimal folder_id;
	private BigDecimal super_id;
	private String original_create_date;
	private String storage_time;

	public BigDecimal getFolders_in_no() { return folders_in_no; }
	public void setFolders_in_no(BigDecimal folders_in_no) {
		if(folders_in_no==null) throw new BusinessException("Entity : SourceFolderAttribute.folders_in_no must not null!");
		this.folders_in_no = folders_in_no;
	}

	public String getOriginal_create_time() { return original_create_time; }
	public void setOriginal_create_time(String original_create_time) {
		if(original_create_time==null) throw new BusinessException("Entity : SourceFolderAttribute.original_create_time must not null!");
		this.original_create_time = original_create_time;
	}

	public String getStorage_date() { return storage_date; }
	public void setStorage_date(String storage_date) {
		if(storage_date==null) throw new BusinessException("Entity : SourceFolderAttribute.storage_date must not null!");
		this.storage_date = storage_date;
	}

	public BigDecimal getAgent_id() { return agent_id; }
	public void setAgent_id(BigDecimal agent_id) {
		if(agent_id==null) throw new BusinessException("Entity : SourceFolderAttribute.agent_id must not null!");
		this.agent_id = agent_id;
	}

	public BigDecimal getFolder_size() { return folder_size; }
	public void setFolder_size(BigDecimal folder_size) {
		if(folder_size==null) throw new BusinessException("Entity : SourceFolderAttribute.folder_size must not null!");
		this.folder_size = folder_size;
	}

	public String getLocation_in_hdfs() { return location_in_hdfs; }
	public void setLocation_in_hdfs(String location_in_hdfs) {
		if(location_in_hdfs==null) throw new BusinessException("Entity : SourceFolderAttribute.location_in_hdfs must not null!");
		this.location_in_hdfs = location_in_hdfs;
	}

	public String getFolder_name() { return folder_name; }
	public void setFolder_name(String folder_name) {
		if(folder_name==null) throw new BusinessException("Entity : SourceFolderAttribute.folder_name must not null!");
		this.folder_name = folder_name;
	}

	public BigDecimal getSource_id() { return source_id; }
	public void setSource_id(BigDecimal source_id) {
		if(source_id==null) throw new BusinessException("Entity : SourceFolderAttribute.source_id must not null!");
		this.source_id = source_id;
	}

	public BigDecimal getFolder_id() { return folder_id; }
	public void setFolder_id(BigDecimal folder_id) {
		if(folder_id==null) throw new BusinessException("Entity : SourceFolderAttribute.folder_id must not null!");
		this.folder_id = folder_id;
	}

	public BigDecimal getSuper_id() { return super_id; }
	public void setSuper_id(BigDecimal super_id) {
		if(super_id==null) addNullValueField("super_id");
		this.super_id = super_id;
	}

	public String getOriginal_create_date() { return original_create_date; }
	public void setOriginal_create_date(String original_create_date) {
		if(original_create_date==null) throw new BusinessException("Entity : SourceFolderAttribute.original_create_date must not null!");
		this.original_create_date = original_create_date;
	}

	public String getStorage_time() { return storage_time; }
	public void setStorage_time(String storage_time) {
		if(storage_time==null) throw new BusinessException("Entity : SourceFolderAttribute.storage_time must not null!");
		this.storage_time = storage_time;
	}

}