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

import java.io.*;

public class WriterFile implements Closeable {

	private static final Log logger = LogFactory.getLog(WriterFile.class);
	private String filePath;
	private RecordWriter orcWriter = null;
	private FileSystem fs = null;
	private Configuration conf;
	private Writer sequenceWriter = null;
	private BufferedWriter bufferedWriter = null;
	private BufferedWriter incrementBufferedWriter = null;
	private CsvListWriter csvWriter = null;

	public WriterFile(String filePath) {
		this.conf = ConfigReader.getConfiguration();
		conf.setBoolean("fs.hdfs.impl.disable.cache", true);
		conf.set("dfs.client.block.write.replace-datanode-on-failure.policy", "NEVER");
		conf.set("dfs.client.block.write.replace-datanode-on-failure.enable", "true");
		conf.set("fs.defaultFS", "file:///");
		this.filePath = filePath;
		File parentFile = new File(filePath).getParentFile();
		if (!parentFile.exists()) {
			boolean mkdirs = parentFile.mkdirs();
			logger.info("创建文件夹..." + parentFile.getAbsolutePath() + "..." + mkdirs);
		}
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
			SequenceFile.Writer.Option optionFile = Writer.file(new Path(filePath));
			SequenceFile.Writer.Option optionKey = Writer.keyClass(NullWritable.class);
			SequenceFile.Writer.Option optionValue = Writer.valueClass(Text.class);
			//HDFS配置
			sequenceWriter = SequenceFile.createWriter(conf, optionFile, optionKey, optionValue);
//			fs = FileSystem.get(conf);
//			sequenceWriter = SequenceFile.createWriter(fs, conf, new Path(filePath), NullWritable.class, Text.class);
		} catch (IOException e) {
			logger.error(e);
		}
		return sequenceWriter;
	}

	public CsvListWriter getCsvWriter(String charset) {
		try {
			csvWriter = new CsvListWriter(new OutputStreamWriter(new FileOutputStream(filePath), charset)
					, CsvPreference.EXCEL_PREFERENCE);
		} catch (IOException e) {
			logger.error(e);
		}
		return csvWriter;
	}

	public CsvListWriter getIncrementCsvWriter(String charset) {
		try {
			csvWriter = new CsvListWriter(new OutputStreamWriter(new FileOutputStream(filePath, true), charset)
					, CsvPreference.EXCEL_PREFERENCE);
		} catch (IOException e) {
			logger.error(e);
		}
		return csvWriter;
	}

	public BufferedWriter getBufferedWriter(String charset) {
		try {
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), charset));
		} catch (IOException e) {
			logger.error(e);
		}
		return bufferedWriter;
	}

	public BufferedWriter getIncrementBufferedWriter(String charset) {
		try {
			incrementBufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath,
					true), charset));
		} catch (IOException e) {
			logger.error(e);
		}
		return incrementBufferedWriter;
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

	public void incrementBufferedWriterClose() {
		try {
			if (incrementBufferedWriter != null)
				incrementBufferedWriter.close();
			if (fs != null)
				fs.close();
		} catch (IOException e) {
			logger.error("incrementBufferedWriter文件关闭失败！！！", e);
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
