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
 * 数据对标结果分类表
 */
@Table(tableName = "dbm_data_sort")
public class Dbm_data_sort extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dbm_data_sort";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据对标结果分类表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("dds_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="dds_id",value="对标分类结构ID:",dataType = Long.class,required = true)
	private Long dds_id;
	@DocBean(name ="dds_name",value="分类名称:",dataType = String.class,required = true)
	private String dds_name;
	@DocBean(name ="dds_number",value="分类编号:",dataType = String.class,required = true)
	private String dds_number;
	@DocBean(name ="dds_remark",value="分类备注:",dataType = String.class,required = false)
	private String dds_remark;
	@DocBean(name ="basic_id",value="标准元主键:",dataType = Long.class,required = true)
	private Long basic_id;
	@DocBean(name ="detect_id",value="检测主键:",dataType = String.class,required = false)
	private String detect_id;

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
	/** 取得：分类名称 */
	public String getDds_name(){
		return dds_name;
	}
	/** 设置：分类名称 */
	public void setDds_name(String dds_name){
		this.dds_name=dds_name;
	}
	/** 取得：分类编号 */
	public String getDds_number(){
		return dds_number;
	}
	/** 设置：分类编号 */
	public void setDds_number(String dds_number){
		this.dds_number=dds_number;
	}
	/** 取得：分类备注 */
	public String getDds_remark(){
		return dds_remark;
	}
	/** 设置：分类备注 */
	public void setDds_remark(String dds_remark){
		this.dds_remark=dds_remark;
	}
	/** 取得：标准元主键 */
	public Long getBasic_id(){
		return basic_id;
	}
	/** 设置：标准元主键 */
	public void setBasic_id(Long basic_id){
		this.basic_id=basic_id;
	}
	/** 设置：标准元主键 */
	public void setBasic_id(String basic_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(basic_id)){
			this.basic_id=new Long(basic_id);
		}
	}
	/** 取得：检测主键 */
	public String getDetect_id(){
		return detect_id;
	}
	/** 设置：检测主键 */
	public void setDetect_id(String detect_id){
		this.detect_id=detect_id;
	}
}
