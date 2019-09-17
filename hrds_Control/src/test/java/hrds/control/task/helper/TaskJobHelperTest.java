package hrds.control.task.helper;

import hrds.commons.codes.Dispatch_Frequency;
import hrds.control.utils.DateUtil;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * ClassName: TaskJobHelperTest
 * Description: 用于测试TaskJobHelper类
 * Author: Tiger.Wang
 * Date: 2019/9/3 17:11
 * Since: JDK 1.8
 **/
public class TaskJobHelperTest {

	@Test
	public void transformProgramDir() {
	}

	@Test
	public void transformLogDir() {
	}

	@Test
	public void transformProName() {
	}

	@Test
	public void transformProPara() {
	}

	@Test
	public void getNextBathDate() {

		String currBathDate = "2019-09-17";
		LocalDate date = LocalDate.parse(currBathDate, DateUtil.DATE);

		date = TaskJobHelper.getNextBathDate(date);

		assertEquals("测试获取下一跑批日期是否正确",
				0, date.compareTo(LocalDate.now().plusDays(1)));
	}

	@Test
	public void getNextExecuteDate() {

		String currBathDate = "2019-09-17";
		LocalDate date = LocalDate.parse(currBathDate, DateUtil.DATE);

		assertEquals("在调度频率为[每日（DAILY）]的情况下，测试计算的下一次执行日期是否正确",
				"2019-09-18", TaskJobHelper.getNextExecuteDate(date, Dispatch_Frequency.DAILY.getCode()));

		assertEquals("在调度频率为[每周（WEEKLY）]的情况下，测试计算的下一次执行日期是否正确",
				"2019-09-24", TaskJobHelper.getNextExecuteDate(date, Dispatch_Frequency.WEEKLY.getCode()));

		assertEquals("在调度频率为[每月（MONTHLY）]的情况下，测试计算的下一次执行日期是否正确",
				"2019-10-17", TaskJobHelper.getNextExecuteDate(date, Dispatch_Frequency.MONTHLY.getCode()));

		assertEquals("在调度频率为[每年（YEARLY）]的情况下，测试计算的下一次执行日期是否正确",
				"2020-09-17", TaskJobHelper.getNextExecuteDate(date, Dispatch_Frequency.YEARLY.getCode()));
	}

	@Test
	public void getPreExecuteDate() {

		String currBathDate = "2019-09-17";
		LocalDate date = LocalDate.parse(currBathDate, DateUtil.DATE);

		assertEquals("在调度频率为[每日（DAILY）]的情况下，测试计算的上一次执行日期是否正确",
				"2019-09-16", TaskJobHelper.getPreExecuteDate(date, Dispatch_Frequency.DAILY.getCode()));

		assertEquals("在调度频率为[每周（WEEKLY）]的情况下，测试计算的上一次执行日期是否正确",
				"2019-09-10", TaskJobHelper.getPreExecuteDate(date, Dispatch_Frequency.WEEKLY.getCode()));

		assertEquals("在调度频率为[每月（MONTHLY）]的情况下，测试计算的上一次执行日期是否正确",
				"2019-08-17", TaskJobHelper.getPreExecuteDate(date, Dispatch_Frequency.MONTHLY.getCode()));

		assertEquals("在调度频率为[每年（YEARLY）]的情况下，测试计算的上一次执行日期是否正确",
				"2018-09-17", TaskJobHelper.getPreExecuteDate(date, Dispatch_Frequency.YEARLY.getCode()));
	}

	@Test
	public void getExecuteTimeByTPlus1() {

		String currBathDateTime = "2019-09-17 16:00:00";

		LocalDateTime dateTime1 = TaskJobHelper.getExecuteTimeByTPlus1(currBathDateTime);
		LocalDateTime dateTime2 = LocalDateTime.parse(currBathDateTime, DateUtil.DATETIME).plusDays(1);

		assertEquals("测试作业为T+1调度时，计算出的下一执行日期时间是否正确",
				0, dateTime1.compareTo(dateTime2));
	}
}