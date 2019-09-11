package hrds.control.task.helper;

import fd.ng.core.utils.StringUtil;
import hrds.commons.codes.Dispatch_Frequency;
import hrds.commons.entity.Etl_job_cur;
import hrds.commons.entity.Etl_job_def;
import hrds.commons.entity.Etl_para;
import hrds.commons.exception.AppSystemException;
import hrds.control.beans.EtlJobDefBean;
import hrds.control.utils.DateUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * ClassName: TaskJobHelper
 * Description: 用于对作业的程序目录、日志目录、程序参数等做检查和转换
 * Author: Tiger.Wang
 * Date: 2019/9/3 17:11
 * Since: JDK 1.8
 **/
public class TaskJobHelper {
    private static final Logger logger = LogManager.getLogger();

    private TaskJobHelper() {};

    public static void transformProgramDir(EtlJobDefBean job) {

        String proDic = job.getPro_dic();
        String[] nameArr = TaskJobHelper.getPara(job, proDic);
        String name = "";
        for(int i = 0; i < nameArr.length; i++) {
            name = name + nameArr[i];
        }
        job.setPro_dic(name);
    }

    public static void transformLogDir(EtlJobDefBean job) {

        String logDic = job.getLog_dic();
        String[] nameArr = TaskJobHelper.getPara(job, logDic);
        String name = "";
        for(int i = 0; i < nameArr.length; i++) {
            name = name + nameArr[i];
        }
        job.setLog_dic(name);
    }

    public static void transformProName(EtlJobDefBean job) {

        String proName = job.getPro_name();
        String[] nameArr = TaskJobHelper.getPara(job, proName);
        String name = "";
        for(int i = 0; i < nameArr.length; i++) {
            name = name + nameArr[i];
        }
        job.setPro_name(name);
    }

    public static void transformProPara(EtlJobDefBean job) {

        String proPara = job.getPro_para();
        String[] nameArr = TaskJobHelper.getPara(job, proPara);
        String name = "";
        for(int i = 0; i < nameArr.length; i++) {
            if( 0 == i ) {
                name = nameArr[i];
            }
            else {
                name = name + "@" + nameArr[i];
            }
        }
        job.setPro_para(name);
    }

    public static String[] getPara(EtlJobDefBean job, String dirPath) {

        String[] arr = null;
        String resultst = "";
        String strs = "";
        Stack<Character> stack = new Stack<Character>();
        if(StringUtil.isEmpty(dirPath)){
            arr = dirPath.split("@");
        }
        else {
            arr = new String[] {};
        }
        String[] newArr = new String[arr.length];
        Calendar cal = Calendar.getInstance();

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
                    if( "#".equals(String.valueOf(x)) ) {
                        Date date = TaskJobHelper.getDateByString(job.getCurr_bath_date());
                        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
                        String p = strs.toLowerCase();
                        //TODO 此处的SQL为：select para_cd from etl_para where para_cd=#{_parameter}，有何意义
//                        String p = processMapper.queryPara(strs.toLowerCase());
                        if( "#txdate".equals(p) ) {
                            arr[i] = arr[i].replace("#{txdate}", sd.format(date));
                        }
                        if( "#date".equals(p) ) {
                            arr[i] = arr[i].replace("#{date}", sd.format(new Date()));
                        }
                        if( "#txdate_pre".equals(p) ) {
                            cal.setTime(date);
                            cal.add(Calendar.DAY_OF_MONTH, -1);
                            arr[i] = arr[i].replace("#{txdate_pre}", sd.format(cal.getTime()));
                        }
                        if( "#txdate_next".equals(p) ) {
                            cal.setTime(date);
                            cal.add(Calendar.DAY_OF_MONTH, 1);
                            arr[i] = arr[i].replace("#{txdate_next}", sd.format(cal.getTime()));
                        }
                    }
                    else if( "!".equals(String.valueOf(x)) ) {
                        Date date = TaskJobHelper.getDateByString(job.getCurr_bath_date());
                        Optional<Etl_para> etlParaOptional = TaskSqlHelper.getEtlParameterVal("", strs);
                        if(!etlParaOptional.isPresent()) {
                            logger.warn("找不到对应变量[{}]", strs);
                            return arr;
                        }
                        String strsc = strs.substring(1, strs.length());
                        Etl_para etlPara = etlParaOptional.get();
                        String paraVal = etlPara.getPara_val();
                        /**添加参数可以包含日期的自定义格式******************开始*/
                        SimpleDateFormat sd = new SimpleDateFormat(paraVal);
                        if( "!txdate".startsWith(strs) ) {
                            arr[i] = arr[i].replace("!{" + strsc + "}", sd.format(date));
                        }
                        if( "!date".startsWith(strs) ) {
                            arr[i] = arr[i].replace("!{" + strsc + "}", sd.format(new Date()));
                        }
                        if( "!txdate_pre".startsWith(strs) ) {
                            cal.setTime(date);
                            cal.add(Calendar.DAY_OF_MONTH, -1);
                            arr[i] = arr[i].replace("!{" + strsc + "}", sd.format(cal.getTime()));
                        }
                        if( "!txdate_next".startsWith(strs) ) {
                            cal.setTime(date);
                            cal.add(Calendar.DAY_OF_MONTH, 1);
                            arr[i] = arr[i].replace("!{" + strsc + "}", sd.format(cal.getTime()));
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
            ca = null;
        }

        return newArr;
    }

    /**
     * 将String日期转成Date型日期
     *
     * @param strDate
     * 			String型日期
     * @return
     * 		Date型日期
     */
    public static Date getDateByString(String strDate) {

        //TODO 此处要用jdk8的LocalDate来进行日期计算，此处没仔细看
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

    /**
     * 根据当前的跑批日期，计算下一次跑批日期。
     * @author Tiger.Wang
     * @date 2019/9/11
     * @param currBathDate  当前跑批日期
     * @return java.lang.String 下一次跑批日期
     */
    public static LocalDate getNextBathDate(LocalDate currBathDate) {

        //TODO 此处要用jdk8的LocalDate来进行日期计算，此处没仔细看
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = currBathDate.atStartOfDay(zoneId);
        Calendar cal = Calendar.getInstance();
        cal.setTime(Date.from(zdt.toInstant()));
        cal.add(Calendar.DAY_OF_MONTH, 1);

        return LocalDateTime.ofInstant(cal.getTime().toInstant(), zoneId).toLocalDate();
    }

    /**
     * 计算下次执行日期
     * @param currBathDate 当次执行日期
     * @param freqType 执行频率
     * @return
     * 		下次执行日期
     */
    public static String getNextExecuteDate(LocalDate currBathDate, String freqType) {

        return getExecuteDate(currBathDate, freqType, 1);
    }

    /**
     * 计算前次执行日期
     *
     * @param currBathDate
     * 			当次执行日期
     * @param freqType
     * 			执行频率
     * @return
     * 		前次执行日期
     */
    public static String getPreExecuteDate(LocalDate currBathDate, String freqType) {

        return getExecuteDate(currBathDate, freqType, -1);
    }

    private static String getExecuteDate(LocalDate currBathDate, String freqType, int offset) {

        //TODO 此处要用jdk8的LocalDate来进行日期计算，此处没仔细看
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = currBathDate.atStartOfDay(zoneId);
        Calendar cal = Calendar.getInstance();
        cal.setTime(Date.from(zdt.toInstant()));

        if(Dispatch_Frequency.DAILY.getCode().equals(freqType)) {   // 每日调度
            cal.add(Calendar.DATE, offset);
        }else if(Dispatch_Frequency.MONTHLY.getCode().equals(freqType)) { // 每月调度
            cal.add(Calendar.MONTH, offset);
        }else if(Dispatch_Frequency.WEEKLY.equals(freqType)) {    // 每周调度
            cal.add(Calendar.WEEK_OF_MONTH, offset);
        }else if(Dispatch_Frequency.YEARLY.equals(freqType)) {    // 每年调度
            cal.add(Calendar.YEAR, offset);
        }else {
            throw new AppSystemException("不支持的频率类型：" + freqType);
        }

        return DateUtil.getStringDate(cal.getTime());
    }

    /**
     * 将String日期转成Date型日期
     *
     * @param strDateTime
     * 			String型日期
     * @return
     * 		Date型日期
     */
    public static Date getTtypeExecuteTime(String strDateTime) {

        //TODO 此处要用jdk8的LocalDate来进行日期计算，此处没仔细看
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);
        Date retValue = null;
        try {
            retValue = dateFormat.parse(strDateTime);
            Calendar cal = Calendar.getInstance();
            cal.setTime(retValue);
            cal.add(Calendar.DAY_OF_MONTH, 1);
            retValue = cal.getTime();
        }
        catch(ParseException e) {
            e.printStackTrace();
        }
        return retValue;
    }

    /**
     * 将String时间转成Date型日期
     *
     * @param strDate
     * 			作业调度时间
     * @return
     * 		Date型日期
     */
    public static Date getZtypeExecuteTime(String strDate) {

        //TODO 此处要用jdk8的LocalDate来进行日期计算，此处没仔细看
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);
        Date retValue = null;
        try {
            retValue = dateFormat.parse(strDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(retValue);
            retValue = cal.getTime();
        }
        catch(ParseException e) {
            e.printStackTrace();
        }
        return retValue;
    }

    /**
     * 将Etl_job_def复制成Etl_job
     *
     * @author Tiger.Wang
     * @date 2019/9/4
     * @param etlJobDef 复制源
     * @return hrds.entity.Etl_job
     */
    public static Etl_job_cur etlJobDefCopy2EltJob(Etl_job_def etlJobDef) {

        Etl_job_cur etlJob = new Etl_job_cur();
        try {
            BeanUtils.copyProperties(etlJob, etlJobDef);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new AppSystemException("将Etl_job_def转换为Etl_job发生异常：" + e.getMessage());
        }

        return etlJob;
    }
}
