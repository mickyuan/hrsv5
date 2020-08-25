package hrds.commons.entity;
/**Auto Created by VBScript Do not modify!*/
import hrds.commons.entity.fdentity.ProjectTableEntity;
import fd.ng.db.entity.anno.Table;
import fd.ng.core.annotation.DocBean;
import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

/**
 * 模板结果设置表
 */
@Table(tableName = "auto_tp_res_set")
public class Auto_tp_res_set extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_tp_res_set";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 模板结果设置表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("template_res_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="column_en_name",value="字段英文名:",dataType = String.class,required = false)
	private String column_en_name;
	@DocBean(name ="column_cn_name",value="字段中文名:",dataType = String.class,required = false)
	private String column_cn_name;
	@DocBean(name ="dese_rule",value="脱敏规则:",dataType = String.class,required = false)
	private String dese_rule;
	@DocBean(name ="create_date",value="创建日期:",dataType = String.class,required = true)
	private String create_date;
	@DocBean(name ="create_time",value="创建时间:",dataType = String.class,required = true)
	private String create_time;
	@DocBean(name ="last_update_date",value="最后更新日期:",dataType = String.class,required = true)
	private String last_update_date;
	@DocBean(name ="last_update_time",value="最后更新时间:",dataType = String.class,required = true)
	private String last_update_time;
	@DocBean(name ="template_res_id",value="模板结果ID:",dataType = Long.class,required = true)
	private Long template_res_id;
	@DocBean(name ="is_dese",value="是否脱敏(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_dese;
	@DocBean(name ="res_show_column",value="结果显示字段:",dataType = String.class,required = false)
	private String res_show_column;
	@DocBean(name ="template_id",value="模板ID:",dataType = Long.class,required = true)
	private Long template_id;
	@DocBean(name ="column_type",value="字段类型:",dataType = String.class,required = false)
	private String column_type;
	@DocBean(name ="source_table_name",value="字段来源表名:",dataType = String.class,required = false)
	private String source_table_name;
	@DocBean(name ="update_user",value="用户ID:",dataType = Long.class,required = true)
	private Long update_user;
	@DocBean(name ="create_user",value="用户ID:",dataType = Long.class,required = true)
	private Long create_user;

	/** 取得：字段英文名 */
	public String getColumn_en_name(){
		return column_en_name;
	}
	/** 设置：字段英文名 */
	public void setColumn_en_name(String column_en_name){
		this.column_en_name=column_en_name;
	}
	/** 取得：字段中文名 */
	public String getColumn_cn_name(){
		return column_cn_name;
	}
	/** 设置：字段中文名 */
	public void setColumn_cn_name(String column_cn_name){
		this.column_cn_name=column_cn_name;
	}
	/** 取得：脱敏规则 */
	public String getDese_rule(){
		return dese_rule;
	}
	/** 设置：脱敏规则 */
	public void setDese_rule(String dese_rule){
		this.dese_rule=dese_rule;
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
	/** 取得：最后更新日期 */
	public String getLast_update_date(){
		return last_update_date;
	}
	/** 设置：最后更新日期 */
	public void setLast_update_date(String last_update_date){
		this.last_update_date=last_update_date;
	}
	/** 取得：最后更新时间 */
	public String getLast_update_time(){
		return last_update_time;
	}
	/** 设置：最后更新时间 */
	public void setLast_update_time(String last_update_time){
		this.last_update_time=last_update_time;
	}
	/** 取得：模板结果ID */
	public Long getTemplate_res_id(){
		return template_res_id;
	}
	/** 设置：模板结果ID */
	public void setTemplate_res_id(Long template_res_id){
		this.template_res_id=template_res_id;
	}
	/** 设置：模板结果ID */
	public void setTemplate_res_id(String template_res_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(template_res_id)){
			this.template_res_id=new Long(template_res_id);
		}
	}
	/** 取得：是否脱敏 */
	public String getIs_dese(){
		return is_dese;
	}
	/** 设置：是否脱敏 */
	public void setIs_dese(String is_dese){
		this.is_dese=is_dese;
	}
	/** 取得：结果显示字段 */
	public String getRes_show_column(){
		return res_show_column;
	}
	/** 设置：结果显示字段 */
	public void setRes_show_column(String res_show_column){
		this.res_show_column=res_show_column;
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
	/** 取得：字段类型 */
	public String getColumn_type(){
		return column_type;
	}
	/** 设置：字段类型 */
	public void setColumn_type(String column_type){
		this.column_type=column_type;
	}
	/** 取得：字段来源表名 */
	public String getSource_table_name(){
		return source_table_name;
	}
	/** 设置：字段来源表名 */
	public void setSource_table_name(String source_table_name){
		this.source_table_name=source_table_name;
	}
	/** 取得：用户ID */
	public Long getUpdate_user(){
		return update_user;
	}
	/** 设置：用户ID */
	public void setUpdate_user(Long update_user){
		this.update_user=update_user;
	}
	/** 设置：用户ID */
	public void setUpdate_user(String update_user){
		if(!fd.ng.core.utils.StringUtil.isEmpty(update_user)){
			this.update_user=new Long(update_user);
		}
	}
	/** 取得：用户ID */
	public Long getCreate_user(){
		return create_user;
	}
	/** 设置：用户ID */
	public void setCreate_user(Long create_user){
		this.create_user=create_user;
	}
	/** 设置：用户ID */
	public void setCreate_user(String create_user){
		if(!fd.ng.core.utils.StringUtil.isEmpty(create_user)){
			this.create_user=new Long(create_user);
		}
	}
}
