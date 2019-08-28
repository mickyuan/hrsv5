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
 * 数据仓库接口信息管理
 */
@Table(tableName = "edw_interface_info")
public class Edw_interface_info extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "edw_interface_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据仓库接口信息管理 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("jobcode");
		__tmpPKS.add("st_dt");
		__tmpPKS.add("st_time");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private String jobcode; //作业编号
	private String syscode; //应用系统编号/来源系统编号
	private String tabname; //表名
	private String filename; //文件名
	private Integer rlength; //单行长度
	private String flagname; //标示文件名
	private String filemode; //文件模式
	private String filetype; //文件类型
	private String file_cod_format; //文件编码格式
	private String col_coldel; //分隔符
	private String file_path; //文件存储位置
	private String file_to_path; //处理后存储位置
	private String file_where; //增量条件
	private String st_dt; //开始日期
	private String end_dt; //结束日期
	private String st_time; //开始时间

	/** 取得：作业编号 */
	public String getJobcode(){
		return jobcode;
	}
	/** 设置：作业编号 */
	public void setJobcode(String jobcode){
		this.jobcode=jobcode;
	}
	/** 取得：应用系统编号/来源系统编号 */
	public String getSyscode(){
		return syscode;
	}
	/** 设置：应用系统编号/来源系统编号 */
	public void setSyscode(String syscode){
		this.syscode=syscode;
	}
	/** 取得：表名 */
	public String getTabname(){
		return tabname;
	}
	/** 设置：表名 */
	public void setTabname(String tabname){
		this.tabname=tabname;
	}
	/** 取得：文件名 */
	public String getFilename(){
		return filename;
	}
	/** 设置：文件名 */
	public void setFilename(String filename){
		this.filename=filename;
	}
	/** 取得：单行长度 */
	public Integer getRlength(){
		return rlength;
	}
	/** 设置：单行长度 */
	public void setRlength(Integer rlength){
		this.rlength=rlength;
	}
	/** 设置：单行长度 */
	public void setRlength(String rlength){
		if(!fd.ng.core.utils.StringUtil.isEmpty(rlength)){
			this.rlength=new Integer(rlength);
		}
	}
	/** 取得：标示文件名 */
	public String getFlagname(){
		return flagname;
	}
	/** 设置：标示文件名 */
	public void setFlagname(String flagname){
		this.flagname=flagname;
	}
	/** 取得：文件模式 */
	public String getFilemode(){
		return filemode;
	}
	/** 设置：文件模式 */
	public void setFilemode(String filemode){
		this.filemode=filemode;
	}
	/** 取得：文件类型 */
	public String getFiletype(){
		return filetype;
	}
	/** 设置：文件类型 */
	public void setFiletype(String filetype){
		this.filetype=filetype;
	}
	/** 取得：文件编码格式 */
	public String getFile_cod_format(){
		return file_cod_format;
	}
	/** 设置：文件编码格式 */
	public void setFile_cod_format(String file_cod_format){
		this.file_cod_format=file_cod_format;
	}
	/** 取得：分隔符 */
	public String getCol_coldel(){
		return col_coldel;
	}
	/** 设置：分隔符 */
	public void setCol_coldel(String col_coldel){
		this.col_coldel=col_coldel;
	}
	/** 取得：文件存储位置 */
	public String getFile_path(){
		return file_path;
	}
	/** 设置：文件存储位置 */
	public void setFile_path(String file_path){
		this.file_path=file_path;
	}
	/** 取得：处理后存储位置 */
	public String getFile_to_path(){
		return file_to_path;
	}
	/** 设置：处理后存储位置 */
	public void setFile_to_path(String file_to_path){
		this.file_to_path=file_to_path;
	}
	/** 取得：增量条件 */
	public String getFile_where(){
		return file_where;
	}
	/** 设置：增量条件 */
	public void setFile_where(String file_where){
		this.file_where=file_where;
	}
	/** 取得：开始日期 */
	public String getSt_dt(){
		return st_dt;
	}
	/** 设置：开始日期 */
	public void setSt_dt(String st_dt){
		this.st_dt=st_dt;
	}
	/** 取得：结束日期 */
	public String getEnd_dt(){
		return end_dt;
	}
	/** 设置：结束日期 */
	public void setEnd_dt(String end_dt){
		this.end_dt=end_dt;
	}
	/** 取得：开始时间 */
	public String getSt_time(){
		return st_time;
	}
	/** 设置：开始时间 */
	public void setSt_time(String st_time){
		this.st_time=st_time;
	}
}
