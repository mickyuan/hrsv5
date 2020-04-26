package hrds.agent.job.biz.core.dfstage.fileparser.impl;

import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.core.dfstage.fileparser.FileParserAbstract;
import hrds.agent.job.biz.core.dfstage.service.ReadFileToDataBase;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.Constant;
import org.apache.hadoop.fs.Path;
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
					valueList.add(ReadFileToDataBase.getParquetValue(dictionaryTypeList.get(j), line,
							dictionaryColumnList.get(j)).toString());
				}
				//校验数据
				checkData(valueList, fileRowCount);
				//处理每一行的数据
				dealLine(valueList);
				//每50000行flash一次
				if (fileRowCount % 50000 == 0) {
					writer.flush();
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
