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
 * 流数据管理接收参数表
 */
@Table(tableName = "sdm_rec_param")
public class Sdm_rec_param extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_rec_param";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 流数据管理接收参数表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("rec_param_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long rec_param_id; //rec_param_id
	private String sdm_param_key; //接收端参数key值
	private String sdm_param_value; //接收端参数value值
	private Long sdm_receive_id; //流数据管理

	/** 取得：rec_param_id */
	public Long getRec_param_id(){
		return rec_param_id;
	}
	/** 设置：rec_param_id */
	public void setRec_param_id(Long rec_param_id){
		this.rec_param_id=rec_param_id;
	}
	/** 设置：rec_param_id */
	public void setRec_param_id(String rec_param_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(rec_param_id)){
			this.rec_param_id=new Long(rec_param_id);
		}
	}
	/** 取得：接收端参数key值 */
	public String getSdm_param_key(){
		return sdm_param_key;
	}
	/** 设置：接收端参数key值 */
	public void setSdm_param_key(String sdm_param_key){
		this.sdm_param_key=sdm_param_key;
	}
	/** 取得：接收端参数value值 */
	public String getSdm_param_value(){
		return sdm_param_value;
	}
	/** 设置：接收端参数value值 */
	public void setSdm_param_value(String sdm_param_value){
		this.sdm_param_value=sdm_param_value;
	}
	/** 取得：流数据管理 */
	public Long getSdm_receive_id(){
		return sdm_receive_id;
	}
	/** 设置：流数据管理 */
	public void setSdm_receive_id(Long sdm_receive_id){
		this.sdm_receive_id=sdm_receive_id;
	}
	/** 设置：流数据管理 */
	public void setSdm_receive_id(String sdm_receive_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(sdm_receive_id)){
			this.sdm_receive_id=new Long(sdm_receive_id);
		}
	}
}
