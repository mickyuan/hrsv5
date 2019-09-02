package hrds.commons.utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @ClassName: DateUtil
 * @Description:
 * @Author: Tiger.Wang
 * @Date: 2019/8/30 11:41
 **/
public class DateUtil {

	public static final DateTimeFormatter DATE_DEFAULT = DateTimeFormatter.ofPattern("yyyyMMdd");
	public static final DateTimeFormatter TIME_DEFAULT = DateTimeFormatter.ofPattern("HHmmss");

	/**
	 * 8位字符的字符串转换为Date对象
	 * @author Tiger.Wang
	 * @date 2019/8/30
	 * @param dateStr	8位字符的字符串
	 * @return java.time.LocalDate	日期对象
	 */
	public static LocalDate parseStr2DateWith8Char(String dateStr) {

		return LocalDate.parse(dateStr, DATE_DEFAULT);
	}

	/**
	 * 6位字符的字符串转换为Time对象
	 * @author Tiger.Wang
	 * @date 2019/8/30
	 * @param timeStr	6位字符的字符串
	 * @return java.time.LocalTime	Time对象
	 */
	public static LocalTime parseStr2TimeWith6Char(String timeStr) {

		return LocalTime.parse(timeStr, TIME_DEFAULT);
	}
}
