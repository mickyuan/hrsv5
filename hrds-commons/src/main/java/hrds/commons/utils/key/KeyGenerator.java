package hrds.commons.utils.key;

import java.util.HashMap;
import java.util.Map;

public class KeyGenerator {
	private static final KeyGenerator m_instance = new KeyGenerator();
	private static final int POOL_SIZE = 5;
	private Map<String, KeyDBPool> keyList;

	public synchronized long getNextKey(String keyName) {
		KeyDBPool key_pool;
		if (keyList.containsKey(keyName)) {
			key_pool = keyList.get(keyName);
		} else {
			key_pool = new KeyDBPool(5, keyName);
			keyList.put(keyName, key_pool);
		}
		return key_pool.getNextKey();
	}

	private KeyGenerator() {
		keyList = new HashMap<>(10);
	}

	public static KeyGenerator getInstance() {
		return m_instance;
	}
}
