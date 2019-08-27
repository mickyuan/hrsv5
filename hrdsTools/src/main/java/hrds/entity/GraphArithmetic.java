package hrds.entity;

import fd.ng.db.entity.TableEntity;
import fd.ng.db.entity.anno.Column;
import fd.ng.db.entity.anno.Table;
import hrds.exception.BusinessException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 实体类中所有属性都应定义为对象，不要使用int等主类型，方便对null值的操作
 */
@Table(tableName = "graph_arithmetic")
public class GraphArithmetic extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "graph_arithmetic";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("arithmetic_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal arithmetic_id;
	private String edge;
	private String vertex;
	private String arithmetic_name;
	private String arithmetic_describe;
	private String arithmetic_type;
	private String hivedb;
	private String arithmetic;
	private String remark;

	public BigDecimal getArithmetic_id() { return arithmetic_id; }
	public void setArithmetic_id(BigDecimal arithmetic_id) {
		if(arithmetic_id==null) throw new BusinessException("Entity : GraphArithmetic.arithmetic_id must not null!");
		this.arithmetic_id = arithmetic_id;
	}

	public String getEdge() { return edge; }
	public void setEdge(String edge) {
		if(edge==null) throw new BusinessException("Entity : GraphArithmetic.edge must not null!");
		this.edge = edge;
	}

	public String getVertex() { return vertex; }
	public void setVertex(String vertex) {
		if(vertex==null) addNullValueField("vertex");
		this.vertex = vertex;
	}

	public String getArithmetic_name() { return arithmetic_name; }
	public void setArithmetic_name(String arithmetic_name) {
		if(arithmetic_name==null) throw new BusinessException("Entity : GraphArithmetic.arithmetic_name must not null!");
		this.arithmetic_name = arithmetic_name;
	}

	public String getArithmetic_describe() { return arithmetic_describe; }
	public void setArithmetic_describe(String arithmetic_describe) {
		if(arithmetic_describe==null) addNullValueField("arithmetic_describe");
		this.arithmetic_describe = arithmetic_describe;
	}

	public String getArithmetic_type() { return arithmetic_type; }
	public void setArithmetic_type(String arithmetic_type) {
		if(arithmetic_type==null) throw new BusinessException("Entity : GraphArithmetic.arithmetic_type must not null!");
		this.arithmetic_type = arithmetic_type;
	}

	public String getHivedb() { return hivedb; }
	public void setHivedb(String hivedb) {
		if(hivedb==null) throw new BusinessException("Entity : GraphArithmetic.hivedb must not null!");
		this.hivedb = hivedb;
	}

	public String getArithmetic() { return arithmetic; }
	public void setArithmetic(String arithmetic) {
		if(arithmetic==null) throw new BusinessException("Entity : GraphArithmetic.arithmetic must not null!");
		this.arithmetic = arithmetic;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

}