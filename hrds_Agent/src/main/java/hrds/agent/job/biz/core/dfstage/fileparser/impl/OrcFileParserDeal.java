package hrds.agent.job.biz.core.dfstage.fileparser.impl;

import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.core.dfstage.fileparser.FileParserAbstract;
import hrds.agent.job.biz.core.dfstage.service.ReadFileToDataBase;
import hrds.agent.job.biz.core.dfstage.service.ReadFileToSolr;
import hrds.commons.exception.AppSystemException;
import hrds.commons.hadoop.readconfig.ConfigReader;
import hrds.commons.utils.Constant;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.exec.vector.VectorizedRowBatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
	//打印日志
	private static final Logger LOGGER = LogManager.getLogger();

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
						valueList.add(ReadFileToSolr.getValue(dictionaryTypeList.get(i),
								result.getFieldValue(i), null).toString());
					}
					//校验数据是否正确
					checkData(valueList, fileRowCount);
					//处理每一行的数据
					dealLine(valueList);
				}
				//每一批刷新一次
				writer.flush();
				LOGGER.info("正在处理转存文件，已写入" + fileRowCount + "行");
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
		return unloadFileAbsolutePath + Constant.METAINFOSPLIT + fileRowCount;
	}

}
