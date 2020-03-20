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
 * 对象采集对应信息
 */
@Table(tableName = "object_collect_task")
public class Object_collect_task extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "object_collect_task";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 对象采集对应信息 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("ocs_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="ocs_id",value="对象采集任务编号:",dataType = Long.class,required = true)
	private Long ocs_id;
	@DocBean(name ="en_name",value="英文名称:",dataType = String.class,required = true)
	private String en_name;
	@DocBean(name ="zh_name",value="中文名称:",dataType = String.class,required = true)
	private String zh_name;
	@DocBean(name ="remark",value="备注:",dataType = String.class,required = false)
	private String remark;
	@DocBean(name ="collect_data_type",value="数据类型(CollectDataType):1-xml<XML> 2-json<JSON> ",dataType = String.class,required = true)
	private String collect_data_type;
	@DocBean(name ="database_code",value="采集编码(DataBaseCode):1-UTF-8<UTF_8> 2-GBK<GBK> 3-UTF-16<UTF_16> 4-GB2312<GB2312> 5-ISO-8859-1<ISO_8859_1> ",dataType = String.class,required = true)
	private String database_code;
	@DocBean(name ="agent_id",value="Agent_id:",dataType = Long.class,required = true)
	private Long agent_id;
	@DocBean(name ="firstline",value="第一行数据:",dataType = String.class,required = false)
	private String firstline;
	@DocBean(name ="odc_id",value="对象采集id:",dataType = Long.class,required = false)
	private Long odc_id;
	@DocBean(name ="updatetype",value="更新方式(UpdateType):0-直接更新<DirectUpdate> 1-拉链更新<IncrementUpdate> ",dataType = String.class,required = true)
	private String updatetype;

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
	/** 取得：英文名称 */
	public String getEn_name(){
		return en_name;
	}
	/** 设置：英文名称 */
	public void setEn_name(String en_name){
		this.en_name=en_name;
	}
	/** 取得：中文名称 */
	public String getZh_name(){
		return zh_name;
	}
	/** 设置：中文名称 */
	public void setZh_name(String zh_name){
		this.zh_name=zh_name;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：数据类型 */
	public String getCollect_data_type(){
		return collect_data_type;
	}
	/** 设置：数据类型 */
	public void setCollect_data_type(String collect_data_type){
		this.collect_data_type=collect_data_type;
	}
	/** 取得：采集编码 */
	public String getDatabase_code(){
		return database_code;
	}
	/** 设置：采集编码 */
	public void setDatabase_code(String database_code){
		this.database_code=database_code;
	}
	/** 取得：Agent_id */
	public Long getAgent_id(){
		return agent_id;
	}
	/** 设置：Agent_id */
	public void setAgent_id(Long agent_id){
		this.agent_id=agent_id;
	}
	/** 设置：Agent_id */
	public void setAgent_id(String agent_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(agent_id)){
			this.agent_id=new Long(agent_id);
		}
	}
	/** 取得：第一行数据 */
	public String getFirstline(){
		return firstline;
	}
	/** 设置：第一行数据 */
	public void setFirstline(String firstline){
		this.firstline=firstline;
	}
	/** 取得：对象采集id */
	public Long getOdc_id(){
		return odc_id;
	}
	/** 设置：对象采集id */
	public void setOdc_id(Long odc_id){
		this.odc_id=odc_id;
	}
	/** 设置：对象采集id */
	public void setOdc_id(String odc_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(odc_id)){
			this.odc_id=new Long(odc_id);
		}
	}
	/** 取得：更新方式 */
	public String getUpdatetype(){
		return updatetype;
	}
	/** 设置：更新方式 */
	public void setUpdatetype(String updatetype){
		this.updatetype=updatetype;
	}
}
