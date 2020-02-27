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
 * 对象采集存储设置
 */
@Table(tableName = "object_storage")
public class Object_storage extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "object_storage";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 对象采集存储设置 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("obj_stid");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="obj_stid",value="存储编号:",dataType = Long.class,required = true)
	private Long obj_stid;
	@DocBean(name ="is_hbase",value="是否进hbase(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_hbase;
	@DocBean(name ="is_hdfs",value="是否进hdfs(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_hdfs;
	@DocBean(name ="remark",value="备注:",dataType = String.class,required = false)
	private String remark;
	@DocBean(name ="ocs_id",value="对象采集任务编号:",dataType = Long.class,required = false)
	private Long ocs_id;
	@DocBean(name ="is_solr",value="是否进solr(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_solr;

	/** 取得：存储编号 */
	public Long getObj_stid(){
		return obj_stid;
	}
	/** 设置：存储编号 */
	public void setObj_stid(Long obj_stid){
		this.obj_stid=obj_stid;
	}
	/** 设置：存储编号 */
	public void setObj_stid(String obj_stid){
		if(!fd.ng.core.utils.StringUtil.isEmpty(obj_stid)){
			this.obj_stid=new Long(obj_stid);
		}
	}
	/** 取得：是否进hbase */
	public String getIs_hbase(){
		return is_hbase;
	}
	/** 设置：是否进hbase */
	public void setIs_hbase(String is_hbase){
		this.is_hbase=is_hbase;
	}
	/** 取得：是否进hdfs */
	public String getIs_hdfs(){
		return is_hdfs;
	}
	/** 设置：是否进hdfs */
	public void setIs_hdfs(String is_hdfs){
		this.is_hdfs=is_hdfs;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：对象采集任务编号 */
	public Long getOcs_id(){
		return ocs_id;
	}
	/** 设置：对象采集任务编号 */
	public void setOcs_id(Long ocs_id){
		this.ocs_id=ocs_id;
	}
	/** 设置：对象采集任务编号 */
	public void setOcs_id(String ocs_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(ocs_id)){
			this.ocs_id=new Long(ocs_id);
		}
	}
	/** 取得：是否进solr */
	public String getIs_solr(){
		return is_solr;
	}
	/** 设置：是否进solr */
	public void setIs_solr(String is_solr){
		this.is_solr=is_solr;
	}
}
