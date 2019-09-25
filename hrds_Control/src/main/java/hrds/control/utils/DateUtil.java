package hrds.control.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @ClassName: DateUtil
 * @Description: 日期时间处理工具类
 * @Author: Tiger.Wang
 * @Date: 2019/8/30 11:41
 **/
public class DateUtil {

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
