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
 * 数据预处理数据转换表
 */
@Table(tableName = "ml_data_transfer")
public class Ml_data_transfer extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_data_transfer";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据预处理数据转换表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("datatran_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long datatran_id; //数据转换编号
	private String datatran_column; //数据转换字段
	private Long dtable_info_id; //数据表信息编号
	private String newcolumn_name; //新字段名称
	private String create_date; //创建日期
	private String create_time; //创建时间
	private String remark; //备注
	private Long funmap_id; //函数对应编码

	/** 取得：数据转换编号 */
	public Long getDatatran_id(){
		return datatran_id;
	}
	/** 设置：数据转换编号 */
	public void setDatatran_id(Long datatran_id){
		this.datatran_id=datatran_id;
	}
	/** 设置：数据转换编号 */
	public void setDatatran_id(String datatran_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(datatran_id)){
			this.datatran_id=new Long(datatran_id);
		}
	}
	/** 取得：数据转换字段 */
	public String getDatatran_column(){
		return datatran_column;
	}
	/** 设置：数据转换字段 */
	public void setDatatran_column(String datatran_column){
		this.datatran_column=datatran_column;
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
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：函数对应编码 */
	public Long getFunmap_id(){
		return funmap_id;
	}
	/** 设置：函数对应编码 */
	public void setFunmap_id(Long funmap_id){
		this.funmap_id=funmap_id;
	}
	/** 设置：函数对应编码 */
	public void setFunmap_id(String funmap_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(funmap_id)){
			this.funmap_id=new Long(funmap_id);
		}
	}
}
