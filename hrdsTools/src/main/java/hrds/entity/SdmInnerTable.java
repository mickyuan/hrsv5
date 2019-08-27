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
@Table(tableName = "sdm_inner_table")
public class SdmInnerTable extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_inner_table";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("table_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String create_time;
	private String execute_state;
	private BigDecimal user_id;
	private String remark;
	private BigDecimal table_id;
	private String table_cn_name;
	private String create_date;
	private String hyren_consumedes;
	private String table_en_name;

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : SdmInnerTable.create_time must not null!");
		this.create_time = create_time;
	}

	public String getExecute_state() { return execute_state; }
	public void setExecute_state(String execute_state) {
		if(execute_state==null) addNullValueField("execute_state");
		this.execute_state = execute_state;
	}

	public BigDecimal getUser_id() { return user_id; }
	public void setUser_id(BigDecimal user_id) {
		if(user_id==null) throw new BusinessException("Entity : SdmInnerTable.user_id must not null!");
		this.user_id = user_id;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public BigDecimal getTable_id() { return table_id; }
	public void setTable_id(BigDecimal table_id) {
		if(table_id==null) throw new BusinessException("Entity : SdmInnerTable.table_id must not null!");
		this.table_id = table_id;
	}

	public String getTable_cn_name() { return table_cn_name; }
	public void setTable_cn_name(String table_cn_name) {
		if(table_cn_name==null) addNullValueField("table_cn_name");
		this.table_cn_name = table_cn_name;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : SdmInnerTable.create_date must not null!");
		this.create_date = create_date;
	}

	public String getHyren_consumedes() { return hyren_consumedes; }
	public void setHyren_consumedes(String hyren_consumedes) {
		if(hyren_consumedes==null) throw new BusinessException("Entity : SdmInnerTable.hyren_consumedes must not null!");
		this.hyren_consumedes = hyren_consumedes;
	}

	public String getTable_en_name() { return table_en_name; }
	public void setTable_en_name(String table_en_name) {
		if(table_en_name==null) throw new BusinessException("Entity : SdmInnerTable.table_en_name must not null!");
		this.table_en_name = table_en_name;
	}

}