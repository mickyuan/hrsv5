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
@Table(tableName = "sdm_con_file")
public class SdmConFile extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_con_file";

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

	private String spilt_flag;
	private BigDecimal file_limit;
	private String file_path;
	private String file_bus_class;
	private String file_name;
	private BigDecimal file_id;
	private BigDecimal sdm_des_id;
	private String file_bus_type;
	private String remark;

	public String getSpilt_flag() { return spilt_flag; }
	public void setSpilt_flag(String spilt_flag) {
		if(spilt_flag==null) throw new BusinessException("Entity : SdmConFile.spilt_flag must not null!");
		this.spilt_flag = spilt_flag;
	}

	public BigDecimal getFile_limit() { return file_limit; }
	public void setFile_limit(BigDecimal file_limit) {
		if(file_limit==null) addNullValueField("file_limit");
		this.file_limit = file_limit;
	}

	public String getFile_path() { return file_path; }
	public void setFile_path(String file_path) {
		if(file_path==null) throw new BusinessException("Entity : SdmConFile.file_path must not null!");
		this.file_path = file_path;
	}

	public String getFile_bus_class() { return file_bus_class; }
	public void setFile_bus_class(String file_bus_class) {
		if(file_bus_class==null) addNullValueField("file_bus_class");
		this.file_bus_class = file_bus_class;
	}

	public String getFile_name() { return file_name; }
	public void setFile_name(String file_name) {
		if(file_name==null) throw new BusinessException("Entity : SdmConFile.file_name must not null!");
		this.file_name = file_name;
	}

	public BigDecimal getFile_id() { return file_id; }
	public void setFile_id(BigDecimal file_id) {
		if(file_id==null) throw new BusinessException("Entity : SdmConFile.file_id must not null!");
		this.file_id = file_id;
	}

	public BigDecimal getSdm_des_id() { return sdm_des_id; }
	public void setSdm_des_id(BigDecimal sdm_des_id) {
		if(sdm_des_id==null) throw new BusinessException("Entity : SdmConFile.sdm_des_id must not null!");
		this.sdm_des_id = sdm_des_id;
	}

	public String getFile_bus_type() { return file_bus_type; }
	public void setFile_bus_type(String file_bus_type) {
		if(file_bus_type==null) throw new BusinessException("Entity : SdmConFile.file_bus_type must not null!");
		this.file_bus_type = file_bus_type;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

}