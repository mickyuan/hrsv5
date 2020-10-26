package hrds.agent.job.biz.bean;

import fd.ng.core.annotation.DocBean;
import hrds.commons.entity.Object_collect_struct;
import hrds.commons.entity.Object_handle_type;

import java.io.Serializable;
import java.util.List;

public class ObjectTableBean implements Serializable {
	@DocBean(name = "ocs_id", value = "对象采集任务编号:", dataType = Long.class, required = true)
	private String ocs_id;
	@DocBean(name = "en_name", value = "英文名称:", dataType = String.class, required = true)
	private String en_name;
	@DocBean(name = "hyren_name", value = "入库后的英文名称:", dataType = String.class, required = true)
	private String hyren_name;
	@DocBean(name = "zh_name", value = "中文名称:", dataType = String.class, required = true)
	private String zh_name;
	@DocBean(name = "collect_data_type", value = "数据类型(CollectDataType):1-xml<XML> 2-json<JSON> ", dataType =
			String.class, required = true)
	private String collect_data_type;
	@DocBean(name = "database_code", value = "采集编码(DataBaseCode):1-UTF-8<UTF_8> 2-GBK<GBK> 3-UTF-16<UTF_16> " +
			"4-GB2312<GB2312> 5-ISO-8859-1<ISO_8859_1> ", dataType = String.class, required = true)
	private String database_code;
	@DocBean(name = "agent_id", value = "Agent_id:", dataType = Long.class, required = true)
	private Long agent_id;
	@DocBean(name = "firstline", value = "第一行数据:", dataType = String.class, required = false)
	private String firstline;
	@DocBean(name = "odc_id", value = "对象采集id:", dataType = Long.class, required = false)
	private String odc_id;
	@DocBean(name = "updatetype", value = "更新方式(UpdateType):0-直接更新<DirectUpdate> 1-拉链更新<IncrementUpdate> ",
			dataType = String.class, required = true)
	private String updatetype;
	@DocBean(name = "etlDate", value = "数据采集的跑批日期", dataType = String.class)
	private String etlDate;
	@DocBean(name = "datasource_name", value = "数据源名称", dataType = String.class)
	private String datasource_name;
	@DocBean(name = "agent_name", value = "agent名称", dataType = String.class)
	private String agent_name;
	@DocBean(name = "user_id", value = "User_id", dataType = Long.class)
	private Long user_id;
	@DocBean(name = "source_id", value = "数据源ID", dataType = Long.class)
	private Long source_id;
	@DocBean(name = "object_collect_structList", value = "对象采集表结构信息的集合", dataType = List.class)
	private List<Object_collect_struct> object_collect_structList;
	@DocBean(name = "object_handle_typeList", value = "对象采集数据处理类型对应表的集合", dataType = List.class)
	private List<Object_handle_type> object_handle_typeList;
	@DocBean(name = "dataStoreConfBean", value = "表存储配置信息", dataType = DataStoreConfBean.class)
	private List<DataStoreConfBean> dataStoreConfBean;

	public String getOcs_id() {
		return ocs_id;
	}

	public void setOcs_id(String ocs_id) {
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

	public Long getAgent_id() {
		return agent_id;
	}

	public void setAgent_id(Long agent_id) {
		this.agent_id = agent_id;
	}

	public String getFirstline() {
		return firstline;
	}

	public void setFirstline(String firstline) {
		this.firstline = firstline;
	}

	public String getOdc_id() {
		return odc_id;
	}

	public void setOdc_id(String odc_id) {
		this.odc_id = odc_id;
	}

	public String getUpdatetype() {
		return updatetype;
	}

	public void setUpdatetype(String updatetype) {
		this.updatetype = updatetype;
	}

	public String getEtlDate() {
		return etlDate;
	}

	public void setEtlDate(String etlDate) {
		this.etlDate = etlDate;
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

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public Long getSource_id() {
		return source_id;
	}

	public void setSource_id(Long source_id) {
		this.source_id = source_id;
	}

	public List<Object_collect_struct> getObject_collect_structList() {
		return object_collect_structList;
	}

	public void setObject_collect_structList(List<Object_collect_struct> object_collect_structList) {
		this.object_collect_structList = object_collect_structList;
	}

	public List<Object_handle_type> getObject_handle_typeList() {
		return object_handle_typeList;
	}

	public void setObject_handle_typeList(List<Object_handle_type> object_handle_typeList) {
		this.object_handle_typeList = object_handle_typeList;
	}

	public List<DataStoreConfBean> getDataStoreConfBean() {
		return dataStoreConfBean;
	}

	public void setDataStoreConfBean(List<DataStoreConfBean> dataStoreConfBean) {
		this.dataStoreConfBean = dataStoreConfBean;
	}

	public String getHyren_name() {
		return hyren_name;
	}

	public void setHyren_name(String hyren_name) {
		this.hyren_name = hyren_name;
	}
}
