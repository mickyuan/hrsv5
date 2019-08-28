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
 * 数值变量重编码表
 */
@Table(tableName = "ml_nvar_recode")
public class Ml_nvar_recode extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_nvar_recode";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数值变量重编码表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("recode_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private String recode_mode; //数值变量重编码处理方式
	private String norm_range; //归一化范围
	private String disc_info; //离散化信息
	private String newcolumn_name; //新字段名称
	private String numevar_column; //数值变量字段名称
	private Long recode_id; //重编码编号
	private Long dtable_info_id; //数据表信息编号
	private String create_date; //创建日期
	private String create_time; //创建时间
	private String remark; //备注

	/** 取得：数值变量重编码处理方式 */
	public String getRecode_mode(){
		return recode_mode;
	}
	/** 设置：数值变量重编码处理方式 */
	public void setRecode_mode(String recode_mode){
		this.recode_mode=recode_mode;
	}
	/** 取得：归一化范围 */
	public String getNorm_range(){
		return norm_range;
	}
	/** 设置：归一化范围 */
	public void setNorm_range(String norm_range){
		this.norm_range=norm_range;
	}
	/** 取得：离散化信息 */
	public String getDisc_info(){
		return disc_info;
	}
	/** 设置：离散化信息 */
	public void setDisc_info(String disc_info){
		this.disc_info=disc_info;
	}
	/** 取得：新字段名称 */
	public String getNewcolumn_name(){
		return newcolumn_name;
	}
	/** 设置：新字段名称 */
	public void setNewcolumn_name(String newcolumn_name){
		this.newcolumn_name=newcolumn_name;
	}
	/** 取得：数值变量字段名称 */
	public String getNumevar_column(){
		return numevar_column;
	}
	/** 设置：数值变量字段名称 */
	public void setNumevar_column(String numevar_column){
		this.numevar_column=numevar_column;
	}
	/** 取得：重编码编号 */
	public Long getRecode_id(){
		return recode_id;
	}
	/** 设置：重编码编号 */
	public void setRecode_id(Long recode_id){
		this.recode_id=recode_id;
	}
	/** 设置：重编码编号 */
	public void setRecode_id(String recode_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(recode_id)){
			this.recode_id=new Long(recode_id);
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
}
