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
@Table(tableName = "ml_data_exception")
public class MlDataException extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_data_exception";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("dataexce_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal dataexce_id;
	private String create_time;
	private BigDecimal standard_devi;
	private String dataexce_column;
	private String remark;
	private BigDecimal dtable_info_id;
	private BigDecimal fixed_quantity;
	private BigDecimal max_condition;
	private String newcolumn_name;
	private BigDecimal replace_percent;
	private BigDecimal min_condition;
	private String create_date;
	private String identify_cond;
	private String replacement;
	private String process_mode;

	public BigDecimal getDataexce_id() { return dataexce_id; }
	public void setDataexce_id(BigDecimal dataexce_id) {
		if(dataexce_id==null) throw new BusinessException("Entity : MlDataException.dataexce_id must not null!");
		this.dataexce_id = dataexce_id;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : MlDataException.create_time must not null!");
		this.create_time = create_time;
	}

	public BigDecimal getStandard_devi() { return standard_devi; }
	public void setStandard_devi(BigDecimal standard_devi) {
		if(standard_devi==null) addNullValueField("standard_devi");
		this.standard_devi = standard_devi;
	}

	public String getDataexce_column() { return dataexce_column; }
	public void setDataexce_column(String dataexce_column) {
		if(dataexce_column==null) throw new BusinessException("Entity : MlDataException.dataexce_column must not null!");
		this.dataexce_column = dataexce_column;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public BigDecimal getDtable_info_id() { return dtable_info_id; }
	public void setDtable_info_id(BigDecimal dtable_info_id) {
		if(dtable_info_id==null) throw new BusinessException("Entity : MlDataException.dtable_info_id must not null!");
		this.dtable_info_id = dtable_info_id;
	}

	public BigDecimal getFixed_quantity() { return fixed_quantity; }
	public void setFixed_quantity(BigDecimal fixed_quantity) {
		if(fixed_quantity==null) addNullValueField("fixed_quantity");
		this.fixed_quantity = fixed_quantity;
	}

	public BigDecimal getMax_condition() { return max_condition; }
	public void setMax_condition(BigDecimal max_condition) {
		if(max_condition==null) addNullValueField("max_condition");
		this.max_condition = max_condition;
	}

	public String getNewcolumn_name() { return newcolumn_name; }
	public void setNewcolumn_name(String newcolumn_name) {
		if(newcolumn_name==null) throw new BusinessException("Entity : MlDataException.newcolumn_name must not null!");
		this.newcolumn_name = newcolumn_name;
	}

	public BigDecimal getReplace_percent() { return replace_percent; }
	public void setReplace_percent(BigDecimal replace_percent) {
		if(replace_percent==null) addNullValueField("replace_percent");
		this.replace_percent = replace_percent;
	}

	public BigDecimal getMin_condition() { return min_condition; }
	public void setMin_condition(BigDecimal min_condition) {
		if(min_condition==null) addNullValueField("min_condition");
		this.min_condition = min_condition;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : MlDataException.create_date must not null!");
		this.create_date = create_date;
	}

	public String getIdentify_cond() { return identify_cond; }
	public void setIdentify_cond(String identify_cond) {
		if(identify_cond==null) throw new BusinessException("Entity : MlDataException.identify_cond must not null!");
		this.identify_cond = identify_cond;
	}

	public String getReplacement() { return replacement; }
	public void setReplacement(String replacement) {
		if(replacement==null) addNullValueField("replacement");
		this.replacement = replacement;
	}

	public String getProcess_mode() { return process_mode; }
	public void setProcess_mode(String process_mode) {
		if(process_mode==null) throw new BusinessException("Entity : MlDataException.process_mode must not null!");
		this.process_mode = process_mode;
	}

}