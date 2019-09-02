package hrds.agent.job.biz.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ClassName: DateUtil <br/>
 * Function: 处理日期的工具. <br/>
 * Date: 2019/7/31 10:31 <br/>
 * <p>
 * Author 13616
 * Version 1.0
 * Since JDK 1.8
 **/
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

    public static void main(String[] args){
        System.out.println(getLocalDateByChar8());
    }
}
