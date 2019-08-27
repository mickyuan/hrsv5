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
@Table(tableName = "ml_chart_data")
public class MlChartData extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_chart_data";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("chart_data_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String chart_name;
	private String create_time;
	private BigDecimal chart_data_id;
	private String categoryaxisx;
	private String clustering;
	private BigDecimal dtable_info_id;
	private String create_date;
	private String chartstatistictype;
	private BigDecimal relation_id;

	public String getChart_name() { return chart_name; }
	public void setChart_name(String chart_name) {
		if(chart_name==null) throw new BusinessException("Entity : MlChartData.chart_name must not null!");
		this.chart_name = chart_name;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : MlChartData.create_time must not null!");
		this.create_time = create_time;
	}

	public BigDecimal getChart_data_id() { return chart_data_id; }
	public void setChart_data_id(BigDecimal chart_data_id) {
		if(chart_data_id==null) throw new BusinessException("Entity : MlChartData.chart_data_id must not null!");
		this.chart_data_id = chart_data_id;
	}

	public String getCategoryaxisx() { return categoryaxisx; }
	public void setCategoryaxisx(String categoryaxisx) {
		if(categoryaxisx==null) throw new BusinessException("Entity : MlChartData.categoryaxisx must not null!");
		this.categoryaxisx = categoryaxisx;
	}

	public String getClustering() { return clustering; }
	public void setClustering(String clustering) {
		if(clustering==null) throw new BusinessException("Entity : MlChartData.clustering must not null!");
		this.clustering = clustering;
	}

	public BigDecimal getDtable_info_id() { return dtable_info_id; }
	public void setDtable_info_id(BigDecimal dtable_info_id) {
		if(dtable_info_id==null) throw new BusinessException("Entity : MlChartData.dtable_info_id must not null!");
		this.dtable_info_id = dtable_info_id;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : MlChartData.create_date must not null!");
		this.create_date = create_date;
	}

	public String getChartstatistictype() { return chartstatistictype; }
	public void setChartstatistictype(String chartstatistictype) {
		if(chartstatistictype==null) throw new BusinessException("Entity : MlChartData.chartstatistictype must not null!");
		this.chartstatistictype = chartstatistictype;
	}

	public BigDecimal getRelation_id() { return relation_id; }
	public void setRelation_id(BigDecimal relation_id) {
		if(relation_id==null) throw new BusinessException("Entity : MlChartData.relation_id must not null!");
		this.relation_id = relation_id;
	}

}