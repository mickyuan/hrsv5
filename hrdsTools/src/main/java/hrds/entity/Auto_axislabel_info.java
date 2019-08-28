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
 * 轴标签配置信息表
 */
@Table(tableName = "auto_axislabel_info")
public class Auto_axislabel_info extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_axislabel_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 轴标签配置信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("lable_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long lable_id; //标签编号
	private String show; //是否显示
	private String inside; //是否朝内
	private Integer rotate; //旋转角度
	private Integer margin; //标签与轴线距离
	private String formatter; //内容格式器
	private Long axis_id; //轴编号

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
	/** 取得：是否显示 */
	public String getShow(){
		return show;
	}
	/** 设置：是否显示 */
	public void setShow(String show){
		this.show=show;
	}
	/** 取得：是否朝内 */
	public String getInside(){
		return inside;
	}
	/** 设置：是否朝内 */
	public void setInside(String inside){
		this.inside=inside;
	}
	/** 取得：旋转角度 */
	public Integer getRotate(){
		return rotate;
	}
	/** 设置：旋转角度 */
	public void setRotate(Integer rotate){
		this.rotate=rotate;
	}
	/** 设置：旋转角度 */
	public void setRotate(String rotate){
		if(!fd.ng.core.utils.StringUtil.isEmpty(rotate)){
			this.rotate=new Integer(rotate);
		}
	}
	/** 取得：标签与轴线距离 */
	public Integer getMargin(){
		return margin;
	}
	/** 设置：标签与轴线距离 */
	public void setMargin(Integer margin){
		this.margin=margin;
	}
	/** 设置：标签与轴线距离 */
	public void setMargin(String margin){
		if(!fd.ng.core.utils.StringUtil.isEmpty(margin)){
			this.margin=new Integer(margin);
		}
	}
	/** 取得：内容格式器 */
	public String getFormatter(){
		return formatter;
	}
	/** 设置：内容格式器 */
	public void setFormatter(String formatter){
		this.formatter=formatter;
	}
	/** 取得：轴编号 */
	public Long getAxis_id(){
		return axis_id;
	}
	/** 设置：轴编号 */
	public void setAxis_id(Long axis_id){
		this.axis_id=axis_id;
	}
	/** 设置：轴编号 */
	public void setAxis_id(String axis_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(axis_id)){
			this.axis_id=new Long(axis_id);
		}
	}
}
