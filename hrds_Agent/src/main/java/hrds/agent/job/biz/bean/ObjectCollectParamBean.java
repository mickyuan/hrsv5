package hrds.agent.job.biz.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.core.annotation.DocClass;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.Serializable;

@DocClass(desc = "非结构化对象采集任务表和结构表的字段综合的实体", author = "zxz", createdate = "2019/10/24 10:07")
public class ObjectCollectParamBean implements Serializable {
	public static final byte[] FILE_HBASE = Bytes.toBytes("file_hbase");
	@DocBean(name = "ocs_id", value = "对象采集任务编号", dataType = Long.class, required = true)
	private Long ocs_id;
	@DocBean(name = "en_name", value = "英文名称", dataType = String.class, required = true)
	private String en_name;
	@DocBean(name = "zh_name", value = "中文名称", dataType = String.class, required = true)
	private String zh_name;
	@DocBean(name = "collect_data_type", value = "对象采集文件类型", dataType = String.class, required = true)
	private String collect_data_type;
	@DocBean(name = "database_code", value = "对象采集编码", dataType = String.class, required = true)
	private String database_code;
	@DocBean(name = "coll_names", value = "采集字段结构名称，逗号分隔", dataType = String.class, required = true)
	private String coll_names;
	@DocBean(name = "struct_types", value = "对象数据类型，逗号分隔", dataType = String.class, required = true)
	private String struct_types;
	@DocBean(name = "data_desc", value = "中文描述信息", dataType = String.class, required = false)
	private String data_desc;
	@DocBean(name = "is_hbase", value = "是否进hbase", dataType = String.class, required = true)
	private String is_hbase;
	@DocBean(name = "is_hdfs", value = "是否进hdfs", dataType = String.class, required = true)
	private String is_hdfs;

	public String getIs_hbase() {
		return is_hbase;
	}

	public void setIs_hbase(String is_hbase) {
		this.is_hbase = is_hbase;
	}

	public String getIs_hdfs() {
		return is_hdfs;
	}

	public void setIs_hdfs(String is_hdfs) {
		this.is_hdfs = is_hdfs;
	}

	public Long getOcs_id() {
		return ocs_id;
	}

	public void setOcs_id(Long ocs_id) {
		this.ocs_id = ocs_id;
	}

	public String getEn_name() {
		return en_name;
	}

	public void setEn_name(String en_name) {
		this.en_name = en_name;
	}

	public String getZh_name() {
		return zh_name;
	}

	public void setZh_name(String zh_name) {
		this.zh_name = zh_name;
	}

	public String getCollect_data_type() {
		return collect_data_type;
	}

	public void setCollect_data_type(String collect_data_type) {
		this.collect_data_type = collect_data_type;
	}

	public String getDatabase_code() {
		return database_code;
	}

	public void setDatabase_code(String database_code) {
		this.database_code = database_code;
	}

	public String getColl_names() {
		return coll_names;
	}

	public void setColl_names(String coll_names) {
		this.coll_names = coll_names;
	}

	public String getStruct_types() {
		return struct_types;
	}

	public void setStruct_types(String struct_types) {
		this.struct_types = struct_types;
	}

	public String getData_desc() {
		return data_desc;
	}

	public void setData_desc(String data_desc) {
		this.data_desc = data_desc;
	}
}
