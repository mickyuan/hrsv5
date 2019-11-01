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
 * Ftp采集设置
 */
@Table(tableName = "ftp_collect")
public class Ftp_collect extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ftp_collect";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** Ftp采集设置 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("ftp_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="ftp_id",value="ftp采集id:",dataType = Long.class,required = true)
	private Long ftp_id;
	@DocBean(name ="ftp_number",value="ftp任务编号:",dataType = String.class,required = true)
	private String ftp_number;
	@DocBean(name ="ftp_name",value="ftp采集任务名称:",dataType = String.class,required = true)
	private String ftp_name;
	@DocBean(name ="start_date",value="开始日期:",dataType = String.class,required = true)
	private String start_date;
	@DocBean(name ="end_date",value="结束日期:",dataType = String.class,required = true)
	private String end_date;
	@DocBean(name ="ftp_ip",value="ftp服务IP:",dataType = String.class,required = true)
	private String ftp_ip;
	@DocBean(name ="ftp_port",value="ftp服务器端口:",dataType = String.class,required = true)
	private String ftp_port;
	@DocBean(name ="ftp_username",value="ftp用户名:",dataType = String.class,required = true)
	private String ftp_username;
	@DocBean(name ="ftp_password",value="用户密码:",dataType = String.class,required = true)
	private String ftp_password;
	@DocBean(name ="ftp_dir",value="ftp服务器目录:",dataType = String.class,required = true)
	private String ftp_dir;
	@DocBean(name ="local_path",value="本地路径:",dataType = String.class,required = true)
	private String local_path;
	@DocBean(name ="file_suffix",value="获取文件后缀:",dataType = String.class,required = false)
	private String file_suffix;
	@DocBean(name ="run_way",value="启动方式(ExecuteWay):1-按时启动<AnShiQiDong> 2-命令触发<MingLingChuFa> 3-信号文件触发<QianZhiTiaoJian> ",dataType = String.class,required = true)
	private String run_way;
	@DocBean(name ="remark",value="备注:",dataType = String.class,required = false)
	private String remark;
	@DocBean(name ="agent_id",value="Agent_id:",dataType = Long.class,required = true)
	private Long agent_id;
	@DocBean(name ="ftp_rule_path",value="下级目录规则(FtpRule):1-流水号<LiuShuiHao> 2-固定目录<GuDingMuLu> 3-按时间<AnShiJian> ",dataType = String.class,required = true)
	private String ftp_rule_path;
	@DocBean(name ="child_file_path",value="下级文件路径:",dataType = String.class,required = false)
	private String child_file_path;
	@DocBean(name ="child_time",value="下级文件时间(TimeType):1-日<Day> 2-小时<Hour> 3-分钟<Minute> 4-秒<Second> ",dataType = String.class,required = false)
	private String child_time;
	@DocBean(name ="is_sendok",value="是否完成(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_sendok;
	@DocBean(name ="is_unzip",value="是否解压(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_unzip;
	@DocBean(name ="reduce_type",value="解压格式(ReduceType):1-tar<TAR> 2-gz<GZ> 3-zip<ZIP> 4-none<NONE> ",dataType = String.class,required = false)
	private String reduce_type;
	@DocBean(name ="ftp_model",value="FTP推拉模式是为推模式(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String ftp_model;
	@DocBean(name ="is_read_realtime",value="是否实时读取(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_read_realtime;
	@DocBean(name ="realtime_interval",value="实时读取间隔时间:",dataType = Long.class,required = true)
	private Long realtime_interval;

	/** 取得：ftp采集id */
	public Long getFtp_id(){
		return ftp_id;
	}
	/** 设置：ftp采集id */
	public void setFtp_id(Long ftp_id){
		this.ftp_id=ftp_id;
	}
	/** 设置：ftp采集id */
	public void setFtp_id(String ftp_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(ftp_id)){
			this.ftp_id=new Long(ftp_id);
		}
	}
	/** 取得：ftp任务编号 */
	public String getFtp_number(){
		return ftp_number;
	}
	/** 设置：ftp任务编号 */
	public void setFtp_number(String ftp_number){
		this.ftp_number=ftp_number;
	}
	/** 取得：ftp采集任务名称 */
	public String getFtp_name(){
		return ftp_name;
	}
	/** 设置：ftp采集任务名称 */
	public void setFtp_name(String ftp_name){
		this.ftp_name=ftp_name;
	}
	/** 取得：开始日期 */
	public String getStart_date(){
		return start_date;
	}
	/** 设置：开始日期 */
	public void setStart_date(String start_date){
		this.start_date=start_date;
	}
	/** 取得：结束日期 */
	public String getEnd_date(){
		return end_date;
	}
	/** 设置：结束日期 */
	public void setEnd_date(String end_date){
		this.end_date=end_date;
	}
	/** 取得：ftp服务IP */
	public String getFtp_ip(){
		return ftp_ip;
	}
	/** 设置：ftp服务IP */
	public void setFtp_ip(String ftp_ip){
		this.ftp_ip=ftp_ip;
	}
	/** 取得：ftp服务器端口 */
	public String getFtp_port(){
		return ftp_port;
	}
	/** 设置：ftp服务器端口 */
	public void setFtp_port(String ftp_port){
		this.ftp_port=ftp_port;
	}
	/** 取得：ftp用户名 */
	public String getFtp_username(){
		return ftp_username;
	}
	/** 设置：ftp用户名 */
	public void setFtp_username(String ftp_username){
		this.ftp_username=ftp_username;
	}
	/** 取得：用户密码 */
	public String getFtp_password(){
		return ftp_password;
	}
	/** 设置：用户密码 */
	public void setFtp_password(String ftp_password){
		this.ftp_password=ftp_password;
	}
	/** 取得：ftp服务器目录 */
	public String getFtp_dir(){
		return ftp_dir;
	}
	/** 设置：ftp服务器目录 */
	public void setFtp_dir(String ftp_dir){
		this.ftp_dir=ftp_dir;
	}
	/** 取得：本地路径 */
	public String getLocal_path(){
		return local_path;
	}
	/** 设置：本地路径 */
	public void setLocal_path(String local_path){
		this.local_path=local_path;
	}
	/** 取得：获取文件后缀 */
	public String getFile_suffix(){
		return file_suffix;
	}
	/** 设置：获取文件后缀 */
	public void setFile_suffix(String file_suffix){
		this.file_suffix=file_suffix;
	}
	/** 取得：启动方式 */
	public String getRun_way(){
		return run_way;
	}
	/** 设置：启动方式 */
	public void setRun_way(String run_way){
		this.run_way=run_way;
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
	/** 取得：下级目录规则 */
	public String getFtp_rule_path(){
		return ftp_rule_path;
	}
	/** 设置：下级目录规则 */
	public void setFtp_rule_path(String ftp_rule_path){
		this.ftp_rule_path=ftp_rule_path;
	}
	/** 取得：下级文件路径 */
	public String getChild_file_path(){
		return child_file_path;
	}
	/** 设置：下级文件路径 */
	public void setChild_file_path(String child_file_path){
		this.child_file_path=child_file_path;
	}
	/** 取得：下级文件时间 */
	public String getChild_time(){
		return child_time;
	}
	/** 设置：下级文件时间 */
	public void setChild_time(String child_time){
		this.child_time=child_time;
	}
	/** 取得：是否完成 */
	public String getIs_sendok(){
		return is_sendok;
	}
	/** 设置：是否完成 */
	public void setIs_sendok(String is_sendok){
		this.is_sendok=is_sendok;
	}
	/** 取得：是否解压 */
	public String getIs_unzip(){
		return is_unzip;
	}
	/** 设置：是否解压 */
	public void setIs_unzip(String is_unzip){
		this.is_unzip=is_unzip;
	}
	/** 取得：解压格式 */
	public String getReduce_type(){
		return reduce_type;
	}
	/** 设置：解压格式 */
	public void setReduce_type(String reduce_type){
		this.reduce_type=reduce_type;
	}
	/** 取得：FTP推拉模式是为推模式 */
	public String getFtp_model(){
		return ftp_model;
	}
	/** 设置：FTP推拉模式是为推模式 */
	public void setFtp_model(String ftp_model){
		this.ftp_model=ftp_model;
	}
	/** 取得：是否实时读取 */
	public String getIs_read_realtime(){
		return is_read_realtime;
	}
	/** 设置：是否实时读取 */
	public void setIs_read_realtime(String is_read_realtime){
		this.is_read_realtime=is_read_realtime;
	}
	/** 取得：实时读取间隔时间 */
	public Long getRealtime_interval(){
		return realtime_interval;
	}
	/** 设置：实时读取间隔时间 */
	public void setRealtime_interval(Long realtime_interval){
		this.realtime_interval=realtime_interval;
	}
	/** 设置：实时读取间隔时间 */
	public void setRealtime_interval(String realtime_interval){
		if(!fd.ng.core.utils.StringUtil.isEmpty(realtime_interval)){
			this.realtime_interval=new Long(realtime_interval);
		}
	}
}
