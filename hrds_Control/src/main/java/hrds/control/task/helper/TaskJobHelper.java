package hrds.control.task.helper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Stack;

import fd.ng.core.utils.ArrayUtil;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import hrds.commons.codes.Dispatch_Frequency;
import hrds.commons.exception.AppSystemException;

/**
 * ClassName: TaskJobHelper
 * Description: 用于对作业的程序目录、日志目录、程序参数等做检查和转换，
 *              以及计算作业一下次跑批日期会执行日期时间。
 * Author: Tiger.Wang
 * Date: 2019/9/3 17:11
 * Since: JDK 1.8
 **/
public class TaskJobHelper {

    private static final String PARASEPARATOR = "@";    //参数分隔符

    private TaskJobHelper() {}

    /**
     * 对作业的作业目录（程序目录、日志目录）及程序名称参数进行处理，转换参数中的占位符（占位符如：#{txdate}）。
     * @author Tiger.Wang
     * @date 2019/9/25
     * @param currBathDate  当前跑批日期，该日期将会设置到参数中
     * @param dirOrName    原始的目录或名称参数
     * @return java.lang.String 转换后的目录或名称参数
     */
    public static String transformDirOrName(String currBathDate, String dirOrName) {

        String[] params = TaskJobHelper.transformPara(currBathDate, dirOrName);
        StringBuilder newDirOrName = new StringBuilder();

        for(String param : params) {
            newDirOrName.append(param);
        }

        return newDirOrName.toString();
    }

    /**
     * 对作业的作业程序参数进行处理，转换参数中的占位符（占位符如：#{txdate}）。
     * @author Tiger.Wang
     * @date 2019/9/25
     * @param currBathDate  当前跑批日期，该日期将会设置到参数中
     * @param programPara   原始的作业程序参数
     * @return java.lang.String 转换后的作业程序参数
     */
    public static String transformProgramPara(String currBathDate, String programPara) {

        String[] nameArr = TaskJobHelper.transformPara(currBathDate, programPara);

        if(nameArr.length < 1) { return programPara; }

        StringBuilder newProgramPara = new StringBuilder(nameArr[0]);
        for(int i = 1; i < nameArr.length; i++) {
            newProgramPara.append(PARASEPARATOR).append(nameArr[i]);
        }

        return newProgramPara.toString();
    }

    /**
     * 用于处理参数字符串中的占位符，将占位符替换为实际的数据。
     * 注意，若占位符前缀不在[#、!]范围内，则抛出AppSystemException异常。
     * @note    1、所有参数字符串按固定分隔符分割，并且开始识别占位符关键字；
     *          2、占位符前缀有[#、!]，这两种前缀都处理txdate、date、txdate_pre、txdate_next关键字，
     *             不同的是[!]前缀允许处理自定义日期格式及自定义关键字和值。
     * @author Tiger.Wang
     * @date 2019/9/25
     * @param currBathDate  当前批量日期
     * @param para  参数字符串
     * @return java.lang.String[]   无占位符的参数字符数组，因不同的参数类型有不同的字符串组织方式，故而提供数组形式。
     */
    private static String[] transformPara(String currBathDate, String para) {

        if(StringUtil.isEmpty(para)) { return ArrayUtil.EMPTY_STRING_ARRAY; }
        //1、所有参数字符串按固定分隔符分割，并且开始识别占位符关键字；
        String[] arr = para.split(PARASEPARATOR);
        String[] newArr = new String[arr.length];
        Stack<Character> stack = new Stack<>();

        for(int i = 0; i < arr.length; i++) {
            char[] ca = arr[i].toCharArray();
            for (char c : ca) {
                stack.push(c);
                if(!stack.peek().equals('}')) { continue; }

                StringBuilder resultst = new StringBuilder();
                while(!stack.peek().equals('{')) {
                    resultst.append(stack.pop());
                }
                stack.pop();
                resultst.append(stack.pop());
                stack.clear();
                char[] charArry = resultst.toString().toCharArray();
                StringBuilder paraCdSB = new StringBuilder();
                for (int num = charArry.length - 1; num >= 1; num--) {
                    paraCdSB.append(charArry[num]);
                }
                //识别占位符关键字
                char prefix = paraCdSB.charAt(0);
                //2、占位符前缀有[#、!]，这两种前缀都处理txdate、date、txdate_pre、txdate_next关键字，
                // 不同的是[!]前缀允许处理自定义日期关键字。
                //TODO 以下两个代码块感觉是不同的人写的
                if ('#' == prefix) {
                    //隐式验证该参数是否存在
                    String paraCd = TaskSqlHelper.getParaByPara(paraCdSB.toString().toLowerCase());
                    //TODO 此处改动：多个if 改为if else if的结构
                    LocalDate date = LocalDate.parse(currBathDate, DateUtil.DATE_DEFAULT);
                    if ("#txdate".equals(paraCd)) {
                        arr[i] = arr[i].replace("#{txdate}", date.format(DateUtil.DATE_DEFAULT));
                    }else if ("#date".equals(paraCd)) {
                        arr[i] = arr[i].replace("#{date}", LocalDate.now().format(DateUtil.DATE_DEFAULT));
                    }else if ("#txdate_pre".equals(paraCd)) {
                        arr[i] = arr[i].replace("#{txdate_pre}",
                                date.plus(-1, ChronoUnit.DAYS).format(DateUtil.DATE_DEFAULT));
                    }else if ("#txdate_next".equals(paraCd)) {
                        arr[i] = arr[i].replace("#{txdate_next}",
                                date.plus(1, ChronoUnit.DAYS).format(DateUtil.DATE_DEFAULT));
                    }
                } else if ('!' == prefix) {
                    //TODO 这里etlSysCd为""，意味着这是默认系统参数？
                    String paraCd = paraCdSB.toString();

                    String paraVal = TaskSqlHelper.getEtlParameterVal("", paraCd);
                    LocalDate date = LocalDate.parse(currBathDate, DateUtil.DATE_DEFAULT);

                    DateTimeFormatter pattern;
                    String strsc = paraCdSB.substring(1);
                    //添加参数可以包含日期的自定义格式******************开始
                    //TODO 较原版改动：以下代码判断用startsWith改为equals，多个if改为switch
                    switch(paraCd) {
                        case "!txdate":
                            pattern = DateTimeFormatter.ofPattern(paraVal);
                            arr[i] = arr[i].replace("!{" + strsc + "}", date.format(pattern));
                            break;
                        case "!date":
                            pattern = DateTimeFormatter.ofPattern(paraVal);
                            arr[i] = arr[i].replace("!{" + strsc + "}", LocalDate.now().format(pattern));
                            break;
                        case "!txdate_pre":
                            pattern = DateTimeFormatter.ofPattern(paraVal);
                            //TODO 新的日期类不支持负数的偏移量，这里应该对应原版：cal.add(Calendar.DAY_OF_MONTH, -1);
                            arr[i] = arr[i].replace("!{" + strsc + "}",
                                    date.plus(-1, ChronoUnit.DAYS).format(pattern));
                            break;
                        case "!txdate_next":
                            pattern = DateTimeFormatter.ofPattern(paraVal);
                            arr[i] = arr[i].replace("!{" + strsc + "}",
                                    date.plus(1, ChronoUnit.DAYS).format(pattern));
                            break;
                        //添加参数可以包含日期的自定义格式******************结束
                        default:
                            arr[i] = arr[i].replace("!{" + strsc + "}", paraVal);
                            break;
                    }
                }else {
                    throw new AppSystemException("无法识别的参数关键字前缀：" + prefix);
                }
            }
            newArr[i] = arr[i];
        }

        return newArr;
    }

    /**
     * 根据当前的跑批日期，计算作业的下一次跑批日期。
     * @author Tiger.Wang
     * @date 2019/9/11
     * @param currBathDateStr  当前跑批日期   （yyyyMMdd）
     * @return java.lang.String 下一次跑批日期 （yyyyMMdd）
     */
    public static String getNextBathDate(String currBathDateStr) {

        return LocalDate.parse(currBathDateStr, DateUtil.DATE_DEFAULT)
                .plus(1, ChronoUnit.DAYS).format(DateUtil.DATE_DEFAULT);
    }

    /**
     * 计算作业的下次执行日期。
     * @param currBathDate 当次执行日期（yyyyMMdd）
     * @param freqType 执行频率
     * @return 下次执行日期（yyyyMMdd）
     */
    public static String getNextExecuteDate(String currBathDate, String freqType) {

        return getExecuteDate(currBathDate, freqType, 1);
    }

    /**
     * 计算作业的上次执行日期。
     * @param currBathDate 当次执行日期（yyyyMMdd）
     * @param freqType 执行频率
     * @return 上一次执行日期（yyyyMMdd）
     */
    public static String getPreExecuteDate(String currBathDate, String freqType) {

        return getExecuteDate(currBathDate, freqType, -1);
    }

    /**
     * 根据调度频率类型及偏移量，使用指定的日期计算出作业的下一次执行日期。
     * @author Tiger.Wang
     * @date 2019/9/17
     * @param currBathDateStr  当前跑批日期（yyyyMMdd）
     * @param freqType  频率类型
     * @param offset    偏移量
     * @return java.lang.String 日期字符串（yyyyMMdd）
     */
    private static String getExecuteDate(String currBathDateStr, String freqType, int offset) {

        LocalDate currBathDate = LocalDate.parse(currBathDateStr, DateUtil.DATE_DEFAULT);

        if(Dispatch_Frequency.DAILY.getCode().equals(freqType)) {   // 每日调度
            currBathDate = currBathDate.plus(offset, ChronoUnit.DAYS);
        }else if(Dispatch_Frequency.MONTHLY.getCode().equals(freqType)) { // 每月调度
            currBathDate = currBathDate.plus(offset, ChronoUnit.MONTHS);
        }else if(Dispatch_Frequency.WEEKLY.getCode().equals(freqType)) {    // 每周调度
            currBathDate = currBathDate.plus(offset, ChronoUnit.WEEKS);
        }else if(Dispatch_Frequency.YEARLY.getCode().equals(freqType)) {    // 每年调度
            currBathDate = currBathDate.plus(offset, ChronoUnit.YEARS);
        }else {
            throw new AppSystemException("不支持的频率类型：" + freqType);
        }

        return currBathDate.format(DateUtil.DATE_DEFAULT);
    }

    /**
     * 在作业调度类型为T+1时使用，通过指定日期时间，计算出下一次执行日期时间
     * @author Tiger.Wang
     * @date 2019/9/17
     * @param strDateTime   当前日期时间（yyyyMMdd HHmmss）
     * @return java.time.LocalDateTime
     */
    public static LocalDateTime getExecuteTimeByTPlus1(String strDateTime) {

        return LocalDateTime.parse(strDateTime, DateUtil.DATETIME_DEFAULT).plus(1, ChronoUnit.DAYS);
    }
}
