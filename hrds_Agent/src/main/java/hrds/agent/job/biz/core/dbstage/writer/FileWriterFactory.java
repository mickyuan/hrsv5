package hrds.agent.job.biz.core.dbstage.writer;

import fd.ng.core.annotation.DocClass;
import hrds.commons.codes.FileFormat;
import hrds.commons.exception.AppSystemException;

/**
 * FileWriterFactory
 * date: 2019/12/6 17:09
 * author: zxz
 */
@DocClass(desc = "卸数，写文件的工厂", author = "zxz", createdate = "2019/12/6 17:09")
public class FileWriterFactory {

	private FileWriterFactory() {
	}

	public static FileWriterInterface getFileWriterImpl(String format) {
		FileWriterInterface fileWriterInterface;
		if (FileFormat.CSV.getCode().equals(format)) {
			//写CSV文件实现类
			fileWriterInterface = new JdbcToCsvFileWriter();
		} else if (FileFormat.ORC.getCode().equals(format)) {
			//写ORC文件实现类
			fileWriterInterface = new JdbcToOrcFileWriter();
		} else if (FileFormat.PARQUET.getCode().equals(format)) {
			//写PARQUET文件实现类
			fileWriterInterface = new JdbcToParquetFileWriter();
		} else if (FileFormat.SEQUENCEFILE.getCode().equals(format)) {
			//写SEQUENCE文件实现类
			fileWriterInterface = new JdbcToSequenceFileWriter();
		} else if (FileFormat.DingChang.getCode().equals(format)) {
			//写定长文件实现类
			fileWriterInterface = new JdbcToFixedFileWriter();
		} else if (FileFormat.FeiDingChang.getCode().equals(format)) {
			//写非定长文件实现类
			fileWriterInterface = new JdbcToNonFixedFileWriter();
		} else {
			throw new AppSystemException("系统仅支持落地CSV/PARQUET/ORC/SEQUENCE/定长/非定长数据文件");
		}
		return fileWriterInterface;
	}
}
