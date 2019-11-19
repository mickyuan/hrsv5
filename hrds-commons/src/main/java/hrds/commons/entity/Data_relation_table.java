package hrds.commons.entity;
/**Auto Created by VBScript Do not modify!*/
import hrds.commons.entity.fdentity.ProjectTableEntity;
import fd.ng.db.entity.anno.Table;
import fd.ng.core.annotation.DocBean;
import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

/**
 * 数据存储关系表
 */
@Table(tableName = "data_relation_table")
public class Data_relation_table extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "data_relation_table";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据存储关系表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("storage_id");
		__tmpPKS.add("dsl_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="storage_id",value="储存编号:",dataType = Long.class,required = true)
	private Long storage_id;
	@DocBean(name ="dsl_id",value="存储层配置ID:",dataType = Long.class,required = true)
	private Long dsl_id;

	/** 取得：储存编号 */
	public Long getStorage_id(){
		return storage_id;
	}
	/** 设置：储存编号 */
	public void setStorage_id(Long storage_id){
		this.storage_id=storage_id;
	}
	/** 设置：储存编号 */
	public void setStorage_id(String storage_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(storage_id)){
			this.storage_id=new Long(storage_id);
		}
	}
	/** 取得：存储层配置ID */
	public Long getDsl_id(){
		return dsl_id;
	}
	/** 设置：存储层配置ID */
	public void setDsl_id(Long dsl_id){
		this.dsl_id=dsl_id;
	}
	/** 设置：存储层配置ID */
	public void setDsl_id(String dsl_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(dsl_id)){
			this.dsl_id=new Long(dsl_id);
		}
	}
}
