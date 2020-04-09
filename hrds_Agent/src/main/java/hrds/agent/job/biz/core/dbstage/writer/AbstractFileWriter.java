package hrds.agent.job.biz.core.dbstage.writer;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.MD5Util;
import fd.ng.core.utils.StringUtil;
import hrds.commons.codes.DataBaseCode;
import hrds.commons.exception.AppSystemException;
import org.apache.avro.Schema;
import org.apache.avro.file.CodecFactory;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@DocClass(desc = "数据库直连采集以指定的格式将数据卸到指定的数据文件，接口适配器，抽象类", author = "WangZhengcheng")
abstract class AbstractFileWriter implements FileWriterInterface {
	private final static Logger LOGGER = LoggerFactory.getLogger(AbstractFileWriter.class);
	private static final String SCHEMA_JSON = "{\"type\": \"record\",\"name\": \"BigFilesTest\", " + "\"fields\": [" + "{\"name\":\"" + "currValue"
			+ "\",\"type\":\"string\"}," + "{\"name\":\"" + "readerToByte" + "\", \"type\":\"bytes\"}" + "]}";//avro schema

	private static final Schema SCHEMA = new Schema.Parser().parse(SCHEMA_JSON);
	private static final GenericRecord record = new GenericData.Record(SCHEMA);

	@Method(desc = "把Blob类型转换为byte字节数组, 用于写Avro，在抽象类中实现，请子类不要覆盖这个方法"
			, logicStep = "1、以流的形式获取此Blob实例指定的BLOB值,并获取BufferedInputStream实例" +
			"              2、构建用于保存结果的字节数组" +
			"              3、从流中读数据并保存到字节数组中")
	@Param(name = "blob", desc = "采集得到的Blob类型的列的值", range = "不为空")
	@Return(desc = "采集得到的Blob类型的列的值转换得到的字节数组", range = "不会为null")
	private byte[] blobToBytes(Blob blob) {
		BufferedInputStream is = null;
		try {
			//1、以流的形式获取此Blob实例指定的BLOB值,并获取BufferedInputStream实例
			is = new BufferedInputStream(blob.getBinaryStream());
			//2、构建用于保存结果的字节数组
			byte[] bytes = new byte[(int) blob.length()];
			int len = bytes.length;
			int offset = 0;
			int read;
			//3、从流中读数据并保存到字节数组中
			while (offset < len && (read = is.read(bytes, offset, len - offset)) >= 0) {
				offset += read;
			}
			return bytes;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
			}
		}
		return null;
	}

	/**
	 * 解析result一行的值
	 */
	String getOneColumnValue(DataFileWriter<Object> avroWriter, long lineCounter, int pageNum, ResultSet resultSet,
	                         int type, StringBuilder sb_, int i, String hbase_name)
			throws SQLException, IOException {

		String reader2String = null;
		byte[] readerToByte = null;
		if (type == java.sql.Types.BLOB) {
			Blob blob = resultSet.getBlob(i);
			if (null != blob) {
				readerToByte = blobToBytes(blob);
				sb_.append("LOBs_").append(hbase_name).append("_").append(i).append("_")
						.append(pageNum).append("_").append(lineCounter).append("_BLOB_").append(avroWriter.sync());
				if (readerToByte != null) {
					reader2String = new String(readerToByte);
				} else {
					reader2String = "";
				}
			}
		} else {
			Object oj = resultSet.getObject(i);
			if (null != oj) {
				if (type == java.sql.Types.TIMESTAMP || type == java.sql.Types.DATE || type == java.sql.Types.TIME) {
					Date date = resultSet.getTimestamp(i);
					reader2String = date.toString();
				} else if (type == java.sql.Types.CHAR || type == java.sql.Types.VARCHAR
						|| type == java.sql.Types.NVARCHAR
						|| type == java.sql.Types.BINARY || type == java.sql.Types.CLOB
						|| type == java.sql.Types.LONGVARCHAR) {
					reader2String = oj.toString();
					//TODO 指定分隔符的情况下，这一行应该不用了，这里如果冲突，就让页面选择别的分隔符好了
//					if (reader2String.indexOf(Constant.DATADELIMITER) > -1) {
//						reader2String = reader2String.replace(Constant.DATADELIMITER, ' ');
//					}
					//TODO 目前针对换行符的问题，经过测试，可以通过自定义hive的TextInputFormat能解决自定义表的换行符，
					//TODO 但是如果页面自定义填写换行符，就导致需要每一个不同的换行符都需要对应一个自定义hive的
					//TODO TextInputFormat，难以实现，因此需要使用默认的行分隔符，或者提前实现几个TextInputFormat供选择
					//TODO 下面几行是使用默认的行分隔符，需要替换到数据本身的换行符，这里应该替换成特殊字符串，以便于还原
					if (reader2String.contains("\r")) {
						reader2String = reader2String.replace('\r', ' ');
					}
					if (reader2String.contains("\n")) {
						reader2String = reader2String.replace('\n', ' ');
					}
					if (reader2String.contains("\r\n")) {
						reader2String = StringUtil.replace(reader2String, "\r\n", " ");
					}
				} else {
					reader2String = oj.toString();
				}
			} else {
				reader2String = "";
			}
			sb_.append(reader2String);
		}
		if (readerToByte != null) {
			record.put("currValue", sb_);
			record.put("readerToByte", ByteBuffer.wrap(readerToByte));
			avroWriter.append(record);//往avro文件中写入信息（每行）
		}
		return reader2String;
	}

	/**
	 * 解析result一行的值
	 */
	String getOneColumnValue(DataFileWriter<Object> avroWriter, long lineCounter, int pageNum, ResultSet resultSet,
	                         int type, StringBuilder sb_, String column_name, String hbase_name)
			throws SQLException, IOException {

		String reader2String = null;
		byte[] readerToByte = null;
		if (type == java.sql.Types.BLOB) {
			Blob blob = resultSet.getBlob(column_name);
			if (null != blob) {
				readerToByte = blobToBytes(blob);
				sb_.append("LOBs_").append(hbase_name).append("_").append(column_name).append("_")
						.append(pageNum).append("_").append(lineCounter).append("_BLOB_").append(avroWriter.sync());
				if (readerToByte != null) {
					reader2String = new String(readerToByte);
				} else {
					reader2String = "";
				}
			}
		} else {
			Object oj = resultSet.getObject(column_name);
			if (null != oj) {
				if (type == java.sql.Types.TIMESTAMP || type == java.sql.Types.DATE || type == java.sql.Types.TIME) {
					Date date = resultSet.getTimestamp(column_name);
					reader2String = date.toString();
				} else if (type == java.sql.Types.CHAR || type == java.sql.Types.VARCHAR
						|| type == java.sql.Types.NVARCHAR
						|| type == java.sql.Types.BINARY || type == java.sql.Types.CLOB
						|| type == java.sql.Types.LONGVARCHAR) {
					reader2String = oj.toString();
					//TODO 指定分隔符的情况下，这一行应该不用了，这里如果冲突，就让页面选择别的分隔符好了
//					if (reader2String.indexOf(Constant.DATADELIMITER) > -1) {
//						reader2String = reader2String.replace(Constant.DATADELIMITER, ' ');
//					}
					//TODO 目前针对换行符的问题，经过测试，可以通过自定义hive的TextInputFormat能解决自定义表的换行符，
					//TODO 但是如果页面自定义填写换行符，就导致需要每一个不同的换行符都需要对应一个自定义hive的
					//TODO TextInputFormat，难以实现，因此需要使用默认的行分隔符，或者提前实现几个TextInputFormat供选择
					//TODO 下面几行是使用默认的行分隔符，需要替换到数据本身的换行符，这里应该替换成特殊字符串，以便于还原
					if (reader2String.contains("\r")) {
						reader2String = reader2String.replace('\r', ' ');
					}
					if (reader2String.contains("\n")) {
						reader2String = reader2String.replace('\n', ' ');
					}
					if (reader2String.contains("\r\n")) {
						reader2String = StringUtil.replace(reader2String, "\r\n", " ");
					}
				} else {
					reader2String = oj.toString();
				}
			} else {
				reader2String = "";
			}
			sb_.append(reader2String);
		}
		if (readerToByte != null) {
			record.put("currValue", sb_);
			record.put("readerToByte", ByteBuffer.wrap(readerToByte));
			avroWriter.append(record);//往avro文件中写入信息（每行）
		}
		return reader2String;
	}

	/**
	 * 源表包含BLOB、VARBINARY类型获取写一个avro文件的文件输出流
	 *
	 * @param typeArray  所有采集字段的类型的数组
	 * @param hbase_name 采集到目的地的表名
	 * @param midName    采集特殊字段合成arvo文件生成的目录
	 * @param pageNum    线程编号
	 * @return 文件输出流
	 * @throws IOException 获取avro文件输出流异常
	 */
	DataFileWriter<Object> getAvroWriter(int[] typeArray, String hbase_name,
	                                     String midName, long pageNum) throws IOException {
		DataFileWriter<Object> avroWriter = null;
		for (int type : typeArray) {
			if (type == Types.BLOB || type == Types.VARBINARY) {
				// 生成Avro文件到local
				OutputStream outputStream = new FileOutputStream(midName + "avro_" + hbase_name + pageNum);
				avroWriter = new DataFileWriter<>(new GenericDatumWriter<>()).setSyncInterval(100);
				avroWriter.setCodec(CodecFactory.snappyCodec());
				avroWriter.create(SCHEMA, outputStream);
				break;
			}
		}
		return avroWriter;
	}

	/**
	 * 源表包含BLOB、VARBINARY类型获取写一个avro文件的文件输出流
	 *
	 * @param typeMap  所有采集字段的类型的集合
	 * @param hbase_name 采集到目的地的表名
	 * @param midName    采集特殊字段合成arvo文件生成的目录
	 * @param pageNum    线程编号
	 * @return 文件输出流
	 * @throws IOException 获取avro文件输出流异常
	 */
	DataFileWriter<Object> getAvroWriter(Map<String, Integer> typeMap, String hbase_name,
	                                     String midName, long pageNum) throws IOException {
		DataFileWriter<Object> avroWriter = null;
		for (String key : typeMap.keySet()) {
			Integer type = typeMap.get(key);
			if (type == Types.BLOB || type == Types.VARBINARY) {
				// 生成Avro文件到local
				OutputStream outputStream = new FileOutputStream(midName + "avro_" + hbase_name + pageNum);
				avroWriter = new DataFileWriter<>(new GenericDatumWriter<>()).setSyncInterval(100);
				avroWriter.setCodec(CodecFactory.snappyCodec());
				avroWriter.create(SCHEMA, outputStream);
				break;
			}
		}
		return avroWriter;
	}

	/**
	 * toMD5:生成MD5值. <br/>
	 *
	 * @author Cool Yu
	 * @since JDK 1.6
	 */
	String toMD5(String plainText) {
		return MD5Util.md5String(plainText);
	}

	/**
	 * 字段变为定长
	 */
	public static String columnToFixed(String columnValue, int length, String database_code) {
		StringBuilder sb;
		try {
			byte[] bytes = columnValue.getBytes(DataBaseCode.ofValueByCode(database_code));
			int columnValueLength = bytes.length;
			sb = new StringBuilder();
			sb.append(columnValue);
			if (length >= columnValueLength) {
				for (int j = 0; j < length - columnValueLength; j++) {
					sb.append(" ");
				}
			} else {
				throw new AppSystemException("定长指定的长度小于源数据长度");
			}
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			throw new AppSystemException("定长文件卸数编码读取错误", e);
		}
	}

	/**
	 * 将string的集合转为int集合
	 */
	List<Integer> stringToIntegerList(List<String> list) {
		List<Integer> integerList = new ArrayList<>();
		for (String string : list) {
			integerList.add(Integer.parseInt(string));
		}
		return integerList;
	}
}
