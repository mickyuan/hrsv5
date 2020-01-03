package hrds.commons.zTree.bean;

import fd.ng.core.annotation.DocClass;

@DocClass(desc = "WEB SQL 操作台树实体", author = "BY-HLL", createdate = "2019/12/20 0020 上午 10:02")
public class TreeDataInfo {

	private String type;
	private String agent_layer;
	private String source_id;
	private String classify_id;
	private String data_mart_id;
	private String category_id;
	private String systemDataType;
	private String kafka_id;
	private String batch_id;
	private String groupid;
	private String sdm_consum_id;
	private String type_id;
	private String parent_id;
	private String spaceTable;
	private String database_type;
	private String isFileCo;
	private String page_from;
	private String isPublic;
	private String isShTable;
	private String modal_pro_id;
	private String ml_project_id;
	private String dtable_info_id;
	private String MLtype;
	private String source;
	private String tableTospace;
	private String show;
	private String tableName;
	private String rootName;
	private String name;
	private String description;
	private String isIntoHBase;

	/*  */
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	/* agent_layer 数据层类型, DCL/DML */
	public String getAgent_layer() {
		return agent_layer;
	}

	public void setAgent_layer(String agent_layer) {
		this.agent_layer = agent_layer;
	}

	/* 数据源id */
	public String getSource_id() {
		return source_id;
	}

	public void setSource_id(String source_id) {
		this.source_id = source_id;
	}

	/* 分类id */
	public String getClassify_id() {
		return classify_id;
	}

	public void setClassify_id(String classify_id) {
		this.classify_id = classify_id;
	}

	/* 集市id */
	public String getData_mart_id() {
		return data_mart_id;
	}

	public void setData_mart_id(String data_mart_id) {
		this.data_mart_id = data_mart_id;
	}

	/* 分类编号 */
	public String getCategory_id() {
		return category_id;
	}

	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}

	/* 系统数据类型 */
	public String getSystemDataType() {
		return systemDataType;
	}

	public void setSystemDataType(String systemDataType) {
		this.systemDataType = systemDataType;
	}

	/* kafka数据id */
	public String getKafka_id() {
		return kafka_id;
	}

	public void setKafka_id(String kafka_id) {
		this.kafka_id = kafka_id;
	}

	/* 批量数据id */
	public String getBatch_id() {
		return batch_id;
	}

	public void setBatch_id(String batch_id) {
		this.batch_id = batch_id;
	}

	/* 分组id */
	public String getGroupid() {
		return groupid;
	}

	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}

	/* 消费id */
	public String getSdm_consum_id() {
		return sdm_consum_id;
	}

	public void setSdm_consum_id(String sdm_consum_id) {
		this.sdm_consum_id = sdm_consum_id;
	}

	/* 类型id */
	public String getType_id() {
		return type_id;
	}

	public void setType_id(String type_id) {
		this.type_id = type_id;
	}

	/* 父id */
	public String getParent_id() {
		return parent_id;
	}

	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
	}

	/* 表空间 */
	public String getSpaceTable() {
		return spaceTable;
	}

	public void setSpaceTable(String spaceTable) {
		this.spaceTable = spaceTable;
	}

	/* 数据库类型 */
	public String getDatabase_type() {
		return database_type;
	}

	public void setDatabase_type(String database_type) {
		this.database_type = database_type;
	}

	/* 是否文件采集 */
	public String getIsFileCo() {
		return isFileCo;
	}

	public void setIsFileCo(String isFileCo) {
		this.isFileCo = isFileCo;
	}

	/* 树菜单来源 */
	public String getPage_from() {
		return page_from;
	}

	public void setPage_from(String page_from) {
		this.page_from = page_from;
	}

	/* 公共 */
	public String getIsPublic() {
		return isPublic;
	}

	public void setIsPublic(String isPublic) {
		this.isPublic = isPublic;
	}

	/* 是否为树的根节点标志 */
	public String getIsShTable() {
		return isShTable;
	}

	public void setIsShTable(String isShTable) {
		this.isShTable = isShTable;
	}

	/* 模型分类id */
	public String getModal_pro_id() {
		return modal_pro_id;
	}

	public void setModal_pro_id(String modal_pro_id) {
		this.modal_pro_id = modal_pro_id;
	}

	/* 机器学习项目编号 */
	public String getMl_project_id() {
		return ml_project_id;
	}

	public void setMl_project_id(String ml_project_id) {
		this.ml_project_id = ml_project_id;
	}

	/* 机器学习数据表编号 */
	public String getDtable_info_id() {
		return dtable_info_id;
	}

	public void setDtable_info_id(String dtable_info_id) {
		this.dtable_info_id = dtable_info_id;
	}

	/* 机器学习文件夹类型，比如：'数据源聚焦'、'数据预处理' */
	public String getMLtype() {
		return MLtype;
	}

	public void setMLtype(String MLtype) {
		this.MLtype = MLtype;
	}

	/* 来源 */
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	/* 表空间 */
	public String getTableTospace() {
		return tableTospace;
	}

	public void setTableTospace(String tableTospace) {
		this.tableTospace = tableTospace;
	}

	/* 是否显示该分类下的表信息 */
	public String getShow() {
		return show;
	}

	public void setShow(String show) {
		this.show = show;
	}

	/* 表名 */
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/* 父级名称 */
	public String getRootName() {
		return rootName;
	}

	public void setRootName(String rootName) {
		this.rootName = rootName;
	}

	/* 表英文名 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/* 描述 */
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/* 是否入HBase 0:是,tableIndex,1:否,intoHBase*/
	public String getIsIntoHBase() {
		return isIntoHBase;
	}

	public void setIsIntoHBase(String isIntoHBase) {
		this.isIntoHBase = isIntoHBase;
	}
}
