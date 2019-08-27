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
@Table(tableName = "sdm_data_source")
public class SdmDataSource extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_data_source";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("sdm_source_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String create_time;
	private BigDecimal user_id;
	private String sdm_source_des;
	private String sdm_source_name;
	private BigDecimal sdm_source_id;
	private String sdm_source_number;
	private String create_date;

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : SdmDataSource.create_time must not null!");
		this.create_time = create_time;
	}

	public BigDecimal getUser_id() { return user_id; }
	public void setUser_id(BigDecimal user_id) {
		if(user_id==null) throw new BusinessException("Entity : SdmDataSource.user_id must not null!");
		this.user_id = user_id;
	}

	public String getSdm_source_des() { return sdm_source_des; }
	public void setSdm_source_des(String sdm_source_des) {
		if(sdm_source_des==null) throw new BusinessException("Entity : SdmDataSource.sdm_source_des must not null!");
		this.sdm_source_des = sdm_source_des;
	}

	public String getSdm_source_name() { return sdm_source_name; }
	public void setSdm_source_name(String sdm_source_name) {
		if(sdm_source_name==null) throw new BusinessException("Entity : SdmDataSource.sdm_source_name must not null!");
		this.sdm_source_name = sdm_source_name;
	}

	public BigDecimal getSdm_source_id() { return sdm_source_id; }
	public void setSdm_source_id(BigDecimal sdm_source_id) {
		if(sdm_source_id==null) throw new BusinessException("Entity : SdmDataSource.sdm_source_id must not null!");
		this.sdm_source_id = sdm_source_id;
	}

	public String getSdm_source_number() { return sdm_source_number; }
	public void setSdm_source_number(String sdm_source_number) {
		if(sdm_source_number==null) addNullValueField("sdm_source_number");
		this.sdm_source_number = sdm_source_number;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : SdmDataSource.create_date must not null!");
		this.create_date = create_date;
	}

}