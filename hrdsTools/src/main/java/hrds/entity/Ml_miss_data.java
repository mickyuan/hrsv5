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
 * 数据预处理数据缺失处理表
 */
@Table(tableName = "ml_miss_data")
public class Ml_miss_data extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_miss_data";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据预处理数据缺失处理表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("missvalue_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long missvalue_id; //缺失值编号
	private String missdata_column; //数据缺失处理字段
	private String missvalue_proc; //缺失值处理方式
	private Long k_num; //k的个数
	private String remark; //备注
	private Long dtable_info_id; //数据表信息编号
	private String missproc_type; //缺失值处理类型
	private String newcolumn_name; //新字段名称
	private String create_date; //创建日期
	private String create_time; //创建时间

	/** 取得：缺失值编号 */
	public Long getMissvalue_id(){
		return missvalue_id;
	}
	/** 设置：缺失值编号 */
	public void setMissvalue_id(Long missvalue_id){
		this.missvalue_id=missvalue_id;
	}
	/** 设置：缺失值编号 */
	public void setMissvalue_id(String missvalue_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(missvalue_id)){
			this.missvalue_id=new Long(missvalue_id);
		}
	}
	/** 取得：数据缺失处理字段 */
	public String getMissdata_column(){
		return missdata_column;
	}
	/** 设置：数据缺失处理字段 */
	public void setMissdata_column(String missdata_column){
		this.missdata_column=missdata_column;
	}
	/** 取得：缺失值处理方式 */
	public String getMissvalue_proc(){
		return missvalue_proc;
	}
	/** 设置：缺失值处理方式 */
	public void setMissvalue_proc(String missvalue_proc){
		this.missvalue_proc=missvalue_proc;
	}
	/** 取得：k的个数 */
	public Long getK_num(){
		return k_num;
	}
	/** 设置：k的个数 */
	public void setK_num(Long k_num){
		this.k_num=k_num;
	}
	/** 设置：k的个数 */
	public void setK_num(String k_num){
		if(!fd.ng.core.utils.StringUtil.isEmpty(k_num)){
			this.k_num=new Long(k_num);
		}
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
	/** 取得：缺失值处理类型 */
	public String getMissproc_type(){
		return missproc_type;
	}
	/** 设置：缺失值处理类型 */
	public void setMissproc_type(String missproc_type){
		this.missproc_type=missproc_type;
	}
	/** 取得：新字段名称 */
	public String getNewcolumn_name(){
		return newcolumn_name;
	}
	/** 设置：新字段名称 */
	public void setNewcolumn_name(String newcolumn_name){
		this.newcolumn_name=newcolumn_name;
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
