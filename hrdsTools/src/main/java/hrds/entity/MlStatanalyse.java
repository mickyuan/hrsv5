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
@Table(tableName = "ml_statanalyse")
public class MlStatanalyse extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_statanalyse";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("statistics_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String average;
	private String qq_plot;
	private String create_time;
	private String pp_plot;
	private String count;
	private String remark;
	private BigDecimal dtable_info_id;
	private String stat_anal_column;
	private String standard_deviation;
	private String mode;
	private String median;
	private String variance;
	private String maximum;
	private BigDecimal quantile_number;
	private String four_quantile;
	private BigDecimal statistics_id;
	private String create_date;
	private String minimum;
	private String cut_point;

	public String getAverage() { return average; }
	public void setAverage(String average) {
		if(average==null) addNullValueField("average");
		this.average = average;
	}

	public String getQq_plot() { return qq_plot; }
	public void setQq_plot(String qq_plot) {
		if(qq_plot==null) addNullValueField("qq_plot");
		this.qq_plot = qq_plot;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : MlStatanalyse.create_time must not null!");
		this.create_time = create_time;
	}

	public String getPp_plot() { return pp_plot; }
	public void setPp_plot(String pp_plot) {
		if(pp_plot==null) addNullValueField("pp_plot");
		this.pp_plot = pp_plot;
	}

	public String getCount() { return count; }
	public void setCount(String count) {
		if(count==null) addNullValueField("count");
		this.count = count;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public BigDecimal getDtable_info_id() { return dtable_info_id; }
	public void setDtable_info_id(BigDecimal dtable_info_id) {
		if(dtable_info_id==null) throw new BusinessException("Entity : MlStatanalyse.dtable_info_id must not null!");
		this.dtable_info_id = dtable_info_id;
	}

	public String getStat_anal_column() { return stat_anal_column; }
	public void setStat_anal_column(String stat_anal_column) {
		if(stat_anal_column==null) throw new BusinessException("Entity : MlStatanalyse.stat_anal_column must not null!");
		this.stat_anal_column = stat_anal_column;
	}

	public String getStandard_deviation() { return standard_deviation; }
	public void setStandard_deviation(String standard_deviation) {
		if(standard_deviation==null) addNullValueField("standard_deviation");
		this.standard_deviation = standard_deviation;
	}

	public String getMode() { return mode; }
	public void setMode(String mode) {
		if(mode==null) addNullValueField("mode");
		this.mode = mode;
	}

	public String getMedian() { return median; }
	public void setMedian(String median) {
		if(median==null) addNullValueField("median");
		this.median = median;
	}

	public String getVariance() { return variance; }
	public void setVariance(String variance) {
		if(variance==null) addNullValueField("variance");
		this.variance = variance;
	}

	public String getMaximum() { return maximum; }
	public void setMaximum(String maximum) {
		if(maximum==null) addNullValueField("maximum");
		this.maximum = maximum;
	}

	public BigDecimal getQuantile_number() { return quantile_number; }
	public void setQuantile_number(BigDecimal quantile_number) {
		if(quantile_number==null) addNullValueField("quantile_number");
		this.quantile_number = quantile_number;
	}

	public String getFour_quantile() { return four_quantile; }
	public void setFour_quantile(String four_quantile) {
		if(four_quantile==null) addNullValueField("four_quantile");
		this.four_quantile = four_quantile;
	}

	public BigDecimal getStatistics_id() { return statistics_id; }
	public void setStatistics_id(BigDecimal statistics_id) {
		if(statistics_id==null) throw new BusinessException("Entity : MlStatanalyse.statistics_id must not null!");
		this.statistics_id = statistics_id;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : MlStatanalyse.create_date must not null!");
		this.create_date = create_date;
	}

	public String getMinimum() { return minimum; }
	public void setMinimum(String minimum) {
		if(minimum==null) addNullValueField("minimum");
		this.minimum = minimum;
	}

	public String getCut_point() { return cut_point; }
	public void setCut_point(String cut_point) {
		if(cut_point==null) addNullValueField("cut_point");
		this.cut_point = cut_point;
	}

}