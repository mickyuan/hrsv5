package hrds.entity;
/**Auto Created by VBScript Do not modify!*/
import fd.ng.db.entity.TableEntity;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.entity.anno.Column;
import fd.ng.db.entity.anno.Table;
import hrds.exception.BusinessException;
import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 * 机器学习数据探索图表数据表
 */
@Table(tableName = "ml_chart_data")
public class Ml_chart_data extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_chart_data";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 机器学习数据探索图表数据表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("chart_data_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long chart_data_id; //图表数据编号
	private Long dtable_info_id; //数据表信息编号
	private String categoryaxisx; //类别轴x
	private String clustering; //聚类
	private Long relation_id; //图表关系编号
	private String chart_name; //图表名称
	private String create_date; //创建日期
	private String create_time; //创建时间
	private String chartstatistictype; //图表统计量类型

	/** 取得：图表数据编号 */
	public Long getChart_data_id(){
		return chart_data_id;
	}
	/** 设置：图表数据编号 */
	public void setChart_data_id(Long chart_data_id){
		this.chart_data_id=chart_data_id;
	}
	/** 设置：图表数据编号 */
	public void setChart_data_id(String chart_data_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(chart_data_id)){
			this.chart_data_id=new Long(chart_data_id);
		}
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
	/** 取得：类别轴x */
	public String getCategoryaxisx(){
		return categoryaxisx;
	}
	/** 设置：类别轴x */
	public void setCategoryaxisx(String categoryaxisx){
		this.categoryaxisx=categoryaxisx;
	}
	/** 取得：聚类 */
	public String getClustering(){
		return clustering;
	}
	/** 设置：聚类 */
	public void setClustering(String clustering){
		this.clustering=clustering;
	}
	/** 取得：图表关系编号 */
	public Long getRelation_id(){
		return relation_id;
	}
	/** 设置：图表关系编号 */
	public void setRelation_id(Long relation_id){
		this.relation_id=relation_id;
	}
	/** 设置：图表关系编号 */
	public void setRelation_id(String relation_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(relation_id)){
			this.relation_id=new Long(relation_id);
		}
	}
	/** 取得：图表名称 */
	public String getChart_name(){
		return chart_name;
	}
	/** 设置：图表名称 */
	public void setChart_name(String chart_name){
		this.chart_name=chart_name;
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
	/** 取得：图表统计量类型 */
	public String getChartstatistictype(){
		return chartstatistictype;
	}
	/** 设置：图表统计量类型 */
	public void setChartstatistictype(String chartstatistictype){
		this.chartstatistictype=chartstatistictype;
	}
}
