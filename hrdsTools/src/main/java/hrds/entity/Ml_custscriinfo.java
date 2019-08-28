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
 * 机器学习用户脚本结果信息表
 */
@Table(tableName = "ml_custscriinfo")
public class Ml_custscriinfo extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_custscriinfo";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 机器学习用户脚本结果信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("script_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long script_id; //脚本编号
	private String new_filepath; //新数据文件路径
	private Long new_filesize; //新数据文件大小
	private String new_tablename; //新数据表名
	private String current_step; //当前操作步骤
	private String update_date; //修改日期
	private String update_time; //修改时间
	private Long dtable_info_id; //数据表信息编号
	private String datamapmode; //数据映射方式
	private String generate_is_flag; //新表是否生成完成
	private String script_path; //脚本文件地址

	/** 取得：脚本编号 */
	public Long getScript_id(){
		return script_id;
	}
	/** 设置：脚本编号 */
	public void setScript_id(Long script_id){
		this.script_id=script_id;
	}
	/** 设置：脚本编号 */
	public void setScript_id(String script_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(script_id)){
			this.script_id=new Long(script_id);
		}
	}
	/** 取得：新数据文件路径 */
	public String getNew_filepath(){
		return new_filepath;
	}
	/** 设置：新数据文件路径 */
	public void setNew_filepath(String new_filepath){
		this.new_filepath=new_filepath;
	}
	/** 取得：新数据文件大小 */
	public Long getNew_filesize(){
		return new_filesize;
	}
	/** 设置：新数据文件大小 */
	public void setNew_filesize(Long new_filesize){
		this.new_filesize=new_filesize;
	}
	/** 设置：新数据文件大小 */
	public void setNew_filesize(String new_filesize){
		if(!fd.ng.core.utils.StringUtil.isEmpty(new_filesize)){
			this.new_filesize=new Long(new_filesize);
		}
	}
	/** 取得：新数据表名 */
	public String getNew_tablename(){
		return new_tablename;
	}
	/** 设置：新数据表名 */
	public void setNew_tablename(String new_tablename){
		this.new_tablename=new_tablename;
	}
	/** 取得：当前操作步骤 */
	public String getCurrent_step(){
		return current_step;
	}
	/** 设置：当前操作步骤 */
	public void setCurrent_step(String current_step){
		this.current_step=current_step;
	}
	/** 取得：修改日期 */
	public String getUpdate_date(){
		return update_date;
	}
	/** 设置：修改日期 */
	public void setUpdate_date(String update_date){
		this.update_date=update_date;
	}
	/** 取得：修改时间 */
	public String getUpdate_time(){
		return update_time;
	}
	/** 设置：修改时间 */
	public void setUpdate_time(String update_time){
		this.update_time=update_time;
	}
	/** 取得：数据表信息编号 */
	public Long getDtable_info_id(){
		return dtable_info_id;
	}
	/** 设置：数据表信息编号 */
	public void setDtable_info_id(Long dtable_info_id){
		this.dtable_info_id=dtable_info_id;
	}
	/** 设置：数据表信息编号 */
	public void setDtable_info_id(String dtable_info_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(dtable_info_id)){
			this.dtable_info_id=new Long(dtable_info_id);
		}
	}
	/** 取得：数据映射方式 */
	public String getDatamapmode(){
		return datamapmode;
	}
	/** 设置：数据映射方式 */
	public void setDatamapmode(String datamapmode){
		this.datamapmode=datamapmode;
	}
	/** 取得：新表是否生成完成 */
	public String getGenerate_is_flag(){
		return generate_is_flag;
	}
	/** 设置：新表是否生成完成 */
	public void setGenerate_is_flag(String generate_is_flag){
		this.generate_is_flag=generate_is_flag;
	}
	/** 取得：脚本文件地址 */
	public String getScript_path(){
		return script_path;
	}
	/** 设置：脚本文件地址 */
	public void setScript_path(String script_path){
		this.script_path=script_path;
	}
}
