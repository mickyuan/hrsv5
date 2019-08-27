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
@Table(tableName = "auto_axis_info")
public class AutoAxisInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_axis_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("axis_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String silent;
	private BigDecimal component_id;
	private Integer max;
	private BigDecimal axis_id;
	private String show;
	private Integer namegap;
	private Integer namerotate;
	private String axis_type;
	private String namelocation;
	private Integer axisoffset;
	private Integer min;
	private String name;
	private String position;

	public String getSilent() { return silent; }
	public void setSilent(String silent) {
		if(silent==null) addNullValueField("silent");
		this.silent = silent;
	}

	public BigDecimal getComponent_id() { return component_id; }
	public void setComponent_id(BigDecimal component_id) {
		if(component_id==null) throw new BusinessException("Entity : AutoAxisInfo.component_id must not null!");
		this.component_id = component_id;
	}

	public Integer getMax() { return max; }
	public void setMax(Integer max) {
		if(max==null) addNullValueField("max");
		this.max = max;
	}

	public BigDecimal getAxis_id() { return axis_id; }
	public void setAxis_id(BigDecimal axis_id) {
		if(axis_id==null) throw new BusinessException("Entity : AutoAxisInfo.axis_id must not null!");
		this.axis_id = axis_id;
	}

	public String getShow() { return show; }
	public void setShow(String show) {
		if(show==null) throw new BusinessException("Entity : AutoAxisInfo.show must not null!");
		this.show = show;
	}

	public Integer getNamegap() { return namegap; }
	public void setNamegap(Integer namegap) {
		if(namegap==null) addNullValueField("namegap");
		this.namegap = namegap;
	}

	public Integer getNamerotate() { return namerotate; }
	public void setNamerotate(Integer namerotate) {
		if(namerotate==null) addNullValueField("namerotate");
		this.namerotate = namerotate;
	}

	public String getAxis_type() { return axis_type; }
	public void setAxis_type(String axis_type) {
		if(axis_type==null) throw new BusinessException("Entity : AutoAxisInfo.axis_type must not null!");
		this.axis_type = axis_type;
	}

	public String getNamelocation() { return namelocation; }
	public void setNamelocation(String namelocation) {
		if(namelocation==null) addNullValueField("namelocation");
		this.namelocation = namelocation;
	}

	public Integer getAxisoffset() { return axisoffset; }
	public void setAxisoffset(Integer axisoffset) {
		if(axisoffset==null) addNullValueField("axisoffset");
		this.axisoffset = axisoffset;
	}

	public Integer getMin() { return min; }
	public void setMin(Integer min) {
		if(min==null) addNullValueField("min");
		this.min = min;
	}

	public String getName() { return name; }
	public void setName(String name) {
		if(name==null) addNullValueField("name");
		this.name = name;
	}

	public String getPosition() { return position; }
	public void setPosition(String position) {
		if(position==null) addNullValueField("position");
		this.position = position;
	}

}