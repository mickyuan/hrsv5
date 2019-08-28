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
 * 机器学习分层抽取范围对应表
 */
@Table(tableName = "ml_hext_scope")
public class Ml_hext_scope extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_hext_scope";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 机器学习分层抽取范围对应表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("hextscope_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long hextscope_id; //分层抽取范围编号
	private String extr_value; //抽取值
	private Long hierextr_id; //分层抽取编号

	/** 取得：分层抽取范围编号 */
	public Long getHextscope_id(){
		return hextscope_id;
	}
	/** 设置：分层抽取范围编号 */
	public void setHextscope_id(Long hextscope_id){
		this.hextscope_id=hextscope_id;
	}
	/** 设置：分层抽取范围编号 */
	public void setHextscope_id(String hextscope_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(hextscope_id)){
			this.hextscope_id=new Long(hextscope_id);
		}
	}
	/** 取得：抽取值 */
	public String getExtr_value(){
		return extr_value;
	}
	/** 设置：抽取值 */
	public void setExtr_value(String extr_value){
		this.extr_value=extr_value;
	}
	/** 取得：分层抽取编号 */
	public Long getHierextr_id(){
		return hierextr_id;
	}
	/** 设置：分层抽取编号 */
	public void setHierextr_id(Long hierextr_id){
		this.hierextr_id=hierextr_id;
	}
	/** 设置：分层抽取编号 */
	public void setHierextr_id(String hierextr_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(hierextr_id)){
			this.hierextr_id=new Long(hierextr_id);
		}
	}
}
