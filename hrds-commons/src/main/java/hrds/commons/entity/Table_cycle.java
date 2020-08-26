package hrds.commons.entity;
/**Auto Created by VBScript Do not modify!*/
import hrds.commons.entity.fdentity.ProjectTableEntity;
import fd.ng.db.entity.anno.Table;
import fd.ng.core.annotation.DocBean;
import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

/**
 * 数据库采集周期
 */
@Table(tableName = "table_cycle")
public class Table_cycle extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "table_cycle";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据库采集周期 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("tc_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="table_id",value="表名ID:",dataType = Long.class,required = true)
	private Long table_id;
	@DocBean(name ="tc_id",value="周期ID:",dataType = Long.class,required = true)
	private Long tc_id;
	@DocBean(name ="interval_time",value="频率间隔时间（秒）:",dataType = Long.class,required = true)
	private Long interval_time;
	@DocBean(name ="over_date",value="结束日期:",dataType = String.class,required = true)
	private String over_date;
	@DocBean(name ="tc_remark",value="备注:",dataType = String.class,required = false)
	private String tc_remark;

	/** 取得：表名ID */
	public Long getTable_id(){
		return table_id;
	}
	/** 设置：表名ID */
	public void setTable_id(Long table_id){
		this.table_id=table_id;
	}
	/** 设置：表名ID */
	public void setTable_id(String table_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(table_id)){
			this.table_id=new Long(table_id);
		}
	}
	/** 取得：周期ID */
	public Long getTc_id(){
		return tc_id;
	}
	/** 设置：周期ID */
	public void setTc_id(Long tc_id){
		this.tc_id=tc_id;
	}
	/** 设置：周期ID */
	public void setTc_id(String tc_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(tc_id)){
			this.tc_id=new Long(tc_id);
		}
	}
	/** 取得：频率间隔时间（秒） */
	public Long getInterval_time(){
		return interval_time;
	}
	/** 设置：频率间隔时间（秒） */
	public void setInterval_time(Long interval_time){
		this.interval_time=interval_time;
	}
	/** 设置：频率间隔时间（秒） */
	public void setInterval_time(String interval_time){
		if(!fd.ng.core.utils.StringUtil.isEmpty(interval_time)){
			this.interval_time=new Long(interval_time);
		}
	}
	/** 取得：结束日期 */
	public String getOver_date(){
		return over_date;
	}
	/** 设置：结束日期 */
	public void setOver_date(String over_date){
		this.over_date=over_date;
	}
	/** 取得：备注 */
	public String getTc_remark(){
		return tc_remark;
	}
	/** 设置：备注 */
	public void setTc_remark(String tc_remark){
		this.tc_remark=tc_remark;
	}
}
