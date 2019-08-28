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
 * 机器学习假设f检验表
 */
@Table(tableName = "ml_f_hypothesis_test")
public class Ml_f_hypothesis_test extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_f_hypothesis_test";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 机器学习假设f检验表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("f_hypotest_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long f_hypotest_id; //f检验编号
	private String f_column_first; //f检验字段1
	private String f_column_second; //f检验字段2
	private String create_date; //创建日期
	private String create_time; //创建时间
	private String remark; //备注
	private Long dtable_info_id; //数据表信息编号

	/** 取得：f检验编号 */
	public Long getF_hypotest_id(){
		return f_hypotest_id;
	}
	/** 设置：f检验编号 */
	public void setF_hypotest_id(Long f_hypotest_id){
		this.f_hypotest_id=f_hypotest_id;
	}
	/** 设置：f检验编号 */
	public void setF_hypotest_id(String f_hypotest_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(f_hypotest_id)){
			this.f_hypotest_id=new Long(f_hypotest_id);
		}
	}
	/** 取得：f检验字段1 */
	public String getF_column_first(){
		return f_column_first;
	}
	/** 设置：f检验字段1 */
	public void setF_column_first(String f_column_first){
		this.f_column_first=f_column_first;
	}
	/** 取得：f检验字段2 */
	public String getF_column_second(){
		return f_column_second;
	}
	/** 设置：f检验字段2 */
	public void setF_column_second(String f_column_second){
		this.f_column_second=f_column_second;
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
