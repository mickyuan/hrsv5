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
 * 文件目录表
 */
@Table(tableName = "floder_table")
public class Floder_table extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "floder_table";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 文件目录表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("folder_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long folder_id; //文件夹编号
	private String floder_name; //文件夹名称
	private String create_date; //创建日期
	private String create_time; //创建时间
	private Long create_id; //用户ID

	/** 取得：文件夹编号 */
	public Long getFolder_id(){
		return folder_id;
	}
	/** 设置：文件夹编号 */
	public void setFolder_id(Long folder_id){
		this.folder_id=folder_id;
	}
	/** 设置：文件夹编号 */
	public void setFolder_id(String folder_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(folder_id)){
			this.folder_id=new Long(folder_id);
		}
	}
	/** 取得：文件夹名称 */
	public String getFloder_name(){
		return floder_name;
	}
	/** 设置：文件夹名称 */
	public void setFloder_name(String floder_name){
		this.floder_name=floder_name;
	}
	/** 取得：创建日期 */
	public String getCreate_date(){
		return create_date;
	}
	/** 设置：创建日期 */
	public void setCreate_date(String create_date){
		this.create_date=create_date;
	}
	/** 取得：创建时间 */
	public String getCreate_time(){
		return create_time;
	}
	/** 设置：创建时间 */
	public void setCreate_time(String create_time){
		this.create_time=create_time;
	}
	/** 取得：用户ID */
	public Long getCreate_id(){
		return create_id;
	}
	/** 设置：用户ID */
	public void setCreate_id(Long create_id){
		this.create_id=create_id;
	}
	/** 设置：用户ID */
	public void setCreate_id(String create_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(create_id)){
			this.create_id=new Long(create_id);
		}
	}
}
