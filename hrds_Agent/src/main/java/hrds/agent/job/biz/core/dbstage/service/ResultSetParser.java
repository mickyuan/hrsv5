package hrds.agent.job.biz.core.dbstage.service;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.core.dbstage.writer.FileWriterFactory;
import hrds.commons.exception.AppSystemException;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

@DocClass(desc = "每个采集线程分别调用，用于解析当前线程采集到的ResultSet,并根据卸数的数据文件类型，" +
		"调用相应的方法写数据文件", author = "WangZhengcheng")
public class ResultSetParser {
	@Method(desc = "解析ResultSet", logicStep = "" +
			"1、获得本次采集的数据库META信息" +
			"2、对后续需要使用的META信息(列名，列类型，列长度)，使用分隔符进行组装" +
			"3、在jobInfo中拿到数据清洗规则(字段清洗，表清洗)，并调用工具类(ColCleanRuleParser，TbCleanRuleParser)中的方法进行解析" +
			"4、如果在表清洗中进行了列合并，调用工具类ColumnTool对组装好的META信息进行更新" +
			"5、如果在列清洗中进行了列拆分，调用工具类ColumnTool对组装好的META信息进行更新" +
			"6、落地文件需要追加开始时间和结束时间(9999-12-31)列，如果需要，还要追加MD5列" +
			"7、构造metaDataMap，根据落地数据文件类型，初始化FileWriterInterface实现类，由实现类去写文件" +
			"8、写文件结束，返回本线程生成数据文件的路径")
	@Param(name = "rs", desc = "当前线程执行分页SQL采集到的数据集", range = "不为空")
	@Param(name = "jobInfo", desc = "保存有当前作业信息的实体类对象", range = "不为空，JobInfo实体类对象")
	@Param(name = "pageNum", desc = "当前采集的页码，用于在写文件时计算行计数器，防止多个数据文件中的Avro行号冲突"
			, range = "不为空")
	@Param(name = "pageRow", desc = "当前码的数据量，用于在写文件时计算行计数器，防止多个数据文件中的Avro行号冲突"
			, range = "不为空")
	@Return(desc = "当前线程生成数据文件的路径", range = "不会为null")
	//TODO pageNum和pageRow一起，在写文件的时候，用于判断文件是否过大，如果文件过大，可以对单个数据文件进行拆分
	public String parseResultSet(ResultSet rs, CollectTableBean collectTableBean, long pageNum,
	                             long pageRow, TableBean tableBean)
			throws SQLException, IOException {
		//获得数据文件格式
		String format = collectTableBean.getDbfile_format();
		if (format == null || format.isEmpty()) {
			throw new AppSystemException("HDFS文件类型不能为空");
		}
		//当前线程生成的数据文件的路径，用于返回
		//8、写文件结束，返回本线程生成数据文件的路径和一个写出数据量
		return FileWriterFactory.getFileWriterImpl(format).writeFiles(rs, collectTableBean,
				pageNum, pageRow, tableBean);
	}

	@Method(desc = "获取列数据类型和长度/精度", logicStep = "" +
			"1、考虑到有些类型在数据库中在获取数据类型的时候就会带有(),同时还能获取到数据的长度/精度，" +
			"因此我们要对所有数据库进行统一处理，去掉()中的内容，使用JDBC提供的方法读取的长度和精度进行拼接" +
			"2、对不包含长度和精度的数据类型进行处理，返回数据类型" +
			"3、对包含长度和精度的数据类型进行处理，返回数据类型(长度,精度)" +
			"4、对只包含长度的数据类型进行处理，返回数据类型(长度)")
	@Param(name = "columnType", desc = "数据库类型", range = "不为null,java.sql.Types对象实例")
	@Param(name = "columnTypeName", desc = "字符串形式的数据库类型，通过调用ResultSetMetaData.getColumnTypeName()得到"
			, range = "不为null")
	@Param(name = "precision", desc = "对于数字类型，precision表示的是数字的精度，对于字符类型，这里表示的是长度，" +
			"调用ResultSetMetaData.getPrecision()得到", range = "不限")
	@Param(name = "scale", desc = "列数据类型小数点右边的指定列的位数，调用ResultSetMetaData.getScale()得到"
			, range = "不限，对于不适用小数位数的数据类型，返回0")
	@Return(desc = "经过处理后的数据类型", range = "" +
			"1、对不包含长度和精度的数据类型进行处理，返回数据类型" +
			"2、对包含长度和精度的数据类型进行处理，返回数据类型(长度,精度)" +
			"3、对只包含长度的数据类型进行处理，返回数据类型(长度)")
	private String getColTypeAndPreci(int columnType, String columnTypeName, int precision, int scale) {
		//1、考虑到有些类型在数据库中在获取数据类型的时候就会带有(),同时还能获取到数据的长度和精度，因此我们要对所有数据库进行统一处理，去掉()中的内容，使用JDBC提供的方法读取的长度/精度进行拼接
		if (precision != 0) {
			int index = columnTypeName.indexOf("(");
			if (index != -1) {
				columnTypeName = columnTypeName.substring(0, index);
			}
		}
		String colTypeAndPreci;
		if (Types.INTEGER == columnType || Types.TINYINT == columnType || Types.SMALLINT == columnType ||
				Types.BIGINT == columnType) {
			//2、上述数据类型不包含长度和精度
			colTypeAndPreci = columnTypeName;
		} else if (Types.NUMERIC == columnType || Types.FLOAT == columnType ||
				Types.DOUBLE == columnType || Types.DECIMAL == columnType) {
			//上述数据类型包含长度和精度，对长度和精度进行处理，返回(长度,精度)
			//1、当一个数的整数部分的长度 > p-s 时，Oracle就会报错
			//2、当一个数的小数部分的长度 > s 时，Oracle就会舍入。
			//3、当s(scale)为负数时，Oracle就对小数点左边的s个数字进行舍入。
			//4、当s > p 时, p表示小数点后第s位向左最多可以有多少位数字，如果大于p则Oracle报错，小数点后s位向右的数字被舍入
			if (precision > precision - Math.abs(scale) || scale > precision || precision == 0) {
				precision = 38;
				scale = 12;
			}
			colTypeAndPreci = columnTypeName + "(" + precision + "," + scale + ")";
		} else {
			//处理字符串类型，只包含长度,不包含精度
			if ("char".equalsIgnoreCase(columnTypeName) && precision > 255) {
				columnTypeName = "varchar";
			}
			colTypeAndPreci = columnTypeName + "(" + precision + ")";
		}
		return colTypeAndPreci;
	}
}
