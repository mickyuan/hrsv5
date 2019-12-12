package hrds.agent.job.biz.utils;

import hrds.commons.hadoop.readconfig.ConfigReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.io.orc.OrcOutputFormat;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.SequenceFile.Writer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputFormat;
import org.apache.hadoop.mapred.RecordWriter;
import org.apache.hadoop.mapred.Reporter;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class WriterFile implements Closeable {

	private static final Log logger = LogFactory.getLog(WriterFile.class);
	private String filePath;
	private RecordWriter orcWriter = null;
	private FileSystem fs = null;
	private Configuration conf;
	private Writer sequenceWriter = null;
	private BufferedWriter bufferedWriter = null;
	private CsvListWriter csvWriter = null;

	public WriterFile(String filePath) {
		this.conf = ConfigReader.getConfiguration();
		conf.setBoolean("fs.hdfs.impl.disable.cache", true);
		conf.set("dfs.client.block.write.replace-datanode-on-failure.policy", "NEVER");
		conf.set("dfs.client.block.write.replace-datanode-on-failure.enable", "true");
		conf.set("fs.defaultFS", "file:///");
		this.filePath = filePath;
	}

	/**
	 *
	 */
	public RecordWriter getOrcWrite() {
		try {
			//HDFS配置
			JobConf jConf = new JobConf();
			jConf.addResource(conf);
			fs = FileSystem.get(conf);
			Path ps = new Path(filePath);
			if (fs.exists(ps)) {
				fs.delete(ps, true);
			}
			OutputFormat<?, ?> outputFormat = new OrcOutputFormat();
			orcWriter = outputFormat.getRecordWriter(fs, jConf, filePath, Reporter.NULL);
		} catch (IOException e) {
			logger.error(e);
		}
		return orcWriter;
	}

	public Writer getSequenceWrite() {
		try {
			//HDFS配置
			fs = FileSystem.get(conf);
			sequenceWriter = SequenceFile.createWriter(fs, conf, new Path(filePath), NullWritable.class, Text.class);
		} catch (IOException e) {
			logger.error(e);
		}
		return sequenceWriter;
	}

	public CsvListWriter getCsvWriter() {
		try {
			fs = FileSystem.get(conf);
//			csvWriter = new CSVWriter(new OutputStreamWriter(fs.create(new Path(filePath)),
//					StandardCharsets.UTF_8), Constant.DATADELIMITER, CSVWriter.NO_QUOTE_CHARACTER);
			csvWriter = new CsvListWriter(new OutputStreamWriter(fs.create(new Path(filePath)),
					StandardCharsets.UTF_8), CsvPreference.EXCEL_PREFERENCE);
		} catch (IOException e) {
			logger.error(e);
		}
		return csvWriter;
	}

	public BufferedWriter getBufferedWriter() {
		try {
			fs = FileSystem.get(conf);
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(fs.create(new Path(filePath)),
					StandardCharsets.UTF_8));
		} catch (IOException e) {
			logger.error(e);
		}
		return bufferedWriter;
	}

	public void orcClose() {

		try {
			if (orcWriter != null)
				orcWriter.close(Reporter.NULL);
			if (fs != null)
				fs.close();
		} catch (IOException e) {
			logger.error("Orc文件关闭失败！！！", e);
		}
	}

	public void bufferedWriterClose() {
		try {
			if (bufferedWriter != null)
				bufferedWriter.close();
			if (fs != null)
				fs.close();
		} catch (IOException e) {
			logger.error("bufferedWriter文件关闭失败！！！", e);
		}
	}

	public void csvClose() {
		try {
			if (csvWriter != null)
				csvWriter.close();
			if (fs != null)
				fs.close();
		} catch (IOException e) {
			logger.error("csvWriter文件关闭失败！！！", e);
		}
	}

	public void sequenceClose() {
		try {
			if (sequenceWriter != null)
				sequenceWriter.close();
			if (fs != null)
				fs.close();
		} catch (IOException e) {
			logger.error("sequenceWriter文件关闭失败！！！", e);
		}
	}


	public void close() {
		logger.info("调用了关闭文件流");
		try {
			if (orcWriter != null)
				orcWriter.close(Reporter.NULL);
			if (sequenceWriter != null)
				sequenceWriter.close();
			if (bufferedWriter != null)
				bufferedWriter.close();
			if (csvWriter != null)
				csvWriter.close();
			if (fs != null)
				fs.close();
		} catch (IOException e) {
			logger.error("文件流关闭失败！！！", e);
		}
	}
}
