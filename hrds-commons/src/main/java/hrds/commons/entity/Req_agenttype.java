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
 * 请求Agent类型
 */
@Table(tableName = "req_agenttype")
public class Req_agenttype extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "req_agenttype";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 请求Agent类型 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("req_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="req_name",value="中文名称:",dataType = String.class,required = true)
	private String req_name;
	@DocBean(name ="req_no",value="请求编号:",dataType = String.class,required = false)
	private String req_no;
	@DocBean(name ="req_remark",value="备注:",dataType = String.class,required = false)
	private String req_remark;
	@DocBean(name ="req_id",value="请求ID:",dataType = Long.class,required = true)
	private Long req_id;
	@DocBean(name ="comp_id",value="组件编号:",dataType = String.class,required = true)
	private String comp_id;

	/** 取得：中文名称 */
	public String getReq_name(){
		return req_name;
	}
	/** 设置：中文名称 */
	public void setReq_name(String req_name){
		this.req_name=req_name;
	}
	/** 取得：请求编号 */
	public String getReq_no(){
		return req_no;
	}
	/** 设置：请求编号 */
	public void setReq_no(String req_no){
		this.req_no=req_no;
	}
	/** 取得：备注 */
	public String getReq_remark(){
		return req_remark;
	}
	/** 设置：备注 */
	public void setReq_remark(String req_remark){
		this.req_remark=req_remark;
	}
	/** 取得：请求ID */
	public Long getReq_id(){
		return req_id;
	}
	/** 设置：请求ID */
	public void setReq_id(Long req_id){
		this.req_id=req_id;
	}
	/** 设置：请求ID */
	public void setReq_id(String req_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(req_id)){
			this.req_id=new Long(req_id);
		}
	}
	/** 取得：组件编号 */
	public String getComp_id(){
		return comp_id;
	}
	/** 设置：组件编号 */
	public void setComp_id(String comp_id){
		this.comp_id=comp_id;
	}
}
