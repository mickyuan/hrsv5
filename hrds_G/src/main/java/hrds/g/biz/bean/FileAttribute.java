package hrds.g.biz.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.core.annotation.DocClass;
import fd.ng.db.entity.anno.Table;
import hrds.commons.entity.fdentity.ProjectTableEntity;

@DocClass(desc = "文件属性查询实体", author = "dhw", createdate = "2020/4/1 17:51")
@Table(tableName = "file_attribute")
public class FileAttribute extends ProjectTableEntity {

	private static final long serialVersionUID = 321566870187324L;

	public static final String TableName = "file_attribute";

	@DocBean(name = "filename", value = "文件名:", dataType = String.class, required = false)
	private String filename;
	@DocBean(name = "filesize", value = "文件大小:数值以字节为单位,例如filesize=1,2000输入范围值以英文逗号隔开",
			dataType = String.class, required = false)
	private String filesize;
	@DocBean(name = "filesuffix", value = "文件后缀:", dataType = String.class, required = false)
	private String filesuffix;
	@DocBean(name = "filemd5", value = "文件MD5:", dataType = String.class, required = false)
	private String fileMD5;
	@DocBean(name = "filepath", value = "文件地址:", dataType = String[].class, required = false)
	private String[] filepath;
	@DocBean(name = "storagedate", value = "入库时间:", dataType = String.class, required = false)
	private String storagedate;
	@DocBean(name = "num", value = "查询记录数:num=1,10输入范围值以英文逗号隔开，表示从第几条开始查询多少条",
			dataType = String.class, required = false)
	private String num;
	@DocBean(name = "ds_name", value = "数据源名称:", dataType = String.class, required = false)
	private String ds_name;
	@DocBean(name = "agent_name", value = "采集agent名称:", dataType = String.class, required = false)
	private String agent_name;
	@DocBean(name = "fcs_name", value = "采集任务名称:", dataType = String.class, required = false)
	private String fcs_name;
	@DocBean(name = "fcs_id", value = "采集任务id:", dataType = Long[].class, required = false)
	private Long[] fcs_id;
	@DocBean(name = "dep_id", value = "所属部门id:", dataType = Long[].class, required = false)
	private Long[] dep_id;

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

	public String getFileMD5() {
		return fileMD5;
	}

	public void setFileMD5(String fileMD5) {
		this.fileMD5 = fileMD5;
	}

	public String[] getFilepath() {
		return filepath;
	}

	public void setFilepath(String[] filepath) {
		this.filepath = filepath;
	}

	public String getStoragedate() {
		return storagedate;
	}

	public void setStoragedate(String storagedate) {
		this.storagedate = storagedate;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
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

	public Long[] getFcs_id() {
		return fcs_id;
	}

	public void setFcs_id(Long[] fcs_id) {
		this.fcs_id = fcs_id;
	}

	public Long[] getDep_id() {
		return dep_id;
	}

	public void setDep_id(Long[] dep_id) {
		this.dep_id = dep_id;
	}
}
