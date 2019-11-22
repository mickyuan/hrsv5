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
 * 字段存储信息
 */
@Table(tableName = "column_storage_info")
public class Column_storage_info extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "column_storage_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 字段存储信息 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("column_id");
		__tmpPKS.add("dslad_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="column_id",value="字段ID:",dataType = Long.class,required = true)
	private Long column_id;
	@DocBean(name ="dslad_id",value="附加信息ID:",dataType = Long.class,required = true)
	private Long dslad_id;
	@DocBean(name ="csi_number",value="序号位置:",dataType = Long.class,required = true)
	private Long csi_number;

	/** 取得：字段ID */
	public Long getColumn_id(){
		return column_id;
	}
	/** 设置：字段ID */
	public void setColumn_id(Long column_id){
		this.column_id=column_id;
	}
	/** 设置：字段ID */
	public void setColumn_id(String column_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(column_id)){
			this.column_id=new Long(column_id);
		}
	}
	/** 取得：附加信息ID */
	public Long getDslad_id(){
		return dslad_id;
	}
	/** 设置：附加信息ID */
	public void setDslad_id(Long dslad_id){
		this.dslad_id=dslad_id;
	}
	/** 设置：附加信息ID */
	public void setDslad_id(String dslad_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(dslad_id)){
			this.dslad_id=new Long(dslad_id);
		}
	}
	/** 取得：序号位置 */
	public Long getCsi_number(){
		return csi_number;
	}
	/** 设置：序号位置 */
	public void setCsi_number(Long csi_number){
		this.csi_number=csi_number;
	}
	/** 设置：序号位置 */
	public void setCsi_number(String csi_number){
		if(!fd.ng.core.utils.StringUtil.isEmpty(csi_number)){
			this.csi_number=new Long(csi_number);
		}
	}
}
