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
 * 模板条件信息表
 */
@Table(tableName = "auto_tp_cond_info")
public class Auto_tp_cond_info extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_tp_cond_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 模板条件信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("template_cond_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private String cond_para_name; //条件参数名称
	private String cond_en_column; //条件对应的英文字段
	private String ci_sp_name; //代码项表名
	private String ci_sp_class; //代码项类别
	private String value_type; //值类型
	private Long template_cond_id; //模板条件ID
	private String cond_cn_column; //条件对应的中文字段
	private String show_type; //展现形式
	private String pre_value; //预设值
	private String is_required; //是否必填
	private String is_dept_id; //是否为部门ID
	private Long template_id; //模板ID
	private String con_relation; //关联关系
	private String value_size; //值大小
	private String con_row; //行号

	/** 取得：条件参数名称 */
	public String getCond_para_name(){
		return cond_para_name;
	}
	/** 设置：条件参数名称 */
	public void setCond_para_name(String cond_para_name){
		this.cond_para_name=cond_para_name;
	}
	/** 取得：条件对应的英文字段 */
	public String getCond_en_column(){
		return cond_en_column;
	}
	/** 设置：条件对应的英文字段 */
	public void setCond_en_column(String cond_en_column){
		this.cond_en_column=cond_en_column;
	}
	/** 取得：代码项表名 */
	public String getCi_sp_name(){
		return ci_sp_name;
	}
	/** 设置：代码项表名 */
	public void setCi_sp_name(String ci_sp_name){
		this.ci_sp_name=ci_sp_name;
	}
	/** 取得：代码项类别 */
	public String getCi_sp_class(){
		return ci_sp_class;
	}
	/** 设置：代码项类别 */
	public void setCi_sp_class(String ci_sp_class){
		this.ci_sp_class=ci_sp_class;
	}
	/** 取得：值类型 */
	public String getValue_type(){
		return value_type;
	}
	/** 设置：值类型 */
	public void setValue_type(String value_type){
		this.value_type=value_type;
	}
	/** 取得：模板条件ID */
	public Long getTemplate_cond_id(){
		return template_cond_id;
	}
	/** 设置：模板条件ID */
	public void setTemplate_cond_id(Long template_cond_id){
		this.template_cond_id=template_cond_id;
	}
	/** 设置：模板条件ID */
	public void setTemplate_cond_id(String template_cond_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(template_cond_id)){
			this.template_cond_id=new Long(template_cond_id);
		}
	}
	/** 取得：条件对应的中文字段 */
	public String getCond_cn_column(){
		return cond_cn_column;
	}
	/** 设置：条件对应的中文字段 */
	public void setCond_cn_column(String cond_cn_column){
		this.cond_cn_column=cond_cn_column;
	}
	/** 取得：展现形式 */
	public String getShow_type(){
		return show_type;
	}
	/** 设置：展现形式 */
	public void setShow_type(String show_type){
		this.show_type=show_type;
	}
	/** 取得：预设值 */
	public String getPre_value(){
		return pre_value;
	}
	/** 设置：预设值 */
	public void setPre_value(String pre_value){
		this.pre_value=pre_value;
	}
	/** 取得：是否必填 */
	public String getIs_required(){
		return is_required;
	}
	/** 设置：是否必填 */
	public void setIs_required(String is_required){
		this.is_required=is_required;
	}
	/** 取得：是否为部门ID */
	public String getIs_dept_id(){
		return is_dept_id;
	}
	/** 设置：是否为部门ID */
	public void setIs_dept_id(String is_dept_id){
		this.is_dept_id=is_dept_id;
	}
	/** 取得：模板ID */
	public Long getTemplate_id(){
		return template_id;
	}
	/** 设置：模板ID */
	public void setTemplate_id(Long template_id){
		this.template_id=template_id;
	}
	/** 设置：模板ID */
	public void setTemplate_id(String template_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(template_id)){
			this.template_id=new Long(template_id);
		}
	}
	/** 取得：关联关系 */
	public String getCon_relation(){
		return con_relation;
	}
	/** 设置：关联关系 */
	public void setCon_relation(String con_relation){
		this.con_relation=con_relation;
	}
	/** 取得：值大小 */
	public String getValue_size(){
		return value_size;
	}
	/** 设置：值大小 */
	public void setValue_size(String value_size){
		this.value_size=value_size;
	}
	/** 取得：行号 */
	public String getCon_row(){
		return con_row;
	}
	/** 设置：行号 */
	public void setCon_row(String con_row){
		this.con_row=con_row;
	}
}
