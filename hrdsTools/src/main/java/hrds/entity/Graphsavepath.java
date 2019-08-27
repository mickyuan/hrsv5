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
@Table(tableName = "graphsavepath")
public class Graphsavepath extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "graphsavepath";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("graph_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String job_storage_path;
	private BigDecimal arithmetic_id;
	private String remark;
	private BigDecimal graph_id;
	private String tablename;
	private String table_space;

	public String getJob_storage_path() { return job_storage_path; }
	public void setJob_storage_path(String job_storage_path) {
		if(job_storage_path==null) throw new BusinessException("Entity : Graphsavepath.job_storage_path must not null!");
		this.job_storage_path = job_storage_path;
	}

	public BigDecimal getArithmetic_id() { return arithmetic_id; }
	public void setArithmetic_id(BigDecimal arithmetic_id) {
		if(arithmetic_id==null) addNullValueField("arithmetic_id");
		this.arithmetic_id = arithmetic_id;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public BigDecimal getGraph_id() { return graph_id; }
	public void setGraph_id(BigDecimal graph_id) {
		if(graph_id==null) throw new BusinessException("Entity : Graphsavepath.graph_id must not null!");
		this.graph_id = graph_id;
	}

	public String getTablename() { return tablename; }
	public void setTablename(String tablename) {
		if(tablename==null) throw new BusinessException("Entity : Graphsavepath.tablename must not null!");
		this.tablename = tablename;
	}

	public String getTable_space() { return table_space; }
	public void setTable_space(String table_space) {
		if(table_space==null) throw new BusinessException("Entity : Graphsavepath.table_space must not null!");
		this.table_space = table_space;
	}

}