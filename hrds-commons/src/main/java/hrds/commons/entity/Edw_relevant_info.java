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
 * 模型前置作业
 */
@Table(tableName = "edw_relevant_info")
public class Edw_relevant_info extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "edw_relevant_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 模型前置作业 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("rel_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long rel_id; //作业相关id
	private String pre_work; //前置作业
	private String post_work; //后置作业
	private String remark; //备注
	private String jobcode; //作业编号

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
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：作业编号 */
	public String getJobcode(){
		return jobcode;
	}
	/** 设置：作业编号 */
	public void setJobcode(String jobcode){
		this.jobcode=jobcode;
	}
}
