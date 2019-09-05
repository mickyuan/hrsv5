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
 * 流数据管理数据源
 */
@Table(tableName = "sdm_data_source")
public class Sdm_data_source extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_data_source";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 流数据管理数据源 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("sdm_source_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long sdm_source_id; //数据源ID
	private String sdm_source_name; //数据源名称
	private String sdm_source_number; //数据源编号
	private String sdm_source_des; //数据源详细描述
	private String create_date; //创建日期
	private String create_time; //创建时间
	private Long user_id; //用户ID

	/** 取得：数据源ID */
	public Long getSdm_source_id(){
		return sdm_source_id;
	}
	/** 设置：数据源ID */
	public void setSdm_source_id(Long sdm_source_id){
		this.sdm_source_id=sdm_source_id;
	}
	/** 设置：数据源ID */
	public void setSdm_source_id(String sdm_source_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(sdm_source_id)){
			this.sdm_source_id=new Long(sdm_source_id);
		}
	}
	/** 取得：数据源名称 */
	public String getSdm_source_name(){
		return sdm_source_name;
	}
	/** 设置：数据源名称 */
	public void setSdm_source_name(String sdm_source_name){
		this.sdm_source_name=sdm_source_name;
	}
	/** 取得：数据源编号 */
	public String getSdm_source_number(){
		return sdm_source_number;
	}
	/** 设置：数据源编号 */
	public void setSdm_source_number(String sdm_source_number){
		this.sdm_source_number=sdm_source_number;
	}
	/** 取得：数据源详细描述 */
	public String getSdm_source_des(){
		return sdm_source_des;
	}
	/** 设置：数据源详细描述 */
	public void setSdm_source_des(String sdm_source_des){
		this.sdm_source_des=sdm_source_des;
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
	/** 取得：用户ID */
	public Long getUser_id(){
		return user_id;
	}
	/** 设置：用户ID */
	public void setUser_id(Long user_id){
		this.user_id=user_id;
	}
	/** 设置：用户ID */
	public void setUser_id(String user_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(user_id)){
			this.user_id=new Long(user_id);
		}
	}
}
