package hrds.agent.job.biz.core.filecollectstage.methods;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.utils.DateUtil;
import hrds.agent.job.biz.bean.AvroBean;
import hrds.agent.job.biz.bean.FileCollectParamBean;
import hrds.agent.job.biz.bean.ObjectCollectParamBean;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.core.increasement.HBaseIncreasement;
import hrds.agent.job.biz.utils.CommunicationUtil;
import hrds.commons.codes.AgentType;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Source_file_attribute;
import hrds.commons.exception.AppSystemException;
import hrds.commons.hadoop.hadoop_helper.HBaseHelper;
import hrds.commons.hadoop.readconfig.ConfigReader;
import hrds.commons.hadoop.solr.ISolrOperator;
import hrds.commons.hadoop.solr.SolrFactory;
import hrds.commons.hadoop.solr.SolrParam;
import hrds.commons.utils.Constant;
import hrds.commons.utils.FileTypeUtil;
import hrds.commons.utils.PropertyParaUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.common.SolrInputDocument;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentMap;


/**
 * 对AvroBeans进行处理（进mysql，solr，hbase）
 */
public class AvroBeanProcess {

	private static final Log logger = LogFactory.getLog(AvroBeanProcess.class);

	private static final byte[] FAP = "file_avro_path".getBytes();
	private static final byte[] FAB = "file_avro_block".getBytes();
	private static final byte[] CF = Constant.HBASE_COLUMN_FAMILY;
	//插入的sql
	private static final String addSql = "INSERT " +
			"INTO " +
			"    Source_file_attribute " +
			"    (" +
			"        agent_id," +
			"        collect_set_id," +
			"        collect_type," +
			"        file_avro_block," +
			"        file_avro_path," +
			"        file_id," +
			"        file_md5," +
			"        file_size," +
			"        file_suffix," +
			"        file_type," +
			"        hbase_name," +
			"        is_big_file," +
			"        is_in_hbase," +
			"        meta_info," +
			"        original_name," +
			"        original_update_date," +
			"        original_update_time," +
			"        source_id," +
			"        source_path," +
			"        storage_date," +
			"        storage_time," +
			"        table_name" +
			"    ) " +
			"    VALUES " +
			"    ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

	private static final String updateSql = "UPDATE " +
			"    source_file_attribute " +
			"SET " +
			"    storage_date = ?," +
			"    storage_time = ?," +
			"    original_update_date = ?," +
			"    Original_update_time = ?," +
			"    file_md5 = ?," +
			"    file_avro_path = ?," +
			"    file_avro_block = ? " +
			"WHERE " +
			"    file_id = ?";
	private final FileCollectParamBean fileCollectParamBean;
	private final String sysDate;
	private final String job_rs_id;

	public AvroBeanProcess(FileCollectParamBean fileCollectParamBean, String sysDate, String job_rs_id) {
		this.fileCollectParamBean = fileCollectParamBean;
		this.sysDate = sysDate;
		this.job_rs_id = job_rs_id;
	}

	/**
	 * 将AvroBean存入到元数据库
	 *
	 * @param avroBeans avro文件抽取出来的信息的集合
	 * @return 需要插入到hbase的信息
	 */
	public List<String[]> saveMetaData(List<AvroBean> avroBeans, ConcurrentMap<String, String> fileNameHTreeMap) {
		logger.info("保存已采集文件信息到元数据库开始...");
		//存储全量插入信息的list
		List<Object[]> addParamsPool = new ArrayList<>();
		// 存储update信息的list
		List<Object[]> updateParamsPool = new ArrayList<>();
		//Hbase插入信息存储List
		List<String[]> hbaseList = new ArrayList<>();
		try {
			//循环遍历每一个文件信息，一个avroBeans存放一个Avro文件的信息，一个avroBean存放一个文件的信息
			for (AvroBean avroBean : avroBeans) {
				Source_file_attribute attribute = new Source_file_attribute();
				// avroBean中存的Uuid，肯定是正确的，如果是增量，则Uuid肯定是存在于数据库中
				String fileId = avroBean.getUuid();
				//TODO 这里有一个缓存采集的处理逻辑，待讨论
				String rowKey;
				if (IsFlag.Fou.getCode().equals(avroBean.getIs_increasement())) {//全量
					Object[] addFileAttributeList = new Object[22];
					attribute.setAgent_id(fileCollectParamBean.getAgent_id());
					attribute.setCollect_set_id(fileCollectParamBean.getFcs_id());
					attribute.setCollect_type(AgentType.WenJianXiTong.getCode());
					attribute.setFile_avro_block(avroBean.getFile_avro_block());
					attribute.setFile_avro_path(avroBean.getFile_avro_path());
					attribute.setFile_id(avroBean.getUuid());
					attribute.setFile_md5(avroBean.getFile_md5());
					attribute.setFile_size(avroBean.getFile_size());
					attribute.setFile_suffix(FilenameUtils.getExtension(avroBean.getFile_name()));
					attribute.setFile_type(FileTypeUtil.fileTypeCode(FilenameUtils.getExtension(avroBean.getFile_name())));
					attribute.setHbase_name(Bytes.toString(ObjectCollectParamBean.FILE_HBASE));
					attribute.setIs_big_file(avroBean.getIs_big_file());
					attribute.setIs_in_hbase("");
					attribute.setMeta_info("");
					attribute.setOriginal_name(avroBean.getFile_name());
					//XXX 这个时间日期一定要按照我们的格式吗，这里需要修改吗，下面用的那个方法是不是要加到工具类里面
					attribute.setOriginal_update_date(stringToDate(avroBean.getFile_time(), "yyyyMMdd"));
					attribute.setOriginal_update_time(stringToDate(avroBean.getFile_time(), "HHmmss"));
					attribute.setSource_id(fileCollectParamBean.getSource_id());
					attribute.setSource_path(avroBean.getFile_scr_path());
					attribute.setStorage_date(DateUtil.getSysDate());
					attribute.setStorage_time(DateUtil.getSysTime());
					attribute.setTable_name("");
					//全量插入
					addFileAttributeList[0] = attribute.getAgent_id();
					addFileAttributeList[1] = attribute.getCollect_set_id();
					addFileAttributeList[2] = attribute.getCollect_type();
					addFileAttributeList[3] = attribute.getFile_avro_block();
					addFileAttributeList[4] = attribute.getFile_avro_path();
					addFileAttributeList[5] = attribute.getFile_id();
					addFileAttributeList[6] = attribute.getFile_md5();
					addFileAttributeList[7] = attribute.getFile_size();
					addFileAttributeList[8] = attribute.getFile_suffix();
					addFileAttributeList[9] = attribute.getFile_type();
					addFileAttributeList[10] = attribute.getHbase_name();
					addFileAttributeList[11] = attribute.getIs_big_file();
					addFileAttributeList[12] = attribute.getIs_in_hbase();
					addFileAttributeList[13] = attribute.getMeta_info();
					addFileAttributeList[14] = attribute.getOriginal_name();
					addFileAttributeList[15] = attribute.getOriginal_update_date();
					addFileAttributeList[16] = attribute.getOriginal_update_time();
					addFileAttributeList[17] = attribute.getSource_id();
					addFileAttributeList[18] = attribute.getSource_path();
					addFileAttributeList[19] = attribute.getStorage_date();
					addFileAttributeList[20] = attribute.getStorage_time();
					addFileAttributeList[21] = attribute.getTable_name();
//					logger.info(addFileAttributeList);
					addParamsPool.add(addFileAttributeList);
					//插入到Hbase该行的rowKey为：file_id+md5
					rowKey = fileId + "_" + avroBean.getFile_md5();
				} else if (IsFlag.Shi.getCode().equals(avroBean.getIs_increasement())) {//增量
					Object[] updateFileAttributeList = new Object[8];
					attribute.setStorage_date(DateUtil.getSysDate());
					attribute.setStorage_time(DateUtil.getSysTime());
					//XXX 这个时间日期一定要按照我们的格式吗，这里需要修改吗，下面用的那个方法是不是要加到工具类里面
					attribute.setOriginal_update_date(stringToDate(avroBean.getFile_time(), "yyyyMMdd"));
					attribute.setOriginal_update_time(stringToDate(avroBean.getFile_time(), "HHmmss"));
					attribute.setFile_md5(avroBean.getFile_md5());
					attribute.setFile_size(avroBean.getFile_size());
					attribute.setFile_avro_path(avroBean.getFile_avro_path());
					attribute.setFile_avro_block(avroBean.getFile_avro_block());
					attribute.setFile_id(fileId);
					updateFileAttributeList[0] = attribute.getStorage_date();
					updateFileAttributeList[1] = attribute.getStorage_time();
					updateFileAttributeList[2] = attribute.getOriginal_update_date();
					updateFileAttributeList[3] = attribute.getOriginal_update_time();
					updateFileAttributeList[4] = attribute.getFile_md5();
					updateFileAttributeList[5] = attribute.getFile_avro_path();
					updateFileAttributeList[6] = attribute.getFile_avro_block();
					updateFileAttributeList[7] = attribute.getFile_id();
					updateParamsPool.add(updateFileAttributeList);
					/* Hbase数据处理 这是上次文件的MD5，作为Hbase主键的一部分 */
					String md5 = JSONObject.parseObject(fileNameHTreeMap.get(avroBean.getFile_scr_path())).
							getString("file_md5");
//					需要关链的行 rowkey为： ${file_id}_${上次插入的文件的md5}
					String[] guanlian = new String[]{fileId + "_" + md5};
					hbaseList.add(guanlian);
					rowKey = fileId + "_" + avroBean.getFile_md5();
				} else {
					throw new AppSystemException("Is_increasement: 的值异常===" + avroBean.getIs_increasement());
				}
				//记录顺序为：rowKey,md5,file_avro_path,file_avro_block
				String[] zengliang = new String[]{rowKey, avroBean.getFile_md5(), avroBean.getFile_avro_path(), avroBean.getFile_avro_block()};
				hbaseList.add(zengliang);
			}
			//判断是否成功插入数据库，如果全部成功才能执行下面的hbase插入和solr插入
			logger.info("addParamsPool.size(): " + addParamsPool.size());
			if (addParamsPool.size() > 0) {
				logger.info("执行全量插入数据（addParamsPool）（executeBatch...）");
				CommunicationUtil.batchAddSourceFileAttribute(addParamsPool, addSql, job_rs_id);
			}
			logger.info("updateParamsPool.size(): " + updateParamsPool.size());
			if (updateParamsPool.size() > 0) {
				logger.info("执行增量插入数据（updateParamsPool）（executeBatch...）");
				CommunicationUtil.batchUpdateSourceFileAttribute(updateParamsPool, updateSql, job_rs_id);
			}
			logger.info("保存已采集文件信息到元数据库结束...");
		} catch (Exception e) {
			logger.error("保存文件信息到元数据库异常", e);
			throw new AppSystemException("保存文件信息到元数据库异常" + e.getMessage());
		}
		return hbaseList;
	}

	/**
	 * XXX 不使用HBASE时 用postgresql代替,此方法需要修改
	 */
	public void saveInPostgreSupersedeHbase(List<String[]> hbaseList) {
		//TODO 根据存储目的地，将值存到对应的地方
		System.out.println(hbaseList);
//		logger.info("Start to saveInPostgre...");
////		SQLExecutor db = null;
//		if (hbaseList == null) {
//			return;
//		}
//		try {
////			db = new SQLExecutor();
//			//存储全量插入信息的list
//			List<List<Object>> addParamsPool = new ArrayList<>();
//			//存储update信息的list
//			List<List<Object>> updateParamsPool = new ArrayList<>();
//
//			StringBuilder sbAdd = new StringBuilder();//插入的sql
//			sbAdd.append("insert into file_hbase(");
//			sbAdd.append(" rowkey,hyren_s_date,hyren_e_date,file_md5,file_avro_path,file_avro_block) values(?,?,?,?,?,?)");
//			StringBuilder sbUpdate = new StringBuilder();//update的sql
//			sbUpdate.append("update file_hbase set hyren_e_date = ? where rowkey = ?");
////			db.beginTrans();//开始事物
//			for (String[] strs : hbaseList) {
//				if (strs.length == 1) {//关链
//					List<Object> updateList = new ArrayList<>();
//					updateList.add(sysDate);
//					updateList.add(strs[0]);
//					updateParamsPool.add(updateList);
//				} else if (strs.length == 4) {
//					List<Object> addFileList = new ArrayList<>();
//					addFileList.add(strs[0]);
//					addFileList.add(sysDate);
//					addFileList.add(Constant.MAXDATE);
//					addFileList.add(strs[1]);
//					addFileList.add(strs[2]);
//					addFileList.add(Long.parseLong(strs[3]));
//					addParamsPool.add(addFileList);
//				} else {
//					throw new BusinessException("saving Hbase fails because strs.length is wrong ...");
//				}
//			}
//
//			/*
//			 * 判断是否成功插入数据库，如果全部成功才能执行下面的hbase插入和solr插入
//			 */
//			boolean flag = true;
//			logger.info("addParamsPool.size(): " + addParamsPool.size());
//			if (addParamsPool.size() != 0) {
//				logger.info("执行全量插入数据（addParamsPool）（executeBatch...）");
////				flag = db.executeBatch(sbAdd.toString(), addParamsPool);
//			}
//			logger.info("updateParamsPool.size(): " + updateParamsPool.size());
//			if (updateParamsPool.size() != 0) {
//				logger.info("执行增量插入数据（updateParamsPool）（executeBatch...）");
////				flag = db.executeBatch(sbUpdate.toString(), updateParamsPool);
//			}
//			//数据库更新未成功，则全部回滚
//			if (!flag) {
////				db.rollback();
//				logger.error("增量加载数据库失败！");
//				CollectionWatcher.saveErrorInfo(jobRsId, new Exception("增量加载数据库失败！"));
//			}
////			db.commit();
//		} catch (Exception e) {
//			CollectionWatcher.saveErrorInfo(jobRsId, e);
//			logger.error("Failed to putInHbase", e);
//		}
////		finally {
////			if( db != null ) {
////				db.close();
////			}
////		}
	}

	/**
	 * 在Hbase中存入信息和拉链
	 */
	public void saveInHbase(List<String[]> hbaseList) throws IOException {

		logger.info("Start to saveInHbase...");
		if (hbaseList == null) {
			return;
		}
		List<Put> putList = new ArrayList<>();
		/*
		一条记录的更新应该会遍历两次：
		一次是关链（数组长度为1），一次是新增（数组长度为4）
		 */
		for (String[] str : hbaseList) {
			Put put = new Put(str[0].getBytes());
			//关链
			if (str.length == 1) {
				put.addColumn(CF, FileCollectParamBean.E_DATE, sysDate.getBytes());
			} else if (str.length == 3) {
				put.addColumn(CF, FAP, str[1].getBytes());
				put.addColumn(CF, FAB, str[2].getBytes());
			} else if (str.length == 4) {
				put.addColumn(CF, FileCollectParamBean.S_DATE, sysDate.getBytes());
				put.addColumn(CF, FileCollectParamBean.E_DATE, FileCollectParamBean.MAXDATE);
				put.addColumn(CF, FAP, str[1].getBytes());
				put.addColumn(CF, FAB, str[2].getBytes());
			} else {
				throw new AppSystemException("saving Hbase fails because strs.length is wrong ...");
			}
			putList.add(put);
		}
		Table table = null;
		try (HBaseHelper helper = HBaseHelper.getHelper(ConfigReader.getConfiguration(
				System.getProperty("user.dir") + File.separator + "conf" + File.separator,
				PropertyParaUtil.getString("platform", ConfigReader.PlatformType.normal.toString()),
				PropertyParaUtil.getString("principle.name", "admin@HADOOP.COM"),
				PropertyParaUtil.getString("HADOOP_USER_NAME", "hyshf")))) {
			if (!helper.existsTable(FileCollectParamBean.FILE_HBASE_NAME)) {
				HBaseIncreasement.createDefaultPrePartTable(helper, FileCollectParamBean.FILE_HBASE_NAME,
						true);
			}
			table = helper.getTable(Bytes.toString(FileCollectParamBean.FILE_HBASE));
			//数据插入到HBase
			table.put(putList);
		} catch (Exception e) {
			logger.error("Failed to putInHbase", e);
			throw new AppSystemException("Failed to putInHbase..." + e.getMessage());
		} finally {
			if (table != null)
				table.close();
		}
	}

	public void saveInSolr(List<AvroBean> avroBeans) {

		logger.info("开始进solr...");
		long start = System.currentTimeMillis();
		//进入solr的记录数
		int count = 0;
		int commitNumber = 500;
		SolrParam solrParam = new SolrParam();
		solrParam.setSolrZkUrl(JobConstant.SOLRZKHOST);
		solrParam.setCollection(JobConstant.SOLRCOLLECTION);
		// TODO
		try (ISolrOperator os = SolrFactory.getInstance(JobConstant.SOLRCLASSNAME, solrParam,
				System.getProperty("user.dir") + File.separator + "conf" + File.separator)) {
			SolrClient server = os.getServer();
			List<SolrInputDocument> docs = new ArrayList<>();
			SolrInputDocument doc;
			for (AvroBean avroBean : avroBeans) {
				//如果id_cache这个字段不为null，就表示这个记录是通过本地缓存（单文件上传接口）的
//				if (!StringUtils.isBlank(avroBean.getIs_cache())) {
//					SolrDocument sd = server.getById(avroBean.getUuid());
//					if (sd == null) {
//						throw new IllegalStateException("File id exsits in meta store but not in solr!");
//					}
//					doc = ClientUtils.toSolrInputDocument(sd);
//					doc.removeField("tf-file_avro_path");
//					doc.addField("tf-file_avro_path", avroBean.getFile_avro_path());
//					doc.removeField("tf-file_avro_block");
//					doc.addField("tf-file_avro_block", avroBean.getFile_avro_block());
//					docs.add(doc);
//				} else {
				//文本处理
				doc = new SolrInputDocument();
				doc.addField("id", avroBean.getUuid());
				doc.addField("tf-collect_type", AgentType.WenJianXiTong.getCode());
				doc.addField("tf-file_name", avroBean.getFile_name());
				//source_path
				doc.addField("tf-file_scr_path", avroBean.getFile_scr_path());
				doc.addField("tf-file_size", avroBean.getFile_size());
				doc.addField("tf-file_time", avroBean.getFile_time());
				doc.addField("tf-file_summary", avroBean.getFile_summary());
				doc.addField("tf-file_text", avroBean.getFile_text());
				doc.addField("tf-file_md5", avroBean.getFile_md5());
				doc.addField("tf-file_avro_path", avroBean.getFile_avro_path());
				doc.addField("tf-file_avro_block", avroBean.getFile_avro_block());
				doc.addField("tf-is_big_file", avroBean.getIs_big_file());

				doc.addField("tf-file_suffix", FilenameUtils.getExtension(avroBean.getFile_name()));
				doc.addField("tf-storage_date", DateUtil.getSysDate());
				doc.addField("tf-fcs_id", fileCollectParamBean.getFcs_id());
				doc.addField("tf-fcs_name", fileCollectParamBean.getFcs_name());

				doc.addField("tf-agent_id", fileCollectParamBean.getAgent_id());
				doc.addField("tf-agent_name", fileCollectParamBean.getAgent_name());

				doc.addField("tf-source_id", fileCollectParamBean.getSource_id());
				doc.addField("tf-datasource_name", fileCollectParamBean.getDatasource_name());
				doc.addField("tf-dep_id", fileCollectParamBean.getDep_id());
				docs.add(doc);
//				}

				// 将solr的document加入到list中去
				count++;
				// 防止内存溢出
				if (count % commitNumber == 0) {
					server.add(docs);
					server.commit();
					docs.clear();
					logger.info("[info] " + count + " 条数据完成索引！");
				}
			}

			// 提交到solr
			if (docs.size() != 0) {
				server.add(docs);
				server.commit();
				logger.info("一共" + count + " 条数据完成索引！");
				docs.clear();
			}
			logger.info("成功建立solr索引,共耗时：" + (System.currentTimeMillis() - start) * 1.0 / 1000 + "秒");
		} catch (Exception e) {
			logger.error("数据进solr失败...", e);
			throw new AppSystemException("数据进solr失败..." + e.getMessage());
		}
	}

	/**
	 * 将long类型的毫秒数时间转换成固定格式的时间字符串
	 *
	 * @param lo     毫秒数时间
	 * @param format 时间字符串格式如： yyyy-MM-dd HH:mm:ss
	 */
	private String stringToDate(String lo, String format) {
		long time = Long.parseLong(lo);
		Date date = new Date(time);
		SimpleDateFormat sd = new SimpleDateFormat(format);
		return sd.format(date);
	}

}
