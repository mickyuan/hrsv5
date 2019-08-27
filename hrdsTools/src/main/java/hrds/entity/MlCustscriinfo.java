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
@Table(tableName = "ml_custscriinfo")
public class MlCustscriinfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_custscriinfo";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("script_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String new_tablename;
	private String update_time;
	private BigDecimal new_filesize;
	private String generate_is_flag;
	private BigDecimal script_id;
	private String script_path;
	private BigDecimal dtable_info_id;
	private String new_filepath;
	private String datamapmode;
	private String update_date;
	private String current_step;

	public String getNew_tablename() { return new_tablename; }
	public void setNew_tablename(String new_tablename) {
		if(new_tablename==null) throw new BusinessException("Entity : MlCustscriinfo.new_tablename must not null!");
		this.new_tablename = new_tablename;
	}

	public String getUpdate_time() { return update_time; }
	public void setUpdate_time(String update_time) {
		if(update_time==null) throw new BusinessException("Entity : MlCustscriinfo.update_time must not null!");
		this.update_time = update_time;
	}

	public BigDecimal getNew_filesize() { return new_filesize; }
	public void setNew_filesize(BigDecimal new_filesize) {
		if(new_filesize==null) throw new BusinessException("Entity : MlCustscriinfo.new_filesize must not null!");
		this.new_filesize = new_filesize;
	}

	public String getGenerate_is_flag() { return generate_is_flag; }
	public void setGenerate_is_flag(String generate_is_flag) {
		if(generate_is_flag==null) throw new BusinessException("Entity : MlCustscriinfo.generate_is_flag must not null!");
		this.generate_is_flag = generate_is_flag;
	}

	public BigDecimal getScript_id() { return script_id; }
	public void setScript_id(BigDecimal script_id) {
		if(script_id==null) throw new BusinessException("Entity : MlCustscriinfo.script_id must not null!");
		this.script_id = script_id;
	}

	public String getScript_path() { return script_path; }
	public void setScript_path(String script_path) {
		if(script_path==null) throw new BusinessException("Entity : MlCustscriinfo.script_path must not null!");
		this.script_path = script_path;
	}

	public BigDecimal getDtable_info_id() { return dtable_info_id; }
	public void setDtable_info_id(BigDecimal dtable_info_id) {
		if(dtable_info_id==null) throw new BusinessException("Entity : MlCustscriinfo.dtable_info_id must not null!");
		this.dtable_info_id = dtable_info_id;
	}

	public String getNew_filepath() { return new_filepath; }
	public void setNew_filepath(String new_filepath) {
		if(new_filepath==null) throw new BusinessException("Entity : MlCustscriinfo.new_filepath must not null!");
		this.new_filepath = new_filepath;
	}

	public String getDatamapmode() { return datamapmode; }
	public void setDatamapmode(String datamapmode) {
		if(datamapmode==null) throw new BusinessException("Entity : MlCustscriinfo.datamapmode must not null!");
		this.datamapmode = datamapmode;
	}

	public String getUpdate_date() { return update_date; }
	public void setUpdate_date(String update_date) {
		if(update_date==null) throw new BusinessException("Entity : MlCustscriinfo.update_date must not null!");
		this.update_date = update_date;
	}

	public String getCurrent_step() { return current_step; }
	public void setCurrent_step(String current_step) {
		if(current_step==null) throw new BusinessException("Entity : MlCustscriinfo.current_step must not null!");
		this.current_step = current_step;
	}

}