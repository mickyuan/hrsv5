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
 * 集市字段存储信息
 */
@Table(tableName = "dm_column_storage")
public class Dm_column_storage extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dm_column_storage";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 集市字段存储信息 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("dslad_id");
		__tmpPKS.add("datatable_field_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="csi_number",value="序号位置:",dataType = Long.class,required = true)
	private Long csi_number;
	@DocBean(name ="dslad_id",value="附加信息ID:",dataType = Long.class,required = true)
	private Long dslad_id;
	@DocBean(name ="datatable_field_id",value="数据表字段id:",dataType = Long.class,required = true)
	private Long datatable_field_id;

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
	/** 取得：数据表字段id */
	public Long getDatatable_field_id(){
		return datatable_field_id;
	}
	/** 设置：数据表字段id */
	public void setDatatable_field_id(Long datatable_field_id){
		this.datatable_field_id=datatable_field_id;
	}
	/** 设置：数据表字段id */
	public void setDatatable_field_id(String datatable_field_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(datatable_field_id)){
			this.datatable_field_id=new Long(datatable_field_id);
		}
	}
}
