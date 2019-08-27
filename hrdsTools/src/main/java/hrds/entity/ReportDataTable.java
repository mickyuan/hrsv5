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
@Table(tableName = "report_data_table")
public class ReportDataTable extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "report_data_table";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("report_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal report_type_id;
	private String report_source;
	private String r_publish_status;
	private BigDecimal create_id;
	private BigDecimal sql_id;
	private String create_time;
	private BigDecimal report_id;
	private String sql_choice_details;
	private BigDecimal folder_id;
	private String report_name;
	private String create_date;
	private BigDecimal configuration_id;

	public BigDecimal getReport_type_id() { return report_type_id; }
	public void setReport_type_id(BigDecimal report_type_id) {
		if(report_type_id==null) throw new BusinessException("Entity : ReportDataTable.report_type_id must not null!");
		this.report_type_id = report_type_id;
	}

	public String getReport_source() { return report_source; }
	public void setReport_source(String report_source) {
		if(report_source==null) addNullValueField("report_source");
		this.report_source = report_source;
	}

	public String getR_publish_status() { return r_publish_status; }
	public void setR_publish_status(String r_publish_status) {
		if(r_publish_status==null) throw new BusinessException("Entity : ReportDataTable.r_publish_status must not null!");
		this.r_publish_status = r_publish_status;
	}

	public BigDecimal getCreate_id() { return create_id; }
	public void setCreate_id(BigDecimal create_id) {
		if(create_id==null) throw new BusinessException("Entity : ReportDataTable.create_id must not null!");
		this.create_id = create_id;
	}

	public BigDecimal getSql_id() { return sql_id; }
	public void setSql_id(BigDecimal sql_id) {
		if(sql_id==null) throw new BusinessException("Entity : ReportDataTable.sql_id must not null!");
		this.sql_id = sql_id;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : ReportDataTable.create_time must not null!");
		this.create_time = create_time;
	}

	public BigDecimal getReport_id() { return report_id; }
	public void setReport_id(BigDecimal report_id) {
		if(report_id==null) throw new BusinessException("Entity : ReportDataTable.report_id must not null!");
		this.report_id = report_id;
	}

	public String getSql_choice_details() { return sql_choice_details; }
	public void setSql_choice_details(String sql_choice_details) {
		if(sql_choice_details==null) throw new BusinessException("Entity : ReportDataTable.sql_choice_details must not null!");
		this.sql_choice_details = sql_choice_details;
	}

	public BigDecimal getFolder_id() { return folder_id; }
	public void setFolder_id(BigDecimal folder_id) {
		if(folder_id==null) addNullValueField("folder_id");
		this.folder_id = folder_id;
	}

	public String getReport_name() { return report_name; }
	public void setReport_name(String report_name) {
		if(report_name==null) throw new BusinessException("Entity : ReportDataTable.report_name must not null!");
		this.report_name = report_name;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : ReportDataTable.create_date must not null!");
		this.create_date = create_date;
	}

	public BigDecimal getConfiguration_id() { return configuration_id; }
	public void setConfiguration_id(BigDecimal configuration_id) {
		if(configuration_id==null) addNullValueField("configuration_id");
		this.configuration_id = configuration_id;
	}

}