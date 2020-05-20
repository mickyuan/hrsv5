package hrds.agent.job.biz.core.dbstage.writer;

import fd.ng.core.annotation.DocClass;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.core.dbstage.writer.impl.*;
import hrds.commons.codes.FileFormat;
import hrds.commons.codes.UnloadType;
import hrds.commons.entity.Data_extraction_def;
import hrds.commons.exception.AppSystemException;

import java.sql.ResultSet;

/**
 * FileWriterFactory
 * date: 2019/12/6 17:09
 * author: zxz
 */
@DocClass(desc = "卸数，写文件的工厂", author = "zxz", createdate = "2019/12/6 17:09")
public class FileWriterFactory {

	private FileWriterFactory() {
	}

	public static FileWriterInterface getFileWriterImpl(ResultSet resultSet, CollectTableBean collectTableBean,
	                                                    int pageNum, TableBean tableBean,
	                                                    Data_extraction_def data_extraction_def) {
		UnloadType unload_type = UnloadType.ofEnumByCode(collectTableBean.getUnload_type());
		FileFormat format = FileFormat.ofEnumByCode(data_extraction_def.getDbfile_format());
		FileWriterInterface fileWriterInterface;
		if (UnloadType.ZengLiangXieShu == unload_type) {
			fileWriterInterface = new JdbcToIncrementFileWriter(resultSet, collectTableBean, pageNum,
					tableBean, data_extraction_def);
		} else if (UnloadType.QuanLiangXieShu == unload_type) {
			if (FileFormat.CSV == format) {
				//写CSV文件实现类
				fileWriterInterface = new JdbcToCsvFileWriter(resultSet, collectTableBean, pageNum,
						tableBean, data_extraction_def);
			} else if (FileFormat.ORC == format) {
				//写ORC文件实现类
				fileWriterInterface = new JdbcToOrcFileWriter(resultSet, collectTableBean, pageNum,
						tableBean, data_extraction_def);
			} else if (FileFormat.PARQUET == format) {
				//写PARQUET文件实现类
				fileWriterInterface = new JdbcToParquetFileWriter(resultSet, collectTableBean, pageNum,
						tableBean, data_extraction_def);
			} else if (FileFormat.SEQUENCEFILE == format) {
				//写SEQUENCE文件实现类
				fileWriterInterface = new JdbcToSequenceFileWriter(resultSet, collectTableBean, pageNum,
						tableBean, data_extraction_def);
			} else if (FileFormat.DingChang == format) {
				//写定长文件实现类
				fileWriterInterface = new JdbcToFixedFileWriter(resultSet, collectTableBean, pageNum,
						tableBean, data_extraction_def);
			} else if (FileFormat.FeiDingChang == format) {
				//写非定长文件实现类
				fileWriterInterface = new JdbcToNonFixedFileWriter(resultSet, collectTableBean, pageNum,
						tableBean, data_extraction_def);
			} else {
				throw new AppSystemException("系统仅支持落地CSV/PARQUET/ORC/SEQUENCE/定长/非定长数据文件");
			}
		} else {
			throw new AppSystemException("数据库抽取方式参数不正确");
		}
		return fileWriterInterface;
	}
}
