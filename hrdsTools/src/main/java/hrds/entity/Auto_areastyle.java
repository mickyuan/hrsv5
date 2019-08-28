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
 * 图表配置区域样式信息表
 */
@Table(tableName = "auto_areastyle")
public class Auto_areastyle extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_areastyle";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 图表配置区域样式信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("style_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long style_id; //样式编号
	private String color; //填充颜色
	private String origin; //区域起始位置
	private BigDecimal opacity; //图形透明度
	private Integer shadowblur; //阴影模糊大小
	private String shadowcolor; //阴影颜色
	private Integer shadowoffsetx; //阴影水平偏移距离
	private Integer shadowoffsety; //阴影垂直偏移距离
	private Long config_id; //配置编号

	/** 取得：样式编号 */
	public Long getStyle_id(){
		return style_id;
	}
	/** 设置：样式编号 */
	public void setStyle_id(Long style_id){
		this.style_id=style_id;
	}
	/** 设置：样式编号 */
	public void setStyle_id(String style_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(style_id)){
			this.style_id=new Long(style_id);
		}
	}
	/** 取得：填充颜色 */
	public String getColor(){
		return color;
	}
	/** 设置：填充颜色 */
	public void setColor(String color){
		this.color=color;
	}
	/** 取得：区域起始位置 */
	public String getOrigin(){
		return origin;
	}
	/** 设置：区域起始位置 */
	public void setOrigin(String origin){
		this.origin=origin;
	}
	/** 取得：图形透明度 */
	public BigDecimal getOpacity(){
		return opacity;
	}
	/** 设置：图形透明度 */
	public void setOpacity(BigDecimal opacity){
		this.opacity=opacity;
	}
	/** 设置：图形透明度 */
	public void setOpacity(String opacity){
		if(!fd.ng.core.utils.StringUtil.isEmpty(opacity)){
			this.opacity=new BigDecimal(opacity);
		}
	}
	/** 取得：阴影模糊大小 */
	public Integer getShadowblur(){
		return shadowblur;
	}
	/** 设置：阴影模糊大小 */
	public void setShadowblur(Integer shadowblur){
		this.shadowblur=shadowblur;
	}
	/** 设置：阴影模糊大小 */
	public void setShadowblur(String shadowblur){
		if(!fd.ng.core.utils.StringUtil.isEmpty(shadowblur)){
			this.shadowblur=new Integer(shadowblur);
		}
	}
	/** 取得：阴影颜色 */
	public String getShadowcolor(){
		return shadowcolor;
	}
	/** 设置：阴影颜色 */
	public void setShadowcolor(String shadowcolor){
		this.shadowcolor=shadowcolor;
	}
	/** 取得：阴影水平偏移距离 */
	public Integer getShadowoffsetx(){
		return shadowoffsetx;
	}
	/** 设置：阴影水平偏移距离 */
	public void setShadowoffsetx(Integer shadowoffsetx){
		this.shadowoffsetx=shadowoffsetx;
	}
	/** 设置：阴影水平偏移距离 */
	public void setShadowoffsetx(String shadowoffsetx){
		if(!fd.ng.core.utils.StringUtil.isEmpty(shadowoffsetx)){
			this.shadowoffsetx=new Integer(shadowoffsetx);
		}
	}
	/** 取得：阴影垂直偏移距离 */
	public Integer getShadowoffsety(){
		return shadowoffsety;
	}
	/** 设置：阴影垂直偏移距离 */
	public void setShadowoffsety(Integer shadowoffsety){
		this.shadowoffsety=shadowoffsety;
	}
	/** 设置：阴影垂直偏移距离 */
	public void setShadowoffsety(String shadowoffsety){
		if(!fd.ng.core.utils.StringUtil.isEmpty(shadowoffsety)){
			this.shadowoffsety=new Integer(shadowoffsety);
		}
	}
	/** 取得：配置编号 */
	public Long getConfig_id(){
		return config_id;
	}
	/** 设置：配置编号 */
	public void setConfig_id(Long config_id){
		this.config_id=config_id;
	}
	/** 设置：配置编号 */
	public void setConfig_id(String config_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(config_id)){
			this.config_id=new Long(config_id);
		}
	}
}
