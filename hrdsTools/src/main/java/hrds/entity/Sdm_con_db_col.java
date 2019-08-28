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
 * 流数据管理消费字段表
 */
@Table(tableName = "sdm_con_db_col")
public class Sdm_con_db_col extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_con_db_col";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 流数据管理消费字段表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("sdm_col_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long sdm_col_id; //sdm_col_id
	private String sdm_col_name_en; //英文字段名
	private String sdm_col_name_cn; //中文字段名
	private String sdm_describe; //含义
	private String sdm_var_type; //变量类型
	private Long sdm_receive_id; //流数据管理receive_id
	private String remark; //备注
	private Long consumer_id; //数据库设置id
	private String is_rowkey; //是否是rowkey
	private Long rowkey_seq; //rowkey序号
	private Long num; //序号
	private String is_send; //是否发送

	/** 取得：sdm_col_id */
	public Long getSdm_col_id(){
		return sdm_col_id;
	}
	/** 设置：sdm_col_id */
	public void setSdm_col_id(Long sdm_col_id){
		this.sdm_col_id=sdm_col_id;
	}
	/** 设置：sdm_col_id */
	public void setSdm_col_id(String sdm_col_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(sdm_col_id)){
			this.sdm_col_id=new Long(sdm_col_id);
		}
	}
	/** 取得：英文字段名 */
	public String getSdm_col_name_en(){
		return sdm_col_name_en;
	}
	/** 设置：英文字段名 */
	public void setSdm_col_name_en(String sdm_col_name_en){
		this.sdm_col_name_en=sdm_col_name_en;
	}
	/** 取得：中文字段名 */
	public String getSdm_col_name_cn(){
		return sdm_col_name_cn;
	}
	/** 设置：中文字段名 */
	public void setSdm_col_name_cn(String sdm_col_name_cn){
		this.sdm_col_name_cn=sdm_col_name_cn;
	}
	/** 取得：含义 */
	public String getSdm_describe(){
		return sdm_describe;
	}
	/** 设置：含义 */
	public void setSdm_describe(String sdm_describe){
		this.sdm_describe=sdm_describe;
	}
	/** 取得：变量类型 */
	public String getSdm_var_type(){
		return sdm_var_type;
	}
	/** 设置：变量类型 */
	public void setSdm_var_type(String sdm_var_type){
		this.sdm_var_type=sdm_var_type;
	}
	/** 取得：流数据管理receive_id */
	public Long getSdm_receive_id(){
		return sdm_receive_id;
	}
	/** 设置：流数据管理receive_id */
	public void setSdm_receive_id(Long sdm_receive_id){
		this.sdm_receive_id=sdm_receive_id;
	}
	/** 设置：流数据管理receive_id */
	public void setSdm_receive_id(String sdm_receive_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(sdm_receive_id)){
			this.sdm_receive_id=new Long(sdm_receive_id);
		}
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：数据库设置id */
	public Long getConsumer_id(){
		return consumer_id;
	}
	/** 设置：数据库设置id */
	public void setConsumer_id(Long consumer_id){
		this.consumer_id=consumer_id;
	}
	/** 设置：数据库设置id */
	public void setConsumer_id(String consumer_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(consumer_id)){
			this.consumer_id=new Long(consumer_id);
		}
	}
	/** 取得：是否是rowkey */
	public String getIs_rowkey(){
		return is_rowkey;
	}
	/** 设置：是否是rowkey */
	public void setIs_rowkey(String is_rowkey){
		this.is_rowkey=is_rowkey;
	}
	/** 取得：rowkey序号 */
	public Long getRowkey_seq(){
		return rowkey_seq;
	}
	/** 设置：rowkey序号 */
	public void setRowkey_seq(Long rowkey_seq){
		this.rowkey_seq=rowkey_seq;
	}
	/** 设置：rowkey序号 */
	public void setRowkey_seq(String rowkey_seq){
		if(!fd.ng.core.utils.StringUtil.isEmpty(rowkey_seq)){
			this.rowkey_seq=new Long(rowkey_seq);
		}
	}
	/** 取得：序号 */
	public Long getNum(){
		return num;
	}
	/** 设置：序号 */
	public void setNum(Long num){
		this.num=num;
	}
	/** 设置：序号 */
	public void setNum(String num){
		if(!fd.ng.core.utils.StringUtil.isEmpty(num)){
			this.num=new Long(num);
		}
	}
	/** 取得：是否发送 */
	public String getIs_send(){
		return is_send;
	}
	/** 设置：是否发送 */
	public void setIs_send(String is_send){
		this.is_send=is_send;
	}
}
