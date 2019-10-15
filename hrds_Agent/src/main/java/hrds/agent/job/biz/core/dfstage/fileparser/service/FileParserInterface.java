package hrds.agent.job.biz.core.dfstage.fileparser.service;

import fd.ng.core.annotation.Class;

import java.io.IOException;

@Class(desc = "数据采集所用到的文件分析接口,所有采集的文件处理，都有该类中的实现", author = "WangZhengcheng")
public interface FileParserInterface {
	/**
	 * 用于处理使用定长方式组织数据文件的接口
	 *
	 * @return long   数据行数
	 * @author 13616
	 * @date 2019/8/2 14:11
	 */
	long handleFixChar();

	/**
	 * 用于处理使用固定分隔符方式组织数据文件的接口
	 *
	 * @return long   数据行数
	 * @author 13616
	 * @date 2019/8/2 14:13
	 */
	long handleSeparator();

	/**
	 * 用于处理使用标准CSV方式组织数据文件的接口
	 *
	 * @return long   数据行数
	 * @throws IOException 写文件发生异常时，抛出该异常
	 * @author 13616
	 * @date 2019/8/7 12:02
	 */
	long handleCsv() throws IOException;
}
