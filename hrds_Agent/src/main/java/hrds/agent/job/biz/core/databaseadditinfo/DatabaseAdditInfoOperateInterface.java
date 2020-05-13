package hrds.agent.job.biz.core.databaseadditinfo;

import fd.ng.db.jdbc.DatabaseWrapper;

import java.util.List;

/**
 * additInfoOperateInterface
 * date: 2020/5/12 17:50
 * author: zxz
 */
public interface DatabaseAdditInfoOperateInterface {

	void addNormalIndex(String tableName, List<String> columns, DatabaseWrapper db);

	void addPkConstraint(String tableName, List<String> columns, DatabaseWrapper db);

	void dropIndex(String tableName, DatabaseWrapper db);

	void dropPkConstraint(String tableName, DatabaseWrapper db);

}
