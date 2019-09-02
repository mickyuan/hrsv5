package hrds.agent.job.biz.core.dfstage;

import hrds.agent.job.biz.bean.StageStatusInfo;
import hrds.agent.job.biz.constant.FileFormatConstant;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.constant.RunStatusConstant;
import hrds.agent.job.biz.constant.StageConstant;
import hrds.agent.job.biz.core.AbstractJobStage;
import hrds.agent.job.biz.core.dfstage.fileparser.CsvFile2Parquet;
import hrds.agent.job.biz.utils.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * ClassName: DFCalIncrementStageImpl <br/>
 * Function: 数据文件采集，数据卸数阶段实现. <br/>
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author 13616
 * Version 1.0
 * Since JDK 1.8
 **/
public class DFUnloadDataStageImpl extends AbstractJobStage {

    private final static Logger LOGGER = LoggerFactory.getLogger(DFUnloadDataStageImpl.class);

    private final String jobId;
    private final String inputFile;
    private final String tableName;
    private final List<String> columns;
    private final List<String> columnTypes;
    private final String startDate;

    private final String outputFile;
    private long rowCount = 0;

    /**
     * 数据文件采集，数据卸数阶段实现.
     * @author   13616
     * @date     2019/8/7 11:49
     *
     * @param jobId 作业编号
     * @param inputFile 数据文件，输入文件
     * @param tableName 名称，可以是数据库的表名或者只是名称
     * @param columns   字段
     * @param columnTypes   字段类型
     * @param startDate 拉链用的开始日期，该数据会额外写入文件为一列
     */
    public DFUnloadDataStageImpl(String jobId, String inputFile, String tableName, List<String> columns, List<String> columnTypes, String startDate) {
        this.jobId = jobId;
        this.inputFile = inputFile;
        this.tableName = tableName;
        this.columns = columns;
        this.columnTypes = columnTypes;
        this.startDate = startDate;
        //TODO 输出文件待定
        this.outputFile = new File(inputFile).getParent() + File.separatorChar + tableName + "." +
                FileFormatConstant.PARQUET.getMessage();
    }

    @Override
    public StageStatusInfo handleStage() {

        StageStatusInfo status = new StageStatusInfo();
        status.setJobId(jobId);
        status.setStageNameCode(StageConstant.UNLOADDATA.getCode());
        status.setStartDate(DateUtil.getLocalDateByChar8());
        status.setStartTime(DateUtil.getLocalTimeByChar6());

        CsvFile2Parquet csvFile2Parquet = new CsvFile2Parquet.Builder(tableName, StringUtils.join(columns,
                JobConstant.COLUMN_SEPARATOR), StringUtils.join(columnTypes, JobConstant.COLUMN_TYPE_SEPARATOR),
                startDate).builCsvFile(inputFile, outputFile).build();

        rowCount = csvFile2Parquet.handle();

        LOGGER.info("------------------数据文件卸数阶段结束------------------");
        status.setEndDate(DateUtil.getLocalDateByChar8());
        status.setEndTime(DateUtil.getLocalTimeByChar6());
        status.setStatusCode(RunStatusConstant.SUCCEED.getCode());

        return status;
    }

    public String getOutputFile() {

        return outputFile;
    }

    public long getRowCount() {

        return rowCount;
    }
}
