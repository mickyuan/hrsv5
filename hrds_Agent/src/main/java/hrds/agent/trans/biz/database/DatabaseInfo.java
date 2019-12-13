package hrds.agent.trans.biz.database;

import com.alibaba.fastjson.JSON;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.agent.job.biz.bean.SourceDataConfBean;
import hrds.agent.trans.biz.ConnetionTool;
import hrds.commons.base.AgentBaseAction;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Database_set;
import hrds.commons.entity.Table_column;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.ConnUtil;
import hrds.commons.utils.PackUtil;
import hrds.commons.utils.Platform;
import hrds.commons.utils.xlstoxml.Xls2xml;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Agent获取远程数据库的表，表中的字段的接口
 * date: 2019/10/15 14:40
 * author: zxz
 */
public class DatabaseInfo extends AgentBaseAction {

	@Method(desc = "根据数据库连接获取数据库下表信息",
			logicStep = "1.根据Db_agent判断是否为平面DB数据采集" +
					"2.DB文件采集将数据字典dd_data.xls转为xml" +
					"3.读取xml获取数据字典下所有的表信息" +
					"4.数据库采集则获取jdbc连接，读取mate信息，将mate信息写成xml文件" +
					"5.读取xml，获取数据库下所有表的信息" +
					"6.返回所有表的信息")
	@Param(name = "database_set", desc = "数据库连接设置信息表", range = "表中不能为空的字段必须有值", isBean = true)
	@Param(name = "search", desc = "需要查询的表，模糊查询，多条使用|分割", range = "可以为空", nullable = true)
	@Return(desc = "数据库下表信息", range = "可能为空，极端情况下空的数据库返回值为空，如果长度超过300，则会进行压缩")
	public String getDatabaseTable(Database_set database_set, String search) {
		List<Object> table_List;
		//1.根据Db_agent判断是否为平面DB数据采集
		if (IsFlag.Shi.getCode().equals(database_set.getDb_agent())) {
			String db_path = database_set.getPlane_url();
			String xmlName = Math.abs(db_path.hashCode()) + ".xml";
			//2.DB文件采集将数据字典dd_data.xls转为xml
			Xls2xml.toXml(db_path, xmlName);
			//3.读取xml获取数据字典下所有的表信息
			table_List = ConnUtil.getTableToXML(xmlName);
		} else {
			String type = database_set.getDatabase_type();//数据库类型
			String database_name = database_set.getDatabase_name();//数据库名称
			String user_name = database_set.getUser_name();//用户名
			String xmlName = ConnUtil.getDataBaseFile(database_set.getDatabase_ip(), database_set.getDatabase_port(),
					database_name, user_name);
			Connection con = null;
			try {
				//4.数据库采集则获取jdbc连接，读取mate信息，将mate信息写成xml文件
				con = ConnUtil.getConnection(database_set.getDatabase_drive(), database_set.getJdbc_url(),
						user_name, database_set.getDatabase_pad());
				if (StringUtil.isEmpty(search)) {
					Platform.readModelFromDatabase(con, xmlName, type, database_name);
				} else {
					Platform.readModelFromDatabase(con, xmlName, type, database_name, search);
				}
				//5.读取xml，获取数据库下所有表的信息
				table_List = ConnUtil.getTable(xmlName);
			} catch (Exception e) {
				throw new BusinessException("获取数据库的表信息失败" + e.getMessage());
			} finally {
				ConnUtil.close(con);
			}
		}
		//6.返回所有表的信息
		return PackUtil.packMsg(JSON.toJSONString(table_List));
	}

	@Method(desc = "根据数据库连接和表名获取表的字段信息",
			logicStep = "1.根据Db_agent判断是否为平面DB数据采集" +
					"2.去读xml下指定表的字段信息" +
					"3.报错了，说明解析xml文件失败，可能是文件被修改或者被删了，重新生成一次xml文件再读取" +
					"4.数据库采集，判断表是否是指定sql查询需要的列，是则根据sql取字段信息" +
					"5.去读xml下指定表的字段信息" +
					"6.返回指定表的字段信息")
	@Param(name = "database_set", desc = "数据库连接设置信息表对象", range = "表中不能为空的字段必须有值"
			, isBean = true)
	@Param(name = "tableName", desc = "需要查询字段的表", range = "可以为空", nullable = true)
	@Param(name = "hy_sql_meta", desc = "表指定sql查询需要的列", range = "可以为空", nullable = true)
	@Return(desc = "数据库下表的字段信息", range = "不会为空，如果字符串的长度超过300，将会被压缩")
	public String getTableColumn(Database_set database_set, String tableName, String hy_sql_meta) {

		List<Map<String, String>> columnList;
		//1.根据Db_agent判断是否为平面DB数据采集
		if (IsFlag.Shi.getCode().equals(database_set.getDb_agent())) {
			String db_path = database_set.getPlane_url();
			String xmlName = ConnUtil.getDataBaseFile("", "", db_path, "");
			try {
				//2.去读xml下指定表的字段信息
				columnList = ConnUtil.getColumnByTable(xmlName, tableName);
			} catch (Exception e) {
				//3.报错了，说明解析xml文件失败，可能是文件被修改或者被删了，重新生成一次xml文件再读取
				Xls2xml.toXml(db_path, xmlName);
				columnList = ConnUtil.getColumnByTable(xmlName, tableName);
			}
		} else {
			String type = database_set.getDatabase_type();//数据库类型
			String database_ip = database_set.getDatabase_ip();//数据库IP
			String database_name = database_set.getDatabase_name();//数据库名称
			String database_pad = database_set.getDatabase_pad();//数据库密码
			String database_port = database_set.getDatabase_port();//数据库端口
			String user_name = database_set.getUser_name();//用户名
			String database_drive = database_set.getDatabase_drive();//数据库驱动
			String jdbc_url = database_set.getJdbc_url();//数据库连接的url

			String xmlName = ConnUtil.getDataBaseFile(database_ip, database_port, database_name, user_name);
			Connection con = null;
			try {
				con = ConnUtil.getConnection(database_drive, jdbc_url, user_name, database_pad);
				//4.数据库采集，判断表是否是指定sql查询需要的列，是则根据sql取字段信息
				if (!StringUtil.isEmpty(hy_sql_meta)) {
					//根据指定sql取需要查询的表的字段信息
					columnList = Platform.getSqlInfo(hy_sql_meta, con);
				} else {
					try {
						//5.去读xml下指定表的字段信息
						columnList = ConnUtil.getColumnByTable(xmlName, tableName);
					} catch (Exception e) {
						//报错了，说明解析xml文件失败，可能是文件被修改或者被删了，重新生成一次
						Platform.readModelFromDatabase(con, xmlName, type, database_name, tableName);
						columnList = ConnUtil.getColumnByTable(xmlName, tableName);
					}
				}
			} catch (Exception e) {
				throw new BusinessException("获取");
			} finally {
				ConnUtil.close(con);
			}
		}
		//6.返回指定表的字段信息
		return PackUtil.packMsg(JSON.toJSONString(columnList));
	}

	@Method(desc = "根据数据库连接和自定义抽取SQL获取表的字段信息",
			logicStep = "1.使用dbinfo将需要测试并行抽取的数据库连接内容填充" +
					"2.执行自定义抽取SQL，获取执行结果集" +
					"3.根据结果集拿到该结果集的meta信息" +
					"4.将每一个采集列加入到List集合中" +
					"5.失败抛出异常给应用管理端" +
					"6.将集合转为json字符串并使用工具类进行压缩，返回给应用管理端")
	@Param(name = "dbSet", desc = "数据库连接设置信息表对象", range = "表中不能为空的字段必须有值", isBean = true)
	@Param(name = "custSQL", desc = "自定义抽取SQL", range = "不为空")
	@Return(desc = "自定义SQL抽取的字段信息", range = "不会为空，如果字符串的长度超过300，将会被压缩")
	public String getCustColumn(Database_set dbSet, String custSQL){
		//1、使用dbinfo将需要测试并行抽取的数据库连接内容填充
		SourceDataConfBean dbInfo = new SourceDataConfBean();
		dbInfo.setDatabase_drive(dbSet.getDatabase_drive());
		dbInfo.setJdbc_url(dbSet.getJdbc_url());
		dbInfo.setUser_name(dbSet.getUser_name());
		dbInfo.setDatabase_pad(dbSet.getDatabase_pad());
		dbInfo.setDatabase_type(dbSet.getDatabase_type());
		try (DatabaseWrapper db = ConnetionTool.getDBWrapper(dbInfo)) {
			//2、执行自定义抽取SQL，获取执行结果集
			ResultSet rs = db.queryGetResultSet(custSQL);
			List<Table_column> tableColumns = new ArrayList<>();
			try {
				//3、根据结果集拿到该结果集的meta信息
				ResultSetMetaData metaData = rs.getMetaData();
				for(int j = 0; j < metaData.getColumnCount(); j++){
					Table_column tableColumn = new Table_column();
					tableColumn.setColume_name(metaData.getColumnName(j + 1));
					//对列类型做特殊处理，处理成varchar(512), numeric(10,4)
					String colTypeAndPreci = getColTypeAndPreci(metaData.getColumnType(j + 1),
							metaData.getColumnTypeName(j + 1), metaData.getPrecision(j + 1),
							metaData.getScale(j + 1));
					tableColumn.setColumn_type(colTypeAndPreci);
					//列中文名默认设置为列英文名
					tableColumn.setColume_ch_name(metaData.getColumnName(j + 1));

					//4、将每一个采集列加入到List集合中
					tableColumns.add(tableColumn);
				}
			} catch (SQLException e) {
				//5.失败抛出异常给应用管理端
				throw new BusinessException("获取自定义SQL抽取列信息失败" + e.getMessage());
			}
			//6.将集合转为json字符串并使用工具类进行压缩，返回给应用管理端
			return PackUtil.packMsg(JSON.toJSONString(tableColumns));
		}
	}

	@Method(desc = "获取列数据类型和长度/精度", logicStep = "" +
			"1、考虑到有些类型在数据库中在获取数据类型的时候就会带有(),同时还能获取到数据的长度/精度，" +
			"因此我们要对所有数据库进行统一处理，去掉()中的内容，使用JDBC提供的方法读取的长度和精度进行拼接" +
			"2、对不包含长度和精度的数据类型进行处理，返回数据类型" +
			"3、对包含长度和精度的数据类型进行处理，返回数据类型(长度,精度)" +
			"4、对只包含长度的数据类型进行处理，返回数据类型(长度)")
	@Param(name = "columnType", desc = "数据库类型", range = "不为null,java.sql.Types对象实例")
	@Param(name = "columnTypeName", desc = "字符串形式的数据库类型，通过调用ResultSetMetaData.getColumnTypeName()得到"
			, range = "不为null")
	@Param(name = "precision", desc = "对于数字类型，precision表示的是数字的精度，对于字符类型，这里表示的是长度，" +
			"调用ResultSetMetaData.getPrecision()得到", range = "不限")
	@Param(name = "scale", desc = "列数据类型小数点右边的指定列的位数，调用ResultSetMetaData.getScale()得到"
			, range = "不限，对于不适用小数位数的数据类型，返回0")
	@Return(desc = "经过处理后的数据类型", range = "" +
			"1、对不包含长度和精度的数据类型进行处理，返回数据类型" +
			"2、对包含长度和精度的数据类型进行处理，返回数据类型(长度,精度)" +
			"3、对只包含长度的数据类型进行处理，返回数据类型(长度)")
	private String getColTypeAndPreci(int columnType, String columnTypeName, int precision, int scale) {
		/*
		 * 1、考虑到有些类型在数据库中在获取数据类型的时候就会带有(),同时还能获取到数据的长度和精度，
		 * 因此我们要对所有数据库进行统一处理，去掉()中的内容，使用JDBC提供的方法读取的长度/精度进行拼接
		 * */
		if (precision != 0) {
			int index = columnTypeName.indexOf("(");
			if (index != -1) {
				columnTypeName = columnTypeName.substring(0, index);
			}
		}
		String colTypeAndPreci;
		if (Types.INTEGER == columnType || Types.TINYINT == columnType || Types.SMALLINT == columnType ||
				Types.BIGINT == columnType) {
			//2、上述数据类型不包含长度和精度
			colTypeAndPreci = columnTypeName;
		} else if (Types.NUMERIC == columnType || Types.FLOAT == columnType ||
				Types.DOUBLE == columnType || Types.DECIMAL == columnType) {
			//上述数据类型包含长度和精度，对长度和精度进行处理，返回(长度,精度)
			//1、当一个数的整数部分的长度 > p-s 时，Oracle就会报错
			//2、当一个数的小数部分的长度 > s 时，Oracle就会舍入。
			//3、当s(scale)为负数时，Oracle就对小数点左边的s个数字进行舍入。
			//4、当s > p 时, p表示小数点后第s位向左最多可以有多少位数字，如果大于p则Oracle报错，小数点后s位向右的数字被舍入
			if (precision > precision - Math.abs(scale) || scale > precision || precision == 0) {
				precision = 38;
				scale = 12;
			}
			colTypeAndPreci = columnTypeName + "(" + precision + "," + scale + ")";
		} else {
			//处理字符串类型，只包含长度,不包含精度
			if ("char".equalsIgnoreCase(columnTypeName) && precision > 255) {
				columnTypeName = "varchar";
			}
			colTypeAndPreci = columnTypeName + "(" + precision + ")";
		}
		return colTypeAndPreci;
	}
}
