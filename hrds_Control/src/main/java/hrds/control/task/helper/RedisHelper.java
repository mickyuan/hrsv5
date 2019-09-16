package hrds.control.task.helper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import redis.clients.jedis.Jedis;

/**
 * ClassName: RedisHelper
 * Description: 用于操作redis的类
 * Author: Tiger.Wang
 * Date: 2019/9/5 13:17
 * Since: JDK 1.8
 **/
public class RedisHelper {

    private static final Logger logger = LogManager.getLogger();

    private final Jedis jedis;
    public final String redisIp;
    public final int redisPort;
    public final int timeout;
    // 目的是做成单例
    private static final RedisHelper INSTANCE = new RedisHelper();

    private RedisHelper() {
        // TODO 此处应该从配置文件读取
        this.redisIp = "127.0.0.1";
        this.redisPort = 6379;
        this.timeout = 100000;

        this.jedis = new Jedis(redisIp, redisPort, timeout);

        logger.info("---------- redis构建成功 ----------");
    }

    /**
     * 获得RedisHelper实例
     * @author Tiger.Wang
     * @date 2019/9/5
     * @return hrds.control.task.helper.RedisHelper
     */
    public static RedisHelper getInstance() {

        return INSTANCE;
    }

    /**
     * 根据key删除redis中的记录
     * @author Tiger.Wang
     * @date 2019/9/5
     * @param key   redis键值
     * @return long 删除的数据行数
     */
    public long deleteByKey(String... key) {

        return jedis.del(key);
    }

    /**
     * 根据key，将内容追加到redis队列后面。注意，该方法若push失败，将会重新连接redis并一直尝试重新rpush
     * @author Tiger.Wang
     * @date 2019/9/5
     * @param key   redis键值
     * @param content   追加的内容
     */
    public void rpush(String key, String content) {

        while( true ) {
            try {
                jedis.rpush(key, content);
            }
            catch(Exception ex) {
                logger.error("JedisError:" + ex.getMessage());
                reconnect();
            }
        }
    }

    /**
     * 根据键值获取redis中，该键值对应的数据行数。
     * 注意，该方法不会弹出redis中的数据，且若获取数据失败，将会重新连接redis并一直尝试重新llen。
     * @author Tiger.Wang
     * @date 2019/9/5
     * @param key   redis键值
     * @return long redis键值对应的数据行数
     */
    public long llen(String key) {

        while( true ) {
            try {
                return jedis.llen(key);
            }
            catch(Exception ex) {
                logger.error("JedisError:" + ex.getMessage());
                reconnect();
            }
        }
    }

    /**
     * 根据键值，将redis中最靠前的数据弹出
     * @author Tiger.Wang
     * @date 2019/9/5
     * @param key   redis键值
     * @return java.lang.String redis键值对应的数据
     */
    public String lpop(String key) {

        while( true ) {
            try {
                return jedis.lpop(key);
            }
            catch(Exception ex) {
                logger.error("JedisError:" + ex.getMessage());
                reconnect();
            }
        }
    }

    /**
     * 重新连接redis
     * @author Tiger.Wang
     * @date 2019/9/5
     * @param
     * @return void
     * @throws
     */
    public void reconnect() {

        while( true ) {
            try {
                close();
                jedis.connect();
                break;
            }catch(Exception ex) {
                logger.error("JedisError:" + ex.getMessage());
            }
        }
    }

    /**
     * 关闭redis连接
     * @author Tiger.Wang
     * @date 2019/9/5
     */
    public void close() {

        if(jedis.isConnected()){
            jedis.disconnect();
        }

        logger.info("---------- redis关闭成功 ----------");
    }
}
