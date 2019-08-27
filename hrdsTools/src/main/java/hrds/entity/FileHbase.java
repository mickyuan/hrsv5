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
@Table(tableName = "file_hbase")
public class FileHbase extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "file_hbase";

	static {
		__PrimaryKeys = Collections.emptySet();
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String rowkey;
	private String hyren_e_date;
	private String file_md5;
	private String file_avro_path;
	private String hyren_s_date;
	private BigDecimal file_avro_block;

	public String getRowkey() { return rowkey; }
	public void setRowkey(String rowkey) {
		if(rowkey==null) addNullValueField("rowkey");
		this.rowkey = rowkey;
	}

	public String getHyren_e_date() { return hyren_e_date; }
	public void setHyren_e_date(String hyren_e_date) {
		if(hyren_e_date==null) addNullValueField("hyren_e_date");
		this.hyren_e_date = hyren_e_date;
	}

	public String getFile_md5() { return file_md5; }
	public void setFile_md5(String file_md5) {
		if(file_md5==null) addNullValueField("file_md5");
		this.file_md5 = file_md5;
	}

	public String getFile_avro_path() { return file_avro_path; }
	public void setFile_avro_path(String file_avro_path) {
		if(file_avro_path==null) addNullValueField("file_avro_path");
		this.file_avro_path = file_avro_path;
	}

	public String getHyren_s_date() { return hyren_s_date; }
	public void setHyren_s_date(String hyren_s_date) {
		if(hyren_s_date==null) addNullValueField("hyren_s_date");
		this.hyren_s_date = hyren_s_date;
	}

	public BigDecimal getFile_avro_block() { return file_avro_block; }
	public void setFile_avro_block(BigDecimal file_avro_block) {
		if(file_avro_block==null) addNullValueField("file_avro_block");
		this.file_avro_block = file_avro_block;
	}

}