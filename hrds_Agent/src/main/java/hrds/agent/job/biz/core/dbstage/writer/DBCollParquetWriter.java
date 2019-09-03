package hrds.agent.job.biz.core.dbstage.writer;

import hrds.agent.job.biz.bean.JobInfo;
import hrds.agent.job.biz.bean.TaskInfo;
import hrds.agent.job.biz.constant.FileFormatConstant;
import hrds.agent.job.biz.constant.IsFlag;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.dataclean.columnclean.ColumnCleanUtil;
import hrds.agent.job.biz.utils.FileUtil;
import hrds.agent.job.biz.utils.ParquetUtil;
import hrds.agent.job.biz.utils.ProductFileUtil;
import org.apache.avro.Schema;
import org.apache.avro.file.CodecFactory;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.example.data.GroupFactory;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.schema.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ClassName: DBCollParquetWriter <br/>
 * Function: 数据库直连采集以Parquet格式进行列数据清洗并写Parquet文件. <br/>
 * Reason: 目前只有列数据清洗功能
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public class DBCollParquetWriter extends AbstractFileWriter {

    private final static Logger LOGGER = LoggerFactory.getLogger(DBCollParquetWriter.class);

    //avro schema
    private static final String SCHEMA_JSON = "{\"type\": \"record\",\"name\": \"BigFilesTest\", " + "\"fields\": [" + "{\"name\":\"" + "currValue"
            + "\",\"type\":\"string\"}," + "{\"name\":\"" + "readerToByte" + "\", \"type\":\"bytes\"}" + "]}";
    private static final Schema SCHEMA = new Schema.Parser().parse(SCHEMA_JSON);
    private static final GenericRecord record = new GenericData.Record(SCHEMA);

    //行计数器(用于处理大字段写入avro时的行号),共享出去,使用AtomicLong保证线程安全
    private AtomicLong lineCounter;

    private final GroupFactory factory;
    private final MessageType schema;
    private ParquetWriter<Group> writer;

    private final JobInfo jobInfo;

    public DBCollParquetWriter(JobInfo jobInfo, MessageType schema, GroupFactory factory, int pageNum, int pageRow) {
        this.jobInfo = jobInfo;
        this.schema = schema;
        this.factory = factory;
        lineCounter = new AtomicLong(pageNum * pageRow);
    }

    /**
    * @Description:  写parquest，完成之后返回文件的文件名(包含路径)
    * @Param:  metaDataMap：写文件需要用到的meta信息
    * @Param:  rs：当前线程采集到的Result
    * @return:  String
    * @Author: WangZhengcheng
    * @Date: 2019/8/13
    */
    /*
     * 1、校验方法入参合法性
     * 2、创建数据文件存放目录
     * 3、创建数据文件文件名，文件名为jobID + 处理线程号 + 时间戳.parquet,在作业配置文件目录下的datafile目录中
     * 4、判断本次采集得到的RS是否有CLOB，BLOB，LONGVARCHAR的大字段类型，如果有，则创建LOBs目录用于存放avro文件，并初始化写avro相关类对象
     * 5、开始写CSV文件，
     *       (1)、创建文件
     *       (2)、循环RS，获得每一行数据，针对每一行数据，循环每一列，根据每一列的类型，决定是写avro还是进行清洗
     *       (3)、执行数据清洗，包括表清洗和列清洗
     *       (4)、清洗后的结果追加到构建MD5的StringBuilder
     *       (5)、将数据放入group，写一行数据，执行下一次RS循环
     * 6、关闭资源，并返回文件路径
     * */
    @Override
    public String writeDataAsSpecifieFormat(Map<String, Object> metaDataMap, ResultSet rs, String tableName) throws IOException, SQLException {
        //1、校验方法入参合法性
        if (metaDataMap == null || metaDataMap.isEmpty()) {
            throw new RuntimeException("写PARQUET文件阶段,元信息不能为空");
        }
        if (rs == null) {
            throw new RuntimeException("写PARQUET文件阶段,数据不能为空");
        }
        if (tableName == null) {
            throw new RuntimeException("写PARQUET文件阶段,表名不能为空");
        }

        OutputStream outputStream = null;
        DataFileWriter<Object> avroWriter = null;

        //2、创建数据文件存放目录
        boolean result = FileUtil.createDataFileDirByJob(this.jobInfo);
        String outputPath;
        if (!result) {
            throw new RuntimeException("创建数据文件目录失败");
        }
        //3、创建数据文件的文件名 ：jobID + 处理线程号 + 时间戳,和作业配置文件位于同一路径下
        outputPath = ProductFileUtil.getDataFilePathByJobID(this.jobInfo) + File.separatorChar + jobInfo.getJobId() + Thread.currentThread().getId()
                + System.currentTimeMillis() + "." + FileFormatConstant.PARQUET.getMessage();

        //列类型(格式为java.sql.Types)
        int[] colTypeArrs = (int[]) metaDataMap.get("colTypeArr");
        //4、判断本次采集得到的RS是否有CLOB，BLOB，LONGVARCHAR的大字段类型，如果有，则创建LOBs目录用于存放avro文件，并初始化写avro相关类对象
        for(int i = 0; i < colTypeArrs.length; i++){
            if(colTypeArrs[i] == java.sql.Types.CLOB || colTypeArrs[i] == java.sql.Types.BLOB || colTypeArrs[i] == java.sql.Types.LONGVARCHAR){
                //说明本次采集到的内容包含大字段类型，需要对其进行avro处理
                String LOBsDir = ProductFileUtil.getLOBsPathByJobID(this.jobInfo);
                //判断LOBs目录是否存在，不存在创建
                boolean isExist = FileUtil.decideFileExist(LOBsDir);
                if (!isExist) {
                    boolean LOBsResult = FileUtil.createDir(LOBsDir);
                    if (!LOBsResult) {
                        throw new RuntimeException("创建LOBs目录失败");
                    }
                }
                File file = new File(LOBsDir);
                String avroFilePath = file.getAbsolutePath() + File.separator + "avro_" + tableName;
                outputStream = new FileOutputStream(avroFilePath);
                avroWriter = new DataFileWriter<Object>(new GenericDatumWriter<Object>()).setSyncInterval(100);
                avroWriter.setCodec(CodecFactory.snappyCodec());
                avroWriter.create(SCHEMA, outputStream);
                break;
            }
        }

        //5-1 创建文件
        LOGGER.info("线程" + Thread.currentThread().getId() + "写PARQUET文件开始");
        writer = ParquetUtil.getParquetWriter(this.schema, outputPath);
        //获取所有列的值用来生成MD5值
        StringBuilder MD5 = new StringBuilder(1024 * 1024);
        //列名
        StringBuilder columns = (StringBuilder) metaDataMap.get("columns");
        String[] columnsName = StringUtils.splitByWholeSeparatorPreserveAllTokens(columns.toString(), JobConstant.COLUMN_NAME_SEPARATOR);
        //TODO 列类型(包括长度和精度),目前不知道写parquet文件时用到的字段类型，是否需要包含长度和精度
        StringBuilder columnsTypeAndPreci = (StringBuilder) metaDataMap.get("columnsTypeAndPreci");
        String[] columnsType = StringUtils.splitByWholeSeparatorPreserveAllTokens(columnsTypeAndPreci.toString(), JobConstant.COLUMN_NAME_SEPARATOR);
        //用于存放每一列的值
        String currColValue = "";
        //列数量
        int columnCount = (int) metaDataMap.get("columnCount");
        //单行数据每列的值
        String[] datas = new String[columnCount];

        //用于存放LONGVARCHAR,BOLB,CLOB类型转换成的字节数组
        byte[] byteArr = null;
        ResultSetMetaData metaData = rs.getMetaData();
        //获取startDate
        String startDate = "";
        Group group = factory.newGroup();
        //5-2 循环RS，获得每一行数据，针对每一行数据，循环每一列，根据每一列的类型，决定是写avro还是进行清洗
        while (rs.next()) {
            long lineNum = lineCounter.incrementAndGet();
            //获取每行数据的所有字段值用来生成MD5，因此每循环一列，就需要清空一次
            MD5.delete(0, MD5.length());
            for (int i = 1; i <= columnCount; i++) {
                int colType = colTypeArrs[i - 1];
                if (colType == java.sql.Types.LONGVARCHAR) {
                    Reader characterStream = rs.getCharacterStream(i);
                    if (characterStream != null) {
                        //对LONGVARCHAR类型进行处理
                        byteArr = longvarcharToByte(characterStream);
                        //以"LOBs_表名_列号_行号_原类型_avro对象序列"存放这个值
                        currColValue = "_LOBs_" + tableName + "_" + i + "_" + lineNum + "_LONGVARCHAR_" + avroWriter.sync();
                        String reader2String = new String(byteArr);
                        MD5.append(reader2String);
                    }else{
                        currColValue = "";
                        MD5.append(currColValue);
                    }
                } else if (colType == java.sql.Types.BLOB) {
                    Blob blob = rs.getBlob(i);
                    if (blob != null) {
                        //对Blob类型进行处理
                        byteArr = blobToBytes(blob);
                        //以"LOBs_表名_列号_行号_原类型_avro对象序列"存放这个值
                        currColValue = "_LOBs_" + tableName + "_" + i + "_" + lineNum + "_BLOB_" + avroWriter.sync();
                        String reader2String = new String(byteArr);
                        MD5.append(reader2String);
                    }else{
                        currColValue = "";
                        MD5.append(currColValue);
                    }
                } else if (colType == java.sql.Types.CLOB) {
                    //对Clob类型进行处理
                    Reader characterStream = rs.getClob(i).getCharacterStream();
                    if (characterStream != null) {
                        byteArr = longvarcharToByte(characterStream);
                        //以"LOBs_表名_列号_行号_原类型_avro对象序列"存放这个值
                        currColValue = "_LOBs_" + tableName + "_" + i + "_" + lineNum + "_CLOB_" + avroWriter.sync();
                        String reader2String = new String(byteArr);
                        MD5.append(reader2String);
                    }else{
                        currColValue = "";
                        MD5.append(currColValue);
                    }
                } else {
                    Object colValue = rs.getObject(i);
                    if (colValue != null) {
                        if (colType == java.sql.Types.DATE || colType == java.sql.Types.TIME || colType == java.sql.Types.TIMESTAMP) {
                            //遇到日期类型，提供可配置的映射转换能力，目前先转为Timestamp
                            Date date = rs.getTimestamp(i);
                            currColValue = date.toString();
                        } else if (colType == java.sql.Types.CHAR || colType == java.sql.Types.VARCHAR ||
                                colType == java.sql.Types.NVARCHAR || colType == java.sql.Types.BINARY) {
                            currColValue = rs.getString(i);
                            if (currColValue.contains("\r")) {
                                currColValue = currColValue.replace('\r', ' ');
                            }
                            if (currColValue.contains("\n")) {
                                currColValue = currColValue.replace('\n', ' ');
                            }
                            if (currColValue.contains("\r\n")) {
                                currColValue = StringUtils.replace(currColValue, "\r\n", " ");
                            }
                        } else {
                            currColValue = colValue.toString();
                        }
                    } else {
                        currColValue = "";
                    }
                    //5-3 执行数据清洗，包括表清洗和列清洗
                    Map<String, Map<String, Object>> columnCleanRule = (Map<String, Map<String, Object>>) metaDataMap.get("columnCleanRule");
                    currColValue = ColumnCleanUtil.colDataClean(currColValue, metaData.getColumnName(i), group, columnsType[i - 1], FileFormatConstant.PARQUET.getMessage(), columnCleanRule, null);
                    //5-4 清洗后的结果追加到构建MD5的StringBuilder
                    MD5.append(currColValue);
                }

                //按行向avro文件中写入信息（每行）
                if(byteArr != null){
                    record.put("currColValue", currColValue);
                    record.put("byteArr", ByteBuffer.wrap(byteArr));
                    avroWriter.append(record);
                }

                if (i < columnCount) {
                    MD5.append(JobConstant.COLUMN_NAME_SEPARATOR);
                }
                datas[i - 1] = currColValue;
            }
            boolean is_MD5 = (IsFlag.YES.getCode() == Integer.parseInt(this.jobInfo.getIs_md5()));
            //5-5 将数据放入group，写一行数据，执行下一次RS循环
            if (is_MD5) {
                //如果用户选择生成MD5，则从任务对象中取到任务的开始时间
                TaskInfo taskInfo = FileUtil.getTaskInfoByTaskID(this.jobInfo.getTaskId());
                if (taskInfo != null) {
                    startDate = taskInfo.getJobstartdate();
                } else {
                    throw new RuntimeException("未获取到包含该作业的任务");
                }
                writeLine(group, MD5, startDate);
            }else{
                writeLine(group);
            }
        }
        //6、关闭资源，并返回文件路径
        avroWriter.close();
        outputStream.close();
        stopStream();

        LOGGER.info("线程" + Thread.currentThread().getId() + "写PARQUET文件结束");
        return outputPath;
    }

    private void writeLine(Group group, StringBuilder MD5, String startDate) throws IOException {
        group.append(JobConstant.START_DATE_NAME, startDate);
        //计算MD5
        String MD5Val = DigestUtils.md5Hex(MD5.toString());
        group.append(JobConstant.MD5_NAME, MD5Val);
        group.append(JobConstant.MAX_DATE_NAME, JobConstant.MAX_DATE);
        writer.write(group);
    }

    private void writeLine(Group group) throws IOException {
        writer.write(group);
    }

    private void stopStream() throws IOException {
        writer.close();
    }
}
