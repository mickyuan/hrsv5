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
 * 系统备份信息表
 */
@Table(tableName = "sys_dump")
public class Sys_dump extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sys_dump";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 系统备份信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("dump_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="dump_id",value="备份id:",dataType = Long.class,required = true)
	private Long dump_id;
	@DocBean(name ="bak_date",value="备份日期:",dataType = String.class,required = true)
	private String bak_date;
	@DocBean(name ="bak_time",value="备份时间:",dataType = String.class,required = true)
	private String bak_time;
	@DocBean(name ="file_size",value="文件大小:",dataType = String.class,required = true)
	private String file_size;
	@DocBean(name ="hdfs_path",value="文件存放hdfs路径:",dataType = String.class,required = true)
	private String hdfs_path;
	@DocBean(name ="length",value="备份时长:",dataType = String.class,required = true)
	private String length;
	@DocBean(name ="remark",value="备注:",dataType = String.class,required = false)
	private String remark;
	@DocBean(name ="file_name",value="文件名称:",dataType = String.class,required = true)
	private String file_name;

	/** 取得：备份id */
	public Long getDump_id(){
		return dump_id;
	}
	/** 设置：备份id */
	public void setDump_id(Long dump_id){
		this.dump_id=dump_id;
	}
	/** 设置：备份id */
	public void setDump_id(String dump_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(dump_id)){
			this.dump_id=new Long(dump_id);
		}
	}
	/** 取得：备份日期 */
	public String getBak_date(){
		return bak_date;
	}
	/** 设置：备份日期 */
	public void setBak_date(String bak_date){
		this.bak_date=bak_date;
	}
	/** 取得：备份时间 */
	public String getBak_time(){
		return bak_time;
	}
	/** 设置：备份时间 */
	public void setBak_time(String bak_time){
		this.bak_time=bak_time;
	}
	/** 取得：文件大小 */
	public String getFile_size(){
		return file_size;
	}
	/** 设置：文件大小 */
	public void setFile_size(String file_size){
		this.file_size=file_size;
	}
	/** 取得：文件存放hdfs路径 */
	public String getHdfs_path(){
		return hdfs_path;
	}
	/** 设置：文件存放hdfs路径 */
	public void setHdfs_path(String hdfs_path){
		this.hdfs_path=hdfs_path;
	}
	/** 取得：备份时长 */
	public String getLength(){
		return length;
	}
	/** 设置：备份时长 */
	public void setLength(String length){
		this.length=length;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：文件名称 */
	public String getFile_name(){
		return file_name;
	}
	/** 设置：文件名称 */
	public void setFile_name(String file_name){
		this.file_name=file_name;
	}
}
