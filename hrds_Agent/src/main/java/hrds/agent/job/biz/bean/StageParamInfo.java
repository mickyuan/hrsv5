package hrds.agent.job.biz.bean;

import fd.ng.core.annotation.DocClass;

import java.io.Serializable;

@DocClass(desc = "采集阶段传参信息")
public class StageParamInfo implements Serializable {

	private static final long serialVersionUID = 7280781296966361533L;

	//卸数完成后，本次采集数据量
	private long rowCount = 0;

	//卸数完成后，本次采集表的meta信息
	private TableBean tableBean;

	//卸数完成后，本次采集生成的数据文件的总大小
	private long fileSize = 0;

	//卸数完成后，本次采集生成的数据文件路径
	private String[] fileArr;

	//卸数完成后的，生成的数据文件名称
	private String[] fileNameArr;

	//TODO 如果后期有使用到这个类来进行各个阶段间的传参，就自行往这里面加

	//本阶段执行状态信息
	private StageStatusInfo statusInfo;

	//任务分类（原子性）表名-顶级文件夹
	private String taskClassify;

	//跑批日期
	private String etlDate;

	//Agent_id
	private Long agentId;

	//数据库设置ID或文件设置id
	private Long collectSetId;

	//数据源ID
	private Long sourceId;

	//采集类型
	private String collectType;

	public long getRowCount() {
		return rowCount;
	}

	public void setRowCount(long rowCount) {
		this.rowCount = rowCount;
	}

	public TableBean getTableBean() {
		return tableBean;
	}

	public void setTableBean(TableBean tableBean) {
		this.tableBean = tableBean;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String[] getFileArr() {
		return fileArr;
	}

	public void setFileArr(String[] fileArr) {
		this.fileArr = fileArr;
	}

	public StageStatusInfo getStatusInfo() {
		return statusInfo;
	}

	public void setStatusInfo(StageStatusInfo statusInfo) {
		this.statusInfo = statusInfo;
	}

	public String getTaskClassify() {
		return taskClassify;
	}

	public void setTaskClassify(String taskClassify) {
		this.taskClassify = taskClassify;
	}

	public String getEtlDate() {
		return etlDate;
	}

	public void setEtlDate(String etlDate) {
		this.etlDate = etlDate;
	}

	public Long getAgentId() {
		return agentId;
	}

	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}

	public Long getCollectSetId() {
		return collectSetId;
	}

	public void setCollectSetId(Long collectSetId) {
		this.collectSetId = collectSetId;
	}

	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

	public String getCollectType() {
		return collectType;
	}

	public void setCollectType(String collectType) {
		this.collectType = collectType;
	}

	public String[] getFileNameArr() {
		return fileNameArr;
	}

	public void setFileNameArr(String[] fileNameArr) {
		this.fileNameArr = fileNameArr;
	}
}
