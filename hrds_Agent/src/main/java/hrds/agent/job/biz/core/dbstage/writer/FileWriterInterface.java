package hrds.agent.job.biz.core.dbstage.writer;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;

@DocClass(desc = "数据库直连采集以指定的格式将数据卸到指定的数据文件的接口", author = "WangZhengcheng")
public interface FileWriterInterface {

	@Method(desc = "根据数据元信息和ResultSet，写指定格式的数据文件", logicStep = "")
	@Param(name = "metaDataMap", desc = "包含有列元信息，清洗规则的map", range = "不为空，共有7对Entry，key分别为" +
			"1、columnsTypeAndPreci：表示列数据类型(长度/精度)" +
			"2、columnsLength : 列长度，在生成信号文件的时候需要使用" +
			"3、columns : 列名" +
			"4、colTypeArr : 列数据类型(java.sql.Types),用于判断，对不同数据类型做不同处理" +
			"5、columnCount ：该表的列的数目" +
			"6、columnCleanRule ：该表每列的清洗规则" +
			"7、tableCleanRule ：整表清洗规则")
	@Return(desc = "生成的数据文件的路径", range = "不会为null")
	String writeFiles();

//	@Method(desc = "将LONGVARCHAR和CLOB类型转换为字节数组，用于写Avro", logicStep = "")
//	@Param(name = "characterStream", desc = "java.io.Reader形式得到此ResultSet结果集中当前行中指定列的值"
//			, range = "不为空")
//	@Return(desc = "此ResultSet结果集中当前行中指定列的值转换得到的字节数组", range = "不会为null")
//	byte[] longVarcharToByte(Reader characterStream);
//
//	@Method(desc = "把Blob类型转换为byte字节数组, 用于写Avro", logicStep = "")
//	@Param(name = "blob", desc = "采集得到的Blob类型的列的值", range = "不为空")
//	@Return(desc = "采集得到的Blob类型的列的值转换得到的字节数组", range = "不会为null")
//	byte[] blobToBytes(Blob blob);
}
