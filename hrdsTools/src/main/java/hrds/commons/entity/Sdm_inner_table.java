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
 * 流数据海云内部消费信息登记表
 */
@Table(tableName = "sdm_inner_table")
public class Sdm_inner_table extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_inner_table";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 流数据海云内部消费信息登记表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("table_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long table_id; //表id
	private String table_cn_name; //表中文名称
	private String table_en_name; //表英文名称
	private String create_date; //创建日期
	private String create_time; //创建时间
	private String hyren_consumedes; //海云内部消费目的地
	private String remark; //备注
	private Long user_id; //用户ID
	private String execute_state; //运行状态

	/** 取得：表id */
	public Long getTable_id(){
		return table_id;
	}
	/** 设置：表id */
	public void setTable_id(Long table_id){
		this.table_id=table_id;
	}
	/** 设置：表id */
	public void setTable_id(String table_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(table_id)){
			this.table_id=new Long(table_id);
		}
	}
	/** 取得：表中文名称 */
	public String getTable_cn_name(){
		return table_cn_name;
	}
	/** 设置：表中文名称 */
	public void setTable_cn_name(String table_cn_name){
		this.table_cn_name=table_cn_name;
	}
	/** 取得：表英文名称 */
	public String getTable_en_name(){
		return table_en_name;
	}
	/** 设置：表英文名称 */
	public void setTable_en_name(String table_en_name){
		this.table_en_name=table_en_name;
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
	/** 取得：海云内部消费目的地 */
	public String getHyren_consumedes(){
		return hyren_consumedes;
	}
	/** 设置：海云内部消费目的地 */
	public void setHyren_consumedes(String hyren_consumedes){
		this.hyren_consumedes=hyren_consumedes;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
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
	/** 取得：运行状态 */
	public String getExecute_state(){
		return execute_state;
	}
	/** 设置：运行状态 */
	public void setExecute_state(String execute_state){
		this.execute_state=execute_state;
	}
}
