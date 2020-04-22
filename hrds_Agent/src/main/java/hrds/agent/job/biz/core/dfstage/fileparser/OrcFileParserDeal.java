package hrds.agent.job.biz.core.dfstage.fileparser;

import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.TableBean;

import java.util.List;

/**
 * CsvFileParserDeal
 * date: 2020/4/21 16:47
 * author: zxz
 */
public class OrcFileParserDeal extends FileParserAbstract {

	public OrcFileParserDeal(TableBean tableBean, CollectTableBean collectTableBean, String readFile) {
		super(tableBean, collectTableBean, readFile);
	}

	@Override
	public String parserFile() {
		return null;
	}

	/**
	 * 处理从文件中读取出来的数据，每读取10000条数据处理一次
	 *
	 * @param lineList 五千条数据所在的集合
	 */
	@Override
	public void dealLine(List<String> lineList) {

	}
}
