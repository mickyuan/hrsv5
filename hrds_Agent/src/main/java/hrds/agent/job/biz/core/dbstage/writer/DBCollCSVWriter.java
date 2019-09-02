package hrds.agent.job.biz.core.dbstage.writer;

import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
import fd.ng.core.utils.StringUtil;
import hrds.agent.job.biz.bean.JobInfo;
import hrds.agent.job.biz.bean.TaskInfo;
import hrds.agent.job.biz.constant.FileFormatConstant;
import hrds.agent.job.biz.constant.IsFlag;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.dataclean.columnclean.ColumnCleanUtil;
import hrds.agent.job.biz.dataclean.tableclean.TableCleanUtil;
import hrds.agent.job.biz.utils.FileUtil;
import hrds.agent.job.biz.utils.ProductFileUtil;
import org.apache.avro.Schema;
import org.apache.avro.file.CodecFactory;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ClassName: DBCollCSVWriter <br/>
 * Function: 数据库直连采集以CSV格式进行列数据清洗并写CSV文件. <br/>
 * Reason:
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public class DBCollCSVWriter extends AbstractFileWriter {

    private final static Logger LOGGER = LoggerFactory.getLogger(DBCollCSVWriter.class);
    //avro
    private static final String SCHEMA_JSON = "{\"type\": \"record\",\"name\": \"BigFilesTest\", " + "\"fields\": [" + "{\"name\":\"" + "currValue"
            + "\",\"type\":\"string\"}," + "{\"name\":\"" + "readerToByte" + "\", \"type\":\"bytes\"}" + "]}";
    private static final Schema SCHEMA = new Schema.Parser().parse(SCHEMA_JSON);
    private static final GenericRecord record = new GenericData.Record(SCHEMA);

    private final JobInfo jobInfo;

    //行计数器(用于处理大字段写入avro时的行号),共享出去,使用AtomicLong保证线程安全
    private AtomicLong lineCounter;

    public DBCollCSVWriter(JobInfo jobInfo, int pageNum, int pageRow) {
        this.jobInfo = jobInfo;
        this.lineCounter = new AtomicLong(pageNum * pageRow);
    }

    /**
     * @Description: 写CSV，完成之后返回文件的文件名(包含路径)
     * @Param: metaDataMap:写文件需要用到的meta信息
     * @Param: rs：当前线程采集到的Result
     * @return:String
     * @Author: WangZhengcheng
     * @Date: 2019/8/13
     */
    @Override
    public String writeDataAsSpecifieFormat(Map<String, Object> metaDataMap, ResultSet rs, String tableName) throws IOException, SQLException {
        if (metaDataMap == null || metaDataMap.isEmpty()) {
            throw new RuntimeException("写CSV文件阶段,元信息不能为空");
        }
        if (rs == null) {
            throw new RuntimeException("写CSV文件阶段,数据不能为空");
        }
        if (tableName == null) {
            throw new RuntimeException("写CSV文件阶段,表名不能为空");
        }

        OutputStream outputStream = null;
        DataFileWriter<Object> avroWriter = null;

        //用于存放整表清洗规则
        Map<String, Object> tableCleanRule = new HashMap<>();

        //创建数据文件存放目录
        boolean result = FileUtil.createDataFileDirByJob(this.jobInfo);
        if (!result) {
            throw new RuntimeException("创建数据文件目录失败");
        }
        //数据文件的文件名 ： jobID + 处理线程号 + 时间戳,在作业配置文件目录下的datafile目录中
        String path = ProductFileUtil.getDataFilePathByJobID(this.jobInfo) + File.separatorChar + jobInfo.getJobId() + Thread.currentThread().getId()
                + System.currentTimeMillis() + "." + FileFormatConstant.CSV.getMessage();

        int[] colTypeArrs = (int[]) metaDataMap.get("colTypeArr");
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

        LOGGER.info("线程" + Thread.currentThread().getId() + "写CSV文件开始");
        //表头
        StringBuilder columns = (StringBuilder) metaDataMap.get("columns");
        List<String> headers = StringUtil.split(columns.toString(), JobConstant.COLUMN_NAME_SEPARATOR);

        //写CSV表头
        File csvFile = new File(path);
        boolean newFile = csvFile.createNewFile();
        if (!newFile) {
            throw new RuntimeException("创建数据文件失败");
        }
        Writer fileWriter = new FileWriter(csvFile);
        CsvWriter writer = new CsvWriter(fileWriter, new CsvWriterSettings());
        writer.writeHeaders(headers);

        //获取所有列的值用来生成该行数据的MD5值
        StringBuilder MD5 = new StringBuilder(1024 * 1024);
        //用于存放每一列的值
        String currColValue;
        //用于存放大字段类型列的值的byte数组
        byte[] byteArr = null;
        //列数据类型(长度,精度) 在列数据清洗字段拆分的时候需要
        List<String> columnsTypeAndPreci = StringUtil.split(metaDataMap.get("columnsTypeAndPreci").toString(), JobConstant.COLUMN_TYPE_SEPARATOR);
        int columnCount = (int) metaDataMap.get("columnCount");

        //用于写文件的list集合
        List<String> fileList = new ArrayList<>();
        ResultSetMetaData metaData = rs.getMetaData();
        while (rs.next()) {
            long lineNum = lineCounter.incrementAndGet();
            //获取每行数据的所有字段值用来生成MD5，因此每循环一列，就需要清空一次
            MD5.delete(0, MD5.length());
            for (int j = 1; j <= columnCount; j++) {
                int colType = colTypeArrs[j - 1];
                if (colType == java.sql.Types.LONGVARCHAR) {
                    Reader characterStream = rs.getCharacterStream(j);
                    if (characterStream != null) {
                        //对LONGVARCHAR类型进行处理
                        byteArr = longvarcharToByte(characterStream);
                        //以"LOBs_表名_列号_行号_原类型_avro对象序列"存放这个值
                        currColValue = "_LOBs_" + tableName + "_" + j + "_" + lineNum + "_LONGVARCHAR_" + avroWriter.sync();
                        String reader2String = new String(byteArr);
                        MD5.append(reader2String);
                    } else {
                        currColValue = "";
                        MD5.append(currColValue);
                    }
                } else if (colType == java.sql.Types.BLOB) {
                    Blob blob = rs.getBlob(j);
                    if (blob != null) {
                        //对Blob类型进行处理
                        byteArr = blobToBytes(blob);
                        //以"LOBs_表名_列号_行号_原类型_avro对象序列"存放这个值
                        currColValue = "_LOBs_" + tableName + "_" + j + "_" + lineNum + "_BLOB_" + avroWriter.sync();
                        String reader2String = new String(byteArr);
                        MD5.append(reader2String);
                    } else {
                        currColValue = "";
                        MD5.append(currColValue);
                    }
                } else if (colType == java.sql.Types.CLOB) {
                    //对Clob类型进行处理
                    Reader characterStream = rs.getClob(j).getCharacterStream();
                    if (characterStream != null) {
                        byteArr = longvarcharToByte(characterStream);
                        //以"LOBs_表名_列号_行号_原类型_avro对象序列"存放这个值
                        currColValue = "_LOBs_" + tableName + "_" + j + "_" + lineNum + "_CLOB_" + avroWriter.sync();
                        String reader2String = new String(byteArr);
                        MD5.append(reader2String);
                    } else {
                        currColValue = "";
                        MD5.append(currColValue);
                    }
                } else {
                    Object colValue = rs.getObject(j);
                    if (colValue != null) {
                        if (colType == java.sql.Types.DATE || colType == java.sql.Types.TIME || colType == java.sql.Types.TIMESTAMP) {
                            //遇到日期类型，提供可配置的映射转换能力，目前先转为Timestamp
                            Date date = rs.getTimestamp(j);
                            currColValue = date.toString();
                        } else if (colType == java.sql.Types.CHAR || colType == java.sql.Types.VARCHAR ||
                                colType == java.sql.Types.NVARCHAR || colType == java.sql.Types.BINARY) {
                            currColValue = rs.getString(j);
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
                    //对单行数据的每一列进行清洗，返回清洗后的结果，并追加到MD5后面
                    //先进行列清洗
                    Map<String, Map<String, Object>> columnCleanRule = (Map<String, Map<String, Object>>) metaDataMap.get("columnCleanRule");
                    currColValue = ColumnCleanUtil.colDataClean(currColValue, metaData.getColumnName(j), null, columnsTypeAndPreci.get(j - 1), FileFormatConstant.CSV.getMessage(), columnCleanRule, null);
                    //对列清洗后的结果再进行表清洗(除列合并)
                    tableCleanRule = (Map<String, Object>) metaDataMap.get("tableCleanRule");
                    TableCleanUtil.tbDataClean(currColValue, metaData.getColumnName(j), null, columnsTypeAndPreci.get(j - 1), FileFormatConstant.CSV.getMessage(), tableCleanRule);
                    MD5.append(currColValue);
                }

                //如果有列合并清洗操作，在此处理列合并
                Map<String, String> mergeMap = (Map<String, String>) tableCleanRule.get("merge");
                if(!mergeMap.isEmpty()){

                }

                //按行向avro文件中写入信息（每行）
                if(byteArr != null){
                    record.put("currColValue", currColValue);
                    record.put("byteArr", ByteBuffer.wrap(byteArr));
                    avroWriter.append(record);
                }

                if (j < columnCount) {
                    MD5.append(JobConstant.COLUMN_NAME_SEPARATOR);
                }
                fileList.add(currColValue);
            }

            //在这里做列合并

            boolean is_MD5 = (IsFlag.YES.getCode() == Integer.parseInt(this.jobInfo.getIs_md5()));
            if (is_MD5) {
                //如果用户选择生成MD5，则从任务对象中取到任务的开始时间
                TaskInfo taskInfo = FileUtil.getTaskInfoByTaskID(this.jobInfo.getTaskId());
                if (taskInfo != null) {
                    fileList.add(taskInfo.getJobstartdate());
                } else {
                    throw new RuntimeException("未获取到包含该作业的任务");
                }
                //设置任务结束时间
                fileList.add(JobConstant.MAX_DATE);
                //设置MD5值
                String MD5Val = DigestUtils.md5Hex(MD5.toString());
                fileList.add(MD5Val);
            }
            writer.writeRow(fileList);
            writer.flush();
            fileList.clear();
        }
        //关闭资源
        avroWriter.close();
        outputStream.close();
        writer.close();
        LOGGER.info("线程" + Thread.currentThread().getId() + "写CSV文件结束");
        return path;
    }
}
