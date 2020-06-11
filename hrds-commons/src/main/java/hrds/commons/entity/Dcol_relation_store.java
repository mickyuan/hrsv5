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
 * 数据字段存储关系表
 */
@Table(tableName = "dcol_relation_store")
public class Dcol_relation_store extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dcol_relation_store";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据字段存储关系表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("dslad_id");
		__tmpPKS.add("col_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="csi_number",value="序号位置:",dataType = Long.class,required = true)
	private Long csi_number;
	@DocBean(name ="dslad_id",value="附加信息ID:",dataType = Long.class,required = true)
	private Long dslad_id;
	@DocBean(name ="col_id",value="结构信息id:",dataType = Long.class,required = true)
	private Long col_id;
	@DocBean(name ="data_source",value="存储层-数据来源(StoreLayerDataSource):1-db采集<DB> 2-数据库采集<DBA> 3-对象采集<OBJ> 4-数据集市<DM> 5-数据管控<DQ> ",dataType = String.class,required = true)
	private String data_source;

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
	/** 取得：结构信息id */
	public Long getCol_id(){
		return col_id;
	}
	/** 设置：结构信息id */
	public void setCol_id(Long col_id){
		this.col_id=col_id;
	}
	/** 设置：结构信息id */
	public void setCol_id(String col_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(col_id)){
			this.col_id=new Long(col_id);
		}
	}
	/** 取得：存储层-数据来源 */
	public String getData_source(){
		return data_source;
	}
	/** 设置：存储层-数据来源 */
	public void setData_source(String data_source){
		this.data_source=data_source;
	}
}
