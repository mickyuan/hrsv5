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
@Table(tableName = "auto_comp_style_attr")
public class AutoCompStyleAttr extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_comp_style_attr";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("component_style_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal component_id;
	private String vertical_grid_line;
	private String legend;
	private BigDecimal component_style_id;
	private String title;
	private String axis;
	private String horizontal_grid_line;

	public BigDecimal getComponent_id() { return component_id; }
	public void setComponent_id(BigDecimal component_id) {
		if(component_id==null) addNullValueField("component_id");
		this.component_id = component_id;
	}

	public String getVertical_grid_line() { return vertical_grid_line; }
	public void setVertical_grid_line(String vertical_grid_line) {
		if(vertical_grid_line==null) addNullValueField("vertical_grid_line");
		this.vertical_grid_line = vertical_grid_line;
	}

	public String getLegend() { return legend; }
	public void setLegend(String legend) {
		if(legend==null) addNullValueField("legend");
		this.legend = legend;
	}

	public BigDecimal getComponent_style_id() { return component_style_id; }
	public void setComponent_style_id(BigDecimal component_style_id) {
		if(component_style_id==null) throw new BusinessException("Entity : AutoCompStyleAttr.component_style_id must not null!");
		this.component_style_id = component_style_id;
	}

	public String getTitle() { return title; }
	public void setTitle(String title) {
		if(title==null) addNullValueField("title");
		this.title = title;
	}

	public String getAxis() { return axis; }
	public void setAxis(String axis) {
		if(axis==null) addNullValueField("axis");
		this.axis = axis;
	}

	public String getHorizontal_grid_line() { return horizontal_grid_line; }
	public void setHorizontal_grid_line(String horizontal_grid_line) {
		if(horizontal_grid_line==null) addNullValueField("horizontal_grid_line");
		this.horizontal_grid_line = horizontal_grid_line;
	}

}