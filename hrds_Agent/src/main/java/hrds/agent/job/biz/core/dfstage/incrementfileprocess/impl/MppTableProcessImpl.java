package hrds.agent.job.biz.core.dfstage.incrementfileprocess.impl;

import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.DataStoreConfBean;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.core.dfstage.incrementfileprocess.TableProcessAbstract;
import hrds.commons.collection.ConnectionTool;
import hrds.commons.exception.AppSystemException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MppTableProcessImpl
 * date: 2020/4/26 17:26
 * author: zxz
 */
public class MppTableProcessImpl extends TableProcessAbstract {
	//存储全量插入信息的list
	private List<Object[]> addParamsPool = new ArrayList<>();
	// 存储update信息的list
	private List<Object[]> updateParamsPool = new ArrayList<>();
	//批量删除的sql
	private StringBuilder deleteSql = new StringBuilder();
	//数据库的连接
	private DatabaseWrapper db;
	//batch插入的sql
	private String insertSql;
	//batch更新的sql
	private String updateSql;
	//需要更新的列信息
	private List<String> setColumnList;
	//更新的where条件列信息
	private List<String> whereColumnList;
	//delete Sql拼接的个数
	private int deleteNum = 0;

	public MppTableProcessImpl(TableBean tableBean, CollectTableBean collectTableBean,
	                           DataStoreConfBean dataStoreConfBean) {
		super(tableBean, collectTableBean);
		//获取batch插入的sql
		this.insertSql = getBatchInsertSql();
		//获取batch更新的sql
		this.updateSql = getBatchUpdateSql();
		//数据库的连接
		this.db = ConnectionTool.getDBWrapper(dataStoreConfBean.getData_store_connect_attr());
		//获取需要更新的数据
		this.setColumnList = getSetColumnList();
		//获取需要跟新的数据的判断条件
		this.whereColumnList = getWhereColumnList();
		deleteSql.append("DELETE FROM ").append(collectTableBean.getHbase_name()).append(" WHERE ").append("(");
		for (String column : deleteColumnList) {
			deleteSql.append(column).append(",");
		}
		deleteSql.delete(deleteSql.length() - 1, deleteSql.length()).append(" IN (");
	}

	@Override
	public void dealData(Map<String, Map<String, Object>> valueList) {
		for (String operate : valueList.keySet()) {
			if ("insert".equals(operate)) {
				Object[] object = new Object[insertColumnList.size()];
				for (int i = 0; i < insertColumnList.size(); i++) {
					//加类型转换
					object[i] = valueList.get(operate).get(insertColumnList.get(i));
				}
				addParamsPool.add(object);
			} else if ("update".equals(operate)) {
				Object[] object = new Object[updateColumnList.size()];
				for (int i = 0; i < setColumnList.size(); i++) {
					//加类型转换
					object[i] = valueList.get(operate).get(setColumnList.get(i));
				}
				for (int i = 0; i < whereColumnList.size(); i++) {
					//加类型转换
					object[setColumnList.size() + i] = valueList.get(operate).get(whereColumnList.get(i));
				}
				updateParamsPool.add(object);
			} else if ("delete".equals(operate)) {
				deleteNum++;
				deleteSql.append("(");
				for (String column : deleteColumnList) {
					deleteSql.append(getDeleteValue(valueList.get(operate).get(column))).append(",");
				}
				deleteSql.delete(deleteSql.length() - 1, deleteSql.length());
				deleteSql.append(")").append(",");
			} else {
				throw new AppSystemException("增量数据采集不自持" + operate + "操作");
			}
		}
		if (addParamsPool.size() % 5000 == 0) {
			//每5000条batch提交一次
			db.execBatch(insertSql, addParamsPool);
			addParamsPool.clear();
		}
		if (updateParamsPool.size() % 5000 == 0) {
			//每5000条batch提交一次
			db.execBatch(updateSql, updateParamsPool);
			updateParamsPool.clear();
		}
		if (deleteNum % 900 == 0) {
			deleteSql.delete(deleteSql.length() - 1, deleteSql.length()).append(")");
			//每900条删除一次
			db.execute(deleteSql.toString());
			deleteSql.delete(0, deleteSql.length());
			deleteSql.append("DELETE FROM ").append(collectTableBean.getHbase_name()).append(" WHERE ").append("(");
			for (String column : deleteColumnList) {
				deleteSql.append(column).append(",");
			}
			deleteSql.delete(deleteSql.length() - 1, deleteSql.length()).append(" IN (");
		}
	}

	@Override
	public void excute() {
		//最后执行一次提交
		db.execBatch(insertSql, addParamsPool);
		db.execBatch(updateSql, updateParamsPool);
		deleteSql.delete(deleteSql.length() - 1, deleteSql.length()).append(")");
		db.execute(deleteSql.toString());
	}

	/**
	 * 获取batch更新的sql
	 */
	private String getBatchUpdateSql() {
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("UPDATE ").append(collectTableBean.getHbase_name()).append(" SET ");
		StringBuilder sb = new StringBuilder();
		sb.append(" WHERE ");
		for (int i = 0; i < updateColumnList.size(); i++) {
			if (!isPrimaryKeyList.get(i)) {
				//不是主键
				updateSql.append(updateColumnList.get(i)).append(" = ?,");
			} else {
				//是主键
				sb.append(updateColumnList.get(i)).append(" = ?,");
			}
		}
		updateSql.delete(updateSql.length() - 1, updateSql.length());
		sb.delete(sb.length() - 1, sb.length());
		updateSql.append(sb);
		return updateSql.toString();
	}

	/**
	 * 获取batch插入的sql
	 */
	private String getBatchInsertSql() {
		StringBuilder insertSql = new StringBuilder();
		insertSql.append("INSERT INTO ").append(collectTableBean.getHbase_name()).append(" (");
		StringBuilder sb = new StringBuilder();
		sb.append(" ) VALUES (");
		for (String column : insertColumnList) {
			insertSql.append(column).append(",");
			sb.append("?").append(",");
		}
		insertSql.delete(insertSql.length() - 1, insertSql.length());
		sb.delete(sb.length() - 1, sb.length()).append(" ) ");
		insertSql.append(sb);
		return insertSql.toString();
	}

	@Override
	public void close() {
		db.close();
	}

	private List<String> getSetColumnList() {
		List<String> setColumnList = new ArrayList<>();
		for (int i = 0; i < updateColumnList.size(); i++) {
			if (!isPrimaryKeyList.get(i)) {
				setColumnList.add(updateColumnList.get(i));
			}
		}
		return setColumnList;
	}

	private List<String> getWhereColumnList() {
		List<String> whereColumnList = new ArrayList<>();
		for (int i = 0; i < updateColumnList.size(); i++) {
			if (isPrimaryKeyList.get(i)) {
				whereColumnList.add(updateColumnList.get(i));
			}
		}
		if (whereColumnList.isEmpty()) {
			throw new AppSystemException("DB文件采集，采集增量数据，直接更新，没有指定主键");
		}
		return whereColumnList;
	}

	/**
	 * 拼接delete的sql语句时，字符串两边要加单引号
	 */
	private Object getDeleteValue(Object value) {
		if (value instanceof String) {
			String strData = (String) value;
			return "'" + strData + "'";
		} else {
			return value;
		}
	}
}
