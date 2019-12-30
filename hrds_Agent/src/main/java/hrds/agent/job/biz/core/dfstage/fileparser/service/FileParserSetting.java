package hrds.agent.job.biz.core.dfstage.fileparser.service;

import fd.ng.core.annotation.DocClass;
import hrds.commons.codes.DataBaseCode;

@DocClass(desc = "用于文件分析处理的设置类", author = "WangZhengcheng")
public class FileParserSetting {
	//以下是必要参数
	private final String inputfile;
	//输出文件地址
	private final String outfile;
	//    private final String tableName;
	private final String columns;
	private final String columnTypes;
	private String addColumns;
	private String addColumnType;
	//以下为可选参数
	private DataBaseCode chartset = DataBaseCode.UTF_8;
	//是否有特殊字符，用于特殊字符处理，TODO 此处还应该包括特殊字符的处理字段
	private boolean specialChar = false;
	private String[] header = null;
	private boolean skipBadRecord = false;

	/**
	 * 用于文件分析处理的设置类
	 *
	 * @param inputfile   文件地址，输入源
	 * @param outfile     文件地址，输出目的地
	 * @param columns     字段列表，以"，"分割字符串
	 * @param columnTypes 字段类型列表，以"|"分割字符串
	 * @author 13616
	 * @date 2019/8/7 12:04
	 */
	public FileParserSetting(String inputfile, String outfile, String columns, String columnTypes) {
		this.inputfile = inputfile;
		this.outfile = outfile;
//        this.tableName = tableName;
		this.columns = columns;
		this.columnTypes = columnTypes;
	}

	/**
	 * 设置字符
	 *
	 * @param chartset    CharSetConstant对象，字符编码
	 * @param specialChar 是否有特殊字符
	 * @return void
	 * @author 13616
	 * @date 2019/8/7 12:06
	 */
	public void setCharset(DataBaseCode chartset, boolean specialChar) {
		this.chartset = chartset;
		this.specialChar = specialChar;
	}

	/**
	 * 设置新增的字段，比如md5列  TODO 此处设计有问题，新增的三列为写死的，该方法是否还需要
	 *
	 * @param columns     字段列表，以"，"分割字符串
	 * @param columnTypes 字段类型列表，以"|"分割字符串
	 * @return void
	 * @throws
	 * @author 13616
	 * @date 2019/8/7 12:06
	 */
	public void setAddtionColumn(String columns, String columnTypes) {
		this.addColumns = columns;
		this.addColumnType = columnTypes;
	}

	/**
	 * 设置表头，TODO 此处设计有问题，字段列表意味则表头，是否需要该设置
	 *
	 * @param header 表头，字段列表
	 * @return void
	 * @author 13616
	 * @date 2019/8/7 12:09
	 */
	public void setHeader(String[] header) {
		this.header = header;
	}

	/**
	 * 设置是否跳过损坏行
	 *
	 * @param skipBadRecord
	 * @return void
	 * @author 13616
	 * @date 2019/8/7 12:10
	 */
	public void setSkipBadRecord(boolean skipBadRecord) {
		this.skipBadRecord = skipBadRecord;
	}

	public String getInputfile() {
		return inputfile;
	}

	public String getOutfile() {
		return outfile;
	}

//    public String getTableName() {
//        return tableName;
//    }

	public String getColumns() {
		return columns;
	}

	public String getColumnTypes() {
		return columnTypes;
	}

	public String getAddColumns() {
		return addColumns;
	}

	public String getAddColumnType() {
		return addColumnType;
	}

	public boolean isSpecialChar() {
		return specialChar;
	}

	public DataBaseCode getChartset() {
		return chartset;
	}

	public boolean getIsSpecialCharChar() {
		return specialChar;
	}

	public String[] getHeader() {
		return header;
	}

	public boolean isSkipBadRecord() {
		return skipBadRecord;
	}
}
