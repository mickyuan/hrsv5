package hrds.commons.utils;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import hrds.commons.codes.FileType;

import java.util.*;

@DocClass(desc = "判断文件类型", author = "zxz", createdate = "2019/10/29 14:23")
public class FileTypeUtil {

	public static String TuPian = "图片";
	public static String PDFWenJian = "PDF文件";
	public static String OfficeWenJian = "office文件";
	public static String WenBenWenJian = "文本文件";
	public static String RiZhiWenJian = "日志文件";
	public static String ShiPin = "视频";
	public static String YinPin = "音频";
	public static String YaSuoWenJian = "压缩文件";
	public static String BiaoShuJuWenJian = "表数据文件";

	//所有文件对应名称和种类列表
	private static final Map<String, String[]> fileTypeMap = new HashMap<>();
	//所有文件对应名称码值和种类列表
	private static final Map<String, String[]> fileTypeCode = new HashMap<>();
	//对应类型的文件扩展名的List集合
	private static final List<String> allFileType = new ArrayList<>();

	static {
		// 图片
		String[] picture = {"bmp", "jpg", "tiff", "gif", "pcx", "tga", "exif", "fpx", "svg", "psd", "cdr", "pcd", "dxf", "ufo", "eps", "ai", "png",
				"raw", "jpeg", ""};
		fileTypeMap.put(TuPian, picture);
		fileTypeCode.put(FileType.TuPian.getCode(), picture);
		// PDF
		String[] pdf = {"pdf"};
		fileTypeMap.put(PDFWenJian, pdf);
		fileTypeCode.put(FileType.PDFFile.getCode(), pdf);
		// Office
		String[] Office = {"doc", "docx", "xlsx", "xls", "pptx", "ppt"};
		fileTypeMap.put(OfficeWenJian, Office);
		fileTypeCode.put(FileType.OfficeFile.getCode(), Office);
		// 文本文件
		String[] txt = {"txt", "bat", "c", "bas", "prg", "cmd", "java", "jsp", "xml"};
		fileTypeMap.put(WenBenWenJian, txt);
		fileTypeCode.put(FileType.WenBenFile.getCode(), txt);
		// log文件
		String[] log = {"log"};
		fileTypeMap.put(RiZhiWenJian, log);
		fileTypeCode.put(FileType.RiZhiFile.getCode(), log);
		// 视频文件
		String[] video = {"avi", "rmvb", "rm", "asf", "divx", "mpg", "mpeg", "mpe", "wmv", "mp4", "mkv", "vob"};
		fileTypeMap.put(ShiPin, video);
		fileTypeCode.put(FileType.ShiPin.getCode(), video);
		// 音频文件
		String[] audio = {"cd", "ogg", "mp3", "asf", "wma", "mp3pro", "real", "ape", "module", "midi", "vqf"};
		fileTypeMap.put(YinPin, audio);
		fileTypeCode.put(FileType.YinPin.getCode(), audio);
		// 压缩文件
		String[] compression = {"zip", "rar", "jar", "7z", "gz", "arj", "gzip", "tar", "iso"};
		fileTypeMap.put(YaSuoWenJian, compression);
		fileTypeCode.put(FileType.YaSuoFile.getCode(), compression);
		// 表数据文件
		String[] csv = {"csv"};
		fileTypeMap.put(BiaoShuJuWenJian, csv);
		fileTypeCode.put(FileType.biaoShuJuFile.getCode(), csv);

		allFileType.add(TuPian);
		allFileType.add(PDFWenJian);
		allFileType.add(OfficeWenJian);
		allFileType.add(WenBenWenJian);
		allFileType.add(RiZhiWenJian);
		allFileType.add(ShiPin);
		allFileType.add(YinPin);
		allFileType.add(YaSuoWenJian);
		allFileType.add(BiaoShuJuWenJian);
	}

	@Method(desc = "判断文件所属类型",
			logicStep = "1.默认没有匹配上文件类型时返回其他" +
					"2.遍历所有文件类型的后缀，判断是否包含，包含则返回对应的文件类型")
	@Param(name = "type", desc = "文件后缀名", range = "不可为空")
	@Return(desc = "文件类型的码值", range = "不会为空")
	public static String fileTypeCode(String type) {
		//1.默认没有匹配上文件类型时返回其他
		String fileType = FileType.Other.getCode();
		//2.遍历所有文件类型的后缀，判断是否包含，包含则返回对应的文件类型
		for (Map.Entry<String, String[]> suffix : fileTypeCode.entrySet()) {
			if (Arrays.asList(suffix.getValue()).contains(type.toLowerCase())) {
				fileType = suffix.getKey();
			}
		}
		return fileType;
	}

	@Method(desc = "根据文件所属中文名称返回对应类型的文件扩展名"
			, logicStep = "1.根据文件所属中文名称返回对应类型的文件扩展名的List集合")
	@Param(name = "type", desc = "文件所属中文名称", range = "不可为空")
	@Return(desc = "对应类型的文件扩展名的List集合", range = "可能为空")
	public static List<String> getTypeFileList(String type) {
		//1.根据文件所属中文名称返回对应类型的文件扩展名的List集合
		String[] types = fileTypeMap.get(type);
		if (types != null) {
			return Arrays.asList(types);
		} else {
			return null;
		}
	}

	@Method(desc = "获取所有文件类型后缀名的集合", logicStep = "获取所有文件类型后缀名的集合")
	@Return(desc = "所有文件类型后缀名的集合", range = "不会为空")
	public static List<String> getAllFileSuffixList() {
		List<String> fileSuffixList = new ArrayList<>();
		for (String key : getFileTypeMap().keySet()) {
			fileSuffixList.addAll(Arrays.asList(fileTypeMap.get(key)));
		}
		return fileSuffixList;
	}

	@Method(desc = "获取所有文件类型", logicStep = "获取所有文件类型")
	@Return(desc = "所有文件类型的List集合", range = "不会为空")
	public static List<String> getAllFileType() {
		return allFileType;
	}

	@Method(desc = "获取所有文件对应名称和种类列表", logicStep = "获取所有文件对应名称和种类列表")
	@Return(desc = "所有文件对应名称和种类列表", range = "不会为空")
	public static Map<String, String[]> getFileTypeMap() {
		return fileTypeMap;
	}

	@Method(desc = "获取所有文件对应名称码值和种类列表", logicStep = "获取所有文件对应名称码值和种类列表")
	@Return(desc = "所有文件对应名称码值和种类列表", range = "不会为空")
	public static Map<String, String[]> getFileTypeCode() {
		return fileTypeCode;
	}

}
