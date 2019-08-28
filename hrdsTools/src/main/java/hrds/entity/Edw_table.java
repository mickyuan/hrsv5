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
 * 数据仓库表清单
 */
@Table(tableName = "edw_table")
public class Edw_table extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "edw_table";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据仓库表清单 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("tabname");
		__tmpPKS.add("st_dt");
		__tmpPKS.add("st_time");
		__tmpPKS.add("table_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private String tabname; //表名称
	private String st_dt; //开始日期
	private String ctname; //表中文名
	private String fromsys; //系统来源
	private String remark; //备注
	private String end_dt; //结束日期
	private String st_time; //开始时间
	private Long table_id; //表id
	private String data_in; //是否已加载数据
	private Long category_id; //模型分类id

	/** 取得：表名称 */
	public String getTabname(){
		return tabname;
	}
	/** 设置：表名称 */
	public void setTabname(String tabname){
		this.tabname=tabname;
	}
	/** 取得：开始日期 */
	public String getSt_dt(){
		return st_dt;
	}
	/** 设置：开始日期 */
	public void setSt_dt(String st_dt){
		this.st_dt=st_dt;
	}
	/** 取得：表中文名 */
	public String getCtname(){
		return ctname;
	}
	/** 设置：表中文名 */
	public void setCtname(String ctname){
		this.ctname=ctname;
	}
	/** 取得：系统来源 */
	public String getFromsys(){
		return fromsys;
	}
	/** 设置：系统来源 */
	public void setFromsys(String fromsys){
		this.fromsys=fromsys;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：结束日期 */
	public String getEnd_dt(){
		return end_dt;
	}
	/** 设置：结束日期 */
	public void setEnd_dt(String end_dt){
		this.end_dt=end_dt;
	}
	/** 取得：开始时间 */
	public String getSt_time(){
		return st_time;
	}
	/** 设置：开始时间 */
	public void setSt_time(String st_time){
		this.st_time=st_time;
	}
	/** 取得：表id */
	public Long getTable_id(){
		return table_id;
	}
	/** 设置：表id */
	public void setTable_id(Long table_id){
		this.table_id=table_id;
	}
	/** 设置：表id */
	public void setTable_id(String table_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(table_id)){
			this.table_id=new Long(table_id);
		}
	}
	/** 取得：是否已加载数据 */
	public String getData_in(){
		return data_in;
	}
	/** 设置：是否已加载数据 */
	public void setData_in(String data_in){
		this.data_in=data_in;
	}
	/** 取得：模型分类id */
	public Long getCategory_id(){
		return category_id;
	}
	/** 设置：模型分类id */
	public void setCategory_id(Long category_id){
		this.category_id=category_id;
	}
	/** 设置：模型分类id */
	public void setCategory_id(String category_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(category_id)){
			this.category_id=new Long(category_id);
		}
	}
}
