package hrds.agent.job.biz.bean;

import fd.ng.core.annotation.DocClass;

import java.io.Serializable;
import java.util.List;

@DocClass(desc = "采集阶段传参信息")
public class StageParamInfo implements Serializable {

	private static final long serialVersionUID = 7280781296966361533L;

	//卸数完成后，列类型
	private List<String> columnTypes;

	//卸数完成后，本次采集数据量
	private long rowCount;

	//卸数完成后，本次采集表的meta信息
	private TableBean tableBean;

	//卸数完成后，本次采集生成的数据文件的总大小
	private long fileSize = 0;

	//卸数完成后，本次采集生成的数据文件路径
	private String[] fileArr;

	//TODO 如果后期有使用到这个类来进行各个阶段间的传参，就自行往这里面加

	//本阶段执行状态信息
	private StageStatusInfo statusInfo;

	public List<String> getColumnTypes() {
		return columnTypes;
	}

	public void setColumnTypes(List<String> columnTypes) {
		this.columnTypes = columnTypes;
	}

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
}
