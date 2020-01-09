package hrds.commons.hadoop.opersolr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import hrds.commons.exception.BusinessException;
import hrds.commons.hadoop.hadoop_helper.HBaseHelper;
import hrds.commons.hadoop.hadoop_helper.HdfsOperator;
import hrds.commons.utils.Constant;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hive.ql.io.orc.OrcFile;
import org.apache.hadoop.hive.ql.io.orc.Reader;
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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


public abstract class OperSolrImpl implements OperSolr {

	protected static final Log logger = LogFactory.getLog(OperSolrImpl.class);
	protected static final String handler = "/reloadDictionary";
	protected static final char dataDelimiter = '\001';
	protected SolrClient server = null;

	public OperSolrImpl() {
	}

	/**
	 * @return the server
	 */
	public SolrClient getServer() {

		return server;
	}

	/**
	 * @param server the server to set
	 */
	public void setServer(SolrClient server) {

		this.server = server;
	}

	public void deleteById(List<String> list) throws Exception {

		try {

			server.deleteById(list);
		} catch (Exception e) {
			throw e;
		}
	}

	public void commit() throws Exception {

		try {
			server.commit();
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public List<String> getAnalysis(String sentence) {

		List<String> results = null;
		try {
			FieldAnalysisRequest request = new FieldAnalysisRequest("/analysis/field");
			request.addFieldName("hanlp");// 字段名，随便指定一个支持中文分词的字段
			request.setFieldValue("");// 字段值，可以为空字符串，但是需要显式指定此参数
			request.setQuery(sentence);

			FieldAnalysisResponse response = null;
			try {
				response = request.process(server);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}

			results = new ArrayList<String>();
			Iterator<AnalysisPhase> it = response.getFieldNameAnalysis("hanlp").getQueryPhases().iterator();
			while (it.hasNext()) {
				AnalysisPhase pharse = (AnalysisPhase) it.next();
				List<TokenInfo> list = pharse.getTokens();
				for (TokenInfo info : list) {
					if (!info.getText().equals(" ")) {
						results.add(info.getText());
					}

				}

			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return results;
	}

	/**
	 * @return List类型的json串
	 * @throws Exception
	 */
	@Override
	public List<JSONObject> QuerySolr(String sql, int start, int num) {

		List<JSONObject> solrDocList = new ArrayList<JSONObject>();
		SolrDocumentList solrDocumentList = null;

		try {
			// 创建SolrQuery对象
			SolrQuery solrQuery = new SolrQuery();
			solrQuery.set("q", sql);
			// 设置查询结果的开始行与行数，有待修改为传入“开始行”和“行数”的2个参数。
			solrQuery.setStart(start);
			solrQuery.setRows(num);

			solrQuery.setParam("qt", "/select");
			solrQuery.setParam("wt", "json");
			solrQuery.setParam("indent", "true");
			//			solrQuery.setHighlightSimplePre("<font style='color:red'>");
			//			solrQuery.setHighlightSimplePost("</font>");
			// 执行查询
			QueryResponse queryResponse = server.query(solrQuery);
			logger.info("共有记录：    " + queryResponse.getResults().getNumFound());
			solrDocumentList = queryResponse.getResults();
			int counter = 1;
			//获取搜索到的所有数量
			long sum = queryResponse.getResults().getNumFound();
			JSONObject jsob = null;
			String sub_field = null;
			for (SolrDocument singleDoc : solrDocumentList) {
				jsob = new JSONObject();
				jsob.put("sum", sum);
				for (String field : singleDoc.getFieldNames()) {
					if (!field.equals("_version_") && !field.equals("score") && !field.equals("tf-text_content") && !field.equals("tf-file_text")) {
						//去掉tf-或F-前缀
						if (!field.equals("id") && !field.equals("table-name")) {
							if (field.startsWith("tf-")) {
								sub_field = field.substring(3, field.length()).trim();
							} else if (field.startsWith("F-")) {
								sub_field = field.substring(2, field.length()).trim();
							}
						} else {
							sub_field = field;
						}
						jsob.put(sub_field, singleDoc.getFieldValue(field));
					}
				}
				solrDocList.add(jsob);
				counter++;
			}
			logger.info("counter：    " + counter);
		} catch (Exception e) {
			logger.error(e);
		}
		return solrDocList;
	}

	@Override
	public List<JSONObject> QuerySolrPlus(Map<String, String> params, int start, int num, boolean flag) throws Exception {

		List<JSONObject> solrDocList = new ArrayList<JSONObject>();
		SolrDocumentList solrDocumentList = null;

		try {
			// 创建SolrQuery对象
			SolrQuery solrQuery = new SolrQuery();
			solrQuery.set("q", params.get("q"));
			// 设置查询结果的开始行与行数，有待修改为传入“开始行”和“行数”的2个参数。
			if (0 != num) {
				solrQuery.setStart(start);
				solrQuery.setRows(num);
			}
			solrQuery.set("fq", params.get("fq"));
			solrQuery.setParam("wt", params.get("wt"));
			solrQuery.setParam("indent", params.get("indent"));
			solrQuery.set("sort", params.get("sort"));
			solrQuery.set("fl", params.get("fl"));

			//增加score的域
			solrQuery.setIncludeScore(true);

			// 执行查询
			QueryResponse queryResponse = server.query(solrQuery);
			//			logger.info( "共有记录：    " + queryResponse.getResults().getNumFound());
			solrDocumentList = queryResponse.getResults();
			int counter = 0;
			//获取搜索到的所有数量
			long sum = queryResponse.getResults().getNumFound();
			JSONObject jsob = null;
			String sub_field = null;
			for (SolrDocument singleDoc : solrDocumentList) {
				jsob = new JSONObject();
				jsob.put("sum", sum);
				jsob.put("score", singleDoc.getFieldValue("score"));
				for (String field : singleDoc.getFieldNames()) {

					if (flag) {
						if (!field.equals("_version_") && !field.equals("score") && !field.equals("tf-text_content")
								&& !field.equals("tf-file_text")) {
							//去掉tf-前缀
							if (!field.equals("id") && !field.equals("table-name")) {
								sub_field = field.substring(3, field.length()).trim();
								;
							} else {
								sub_field = field;
							}
							jsob.put(sub_field, singleDoc.getFieldValue(field));
						}
					} else {
						if (!field.equals("_version_") && !field.equals("score") && !field.equals("tf-text_content")) {
							//去掉tf-前缀
							if (!field.equals("id") && !field.equals("table-name")) {
								sub_field = field.substring(3, field.length()).trim();
								;
							} else {
								sub_field = field;
							}
							jsob.put(sub_field, singleDoc.getFieldValue(field));
						}
					}
				}
				solrDocList.add(jsob);
				counter++;
			}
			logger.info("返回结果共有	" + counter + "	条记录");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return solrDocList;
	}

	/**
	 * 全表建立solr索引
	 * solr schema.xml配置如下：
	 * field name="table-name" type="string" indexed="true" stored="true"/>
	 * <dynamicField name="tf-*" type="text_ansj" indexed="true" stored="true"/>
	 *
	 * @param table_name 传入表名
	 * @throws Exception
	 */
	@Override
	public void indexCreateAll(String table_name, HBaseHelper helper) {

		int i = 1;
		int numIndex = 50000;
		ResultScanner scanner = null;
		try (Table table = helper.getTable(table_name)) {
			Scan scan = new Scan();
			scanner = table.getScanner(scan);
			List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
			SolrInputDocument doc = null;
			for (Result scanResult : scanner) {
				doc = new SolrInputDocument();
				doc.addField("id", Bytes.toString(scanResult.getRow()));
				doc.addField("table-name", table_name);
				for (Cell cell : scanResult.listCells()) {
					if (!Bytes.toString(CellUtil.cloneQualifier(cell)).equals("stream_content")) {
						doc.addField("tf-" + Bytes.toString(CellUtil.cloneQualifier(cell)), Bytes.toString(CellUtil.cloneValue(cell)));
						if (doc.size() != 0)
							docs.add(doc);

						// 防止内存溢出
						if (docs != null && docs.size() >= numIndex) {
							server.add(docs);
							server.commit();
							docs.clear();
							logger.info("[info] " + i + " 条数据完成索引！");
						}
						i++;

					}
				}

			}
			if (docs != null && docs.size() != 0) {
				server.add(docs);
				server.commit();
			}
			logger.info("[info] " + i + " 条数据完成索引！");
			logger.info("[info]----Indexing is over!!!----");
			scanner.close();

		} catch (Exception e) {
			logger.error(e);
		}
	}

	/**
	 * 增量建单个索引
	 *
	 * @param table_name 表名
	 * @param row_key    单个row_key
	 * @throws Exception
	 */
	@Override
	public void increasingIndexing(String table_name, String row_key, HBaseHelper helper) {

		int numIndex = 50000;
		ResultScanner scanner = null;
		try (Table table = helper.getTable(table_name)) {
			Scan scan = new Scan();
			// 建立filter,获取指定row_key的scanner结果
			Filter rf = new RowFilter(CompareFilter.CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes(row_key)));
			scan.setFilter(rf);
			scanner = table.getScanner(scan);
			List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
			SolrInputDocument doc = null;
			int i = 0;
			for (Result scanResult : scanner) {
				doc = new SolrInputDocument();
				doc.addField("id", Bytes.toString(scanResult.getRow()));
				doc.addField("table-name", table_name);
				for (Cell cell : scanResult.listCells()) {
					if (!Bytes.toString(CellUtil.cloneQualifier(cell)).equals("stream_content")) {
						doc.addField("tf-" + Bytes.toString(CellUtil.cloneQualifier(cell)), Bytes.toString(CellUtil.cloneValue(cell)));
						if (doc.size() != 0)
							docs.add(doc);
						i++;
						// 防止内存溢出
						if (docs != null && docs.size() >= numIndex) {
							server.add(docs);
							server.commit();
							docs.clear();
							logger.info("[info] " + i + " 条数据完成索引！");
						}

					}
				}
			}
			if (docs != null && docs.size() != 0) {
				server.add(docs);
				server.commit();
				logger.info("[info] " + i + " 条数据完成索引！");
			}
			logger.info("[info]----Indexing is over!!!----");
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (scanner != null)
				scanner.close();
		}
	}

	/**
	 * 增量建批量索引
	 *
	 * @param table_name 表名
	 * @throws Exception
	 */
	@Override
	public void increasingIndexing(String table_name, List<String> row_keys, HBaseHelper helper) {

		ResultScanner scanner = null;
		int numIndex = 50000;
		try (Table table = helper.getTable(table_name)) {
			// 建立filter列表
			ArrayList<Filter> listForFilters = new ArrayList<Filter>();
			for (String singleId : row_keys) {
				if ((null != singleId) && (!"".equals(singleId))) {
					Filter filter = new RowFilter(CompareFilter.CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes(singleId)));// 当前taskID
					listForFilters.add(filter);
				}
			}
			Filter filterList = new FilterList(FilterList.Operator.MUST_PASS_ONE, listForFilters);
			// 经过过滤器(filter)获得扫描结果到scanner
			Scan scan = new Scan();
			scan.setFilter(filterList);// 多条件过滤
			scanner = table.getScanner(scan);
			List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
			SolrInputDocument doc = null;
			int i = 0;
			for (Result scanResult : scanner) {
				doc = new SolrInputDocument();
				doc.addField("id", Bytes.toString(scanResult.getRow()));
				doc.addField("table-name", table_name);

				for (Cell cell : scanResult.listCells()) {

					if (!Bytes.toString(CellUtil.cloneQualifier(cell)).equals("stream_content")) {
						doc.addField("tf-" + Bytes.toString(CellUtil.cloneQualifier(cell)), Bytes.toString(CellUtil.cloneValue(cell)));
						if (doc.size() != 0)
							docs.add(doc);

						// 防止内存溢出
						if (docs != null && docs.size() >= numIndex) {
							server.add(docs);
							server.commit();
							docs.clear();
							logger.info("[info] " + i + " 条数据完成索引！");
						}
						i++;
					}
				}
			}
			if (docs != null && docs.size() != 0) {
				server.add(docs);
				server.commit();
				logger.info("[info] " + i + " 条数据完成索引！");
			}
			logger.info("[info]----Indexing task is completed successfully!!!----");
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (scanner != null)
				scanner.close();
		}
	}

	/*
	 * 按照row_key删除(non-Javadoc)
	 * @see opersolr.OperSolr#deleteIndexById(java.lang.String)
	 */
	@Override
	public void deleteIndexById(String row_key) {

		try {
			server.deleteById(row_key);
			server.commit();
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/*
	 *  按照查询语句删除(non-Javadoc)
	 * @see opersolr.OperSolr#deleteIndexByQuery(java.lang.String)
	 */
	@Override
	public void deleteIndexByQuery(String query) {

		try {
			server.deleteByQuery(query);
			server.commit();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/*
	 * 全索引删除，慎用！！！(non-Javadoc)
	 * @see opersolr.OperSolr#deleteIndexAll()
	 */
	@Override
	public void deleteIndexAll() {

		try {
			server.deleteByQuery("*:*");
			server.commit();
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Override
	/**
	 *
	 * @param table_name 表名
	 * @param hdfspath  HDFS上的路径
	 * @param oper
	 * @param columnline 列名
	 */
	public void indexCreateAll4csv(String table_name, String hdfspath, HdfsOperator operater, String columnline) {

		indexCreateAll4csv(table_name, hdfspath, operater, columnline, table_name);
	}

	public void indexCreateAll4csv(String table_name, String hdfspath, HdfsOperator operater, String columnline, String hbase_name) {

		logger.info("要读取得文件目录是: " + hdfspath);
		Path path = new Path(hdfspath);
		try {
			FileSystem fs = operater.getFileSystem();
			if (fs.isFile(path)) {
				procesSingleCsv(hbase_name.toUpperCase(), new Path(hdfspath), operater, columnline);
			} else {
				FileStatus[] filesStatus = fs.listStatus(path, new PathFilter() {

					@Override
					public boolean accept(Path p) {

						return p.getName().toLowerCase().contains(table_name.toLowerCase());
					}
				});
				for (FileStatus file : filesStatus) {
					logger.info("要读取得文件是: " + file);
					procesSingleCsv(hbase_name.toUpperCase(), file.getPath(), operater, columnline);
				}
			}

		} catch (Exception e) {
			logger.error(e);
		}
	}

	private void procesSingleCsv(String table_name, Path path, HdfsOperator operater, String columnline) {

		//进入solr的记录数
		int count = 0;
		// 设置写solr的频率
		int numIndex = 10000;
		//记录耗时
		long start = System.currentTimeMillis();
		try (ICsvListReader reader = new CsvListReader(operater.toBufferedReader(path), CsvPreference.EXCEL_PREFERENCE)) {
			String[] columnNames = columnline.split(",");
			List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
			SolrInputDocument doc = null;
			List<String> list = null;
			logger.info("[开始读取文件 ] " + path);
			while ((list = reader.read()) != null) {
				//完成一条记录
				doc = new SolrInputDocument();
				//获取MD5值作为id
				doc.addField("id", table_name + "_" + list.get(list.size() - 1));
				doc.addField("table-name", table_name);
				for (int i = 0; i < list.size(); i++) {
					//跳过MD5值这一列
					if (i != list.size() - 1) {
						doc.addField("tf-" + columnNames[i], list.get(i));
					}
				}
				if (doc.size() != 0)
					docs.add(doc);
				// 防止内存溢出
				if (docs != null && docs.size() >= numIndex) {
					server.add(docs);
					server.commit();
					docs.clear();
					logger.info("[info] " + count + " 条数据完成索引！");
				}
				count++;
			}
			if (docs != null && docs.size() != 0) {
				server.add(docs);
				server.commit();
			}
			logger.info("[INFO] " + count + " 条数据完成索引！");
			logger.info("[INFO]----Indexing is successful!!!----");
			long stop = System.currentTimeMillis();
			logger.info("[INFO]建立solr索引共耗时：" + (stop - start) * 1.0 / 1000 + "s");

		} catch (Exception e) {
			logger.error(e);
		}
	}

	public void indexCreateAll4Parquet(String table_name, String path, HdfsOperator operater, String columnline) {

		indexCreateAll4Parquet(table_name, path, operater, columnline, table_name);
	}

	public void indexCreateAll4Parquet(String table_name, String parquetPath, HdfsOperator operater, String columnline, String hbase_name) {

		logger.info("要读取得文件目录是: " + parquetPath);
		Path path = new Path(parquetPath);
		try {
			FileSystem fs = operater.getFileSystem();
			Configuration conf = operater.getConfiguration();
			if (fs.isFile(path)) {
				procesSingleParquet(hbase_name.toUpperCase(), new Path(parquetPath), conf, columnline);
			} else {
				FileStatus[] filesStatus = fs.listStatus(path, new PathFilter() {

					@Override
					public boolean accept(Path p) {

						return p.getName().toLowerCase().contains(table_name.toLowerCase());
					}
				});
				for (FileStatus file : filesStatus) {
					if (file.getLen() == 0) {
						logger.info("待处理文件为空跳过: " + file.getPath());
						continue;
					}
					logger.info("开始处理文件: " + file.getPath());
					procesSingleParquet(hbase_name.toUpperCase(), file.getPath(), conf, columnline);
				}
			}

		} catch (Exception e) {
			throw new BusinessException("读取parquet文件入solr失败：" + e.getMessage());
		}
	}

	private void procesSingleParquet(String table_name, Path parguetPath, Configuration conf, String columnline) {

		//进入solr的记录数
		int count = 0;
		// 设置写solr的频率
		int numIndex = 10000;
		//记录耗时
		long start = System.currentTimeMillis();
		String[] columnNames = columnline.split(",");
		try (ParquetReader<Group> reader = ParquetReader.builder(new GroupReadSupport(), parguetPath).withConf(conf).build();) {

			Group group = null;
			logger.info("[开始读取文件 ] " + parguetPath);
			List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
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
				// 防止内存溢出
				if (docs != null && docs.size() >= numIndex) {
					server.add(docs);
					server.commit();
					docs.clear();
					System.out.println("[info] " + count + " 条数据完成索引！");
				}
				count++;

			}
			if (docs != null && docs.size() != 0) {
				server.add(docs);
				server.commit();
			}
			logger.info("[INFO] " + count + " 条数据完成索引！");
			logger.info("[INFO]----Indexing is successful!!!----");
			long stop = System.currentTimeMillis();
			logger.info("[INFO]建立solr索引共耗时：" + (stop - start) * 1.0 / 1000 + "s");

		} catch (Exception e) {
			throw new BusinessException("处理单个文件入solr失败： " + parguetPath + "=====" + e.getMessage());
		}
	}

	@Override
	/**
	 * 针对请求solr的自定义handler，需要返回值的情况
	 * @param is
	 * 							是否需要返回值
	 * @param parameters
	 * 							键值对参数
	 * @param temp
	 * 							可以为多个或空，默认："/reloadDictionary"
	 * @return
	 *                            返回运行情况，key为输入参数temp，value为返回信息或者错误信息
	 * @throws IOException
	 * @throws SolrServerException
	 */
	public JSONArray requestHandler(boolean is, JSONObject parameters, String... temp) {

		temp = temp.length < 1 ? temp = new String[]{handler} : temp;

		if (!is)
			return requestHandler(temp);

		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		SolrQuery sq = new SolrQuery();

		if (!parameters.isEmpty()) {
			for (Map.Entry<String, Object> entry : parameters.entrySet()) {
				sq.set(entry.getKey(), entry.getValue().toString());
			}
		}

		QueryResponse response = null;

		for (String handler : temp) {

			sq.setRequestHandler(handler);

			try {
				response = server.query(sq);
			} catch (SolrServerException | IOException e) {

				jsonObject.put(handler, e);

				e.printStackTrace();
			}

			jsonObject.put(handler, response.getResponse());
			jsonArray.add(jsonObject);

			logger.info("[INFO] Spend time on request to custom handler    " + handler + " : " + response.getQTime() + " ms");
		}

		return jsonArray;
	}

	@Override
	public SolrDocumentList queryByField(String fieldName, String value, String responseColumns, int rows) {

		try {
			SolrQuery solrQuery = new SolrQuery();
			solrQuery.set("q", fieldName + ":" + value);
			solrQuery.set("fl", responseColumns);
			solrQuery.setParam("qt", "/select");
			solrQuery.setParam("wt", "json");
			solrQuery.setParam("indent", "true");
			solrQuery.setRows(rows);
			return server.query(solrQuery).getResults();
		} catch (SolrServerException | IOException e) {
			logger.error(e);
		}
		return null;
	}

	public void indexCreateAll4Orc(String table_name, String hdfspath, HdfsOperator operater, String columnline) {

		indexCreateAll4Orc(table_name, hdfspath, operater, columnline, table_name);
	}

	public void indexCreateAll4Orc(String table_name, String hdfspath, HdfsOperator operater, String columnline, String hbase_name) { //进入solr的记录数

		Path path = new Path(hdfspath);
		logger.info("要读取得文件目录是: " + hdfspath);
		try {
			FileSystem fs = operater.getFileSystem();
			if (fs.isFile(path)) {
				procesSingleOrc(hbase_name.toUpperCase(), new Path(hdfspath), operater, columnline);
			} else {
				FileStatus[] filesStatus = fs.listStatus(path, new PathFilter() {

					@Override
					public boolean accept(Path p) {

						return p.getName().toLowerCase().contains(table_name.toLowerCase());
					}
				});
				for (FileStatus file : filesStatus) {
					logger.info("要读取得文件是: " + file);
					procesSingleOrc(hbase_name.toUpperCase(), file.getPath(), operater, columnline);
				}
			}

		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
	}

	private void procesSingleOrc(String table_name, Path path, HdfsOperator operater, String columnline) {

		int count = 1;
		// 设置写solr的频率
		int numIndex = 10000;
		//记录耗时
		long start = System.currentTimeMillis();
		org.apache.hadoop.hive.ql.io.orc.RecordReader records = null;
		try {
			String[] columnNames = columnline.split(",");//列名称
			String[] columns = null;
			Reader reader = OrcFile.createReader(operater.getFileSystem(), path);
			List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
			SolrInputDocument doc = null;
			records = reader.rows();
			Object row = null;
			while (records.hasNext()) {
				//完成一条记录
				String lineData = records.next(row).toString();
				columns = lineData.substring(1, lineData.length() - 1).split(",");//列值
				doc = new SolrInputDocument();
				//获取MD5值作为id
				doc.addField("id", table_name + "_" + columns[columns.length - 1]);
				doc.addField("table-name", table_name);
				for (int i = 1; i < columnNames.length; i++) {
					//跳过MD5值这一列
					if (!Constant.MD5NAME.equalsIgnoreCase(columnNames[i])) {
						doc.addField("tf-" + columnNames[i], columns[i]);
					}
				}
				if (doc.size() != 0)
					docs.add(doc);
				// 防止内存溢出
				if (docs != null && docs.size() >= numIndex) {
					server.add(docs);
					server.commit();
					docs.clear();
				}
				count++;
			}
			if (docs != null && docs.size() != 0) {
				server.add(docs);
				server.commit();
			}
			logger.info("[INFO] " + count + " 条数据完成索引！");
			logger.info("[INFO]----Indexing is successful!!!----");
			long stop = System.currentTimeMillis();
			logger.info("[INFO]建立solr索引共耗时：" + (stop - start) * 1.0 / 1000 + "s");

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {
				if (records != null) {
					records.close();
				}
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}

	public void indexCreateAll4Sequence(String table_name, String hdfspath, HdfsOperator operater, String columnline) {

		indexCreateAll4Sequence(table_name, hdfspath, operater, columnline, table_name);
	}

	public void indexCreateAll4Sequence(String table_name, String hdfspath, HdfsOperator operater, String columnline, String hbase_name) {

		logger.info("要读取得文件目录是: " + hdfspath);
		Path path = new Path(hdfspath);
		try {
			FileSystem fs = operater.getFileSystem();
			if (fs.isFile(path)) {
				procesSingleSeq(hbase_name.toUpperCase(), new Path(hdfspath), operater, columnline);
			} else {
				FileStatus[] filesStatus = fs.listStatus(path, new PathFilter() {

					@Override
					public boolean accept(Path p) {

						return p.getName().toLowerCase().contains(table_name.toLowerCase());
					}
				});
				for (FileStatus file : filesStatus) {
					logger.info("要读取得文件是: " + file);
					procesSingleSeq(hbase_name.toUpperCase(), file.getPath(), operater, columnline);
				}
			}

		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	private void procesSingleSeq(String table_name, Path path, HdfsOperator operater, String columnline) {

		//进入solr的记录数
		int count = 0;
		// 设置写solr的频率
		int numIndex = 10000;
		//记录耗时
		long start = System.currentTimeMillis();
		SequenceFile.Reader reader = null;
		FileSystem fs = null;
		try {
			String[] columnNames = columnline.split(",");//列名称
			String[] columns = null;
			fs = operater.getFileSystem();
			reader = new SequenceFile.Reader(fs, path, operater.getConfiguration());
			Writable key = (Writable) ReflectionUtils.newInstance(reader.getKeyClass(), operater.getConfiguration());
			Writable value = (Writable) ReflectionUtils.newInstance(reader.getValueClass(), operater.getConfiguration());
			List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
			SolrInputDocument doc = null;
			logger.info("[开始读取文件 ] " + path);
			while (reader.next(key, value)) {
				//同步记录的边界
				//完成一条记录
				columns = StringUtils.splitPreserveAllTokens(value.toString(), dataDelimiter);//列值
				System.out.println(Arrays.toString(columns));
				doc = new SolrInputDocument();
				//获取MD5值作为id
				doc.addField("id", table_name + "_" + columns[columns.length - 1]);
				doc.addField("table-name", table_name);
				for (int i = 1; i < columnNames.length; i++) {
					//跳过MD5值这一列
					if (!Constant.MD5NAME.equalsIgnoreCase(columnNames[i])) {
						doc.addField("tf-" + columnNames[i], columns[i]);
					}
				}
				if (doc.size() != 0)
					docs.add(doc);
				// 防止内存溢出
				if (docs != null && docs.size() >= numIndex) {
					server.add(docs);
					server.commit();
					docs.clear();
					System.out.println("[info] " + count + " 条数据完成索引！");
				}
				count++;
			}
			if (docs != null && docs.size() != 0) {
				server.add(docs);
				server.commit();
			}
			logger.info("[INFO] " + count + " 条数据完成索引！");
			logger.info("[INFO]----Indexing is successful!!!----");
			long stop = System.currentTimeMillis();
			logger.info("[INFO]建立solr索引共耗时：" + (stop - start) * 1.0 / 1000 + "s");

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {
				reader.close();
				fs.close();
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}

}
