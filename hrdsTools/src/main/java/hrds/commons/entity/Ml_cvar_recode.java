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
 * 分类变量重编码表
 */
@Table(tableName = "ml_cvar_recode")
public class Ml_cvar_recode extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_cvar_recode";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 分类变量重编码表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("recode_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long recode_id; //重编码编号
	private String catevar_code; //分类变量编码方式
	private String catevar_column; //分类变量字段名称
	private Long dtable_info_id; //数据表信息编号
	private String create_date; //创建日期
	private String create_time; //创建时间
	private String remark; //备注
	private String tagencode_info; //标签编码信息

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
	/** 取得：分类变量编码方式 */
	public String getCatevar_code(){
		return catevar_code;
	}
	/** 设置：分类变量编码方式 */
	public void setCatevar_code(String catevar_code){
		this.catevar_code=catevar_code;
	}
	/** 取得：分类变量字段名称 */
	public String getCatevar_column(){
		return catevar_column;
	}
	/** 设置：分类变量字段名称 */
	public void setCatevar_column(String catevar_column){
		this.catevar_column=catevar_column;
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
	/** 取得：标签编码信息 */
	public String getTagencode_info(){
		return tagencode_info;
	}
	/** 设置：标签编码信息 */
	public void setTagencode_info(String tagencode_info){
		this.tagencode_info=tagencode_info;
	}
}
