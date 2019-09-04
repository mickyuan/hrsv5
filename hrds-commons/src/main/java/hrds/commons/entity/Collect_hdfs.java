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
 * hdfs存储作业参数表
 */
@Table(tableName = "collect_hdfs")
public class Collect_hdfs extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "collect_hdfs";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** hdfs存储作业参数表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("chdfs_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long chdfs_id; //HDFS存储
	private String is_hbase; //是否存入HBASE
	private String is_hive; //是否存入HIVE
	private String is_fullindex; //是否建立全文索引
	private String cc_remark; //备注
	private Long collect_set_id; //数据库设置id
	private String run_way; //启动方式
	private String collect_type; //采集类型
	private Long agent_id; //Agent_id
	private String comp_id; //组件编号
	private Long rely_job_id; //依赖作业id

	/** 取得：HDFS存储 */
	public Long getChdfs_id(){
		return chdfs_id;
	}
	/** 设置：HDFS存储 */
	public void setChdfs_id(Long chdfs_id){
		this.chdfs_id=chdfs_id;
	}
	/** 设置：HDFS存储 */
	public void setChdfs_id(String chdfs_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(chdfs_id)){
			this.chdfs_id=new Long(chdfs_id);
		}
	}
	/** 取得：是否存入HBASE */
	public String getIs_hbase(){
		return is_hbase;
	}
	/** 设置：是否存入HBASE */
	public void setIs_hbase(String is_hbase){
		this.is_hbase=is_hbase;
	}
	/** 取得：是否存入HIVE */
	public String getIs_hive(){
		return is_hive;
	}
	/** 设置：是否存入HIVE */
	public void setIs_hive(String is_hive){
		this.is_hive=is_hive;
	}
	/** 取得：是否建立全文索引 */
	public String getIs_fullindex(){
		return is_fullindex;
	}
	/** 设置：是否建立全文索引 */
	public void setIs_fullindex(String is_fullindex){
		this.is_fullindex=is_fullindex;
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
