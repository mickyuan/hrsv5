package hrds.agent.job.biz.utils;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.StringUtil;
import hrds.agent.job.biz.constant.DataTypeConstant;
import hrds.agent.job.biz.constant.JobConstant;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.column.ParquetProperties;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.ParquetFileWriter;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.example.GroupWriteSupport;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.MessageTypeParser;

import java.io.File;
import java.io.IOException;
import java.util.List;

@DocClass(desc = "构建parquet对象", author = "WangZhengcheng")
public class ParquetUtil {

	/**
	 * 构建schema
	 *
	 * @param columns 字段名，","分割
	 * @param types   字段类型，"|"分隔
	 * @return org.apache.parquet.schema.MessageType
	 * @author 13616
	 * @date 2019/8/7 11:29
	 */
	public static MessageType getSchema(String columns, String types) {
		//列数组
		String[] colArray = columns.toUpperCase().split(JobConstant.COLUMN_SEPARATOR);
		//字段类型
		List<String> typeArray = StringUtil.split(types.toLowerCase(),
				JobConstant.COLUMN_TYPE_SEPARATOR);
		StringBuilder sb = new StringBuilder(170);
		sb.append("message Pair {\n");

		for (int i = 0; i < colArray.length; i++) {
			String columns_type = typeArray.get(i);
			if (columns_type.contains(DataTypeConstant.BOOLEAN.getMessage())) {
				sb.append("required ").append("BOOLEAN ").append(colArray[i]).append(" ;");
			} else if (columns_type.contains(DataTypeConstant.INT.getMessage())) {
				sb.append("required ").append("INT32 ").append(colArray[i]).append(" ;");
			} else if (columns_type.contains(DataTypeConstant.DECIMAL.getMessage())) {
				sb.append("required ").append("DOUBLE ").append(colArray[i]).append(" ;");
			} else if (columns_type.contains(DataTypeConstant.FLOAT.getMessage())) {
				sb.append("required ").append("FLOAT ").append(colArray[i]).append(" ;");
			} else if (columns_type.contains(DataTypeConstant.DOUBLE.getMessage())) {
				sb.append("required ").append("DOUBLE ").append(colArray[i]).append(" ;");
			} else {
				sb.append("required binary ").append(colArray[i]).append(" (UTF8);");
			}
		}
		sb.append(" }");

		return MessageTypeParser.parseMessageType(sb.toString());
	}

	/*用于数据库直连采集根据列名和列类型生成Schema,数据库直连采集列名是根据\001分隔的*/
	public static MessageType getSchemaAsDBColl(String columns, String types) {
		//列数组
		String[] colArray = columns.toUpperCase().split(JobConstant.COLUMN_NAME_SEPARATOR);
		//字段类型
		List<String> typeArray = StringUtil.split(types.toLowerCase(),
				JobConstant.COLUMN_TYPE_SEPARATOR);
		StringBuilder sb = new StringBuilder(170);
		sb.append("message Pair {\n");

		for (int i = 0; i < colArray.length; i++) {
			String columns_type = typeArray.get(i);
			if (columns_type.contains(DataTypeConstant.BOOLEAN.getMessage())) {
				sb.append("required ").append("BOOLEAN ").append(colArray[i]).append(" ;");
			} else if (columns_type.contains(DataTypeConstant.INT.getMessage())) {
				sb.append("required ").append("INT32 ").append(colArray[i]).append(" ;");
			} else if (columns_type.contains(DataTypeConstant.DECIMAL.getMessage())) {
				sb.append("required ").append("DOUBLE ").append(colArray[i]).append(" ;");
			} else if (columns_type.contains(DataTypeConstant.FLOAT.getMessage())) {
				sb.append("required ").append("FLOAT ").append(colArray[i]).append(" ;");
			} else if (columns_type.contains(DataTypeConstant.DOUBLE.getMessage())) {
				sb.append("required ").append("DOUBLE ").append(colArray[i]).append(" ;");
			} else {
				sb.append("required binary ").append(colArray[i]).append(" (UTF8);");
			}
		}
		sb.append(" }");

		return MessageTypeParser.parseMessageType(sb.toString());
	}

	/**
	 * 对数据进行格式化，存入Group中
	 *
	 * @param group      Group对象
	 * @param columname  字段名称
	 * @param columnType 字段类型
	 * @param data       数据
	 * @author 13616
	 * @date 2019/8/7 11:31
	 */
	public static void addData2Group(Group group, String columname, String columnType, String data) {

		if (columnType.indexOf(DataTypeConstant.BOOLEAN.getMessage()) > -1) {
			group.add(columname, Boolean.valueOf(data));
		} else if (columnType.indexOf(DataTypeConstant.INT.getMessage()) > -1) {
			group.add(columname, null == data ? 0 : Integer.valueOf(data));
		} else if (columnType.indexOf(DataTypeConstant.FLOAT.getMessage()) > -1) {
			group.add(columname, null == data ? 0 : Float.valueOf(data));
		} else if (columnType.indexOf(DataTypeConstant.DOUBLE.getMessage()) > -1 ||
				columnType.indexOf(DataTypeConstant.DECIMAL.getMessage()) > -1) {
			group.add(columname, null == data ? 0 : Double.valueOf(data));
		} else {
			//char与varchar都为string
			group.add(columname, null == data ? "" : data);
		}
	}

	/**
	 * 返回parquet写文件流
	 *
	 * @param schema  MessageType对象
	 * @param Path    本地或hdfs文件地址
	 * @param conf    Configuration对象
	 * @param isLocal 是否为本地文件流
	 * @throws IOException 无法对指定文件写入数据时抛出该异常
	 * @return ParquetWriter<Group>
	 */
	public static ParquetWriter<Group> getParquetWriter(MessageType schema, String Path,
	                                                    Configuration conf,
	                                                    boolean isLocal) throws IOException {

		org.apache.hadoop.fs.Path path = new Path(Path);
		return getParquetWriter(schema, path, conf, isLocal);

	}

	/**
	 * 返回parquet写文件流
	 *
	 * @param schema
	 * @param Path
	 * @return ParquetWriter<Group>
	 * @throws IOException 无法对指定文件写入数据时抛出该异常
	 */
	public static ParquetWriter<Group> getParquetWriter(MessageType schema, String Path)
			throws IOException {

		Configuration conf = new Configuration();
		return getParquetWriter(schema, Path, conf, true);

	}

	/**
	 * 返回parquet写文件流
	 *
	 * @param schema
	 * @param path
	 * @param conf
	 * @param isLocal 是否为本地文件流
	 * @throws IOException 无法对指定文件写入数据时抛出该异常
	 * @return ParquetWriter<Group>
	 */
	public static ParquetWriter<Group> getParquetWriter(MessageType schema, Path path,
	                                                    Configuration conf, boolean isLocal)
			throws IOException {

		if (isLocal) {
			FileUtils.deleteQuietly(new File(path.toString()));
			conf.set("fs.defaultFS", "file:///");
		}
		GroupWriteSupport writeSupport = new GroupWriteSupport();
		GroupWriteSupport.setSchema(schema, conf);
		@SuppressWarnings("deprecation")
		ParquetWriter<Group> writer = new ParquetWriter<Group>(path, ParquetFileWriter.Mode.OVERWRITE,
				writeSupport, CompressionCodecName.SNAPPY,
				134217728, 1048576, 1048576, true,
				false, ParquetProperties.WriterVersion.PARQUET_1_0, conf);
		return writer;
	}
}
