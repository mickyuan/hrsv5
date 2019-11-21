package hrds.agent.trans.biz.database;

import com.alibaba.fastjson.JSON;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import hrds.commons.base.AgentBaseAction;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Database_set;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.ConnUtil;
import hrds.commons.utils.PackUtil;
import hrds.commons.utils.Platform;
import hrds.commons.utils.xlstoxml.Xls2xml;

import java.sql.Connection;
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
		return JSON.toJSONString(columnList);
	}
}
