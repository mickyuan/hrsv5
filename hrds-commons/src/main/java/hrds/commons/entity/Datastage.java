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
 * 数据采集阶段表
 */
@Table(tableName = "datastage")
public class Datastage extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "datastage";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据采集阶段表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("jobkey");
		__tmpPKS.add("tablename");
		__tmpPKS.add("stage");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long jobkey; //数据库设置id
	private String tablename; //表名字
	private String stage; //采集阶段
	private String state; //所处状态
	private String previousstage; //上一阶段
	private String nextstage; //下一阶段
	private String remark; //备注

	/** 取得：数据库设置id */
	public Long getJobkey(){
		return jobkey;
	}
	/** 设置：数据库设置id */
	public void setJobkey(Long jobkey){
		this.jobkey=jobkey;
	}
	/** 设置：数据库设置id */
	public void setJobkey(String jobkey){
		if(!fd.ng.core.utils.StringUtil.isEmpty(jobkey)){
			this.jobkey=new Long(jobkey);
		}
	}
	/** 取得：表名字 */
	public String getTablename(){
		return tablename;
	}
	/** 设置：表名字 */
	public void setTablename(String tablename){
		this.tablename=tablename;
	}
	/** 取得：采集阶段 */
	public String getStage(){
		return stage;
	}
	/** 设置：采集阶段 */
	public void setStage(String stage){
		this.stage=stage;
	}
	/** 取得：所处状态 */
	public String getState(){
		return state;
	}
	/** 设置：所处状态 */
	public void setState(String state){
		this.state=state;
	}
	/** 取得：上一阶段 */
	public String getPreviousstage(){
		return previousstage;
	}
	/** 设置：上一阶段 */
	public void setPreviousstage(String previousstage){
		this.previousstage=previousstage;
	}
	/** 取得：下一阶段 */
	public String getNextstage(){
		return nextstage;
	}
	/** 设置：下一阶段 */
	public void setNextstage(String nextstage){
		this.nextstage=nextstage;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
}
