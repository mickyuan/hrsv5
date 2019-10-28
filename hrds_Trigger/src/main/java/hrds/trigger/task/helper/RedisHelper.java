package hrds.trigger.task.helper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hrds.trigger.constans.TriggerConfigure;
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

    /**
     * RedisHelper类构造器。<br>
     * 1.初始化类变量。
     * @author Tiger.Wang
     * @date 2019/10/9
     */
    private RedisHelper() {

        //1.初始化类变量。
        this.redisIp = TriggerConfigure.RedisConfig.redisIp;
        this.redisPort = TriggerConfigure.RedisConfig.redisPort;
        this.timeout = TriggerConfigure.RedisConfig.timeout;

        this.jedis = new Jedis(redisIp, redisPort, timeout);

        logger.info("---------- redis构建成功 ----------");
    }

    /**
     * 获得RedisHelper实例。<br>
     * 1.返回RedisHelper实例。
     * @author Tiger.Wang
     * @date 2019/10/9
     * @return hrds.control.task.helper.RedisHelper
     *          含义：RedisHelper类实例。
     *          取值范围：不会为null。
     */
    public static RedisHelper getInstance() {

        //1.返回RedisHelper实例。
        return INSTANCE;
    }

    /**
     * 根据key，将内容追加到redis队列后。注意，
     * 该方法若push失败，将会重新连接redis并一直尝试重新rpush。<br>
     * 1.将内容追加到redis队列后。
     * @author Tiger.Wang
     * @date 2019/10/9
     * @param key
     *          含义：redis键值。
     *          取值范围：不能为null。
     * @param content
     *          含义：追加的内容。
     *          取值范围：不能为null。
     */
    public void rpush(String key, String content) {

        //1.将内容追加到redis队列后。
        while( true ) {
            try {
                jedis.rpush(key, content);
                break;
            }
            catch(Exception ex) {
                logger.error("JedisError:" + ex.getMessage());
                reconnect();
            }
        }
    }

    /**
     * 根据键值获取redis中，该键值对应的数据行数。注意，
     * 该方法不会弹出redis中的数据，且若获取数据失败，将会重新连接redis并一直尝试重新llen。<br>
     * 1.获取redis中，该键值对应的数据行数。
     * @author Tiger.Wang
     * @date 2019/10/9
     * @param key
     *          含义：redis键值。
     *          取值范围：不能为null。
     * @return long
     *          含义：redis键值对应的数据行数。
     *          取值范围：long范围内的任意数值。
     */
    public long llen(String key) {

        //1.获取redis中，该键值对应的数据行数。
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
     * 根据键值，将redis中最靠前的数据弹出。<br>
     * 1.弹出redis中最靠前的数据。
     * @author Tiger.Wang
     * @date 2019/10/9
     * @param key
     *          含义：redis键值。
     *          取值范围：不能为null。
     * @return java.lang.String
     *          含义：redis键值对应的数据。
     *          取值范围：任何值。
     */
    public String lpop(String key) {

        //1.弹出redis中最靠前的数据。
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
     * 重新连接redis。<br>
     * 1.重新连接redis。
     * @author Tiger.Wang
     * @date 2019/10/9
     */
    private void reconnect() {

        //1.重新连接redis。
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
     * 关闭redis连接。<br>
     * 1.关闭redis连接。
     * @author Tiger.Wang
     * @date 2019/10/9
     */
    public void close() {

        //1.关闭redis连接。
        if(jedis.isConnected()){
            jedis.disconnect();
        }

        logger.info("---------- redis关闭成功 ----------");
    }
}
