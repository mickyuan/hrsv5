package hrds.agent.job.biz.core.dbstage.writer;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.MD5Util;
import fd.ng.core.utils.StringUtil;
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
import java.util.Date;

@DocClass(desc = "数据库直连采集以指定的格式将数据卸到指定的数据文件，接口适配器，抽象类", author = "WangZhengcheng")
public abstract class AbstractFileWriter implements FileWriterInterface {
	private final static Logger LOGGER = LoggerFactory.getLogger(AbstractFileWriter.class);
	private static final String SCHEMA_JSON = "{\"type\": \"record\",\"name\": \"BigFilesTest\", " + "\"fields\": [" + "{\"name\":\"" + "currValue"
			+ "\",\"type\":\"string\"}," + "{\"name\":\"" + "readerToByte" + "\", \"type\":\"bytes\"}" + "]}";//avro schema

	private static final Schema SCHEMA = new Schema.Parser().parse(SCHEMA_JSON);
	private static final GenericRecord record = new GenericData.Record(SCHEMA);


//	/**
//	 * 根据数据元信息和ResultSet，写指定格式的数据文件,
//	 *
//	 * @Param: metaDataMap Map<String, Object>
//	 *         含义：包含有列元信息，清洗规则的map
//	 *         取值范围：不为空，共有7对Entry，key分别为
//	 *                      columnsTypeAndPreci：表示列数据类型(长度/精度)
//	 *                      columnsLength : 列长度，在生成信号文件的时候需要使用
//	 *                      columns : 列名
//	 *                      colTypeArr : 列数据类型(java.sql.Types),用于判断，对不同数据类型做不同处理
//	 *                      columnCount ：该表的列的数目
//	 *                      columnCleanRule ：该表每列的清洗规则
//	 *                      tableCleanRule ：整表清洗规则
//	 * @Param: rs ResultSet
//	 *         含义：当前线程执行分页SQL得到的结果集
//	 *         取值范围：不为空
//	 * @Param: tableName String
//	 *         含义：表名, 用于大字段数据写avro
//	 *         取值范围：不为空
//	 *
//	 * @return: String
//	 *          含义：生成的数据文件的路径
//	 *          取值范围：不会为null
//	 *
//	 * */
//	@Method(desc = "根据数据元信息和ResultSet，写指定格式的数据文件,这是一个空实现，留给每个具体的实现类去实现"
//			, logicStep = "")
//	@Param(name = "metaDataMap", desc = "包含有列元信息，清洗规则的map", range = "不为空，共有7对Entry，key分别为" +
//			"1、columnsTypeAndPreci：表示列数据类型(长度/精度)" +
//			"2、columnsLength : 列长度，在生成信号文件的时候需要使用" +
//			"3、columns : 列名" +
//			"4、colTypeArr : 列数据类型(java.sql.Types),用于判断，对不同数据类型做不同处理" +
//			"5、columnCount ：该表的列的数目" +
//			"6、columnCleanRule ：该表每列的清洗规则" +
//			"7、tableCleanRule ：整表清洗规则")
//	@Param(name = "rs", desc = "当前线程执行分页SQL得到的结果集", range = "不为空")
//	@Param(name = "tableName", desc = "表名, 用于大字段数据写avro", range = "不为空")
//	@Return(desc = "生成的数据文件的路径", range = "不会为null")
//	@Override
//	public String writeDataAsSpecifieFormat(Map<String, Object> metaDataMap, ResultSet rs, String tableName)
//			throws IOException, SQLException {
//		throw new IllegalStateException("这是一个空实现");
//	}

	@Method(desc = "将LONGVARCHAR和CLOB类型转换为字节数组，用于写Avro，在抽象类中实现，请子类不要覆盖这个方法"
			, logicStep = "1、将characterStream用BufferedReader进行读取" +
			"              2、读取的结果存到ByteArrayOutputStream内置的字节数组中" +
			"              3、获得字节数组并返回")
	@Param(name = "characterStream", desc = "java.io.Reader形式得到此ResultSet结果集中当前行中指定列的值"
			, range = "不为空")
	@Return(desc = "此ResultSet结果集中当前行中指定列的值转换得到的字节数组", range = "不会为null")
	public byte[] longvarcharToByte(Reader characterStream) {
		ByteArrayOutputStream bytestream = null;
		BufferedReader in = null;
		byte[] imgData = null;
		try {
			bytestream = new ByteArrayOutputStream();
			//1、将characterStream用BufferedReader进行读取
			in = new BufferedReader(characterStream);
			int ch;
			//2、读取的结果存到ByteArrayOutputStream内置的字节数组中
			while ((ch = in.read()) != -1) {
				bytestream.write(ch);
			}
			//3、获得字节数组并返回
			imgData = bytestream.toByteArray();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		} finally {
			try {
				if (bytestream != null) {
					bytestream.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (Exception ex) {
				LOGGER.error(ex.getMessage());
			}
		}
		return imgData;
	}

	@Method(desc = "把Blob类型转换为byte字节数组, 用于写Avro，在抽象类中实现，请子类不要覆盖这个方法"
			, logicStep = "1、以流的形式获取此Blob实例指定的BLOB值,并获取BufferedInputStream实例" +
			"              2、构建用于保存结果的字节数组" +
			"              3、从流中读数据并保存到字节数组中")
	@Param(name = "blob", desc = "采集得到的Blob类型的列的值", range = "不为空")
	@Return(desc = "采集得到的Blob类型的列的值转换得到的字节数组", range = "不会为null")
	public byte[] blobToBytes(Blob blob) {
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
	protected String getOneColumnValue(DataFileWriter<Object> avroWriter, long lineCounter, ResultSet resultSet,
	                                   int type, StringBuilder sb_, int i, String hbase_name)
			throws SQLException, IOException {

		String reader2String = null;
		byte[] readerToByte = null;
		if (type == java.sql.Types.BLOB) {
			Blob blob = resultSet.getBlob(i);
			if (null != blob) {
				readerToByte = blobToBytes(blob);
				sb_.append("LOBs_").append(hbase_name).append("_").append(i).append("_").append(lineCounter)
						.append("_BLOB_").append(avroWriter.sync());
				reader2String = new String(readerToByte);
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

	protected DataFileWriter<Object> getAvroWriter(int[] typeArray, String hbase_name,
	                                               String midName, long pageNum) throws IOException {
		DataFileWriter<Object> avroWriter = null;
		for (int type : typeArray) {
			if (type == java.sql.Types.BLOB || type == java.sql.Types.LONGVARCHAR) {
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
	protected String toMD5(String plainText) {
		return MD5Util.md5String(plainText);
	}
}
