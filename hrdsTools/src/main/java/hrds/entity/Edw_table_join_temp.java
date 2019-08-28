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
 * 表关联关系临时表
 */
@Table(tableName = "edw_table_join_temp")
public class Edw_table_join_temp extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "edw_table_join_temp";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 表关联关系临时表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("jobcode");
		__tmpPKS.add("serial_num");
		__tmpPKS.add("st_dt");
		__tmpPKS.add("st_time");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private String jobcode; //作业编号
	private Integer serial_num; //序号
	private String tabname; //表名
	private String tabalias; //表别称
	private String jointype; //关联方式
	private String join_condition; //关联规则
	private String st_dt; //开始日期
	private String end_dt; //结束日期
	private String usercode; //用户编号
	private String st_time; //开始时间

	/** 取得：作业编号 */
	public String getJobcode(){
		return jobcode;
	}
	/** 设置：作业编号 */
	public void setJobcode(String jobcode){
		this.jobcode=jobcode;
	}
	/** 取得：序号 */
	public Integer getSerial_num(){
		return serial_num;
	}
	/** 设置：序号 */
	public void setSerial_num(Integer serial_num){
		this.serial_num=serial_num;
	}
	/** 设置：序号 */
	public void setSerial_num(String serial_num){
		if(!fd.ng.core.utils.StringUtil.isEmpty(serial_num)){
			this.serial_num=new Integer(serial_num);
		}
	}
	/** 取得：表名 */
	public String getTabname(){
		return tabname;
	}
	/** 设置：表名 */
	public void setTabname(String tabname){
		this.tabname=tabname;
	}
	/** 取得：表别称 */
	public String getTabalias(){
		return tabalias;
	}
	/** 设置：表别称 */
	public void setTabalias(String tabalias){
		this.tabalias=tabalias;
	}
	/** 取得：关联方式 */
	public String getJointype(){
		return jointype;
	}
	/** 设置：关联方式 */
	public void setJointype(String jointype){
		this.jointype=jointype;
	}
	/** 取得：关联规则 */
	public String getJoin_condition(){
		return join_condition;
	}
	/** 设置：关联规则 */
	public void setJoin_condition(String join_condition){
		this.join_condition=join_condition;
	}
	/** 取得：开始日期 */
	public String getSt_dt(){
		return st_dt;
	}
	/** 设置：开始日期 */
	public void setSt_dt(String st_dt){
		this.st_dt=st_dt;
	}
	/** 取得：结束日期 */
	public String getEnd_dt(){
		return end_dt;
	}
	/** 设置：结束日期 */
	public void setEnd_dt(String end_dt){
		this.end_dt=end_dt;
	}
	/** 取得：用户编号 */
	public String getUsercode(){
		return usercode;
	}
	/** 设置：用户编号 */
	public void setUsercode(String usercode){
		this.usercode=usercode;
	}
	/** 取得：开始时间 */
	public String getSt_time(){
		return st_time;
	}
	/** 设置：开始时间 */
	public void setSt_time(String st_time){
		this.st_time=st_time;
	}
}
