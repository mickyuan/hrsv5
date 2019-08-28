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
 * StreamingPro作业输出信息表
 */
@Table(tableName = "sdm_sp_output")
public class Sdm_sp_output extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_sp_output";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** StreamingPro作业输出信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("sdm_info_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long sdm_info_id; //作业输出信息表id
	private String output_type; //输出类型
	private String output_mode; //输出模式
	private String output_table_name; //输入表名称
	private Long ssj_job_id; //作业id
	private Long output_number; //序号
	private String stream_tablename; //输出到流表的表名

	/** 取得：作业输出信息表id */
	public Long getSdm_info_id(){
		return sdm_info_id;
	}
	/** 设置：作业输出信息表id */
	public void setSdm_info_id(Long sdm_info_id){
		this.sdm_info_id=sdm_info_id;
	}
	/** 设置：作业输出信息表id */
	public void setSdm_info_id(String sdm_info_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(sdm_info_id)){
			this.sdm_info_id=new Long(sdm_info_id);
		}
	}
	/** 取得：输出类型 */
	public String getOutput_type(){
		return output_type;
	}
	/** 设置：输出类型 */
	public void setOutput_type(String output_type){
		this.output_type=output_type;
	}
	/** 取得：输出模式 */
	public String getOutput_mode(){
		return output_mode;
	}
	/** 设置：输出模式 */
	public void setOutput_mode(String output_mode){
		this.output_mode=output_mode;
	}
	/** 取得：输入表名称 */
	public String getOutput_table_name(){
		return output_table_name;
	}
	/** 设置：输入表名称 */
	public void setOutput_table_name(String output_table_name){
		this.output_table_name=output_table_name;
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
	public Long getOutput_number(){
		return output_number;
	}
	/** 设置：序号 */
	public void setOutput_number(Long output_number){
		this.output_number=output_number;
	}
	/** 设置：序号 */
	public void setOutput_number(String output_number){
		if(!fd.ng.core.utils.StringUtil.isEmpty(output_number)){
			this.output_number=new Long(output_number);
		}
	}
	/** 取得：输出到流表的表名 */
	public String getStream_tablename(){
		return stream_tablename;
	}
	/** 设置：输出到流表的表名 */
	public void setStream_tablename(String stream_tablename){
		this.stream_tablename=stream_tablename;
	}
}
