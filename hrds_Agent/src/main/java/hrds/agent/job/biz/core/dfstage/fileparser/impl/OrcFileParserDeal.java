package hrds.agent.job.biz.core.dfstage.fileparser.impl;

import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.core.dfstage.fileparser.FileParserAbstract;
import hrds.agent.job.biz.core.dfstage.service.ReadFileToDataBase;
import hrds.agent.job.biz.core.service.JdbcCollectTableHandleParse;
import hrds.commons.exception.AppSystemException;
import hrds.commons.hadoop.readconfig.ConfigReader;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.exec.vector.VectorizedRowBatch;
import org.apache.orc.OrcFile;
import org.apache.orc.Reader;
import org.apache.orc.RecordReader;
import org.apache.orc.TypeDescription;
import org.apache.orc.mapred.OrcMapredRecordReader;
import org.apache.orc.mapred.OrcStruct;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * CsvFileParserDeal
 * date: 2020/4/21 16:47
 * author: zxz
 */
public class OrcFileParserDeal extends FileParserAbstract {

	public OrcFileParserDeal(TableBean tableBean, CollectTableBean collectTableBean, String readFile) throws Exception {
		super(tableBean, collectTableBean, readFile);
	}

	@Override
	public String parserFile() {
		RecordReader rows = null;
		long fileRowCount = 0L;
		try {
			Reader reader = OrcFile.createReader(new Path(readFile), OrcFile.readerOptions(
					ConfigReader.getConfiguration()));
			rows = reader.rows();
			TypeDescription schema = reader.getSchema();
			List<TypeDescription> children = schema.getChildren();
			VectorizedRowBatch batch = schema.createRowBatch();
			int numberOfChildren = children.size();
			while (rows.nextBatch(batch)) {
				for (int r = 0; r < batch.size; r++) {
					List<String> valueList = new ArrayList<>();// 存储全量插入信息的list
					OrcStruct result = new OrcStruct(schema);
					for (int i = 0; i < numberOfChildren; ++i) {
						OrcMapredRecordReader.nextValue(batch.cols[i], r,
								children.get(i), result.getFieldValue(i));
						result.setFieldValue(i, OrcMapredRecordReader.nextValue(batch.cols[i], r,
								children.get(i), result.getFieldValue(i)));
					}
					fileRowCount++;
					for (int i = 0; i < result.getNumFields(); i++) {
						valueList.add(ReadFileToDataBase.getValue(dictionaryTypeList.get(i),
								result.getFieldValue(i)).toString());
					}
					//校验数据是否正确
					checkData(valueList, fileRowCount);
					//处理每一行的数据
					dealLine(valueList);
				}
				//每一批刷新一次
				writer.flush();
			}
			writer.flush();
		} catch (Exception e) {
			throw new AppSystemException("DB文件采集解析Orc文件失败");
		} finally {
			try {
				if (rows != null)
					rows.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return unloadFileAbsolutePath + JdbcCollectTableHandleParse.STRSPLIT + fileRowCount;
	}

}
