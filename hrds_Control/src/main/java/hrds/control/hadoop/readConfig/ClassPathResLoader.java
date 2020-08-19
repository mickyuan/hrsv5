package hrds.control.hadoop.readConfig;

import hrds.commons.exception.AppSystemException;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;

/**
 * @ClassName: ClassPathResLoader
 * @Description: 用于加载Hadoop相关组件客户端配置文件的类。
 * @Author: Tiger.Wang
 * @Date: 2019/8/30 11:41
 **/
class ClassPathResLoader {

	private static final Method addURL = initAddMethod();
	private static Instrumentation inst = null;

	// The JRE will call method before launching your main()
	public static void agentmain(final String a, final Instrumentation inst) {
		ClassPathResLoader.inst = inst;
	}


	/**
	 * 通过反射方式加载URLClassLoader类的addURL方法。
	 * 1.反射方式加载URLClassLoader类的addURL方法。
	 *
	 * @return java.lang.reflect.Method <br>
	 * 含义：表示URLClassLoader类的addURL方法。 <br>
	 * 取值范围：不会为null。
	 * @author Tiger.Wang
	 * @date 2019/10/8
	 */
	private static Method initAddMethod() {

		//1.反射方式加载URLClassLoader类的addURL方法。
		try {
			Method add = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
			add.setAccessible(true);
			return add;
		} catch (Exception e) {
			throw new AppSystemException(e);
		}
	}

	/**
	 * 根据传入的目录路径参数，加载该路径下所有的配置文件（包括多级目录下的配置文件）。
	 * 1.加载配置文件。
	 *
	 * @param dirPath <br>
	 *                含义：Hadoop相关的配置文件所在目录路径。 <br>
	 *                取值范围：任意字符串。
	 * @author Tiger.Wang
	 * @date 2019/10/8
	 */
	static void loadResourceDir(String dirPath) {

		//1.加载配置文件。
		File file = new File(dirPath);
		loopDirs(file);
	}

	/**
	 * 递归扫描目录，该方法只会在传入的参数为目录对象的情况下工作。
	 * 1.递归扫描目录。
	 *
	 * @param file <br>
	 *             含义：表示一个目录对象。 <br>
	 *             取值范围：不能为null。
	 * @author Tiger.Wang
	 * @date 2019/10/8
	 */
	private static void loopDirs(File file) {

		//1.递归扫描目录。
		if (file.isDirectory()) {
			addURL(file);
			File[] tmps = file.listFiles();
			if (null == tmps) {
				return;
			}

			for (File tmp : tmps) {
				loopDirs(tmp);
			}
		}
	}

	/**
	 * 加载指定目录下的文件到classpath。<br>
	 * 1.执行反射调用。
	 *
	 * @param file <br>
	 *             含义：表示一个待加载配置文件的目录对象。 <br>
	 *             取值范围：不能为null。
	 * @author Tiger.Wang
	 * @date 2019/10/8
	 */
	private static void addURL(File file) {
		//执行反射调用
		ClassLoader classloader = ClassLoader.getSystemClassLoader();
		try {
			// If Java 9 or higher use Instrumentation
			if (!(classloader instanceof URLClassLoader)) {
				inst.appendToSystemClassLoaderSearch(new JarFile(file));
				return;
			}
			addURL.invoke(classloader, file.toURI().toURL());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
