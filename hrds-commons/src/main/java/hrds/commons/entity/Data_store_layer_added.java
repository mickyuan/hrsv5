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
 * 数据存储附加信息表
 */
@Table(tableName = "data_store_layer_added")
public class Data_store_layer_added extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "data_store_layer_added";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据存储附加信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("dslad_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="dslad_id",value="附加信息ID:",dataType = Long.class,required = true)
	private Long dslad_id;
	@DocBean(name ="dsl_id",value="存储层配置ID:",dataType = Long.class,required = true)
	private Long dsl_id;
	@DocBean(name ="dsla_storelayer",value="配置附加属性信息(StoreLayerAdded):01-主键<ZhuJian> 02-rowkey<RowKey> 03-索引列<SuoYinLie> 04-预聚合列<YuJuHe> 05-排序列<PaiXuLie> 06-分区列<FenQuLie> ",dataType = String.class,required = true)
	private String dsla_storelayer;
	@DocBean(name ="dslad_remark",value="备注:",dataType = String.class,required = false)
	private String dslad_remark;

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
	/** 取得：配置附加属性信息 */
	public String getDsla_storelayer(){
		return dsla_storelayer;
	}
	/** 设置：配置附加属性信息 */
	public void setDsla_storelayer(String dsla_storelayer){
		this.dsla_storelayer=dsla_storelayer;
	}
	/** 取得：备注 */
	public String getDslad_remark(){
		return dslad_remark;
	}
	/** 设置：备注 */
	public void setDslad_remark(String dslad_remark){
		this.dslad_remark=dslad_remark;
	}
}
