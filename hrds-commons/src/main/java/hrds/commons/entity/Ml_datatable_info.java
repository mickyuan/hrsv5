package hrds.commons.entity;
/**
 * Auto Created by VBScript Do not modify!
 */

import fd.ng.core.utils.StringUtil;
import hrds.commons.entity.fdentity.ProjectTableEntity;

import java.util.HashSet;
import java.util.Set;

/**
 * 机器学习数据信息表
 */
public class Ml_datatable_info extends ProjectTableEntity {
	public static final String TableName = "ml_datatable_info";
	private Long dtable_info_id; //数据表信息编号
	private String stable_cn_name; //数据源表中文名
	private String remark; //备注
	private Long project_id; //项目编号
	private String stable_en_name; //数据源表英文名
	private String dstorage_mode; //数据存储方式
	private String data_load_mode; //数据加载方式
	private String program_path; //程序路径
	private String dtable_runstate; //数据表运行状态
	private String dtable_cdate; //数据表创建日期
	private String dtable_ctime; //数据表创建时间
	private String run_date; //运行日期
	private String run_time; //运行时间
	private String run_end_date; //结束运行日期
	private String run_end_time; //结束运行时间
	private Long file_size; //数据表文件大小
	private String file_path; //数据源文件路径
	private String datamapmode; //数据映射方式

	/**
	 * 取得：数据表信息编号
	 */
	public Long getDtable_info_id() {
		return dtable_info_id;
	}

	/**
	 * 设置：数据表信息编号
	 */
	public void setDtable_info_id(Long dtable_info_id) {
		this.dtable_info_id = dtable_info_id;
	}

	/**
	 * 设置：数据表信息编号
	 */
	public void setDtable_info_id(String dtable_info_id) {
		if (!StringUtil.isEmpty(dtable_info_id))
			this.dtable_info_id = new Long(dtable_info_id);
	}

	/**
	 * 取得：数据源表中文名
	 */
	public String getStable_cn_name() {
		return stable_cn_name;
	}

	/**
	 * 设置：数据源表中文名
	 */
	public void setStable_cn_name(String stable_cn_name) {
		this.stable_cn_name = stable_cn_name;
	}

	/**
	 * 取得：备注
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * 设置：备注
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

	/**
	 * 取得：项目编号
	 */
	public Long getProject_id() {
		return project_id;
	}

	/**
	 * 设置：项目编号
	 */
	public void setProject_id(Long project_id) {
		this.project_id = project_id;
	}

	/**
	 * 设置：项目编号
	 */
	public void setProject_id(String project_id) {
		if (!StringUtil.isEmpty(project_id))
			this.project_id = new Long(project_id);
	}

	/**
	 * 取得：数据源表英文名
	 */
	public String getStable_en_name() {
		return stable_en_name;
	}

	/**
	 * 设置：数据源表英文名
	 */
	public void setStable_en_name(String stable_en_name) {
		this.stable_en_name = stable_en_name;
	}

	/**
	 * 取得：数据存储方式
	 */
	public String getDstorage_mode() {
		return dstorage_mode;
	}

	/**
	 * 设置：数据存储方式
	 */
	public void setDstorage_mode(String dstorage_mode) {
		this.dstorage_mode = dstorage_mode;
	}

	/**
	 * 取得：数据加载方式
	 */
	public String getData_load_mode() {
		return data_load_mode;
	}

	/**
	 * 设置：数据加载方式
	 */
	public void setData_load_mode(String data_load_mode) {
		this.data_load_mode = data_load_mode;
	}

	/**
	 * 取得：程序路径
	 */
	public String getProgram_path() {
		return program_path;
	}

	/**
	 * 设置：程序路径
	 */
	public void setProgram_path(String program_path) {
		this.program_path = program_path;
	}

	/**
	 * 取得：数据表运行状态
	 */
	public String getDtable_runstate() {
		return dtable_runstate;
	}

	/**
	 * 设置：数据表运行状态
	 */
	public void setDtable_runstate(String dtable_runstate) {
		this.dtable_runstate = dtable_runstate;
	}

	/**
	 * 取得：数据表创建日期
	 */
	public String getDtable_cdate() {
		return dtable_cdate;
	}

	/**
	 * 设置：数据表创建日期
	 */
	public void setDtable_cdate(String dtable_cdate) {
		this.dtable_cdate = dtable_cdate;
	}

	/**
	 * 取得：数据表创建时间
	 */
	public String getDtable_ctime() {
		return dtable_ctime;
	}

	/**
	 * 设置：数据表创建时间
	 */
	public void setDtable_ctime(String dtable_ctime) {
		this.dtable_ctime = dtable_ctime;
	}

	/**
	 * 取得：运行日期
	 */
	public String getRun_date() {
		return run_date;
	}

	/**
	 * 设置：运行日期
	 */
	public void setRun_date(String run_date) {
		this.run_date = run_date;
	}

	/**
	 * 取得：运行时间
	 */
	public String getRun_time() {
		return run_time;
	}

	/**
	 * 设置：运行时间
	 */
	public void setRun_time(String run_time) {
		this.run_time = run_time;
	}

	/**
	 * 取得：结束运行日期
	 */
	public String getRun_end_date() {
		return run_end_date;
	}

	/**
	 * 设置：结束运行日期
	 */
	public void setRun_end_date(String run_end_date) {
		this.run_end_date = run_end_date;
	}

	/**
	 * 取得：结束运行时间
	 */
	public String getRun_end_time() {
		return run_end_time;
	}

	/**
	 * 设置：结束运行时间
	 */
	public void setRun_end_time(String run_end_time) {
		this.run_end_time = run_end_time;
	}

	/**
	 * 取得：数据表文件大小
	 */
	public Long getFile_size() {
		return file_size;
	}

	/**
	 * 设置：数据表文件大小
	 */
	public void setFile_size(Long file_size) {
		this.file_size = file_size;
	}

	/**
	 * 设置：数据表文件大小
	 */
	public void setFile_size(String file_size) {
		if (!StringUtil.isEmpty(file_size))
			this.file_size = new Long(file_size);
	}

	/**
	 * 取得：数据源文件路径
	 */
	public String getFile_path() {
		return file_path;
	}

	/**
	 * 设置：数据源文件路径
	 */
	public void setFile_path(String file_path) {
		this.file_path = file_path;
	}

	/**
	 * 取得：数据映射方式
	 */
	public String getDatamapmode() {
		return datamapmode;
	}

	/**
	 * 设置：数据映射方式
	 */
	public void setDatamapmode(String datamapmode) {
		this.datamapmode = datamapmode;
	}

	private Set primaryKeys = new HashSet();

	public boolean isPrimaryKey(String name) {
		return primaryKeys.contains(name);
	}

	public String getPrimaryKey() {
		return primaryKeys.iterator().next().toString();
	}

	/**
	 * 机器学习数据信息表
	 */
	public Ml_datatable_info() {
		primaryKeys.add("dtable_info_id");
	}
}
