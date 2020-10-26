package hrds.commons.cache;

public class CacheObj {

	/**
	 * 缓存的key
	 */
	private Object CacheKey;
	/**
	 * 缓存对象
	 */
	private Object CacheValue;
	/**
	 * 缓存过期时间
	 */
	private Long ttlTime;

	public Object getCacheKey() {
		return CacheKey;
	}

	public void setCacheKey(Object cacheKey) {
		CacheKey = cacheKey;
	}

	public Object getCacheValue() {
		return CacheValue;
	}

	public void setCacheValue(Object cacheValue) {
		CacheValue = cacheValue;
	}

	public Long getTtlTime() {
		return ttlTime;
	}

	public void setTtlTime(Long ttlTime) {
		this.ttlTime = ttlTime;
	}

	CacheObj(String cacheKey, Object cacheValue, Long ttl_time) {
		CacheKey = cacheKey;
		CacheValue = cacheValue;
		ttlTime = ttl_time;
	}

	@Override
	public String toString() {
		return "CacheObj{CacheValue=" + CacheValue + ", ttlTime=" + ttlTime + '}';
	}
}
