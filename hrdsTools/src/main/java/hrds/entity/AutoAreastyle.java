package hrds.entity;

import fd.ng.db.entity.TableEntity;
import fd.ng.db.entity.anno.Column;
import fd.ng.db.entity.anno.Table;
import hrds.exception.BusinessException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 实体类中所有属性都应定义为对象，不要使用int等主类型，方便对null值的操作
 */
@Table(tableName = "auto_areastyle")
public class AutoAreastyle extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_areastyle";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("style_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String shadowcolor;
	private String color;
	private BigDecimal config_id;
	private Integer shadowblur;
	private String origin;
	private Integer shadowoffsetx;
	private Integer shadowoffsety;
	private BigDecimal style_id;
	private BigDecimal opacity;

	public String getShadowcolor() { return shadowcolor; }
	public void setShadowcolor(String shadowcolor) {
		if(shadowcolor==null) addNullValueField("shadowcolor");
		this.shadowcolor = shadowcolor;
	}

	public String getColor() { return color; }
	public void setColor(String color) {
		if(color==null) addNullValueField("color");
		this.color = color;
	}

	public BigDecimal getConfig_id() { return config_id; }
	public void setConfig_id(BigDecimal config_id) {
		if(config_id==null) addNullValueField("config_id");
		this.config_id = config_id;
	}

	public Integer getShadowblur() { return shadowblur; }
	public void setShadowblur(Integer shadowblur) {
		if(shadowblur==null) addNullValueField("shadowblur");
		this.shadowblur = shadowblur;
	}

	public String getOrigin() { return origin; }
	public void setOrigin(String origin) {
		if(origin==null) addNullValueField("origin");
		this.origin = origin;
	}

	public Integer getShadowoffsetx() { return shadowoffsetx; }
	public void setShadowoffsetx(Integer shadowoffsetx) {
		if(shadowoffsetx==null) addNullValueField("shadowoffsetx");
		this.shadowoffsetx = shadowoffsetx;
	}

	public Integer getShadowoffsety() { return shadowoffsety; }
	public void setShadowoffsety(Integer shadowoffsety) {
		if(shadowoffsety==null) addNullValueField("shadowoffsety");
		this.shadowoffsety = shadowoffsety;
	}

	public BigDecimal getStyle_id() { return style_id; }
	public void setStyle_id(BigDecimal style_id) {
		if(style_id==null) throw new BusinessException("Entity : AutoAreastyle.style_id must not null!");
		this.style_id = style_id;
	}

	public BigDecimal getOpacity() { return opacity; }
	public void setOpacity(BigDecimal opacity) {
		if(opacity==null) addNullValueField("opacity");
		this.opacity = opacity;
	}

}