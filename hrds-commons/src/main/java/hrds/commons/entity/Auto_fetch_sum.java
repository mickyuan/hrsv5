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
 * 取数汇总表
 */
@Table(tableName = "auto_fetch_sum")
public class Auto_fetch_sum extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_fetch_sum";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 取数汇总表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("fetch_sum_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="create_date",value="创建日期:",dataType = String.class,required = true)
	private String create_date;
	@DocBean(name ="create_time",value="创建时间:",dataType = String.class,required = true)
	private String create_time;
	@DocBean(name ="last_update_date",value="最后更新日期:",dataType = String.class,required = false)
	private String last_update_date;
	@DocBean(name ="last_update_time",value="最后更新时间:",dataType = String.class,required = false)
	private String last_update_time;
	@DocBean(name ="fetch_sum_id",value="取数汇总ID:",dataType = Long.class,required = true)
	private Long fetch_sum_id;
	@DocBean(name ="template_id",value="模板ID:",dataType = Long.class,required = true)
	private Long template_id;
	@DocBean(name ="fetch_status",value="取数状态(AutoFetchStatus):01-编辑<BianJi> 04-完成<WanCheng> 05-注销<ZhuXiao> ",dataType = String.class,required = true)
	private String fetch_status;
	@DocBean(name ="fetch_sql",value="取数sql:",dataType = String.class,required = true)
	private String fetch_sql;
	@DocBean(name ="fetch_name",value="取数名称:",dataType = String.class,required = true)
	private String fetch_name;
	@DocBean(name ="fetch_desc",value="取数用途:",dataType = String.class,required = false)
	private String fetch_desc;
	@DocBean(name ="create_user",value="用户ID:",dataType = Long.class,required = true)
	private Long create_user;
	@DocBean(name ="update_user",value="用户ID:",dataType = Long.class,required = false)
	private Long update_user;

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
	/** 取得：取数汇总ID */
	public Long getFetch_sum_id(){
		return fetch_sum_id;
	}
	/** 设置：取数汇总ID */
	public void setFetch_sum_id(Long fetch_sum_id){
		this.fetch_sum_id=fetch_sum_id;
	}
	/** 设置：取数汇总ID */
	public void setFetch_sum_id(String fetch_sum_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(fetch_sum_id)){
			this.fetch_sum_id=new Long(fetch_sum_id);
		}
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
	/** 取得：取数状态 */
	public String getFetch_status(){
		return fetch_status;
	}
	/** 设置：取数状态 */
	public void setFetch_status(String fetch_status){
		this.fetch_status=fetch_status;
	}
	/** 取得：取数sql */
	public String getFetch_sql(){
		return fetch_sql;
	}
	/** 设置：取数sql */
	public void setFetch_sql(String fetch_sql){
		this.fetch_sql=fetch_sql;
	}
	/** 取得：取数名称 */
	public String getFetch_name(){
		return fetch_name;
	}
	/** 设置：取数名称 */
	public void setFetch_name(String fetch_name){
		this.fetch_name=fetch_name;
	}
	/** 取得：取数用途 */
	public String getFetch_desc(){
		return fetch_desc;
	}
	/** 设置：取数用途 */
	public void setFetch_desc(String fetch_desc){
		this.fetch_desc=fetch_desc;
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
}
