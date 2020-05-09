package hrds.control.task.helper;

import fd.ng.core.utils.DateUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.codes.Dispatch_Frequency;
import hrds.commons.codes.ParamType;
import hrds.commons.entity.Etl_para;
import hrds.control.task.TaskManagerTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;

/**
 * ClassName: TaskJobHelperTest
 * Description: 用于测试TaskJobHelper类
 * Author: Tiger.Wang
 * Date: 2019/9/3 17:11
 * Since: JDK 1.8
 **/
public class TaskJobHelperTest {
	private static final Logger logger = LogManager.getLogger();

	@BeforeClass
	public static void before() {

		try(DatabaseWrapper db = new DatabaseWrapper()) {

			Etl_para etlPara = new Etl_para();
			etlPara.setEtl_sys_cd(TaskManagerTest.syscode);
			etlPara.setPara_cd("#txdate");
			etlPara.setPara_type(ParamType.LuJing.getCode());
			etlPara.setPara_val("对于#号来说，值没意义");
			etlPara.add(db);

			etlPara.setPara_cd("#date");
			etlPara.add(db);

			etlPara.setPara_cd("#txdate_pre");
			etlPara.add(db);

			etlPara.setPara_cd("#txdate_next");
			etlPara.add(db);

			etlPara.setPara_cd("!txdate");
			etlPara.setEtl_sys_cd("");  //含义为系统默认参数
			etlPara.setPara_val("yyyy年MM月dd日");
			etlPara.add(db);

			etlPara.setPara_cd("!date");
			etlPara.setPara_val("yyyy-MM-dd");
			etlPara.add(db);

			etlPara.setPara_cd("!txdate_pre");
			etlPara.setPara_val("yyyy-MM-dd");
			etlPara.add(db);

			etlPara.setPara_cd("!txdate_next");
			etlPara.setPara_val("yyyyMMdd");
			etlPara.add(db);

			etlPara.setPara_cd("!test");
			etlPara.setPara_val("anyone");
			etlPara.add(db);

			etlPara.setPara_cd("!execute_target");
			etlPara.setPara_val("HelloWord");
			etlPara.add(db);

			etlPara.setPara_cd("!program_para");
			etlPara.setPara_type(ParamType.CanShu.getCode());
			etlPara.setPara_val("false");
			etlPara.add(db);

			SqlOperator.commitTransaction(db);
		}
	}

	@AfterClass
	public static void after() {

		try(DatabaseWrapper db = new DatabaseWrapper()) {

			int num = SqlOperator.execute(db, "DELETE FROM etl_para WHERE etl_sys_cd = ? OR etl_sys_cd = ''",
					TaskManagerTest.syscode);

			SqlOperator.commitTransaction(db);

			logger.info("清理etl_resource表{}条数据", num);
		}
		TaskSqlHelper.closeDbConnector();
	}

	@Test
	public void transformDirOrName() {

		String currBathDate = "20190915";
		String etl_sys_cd = "";
		String dirPrefix1 = "/data01/#{txdate}/#{date}_prefix1/#{txdate_pre}/#{txdate_next}";
		String dir1 = TaskJobHelper.transformDirOrName(currBathDate, etl_sys_cd,dirPrefix1);
		assertEquals("测试参数变量前缀为[#]时，参数占位符是否处理正确",
				"/data01/20190915/" + LocalDate.now().format(DateUtil.DATE_DEFAULT) +
						"_prefix1/20190914/20190916",	dir1);

		String dirPrefix2 = "/data01/!{txdate}/!{date}_prefix2/!{txdate_pre}/!{txdate_next}/!{test}";
		String dir2 = TaskJobHelper.transformDirOrName(currBathDate,etl_sys_cd, dirPrefix2);
		assertEquals("测试参数变量前缀为[!]时，参数占位符是否处理正确",
				"/data01/2019年09月15日/" + LocalDate.now().format(
						DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "_prefix2/2019-09-14" +
						"/20190916/anyone", dir2);

		String programName = "!{execute_target}.jar";
		String program = TaskJobHelper.transformDirOrName(currBathDate, etl_sys_cd,programName);

		assertEquals("测试参数变量前缀为[!]时，自定义占位符是否处理正确", "HelloWord.jar", program);
	}

	@Test
	public void transformProgramPara() {

		String currBathDate = "20190915";
	String etl_sys_cd = "";
		String programPara = "#{txdate} 110 true !{program_para}";
		String para = TaskJobHelper.transformProgramPara(currBathDate,etl_sys_cd, programPara);

		assertEquals("测试程序参数的占位符是否处理正确", "20190915 110 true false", para);
	}

	@Test
	public void getNextBathDate() {

		String currBathDate = LocalDate.now().format(DateUtil.DATE_DEFAULT);
		String date = TaskJobHelper.getNextBathDate(currBathDate);

		assertEquals("测试获取下一跑批日期是否正确",
				LocalDate.now().plusDays(1).format(DateUtil.DATE_DEFAULT), date);
	}

	@Test
	public void getNextExecuteDate() {

		String currBathDate = "20190917";

		assertEquals("在调度频率为[每日（DAILY）]的情况下，测试计算的下一次执行日期是否正确",
				"20190918",
				TaskJobHelper.getNextExecuteDate(currBathDate, Dispatch_Frequency.DAILY.getCode()));

		assertEquals("在调度频率为[每周（WEEKLY）]的情况下，测试计算的下一次执行日期是否正确",
				"20190924",
				TaskJobHelper.getNextExecuteDate(currBathDate, Dispatch_Frequency.WEEKLY.getCode()));

		assertEquals("在调度频率为[每月（MONTHLY）]的情况下，测试计算的下一次执行日期是否正确",
				"20191017",
				TaskJobHelper.getNextExecuteDate(currBathDate, Dispatch_Frequency.MONTHLY.getCode()));

		assertEquals("在调度频率为[每年（YEARLY）]的情况下，测试计算的下一次执行日期是否正确",
				"20200917",
				TaskJobHelper.getNextExecuteDate(currBathDate, Dispatch_Frequency.YEARLY.getCode()));
	}

	@Test
	public void getPreExecuteDate() {

		String currBathDate = "20190917";

		assertEquals("在调度频率为[每日（DAILY）]的情况下，测试计算的上一次执行日期是否正确",
				"20190916",
				TaskJobHelper.getPreExecuteDate(currBathDate, Dispatch_Frequency.DAILY.getCode()));

		assertEquals("在调度频率为[每周（WEEKLY）]的情况下，测试计算的上一次执行日期是否正确",
				"20190910",
				TaskJobHelper.getPreExecuteDate(currBathDate, Dispatch_Frequency.WEEKLY.getCode()));

		assertEquals("在调度频率为[每月（MONTHLY）]的情况下，测试计算的上一次执行日期是否正确",
				"20190817",
				TaskJobHelper.getPreExecuteDate(currBathDate, Dispatch_Frequency.MONTHLY.getCode()));

		assertEquals("在调度频率为[每年（YEARLY）]的情况下，测试计算的上一次执行日期是否正确",
				"20180917",
				TaskJobHelper.getPreExecuteDate(currBathDate, Dispatch_Frequency.YEARLY.getCode()));
	}

	@Test
	public void getExecuteTimeByTPlus1() {

		String currBathDateTime = "20190917 160000";

		LocalDateTime dateTime1 = TaskJobHelper.getExecuteTimeByTPlus1(currBathDateTime);
		LocalDateTime dateTime2 = LocalDateTime.parse(currBathDateTime, DateUtil.DATETIME_DEFAULT).plusDays(1);

		assertEquals("测试作业为T+1调度时，计算出的下一执行日期时间是否正确",
				0, dateTime1.compareTo(dateTime2));
	}
}