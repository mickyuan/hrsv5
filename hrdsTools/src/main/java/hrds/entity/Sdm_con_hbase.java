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
 * 数据管理消费至Hbase
 */
@Table(tableName = "sdm_con_hbase")
public class Sdm_con_hbase extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_con_hbase";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据管理消费至Hbase */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("hbase_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long hbase_id; //hbaseId
	private String hbase_name; //hbase表名
	private String hbase_family; //列簇
	private String remark; //备注
	private Long sdm_des_id; //配置id
	private String pre_partition; //hbase预分区
	private String rowkey_separator; //rowkey分隔符
	private String hbase_bus_class; //hbase业务处理类
	private String hbase_bus_type; //hbase业务处理类类型

	/** 取得：hbaseId */
	public Long getHbase_id(){
		return hbase_id;
	}
	/** 设置：hbaseId */
	public void setHbase_id(Long hbase_id){
		this.hbase_id=hbase_id;
	}
	/** 设置：hbaseId */
	public void setHbase_id(String hbase_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(hbase_id)){
			this.hbase_id=new Long(hbase_id);
		}
	}
	/** 取得：hbase表名 */
	public String getHbase_name(){
		return hbase_name;
	}
	/** 设置：hbase表名 */
	public void setHbase_name(String hbase_name){
		this.hbase_name=hbase_name;
	}
	/** 取得：列簇 */
	public String getHbase_family(){
		return hbase_family;
	}
	/** 设置：列簇 */
	public void setHbase_family(String hbase_family){
		this.hbase_family=hbase_family;
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
	/** 取得：hbase预分区 */
	public String getPre_partition(){
		return pre_partition;
	}
	/** 设置：hbase预分区 */
	public void setPre_partition(String pre_partition){
		this.pre_partition=pre_partition;
	}
	/** 取得：rowkey分隔符 */
	public String getRowkey_separator(){
		return rowkey_separator;
	}
	/** 设置：rowkey分隔符 */
	public void setRowkey_separator(String rowkey_separator){
		this.rowkey_separator=rowkey_separator;
	}
	/** 取得：hbase业务处理类 */
	public String getHbase_bus_class(){
		return hbase_bus_class;
	}
	/** 设置：hbase业务处理类 */
	public void setHbase_bus_class(String hbase_bus_class){
		this.hbase_bus_class=hbase_bus_class;
	}
	/** 取得：hbase业务处理类类型 */
	public String getHbase_bus_type(){
		return hbase_bus_type;
	}
	/** 设置：hbase业务处理类类型 */
	public void setHbase_bus_type(String hbase_bus_type){
		this.hbase_bus_type=hbase_bus_type;
	}
}
