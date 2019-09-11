package hrds.agent.job.biz.core.dbstage.writer;

import java.io.IOException;
import java.io.Reader;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * ClassName: FileWriterInterface <br/>
 * Function: 数据库直连采集以指定的格式将数据卸到指定的数据文件. <br/>
 * Reason: 接口
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public interface FileWriterInterface {

	/**
	 * @Description: 根据数据元信息和ResultSet，写指定格式的数据文件
	 * @Param: metaDataMap : 包含有列元信息，清洗规则的map，取值范围 : Map<String, Object>
	 * @Param: rs : 数据集, 取值范围 : ResultSet
	 * @Param: tableName : 表名, 用于大字段数据写avro, 取值范围 : String
	 * @return: String
	 * @Author: WangZhengcheng
	 * @Date: 2019/8/13
	 */
	String writeDataAsSpecifieFormat(Map<String, Object> metaDataMap, ResultSet rs, String tableName) throws IOException, SQLException;

	/**
	 * @Description: 将LONGVARCHAR和CLOB类型转换为字节数组，用于写Avro
	 * @Param: characterStream : LONGVARCHAR、CLOB类型数据的流, 取值范围 : java.io.Reader
	 * @return: byte[]
	 * @Author: WangZhengcheng
	 * @Date: 2019/8/13
	 */
	byte[] longvarcharToByte(Reader characterStream);

	/**
	 * @Description: 把Blob类型转换为byte字节数组, 用于写Avro
	 * @Param: Blob blob : blob类型字段, 取值范围 : java.sql.Blob
	 * @return: byte[]
	 * @Author: WangZhengcheng
	 * @Date: 2019/8/13
	 */
	byte[] blobToBytes(Blob blob);
}
