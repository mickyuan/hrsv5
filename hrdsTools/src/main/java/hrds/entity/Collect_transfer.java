package hrds.entity;
/**Auto Created by VBScript Do not modify!*/
import fd.ng.db.entity.TableEntity;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.entity.anno.Column;
import fd.ng.db.entity.anno.Table;
import hrds.exception.BusinessException;
import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 * 传送作业参数表
 */
@Table(tableName = "collect_transfer")
public class Collect_transfer extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "collect_transfer";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 传送作业参数表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("ct_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long ct_id; //采集传输
	private String is_parallel; //是否并行传输
	private Long failure_count; //传输失败重试次数
	private String is_biglink; //是否大文件独占链路
	private Long file_size_link; //文件大于多少
	private String remark; //备注
	private Long collect_set_id; //数据库设置id
	private String run_way; //启动方式
	private String collect_type; //采集类型
	private Long agent_id; //Agent_id
	private String comp_id; //组件编号
	private Long rely_job_id; //依赖作业id

	/** 取得：采集传输 */
	public Long getCt_id(){
		return ct_id;
	}
	/** 设置：采集传输 */
	public void setCt_id(Long ct_id){
		this.ct_id=ct_id;
	}
	/** 设置：采集传输 */
	public void setCt_id(String ct_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(ct_id)){
			this.ct_id=new Long(ct_id);
		}
	}
	/** 取得：是否并行传输 */
	public String getIs_parallel(){
		return is_parallel;
	}
	/** 设置：是否并行传输 */
	public void setIs_parallel(String is_parallel){
		this.is_parallel=is_parallel;
	}
	/** 取得：传输失败重试次数 */
	public Long getFailure_count(){
		return failure_count;
	}
	/** 设置：传输失败重试次数 */
	public void setFailure_count(Long failure_count){
		this.failure_count=failure_count;
	}
	/** 设置：传输失败重试次数 */
	public void setFailure_count(String failure_count){
		if(!fd.ng.core.utils.StringUtil.isEmpty(failure_count)){
			this.failure_count=new Long(failure_count);
		}
	}
	/** 取得：是否大文件独占链路 */
	public String getIs_biglink(){
		return is_biglink;
	}
	/** 设置：是否大文件独占链路 */
	public void setIs_biglink(String is_biglink){
		this.is_biglink=is_biglink;
	}
	/** 取得：文件大于多少 */
	public Long getFile_size_link(){
		return file_size_link;
	}
	/** 设置：文件大于多少 */
	public void setFile_size_link(Long file_size_link){
		this.file_size_link=file_size_link;
	}
	/** 设置：文件大于多少 */
	public void setFile_size_link(String file_size_link){
		if(!fd.ng.core.utils.StringUtil.isEmpty(file_size_link)){
			this.file_size_link=new Long(file_size_link);
		}
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
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
