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
 * 组件条件表
 */
@Table(tableName = "auto_comp_cond")
public class Auto_comp_cond extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_comp_cond";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 组件条件表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("component_cond_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="arithmetic_logic",value="运算逻辑:",dataType = String.class,required = false)
	private String arithmetic_logic;
	@DocBean(name ="cond_en_column",value="条件字段英文名称:",dataType = String.class,required = false)
	private String cond_en_column;
	@DocBean(name ="cond_value",value="条件值:",dataType = String.class,required = false)
	private String cond_value;
	@DocBean(name ="create_date",value="创建日期:",dataType = String.class,required = true)
	private String create_date;
	@DocBean(name ="create_time",value="创建时间:",dataType = String.class,required = true)
	private String create_time;
	@DocBean(name ="last_update_date",value="最后更新日期:",dataType = String.class,required = true)
	private String last_update_date;
	@DocBean(name ="last_update_time",value="最后更新时间:",dataType = String.class,required = true)
	private String last_update_time;
	@DocBean(name ="component_cond_id",value="组件条件ID:",dataType = Long.class,required = true)
	private Long component_cond_id;
	@DocBean(name ="cond_cn_column",value="条件字段中文名称:",dataType = String.class,required = false)
	private String cond_cn_column;
	@DocBean(name ="operator",value="操作符(AutoDataOperator):01-介于<JieYu> 02-不介于<BuJieYu> 03-等于<DengYu> 04-不等于<BuDengYu> 05-大于<DaYu> 06-小于<XiaoYu> 07-大于等于<DaYuDengYu> 08-小于等于<XiaoYuDengYu> 09-最大的N个<ZuiDaDeNGe> 10-最小的N个<ZuiXiaoDeNGe> 11-为空<WeiKong> 12-非空<FeiKong> ",dataType = String.class,required = false)
	private String operator;
	@DocBean(name ="component_id",value="组件ID:",dataType = Long.class,required = false)
	private Long component_id;
	@DocBean(name ="update_user",value="用户ID:",dataType = Long.class,required = true)
	private Long update_user;
	@DocBean(name ="create_user",value="用户ID:",dataType = Long.class,required = true)
	private Long create_user;

	/** 取得：运算逻辑 */
	public String getArithmetic_logic(){
		return arithmetic_logic;
	}
	/** 设置：运算逻辑 */
	public void setArithmetic_logic(String arithmetic_logic){
		this.arithmetic_logic=arithmetic_logic;
	}
	/** 取得：条件字段英文名称 */
	public String getCond_en_column(){
		return cond_en_column;
	}
	/** 设置：条件字段英文名称 */
	public void setCond_en_column(String cond_en_column){
		this.cond_en_column=cond_en_column;
	}
	/** 取得：条件值 */
	public String getCond_value(){
		return cond_value;
	}
	/** 设置：条件值 */
	public void setCond_value(String cond_value){
		this.cond_value=cond_value;
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
	/** 取得：组件条件ID */
	public Long getComponent_cond_id(){
		return component_cond_id;
	}
	/** 设置：组件条件ID */
	public void setComponent_cond_id(Long component_cond_id){
		this.component_cond_id=component_cond_id;
	}
	/** 设置：组件条件ID */
	public void setComponent_cond_id(String component_cond_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(component_cond_id)){
			this.component_cond_id=new Long(component_cond_id);
		}
	}
	/** 取得：条件字段中文名称 */
	public String getCond_cn_column(){
		return cond_cn_column;
	}
	/** 设置：条件字段中文名称 */
	public void setCond_cn_column(String cond_cn_column){
		this.cond_cn_column=cond_cn_column;
	}
	/** 取得：操作符 */
	public String getOperator(){
		return operator;
	}
	/** 设置：操作符 */
	public void setOperator(String operator){
		this.operator=operator;
	}
	/** 取得：组件ID */
	public Long getComponent_id(){
		return component_id;
	}
	/** 设置：组件ID */
	public void setComponent_id(Long component_id){
		this.component_id=component_id;
	}
	/** 设置：组件ID */
	public void setComponent_id(String component_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(component_id)){
			this.component_id=new Long(component_id);
		}
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
