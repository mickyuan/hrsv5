package hrds.agent.job.biz.utils;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import hrds.agent.job.biz.bean.CollectTableColumnBean;
import hrds.agent.job.biz.constant.DataTypeConstant;
import hrds.commons.codes.IsFlag;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.Constant;
import org.apache.hadoop.hive.common.type.HiveDecimal;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.orc.TypeDescription;
import org.apache.parquet.example.data.Group;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@DocClass(desc = "用于对需要进行列合并(表清洗)、列拆分(列清洗)清洗的列，获取采集列列名，更新列信息的工具类",
		author = "WangZhengcheng")
public class ColumnTool {

	@Method(desc = "根据JobInfo中的列清洗信息返回需要采集的列名", logicStep = "" +
			"1、验证入参是否为空，如果为空，则抛出异常" +
			"2、遍历list集合，获取每一个ColumnCleanResult对象的columnName" +
			"3、将得到的columnName放入Set集合并返回")
	@Param(name = "list", desc = "装有所有采集列的列清洗规则的List集合", range = "不为空")
	@Return(desc = "装有所有采集列列名的Set集合", range = "不为空")
	public static Set<String> getCollectColumnName(List<CollectTableColumnBean> columnList) {
		//1、验证入参是否为空，如果为空，则抛出异常
		if (columnList == null || columnList.isEmpty()) {
			throw new AppSystemException("采集作业信息不能为空");
		}
		//2、遍历list集合，获取每一个ColumnCleanResult对象的columnName
		Set<String> columnNames = new HashSet<>();
		for (CollectTableColumnBean column : columnList) {
			//不是新列则放到set
			if (IsFlag.Fou.getCode().equals(column.getIs_new())) {
				String columnName = column.getColumn_name();
				columnNames.add(columnName);
			}
		}
		//3、将得到的columnName放入Set集合并返回
		return columnNames;
	}

	@Method(desc = "在原列名信息中，按照某分隔符，找到某列所在位置并返回", logicStep = "")
	@Param(name = "columnsName", desc = "原列名", range = "不为空")
	@Param(name = "colName", desc = "要寻找的列名", range = "不为空")
	@Param(name = "separator", desc = "分隔符", range = "不为空")
	@Return(desc = "查找到的列的位置", range = "不会为null")
	public static int findColIndex(String columnsName, String colName, String separator) {
		List<String> column = StringUtil.split(columnsName, separator);
		int index = 0;
		for (int j = 0; j < column.size(); j++) {
			if (column.get(j).equalsIgnoreCase(colName)) {
				index = j + 1;
				break;
			}
		}
		return index;
	}

	@Method(desc = "用于获取对应列类型的位置", logicStep = "")
	@Param(name = "columnsTypeAndPreci", desc = "列类型", range = "不为空,格式为列类型(长度,精度)")
	@Param(name = "index", desc = "该列在原字符串中出现的位置", range = "不为空")
	@Param(name = "separator", desc = "分隔符", range = "不为空")
	@Return(desc = "对应列类型的位置", range = "不会为null")
	public static int searchIndex(String columnsTypeAndPreci, int index, String separator) {
		int temp = 0;//第一个出现的索引位置
		int num = 0;
		while (temp != -1) {
			num++;
			temp = columnsTypeAndPreci.indexOf(separator, temp + 1);//从这个索引往后开始第一个出现的位置
			if (num == index) {
				break;
			}
		}
		return temp;
	}

//	/**
//	 * 写表的字段信息及生成文件信息
//	 */
//	public static void writeFileMeta(String tableName, File file, String columns, long liner, String list, String lengths, long meta_filesize, String mr) {
//
//		BufferedWriter bw = null;
////		String metaFile = file.getAbsolutePath() + "tableData.meta";
////		metaFile = FilenameUtils.normalize(metaFile);
//		try {
//			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false),
//					StandardCharset.UTF_8));
//			JSONObject jsonSon = new JSONObject();
//			jsonSon.put("tableName", tableName);
//			jsonSon.put("column", columns.toUpperCase());
//			jsonSon.put("length", lengths);
//			jsonSon.put("records", String.valueOf(liner));
//			jsonSon.put("fileSize", meta_filesize);
//			jsonSon.put("type", list);
//			jsonSon.put("mr", mr);
//			bw.write(jsonSon + "\n");
//			bw.flush();
//
//		} catch (Exception e) {
//			throw new AppSystemException("写表的字段信息及生成文件信息失败", e);
//		} finally {
//			try {
//				if (bw != null)
//					bw.close();
//			} catch (IOException e) {
//				logger.error(e.getMessage(), e);
//			}
//		}
//	}

	public static StructObjectInspector schemaInfo(String columns, String types) {

		List<ObjectInspector> listType = new ArrayList<>();
		List<String> listColumn = new ArrayList<>();
		List<String> columnAll = StringUtil.split(columns, Constant.METAINFOSPLIT);
		List<String> split = StringUtil.split(types, Constant.METAINFOSPLIT);//字段类型
		for (int i = 0; i < columnAll.size(); i++) {
			//TODO 类型转换这个类是否需要修改
			String columns_type = split.get(i).toLowerCase();
			if (columns_type.contains(DataTypeConstant.BOOLEAN.getMessage())) {
				listType.add(PrimitiveObjectInspectorFactory.javaBooleanObjectInspector);//orc文件字段类型
			} else if (columns_type.contains(DataTypeConstant.INT8.getMessage())
					|| columns_type.equals(DataTypeConstant.BIGINT.getMessage())
					|| columns_type.equals(DataTypeConstant.LONG.getMessage())) {
				listType.add(PrimitiveObjectInspectorFactory.javaLongObjectInspector);//orc文件字段类型
			} else if (columns_type.contains(DataTypeConstant.INT.getMessage())) {
				listType.add(PrimitiveObjectInspectorFactory.javaIntObjectInspector);//orc文件字段类型
			} else if (columns_type.contains(DataTypeConstant.CHAR.getMessage())) {
				listType.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);//orc文件字段类型
			} else if (columns_type.contains(DataTypeConstant.DECIMAL.getMessage())
					|| columns_type.contains(DataTypeConstant.NUMERIC.getMessage())) {
				listType.add(PrimitiveObjectInspectorFactory.javaHiveDecimalObjectInspector);//orc文件字段类型
			} else if (columns_type.contains(DataTypeConstant.FLOAT.getMessage())) {
				listType.add(PrimitiveObjectInspectorFactory.javaFloatObjectInspector);//orc文件字段类型
			} else if (columns_type.contains(DataTypeConstant.DOUBLE.getMessage())) {
				listType.add(PrimitiveObjectInspectorFactory.javaDoubleObjectInspector);//orc文件字段类型
			} else {
				listType.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);//orc文件字段类型
			}
			listColumn.add(columnAll.get(i));//列名称

		}
		//Orc文件的列信息
		return ObjectInspectorFactory.getStandardStructObjectInspector(listColumn, listType);
	}

	public static TypeDescription getTypeDescription(String columns, String types) {
		TypeDescription readSchema = TypeDescription.createStruct();
		List<String> columnAll = StringUtil.split(columns, Constant.METAINFOSPLIT);
		List<String> typeList = StringUtil.split(types, Constant.METAINFOSPLIT);//字段类型
		for (int i = 0; i < columnAll.size(); i++) {
			//TODO 类型转换这个类是否需要修改
//			String columns_type = DataTypeTransformHive.tansform(split.get(i).toUpperCase());
			String columns_type = typeList.get(i).toLowerCase();
			if (columns_type.contains(DataTypeConstant.BOOLEAN.getMessage())) {
				readSchema.addField(columnAll.get(i), TypeDescription.createBoolean());
			} else if (columns_type.contains(DataTypeConstant.INT8.getMessage())
					|| columns_type.equals(DataTypeConstant.BIGINT.getMessage())
					|| columns_type.equals(DataTypeConstant.LONG.getMessage())) {
				readSchema.addField(columnAll.get(i), TypeDescription.createLong());
			} else if (columns_type.contains(DataTypeConstant.INT.getMessage())) {
				readSchema.addField(columnAll.get(i), TypeDescription.createInt());
			} else if (columns_type.contains(DataTypeConstant.CHAR.getMessage())) {
				readSchema.addField(columnAll.get(i), TypeDescription.createChar());
			} else if (columns_type.contains(DataTypeConstant.DECIMAL.getMessage())
					|| columns_type.contains(DataTypeConstant.NUMERIC.getMessage())) {
				readSchema.addField(columnAll.get(i), TypeDescription.createDecimal());
			} else if (columns_type.contains(DataTypeConstant.FLOAT.getMessage())) {
				readSchema.addField(columnAll.get(i), TypeDescription.createFloat());
			} else if (columns_type.contains(DataTypeConstant.DOUBLE.getMessage())) {
				readSchema.addField(columnAll.get(i), TypeDescription.createDouble());
			} else if (columns_type.contains(DataTypeConstant.BYTE.getMessage())) {
				readSchema.addField(columnAll.get(i), TypeDescription.createByte());
			} else if (columns_type.contains(DataTypeConstant.TIMESTAMP.getMessage())) {
				readSchema.addField(columnAll.get(i), TypeDescription.createTimestamp());
			} else if (columns_type.contains(DataTypeConstant.DATE.getMessage())) {
				readSchema.addField(columnAll.get(i), TypeDescription.createDate());
			} else {
				readSchema.addField(columnAll.get(i), TypeDescription.createString());
			}
		}
		//Orc文件的列信息
		return readSchema;
	}

	/**
	 * @param group      parquet文件group
	 * @param columnType 字段类型
	 * @param columname  字段名称
	 * @param data       数据
	 */
	public static void addData2Group(Group group, String columnType, String columname, String data) {
		//TODO 字段类型转换，这里要设计表
		columnType = columnType.toLowerCase();
		if (columnType.contains(DataTypeConstant.BOOLEAN.getMessage())) {
			boolean dataResult = StringUtil.isEmpty(data) ? null : Boolean.valueOf(data.trim());
			group.add(columname, dataResult);
		} else if (columnType.contains(DataTypeConstant.INT8.getMessage())
				|| columnType.equals(DataTypeConstant.BIGINT.getMessage())
				|| columnType.equals(DataTypeConstant.LONG.getMessage())) {
			long dataResult = StringUtil.isEmpty(data) ? null : Long.valueOf(data.trim());
			group.add(columname, dataResult);
		} else if (columnType.contains(DataTypeConstant.INT.getMessage())) {
			int dataResult = StringUtil.isEmpty(data) ? null : Integer.valueOf(data.trim());
			group.add(columname, dataResult);
		} else if (columnType.contains(DataTypeConstant.FLOAT.getMessage())) {
			float dataResult = StringUtil.isEmpty(data) ? null : Float.valueOf(data.trim());
			group.add(columname, dataResult);
		} else if (columnType.contains(DataTypeConstant.DOUBLE.getMessage())
				|| columnType.contains(DataTypeConstant.DECIMAL.getMessage())
				|| columnType.contains(DataTypeConstant.NUMERIC.getMessage())) {
			double dataResult = StringUtil.isEmpty(data) ? null : Double.valueOf(data.trim());
			group.add(columname, dataResult);
		} else {
			//char与varchar都为string
			data = StringUtil.isEmpty(data) ? "" : data;
			group.add(columname, data);
		}
	}

	public static void addData2Inspector(List<Object> lineData, String columnType, String data) {
		//TODO 字段类型转换，这里要设计表
//		columnType = DataTypeTransformHive.tansform(columnType.toUpperCase());
		columnType = columnType.toLowerCase();
		if (columnType.contains(DataTypeConstant.BOOLEAN.getMessage())) {
			boolean dataResult = Boolean.valueOf(data.trim());
			lineData.add(dataResult);
		} else if (columnType.contains(DataTypeConstant.INT8.getMessage())
				|| columnType.equals(DataTypeConstant.BIGINT.getMessage())
				|| columnType.equals(DataTypeConstant.LONG.getMessage())) {
			long dataResult = StringUtil.isEmpty(data) ? 0L : Long.valueOf(data.trim());
			lineData.add(dataResult);
		} else if (columnType.contains(DataTypeConstant.INT.getMessage())) {
			int dataResult = StringUtil.isEmpty(data) ? 0 : Integer.valueOf(data.trim());
			lineData.add(dataResult);
		} else if (columnType.contains(DataTypeConstant.FLOAT.getMessage())) {
			float dataResult = StringUtil.isEmpty(data) ? 0 : Float.valueOf(data.trim());
			lineData.add(dataResult);
		} else if (columnType.contains(DataTypeConstant.DOUBLE.getMessage())) {
			double dataResult = StringUtil.isEmpty(data) ? 0 : Double.valueOf(data.trim());
			lineData.add(dataResult);
		} else if (columnType.contains(DataTypeConstant.DECIMAL.getMessage())
				|| columnType.contains(DataTypeConstant.NUMERIC.getMessage())) {
			BigDecimal dataResult = StringUtil.isEmpty(data) ? new BigDecimal("0") : new BigDecimal(data.trim());
			HiveDecimal create = HiveDecimal.create(dataResult);
			lineData.add(create);
		} else {
			//char与varchar都为string
			data = StringUtil.isEmpty(data) ? "" : data;
			lineData.add(data);
		}
	}
}
