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
 * 流数据管理消费至rest服务
 */
@Table(tableName = "sdm_con_rest")
public class Sdm_con_rest extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_con_rest";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 流数据管理消费至rest服务 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("rest_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long rest_id; //restId
	private String rest_port; //rest服务器端口
	private String rest_ip; //restIP
	private String rest_parameter; //标志rest服务
	private String remark; //备注
	private Long sdm_des_id; //配置id
	private String rest_bus_class; //rest业务处理类
	private String rest_bus_type; //rest业务处理类类型

	/** 取得：restId */
	public Long getRest_id(){
		return rest_id;
	}
	/** 设置：restId */
	public void setRest_id(Long rest_id){
		this.rest_id=rest_id;
	}
	/** 设置：restId */
	public void setRest_id(String rest_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(rest_id)){
			this.rest_id=new Long(rest_id);
		}
	}
	/** 取得：rest服务器端口 */
	public String getRest_port(){
		return rest_port;
	}
	/** 设置：rest服务器端口 */
	public void setRest_port(String rest_port){
		this.rest_port=rest_port;
	}
	/** 取得：restIP */
	public String getRest_ip(){
		return rest_ip;
	}
	/** 设置：restIP */
	public void setRest_ip(String rest_ip){
		this.rest_ip=rest_ip;
	}
	/** 取得：标志rest服务 */
	public String getRest_parameter(){
		return rest_parameter;
	}
	/** 设置：标志rest服务 */
	public void setRest_parameter(String rest_parameter){
		this.rest_parameter=rest_parameter;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：配置id */
	public Long getSdm_des_id(){
		return sdm_des_id;
	}
	/** 设置：配置id */
	public void setSdm_des_id(Long sdm_des_id){
		this.sdm_des_id=sdm_des_id;
	}
	/** 设置：配置id */
	public void setSdm_des_id(String sdm_des_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(sdm_des_id)){
			this.sdm_des_id=new Long(sdm_des_id);
		}
	}
	/** 取得：rest业务处理类 */
	public String getRest_bus_class(){
		return rest_bus_class;
	}
	/** 设置：rest业务处理类 */
	public void setRest_bus_class(String rest_bus_class){
		this.rest_bus_class=rest_bus_class;
	}
	/** 取得：rest业务处理类类型 */
	public String getRest_bus_type(){
		return rest_bus_type;
	}
	/** 设置：rest业务处理类类型 */
	public void setRest_bus_type(String rest_bus_type){
		this.rest_bus_type=rest_bus_type;
	}
}
