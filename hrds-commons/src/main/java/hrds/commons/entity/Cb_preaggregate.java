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
 * carbondata预聚合信息表
 */
@Table(tableName = "cb_preaggregate")
public class Cb_preaggregate extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "cb_preaggregate";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** carbondata预聚合信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("agg_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="agg_id",value="预聚合id:",dataType = Long.class,required = true)
	private Long agg_id;
	@DocBean(name ="agg_sql",value="预聚合SQL:",dataType = String.class,required = true)
	private String agg_sql;
	@DocBean(name ="agg_time",value="时间:",dataType = String.class,required = true)
	private String agg_time;
	@DocBean(name ="agg_name",value="预聚合名称:",dataType = String.class,required = true)
	private String agg_name;
	@DocBean(name ="agg_status",value="预聚合是否成功(JobExecuteState):100-等待<DengDai> 101-运行<YunXing> 102-暂停<ZanTing> 103-中止<ZhongZhi> 104-完成<WanCheng> 105-失败<ShiBai> ",dataType = String.class,required = false)
	private String agg_status;
	@DocBean(name ="remark",value="备注:",dataType = String.class,required = false)
	private String remark;
	@DocBean(name ="agg_date",value="日期:",dataType = String.class,required = true)
	private String agg_date;
	@DocBean(name ="datatable_id",value="数据表id:",dataType = Long.class,required = true)
	private Long datatable_id;

	/** 取得：预聚合id */
	public Long getAgg_id(){
		return agg_id;
	}
	/** 设置：预聚合id */
	public void setAgg_id(Long agg_id){
		this.agg_id=agg_id;
	}
	/** 设置：预聚合id */
	public void setAgg_id(String agg_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(agg_id)){
			this.agg_id=new Long(agg_id);
		}
	}
	/** 取得：预聚合SQL */
	public String getAgg_sql(){
		return agg_sql;
	}
	/** 设置：预聚合SQL */
	public void setAgg_sql(String agg_sql){
		this.agg_sql=agg_sql;
	}
	/** 取得：时间 */
	public String getAgg_time(){
		return agg_time;
	}
	/** 设置：时间 */
	public void setAgg_time(String agg_time){
		this.agg_time=agg_time;
	}
	/** 取得：预聚合名称 */
	public String getAgg_name(){
		return agg_name;
	}
	/** 设置：预聚合名称 */
	public void setAgg_name(String agg_name){
		this.agg_name=agg_name;
	}
	/** 取得：预聚合是否成功 */
	public String getAgg_status(){
		return agg_status;
	}
	/** 设置：预聚合是否成功 */
	public void setAgg_status(String agg_status){
		this.agg_status=agg_status;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：日期 */
	public String getAgg_date(){
		return agg_date;
	}
	/** 设置：日期 */
	public void setAgg_date(String agg_date){
		this.agg_date=agg_date;
	}
	/** 取得：数据表id */
	public Long getDatatable_id(){
		return datatable_id;
	}
	/** 设置：数据表id */
	public void setDatatable_id(Long datatable_id){
		this.datatable_id=datatable_id;
	}
	/** 设置：数据表id */
	public void setDatatable_id(String datatable_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(datatable_id)){
			this.datatable_id=new Long(datatable_id);
		}
	}
}
