package hrds.agent.job.biz.core.dfstage.incrementfileprocess.impl;

import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.DataStoreConfBean;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.core.dfstage.incrementfileprocess.TableProcessAbstract;
import hrds.agent.job.biz.utils.DataTypeTransform;
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
	private final List<Object[]> addParamsPool = new ArrayList<>();
	// 存储update信息的list
	private final List<Object[]> updateParamsPool = new ArrayList<>();
	//批量删除的sql
	private final StringBuilder deleteSql = new StringBuilder();
	//数据库的连接
	private final DatabaseWrapper db;
	//batch插入的sql
	private final String insertSql;
	//batch更新的sql
	private final String updateSql;
	//需要更新的列信息
	private final List<String> setColumnList;
	//更新的where条件列信息
	private final List<String> whereColumnList;
	//delete Sql拼接的个数
	private int deleteNum = 0;
	//存储层名称
	private final String dsl_name;

	public MppTableProcessImpl(TableBean tableBean, CollectTableBean collectTableBean,
							   DataStoreConfBean dataStoreConfBean) {
		super(tableBean, collectTableBean);
		//获取batch插入的sql
		this.insertSql = getBatchInsertSql();
		//获取batch更新的sql
		this.updateSql = getBatchUpdateSql();
		this.dsl_name = dataStoreConfBean.getDsl_name();
		//数据库的连接
		this.db = ConnectionTool.getDBWrapper(dataStoreConfBean.getData_store_connect_attr());
		this.db.beginTrans();
		//判断增量更新的表是否存在，不存在则创建表
		createTableIfNotExist();
		//获取需要更新的数据
		this.setColumnList = getSetColumnList();
		//获取需要跟新的数据的判断条件
		this.whereColumnList = getWhereColumnList();
		deleteSql.append("DELETE FROM ").append(collectTableBean.getHbase_name()).append(" WHERE ").append("(");
		for (String column : deleteColumnList) {
			deleteSql.append(column).append(",");
		}
		deleteSql.delete(deleteSql.length() - 1, deleteSql.length()).append(") IN (");
	}

	/**
	 * 判断增量表是否存在，不存在创建表
	 */
	private void createTableIfNotExist() {
		if (!db.isExistTable(collectTableBean.getHbase_name())) {
			StringBuilder create = new StringBuilder(1024);
			create.append("CREATE TABLE ");
			create.append(collectTableBean.getHbase_name());
			create.append("(");
			for (int i = 0; i < dictionaryColumnList.size(); i++) {
				create.append(dictionaryColumnList.get(i)).append(" ").append(
						DataTypeTransform.tansform(dictionaryTypeList.get(i), dsl_name));
				if (isPrimaryKeyMap.get(dictionaryColumnList.get(i))) {
					create.append(" primary key");
				}
				create.append(",");
			}
			//将最后的逗号删除
			create.deleteCharAt(create.length() - 1);
			create.append(")");
			db.execute(create.toString());
		}
	}

	@Override
	public void dealData(Map<String, Map<String, Object>> valueList) {
		try {
			for (String operate : valueList.keySet()) {
				if ("insert".equals(operate)) {
					Object[] object = new Object[insertColumnList.size()];
					for (int i = 0; i < insertColumnList.size(); i++) {
						//加类型转换
						object[i] = valueList.get(operate).get(insertColumnList.get(i));
					}
					addParamsPool.add(object);
				} else if ("update".equals(operate)) {
					Object[] object = new Object[setColumnList.size() + whereColumnList.size()];
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
			//先执行删除，再执行更新, 再执行新增
			if (deleteNum % 900 == 0) {
				deleteSql.delete(deleteSql.length() - 1, deleteSql.length()).append(")");
				//每900条删除一次
				db.execute(deleteSql.toString());
				deleteSql.delete(0, deleteSql.length());
				deleteSql.append("DELETE FROM ").append(collectTableBean.getHbase_name()).append(" WHERE ").append("(");
				for (String column : deleteColumnList) {
					deleteSql.append(column).append(",");
				}
				deleteSql.delete(deleteSql.length() - 1, deleteSql.length()).append(") IN (");
				deleteNum = 0;
			}
			if (updateParamsPool.size() != 0 && updateParamsPool.size() % 5000 == 0) {
				//如果更新的有数据，说明delete的值全部读完，这时候判断如果deleteNum不等于0则立即执行剩余的删除操作
				if (deleteNum > 0) {
					deleteSql.delete(deleteSql.length() - 1, deleteSql.length()).append(")");
					//每900条删除一次
					db.execute(deleteSql.toString());
					deleteNum = 0;
				}
				//每5000条batch提交一次
				db.execBatch(updateSql, updateParamsPool);
				updateParamsPool.clear();
			}
			if (addParamsPool.size() != 0 && addParamsPool.size() % 5000 == 0) {
				//如果新增的有数据,说明update的值全部读完，判断如果updateParamsPool.size()大于0则立即执行剩余的更新操作
				if (updateParamsPool.size() > 0) {
					db.execBatch(updateSql, updateParamsPool);
					updateParamsPool.clear();
				}
				//每5000条batch提交一次
				db.execBatch(insertSql, addParamsPool);
				addParamsPool.clear();
			}
		} catch (Exception e) {
			if (db != null)
				db.rollback();
			throw new AppSystemException("Mpp数据库增量模式直接更新库失败", e);
		}
	}

	@Override
	public void excute() {
		//最后执行一次提交,如果删除的没有过900，更新和新增的都没有过5000则第一次执行就到这里
		try {
			if (deleteNum > 0) {
				deleteSql.delete(deleteSql.length() - 1, deleteSql.length()).append(")");
				//每900条删除一次
				db.execute(deleteSql.toString());
				deleteNum = 0;
			}
			if (updateParamsPool.size() > 0) {
				db.execBatch(updateSql, updateParamsPool);
			}
			if (addParamsPool.size() > 0) {
				db.execBatch(insertSql, addParamsPool);
			}
		} catch (Exception e) {
			if (db != null)
				db.rollback();
			throw new AppSystemException("Mpp数据库增量模式直接更新库失败", e);
		}
	}

	/**
	 * 获取batch更新的sql
	 */
	private String getBatchUpdateSql() {
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("UPDATE ").append(collectTableBean.getHbase_name()).append(" SET ");
		StringBuilder sb = new StringBuilder();
		sb.append(" WHERE ");
		for (String updateColumn : updateColumnList) {
			if (!isPrimaryKeyMap.get(updateColumn)) {
				//不是主键
				updateSql.append(updateColumn).append(" = ?,");
			} else {
				//是主键
				sb.append(updateColumn).append(" = ? and ");
			}
		}
		updateSql.delete(updateSql.length() - 1, updateSql.length());
		sb.delete(sb.length() - 4, sb.length());
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
		db.commit();
		db.close();
	}

	private List<String> getSetColumnList() {
		List<String> setColumnList = new ArrayList<>();
		for (String updateColumn : updateColumnList) {
			if (!isPrimaryKeyMap.get(updateColumn)) {
				setColumnList.add(updateColumn);
			}
		}
		return setColumnList;
	}

	private List<String> getWhereColumnList() {
		List<String> whereColumnList = new ArrayList<>();
		for (String updateColumn : updateColumnList) {
			if (isPrimaryKeyMap.get(updateColumn)) {
				whereColumnList.add(updateColumn);
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
