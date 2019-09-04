package hrds.commons.entity;
/**Auto Created by VBScript Do not modify!*/
import fd.ng.db.entity.TableEntity;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.entity.anno.Column;
import fd.ng.db.entity.anno.Table;
import hrds.commons.exception.BusinessException;
import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 * 机器学习数据探索统计分析表
 */
@Table(tableName = "ml_statanalyse")
public class Ml_statanalyse extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_statanalyse";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 机器学习数据探索统计分析表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("statistics_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long statistics_id; //统计表编号
	private String stat_anal_column; //统计性分析字段
	private String remark; //备注
	private Long dtable_info_id; //数据表信息编号
	private String average; //均值
	private String median; //中位数
	private String mode; //众数
	private String count; //计数
	private String standard_deviation; //标准差
	private String maximum; //最大值
	private String minimum; //最小值
	private String variance; //方差
	private String four_quantile; //四位分位数
	private String cut_point; //割点
	private Long quantile_number; //分位数位数
	private String pp_plot; //p-p图
	private String qq_plot; //q-q图
	private String create_date; //创建日期
	private String create_time; //创建时间

	/** 取得：统计表编号 */
	public Long getStatistics_id(){
		return statistics_id;
	}
	/** 设置：统计表编号 */
	public void setStatistics_id(Long statistics_id){
		this.statistics_id=statistics_id;
	}
	/** 设置：统计表编号 */
	public void setStatistics_id(String statistics_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(statistics_id)){
			this.statistics_id=new Long(statistics_id);
		}
	}
	/** 取得：统计性分析字段 */
	public String getStat_anal_column(){
		return stat_anal_column;
	}
	/** 设置：统计性分析字段 */
	public void setStat_anal_column(String stat_anal_column){
		this.stat_anal_column=stat_anal_column;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：数据表信息编号 */
	public Long getDtable_info_id(){
		return dtable_info_id;
	}
	/** 设置：数据表信息编号 */
	public void setDtable_info_id(Long dtable_info_id){
		this.dtable_info_id=dtable_info_id;
	}
	/** 设置：数据表信息编号 */
	public void setDtable_info_id(String dtable_info_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(dtable_info_id)){
			this.dtable_info_id=new Long(dtable_info_id);
		}
	}
	/** 取得：均值 */
	public String getAverage(){
		return average;
	}
	/** 设置：均值 */
	public void setAverage(String average){
		this.average=average;
	}
	/** 取得：中位数 */
	public String getMedian(){
		return median;
	}
	/** 设置：中位数 */
	public void setMedian(String median){
		this.median=median;
	}
	/** 取得：众数 */
	public String getMode(){
		return mode;
	}
	/** 设置：众数 */
	public void setMode(String mode){
		this.mode=mode;
	}
	/** 取得：计数 */
	public String getCount(){
		return count;
	}
	/** 设置：计数 */
	public void setCount(String count){
		this.count=count;
	}
	/** 取得：标准差 */
	public String getStandard_deviation(){
		return standard_deviation;
	}
	/** 设置：标准差 */
	public void setStandard_deviation(String standard_deviation){
		this.standard_deviation=standard_deviation;
	}
	/** 取得：最大值 */
	public String getMaximum(){
		return maximum;
	}
	/** 设置：最大值 */
	public void setMaximum(String maximum){
		this.maximum=maximum;
	}
	/** 取得：最小值 */
	public String getMinimum(){
		return minimum;
	}
	/** 设置：最小值 */
	public void setMinimum(String minimum){
		this.minimum=minimum;
	}
	/** 取得：方差 */
	public String getVariance(){
		return variance;
	}
	/** 设置：方差 */
	public void setVariance(String variance){
		this.variance=variance;
	}
	/** 取得：四位分位数 */
	public String getFour_quantile(){
		return four_quantile;
	}
	/** 设置：四位分位数 */
	public void setFour_quantile(String four_quantile){
		this.four_quantile=four_quantile;
	}
	/** 取得：割点 */
	public String getCut_point(){
		return cut_point;
	}
	/** 设置：割点 */
	public void setCut_point(String cut_point){
		this.cut_point=cut_point;
	}
	/** 取得：分位数位数 */
	public Long getQuantile_number(){
		return quantile_number;
	}
	/** 设置：分位数位数 */
	public void setQuantile_number(Long quantile_number){
		this.quantile_number=quantile_number;
	}
	/** 设置：分位数位数 */
	public void setQuantile_number(String quantile_number){
		if(!fd.ng.core.utils.StringUtil.isEmpty(quantile_number)){
			this.quantile_number=new Long(quantile_number);
		}
	}
	/** 取得：p-p图 */
	public String getPp_plot(){
		return pp_plot;
	}
	/** 设置：p-p图 */
	public void setPp_plot(String pp_plot){
		this.pp_plot=pp_plot;
	}
	/** 取得：q-q图 */
	public String getQq_plot(){
		return qq_plot;
	}
	/** 设置：q-q图 */
	public void setQq_plot(String qq_plot){
		this.qq_plot=qq_plot;
	}
	/** 取得：创建日期 */
	public String getCreate_date(){
		return create_date;
	}
	/** 设置：创建日期 */
	public void setCreate_date(String create_date){
		this.create_date=create_date;
	}
	/** 取得：创建时间 */
	public String getCreate_time(){
		return create_time;
	}
	/** 设置：创建时间 */
	public void setCreate_time(String create_time){
		this.create_time=create_time;
	}
}
