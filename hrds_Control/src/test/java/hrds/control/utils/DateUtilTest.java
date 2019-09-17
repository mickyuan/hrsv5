package hrds.control.utils;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.Assert.*;

/**
 * @ClassName: DateUtilTest
 * @Description: 用于测试DateUtil类
 * @Author: Tiger.Wang
 * @Date: 2019/8/30 11:41
 **/
public class DateUtilTest {

	@Test
	public void parseStr2DateTime() {

		String dateTime = "2019-09-17 16:00:00";

		LocalDateTime dateTime1 = DateUtil.parseStr2DateTime(dateTime);
		LocalDateTime dateTime2 = LocalDateTime.parse(dateTime, DateUtil.DATETIME);

		assertTrue("测试将19位日期时间字符串转对象是否正确", dateTime1.compareTo(dateTime2) == 0);
	}

	@Test
	public void getNowDateTime2Milli() {

		long dateTime1 = DateUtil.getNowDateTime2Milli();
		long dateTime2 = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

		assertTrue("测试获取本地系统日期时间毫秒数是否一致", dateTime1 == dateTime2);
	}

	@Test
	public void timestamp2DateTime() {

		LocalDateTime localDateTime = LocalDateTime.now();
		long dateTime = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

		assertTrue("测试timestamp转换为日期时间对象是否正确",
				DateUtil.timestamp2DateTime(dateTime).compareTo(localDateTime) == 0);
	}
}