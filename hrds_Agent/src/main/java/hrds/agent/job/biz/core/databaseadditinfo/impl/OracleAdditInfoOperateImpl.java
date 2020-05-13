package hrds.agent.job.biz.core.databaseadditinfo.impl;

import fd.ng.core.annotation.DocClass;
import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.agent.job.biz.core.databaseadditinfo.DatabaseAdditInfoOperateInterface;
import hrds.agent.job.biz.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;

@DocClass(desc = "oracle数据库字段添加附加信息属性", author = "zxz", createdate = "2020/5/12 18:02")
public class OracleAdditInfoOperateImpl implements DatabaseAdditInfoOperateInterface {
	private static final Log logger = LogFactory.getLog(OracleAdditInfoOperateImpl.class);
	private static final String PK = "_pk";
	private static final String INDEX = "_ix";

	@Override
	public void addNormalIndex(String tableName, ArrayList<String> columns, DatabaseWrapper db) {
		if (columns != null && columns.size() > 0) {
			String indexName = tableName + INDEX;
			if (!SQLUtil.indexIfExistForPostgresql(indexName, db)) {
				long startTime = System.currentTimeMillis();
				StringBuilder sb = new StringBuilder();
				sb.append("create index ").append(indexName);
				sb.append(" on ").append(tableName).append("(");
				for (String column : columns) {
					sb.append(column).append(",");
				}
				sb.delete(sb.length() - 1, sb.length());
				sb.append(")");
				db.execute(sb.toString());
				logger.info("表" + tableName + "创建普通索引耗时："
						+ ((System.currentTimeMillis() - startTime) / 1000) + "秒");
			} else {
				logger.info("表" + tableName + "索引已经存在，无需创建");
			}
		}
	}

	@Override
	public void addPkConstraint(String tableName, ArrayList<String> columns, DatabaseWrapper db) {
		if (columns != null && columns.size() > 0) {
			String pkObjectName = tableName + PK;
			if (SQLUtil.pkIfExistForPostgresql(pkObjectName, db)) {
				long startTime = System.currentTimeMillis();
				StringBuilder sb = new StringBuilder();
				sb.append("alter table ").append(tableName);
				sb.append(" add constraint ").append(pkObjectName).append(" primary key ").append("(");
				for (String column : columns) {
					sb.append(column).append(",");
				}
				sb.delete(sb.length() - 1, sb.length());
				sb.append(")");
				db.execute(sb.toString());
				logger.info("表" + tableName + "添加主键约束耗时："
						+ ((System.currentTimeMillis() - startTime) / 1000) + "秒");
			}
		}
	}

	@Override
	public void dropIndex(String tableName, DatabaseWrapper db) {
		String normalIndexName = tableName + INDEX;
		if (SQLUtil.indexIfExistForPostgresql(normalIndexName, db)) {
			String sql = "drop index " + normalIndexName;
			db.execute(sql);
		}
	}

	@Override
	public void dropPkConstraint(String tableName, DatabaseWrapper db) {
		String pkObjectName = tableName + PK;
		if (SQLUtil.pkIfExistForPostgresql(pkObjectName, db)) {
			String sql = "alter table " + tableName + " drop constraint " + pkObjectName;
			db.execute(sql);
		}
	}
}
