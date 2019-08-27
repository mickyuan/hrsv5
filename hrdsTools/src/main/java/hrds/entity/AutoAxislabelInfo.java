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
@Table(tableName = "auto_axislabel_info")
public class AutoAxislabelInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_axislabel_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("lable_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String formatter;
	private Integer rotate;
	private Integer margin;
	private BigDecimal lable_id;
	private BigDecimal axis_id;
	private String show;
	private String inside;

	public String getFormatter() { return formatter; }
	public void setFormatter(String formatter) {
		if(formatter==null) addNullValueField("formatter");
		this.formatter = formatter;
	}

	public Integer getRotate() { return rotate; }
	public void setRotate(Integer rotate) {
		if(rotate==null) addNullValueField("rotate");
		this.rotate = rotate;
	}

	public Integer getMargin() { return margin; }
	public void setMargin(Integer margin) {
		if(margin==null) addNullValueField("margin");
		this.margin = margin;
	}

	public BigDecimal getLable_id() { return lable_id; }
	public void setLable_id(BigDecimal lable_id) {
		if(lable_id==null) throw new BusinessException("Entity : AutoAxislabelInfo.lable_id must not null!");
		this.lable_id = lable_id;
	}

	public BigDecimal getAxis_id() { return axis_id; }
	public void setAxis_id(BigDecimal axis_id) {
		if(axis_id==null) addNullValueField("axis_id");
		this.axis_id = axis_id;
	}

	public String getShow() { return show; }
	public void setShow(String show) {
		if(show==null) throw new BusinessException("Entity : AutoAxislabelInfo.show must not null!");
		this.show = show;
	}

	public String getInside() { return inside; }
	public void setInside(String inside) {
		if(inside==null) addNullValueField("inside");
		this.inside = inside;
	}

}