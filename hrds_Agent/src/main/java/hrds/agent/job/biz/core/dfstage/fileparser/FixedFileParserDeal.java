package hrds.agent.job.biz.core.dfstage.fileparser;

import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.core.dfstage.service.ReadFileToDataBase;
import hrds.agent.job.biz.core.service.JdbcCollectTableHandleParse;
import hrds.agent.job.biz.utils.TypeTransLength;
import hrds.commons.codes.DataBaseCode;
import hrds.commons.exception.AppSystemException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * CsvFileParserDeal
 * date: 2020/4/21 16:47
 * author: zxz
 */
public class FixedFileParserDeal extends FileParserAbstract {

	public FixedFileParserDeal(TableBean tableBean, CollectTableBean collectTableBean, String readFile)
			throws Exception {
		super(tableBean, collectTableBean, readFile);
	}

	@Override
	public String parserFile() {
		long fileRowCount = 0;
		String lineValue;
		String code = DataBaseCode.ofValueByCode(tableBean.getFile_code());
		List<Integer> lengthList = new ArrayList<>();
		for (String type : dictionaryTypeList) {
			lengthList.add(TypeTransLength.getLength(type));
		}
		// 存储全量插入信息的list
		List<String> valueList;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File(readFile)), code))) {
			while ((lineValue = br.readLine()) != null) {
				fileRowCount++;
				//获取定长文件，解析每行数据转为list
				valueList = ReadFileToDataBase.getDingChangValueList(lineValue, lengthList, code);
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
			throw new AppSystemException("解析非定长文件转存报错", e);
		}
		return unloadFileAbsolutePath + JdbcCollectTableHandleParse.STRSPLIT + fileRowCount;
	}

}
