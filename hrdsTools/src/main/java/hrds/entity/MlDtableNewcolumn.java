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
@Table(tableName = "ml_dtable_newcolumn")
public class MlDtableNewcolumn extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_dtable_newcolumn";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("table_column_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String ocolumn_type;
	private String column_cn_name;
	private BigDecimal table_column_id;
	private String ocolumn_en_name;
	private String create_time;
	private BigDecimal datapo_info_id;
	private String use_isflag;
	private String newcolumn_name;
	private String create_date;

	public String getOcolumn_type() { return ocolumn_type; }
	public void setOcolumn_type(String ocolumn_type) {
		if(ocolumn_type==null) throw new BusinessException("Entity : MlDtableNewcolumn.ocolumn_type must not null!");
		this.ocolumn_type = ocolumn_type;
	}

	public String getColumn_cn_name() { return column_cn_name; }
	public void setColumn_cn_name(String column_cn_name) {
		if(column_cn_name==null) throw new BusinessException("Entity : MlDtableNewcolumn.column_cn_name must not null!");
		this.column_cn_name = column_cn_name;
	}

	public BigDecimal getTable_column_id() { return table_column_id; }
	public void setTable_column_id(BigDecimal table_column_id) {
		if(table_column_id==null) throw new BusinessException("Entity : MlDtableNewcolumn.table_column_id must not null!");
		this.table_column_id = table_column_id;
	}

	public String getOcolumn_en_name() { return ocolumn_en_name; }
	public void setOcolumn_en_name(String ocolumn_en_name) {
		if(ocolumn_en_name==null) throw new BusinessException("Entity : MlDtableNewcolumn.ocolumn_en_name must not null!");
		this.ocolumn_en_name = ocolumn_en_name;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : MlDtableNewcolumn.create_time must not null!");
		this.create_time = create_time;
	}

	public BigDecimal getDatapo_info_id() { return datapo_info_id; }
	public void setDatapo_info_id(BigDecimal datapo_info_id) {
		if(datapo_info_id==null) throw new BusinessException("Entity : MlDtableNewcolumn.datapo_info_id must not null!");
		this.datapo_info_id = datapo_info_id;
	}

	public String getUse_isflag() { return use_isflag; }
	public void setUse_isflag(String use_isflag) {
		if(use_isflag==null) throw new BusinessException("Entity : MlDtableNewcolumn.use_isflag must not null!");
		this.use_isflag = use_isflag;
	}

	public String getNewcolumn_name() { return newcolumn_name; }
	public void setNewcolumn_name(String newcolumn_name) {
		if(newcolumn_name==null) throw new BusinessException("Entity : MlDtableNewcolumn.newcolumn_name must not null!");
		this.newcolumn_name = newcolumn_name;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : MlDtableNewcolumn.create_date must not null!");
		this.create_date = create_date;
	}

}