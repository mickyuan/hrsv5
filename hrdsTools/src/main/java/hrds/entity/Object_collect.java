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
 * 对象采集设置
 */
@Table(tableName = "object_collect")
public class Object_collect extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "object_collect";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 对象采集设置 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("odc_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long odc_id; //对象采集id
	private String obj_number; //对象采集设置编号
	private String obj_collect_name; //对象采集任务名称
	private String system_name; //操作系统类型
	private String host_name; //主机名称
	private String local_time; //本地系统时间
	private String server_date; //服务器日期
	private String s_date; //开始日期
	private String e_date; //结束日期
	private String database_code; //采集编码
	private String run_way; //启动方式
	private String file_path; //采集文件路径
	private String remark; //备注
	private Long agent_id; //Agent_id
	private String is_sendok; //是否设置完成并发送成功
	private String object_collect_type; //对象采集方式

	/** 取得：对象采集id */
	public Long getOdc_id(){
		return odc_id;
	}
	/** 设置：对象采集id */
	public void setOdc_id(Long odc_id){
		this.odc_id=odc_id;
	}
	/** 设置：对象采集id */
	public void setOdc_id(String odc_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(odc_id)){
			this.odc_id=new Long(odc_id);
		}
	}
	/** 取得：对象采集设置编号 */
	public String getObj_number(){
		return obj_number;
	}
	/** 设置：对象采集设置编号 */
	public void setObj_number(String obj_number){
		this.obj_number=obj_number;
	}
	/** 取得：对象采集任务名称 */
	public String getObj_collect_name(){
		return obj_collect_name;
	}
	/** 设置：对象采集任务名称 */
	public void setObj_collect_name(String obj_collect_name){
		this.obj_collect_name=obj_collect_name;
	}
	/** 取得：操作系统类型 */
	public String getSystem_name(){
		return system_name;
	}
	/** 设置：操作系统类型 */
	public void setSystem_name(String system_name){
		this.system_name=system_name;
	}
	/** 取得：主机名称 */
	public String getHost_name(){
		return host_name;
	}
	/** 设置：主机名称 */
	public void setHost_name(String host_name){
		this.host_name=host_name;
	}
	/** 取得：本地系统时间 */
	public String getLocal_time(){
		return local_time;
	}
	/** 设置：本地系统时间 */
	public void setLocal_time(String local_time){
		this.local_time=local_time;
	}
	/** 取得：服务器日期 */
	public String getServer_date(){
		return server_date;
	}
	/** 设置：服务器日期 */
	public void setServer_date(String server_date){
		this.server_date=server_date;
	}
	/** 取得：开始日期 */
	public String getS_date(){
		return s_date;
	}
	/** 设置：开始日期 */
	public void setS_date(String s_date){
		this.s_date=s_date;
	}
	/** 取得：结束日期 */
	public String getE_date(){
		return e_date;
	}
	/** 设置：结束日期 */
	public void setE_date(String e_date){
		this.e_date=e_date;
	}
	/** 取得：采集编码 */
	public String getDatabase_code(){
		return database_code;
	}
	/** 设置：采集编码 */
	public void setDatabase_code(String database_code){
		this.database_code=database_code;
	}
	/** 取得：启动方式 */
	public String getRun_way(){
		return run_way;
	}
	/** 设置：启动方式 */
	public void setRun_way(String run_way){
		this.run_way=run_way;
	}
	/** 取得：采集文件路径 */
	public String getFile_path(){
		return file_path;
	}
	/** 设置：采集文件路径 */
	public void setFile_path(String file_path){
		this.file_path=file_path;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
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
	/** 取得：是否设置完成并发送成功 */
	public String getIs_sendok(){
		return is_sendok;
	}
	/** 设置：是否设置完成并发送成功 */
	public void setIs_sendok(String is_sendok){
		this.is_sendok=is_sendok;
	}
	/** 取得：对象采集方式 */
	public String getObject_collect_type(){
		return object_collect_type;
	}
	/** 设置：对象采集方式 */
	public void setObject_collect_type(String object_collect_type){
		this.object_collect_type=object_collect_type;
	}
}
