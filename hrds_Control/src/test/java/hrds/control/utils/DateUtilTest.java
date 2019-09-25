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
	public void timestamp2DateTime() {

		LocalDateTime localDateTime = LocalDateTime.now();
		long dateTime = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

		assertTrue("测试timestamp转换为日期时间对象是否正确",
				DateUtil.timestamp2DateTime(dateTime).compareTo(localDateTime) == 0);
	}
}