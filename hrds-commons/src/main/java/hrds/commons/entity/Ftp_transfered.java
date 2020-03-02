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
 * ftp已传输表
 */
@Table(tableName = "ftp_transfered")
public class Ftp_transfered extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ftp_transfered";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** ftp已传输表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("ftp_transfered_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="ftp_transfered_id",value="已传输表id-UUID:",dataType = String.class,required = true)
	private String ftp_transfered_id;
	@DocBean(name ="transfered_name",value="已传输文件名称:",dataType = String.class,required = true)
	private String transfered_name;
	@DocBean(name ="ftp_date",value="ftp日期:",dataType = String.class,required = true)
	private String ftp_date;
	@DocBean(name ="ftp_time",value="ftp时间:",dataType = String.class,required = true)
	private String ftp_time;
	@DocBean(name ="remark",value="备注:",dataType = String.class,required = false)
	private String remark;
	@DocBean(name ="ftp_id",value="ftp采集id:",dataType = Long.class,required = true)
	private Long ftp_id;
	@DocBean(name ="ftp_filemd5",value="文件MD5:",dataType = String.class,required = false)
	private String ftp_filemd5;
	@DocBean(name ="file_path",value="文件绝对路径:",dataType = String.class,required = true)
	private String file_path;

	/** 取得：已传输表id-UUID */
	public String getFtp_transfered_id(){
		return ftp_transfered_id;
	}
	/** 设置：已传输表id-UUID */
	public void setFtp_transfered_id(String ftp_transfered_id){
		this.ftp_transfered_id=ftp_transfered_id;
	}
	/** 取得：已传输文件名称 */
	public String getTransfered_name(){
		return transfered_name;
	}
	/** 设置：已传输文件名称 */
	public void setTransfered_name(String transfered_name){
		this.transfered_name=transfered_name;
	}
	/** 取得：ftp日期 */
	public String getFtp_date(){
		return ftp_date;
	}
	/** 设置：ftp日期 */
	public void setFtp_date(String ftp_date){
		this.ftp_date=ftp_date;
	}
	/** 取得：ftp时间 */
	public String getFtp_time(){
		return ftp_time;
	}
	/** 设置：ftp时间 */
	public void setFtp_time(String ftp_time){
		this.ftp_time=ftp_time;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：ftp采集id */
	public Long getFtp_id(){
		return ftp_id;
	}
	/** 设置：ftp采集id */
	public void setFtp_id(Long ftp_id){
		this.ftp_id=ftp_id;
	}
	/** 设置：ftp采集id */
	public void setFtp_id(String ftp_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(ftp_id)){
			this.ftp_id=new Long(ftp_id);
		}
	}
	/** 取得：文件MD5 */
	public String getFtp_filemd5(){
		return ftp_filemd5;
	}
	/** 设置：文件MD5 */
	public void setFtp_filemd5(String ftp_filemd5){
		this.ftp_filemd5=ftp_filemd5;
	}
	/** 取得：文件绝对路径 */
	public String getFile_path(){
		return file_path;
	}
	/** 设置：文件绝对路径 */
	public void setFile_path(String file_path){
		this.file_path=file_path;
	}
}
