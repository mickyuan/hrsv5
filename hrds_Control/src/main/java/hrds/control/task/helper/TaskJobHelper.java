package hrds.control.task.helper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Stack;

import fd.ng.core.utils.ArrayUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fd.ng.core.utils.StringUtil;
import hrds.commons.codes.Dispatch_Frequency;
import hrds.commons.entity.Etl_para;
import hrds.commons.exception.AppSystemException;
import hrds.control.beans.EtlJobDefBean;
import hrds.control.utils.DateUtil;

/**
 * ClassName: TaskJobHelper
 * Description: 用于对作业的程序目录、日志目录、程序参数等做检查和转换
 * Author: Tiger.Wang
 * Date: 2019/9/3 17:11
 * Since: JDK 1.8
 **/
public class TaskJobHelper {
    private static final Logger logger = LogManager.getLogger();

    private static final String PARASEPARATOR = "@";    //参数分隔符

    private TaskJobHelper() {};

    public static void transformProgramDir(EtlJobDefBean job) {

        String proDic = job.getPro_dic();
        String[] nameArr = TaskJobHelper.getPara(job.getCurr_bath_date(), proDic);
        String name = "";
        for(String s : nameArr) {
            name = name + s;
        }
        job.setPro_dic(name);
    }

    public static void transformLogDir(EtlJobDefBean job) {

        String logDic = job.getLog_dic();
        String[] nameArr = TaskJobHelper.getPara(job.getCurr_bath_date(), logDic);
        String name = "";
        for(String s : nameArr) {
            name = name + s;
        }
        job.setLog_dic(name);
    }

    public static void transformProName(EtlJobDefBean job) {

        String proName = job.getPro_name();
        String[] nameArr = TaskJobHelper.getPara(job.getCurr_bath_date(), proName);
        String name = "";
        for(String s : nameArr) {
            name = name + s;
        }
        job.setPro_name(name);
    }

    public static void transformProPara(EtlJobDefBean job) {

        String proPara = job.getPro_para();
        String[] nameArr = TaskJobHelper.getPara(job.getCurr_bath_date(), proPara);
        String name = "";
        for(int i = 0; i < nameArr.length; i++) {
            if( 0 == i ) {
                name = nameArr[0];
            }
            else {
                name = name + PARASEPARATOR + nameArr[i];
            }
        }

        job.setPro_para(name);
    }

    //TODO 看着头大
    //FIXME 要有方法的详细注释
    private static String[] getPara(String currBathDate, String para) {
//FIXME 这个方法会被反复调用？能不能全局处理一次？
        String[] arr;
        String resultst = "";
        String strs = "";
        Stack<Character> stack = new Stack<>();
        if(StringUtil.isNotEmpty(para)) {
            arr = para.split(PARASEPARATOR);
        }
        else {
            arr = new String[] {};//FIXME 应该直接返回吧。而且，应该使用 ArrayUtil.EMPTY_STRING_ARRAY
        }
        String[] newArr = new String[arr.length];

        for(int i = 0; i < arr.length; i++) {
            char[] ca = arr[i].toCharArray();
            for(int a = 0; a < ca.length; a++) {
                stack.push(ca[a]);
                if( stack.peek().equals('}') ) {
                    while( !stack.peek().equals('{') ) {
                        resultst += stack.pop();
                    }
                    stack.pop();
                    resultst += stack.pop();
                    stack.clear();
                    char[] charArry = resultst.toCharArray();
                    for(int num = charArry.length - 1; num >= 1; num--) {
                        strs += charArry[num];
                    } ;

                    char x = strs.charAt(0);
                    if( "#".equals(String.valueOf(x)) ) {//FIXME 为什么不用字符比较！
                        LocalDate date = LocalDate.parse(currBathDate, DateUtil.DATE);

                        String paraCd = TaskSqlHelper.getParaByPara(strs.toLowerCase()).getPara_cd();
                        if( "#txdate".equals(paraCd) ) {
                            arr[i] = arr[i].replace("#{txdate}", date.format(DateUtil.DATE_DEFAULT));
                        }
                        if( "#date".equals(paraCd) ) {
                            arr[i] = arr[i].replace("#{date}", date.format(DateUtil.DATE_DEFAULT));
                        }
                        if( "#txdate_pre".equals(paraCd) ) {
                            arr[i] = arr[i].replace("#{txdate_pre}",
                                    date.plus(-1, ChronoUnit.DAYS).format(DateUtil.DATE_DEFAULT));
                        }
                        if( "#txdate_next".equals(paraCd) ) {
                            arr[i] = arr[i].replace("#{txdate_next}",
                                    date.plus(1, ChronoUnit.DAYS).format(DateUtil.DATE_DEFAULT));
                        }
                    }
                    else if( "!".equals(String.valueOf(x)) ) {//FIXME 为什么不用字符比较！
                        //TODO 这里etlSysCd为""，意味着这是默认系统参数？
                        Optional<Etl_para> etlParaOptional = TaskSqlHelper.getEtlParameterVal("", strs);
                        if(!etlParaOptional.isPresent()) {//FIXME 为什么打印警告而不是退出
                            logger.warn("找不到对应变量[{}]", strs);
                            return arr;
                        }
                        String strsc = strs.substring(1);
                        Etl_para etlPara = etlParaOptional.get();
                        String paraVal = etlPara.getPara_val();
                        /**添加参数可以包含日期的自定义格式******************开始*/
                        LocalDate date = LocalDate.parse(currBathDate, DateUtil.DATE);
                        DateTimeFormatter pattern = DateTimeFormatter.ofPattern(paraVal);
                        if( "!txdate".startsWith(strs) ) {
                            arr[i] = arr[i].replace("!{" + strsc + "}", date.format(pattern));
                        }
                        if( "!date".startsWith(strs) ) {
                            arr[i] = arr[i].replace("!{" + strsc + "}", date.format(pattern));
                        }
                        if( "!txdate_pre".startsWith(strs) ) {
                            arr[i] = arr[i].replace("!{" + strsc + "}",
                                    date.plus(-1, ChronoUnit.DAYS).format(pattern));
                        }
                        if( "!txdate_next".startsWith(strs) ) {
                            arr[i] = arr[i].replace("!{" + strsc + "}",
                                    date.plus(1, ChronoUnit.DAYS).format(pattern));
                        }
                        /**添加参数可以包含日期的自定义格式******************结束*/
                        else {
                            arr[i] = arr[i].replace("!{" + strsc + "}", paraVal);
                        }
                    }

                    resultst = "";
                    strs = "";
                }
            }
            newArr[i] = arr[i];
        }

        return newArr;
    }

    /**
     * 根据当前的跑批日期，计算下一次跑批日期。
     * @author Tiger.Wang
     * @date 2019/9/11
     * @param currBathDate  当前跑批日期
     * @return java.lang.String 下一次跑批日期
     */
    public static LocalDate getNextBathDate(LocalDate currBathDate) {

        return currBathDate.plus(1, ChronoUnit.DAYS);
    }

    /**
     * 计算下次执行日期。
     * @param currBathDate 当次执行日期
     * @param freqType 执行频率
     * @return 下次执行日期（yyyy-MM-dd）
     */
    public static String getNextExecuteDate(LocalDate currBathDate, String freqType) {

        return getExecuteDate(currBathDate, freqType, 1);
    }

    /**
     * 计算上次执行日期。
     * @param currBathDate 当次执行日期
     * @param freqType 执行频率
     * @return 上一次执行日期（yyyy-MM-dd）
     */
    public static String getPreExecuteDate(LocalDate currBathDate, String freqType) {

        return getExecuteDate(currBathDate, freqType, -1);
    }

    /**
     * 根据调度频率类型及偏移量，使用指定的日期计算出下一次执行日期。
     * @author Tiger.Wang
     * @date 2019/9/17
     * @param currBathDate  当前跑批日期
     * @param freqType  频率类型
     * @param offset    偏移量
     * @return java.lang.String 日期字符串（yyyy-MM-dd）
     */
    private static String getExecuteDate(LocalDate currBathDate, String freqType, int offset) {

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

        return currBathDate.format(DateUtil.DATE);
    }

    /**
     * 在作业调度类型为T+1时使用，通过指定日期时间，计算出下一次执行日期时间
     * @author Tiger.Wang
     * @date 2019/9/17
     * @param strDateTime   当前日期时间（yyyy-MM-dd HH:mm:ss）
     * @return java.time.LocalDateTime
     */
    public static LocalDateTime getExecuteTimeByTPlus1(String strDateTime) {

        return LocalDateTime.parse(strDateTime, DateUtil.DATETIME).plus(1, ChronoUnit.DAYS);
    }
}
