package hrds.agent.job.biz.core.objectstage;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.FileNameUtils;
import fd.ng.core.utils.StringUtil;
import hrds.agent.job.biz.bean.ObjectCollectParamBean;
import hrds.agent.job.biz.bean.StageStatusInfo;
import hrds.agent.job.biz.constant.RunStatusConstant;
import hrds.agent.job.biz.constant.StageConstant;
import hrds.agent.job.biz.core.AbstractJobStage;
import hrds.commons.codes.CollectDataType;
import hrds.commons.codes.DataBaseCode;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Object_collect;
import hrds.commons.exception.BusinessException;
import hrds.commons.hadoop.hadoop_helper.HBaseHelper;
import hrds.commons.hadoop.hadoop_helper.HdfsOperator;
import hrds.commons.hadoop.readconfig.ConfigReader;
import hrds.commons.hadoop.utils.HSqlExecute;
import hrds.commons.hadoop.utils.HSqlHandle;
import hrds.commons.utils.Constant;
import hrds.commons.utils.PathUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@DocClass(desc = "半结构化对象采集数据加载实现", author = "zxz", createdate = "2019/10/24 11:43")
public class ObjectLoadingDataStageImpl extends AbstractJobStage {
	//打印日志
	private static final Log log = LogFactory.getLog(ObjectLoadingDataStageImpl.class);
	//半结构化对象采集设置表对象
	private Object_collect object_collect;
	//多条半结构化对象采集存储到hadoop存储信息实体合集
	private List<ObjectCollectParamBean> objectCollectParamBeanList;
	//整条数据进hbase时的列名称
	private static final String CONTENT = "content";
	//开始日期
	private static final String HYREN_S_DATE = "hyren_s_date";

	/**
	 * 半结构化对象采集数据加载实现类构造方法
	 *
	 * @param object_collect             Object_collect
	 *                                   含义：半结构化对象采集设置表对象
	 *                                   取值范围：所有这张表不能为空的字段的值必须有，为空则会抛异常
	 * @param objectCollectParamBeanList List<ObjectCollectParamBean>
	 *                                   含义：多条半结构化对象采集存储到hadoop配置信息实体合集
	 *                                   取值范围：所有这个实体不能为空的字段的值必须有，为空则会抛异常
	 */
	public ObjectLoadingDataStageImpl(Object_collect object_collect, List<ObjectCollectParamBean>
			objectCollectParamBeanList) {
		this.object_collect = object_collect;
		this.objectCollectParamBeanList = objectCollectParamBeanList;
	}

	@Method(desc = "半结构化对象采集，数据加载阶段实现，处理完成后，无论成功还是失败，" +
			"将相关状态信息封装到StageStatusInfo对象中返回"
			, logicStep = "1.设置数据加载阶段的任务id,开始日期开始时间" +
			"2.取客户给定的采集目录下取到最新的目录（修改日期最近的），作为采集目录" +
			"3.遍历对象采集任务，根据对应的配置找到指定的文件进行采集" +
			"4.设置数据加载阶段结束日期结束时间并返回")
	@Return(desc = "StageStatusInfo是保存每个阶段状态信息的实体类", range = "不会为null,StageStatusInfo实体类对象")
	@Override
	public StageStatusInfo handleStage() {
		//1.设置数据加载阶段的任务id,开始日期开始时间
		StageStatusInfo statusInfo = new StageStatusInfo();
		statusInfo.setJobId(String.valueOf(object_collect.getOdc_id()));
		statusInfo.setStageNameCode(StageConstant.DATALOADING.getCode());
		statusInfo.setStartDate(DateUtil.getSysDate());
		statusInfo.setStartTime(DateUtil.getSysTime());
		try {
			String file_path = object_collect.getFile_path();
			//2.取客户给定的采集目录下取到最新的目录（修改日期最近的），作为采集目录
			String collectDir = getNewestDir(file_path);
			collectDir = FileNameUtils.normalize(collectDir, true);
			//3.遍历对象采集任务，根据对应的配置找到指定的文件进行采集
			for (ObjectCollectParamBean bean : objectCollectParamBeanList) {
				processTableJob(bean, collectDir);
			}
		} catch (Exception e) {
			statusInfo.setEndDate(DateUtil.getSysDate());
			statusInfo.setEndTime(DateUtil.getSysTime());
			statusInfo.setStatusCode(RunStatusConstant.FAILED.getCode());
			log.error("非结构化对象采集卸数失败", e);
		}
		log.info("------------------非结构化对象采集卸数阶段结束------------------");
		//4.设置数据加载阶段结束日期结束时间并返回
		statusInfo.setEndDate(DateUtil.getSysDate());
		statusInfo.setEndTime(DateUtil.getSysTime());
		statusInfo.setStatusCode(RunStatusConstant.SUCCEED.getCode());
		return statusInfo;
	}

	@Method(desc = "处理文件夹下同类型的非结构化对象采集",
			logicStep = "1.找到文件夹下符合规则的文件" +
					"2.判断非结构化对象采集任务存储目的地，调用对应方法")
	@Param(name = "bean", desc = "多条半结构化对象采集存储到hadoop配置信息", range = "实体不可为空的值不能为空")
	@Param(name = "collectDir", desc = "采集文件所在的目录", range = "不可为空")
	private void processTableJob(ObjectCollectParamBean bean, String collectDir) {
		try {
			//1.找到文件夹下符合规则的文件
			File[] files = new File(collectDir).listFiles((file) -> Pattern.compile(bean.getZh_name()).
					matcher(file.getName()).matches());
			if (files == null || files.length == 0) {
				log.info("对象数据： " + bean.getEn_name() + "在文件夹" + collectDir + "下找不到可匹配的文件");
				return;
			}
			//2.判断非结构化对象采集任务存储目的地，调用对应方法
			if (IsFlag.Shi.getCode().equals(bean.getIs_hdfs())) {
				//拼接上传到hdfs的路径
				String hdfsPath = PathUtil.DCLRELEASE + object_collect.getOdc_id() + "/" + bean.getEn_name() + "/";
				hdfsPath = FileNameUtils.normalize(hdfsPath, true);
				//数据上传到HDFS
				uploadHDFS(files, hdfsPath);
			}
			if (IsFlag.Shi.toString().equals(bean.getIs_hbase())) {
				//数据加载进HBase
				loadIntoHBase(bean, files);
			}
		} catch (Exception e) {
			log.error("处理表失败：" + bean.getEn_name(), e);
			throw new BusinessException("处理表失败：" + bean.getEn_name());
		}
	}

	@Method(desc = "上传文件到hdfs",
			logicStep = "1.hdfs上目录不存在则先创建hdfs上的目录" +
					"2.遍历文件，将本地文件上传到hdfs")
	@Param(name = "files", desc = "需要上传的文件的数组", range = "不可为空")
	@Param(name = "hdfsPath", desc = "上传到hdfs的路径", range = "不可为空")
	private void uploadHDFS(File[] files, String hdfsPath) {
		//XXX 上传到hdfs的路径待讨论，这里如果选择了上传hdfs之后文件不解析吗？
		try (FileSystem fs = FileSystem.get(ConfigReader.getConfiguration());
		     HdfsOperator operator = new HdfsOperator()) {
			//1.hdfs上目录不存在则先创建hdfs上的目录
			if (!operator.mkdir(hdfsPath)) {
				throw new BusinessException("创建hdfs目录： " + hdfsPath + " 失败！！！");
			}
			//2.遍历文件，将本地文件上传到hdfs
			for (File file : files) {
				fs.copyFromLocalFile(false, true, new Path(file.getAbsolutePath()),
						new Path(hdfsPath));
			}
		} catch (Exception e) {
			throw new BusinessException("上传hdfs目录：" + hdfsPath + " 失败！！！");
		}
	}

	@Method(desc = "加载数据进hbase",
			logicStep = "1.页面没有给解析半结构化对象的字段时，给默认值" +
					"2.字段个数和字段类型个数不匹配时抛异常" +
					"3.创建hbase表" +
					"4.遍历文件，根据指定编码读取，调用每一行文件处理类" +
					"5.对hbase表做hive的映射")
	@Param(name = "bean", desc = "对象采集解析文件所需要的实体", range = "实体不能为空的字段必须有值")
	@Param(name = "files", desc = "加载进hbase的文件的数组", range = "不可为空")
	private void loadIntoHBase(ObjectCollectParamBean bean, File[] files) {
		log.info("load HBase ... ");
		//1.页面没有给解析半结构化对象的字段时，给默认值
		String columnNames = StringUtil.isBlank(bean.getColl_names()) ?
				CONTENT + "," + HYREN_S_DATE : bean.getColl_names();
		String columnTypes = StringUtil.isBlank(bean.getStruct_types()) ? "string,string" : bean.getStruct_types();
		//2.字段个数和字段类型个数不匹配时抛异常
		if (!StringUtil.isBlank(columnNames)) {
			if (columnNames.split(",").length != columnTypes.split(",").length) {
				throw new BusinessException("字段名称和字段类型个数不匹配");
			}
		}
		String hbaseTableName = bean.getEn_name();
		//3.创建hbase表
		try (HBaseHelper helper = HBaseHelper.getHelper()) {
			if (!helper.existsTable(hbaseTableName)) {
				helper.createTable(hbaseTableName, Bytes.toString(Constant.HBASE_COLUMN_FAMILY));
			}
			//读取文件的编码
			String charset = DataBaseCode.ofValueByCode(bean.getDatabase_code());
			Table table = helper.getTable(hbaseTableName);
			//4.遍历文件，根据指定编码读取，调用每一行文件处理类
			for (File file : files) {
				try (BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(new FileInputStream(file), charset))) {
					loadDataByLine(bufferedReader, file, table, columnNames, bean.getCollect_data_type());
				}
			}
			//5.对hbase表做hive的映射
			mapHive(hbaseTableName, columnNames, columnTypes);
		} catch (Exception e) {
			log.error(e);
			throw new BusinessException("load Hbase 失败！！！");
		}
	}

	@Method(desc = "读文件每一行数据存到hbase",
			logicStep = "1.取文件所在的上层目录名称当做开始日期" +
					"2.读取每一行数据，为空跳过" +
					"3.如果传过来的字段名称是默认值，不解析，整行数据进Hbase" +
					"4.根据文件数据的类型解析数据" +
					"5.每5000行put一次数据到hbase表")
	@Param(name = "bufferedReader", desc = "读取文件的包装类", range = "不可为空")
	@Param(name = "file", desc = "需要读取的文件", range = "不可为空")
	@Param(name = "table", desc = "hbase表操作类", range = "不可为空")
	@Param(name = "columnNames", desc = "多条字段名称拼接的字符串，逗号隔开", range = "不可为空")
	@Param(name = "file_type", desc = "读取的文件类型", range = "不可为空")
	private void loadDataByLine(BufferedReader bufferedReader, File file, Table table,
	                            String columnNames, String file_type)
			throws IOException {
		String lineTxt;
		//1.取文件所在的上层目录名称当做开始日期
		byte[] time = FileNameUtils.getBaseName(file.getParent()).getBytes();
		String[] columnName = columnNames.split(",");
		int rowNum = 0;
		List<Put> putList = new ArrayList<>();
		while ((lineTxt = bufferedReader.readLine()) != null) {
			rowNum++;
			//2.读取每一行数据，为空跳过
			if (StringUtil.isEmpty(lineTxt)) {
				continue;
			}
			//3.如果传过来的字段名称是默认值，不解析，整行数据进Hbase
			if (CONTENT.equals(columnName[0]) && HYREN_S_DATE.equals(columnName[1])) {
				//XXX 进HBase的rowkey应该使用什么值
				Put put = new Put(DigestUtils.md5Hex(lineTxt).getBytes());
				put.addColumn(Constant.HBASE_COLUMN_FAMILY, CONTENT.getBytes(), lineTxt.getBytes());
				put.addColumn(Constant.HBASE_COLUMN_FAMILY, HYREN_S_DATE.getBytes(), time);
				putList.add(put);
			} else {
				//XXX 目前只实现了xml文件的解析
				//4.根据文件数据的类型解析数据
				if (CollectDataType.JSON.getCode().equals(file_type)) {
					//XXX 进HBase的rowkey应该使用什么值
					Put put = new Put(DigestUtils.md5Hex(lineTxt).getBytes());
					JSONObject object = JSONObject.parseObject(lineTxt);
					for (String col : columnName) {
						put.addColumn(Constant.HBASE_COLUMN_FAMILY, col.getBytes(), object.getBytes(col));
					}
					put.addColumn(Constant.HBASE_COLUMN_FAMILY, HYREN_S_DATE.getBytes(), time);
					putList.add(put);
				} else if (CollectDataType.XML.getCode().equals(file_type)) {
					//TODO 待讨论
					throw new BusinessException("待讨论的数据文件类型");
				} else {
					throw new BusinessException("不支持的数据文件类型");
				}
			}
			//5.每5000行put一次数据到hbase表
			if (rowNum % 5000 == 0) {
				table.put(putList);
				putList.clear();
			}
		}
		table.put(putList);
	}

	@Method(desc = "hbase表做hive的映射",
			logicStep = "1.调用方法，生成创建hive映射hbase表的sql" +
					"2.执行sql")
	@Param(name = "tableName", desc = "需要做映射的hbase表名", range = "不可为空")
	@Param(name = "hiveColumns", desc = "多条字段名称拼接的字符串，逗号隔开", range = "不可为空")
	@Param(name = "hiveTypes", desc = "多条字段类型拼接的字符串，逗号隔开", range = "不可为空")
	private void mapHive(String tableName, String columnNames, String columnTypes) {
		//1.调用方法，生成创建hive映射hbase表的sql
		List<String> sqlList = HSqlHandle.hiveMapHBase(tableName, columnNames, columnTypes);
		//XXX 这里动态指定存储后台后需要改为每个存储的唯一标识
		//2.执行sql
		HSqlExecute.executeSql(sqlList, "_hive");
	}

	@Method(desc = "取该路径下修改日期最新的目录",
			logicStep = "1.判断该路径存在且必须是目录" +
					"2.获取该路径下所有的目录" +
					"3.遍历目录，找到修改日期是最新的，返回其全路径")
	@Param(name = "file_path", desc = "文件夹全路径", range = "不可为空")
	@Return(desc = "文件夹下最新目录的全路径", range = "不会为空")
	private String getNewestDir(String file_path) {
		//1.判断该路径存在且必须是目录
		File file = new File(file_path);
		if (!file.exists() || !file.isDirectory()) {
			throw new BusinessException("用户定义的采集目录不是目录或者不存在！");
		}
		//2.获取该路径下所有的目录
		File[] collectDirArray = file.listFiles(File::isDirectory);
		if (collectDirArray == null || collectDirArray.length == 0) {
			throw new BusinessException("用户定义的采集目录是空目录！");
		}
		//3.遍历目录，找到修改日期是最新的，返回其全路径
		File newFile = collectDirArray[0];
		for (int i = 1, k = collectDirArray.length; i < k; i++) {//取到最新的目录
			if (collectDirArray[i].lastModified() > newFile.lastModified()) {
				newFile = collectDirArray[i];
			}
		}
		return newFile.getAbsolutePath();
	}
}
