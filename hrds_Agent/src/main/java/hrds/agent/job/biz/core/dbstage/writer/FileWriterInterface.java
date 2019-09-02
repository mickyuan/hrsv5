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
    * @Description:  根据数据元信息和ResultSet，写指定格式的数据文件
    * @Param:  metaDataMap
    * @Param:  rs
    * @return:  String
    * @Author: WangZhengcheng
    * @Date: 2019/8/13
    */
    String writeDataAsSpecifieFormat(Map<String, Object> metaDataMap, ResultSet rs, String tableName) throws IOException, SQLException;

    /**
    * @Description:  将LONGVARCHAR和CLOB类型转换为字节数组，用于写Avro
    * @Param:  characterStream
    * @return:  byte[]
    * @Author: WangZhengcheng
    * @Date: 2019/8/13
    */
    byte[] longvarcharToByte(Reader characterStream);

    /**
    * @Description:  把Blob类型转换为byte字节数组,用于写Avro
    * @Param:  Blob blob
    * @return:  byte[]
    * @Author: WangZhengcheng
    * @Date: 2019/8/13
    */
    byte[] blobToBytes(Blob blob);
}
