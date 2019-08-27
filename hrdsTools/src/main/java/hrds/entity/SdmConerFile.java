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
@Table(tableName = "sdm_coner_file")
public class SdmConerFile extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_coner_file";

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

	private String file_path;
	private BigDecimal sdm_des_id;
	private String remark;
	private String time_interval;
	private String file_name;
	private BigDecimal file_id;

	public String getFile_path() { return file_path; }
	public void setFile_path(String file_path) {
		if(file_path==null) throw new BusinessException("Entity : SdmConerFile.file_path must not null!");
		this.file_path = file_path;
	}

	public BigDecimal getSdm_des_id() { return sdm_des_id; }
	public void setSdm_des_id(BigDecimal sdm_des_id) {
		if(sdm_des_id==null) addNullValueField("sdm_des_id");
		this.sdm_des_id = sdm_des_id;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public String getTime_interval() { return time_interval; }
	public void setTime_interval(String time_interval) {
		if(time_interval==null) throw new BusinessException("Entity : SdmConerFile.time_interval must not null!");
		this.time_interval = time_interval;
	}

	public String getFile_name() { return file_name; }
	public void setFile_name(String file_name) {
		if(file_name==null) throw new BusinessException("Entity : SdmConerFile.file_name must not null!");
		this.file_name = file_name;
	}

	public BigDecimal getFile_id() { return file_id; }
	public void setFile_id(BigDecimal file_id) {
		if(file_id==null) throw new BusinessException("Entity : SdmConerFile.file_id must not null!");
		this.file_id = file_id;
	}

}