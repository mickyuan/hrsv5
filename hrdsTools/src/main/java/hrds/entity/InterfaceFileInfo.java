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
@Table(tableName = "interface_file_info")
public class InterfaceFileInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "interface_file_info";

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
	private String data_output;
	private String remark;
	private BigDecimal user_id;
	private String data_class;
	private String file_id;

	public String getFile_path() { return file_path; }
	public void setFile_path(String file_path) {
		if(file_path==null) throw new BusinessException("Entity : InterfaceFileInfo.file_path must not null!");
		this.file_path = file_path;
	}

	public String getData_output() { return data_output; }
	public void setData_output(String data_output) {
		if(data_output==null) throw new BusinessException("Entity : InterfaceFileInfo.data_output must not null!");
		this.data_output = data_output;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public BigDecimal getUser_id() { return user_id; }
	public void setUser_id(BigDecimal user_id) {
		if(user_id==null) throw new BusinessException("Entity : InterfaceFileInfo.user_id must not null!");
		this.user_id = user_id;
	}

	public String getData_class() { return data_class; }
	public void setData_class(String data_class) {
		if(data_class==null) throw new BusinessException("Entity : InterfaceFileInfo.data_class must not null!");
		this.data_class = data_class;
	}

	public String getFile_id() { return file_id; }
	public void setFile_id(String file_id) {
		if(file_id==null) throw new BusinessException("Entity : InterfaceFileInfo.file_id must not null!");
		this.file_id = file_id;
	}

}