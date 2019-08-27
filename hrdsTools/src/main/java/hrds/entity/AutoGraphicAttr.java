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
@Table(tableName = "auto_graphic_attr")
public class AutoGraphicAttr extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_graphic_attr";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("graphic_attr_id");
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
	private String color;
	private Integer size;
	private String connection;
	private String label;
	private BigDecimal graphic_attr_id;
	private String prompt;

	public BigDecimal getComponent_id() { return component_id; }
	public void setComponent_id(BigDecimal component_id) {
		if(component_id==null) addNullValueField("component_id");
		this.component_id = component_id;
	}

	public String getColor() { return color; }
	public void setColor(String color) {
		if(color==null) addNullValueField("color");
		this.color = color;
	}

	public Integer getSize() { return size; }
	public void setSize(Integer size) {
		if(size==null) addNullValueField("size");
		this.size = size;
	}

	public String getConnection() { return connection; }
	public void setConnection(String connection) {
		if(connection==null) addNullValueField("connection");
		this.connection = connection;
	}

	public String getLabel() { return label; }
	public void setLabel(String label) {
		if(label==null) addNullValueField("label");
		this.label = label;
	}

	public BigDecimal getGraphic_attr_id() { return graphic_attr_id; }
	public void setGraphic_attr_id(BigDecimal graphic_attr_id) {
		if(graphic_attr_id==null) throw new BusinessException("Entity : AutoGraphicAttr.graphic_attr_id must not null!");
		this.graphic_attr_id = graphic_attr_id;
	}

	public String getPrompt() { return prompt; }
	public void setPrompt(String prompt) {
		if(prompt==null) addNullValueField("prompt");
		this.prompt = prompt;
	}

}