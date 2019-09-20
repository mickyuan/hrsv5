package hrds.agent.job.biz.core.dbstage.writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * ClassName: AbstractFileWriter <br/>
 * Function: 数据库直连采集以指定的格式将数据卸到指定的数据文件. <br/>
 * Reason: 接口适配器，抽象类
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public class AbstractFileWriter implements FileWriterInterface {
	private final static Logger LOGGER = LoggerFactory.getLogger(AbstractFileWriter.class);

	/**
	 * @Description: 这是一个空实现，留给每个具体的实现类去实现
	 * @Param: metaDataMap：写文件需要用到的meta信息，取值范围 : Map<String, Object>
	 * @Param: rs：当前线程采集到的Result, 取值范围 : ResultSet
	 * @Param: tableName：表名, 用于大字段数据写avro, 取值范围 : String
	 * @return: String
	 * @Author: WangZhengcheng
	 * @Date: 2019/8/13
	 */
	@Override
	public String writeDataAsSpecifieFormat(Map<String, Object> metaDataMap, ResultSet rs, String tableName)
			throws IOException, SQLException {
		throw new IllegalStateException("这是一个空实现");
	}

	/**
	 * @Description: 抽象类中实现LONGVARCHAR、CLOB转byte[]
	 * @Param: characterStream：LONGVARCHAR、CLOB类型数据的流, 取值范围 : java.io.Reader
	 * @return: byte[]
	 * @Author: WangZhengcheng
	 * @Date: 2019/8/13
	 * 步骤：
	 * 1、将characterStream用BufferedReader进行读取
	 * 2、读取的结果存到ByteArrayOutputStream内置的字节数组中
	 * 3、获得字节数组并返回
	 */
	@Override
	public byte[] longvarcharToByte(Reader characterStream) {
		ByteArrayOutputStream bytestream = null;
		BufferedReader in = null;
		byte imgdata[] = null;
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
			imgdata = bytestream.toByteArray();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		} finally {
			try {
				bytestream.close();
				in.close();
			} catch (Exception ex) {
				LOGGER.error(ex.getMessage());
			}
		}
		return imgdata;
	}

	/**
	 * @Description: 抽象类中实现LONGVARCHAR、CLOB转byte[]
	 * @Param: blob：blob类型字段
	 * @return: byte[]
	 * @Author: WangZhengcheng
	 * @Date: 2019/8/13
	 * 步骤:
	 * 1、以流的形式获取此Blob实例指定的BLOB值,并获取BufferedInputStream实例
	 * 2、构建用于保存结果的字节数组
	 * 3、从流中读数据并保存到字节数组中
	 */
	@Override
	public byte[] blobToBytes(Blob blob) {
		BufferedInputStream is = null;
		try {
			//1、以流的形式获取此Blob实例指定的BLOB值,并获取BufferedInputStream实例
			is = new BufferedInputStream(blob.getBinaryStream());
			//2、构建用于保存结果的字节数组
			byte[] bytes = new byte[(int) blob.length()];
			int len = bytes.length;
			int offset = 0;
			int read = 0;
			//3、从流中读数据并保存到字节数组中
			while (offset < len && (read = is.read(bytes, offset, len - offset)) >= 0) {
				offset += read;
			}
			return bytes;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
			}
		}
		return null;
	}
}
