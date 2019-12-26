package hrds.agent.job.biz.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.core.annotation.DocClass;
import hrds.commons.entity.Column_merge;

import java.io.Serializable;
import java.util.List;

@DocClass(desc = "DB文件采集,数据库采集选择表配置信息", author = "zxz", createdate = "2019/11/29 14:26")
public class CollectTableBean implements Serializable {
	@DocBean(name = "database_id", value = "数据库设置id:", dataType = Long.class, required = true)
	private String database_id;
	@DocBean(name = "table_id", value = "表名ID:", dataType = Long.class, required = true)
	private String table_id;
	@DocBean(name = "table_name", value = "表名:", dataType = String.class, required = true)
	private String table_name;
	@DocBean(name = "table_ch_name", value = "中文名称:", dataType = String.class, required = true)
	private String table_ch_name;
	@DocBean(name = "table_count", value = "记录数(CountNum):10000-1万左右<YiWan> 100000-10万左右<ShiWan> " +
			"1000000-100万左右<BaiWan> 10000000-1000万左右<Qianwan> 100000000-亿左右<Yi> " +
			"100000001-亿以上<YiYiShang> ", dataType = String.class, required = false)
	private String table_count;
	@DocBean(name = "source_tableid", value = "源表ID:", dataType = String.class, required = false)
	private String source_tableid;
	@DocBean(name = "valid_s_date", value = "有效开始日期:", dataType = String.class, required = true)
	private String valid_s_date;
	@DocBean(name = "valid_e_date", value = "有效结束日期:", dataType = String.class, required = true)
	private String valid_e_date;
	@DocBean(name = "sql", value = "自定义sql语句:", dataType = String.class, required = false)
	private String sql;
	@DocBean(name = "remark", value = "备注:", dataType = String.class, required = false)
	private String remark;
	@DocBean(name = "is_user_defined", value = "是否自定义sql采集(IsFlag):1-是<Shi> 0-否<Fou> ",
			dataType = String.class, required = true)
	private String is_user_defined;
	@DocBean(name = "is_md5", value = "是否使用MD5(IsFlag):1-是<Shi> 0-否<Fou> ",
			dataType = String.class, required = true)
	private String is_md5;
	@DocBean(name = "is_register", value = "是否仅登记(IsFlag):1-是<Shi> 0-否<Fou> ",
			dataType = String.class, required = true)
	private String is_register;
	@DocBean(name = "is_parallel", value = "是否并行抽取(IsFlag):1-是<Shi> 0-否<Fou> ",
			dataType = String.class, required = true)
	private String is_parallel;
	@DocBean(name = "page_sql", value = "分页sql:", dataType = String.class, required = false)
	private String page_sql;
	@DocBean(name = "is_header", value = "是否需要表头(IsFlag):1-是<Shi> 0-否<Fou> ",
			dataType = String.class, required = true)
	private String is_header;
	@DocBean(name = "data_extract_type", value = "数据抽取方式(DataExtractType):1-仅数据抽取<JinShuJuChouQu> " +
			"2-数据抽取及入库<ShuJuChouQuJiRuKu> ", dataType = String.class, required = true)
	private String data_extract_type;
	@DocBean(name = "database_code", value = "数据抽取落地编码(DataBaseCode):1-UTF-8<UTF_8> 2-GBK<GBK> " +
			"3-UTF-16<UTF_16> 4-GB2312<GB2312> 5-ISO-8859-1<ISO_8859_1> ", dataType = String.class, required = true)
	private String database_code;
	@DocBean(name = "row_separator", value = "行分隔符:", dataType = String.class, required = false)
	private String row_separator;
	@DocBean(name = "database_separatorr", value = "列分割符:", dataType = String.class, required = false)
	private String database_separatorr;
	@DocBean(name = "ded_remark", value = "备注:", dataType = String.class, required = false)
	private String ded_remark;
	@DocBean(name = "dbfile_format", value = "数据落地格式(FileFormat):0-定长<DingChang> 1-非定长<FeiDingChang> " +
			"2-CSV<CSV> 3-SEQUENCEFILE<SEQUENCEFILE> 4-PARQUET<PARQUET> 5-ORC<ORC> ",
			dataType = String.class, required = true)
	private String dbfile_format;
	@DocBean(name = "plane_url", value = "数据落地目录:", dataType = String.class, required = false)
	private String plane_url;
	@DocBean(name = "file_suffix", value = "落地文件后缀名:", dataType = String.class, required = false)
	private String file_suffix;
	@DocBean(name = "collectTableColumnBeanList", value = "表采集字段集合:", dataType = String.class, required = false)
	private List<CollectTableColumnBean> collectTableColumnBeanList;
	@DocBean(name = "column_merge", value = "列合并参数信息:", dataType = Column_merge.class, required = false)
	private Column_merge column_merge;
	@DocBean(name = "storage_type", value = "进数方式(StorageType):1-增量<ZengLiang> 2-追加<ZhuiJia> 3-替换<TiHuan> ", dataType = String.class, required = true)
	private String storage_type;
	@DocBean(name = "storage_time", value = "存储期限（以天为单位）:", dataType = Long.class, required = true)
	private Long storage_time;
	@DocBean(name = "is_zipper", value = "是否拉链存储(IsFlag):1-是<Shi> 0-否<Fou> ", dataType = String.class, required = true)
	private String is_zipper;
	@DocBean(name = "dataStoreConfBean", value = "表存储配置信息", dataType = DataStoreConfBean.class, required = true)
	private List<DataStoreConfBean> dataStoreConfBean;
	@DocBean(name = "hbase_name", value = "数据采集之后的唯一表名", dataType = String.class, required = true)
	private String hbase_name;
	@DocBean(name = "eltDate", value = "数据采集的跑批日期", dataType = String.class, required = true)
	private String eltDate;
	@DocBean(name = "datasource_name", value = "数据源名称", dataType = String.class, required = true)
	private String datasource_name;
	@DocBean(name = "agent_name", value = "agent名称", dataType = String.class, required = true)
	private String agent_name;
	@DocBean(name = "agent_id", value = "Agent_id", dataType = Long.class, required = true)
	private Long agent_id;
	@DocBean(name = "source_id", value = "数据源ID", dataType = Long.class, required = true)
	private Long source_id;

	public Column_merge getColumn_merge() {
		return column_merge;
	}

	public void setColumn_merge(Column_merge column_merge) {
		this.column_merge = column_merge;
	}

	public String getTable_id() {
		return table_id;
	}

	public void setTable_id(String table_id) {
		this.table_id = table_id;
	}

	public String getTable_name() {
		return table_name;
	}

	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}

	public String getTable_ch_name() {
		return table_ch_name;
	}

	public void setTable_ch_name(String table_ch_name) {
		this.table_ch_name = table_ch_name;
	}

	public String getTable_count() {
		return table_count;
	}

	public void setTable_count(String table_count) {
		this.table_count = table_count;
	}

	public String getSource_tableid() {
		return source_tableid;
	}

	public void setSource_tableid(String source_tableid) {
		this.source_tableid = source_tableid;
	}

	public String getValid_s_date() {
		return valid_s_date;
	}

	public void setValid_s_date(String valid_s_date) {
		this.valid_s_date = valid_s_date;
	}

	public String getValid_e_date() {
		return valid_e_date;
	}

	public void setValid_e_date(String valid_e_date) {
		this.valid_e_date = valid_e_date;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getIs_user_defined() {
		return is_user_defined;
	}

	public void setIs_user_defined(String is_user_defined) {
		this.is_user_defined = is_user_defined;
	}

	public String getIs_md5() {
		return is_md5;
	}

	public void setIs_md5(String is_md5) {
		this.is_md5 = is_md5;
	}

	public String getIs_register() {
		return is_register;
	}

	public void setIs_register(String is_register) {
		this.is_register = is_register;
	}

	public String getIs_parallel() {
		return is_parallel;
	}

	public void setIs_parallel(String is_parallel) {
		this.is_parallel = is_parallel;
	}

	public String getPage_sql() {
		return page_sql;
	}

	public void setPage_sql(String page_sql) {
		this.page_sql = page_sql;
	}

	public String getIs_header() {
		return is_header;
	}

	public void setIs_header(String is_header) {
		this.is_header = is_header;
	}

	public String getData_extract_type() {
		return data_extract_type;
	}

	public void setData_extract_type(String data_extract_type) {
		this.data_extract_type = data_extract_type;
	}

	public String getDatabase_code() {
		return database_code;
	}

	public void setDatabase_code(String database_code) {
		this.database_code = database_code;
	}

	public String getRow_separator() {
		return row_separator;
	}

	public void setRow_separator(String row_separator) {
		this.row_separator = row_separator;
	}

	public String getDatabase_separatorr() {
		return database_separatorr;
	}

	public void setDatabase_separatorr(String database_separatorr) {
		this.database_separatorr = database_separatorr;
	}

	public String getDed_remark() {
		return ded_remark;
	}

	public void setDed_remark(String ded_remark) {
		this.ded_remark = ded_remark;
	}

	public String getDbfile_format() {
		return dbfile_format;
	}

	public void setDbfile_format(String dbfile_format) {
		this.dbfile_format = dbfile_format;
	}

	public String getPlane_url() {
		return plane_url;
	}

	public void setPlane_url(String plane_url) {
		this.plane_url = plane_url;
	}

	public String getFile_suffix() {
		return file_suffix;
	}

	public void setFile_suffix(String file_suffix) {
		this.file_suffix = file_suffix;
	}

	public List<CollectTableColumnBean> getCollectTableColumnBeanList() {
		return collectTableColumnBeanList;
	}

	public void setCollectTableColumnBeanList(List<CollectTableColumnBean> collectTableColumnBeanList) {
		this.collectTableColumnBeanList = collectTableColumnBeanList;
	}

	public String getStorage_type() {
		return storage_type;
	}

	public void setStorage_type(String storage_type) {
		this.storage_type = storage_type;
	}

	public Long getStorage_time() {
		return storage_time;
	}

	public void setStorage_time(Long storage_time) {
		this.storage_time = storage_time;
	}

	public String getIs_zipper() {
		return is_zipper;
	}

	public void setIs_zipper(String is_zipper) {
		this.is_zipper = is_zipper;
	}

	public List<DataStoreConfBean> getDataStoreConfBean() {
		return dataStoreConfBean;
	}

	public void setDataStoreConfBean(List<DataStoreConfBean> dataStoreConfBean) {
		this.dataStoreConfBean = dataStoreConfBean;
	}

	public String getHbase_name() {
		return hbase_name;
	}

	public void setHbase_name(String hbase_name) {
		this.hbase_name = hbase_name;
	}

	public String getDatabase_id() {
		return database_id;
	}

	public void setDatabase_id(String database_id) {
		this.database_id = database_id;
	}

	public String getEltDate() {
		return eltDate;
	}

	public void setEltDate(String eltDate) {
		this.eltDate = eltDate;
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

	public Long getAgent_id() {
		return agent_id;
	}

	public void setAgent_id(Long agent_id) {
		this.agent_id = agent_id;
	}

	public Long getSource_id() {
		return source_id;
	}

	public void setSource_id(Long source_id) {
		this.source_id = source_id;
	}
}
