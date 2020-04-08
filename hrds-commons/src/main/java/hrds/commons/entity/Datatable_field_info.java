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
 * 数据表字段信息
 */
@Table(tableName = "datatable_field_info")
public class Datatable_field_info extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "datatable_field_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据表字段信息 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("datatable_field_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="datatable_field_id",value="数据表字段id:",dataType = Long.class,required = true)
	private Long datatable_field_id;
	@DocBean(name ="field_cn_name",value="字段中文名称:",dataType = String.class,required = true)
	private String field_cn_name;
	@DocBean(name ="field_en_name",value="字段英文名称:",dataType = String.class,required = true)
	private String field_en_name;
	@DocBean(name ="field_type",value="字段类型:",dataType = String.class,required = true)
	private String field_type;
	@DocBean(name ="field_desc",value="字段描述:",dataType = String.class,required = false)
	private String field_desc;
	@DocBean(name ="field_seq",value="字段序号:",dataType = Long.class,required = true)
	private Long field_seq;
	@DocBean(name ="remark",value="备注:",dataType = String.class,required = false)
	private String remark;
	@DocBean(name ="field_length",value="字段长度:",dataType = String.class,required = false)
	private String field_length;
	@DocBean(name ="field_process",value="处理方式(ProcessType):1-定值<DingZhi> 2-自增<ZiZeng> 3-映射<YingShe> ",dataType = String.class,required = true)
	private String field_process;
	@DocBean(name ="process_para",value="处理方式对应参数:",dataType = String.class,required = false)
	private String process_para;
	@DocBean(name ="datatable_id",value="数据表id:",dataType = Long.class,required = true)
	private Long datatable_id;

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
	/** 取得：字段中文名称 */
	public String getField_cn_name(){
		return field_cn_name;
	}
	/** 设置：字段中文名称 */
	public void setField_cn_name(String field_cn_name){
		this.field_cn_name=field_cn_name;
	}
	/** 取得：字段英文名称 */
	public String getField_en_name(){
		return field_en_name;
	}
	/** 设置：字段英文名称 */
	public void setField_en_name(String field_en_name){
		this.field_en_name=field_en_name;
	}
	/** 取得：字段类型 */
	public String getField_type(){
		return field_type;
	}
	/** 设置：字段类型 */
	public void setField_type(String field_type){
		this.field_type=field_type;
	}
	/** 取得：字段描述 */
	public String getField_desc(){
		return field_desc;
	}
	/** 设置：字段描述 */
	public void setField_desc(String field_desc){
		this.field_desc=field_desc;
	}
	/** 取得：字段序号 */
	public Long getField_seq(){
		return field_seq;
	}
	/** 设置：字段序号 */
	public void setField_seq(Long field_seq){
		this.field_seq=field_seq;
	}
	/** 设置：字段序号 */
	public void setField_seq(String field_seq){
		if(!fd.ng.core.utils.StringUtil.isEmpty(field_seq)){
			this.field_seq=new Long(field_seq);
		}
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：字段长度 */
	public String getField_length(){
		return field_length;
	}
	/** 设置：字段长度 */
	public void setField_length(String field_length){
		this.field_length=field_length;
	}
	/** 取得：处理方式 */
	public String getField_process(){
		return field_process;
	}
	/** 设置：处理方式 */
	public void setField_process(String field_process){
		this.field_process=field_process;
	}
	/** 取得：处理方式对应参数 */
	public String getProcess_para(){
		return process_para;
	}
	/** 设置：处理方式对应参数 */
	public void setProcess_para(String process_para){
		this.process_para=process_para;
	}
	/** 取得：数据表id */
	public Long getDatatable_id(){
		return datatable_id;
	}
	/** 设置：数据表id */
	public void setDatatable_id(Long datatable_id){
		this.datatable_id=datatable_id;
	}
	/** 设置：数据表id */
	public void setDatatable_id(String datatable_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(datatable_id)){
			this.datatable_id=new Long(datatable_id);
		}
	}
}
