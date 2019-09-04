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
 * 机器学习数据拆分分层抽取表
 */
@Table(tableName = "ml_datas_hext")
public class Ml_datas_hext extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_datas_hext";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 机器学习数据拆分分层抽取表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("hierextr_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long hierextr_id; //分层抽取编号
	private Long hierextr_seedn; //分层抽取种子总数
	private BigDecimal trainset_perc; //训练集占比
	private Long datasplit_id; //数据拆分编号
	private String extr_column; //抽取字段

	/** 取得：分层抽取编号 */
	public Long getHierextr_id(){
		return hierextr_id;
	}
	/** 设置：分层抽取编号 */
	public void setHierextr_id(Long hierextr_id){
		this.hierextr_id=hierextr_id;
	}
	/** 设置：分层抽取编号 */
	public void setHierextr_id(String hierextr_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(hierextr_id)){
			this.hierextr_id=new Long(hierextr_id);
		}
	}
	/** 取得：分层抽取种子总数 */
	public Long getHierextr_seedn(){
		return hierextr_seedn;
	}
	/** 设置：分层抽取种子总数 */
	public void setHierextr_seedn(Long hierextr_seedn){
		this.hierextr_seedn=hierextr_seedn;
	}
	/** 设置：分层抽取种子总数 */
	public void setHierextr_seedn(String hierextr_seedn){
		if(!fd.ng.core.utils.StringUtil.isEmpty(hierextr_seedn)){
			this.hierextr_seedn=new Long(hierextr_seedn);
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
	/** 取得：抽取字段 */
	public String getExtr_column(){
		return extr_column;
	}
	/** 设置：抽取字段 */
	public void setExtr_column(String extr_column){
		this.extr_column=extr_column;
	}
}
