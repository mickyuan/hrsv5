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
 * 压缩作业参数表
 */
@Table(tableName = "collect_reduce")
public class Collect_reduce extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "collect_reduce";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 压缩作业参数表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("cr_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long cr_id; //采集压缩信息
	private String reduce_scope; //压缩范围
	private Long file_size; //文件大于多少压缩
	private String is_encrypt; //是否需要加密
	private String is_md5check; //是否使用MD5校验
	private String remark; //备注
	private Long collect_set_id; //数据库设置id
	private String run_way; //启动方式
	private String collect_type; //采集类型
	private Long agent_id; //Agent_id
	private String comp_id; //组件编号
	private String is_reduce; //是否压缩
	private Long rely_job_id; //依赖作业id

	/** 取得：采集压缩信息 */
	public Long getCr_id(){
		return cr_id;
	}
	/** 设置：采集压缩信息 */
	public void setCr_id(Long cr_id){
		this.cr_id=cr_id;
	}
	/** 设置：采集压缩信息 */
	public void setCr_id(String cr_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(cr_id)){
			this.cr_id=new Long(cr_id);
		}
	}
	/** 取得：压缩范围 */
	public String getReduce_scope(){
		return reduce_scope;
	}
	/** 设置：压缩范围 */
	public void setReduce_scope(String reduce_scope){
		this.reduce_scope=reduce_scope;
	}
	/** 取得：文件大于多少压缩 */
	public Long getFile_size(){
		return file_size;
	}
	/** 设置：文件大于多少压缩 */
	public void setFile_size(Long file_size){
		this.file_size=file_size;
	}
	/** 设置：文件大于多少压缩 */
	public void setFile_size(String file_size){
		if(!fd.ng.core.utils.StringUtil.isEmpty(file_size)){
			this.file_size=new Long(file_size);
		}
	}
	/** 取得：是否需要加密 */
	public String getIs_encrypt(){
		return is_encrypt;
	}
	/** 设置：是否需要加密 */
	public void setIs_encrypt(String is_encrypt){
		this.is_encrypt=is_encrypt;
	}
	/** 取得：是否使用MD5校验 */
	public String getIs_md5check(){
		return is_md5check;
	}
	/** 设置：是否使用MD5校验 */
	public void setIs_md5check(String is_md5check){
		this.is_md5check=is_md5check;
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
	/** 取得：是否压缩 */
	public String getIs_reduce(){
		return is_reduce;
	}
	/** 设置：是否压缩 */
	public void setIs_reduce(String is_reduce){
		this.is_reduce=is_reduce;
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
