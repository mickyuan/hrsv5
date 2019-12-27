package hrds.agent.job.biz.core;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.utils.FileNameUtils;
import fd.ng.core.utils.MD5Util;
import fd.ng.core.utils.StringUtil;
import hrds.agent.job.biz.bean.FileCollectParamBean;
import hrds.agent.job.biz.bean.JobStatusInfo;
import hrds.agent.job.biz.bean.MetaInfoBean;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.core.filecollectstage.FileCollectUnloadDataStageImpl;
import hrds.agent.job.biz.utils.JobStatusInfoUtil;
import hrds.agent.trans.biz.unstructuredfilecollect.FileCollectJob;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.File_source;
import hrds.commons.utils.Constant;
import hrds.commons.utils.FileTypeUtil;
import hrds.commons.utils.MapDBHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentMap;

@DocClass(desc = "文件采集作业实现类", author = "zxz", createdate = "2019/10/28 15:15")
public class FileCollectJobImpl implements JobInterface {
	//打印日志
	private static final Log log = LogFactory.getLog(FtpCollectJobImpl.class);
	//文件采集需要的参数实体bean
	private FileCollectParamBean fileCollectParamBean;
	//源文件设置表
	private File_source file_source;


	/**
	 * 非结构化文件采集的作业实现类构造方法.
	 *
	 * @param fileCollectParamBean FileCollectParamBean
	 *                             含义：文件采集需要的参数实体bean
	 *                             取值范围：所有这张表不能为空的字段的值必须有，为空则会抛异常
	 * @param file_source          List<File_source>
	 *                             含义：源文件设置表
	 *                             取值范围：所有这个实体不能为空的字段的值必须有，为空则会抛异常
	 */
	public FileCollectJobImpl(FileCollectParamBean fileCollectParamBean, File_source file_source) {
		fileCollectParamBean.setFile_source_id(file_source.getFile_source_id().toString());
		fileCollectParamBean.setFile_source_path(file_source.getFile_source_path());
		this.fileCollectParamBean = fileCollectParamBean;
		this.file_source = file_source;
	}

	@Method(desc = "文件采集作业执行的方法",
			logicStep = "1.设置作业ID" +
					"2.设置作业ID、开始日期和开始时间" +
					"3.创建mapDB对象，用于检测文件夹下文件是否被采集过" +
					"4.遍历所有需要采集的文件夹" +
					"5.获取所有需要采集的文件的后缀名" +
					"6.获取文件夹下需要采集的变化的文件和新增的文件" +
					"7.构建责任链，串起每个阶段" +
					"8.按照顺序从第一个阶段开始执行作业")
	@Override
	public JobStatusInfo runJob() {
		//1.获取文件采集的id
		String fcs_id = fileCollectParamBean.getFcs_id();
		//获取源文件夹id
		String file_source_id = fileCollectParamBean.getFile_source_id();
		//在进行文件采集时，维护一个消费生成队列
		if (FileCollectJob.mapQueue.get(file_source_id) == null) {
			ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(10);
			FileCollectJob.mapQueue.put(file_source_id, queue);
		}
		String statusFilePath = Constant.JOBINFOPATH + fcs_id
				+ File.separator + file_source_id + File.separator + Constant.JOBFILENAME;
		//JobStatusInfo对象，表示一个作业的状态
		JobStatusInfo jobStatus = JobStatusInfoUtil.getStartJobStatusInfo(statusFilePath, file_source_id);
		//3.创建mapDB对象，用于检测文件夹下文件是否被采集过
		try (MapDBHelper mapDBHelper = new MapDBHelper(Constant.MAPDBPATH + fcs_id + File.separator
				+ file_source_id, file_source_id + ".db")) {
			ConcurrentMap<String, String> fileNameHTreeMap = mapDBHelper.htMap(file_source_id, 25 * 12);
			List<String> newFile = new ArrayList<>();
			List<String> changeFileList = new ArrayList<>();
			//4.获取所有需要采集的文件夹
			String fileSourcePath = file_source.getFile_source_path();
			log.info("file_source_path: " + fileSourcePath);
			//5.获取所有需要采集的文件的后缀名
			List<String> fileTypeList = getFileSuffixList(file_source);
			//6.获取文件夹下需要采集的变化的文件和新增的文件
			getNewFileListByFileType(fileSourcePath, fileTypeList, fileNameHTreeMap, newFile
					, file_source.getIs_other());
			getChangeFileListByFileType(fileSourcePath, fileTypeList, fileNameHTreeMap, changeFileList
					, file_source.getIs_other());

			//构建每个阶段具体的实现类，按照顺序执行(卸数,数据加载,数据登记)
			JobStageInterface unloadData = new FileCollectUnloadDataStageImpl(fileCollectParamBean,
					newFile, changeFileList, fileNameHTreeMap, mapDBHelper);
			//利用JobStageController构建本次非结构化文件采集作业流程
			JobStageController controller = new JobStageController();
			//7.构建责任链，串起每个阶段
			controller.registerJobStage(unloadData);
			//8.按照顺序从第一个阶段开始执行作业
			//XXX 这里需要决定每个状态执行完成数据存到哪里，文件？mapDB?palDB?,哪个目录？
			jobStatus = controller.handleStageByOrder(statusFilePath, jobStatus);
			//所有的流程执行完成，将队列从map中移除
			FileCollectJob.mapQueue.remove(file_source_id);
		} catch (Exception e) {
			log.error("文件采集失败" + e.getMessage());
			//TODO 这里是抛异常还是放到JobStatusInfo，目前的这个不支持针对每个文件夹去记录文件采集失败还是成功
		}
		return jobStatus;
	}

	@Method(desc = "获取新增的文件",
			logicStep = "1.获取文件夹下没有被采集过的文件或者文件夹" +
					"2.文件夹则递归调用本方法，文件则放到新增的文件List中")
	@Param(name = "path", desc = "需要采集的文件夹路径", range = "不能为空")
	@Param(name = "fileTypeList", desc = "需要采集的文件后缀名的集合", range = "不可为空")
	@Param(name = "fileNameHTreeMap", desc = "已经被采集过的文件，和文件最后一次修改时间的Map集合", range = "不可为空")
	@Param(name = "newFile", desc = "文件夹下未被采集过的文件全路径的集合", range = "不可为空")
	private void getNewFileListByFileType(String path, List<String> fileTypeList, ConcurrentMap<String, String>
			fileNameHTreeMap, List<String> newFile, String is_other) {
		File[] files;
		//如果选择了其他，则fileTypeList里面的文件类型不采集
		if (IsFlag.Shi.getCode().equals(is_other)) {
			//1.获取文件夹下没有被采集过的文件或者文件夹
			files = new File(path).listFiles((file) -> file.isDirectory()
					|| (!fileNameHTreeMap.containsKey(file.getAbsolutePath())
					&& !fileTypeList.contains(FileNameUtils.getExtension(file.getName()))));
		} else {//如果没有选择其他，则只采集fileTypeList里面的文件类型
			//1.获取文件夹下没有被采集过的文件或者文件夹
			files = new File(path).listFiles((file) -> file.isDirectory()
					|| (!fileNameHTreeMap.containsKey(file.getAbsolutePath())
					&& fileTypeList.contains(FileNameUtils.getExtension(file.getName()))));
		}
		if (files != null && files.length > 0) {
			for (File file : files) {
				//2.文件夹则递归调用本方法，文件则放到新增的文件List中
				if (file.isDirectory()) {
					getNewFileListByFileType(file.getAbsolutePath(), fileTypeList, fileNameHTreeMap, newFile, is_other);
				} else {
					newFile.add(file.getAbsolutePath());
				}
			}
		}
	}

	@Method(desc = "获取文件夹下变化的文件",
			logicStep = "1.获取文件夹下被采集过的但又被编辑过的文件或者文件夹" +
					"2.文件夹则递归调用本方法，文件则放到改变过的文件List中")
	@Param(name = "path", desc = "需要采集的文件夹路径", range = "不能为空")
	@Param(name = "fileTypeList", desc = "需要采集的文件后缀名的集合", range = "不可为空")
	@Param(name = "fileNameHTreeMap", desc = "已经被采集过的文件，和文件最后一次修改时间的Map集合", range = "不可为空")
	@Param(name = "newFile", desc = "文件夹下被采集过的但又被编辑过的文件全路径的集合", range = "不可为空")
	private void getChangeFileListByFileType(String path, List<String> fileTypeList, ConcurrentMap<String, String>
			fileNameHTreeMap, List<String> changeFileList, String is_other) {
		File[] files;
		//如果选择了其他，则fileTypeList里面的文件类型不采集
		if (IsFlag.Shi.getCode().equals(is_other)) {
			//1.获取文件夹下被采集过的但又被编辑过的文件或者文件夹
			if (JobConstant.FILECHANGESTYPEMD5) {
				files = new File(path).listFiles((file) -> file.isDirectory()
						|| (fileNameHTreeMap.containsKey(file.getAbsolutePath())
						&& !fileTypeList.contains(FileNameUtils.getExtension(file.getName()))
						&& (!JSONObject.parseObject(fileNameHTreeMap.get(file.getAbsolutePath()))
						.getString("file_md5").equals(MD5Util.md5File(file)))));
			} else {
				files = new File(path).listFiles((file) -> file.isDirectory()
						|| (fileNameHTreeMap.containsKey(file.getAbsolutePath())
						&& !fileTypeList.contains(FileNameUtils.getExtension(file.getName()))
						&& (!JSONObject.parseObject(fileNameHTreeMap.get(file.getAbsolutePath()))
						.getString("file_md5").equals(String.valueOf(file.lastModified())))));
			}
		} else {//如果没有选择其他，则只采集fileTypeList里面的文件类型
			//1.获取文件夹下被采集过的但又被编辑过的文件或者文件夹
			if (JobConstant.FILECHANGESTYPEMD5) {
				files = new File(path).listFiles((file) -> file.isDirectory()
						|| (fileNameHTreeMap.containsKey(file.getAbsolutePath())
						&& fileTypeList.contains(FileNameUtils.getExtension(file.getName()))
						&& (!JSONObject.parseObject(fileNameHTreeMap.get(file.getAbsolutePath()))
						.getString("file_md5").equals(MD5Util.md5File(file)))));
			} else {
				files = new File(path).listFiles((file) -> file.isDirectory()
						|| (fileNameHTreeMap.containsKey(file.getAbsolutePath())
						&& fileTypeList.contains(FileNameUtils.getExtension(file.getName()))
						&& (!JSONObject.parseObject(fileNameHTreeMap.get(file.getAbsolutePath()))
						.getString("file_md5").equals(String.valueOf(file.lastModified())))));
			}
		}
		if (files != null && files.length > 0) {
			//2.文件夹则递归调用本方法，文件则放到改变过的文件List中
			for (File file : files) {
				if (file.isDirectory()) {
					getChangeFileListByFileType(file.getAbsolutePath(), fileTypeList, fileNameHTreeMap,
							changeFileList, is_other);
				} else {
					changeFileList.add(file.getAbsolutePath());
				}
			}
		}
	}

	@Method(desc = "根据源文件设置表对象获取需要采集的文件类型后缀名",
			logicStep = "1.获取所有文件对应名称和种类列表" +
					"2.判断是否采集音频，图片，视频等将对应的需要采集的文件的后缀名放到需要返回的List")
	private static List<String> getFileSuffixList(File_source file_source) {
		List<String> fileSuffixList = new ArrayList<>();
		//1.获取所有文件对应名称和种类列表
		Map<String, String[]> fileTypeMap = FileTypeUtil.getFileTypeMap();
		//如果选择了其他，则取所有未选择的文件类型后缀名，文件筛选时，只要是此类文件后缀名的不采集
		if (IsFlag.Shi.getCode().equals(file_source.getIs_other())) {
			//2.判断未选择的音频，图片，视频等将对应的需要采集的文件的后缀名放到需要返回的List
			if (IsFlag.Fou.getCode().equals(file_source.getIs_audio())) {
				fileSuffixList.addAll(Arrays.asList(fileTypeMap.get(FileTypeUtil.YinPin)));
			}
			if (IsFlag.Fou.getCode().equals(file_source.getIs_image())) {
				fileSuffixList.addAll(Arrays.asList(fileTypeMap.get(FileTypeUtil.TuPian)));
			}
			if (IsFlag.Fou.getCode().equals(file_source.getIs_office())) {
				fileSuffixList.addAll(Arrays.asList(fileTypeMap.get(FileTypeUtil.OfficeWenJian)));
			}
			if (IsFlag.Fou.getCode().equals(file_source.getIs_pdf())) {
				fileSuffixList.addAll(Arrays.asList(fileTypeMap.get(FileTypeUtil.PDFWenJian)));
			}
			if (IsFlag.Fou.getCode().equals(file_source.getIs_text())) {
				addTextType(fileSuffixList, fileTypeMap);
			}
			if (IsFlag.Fou.getCode().equals(file_source.getIs_video())) {
				fileSuffixList.addAll(Arrays.asList(fileTypeMap.get(FileTypeUtil.ShiPin)));
			}
			if (IsFlag.Fou.getCode().equals(file_source.getIs_compress())) {
				fileSuffixList.addAll(Arrays.asList(fileTypeMap.get(FileTypeUtil.YaSuoWenJian)));
			}
		} else {//未选择其他则只采集已选文件后缀名的文件
			//2.判断是否采集音频，图片，视频等将对应的需要采集的文件的后缀名放到需要返回的List
			if (IsFlag.Shi.getCode().equals(file_source.getIs_audio())) {
				fileSuffixList.addAll(Arrays.asList(fileTypeMap.get(FileTypeUtil.YinPin)));
			}
			if (IsFlag.Shi.getCode().equals(file_source.getIs_image())) {
				fileSuffixList.addAll(Arrays.asList(fileTypeMap.get(FileTypeUtil.TuPian)));
			}
			if (IsFlag.Shi.getCode().equals(file_source.getIs_office())) {
				fileSuffixList.addAll(Arrays.asList(fileTypeMap.get(FileTypeUtil.OfficeWenJian)));
			}
			if (IsFlag.Shi.getCode().equals(file_source.getIs_pdf())) {
				fileSuffixList.addAll(Arrays.asList(fileTypeMap.get(FileTypeUtil.PDFWenJian)));
			}
			if (IsFlag.Shi.getCode().equals(file_source.getIs_text())) {
				addTextType(fileSuffixList, fileTypeMap);
			}
			if (IsFlag.Shi.getCode().equals(file_source.getIs_video())) {
				fileSuffixList.addAll(Arrays.asList(fileTypeMap.get(FileTypeUtil.ShiPin)));
			}
			if (IsFlag.Shi.getCode().equals(file_source.getIs_compress())) {
				fileSuffixList.addAll(Arrays.asList(fileTypeMap.get(FileTypeUtil.YaSuoWenJian)));
			}
			//再加上用户自定义的类型
			if (!StringUtil.isBlank(file_source.getCustom_suffix())) {
				fileSuffixList.addAll(StringUtil.split(file_source.getCustom_suffix(), "|"));
			}
		}
		return fileSuffixList;
	}

	@Method(desc = "获取文本类型的文件后缀名放到集合", logicStep = "")
	@Param(name = "fileSuffixList", desc = "文件后缀名的集合", range = "可以为空集合")
	@Param(name = "fileTypeMap", desc = "文件类型及其类型对应后缀名集合的列表", range = "不可为空")
	private static void addTextType(List<String> fileSuffixList, Map<String, String[]> fileTypeMap) {
		fileSuffixList.addAll(Arrays.asList(fileTypeMap.get(FileTypeUtil.WenBenWenJian)));
		fileSuffixList.addAll(Arrays.asList(fileTypeMap.get(FileTypeUtil.RiZhiWenJian)));
		fileSuffixList.addAll(Arrays.asList(fileTypeMap.get(FileTypeUtil.BiaoShuJuWenJian)));
	}

	@Override
	public List<MetaInfoBean> getMetaInfoGroup() {
		return null;
	}

	@Override
	public MetaInfoBean getMetaInfo() {
		return null;
	}

	@Override
	public JobStatusInfo call() {
		//多线程执行作业
		return runJob();
	}
}
