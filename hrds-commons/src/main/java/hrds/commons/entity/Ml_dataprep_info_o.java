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
 * 数据预处理其它信息表
 */
@Table(tableName = "ml_dataprep_info_o")
public class Ml_dataprep_info_o extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_dataprep_info_o";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据预处理其它信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("datapo_info_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long datapo_info_id; //数据预处理其它信息编号
	private String newtable_name; //新表名称
	private Long dtable_info_id; //数据表信息编号
	private String create_date; //创建日期
	private String create_time; //创建时间
	private String remark; //备注
	private String generate_is_flag; //新表是否生成完成
	private String new_filepath; //新表文件路径
	private Long new_filesize; //新表文件大小
	private String datamapmode; //数据映射方式

	/** 取得：数据预处理其它信息编号 */
	public Long getDatapo_info_id(){
		return datapo_info_id;
	}
	/** 设置：数据预处理其它信息编号 */
	public void setDatapo_info_id(Long datapo_info_id){
		this.datapo_info_id=datapo_info_id;
	}
	/** 设置：数据预处理其它信息编号 */
	public void setDatapo_info_id(String datapo_info_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(datapo_info_id)){
			this.datapo_info_id=new Long(datapo_info_id);
		}
	}
	/** 取得：新表名称 */
	public String getNewtable_name(){
		return newtable_name;
	}
	/** 设置：新表名称 */
	public void setNewtable_name(String newtable_name){
		this.newtable_name=newtable_name;
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
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：新表是否生成完成 */
	public String getGenerate_is_flag(){
		return generate_is_flag;
	}
	/** 设置：新表是否生成完成 */
	public void setGenerate_is_flag(String generate_is_flag){
		this.generate_is_flag=generate_is_flag;
	}
	/** 取得：新表文件路径 */
	public String getNew_filepath(){
		return new_filepath;
	}
	/** 设置：新表文件路径 */
	public void setNew_filepath(String new_filepath){
		this.new_filepath=new_filepath;
	}
	/** 取得：新表文件大小 */
	public Long getNew_filesize(){
		return new_filesize;
	}
	/** 设置：新表文件大小 */
	public void setNew_filesize(Long new_filesize){
		this.new_filesize=new_filesize;
	}
	/** 设置：新表文件大小 */
	public void setNew_filesize(String new_filesize){
		if(!fd.ng.core.utils.StringUtil.isEmpty(new_filesize)){
			this.new_filesize=new Long(new_filesize);
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
}
