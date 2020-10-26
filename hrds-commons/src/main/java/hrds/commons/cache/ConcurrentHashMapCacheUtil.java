package hrds.commons.cache;

import fd.ng.core.annotation.DocClass;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@DocClass(desc = "ConcurrentHashMap缓存工具类", author = "博彦科技", createdate = "2020/8/17 0017 下午 02:29")
public class ConcurrentHashMapCacheUtil {

	private CacheConfBean confBean;

	/**
	 * 当前缓存个数
	 */
	public Integer current_size = 0;
	/**
	 * 缓存对象
	 */
	public Map<String, CacheObj> cache_object_map = new ConcurrentHashMap<>();
	/**
	 * 这个记录了缓存使用的最后一次的记录，最近使用的在最前面
	 */
	public List<String> cache_use_log_list = new LinkedList<>();
	/**
	 * 清理过期缓存的线程是否在运行
	 */
	public Boolean clean_thread_is_run = false;

	//类初始化,使用默认值
	public ConcurrentHashMapCacheUtil(CacheConfBean cacheConfBean) {
		confBean = cacheConfBean;
		//清理线程的执行服务(单线程服务)
		ExecutorService executor = Executors.newSingleThreadExecutor();
		//如果清理过期缓存的线程没有运行则启动清理缓存的线程
		if (!clean_thread_is_run) {
			executor.submit(new CleanTimeOutThread());
		}
	}

	/**
	 * 设置缓存
	 */
	public void setCache(String cacheKey, Object cacheValue, long cacheTime) {
		//设置缓存时间
		Long ttlTime = null;
		//如果缓存时间小于等于0,则设置缓存的有效时间为-1L
		if (cacheTime <= 0L) {
			ttlTime = cacheTime == -1L ? -1L : null;
		}
		//检查检查大小,当前大小如果已经达到最大,首先删除过期缓存，如果过期缓存删除过后还是达到最大缓存数目,删除最久未使用缓存
		checkSize();
		//保存缓存的使用记录
		saveCacheUseLog(cacheKey);
		//设置当前缓存大小
		current_size = current_size + 1;
		//如果设置的缓存的有效时间null,则取传入的缓存时间
		if (ttlTime == null) {
			ttlTime = System.currentTimeMillis() + cacheTime;
		}
		CacheObj cacheObj = new CacheObj(cacheKey, cacheValue, ttlTime);
		cache_object_map.put(cacheKey, cacheObj);
	}

	/**
	 * 设置缓存,设置默认缓存时间为-1
	 */
	public void setCache(String cacheKey, Object cacheValue) {
		setCache(cacheKey, cacheValue, confBean.getCache_time());
	}

	/**
	 * 获取缓存
	 */
	public CacheObj getCache(String cacheKey) {
		CacheObj cacheObj = null;
		if (checkCache(cacheKey)) {
			saveCacheUseLog(cacheKey);
			cacheObj = cache_object_map.get(cacheKey);
		}
		return cacheObj;
	}

	/**
	 * 根据cacheKey判断缓存数据是否存在
	 */
	public boolean isExist(String cacheKey) {
		return checkCache(cacheKey);
	}

	/**
	 * 删除所有缓存
	 */
	public void clear() {
		cache_object_map.clear();
		current_size = 0;
	}

	/**
	 * 删除某个缓存
	 */
	public void deleteCache(String cacheKey) {
		Object cacheValue = cache_object_map.remove(cacheKey);
		if (cacheValue != null) {
			current_size = current_size - 1;
		}
	}

	/**
	 * 判断缓存在不在,过没过期
	 */
	private boolean checkCache(String cacheKey) {
		CacheObj cacheObj = cache_object_map.get(cacheKey);
		//如果该条缓存的cacheObj为null,表示不存在
		if (cacheObj == null) {
			return false;
		}
		//如果失效时间为-1L,说明该条缓存不会失效,代表数据存在
		if (cacheObj.getTtlTime() == -1L) {
			return true;
		}
		//如果该条缓存的失效时间小于当前时间,则表示该条缓存数据已经失效,删除该条数据
		if (cacheObj.getTtlTime() < System.currentTimeMillis()) {
			deleteCache(cacheKey);
			return false;
		}
		return true;
	}

	/**
	 * 删除最久未使用的缓存
	 */
	private void deleteLRU() {
		String cacheKey = cache_use_log_list.remove(cache_use_log_list.size() - 1);
		deleteCache(cacheKey);
	}

	/**
	 * 删除过期的缓存
	 */
	public void deleteTimeOut() {
		//设置需要清理的缓存数据
		for (Map.Entry<String, CacheObj> entry : cache_object_map.entrySet()) {
			//如果缓存数据设置的过期时间小于当前时间,则清理过期的缓存数据
			if (entry.getValue().getTtlTime() < System.currentTimeMillis() && entry.getValue().getTtlTime() != -1L) {
				deleteCache(entry.getKey());
			}
		}
	}

	/**
	 * 检查大小
	 * 当前大小如果已经达到最大
	 * 首先删除过期缓存，如果过期缓存删除过后还是达到最大缓存数目
	 * 删除最久未使用缓存
	 */
	private void checkSize() {
		if (current_size >= confBean.getCache_max_number()) {
			deleteTimeOut();
		}
		if (current_size >= confBean.getCache_max_number()) {
			deleteLRU();
		}
	}

	/**
	 * 保存缓存的使用记录
	 */
	private synchronized void saveCacheUseLog(String cacheKey) {
		cache_use_log_list.remove(cacheKey);
		cache_use_log_list.add(0, cacheKey);
	}

	/**
	 * 设置清理线程的运行状态为正在运行
	 */
	public void setCleanThreadRun(Boolean boo) {
		clean_thread_is_run = boo;
	}

	/**
	 * 开启清理过期缓存的线程
	 */
	private void startCleanThread() {
	}


	/**
	 * 每十分钟清理一次过期缓存
	 * 如果发生异常设置清理线程状态为false
	 * 退出时设置退出状态为-1
	 */
	class CleanTimeOutThread implements Runnable {

		@Override
		public void run() {
			setCleanThreadRun(Boolean.TRUE);
			while (true) {
				try {
					//线程启动时清理一次
					deleteTimeOut();
					//根据配置的频率进行清理
					Thread.sleep(confBean.getCache_cleaning_frequency());
				} catch (InterruptedException e) {
					e.printStackTrace();
					setCleanThreadRun(Boolean.FALSE);
					System.exit(-1);
				}
			}
		}
	}

	public void showUtilsInfo() {
		System.out.println("cache clean thread is run :" + clean_thread_is_run);
		System.out.println("cache max count is :" + confBean.getCache_max_number());
		System.out.println("cache current count is :" + current_size);
		System.out.println("cache object map is :" + cache_object_map.toString());
		System.out.println("cache use log list is :" + cache_use_log_list.toString());
	}

	public static void main(String[] args) {
		//初始化缓存配置信息
		CacheConfBean cacheConfBean = new CacheConfBean();
		//设置缓存时间,缓存数据保存时间 {分钟 * 秒 * 毫秒}  默认值: 时间十分钟
		cacheConfBean.setCache_time(2 * 5 * 1000L);
		//设置缓存大小 默认值: 1000
		cacheConfBean.setCache_max_number(1000);
		//设置缓存清理线程的清理频率
		cacheConfBean.setCache_cleaning_frequency(2 * 5 * 1000L);
		ConcurrentHashMapCacheUtil concurrentHashMapCacheUtil = new ConcurrentHashMapCacheUtil(cacheConfBean);
		//初始化1条缓存数据测试,过期时间 10 * 1000L = 10 秒
		concurrentHashMapCacheUtil.setCache("my_cache_key", "my_cache_key_value");
		concurrentHashMapCacheUtil.showUtilsInfo();
		//测试输出,如果当前缓存的大小为0,则插入一条缓存数据
		while (true) {
			try {
				//执行频率1秒
				Thread.sleep(1000L);
				concurrentHashMapCacheUtil.showUtilsInfo();
				if (concurrentHashMapCacheUtil.current_size == 0) {
					System.out.println("aaa" + concurrentHashMapCacheUtil.current_size);
					concurrentHashMapCacheUtil.setCache("my_cache_key_aaa", "aaa_test", 10 * 1000);
					System.out.println("bbb" + concurrentHashMapCacheUtil.current_size);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				concurrentHashMapCacheUtil.setCleanThreadRun(Boolean.FALSE);
				System.exit(-1);
			}
		}
	}

}

