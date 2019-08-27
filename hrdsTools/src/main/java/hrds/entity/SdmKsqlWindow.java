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
@Table(tableName = "sdm_ksql_window")
public class SdmKsqlWindow extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_ksql_window";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("sdm_win_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String window_type;
	private BigDecimal window_size;
	private String window_remark;
	private BigDecimal sdm_win_id;
	private BigDecimal advance_interval;
	private BigDecimal sdm_ksql_id;

	public String getWindow_type() { return window_type; }
	public void setWindow_type(String window_type) {
		if(window_type==null) addNullValueField("window_type");
		this.window_type = window_type;
	}

	public BigDecimal getWindow_size() { return window_size; }
	public void setWindow_size(BigDecimal window_size) {
		if(window_size==null) throw new BusinessException("Entity : SdmKsqlWindow.window_size must not null!");
		this.window_size = window_size;
	}

	public String getWindow_remark() { return window_remark; }
	public void setWindow_remark(String window_remark) {
		if(window_remark==null) addNullValueField("window_remark");
		this.window_remark = window_remark;
	}

	public BigDecimal getSdm_win_id() { return sdm_win_id; }
	public void setSdm_win_id(BigDecimal sdm_win_id) {
		if(sdm_win_id==null) throw new BusinessException("Entity : SdmKsqlWindow.sdm_win_id must not null!");
		this.sdm_win_id = sdm_win_id;
	}

	public BigDecimal getAdvance_interval() { return advance_interval; }
	public void setAdvance_interval(BigDecimal advance_interval) {
		if(advance_interval==null) throw new BusinessException("Entity : SdmKsqlWindow.advance_interval must not null!");
		this.advance_interval = advance_interval;
	}

	public BigDecimal getSdm_ksql_id() { return sdm_ksql_id; }
	public void setSdm_ksql_id(BigDecimal sdm_ksql_id) {
		if(sdm_ksql_id==null) throw new BusinessException("Entity : SdmKsqlWindow.sdm_ksql_id must not null!");
		this.sdm_ksql_id = sdm_ksql_id;
	}

}