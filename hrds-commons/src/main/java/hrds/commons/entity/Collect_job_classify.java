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
 * 采集任务分类表
 */
@Table(tableName = "collect_job_classify")
public class Collect_job_classify extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "collect_job_classify";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 采集任务分类表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("classify_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="classify_id",value="分类id:",dataType = Long.class,required = true)
	private Long classify_id;
	@DocBean(name ="classify_num",value="分类编号:",dataType = String.class,required = true)
	private String classify_num;
	@DocBean(name ="classify_name",value="分类名称:",dataType = String.class,required = true)
	private String classify_name;
	@DocBean(name ="remark",value="备注:",dataType = String.class,required = false)
	private String remark;
	@DocBean(name ="user_id",value="用户ID:",dataType = Long.class,required = true)
	private Long user_id;
	@DocBean(name ="agent_id",value="Agent_id:",dataType = Long.class,required = true)
	private Long agent_id;

	/** 取得：分类id */
	public Long getClassify_id(){
		return classify_id;
	}
	/** 设置：分类id */
	public void setClassify_id(Long classify_id){
		this.classify_id=classify_id;
	}
	/** 设置：分类id */
	public void setClassify_id(String classify_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(classify_id)){
			this.classify_id=new Long(classify_id);
		}
	}
	/** 取得：分类编号 */
	public String getClassify_num(){
		return classify_num;
	}
	/** 设置：分类编号 */
	public void setClassify_num(String classify_num){
		this.classify_num=classify_num;
	}
	/** 取得：分类名称 */
	public String getClassify_name(){
		return classify_name;
	}
	/** 设置：分类名称 */
	public void setClassify_name(String classify_name){
		this.classify_name=classify_name;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：用户ID */
	public Long getUser_id(){
		return user_id;
	}
	/** 设置：用户ID */
	public void setUser_id(Long user_id){
		this.user_id=user_id;
	}
	/** 设置：用户ID */
	public void setUser_id(String user_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(user_id)){
			this.user_id=new Long(user_id);
		}
	}
	/** 取得：Agent_id */
	public Long getAgent_id(){
		return agent_id;
	}
	/** 设置：Agent_id */
	public void setAgent_id(Long agent_id){
		this.agent_id=agent_id;
	}
	/** 设置：Agent_id */
	public void setAgent_id(String agent_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(agent_id)){
			this.agent_id=new Long(agent_id);
		}
	}
}
