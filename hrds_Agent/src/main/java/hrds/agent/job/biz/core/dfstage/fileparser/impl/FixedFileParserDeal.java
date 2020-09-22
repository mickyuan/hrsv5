package hrds.agent.job.biz.core.dfstage.fileparser.impl;

import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.core.dfstage.fileparser.FileParserAbstract;
import hrds.agent.job.biz.core.dfstage.service.ReadFileToDataBase;
import hrds.agent.job.biz.utils.TypeTransLength;
import hrds.commons.codes.DataBaseCode;
import hrds.commons.codes.IsFlag;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.Constant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
	//打印日志
	private static final Logger LOGGER = LogManager.getLogger();

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
			if (IsFlag.Shi.getCode().equals(tableBean.getIs_header())) {
				//判断包含表头，先读取表头
				lineValue = br.readLine();
				if (lineValue != null) {
					LOGGER.info("读取到表头为：" + lineValue);
				}
			}
			while ((lineValue = br.readLine()) != null) {
				fileRowCount++;
				//获取定长文件，解析每行数据转为list
				valueList = ReadFileToDataBase.getDingChangValueList(lineValue, lengthList, code);
				//校验数据
				checkData(valueList, fileRowCount);
				dealLine(valueList);
				//每50000行flash一次
				if (fileRowCount % JobConstant.BUFFER_ROW == 0) {
					writer.flush();
					LOGGER.info("正在处理转存文件，已写入" + fileRowCount + "行");
				}
			}
			writer.flush();
		} catch (Exception e) {
			throw new AppSystemException("解析非定长文件转存报错", e);
		}
		return unloadFileAbsolutePath + Constant.METAINFOSPLIT + fileRowCount;
	}

}
