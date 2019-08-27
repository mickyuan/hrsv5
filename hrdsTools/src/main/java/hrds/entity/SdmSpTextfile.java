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
@Table(tableName = "sdm_sp_textfile")
public class SdmSpTextfile extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_sp_textfile";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("tsst_extfile_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String sst_file_path;
	private String sst_schema;
	private BigDecimal tsst_extfile_id;
	private String sst_file_type;
	private BigDecimal sdm_info_id;
	private String sst_is_header;

	public String getSst_file_path() { return sst_file_path; }
	public void setSst_file_path(String sst_file_path) {
		if(sst_file_path==null) throw new BusinessException("Entity : SdmSpTextfile.sst_file_path must not null!");
		this.sst_file_path = sst_file_path;
	}

	public String getSst_schema() { return sst_schema; }
	public void setSst_schema(String sst_schema) {
		if(sst_schema==null) addNullValueField("sst_schema");
		this.sst_schema = sst_schema;
	}

	public BigDecimal getTsst_extfile_id() { return tsst_extfile_id; }
	public void setTsst_extfile_id(BigDecimal tsst_extfile_id) {
		if(tsst_extfile_id==null) throw new BusinessException("Entity : SdmSpTextfile.tsst_extfile_id must not null!");
		this.tsst_extfile_id = tsst_extfile_id;
	}

	public String getSst_file_type() { return sst_file_type; }
	public void setSst_file_type(String sst_file_type) {
		if(sst_file_type==null) throw new BusinessException("Entity : SdmSpTextfile.sst_file_type must not null!");
		this.sst_file_type = sst_file_type;
	}

	public BigDecimal getSdm_info_id() { return sdm_info_id; }
	public void setSdm_info_id(BigDecimal sdm_info_id) {
		if(sdm_info_id==null) throw new BusinessException("Entity : SdmSpTextfile.sdm_info_id must not null!");
		this.sdm_info_id = sdm_info_id;
	}

	public String getSst_is_header() { return sst_is_header; }
	public void setSst_is_header(String sst_is_header) {
		if(sst_is_header==null) throw new BusinessException("Entity : SdmSpTextfile.sst_is_header must not null!");
		this.sst_is_header = sst_is_header;
	}

}