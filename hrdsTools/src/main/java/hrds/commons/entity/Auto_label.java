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
 * 图形文本标签表
 */
@Table(tableName = "auto_label")
public class Auto_label extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_label";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 图形文本标签表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("lable_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long lable_id; //标签编号
	private String show_label; //显示文本标签
	private String position; //标签位置
	private String formatter; //标签内容格式器
	private String label_corr_tname; //标签对应的表名
	private Long label_corr_id; //标签对于的编号
	private String show_line; //显示视觉引导线
	private Integer length; //引导线第一段长度
	private Integer length2; //引导线第二段长度
	private String smooth; //平滑引导线

	/** 取得：标签编号 */
	public Long getLable_id(){
		return lable_id;
	}
	/** 设置：标签编号 */
	public void setLable_id(Long lable_id){
		this.lable_id=lable_id;
	}
	/** 设置：标签编号 */
	public void setLable_id(String lable_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(lable_id)){
			this.lable_id=new Long(lable_id);
		}
	}
	/** 取得：显示文本标签 */
	public String getShow_label(){
		return show_label;
	}
	/** 设置：显示文本标签 */
	public void setShow_label(String show_label){
		this.show_label=show_label;
	}
	/** 取得：标签位置 */
	public String getPosition(){
		return position;
	}
	/** 设置：标签位置 */
	public void setPosition(String position){
		this.position=position;
	}
	/** 取得：标签内容格式器 */
	public String getFormatter(){
		return formatter;
	}
	/** 设置：标签内容格式器 */
	public void setFormatter(String formatter){
		this.formatter=formatter;
	}
	/** 取得：标签对应的表名 */
	public String getLabel_corr_tname(){
		return label_corr_tname;
	}
	/** 设置：标签对应的表名 */
	public void setLabel_corr_tname(String label_corr_tname){
		this.label_corr_tname=label_corr_tname;
	}
	/** 取得：标签对于的编号 */
	public Long getLabel_corr_id(){
		return label_corr_id;
	}
	/** 设置：标签对于的编号 */
	public void setLabel_corr_id(Long label_corr_id){
		this.label_corr_id=label_corr_id;
	}
	/** 设置：标签对于的编号 */
	public void setLabel_corr_id(String label_corr_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(label_corr_id)){
			this.label_corr_id=new Long(label_corr_id);
		}
	}
	/** 取得：显示视觉引导线 */
	public String getShow_line(){
		return show_line;
	}
	/** 设置：显示视觉引导线 */
	public void setShow_line(String show_line){
		this.show_line=show_line;
	}
	/** 取得：引导线第一段长度 */
	public Integer getLength(){
		return length;
	}
	/** 设置：引导线第一段长度 */
	public void setLength(Integer length){
		this.length=length;
	}
	/** 设置：引导线第一段长度 */
	public void setLength(String length){
		if(!fd.ng.core.utils.StringUtil.isEmpty(length)){
			this.length=new Integer(length);
		}
	}
	/** 取得：引导线第二段长度 */
	public Integer getLength2(){
		return length2;
	}
	/** 设置：引导线第二段长度 */
	public void setLength2(Integer length2){
		this.length2=length2;
	}
	/** 设置：引导线第二段长度 */
	public void setLength2(String length2){
		if(!fd.ng.core.utils.StringUtil.isEmpty(length2)){
			this.length2=new Integer(length2);
		}
	}
	/** 取得：平滑引导线 */
	public String getSmooth(){
		return smooth;
	}
	/** 设置：平滑引导线 */
	public void setSmooth(String smooth){
		this.smooth=smooth;
	}
}
