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
 * 机器学习数据探索相关性分析表
 */
@Table(tableName = "ml_corranalyse")
public class Ml_corranalyse extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_corranalyse";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 机器学习数据探索相关性分析表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("correlation_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long correlation_id; //相关性表编号
	private String corr_type; //系数类型
	private String remark; //备注
	private Long dtable_info_id; //数据表信息编号
	private String create_date; //创建日期
	private String create_time; //创建时间

	/** 取得：相关性表编号 */
	public Long getCorrelation_id(){
		return correlation_id;
	}
	/** 设置：相关性表编号 */
	public void setCorrelation_id(Long correlation_id){
		this.correlation_id=correlation_id;
	}
	/** 设置：相关性表编号 */
	public void setCorrelation_id(String correlation_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(correlation_id)){
			this.correlation_id=new Long(correlation_id);
		}
	}
	/** 取得：系数类型 */
	public String getCorr_type(){
		return corr_type;
	}
	/** 设置：系数类型 */
	public void setCorr_type(String corr_type){
		this.corr_type=corr_type;
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
}
