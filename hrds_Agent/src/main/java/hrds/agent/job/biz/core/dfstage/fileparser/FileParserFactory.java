package hrds.agent.job.biz.core.dfstage.fileparser;

import fd.ng.core.annotation.DocClass;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.TableBean;
import hrds.commons.codes.FileFormat;
import hrds.commons.codes.UnloadType;
import hrds.commons.exception.AppSystemException;

/**
 * FileWriterFactory
 * date: 2019/12/6 17:09
 * author: zxz
 */
@DocClass(desc = "卸数，写文件的工厂", author = "zxz", createdate = "2019/12/6 17:09")
public class FileParserFactory {

	private FileParserFactory() {

	}

	public static FileParserInterface getFileParserImpl(TableBean tableBean,
	                                                    CollectTableBean collectTableBean, String readFile) {
		String unload_type = collectTableBean.getUnload_type();
		String format = collectTableBean.getSourceData_extraction_def().getDbfile_format();
		FileParserInterface fileParserInterface;
		if (UnloadType.ZengLiangXieShu.getCode().equals(unload_type)) {
			fileParserInterface = new IncrementFileParserDeal(tableBean, collectTableBean, readFile);
		} else if (UnloadType.QuanLiangXieShu.getCode().equals(unload_type)) {
			if (FileFormat.CSV.getCode().equals(format)) {
				//写CSV文件实现类
				fileParserInterface = new CsvFileParserDeal(tableBean, collectTableBean, readFile);
			} else if (FileFormat.ORC.getCode().equals(format)) {
				//写ORC文件实现类
				fileParserInterface = new OrcFileParserDeal(tableBean, collectTableBean, readFile);
			} else if (FileFormat.PARQUET.getCode().equals(format)) {
				//写PARQUET文件实现类
				fileParserInterface = new ParquetFileParserDeal(tableBean, collectTableBean, readFile);
			} else if (FileFormat.SEQUENCEFILE.getCode().equals(format)) {
				//写SEQUENCE文件实现类
				fileParserInterface = new SequenceFileParserDeal(tableBean, collectTableBean, readFile);
			} else if (FileFormat.DingChang.getCode().equals(format)) {
				//写定长文件实现类
				fileParserInterface = new FixedFileParserDeal(tableBean, collectTableBean, readFile);
			} else if (FileFormat.FeiDingChang.getCode().equals(format)) {
				//写非定长文件实现类
				fileParserInterface = new NonFixedFileParserDeal(tableBean, collectTableBean, readFile);
			} else {
				throw new AppSystemException("系统仅支持落地CSV/PARQUET/ORC/SEQUENCE/定长/非定长数据文件");
			}
		} else {
			throw new AppSystemException("数据库抽取方式参数不正确");
		}
		return fileParserInterface;
	}
}
