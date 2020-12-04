package hrds.commons.hadoop.hive.serde;


import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.common.type.HiveChar;
import org.apache.hadoop.hive.common.type.HiveDecimal;
import org.apache.hadoop.hive.common.type.HiveVarchar;
import org.apache.hadoop.hive.serde2.AbstractSerDe;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.SerDeSpec;
import org.apache.hadoop.hive.serde2.SerDeStats;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.AbstractPrimitiveJavaObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.typeinfo.*;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@SerDeSpec(
		schemaProps = {"columns", "columns.types", "input.regex", "input.regex.case.insensitive",
				"hyren.columns.lengths", "serialization.encoding", "field.delim"}
)
public class HyrenSerDe extends AbstractSerDe {
	public static final Log LOG = LogFactory.getLog(HyrenSerDe.class.getName());
	int numColumns;
	StructObjectInspector rowOI;
	List<Object> row;
	List<TypeInfo> columnTypes;
	List<Integer> columnLengths = new ArrayList<>();
	Object[] outputFields;
	Text outputRowText;
	boolean alreadyLoggedPartialMatch = false;
	long partialMatchedRowsCount = 0L;
	protected String charsetName;
	private int field_delim_length = 0;

	public void initialize(Configuration conf, Properties tbl) throws SerDeException {
		String columnNameProperty = tbl.getProperty("columns");
		String columnTypeProperty = tbl.getProperty("columns.types");
		String columnLengthProperty = tbl.getProperty("hyren.columns.lengths");
		this.charsetName = tbl.getProperty("serialization.encoding", "").trim();
		if (StringUtils.isEmpty(charsetName)) {
			throw new SerDeException("serialization.encoding need to set");
		}
		String field_delim = tbl.getProperty("field.delim");
		if (!StringUtils.isEmpty(field_delim)) {
			try {
				field_delim_length = field_delim.getBytes(charsetName).length;
			} catch (UnsupportedEncodingException e) {
				throw new SerDeException("serialization.encoding incorrect ");
			}
		}
		if (null != tbl.getProperty("output.format.string")) {
			LOG.warn("output.format.string has been deprecated");
		}
		List<String> columnNames = Arrays.asList(columnNameProperty.split(","));
		this.columnTypes = TypeInfoUtils.getTypeInfosFromTypeString(columnTypeProperty);
		String[] columnLengthArr = columnLengthProperty.split(",");
		assert columnNames.size() == this.columnTypes.size();
		assert columnNames.size() == columnLengthArr.length;
		for (String len : columnLengthArr) {
			columnLengths.add(Integer.parseInt(len));
		}
		this.numColumns = columnNames.size();
		List<ObjectInspector> columnOIs = new ArrayList<>(columnNames.size());

		int c;
		for (c = 0; c < this.numColumns; ++c) {
			TypeInfo typeInfo = this.columnTypes.get(c);
			if (!(typeInfo instanceof PrimitiveTypeInfo)) {
				throw new SerDeException(this.getClass().getName() + " doesn't allow column [" + c + "] named " + columnNames.get(c) + " with type " + this.columnTypes.get(c));
			}

			PrimitiveTypeInfo pti = (PrimitiveTypeInfo) this.columnTypes.get(c);
			AbstractPrimitiveJavaObjectInspector oi = PrimitiveObjectInspectorFactory.getPrimitiveJavaObjectInspector(pti);
			columnOIs.add(oi);
		}

		this.rowOI = ObjectInspectorFactory.getStandardStructObjectInspector(columnNames, columnOIs, Lists.newArrayList(Splitter.on('\u0000').split(tbl.getProperty("columns.comments"))));
		this.row = new ArrayList<>(this.numColumns);

		for (c = 0; c < this.numColumns; ++c) {
			this.row.add(null);
		}

		this.outputFields = new Object[this.numColumns];
		this.outputRowText = new Text();
	}

	public ObjectInspector getObjectInspector() {
		return this.rowOI;
	}

	public Class<? extends Writable> getSerializedClass() {
		return Text.class;
	}

	public Object deserialize(Writable blob) throws SerDeException {
		String rowText = transformTextToYTF8((Text) blob, charsetName);
		if (StringUtils.isEmpty(rowText)) {
			return null;
		}
		List<String> dingChangValueList = getDingChangValueList(rowText,
				columnLengths, this.charsetName, this.field_delim_length);
		for (int c = 0; c < this.numColumns; ++c) {
			try {
				String t = dingChangValueList.get(c).trim();
				TypeInfo typeInfo = this.columnTypes.get(c);
				PrimitiveTypeInfo pti = (PrimitiveTypeInfo) typeInfo;
				switch (pti.getPrimitiveCategory()) {
					case STRING:
						this.row.set(c, t);
						break;
					case BYTE:
						Byte b = Byte.valueOf(t);
						this.row.set(c, b);
						break;
					case SHORT:
						Short s = Short.valueOf(t);
						this.row.set(c, s);
						break;
					case INT:
						Integer i = Integer.valueOf(t);
						this.row.set(c, i);
						break;
					case LONG:
						Long l = Long.valueOf(t);
						this.row.set(c, l);
						break;
					case FLOAT:
						Float f = Float.valueOf(t);
						this.row.set(c, f);
						break;
					case DOUBLE:
						Double d = Double.valueOf(t);
						this.row.set(c, d);
						break;
					case BOOLEAN:
						Boolean bool = Boolean.valueOf(t);
						this.row.set(c, bool);
						break;
					case TIMESTAMP:
						Timestamp ts = Timestamp.valueOf(t);
						this.row.set(c, ts);
						break;
					case DATE:
						Date date = Date.valueOf(t);
						this.row.set(c, date);
						break;
					case DECIMAL:
						HiveDecimal bd = HiveDecimal.create(t);
						this.row.set(c, bd);
						break;
					case CHAR:
						HiveChar hc = new HiveChar(t, ((CharTypeInfo) typeInfo).getLength());
						this.row.set(c, hc);
						break;
					case VARCHAR:
						HiveVarchar hv = new HiveVarchar(t, ((VarcharTypeInfo) typeInfo).getLength());
						this.row.set(c, hv);
						break;
					default:
						throw new SerDeException("Unsupported type " + typeInfo);
				}
			} catch (RuntimeException var20) {
				++this.partialMatchedRowsCount;
				if (!this.alreadyLoggedPartialMatch) {
					LOG.warn("" + this.partialMatchedRowsCount + " partially unmatched rows are found, " + " cannot find group " + c + ": " + rowText);
					this.alreadyLoggedPartialMatch = true;
				}

				this.row.set(c, null);
			}
		}
		return this.row;

	}

	public Writable serialize(Object obj, ObjectInspector objInspector) {
		throw new UnsupportedOperationException("Regex SerDe doesn't support the serialize() method");
	}

	public SerDeStats getSerDeStats() {
		return null;
	}

	/**
	 * 获取定长的文件，解析，转为list，这里定长是针对没有分割符的定长文件的
	 */
	private List<String> getDingChangValueList(String line, List<Integer> lengthList, String charsetName,
											   int field_delim) {
		List<String> valueList = new ArrayList<>();
		try {
			byte[] bytes = line.getBytes(charsetName);
			int begin = 0;
			for (int length : lengthList) {
				byte[] byteTmp = new byte[length];
				System.arraycopy(bytes, begin, byteTmp, 0, length);
				begin += (length + field_delim);
				valueList.add(new String(byteTmp, charsetName));
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return valueList;
	}

	/**
	 * 根据文件编码解析字符串，防止乱码
	 */
	private String transformTextToYTF8(Text text, String encoding) {
		String value = "";
		try {
			value = new String(text.getBytes(), 0, text.getLength(), encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return value;
	}

	public static void main(String[] args) throws Exception {
		List<String> strings = Files.readAllLines(Paths.get("D:\\data\\20201202\\agent_info" +
				"\\FIXEDFILE\\ZXZ_ZX09_agent_info00.dat"), Charset.forName("GBK"));
		for (String aaa : strings) {
			String[] columnLengthArr = "1,50,19,512,6,19,1,8,10,8,4".split(",");
			List<Integer> columnLengths = new ArrayList<>();
			for (String len : columnLengthArr) {
				columnLengths.add(Integer.parseInt(len));
			}
			List<String> gbk = new HyrenSerDe().getDingChangValueList(aaa, columnLengths, "GBK", 0);
			gbk.forEach(System.out::println);
			System.out.println("====================================");
		}

	}
}

