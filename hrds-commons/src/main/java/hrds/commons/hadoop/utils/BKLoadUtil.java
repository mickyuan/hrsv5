package hrds.commons.hadoop.utils;

import hrds.commons.hadoop.hadoop_helper.HBaseHelper;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.RegionLocator;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.mapreduce.HFileOutputFormat2;
import org.apache.hadoop.hbase.mapreduce.LoadIncrementalHFiles;
import org.apache.hadoop.mapreduce.Job;

public class BKLoadUtil {

	public static int jobOutLoader(String tableName, Path tmpPath, HBaseHelper helper, Job job) throws Exception {

		Table table = helper.getTable(tableName);
		RegionLocator regionLocator = helper.getConnection().getRegionLocator(TableName.valueOf(tableName));
		HFileOutputFormat2.configureIncrementalLoad(job, table, regionLocator);
		HFileOutputFormat2.setOutputPath(job, tmpPath);

		if (!job.waitForCompletion(true)) {
			return 1;
		}
		//bulk load hBase files
		LoadIncrementalHFiles loader = new LoadIncrementalHFiles(helper.getConfiguration());
		loader.doBulkLoad(tmpPath, (HTable) table);
		return 0;
	}
}
