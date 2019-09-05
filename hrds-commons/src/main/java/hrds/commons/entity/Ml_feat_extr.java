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
 * 机器学习特征抽取表
 */
@Table(tableName = "ml_feat_extr")
public class Ml_feat_extr extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_feat_extr";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 机器学习特征抽取表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("featextr_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long featextr_id; //特征抽取编号
	private String dv_column; //因变量字段
	private Long k_number; //K的个数
	private BigDecimal percent; //百分比
	private Long dtable_info_id; //数据表信息编号
	private String create_date; //创建日期
	private String create_time; //创建时间
	private String remark; //备注
	private String extr_method; //抽取方式

	/** 取得：特征抽取编号 */
	public Long getFeatextr_id(){
		return featextr_id;
	}
	/** 设置：特征抽取编号 */
	public void setFeatextr_id(Long featextr_id){
		this.featextr_id=featextr_id;
	}
	/** 设置：特征抽取编号 */
	public void setFeatextr_id(String featextr_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(featextr_id)){
			this.featextr_id=new Long(featextr_id);
		}
	}
	/** 取得：因变量字段 */
	public String getDv_column(){
		return dv_column;
	}
	/** 设置：因变量字段 */
	public void setDv_column(String dv_column){
		this.dv_column=dv_column;
	}
	/** 取得：K的个数 */
	public Long getK_number(){
		return k_number;
	}
	/** 设置：K的个数 */
	public void setK_number(Long k_number){
		this.k_number=k_number;
	}
	/** 设置：K的个数 */
	public void setK_number(String k_number){
		if(!fd.ng.core.utils.StringUtil.isEmpty(k_number)){
			this.k_number=new Long(k_number);
		}
	}
	/** 取得：百分比 */
	public BigDecimal getPercent(){
		return percent;
	}
	/** 设置：百分比 */
	public void setPercent(BigDecimal percent){
		this.percent=percent;
	}
	/** 设置：百分比 */
	public void setPercent(String percent){
		if(!fd.ng.core.utils.StringUtil.isEmpty(percent)){
			this.percent=new BigDecimal(percent);
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
	/** 取得：抽取方式 */
	public String getExtr_method(){
		return extr_method;
	}
	/** 设置：抽取方式 */
	public void setExtr_method(String extr_method){
		this.extr_method=extr_method;
	}
}
