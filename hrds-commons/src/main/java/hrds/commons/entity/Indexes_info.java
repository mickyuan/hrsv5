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
 * 二级索引信息表
 */
@Table(tableName = "indexes_info")
public class Indexes_info extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "indexes_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 二级索引信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("indexes_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long indexes_id; //索引ID
	private String indexes_field; //索引字段
	private String file_id; //文件编号
	private String indexes_select_field; //查询字段
	private String indexes_type; //索引类型

	/** 取得：索引ID */
	public Long getIndexes_id(){
		return indexes_id;
	}
	/** 设置：索引ID */
	public void setIndexes_id(Long indexes_id){
		this.indexes_id=indexes_id;
	}
	/** 设置：索引ID */
	public void setIndexes_id(String indexes_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(indexes_id)){
			this.indexes_id=new Long(indexes_id);
		}
	}
	/** 取得：索引字段 */
	public String getIndexes_field(){
		return indexes_field;
	}
	/** 设置：索引字段 */
	public void setIndexes_field(String indexes_field){
		this.indexes_field=indexes_field;
	}
	/** 取得：文件编号 */
	public String getFile_id(){
		return file_id;
	}
	/** 设置：文件编号 */
	public void setFile_id(String file_id){
		this.file_id=file_id;
	}
	/** 取得：查询字段 */
	public String getIndexes_select_field(){
		return indexes_select_field;
	}
	/** 设置：查询字段 */
	public void setIndexes_select_field(String indexes_select_field){
		this.indexes_select_field=indexes_select_field;
	}
	/** 取得：索引类型 */
	public String getIndexes_type(){
		return indexes_type;
	}
	/** 设置：索引类型 */
	public void setIndexes_type(String indexes_type){
		this.indexes_type=indexes_type;
	}
}
