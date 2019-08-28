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
 * 报表类型表
 */
@Table(tableName = "report_type_table")
public class Report_type_table extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "report_type_table";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 报表类型表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("report_type_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long report_type_id; //报表类型编号
	private String report_type_name; //报表类型名称
	private String report_title; //报表标题
	private String report_print_position; //报表图片所在位置
	private String report_data_filed; //报表数据构成字段
	private String report_style_filed; //报表样式构成字段
	private String report_type; //报表类型

	/** 取得：报表类型编号 */
	public Long getReport_type_id(){
		return report_type_id;
	}
	/** 设置：报表类型编号 */
	public void setReport_type_id(Long report_type_id){
		this.report_type_id=report_type_id;
	}
	/** 设置：报表类型编号 */
	public void setReport_type_id(String report_type_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(report_type_id)){
			this.report_type_id=new Long(report_type_id);
		}
	}
	/** 取得：报表类型名称 */
	public String getReport_type_name(){
		return report_type_name;
	}
	/** 设置：报表类型名称 */
	public void setReport_type_name(String report_type_name){
		this.report_type_name=report_type_name;
	}
	/** 取得：报表标题 */
	public String getReport_title(){
		return report_title;
	}
	/** 设置：报表标题 */
	public void setReport_title(String report_title){
		this.report_title=report_title;
	}
	/** 取得：报表图片所在位置 */
	public String getReport_print_position(){
		return report_print_position;
	}
	/** 设置：报表图片所在位置 */
	public void setReport_print_position(String report_print_position){
		this.report_print_position=report_print_position;
	}
	/** 取得：报表数据构成字段 */
	public String getReport_data_filed(){
		return report_data_filed;
	}
	/** 设置：报表数据构成字段 */
	public void setReport_data_filed(String report_data_filed){
		this.report_data_filed=report_data_filed;
	}
	/** 取得：报表样式构成字段 */
	public String getReport_style_filed(){
		return report_style_filed;
	}
	/** 设置：报表样式构成字段 */
	public void setReport_style_filed(String report_style_filed){
		this.report_style_filed=report_style_filed;
	}
	/** 取得：报表类型 */
	public String getReport_type(){
		return report_type;
	}
	/** 设置：报表类型 */
	public void setReport_type(String report_type){
		this.report_type=report_type;
	}
}
