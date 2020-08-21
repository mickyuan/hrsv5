package hrds.agent.trans.biz.database;

import com.alibaba.fastjson.JSON;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.commons.base.AgentBaseAction;
import hrds.commons.codes.IsFlag;
import hrds.commons.collection.ConnectionTool;
import hrds.commons.entity.Database_set;
import hrds.commons.entity.Table_column;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.ConnUtil;
import hrds.commons.utils.PackUtil;
import hrds.commons.utils.Platform;
import hrds.commons.utils.xlstoxml.Xls2xml;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
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
			String database_name = database_set.getDatabase_name();//数据库名称
			String user_name = database_set.getUser_name();//用户名
			String xmlName = ConnUtil.getDataBaseFile(database_set.getDatabase_ip(), database_set.getDatabase_port(),
					database_name, user_name);
			try (DatabaseWrapper db = ConnectionTool.getDBWrapper(database_set)) {
				//4.数据库采集则获取jdbc连接，读取mate信息，将mate信息写成xml文件
				if (StringUtil.isEmpty(search)) {
					Platform.readModelFromDatabase(db, xmlName);
				} else {
					Platform.readModelFromDatabase(db, xmlName, search);
				}
				//5.读取xml，获取数据库下所有表的信息
				table_List = ConnUtil.getTable(xmlName, search);
			} catch (Exception e) {
				throw new BusinessException("获取数据库的表信息失败" + e.getMessage());
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
			String database_ip = database_set.getDatabase_ip();//数据库IP
			String database_name = database_set.getDatabase_name();//数据库名称
			String database_port = database_set.getDatabase_port();//数据库端口
			String user_name = database_set.getUser_name();//用户名
			String xmlName = ConnUtil.getDataBaseFile(database_ip, database_port, database_name, user_name);
			try (DatabaseWrapper db = ConnectionTool.getDBWrapper(database_set)) {
				//4.数据库采集，判断表是否是指定sql查询需要的列，是则根据sql取字段信息
				if (!StringUtil.isEmpty(hy_sql_meta)) {
					//根据指定sql取需要查询的表的字段信息
					columnList = Platform.getSqlColumnMeta(hy_sql_meta, db);
				} else {
					try {
						//5.去读xml下指定表的字段信息
						columnList = ConnUtil.getColumnByTable(xmlName, tableName);
					} catch (Exception e) {
						//报错了，说明解析xml文件失败，可能是文件被修改或者被删了，重新生成一次
						Platform.readModelFromDatabase(db, xmlName, tableName);
						columnList = ConnUtil.getColumnByTable(xmlName, tableName);
					}
				}
			} catch (Exception e) {
				throw new BusinessException("获取表字段异常");
			}
		}
		//6.返回指定表的字段信息
		return PackUtil.packMsg(JSON.toJSONString(columnList));
	}

	@Method(desc = "根据数据库连接和表名获取表的字段信息",
			logicStep = "1.根据Db_agent判断是否为平面DB数据采集" +
					"2.去读xml下指定表的字段信息" +
					"3.报错了，说明解析xml文件失败，可能是文件被修改或者被删了，重新生成一次xml文件再读取" +
					"4.返回指定表的字段信息")
	@Param(name = "database_set", desc = "数据库连接设置信息表对象", range = "表中不能为空的字段必须有值"
			, isBean = true)
	@Return(desc = "数据库下表的字段信息", range = "不会为空，如果字符串的长度超过300，将会被压缩")
	public String getAllTableColumn(Database_set database_set) {
		Map<String, List<Map<String, String>>> columnList;
		//1.根据Db_agent判断是否为平面DB数据采集
		String db_path = database_set.getPlane_url();
		String xmlName = ConnUtil.getDataBaseFile("", "", db_path, "");
		try {
			//2.去读xml下指定表的字段信息
			columnList = ConnUtil.getColumnByXml(xmlName);
		} catch (Exception e) {
			//3.报错了，说明解析xml文件失败，可能是文件被修改或者被删了，重新生成一次xml文件再读取
			Xls2xml.toXml(db_path, xmlName);
			columnList = ConnUtil.getColumnByXml(xmlName);
		}
		//4.返回指定表的字段信息
		return PackUtil.packMsg(JSON.toJSONString(columnList));
	}

	@Method(desc = "根据数据库连接和表名获取表的字段信息",
			logicStep = "1.根据Db_agent判断是否为平面DB数据采集" +
					"2.去读xml下指定表的字段信息" +
					"3.报错了，说明解析xml文件失败，可能是文件被修改或者被删了，重新生成一次xml文件再读取" +
					"4.返回指定表的文件基本信息")
	@Param(name = "database_set", desc = "数据库连接设置信息表对象", range = "表中不能为空的字段必须有值"
			, isBean = true)
	@Return(desc = "数据库下表的字段信息", range = "不会为空，如果字符串的长度超过300，将会被压缩")
	public String getAllTableStorage(Database_set database_set) {
		List<Map<String, Object>> allStorageList;
		//1.根据Db_agent判断是否为平面DB数据采集
		String db_path = database_set.getPlane_url();
		String xmlName = ConnUtil.getDataBaseFile("", "", db_path, "");
		try {
			//2.去读xml下指定表的字段信息
			allStorageList = ConnUtil.getStorageByXml(xmlName);
		} catch (Exception e) {
			//3.报错了，说明解析xml文件失败，可能是文件被修改或者被删了，重新生成一次xml文件再读取
			Xls2xml.toXml(db_path, xmlName);
			allStorageList = ConnUtil.getStorageByXml(xmlName);
		}
		//4.返回指定表的字段信息
		return PackUtil.packMsg(JSON.toJSONString(allStorageList));
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
	public String getCustColumn(Database_set dbSet, String custSQL) {
		//替换占位符custSQL,为了防止sql有占位符，替换ccc = #{aaa}为1=2
		custSQL = custSQL.replaceAll("\\s+?((?!\\s).)+?\\s+?=\\s+?#\\{.*\\}", " 1=2");
		//替换 #{aaa}为 ''
		custSQL = custSQL.replaceAll("#\\{.*\\}", "' '");
		//1、使用dbinfo将需要测试并行抽取的数据库连接内容填充
		try (DatabaseWrapper db = ConnectionTool.getDBWrapper(dbSet)) {
			//2、执行自定义抽取SQL，获取执行结果集
			ResultSet rs = db.queryGetResultSet(custSQL);
			List<Table_column> tableColumns = new ArrayList<>();
			try {
				//3、根据结果集拿到该结果集的meta信息
				ResultSetMetaData metaData = rs.getMetaData();
				for (int j = 1; j <= metaData.getColumnCount(); j++) {
					Table_column tableColumn = new Table_column();
					tableColumn.setColumn_name(metaData.getColumnName(j));
					//对列类型做特殊处理，处理成varchar(512), numeric(10,4)
					String colTypeAndPreci = Platform.getColType(metaData.getColumnType(j),
							metaData.getColumnTypeName(j), metaData.getPrecision(j),
							metaData.getScale(j), metaData.getColumnDisplaySize(j));
					tableColumn.setColumn_type(colTypeAndPreci);
					//列中文名默认设置为列英文名
					tableColumn.setColumn_ch_name(metaData.getColumnName(j));
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
}
