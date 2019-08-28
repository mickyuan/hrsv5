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
 * 数据源与部门关系
 */
@Table(tableName = "source_relation_dep")
public class Source_relation_dep extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "source_relation_dep";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据源与部门关系 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("dep_id");
		__tmpPKS.add("source_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long dep_id; //部门ID
	private Long source_id; //数据源ID

	/** 取得：部门ID */
	public Long getDep_id(){
		return dep_id;
	}
	/** 设置：部门ID */
	public void setDep_id(Long dep_id){
		this.dep_id=dep_id;
	}
	/** 设置：部门ID */
	public void setDep_id(String dep_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(dep_id)){
			this.dep_id=new Long(dep_id);
		}
	}
	/** 取得：数据源ID */
	public Long getSource_id(){
		return source_id;
	}
	/** 设置：数据源ID */
	public void setSource_id(Long source_id){
		this.source_id=source_id;
	}
	/** 设置：数据源ID */
	public void setSource_id(String source_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(source_id)){
			this.source_id=new Long(source_id);
		}
	}
}
