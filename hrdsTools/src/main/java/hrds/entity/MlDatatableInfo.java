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
@Table(tableName = "ml_datatable_info")
public class MlDatatableInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_datatable_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("dtable_info_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String file_path;
	private String run_end_date;
	private String dstorage_mode;
	private String dtable_cdate;
	private String program_path;
	private String run_time;
	private String data_load_mode;
	private String stable_cn_name;
	private BigDecimal dtable_info_id;
	private String remark;
	private String run_end_time;
	private BigDecimal file_size;
	private String dtable_runstate;
	private BigDecimal project_id;
	private String stable_en_name;
	private String dtable_ctime;
	private String datamapmode;
	private String run_date;

	public String getFile_path() { return file_path; }
	public void setFile_path(String file_path) {
		if(file_path==null) addNullValueField("file_path");
		this.file_path = file_path;
	}

	public String getRun_end_date() { return run_end_date; }
	public void setRun_end_date(String run_end_date) {
		if(run_end_date==null) addNullValueField("run_end_date");
		this.run_end_date = run_end_date;
	}

	public String getDstorage_mode() { return dstorage_mode; }
	public void setDstorage_mode(String dstorage_mode) {
		if(dstorage_mode==null) throw new BusinessException("Entity : MlDatatableInfo.dstorage_mode must not null!");
		this.dstorage_mode = dstorage_mode;
	}

	public String getDtable_cdate() { return dtable_cdate; }
	public void setDtable_cdate(String dtable_cdate) {
		if(dtable_cdate==null) throw new BusinessException("Entity : MlDatatableInfo.dtable_cdate must not null!");
		this.dtable_cdate = dtable_cdate;
	}

	public String getProgram_path() { return program_path; }
	public void setProgram_path(String program_path) {
		if(program_path==null) addNullValueField("program_path");
		this.program_path = program_path;
	}

	public String getRun_time() { return run_time; }
	public void setRun_time(String run_time) {
		if(run_time==null) addNullValueField("run_time");
		this.run_time = run_time;
	}

	public String getData_load_mode() { return data_load_mode; }
	public void setData_load_mode(String data_load_mode) {
		if(data_load_mode==null) throw new BusinessException("Entity : MlDatatableInfo.data_load_mode must not null!");
		this.data_load_mode = data_load_mode;
	}

	public String getStable_cn_name() { return stable_cn_name; }
	public void setStable_cn_name(String stable_cn_name) {
		if(stable_cn_name==null) throw new BusinessException("Entity : MlDatatableInfo.stable_cn_name must not null!");
		this.stable_cn_name = stable_cn_name;
	}

	public BigDecimal getDtable_info_id() { return dtable_info_id; }
	public void setDtable_info_id(BigDecimal dtable_info_id) {
		if(dtable_info_id==null) throw new BusinessException("Entity : MlDatatableInfo.dtable_info_id must not null!");
		this.dtable_info_id = dtable_info_id;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public String getRun_end_time() { return run_end_time; }
	public void setRun_end_time(String run_end_time) {
		if(run_end_time==null) addNullValueField("run_end_time");
		this.run_end_time = run_end_time;
	}

	public BigDecimal getFile_size() { return file_size; }
	public void setFile_size(BigDecimal file_size) {
		if(file_size==null) addNullValueField("file_size");
		this.file_size = file_size;
	}

	public String getDtable_runstate() { return dtable_runstate; }
	public void setDtable_runstate(String dtable_runstate) {
		if(dtable_runstate==null) throw new BusinessException("Entity : MlDatatableInfo.dtable_runstate must not null!");
		this.dtable_runstate = dtable_runstate;
	}

	public BigDecimal getProject_id() { return project_id; }
	public void setProject_id(BigDecimal project_id) {
		if(project_id==null) addNullValueField("project_id");
		this.project_id = project_id;
	}

	public String getStable_en_name() { return stable_en_name; }
	public void setStable_en_name(String stable_en_name) {
		if(stable_en_name==null) throw new BusinessException("Entity : MlDatatableInfo.stable_en_name must not null!");
		this.stable_en_name = stable_en_name;
	}

	public String getDtable_ctime() { return dtable_ctime; }
	public void setDtable_ctime(String dtable_ctime) {
		if(dtable_ctime==null) throw new BusinessException("Entity : MlDatatableInfo.dtable_ctime must not null!");
		this.dtable_ctime = dtable_ctime;
	}

	public String getDatamapmode() { return datamapmode; }
	public void setDatamapmode(String datamapmode) {
		if(datamapmode==null) throw new BusinessException("Entity : MlDatatableInfo.datamapmode must not null!");
		this.datamapmode = datamapmode;
	}

	public String getRun_date() { return run_date; }
	public void setRun_date(String run_date) {
		if(run_date==null) addNullValueField("run_date");
		this.run_date = run_date;
	}

}