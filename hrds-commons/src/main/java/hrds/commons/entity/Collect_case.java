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
 * 采集情况信息表
 */
@Table(tableName = "collect_case")
public class Collect_case extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "collect_case";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 采集情况信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("job_rs_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private String job_rs_id; //作业执行结果ID
	private Long colect_record; //总共采集记录数
	private Long collect_total; //总共采集(文件)表
	private String collet_database_size; //总共采集数据大小
	private String collect_s_time; //开始采集时间
	private String collect_s_date; //开始采集日期
	private String collect_e_date; //采集结束日期
	private String collect_e_time; //采集结束时间
	private String execute_state; //运行状态
	private String execute_length; //运行总时长
	private String is_again; //是否重跑
	private String job_group; //agent组ID
	private Long collect_set_id; //数据库设置id
	private String table_name; //表名
	private String collect_type; //采集类型
	private String job_type; //任务类型
	private String cc_remark; //备注
	private String etl_date; //跑批日期
	private Long again_num; //重跑次数
	private Long agent_id; //Agent_id
	private Long source_id; //数据源ID

	/** 取得：作业执行结果ID */
	public String getJob_rs_id(){
		return job_rs_id;
	}
	/** 设置：作业执行结果ID */
	public void setJob_rs_id(String job_rs_id){
		this.job_rs_id=job_rs_id;
	}
	/** 取得：总共采集记录数 */
	public Long getColect_record(){
		return colect_record;
	}
	/** 设置：总共采集记录数 */
	public void setColect_record(Long colect_record){
		this.colect_record=colect_record;
	}
	/** 设置：总共采集记录数 */
	public void setColect_record(String colect_record){
		if(!fd.ng.core.utils.StringUtil.isEmpty(colect_record)){
			this.colect_record=new Long(colect_record);
		}
	}
	/** 取得：总共采集(文件)表 */
	public Long getCollect_total(){
		return collect_total;
	}
	/** 设置：总共采集(文件)表 */
	public void setCollect_total(Long collect_total){
		this.collect_total=collect_total;
	}
	/** 设置：总共采集(文件)表 */
	public void setCollect_total(String collect_total){
		if(!fd.ng.core.utils.StringUtil.isEmpty(collect_total)){
			this.collect_total=new Long(collect_total);
		}
	}
	/** 取得：总共采集数据大小 */
	public String getCollet_database_size(){
		return collet_database_size;
	}
	/** 设置：总共采集数据大小 */
	public void setCollet_database_size(String collet_database_size){
		this.collet_database_size=collet_database_size;
	}
	/** 取得：开始采集时间 */
	public String getCollect_s_time(){
		return collect_s_time;
	}
	/** 设置：开始采集时间 */
	public void setCollect_s_time(String collect_s_time){
		this.collect_s_time=collect_s_time;
	}
	/** 取得：开始采集日期 */
	public String getCollect_s_date(){
		return collect_s_date;
	}
	/** 设置：开始采集日期 */
	public void setCollect_s_date(String collect_s_date){
		this.collect_s_date=collect_s_date;
	}
	/** 取得：采集结束日期 */
	public String getCollect_e_date(){
		return collect_e_date;
	}
	/** 设置：采集结束日期 */
	public void setCollect_e_date(String collect_e_date){
		this.collect_e_date=collect_e_date;
	}
	/** 取得：采集结束时间 */
	public String getCollect_e_time(){
		return collect_e_time;
	}
	/** 设置：采集结束时间 */
	public void setCollect_e_time(String collect_e_time){
		this.collect_e_time=collect_e_time;
	}
	/** 取得：运行状态 */
	public String getExecute_state(){
		return execute_state;
	}
	/** 设置：运行状态 */
	public void setExecute_state(String execute_state){
		this.execute_state=execute_state;
	}
	/** 取得：运行总时长 */
	public String getExecute_length(){
		return execute_length;
	}
	/** 设置：运行总时长 */
	public void setExecute_length(String execute_length){
		this.execute_length=execute_length;
	}
	/** 取得：是否重跑 */
	public String getIs_again(){
		return is_again;
	}
	/** 设置：是否重跑 */
	public void setIs_again(String is_again){
		this.is_again=is_again;
	}
	/** 取得：agent组ID */
	public String getJob_group(){
		return job_group;
	}
	/** 设置：agent组ID */
	public void setJob_group(String job_group){
		this.job_group=job_group;
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
	/** 取得：表名 */
	public String getTable_name(){
		return table_name;
	}
	/** 设置：表名 */
	public void setTable_name(String table_name){
		this.table_name=table_name;
	}
	/** 取得：采集类型 */
	public String getCollect_type(){
		return collect_type;
	}
	/** 设置：采集类型 */
	public void setCollect_type(String collect_type){
		this.collect_type=collect_type;
	}
	/** 取得：任务类型 */
	public String getJob_type(){
		return job_type;
	}
	/** 设置：任务类型 */
	public void setJob_type(String job_type){
		this.job_type=job_type;
	}
	/** 取得：备注 */
	public String getCc_remark(){
		return cc_remark;
	}
	/** 设置：备注 */
	public void setCc_remark(String cc_remark){
		this.cc_remark=cc_remark;
	}
	/** 取得：跑批日期 */
	public String getEtl_date(){
		return etl_date;
	}
	/** 设置：跑批日期 */
	public void setEtl_date(String etl_date){
		this.etl_date=etl_date;
	}
	/** 取得：重跑次数 */
	public Long getAgain_num(){
		return again_num;
	}
	/** 设置：重跑次数 */
	public void setAgain_num(Long again_num){
		this.again_num=again_num;
	}
	/** 设置：重跑次数 */
	public void setAgain_num(String again_num){
		if(!fd.ng.core.utils.StringUtil.isEmpty(again_num)){
			this.again_num=new Long(again_num);
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
	/** 取得：数据源ID */
	public Long getSource_id(){
		return source_id;
	}
	/** 设置：数据源ID */
	public void setSource_id(Long source_id){
		this.source_id=source_id;
	}
	/** 设置：数据源ID */
	public void setSource_id(String source_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(source_id)){
			this.source_id=new Long(source_id);
		}
	}
}
