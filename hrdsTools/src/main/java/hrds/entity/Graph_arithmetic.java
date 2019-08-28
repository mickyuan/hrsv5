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
 * 图算法作业信息
 */
@Table(tableName = "graph_arithmetic")
public class Graph_arithmetic extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "graph_arithmetic";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 图算法作业信息 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("arithmetic_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long arithmetic_id; //图算法作业ID
	private String arithmetic_name; //算法名称
	private String vertex; //点集数据表名称
	private String edge; //边集数据表名称
	private String hivedb; //Hive数据库名称
	private String arithmetic_type; //算法类型
	private String arithmetic_describe; //算法作业描述
	private String remark; //备注
	private String arithmetic; //算法参数

	/** 取得：图算法作业ID */
	public Long getArithmetic_id(){
		return arithmetic_id;
	}
	/** 设置：图算法作业ID */
	public void setArithmetic_id(Long arithmetic_id){
		this.arithmetic_id=arithmetic_id;
	}
	/** 设置：图算法作业ID */
	public void setArithmetic_id(String arithmetic_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(arithmetic_id)){
			this.arithmetic_id=new Long(arithmetic_id);
		}
	}
	/** 取得：算法名称 */
	public String getArithmetic_name(){
		return arithmetic_name;
	}
	/** 设置：算法名称 */
	public void setArithmetic_name(String arithmetic_name){
		this.arithmetic_name=arithmetic_name;
	}
	/** 取得：点集数据表名称 */
	public String getVertex(){
		return vertex;
	}
	/** 设置：点集数据表名称 */
	public void setVertex(String vertex){
		this.vertex=vertex;
	}
	/** 取得：边集数据表名称 */
	public String getEdge(){
		return edge;
	}
	/** 设置：边集数据表名称 */
	public void setEdge(String edge){
		this.edge=edge;
	}
	/** 取得：Hive数据库名称 */
	public String getHivedb(){
		return hivedb;
	}
	/** 设置：Hive数据库名称 */
	public void setHivedb(String hivedb){
		this.hivedb=hivedb;
	}
	/** 取得：算法类型 */
	public String getArithmetic_type(){
		return arithmetic_type;
	}
	/** 设置：算法类型 */
	public void setArithmetic_type(String arithmetic_type){
		this.arithmetic_type=arithmetic_type;
	}
	/** 取得：算法作业描述 */
	public String getArithmetic_describe(){
		return arithmetic_describe;
	}
	/** 设置：算法作业描述 */
	public void setArithmetic_describe(String arithmetic_describe){
		this.arithmetic_describe=arithmetic_describe;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：算法参数 */
	public String getArithmetic(){
		return arithmetic;
	}
	/** 设置：算法参数 */
	public void setArithmetic(String arithmetic){
		this.arithmetic=arithmetic;
	}
}
