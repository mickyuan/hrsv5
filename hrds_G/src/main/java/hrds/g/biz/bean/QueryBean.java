package hrds.g.biz.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.core.annotation.DocClass;
import fd.ng.db.entity.anno.Table;

@DocClass(desc = "全文检索查询实体对象", author = "dhw", createdate = "2020/4/1 17:51")
@Table(tableName = "query_bean")
public class QueryBean {

	private static final long serialVersionUID = 321566870187324L;

	public static final String TableName = "query_bean";

	@DocBean(name = "query", value = "查询的关键字:", dataType = String.class, required = true)
	private String query;
	@DocBean(name = "num", value = "显示条数:", dataType = String.class, required = true)
	private String num;
	@DocBean(name = "filename", value = "文件名:", dataType = String.class, required = true)
	private String filename;
	@DocBean(name = "filesize", value = "文件大小:", dataType = String.class, required = true)
	private String filesize;
	@DocBean(name = "filesuffix", value = "文件后缀:", dataType = String.class, required = true)
	private String filesuffix;
	@DocBean(name = "filemd5", value = "文件MD5:", dataType = String.class, required = true)
	private String filemd5;
	@DocBean(name = "filepath", value = "文件地址:", dataType = String.class, required = true)
	private String filepath;
	@DocBean(name = "storagedate", value = "入库时间:", dataType = String.class, required = true)
	private String storagedate;
	@DocBean(name = "ds_name", value = "数据源名称:", dataType = String.class, required = true)
	private String ds_name;
	@DocBean(name = "agent_name", value = "采集agent名称:", dataType = String.class, required = true)
	private String agent_name;
	@DocBean(name = "fcs_name", value = "任务采集名称:", dataType = String.class, required = true)
	private String fcs_name;
	@DocBean(name = "fcs_id", value = "要查询任务id:", dataType = String.class, required = true)
	private String fcs_id;
	@DocBean(name = "dep_id", value = "部门ID:", dataType = String.class, required = true)
	private String dep_id;
	@DocBean(name = "hyren_sort", value = "字段排列:", dataType = String.class, required = true)
	private String hyren_sort;
	@DocBean(name = "hdp_file_id", value = "要查询的文件的唯一id:", dataType = String.class, required = true)
	private String hdp_file_id;

	public String getHyren_sort() {

		return hyren_sort;
	}

	public void setHyren_sort(String hyren_sort) {

		this.hyren_sort = hyren_sort;
	}

	public String getQuery() {

		return query;
	}

	public void setQuery(String query) {

		this.query = query;
	}

	public String getNum() {

		return num;
	}

	public void setNum(String num) {

		this.num = num;
	}

	public String getFilename() {

		return filename;
	}

	public void setFilename(String filename) {

		this.filename = filename;
	}

	public String getFilesize() {

		return filesize;
	}

	public void setFilesize(String filesize) {

		this.filesize = filesize;
	}

	public String getFilesuffix() {

		return filesuffix;
	}

	public void setFilesuffix(String filesuffix) {

		this.filesuffix = filesuffix;
	}

	public String getFilemd5() {

		return filemd5;
	}

	public void setFilemd5(String filemd5) {

		this.filemd5 = filemd5;
	}

	public String getFilepath() {

		return filepath;
	}

	public void setFilepath(String filepath) {

		this.filepath = filepath;
	}

	public String getStoragedate() {

		return storagedate;
	}

	public void setStoragedate(String storagedate) {

		this.storagedate = storagedate;
	}

	public String getDs_name() {

		return ds_name;
	}

	public void setDs_name(String ds_name) {

		this.ds_name = ds_name;
	}

	public String getAgent_name() {

		return agent_name;
	}

	public void setAgent_name(String agent_name) {

		this.agent_name = agent_name;
	}

	public String getFcs_name() {

		return fcs_name;
	}

	public void setFcs_name(String fcs_name) {

		this.fcs_name = fcs_name;
	}

	public String getFcs_id() {

		return fcs_id;
	}

	public void setFcs_id(String fcs_id) {

		this.fcs_id = fcs_id;
	}

	public String getDep_id() {

		return dep_id;
	}

	public void setDep_id(String dep_id) {

		this.dep_id = dep_id;
	}

	public String getHdp_file_id() {

		return hdp_file_id;
	}

	public void setHdp_file_id(String hdp_file_id) {

		this.hdp_file_id = hdp_file_id;
	}
}
