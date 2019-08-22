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
@Table(tableName = "data_source")
public class DataSource extends TableEntity {
    private static final long serialVersionUID = 321566460595860L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "data_source";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("source_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String source_remark;
	private String create_time;
	private BigDecimal user_id;
	private String datasource_name;
	private BigDecimal source_id;
	private String datasource_number;
	private String create_date;

	public String getSource_remark() { return source_remark; }
	public void setSource_remark(String source_remark) {
		if (source_remark==null) {
			addNullValueField("source_remark");
		}
		this.source_remark = source_remark;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if (create_time==null) {
			throw new BusinessException("Entity : DataSource.create_time must not null!");
		}
		this.create_time = create_time;
	}

	public BigDecimal getUser_id() { return user_id; }
	public void setUser_id(BigDecimal user_id) {
		if (user_id==null) {
			throw new BusinessException("Entity : DataSource.user_id must not null!");
		}
		this.user_id = user_id;
	}

	public String getDatasource_name() { return datasource_name; }
	public void setDatasource_name(String datasource_name) {
		if (datasource_name==null) {
			throw new BusinessException("Entity : DataSource.datasource_name must not null!");
		}
		this.datasource_name = datasource_name;
	}

	public BigDecimal getSource_id() { return source_id; }
	public void setSource_id(BigDecimal source_id) {
		if (source_id==null) {
			throw new BusinessException("Entity : DataSource.source_id must not null!");
		}
		this.source_id = source_id;
	}

	public String getDatasource_number() { return datasource_number; }
	public void setDatasource_number(String datasource_number) {
		if (datasource_number==null) {
			addNullValueField("datasource_number");
		}
		this.datasource_number = datasource_number;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if (create_date==null) {
			throw new BusinessException("Entity : DataSource.create_date must not null!");
		}
		this.create_date = create_date;
	}

}