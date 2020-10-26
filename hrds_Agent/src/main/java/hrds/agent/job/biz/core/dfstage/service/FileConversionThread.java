package hrds.agent.job.biz.core.dfstage.service;

import fd.ng.core.annotation.DocClass;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.core.dfstage.fileparser.FileParserFactory;
import hrds.agent.job.biz.core.dfstage.fileparser.FileParserInterface;

import java.util.concurrent.Callable;

@DocClass(desc = "按照指定格式读取文件，根据页面配置进行清洗，算增量，最后写成既定格式的文件",
		author = "zxz", createdate = "2020/04/21 16:19")
public class FileConversionThread implements Callable<String> {
	private final TableBean tableBean;
	private final CollectTableBean collectTableBean;
	private final String readFile;

	public FileConversionThread(TableBean tableBean, CollectTableBean collectTableBean, String readFile) {
		this.tableBean = tableBean;
		this.collectTableBean = collectTableBean;
		this.readFile = readFile;
	}

	@Override
	public String call() throws Exception {
		FileParserInterface fileParser = FileParserFactory.getFileParserImpl(tableBean, collectTableBean, readFile);
		//解析文件，转换成固定分隔符的文件
		String parseResult = fileParser.parserFile();
		//关闭流
		fileParser.stopStream();
		return parseResult;
	}
}
