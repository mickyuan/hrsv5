package hrds.agent.job.biz.core.increasement;

import fd.ng.core.utils.FileNameUtils;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.utils.DataTypeTransform;
import hrds.commons.exception.AppSystemException;
import hrds.commons.hadoop.hadoop_helper.HBaseHelper;
import hrds.commons.hadoop.hadoop_helper.HashChoreWoker;
import hrds.commons.hadoop.readconfig.ConfigReader;
import hrds.commons.utils.Constant;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * HBaseIncreasement
 * date: 2020/7/20 13:50
 * author: zxz
 */
public abstract class HBaseIncreasement implements Closeable, Increasement {
	protected DatabaseWrapper db;
	protected List<String> columns;// csv中存有的字段
	protected List<String> types;// csv中存有的字段类型
	protected String sysDate;//任务跑批日期
	protected String tableNameInHBase; //hbase的表名
	protected String deltaTableName; //增量表的名字
	protected String yesterdayTableName;//上次的表
	protected HBaseHelper helper;
	protected String todayTableName;

	protected HBaseIncreasement(TableBean tableBean, String hbase_name, String sysDate
			, String dsl_name, String hadoop_user_name, String platform, String prncipal_name, DatabaseWrapper db) {
		this.columns = StringUtil.split(tableBean.getColumnMetaInfo(), Constant.METAINFOSPLIT);
		this.types = DataTypeTransform.tansform(StringUtil.split(tableBean.getColTypeMetaInfo(),
				Constant.METAINFOSPLIT), dsl_name);
		this.sysDate = sysDate;
		this.tableNameInHBase = hbase_name;
		this.deltaTableName = hbase_name + "_hy";
		this.yesterdayTableName = hbase_name;
		//当天的数据为拼接后的表名加序号1。例如：默认保留数据的天数为4，则会有四张表，从当天跑批往后依次加下标1、2、3、4
		this.todayTableName = hbase_name + "_" + 1;
		this.db = db;
		try {
			this.helper = HBaseHelper.getHelper(ConfigReader.getConfiguration(FileNameUtils.normalize(
					Constant.STORECONFIGPATH + dsl_name + File.separator, true)
					, platform, prncipal_name, hadoop_user_name));
		} catch (IOException e) {
			throw new AppSystemException("获取helper异常", e);
		}
	}

	@Override
	public void replace() {
		try {
			//历史表存在，删除历史表
			if (helper.existsTable(tableNameInHBase)) {
				helper.dropTable(tableNameInHBase);
			}
			//将当天加载的表的数据克隆到历史表
			helper.cloneTable(todayTableName, tableNameInHBase);
		} catch (IOException e) {
			throw new AppSystemException("替换HBase表失败", e);
		}
	}

	@Override
	public void close() throws IOException {
		if (db != null) {
			db.close();
		}
		if (helper != null) {
			helper.close();
		}
	}

	/**
	 * 创建默认预分区的HBase表
	 */
	public static void createDefaultPrePartTable(HBaseHelper helper, String table,
	                                             boolean snappycompress) {
		try {
			// 预分区建表
			HashChoreWoker worker = new HashChoreWoker(1000000, 10);
			byte[][] splitKeys = worker.calcSplitKeys();
			helper.createTable(table, splitKeys, snappycompress, Bytes.toString(Constant.HBASE_COLUMN_FAMILY));
		} catch (IOException e) {
			throw new AppSystemException("创建默认预分区的HBase表失败", e);
		}
	}

}
