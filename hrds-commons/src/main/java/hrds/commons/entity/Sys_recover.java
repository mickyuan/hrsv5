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
 * 备份恢复信息表
 */
@Table(tableName = "sys_recover")
public class Sys_recover extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sys_recover";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 备份恢复信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("re_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="re_id",value="恢复id:",dataType = Long.class,required = true)
	private Long re_id;
	@DocBean(name ="re_date",value="恢复日期:",dataType = String.class,required = true)
	private String re_date;
	@DocBean(name ="re_time",value="恢复时间:",dataType = String.class,required = true)
	private String re_time;
	@DocBean(name ="length",value="恢复时长:",dataType = String.class,required = true)
	private String length;
	@DocBean(name ="remark",value="备注:",dataType = String.class,required = false)
	private String remark;
	@DocBean(name ="dump_id",value="备份id:",dataType = Long.class,required = true)
	private Long dump_id;

	/** 取得：恢复id */
	public Long getRe_id(){
		return re_id;
	}
	/** 设置：恢复id */
	public void setRe_id(Long re_id){
		this.re_id=re_id;
	}
	/** 设置：恢复id */
	public void setRe_id(String re_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(re_id)){
			this.re_id=new Long(re_id);
		}
	}
	/** 取得：恢复日期 */
	public String getRe_date(){
		return re_date;
	}
	/** 设置：恢复日期 */
	public void setRe_date(String re_date){
		this.re_date=re_date;
	}
	/** 取得：恢复时间 */
	public String getRe_time(){
		return re_time;
	}
	/** 设置：恢复时间 */
	public void setRe_time(String re_time){
		this.re_time=re_time;
	}
	/** 取得：恢复时长 */
	public String getLength(){
		return length;
	}
	/** 设置：恢复时长 */
	public void setLength(String length){
		this.length=length;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：备份id */
	public Long getDump_id(){
		return dump_id;
	}
	/** 设置：备份id */
	public void setDump_id(Long dump_id){
		this.dump_id=dump_id;
	}
	/** 设置：备份id */
	public void setDump_id(String dump_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(dump_id)){
			this.dump_id=new Long(dump_id);
		}
	}
}
