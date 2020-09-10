package hrds.agent.job.biz.core.jdbcdirectstage.service;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.DataStoreConfBean;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.core.dfstage.service.ReadFileToDataBase;
import hrds.commons.collection.ConnectionTool;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@DocClass(desc = "解析ResultSet数据到数据库", author = "zxz", createdate = "2019/12/17 15:43")
public class ParseResultSetToDataBase {

	private final static Logger LOGGER = LoggerFactory.getLogger(ParseResultSetToDataBase.class);
	//采集数据的结果集
	private final ResultSet resultSet;
	//数据采集表对应的存储的所有信息
	private final CollectTableBean collectTableBean;
	//数据库采集表对应的meta信息
	private final TableBean tableBean;
	//直连采集对应存储的目的地信息
	private final DataStoreConfBean dataStoreConfBean;
	//操作日期
	protected String operateDate;
	//操作时间
	protected String operateTime;
	//操作人
	protected String user_id;

	/**
	 * 读取文件到数据库构造方法
	 *
	 * @param resultSet         ResultSet
	 *                          含义：采集数据的结果集
	 * @param tableBean         TableBean
	 *                          含义：文件对应的表结构信息
	 * @param collectTableBean  CollectTableBean
	 *                          含义：文件对应的卸数信息
	 * @param dataStoreConfBean DataStoreConfBean
	 *                          含义：文件需要上传到表对应的存储信息
	 */
	public ParseResultSetToDataBase(ResultSet resultSet, TableBean tableBean, CollectTableBean collectTableBean,
									DataStoreConfBean dataStoreConfBean) {
		this.resultSet = resultSet;
		this.collectTableBean = collectTableBean;
		this.dataStoreConfBean = dataStoreConfBean;
		this.tableBean = tableBean;
		this.operateDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		this.operateTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
		this.user_id = String.valueOf(collectTableBean.getUser_id());
	}

	public long parseResultSet() {
		//获取所有字段的名称，包括列分割和列合并出来的字段名称
		List<String> columnMetaInfoList = StringUtil.split(tableBean.getColumnMetaInfo(), Constant.METAINFOSPLIT);
		String etlDate = collectTableBean.getEtlDate();
		String batchSql = ReadFileToDataBase.getBatchSql(columnMetaInfoList,
				collectTableBean.getHbase_name() + "_" + 1);
		long counter = 0;
		try (DatabaseWrapper db = ConnectionTool.getDBWrapper(dataStoreConfBean.getData_store_connect_attr())) {
			//获取所有查询的字段的名称，不包括列分割和列合并出来的字段名称
			List<String> selectColumnList = StringUtil.split(tableBean.getAllColumns(), Constant.METAINFOSPLIT);
			//用来batch提交
			List<Object[]> pool = new ArrayList<>();
			List<String> typeList = StringUtil.split(tableBean.getAllType(),
					Constant.METAINFOSPLIT);
			int numberOfColumns = selectColumnList.size();
			LOGGER.info("type : " + typeList.size() + "  colName " + numberOfColumns);
			while (resultSet.next()) {
				//用来写一行数据
				Object[] obj;
				if (JobConstant.ISADDOPERATEINFO) {
					obj = new Object[numberOfColumns + 4];
				} else {
					obj = new Object[numberOfColumns + 1];
				}
				// Count it
				counter++;
				// Write columns
				for (int i = 0; i < numberOfColumns; i++) {
					//获取值
					obj[i] = resultSet.getObject(selectColumnList.get(i));
				}
				obj[numberOfColumns] = etlDate;
				//拼接操作时间、操作日期、操作人
				appendOperateInfo(obj, numberOfColumns);
				pool.add(obj);
				if (pool.size() == JobConstant.BUFFER_ROW) {
					LOGGER.info("正在入库，已batch插入" + counter + "行");
					db.execBatch(batchSql, pool);
					pool.clear();
				}
			}
			if (pool.size() > 0) {
				db.execBatch(batchSql, pool);
			}
		} catch (Exception e) {
			LOGGER.error("batch入库失败", e);
			throw new AppSystemException("数据库采集卸数Csv文件失败", e);
		}
		//返回batch插入数据的条数
		return counter;
	}

	/**
	 * 添加操作日期、操作时间、操作人
	 */
	private void appendOperateInfo(Object[] obj, int numberOfColumns) {
		if (JobConstant.ISADDOPERATEINFO) {
			obj[numberOfColumns + 1] = operateDate;
			obj[numberOfColumns + 2] = operateTime;
			obj[numberOfColumns + 3] = user_id;
		}
	}

}
