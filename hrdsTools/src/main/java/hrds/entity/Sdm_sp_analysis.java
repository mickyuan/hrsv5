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
 * StreamingPro作业分析信息表
 */
@Table(tableName = "sdm_sp_analysis")
public class Sdm_sp_analysis extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_sp_analysis";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** StreamingPro作业分析信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("ssa_info_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long ssa_info_id; //分析信息表id
	private String analysis_table_name; //输出表名
	private String analysis_sql; //分析sql
	private Long ssj_job_id; //作业id
	private Long analysis_number; //序号

	/** 取得：分析信息表id */
	public Long getSsa_info_id(){
		return ssa_info_id;
	}
	/** 设置：分析信息表id */
	public void setSsa_info_id(Long ssa_info_id){
		this.ssa_info_id=ssa_info_id;
	}
	/** 设置：分析信息表id */
	public void setSsa_info_id(String ssa_info_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(ssa_info_id)){
			this.ssa_info_id=new Long(ssa_info_id);
		}
	}
	/** 取得：输出表名 */
	public String getAnalysis_table_name(){
		return analysis_table_name;
	}
	/** 设置：输出表名 */
	public void setAnalysis_table_name(String analysis_table_name){
		this.analysis_table_name=analysis_table_name;
	}
	/** 取得：分析sql */
	public String getAnalysis_sql(){
		return analysis_sql;
	}
	/** 设置：分析sql */
	public void setAnalysis_sql(String analysis_sql){
		this.analysis_sql=analysis_sql;
	}
	/** 取得：作业id */
	public Long getSsj_job_id(){
		return ssj_job_id;
	}
	/** 设置：作业id */
	public void setSsj_job_id(Long ssj_job_id){
		this.ssj_job_id=ssj_job_id;
	}
	/** 设置：作业id */
	public void setSsj_job_id(String ssj_job_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(ssj_job_id)){
			this.ssj_job_id=new Long(ssj_job_id);
		}
	}
	/** 取得：序号 */
	public Long getAnalysis_number(){
		return analysis_number;
	}
	/** 设置：序号 */
	public void setAnalysis_number(Long analysis_number){
		this.analysis_number=analysis_number;
	}
	/** 设置：序号 */
	public void setAnalysis_number(String analysis_number){
		if(!fd.ng.core.utils.StringUtil.isEmpty(analysis_number)){
			this.analysis_number=new Long(analysis_number);
		}
	}
}
