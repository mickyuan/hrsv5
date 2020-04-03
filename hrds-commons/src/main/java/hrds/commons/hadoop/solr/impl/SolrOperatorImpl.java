package hrds.commons.hadoop.solr.impl;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import hrds.commons.exception.BusinessException;
import hrds.commons.hadoop.hadoop_helper.HBaseHelper;
import hrds.commons.hadoop.hadoop_helper.HdfsOperator;
import hrds.commons.hadoop.solr.ISolrOperator;
import hrds.commons.utils.CommonVariables;
import hrds.commons.utils.Constant;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hive.ql.io.orc.OrcFile;
import org.apache.hadoop.hive.ql.io.orc.Reader;
import org.apache.hadoop.hive.ql.io.orc.RecordReader;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.example.GroupReadSupport;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.FieldAnalysisRequest;
import org.apache.solr.client.solrj.response.AnalysisResponseBase.AnalysisPhase;
import org.apache.solr.client.solrj.response.AnalysisResponseBase.TokenInfo;
import org.apache.solr.client.solrj.response.FieldAnalysisResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "solr操作实现类", author = "BY-HLL", createdate = "2020/1/14 0014 下午 05:02")
public abstract class SolrOperatorImpl implements ISolrOperator {

    protected static final Log logger = LogFactory.getLog(SolrOperatorImpl.class);
    protected SolrClient server;

    public SolrOperatorImpl() {
    }

    /* 获取solr server */
    @Override
    public SolrClient getServer() {
        return server;
    }

    /* 设置solr server */
    public void setServer(SolrClient server) {
        this.server = server;
    }

    @Method(desc = "获取语句解析字段",
            logicStep = "获取语句解析字段")
    @Param(name = "sentence", desc = "需要解析的字符串", range = "字符串", example = "今天天气很好!")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    @Override
    public List<String> getAnalysis(String sentence) {
        List<String> words = new ArrayList<>();
        try {
            FieldAnalysisRequest request = new FieldAnalysisRequest("/analysis/field");
            // 字段名，随便指定一个支持中文分词的字段
            request.addFieldName("hanlp");
            // 字段值，可以为空字符串，但是需要显式指定此参数
            request.setFieldValue("");
            request.setQuery(sentence);
            FieldAnalysisResponse response = request.process(server);
            for (AnalysisPhase phase : response.getFieldNameAnalysis("hanlp").getQueryPhases()) {
                List<TokenInfo> list = phase.getTokens();
                for (TokenInfo info : list) {
                    if (!info.getText().equals(" ")) {
                        words.add(info.getText());
                    }
                }
            }
        } catch (Exception e) {
            throw new BusinessException("获取字段分词解析失败!");
        }
        return words;
    }

    @Method(desc = "获取solr检索结果,全文检索", logicStep = "获取solr检索结果")
    @Param(name = "searchCondition", desc = "检索条件", range = "检索条件")
    @Param(name = "start", desc = "开始行", range = "开始行")
    @Param(name = "num", desc = "检索条数", range = "检索条数")
    @Return(desc = "检索结果的List集合", range = "返回值取值范围")
    @Override
    public List<Map<String, Object>> querySolr(String searchCondition, int start, int num) {
        Map<String, String> solrParams = new HashMap<>();
        solrParams.put("q", searchCondition);
        solrParams.put("qt", "/select");
        solrParams.put("wt", "json");
        solrParams.put("indent", "true");
        return querySolrPlus(solrParams, start, num, Boolean.TRUE);
    }

    @Method(desc = "获取solr检索结果,全文检索", logicStep = "获取solr检索结果")
    @Param(name = "solrParams", desc = "检索条件Map集合", range = "检索条件")
    @Param(name = "start", desc = "开始行", range = "开始行")
    @Param(name = "num", desc = "检索条数", range = "检索条数")
    @Param(name = "flag", desc = "是否返回文本内容", range = "true:返回,false:不敢回")
    @Return(desc = "检索结果的List集合", range = "返回值取值范围")
    @Override
    public List<Map<String, Object>> querySolrPlus(Map<String, String> solrParams, int start, int num, boolean flag) {
        //初始化返回结果List
        List<Map<String, Object>> solrDocList = new ArrayList<>();
        try {
            //1.创建SolrQuery对象
            SolrQuery solrQuery = new SolrQuery();
            //2.设置查询条件
            solrQuery.set("q", solrParams.get("q"));
            //3.设置查询结果的开始行与行数，有待修改为传入“开始行”和“行数”的2个参数。
            if (0 != num) {
                solrQuery.setStart(start);
                solrQuery.setRows(num);
            }
            //4.设置查询参数
            solrQuery.set("fq", solrParams.get("fq"));
            solrQuery.set("wt", solrParams.get("wt"));
            solrQuery.set("indent", solrParams.get("indent"));
            solrQuery.set("sort", solrParams.get("sort"));
            solrQuery.set("fl", solrParams.get("fl"));
            //5.增加score的域
            solrQuery.setIncludeScore(true);
            //6.执行查询
            QueryResponse queryResponse = server.query(solrQuery);
            //7.获取查询结果集
            SolrDocumentList solrDocumentList = queryResponse.getResults();
            logger.info("共有记录:" + queryResponse.getResults().getNumFound());
            //8.获取搜索到的所有数量
            long numFound = queryResponse.getResults().getNumFound();
            //9.设置结果计数
            int counter = 0;
            for (SolrDocument singleDoc : solrDocumentList) {
                Map<String, Object> resMap = new HashMap<>();
                resMap.put("sum", numFound);
                resMap.put("score", singleDoc.getFieldValue("score"));
                String sub_field;
                for (String fieldName : singleDoc.getFieldNames()) {
                    if (!flag) {
                        if (!fieldName.equals("_version_") && !fieldName.equals("score")
                                && !fieldName.equals("tf-text_content") && !fieldName.equals("tf-file_text")) {
                            //去掉tf-前缀
                            if (!fieldName.equals("id") && !fieldName.equals("table-name")) {
                                sub_field = fieldName.substring(3).trim();
                            } else {
                                sub_field = fieldName;
                            }
                            resMap.put(sub_field, singleDoc.getFieldValue(fieldName));
                        }
                    } else {
                        if (!fieldName.equals("_version_") && !fieldName.equals("score")
                                && !fieldName.equals("tf-text_content")) {
                            //去掉tf-前缀
                            if (!fieldName.equals("id") && !fieldName.equals("table-name")) {
                                sub_field = fieldName.substring(3).trim();
                            } else {
                                sub_field = fieldName;
                            }
                            resMap.put(sub_field, singleDoc.getFieldValue(fieldName));
                        }
                    }
                }
                solrDocList.add(resMap);
                counter++;
            }
            logger.info("返回结果共有:" + counter + "条记录!");
        } catch (Exception e) {
            logger.error("获取solr检索结果失败!");
            throw new BusinessException("获取solr检索结果失败!");
        }
        return solrDocList;
    }

    @Method(desc = "全表建立solr索引", logicStep = "全表建立solr索引")
    @Param(name = "tableName", desc = "需要创建索引的表名", range = "String类型")
    @Param(name = "HBaseHelper", desc = "HBaseHelper", range = "HBaseHelper")
    @Override
    public void indexCreateAll(String tableName, HBaseHelper helper) {
        ResultScanner scanner;
        try (Table table = helper.getTable(tableName)) {
            Scan scan = new Scan();
            scanner = table.getScanner(scan);
            //索引HBase Scanner到的数
            indexHBaseScanner(scanner, tableName);
            //关闭HBase扫描器
            scanner.close();
        } catch (Exception e) {
            logger.error("获取HBase表失败! tableName=" + tableName);
            throw new BusinessException("获取HBase表失败" + e);
        }
    }

    @Method(desc = "指定rowKey建立solr索引", logicStep = "全表建立solr索引")
    @Param(name = "rowKey", desc = "指定索引列", range = "String类型")
    @Param(name = "tableName", desc = "需要创建索引的表名", range = "String类型")
    @Param(name = "HBaseHelper", desc = "HBaseHelper", range = "HBaseHelper")
    @Override
    public void increasingIndexing(String tableName, String rowKey, HBaseHelper helper) {
        ResultScanner scanner;
        try (Table table = helper.getTable(tableName)) {
            Scan scan = new Scan();
            //建立filter,获取指定row_key的scanner结果
            Filter rf = new RowFilter(CompareFilter.CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes(rowKey)));
            scan.setFilter(rf);
            scanner = table.getScanner(scan);
            //索引HBase Scanner到的数
            indexHBaseScanner(scanner, tableName);
            //关闭HBase扫描器
            scanner.close();
        } catch (Exception e) {
            logger.error("获取HBase表失败! tableName=" + tableName);
            throw new BusinessException("获取HBase表失败" + e);
        }
    }

    @Method(desc = "指定字段建立solr索引", logicStep = "全表建立solr索引")
    @Param(name = "tableName", desc = "需要创建索引的表名", range = "String类型")
    @Param(name = "rowKeys", desc = "字段List", range = "String的List列表")
    @Param(name = "HBaseHelper", desc = "HBaseHelper", range = "HBaseHelper")
    @Override
    public void increasingIndexing(String tableName, List<String> rowKeys, HBaseHelper helper) {
        ResultScanner scanner;
        try (Table table = helper.getTable(tableName)) {
            //建立filter列表
            ArrayList<Filter> listForFilters = new ArrayList<>();
            for (String rowKey : rowKeys) {
                if (StringUtil.isNotBlank(rowKey)) {
                    Filter filter = new RowFilter(CompareFilter.CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes(rowKey)));
                    listForFilters.add(filter);
                }
            }
            Filter filterList = new FilterList(FilterList.Operator.MUST_PASS_ONE, listForFilters);
            //经过过滤器(filter)获得扫描结果到scanner
            Scan scan = new Scan();
            scan.setFilter(filterList);
            scanner = table.getScanner(scan);
            //索引HBase Scanner到的数
            indexHBaseScanner(scanner, tableName);
            //关闭HBase扫描器
            scanner.close();
        } catch (Exception e) {
            logger.error("获取HBase表失败! tableName=" + tableName);
            throw new BusinessException("获取HBase表失败" + e);
        }
    }

    @Method(desc = "按照id删除", logicStep = "按照id删除")
    @Param(name = "id", desc = "索引id,唯一", range = "String类型")
    @Override
    public void deleteIndexById(String id) {
        //按照id删除
        try {
            server.deleteById(id);
            server.commit();
        } catch (Exception e) {
            logger.error("根据id删除索引失败! id=" + id);
            throw new BusinessException("根据id删除索引失败! id=" + id);
        }
    }

    @Method(desc = "按照查询语句删除", logicStep = "按照查询语句删除")
    @Param(name = "query", desc = "查询语句", range = "String类型")
    @Override
    public void deleteIndexByQuery(String query) {
        //按照查询语句删除
        try {
            server.deleteByQuery(query);
            server.commit();
        } catch (Exception e) {
            logger.error("根据查询语句删除索引失败! query=" + query);
            throw new BusinessException("根据查询语句删除索引失败! query=" + query);
        }
    }

    @Method(desc = "全索引删除，慎用!!!", logicStep = "全索引删除")
    @Param(name = "参数名", desc = "参数说明", range = "取值范围说明")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    @Override
    public void deleteIndexAll() {
        //全索引删除，慎用!!!
        try {
            server.deleteByQuery("*:*");
            server.commit();
        } catch (Exception e) {
            logger.error("全索引删除索引失败!");
            throw new BusinessException("全索引删除索引失败!");
        }
    }

    @Method(desc = "HDFS文件创建所有字段的索引(csv)", logicStep = "HDFS文件创建所有字段的索引(csv)")
    @Param(name = "tableName", desc = "表名", range = "String类型")
    @Param(name = "HDFSPath", desc = "HDFS文件路径", range = "String类型")
    @Param(name = "operator", desc = "HdfsOperator对象", range = "HdfsOperator")
    @Param(name = "columnLine", desc = "列名组成的字符串,以逗号(,)分隔", range = "String类型")
    @Override
    public void indexCreateAll4csv(String tableName, String HDFSPath, HdfsOperator operator, String columnLine) {
        indexCreateAll4csv(tableName, HDFSPath, operator, columnLine, tableName);
    }

    @Method(desc = "HDFS文件创建所有字段的索引(csv)", logicStep = "HDFS文件创建所有字段的索引(csv)")
    @Param(name = "tableName", desc = "表名", range = "String类型")
    @Param(name = "HDFSPath", desc = "HDFS文件路径", range = "String类型")
    @Param(name = "operator", desc = "HdfsOperator对象", range = "HdfsOperator")
    @Param(name = "columnLine", desc = "列名组成的字符串,以逗号(,)分隔", range = "String类型")
    @Param(name = "hbaseName", desc = "HBase表名", range = "String类型")
    public void indexCreateAll4csv(String tableName, String HDFSPath, HdfsOperator operator, String columnLine,
                                   String hbaseName) {
        try {
            logger.info("要读取得文件目录是: " + HDFSPath);
            Path path = new Path(HDFSPath);
            FileSystem fs = operator.getFileSystem();
            if (fs.getFileStatus(path).isFile()) {
                processSingleCsv(hbaseName.toUpperCase(), new Path(HDFSPath), operator, columnLine);
            } else {
                FileStatus[] filesStatus = fs.listStatus(path, p ->
                        p.getName().toLowerCase().contains(tableName.toLowerCase()));
                for (FileStatus file : filesStatus) {
                    logger.info("开始处理CSV文件: " + file.getPath());
                    //处理单个CSV文件入solr
                    processSingleCsv(hbaseName.toUpperCase(), file.getPath(), operator, columnLine);
                }
            }
        } catch (Exception e) {
            logger.error("读取文件或者文件目录失败!" + HDFSPath);
            throw new BusinessException("读取得文件或者文件目录失败!");
        }
    }

    @Method(desc = "HDFS文件创建所有字段的索引(Parquet)", logicStep = "HDFS文件创建所有字段的索引(Parquet)")
    @Param(name = "tableName", desc = "表名", range = "String类型")
    @Param(name = "filePath", desc = "HDFS文件路径", range = "String类型")
    @Param(name = "operator", desc = "HdfsOperator对象", range = "HdfsOperator")
    @Param(name = "columnLine", desc = "列名组成的字符串,以逗号(,)分隔", range = "String类型")
    public void indexCreateAll4Parquet(String tableName, String filePath, HdfsOperator operator, String columnLine) {
        indexCreateAll4Parquet(tableName, filePath, operator, columnLine, tableName);
    }


    @Method(desc = "HDFS文件创建所有字段的索引(Parquet)", logicStep = "HDFS文件创建所有字段的索引(Parquet)")
    @Param(name = "tableName", desc = "表名", range = "String类型")
    @Param(name = "HDFSPath", desc = "HDFS文件路径", range = "String类型")
    @Param(name = "operator", desc = "HdfsOperator对象", range = "HdfsOperator")
    @Param(name = "columnLine", desc = "列名组成的字符串,以逗号(,)分隔", range = "String类型")
    @Param(name = "hbaseName", desc = "HBase表名", range = "String类型")
    public void indexCreateAll4Parquet(String tableName, String filePath, HdfsOperator operator, String columnLine,
                                       String hbaseName) {
        try {
            logger.info("要读取得文件目录是: " + filePath);
            Path path = new Path(filePath);
            FileSystem fs = operator.getFileSystem();
            Configuration conf = operator.getConfiguration();
            if (fs.getFileStatus(path).isFile()) {
                processSingleParquet(hbaseName.toUpperCase(), new Path(filePath), conf, columnLine);
            } else {
                FileStatus[] filesStatus = fs.listStatus(path, p ->
                        p.getName().toLowerCase().contains(tableName.toLowerCase()));
                for (FileStatus file : filesStatus) {
                    if (file.getLen() == 0) {
                        logger.info("待处理文件为空跳过:" + file.getPath());
                        continue;
                    }
                    logger.info("开始处理Parquet文件: " + file.getPath());
                    processSingleParquet(hbaseName.toUpperCase(), file.getPath(), conf, columnLine);
                }
            }
        } catch (Exception e) {
            logger.error("读取Parquet文件或目录失败!" + filePath);
            throw new BusinessException("读取parquet文件入solr失败:" + e.getMessage());
        }
    }

    @Method(desc = "获取自定义的handler,(带返回值)", logicStep = "获取自定义的handler,(带返回值)")
    @Param(name = "is", desc = "是否需要返回值", range = "Boolean类型")
    @Param(name = "params", desc = "查询参数设置", range = "Map类型")
    @Param(name = "handler", desc = "自定义handler", range = "String类型")
    @Return(desc = "handlerList", range = "返回值取值范围")
    @Override
    public List<Map<String, Object>> requestHandler(boolean is, Map<String, Object> params, String... handler) {
        //设置handler
        handler = handler.length < 1 ? new String[]{Constant.HANDLER} : handler;
        if (!is) {
            return requestHandler(handler);
        }
        SolrQuery sq = new SolrQuery();
        //设置查询参数
        if (!params.isEmpty()) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                sq.set(entry.getKey(), entry.getValue().toString());
            }
        }
        List<Map<String, Object>> handlerList = new ArrayList<>();
        Map<String, Object> handlerMap = new HashMap<>();
        QueryResponse response = new QueryResponse();
        for (String h : handler) {
            sq.setRequestHandler(h);
            try {
                response = server.query(sq);
            } catch (SolrServerException | IOException e) {
                handlerMap.put(h, e);
                e.printStackTrace();
            }
            handlerMap.put(h, response.getResponse());
            handlerList.add(handlerMap);
            logger.info("spend time on request to custom handler " + h + ":" + response.getQTime() + " ms");
        }
        return handlerList;
    }

    @Method(desc = "根据fieldName和值自定义查询,自定义返回字段", logicStep = "根据fieldName和值自定义查询,自定义返回字段")
    @Param(name = "fieldName", desc = "查询字段名", range = "String类型")
    @Param(name = "filedValue", desc = "查询字段值", range = "String类型")
    @Param(name = "fieldName", desc = "查询字段名", range = "String类型")
    @Return(desc = "SolrDocumentList", range = "返回值取值范围")
    @Override
    public SolrDocumentList queryByField(String fieldName, String filedValue, String responseColumns, int rows) {
        SolrDocumentList solrDocuments;
        try {
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.set("q", fieldName + ":" + filedValue);
            solrQuery.set("fl", responseColumns);
            solrQuery.set("qt", "/select");
            solrQuery.set("wt", "json");
            solrQuery.set("indent", "true");
            solrQuery.setRows(rows);
            solrDocuments = server.query(solrQuery).getResults();
        } catch (SolrServerException | IOException e) {
            logger.error(e);
            throw new BusinessException("SolrServer服务异常!");
        }
        return solrDocuments;
    }

    @Method(desc = "HDFS文件创建所有字段的索引(ORC)", logicStep = "HDFS文件创建所有字段的索引(ORC)")
    @Param(name = "tableName", desc = "表名", range = "String类型")
    @Param(name = "filePath", desc = "HDFS文件路径", range = "String类型")
    @Param(name = "operator", desc = "HdfsOperator对象", range = "HdfsOperator")
    @Param(name = "columnLine", desc = "列名组成的字符串,以逗号(,)分隔", range = "String类型")
    public void indexCreateAll4Orc(String tableName, String filePath, HdfsOperator operator, String columnLine) {
        indexCreateAll4Orc(tableName, filePath, operator, columnLine, tableName);
    }

    @Method(desc = "HDFS文件创建所有字段的索引(ORC)", logicStep = "HDFS文件创建所有字段的索引(ORC)")
    @Param(name = "tableName", desc = "表名", range = "String类型")
    @Param(name = "filePath", desc = "HDFS文件路径", range = "String类型")
    @Param(name = "operator", desc = "HdfsOperator对象", range = "HdfsOperator")
    @Param(name = "columnLine", desc = "列名组成的字符串,以逗号(,)分隔", range = "String类型")
    @Param(name = "hbaseName", desc = "HBase表名", range = "String类型")
    public void indexCreateAll4Orc(String tableName, String filePath, HdfsOperator operator, String columnLine,
                                   String hbaseName) {
        try {
            logger.info("要读取得文件目录是: " + filePath);
            Path path = new Path(filePath);
            FileSystem fs = operator.getFileSystem();
            if (fs.getFileStatus(path).isFile()) {
                processSingleOrc(hbaseName.toUpperCase(), new Path(filePath), operator, columnLine);
            } else {
                FileStatus[] filesStatus = fs.listStatus(path, p -> p.getName().toLowerCase().contains(tableName.toLowerCase()));
                for (FileStatus file : filesStatus) {
                    logger.info("开始处理Orc文件: " + file.getPath());
                    /* 处理单个ORC文件 */
                    processSingleOrc(hbaseName.toUpperCase(), file.getPath(), operator, columnLine);
                }
            }
        } catch (Exception e) {

            throw new BusinessException("读取ORC文件入solr失败:" + filePath);
        }
    }

    @Method(desc = "HDFS文件创建所有字段的索引(SequenceFile)", logicStep = "HDFS文件创建所有字段的索引(SequenceFile)")
    @Param(name = "tableName", desc = "表名", range = "String类型")
    @Param(name = "filePath", desc = "HDFS文件路径", range = "String类型")
    @Param(name = "operator", desc = "HdfsOperator对象", range = "HdfsOperator")
    @Param(name = "columnLine", desc = "列名组成的字符串,以逗号(,)分隔", range = "String类型")
    public void indexCreateAll4Sequence(String tableName, String filePath, HdfsOperator operator, String columnLine) {
        indexCreateAll4Sequence(tableName, filePath, operator, columnLine, tableName);
    }

    @Method(desc = "HDFS文件创建所有字段的索引(SequenceFile)", logicStep = "HDFS文件创建所有字段的索引(SequenceFile)")
    @Param(name = "tableName", desc = "表名", range = "String类型")
    @Param(name = "filePath", desc = "HDFS文件路径", range = "String类型")
    @Param(name = "operator", desc = "HdfsOperator对象", range = "HdfsOperator")
    @Param(name = "columnLine", desc = "列名组成的字符串,以逗号(,)分隔", range = "String类型")
    @Param(name = "hbaseName", desc = "HBase表名", range = "String类型")
    public void indexCreateAll4Sequence(String tableName, String filePath, HdfsOperator operator, String columnLine,
                                        String hbaseName) {
        try {
            logger.info("要读取得文件目录是: " + filePath);
            Path path = new Path(filePath);
            FileSystem fs = operator.getFileSystem();
            if (fs.getFileStatus(path).isFile()) {
                processSingleSeq(hbaseName.toUpperCase(), new Path(filePath), operator, columnLine);
            } else {
                FileStatus[] filesStatus = fs.listStatus(path, p ->
                        p.getName().toLowerCase().contains(tableName.toLowerCase()));
                for (FileStatus file : filesStatus) {
                    logger.info("开始处理SequenceFile文件: " + file.getPath());
                    processSingleSeq(hbaseName.toUpperCase(), file.getPath(), operator, columnLine);
                }
            }
        } catch (Exception e) {
            logger.error(e);
            throw new BusinessException("读取SequenceFile文件入solr失败!" + filePath);
        }
    }

    @Method(desc = "索引HBase Scanner到的数据",
            logicStep = "逻辑说明")
    @Param(name = "scanner", desc = "HBase根据表扫描到的数据", range = "String类型")
    @Param(name = "tableName", desc = "HBase表名", range = "String类型")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    private void indexHBaseScanner(ResultScanner scanner, String tableName) {
        //1.设置统计计数
        int count = 1;
        try {
            List<SolrInputDocument> docs = new ArrayList<>();
            SolrInputDocument doc;
            for (Result scanResult : scanner) {
                doc = new SolrInputDocument();
                doc.addField("id", Bytes.toString(scanResult.getRow()));
                doc.addField("table-name", tableName);
                for (Cell cell : scanResult.listCells()) {
                    if (!Bytes.toString(CellUtil.cloneQualifier(cell)).equals("stream_content")) {
                        doc.addField("tf-" + Bytes.toString(CellUtil.cloneQualifier(cell)), Bytes.toString(CellUtil.cloneValue(cell)));
                        if (doc.size() != 0)
                            docs.add(doc);
                        // 防止内存溢出
                        if (docs.size() >= CommonVariables.SOLR_BULK_SUBMISSIONS_NUM) {
                            server.add(docs);
                            server.commit();
                            docs.clear();
                            logger.info("[info] " + count + " 条数据完成索引!");
                        }
                        count++;
                    }
                }
            }
            if (docs.size() != 0) {
                server.add(docs);
                server.commit();
                logger.info(count + " 条数据完成索引!");
            }
            logger.info("----solr on hbase indexing is over!!!----");
        } catch (Exception ignored) {
            logger.error("solr创建索引失败!");
            throw new BusinessException("solr创建索引失败!");
        }
    }

    /* 处理单个CSV文件 */
    private void processSingleCsv(String tableName, Path path, HdfsOperator operator, String columnLine) {
        //记录耗时
        long start = System.currentTimeMillis();
        try (ICsvListReader rd = new CsvListReader(operator.toBufferedReader(path), CsvPreference.EXCEL_PREFERENCE)) {
            String[] columnNames = columnLine.split(",");
            List<SolrInputDocument> docs = new ArrayList<>();
            List<String> list;
            logger.info("[开始读取文件] " + path);
            //统计进入solr的记录数
            int count = 0;
            while ((list = rd.read()) != null) {
                //完成一条记录
                SolrInputDocument doc = new SolrInputDocument();
                //获取MD5值作为id
                doc.addField("id", tableName + "_" + list.get(list.size() - 1));
                doc.addField("table-name", tableName);
                for (int i = 0; i < list.size(); i++) {
                    //跳过MD5值这一列
                    if (i != list.size() - 1) {
                        doc.addField("tf-" + columnNames[i], list.get(i));
                    }
                }
                if (doc.size() != 0)
                    docs.add(doc);
                // 防止内存溢出,SOLR_BULK_SUBMISSIONS_NUM:提交到solr的频率
                if (docs.size() >= CommonVariables.SOLR_BULK_SUBMISSIONS_NUM) {
                    server.add(docs);
                    server.commit();
                    docs.clear();
                    logger.info(count + "条数据完成索引!");
                }
                count++;
            }
            if (docs.size() != 0) {
                server.add(docs);
                server.commit();
            }
            logger.info(count + "条数据完成索引!");
            logger.info("----Indexing is successful!!!----");
            long stop = System.currentTimeMillis();
            logger.info("建立solr索引共耗时:" + (stop - start) * 1.0 / 1000 + "s");
        } catch (Exception e) {
            logger.error("单个CSV文件入solr失败!" + path);
            throw new BusinessException("单个CSV文件入solr失败!" + path);
        }
    }

    /* 处理单个Parquet文件 */
    private void processSingleParquet(String table_name, Path parquetPath, Configuration conf, String columnLine) {
        //记录耗时
        long start = System.currentTimeMillis();
        String[] columnNames = columnLine.split(",");
        try (ParquetReader<Group> reader =
                     ParquetReader.builder(new GroupReadSupport(), parquetPath).withConf(conf).build()) {
            Group group;
            logger.info("[开始读取文件 ] " + parquetPath);
            List<SolrInputDocument> docs = new ArrayList<>();
            //进入solr的记录数
            int count = 0;
            while ((group = reader.read()) != null) {
                //完成一条记录
                SolrInputDocument doc = new SolrInputDocument();
                doc.addField("id", table_name + "_" + group.getString(Constant.MD5NAME, 0));
                doc.addField("table-name", table_name);
                for (int i = 0; i < columnNames.length; i++) {
                    //跳过MD5值这一列
                    if (!Constant.MD5NAME.equalsIgnoreCase(columnNames[i])) {
                        doc.addField("tf-" + columnNames[i], group.getValueToString(i, 0));
                    }
                }
                if (doc.size() != 0)
                    docs.add(doc);
                // 防止内存溢出,SOLR_BULK_SUBMISSIONS_NUM:提交到solr的频率
                if (docs.size() >= CommonVariables.SOLR_BULK_SUBMISSIONS_NUM) {
                    server.add(docs);
                    server.commit();
                    docs.clear();
                }
                count++;
            }
            if (docs.size() != 0) {
                server.add(docs);
                server.commit();
            }
            logger.info(count + " 条数据完成索引!");
            logger.info("----Indexing is successful!!!----");
            long stop = System.currentTimeMillis();
            logger.info("建立solr索引共耗时:" + (stop - start) * 1.0 / 1000 + "s");
        } catch (Exception e) {
            logger.error("单个CSV文件入solr失败!" + parquetPath);
            throw new BusinessException("单个parquet文件入solr失败!" + parquetPath);
        }
    }

    /* 处理单个ORC文件 */
    private void processSingleOrc(String tableName, Path orcPath, HdfsOperator operator, String columnLine) {
        long start = System.currentTimeMillis();
        try {
            //列名称
            String[] columnNames = columnLine.split(",");
            //列值
            String[] columnValues;
            Reader reader = OrcFile.createReader(operator.getFileSystem(), orcPath);
            List<SolrInputDocument> docs = new ArrayList<>();
            SolrInputDocument doc;
            RecordReader records = reader.rows();
            Object row = new Object();
            //计数
            int count = 1;
            while (records.hasNext()) {
                //完成一条记录
                String lineData = records.next(row).toString();
                //列值
                columnValues = lineData.substring(1, lineData.length() - 1).split(",");
                doc = new SolrInputDocument();
                //获取MD5值作为id
                doc.addField("id", tableName + "_" + columnValues[columnValues.length - 1]);
                doc.addField("table-name", tableName);
                for (int i = 1; i < columnNames.length; i++) {
                    //跳过MD5值这一列
                    if (!Constant.MD5NAME.equalsIgnoreCase(columnNames[i])) {
                        doc.addField("tf-" + columnNames[i], columnValues[i]);
                    }
                }
                if (doc.size() != 0)
                    docs.add(doc);
                //防止内存溢出
                if (docs.size() >= CommonVariables.SOLR_BULK_SUBMISSIONS_NUM) {
                    server.add(docs);
                    server.commit();
                    docs.clear();
                }
                count++;
            }
            if (docs.size() != 0) {
                server.add(docs);
                server.commit();
            }
            logger.info(count + " 条数据完成索引!");
            logger.info("----Indexing is successful!!!----");
            long stop = System.currentTimeMillis();
            logger.info("建立solr索引共耗时:" + (stop - start) * 1.0 / 1000 + "s");

        } catch (Exception e) {
            logger.error("单个ORC文件入solr失败!" + orcPath);
            throw new BusinessException("单个ORC文件入solr失败!" + orcPath);

        }
    }

    /* 处理单个SequenceFile文件 */
    private void processSingleSeq(String tableName, Path sequenceFilePath, HdfsOperator operator, String columnLine) {
        //记录耗时
        long start = System.currentTimeMillis();
        try (SequenceFile.Reader reader = new SequenceFile.Reader(operator.getConfiguration(),
                SequenceFile.Reader.file(sequenceFilePath))) {
            Writable key = (Writable) ReflectionUtils.newInstance(reader.getKeyClass(), operator.getConfiguration());
            Writable val = (Writable) ReflectionUtils.newInstance(reader.getValueClass(), operator.getConfiguration());
            //列名称
            String[] columnNames = columnLine.split(",");
            //列值
            String[] columnValues;
            //进入solr的记录数
            int count = 0;
            List<SolrInputDocument> docs = new ArrayList<>();
            SolrInputDocument doc;
            while (reader.next(key, val)) {
                //设置记录的边界
                columnValues = StringUtils.splitPreserveAllTokens(val.toString(), Constant.SOLR_DATA_DELIMITER);
                doc = new SolrInputDocument();
                //获取MD5值作为id
                doc.addField("id", tableName + "_" + columnValues[columnValues.length - 1]);
                doc.addField("table-name", tableName);
                for (int i = 1; i < columnNames.length; i++) {
                    //跳过MD5值这一列
                    if (!Constant.MD5NAME.equalsIgnoreCase(columnNames[i])) {
                        doc.addField("tf-" + columnNames[i], columnValues[i]);
                    }
                }
                if (doc.size() != 0)
                    docs.add(doc);
                // 防止内存溢出
                if (docs.size() >= CommonVariables.SOLR_BULK_SUBMISSIONS_NUM) {
                    server.add(docs);
                    server.commit();
                    docs.clear();
                }
                count++;
            }
            if (docs.size() != 0) {
                server.add(docs);
                server.commit();
            }
            logger.info(count + " 条数据完成索引!");
            logger.info("----Indexing is successful!!!----");
            long stop = System.currentTimeMillis();
            logger.info("建立solr索引共耗时:" + (stop - start) * 1.0 / 1000 + "s");
        } catch (Exception e) {
            logger.error("单个SequenceFile文件入solr失败!" + sequenceFilePath);
            throw new BusinessException("单个SequenceFile文件入solr失败!" + sequenceFilePath);
        }
    }
}
