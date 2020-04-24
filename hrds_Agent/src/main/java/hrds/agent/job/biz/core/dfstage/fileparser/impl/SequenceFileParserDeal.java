package hrds.agent.job.biz.core.dfstage.fileparser.impl;

import fd.ng.core.utils.StringUtil;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.core.dfstage.fileparser.FileParserAbstract;
import hrds.agent.job.biz.core.service.JdbcCollectTableHandleParse;
import hrds.commons.exception.AppSystemException;
import hrds.commons.hadoop.readconfig.ConfigReader;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;

import java.io.IOException;
import java.util.List;

/**
 * CsvFileParserDeal
 * date: 2020/4/21 16:47
 * author: zxz
 */
public class SequenceFileParserDeal extends FileParserAbstract {

	public SequenceFileParserDeal(TableBean tableBean, CollectTableBean collectTableBean, String readFile)
			throws Exception {
		super(tableBean, collectTableBean, readFile);
	}

	@Override
	public String parserFile() {
		//TODO 这里是不是修改？？
		Configuration conf = ConfigReader.getConfiguration();
		conf.setBoolean("fs.hdfs.impl.disable.cache", true);
		conf.set("dfs.client.block.write.replace-datanode-on-failure.policy", "NEVER");
		conf.set("dfs.client.block.write.replace-datanode-on-failure.enable", "true");
		conf.set("fs.defaultFS", "file:///");
		long fileRowCount = 0L;
		SequenceFile.Reader sfr = null;
		try {
			SequenceFile.Reader.Option optionFile = SequenceFile.Reader.file((new Path(readFile)));
			sfr = new SequenceFile.Reader(conf, optionFile);
			NullWritable key = NullWritable.get();
			Text value = new Text();
			List<String> valueList;
			while (sfr.next(key, value)) {
				fileRowCount++;
				String str = value.toString();
				//XXX SequenceFile不指定分隔符，页面也不允许其指定分隔符，使用hive默认的\001隐藏字符做分隔符
				//XXX 这样只要创建hive映射外部表时使用store as sequencefile hive会自动解析。batch方式使用默认的去解析
				valueList = StringUtil.split(str, JobConstant.SEQUENCEDELIMITER);
				//校验数据
				checkData(valueList, fileRowCount);
				dealLine(valueList);
				//每50000行flash一次
				if (fileRowCount % 50000 == 0) {
					writer.flush();
				}
			}
			writer.flush();
		} catch (Exception e) {
			throw new AppSystemException("DB文件采集解析sequenceFile文件失败");
		} finally {
			try {
				if (sfr != null)
					sfr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return unloadFileAbsolutePath + JdbcCollectTableHandleParse.STRSPLIT + fileRowCount;
	}
}
