package hrds.commons.utils;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.FileNameUtils;
import hrds.commons.codes.DataSourceType;
import hrds.commons.exception.BusinessException;

import java.io.File;

@DocClass(desc = "路径处理工具类", author = "BY-HLL", createdate = "2019/10/17 0017")
public class PathUtil {

    private static final String PREFIX = PropertyParaValue.getString("pathprefix", "/hrds");
    private static final String ISL = DataSourceType.ISL.getCode();//贴源区_01
    private static final String DCL = DataSourceType.DCL.getCode();//贴源区
    private static final String DPL = DataSourceType.DPL.getCode();//加工区
    private static final String DML = DataSourceType.DML.getCode();//集市区
    private static final String SFL = DataSourceType.SFL.getCode();//系统区
    private static final String AML = DataSourceType.AML.getCode();//AI模型层
    private static final String DQC = DataSourceType.DQC.getCode();//数据质量层
    private static final String UDL = DataSourceType.UDL.getCode();//用户自定义层

    public static final String ISLRELEASE = PREFIX + File.separator + ISL + File.separator;//贴源区_01
    public static final String DCLRELEASE = PREFIX + File.separator + DCL + File.separator;//贴源区
    public static final String DPLRELEASE = PREFIX + File.separator + DPL + File.separator;//加工区
    public static final String DMLRELEASE = PREFIX + File.separator + DML + File.separator;//集市区
    public static final String SFLRELEASE = PREFIX + File.separator + SFL + File.separator;//系统区
    public static final String AMLRELEASE = PREFIX + File.separator + AML + File.separator;//AI模型层
    public static final String DQCRELEASE = PREFIX + File.separator + DQC + File.separator;//数据质量层
    public static final String UDLRELEASE = PREFIX + File.separator + UDL + File.separator;//用户自定义层

    public static final String TMPDIR = PREFIX + File.separator + "TMP";//临时文件产生区
    public static final String WAREHOUSE = PropertyParaValue.getString("hivehouse",
            "/hive/warehouse");
    public static final String GPROOTDIR = PropertyParaValue.getString("gpfdistRootDir",
            "/home/hyshf/HRDS");

    @Method(desc = "将本地路径转换为HDFS路径", logicStep = "将本地路径转换为HDFS路径")
    @Param(name = "localPath", desc = "本地文件路径", range = "本地文件的全路径")
    @Return(desc = "HDFSPath", range = "HDFS的数据存储路径")
    public static String convertLocalPathToHDFSPath(String localPath) {
        localPath = FileNameUtils.normalize(localPath).replace("\\", "/");
        String HDFSPath;
        try {
            HDFSPath = localPath.substring(localPath.lastIndexOf(PREFIX));
        } catch (StringIndexOutOfBoundsException e) {
            throw new BusinessException("本地文件路径转HDFS路径失败! localPath=" + localPath);
        }
        return HDFSPath;
    }
}
