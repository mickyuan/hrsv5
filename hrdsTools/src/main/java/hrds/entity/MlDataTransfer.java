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
@Table(tableName = "ml_data_transfer")
public class MlDataTransfer extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_data_transfer";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("datatran_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String datatran_column;
	private String create_time;
	private BigDecimal datatran_id;
	private String newcolumn_name;
	private String remark;
	private BigDecimal dtable_info_id;
	private BigDecimal funmap_id;
	private String create_date;

	public String getDatatran_column() { return datatran_column; }
	public void setDatatran_column(String datatran_column) {
		if(datatran_column==null) throw new BusinessException("Entity : MlDataTransfer.datatran_column must not null!");
		this.datatran_column = datatran_column;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : MlDataTransfer.create_time must not null!");
		this.create_time = create_time;
	}

	public BigDecimal getDatatran_id() { return datatran_id; }
	public void setDatatran_id(BigDecimal datatran_id) {
		if(datatran_id==null) throw new BusinessException("Entity : MlDataTransfer.datatran_id must not null!");
		this.datatran_id = datatran_id;
	}

	public String getNewcolumn_name() { return newcolumn_name; }
	public void setNewcolumn_name(String newcolumn_name) {
		if(newcolumn_name==null) throw new BusinessException("Entity : MlDataTransfer.newcolumn_name must not null!");
		this.newcolumn_name = newcolumn_name;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public BigDecimal getDtable_info_id() { return dtable_info_id; }
	public void setDtable_info_id(BigDecimal dtable_info_id) {
		if(dtable_info_id==null) throw new BusinessException("Entity : MlDataTransfer.dtable_info_id must not null!");
		this.dtable_info_id = dtable_info_id;
	}

	public BigDecimal getFunmap_id() { return funmap_id; }
	public void setFunmap_id(BigDecimal funmap_id) {
		if(funmap_id==null) throw new BusinessException("Entity : MlDataTransfer.funmap_id must not null!");
		this.funmap_id = funmap_id;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : MlDataTransfer.create_date must not null!");
		this.create_date = create_date;
	}

}