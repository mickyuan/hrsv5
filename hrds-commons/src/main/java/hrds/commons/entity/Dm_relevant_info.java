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
 * 集市表前置后置作业
 */
@Table(tableName = "dm_relevant_info")
public class Dm_relevant_info extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dm_relevant_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 集市表前置后置作业 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("rel_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="rel_id",value="作业相关id:",dataType = Long.class,required = true)
	private Long rel_id;
	@DocBean(name ="pre_work",value="前置作业:",dataType = String.class,required = false)
	private String pre_work;
	@DocBean(name ="post_work",value="后置作业:",dataType = String.class,required = false)
	private String post_work;
	@DocBean(name ="rel_remark",value="备注:",dataType = String.class,required = false)
	private String rel_remark;
	@DocBean(name ="datatable_id",value="数据表id:",dataType = Long.class,required = false)
	private Long datatable_id;

	/** 取得：作业相关id */
	public Long getRel_id(){
		return rel_id;
	}
	/** 设置：作业相关id */
	public void setRel_id(Long rel_id){
		this.rel_id=rel_id;
	}
	/** 设置：作业相关id */
	public void setRel_id(String rel_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(rel_id)){
			this.rel_id=new Long(rel_id);
		}
	}
	/** 取得：前置作业 */
	public String getPre_work(){
		return pre_work;
	}
	/** 设置：前置作业 */
	public void setPre_work(String pre_work){
		this.pre_work=pre_work;
	}
	/** 取得：后置作业 */
	public String getPost_work(){
		return post_work;
	}
	/** 设置：后置作业 */
	public void setPost_work(String post_work){
		this.post_work=post_work;
	}
	/** 取得：备注 */
	public String getRel_remark(){
		return rel_remark;
	}
	/** 设置：备注 */
	public void setRel_remark(String rel_remark){
		this.rel_remark=rel_remark;
	}
	/** 取得：数据表id */
	public Long getDatatable_id(){
		return datatable_id;
	}
	/** 设置：数据表id */
	public void setDatatable_id(Long datatable_id){
		this.datatable_id=datatable_id;
	}
	/** 设置：数据表id */
	public void setDatatable_id(String datatable_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(datatable_id)){
			this.datatable_id=new Long(datatable_id);
		}
	}
}
