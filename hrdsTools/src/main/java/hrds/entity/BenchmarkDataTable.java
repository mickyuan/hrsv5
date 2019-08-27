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
@Table(tableName = "benchmark_data_table")
public class BenchmarkDataTable extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "benchmark_data_table";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("benchmark_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String source_table_name;
	private String source_ch_column;
	private BigDecimal benchmark_id;
	private String category;
	private String trans_column_name;
	private String source_column_name;

	public String getSource_table_name() { return source_table_name; }
	public void setSource_table_name(String source_table_name) {
		if(source_table_name==null) throw new BusinessException("Entity : BenchmarkDataTable.source_table_name must not null!");
		this.source_table_name = source_table_name;
	}

	public String getSource_ch_column() { return source_ch_column; }
	public void setSource_ch_column(String source_ch_column) {
		if(source_ch_column==null) addNullValueField("source_ch_column");
		this.source_ch_column = source_ch_column;
	}

	public BigDecimal getBenchmark_id() { return benchmark_id; }
	public void setBenchmark_id(BigDecimal benchmark_id) {
		if(benchmark_id==null) throw new BusinessException("Entity : BenchmarkDataTable.benchmark_id must not null!");
		this.benchmark_id = benchmark_id;
	}

	public String getCategory() { return category; }
	public void setCategory(String category) {
		if(category==null) throw new BusinessException("Entity : BenchmarkDataTable.category must not null!");
		this.category = category;
	}

	public String getTrans_column_name() { return trans_column_name; }
	public void setTrans_column_name(String trans_column_name) {
		if(trans_column_name==null) addNullValueField("trans_column_name");
		this.trans_column_name = trans_column_name;
	}

	public String getSource_column_name() { return source_column_name; }
	public void setSource_column_name(String source_column_name) {
		if(source_column_name==null) throw new BusinessException("Entity : BenchmarkDataTable.source_column_name must not null!");
		this.source_column_name = source_column_name;
	}

}