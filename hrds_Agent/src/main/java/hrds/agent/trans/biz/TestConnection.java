package hrds.agent.trans.biz;

import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.agent.job.biz.bean.DBConfigBean;
import hrds.commons.base.AgentBaseAction;
import hrds.commons.entity.Database_set;

/**
 * description: 测试连接
 * author: xchao
 * create: 2019-09-05 11:18
 */
public class TestConnection extends AgentBaseAction {

	@Method(desc = "测试连接的方法",
			logicStep = "1、通过request获取服务发过来的数据" +
					"2、使用dbinfo将需要测试连接的内容填充" +
					"3、测试连接")
	@Param(name = "dbSet", desc = "数据库连接设置表对象，此对象不能为空的字段必须有值",
			range = "不能为空", isBean = true)
	@Return(desc = "是否连接成功的判断", range = "不会为空")
	public boolean testConn(Database_set dbSet) {
		//2、使用dbinfo将需要测试连接的内容填充
		DBConfigBean dbInfo = new DBConfigBean();
		dbInfo.setDatabase_drive(dbSet.getDatabase_drive());
		dbInfo.setJdbc_url(dbSet.getJdbc_url());
		dbInfo.setUser_name(dbSet.getUser_name());
		dbInfo.setDatabase_pad(dbSet.getDatabase_pad());
		dbInfo.setDatabase_type(dbSet.getDatabase_type());
		//3、测试连接
		try (DatabaseWrapper db = ConnetionTool.getDBWrapper(dbInfo)) {
			return db.isConnected();
		}
	}

}
