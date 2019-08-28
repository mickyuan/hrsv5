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
 * 机器学习k-means模型表
 */
@Table(tableName = "ml_kmeansmodel")
public class Ml_kmeansmodel extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_kmeansmodel";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 机器学习k-means模型表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("model_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long model_id; //模型编号
	private String model_name; //模型名称
	private Long numiterations; //迭代次数
	private Long clusternum; //聚类个数
	private String create_date; //创建日期
	private String create_time; //创建时间
	private String model_runstate; //模型运行状态
	private String model_path; //模型地址
	private String remark; //备注
	private Long dtable_info_id; //数据表信息编号
	private String observe_columns; //观察字段

	/** 取得：模型编号 */
	public Long getModel_id(){
		return model_id;
	}
	/** 设置：模型编号 */
	public void setModel_id(Long model_id){
		this.model_id=model_id;
	}
	/** 设置：模型编号 */
	public void setModel_id(String model_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(model_id)){
			this.model_id=new Long(model_id);
		}
	}
	/** 取得：模型名称 */
	public String getModel_name(){
		return model_name;
	}
	/** 设置：模型名称 */
	public void setModel_name(String model_name){
		this.model_name=model_name;
	}
	/** 取得：迭代次数 */
	public Long getNumiterations(){
		return numiterations;
	}
	/** 设置：迭代次数 */
	public void setNumiterations(Long numiterations){
		this.numiterations=numiterations;
	}
	/** 设置：迭代次数 */
	public void setNumiterations(String numiterations){
		if(!fd.ng.core.utils.StringUtil.isEmpty(numiterations)){
			this.numiterations=new Long(numiterations);
		}
	}
	/** 取得：聚类个数 */
	public Long getClusternum(){
		return clusternum;
	}
	/** 设置：聚类个数 */
	public void setClusternum(Long clusternum){
		this.clusternum=clusternum;
	}
	/** 设置：聚类个数 */
	public void setClusternum(String clusternum){
		if(!fd.ng.core.utils.StringUtil.isEmpty(clusternum)){
			this.clusternum=new Long(clusternum);
		}
	}
	/** 取得：创建日期 */
	public String getCreate_date(){
		return create_date;
	}
	/** 设置：创建日期 */
	public void setCreate_date(String create_date){
		this.create_date=create_date;
	}
	/** 取得：创建时间 */
	public String getCreate_time(){
		return create_time;
	}
	/** 设置：创建时间 */
	public void setCreate_time(String create_time){
		this.create_time=create_time;
	}
	/** 取得：模型运行状态 */
	public String getModel_runstate(){
		return model_runstate;
	}
	/** 设置：模型运行状态 */
	public void setModel_runstate(String model_runstate){
		this.model_runstate=model_runstate;
	}
	/** 取得：模型地址 */
	public String getModel_path(){
		return model_path;
	}
	/** 设置：模型地址 */
	public void setModel_path(String model_path){
		this.model_path=model_path;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
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
	/** 取得：观察字段 */
	public String getObserve_columns(){
		return observe_columns;
	}
	/** 设置：观察字段 */
	public void setObserve_columns(String observe_columns){
		this.observe_columns=observe_columns;
	}
}
