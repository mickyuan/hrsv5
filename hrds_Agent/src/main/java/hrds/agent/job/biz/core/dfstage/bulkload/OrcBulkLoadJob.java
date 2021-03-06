package hrds.agent.job.biz.core.dfstage.bulkload;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.StringUtil;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.commons.codes.IsFlag;
import hrds.commons.hadoop.hadoop_helper.HBaseHelper;
import hrds.commons.hadoop.readconfig.ConfigReader;
import hrds.commons.hadoop.utils.BKLoadUtil;
import hrds.commons.utils.Constant;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.HFileOutputFormat2;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.MRJobConfig;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.orc.mapred.OrcStruct;
import org.apache.orc.mapreduce.OrcInputFormat;
import org.apache.parquet.hadoop.ParquetInputFormat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@DocClass(author = "zxz", desc = "Orc文件采用bulkLoad方式加载到hbase", createdate = "2020/07/17")
public class OrcBulkLoadJob extends Configured implements Tool {

	//打印日志
	private static final Logger logger = LogManager.getLogger();

	public static class BulkLoadMap extends Mapper<NullWritable, OrcStruct, ImmutableBytesWritable, Put> {

		private List<byte[]> headByte = null;
		private List<Integer> rowKeyIndex = null;
		private boolean isMd5 = false;
		private final StringBuilder sb = new StringBuilder();

		/*
		 * 初始化类参数
		 */
		protected void setup(Context context) {
			Configuration conf = context.getConfiguration();
			List<String> columnList = StringUtil.split(conf.get("columnMetaInfo"), Constant.METAINFOSPLIT);
			headByte = new ArrayList<>(columnList.size());
			columnList.forEach(column -> headByte.add(column.getBytes()));
			isMd5 = conf.get("isMd5").equals(IsFlag.Shi.getCode());
			List<String> rowKeyIndexList = StringUtil.split(conf.get("rowKeyIndex"), Constant.METAINFOSPLIT);
			rowKeyIndex = new ArrayList<>(rowKeyIndexList.size());
			rowKeyIndexList.forEach(index -> rowKeyIndex.add(Integer.valueOf(index)));
		}

		public void map(NullWritable key, OrcStruct value, Context context) throws IOException, InterruptedException {
			String row_key;
			//自己算md5
			if (isMd5) {
				for (int index : rowKeyIndex) {
					sb.append(value.getFieldValue(index));
				}
				row_key = DigestUtils.md5Hex(sb.toString());
				sb.delete(0, sb.length());
			} else {
				for (int index : rowKeyIndex) {
					sb.append(value.getFieldValue(index));
				}
				row_key = sb.toString();
				sb.delete(0, sb.length());
			}
			ImmutableBytesWritable rowkey = new ImmutableBytesWritable(row_key.getBytes());//主键：md5值+_+任务时间
			Put put = new Put(Bytes.toBytes(row_key));
			for (int i = 0; i < headByte.size(); i++) {
				put.addColumn(Constant.HBASE_COLUMN_FAMILY, headByte.get(i), Bytes.toBytes(value.getFieldValue(i).toString()));
			}
			context.write(rowkey, put);
		}
	}

	/*
	 * mapreduce提交驱动
	 */
	public int run(String[] args) throws Exception {

		String todayTableName = args[0];
		String hdfsFilePath = args[1];
		String columnMetaInfo = args[2];
		String rowKeyIndex = args[3];
		String configPath = args[4];
		String etlDate = args[5];
		String isMd5 = args[6];
		String hadoop_user_name = args[7];
		String platform = args[8];
		String prncipal_name = args[9];
		logger.info("Arguments: " + todayTableName + "  " + hdfsFilePath + "  " + columnMetaInfo + "  "
				+ rowKeyIndex + "  " + configPath + "  " + etlDate + "  " + isMd5 + "  " + hadoop_user_name
				+ "  " + platform + "  " + prncipal_name);
		Configuration conf = ConfigReader.getConfiguration(configPath, platform, prncipal_name, hadoop_user_name);
		try (HBaseHelper helper = HBaseHelper.getHelper(conf)) {
			conf.set("columnMetaInfo", columnMetaInfo);
			conf.set("etlDate", etlDate);
			conf.set("isMd5", isMd5);
			conf.set("rowKeyIndex", rowKeyIndex);
			conf.set(MRJobConfig.QUEUE_NAME, "root." + hadoop_user_name);
			Job job = Job.getInstance(conf, "OrcBulkLoadJob_" + todayTableName);
			job.setJarByClass(OrcBulkLoadJob.class);

			job.setInputFormatClass(OrcInputFormat.class);
			ParquetInputFormat.setInputPaths(job, hdfsFilePath);

			String outputPath = JobConstant.TMPDIR + "/bulkload/output" + System.currentTimeMillis();
			Path tmpPath = new Path(outputPath);
			FileOutputFormat.setOutputPath(job, tmpPath);

			job.setMapperClass(BulkLoadMap.class);
			job.setMapOutputKeyClass(ImmutableBytesWritable.class);
			job.setMapOutputValueClass(Put.class);
			job.setOutputFormatClass(HFileOutputFormat2.class);

//			job.setNumReduceTasks(0);

			int resultCode = BKLoadUtil.jobOutLoader(todayTableName, tmpPath, helper, job);
			//delete the hfiles
			try (FileSystem fs = FileSystem.get(conf)) {
				fs.delete(tmpPath, true);
			}

			return resultCode;
		}
	}

	/**
	 * args[1] 表名
	 * args[2] 表头
	 * args[2] 任务时间
	 */
	public static void main(String[] args) throws Exception {

		int exitCode = ToolRunner.run(new OrcBulkLoadJob(), args);
		System.exit(exitCode);
	}

}