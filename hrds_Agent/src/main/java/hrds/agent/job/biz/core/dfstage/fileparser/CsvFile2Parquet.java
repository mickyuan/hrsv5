package hrds.agent.job.biz.core.dfstage.fileparser;

import hrds.agent.job.biz.constant.DataTypeConstant;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.core.dfstage.fileparser.service.FileParserSetting;
import hrds.agent.job.biz.core.dfstage.fileparser.service.ParquetFileParser;
import hrds.agent.job.biz.utils.ParquetUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.parquet.example.data.GroupFactory;
import org.apache.parquet.example.data.simple.SimpleGroupFactory;
import org.apache.parquet.schema.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * ClassName: CsvFile2Parquet <br/>
 * Function: csv文件转为parquet文件. <br/>
 * Date: 2019/8/4 17:47 <br/>
 * <p>
 * Author 13616
 * Version 1.0
 * Since JDK 1.8
 **/
public class CsvFile2Parquet {

	private final static Logger LOGGER = LoggerFactory.getLogger(CsvFile2Parquet.class);

	private final String name;
	private final FileParserSetting setting;
	private final MessageType schema;
	private final GroupFactory factory;
	private final String startDate;

	/**
	 * 构造csv转parquet类
	 *
	 * @param builder Builder构建器对象，内部类
	 * @author 13616
	 * @date 2019/8/7 14:17
	 */
	private CsvFile2Parquet(Builder builder) {

		String addColumns = JobConstant.START_DATE_NAME + JobConstant.COLUMN_SEPARATOR +
				JobConstant.MAX_DATE_NAME + JobConstant.COLUMN_SEPARATOR + JobConstant.MD5_NAME;
		String columns = builder.columns + JobConstant.COLUMN_SEPARATOR + addColumns;
		String addColumnTypes = DataTypeConstant.STRING + JobConstant.COLUMN_TYPE_SEPARATOR +
				DataTypeConstant.STRING + JobConstant.COLUMN_TYPE_SEPARATOR + DataTypeConstant.STRING;
		String columnTypes = builder.columnTypes + JobConstant.COLUMN_TYPE_SEPARATOR + addColumnTypes;
		this.setting = new FileParserSetting(builder.inputFile, builder.outputFile, columns, columnTypes);
		this.setting.setAddtionColumn(addColumns, addColumnTypes);
		this.schema = ParquetUtil.getSchema(columns, columnTypes);
		this.factory = new SimpleGroupFactory(schema);
		this.name = builder.name;
		this.startDate = builder.startDate;
	}

	/**
	 * 执行csv转parquet操作
	 *
	 * @return long   数据行数
	 * @author 13616
	 * @date 2019/8/7 14:19
	 */
	public long handle() {

		long rowNum = 0;
		try {
			ParquetFileParser parser = ParquetFileParser.getInstance(name, setting, schema, factory, startDate);
			long startTime = System.currentTimeMillis();
			rowNum = parser.handleCsv();
			long endTime = System.currentTimeMillis();
			LOGGER.info("总共{}行数据，耗时：{} ms", rowNum, (endTime - startTime));
		} catch (IOException e) {
			LOGGER.error("在将CSV风格文件卸数为Parquet时失败：{}", e.getMessage());
		}
		return rowNum;
	}

	public static class Builder {

		private final String name;
		private final String columns;
		private final String columnTypes;
		private final String startDate;

		private String inputFile;
		private String outputFile;

		/**
		 * 构造构建器对象
		 *
		 * @param name        名字，可以是数据表名或只是名字
		 * @param columns     字段列表，","分割
		 * @param columnTypes 字段类型列表，"|"分割
		 * @param startDate   8位字符日期，用于拉链算法，该日期作为一列数据写入parquet文件中
		 * @author 13616
		 * @date 2019/8/7 14:19
		 */
		public Builder(String name, String columns, String columnTypes, String startDate) {
			this.name = name;
			this.columns = columns;
			this.columnTypes = columnTypes;
			this.startDate = startDate;
		}

		/**
		 * 设置输入与输出文件
		 *
		 * @param inputFile  csv数据文件地址
		 * @param outputFile parquet文件地址
		 * @return com.beyondsoft.agent.core.fileparser.CsvFile2Parquet.Builder
		 * @author 13616
		 * @date 2019/8/7 14:21
		 */
		public Builder builCsvFile(String inputFile, String outputFile) {
			this.inputFile = inputFile;
			this.outputFile = outputFile;
			return this;
		}

		/**
		 * 构造csv转parquet类对象
		 *
		 * @return com.beyondsoft.agent.core.fileparser.CsvFile2Parquet
		 * @author 13616
		 * @date 2019/8/7 14:22
		 */
		public CsvFile2Parquet build() {
			if (StringUtils.isEmpty(this.inputFile) || StringUtils.isEmpty(this.outputFile)) {
				throw new IllegalArgumentException("请调用builCsvFile构建inputFile和outputFile");
			}
			return new CsvFile2Parquet(this);
		}
	}
}
