package hrds.g.biz.serviceuser;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import hrds.g.biz.bean.*;

import java.util.Map;

@DocClass(desc = "接口定义", author = "dhw", createdate = "2020/4/9 18:03")
public interface ServiceInterfaceUserDefine {

	@Method(desc = "获取token值", logicStep = "")
	@Param(name = "user_id", desc = "用户ID", range = "新增用户时生成")
	@Param(name = "user_password", desc = "密码", range = "新增用户时生成")
	@Return(desc = "返回接口响应信息", range = "无限制")
	Map<String, Object> getToken(Long user_id, String user_password);

//	@Method(desc = "集市表分页查询", logicStep = "")
//	@Param(name = "marketPagingQuery", desc = "集市分页查询参数实体", range = "自定义", isBean = true)
//	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
//	@Return(desc = "返回接口响应信息", range = "无限制")
//	Map<String, Object> marketTablePagingQuery(MarketPagingQuery marketPagingQuery, CheckParam checkParam);

	@Method(desc = "表使用权限查询", logicStep = "")
	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
	@Return(desc = "返回接口响应信息", range = "无限制")
	Map<String, Object> tableUsePermissions(CheckParam checkParam);

	@Method(desc = "单表普通查询", logicStep = "")
	@Param(name = "singleTable", desc = "单表普通查询参数实体", range = "无限制", isBean = true)
	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
	@Return(desc = "返回接口响应信息", range = "无限制")
	Map<String, Object> generalQuery(SingleTable singleTable, CheckParam checkParam);

//	@Method(desc = "单表索引查询接口", logicStep = "")
//	@Param(name = "singleTable", desc = "单表普通查询参数实体", range = "无限制", isBean = true)
//	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
//	@Return(desc = "返回接口响应信息", range = "无限制")
//	Map<String, Object> singleTableIndexQuery(SingleTable singleTable, CheckParam checkParam);
//
//	@Method(desc = "全文检索接口", logicStep = "")
//	@Param(name = "queryBean", desc = "全文检索查询实体对象", range = "无限制", isBean = true)
//	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
//	@Return(desc = "返回接口响应信息", range = "无限制")
//	Map<String, Object> fullTextSearch(QueryBean queryBean, CheckParam checkParam);

//	@Method(desc = "单表数据删除接口", logicStep = "")
//	@Param(name = "tableName", desc = "表名", range = "无限制")
//	@Param(name = "rowKey", desc = "主键", range = "无限制")
//	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
//	@Return(desc = "返回接口响应信息", range = "无限制")
//	Map<String, Object> singleTableDataDelete(String tableName, String[] rowKey, CheckParam checkParam);
//
//	@Method(desc = "单表删除接口", logicStep = "")
//	@Param(name = "tableName", desc = "表名", range = "无限制")
//	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
//	@Return(desc = "返回接口响应信息", range = "无限制")
//	Map<String, Object> singleTableDelete(String tableName, CheckParam checkParam);

	@Method(desc = "表结构查询接口", logicStep = "")
	@Param(name = "tableName", desc = "表名", range = "无限制")
	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
	@Return(desc = "返回接口响应信息", range = "无限制")
	Map<String, Object> tableStructureQuery(String tableName, CheckParam checkParam);

//	@Method(desc = "文章相似度比较接口", logicStep = "")
//	@Param(name = "file", desc = "文件流", range = "无限制")
//	@Param(name = "rate", desc = "文档相似度(小数0~1，精确2位小数)", range = "无限制")
//	@Param(name = "dep_id", desc = "部门ID", range = "新增部门时生成", nullable = true)
//	@Param(name = "fcs_id", desc = "要查询任务id", range = "无限制", nullable = true)
//	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
//	@Return(desc = "返回接口响应信息", range = "无限制")
//	@UploadFile
//	Map<String, Object> textSimilarComparison(String file, String rate, String dep_id, String fcs_id,
//	                                          CheckParam checkParam);

	@Method(desc = "文件属性搜索接口", logicStep = "")
	@Param(name = "fileAttribute", desc = "文件屬性参数实体", range = "无限制", isBean = true)
	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
	@Return(desc = "返回接口响应信息", range = "无限制")
	Map<String, Object> fileAttributeSearch(FileAttribute fileAttribute, CheckParam checkParam);

	@Method(desc = "sql查询接口", logicStep = "")
	@Param(name = "sqlSearch", desc = "sql查询参数实体", range = "无限制", isBean = true)
	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
	@Return(desc = "返回接口响应信息", range = "无限制")
	Map<String, Object> sqlInterfaceSearch(SqlSearch sqlSearch, CheckParam checkParam);

//	@Method(desc = "数据下载接口", logicStep = "")
//	@Param(name = "table", desc = "表名称", range = "无限制")
//	@Param(name = "fileName", desc = "生成的文件名称", range = "无限制")
//	@Param(name = "directory", desc = "生成的文件存放的目录", range = "无限制")
//	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
//	@Return(desc = "返回接口响应信息", range = "无限制")
//	Map<String, Object> dataDownload(String table, String fileName, String directory,
//	                                 CheckParam checkParam);

//	@Method(desc = "二级索引日期范围查询接口", logicStep = "")
//	@Param(name = "secondIndexDate", desc = "二级索引日期范围查询参数实体", range = "无限制", isBean = true)
//	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
//	@Return(desc = "返回接口响应信息", range = "无限制")
//	Map<String, Object> secondIndexDateRangeQuery(SecondIndexDate secondIndexDate, CheckParam checkParam);

	@Method(desc = "rowkey查询", logicStep = "")
	@Param(name = "rowKeySearch", desc = "rowkey查询参数实体", range = "无限制", isBean = true)
	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
	@Return(desc = "返回接口响应信息", range = "无限制")
	Map<String, Object> rowKeySearch(RowKeySearch rowKeySearch, CheckParam checkParam);

//	@Method(desc = "solr查询接口", logicStep = "")
//	@Param(name = "solrSearch", desc = "solr查询参数实体", range = "无限制", isBean = true)
//	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
//	@Return(desc = "返回接口响应信息", range = "无限制")
//	Map<String, Object> solrSearch(SolrSearch solrSearch, CheckParam checkParam);
//
//	@Method(desc = "单表二级索引查询接口（hyren）", logicStep = "")
//	@Param(name = "secondIndexQuery", desc = "单表二级索引查询参数实体", range = "无限制", isBean = true)
//	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
//	@Return(desc = "返回接口响应信息", range = "无限制")
//	Map<String, Object> singleTableSecondIndexQuery(SecondIndexQuery secondIndexQuery,
//	                                                CheckParam checkParam);
//
//	@Method(desc = "表数据批量更新接口", logicStep = "")
//	@Param(name = "dataBatchUpdate", desc = "表数据批量更新参数实体", range = "无限制", isBean = true)
//	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
//	@Return(desc = "返回接口响应信息", range = "无限制")
//	Map<String, Object> tableDataBatchUpdate(DataBatchUpdate dataBatchUpdate, CheckParam checkParam);

//	@Method(desc = "Solr查询Hbase数据接口", logicStep = "")
//	@Param(name = "hBaseSolr", desc = "HBaseSolr查询参数实体", range = "无限制", isBean = true)
//	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
//	@Return(desc = "返回接口响应信息", range = "无限制")
//	Map<String, Object> hBaseSolrQuery(HBaseSolr hBaseSolr, CheckParam checkParam);

	@Method(desc = "UUID数据下载", logicStep = "")
	@Param(name = "uuid", desc = "uuid", range = "无限制")
	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
	@Return(desc = "返回接口响应信息", range = "无限制")
	Map<String, Object> uuidDownload(String uuid, CheckParam checkParam);

//	@Method(desc = "文本语种", logicStep = "")
//	@Param(name = "file", desc = "文件流", range = "无限制", isBean = true)
//	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
//	@Return(desc = "返回接口响应信息", range = "无限制")
//	@UploadFile
//	Map<String, Object> textLanguage(String[] file, CheckParam checkParam);

//	@Method(desc = "实体识别", logicStep = "")
//	@Param(name = "file", desc = "文件流", range = "无限制", isBean = true)
//	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
//	@Return(desc = "返回接口响应信息", range = "无限制")
//	@UploadFile
//	Map<String, Object> entityRecognition(String[] file, CheckParam checkParam);

//	@Method(desc = "情感分析", logicStep = "")
//	@Param(name = "file", desc = "文件流", range = "无限制", isBean = true)
//	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
//	@Return(desc = "返回接口响应信息", range = "无限制")
//	@UploadFile
//	Map<String, Object> emotionValue(String[] file, CheckParam checkParam);

//	@Method(desc = "文本分类", logicStep = "")
//	@Param(name = "file", desc = "文件流", range = "无限制", isBean = true)
//	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
//	@Return(desc = "返回接口响应信息", range = "无限制")
//	@UploadFile
//	Map<String, Object> textClassification(String[] file, CheckParam checkParam);

//	@Method(desc = "OCR识别", logicStep = "")
//	@Param(name = "file", desc = "文件流", range = "无限制", isBean = true)
//	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
//	@Return(desc = "返回接口响应信息", range = "无限制")
//	@UploadFile
//	Map<String, Object> ocrExtract(String[] file, CheckParam checkParam);
//
//	@Method(desc = "以图搜图接口", logicStep = "")
//	@Param(name = "file", desc = "文件流", range = "无限制", isBean = true)
//	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
//	@Return(desc = "返回接口响应信息", range = "无限制")
//	@UploadFile
//	Map<String, Object> pictureSearch(String[] file, CheckParam checkParam);

//	@Method(desc = "数据表操作接口", logicStep = "")
//	@Param(name = "sql", desc = "sql语句", range = "无限制")
//	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
//	@Return(desc = "返回接口响应信息", range = "无限制")
//	Map<String, Object> dataTableOperate(String sql, CheckParam checkParam);

//	@Method(desc = "HBase数据新增接口", logicStep = "")
//	@Param(name = "hbaseData", desc = "新增hbase数据参数实体", range = "无限制", isBean = true)
//	@Param(name = "columnComposition", desc = "列组成参数实体", range = "无限制", isBean = true)
//	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
//	@Return(desc = "返回接口响应信息", range = "无限制")
//	Map<String, Object> insertHBaseData(HbaseData hbaseData, ColumnComposition columnComposition,
//	                                    CheckParam checkParam);

//	@Method(desc = "hbase表创建接口", logicStep = "")
//	@Param(name = "tableCreate", desc = "hbase表创建参数实体", range = "无限制", isBean = true)
//	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
//	@Return(desc = "返回接口响应信息", range = "无限制")
//	Map<String, Object> hBaseTableCreate(HBaseTableCreate tableCreate, CheckParam checkParam);

//	@Method(desc = "创建无溯源Spark表接口", logicStep = "")
//	@Param(name = "sparkUntraceTable", desc = "创建无溯源Spark实体", range = "无限制", isBean = true)
//	@Param(name = "columnField", desc = "表字段参数实体", range = "无限制", isBean = true)
//	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
//	@Return(desc = "返回接口响应信息", range = "无限制")
//	Map<String, Object> createUntraceableSparkTable(SparkUntraceTable sparkUntraceTable,
//	                                                ColumnField columnField, CheckParam checkParam);

//	@Method(desc = "插入Spark数据接口", logicStep = "")
//	@Param(name = "sparkTable", desc = "spark表数据插入参数实体", range = "无限制", isBean = true)
//	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
//	@Return(desc = "返回接口响应信息", range = "无限制")
//	Map<String, Object> insertSparkTable(SparkTable sparkTable, CheckParam checkParam);

//	@Method(desc = "更新spark接口", logicStep = "")
//	@Param(name = "sparkTable", desc = "spark表数据插入参数实体", range = "无限制", isBean = true)
//	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
//	@Return(desc = "返回接口响应信息", range = "无限制")
//	Map<String, Object> updateSparkTable(SparkTable sparkTable, CheckParam checkParam);

//	@Method(desc = "删除Spark表数据接口", logicStep = "")
//	@Param(name = "sparkTable", desc = "spark表数据插入参数实体", range = "无限制", isBean = true)
//	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
//	@Return(desc = "返回接口响应信息", range = "无限制")
//	Map<String, Object> deleteSparkTableData(SparkTable sparkTable, CheckParam checkParam);

//	@Method(desc = "删除spark表接口", logicStep = "")
//	@Param(name = "entabname", desc = "表英文名", range = "无限制")
//	@Param(name = "tableSpace", desc = "表空间", range = "无限制")
//	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
//	@Return(desc = "返回接口响应信息", range = "无限制")
//	Map<String, Object> dropSparkTable(String entabname, String tableSpace, CheckParam checkParam);
//
//	@Method(desc = "创建有溯源Spark表接口", logicStep = "")
//	@Param(name = "sparkTable", desc = "spark表数据插入参数实体", range = "无限制", isBean = true)
//	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
//	@Return(desc = "返回接口响应信息", range = "无限制")
//	Map<String, Object> createTraceSparkTable(SparkTable sparkTable, CheckParam checkParam);
//
//	@Method(desc = "单文件上传", logicStep = "")
//	@Param(name = "singleFile", desc = "单文件上传参数实体", range = "无限制", isBean = true)
//	@Param(name = "checkParam", desc = "接口检查参数实体", range = "无限制", isBean = true)
//	@Param(name = "file", desc = "文件流", range = "删除操作时可不填", nullable = true)
//	@Return(desc = "返回接口响应信息", range = "无限制")
//	@UploadFile
//	Map<String, Object> singleFileUpload(SingleFile singleFile, CheckParam checkParam, String file);
}
