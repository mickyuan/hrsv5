package hrds.agent.job.biz.core.objectstage;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.FileNameUtils;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.agent.job.biz.bean.*;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.constant.RunStatusConstant;
import hrds.agent.job.biz.constant.StageConstant;
import hrds.agent.job.biz.core.AbstractJobStage;
import hrds.agent.job.biz.core.dfstage.DFDataLoadingStageImpl;
import hrds.agent.job.biz.core.dfstage.bulkload.*;
import hrds.agent.job.biz.core.increasement.HBaseIncreasement;
import hrds.agent.job.biz.utils.JobStatusInfoUtil;
import hrds.commons.codes.*;
import hrds.commons.collection.ConnectionTool;
import hrds.commons.exception.AppSystemException;
import hrds.commons.hadoop.hadoop_helper.HBaseHelper;
import hrds.commons.hadoop.readconfig.ConfigReader;
import hrds.commons.hadoop.utils.HSqlExecute;
import hrds.commons.utils.Constant;
import hrds.commons.utils.StorageTypeKey;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.ToolRunner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@DocClass(desc = "半结构化对象采集数据加载实现", author = "zxz", createdate = "2019/10/24 11:43")
public class ObjectLoadingDataStageImpl extends AbstractJobStage {
	//打印日志
	private static final Logger log = LogManager.getLogger();
	//多条半结构化对象采集存储到hadoop存储信息实体合集
	private final ObjectTableBean objectTableBean;
//	//整条数据进hbase时的列名称
//	private static final String CONTENT = "content";
//	//开始日期
//	private static final String HYREN_S_DATE = "hyren_s_date";

	/**
	 * 半结构化对象采集数据加载实现类构造方法
	 *
	 * @param objectTableBean ObjectTableBean
	 *                        含义：多条半结构化对象采集存储到hadoop配置信息实体合集
	 *                        取值范围：所有这个实体不能为空的字段的值必须有，为空则会抛异常
	 */
	public ObjectLoadingDataStageImpl(ObjectTableBean objectTableBean) {
		this.objectTableBean = objectTableBean;
	}

	@Method(desc = "半结构化对象采集数据加载阶段处理逻辑，处理完成后，无论成功还是失败，" +
			"将相关状态信息封装到StageStatusInfo对象中返回", logicStep = "")
	@Return(desc = "StageStatusInfo是保存每个阶段状态信息的实体类", range = "不会为null,StageStatusInfo实体类对象")
	@Override
	public StageParamInfo handleStage(StageParamInfo stageParamInfo) {
		long startTime = System.currentTimeMillis();
		log.info("------------------表" + objectTableBean.getEn_name()
				+ "半结构化对象采集数据加载阶段开始------------------");
		//1、创建卸数阶段状态信息，更新作业ID,阶段名，阶段开始时间
		StageStatusInfo statusInfo = new StageStatusInfo();
		JobStatusInfoUtil.startStageStatusInfo(statusInfo, objectTableBean.getOcs_id(),
				StageConstant.DATALOADING.getCode());
		try {
			String todayTableName = objectTableBean.getHyren_name() + "_" + 1;
			String hdfsPath = FileNameUtils.normalize(JobConstant.PREFIX + File.separator
					+ objectTableBean.getOdc_id() + File.separator + objectTableBean.getHyren_name()
					+ File.separator, true);
			List<DataStoreConfBean> dataStoreConfBeanList = objectTableBean.getDataStoreConfBean();
			for (DataStoreConfBean dataStoreConfBean : dataStoreConfBeanList) {
				if (Store_type.HIVE.getCode().equals(dataStoreConfBean.getStore_type())) {
					//设置hive的默认类型
					dataStoreConfBean.getData_store_connect_attr().put(StorageTypeKey.database_type,
							DatabaseType.Hive.getCode());
					createHiveTableLoadData(todayTableName, hdfsPath, dataStoreConfBean,
							stageParamInfo.getTableBean());
				} else if (Store_type.HBASE.getCode().equals(dataStoreConfBean.getStore_type())) {
					bulkloadLoadDataToHbase(todayTableName, hdfsPath, objectTableBean.getEtlDate(), dataStoreConfBean,
							stageParamInfo.getTableBean());
				}
			}
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.SUCCEED.getCode(), "执行成功");
			log.info("------------------表" + objectTableBean.getEn_name()
					+ "半结构化对象采集数据加载阶段成功------------------执行时间为："
					+ (System.currentTimeMillis() - startTime) / 1000 + "，秒");
		} catch (Exception e) {
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.FAILED.getCode(), "执行失败");
			log.error("表" + objectTableBean.getEn_name()
					+ "半结构化对象采集数据加载阶段失败：", e);
		}
		//结束给stageParamInfo塞值
		JobStatusInfoUtil.endStageParamInfo(stageParamInfo, statusInfo, objectTableBean
				, AgentType.DuiXiang.getCode());
		return stageParamInfo;
	}

	public static void createHiveTableLoadData(String todayTableName, String hdfsFilePath, DataStoreConfBean
			dataStoreConfBean, TableBean tableBean) {
		try (DatabaseWrapper db = ConnectionTool.getDBWrapper(dataStoreConfBean.getData_store_connect_attr())) {
			List<String> sqlList = new ArrayList<>();
			//1.如果表存在,删除当天卸数的表
			if (db.isExistTable(todayTableName)) {
				db.execute("DROP TABLE " + todayTableName);
			}
			//2.创建表
			sqlList.add(DFDataLoadingStageImpl.genHiveLoad(
					todayTableName, tableBean, tableBean.getColumn_separator()));
			//3.加载数据
			sqlList.add("load data inpath '" + hdfsFilePath + "' into table " + todayTableName);
			//4.执行sql语句
			HSqlExecute.executeSql(sqlList, db);
		} catch (Exception e) {
			throw new AppSystemException("执行hive加载数据的sql报错", e);
		}
	}

//	@Method(desc = "半结构化对象采集，数据加载阶段实现，处理完成后，无论成功还是失败，" +
//			"将相关状态信息封装到StageStatusInfo对象中返回"
//			, logicStep = "1.设置数据加载阶段的任务id,开始日期开始时间" +
//			"2.取客户给定的采集目录下取到最新的目录（修改日期最近的），作为采集目录" +
//			"3.遍历对象采集任务，根据对应的配置找到指定的文件进行采集" +
//			"4.设置数据加载阶段结束日期结束时间并返回")
//	@Return(desc = "StageStatusInfo是保存每个阶段状态信息的实体类", range = "不会为null,StageStatusInfo实体类对象")
//	@Override
//	public StageParamInfo handleStage(StageParamInfo stageParamInfo) {
//		//1.设置数据加载阶段的任务id,开始日期开始时间
//		StageStatusInfo statusInfo = new StageStatusInfo();
//		statusInfo.setJobId(String.valueOf(objectCollectParamBean.getOdc_id()));
//		statusInfo.setStageNameCode(StageConstant.DATALOADING.getCode());
//		statusInfo.setStartDate(DateUtil.getSysDate());
//		statusInfo.setStartTime(DateUtil.getSysTime());
//		try {
//			String file_path = objectCollectParamBean.getFile_path();
//			//2.取客户给定的采集目录下取到最新的目录（修改日期最近的），作为采集目录
//			String collectDir = getNewestDir(file_path);
//			collectDir = FileNameUtils.normalize(collectDir, true);
//			//3.遍历对象采集任务，根据对应的配置找到指定的文件进行采集
//			processTableJob(objectTableBean, collectDir);
//		} catch (Exception e) {
//			statusInfo.setEndDate(DateUtil.getSysDate());
//			statusInfo.setEndTime(DateUtil.getSysTime());
//			statusInfo.setStatusCode(RunStatusConstant.FAILED.getCode());
//			log.error("非结构化对象采集卸数失败", e);
//		}
//		log.info("------------------非结构化对象采集卸数阶段结束------------------");
//		//4.设置数据加载阶段结束日期结束时间并返回
//		statusInfo.setEndDate(DateUtil.getSysDate());
//		statusInfo.setEndTime(DateUtil.getSysTime());
//		statusInfo.setStatusCode(RunStatusConstant.SUCCEED.getCode());
//		stageParamInfo.setStatusInfo(statusInfo);
//		return stageParamInfo;
//	}

	@Override
	public int getStageCode() {
		return StageConstant.DATALOADING.getCode();
	}
//
//	@Method(desc = "处理文件夹下同类型的非结构化对象采集",
//			logicStep = "1.找到文件夹下符合规则的文件" +
//					"2.判断非结构化对象采集任务存储目的地，调用对应方法")
//	@Param(name = "bean", desc = "多条半结构化对象采集存储到hadoop配置信息", range = "实体不可为空的值不能为空")
//	@Param(name = "collectDir", desc = "采集文件所在的目录", range = "不可为空")
//	private void processTableJob(ObjectTableBean bean, String collectDir) {
//		try {
//			//1.找到文件夹下符合规则的文件
//			File[] files = new File(collectDir).listFiles((file) -> Pattern.compile(bean.getZh_name()).
//					matcher(file.getName()).matches());
//			if (files == null || files.length == 0) {
//				log.info("对象数据： " + bean.getEn_name() + "在文件夹" + collectDir + "下找不到可匹配的文件");
//				return;
//			}
//			//2.判断非结构化对象采集任务存储目的地，调用对应方法
////			if (IsFlag.Shi.getCode().equals(bean.getIs_hdfs())) {
//			//拼接上传到hdfs的路径
////				String hdfsPath = PathUtil.DCLRELEASE + object_collect.getOdc_id() + "/" + bean.getEn_name() + "/";
////				hdfsPath = FileNameUtils.normalize(hdfsPath, true);
//			//数据上传到HDFS
////				uploadHDFS(files, hdfsPath);
////			}
////			if (IsFlag.Shi.toString().equals(bean.getIs_hbase())) {
//			//数据加载进HBase
//			loadIntoHBase(bean, files);
////			}
//		} catch (Exception e) {
//			log.error("处理表失败：" + bean.getEn_name(), e);
//			throw new BusinessException("处理表失败：" + bean.getEn_name());
//		}
//	}

//	@Method(desc = "上传文件到hdfs",
//			logicStep = "1.hdfs上目录不存在则先创建hdfs上的目录" +
//					"2.遍历文件，将本地文件上传到hdfs")
//	@Param(name = "files", desc = "需要上传的文件的数组", range = "不可为空")
//	@Param(name = "hdfsPath", desc = "上传到hdfs的路径", range = "不可为空")
//	private void uploadHDFS(File[] files, String hdfsPath) {
//		//XXX 上传到hdfs的路径待讨论，这里如果选择了上传hdfs之后文件不解析吗？
//		try (FileSystem fs = FileSystem.get(ConfigReader.getConfiguration());
//			 HdfsOperator operator = new HdfsOperator()) {
//			//1.hdfs上目录不存在则先创建hdfs上的目录
//			if (!operator.mkdir(hdfsPath)) {
//				throw new BusinessException("创建hdfs目录： " + hdfsPath + " 失败！！！");
//			}
//			//2.遍历文件，将本地文件上传到hdfs
//			for (File file : files) {
//				fs.copyFromLocalFile(false, true, new Path(file.getAbsolutePath()),
//						new Path(hdfsPath));
//			}
//		} catch (Exception e) {
//			throw new BusinessException("上传hdfs目录：" + hdfsPath + " 失败！！！");
//		}
//	}

//	@Method(desc = "加载数据进hbase",
//			logicStep = "1.页面没有给解析半结构化对象的字段时，给默认值" +
//					"2.字段个数和字段类型个数不匹配时抛异常" +
//					"3.创建hbase表" +
//					"4.遍历文件，根据指定编码读取，调用每一行文件处理类" +
//					"5.对hbase表做hive的映射")
//	@Param(name = "bean", desc = "对象采集解析文件所需要的实体", range = "实体不能为空的字段必须有值")
//	@Param(name = "files", desc = "加载进hbase的文件的数组", range = "不可为空")
//	private void loadIntoHBase(ObjectTableBean bean, File[] files) {
//		log.info("load HBase ... ");
//		//1.页面没有给解析半结构化对象的字段时，给默认值
////		String columnNames = StringUtil.isBlank(bean.getColl_names()) ?
////				CONTENT + "," + HYREN_S_DATE : bean.getColl_names();
////		String columnTypes = StringUtil.isBlank(bean.getStruct_types()) ? "string,string" : bean.getStruct_types();
//		//2.字段个数和字段类型个数不匹配时抛异常
////		if (!StringUtil.isBlank(columnNames)) {
////			if (columnNames.split(",").length != columnTypes.split(",").length) {
////				throw new BusinessException("字段名称和字段类型个数不匹配");
////			}
////		}
//		String hbaseTableName = bean.getEn_name();
//		//3.创建hbase表
//		try (HBaseHelper helper = HBaseHelper.getHelper()) {
//			if (!helper.existsTable(hbaseTableName)) {
//				helper.createTable(hbaseTableName, Bytes.toString(Constant.HBASE_COLUMN_FAMILY));
//			}
//			//读取文件的编码
//			String charset = DataBaseCode.ofValueByCode(bean.getDatabase_code());
//			Table table = helper.getTable(hbaseTableName);
//			//4.遍历文件，根据指定编码读取，调用每一行文件处理类
//			for (File file : files) {
//				try (BufferedReader bufferedReader = new BufferedReader(
//						new InputStreamReader(new FileInputStream(file), charset))) {
////					loadDataByLine(bufferedReader, file, table, columnNames, bean.getCollect_data_type());
//				}
//			}
//			//5.对hbase表做hive的映射
////			mapHive(hbaseTableName, columnNames, columnTypes);
//		} catch (Exception e) {
//			log.error(e);
//			throw new BusinessException("load Hbase 失败！！！");
//		}
//	}

//	@Method(desc = "读文件每一行数据存到hbase",
//			logicStep = "1.取文件所在的上层目录名称当做开始日期" +
//					"2.读取每一行数据，为空跳过" +
//					"3.如果传过来的字段名称是默认值，不解析，整行数据进Hbase" +
//					"4.根据文件数据的类型解析数据" +
//					"5.每5000行put一次数据到hbase表")
//	@Param(name = "bufferedReader", desc = "读取文件的包装类", range = "不可为空")
//	@Param(name = "file", desc = "需要读取的文件", range = "不可为空")
//	@Param(name = "table", desc = "hbase表操作类", range = "不可为空")
//	@Param(name = "columnNames", desc = "多条字段名称拼接的字符串，逗号隔开", range = "不可为空")
//	@Param(name = "file_type", desc = "读取的文件类型", range = "不可为空")
//	private void loadDataByLine(BufferedReader bufferedReader, File file, Table table,
//								String columnNames, String file_type)
//			throws IOException {
//		String lineTxt;
//		//1.取文件所在的上层目录名称当做开始日期
//		byte[] time = FileNameUtils.getBaseName(file.getParent()).getBytes();
//		String[] columnName = columnNames.split(",");
//		int rowNum = 0;
//		List<Put> putList = new ArrayList<>();
//		while ((lineTxt = bufferedReader.readLine()) != null) {
//			rowNum++;
//			//2.读取每一行数据，为空跳过
//			if (StringUtil.isEmpty(lineTxt)) {
//				continue;
//			}
//			//3.如果传过来的字段名称是默认值，不解析，整行数据进Hbase
//			if (CONTENT.equals(columnName[0]) && HYREN_S_DATE.equals(columnName[1])) {
//				//XXX 进HBase的rowkey应该使用什么值
//				Put put = new Put(DigestUtils.md5Hex(lineTxt).getBytes());
//				put.addColumn(Constant.HBASE_COLUMN_FAMILY, CONTENT.getBytes(), lineTxt.getBytes());
//				put.addColumn(Constant.HBASE_COLUMN_FAMILY, HYREN_S_DATE.getBytes(), time);
//				putList.add(put);
//			} else {
//				//XXX 目前只实现了JSON文件的解析
//				//4.根据文件数据的类型解析数据
//				if (CollectDataType.JSON.getCode().equals(file_type)) {
//					//XXX 进HBase的rowkey应该使用什么值
//					Put put = new Put(DigestUtils.md5Hex(lineTxt).getBytes());
//					JSONObject object = JSONObject.parseObject(lineTxt);
//					for (String col : columnName) {
//						put.addColumn(Constant.HBASE_COLUMN_FAMILY, col.getBytes(), object.getBytes(col));
//					}
//					put.addColumn(Constant.HBASE_COLUMN_FAMILY, HYREN_S_DATE.getBytes(), time);
//					putList.add(put);
//				} else if (CollectDataType.XML.getCode().equals(file_type)) {
//					//TODO 待讨论
//					throw new BusinessException("待讨论的数据文件类型");
//				} else {
//					throw new BusinessException("不支持的数据文件类型");
//				}
//			}
//			//5.每5000行put一次数据到hbase表
//			if (rowNum % 5000 == 0) {
//				table.put(putList);
//				putList.clear();
//			}
//		}
//		table.put(putList);
//	}

//	@Method(desc = "hbase表做hive的映射",
//			logicStep = "1.调用方法，生成创建hive映射hbase表的sql" +
//					"2.执行sql")
//	@Param(name = "tableName", desc = "需要做映射的hbase表名", range = "不可为空")
//	@Param(name = "hiveColumns", desc = "多条字段名称拼接的字符串，逗号隔开", range = "不可为空")
//	@Param(name = "hiveTypes", desc = "多条字段类型拼接的字符串，逗号隔开", range = "不可为空")
//	private void mapHive(String tableName, String columnNames, String columnTypes) {
//		//1.调用方法，生成创建hive映射hbase表的sql
//		List<String> sqlList = HSqlHandle.hiveMapHBase(tableName, columnNames, columnTypes);
//		//XXX 这里动态指定存储后台后需要改为每个存储的唯一标识
//		//2.执行sql
//		HSqlExecute.executeSql(sqlList, "_hive");
//	}

//	@Method(desc = "取该路径下修改日期最新的目录",
//			logicStep = "1.判断该路径存在且必须是目录" +
//					"2.获取该路径下所有的目录" +
//					"3.遍历目录，找到修改日期是最新的，返回其全路径")
//	@Param(name = "file_path", desc = "文件夹全路径", range = "不可为空")
//	@Return(desc = "文件夹下最新目录的全路径", range = "不会为空")
//	private String getNewestDir(String file_path) {
//		//1.判断该路径存在且必须是目录
//		File file = new File(file_path);
//		if (!file.exists() || !file.isDirectory()) {
//			throw new BusinessException("用户定义的采集目录不是目录或者不存在！");
//		}
//		//2.获取该路径下所有的目录
//		File[] collectDirArray = file.listFiles(File::isDirectory);
//		if (collectDirArray == null || collectDirArray.length == 0) {
//			throw new BusinessException("用户定义的采集目录是空目录！");
//		}
//		//3.遍历目录，找到修改日期是最新的，返回其全路径
//		File newFile = collectDirArray[0];
//		for (int i = 1, k = collectDirArray.length; i < k; i++) {//取到最新的目录
//			if (collectDirArray[i].lastModified() > newFile.lastModified()) {
//				newFile = collectDirArray[i];
//			}
//		}
//		return newFile.getAbsolutePath();
//	}

	private void bulkloadLoadDataToHbase(String todayTableName, String hdfsFilePath, String etlDate
			, DataStoreConfBean dataStoreConfBean, TableBean tableBean) {
		int run;
		String isMd5 = IsFlag.Fou.getCode();
		String file_format = tableBean.getFile_format();
		String columnMetaInfo = tableBean.getColumnMetaInfo();
		List<String> columnList = StringUtil.split(columnMetaInfo, Constant.METAINFOSPLIT);
		StringBuilder rowKeyIndex = new StringBuilder();
		//获取配置的hbase的rowkey列
		Map<String, Map<Integer, String>> additInfoFieldMap = dataStoreConfBean.getSortAdditInfoFieldMap();
		if (additInfoFieldMap != null && !additInfoFieldMap.isEmpty()) {
			Map<Integer, String> column_map = additInfoFieldMap.get(StoreLayerAdded.RowKey.getCode());
			if (column_map != null && !column_map.isEmpty()) {
				//获取配置rowKey的列在文件中的下标
				for (int key : column_map.keySet()) {
					for (int i = 0; i < columnList.size(); i++) {
						if (column_map.get(key).equalsIgnoreCase(columnList.get(i))) {
							rowKeyIndex.append(i).append(Constant.METAINFOSPLIT);
						}
					}
				}
			}
		}
		if (rowKeyIndex.length() == 0) {
			//择表示没有选择rowkey，则默认使用md5值
			if (columnList.contains(Constant.MD5NAME)) {
				for (int i = 0; i < columnList.size(); i++) {
					if (Constant.MD5NAME.equals(columnList.get(i))) {
						rowKeyIndex.append(i).append(Constant.METAINFOSPLIT);
					}
				}
			} else {
				//没有算MD5，则使用全字段的值，这里的全字段的值，算md5,不包括海云拼接的字段
				for (int i = 0; i < columnList.size(); i++) {
					String colName = columnList.get(i);
					if (!(Constant.SDATENAME.equals(colName) || Constant.EDATENAME.equals(colName)
							|| Constant.HYREN_OPER_DATE.equals(colName) || Constant.HYREN_OPER_TIME.equals(colName)
							|| Constant.HYREN_OPER_PERSON.equals(colName))) {
						rowKeyIndex.append(i).append(Constant.METAINFOSPLIT);
					}
				}
				isMd5 = IsFlag.Shi.getCode();
			}
		}
		rowKeyIndex.delete(rowKeyIndex.length() - Constant.METAINFOSPLIT.length(), rowKeyIndex.length());
		String configPath = FileNameUtils.normalize(Constant.STORECONFIGPATH
				+ dataStoreConfBean.getDsl_name() + File.separator, true);
		Map<String, String> data_store_connect_attr = dataStoreConfBean.getData_store_connect_attr();
		String[] args = {todayTableName, hdfsFilePath, columnMetaInfo, rowKeyIndex.toString(), configPath, etlDate,
				isMd5, data_store_connect_attr.get(StorageTypeKey.hadoop_user_name),
				data_store_connect_attr.get(StorageTypeKey.platform),
				data_store_connect_attr.get(StorageTypeKey.prncipal_name), tableBean.getIs_header()};
		//如果表已经存在，先删除当天的表
		HBaseHelper helper = null;
		try {
			Configuration conf = ConfigReader.getConfiguration(configPath,
					data_store_connect_attr.get(StorageTypeKey.platform),
					data_store_connect_attr.get(StorageTypeKey.prncipal_name),
					data_store_connect_attr.get(StorageTypeKey.hadoop_user_name));
			helper = HBaseHelper.getHelper(conf);
			//判断当天卸数的表，如果存在，删除
			helper.dropTable(todayTableName);
			//默认是不压缩   TODO 是否压缩需要从页面配置
			HBaseIncreasement.createDefaultPrePartTable(helper, todayTableName, false);
			//根据文件类型使用bulkload解析文件生成HFile，加载数据到HBase表
			if (FileFormat.SEQUENCEFILE.getCode().equals(file_format)) {
				run = ToolRunner.run(conf, new SequeceBulkLoadJob(), args);
			} else if (FileFormat.FeiDingChang.getCode().equals(file_format)) {
				//非定长需要文件分隔符
				String[] args2 = {todayTableName, hdfsFilePath, columnMetaInfo, rowKeyIndex.toString(), configPath, etlDate,
						isMd5, data_store_connect_attr.get(StorageTypeKey.hadoop_user_name),
						data_store_connect_attr.get(StorageTypeKey.platform),
						data_store_connect_attr.get(StorageTypeKey.prncipal_name),
						tableBean.getColumn_separator(), tableBean.getIs_header()};
				run = ToolRunner.run(conf, new NonFixedBulkLoadJob(), args2);
			} else if (FileFormat.PARQUET.getCode().equals(file_format)) {
				run = ToolRunner.run(conf, new ParquetBulkLoadJob(), args);
			} else if (FileFormat.ORC.getCode().equals(file_format)) {
				run = ToolRunner.run(conf, new OrcBulkLoadJob(), args);
			} else if (FileFormat.DingChang.getCode().equals(file_format)) {
				//定长需要根据文件的编码去获取字节长度,需要每一列的长度
				String[] args2 = {todayTableName, hdfsFilePath, columnMetaInfo, rowKeyIndex.toString(), configPath, etlDate,
						isMd5, data_store_connect_attr.get(StorageTypeKey.hadoop_user_name),
						data_store_connect_attr.get(StorageTypeKey.platform),
						data_store_connect_attr.get(StorageTypeKey.prncipal_name),
						DataBaseCode.ofValueByCode(tableBean.getFile_code()), tableBean.getColLengthInfo(),
						tableBean.getIs_header()};
				run = ToolRunner.run(conf, new FixedBulkLoadJob(), args2);
			} else if (FileFormat.CSV.getCode().equals(file_format)) {
				run = ToolRunner.run(conf, new CsvBulkLoadJob(), args);
			} else {
				throw new AppSystemException("暂不支持定长或者其他类型直接加载到hive表");
			}
			if (run != 0) {
				throw new AppSystemException("半结构化对象采集数据加载table hbase 失败 " + todayTableName);
			}
		} catch (Exception e) {
			throw new AppSystemException("半结构化对象采集数据加载table hbase 失败 " + todayTableName, e);
		} finally {
			try {
				if (helper != null)
					helper.close();
			} catch (IOException e) {
				log.warn("关闭HBaseHelper异常", e);
			}
		}
	}
}
