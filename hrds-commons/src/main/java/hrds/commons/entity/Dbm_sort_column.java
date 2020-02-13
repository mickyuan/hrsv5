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
 * 分类对应的表字段
 */
@Table(tableName = "dbm_sort_column")
public class Dbm_sort_column extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dbm_sort_column";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 分类对应的表字段 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("dsc_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="dds_id",value="对标分类结构ID:",dataType = Long.class,required = true)
	private Long dds_id;
	@DocBean(name ="col_id",value="字段主键:",dataType = Long.class,required = true)
	private Long col_id;
	@DocBean(name ="dsc_id",value="分类对应主键ID:",dataType = Long.class,required = true)
	private Long dsc_id;
	@DocBean(name ="dsc_remark",value="备注:",dataType = String.class,required = false)
	private String dsc_remark;
	@DocBean(name ="dbm_tableid",value="检测表主键:",dataType = Long.class,required = true)
	private Long dbm_tableid;

	/** 取得：对标分类结构ID */
	public Long getDds_id(){
		return dds_id;
	}
	/** 设置：对标分类结构ID */
	public void setDds_id(Long dds_id){
		this.dds_id=dds_id;
	}
	/** 设置：对标分类结构ID */
	public void setDds_id(String dds_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(dds_id)){
			this.dds_id=new Long(dds_id);
		}
	}
	/** 取得：字段主键 */
	public Long getCol_id(){
		return col_id;
	}
	/** 设置：字段主键 */
	public void setCol_id(Long col_id){
		this.col_id=col_id;
	}
	/** 设置：字段主键 */
	public void setCol_id(String col_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(col_id)){
			this.col_id=new Long(col_id);
		}
	}
	/** 取得：分类对应主键ID */
	public Long getDsc_id(){
		return dsc_id;
	}
	/** 设置：分类对应主键ID */
	public void setDsc_id(Long dsc_id){
		this.dsc_id=dsc_id;
	}
	/** 设置：分类对应主键ID */
	public void setDsc_id(String dsc_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(dsc_id)){
			this.dsc_id=new Long(dsc_id);
		}
	}
	/** 取得：备注 */
	public String getDsc_remark(){
		return dsc_remark;
	}
	/** 设置：备注 */
	public void setDsc_remark(String dsc_remark){
		this.dsc_remark=dsc_remark;
	}
	/** 取得：检测表主键 */
	public Long getDbm_tableid(){
		return dbm_tableid;
	}
	/** 设置：检测表主键 */
	public void setDbm_tableid(Long dbm_tableid){
		this.dbm_tableid=dbm_tableid;
	}
	/** 设置：检测表主键 */
	public void setDbm_tableid(String dbm_tableid){
		if(!fd.ng.core.utils.StringUtil.isEmpty(dbm_tableid)){
			this.dbm_tableid=new Long(dbm_tableid);
		}
	}
}
