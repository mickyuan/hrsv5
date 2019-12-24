package hrds.commons.utils;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@DocClass(desc = "获取包路径工具类", author = "BY-HLL", createdate = "2019/12/24 0024 下午 03:32")
public class PackageUtil {

	@Method(desc = "获取某包下（包括该包的所有子包）所有类",
			logicStep = "逻辑说明")
	@Param(name = "packageName", desc = "包名", range = "String类型,包名")
	@Return(desc = "类名的List集合", range = "无限制")
	public static List<String> getClassName(String packageName) throws Exception {

		return getClassName(packageName, true);
	}

	@Method(desc = "获取某包下（包括该包的所有子包）所有类",
			logicStep = "逻辑说明")
	@Param(name = "packageName", desc = "包名", range = "String类型,包名")
	@Param(name = "childPackage", desc = "是否遍历子包", range = "true:遍历,false:不遍历")
	@Return(desc = "类名的List集合", range = "无限制")
	public static List<String> getClassName(String packageName, boolean childPackage) throws Exception {

		List<String> fileNames = null;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		String packagePath = packageName.replace(".", "/");
		URL url = loader.getResource(packagePath);
		if (url != null) {
			String type = url.getProtocol();
			if ("file".equals(type)) {
				fileNames = getClassNameByFile(url.getPath(), null, childPackage);
			} else if (("jar").equals(type)) {
				fileNames = getClassNameByJar(url.getPath(), childPackage);
			}
		} else {
			fileNames = getClassNameByJars(((URLClassLoader) loader).getURLs(), packagePath, childPackage);
		}
		return fileNames;
	}

	@Method(desc = "从项目文件获取某包下所有类",
			logicStep = "从项目文件获取某包下所有类")
	@Param(name = "filePath", desc = "文件路径", range = "String类型")
	@Param(name = "className", desc = "类名集合", range = "String类型")
	@Param(name = "childPackage", desc = "是否遍历子包", range = "true:遍历,false:不遍历")
	@Return(desc = "类名的List集合", range = "无限制")
	private static List<String> getClassNameByFile(String filePath, List<String> className, boolean childPackage) {

		List<String> myClassName = new ArrayList<String>();
		File file = new File(filePath);
		File[] childFiles = file.listFiles();
		assert childFiles != null;
		for (File childFile : childFiles) {
			if (childFile.isDirectory()) {
				if (childPackage) {
					myClassName.addAll(getClassNameByFile(childFile.getPath(), myClassName, childPackage));
				}
			} else {
				String childFilePath = childFile.getPath();
				if (childFilePath.endsWith(".class")) {
					childFilePath = childFilePath.substring(childFilePath.indexOf("\\classes") + 9,
							childFilePath.lastIndexOf("."));
					childFilePath = childFilePath.replace("\\", ".");
					myClassName.add(childFilePath);
				}
			}
		}

		return myClassName;
	}

	@Method(desc = "从jar获取某包下所有类",
			logicStep = "从jar获取某包下所有类")
	@Param(name = "jarPath", desc = "jar文件路径", range = "String类型")
	@Param(name = "childPackage", desc = "是否遍历子包", range = "true:遍历,false:不遍历")
	@Return(desc = "类名的List集合", range = "无限制")
	private static List<String> getClassNameByJar(String jarPath, boolean childPackage) throws Exception {

		List<String> myClassName = new ArrayList<String>();
		String[] jarInfo = jarPath.split("!");
		String jarFilePath = jarInfo[0].substring(jarInfo[0].indexOf("/"));
		String packagePath = jarInfo[1].substring(1);
		try (JarFile jarFile = new JarFile(jarFilePath)) {
			Enumeration<JarEntry> entrys = jarFile.entries();
			while (entrys.hasMoreElements()) {
				JarEntry jarEntry = entrys.nextElement();
				String entryName = jarEntry.getName();
				if (entryName.endsWith(".class")) {
					if (childPackage) {
						if (entryName.startsWith(packagePath)) {
							entryName = entryName.replace("/", ".")
									.substring(0, entryName.lastIndexOf("."));
							myClassName.add(entryName);
						}
					} else {
						int index = entryName.lastIndexOf("/");
						String myPackagePath;
						if (index != -1) {
							myPackagePath = entryName.substring(0, index);
						} else {
							myPackagePath = entryName;
						}
						if (myPackagePath.equals(packagePath)) {
							entryName = entryName.replace("/", ".")
									.substring(0, entryName.lastIndexOf("."));
							myClassName.add(entryName);
						}
					}
				}
			}
		}
		return myClassName;
	}

	@Method(desc = "从所有jar中搜索该包，并获取该包下所有类",
			logicStep = "从jar获取某包下所有类")
	@Param(name = "urls", desc = "URL集合", range = "URL[],URL集合")
	@Param(name = "packagePath", desc = "包路径", range = "String类型")
	@Param(name = "childPackage", desc = "是否遍历子包", range = "true:遍历,false:不遍历")
	@Return(desc = "类名的List集合", range = "无限制")
	private static List<String> getClassNameByJars(URL[] urls, String packagePath, boolean childPackage) throws Exception {

		List<String> myClassName = new ArrayList<String>();
		if (urls != null) {
			for (URL url : urls) {
				String urlPath = url.getPath();
				// 不必搜索classes文件夹
				if (urlPath.endsWith("classes/")) {
					continue;
				}
				String jarPath = urlPath + "!/" + packagePath;
				myClassName.addAll(getClassNameByJar(jarPath, childPackage));
			}
		}
		return myClassName;
	}

	@Method(desc = "MAIN",
			logicStep = "MAIN")
	public static void main(String[] args) throws Exception {

		String packageName = "hrds.entity";
		List<String> classNames = getClassName(packageName, false);
		if (classNames != null) {
			for (String className : classNames) {
				System.out.println(className);
			}
		}
	}
}
