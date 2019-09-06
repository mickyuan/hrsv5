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
	 * @Param: metaDataMap：写文件需要用到的meta信息
	 * @Param: rs：当前线程采集到的Result
	 * @return: String
	 * @Author: WangZhengcheng
	 * @Date: 2019/8/13
	 */
	@Override
	public String writeDataAsSpecifieFormat(Map<String, Object> metaDataMap, ResultSet rs, String tableName) throws IOException, SQLException {
		throw new IllegalStateException("这是一个空实现");
	}

	/**
	 * @Description: 抽象类中实现LONGVARCHAR、CLOB转byte[]
	 * @Param: characterStream：LONGVARCHAR、CLOB类型数据的流
	 * @return: byte[]
	 * @Author: WangZhengcheng
	 * @Date: 2019/8/13
	 */
	@Override
	public byte[] longvarcharToByte(Reader characterStream) {
		ByteArrayOutputStream bytestream = null;
		BufferedReader in = null;
		byte imgdata[] = null;
		try {
			bytestream = new ByteArrayOutputStream();
			in = new BufferedReader(characterStream);
			int ch;
			while ((ch = in.read()) != -1) {
				bytestream.write(ch);
			}
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
	 */
	@Override
	public byte[] blobToBytes(Blob blob) {
		BufferedInputStream is = null;
		try {
			is = new BufferedInputStream(blob.getBinaryStream());
			byte[] bytes = new byte[(int) blob.length()];
			int len = bytes.length;
			int offset = 0;
			int read = 0;
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
