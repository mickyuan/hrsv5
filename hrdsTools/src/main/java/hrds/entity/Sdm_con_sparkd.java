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
 * 数据消费至SparkD
 */
@Table(tableName = "sdm_con_sparkd")
public class Sdm_con_sparkd extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_con_sparkd";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据消费至SparkD */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("sparkd_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long sparkd_id; //sparkd_id
	private String table_en_name; //SparkD的表名
	private String sparkd_bus_type; //SparkD业务处理类类型
	private String sparkd_bus_class; //SparkD业务处理类
	private String table_space; //SparkD表空间
	private Long sdm_des_id; //配置id

	/** 取得：sparkd_id */
	public Long getSparkd_id(){
		return sparkd_id;
	}
	/** 设置：sparkd_id */
	public void setSparkd_id(Long sparkd_id){
		this.sparkd_id=sparkd_id;
	}
	/** 设置：sparkd_id */
	public void setSparkd_id(String sparkd_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(sparkd_id)){
			this.sparkd_id=new Long(sparkd_id);
		}
	}
	/** 取得：SparkD的表名 */
	public String getTable_en_name(){
		return table_en_name;
	}
	/** 设置：SparkD的表名 */
	public void setTable_en_name(String table_en_name){
		this.table_en_name=table_en_name;
	}
	/** 取得：SparkD业务处理类类型 */
	public String getSparkd_bus_type(){
		return sparkd_bus_type;
	}
	/** 设置：SparkD业务处理类类型 */
	public void setSparkd_bus_type(String sparkd_bus_type){
		this.sparkd_bus_type=sparkd_bus_type;
	}
	/** 取得：SparkD业务处理类 */
	public String getSparkd_bus_class(){
		return sparkd_bus_class;
	}
	/** 设置：SparkD业务处理类 */
	public void setSparkd_bus_class(String sparkd_bus_class){
		this.sparkd_bus_class=sparkd_bus_class;
	}
	/** 取得：SparkD表空间 */
	public String getTable_space(){
		return table_space;
	}
	/** 设置：SparkD表空间 */
	public void setTable_space(String table_space){
		this.table_space=table_space;
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
}
