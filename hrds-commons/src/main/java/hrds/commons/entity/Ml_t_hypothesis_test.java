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
 * 机器学习假设t检验表
 */
@Table(tableName = "ml_t_hypothesis_test")
public class Ml_t_hypothesis_test extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_t_hypothesis_test";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 机器学习假设t检验表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("t_hypotest_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long t_hypotest_id; //t检验编号
	private String singaamp_colu; //单样本字段
	private BigDecimal singsamp_mean; //单样本均值
	private String two_sampcf; //双样本字段1
	private String two_sampcs; //双样本字段2
	private String indesamp_style; //双样本独立样本类型
	private String t_hypo_type; //t检验样本类型
	private String create_date; //创建日期
	private String create_time; //创建时间
	private String remark; //备注
	private Long dtable_info_id; //数据表信息编号

	/** 取得：t检验编号 */
	public Long getT_hypotest_id(){
		return t_hypotest_id;
	}
	/** 设置：t检验编号 */
	public void setT_hypotest_id(Long t_hypotest_id){
		this.t_hypotest_id=t_hypotest_id;
	}
	/** 设置：t检验编号 */
	public void setT_hypotest_id(String t_hypotest_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(t_hypotest_id)){
			this.t_hypotest_id=new Long(t_hypotest_id);
		}
	}
	/** 取得：单样本字段 */
	public String getSingaamp_colu(){
		return singaamp_colu;
	}
	/** 设置：单样本字段 */
	public void setSingaamp_colu(String singaamp_colu){
		this.singaamp_colu=singaamp_colu;
	}
	/** 取得：单样本均值 */
	public BigDecimal getSingsamp_mean(){
		return singsamp_mean;
	}
	/** 设置：单样本均值 */
	public void setSingsamp_mean(BigDecimal singsamp_mean){
		this.singsamp_mean=singsamp_mean;
	}
	/** 设置：单样本均值 */
	public void setSingsamp_mean(String singsamp_mean){
		if(!fd.ng.core.utils.StringUtil.isEmpty(singsamp_mean)){
			this.singsamp_mean=new BigDecimal(singsamp_mean);
		}
	}
	/** 取得：双样本字段1 */
	public String getTwo_sampcf(){
		return two_sampcf;
	}
	/** 设置：双样本字段1 */
	public void setTwo_sampcf(String two_sampcf){
		this.two_sampcf=two_sampcf;
	}
	/** 取得：双样本字段2 */
	public String getTwo_sampcs(){
		return two_sampcs;
	}
	/** 设置：双样本字段2 */
	public void setTwo_sampcs(String two_sampcs){
		this.two_sampcs=two_sampcs;
	}
	/** 取得：双样本独立样本类型 */
	public String getIndesamp_style(){
		return indesamp_style;
	}
	/** 设置：双样本独立样本类型 */
	public void setIndesamp_style(String indesamp_style){
		this.indesamp_style=indesamp_style;
	}
	/** 取得：t检验样本类型 */
	public String getT_hypo_type(){
		return t_hypo_type;
	}
	/** 设置：t检验样本类型 */
	public void setT_hypo_type(String t_hypo_type){
		this.t_hypo_type=t_hypo_type;
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
}
