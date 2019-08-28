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
 * 对标数据表
 */
@Table(tableName = "benchmark_data_table")
public class Benchmark_data_table extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "benchmark_data_table";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 对标数据表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("benchmark_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long benchmark_id; //对标数据表id
	private String source_table_name; //源表名
	private String source_column_name; //源字段名
	private String category; //表字段分类
	private String trans_column_name; //翻译后的字段名
	private String source_ch_column; //源字段中文名

	/** 取得：对标数据表id */
	public Long getBenchmark_id(){
		return benchmark_id;
	}
	/** 设置：对标数据表id */
	public void setBenchmark_id(Long benchmark_id){
		this.benchmark_id=benchmark_id;
	}
	/** 设置：对标数据表id */
	public void setBenchmark_id(String benchmark_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(benchmark_id)){
			this.benchmark_id=new Long(benchmark_id);
		}
	}
	/** 取得：源表名 */
	public String getSource_table_name(){
		return source_table_name;
	}
	/** 设置：源表名 */
	public void setSource_table_name(String source_table_name){
		this.source_table_name=source_table_name;
	}
	/** 取得：源字段名 */
	public String getSource_column_name(){
		return source_column_name;
	}
	/** 设置：源字段名 */
	public void setSource_column_name(String source_column_name){
		this.source_column_name=source_column_name;
	}
	/** 取得：表字段分类 */
	public String getCategory(){
		return category;
	}
	/** 设置：表字段分类 */
	public void setCategory(String category){
		this.category=category;
	}
	/** 取得：翻译后的字段名 */
	public String getTrans_column_name(){
		return trans_column_name;
	}
	/** 设置：翻译后的字段名 */
	public void setTrans_column_name(String trans_column_name){
		this.trans_column_name=trans_column_name;
	}
	/** 取得：源字段中文名 */
	public String getSource_ch_column(){
		return source_ch_column;
	}
	/** 设置：源字段中文名 */
	public void setSource_ch_column(String source_ch_column){
		this.source_ch_column=source_ch_column;
	}
}
