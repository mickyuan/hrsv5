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
 * 接口文件生成信息表
 */
@Table(tableName = "interface_file_info")
public class Interface_file_info extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "interface_file_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 接口文件生成信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("file_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private String file_id; //file_id
	private String file_path; //文件路径
	private String remark; //备注
	private Long user_id; //用户ID
	private String data_class; //输出数据类型
	private String data_output; //数据数据形式

	/** 取得：file_id */
	public String getFile_id(){
		return file_id;
	}
	/** 设置：file_id */
	public void setFile_id(String file_id){
		this.file_id=file_id;
	}
	/** 取得：文件路径 */
	public String getFile_path(){
		return file_path;
	}
	/** 设置：文件路径 */
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
	/** 取得：用户ID */
	public Long getUser_id(){
		return user_id;
	}
	/** 设置：用户ID */
	public void setUser_id(Long user_id){
		this.user_id=user_id;
	}
	/** 设置：用户ID */
	public void setUser_id(String user_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(user_id)){
			this.user_id=new Long(user_id);
		}
	}
	/** 取得：输出数据类型 */
	public String getData_class(){
		return data_class;
	}
	/** 设置：输出数据类型 */
	public void setData_class(String data_class){
		this.data_class=data_class;
	}
	/** 取得：数据数据形式 */
	public String getData_output(){
		return data_output;
	}
	/** 设置：数据数据形式 */
	public void setData_output(String data_output){
		this.data_output=data_output;
	}
}
