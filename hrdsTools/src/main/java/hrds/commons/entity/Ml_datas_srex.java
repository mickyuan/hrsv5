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
 * 机器学习数据拆分简单随机抽取表
 */
@Table(tableName = "ml_datas_srex")
public class Ml_datas_srex extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_datas_srex";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 机器学习数据拆分简单随机抽取表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("rextr_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long rextr_id; //随机抽取编号
	private Long rextrseed_num; //随机抽取种子总数
	private BigDecimal trainset_perc; //训练集占比
	private BigDecimal testset_perc; //测试集占比
	private Long datasplit_id; //数据拆分编号

	/** 取得：随机抽取编号 */
	public Long getRextr_id(){
		return rextr_id;
	}
	/** 设置：随机抽取编号 */
	public void setRextr_id(Long rextr_id){
		this.rextr_id=rextr_id;
	}
	/** 设置：随机抽取编号 */
	public void setRextr_id(String rextr_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(rextr_id)){
			this.rextr_id=new Long(rextr_id);
		}
	}
	/** 取得：随机抽取种子总数 */
	public Long getRextrseed_num(){
		return rextrseed_num;
	}
	/** 设置：随机抽取种子总数 */
	public void setRextrseed_num(Long rextrseed_num){
		this.rextrseed_num=rextrseed_num;
	}
	/** 设置：随机抽取种子总数 */
	public void setRextrseed_num(String rextrseed_num){
		if(!fd.ng.core.utils.StringUtil.isEmpty(rextrseed_num)){
			this.rextrseed_num=new Long(rextrseed_num);
		}
	}
	/** 取得：训练集占比 */
	public BigDecimal getTrainset_perc(){
		return trainset_perc;
	}
	/** 设置：训练集占比 */
	public void setTrainset_perc(BigDecimal trainset_perc){
		this.trainset_perc=trainset_perc;
	}
	/** 设置：训练集占比 */
	public void setTrainset_perc(String trainset_perc){
		if(!fd.ng.core.utils.StringUtil.isEmpty(trainset_perc)){
			this.trainset_perc=new BigDecimal(trainset_perc);
		}
	}
	/** 取得：测试集占比 */
	public BigDecimal getTestset_perc(){
		return testset_perc;
	}
	/** 设置：测试集占比 */
	public void setTestset_perc(BigDecimal testset_perc){
		this.testset_perc=testset_perc;
	}
	/** 设置：测试集占比 */
	public void setTestset_perc(String testset_perc){
		if(!fd.ng.core.utils.StringUtil.isEmpty(testset_perc)){
			this.testset_perc=new BigDecimal(testset_perc);
		}
	}
	/** 取得：数据拆分编号 */
	public Long getDatasplit_id(){
		return datasplit_id;
	}
	/** 设置：数据拆分编号 */
	public void setDatasplit_id(Long datasplit_id){
		this.datasplit_id=datasplit_id;
	}
	/** 设置：数据拆分编号 */
	public void setDatasplit_id(String datasplit_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(datasplit_id)){
			this.datasplit_id=new Long(datasplit_id);
		}
	}
}
