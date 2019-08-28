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
 * 机器学习穷举法特征搜索表
 */
@Table(tableName = "ml_efsearch")
public class Ml_efsearch extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_efsearch";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 机器学习穷举法特征搜索表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("exhafeats_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long exhafeats_id; //穷举法特征搜索编号
	private String dv_column; //因变量字段
	private Long featmin_num; //最小特征个数
	private Long featlarg_num; //最大特征个数
	private Long crossveri_num; //交叉验证数
	private Long dtable_info_id; //数据表信息编号
	private String algorithm_type; //算法类型
	private String specific_algorithm; //具体算法
	private String algo_eval_crit; //算法评判标准
	private String create_date; //创建日期
	private String create_time; //创建时间
	private String remark; //备注

	/** 取得：穷举法特征搜索编号 */
	public Long getExhafeats_id(){
		return exhafeats_id;
	}
	/** 设置：穷举法特征搜索编号 */
	public void setExhafeats_id(Long exhafeats_id){
		this.exhafeats_id=exhafeats_id;
	}
	/** 设置：穷举法特征搜索编号 */
	public void setExhafeats_id(String exhafeats_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(exhafeats_id)){
			this.exhafeats_id=new Long(exhafeats_id);
		}
	}
	/** 取得：因变量字段 */
	public String getDv_column(){
		return dv_column;
	}
	/** 设置：因变量字段 */
	public void setDv_column(String dv_column){
		this.dv_column=dv_column;
	}
	/** 取得：最小特征个数 */
	public Long getFeatmin_num(){
		return featmin_num;
	}
	/** 设置：最小特征个数 */
	public void setFeatmin_num(Long featmin_num){
		this.featmin_num=featmin_num;
	}
	/** 设置：最小特征个数 */
	public void setFeatmin_num(String featmin_num){
		if(!fd.ng.core.utils.StringUtil.isEmpty(featmin_num)){
			this.featmin_num=new Long(featmin_num);
		}
	}
	/** 取得：最大特征个数 */
	public Long getFeatlarg_num(){
		return featlarg_num;
	}
	/** 设置：最大特征个数 */
	public void setFeatlarg_num(Long featlarg_num){
		this.featlarg_num=featlarg_num;
	}
	/** 设置：最大特征个数 */
	public void setFeatlarg_num(String featlarg_num){
		if(!fd.ng.core.utils.StringUtil.isEmpty(featlarg_num)){
			this.featlarg_num=new Long(featlarg_num);
		}
	}
	/** 取得：交叉验证数 */
	public Long getCrossveri_num(){
		return crossveri_num;
	}
	/** 设置：交叉验证数 */
	public void setCrossveri_num(Long crossveri_num){
		this.crossveri_num=crossveri_num;
	}
	/** 设置：交叉验证数 */
	public void setCrossveri_num(String crossveri_num){
		if(!fd.ng.core.utils.StringUtil.isEmpty(crossveri_num)){
			this.crossveri_num=new Long(crossveri_num);
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
	/** 取得：算法类型 */
	public String getAlgorithm_type(){
		return algorithm_type;
	}
	/** 设置：算法类型 */
	public void setAlgorithm_type(String algorithm_type){
		this.algorithm_type=algorithm_type;
	}
	/** 取得：具体算法 */
	public String getSpecific_algorithm(){
		return specific_algorithm;
	}
	/** 设置：具体算法 */
	public void setSpecific_algorithm(String specific_algorithm){
		this.specific_algorithm=specific_algorithm;
	}
	/** 取得：算法评判标准 */
	public String getAlgo_eval_crit(){
		return algo_eval_crit;
	}
	/** 设置：算法评判标准 */
	public void setAlgo_eval_crit(String algo_eval_crit){
		this.algo_eval_crit=algo_eval_crit;
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
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
}
