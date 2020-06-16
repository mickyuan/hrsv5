package hrds.commons.utils.key;

import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import hrds.commons.exception.BusinessException;

import java.util.HashMap;
import java.util.Map;

/**
 * 生成流水号。需要在数据库中创建表：<br>
 * create table keytable(
 * key_name varchar2(80) not null primary key,
 * value int not null
 * );
 * 如果希望按照某个key_name取流水号，但改key_name在表中不存在，则会自动增加一个从1开始的流水号记录。
 */
class KeyDBPool {

	private long keyMax;
	private long keyMin;
	private long nextKey;
	private int poolSize;
	private String keyName;

	public KeyDBPool(int poolSize, String keyName) {

		this.poolSize = poolSize;
		this.keyName = keyName;
		retrieveFromDB();
	}

	public long getKeyMax() {

		return keyMax;
	}

	public long getKeyMin() {

		return keyMin;
	}

	public long getNextKey() {

		if (nextKey > keyMax) {
			retrieveFromDB();
		}
		return nextKey++;
	}

	private void retrieveFromDB() {
		int keyFromDB = -1;
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			int result = SqlOperator.execute(db, "UPDATE keytable SET key_value = key_value+" + this.poolSize + " WHERE key_name = ?", this.keyName);
			if (result > 1) {
				throw new BusinessException("DB data update exception: keytable, key is duplictate!");
			}
			if (result < 1) {
				int a = SqlOperator.execute(db, "insert into keytable values(?, 1)", this.keyName);
				if (a != 1) {
					throw new BusinessException("DB data insert exception: keytable, init data fail!");
				}
				result = SqlOperator.execute(db, "UPDATE keytable SET key_value = key_value+" + this.poolSize + " WHERE key_name = ?", this.keyName);
			}
			if (result != 1) {
				throw new BusinessException("DB data update exception: keytable, update key value fail!");
			}
			Result rs = SqlOperator.queryResult(db, "SELECT key_value FROM keytable WHERE key_name = ?", this.keyName);
			if (rs == null || rs.getRowCount() != 1) {
				throw new BusinessException("DB data select exception: keytable select fail!");
			}
			keyFromDB = rs.getInt(0, "key_value");
			SqlOperator.commitTransaction(db);
		}
		keyMax = keyFromDB;
		keyMin = keyFromDB - poolSize + 1;
		nextKey = keyMin;
	}
	/*public static Map<String, Integer> getSnowflakeInit() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			Map<String, Integer> map = new HashMap<>();
			Result rs = SqlOperator.queryResult(db, "SELECT key_name,key_value FROM keytable WHERE key_name in(?,?) for update",
					"datacenterId", "machineId");
			for (int i = 0; i < rs.getRowCount(); i++) {
				String keyName = rs.getString(i, "key_name");
				int keyValue = rs.getInt(i, "key_value");
				map.put(keyName, keyValue);
			}
			Integer dVal = map.get("datacenterId");
			Integer mVal = map.get("machineId");
			if (mVal < 31) {
				int result = SqlOperator.execute(db, "UPDATE keytable SET key_value = key_value+1 WHERE key_name = ?", "machineId");
				if (result != 1) {
					throw new BusinessException("DB data update exception: keytable, update key value fail!");
				}
			} else {
				int result = SqlOperator.execute(db, "UPDATE keytable SET key_value = key_value+1 WHERE key_name = ?", "datacenterId");
				if (result != 1) {
					throw new BusinessException("DB data update exception: keytable, update key value fail!");
				}
			}
			if (dVal >= 31 && mVal >= 31) {
				int result = SqlOperator.execute(db, "UPDATE keytable SET key_value = 0 WHERE key_name in(?,?)", "machineId","datacenterId");
				if (result != 2) {
					throw new BusinessException("DB data update exception: keytable, update key value fail!");
				}
			}
			SqlOperator.commitTransaction(db);
			return map;
		}
	}*/
}