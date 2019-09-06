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
 * 数据质量校验结果表
 */
@Table(tableName = "dq_result")
public class Dq_result extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dq_result";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据质量校验结果表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("task_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private String task_id; //任务编号
	private String verify_date; //校验会计日期
	private Long reg_num; //规则编号
	private String target_tab; //目标表名
	private String target_key_fields; //目标表关键字段
	private String start_date; //执行开始日期
	private String start_time; //执行开始时间
	private String end_date; //执行结束日期
	private String end_time; //执行结束时间
	private Integer elapsed_ms; //执行耗时
	private String verify_result; //校验结果
	private Integer check_index1; //检查指标1结果
	private Integer check_index2; //检查指标2结果
	private String check_index3; //检查指标3结果
	private String index_desc1; //检查指标1含义
	private String index_desc2; //检查指标2含义
	private String index_desc3; //检查指标3含义
	private String errno; //校验错误码
	private String verify_sql; //校验SQL
	private String err_dtl_sql; //异常明细SQL
	private String remark; //备注
	private String dl_stat; //处理状态
	private String exec_mode; //执行方式
	private String err_dtl_file_name; //异常数据文件名
	private String case_type; //规则类型
	private String is_saveindex3; //是否保存指标3数据
	private String is_saveindex1; //是否保存指标1数据
	private String is_saveindex2; //是否保存指标2数据

	/** 取得：任务编号 */
	public String getTask_id(){
		return task_id;
	}
	/** 设置：任务编号 */
	public void setTask_id(String task_id){
		this.task_id=task_id;
	}
	/** 取得：校验会计日期 */
	public String getVerify_date(){
		return verify_date;
	}
	/** 设置：校验会计日期 */
	public void setVerify_date(String verify_date){
		this.verify_date=verify_date;
	}
	/** 取得：规则编号 */
	public Long getReg_num(){
		return reg_num;
	}
	/** 设置：规则编号 */
	public void setReg_num(Long reg_num){
		this.reg_num=reg_num;
	}
	/** 设置：规则编号 */
	public void setReg_num(String reg_num){
		if(!fd.ng.core.utils.StringUtil.isEmpty(reg_num)){
			this.reg_num=new Long(reg_num);
		}
	}
	/** 取得：目标表名 */
	public String getTarget_tab(){
		return target_tab;
	}
	/** 设置：目标表名 */
	public void setTarget_tab(String target_tab){
		this.target_tab=target_tab;
	}
	/** 取得：目标表关键字段 */
	public String getTarget_key_fields(){
		return target_key_fields;
	}
	/** 设置：目标表关键字段 */
	public void setTarget_key_fields(String target_key_fields){
		this.target_key_fields=target_key_fields;
	}
	/** 取得：执行开始日期 */
	public String getStart_date(){
		return start_date;
	}
	/** 设置：执行开始日期 */
	public void setStart_date(String start_date){
		this.start_date=start_date;
	}
	/** 取得：执行开始时间 */
	public String getStart_time(){
		return start_time;
	}
	/** 设置：执行开始时间 */
	public void setStart_time(String start_time){
		this.start_time=start_time;
	}
	/** 取得：执行结束日期 */
	public String getEnd_date(){
		return end_date;
	}
	/** 设置：执行结束日期 */
	public void setEnd_date(String end_date){
		this.end_date=end_date;
	}
	/** 取得：执行结束时间 */
	public String getEnd_time(){
		return end_time;
	}
	/** 设置：执行结束时间 */
	public void setEnd_time(String end_time){
		this.end_time=end_time;
	}
	/** 取得：执行耗时 */
	public Integer getElapsed_ms(){
		return elapsed_ms;
	}
	/** 设置：执行耗时 */
	public void setElapsed_ms(Integer elapsed_ms){
		this.elapsed_ms=elapsed_ms;
	}
	/** 设置：执行耗时 */
	public void setElapsed_ms(String elapsed_ms){
		if(!fd.ng.core.utils.StringUtil.isEmpty(elapsed_ms)){
			this.elapsed_ms=new Integer(elapsed_ms);
		}
	}
	/** 取得：校验结果 */
	public String getVerify_result(){
		return verify_result;
	}
	/** 设置：校验结果 */
	public void setVerify_result(String verify_result){
		this.verify_result=verify_result;
	}
	/** 取得：检查指标1结果 */
	public Integer getCheck_index1(){
		return check_index1;
	}
	/** 设置：检查指标1结果 */
	public void setCheck_index1(Integer check_index1){
		this.check_index1=check_index1;
	}
	/** 设置：检查指标1结果 */
	public void setCheck_index1(String check_index1){
		if(!fd.ng.core.utils.StringUtil.isEmpty(check_index1)){
			this.check_index1=new Integer(check_index1);
		}
	}
	/** 取得：检查指标2结果 */
	public Integer getCheck_index2(){
		return check_index2;
	}
	/** 设置：检查指标2结果 */
	public void setCheck_index2(Integer check_index2){
		this.check_index2=check_index2;
	}
	/** 设置：检查指标2结果 */
	public void setCheck_index2(String check_index2){
		if(!fd.ng.core.utils.StringUtil.isEmpty(check_index2)){
			this.check_index2=new Integer(check_index2);
		}
	}
	/** 取得：检查指标3结果 */
	public String getCheck_index3(){
		return check_index3;
	}
	/** 设置：检查指标3结果 */
	public void setCheck_index3(String check_index3){
		this.check_index3=check_index3;
	}
	/** 取得：检查指标1含义 */
	public String getIndex_desc1(){
		return index_desc1;
	}
	/** 设置：检查指标1含义 */
	public void setIndex_desc1(String index_desc1){
		this.index_desc1=index_desc1;
	}
	/** 取得：检查指标2含义 */
	public String getIndex_desc2(){
		return index_desc2;
	}
	/** 设置：检查指标2含义 */
	public void setIndex_desc2(String index_desc2){
		this.index_desc2=index_desc2;
	}
	/** 取得：检查指标3含义 */
	public String getIndex_desc3(){
		return index_desc3;
	}
	/** 设置：检查指标3含义 */
	public void setIndex_desc3(String index_desc3){
		this.index_desc3=index_desc3;
	}
	/** 取得：校验错误码 */
	public String getErrno(){
		return errno;
	}
	/** 设置：校验错误码 */
	public void setErrno(String errno){
		this.errno=errno;
	}
	/** 取得：校验SQL */
	public String getVerify_sql(){
		return verify_sql;
	}
	/** 设置：校验SQL */
	public void setVerify_sql(String verify_sql){
		this.verify_sql=verify_sql;
	}
	/** 取得：异常明细SQL */
	public String getErr_dtl_sql(){
		return err_dtl_sql;
	}
	/** 设置：异常明细SQL */
	public void setErr_dtl_sql(String err_dtl_sql){
		this.err_dtl_sql=err_dtl_sql;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：处理状态 */
	public String getDl_stat(){
		return dl_stat;
	}
	/** 设置：处理状态 */
	public void setDl_stat(String dl_stat){
		this.dl_stat=dl_stat;
	}
	/** 取得：执行方式 */
	public String getExec_mode(){
		return exec_mode;
	}
	/** 设置：执行方式 */
	public void setExec_mode(String exec_mode){
		this.exec_mode=exec_mode;
	}
	/** 取得：异常数据文件名 */
	public String getErr_dtl_file_name(){
		return err_dtl_file_name;
	}
	/** 设置：异常数据文件名 */
	public void setErr_dtl_file_name(String err_dtl_file_name){
		this.err_dtl_file_name=err_dtl_file_name;
	}
	/** 取得：规则类型 */
	public String getCase_type(){
		return case_type;
	}
	/** 设置：规则类型 */
	public void setCase_type(String case_type){
		this.case_type=case_type;
	}
	/** 取得：是否保存指标3数据 */
	public String getIs_saveindex3(){
		return is_saveindex3;
	}
	/** 设置：是否保存指标3数据 */
	public void setIs_saveindex3(String is_saveindex3){
		this.is_saveindex3=is_saveindex3;
	}
	/** 取得：是否保存指标1数据 */
	public String getIs_saveindex1(){
		return is_saveindex1;
	}
	/** 设置：是否保存指标1数据 */
	public void setIs_saveindex1(String is_saveindex1){
		this.is_saveindex1=is_saveindex1;
	}
	/** 取得：是否保存指标2数据 */
	public String getIs_saveindex2(){
		return is_saveindex2;
	}
	/** 设置：是否保存指标2数据 */
	public void setIs_saveindex2(String is_saveindex2){
		this.is_saveindex2=is_saveindex2;
	}
}
