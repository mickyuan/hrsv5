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
@Table(tableName = "data_mart_info")
public class DataMartInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "data_mart_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("data_mart_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal data_mart_id;
	private String create_time;
	private BigDecimal create_id;
	private String mart_name;
	private String mart_storage_path;
	private String remark;
	private String mart_number;
	private String create_date;
	private String mart_desc;

	public BigDecimal getData_mart_id() { return data_mart_id; }
	public void setData_mart_id(BigDecimal data_mart_id) {
		if(data_mart_id==null) throw new BusinessException("Entity : DataMartInfo.data_mart_id must not null!");
		this.data_mart_id = data_mart_id;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : DataMartInfo.create_time must not null!");
		this.create_time = create_time;
	}

	public BigDecimal getCreate_id() { return create_id; }
	public void setCreate_id(BigDecimal create_id) {
		if(create_id==null) throw new BusinessException("Entity : DataMartInfo.create_id must not null!");
		this.create_id = create_id;
	}

	public String getMart_name() { return mart_name; }
	public void setMart_name(String mart_name) {
		if(mart_name==null) throw new BusinessException("Entity : DataMartInfo.mart_name must not null!");
		this.mart_name = mart_name;
	}

	public String getMart_storage_path() { return mart_storage_path; }
	public void setMart_storage_path(String mart_storage_path) {
		if(mart_storage_path==null) throw new BusinessException("Entity : DataMartInfo.mart_storage_path must not null!");
		this.mart_storage_path = mart_storage_path;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public String getMart_number() { return mart_number; }
	public void setMart_number(String mart_number) {
		if(mart_number==null) throw new BusinessException("Entity : DataMartInfo.mart_number must not null!");
		this.mart_number = mart_number;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : DataMartInfo.create_date must not null!");
		this.create_date = create_date;
	}

	public String getMart_desc() { return mart_desc; }
	public void setMart_desc(String mart_desc) {
		if(mart_desc==null) addNullValueField("mart_desc");
		this.mart_desc = mart_desc;
	}

}