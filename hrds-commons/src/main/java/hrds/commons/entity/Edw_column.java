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
 * 数据仓库字段信息
 */
@Table(tableName = "edw_column")
public class Edw_column extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "edw_column";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据仓库字段信息 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("tabname");
		__tmpPKS.add("colno");
		__tmpPKS.add("st_dt");
		__tmpPKS.add("st_time");
		__tmpPKS.add("table_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private String tabname; //表名称
	private Integer colno; //字段序号
	private String colname; //字段名称
	private String ccolname; //字段中文名
	private String coltype; //字段类型
	private String ifpk; //是否主键
	private String ifnull; //是否允许为空
	private String coltra; //转换类型
	private String colformat; //字段格式
	private String tratype; //转换后数据类型
	private String st_dt; //开始日期
	private String end_dt; //结束日期
	private String st_time; //开始时间
	private Long table_id; //表id
	private String remark; //备注
	private String is_sortcolumns; //是否为cb的sortcolumns列
	private Long column_sort; //字段排列顺序
	private String is_diccol; //是否为cb的字段编码列
	private String is_first_level_col; //是否是一级分区列
	private String is_second_level_col; //是否是二级分区列

	/** 取得：表名称 */
	public String getTabname(){
		return tabname;
	}
	/** 设置：表名称 */
	public void setTabname(String tabname){
		this.tabname=tabname;
	}
	/** 取得：字段序号 */
	public Integer getColno(){
		return colno;
	}
	/** 设置：字段序号 */
	public void setColno(Integer colno){
		this.colno=colno;
	}
	/** 设置：字段序号 */
	public void setColno(String colno){
		if(!fd.ng.core.utils.StringUtil.isEmpty(colno)){
			this.colno=new Integer(colno);
		}
	}
	/** 取得：字段名称 */
	public String getColname(){
		return colname;
	}
	/** 设置：字段名称 */
	public void setColname(String colname){
		this.colname=colname;
	}
	/** 取得：字段中文名 */
	public String getCcolname(){
		return ccolname;
	}
	/** 设置：字段中文名 */
	public void setCcolname(String ccolname){
		this.ccolname=ccolname;
	}
	/** 取得：字段类型 */
	public String getColtype(){
		return coltype;
	}
	/** 设置：字段类型 */
	public void setColtype(String coltype){
		this.coltype=coltype;
	}
	/** 取得：是否主键 */
	public String getIfpk(){
		return ifpk;
	}
	/** 设置：是否主键 */
	public void setIfpk(String ifpk){
		this.ifpk=ifpk;
	}
	/** 取得：是否允许为空 */
	public String getIfnull(){
		return ifnull;
	}
	/** 设置：是否允许为空 */
	public void setIfnull(String ifnull){
		this.ifnull=ifnull;
	}
	/** 取得：转换类型 */
	public String getColtra(){
		return coltra;
	}
	/** 设置：转换类型 */
	public void setColtra(String coltra){
		this.coltra=coltra;
	}
	/** 取得：字段格式 */
	public String getColformat(){
		return colformat;
	}
	/** 设置：字段格式 */
	public void setColformat(String colformat){
		this.colformat=colformat;
	}
	/** 取得：转换后数据类型 */
	public String getTratype(){
		return tratype;
	}
	/** 设置：转换后数据类型 */
	public void setTratype(String tratype){
		this.tratype=tratype;
	}
	/** 取得：开始日期 */
	public String getSt_dt(){
		return st_dt;
	}
	/** 设置：开始日期 */
	public void setSt_dt(String st_dt){
		this.st_dt=st_dt;
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
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：是否为cb的sortcolumns列 */
	public String getIs_sortcolumns(){
		return is_sortcolumns;
	}
	/** 设置：是否为cb的sortcolumns列 */
	public void setIs_sortcolumns(String is_sortcolumns){
		this.is_sortcolumns=is_sortcolumns;
	}
	/** 取得：字段排列顺序 */
	public Long getColumn_sort(){
		return column_sort;
	}
	/** 设置：字段排列顺序 */
	public void setColumn_sort(Long column_sort){
		this.column_sort=column_sort;
	}
	/** 设置：字段排列顺序 */
	public void setColumn_sort(String column_sort){
		if(!fd.ng.core.utils.StringUtil.isEmpty(column_sort)){
			this.column_sort=new Long(column_sort);
		}
	}
	/** 取得：是否为cb的字段编码列 */
	public String getIs_diccol(){
		return is_diccol;
	}
	/** 设置：是否为cb的字段编码列 */
	public void setIs_diccol(String is_diccol){
		this.is_diccol=is_diccol;
	}
	/** 取得：是否是一级分区列 */
	public String getIs_first_level_col(){
		return is_first_level_col;
	}
	/** 设置：是否是一级分区列 */
	public void setIs_first_level_col(String is_first_level_col){
		this.is_first_level_col=is_first_level_col;
	}
	/** 取得：是否是二级分区列 */
	public String getIs_second_level_col(){
		return is_second_level_col;
	}
	/** 设置：是否是二级分区列 */
	public void setIs_second_level_col(String is_second_level_col){
		this.is_second_level_col=is_second_level_col;
	}
}
