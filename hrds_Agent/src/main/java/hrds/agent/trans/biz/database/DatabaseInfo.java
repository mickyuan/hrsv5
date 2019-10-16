package hrds.agent.trans.biz.database;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Database_set;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.ConnUtil;
import hrds.commons.utils.Platform;
import hrds.commons.utils.xlstoxml.Xls2xml;

import java.sql.Connection;

/**
 * Agent获取远程数据库的表，表中的字段的接口
 * date: 2019/10/15 14:40
 * author: zxz
 */
public class DatabaseInfo extends BaseAction {

	@Method(desc = "根据数据库连接获取数据库下表信息", logicStep = "")
	@Param(name = "database_set", desc = "数据库连接设置信息表", range = "表中不能为空的字段必须有值", isBean = true)
	@Param(name = "search", desc = "需要查询的表", range = "可以为空", nullable = true)
	@Return(desc = "数据库下表信息", range = "可能为空，极端情况下空的数据库返回值为空")
	public String getDatabaseInfo(Database_set database_set, String search) {
		JSONObject receiveMsg;
		//是db平面采集
		if (IsFlag.Shi.getCode().equals(database_set.getDb_agent())) {
			String db_path = database_set.getPlane_url();
			String xmlName = Math.abs(db_path.hashCode()) + ".xml";
			Xls2xml.toXml(db_path, xmlName);
			receiveMsg = ConnUtil.getTableToXML(xmlName);
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
			//判断文件是否存在，如果不存在才进行生成新的xml
			Connection con = null;
			try {
				con = ConnUtil.getConnection(database_drive, jdbc_url, user_name, database_pad);
				if (StringUtil.isEmpty(search)) {
					Platform.readModelFromDatabase(con, xmlName, type, database_name);
				} else {
					Platform.readModelFromDatabase(con, xmlName, type, database_name, search);
				}
				receiveMsg = ConnUtil.getTable(xmlName);
			} catch (Exception e) {
				throw new BusinessException("获取数据库的表信息失败" + e.getMessage());
			} finally {
				ConnUtil.close(con);
			}

		}
		return receiveMsg.toJSONString();
	}
}
