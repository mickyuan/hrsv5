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
 * 清洗作业参数表
 */
@Table(tableName = "collect_clean")
public class Collect_clean extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "collect_clean";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 清洗作业参数表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("cc_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long cc_id; //采集概况信息
	private String is_del_space; //是否去空格
	private String is_special_escape; //是否特殊字符转义
	private String is_del_escape; //是否去除特殊字符
	private String is_filling_column; //是否字段补齐
	private String cc_remark; //备注
	private Long collect_set_id; //数据库设置id
	private String run_way; //启动方式
	private String collect_type; //采集类型
	private Long agent_id; //Agent_id
	private String comp_id; //组件编号
	private Long rely_job_id; //依赖作业id

	/** 取得：采集概况信息 */
	public Long getCc_id(){
		return cc_id;
	}
	/** 设置：采集概况信息 */
	public void setCc_id(Long cc_id){
		this.cc_id=cc_id;
	}
	/** 设置：采集概况信息 */
	public void setCc_id(String cc_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(cc_id)){
			this.cc_id=new Long(cc_id);
		}
	}
	/** 取得：是否去空格 */
	public String getIs_del_space(){
		return is_del_space;
	}
	/** 设置：是否去空格 */
	public void setIs_del_space(String is_del_space){
		this.is_del_space=is_del_space;
	}
	/** 取得：是否特殊字符转义 */
	public String getIs_special_escape(){
		return is_special_escape;
	}
	/** 设置：是否特殊字符转义 */
	public void setIs_special_escape(String is_special_escape){
		this.is_special_escape=is_special_escape;
	}
	/** 取得：是否去除特殊字符 */
	public String getIs_del_escape(){
		return is_del_escape;
	}
	/** 设置：是否去除特殊字符 */
	public void setIs_del_escape(String is_del_escape){
		this.is_del_escape=is_del_escape;
	}
	/** 取得：是否字段补齐 */
	public String getIs_filling_column(){
		return is_filling_column;
	}
	/** 设置：是否字段补齐 */
	public void setIs_filling_column(String is_filling_column){
		this.is_filling_column=is_filling_column;
	}
	/** 取得：备注 */
	public String getCc_remark(){
		return cc_remark;
	}
	/** 设置：备注 */
	public void setCc_remark(String cc_remark){
		this.cc_remark=cc_remark;
	}
	/** 取得：数据库设置id */
	public Long getCollect_set_id(){
		return collect_set_id;
	}
	/** 设置：数据库设置id */
	public void setCollect_set_id(Long collect_set_id){
		this.collect_set_id=collect_set_id;
	}
	/** 设置：数据库设置id */
	public void setCollect_set_id(String collect_set_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(collect_set_id)){
			this.collect_set_id=new Long(collect_set_id);
		}
	}
	/** 取得：启动方式 */
	public String getRun_way(){
		return run_way;
	}
	/** 设置：启动方式 */
	public void setRun_way(String run_way){
		this.run_way=run_way;
	}
	/** 取得：采集类型 */
	public String getCollect_type(){
		return collect_type;
	}
	/** 设置：采集类型 */
	public void setCollect_type(String collect_type){
		this.collect_type=collect_type;
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
	/** 取得：组件编号 */
	public String getComp_id(){
		return comp_id;
	}
	/** 设置：组件编号 */
	public void setComp_id(String comp_id){
		this.comp_id=comp_id;
	}
	/** 取得：依赖作业id */
	public Long getRely_job_id(){
		return rely_job_id;
	}
	/** 设置：依赖作业id */
	public void setRely_job_id(Long rely_job_id){
		this.rely_job_id=rely_job_id;
	}
	/** 设置：依赖作业id */
	public void setRely_job_id(String rely_job_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(rely_job_id)){
			this.rely_job_id=new Long(rely_job_id);
		}
	}
}
