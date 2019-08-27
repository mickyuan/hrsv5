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
@Table(tableName = "report_type_table")
public class ReportTypeTable extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "report_type_table";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("report_type_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String report_title;
	private String report_data_filed;
	private BigDecimal report_type_id;
	private String report_style_filed;
	private String report_type_name;
	private String report_print_position;
	private String report_type;

	public String getReport_title() { return report_title; }
	public void setReport_title(String report_title) {
		if(report_title==null) throw new BusinessException("Entity : ReportTypeTable.report_title must not null!");
		this.report_title = report_title;
	}

	public String getReport_data_filed() { return report_data_filed; }
	public void setReport_data_filed(String report_data_filed) {
		if(report_data_filed==null) throw new BusinessException("Entity : ReportTypeTable.report_data_filed must not null!");
		this.report_data_filed = report_data_filed;
	}

	public BigDecimal getReport_type_id() { return report_type_id; }
	public void setReport_type_id(BigDecimal report_type_id) {
		if(report_type_id==null) throw new BusinessException("Entity : ReportTypeTable.report_type_id must not null!");
		this.report_type_id = report_type_id;
	}

	public String getReport_style_filed() { return report_style_filed; }
	public void setReport_style_filed(String report_style_filed) {
		if(report_style_filed==null) addNullValueField("report_style_filed");
		this.report_style_filed = report_style_filed;
	}

	public String getReport_type_name() { return report_type_name; }
	public void setReport_type_name(String report_type_name) {
		if(report_type_name==null) throw new BusinessException("Entity : ReportTypeTable.report_type_name must not null!");
		this.report_type_name = report_type_name;
	}

	public String getReport_print_position() { return report_print_position; }
	public void setReport_print_position(String report_print_position) {
		if(report_print_position==null) addNullValueField("report_print_position");
		this.report_print_position = report_print_position;
	}

	public String getReport_type() { return report_type; }
	public void setReport_type(String report_type) {
		if(report_type==null) addNullValueField("report_type");
		this.report_type = report_type;
	}

}