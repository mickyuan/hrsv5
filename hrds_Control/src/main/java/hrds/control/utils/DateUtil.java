package hrds.control.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @ClassName: DateUtil
 * @Description: 日期时间处理工具类
 * @Author: Tiger.Wang
 * @Date: 2019/8/30 11:41
 **/
public class DateUtil {

	public static final DateTimeFormatter DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	public static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss");
	public static final DateTimeFormatter DATE_DEFAULT = DateTimeFormatter.ofPattern("yyyyMMdd");
	public static final DateTimeFormatter TIME_DEFAULT = DateTimeFormatter.ofPattern("HHmmss");

	/**
	 * 将19位的日期时间字符串转换为LocalDateTime对象
	 * @author Tiger.Wang
	 * @date 2019/9/2
	 * @param dateTimeStr	日期时间字符串（yyyy-MM-dd HH:mm:ss）
	 * @return java.time.LocalDateTime
	 */
	public static LocalDateTime parseStr2DateTime(String dateTimeStr) {

		return LocalDateTime.parse(dateTimeStr, DATETIME);
	}

	/**
	 * 获取当前系统毫秒数。
	 * @author Tiger.Wang
	 * @date 2019/9/17
	 * @return long 当前系统毫秒数
	 */
	public static long getNowDateTime2Milli() {

		//FIXME 为何不用System.currentTimeMillis()。而且也不需要这个方法，直接用就行了
		return LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	/**
	 * timestamp转换为LocalDateTime对象
	 * @author Tiger.Wang
	 * @date 2019/9/17
	 * @param timestamp 时间戳
	 * @return java.time.LocalDateTime
	 */
	public static LocalDateTime timestamp2DateTime(long timestamp) {

		return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
	}
}
