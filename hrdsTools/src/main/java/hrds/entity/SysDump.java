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
@Table(tableName = "sys_dump")
public class SysDump extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sys_dump";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("dump_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String hdfs_path;
	private String file_name;
	private String bak_time;
	private String length;
	private String remark;
	private BigDecimal dump_id;
	private String bak_date;
	private String file_size;

	public String getHdfs_path() { return hdfs_path; }
	public void setHdfs_path(String hdfs_path) {
		if(hdfs_path==null) throw new BusinessException("Entity : SysDump.hdfs_path must not null!");
		this.hdfs_path = hdfs_path;
	}

	public String getFile_name() { return file_name; }
	public void setFile_name(String file_name) {
		if(file_name==null) throw new BusinessException("Entity : SysDump.file_name must not null!");
		this.file_name = file_name;
	}

	public String getBak_time() { return bak_time; }
	public void setBak_time(String bak_time) {
		if(bak_time==null) throw new BusinessException("Entity : SysDump.bak_time must not null!");
		this.bak_time = bak_time;
	}

	public String getLength() { return length; }
	public void setLength(String length) {
		if(length==null) throw new BusinessException("Entity : SysDump.length must not null!");
		this.length = length;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public BigDecimal getDump_id() { return dump_id; }
	public void setDump_id(BigDecimal dump_id) {
		if(dump_id==null) throw new BusinessException("Entity : SysDump.dump_id must not null!");
		this.dump_id = dump_id;
	}

	public String getBak_date() { return bak_date; }
	public void setBak_date(String bak_date) {
		if(bak_date==null) throw new BusinessException("Entity : SysDump.bak_date must not null!");
		this.bak_date = bak_date;
	}

	public String getFile_size() { return file_size; }
	public void setFile_size(String file_size) {
		if(file_size==null) throw new BusinessException("Entity : SysDump.file_size must not null!");
		this.file_size = file_size;
	}

}