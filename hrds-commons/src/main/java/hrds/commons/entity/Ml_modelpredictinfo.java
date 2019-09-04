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
 * 机器学习模型预测信息表
 */
@Table(tableName = "ml_modelpredictinfo")
public class Ml_modelpredictinfo extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_modelpredictinfo";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 机器学习模型预测信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("predictinfo_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long predictinfo_id; //预测信息编号
	private String model_type; //模型类型
	private String specificmodel; //具体模型
	private String predict_status; //模型预测状态
	private String model_path; //模型路径
	private String predict_path; //预测结果路径
	private String predictstratdate; //预测开始日期
	private String predictstarttime; //预测开始时间
	private String predictendtime; //预测结束时间
	private Long project_id; //项目编号
	private Long dtable_info_id; //数据表信息编号
	private Long user_id; //用户ID
	private String filename; //结果文件名称

	/** 取得：预测信息编号 */
	public Long getPredictinfo_id(){
		return predictinfo_id;
	}
	/** 设置：预测信息编号 */
	public void setPredictinfo_id(Long predictinfo_id){
		this.predictinfo_id=predictinfo_id;
	}
	/** 设置：预测信息编号 */
	public void setPredictinfo_id(String predictinfo_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(predictinfo_id)){
			this.predictinfo_id=new Long(predictinfo_id);
		}
	}
	/** 取得：模型类型 */
	public String getModel_type(){
		return model_type;
	}
	/** 设置：模型类型 */
	public void setModel_type(String model_type){
		this.model_type=model_type;
	}
	/** 取得：具体模型 */
	public String getSpecificmodel(){
		return specificmodel;
	}
	/** 设置：具体模型 */
	public void setSpecificmodel(String specificmodel){
		this.specificmodel=specificmodel;
	}
	/** 取得：模型预测状态 */
	public String getPredict_status(){
		return predict_status;
	}
	/** 设置：模型预测状态 */
	public void setPredict_status(String predict_status){
		this.predict_status=predict_status;
	}
	/** 取得：模型路径 */
	public String getModel_path(){
		return model_path;
	}
	/** 设置：模型路径 */
	public void setModel_path(String model_path){
		this.model_path=model_path;
	}
	/** 取得：预测结果路径 */
	public String getPredict_path(){
		return predict_path;
	}
	/** 设置：预测结果路径 */
	public void setPredict_path(String predict_path){
		this.predict_path=predict_path;
	}
	/** 取得：预测开始日期 */
	public String getPredictstratdate(){
		return predictstratdate;
	}
	/** 设置：预测开始日期 */
	public void setPredictstratdate(String predictstratdate){
		this.predictstratdate=predictstratdate;
	}
	/** 取得：预测开始时间 */
	public String getPredictstarttime(){
		return predictstarttime;
	}
	/** 设置：预测开始时间 */
	public void setPredictstarttime(String predictstarttime){
		this.predictstarttime=predictstarttime;
	}
	/** 取得：预测结束时间 */
	public String getPredictendtime(){
		return predictendtime;
	}
	/** 设置：预测结束时间 */
	public void setPredictendtime(String predictendtime){
		this.predictendtime=predictendtime;
	}
	/** 取得：项目编号 */
	public Long getProject_id(){
		return project_id;
	}
	/** 设置：项目编号 */
	public void setProject_id(Long project_id){
		this.project_id=project_id;
	}
	/** 设置：项目编号 */
	public void setProject_id(String project_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(project_id)){
			this.project_id=new Long(project_id);
		}
	}
	/** 取得：数据表信息编号 */
	public Long getDtable_info_id(){
		return dtable_info_id;
	}
	/** 设置：数据表信息编号 */
	public void setDtable_info_id(Long dtable_info_id){
		this.dtable_info_id=dtable_info_id;
	}
	/** 设置：数据表信息编号 */
	public void setDtable_info_id(String dtable_info_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(dtable_info_id)){
			this.dtable_info_id=new Long(dtable_info_id);
		}
	}
	/** 取得：用户ID */
	public Long getUser_id(){
		return user_id;
	}
	/** 设置：用户ID */
	public void setUser_id(Long user_id){
		this.user_id=user_id;
	}
	/** 设置：用户ID */
	public void setUser_id(String user_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(user_id)){
			this.user_id=new Long(user_id);
		}
	}
	/** 取得：结果文件名称 */
	public String getFilename(){
		return filename;
	}
	/** 设置：结果文件名称 */
	public void setFilename(String filename){
		this.filename=filename;
	}
}
