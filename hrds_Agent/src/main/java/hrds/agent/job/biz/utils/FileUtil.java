package hrds.agent.job.biz.utils;

import com.alibaba.fastjson.JSONObject;
import hrds.agent.job.biz.bean.JobInfo;
import hrds.agent.job.biz.bean.TaskInfo;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileUtil {

    public static final String ENCODING = "UTF-8";

    private FileUtil() {
    }

    /**
     * 用于在文件系统创建文件
     *
     * @param filePath 文件路径
     * @param context  文件内容，可为空字符
     * @return boolean    是否创建成功
     * @author 13616
     * @date 2019/7/31 9:52
     */
    public static boolean createFile(String filePath, String context) {

        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            return file.getParentFile().mkdirs();
        }

        try {
            FileUtils.write(file, context, ENCODING);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }

        return true;
    }

    /**
     * 用于在文件系统创建目录
     *
     * @param dirPath 目录地址
     * @return boolean    目录是否创建成功
     * @author 13616
     * @date 2019/7/31 9:55
     */
    public static boolean createDir(String dirPath) {

        File file = new File(dirPath);

        if (!file.exists()) {
            return file.mkdirs();
        }

        return true;
    }

    /**
     * 检查目录是否有自己的可读、可写、可执行权限
     *
     * @param dirPath 目录地址
     * @return boolean    是否有三种权限
     * @author 13616
     * @date 2019/7/31 9:57
     */
    public static boolean checkDirWithAllAuth(String dirPath) {

        File file = new File(dirPath);
        if (!file.getParentFile().canRead() || !file.getParentFile().canWrite() || !file.getParentFile().canExecute()) {
            return false;
        }

        return true;
    }

    /**
     * 根据文件后缀获取某个目录下的所有文件
     *
     * @param dirPath    目录地址
     * @param fileSuffix 文件后缀
     * @return java.util.List<java.io.File>	文件List集合
     * @author 13616
     * @date 2019/7/31 9:58
     */
    public static List<File> getAllFilesByFileSuffix(String dirPath, String fileSuffix) {

        File file_root = new File(dirPath);
        List<File> file_result = new ArrayList<>();
        File[] files = file_root.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isDirectory()) {
                    List<File> deep_files = FileUtil.getAllFilesByFileSuffix(pathname.getAbsolutePath(), fileSuffix);
                    file_result.addAll(deep_files);
                } else if (pathname.isFile() && pathname.getName().endsWith(fileSuffix)) {
                    return true;
                }
                return false;
            }
        });

        file_result.addAll(Arrays.asList(files));
        return file_result;
    }

    /**
     * 将文件内容读为字符串，编码为UTF-8
     *
     * @param file File对象
     * @return java.lang.String    文件内容
     * @author 13616
     * @date 2019/7/31 10:01
     */
    public static String readFile2String(File file) throws IOException, IllegalArgumentException {

        if (file.exists() && file.isFile()) {
            return FileUtils.readFileToString(file, ENCODING);
        } else {
            throw new IllegalArgumentException(file.getName() + "：不是一个可读的文件");
        }
    }

    /**
    * @Description:  根据任务ID获取任务信息
    * @Param: [taskID]
    * @return: com.beyondsoft.agent.beans.TaskInfo
    * @Author: WangZhengcheng
    * @Date: 2019/8/27
    */
    public static TaskInfo getTaskInfoByTaskID(String taskID) throws IOException {
        if (taskID == null) {
            return null;
        }
        List<File> files = getAllFilesByFileSuffix(ProductFileUtil.TASKCONF_ROOT_PATH, ProductFileUtil.TASK_FILE_SUFFIX);
        for (File file : files) {
            String taskStr = FileUtil.readFile2String(file);
            TaskInfo task = JSONObject.parseObject(taskStr, TaskInfo.class);
            if (taskID.equals(task.getTaskId())) {
                return task;
            }
        }
        return null;
    }

    /**
    * @Description:  在具体的作业目录下创建用于存放卸数生成的数据文件的目录
    * @Param: [jobInfo]
    * @return: boolean
    * @Author: WangZhengcheng
    * @Date: 2019/8/27
    */
    public static boolean createDataFileDirByJob(JobInfo jobInfo){
        String jobFilePath = jobInfo.getJobFilePath();
        File file = new File(jobFilePath);
        if (!file.getParentFile().exists()) {
            throw new RuntimeException("作业配置文件路径不存在");
        } else {
            String dataFilePath = file.getParent() + File.separator + "datafile";
            File dataFlie = new File(dataFilePath);
            if (!dataFlie.exists()) {
                return dataFlie.mkdirs();
            }
        }
        return true;
    }

    /**
     * 根据文件路径判断文件是否存在
     *
     * @param filePath    文件路径
     * @return boolean true : exist, false : not exist
     */
    public static boolean decideFileExist(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return true;
        }else{
            return false;
        }
    }

    public static long getFileSize(String filePath) {

        return new File(filePath).length();
    }
}
