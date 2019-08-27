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
@Table(tableName = "edw_interface_info")
public class EdwInterfaceInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "edw_interface_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("jobcode");
		__tmpPKS.add("st_dt");
		__tmpPKS.add("st_time");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String filetype;
	private String file_path;
	private String end_dt;
	private String st_dt;
	private String st_time;
	private String file_where;
	private String flagname;
	private String col_coldel;
	private String file_to_path;
	private String filemode;
	private Integer rlength;
	private String syscode;
	private String tabname;
	private String filename;
	private String file_cod_format;
	private String jobcode;

	public String getFiletype() { return filetype; }
	public void setFiletype(String filetype) {
		if(filetype==null) addNullValueField("filetype");
		this.filetype = filetype;
	}

	public String getFile_path() { return file_path; }
	public void setFile_path(String file_path) {
		if(file_path==null) addNullValueField("file_path");
		this.file_path = file_path;
	}

	public String getEnd_dt() { return end_dt; }
	public void setEnd_dt(String end_dt) {
		if(end_dt==null) addNullValueField("end_dt");
		this.end_dt = end_dt;
	}

	public String getSt_dt() { return st_dt; }
	public void setSt_dt(String st_dt) {
		if(st_dt==null) throw new BusinessException("Entity : EdwInterfaceInfo.st_dt must not null!");
		this.st_dt = st_dt;
	}

	public String getSt_time() { return st_time; }
	public void setSt_time(String st_time) {
		if(st_time==null) throw new BusinessException("Entity : EdwInterfaceInfo.st_time must not null!");
		this.st_time = st_time;
	}

	public String getFile_where() { return file_where; }
	public void setFile_where(String file_where) {
		if(file_where==null) addNullValueField("file_where");
		this.file_where = file_where;
	}

	public String getFlagname() { return flagname; }
	public void setFlagname(String flagname) {
		if(flagname==null) addNullValueField("flagname");
		this.flagname = flagname;
	}

	public String getCol_coldel() { return col_coldel; }
	public void setCol_coldel(String col_coldel) {
		if(col_coldel==null) addNullValueField("col_coldel");
		this.col_coldel = col_coldel;
	}

	public String getFile_to_path() { return file_to_path; }
	public void setFile_to_path(String file_to_path) {
		if(file_to_path==null) addNullValueField("file_to_path");
		this.file_to_path = file_to_path;
	}

	public String getFilemode() { return filemode; }
	public void setFilemode(String filemode) {
		if(filemode==null) addNullValueField("filemode");
		this.filemode = filemode;
	}

	public Integer getRlength() { return rlength; }
	public void setRlength(Integer rlength) {
		if(rlength==null) addNullValueField("rlength");
		this.rlength = rlength;
	}

	public String getSyscode() { return syscode; }
	public void setSyscode(String syscode) {
		if(syscode==null) addNullValueField("syscode");
		this.syscode = syscode;
	}

	public String getTabname() { return tabname; }
	public void setTabname(String tabname) {
		if(tabname==null) throw new BusinessException("Entity : EdwInterfaceInfo.tabname must not null!");
		this.tabname = tabname;
	}

	public String getFilename() { return filename; }
	public void setFilename(String filename) {
		if(filename==null) addNullValueField("filename");
		this.filename = filename;
	}

	public String getFile_cod_format() { return file_cod_format; }
	public void setFile_cod_format(String file_cod_format) {
		if(file_cod_format==null) addNullValueField("file_cod_format");
		this.file_cod_format = file_cod_format;
	}

	public String getJobcode() { return jobcode; }
	public void setJobcode(String jobcode) {
		if(jobcode==null) throw new BusinessException("Entity : EdwInterfaceInfo.jobcode must not null!");
		this.jobcode = jobcode;
	}

}