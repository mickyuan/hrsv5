package hrds.entity;
/**Auto Created by VBScript Do not modify!*/
import fd.ng.db.entity.TableEntity;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.entity.anno.Column;
import fd.ng.db.entity.anno.Table;
import hrds.exception.BusinessException;
import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 * 数据集市文件导出
 */
@Table(tableName = "datamart_export")
public class Datamart_export extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "datamart_export";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据集市文件导出 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("export_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long export_id; //导出表id
	private String remark; //备注
	private String storage_path; //文件存储地址
	private String datafile_separator; //数据文件使用分隔符
	private String file_hbase; //文件是否存放集群
	private String reduce_type; //压缩格式
	private String file_code; //导出文件编码
	private String is_fixed; //是否定长
	private String filling_type; //补齐方式
	private String filling_char; //补齐字符
	private Long datatable_id; //数据表id
	private String is_info; //是否生成信号文件

	/** 取得：导出表id */
	public Long getExport_id(){
		return export_id;
	}
	/** 设置：导出表id */
	public void setExport_id(Long export_id){
		this.export_id=export_id;
	}
	/** 设置：导出表id */
	public void setExport_id(String export_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(export_id)){
			this.export_id=new Long(export_id);
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
	/** 取得：文件存储地址 */
	public String getStorage_path(){
		return storage_path;
	}
	/** 设置：文件存储地址 */
	public void setStorage_path(String storage_path){
		this.storage_path=storage_path;
	}
	/** 取得：数据文件使用分隔符 */
	public String getDatafile_separator(){
		return datafile_separator;
	}
	/** 设置：数据文件使用分隔符 */
	public void setDatafile_separator(String datafile_separator){
		this.datafile_separator=datafile_separator;
	}
	/** 取得：文件是否存放集群 */
	public String getFile_hbase(){
		return file_hbase;
	}
	/** 设置：文件是否存放集群 */
	public void setFile_hbase(String file_hbase){
		this.file_hbase=file_hbase;
	}
	/** 取得：压缩格式 */
	public String getReduce_type(){
		return reduce_type;
	}
	/** 设置：压缩格式 */
	public void setReduce_type(String reduce_type){
		this.reduce_type=reduce_type;
	}
	/** 取得：导出文件编码 */
	public String getFile_code(){
		return file_code;
	}
	/** 设置：导出文件编码 */
	public void setFile_code(String file_code){
		this.file_code=file_code;
	}
	/** 取得：是否定长 */
	public String getIs_fixed(){
		return is_fixed;
	}
	/** 设置：是否定长 */
	public void setIs_fixed(String is_fixed){
		this.is_fixed=is_fixed;
	}
	/** 取得：补齐方式 */
	public String getFilling_type(){
		return filling_type;
	}
	/** 设置：补齐方式 */
	public void setFilling_type(String filling_type){
		this.filling_type=filling_type;
	}
	/** 取得：补齐字符 */
	public String getFilling_char(){
		return filling_char;
	}
	/** 设置：补齐字符 */
	public void setFilling_char(String filling_char){
		this.filling_char=filling_char;
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
	/** 取得：是否生成信号文件 */
	public String getIs_info(){
		return is_info;
	}
	/** 设置：是否生成信号文件 */
	public void setIs_info(String is_info){
		this.is_info=is_info;
	}
}
