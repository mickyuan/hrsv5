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
 * 机器学习梯度增强回归树模型
 */
@Table(tableName = "ml_gbrtmodel")
public class Ml_gbrtmodel extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_gbrtmodel";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 机器学习梯度增强回归树模型 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("model_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long model_id; //模型编号
	private Long n_estimators; //估计量个数
	private BigDecimal learning_rate; //学习率
	private Long max_depth; //最大深度
	private Long random_state; //随机种子数
	private Long max_leaf_nodes; //最大叶子节点数
	private String create_date; //创建日期
	private String create_time; //创建时间
	private String model_runstate; //模型运行状态
	private String model_path; //模型地址
	private String remark; //备注
	private String dv_column; //因变量字段
	private Long dtable_info_id; //数据表信息编号
	private String model_name; //模型名称

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
	/** 取得：估计量个数 */
	public Long getN_estimators(){
		return n_estimators;
	}
	/** 设置：估计量个数 */
	public void setN_estimators(Long n_estimators){
		this.n_estimators=n_estimators;
	}
	/** 设置：估计量个数 */
	public void setN_estimators(String n_estimators){
		if(!fd.ng.core.utils.StringUtil.isEmpty(n_estimators)){
			this.n_estimators=new Long(n_estimators);
		}
	}
	/** 取得：学习率 */
	public BigDecimal getLearning_rate(){
		return learning_rate;
	}
	/** 设置：学习率 */
	public void setLearning_rate(BigDecimal learning_rate){
		this.learning_rate=learning_rate;
	}
	/** 设置：学习率 */
	public void setLearning_rate(String learning_rate){
		if(!fd.ng.core.utils.StringUtil.isEmpty(learning_rate)){
			this.learning_rate=new BigDecimal(learning_rate);
		}
	}
	/** 取得：最大深度 */
	public Long getMax_depth(){
		return max_depth;
	}
	/** 设置：最大深度 */
	public void setMax_depth(Long max_depth){
		this.max_depth=max_depth;
	}
	/** 设置：最大深度 */
	public void setMax_depth(String max_depth){
		if(!fd.ng.core.utils.StringUtil.isEmpty(max_depth)){
			this.max_depth=new Long(max_depth);
		}
	}
	/** 取得：随机种子数 */
	public Long getRandom_state(){
		return random_state;
	}
	/** 设置：随机种子数 */
	public void setRandom_state(Long random_state){
		this.random_state=random_state;
	}
	/** 设置：随机种子数 */
	public void setRandom_state(String random_state){
		if(!fd.ng.core.utils.StringUtil.isEmpty(random_state)){
			this.random_state=new Long(random_state);
		}
	}
	/** 取得：最大叶子节点数 */
	public Long getMax_leaf_nodes(){
		return max_leaf_nodes;
	}
	/** 设置：最大叶子节点数 */
	public void setMax_leaf_nodes(Long max_leaf_nodes){
		this.max_leaf_nodes=max_leaf_nodes;
	}
	/** 设置：最大叶子节点数 */
	public void setMax_leaf_nodes(String max_leaf_nodes){
		if(!fd.ng.core.utils.StringUtil.isEmpty(max_leaf_nodes)){
			this.max_leaf_nodes=new Long(max_leaf_nodes);
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
	/** 取得：因变量字段 */
	public String getDv_column(){
		return dv_column;
	}
	/** 设置：因变量字段 */
	public void setDv_column(String dv_column){
		this.dv_column=dv_column;
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
	/** 取得：模型名称 */
	public String getModel_name(){
		return model_name;
	}
	/** 设置：模型名称 */
	public void setModel_name(String model_name){
		this.model_name=model_name;
	}
}
