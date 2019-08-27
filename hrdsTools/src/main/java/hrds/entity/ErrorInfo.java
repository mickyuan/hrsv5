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
@Table(tableName = "error_info")
public class ErrorInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "error_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("error_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String job_rs_id;
	private BigDecimal error_id;
	private String error_msg;

	public String getJob_rs_id() { return job_rs_id; }
	public void setJob_rs_id(String job_rs_id) {
		if(job_rs_id==null) addNullValueField("job_rs_id");
		this.job_rs_id = job_rs_id;
	}

	public BigDecimal getError_id() { return error_id; }
	public void setError_id(BigDecimal error_id) {
		if(error_id==null) throw new BusinessException("Entity : ErrorInfo.error_id must not null!");
		this.error_id = error_id;
	}

	public String getError_msg() { return error_msg; }
	public void setError_msg(String error_msg) {
		if(error_msg==null) addNullValueField("error_msg");
		this.error_msg = error_msg;
	}

}