package hrds.agent.job.biz.core.objectstage.service.impl;

import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.agent.job.biz.bean.DataStoreConfBean;
import hrds.agent.job.biz.bean.ObjectTableBean;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.core.objectstage.service.ObjectProcessAbstract;
import hrds.agent.job.biz.utils.DataTypeTransform;
import hrds.commons.codes.StoreLayerAdded;
import hrds.commons.collection.ConnectionTool;
import hrds.commons.exception.AppSystemException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MppTableProcessImpl
 * date: 2020/4/26 17:26
 * author: zxz
 */
public class MppTableProcessImpl extends ObjectProcessAbstract {
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
	//是否为主键的列
	protected Map<String, Boolean> isPrimaryKeyMap;
	//删除的依据的列
	private final List<String> deleteColumnList;
	//需要更新的列信息
	private final List<String> setColumnList;
	//更新的where条件列信息
	private final List<String> whereColumnList;
	//delete Sql拼接的个数
	private int deleteNum = 1;
	//存储层名称
	private final String dsl_name;

	public MppTableProcessImpl(TableBean tableBean, ObjectTableBean objectTableBean,
							   DataStoreConfBean dataStoreConfBean) {
		super(tableBean, objectTableBean);
		this.dsl_name = dataStoreConfBean.getDsl_name();
		//获取batch插入的sql
		this.insertSql = getBatchInsertSql();
		//获取batch更新的sql
		this.updateSql = getBatchUpdateSql();
		//获取是否设置主键属性
		this.isPrimaryKeyMap = getPrimaryKeyMap(dataStoreConfBean.getAdditInfoFieldMap());
		//数据库的连接
		this.db = ConnectionTool.getDBWrapper(dataStoreConfBean.getData_store_connect_attr());
		this.db.beginTrans();
		//判断增量更新的表是否存在，不存在则创建表
		createTableIfNotExist();
		//获取删除依据的判断条件
		this.deleteColumnList = getDeleteColumnList(isZipperKeyMap);
		//获取需要更新的数据
		this.setColumnList = getSetColumnList(isZipperKeyMap);
		//获取需要跟新的数据的判断条件
		this.whereColumnList = getWhereColumnList();
		deleteSql.append("DELETE FROM ").append(objectTableBean.getEn_name()).append(" WHERE ").append("(");
		for (String column : deleteColumnList) {
			deleteSql.append(column).append(",");
		}
		deleteSql.delete(deleteSql.length() - 1, deleteSql.length()).append(") IN (");
	}

	private List<String> getDeleteColumnList(Map<String, Boolean> isZipperKeyMap) {
		List<String> delColumnList = new ArrayList<>();
		for (String column : selectColumnList) {
			if (isZipperKeyMap.get(column)) {
				delColumnList.add(column);
			}
		}
		if (delColumnList.size() == 0) {
			delColumnList = selectColumnList;
		}
		return delColumnList;
	}

	private Map<String, Boolean> getPrimaryKeyMap(Map<String, Map<String, Integer>> additInfoFieldMap) {
		Map<String, Boolean> pMap = new HashMap<>();
		if (additInfoFieldMap != null && !additInfoFieldMap.isEmpty()) {
			for (String dsla_storelayer : additInfoFieldMap.keySet()) {
				if (StoreLayerAdded.ZhuJian.getCode().equals(dsla_storelayer)) {
					List<String> primaryColumnList = new ArrayList<>(additInfoFieldMap.get(dsla_storelayer).keySet());
					for (String column : metaColumnList) {
						if (primaryColumnList.contains(column)) {
							pMap.put(column, true);
						} else {
							pMap.put(column, false);
						}
					}
				}
			}
		}
		if (pMap.isEmpty()) {
			for (String column : metaColumnList) {
				pMap.put(column, false);
			}
		}
		return pMap;
	}

	/**
	 * 判断增量表是否存在，不存在创建表
	 */
	private void createTableIfNotExist() {
		if (!db.isExistTable(objectTableBean.getEn_name())) {
			StringBuilder create = new StringBuilder(1024);
			create.append("CREATE TABLE ");
			create.append(objectTableBean.getEn_name());
			create.append("(");
			for (int i = 0; i < metaColumnList.size(); i++) {
				create.append(metaColumnList.get(i)).append(" ").append(
						DataTypeTransform.tansform(metaTypeList.get(i), dsl_name));
				if (isPrimaryKeyMap.get(metaColumnList.get(i))) {
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
					Object[] object = new Object[metaColumnList.size()];
					for (int i = 0; i < metaColumnList.size(); i++) {
						//加类型转换
						object[i] = valueList.get(operate).get(metaColumnList.get(i));
					}
					addParamsPool.add(object);
				} else if ("update".equals(operate)) {
					if (whereColumnList.isEmpty()) {
						throw new AppSystemException("半结构对象采集存储层选择" + dsl_name
								+ "有更新操作，但没有选择主键");
					}
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
			if (deleteNum % 1000 == 0) {
				deleteSql.delete(deleteSql.length() - 1, deleteSql.length()).append(")");
				//每900条删除一次
				db.execute(deleteSql.toString());
				deleteSql.delete(0, deleteSql.length());
				deleteSql.append("DELETE FROM ").append(objectTableBean.getEn_name()).append(" WHERE ").append("(");
				for (String column : deleteColumnList) {
					deleteSql.append(column).append(",");
				}
				deleteSql.delete(deleteSql.length() - 1, deleteSql.length()).append(") IN (");
				deleteNum = 1;
			}
			if (updateParamsPool.size() != 0 && updateParamsPool.size() % 5000 == 0) {
				//如果更新的有数据，说明delete的值全部读完，这时候判断如果deleteNum不等于0则立即执行剩余的删除操作
				if (deleteNum > 1) {
					deleteSql.delete(deleteSql.length() - 1, deleteSql.length()).append(")");
					//每900条删除一次
					db.execute(deleteSql.toString());
					deleteNum = 1;
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
			if (deleteNum > 1) {
				deleteSql.delete(deleteSql.length() - 1, deleteSql.length()).append(")");
				//每900条删除一次
				db.execute(deleteSql.toString());
				deleteNum = 1;
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
		updateSql.append("UPDATE ").append(objectTableBean.getEn_name()).append(" SET ");
		StringBuilder sb = new StringBuilder();
		sb.append(" WHERE ");
		for (String updateColumn : metaColumnList) {
			if (!isZipperKeyMap.get(updateColumn)) {
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
		insertSql.append("INSERT INTO ").append(objectTableBean.getEn_name()).append(" (");
		StringBuilder sb = new StringBuilder();
		sb.append(" ) VALUES (");
		for (String column : metaColumnList) {
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

	private List<String> getSetColumnList(Map<String, Boolean> isPrimaryKeyMap) {
		List<String> setColumnList = new ArrayList<>();
		for (String updateColumn : metaColumnList) {
			if (!isPrimaryKeyMap.get(updateColumn)) {
				setColumnList.add(updateColumn);
			}
		}
		return setColumnList;
	}

	private List<String> getWhereColumnList() {
		List<String> whereColumnList = new ArrayList<>();
		for (String updateColumn : metaColumnList) {
			if (isZipperKeyMap.get(updateColumn)) {
				whereColumnList.add(updateColumn);
			}
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
