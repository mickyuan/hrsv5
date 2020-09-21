package hrds.agent.job.biz.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.core.annotation.DocClass;
import hrds.commons.entity.File_source;
import hrds.commons.utils.Constant;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.Serializable;
import java.util.List;

@DocClass(desc = "文件采集需要的参数实体bean", author = "zxz", createdate = "2019/10/31 9:41")
public class FileCollectParamBean implements Serializable {
	public static final String E_MAXDATE = Constant.MAXDATE;

	public static final byte[] MAXDATE = Bytes.toBytes(E_MAXDATE);

	public static final byte[] S_DATE = Bytes.toBytes(Constant.SDATENAME);

	public static final byte[] E_DATE = Bytes.toBytes(Constant.EDATENAME);

	public static final String FILE_HBASE_NAME = "file_hbase";

	public static final byte[] FILE_HBASE = Bytes.toBytes(FILE_HBASE_NAME);

	@DocBean(name = "fcs_id", value = "文件系统采集ID", dataType = Long.class, required = true)
	private String fcs_id;
	@DocBean(name = "file_source_id", value = "文件源ID:", dataType = Long.class, required = false)
	private String file_source_id;
	@DocBean(name = "agent_id", value = "Agent_id", dataType = Long.class, required = true)
	private Long agent_id;
	@DocBean(name = "host_name", value = "主机名称", dataType = String.class, required = false)
	private String host_name;
	@DocBean(name = "system_type", value = "操作系统类型", dataType = String.class, required = false)
	private String system_type;
	@DocBean(name = "is_solr", value = "是否入solr", dataType = String.class, required = true)
	private String is_solr;
	@DocBean(name = "source_id", value = "数据源ID", dataType = Long.class, required = true)
	private Long source_id;
	@DocBean(name = "unLoadPath", value = "上传到hdfs的路径", dataType = String.class, required = false)
	private String unLoadPath;
	@DocBean(name = "sysDate", value = "开始采集日期", dataType = String.class, required = false)
	private String sysDate;
	@DocBean(name = "sysTime", value = "开始采集时间", dataType = String.class, required = false)
	private String sysTime;
	@DocBean(name = "file_source_path", value = "文件夹源路径", dataType = String.class, required = false)
	private String file_source_path;
	@DocBean(name = "datasource_name", value = "数据源名称", dataType = String.class, required = true)
	private String datasource_name;
	@DocBean(name = "agent_name", value = "agent名称", dataType = String.class, required = true)
	private String agent_name;
	@DocBean(name = "fcs_name", value = "文件系统采集任务名称", dataType = String.class, required = true)
	private String fcs_name;
	@DocBean(name = "dep_id", value = "部门id", dataType = String.class, required = true)
	private String dep_id;
	@DocBean(name = "file_sourceList", value = "文件源设置表的集合", dataType = List.class, required = true)
	private List<File_source> file_sourceList;

	public String getSysTime() {
		return sysTime;
	}

	public void setSysTime(String sysTime) {
		this.sysTime = sysTime;
	}

	public String getSysDate() {
		return sysDate;
	}

	public void setSysDate(String sysDate) {
		this.sysDate = sysDate;
	}

	public String getUnLoadPath() {
		return unLoadPath;
	}

	public void setUnLoadPath(String unLoadPath) {
		this.unLoadPath = unLoadPath;
	}

	public String getFcs_id() {
		return fcs_id;
	}

	public void setFcs_id(String fcs_id) {
		this.fcs_id = fcs_id;
	}

	public Long getAgent_id() {
		return agent_id;
	}

	public void setAgent_id(Long agent_id) {
		this.agent_id = agent_id;
	}

	public String getHost_name() {
		return host_name;
	}

	public void setHost_name(String host_name) {
		this.host_name = host_name;
	}

	public String getSystem_type() {
		return system_type;
	}

	public void setSystem_type(String system_type) {
		this.system_type = system_type;
	}

	public String getIs_solr() {
		return is_solr;
	}

	public void setIs_solr(String is_solr) {
		this.is_solr = is_solr;
	}

	public Long getSource_id() {
		return source_id;
	}

	public void setSource_id(Long source_id) {
		this.source_id = source_id;
	}

	public String getFile_source_id() {
		return file_source_id;
	}

	public void setFile_source_id(String file_source_id) {
		this.file_source_id = file_source_id;
	}

	public String getFile_source_path() {
		return file_source_path;
	}

	public void setFile_source_path(String file_source_path) {
		this.file_source_path = file_source_path;
	}

	public String getDatasource_name() {
		return datasource_name;
	}

	public void setDatasource_name(String datasource_name) {
		this.datasource_name = datasource_name;
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

	public String getDep_id() {
		return dep_id;
	}

	public void setDep_id(String dep_id) {
		this.dep_id = dep_id;
	}

	public List<File_source> getFile_sourceList() {
		return file_sourceList;
	}

	public void setFile_sourceList(List<File_source> file_sourceList) {
		this.file_sourceList = file_sourceList;
	}
}
