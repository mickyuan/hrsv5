package hrds.agent.job.biz.core.dfstage.fileparser;

import fd.ng.core.utils.StringUtil;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.core.service.JdbcCollectTableHandleParse;
import hrds.commons.codes.DataBaseCode;
import hrds.commons.exception.AppSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

/**
 * CsvFileParserDeal
 * date: 2020/4/21 16:47
 * author: zxz
 */
public class NonFixedFileParserDeal extends FileParserAbstract {
	private final static Logger LOGGER = LoggerFactory.getLogger(NonFixedFileParserDeal.class);

	public NonFixedFileParserDeal(TableBean tableBean, CollectTableBean collectTableBean, String readFile)
			throws Exception {
		super(tableBean, collectTableBean, readFile);
	}

	@Override
	public String parserFile() {
		BufferedReader br = null;
		long fileRowCount = 0;
		String lineValue;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(readFile)),
					DataBaseCode.ofValueByCode(tableBean.getFile_code())));
			while ((lineValue = br.readLine()) != null) {
				fileRowCount++;
				List<String> valueList = StringUtil.split(lineValue, tableBean.getColumn_separator());
				if (dictionaryColumnList.size() != valueList.size()) {
					String mss = "第 " + fileRowCount + " 行数据 ，数据字典表(" + collectTableBean.getTable_name()
							+ " )定义长度和数据不匹配！" + "\n" + "数据字典定义的长度是  " + valueList.size()
							+ " 现在获取的长度是  " + dictionaryColumnList.size() + "\n" + "数据为 " + lineValue;
					//XXX 写文件还是直接抛异常
					throw new AppSystemException(mss);
				}
				dealLine(valueList);
				//每50000行flash一次
				if (fileRowCount % 50000 == 0) {
					writer.flush();
				}
			}
			writer.flush();
		} catch (Exception e) {
			throw new AppSystemException("解析非定长文件转存报错", e);
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				LOGGER.warn("关闭read文件流异常");
			}
		}
		return unloadFileAbsolutePath + JdbcCollectTableHandleParse.STRSPLIT + fileRowCount;
	}
}
