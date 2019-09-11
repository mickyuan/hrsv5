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
 * 数据存储关系表
 */
@Table(tableName = "data_relation_table")
public class Data_relation_table extends TableEntity
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
		__tmpPKS.add("datasc_id");
		__tmpPKS.add("storage_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long datasc_id; //存储配置主键信息
	private Long storage_id; //储存编号

	/** 取得：存储配置主键信息 */
	public Long getDatasc_id(){
		return datasc_id;
	}
	/** 设置：存储配置主键信息 */
	public void setDatasc_id(Long datasc_id){
		this.datasc_id=datasc_id;
	}
	/** 设置：存储配置主键信息 */
	public void setDatasc_id(String datasc_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(datasc_id)){
			this.datasc_id=new Long(datasc_id);
		}
	}
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
}
