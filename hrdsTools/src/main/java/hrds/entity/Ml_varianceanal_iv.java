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
 * 机器学习方差分析自变量表
 */
@Table(tableName = "ml_varianceanal_iv")
public class Ml_varianceanal_iv extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_varianceanal_iv";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 机器学习方差分析自变量表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("varianal_iv_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long varianal_iv_id; //方差分析自变量编号
	private String iv_column; //自变量字段
	private String create_date; //创建日期
	private String create_time; //创建时间
	private String remark; //备注
	private Long dtable_info_id; //数据表信息编号

	/** 取得：方差分析自变量编号 */
	public Long getVarianal_iv_id(){
		return varianal_iv_id;
	}
	/** 设置：方差分析自变量编号 */
	public void setVarianal_iv_id(Long varianal_iv_id){
		this.varianal_iv_id=varianal_iv_id;
	}
	/** 设置：方差分析自变量编号 */
	public void setVarianal_iv_id(String varianal_iv_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(varianal_iv_id)){
			this.varianal_iv_id=new Long(varianal_iv_id);
		}
	}
	/** 取得：自变量字段 */
	public String getIv_column(){
		return iv_column;
	}
	/** 设置：自变量字段 */
	public void setIv_column(String iv_column){
		this.iv_column=iv_column;
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
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：数据表信息编号 */
	public Long getDtable_info_id(){
		return dtable_info_id;
	}
	/** 设置：数据表信息编号 */
	public void setDtable_info_id(Long dtable_info_id){
		this.dtable_info_id=dtable_info_id;
	}
	/** 设置：数据表信息编号 */
	public void setDtable_info_id(String dtable_info_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(dtable_info_id)){
			this.dtable_info_id=new Long(dtable_info_id);
		}
	}
}
