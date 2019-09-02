package hrds.agent.job.biz.core.dfstage.fileparser.service;

import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.utils.ParquetUtil;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.example.data.GroupFactory;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.schema.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * ClassName: ParquetFileParser <br/>
 * Function: 写parquet文件的类. <br/>
 * Reason: 有写为parquet文件的需求，则使用该类. <br/>
 * Date: 2019/8/2 14:19 <br/>
 * <p>
 * Author 13616
 * Version 1.0
 * Since JDK 1.8
 **/
public class ParquetFileParser extends AbstractFileParser {

    private final static Logger LOGGER = LoggerFactory.getLogger(ParquetFileParser.class);

    private StringBuilder strBuilder = new StringBuilder();
    private final GroupFactory factory;
    private final MessageType schema;
    private final ParquetWriter<Group> writer;
    private final String startDate;
//    private final boolean isMD5;

    /**
     * 构造parquet文件处理对象
     * @author   13616
     * @date     2019/8/7 12:10
     *
     * @param name  名字，可以是数据表名或只是名字
     * @param setting   FileParserSetting 对象
     * @param schema    MessageType对象
     * @param factory   GroupFactory对象
     * @param startDate 8位字符日期，用于拉链算法，该日期作为一列数据写入parquet文件中
     * @throws IOException  无法写parquet文件时抛出该异常
     */
    private ParquetFileParser(String name, FileParserSetting setting, MessageType schema, GroupFactory factory, String startDate) throws IOException {

        super(name, setting);
        this.schema = schema;
        this.factory = factory;
        this.writer = ParquetUtil.getParquetWriter(schema, setting.getOutfile());
        this.startDate = startDate;
//        this.isMD5 = isMD5;
    }

    /**
     * 静态工厂方法，构造parquet文件处理对象
     * @author   13616
     * @date     2019/8/7 14:13
     *
     * @param name  名字，可以是数据表名或只是名字
     * @param setting   FileParserSetting 对象
     * @param schema    MessageType对象
     * @param factory   GroupFactory对象
     * @param startDate 8位字符日期，用于拉链算法，该日期作为一列数据写入parquet文件中
     * @throws IOException  无法写parquet文件时抛出该异常
     * @return   com.beyondsoft.agent.core.fileparser.service.ParquetFileParser
     */
    public static ParquetFileParser getInstance(String name, FileParserSetting setting, MessageType schema, GroupFactory factory, String startDate) throws IOException {

        return new ParquetFileParser(name, setting, schema, factory, startDate);
    }

    @Override
    protected void dealLine(String[] columns, String[] columnTypes, String[] datas) throws IOException {

        strBuilder = strBuilder.delete(0, strBuilder.length());
        Group group = factory.newGroup();
        int length = datas.length;
        String column;
        String columnType;
        String data;
        for(int i = 0 ; i < length ; i++){
            column = columns[i];
            columnType = columnTypes[i];
            data = datas[i];
            strBuilder.append(data);
            strBuilder.append(JobConstant.COLUMN_SEPARATOR);
            ParquetUtil.addData2Group(group, column, columnType, data);
        }
        group.append(JobConstant.START_DATE_NAME, startDate);
        //计算MD5
        strBuilder = strBuilder.deleteCharAt(strBuilder.length() - 1);
        //TODO 缺少MD5的代码
//        String hyren_md5_val = MD5Util.md5String(strBuilder.toString());
        String hyren_md5_val = strBuilder.toString();
        group.append(JobConstant.MD5_NAME, hyren_md5_val);
        group.append(JobConstant.MAX_DATE_NAME, JobConstant.MAX_DATE);
        //TODO 缺少写多个文件的代码
        writer.write(group);
    }

    private void stopStream() throws IOException {
        writer.close();
    }

    @Override
    protected void flushFile() throws IOException {
        stopStream();
        LOGGER.info("-----------Parquet文件处理完成-----------");
    }
}
