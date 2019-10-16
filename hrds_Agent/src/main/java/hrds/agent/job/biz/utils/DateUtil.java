package hrds.agent.job.biz.utils;

import fd.ng.core.annotation.DocClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@DocClass(desc = "处理日期的工具", author = "WangZhengcheng")
public class DateUtil {

	public final static String DATE_CHAR8_FORMAT = "yyyyMMdd";
	public final static String TIME_CHAR6_FORMAT = "HHmmss";

	public static String getLocalDateByChar8() {
		DateTimeFormatter sdf = DateTimeFormatter.ofPattern(DATE_CHAR8_FORMAT);
		return sdf.format(LocalDateTime.now());
	}

	public static String getLocalTimeByChar6() {
		DateTimeFormatter sdf = DateTimeFormatter.ofPattern(TIME_CHAR6_FORMAT);
		return sdf.format(LocalDateTime.now());
	}

	public static void main(String[] args) {
		System.out.println(getLocalDateByChar8());
	}
}
