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
 * 机器学习特征过滤表
 */
@Table(tableName = "ml_feature_filter")
public class Ml_feature_filter extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_feature_filter";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 机器学习特征过滤表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("featfilt_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long featfilt_id; //特征过滤编号
	private BigDecimal variance_range; //方差值域
	private Long dtable_info_id; //数据表信息编号
	private String create_date; //创建日期
	private String create_time; //创建时间
	private String remark; //备注

	/** 取得：特征过滤编号 */
	public Long getFeatfilt_id(){
		return featfilt_id;
	}
	/** 设置：特征过滤编号 */
	public void setFeatfilt_id(Long featfilt_id){
		this.featfilt_id=featfilt_id;
	}
	/** 设置：特征过滤编号 */
	public void setFeatfilt_id(String featfilt_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(featfilt_id)){
			this.featfilt_id=new Long(featfilt_id);
		}
	}
	/** 取得：方差值域 */
	public BigDecimal getVariance_range(){
		return variance_range;
	}
	/** 设置：方差值域 */
	public void setVariance_range(BigDecimal variance_range){
		this.variance_range=variance_range;
	}
	/** 设置：方差值域 */
	public void setVariance_range(String variance_range){
		if(!fd.ng.core.utils.StringUtil.isEmpty(variance_range)){
			this.variance_range=new BigDecimal(variance_range);
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
