package hrds.agent.job.biz.core.dfstage.fileparser.service;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import fd.ng.core.annotation.DocClass;
import hrds.agent.job.biz.constant.CharSetConstant;
import hrds.agent.job.biz.constant.JobConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

@DocClass(desc = "数据采集所用到的文件分析抽象类,所有采集的文件处理，都可以为该类的字类，该类提供部分默认实现",
		author = "WangZhengcheng")
public abstract class AbstractFileParser implements FileParserInterface {

	private final static Logger LOGGER = LoggerFactory.getLogger(AbstractFileParser.class);
	private final static int ROW_REPORT_NUM = 1000000;
	//以下是必要参数
	private final String name;
	private final String inputfile;
	//输出文件地址
	private final String[] columns;
	private final String[] columnType;
	//以下为可选参数
	private CharSetConstant chartset;
	//是否有特殊字符，用于特殊字符处理，TODO 此处还应该包括特殊字符的处理字段
	private boolean hasSpecicalChar;
	private String[] header;
	private boolean isSkipBadRecord;
	private int addtionColumnsNum = 0;

	/**
	 * 抽象文件分析类的构造器
	 *
	 * @param name    名字，可以是数据表名或只是名字
	 * @param setting FileParserSetting对象
	 * @author 13616
	 * @date 2019/8/7 14:23
	 */
	AbstractFileParser(String name, FileParserSetting setting) {
		this.name = name;
		this.inputfile = setting.getInputfile();
		this.columns = StringUtils.splitByWholeSeparatorPreserveAllTokens(setting.getColumns().toUpperCase(), JobConstant.COLUMN_SEPARATOR);
		this.columnType = StringUtils.splitByWholeSeparatorPreserveAllTokens(setting.getColumnTypes(), JobConstant.COLUMN_TYPE_SEPARATOR);
		this.chartset = setting.getChartset();
		this.hasSpecicalChar = setting.getIsSpecialCharChar();
		this.header = setting.getHeader();
		this.isSkipBadRecord = setting.isSkipBadRecord();
		//TODO 这个东西要看具体业务实现，在判断时每行数据是否合法时（源数据没有新增的三列数据情况下），为什么不需要该信息。
		this.addtionColumnsNum = StringUtils.splitByWholeSeparatorPreserveAllTokens(setting.getAddColumns(), ",").length;
	}

	@Override
	public long handleFixChar() {
		throw new IllegalStateException("该功能还未实现");
	}

	@Override
	public long handleSeparator() {
		throw new IllegalStateException("该功能还未实现");
	}

	@Override
	public long handleCsv() throws IOException {

		CsvParserSettings settings = new CsvParserSettings();
		settings.setIgnoreLeadingWhitespaces(true);
		settings.setIgnoreTrailingWhitespaces(true);
		//设置表头
		if (this.header.length > 0) {
			settings.setHeaderExtractionEnabled(true);
			settings.setHeaders(header);
		}
		CsvParser parser = new CsvParser(settings);
		long rowNum = 1;
		try {
			parser.beginParsing(new InputStreamReader(new FileInputStream(inputfile), chartset.toString()));
			String[] row;
			while ((row = parser.parseNext()) != null) {
				if (row.length != (columns.length - addtionColumnsNum)) {
					String msg = "第 " + rowNum + " 行数据 ，数据字典表(" + name + " )定义长度和数据不匹配！\n"
							+ "数据字典定义的长度 是  " + (columns.length - addtionColumnsNum) + " 现在获取的长度是  " + row.length + "\n";
					LOGGER.warn(msg);
					//是否跳过数据损坏的行
					if (isSkipBadRecord) {
						continue;
					} else {
						throw new IllegalStateException(msg);
					}
				}
				//是否有特殊字符
				if (hasSpecicalChar) {
					int size = row.length;
					for (int i = 0; i < size; i++) {
						String data = row[i];
						//TODO 特殊字符应该用静态常量代替
						data = StringUtils.replace(data, "\n", "");
						data = StringUtils.replace(data, "\r", "");
						data = StringUtils.replace(data, "\r\n", "");
						row[i] = data;
					}
				}

				dealLine(columns, columnType, row);
				if (rowNum % ROW_REPORT_NUM == 0) {
					LOGGER.info(name + "已经处理了 ：" + rowNum + " 行数据！");
				}
				rowNum++;
			}
		} catch (IOException e) {
			LOGGER.error("在处理标准CSV文件时，发生异常：{}", e.getMessage());
		} finally {
			parser.stopParsing();
		}

		flushFile();
		return (rowNum - 1);
	}

	/**
	 * 用于处理一行数据，子类必须重载
	 *
	 * @param columns    字段列表
	 * @param columnType 字段类型列表
	 * @param data       数据列表
	 * @return void
	 * @author 13616
	 * @date 2019/8/3 23:01
	 */
	protected abstract void dealLine(String[] columns, String[] columnType, String[] data) throws IOException;

	/**
	 * 用于刷新文件，关闭IO操作。子类必须重载
	 *
	 * @author 13616
	 * @date 2019/8/2 14:35
	 */
	protected abstract void flushFile() throws IOException;

}
