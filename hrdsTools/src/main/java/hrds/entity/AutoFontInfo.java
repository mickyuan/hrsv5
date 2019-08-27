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
@Table(tableName = "auto_font_info")
public class AutoFontInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_font_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("font_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal font_id;
	private String color;
	private Integer lineheight;
	private String fontfamily;
	private Integer fontsize;
	private String align;
	private String font_corr_tname;
	private String backgroundcolor;
	private String bordercolor;
	private String fontstyle;
	private String fontweight;
	private Integer borderwidth;
	private Integer borderradius;
	private String verticalalign;
	private BigDecimal font_corr_id;

	public BigDecimal getFont_id() { return font_id; }
	public void setFont_id(BigDecimal font_id) {
		if(font_id==null) throw new BusinessException("Entity : AutoFontInfo.font_id must not null!");
		this.font_id = font_id;
	}

	public String getColor() { return color; }
	public void setColor(String color) {
		if(color==null) addNullValueField("color");
		this.color = color;
	}

	public Integer getLineheight() { return lineheight; }
	public void setLineheight(Integer lineheight) {
		if(lineheight==null) addNullValueField("lineheight");
		this.lineheight = lineheight;
	}

	public String getFontfamily() { return fontfamily; }
	public void setFontfamily(String fontfamily) {
		if(fontfamily==null) addNullValueField("fontfamily");
		this.fontfamily = fontfamily;
	}

	public Integer getFontsize() { return fontsize; }
	public void setFontsize(Integer fontsize) {
		if(fontsize==null) addNullValueField("fontsize");
		this.fontsize = fontsize;
	}

	public String getAlign() { return align; }
	public void setAlign(String align) {
		if(align==null) addNullValueField("align");
		this.align = align;
	}

	public String getFont_corr_tname() { return font_corr_tname; }
	public void setFont_corr_tname(String font_corr_tname) {
		if(font_corr_tname==null) throw new BusinessException("Entity : AutoFontInfo.font_corr_tname must not null!");
		this.font_corr_tname = font_corr_tname;
	}

	public String getBackgroundcolor() { return backgroundcolor; }
	public void setBackgroundcolor(String backgroundcolor) {
		if(backgroundcolor==null) addNullValueField("backgroundcolor");
		this.backgroundcolor = backgroundcolor;
	}

	public String getBordercolor() { return bordercolor; }
	public void setBordercolor(String bordercolor) {
		if(bordercolor==null) addNullValueField("bordercolor");
		this.bordercolor = bordercolor;
	}

	public String getFontstyle() { return fontstyle; }
	public void setFontstyle(String fontstyle) {
		if(fontstyle==null) addNullValueField("fontstyle");
		this.fontstyle = fontstyle;
	}

	public String getFontweight() { return fontweight; }
	public void setFontweight(String fontweight) {
		if(fontweight==null) addNullValueField("fontweight");
		this.fontweight = fontweight;
	}

	public Integer getBorderwidth() { return borderwidth; }
	public void setBorderwidth(Integer borderwidth) {
		if(borderwidth==null) addNullValueField("borderwidth");
		this.borderwidth = borderwidth;
	}

	public Integer getBorderradius() { return borderradius; }
	public void setBorderradius(Integer borderradius) {
		if(borderradius==null) addNullValueField("borderradius");
		this.borderradius = borderradius;
	}

	public String getVerticalalign() { return verticalalign; }
	public void setVerticalalign(String verticalalign) {
		if(verticalalign==null) addNullValueField("verticalalign");
		this.verticalalign = verticalalign;
	}

	public BigDecimal getFont_corr_id() { return font_corr_id; }
	public void setFont_corr_id(BigDecimal font_corr_id) {
		if(font_corr_id==null) throw new BusinessException("Entity : AutoFontInfo.font_corr_id must not null!");
		this.font_corr_id = font_corr_id;
	}

}