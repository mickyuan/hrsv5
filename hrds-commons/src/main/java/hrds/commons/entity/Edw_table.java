package hrds.commons.entity;
/**Auto Created by VBScript Do not modify!*/

import fd.ng.core.utils.StringUtil;
import hrds.commons.entity.fdentity.ProjectTableEntity;

import java.util.HashSet;
import java.util.Set;

/**
 * 数据仓库表清单
 */
public class Edw_table extends ProjectTableEntity
{
	public static final String TableName = "edw_table";
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
		if(!StringUtil.isEmpty(table_id))
			this.table_id=new Long(table_id);
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
		if(!StringUtil.isEmpty(category_id))
			this.category_id=new Long(category_id);
	}
	private Set primaryKeys = new HashSet();
	public boolean isPrimaryKey(String name){ return primaryKeys.contains(name); } 
	public String getPrimaryKey(){return primaryKeys.iterator().next().toString();} 
	/** 数据仓库表清单 */
	public Edw_table(){
		primaryKeys.add("tabname");
		primaryKeys.add("st_dt");
		primaryKeys.add("st_time");
		primaryKeys.add("table_id");
	}
}
