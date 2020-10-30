package hrds.agent.job.biz.core.dfstage.fileparser.impl;

import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.core.dfstage.fileparser.FileParserAbstract;
import hrds.agent.job.biz.core.dfstage.service.ReadFileToSolr;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.Constant;
import org.apache.hadoop.fs.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.example.GroupReadSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * CsvFileParserDeal
 * date: 2020/4/21 16:47
 * author: zxz
 */
public class ParquetFileParserDeal extends FileParserAbstract {
	//打印日志
	private static final Logger LOGGER = LogManager.getLogger();

	public ParquetFileParserDeal(TableBean tableBean, CollectTableBean collectTableBean, String readFile)
			throws Exception {
		super(tableBean, collectTableBean, readFile);
	}

	@Override
	public String parserFile() {
		ParquetReader<Group> build = null;
		long fileRowCount = 0;
		try {
			GroupReadSupport readSupport = new GroupReadSupport();
			ParquetReader.Builder<Group> reader = ParquetReader.builder(readSupport, new Path(readFile));
			build = reader.build();
			Group line;
			while ((line = build.read()) != null) {
				List<String> valueList = new ArrayList<>();// 存储全量插入信息的list
				fileRowCount++;
				for (int j = 0; j < dictionaryColumnList.size(); j++) {
					valueList.add(ReadFileToSolr.getParquetValue(dictionaryTypeList.get(j), line,
							dictionaryColumnList.get(j)).toString());
				}
				//校验数据
				checkData(valueList, fileRowCount);
				//处理每一行的数据
				dealLine(valueList);
				//每50000行flash一次
				if (fileRowCount % JobConstant.BUFFER_ROW == 0) {
					writer.flush();
					LOGGER.info("正在处理转存文件，已写入" + fileRowCount + "行");
				}
			}
			writer.flush();
		} catch (Exception e) {
			throw new AppSystemException("读取parquet文件失败", e);
		} finally {
			try {
				if (build != null) {
					build.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return unloadFileAbsolutePath + Constant.METAINFOSPLIT + fileRowCount;
	}
}
