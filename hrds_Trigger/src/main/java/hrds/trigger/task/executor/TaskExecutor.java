package hrds.trigger.task.executor;

import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import hrds.commons.codes.Pro_Type;
import hrds.commons.entity.Etl_job_cur;
import hrds.commons.exception.AppSystemException;
import hrds.trigger.task.helper.TaskSqlHelper;
import hrds.trigger.utils.ProcessUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.pentaho.di.repository.kdr.KettleDatabaseRepositoryMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;

/**
 * ClassName: TaskExecutor<br>
 * Description: trigger程序核心逻辑的执行类，用于解析作业参数并且执行作业。<br>
 * Author: Tiger.Wang<br>
 * Date: 2019/10/23 14:54<br>
 * Since: JDK 1.8
 **/
public class TaskExecutor {

    private static final Logger logger = LogManager.getLogger();

    private static final String PARAM_SEPARATOR = "@"; //参数分隔符
    private static final String LOGNAME_SEPARATOR = "_"; //日志文件名分隔符
    private static final String LOG_SUFFIX = ".log"; //日志文件后缀
    private static final String ERRORLOG_SUFFIX = "error.log"; //错误日志文件后缀
    private static final String YARN_JOBSUFFIX = "_hyren";//yarn作业名后缀

    private static final String CRAWL_FLAG = "CRAWL";//这个值在代码项中没有
    private static final String SHELL_COMMANDLINE = "bash";
    private static final String YARN_COMMANDLINE = "sh";
    private static final String JAVA_COMMANDLINE = "java";
    private static final String PERL_COMMANDLINE = "perl";
    private static final String PYTHON_COMMANDLINE = "python";
    /**
     * 进程执行正常结束标识
     */
    public final static int PROGRAM_DONE_FLAG = 0;
    /**
     * 进程执行异常结束标识
     */
    public final static int PROGRAM_ERROR_FLAG = 1;

    /**
     * trigger程序核心逻辑的执行方法，根据不同的程序类型构造执行指令并执行程序。主要逻辑点：<br>
     * 1、在程序类型为DBTRAN、DBJOB时，使用Kettle来完成对数据的基础转换，该种方式无法获得进程编号；<br>
     * 2、在程序类型不为DBTRAN、DBJOB时，使用适合各种的执行指令来启动作业，完成作业的执行；<br>
     * 3、在程序类型不为DBTRAN、DBJOB时，更新作业的进程编号到数据库中。
     *
     * @param etlJobCur 含义：表示一个已登记的作业，并且该作业需要马上执行。
     *                  取值范围：不能为null。
     * @return hrds.commons.entity.Etl_job_cur
     * 含义：表示一个执行完毕的作业，该对象包含该作业的运行结果状态等。
     * 取值范围：不会为null。
     * @throws IOException          虚拟机无法以进程方式启动作业时，抛出该异常。
     * @throws InterruptedException 虚拟机无法使用sleep函数休眠时，抛出该异常。
     * @author Tiger.Wang
     * @date 2019/10/25
     */
    public static Etl_job_cur executeEtlJob(final Etl_job_cur etlJobCur) throws IOException,
            InterruptedException {

        String etlSysCode = etlJobCur.getEtl_sys_cd();
        String etlJob = etlJobCur.getEtl_job();
        String proType = etlJobCur.getPro_type();
        //1、在程序类型为DBTRAN、DBJOB时，使用Kettle来完成对数据的基础转换，该种方式无法使用进程编号；
        if (Pro_Type.DBTRAN.getCode().equals(proType)) {
            //调用资源库中的Trans
            try {
                etlJobCur.setJob_return_val(runTran(etlJobCur));
            } catch (KettleException e) {
                etlJobCur.setJob_return_val(TaskExecutor.PROGRAM_ERROR_FLAG);
                logger.warn("{} execute error!" + e, etlJob);
            }
        } else if (Pro_Type.DBJOB.getCode().equals(proType)) {
            //调用资源库中的Job
            try {
                etlJobCur.setJob_return_val(runJob(etlJobCur));
            } catch (KettleException e) {
                etlJobCur.setJob_return_val(TaskExecutor.PROGRAM_ERROR_FLAG);
                logger.warn("{} execute error!" + e, etlJob);
            }
        } else {
            //2、在程序类型不为DBTRAN、DBJOB时，使用适合各种的执行指令来启动作业，完成作业的执行；
            String[] cmds = TaskExecutor.getCommend(etlJobCur);
            logger.info("{} 作业开始执行，指令为 {}", etlJob, Arrays.toString(cmds));
            Process process = Runtime.getRuntime().exec(cmds);
            if (Pro_Type.Yarn.getCode().equals(proType)) {
                //根据Name获取appid，并存入数据库中1
                String yarnName = cmds[2];
                String appId = ProcessUtil.getYarnAppID(yarnName);
                etlJobCur.setJob_process_id(appId);
                //3、在程序类型不为DBTRAN、DBJOB时，更新作业的进程编号到数据库中。
                TaskSqlHelper.updateEtlJobProcessId(appId, etlSysCode, etlJob);
                String logDirc = TaskExecutor.getLogPath(etlJob, etlJobCur.getLog_dic(),
                        etlJobCur.getCurr_bath_date(), false);
                String logDircErr = TaskExecutor.getLogPath(etlJob, etlJobCur.getLog_dic(),
                        etlJobCur.getCurr_bath_date(), true);
                new WatchThread(process.getInputStream(), logDirc).start();
                new WatchThread(process.getErrorStream(), logDircErr).start();
                //作业为完成（成功、失败、结束）都退出，只有作业是运行中的这里会继续获取状态 TODO 此处有改动
//				if(status == 1 || status == 0 || status ==2) {
//				}
                etlJobCur.setJob_return_val(ProcessUtil.getStatusOnYarn(appId));
            } else {
                String appId = String.valueOf(ProcessUtil.getPid(process));
                etlJobCur.setJob_process_id(appId);
                //3、在程序类型不为DBTRAN、DBJOB时，更新作业的进程编号到数据库中。
                TaskSqlHelper.updateEtlJobProcessId(appId, etlSysCode, etlJob);
                String logDirc = TaskExecutor.getLogPath(etlJob, etlJobCur.getLog_dic(),
                        etlJobCur.getCurr_bath_date(), false);
                String logDircErr = TaskExecutor.getLogPath(etlJob, etlJobCur.getLog_dic(),
                        etlJobCur.getCurr_bath_date(), true);
                new WatchThread(process.getInputStream(), logDirc).start();
                new WatchThread(process.getErrorStream(), logDircErr).start();
                etlJobCur.setJob_return_val(process.waitFor());
            }
        }

        logger.info("{} 任务执行结束，返回值 {}", etlJob, etlJobCur.getJob_return_val());

        return etlJobCur;
    }

    /**
     * 使用Kettle来完成对数据的ETL操作，此方法对数据进行基本的数据转换(transformation的方式)。
     * 注意逻辑点：<br>
     * 1、验证参数个数，并解析参数，若参数错误，则认为该作业执行失败；<br>
     * 2、为Trans方式设置参数并且执行；<br>
     * 3、等待作业执行完毕，返回执行结果。
     *
     * @param etlJobCur 含义：表示一个已登记的作业，并且该作业需要马上执行。
     *                  取值范围：不能为null。
     * @return int
     * 含义：作业执行结果。
     * 取值范围：PROGRAM_ERROR_FLAG/PROGRAM_DONE_FLAG变量值。
     * @throws KettleException Kettle环境无法初始化时抛出该异常。
     * @author Tiger.Wang
     * @date 2019/10/25
     */
    private static int runTran(final Etl_job_cur etlJobCur) throws KettleException {

        //1、验证参数个数，并解析参数，若参数错误，则认为该作业执行失败；
        String jobName = etlJobCur.getEtl_job();
        String proName = etlJobCur.getPro_name();
        String[] propArr = getParaArr(etlJobCur.getPro_para());

        // 判断是否已经设置资源库信息
        if (propArr.length < 4) {
            logger.warn(jobName + " Repository info is not enough!");
            return TaskExecutor.PROGRAM_ERROR_FLAG;
        }

        KettleDatabaseRepository repository =
                TaskExecutor.initAndGetKettle(propArr[0], propArr[1], propArr[2], propArr[3]);

        //获取作业的目录"/test/lyy"
        String proDic = getDic(etlJobCur.getPro_dic());
        RepositoryDirectoryInterface directoryInterface = repository.findDirectory(proDic);

        //2、为Trans方式设置参数并且执行；
        TransMeta transformationMeta = ((Repository) repository)
                .loadTransformation(proName, directoryInterface, null, true, null);
        Trans trans = new Trans(transformationMeta);
        if (propArr.length > 4) {
            String[] parms = new String[propArr.length - 4];
            System.arraycopy(propArr, propArr.length - 3, parms, 0, propArr.length - 4);
            trans.execute(parms);
        } else {
            trans.execute(null);
        }

        //3、等待作业执行完毕，返回执行结果。
        trans.waitUntilFinished();//等待直到数据结束
        if (trans.getErrors() > 0) {
            logger.warn(jobName + " execute error.");
            return TaskExecutor.PROGRAM_ERROR_FLAG;
        } else {
            logger.info(jobName + " execute success.");
            return TaskExecutor.PROGRAM_DONE_FLAG;
        }
    }

    /**
     * 使用Kettle来完成对数据的ETL操作，此方法对数据完成整个工作流的控制(job的方式)。
     * 注意逻辑点：<br>
     * 1、验证参数个数，并解析参数，若参数错误，则认为该作业执行失败；<br>
     * 2、为Job方式设置参数并且执行；<br>
     * 3、等待作业执行完毕，返回执行结果。
     *
     * @param etlJobCur 含义：表示一个已登记的作业，并且该作业需要马上执行。
     *                  取值范围：不能为null。
     * @return int
     * 含义：作业执行结果。
     * 取值范围：PROGRAM_ERROR_FLAG/PROGRAM_DONE_FLAG变量值。
     * @throws KettleException Kettle环境无法初始化时抛出该异常。
     * @author Tiger.Wang
     * @date 2019/10/25
     */
    private static int runJob(final Etl_job_cur etlJobCur) throws KettleException {

        //1、验证参数个数，并解析参数，若参数错误，则认为该作业执行失败；
        String jobName = etlJobCur.getEtl_job();
        String proName = etlJobCur.getPro_name();
        String[] propArr = getParaArr(etlJobCur.getPro_para());

        // 判断是否已经设置资源库信息
        if (propArr.length < 4) {
            logger.warn(jobName + " Repository info is not enough!");
            return PROGRAM_ERROR_FLAG;
        }

        KettleDatabaseRepository repository =
                TaskExecutor.initAndGetKettle(propArr[0], propArr[1], propArr[2], propArr[3]);

        String proDic = getDic(etlJobCur.getPro_dic());
        //获取作业的目录"/test/lyy"
        RepositoryDirectoryInterface directoryInterface = repository.findDirectory(proDic);
        repository.loadRepositoryDirectoryTree(directoryInterface);

        //2、为Job方式设置参数并且执行；
        JobMeta jobMeta = repository.loadJob(proName, directoryInterface, null, null);
        //从资源库中加载一个job
        //Job(Repository repository,JobMeta jobMeta),KettleDatabaseRepository是Repository的实现类
        Job job = new Job(repository, jobMeta);
        for (int i = 4; i < propArr.length; ) {
            job.setParameterValue(propArr[i], propArr[i + 1]);
            i = i + 2;
        }
        job.start();

        //3、等待作业执行完毕，返回执行结果。
        job.waitUntilFinished();//等待直到数据结束
        if (job.getErrors() > 0) {
            logger.warn(jobName + " execute error.");
            return TaskExecutor.PROGRAM_ERROR_FLAG;
        } else {
            logger.info(jobName + " execute success.");
            return TaskExecutor.PROGRAM_DONE_FLAG;
        }
    }

    /**
     * 初始化Kettle环境，设置数据库参数。主要逻辑点：<br>
     * 1、初始化Kettle环境，设置数据库参数。<br>
     *
     * @param schema   含义：目标数据库名称。
     *                 取值范围：不能为null。
     * @param url      含义：目标数据库jdbcUrl。
     *                 取值范围：不能为null。
     * @param userName 含义：目标数据库用户名。
     *                 取值范围：不能为null。
     * @param passWord 含义：目标数据库密码。
     *                 取值范围：不能为null。
     * @return org.pentaho.di.repository.kdr.KettleDatabaseRepository
     * 含义：表示Kettle的一个db资源库对象。
     * 取值范围：。
     * @throws KettleException Kettle环境无法初始化时抛出该异常。
     * @author Tiger.Wang
     * @date 2019/10/25
     */
    private static KettleDatabaseRepository initAndGetKettle(String schema, String url,
                                                             String userName, String passWord)
            throws KettleException {

        //1、初始化Kettle环境，设置数据库参数；
        KettleEnvironment.init();
        //创建DB资源库
        KettleDatabaseRepository repository = new KettleDatabaseRepository();
        //定义一个数据库类型及参数（数据库连接名称，资源库类型，连接方式，IP，数据库名，端口，用户名，密码）
        DatabaseMeta databaseMeta = new DatabaseMeta("kettle", "Oracle",
                "jdbc", url, schema, "1521", userName, passWord);

        //选择资源库(ID，名称，描述)
        KettleDatabaseRepositoryMeta kettleDatabaseRepositoryMeta =
                new KettleDatabaseRepositoryMeta("kettle", "kettle",
                        "Transformation description", databaseMeta);
        repository.init(kettleDatabaseRepositoryMeta);
        //连接资源库（用户名，密码）
        repository.connect("admin", "admin");

        return repository;
    }

    /**
     * 根据不同的程序类型，构建各自的命令行执行指令，若参数的日期格式不正确，
     * 则会抛出DateTimeParseException异常。<br>
     * 1、根据不同的程序类型，构建各自的命令行执行指令。
     *
     * @param etlJobCur 含义：表示一个已登记的作业，并且该作业需要马上执行。
     *                  取值范围：不能为null。
     * @return java.lang.String[]
     * 含义：构建完成后的参数数组。
     * 取值范围：不会为null。
     * @author Tiger.Wang
     * @date 2019/10/25
     */
    private static String[] getCommend(final Etl_job_cur etlJobCur) {

        String jobName = etlJobCur.getEtl_job();
        //目的是验证日期格式
        LocalDate.parse(etlJobCur.getCurr_bath_date(), DateUtil.DATE_DEFAULT);
        String currBathDate = etlJobCur.getCurr_bath_date();
        String proType = etlJobCur.getPro_type();
        String proName = etlJobCur.getPro_name();
        String proDic = TaskExecutor.getDic(etlJobCur.getPro_dic());
        String logDic = TaskExecutor.getDic(etlJobCur.getLog_dic());
        String[] propArr = TaskExecutor.getParaArr(etlJobCur.getPro_para());
        String[] commands;
        //1、根据不同的程序类型，构建各自的命令行执行指令。
        if (Pro_Type.SHELL.getCode().equals(proType)) {
            commands = new String[propArr.length + 2];
            commands[0] = SHELL_COMMANDLINE;
            commands[1] = proDic + proName;
            System.arraycopy(propArr, 0, commands, 2, propArr.length);
        } else if (Pro_Type.Yarn.getCode().equals(proType)) {
            //添加yarn的作业调度方式，目前我们yarn的调度方式只支持shell的凡是调度，现不支持直接使用jar的方式进行调度
            commands = new String[propArr.length + 2];
            commands[0] = YARN_COMMANDLINE;
            commands[1] = proDic + proName;
            commands[2] = UUID.randomUUID() + YARN_JOBSUFFIX;
            //这里需要生产一个唯一的yarn的name名称，shell必须要求第一个参数是作业名称，强制要求，否则我们获取不到
            System.arraycopy(propArr, 0, commands, 3, propArr.length);
        } else if (Pro_Type.JAVA.getCode().equals(proType)) {
            commands = new String[propArr.length + 2];
            commands[0] = JAVA_COMMANDLINE;
            commands[1] = proDic + proName;
            System.arraycopy(propArr, 0, commands, 2, propArr.length);
        } else if (Pro_Type.BAT.getCode().equals(proType)) {
            commands = new String[propArr.length + 2];
            commands[0] = proDic + proName;
            System.arraycopy(propArr, 0, commands, 1, propArr.length);
        } else if (Pro_Type.PERL.getCode().equals(proType)) {
            commands = new String[propArr.length + 2];
            commands[0] = PERL_COMMANDLINE;
            commands[1] = proDic + proName;
            System.arraycopy(propArr, 0, commands, 2, propArr.length);
        } else if (Pro_Type.PYTHON.getCode().equals(proType)) {
            commands = new String[propArr.length + 2];
            commands[0] = PYTHON_COMMANDLINE;
            commands[1] = proDic + proName;
            System.arraycopy(propArr, 0, commands, 2, propArr.length);
        } else if (CRAWL_FLAG.equals(proType)) {
            commands = new String[propArr.length + 1];
            commands[0] = proDic + proName;
            System.arraycopy(propArr, 0, commands, 1, propArr.length);
        } else {
            throw new AppSystemException("暂不支持的程序类型：" + proType);
        }

//        if (!StringUtil.isEmpty(logDic)) {
//            commands[commands.length - 2] = TaskExecutor.getLogPath(jobName, logDic,
//                    currBathDate, false);
//            commands[commands.length - 1] = TaskExecutor.getLogPath(jobName, logDic,
//                    currBathDate, true);
//        }

        return commands;
    }

    /**
     * 根据传入的目录地址参数，转换目录地址，注意，此方法在参入的目录地址不存在的情况下会创建目录。<br>
     * 1、若参入的目录地址不存在的情况下会创建目录。
     *
     * @param dicPath 含义：目录地址。
     *                取值范围：不能为null。
     * @return java.lang.String
     * 含义：目录地址。
     * 取值范围：不会为null。
     * @author Tiger.Wang
     * @date 2019/10/25
     */
    private static String getDic(final String dicPath) {

        //1、若参入的目录地址不存在的情况下会创建目录。
        File file = new File(dicPath);
        if (!file.exists()) {
            try {
                file.mkdirs();
            } catch (Exception e) {
                throw new AppSystemException("无法创建目录：" + dicPath);
            }
        }

        return dicPath;
    }

    /**
     * 根据传入的参数字符串，对参数进行解析，转换为参数数组。<br>
     * 1、以固定分隔符分割字符串。
     *
     * @param para 含义：传入的参数字符串。
     *             取值范围：不能为null。
     * @return java.lang.String[]
     * 含义：参数数组。
     * 取值范围：不会为null。
     * @author Tiger.Wang
     * @date 2019/10/25
     */
    private static String[] getParaArr(final String para) {

        if (StringUtil.isEmpty(para)) return new String[]{};

        //1、以固定分隔符分割字符串。
        return para.split(PARAM_SEPARATOR);
    }

    /**
     * 根据传入的参数，构建日志地址。<br>
     * 1、根据传入的参数，构建日志地址。
     *
     * @param proName    含义：程序名称。
     *                   取值范围：不能为null。
     * @param logDic     含义：日志所在目录地址。
     *                   取值范围：不能为null。
     * @param currDate   含义：当然跑批日期。
     *                   取值范围：不能为null。
     * @param isErrorLog 含义：是否为错误日志文件。
     *                   取值范围：不能为null。
     * @return java.lang.String
     * 含义：日志文件地址。
     * 取值范围：不会为null。
     * @author Tiger.Wang
     * @date 2019/10/25
     */
    private static String getLogPath(final String proName, final String logDic,
                                     final String currDate, final boolean isErrorLog) {

        //1、根据传入的参数，构建日志地址。
        String logPath = logDic + File.separator + proName + LOGNAME_SEPARATOR + currDate;

        if (isErrorLog) {
            logPath = logPath + ERRORLOG_SUFFIX;
        } else {
            logPath = logPath + LOG_SUFFIX;
        }

        return logPath;
    }
}
