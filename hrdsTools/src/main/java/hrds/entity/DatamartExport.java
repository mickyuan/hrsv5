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
@Table(tableName = "datamart_export")
public class DatamartExport extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "datamart_export";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("export_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String file_code;
	private String filling_char;
	private String is_info;
	private BigDecimal datatable_id;
	private String storage_path;
	private String filling_type;
	private String file_hbase;
	private String reduce_type;
	private String remark;
	private BigDecimal export_id;
	private String datafile_separator;
	private String is_fixed;

	public String getFile_code() { return file_code; }
	public void setFile_code(String file_code) {
		if(file_code==null) addNullValueField("file_code");
		this.file_code = file_code;
	}

	public String getFilling_char() { return filling_char; }
	public void setFilling_char(String filling_char) {
		if(filling_char==null) addNullValueField("filling_char");
		this.filling_char = filling_char;
	}

	public String getIs_info() { return is_info; }
	public void setIs_info(String is_info) {
		if(is_info==null) throw new BusinessException("Entity : DatamartExport.is_info must not null!");
		this.is_info = is_info;
	}

	public BigDecimal getDatatable_id() { return datatable_id; }
	public void setDatatable_id(BigDecimal datatable_id) {
		if(datatable_id==null) addNullValueField("datatable_id");
		this.datatable_id = datatable_id;
	}

	public String getStorage_path() { return storage_path; }
	public void setStorage_path(String storage_path) {
		if(storage_path==null) addNullValueField("storage_path");
		this.storage_path = storage_path;
	}

	public String getFilling_type() { return filling_type; }
	public void setFilling_type(String filling_type) {
		if(filling_type==null) addNullValueField("filling_type");
		this.filling_type = filling_type;
	}

	public String getFile_hbase() { return file_hbase; }
	public void setFile_hbase(String file_hbase) {
		if(file_hbase==null) addNullValueField("file_hbase");
		this.file_hbase = file_hbase;
	}

	public String getReduce_type() { return reduce_type; }
	public void setReduce_type(String reduce_type) {
		if(reduce_type==null) addNullValueField("reduce_type");
		this.reduce_type = reduce_type;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public BigDecimal getExport_id() { return export_id; }
	public void setExport_id(BigDecimal export_id) {
		if(export_id==null) throw new BusinessException("Entity : DatamartExport.export_id must not null!");
		this.export_id = export_id;
	}

	public String getDatafile_separator() { return datafile_separator; }
	public void setDatafile_separator(String datafile_separator) {
		if(datafile_separator==null) addNullValueField("datafile_separator");
		this.datafile_separator = datafile_separator;
	}

	public String getIs_fixed() { return is_fixed; }
	public void setIs_fixed(String is_fixed) {
		if(is_fixed==null) throw new BusinessException("Entity : DatamartExport.is_fixed must not null!");
		this.is_fixed = is_fixed;
	}

}