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
 * 图表配置区域样式信息表
 */
@Table(tableName = "auto_areastyle")
public class Auto_areastyle extends ProjectTableEntity
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
	@DocBean(name ="style_id",value="样式编号:",dataType = Long.class,required = true)
	private Long style_id;
	@DocBean(name ="color",value="填充颜色:",dataType = String.class,required = false)
	private String color;
	@DocBean(name ="origin",value="区域起始位置:",dataType = String.class,required = false)
	private String origin;
	@DocBean(name ="opacity",value="图形透明度:",dataType = BigDecimal.class,required = true)
	private BigDecimal opacity;
	@DocBean(name ="shadowblur",value="阴影模糊大小:",dataType = Long.class,required = true)
	private Long shadowblur;
	@DocBean(name ="shadowcolor",value="阴影颜色:",dataType = String.class,required = false)
	private String shadowcolor;
	@DocBean(name ="shadowoffsetx",value="阴影水平偏移距离:",dataType = Long.class,required = true)
	private Long shadowoffsetx;
	@DocBean(name ="shadowoffsety",value="阴影垂直偏移距离:",dataType = Long.class,required = true)
	private Long shadowoffsety;
	@DocBean(name ="config_id",value="配置编号:",dataType = Long.class,required = false)
	private Long config_id;

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
	public Long getShadowblur(){
		return shadowblur;
	}
	/** 设置：阴影模糊大小 */
	public void setShadowblur(Long shadowblur){
		this.shadowblur=shadowblur;
	}
	/** 设置：阴影模糊大小 */
	public void setShadowblur(String shadowblur){
		if(!fd.ng.core.utils.StringUtil.isEmpty(shadowblur)){
			this.shadowblur=new Long(shadowblur);
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
	public Long getShadowoffsetx(){
		return shadowoffsetx;
	}
	/** 设置：阴影水平偏移距离 */
	public void setShadowoffsetx(Long shadowoffsetx){
		this.shadowoffsetx=shadowoffsetx;
	}
	/** 设置：阴影水平偏移距离 */
	public void setShadowoffsetx(String shadowoffsetx){
		if(!fd.ng.core.utils.StringUtil.isEmpty(shadowoffsetx)){
			this.shadowoffsetx=new Long(shadowoffsetx);
		}
	}
	/** 取得：阴影垂直偏移距离 */
	public Long getShadowoffsety(){
		return shadowoffsety;
	}
	/** 设置：阴影垂直偏移距离 */
	public void setShadowoffsety(Long shadowoffsety){
		this.shadowoffsety=shadowoffsety;
	}
	/** 设置：阴影垂直偏移距离 */
	public void setShadowoffsety(String shadowoffsety){
		if(!fd.ng.core.utils.StringUtil.isEmpty(shadowoffsety)){
			this.shadowoffsety=new Long(shadowoffsety);
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
