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
 * 数据库设置
 */
@Table(tableName = "database_set")
public class Database_set extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "database_set";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据库设置 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("database_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long agent_id; //Agent_id
	private Long database_id; //数据库设置id
	private String task_name; //数据库采集任务名称
	private String database_name; //数据库名称
	private String database_pad; //数据库密码
	private String database_drive; //数据库驱动
	private String database_type; //数据库类型
	private String user_name; //用户名称
	private String database_ip; //数据库服务器IP
	private String database_port; //数据库端口
	private String host_name; //主机名
	private String system_type; //操作系统类型
	private String is_sendok; //是否设置完成并发送成功
	private String database_number; //数据库设置编号
	private String db_agent; //是否为平面DB数据采集
	private String plane_url; //DB文件源数据路径
	private String database_separatorr; //数据采用分隔符
	private String database_code; //数据使用编码格式
	private String dbfile_format; //DB文件格式
	private String is_hidden; //分隔符是否为ASCII隐藏字符
	private String file_suffix; //采集文件名后缀
	private String is_load; //是否直接加载数据
	private String row_separator; //数据行分隔符
	private String check_time; //检测时间
	private String signal_file_suffix; //信号文件后缀
	private String analysis_signalfile; //是否解析信号文件
	private Long classify_id; //分类id
	private String data_extract_type; //数据抽取方式
	private String is_header; //是否有表头
	private String cp_or; //清洗顺序
	private String jdbc_url; //数据库连接地址

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
	/** 取得：数据库设置id */
	public Long getDatabase_id(){
		return database_id;
	}
	/** 设置：数据库设置id */
	public void setDatabase_id(Long database_id){
		this.database_id=database_id;
	}
	/** 设置：数据库设置id */
	public void setDatabase_id(String database_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(database_id)){
			this.database_id=new Long(database_id);
		}
	}
	/** 取得：数据库采集任务名称 */
	public String getTask_name(){
		return task_name;
	}
	/** 设置：数据库采集任务名称 */
	public void setTask_name(String task_name){
		this.task_name=task_name;
	}
	/** 取得：数据库名称 */
	public String getDatabase_name(){
		return database_name;
	}
	/** 设置：数据库名称 */
	public void setDatabase_name(String database_name){
		this.database_name=database_name;
	}
	/** 取得：数据库密码 */
	public String getDatabase_pad(){
		return database_pad;
	}
	/** 设置：数据库密码 */
	public void setDatabase_pad(String database_pad){
		this.database_pad=database_pad;
	}
	/** 取得：数据库驱动 */
	public String getDatabase_drive(){
		return database_drive;
	}
	/** 设置：数据库驱动 */
	public void setDatabase_drive(String database_drive){
		this.database_drive=database_drive;
	}
	/** 取得：数据库类型 */
	public String getDatabase_type(){
		return database_type;
	}
	/** 设置：数据库类型 */
	public void setDatabase_type(String database_type){
		this.database_type=database_type;
	}
	/** 取得：用户名称 */
	public String getUser_name(){
		return user_name;
	}
	/** 设置：用户名称 */
	public void setUser_name(String user_name){
		this.user_name=user_name;
	}
	/** 取得：数据库服务器IP */
	public String getDatabase_ip(){
		return database_ip;
	}
	/** 设置：数据库服务器IP */
	public void setDatabase_ip(String database_ip){
		this.database_ip=database_ip;
	}
	/** 取得：数据库端口 */
	public String getDatabase_port(){
		return database_port;
	}
	/** 设置：数据库端口 */
	public void setDatabase_port(String database_port){
		this.database_port=database_port;
	}
	/** 取得：主机名 */
	public String getHost_name(){
		return host_name;
	}
	/** 设置：主机名 */
	public void setHost_name(String host_name){
		this.host_name=host_name;
	}
	/** 取得：操作系统类型 */
	public String getSystem_type(){
		return system_type;
	}
	/** 设置：操作系统类型 */
	public void setSystem_type(String system_type){
		this.system_type=system_type;
	}
	/** 取得：是否设置完成并发送成功 */
	public String getIs_sendok(){
		return is_sendok;
	}
	/** 设置：是否设置完成并发送成功 */
	public void setIs_sendok(String is_sendok){
		this.is_sendok=is_sendok;
	}
	/** 取得：数据库设置编号 */
	public String getDatabase_number(){
		return database_number;
	}
	/** 设置：数据库设置编号 */
	public void setDatabase_number(String database_number){
		this.database_number=database_number;
	}
	/** 取得：是否为平面DB数据采集 */
	public String getDb_agent(){
		return db_agent;
	}
	/** 设置：是否为平面DB数据采集 */
	public void setDb_agent(String db_agent){
		this.db_agent=db_agent;
	}
	/** 取得：DB文件源数据路径 */
	public String getPlane_url(){
		return plane_url;
	}
	/** 设置：DB文件源数据路径 */
	public void setPlane_url(String plane_url){
		this.plane_url=plane_url;
	}
	/** 取得：数据采用分隔符 */
	public String getDatabase_separatorr(){
		return database_separatorr;
	}
	/** 设置：数据采用分隔符 */
	public void setDatabase_separatorr(String database_separatorr){
		this.database_separatorr=database_separatorr;
	}
	/** 取得：数据使用编码格式 */
	public String getDatabase_code(){
		return database_code;
	}
	/** 设置：数据使用编码格式 */
	public void setDatabase_code(String database_code){
		this.database_code=database_code;
	}
	/** 取得：DB文件格式 */
	public String getDbfile_format(){
		return dbfile_format;
	}
	/** 设置：DB文件格式 */
	public void setDbfile_format(String dbfile_format){
		this.dbfile_format=dbfile_format;
	}
	/** 取得：分隔符是否为ASCII隐藏字符 */
	public String getIs_hidden(){
		return is_hidden;
	}
	/** 设置：分隔符是否为ASCII隐藏字符 */
	public void setIs_hidden(String is_hidden){
		this.is_hidden=is_hidden;
	}
	/** 取得：采集文件名后缀 */
	public String getFile_suffix(){
		return file_suffix;
	}
	/** 设置：采集文件名后缀 */
	public void setFile_suffix(String file_suffix){
		this.file_suffix=file_suffix;
	}
	/** 取得：是否直接加载数据 */
	public String getIs_load(){
		return is_load;
	}
	/** 设置：是否直接加载数据 */
	public void setIs_load(String is_load){
		this.is_load=is_load;
	}
	/** 取得：数据行分隔符 */
	public String getRow_separator(){
		return row_separator;
	}
	/** 设置：数据行分隔符 */
	public void setRow_separator(String row_separator){
		this.row_separator=row_separator;
	}
	/** 取得：检测时间 */
	public String getCheck_time(){
		return check_time;
	}
	/** 设置：检测时间 */
	public void setCheck_time(String check_time){
		this.check_time=check_time;
	}
	/** 取得：信号文件后缀 */
	public String getSignal_file_suffix(){
		return signal_file_suffix;
	}
	/** 设置：信号文件后缀 */
	public void setSignal_file_suffix(String signal_file_suffix){
		this.signal_file_suffix=signal_file_suffix;
	}
	/** 取得：是否解析信号文件 */
	public String getAnalysis_signalfile(){
		return analysis_signalfile;
	}
	/** 设置：是否解析信号文件 */
	public void setAnalysis_signalfile(String analysis_signalfile){
		this.analysis_signalfile=analysis_signalfile;
	}
	/** 取得：分类id */
	public Long getClassify_id(){
		return classify_id;
	}
	/** 设置：分类id */
	public void setClassify_id(Long classify_id){
		this.classify_id=classify_id;
	}
	/** 设置：分类id */
	public void setClassify_id(String classify_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(classify_id)){
			this.classify_id=new Long(classify_id);
		}
	}
	/** 取得：数据抽取方式 */
	public String getData_extract_type(){
		return data_extract_type;
	}
	/** 设置：数据抽取方式 */
	public void setData_extract_type(String data_extract_type){
		this.data_extract_type=data_extract_type;
	}
	/** 取得：是否有表头 */
	public String getIs_header(){
		return is_header;
	}
	/** 设置：是否有表头 */
	public void setIs_header(String is_header){
		this.is_header=is_header;
	}
	/** 取得：清洗顺序 */
	public String getCp_or(){
		return cp_or;
	}
	/** 设置：清洗顺序 */
	public void setCp_or(String cp_or){
		this.cp_or=cp_or;
	}
	/** 取得：数据库连接地址 */
	public String getJdbc_url(){
		return jdbc_url;
	}
	/** 设置：数据库连接地址 */
	public void setJdbc_url(String jdbc_url){
		this.jdbc_url=jdbc_url;
	}
}
