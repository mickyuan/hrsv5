package hrds.agent.job.biz.core.dfstage.fileparser;

import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.TableBean;

import java.io.File;

/**
 * FileParserAbstract
 * date: 2020/4/21 16:44
 * author: zxz
 */
public abstract class FileParserAbstract implements FileParserInterface {
	TableBean tableBean;
	CollectTableBean collectTableBean;
	String readFile;

	FileParserAbstract(TableBean tableBean, CollectTableBean collectTableBean, String readFile) {
		this.collectTableBean = collectTableBean;
		this.readFile = readFile;
		this.tableBean = tableBean;
	}
}
