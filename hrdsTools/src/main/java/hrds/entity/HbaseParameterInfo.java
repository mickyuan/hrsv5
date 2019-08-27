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
@Table(tableName = "hbase_parameter_info")
public class HbaseParameterInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "hbase_parameter_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("parameter_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal use_id;
	private String remark;
	private String table_column_name;
	private BigDecimal user_id;
	private BigDecimal parameter_id;
	private String is_flag;

	public BigDecimal getUse_id() { return use_id; }
	public void setUse_id(BigDecimal use_id) {
		if(use_id==null) throw new BusinessException("Entity : HbaseParameterInfo.use_id must not null!");
		this.use_id = use_id;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public String getTable_column_name() { return table_column_name; }
	public void setTable_column_name(String table_column_name) {
		if(table_column_name==null) throw new BusinessException("Entity : HbaseParameterInfo.table_column_name must not null!");
		this.table_column_name = table_column_name;
	}

	public BigDecimal getUser_id() { return user_id; }
	public void setUser_id(BigDecimal user_id) {
		if(user_id==null) throw new BusinessException("Entity : HbaseParameterInfo.user_id must not null!");
		this.user_id = user_id;
	}

	public BigDecimal getParameter_id() { return parameter_id; }
	public void setParameter_id(BigDecimal parameter_id) {
		if(parameter_id==null) throw new BusinessException("Entity : HbaseParameterInfo.parameter_id must not null!");
		this.parameter_id = parameter_id;
	}

	public String getIs_flag() { return is_flag; }
	public void setIs_flag(String is_flag) {
		if(is_flag==null) throw new BusinessException("Entity : HbaseParameterInfo.is_flag must not null!");
		this.is_flag = is_flag;
	}

}