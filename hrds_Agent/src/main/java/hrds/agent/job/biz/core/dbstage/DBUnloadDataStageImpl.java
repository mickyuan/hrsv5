package hrds.agent.job.biz.core.dbstage;

import hrds.agent.job.biz.bean.ColumnCleanResult;
import hrds.agent.job.biz.bean.DBConfigBean;
import hrds.agent.job.biz.bean.JobInfo;
import hrds.agent.job.biz.bean.StageStatusInfo;
import hrds.agent.job.biz.constant.RunStatusConstant;
import hrds.agent.job.biz.constant.StageConstant;
import hrds.agent.job.biz.core.AbstractJobStage;
import hrds.agent.job.biz.core.dbstage.dbdialect.strategy.DataBaseDialectStrategy;
import hrds.agent.job.biz.core.dbstage.dbdialect.strategy.DialectStrategyFactory;
import hrds.agent.job.biz.core.dbstage.service.CollectPage;
import hrds.agent.job.biz.utils.DateUtil;
import hrds.agent.job.biz.utils.FileUtil;
import hrds.agent.job.biz.utils.JobUtil;
import hrds.agent.job.biz.utils.SQLUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * ClassName: DBUnloadDataStageImpl <br/>
 * Function: 数据卸数阶段  <br/>
 * Reason: 数据库直连采集
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public class DBUnloadDataStageImpl extends AbstractJobStage {

    private final static Logger LOGGER = LoggerFactory.getLogger(DBUnloadDataStageImpl.class);

    //数据库连接信息
    private DBConfigBean dbInfo;
    //作业信息
    private JobInfo jobInfo;
    //列类型
    private List<String> columnTypes = new ArrayList<>();
    //本次采集数据量
    private long rowCount = 0;
    //本次采集生成的数据文件的总大小
    private long fileSize = 0;
    //本次采集生成的数据文件
    private String[] fileArr;

    public DBUnloadDataStageImpl(DBConfigBean dbInfo, JobInfo jobInfo) {
        this.dbInfo = dbInfo;
        this.jobInfo = jobInfo;
    }

    /*
    * 1、创建卸数阶段状态信息，更新作业ID,阶段名，阶段开始时间
    * 2、解析作业信息，得到表名和表数据量
    * 3、根据列名和表名获得采集SQL
    * 4、使用工厂模式获得数据库方言策略
    * 5、根据采集线程数，计算每个任务的采集数量
    * 6、构建线程对象CollectPage，放入线程池执行
    * 7、获得结果,用于校验多线程采集的结果和写Meta文件
    * */
    @Override
    public StageStatusInfo handleStage() throws InterruptedException, ExecutionException, SQLException {
        LOGGER.info("------------------数据库直连采集卸数阶段开始------------------");
        //1、创建卸数阶段状态信息，更新作业ID,阶段名，阶段开始时间
        StageStatusInfo statusInfo = new StageStatusInfo();
        statusInfo.setJobId(this.jobInfo.getJobId());
        statusInfo.setStageNameCode(StageConstant.UNLOADDATA.getCode());
        statusInfo.setStartDate(DateUtil.getLocalDateByChar8());
        statusInfo.setStartTime(DateUtil.getLocalTimeByChar6());
        if (jobInfo == null || dbInfo == null) {
            throw new RuntimeException("数据库直连采集,数据库信息和作业信息不能为空");
        }

        //2、解析作业信息，得到表名和表数据量
        String tableName = jobInfo.getTable_name();
        String tableCount = jobInfo.getTable_count();
        List<ColumnCleanResult> columnList = jobInfo.getColumnList();
        Set<String> collectColumnNames = JobUtil.getCollectColumnName(columnList);
        //3、根据列名和表名获得采集SQL
        // TODO 缺少支持自定义SQL
        String collectSQL = SQLUtil.getCollectSQL(tableName, collectColumnNames, dbInfo.getDatabase_type());
        //获得用户提供的用于分页的列
        String pageColumn = jobInfo.getPageColumn();
        //4、使用工厂模式获得数据库方言策略
        DataBaseDialectStrategy strategy = DialectStrategyFactory.getInstance().createDialectStrategy(dbInfo.getDatabase_type());
        //fileResult中是生成的所有数据文件的路径，用于判断卸数阶段结果
        List<String> fileResult = new ArrayList<>();
        //RSResult中是所有分页查询得到的ResultSet，用于写meta文件
        List<ResultSet> RSResult = new ArrayList<>();
        //pageCountResult是本次采集作业每个线程采集到的数据量，用于写meta文件
        List<Long> pageCountResult = new ArrayList<>();
        //5、16+1个线程采集。计算每个任务的采集数量,得到start,end,然后封装成CollectTask对象,目前写死，后期可以放在yml文件中,根据服务器实际情况，修改配置
        //TODO 讨论如何配置线程，海云现在是根据页面预估数+后台配置每个线程数据量，算出来需要多少个线程。也可以改成页面配置线程数，后台分配每个线程的数据量
        int threadCount = 16;
        if (pageColumn == null || pageColumn.trim().isEmpty()) {
            //TODO 用户若未提供该张表用于分页的数据列，后续操作待讨论
        } else {
            //用户提供了该张表用于分页的数据列
            int totalCount = Integer.parseInt(tableCount);
            int pageRow = (int)Math.ceil(totalCount / threadCount) + 1;
            //6、创建固定大小的线程池，执行分页查询(线程池类型和线程数可以后续改造)
            ExecutorService executorService = Executors.newFixedThreadPool(threadCount + 1);
            List<Future<Map<String,Object>>> futures = new ArrayList<>();
            for (int i = 0; i < threadCount; i++) {
                int start = (i * pageRow) + 1;
                int end = (i + 1) * pageRow;
                //传入i(分页页码)，pageRow(每页的数据量)，用于写avro时的行号
                CollectPage page = new CollectPage(this.jobInfo, this.dbInfo, strategy, collectSQL, pageColumn, start, end, i, pageRow);
                Future<Map<String,Object>> future = executorService.submit(page);
                futures.add(future);
            }
            int lastPageStart = (pageRow * threadCount) + 1;
            //最后一个线程的最大条数设为Integer.MAX_VALUE
            int lastPageEnd = Integer.MAX_VALUE;
            CollectPage lastPage = new CollectPage(this.jobInfo, this.dbInfo, strategy, collectSQL, pageColumn, lastPageStart, lastPageEnd, threadCount + 1, pageRow);
            Future<Map<String,Object>> lastFuture = executorService.submit(lastPage);
            futures.add(lastFuture);
            //关闭线程池
            executorService.shutdown();
            while (!executorService.awaitTermination(100, TimeUnit.MILLISECONDS)) {
                System.out.println("线程池正在关闭");
            }
            //7、获得结果,用于校验多线程采集的结果和写Meta文件
            for (Future<Map<String,Object>> future : futures) {
                fileResult.add((String) future.get().get("filePath"));
                RSResult.add((ResultSet) future.get().get("pageData"));
                pageCountResult.add((Long) future.get().get("pageCount"));
            }
        }

        //获得列类型
        ResultSetMetaData metaData = RSResult.get(0).getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            String columnType = getColumnType(metaData.getColumnTypeName(i), metaData.getPrecision(i));
            columnTypes.add(columnType);
        }
        //获得本次采集总数据量
        for(Long pageCount : pageCountResult){
            rowCount += pageCount;
        }
        //获得本次采集生成的数据文件的总大小
        for(String filePath : fileResult){
            //判断文件是否存在，如果某个文件存在，则计算大小，若不存在，记录日志并继续运行
            if(FileUtil.decideFileExist(filePath)){
                long singleFileSize = FileUtil.getFileSize(filePath);
                fileSize += singleFileSize;
            }else{
                LOGGER.error("数据库直连采集" + filePath + "文件不存在");
            }
        }

        //判断本次卸数阶段是否成功
        if (!(fileResult.isEmpty())) {
            for (int i = 0; i < fileResult.size(); i++) {
                if (!FileUtil.decideFileExist(fileResult.get(i))) {
                    //如果某个数据文件在指定的目录下不存在，则卸数阶段失败
                    statusInfo.setStatusCode(RunStatusConstant.FAILED.getCode());
                    //设置错误信息
                    statusInfo.setMessage("名为 : " + fileResult.get(i) + "的数据文件不存在");
                    //设置结束时间
                    statusInfo.setEndDate(DateUtil.getLocalDateByChar8());
                    statusInfo.setEndTime(DateUtil.getLocalTimeByChar6());
                    //记录日志
                    LOGGER.info("------------------数据库直连采集卸数阶段失败------------------");
                    return statusInfo;
                }
            }
            statusInfo.setStatusCode(RunStatusConstant.SUCCEED.getCode());
            statusInfo.setEndDate(DateUtil.getLocalDateByChar8());
            statusInfo.setEndTime(DateUtil.getLocalTimeByChar6());
            fileArr = new String[fileResult.size()];
            fileResult.toArray(fileArr);
            //记录日志
            LOGGER.info("------------------数据库直连采集卸数阶段成功------------------");
            return statusInfo;
        } else {
            statusInfo.setStatusCode(RunStatusConstant.FAILED.getCode());
            statusInfo.setMessage("数据文件全部缺失");
            statusInfo.setEndDate(DateUtil.getLocalDateByChar8());
            statusInfo.setEndTime(DateUtil.getLocalTimeByChar6());
            //记录日志
            LOGGER.info("------------------数据库直连采集卸数阶段失败------------------");
            return statusInfo;
        }
    }

    /** 
    * @Description: 获取数据列类型，用于写meta文件
    * @Param:  
    * @return:  List<String>
    * @Author: WangZhengcheng 
    * @Date: 2019/8/13 
    */ 
    public List<String> getColumnTypes(){
        return this.columnTypes;
    }

    /**
     * @Description: 获取本次数据库直连采集作业采集到的数据总条数，用于写meta文件
     * @Param:
     * @return:  long
     * @Author: WangZhengcheng
     * @Date: 2019/8/13
     */
    public long getRowCount() {
        return rowCount;
    }

    /**
     * @Description: 获取本次数据库直连采集作业采集卸数后生成的数据文件总大小，用于写meta文件
     * @Param:
     * @return:  long 单位是字节
     * @Author: WangZhengcheng
     * @Date: 2019/8/13
     */
    public long getFileSize() {
        return fileSize;
    }

    /**
     * @Description: 获取本次数据库直连采集作业采集卸数后生成的数据文件的路径，用于上传HDFS
     * @Param:
     * @return:  String[]：多线程采集，每个线程写一个数据文件，多线程采集最终会有多个文件
     * @Author: WangZhengcheng
     * @Date: 2019/8/13
     */
    public String[] getFileArr() {
        return fileArr;
    }

    /*
     * 获取列类型
     * */
    /**
    * @Description:  根据列类型和列宽组装最终返回的列类型
    * @Param: columnTypeName：列类型
    * @Param: precision：列宽
    * @return:String    格式：列类型(宽度)
    * @Author: WangZhengcheng
    * @Date: 2019/8/13
    */
    private String getColumnType(String columnTypeName, int precision) {
        if (precision != 0) {
            int index = columnTypeName.indexOf("(");
            if (index != -1) {
                columnTypeName = columnTypeName.substring(0, index);
            }
        }
        return columnTypeName;
    }
}
