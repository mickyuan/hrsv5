package hrds.commons.cache;

public class CacheObj {

	/**
	 * 缓存对象
	 */
	private Object CacheValue;
	/**
	 * 缓存过期时间
	 */
	private Long ttlTime;

	CacheObj(Object cacheValue, Long ttlTime) {
		CacheValue = cacheValue;
		this.ttlTime = ttlTime;
	}

	Object getCacheValue() {
		return CacheValue;
	}

	Long getTtlTime() {
		return ttlTime;
	}

	@Override
	public String toString() {
		return "CacheObj{CacheValue=" + CacheValue + ", ttlTime=" + ttlTime + '}';
	}
}
