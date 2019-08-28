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
 * 模型后置作业表信息
 */
@Table(tableName = "edw_hzzy_tableinfo")
public class Edw_hzzy_tableinfo extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "edw_hzzy_tableinfo";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 模型后置作业表信息 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("hzzy_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long hzzy_id; //后置作业表id
	private String hzzy_tbname; //后置作业表名称
	private String create_date; //表创建日期
	private String create_time; //表创建时间
	private String remark; //备注
	private String tabname; //表名称
	private Long table_id; //表id
	private String st_dt; //开始日期
	private String st_time; //开始时间

	/** 取得：后置作业表id */
	public Long getHzzy_id(){
		return hzzy_id;
	}
	/** 设置：后置作业表id */
	public void setHzzy_id(Long hzzy_id){
		this.hzzy_id=hzzy_id;
	}
	/** 设置：后置作业表id */
	public void setHzzy_id(String hzzy_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(hzzy_id)){
			this.hzzy_id=new Long(hzzy_id);
		}
	}
	/** 取得：后置作业表名称 */
	public String getHzzy_tbname(){
		return hzzy_tbname;
	}
	/** 设置：后置作业表名称 */
	public void setHzzy_tbname(String hzzy_tbname){
		this.hzzy_tbname=hzzy_tbname;
	}
	/** 取得：表创建日期 */
	public String getCreate_date(){
		return create_date;
	}
	/** 设置：表创建日期 */
	public void setCreate_date(String create_date){
		this.create_date=create_date;
	}
	/** 取得：表创建时间 */
	public String getCreate_time(){
		return create_time;
	}
	/** 设置：表创建时间 */
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
	/** 取得：表名称 */
	public String getTabname(){
		return tabname;
	}
	/** 设置：表名称 */
	public void setTabname(String tabname){
		this.tabname=tabname;
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
	/** 取得：开始日期 */
	public String getSt_dt(){
		return st_dt;
	}
	/** 设置：开始日期 */
	public void setSt_dt(String st_dt){
		this.st_dt=st_dt;
	}
	/** 取得：开始时间 */
	public String getSt_time(){
		return st_time;
	}
	/** 设置：开始时间 */
	public void setSt_time(String st_time){
		this.st_time=st_time;
	}
}
