package hrds.commons.entity;
/**Auto Created by VBScript Do not modify!*/
import fd.ng.db.entity.TableEntity;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.entity.anno.Column;
import fd.ng.db.entity.anno.Table;
import hrds.commons.exception.BusinessException;
import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 * 数据管理消费至文件
 */
@Table(tableName = "sdm_con_file")
public class Sdm_con_file extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_con_file";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据管理消费至文件 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("file_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long file_id; //fileID
	private String file_name; //file名称
	private String file_path; //文件绝对路径
	private String remark; //备注
	private Long sdm_des_id; //配置id
	private String spilt_flag; //是否分割标志
	private Long file_limit; //分割大小
	private String file_bus_class; //file业务处理类
	private String file_bus_type; //file业务类类型

	/** 取得：fileID */
	public Long getFile_id(){
		return file_id;
	}
	/** 设置：fileID */
	public void setFile_id(Long file_id){
		this.file_id=file_id;
	}
	/** 设置：fileID */
	public void setFile_id(String file_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(file_id)){
			this.file_id=new Long(file_id);
		}
	}
	/** 取得：file名称 */
	public String getFile_name(){
		return file_name;
	}
	/** 设置：file名称 */
	public void setFile_name(String file_name){
		this.file_name=file_name;
	}
	/** 取得：文件绝对路径 */
	public String getFile_path(){
		return file_path;
	}
	/** 设置：文件绝对路径 */
	public void setFile_path(String file_path){
		this.file_path=file_path;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：配置id */
	public Long getSdm_des_id(){
		return sdm_des_id;
	}
	/** 设置：配置id */
	public void setSdm_des_id(Long sdm_des_id){
		this.sdm_des_id=sdm_des_id;
	}
	/** 设置：配置id */
	public void setSdm_des_id(String sdm_des_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(sdm_des_id)){
			this.sdm_des_id=new Long(sdm_des_id);
		}
	}
	/** 取得：是否分割标志 */
	public String getSpilt_flag(){
		return spilt_flag;
	}
	/** 设置：是否分割标志 */
	public void setSpilt_flag(String spilt_flag){
		this.spilt_flag=spilt_flag;
	}
	/** 取得：分割大小 */
	public Long getFile_limit(){
		return file_limit;
	}
	/** 设置：分割大小 */
	public void setFile_limit(Long file_limit){
		this.file_limit=file_limit;
	}
	/** 设置：分割大小 */
	public void setFile_limit(String file_limit){
		if(!fd.ng.core.utils.StringUtil.isEmpty(file_limit)){
			this.file_limit=new Long(file_limit);
		}
	}
	/** 取得：file业务处理类 */
	public String getFile_bus_class(){
		return file_bus_class;
	}
	/** 设置：file业务处理类 */
	public void setFile_bus_class(String file_bus_class){
		this.file_bus_class=file_bus_class;
	}
	/** 取得：file业务类类型 */
	public String getFile_bus_type(){
		return file_bus_type;
	}
	/** 设置：file业务类类型 */
	public void setFile_bus_type(String file_bus_type){
		this.file_bus_type=file_bus_type;
	}
}
