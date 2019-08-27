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
@Table(tableName = "auto_axisline_info")
public class AutoAxislineInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_axisline_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("axisline_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String show;
	private String symbol;
	private Integer symboloffset;
	private BigDecimal axisline_id;
	private String onzero;
	private BigDecimal axis_id;

	public String getShow() { return show; }
	public void setShow(String show) {
		if(show==null) throw new BusinessException("Entity : AutoAxislineInfo.show must not null!");
		this.show = show;
	}

	public String getSymbol() { return symbol; }
	public void setSymbol(String symbol) {
		if(symbol==null) addNullValueField("symbol");
		this.symbol = symbol;
	}

	public Integer getSymboloffset() { return symboloffset; }
	public void setSymboloffset(Integer symboloffset) {
		if(symboloffset==null) addNullValueField("symboloffset");
		this.symboloffset = symboloffset;
	}

	public BigDecimal getAxisline_id() { return axisline_id; }
	public void setAxisline_id(BigDecimal axisline_id) {
		if(axisline_id==null) throw new BusinessException("Entity : AutoAxislineInfo.axisline_id must not null!");
		this.axisline_id = axisline_id;
	}

	public String getOnzero() { return onzero; }
	public void setOnzero(String onzero) {
		if(onzero==null) addNullValueField("onzero");
		this.onzero = onzero;
	}

	public BigDecimal getAxis_id() { return axis_id; }
	public void setAxis_id(BigDecimal axis_id) {
		if(axis_id==null) addNullValueField("axis_id");
		this.axis_id = axis_id;
	}

}