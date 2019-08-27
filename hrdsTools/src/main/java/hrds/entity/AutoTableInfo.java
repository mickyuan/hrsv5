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
@Table(tableName = "auto_table_info")
public class AutoTableInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_table_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("config_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String th_background;
	private String zl_background;
	private BigDecimal component_id;
	private BigDecimal config_id;
	private String is_gridline;
	private String is_zebraline;

	public String getTh_background() { return th_background; }
	public void setTh_background(String th_background) {
		if(th_background==null) addNullValueField("th_background");
		this.th_background = th_background;
	}

	public String getZl_background() { return zl_background; }
	public void setZl_background(String zl_background) {
		if(zl_background==null) addNullValueField("zl_background");
		this.zl_background = zl_background;
	}

	public BigDecimal getComponent_id() { return component_id; }
	public void setComponent_id(BigDecimal component_id) {
		if(component_id==null) addNullValueField("component_id");
		this.component_id = component_id;
	}

	public BigDecimal getConfig_id() { return config_id; }
	public void setConfig_id(BigDecimal config_id) {
		if(config_id==null) throw new BusinessException("Entity : AutoTableInfo.config_id must not null!");
		this.config_id = config_id;
	}

	public String getIs_gridline() { return is_gridline; }
	public void setIs_gridline(String is_gridline) {
		if(is_gridline==null) throw new BusinessException("Entity : AutoTableInfo.is_gridline must not null!");
		this.is_gridline = is_gridline;
	}

	public String getIs_zebraline() { return is_zebraline; }
	public void setIs_zebraline(String is_zebraline) {
		if(is_zebraline==null) throw new BusinessException("Entity : AutoTableInfo.is_zebraline must not null!");
		this.is_zebraline = is_zebraline;
	}

}