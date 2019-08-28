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
 * 机器学习pagerank算法表
 */
@Table(tableName = "ml_pagerankalgo")
public class Ml_pagerankalgo extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_pagerankalgo";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 机器学习pagerank算法表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("algorithm_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long algorithm_id; //算法编号
	private String algorithm_name; //算法名称
	private String vote_link; //投票链接字段
	private String bevoted_link; //受投链接字段
	private Long numiterations; //迭代次数
	private String create_date; //创建日期
	private String create_time; //创建时间
	private String algo_runstate; //算法运行状态
	private String result_path; //结果地址
	private String remark; //备注
	private Long dtable_info_id; //数据表信息编号

	/** 取得：算法编号 */
	public Long getAlgorithm_id(){
		return algorithm_id;
	}
	/** 设置：算法编号 */
	public void setAlgorithm_id(Long algorithm_id){
		this.algorithm_id=algorithm_id;
	}
	/** 设置：算法编号 */
	public void setAlgorithm_id(String algorithm_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(algorithm_id)){
			this.algorithm_id=new Long(algorithm_id);
		}
	}
	/** 取得：算法名称 */
	public String getAlgorithm_name(){
		return algorithm_name;
	}
	/** 设置：算法名称 */
	public void setAlgorithm_name(String algorithm_name){
		this.algorithm_name=algorithm_name;
	}
	/** 取得：投票链接字段 */
	public String getVote_link(){
		return vote_link;
	}
	/** 设置：投票链接字段 */
	public void setVote_link(String vote_link){
		this.vote_link=vote_link;
	}
	/** 取得：受投链接字段 */
	public String getBevoted_link(){
		return bevoted_link;
	}
	/** 设置：受投链接字段 */
	public void setBevoted_link(String bevoted_link){
		this.bevoted_link=bevoted_link;
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
	/** 取得：算法运行状态 */
	public String getAlgo_runstate(){
		return algo_runstate;
	}
	/** 设置：算法运行状态 */
	public void setAlgo_runstate(String algo_runstate){
		this.algo_runstate=algo_runstate;
	}
	/** 取得：结果地址 */
	public String getResult_path(){
		return result_path;
	}
	/** 设置：结果地址 */
	public void setResult_path(String result_path){
		this.result_path=result_path;
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
}
