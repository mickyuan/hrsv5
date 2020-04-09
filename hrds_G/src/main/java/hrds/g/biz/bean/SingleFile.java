package hrds.g.biz.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.core.annotation.DocClass;
import fd.ng.db.entity.anno.Table;
import hrds.commons.entity.fdentity.ProjectTableEntity;

@DocClass(desc = "单文件上传参数实体", author = "dhw", createdate = "2020/4/1 15:36")
@Table(tableName = "single_file")
public class SingleFile extends ProjectTableEntity {
	private static final long serialVersionUID = 321566870187324L;

	public static final String TableName = "single_file";

	@DocBean(name = "custSourceId", value = "自定义数据源id:", dataType = String.class, required = true)
	private String custSourceId;
	@DocBean(name = "businessId", value = "业务id:", dataType = String.class, required = true)
	private String businessId;
	@DocBean(name = "sourceFileId", value = "源文件id:", dataType = String.class, required = true)
	private String sourceFileId;
	@DocBean(name = "operateMode", value = "操作类型（insert/delete/update）:", dataType = String.class, required = true)
	private String operateMode;
	@DocBean(name = "fileName", value = "文件名称必须带后缀(a.jpg,a.jpeg,a.gif):", dataType = String.class, required = true)
	private String fileName;
	@DocBean(name = "fileType", value = "文件类型(image,file,music,video etc.):", dataType = String.class, required = true)
	private String fileType;

	public String getCustSourceId() {
		return custSourceId;
	}

	public void setCustSourceId(String custSourceId) {
		this.custSourceId = custSourceId;
	}

	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	public String getSourceFileId() {
		return sourceFileId;
	}

	public void setSourceFileId(String sourceFileId) {
		this.sourceFileId = sourceFileId;
	}

	public String getOperateMode() {
		return operateMode;
	}

	public void setOperateMode(String operateMode) {
		this.operateMode = operateMode;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
}
