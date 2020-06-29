package hrds.agent.job.biz.utils;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.StringUtil;
import hrds.agent.job.biz.constant.DataTypeConstant;
import hrds.commons.utils.Constant;
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
import org.apache.parquet.schema.PrimitiveType;
import org.apache.parquet.schema.Type;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@DocClass(desc = "构建parquet对象", author = "WangZhengcheng")
public class ParquetUtil {

	/**
	 * 用于数据库直连采集和DB文件采集根据列名和列类型生成Schema
	 */
	public static MessageType getSchema(String columns, String types) {
		//列数组
		List<String> colList = StringUtil.split(columns.toUpperCase(), Constant.METAINFOSPLIT);
		//字段类型
		List<String> typeArray = StringUtil.split(types.toLowerCase(), Constant.METAINFOSPLIT);
//		StringBuilder sb = new StringBuilder(170);
//		sb.append("message Pair {\n");
//		for (int i = 0; i < colList.size(); i++) {
//			String columns_type = typeArray.get(i);
//			if (columns_type.contains(DataTypeConstant.BOOLEAN.getMessage())) {
//				sb.append("required ").append("BOOLEAN ").append(colList.get(i)).append(" ;");
//			} else if (columns_type.contains(DataTypeConstant.INT8.getMessage())
//					|| columns_type.equals(DataTypeConstant.BIGINT.getMessage())
//					|| columns_type.equals(DataTypeConstant.LONG.getMessage())) {
//				sb.append("required ").append("INT64 ").append(colList.get(i)).append(" ;");
//			} else if (columns_type.contains(DataTypeConstant.INT.getMessage())) {
//				sb.append("required ").append("INT32 ").append(colList.get(i)).append(" ;");
//			} else if (columns_type.contains(DataTypeConstant.DECIMAL.getMessage())
//					|| columns_type.contains(DataTypeConstant.NUMERIC.getMessage())
//					|| columns_type.contains(DataTypeConstant.DOUBLE.getMessage())) {
//				sb.append("required ").append("DOUBLE ").append(colList.get(i)).append(" ;");
//			} else if (columns_type.contains(DataTypeConstant.FLOAT.getMessage())) {
//				sb.append("required ").append("FLOAT ").append(colList.get(i)).append(" ;");
//			} else {
//				sb.append("required binary ").append(colList.get(i)).append(" (UTF8);");
//			}
//		}
//		sb.append(" }");
//		return MessageTypeParser.parseMessageType(sb.toString());
		ArrayList<Type> fields = new ArrayList<>();
		for (int i = 0; i < colList.size(); i++) {
			String columns_type = typeArray.get(i);
			if (columns_type.contains(DataTypeConstant.BOOLEAN.getMessage())) {
				fields.add(new PrimitiveType(Type.Repetition.OPTIONAL, PrimitiveType.PrimitiveTypeName.BOOLEAN,
						colList.get(i), null));
			} else if (columns_type.contains(DataTypeConstant.INT8.getMessage())
					|| columns_type.equals(DataTypeConstant.BIGINT.getMessage())
					|| columns_type.equals(DataTypeConstant.LONG.getMessage())) {
				fields.add(new PrimitiveType(Type.Repetition.OPTIONAL, PrimitiveType.PrimitiveTypeName.INT64,
						colList.get(i), null));
			} else if (columns_type.contains(DataTypeConstant.INT.getMessage())) {
				fields.add(new PrimitiveType(Type.Repetition.OPTIONAL, PrimitiveType.PrimitiveTypeName.INT32,
						colList.get(i), null));
			} else if (columns_type.contains(DataTypeConstant.DECIMAL.getMessage())
					|| columns_type.contains(DataTypeConstant.NUMERIC.getMessage())
					|| columns_type.contains(DataTypeConstant.DOUBLE.getMessage())) {
				fields.add(new PrimitiveType(Type.Repetition.OPTIONAL, PrimitiveType.PrimitiveTypeName.DOUBLE,
						colList.get(i), null));
			} else if (columns_type.contains(DataTypeConstant.FLOAT.getMessage())) {
				fields.add(new PrimitiveType(Type.Repetition.OPTIONAL, PrimitiveType.PrimitiveTypeName.FLOAT,
						colList.get(i), null));
			} else {
				fields.add(new PrimitiveType(Type.Repetition.OPTIONAL, PrimitiveType.PrimitiveTypeName.BINARY,
						colList.get(i), null));
			}
		}
		return new MessageType("input", fields);
	}

	/**
	 * 返回parquet写文件流
	 *
	 * @param schema  MessageType对象
	 * @param Path    本地或hdfs文件地址
	 * @param conf    Configuration对象
	 * @param isLocal 是否为本地文件流
	 * @return ParquetWriter<Group>
	 * @throws IOException 无法对指定文件写入数据时抛出该异常
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
	 * @return ParquetWriter<Group>
	 * @throws IOException 无法对指定文件写入数据时抛出该异常
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
