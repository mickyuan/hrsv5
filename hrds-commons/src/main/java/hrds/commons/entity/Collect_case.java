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
 * 采集情况信息表
 */
@Table(tableName = "collect_case")
public class Collect_case extends ProjectTableEntity
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
	@DocBean(name ="job_rs_id",value="作业执行结果ID:",dataType = String.class,required = true)
	private String job_rs_id;
	@DocBean(name ="colect_record",value="总共采集记录数:",dataType = Long.class,required = true)
	private Long colect_record;
	@DocBean(name ="collect_total",value="总共采集(文件)表:",dataType = Long.class,required = false)
	private Long collect_total;
	@DocBean(name ="collet_database_size",value="总共采集数据大小:",dataType = String.class,required = false)
	private String collet_database_size;
	@DocBean(name ="collect_s_time",value="开始采集时间:",dataType = String.class,required = true)
	private String collect_s_time;
	@DocBean(name ="collect_s_date",value="开始采集日期:",dataType = String.class,required = true)
	private String collect_s_date;
	@DocBean(name ="collect_e_date",value="采集结束日期:",dataType = String.class,required = false)
	private String collect_e_date;
	@DocBean(name ="collect_e_time",value="采集结束时间:",dataType = String.class,required = false)
	private String collect_e_time;
	@DocBean(name ="execute_state",value="运行状态(ExecuteState):01-开始运行<KaiShiYunXing> 02-运行完成<YunXingWanCheng> 99-运行失败<YunXingShiBai> 20-通知成功<TongZhiChengGong> 21-通知失败<TongZhiShiBai> 30-暂停运行<ZanTingYunXing> ",dataType = String.class,required = true)
	private String execute_state;
	@DocBean(name ="execute_length",value="运行总时长:",dataType = String.class,required = false)
	private String execute_length;
	@DocBean(name ="is_again",value="是否重跑(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_again;
	@DocBean(name ="job_group",value="agent组ID:",dataType = String.class,required = true)
	private String job_group;
	@DocBean(name ="collect_set_id",value="数据库设置id:",dataType = Long.class,required = true)
	private Long collect_set_id;
	@DocBean(name ="task_classify",value="任务分类（原子性）表名-顶级文件夹:",dataType = String.class,required = false)
	private String task_classify;
	@DocBean(name ="collect_type",value="采集类型(AgentType):1-数据库Agent<ShuJuKu> 2-文件系统Agent<WenJianXiTong> 3-FtpAgent<FTP> 4-数据文件Agent<DBWenJian> 5-对象Agent<DuiXiang> ",dataType = String.class,required = true)
	private String collect_type;
	@DocBean(name ="job_type",value="任务类型:",dataType = String.class,required = false)
	private String job_type;
	@DocBean(name ="cc_remark",value="备注:",dataType = String.class,required = false)
	private String cc_remark;
	@DocBean(name ="etl_date",value="跑批日期:",dataType = String.class,required = false)
	private String etl_date;
	@DocBean(name ="again_num",value="重跑次数:",dataType = Long.class,required = false)
	private Long again_num;
	@DocBean(name ="agent_id",value="Agent_id:",dataType = Long.class,required = true)
	private Long agent_id;
	@DocBean(name ="source_id",value="数据源ID:",dataType = Long.class,required = true)
	private Long source_id;

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
	/** 取得：任务分类（原子性）表名-顶级文件夹 */
	public String getTask_classify(){
		return task_classify;
	}
	/** 设置：任务分类（原子性）表名-顶级文件夹 */
	public void setTask_classify(String task_classify){
		this.task_classify=task_classify;
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
