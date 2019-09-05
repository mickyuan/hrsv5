package hrds.control.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @ClassName: DateUtil
 * @Description:
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

	public static String getStringDate(Date date) {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(date);
	}

	public static Date getDateByString(String strDate) {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		Date retValue = null;
		try {
			retValue = dateFormat.parse(strDate);
		}
		catch(ParseException e) {
			e.printStackTrace();
		}
		return retValue;
	}

	public static void main(String[] args) {
		String dateTime = "2019-09-03 12:10:01";
		LocalDateTime dateTime1 = DateUtil.parseStr2DateTime(dateTime);
		System.out.printf(LocalDateTime.now().compareTo(dateTime1) + "\n");
		System.out.println(dateTime1.format(DATETIME).toString());
	}
}
