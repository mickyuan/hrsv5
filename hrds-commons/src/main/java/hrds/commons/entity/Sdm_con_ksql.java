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
 * ksql字段配置
 */
@Table(tableName = "sdm_con_ksql")
public class Sdm_con_ksql extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_con_ksql";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** ksql字段配置 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("sdm_col_ksql");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long sdm_col_ksql; //ksql字段编号
	private String column_name; //字段英文名称
	private String column_type; //字段类型
	private String column_cname; //字段中文名称
	private Long sdm_ksql_id; //映射表主键
	private String is_key; //是否为key
	private String is_timestamp; //是否为时间戳
	private String sdm_remark; //备注
	private String column_hy; //字段含义

	/** 取得：ksql字段编号 */
	public Long getSdm_col_ksql(){
		return sdm_col_ksql;
	}
	/** 设置：ksql字段编号 */
	public void setSdm_col_ksql(Long sdm_col_ksql){
		this.sdm_col_ksql=sdm_col_ksql;
	}
	/** 设置：ksql字段编号 */
	public void setSdm_col_ksql(String sdm_col_ksql){
		if(!fd.ng.core.utils.StringUtil.isEmpty(sdm_col_ksql)){
			this.sdm_col_ksql=new Long(sdm_col_ksql);
		}
	}
	/** 取得：字段英文名称 */
	public String getColumn_name(){
		return column_name;
	}
	/** 设置：字段英文名称 */
	public void setColumn_name(String column_name){
		this.column_name=column_name;
	}
	/** 取得：字段类型 */
	public String getColumn_type(){
		return column_type;
	}
	/** 设置：字段类型 */
	public void setColumn_type(String column_type){
		this.column_type=column_type;
	}
	/** 取得：字段中文名称 */
	public String getColumn_cname(){
		return column_cname;
	}
	/** 设置：字段中文名称 */
	public void setColumn_cname(String column_cname){
		this.column_cname=column_cname;
	}
	/** 取得：映射表主键 */
	public Long getSdm_ksql_id(){
		return sdm_ksql_id;
	}
	/** 设置：映射表主键 */
	public void setSdm_ksql_id(Long sdm_ksql_id){
		this.sdm_ksql_id=sdm_ksql_id;
	}
	/** 设置：映射表主键 */
	public void setSdm_ksql_id(String sdm_ksql_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(sdm_ksql_id)){
			this.sdm_ksql_id=new Long(sdm_ksql_id);
		}
	}
	/** 取得：是否为key */
	public String getIs_key(){
		return is_key;
	}
	/** 设置：是否为key */
	public void setIs_key(String is_key){
		this.is_key=is_key;
	}
	/** 取得：是否为时间戳 */
	public String getIs_timestamp(){
		return is_timestamp;
	}
	/** 设置：是否为时间戳 */
	public void setIs_timestamp(String is_timestamp){
		this.is_timestamp=is_timestamp;
	}
	/** 取得：备注 */
	public String getSdm_remark(){
		return sdm_remark;
	}
	/** 设置：备注 */
	public void setSdm_remark(String sdm_remark){
		this.sdm_remark=sdm_remark;
	}
	/** 取得：字段含义 */
	public String getColumn_hy(){
		return column_hy;
	}
	/** 设置：字段含义 */
	public void setColumn_hy(String column_hy){
		this.column_hy=column_hy;
	}
}
